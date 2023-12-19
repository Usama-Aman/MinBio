package com.vic.vicwsp.Views.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vic.vicwsp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.vic.vicwsp.Utils.Common.showToast;
import static com.vic.vicwsp.Utils.Constants.MY_PERMISSIONS_REQUEST_LOCATION;

public class AccountMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private View mapView;
    private TextView tvAddress;
    private boolean mTimerIsRunning = false;
    private AutocompleteSupportFragment autocompleteSupportFragment;
    private ConstraintLayout btnDoneMap;
    private ImageView back;
    private MarkerOptions markerOptions;
    private LatLng accountLatLng;
    private String accountAddress = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle bundle = getIntent().getExtras();
        accountLatLng = new LatLng(bundle.getDouble("lat"), bundle.getDouble("lng"));
        accountAddress = bundle.getString("userAddress");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        initialize();
    }

    private void initialize() {
        Places.initialize(AccountMapsActivity.this, getResources().getString(R.string.places_api_billing_key));

        back = findViewById(R.id.ivBack);
        back.setOnClickListener(this);
        btnDoneMap = findViewById(R.id.btnDoneMap);
        btnDoneMap.setOnClickListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to ble used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mapView = mapFragment.getView();

        autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().
                findFragmentById(R.id.autocomplete_fragment);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                accountLatLng = place.getLatLng();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(accountLatLng, 15);
                mMap.animateCamera(cameraUpdate);
                updateMarker();
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        getAddress(accountLatLng.latitude, accountLatLng.longitude);
                    }
                };
                thread.start();
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d(TAG, "onError: Auto Complete" + status);
            }

        });
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
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(AccountMapsActivity.this, R.raw.maps_style));


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                getAddress(marker.getPosition().latitude, marker.getPosition().longitude);
                accountLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            }
        });


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!accountAddress.equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    mMap.setMyLocationEnabled(true);
            }
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(accountLatLng, 15);
            mMap.animateCamera(cameraUpdate);

            getAddress(accountLatLng.latitude, accountLatLng.longitude);
            updateMarker();

        } else {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        getCurrentLocation();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getCurrentLocation() throws Exception {

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {

            accountLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(accountLatLng, 15);
            mMap.animateCamera(cameraUpdate);

            getAddress(accountLatLng.latitude, accountLatLng.longitude);
            updateMarker();

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleLocation(location);
        }
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);

                        if (!accountAddress.equals("")) {
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(accountLatLng, 15);
                            mMap.animateCamera(cameraUpdate);
                            getAddress(accountLatLng.latitude, accountLatLng.longitude);
                            updateMarker();

                        } else {
                            try {
                                getCurrentLocation();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                } else {
                    showToast(AccountMapsActivity.this, getResources().getString(R.string.permission_denied), false);
                }
                return;
            }
        }
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
        handleLocation(location);
    }

    private void handleLocation(Location location) {
        accountLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(accountLatLng, 15);
        mMap.animateCamera(cameraUpdate);
        getAddress(location.getLatitude(), location.getLongitude());
        updateMarker();

    }

    private void updateMarker() {
        markerOptions = new MarkerOptions();
        markerOptions.position(accountLatLng);
        markerOptions.draggable(true);
        mMap.clear();
        mMap.addMarker(markerOptions);
    }

    private String getAddress(double latitude, double longitude) {
        Address address;
        StringBuilder result = new StringBuilder();
        try {

            System.out.println("get address");
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                System.out.println("size====" + addresses.size());
                address = addresses.get(0);

                for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                    if (i == addresses.get(0).getMaxAddressLineIndex()) {
                        result.append(addresses.get(0).getAddressLine(i));
                    } else {
                        result.append(addresses.get(0).getAddressLine(i) + ",");
                    }
                }
                System.out.println("ad==" + address);
                System.out.println("result---" + result.toString());

                autocompleteSupportFragment.setText(result.toString()); // Here is you AutoCompleteTextView where you want to set your string address (You can remove it if you not need it)
                return result.toString();
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        return "";
    }

    @Override
    protected void onStart() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
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

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btnDoneMap) {
            accountAddress = getAddress(accountLatLng.latitude, accountLatLng.longitude);
            sendAccountAddressBroadCast();
            this.finish();
        } else if (view.getId() == R.id.ivBack) {
            this.finish();
        }
    }

    private void sendAccountAddressBroadCast() {
        Intent accountAddressBroadCast = new Intent("accountAddressBroadCast");
        accountAddressBroadCast.putExtra("userAddress", accountAddress);
        accountAddressBroadCast.putExtra("lat", String.valueOf(accountLatLng.latitude));
        accountAddressBroadCast.putExtra("lng", String.valueOf(accountLatLng.longitude));

        LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(accountAddressBroadCast);
    }
}
