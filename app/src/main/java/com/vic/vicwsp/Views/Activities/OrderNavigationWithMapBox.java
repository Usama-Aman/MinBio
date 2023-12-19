package com.vic.vicwsp.Views.Activities;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vic.vicwsp.Controllers.Interfaces.SocketCallback;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.AppUtils;
import com.vic.vicwsp.Utils.Common;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.vic.vicwsp.Views.Activities.MainActivity.sockets;
import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class OrderNavigationWithMapBox extends AppCompatActivity implements OnMapReadyCallback, SocketCallback {

    private MapView mapView;
    private DirectionsRoute currentRoute;
    private MapboxMap mapboxMap;
    private MapboxDirections client;
    private GeoJsonSource originGeoJsonSource, destinationGeoJsonSource;
    private ValueAnimator animator;


    double driverLat, driverLng;
    double deliveryLat, deliveryLng;
    private int driverId = 0;

    private Point originPoint, destinationPoint;
    private LatLng originLatLng, destinationLatLng;
    private String TAG = "OrderNavigationWithMapBox";

    //IDs for marker
    private static final String ORIGIN_SOURCE_ID = "origin-source-id";
    private static final String DESTINATION_SOURCE_ID = "destination-source-id";

    private static final String ORIGIN_ICON_ID = "origin-icon-id";
    private static final String DESTINATION_ICON_ID = "destination-icon-id";

    private static final String ORIGIN_LAYER_ID = "origin-layer-id";
    private static final String DESTINATION_LAYER_ID = "destination-layer-id";

    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";

    private ProgressDialog dialog;
    private Handler handler;
    private Runnable runnable;

    private ImageView ivToolbarBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getResources().getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_order_navigation_mapbox);
        mapView = findViewById(R.id.mapViewMapBox);
        mapView.onCreate(savedInstanceState);
        sockets.initializeCallback(this);
        Log.d(TAG, "onCreate: " +  sockets.getSocket().connected());
        initialize();
    }

    private void initialize() {

        ivToolbarBack = findViewById(R.id.ivToolbarBack);
        ivToolbarBack.setOnClickListener(v -> finish());
        ImageView headerLogo = findViewById(R.id.headerLogo);
        headerLogo.setOnClickListener(v -> {
            Common.hideKeyboard(this);
            Common.goToMain(this);
        });

        driverLat = AppUtils.convertStringToDouble(getIntent().getStringExtra("driverLat"));
        driverLng = AppUtils.convertStringToDouble(getIntent().getStringExtra("driverLng"));
        deliveryLat = AppUtils.convertStringToDouble(getIntent().getStringExtra("deliveryLat"));
        deliveryLng = AppUtils.convertStringToDouble(getIntent().getStringExtra("deliveryLng"));
        driverId = getIntent().getIntExtra("driverId", 0);

        if (driverLat != 0 && driverLng != 0 && deliveryLat != 0 && deliveryLng != 0) {

            originPoint = Point.fromLngLat(driverLng, driverLat);
            destinationPoint = Point.fromLngLat(deliveryLng, deliveryLat);

            originLatLng = new LatLng(driverLat, driverLng);
            destinationLatLng = new LatLng(deliveryLat, deliveryLng);

            try {
                dialog = new ProgressDialog(this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setTitle(getResources().getString(R.string.loading));
                dialog.setMessage(getResources().getString(R.string.loading_please_wait));
                dialog.setIndeterminate(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mapView.getMapAsync(this);

    }


    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        this.mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            if (style.isFullyLoaded()) {

                LocalizationPlugin localizationPlugin = new LocalizationPlugin(mapView, mapboxMap, style);
                try {
                    localizationPlugin.matchMapLanguageWithDeviceDefault();
                } catch (RuntimeException exception) {
                    Log.d(TAG, exception.toString());
                }

                addMarkers(style);


//                try {
                    getCameraZoomInCenter();
                    getRoute(mapboxMap, originPoint, destinationPoint);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    private void addMarkers(Style style) {

        originGeoJsonSource = new GeoJsonSource(ORIGIN_SOURCE_ID,
                Feature.fromGeometry(originPoint));

        destinationGeoJsonSource = new GeoJsonSource(DESTINATION_SOURCE_ID,
                Feature.fromGeometry(destinationPoint));

        style.addImage((ORIGIN_ICON_ID), BitmapFactory.decodeResource(
                getResources(), R.drawable.map_marker_dark));

        style.addImage((DESTINATION_ICON_ID), BitmapFactory.decodeResource(
                getResources(), R.drawable.map_marker_light));

        style.addSource(originGeoJsonSource);
        style.addSource(destinationGeoJsonSource);

        style.addLayer(new SymbolLayer(ORIGIN_LAYER_ID, ORIGIN_SOURCE_ID)
                .withProperties(
                        PropertyFactory.iconImage(ORIGIN_ICON_ID),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconAllowOverlap(true)
                ));

        style.addLayer(new SymbolLayer(DESTINATION_LAYER_ID, DESTINATION_SOURCE_ID)
                .withProperties(
                        PropertyFactory.iconImage(DESTINATION_ICON_ID),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconAllowOverlap(true)
                ));

        style.addSource(new GeoJsonSource(ROUTE_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[]{})));

        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(3f),
                lineColor(Color.parseColor("#0a3a52"))
        );

        style.addLayer(routeLayer);

    }


    private void getCameraZoomInCenter() {

        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new LatLng(originPoint.latitude(), originPoint.longitude()))
                .include(new LatLng(destinationPoint.latitude(), destinationPoint.longitude()))
                .build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int padding = (int) (width * 0.25);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, padding);
        mapboxMap.moveCamera(cameraUpdate);
        mapboxMap.animateCamera(cameraUpdate);

    }

    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination) {

        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                try {
                    if (dialog != null)
                        dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Timber.d(String.valueOf(response.code()));
                if (response.body() == null) {
                    Timber.e("No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Timber.e("No routes found");
                    return;
                }

                currentRoute = response.body().routes().get(0);

                if (mapboxMap != null) {
                    mapboxMap.getStyle(style -> {

                        GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

                        if (source != null) {
                            Timber.d("onResponse: source != null");
                            source.setGeoJson(FeatureCollection.fromFeature(
                                    Feature.fromGeometry(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6))));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                try {
                    if (dialog != null)
                        dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Timber.e("Error: " + throwable.getMessage());
            }
        });
    }

    @Override
    public void onSuccess(JSONObject jsonObject, String tag) {
        if (tag.equals("update_location")) {

            runOnUiThread(() -> {

                try {

                    int dID = jsonObject.getInt("driver_id");
                    double lat = AppUtils.convertStringToDouble(jsonObject.getString("latitude"));
                    double lng = AppUtils.convertStringToDouble(jsonObject.getString("longitude"));

                    if (dID == driverId) {
                        originPoint = Point.fromLngLat(lng, lat);

                        Log.d(TAG, "MarkerUpdate: " + lat +""+ lng);

                        animateMarker(new LatLng(lat, lng));
                        originGeoJsonSource.setGeoJson(originPoint);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void animateMarker(LatLng latLng) {
        if (animator != null && animator.isStarted()) {
            originLatLng = (LatLng) animator.getAnimatedValue();
            animator.cancel();
        }

        animator = ObjectAnimator
                .ofObject(latLngEvaluator, originLatLng, latLng)
                .setDuration(2000);
        animator.addUpdateListener(animatorUpdateListener);
        animator.start();

        originLatLng = latLng;
    }

    private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LatLng animatedPosition = (LatLng) valueAnimator.getAnimatedValue();
                    if (originGeoJsonSource != null)
                        originGeoJsonSource.setGeoJson(Point.fromLngLat(animatedPosition.getLongitude(), animatedPosition.getLatitude()));
                }
            };


    private static final TypeEvaluator<LatLng> latLngEvaluator = new TypeEvaluator<LatLng>() {

        private final LatLng latLng = new LatLng();

        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            latLng.setLatitude(startValue.getLatitude()
                    + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
            latLng.setLongitude(startValue.getLongitude()
                    + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
            return latLng;
        }
    };

    @Override
    public void onFailure(String message, String tag) {
        Timber.d("onFailure: " + message);
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();

        handler = new Handler();
        int delay = 2000; //milliseconds

        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    Log.d(TAG, "RouteUpdate: ");
                    getRoute(mapboxMap, originPoint, destinationPoint);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }


    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();


        if (handler != null)
            handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null) {
            client.cancelCall();
        }


        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
