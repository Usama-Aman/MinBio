package com.vic.vicwsp.Views.Activities;


import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vic.vicwsp.Controllers.Interfaces.SocketCallback;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.ApplicationClass;
import com.vic.vicwsp.Utils.Common;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.vic.vicwsp.Views.Activities.MainActivity.sockets;

public class OrderNavigation extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SocketCallback {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap mMap;
    private static final String TAG = "OrderNavigation";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private View mapView;
    private LatLng origin, destination;
    private Marker driverMarker, userMarker;
    private Location currLocation;
    private Intent serviceIntent;
    private Polyline polyline;
    private ImageView ivToolbarBack;
    private int driverId = 0;
    private List<LatLng> pointsArrayList = new ArrayList<>();
    private PolylineOptions lineOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_tracking);

        ((ApplicationClass) getApplication()).initializeCallback(this);

        Log.d(TAG, "onCreate: " + sockets.getSocket().connected());
        sockets.initializeCallback(this);
        ivToolbarBack = findViewById(R.id.ivToolbarBack);
        ivToolbarBack.setOnClickListener(view -> finish());
        initialize();


    }

    private void initialize() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.liveMap);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mapView = mapFragment.getView();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap == null) {
            return;
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(OrderNavigation.this, R.raw.maps_style));

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {
            double driverLat, driverLng;
            double deliveryLat, deliveryLng;

            driverLat = AppUtils.convertStringToDouble(getIntent().getStringExtra("driverLat"));
            driverLng = AppUtils.convertStringToDouble(getIntent().getStringExtra("driverLng"));
            deliveryLat = AppUtils.convertStringToDouble(getIntent().getStringExtra("deliveryLat"));
            deliveryLng = AppUtils.convertStringToDouble(getIntent().getStringExtra("deliveryLng"));
            driverId = getIntent().getIntExtra("driverId", 0);

            if (driverLat != 0 && driverLng != 0 && deliveryLat != 0 && deliveryLng != 0) {

                origin = new LatLng(driverLat, driverLng);
                destination = new LatLng(deliveryLat, deliveryLng);

                showMarkersOnMap();
            } else {
                Common.showToast(this, "", false);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showMarkersOnMap() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(origin);
        markerOptions.draggable(false);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mMap.clear();
        driverMarker = mMap.addMarker(markerOptions);

        markerOptions = new MarkerOptions();
        markerOptions.position(destination);
        markerOptions.draggable(false);
        userMarker = mMap.addMarker(markerOptions);


        viewBoundsTheMap();

        String url = getDirectionsUrl(origin, destination);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    private void viewBoundsTheMap() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(userMarker.getPosition());
        builder.include(driverMarker.getPosition());

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30); // offset from edges of the map - 20% of screen

        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.moveCamera(cu);
        mMap.animateCamera(cu);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: " + String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private void updateTheRouteAndMarker(LatLng latLng, String route) {

        driverMarker.setPosition(latLng);
        origin = latLng;

//        if (route.equals("")) {
            String url = getDirectionsUrl(origin, destination);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
//        } else {
//            makePolyLine(PolyUtil.decode(route));
//        }
    }

    @Override
    protected void onStart() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getResources().getString(R.string.google_maps_key)
                + "&mode=" + "DRIVING";

        return url;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        // Executes in UI thread, after the execution of
        // doInBackground()

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            try {

                final JSONObject json = new JSONObject(result);
                JSONArray routeArray = json.getJSONArray("routes");
                JSONObject routes = routeArray.getJSONObject(0);
                JSONObject overviewPolylines = routes
                        .getJSONObject("overview_polyline");

                String encodedString = overviewPolylines.getString("points");

                pointsArrayList.clear();
                pointsArrayList = PolyUtil.decode(encodedString);
                makePolyLine(pointsArrayList);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Error Downloading URL", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    private void makePolyLine(List<LatLng> pointsArrayList) {

        try {
            lineOptions = new PolylineOptions();
            lineOptions.addAll(pointsArrayList);
            lineOptions.width(8);
            lineOptions.color(getResources().getColor(R.color.splashColor));

            if (polyline != null)
                polyline.remove();

            // Drawing polyline in the Google Map for the i-th route
            polyline = mMap.addPolyline(lineOptions);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSuccess(JSONObject jsonObject, String tag) {

        Log.d(TAG, "onSuccess: " + "Location Received");
        if (tag.equals("update_location")) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {

                        int dID = jsonObject.getInt("driver_id");
                        double lat = AppUtils.convertStringToDouble(jsonObject.getString("latitude"));
                        double lng = AppUtils.convertStringToDouble(jsonObject.getString("longitude"));
                        String route = jsonObject.getString("path");

                        LatLng latLng = new LatLng(lat, lng);
                        driverMarker.setPosition(latLng);
                        origin = latLng;

                        if (dID == driverId) {
                            if (route.equals("")) {
                                updateTheRouteAndMarker(latLng, route);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onFailure(String message, String tag) {

        Timber.d("onFailure: " + message);
    }

}
