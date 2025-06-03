package com.mirea.kuznetsovkv.osmmaps;



import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


import java.util.List;


public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private MyLocationNewOverlay locationNewOverlay;


    private static final GeoPoint START_POINT = new GeoPoint(55.741510, 37.628620);
    private static final GeoPoint MIREA_POINT = new GeoPoint(55.794229, 37.700772);


    private static final float DEFAULT_ZOOM = 13.0f;
    private static final float ROUTE_WIDTH = 5f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(this, getPreferences(Context.MODE_PRIVATE));
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_main);


        initMap();
        setupControls();
        setupLocationOverlay();
        setupCompass();
        setupScaleBar();
        addMarkers();
        buildRoutes();
    }


    private void initMap() {
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);


        MapController mapController = (MapController) mapView.getController();
        mapController.setZoom(DEFAULT_ZOOM);
        mapController.setCenter(START_POINT);
    }


    private void setupControls() {
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setZoomRounding(true);
    }


    private void setupLocationOverlay() {
        locationNewOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(getApplicationContext()), mapView);
        locationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(locationNewOverlay);
    }


    private void setupCompass() {
        CompassOverlay compassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);
    }


    private void setupScaleBar() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mapView.getOverlays().add(scaleBarOverlay);
    }


    private void addMarkers() {
        addMarker(START_POINT, "ВДНХ", "Станция метро ВДНХ");
        addMarker(MIREA_POINT, "МИРЭА", "РТУ МИРЭА, ул. Стромынка");
    }


    private void addMarker(GeoPoint point, String title, String description) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setTitle(title);
        marker.setSnippet(description);
        marker.setOnMarkerClickListener((m, mv) -> {
            Toast.makeText(this, title + ": " + description, Toast.LENGTH_SHORT).show();
            return true;
        });
        mapView.getOverlays().add(marker);
    }


    private void buildRoutes() {
        // 1. Прямой маршрут (севернее)
        Polyline route1 = new Polyline();
        route1.setPoints(List.of(
                START_POINT,
                new GeoPoint(55.8200, 37.6600),
                MIREA_POINT
        ));
        route1.setColor(Color.BLUE);
        route1.setWidth(ROUTE_WIDTH);
        mapView.getOverlays().add(route1);


        // 2. Через северо-восток
        Polyline route2 = new Polyline();
        route2.setPoints(List.of(
                START_POINT,
                new GeoPoint(55.8350, 37.6750),
                MIREA_POINT
        ));
        route2.setColor(Color.RED);
        route2.setWidth(ROUTE_WIDTH);
        mapView.getOverlays().add(route2);


        // 3. Через юг
        Polyline route3 = new Polyline();
        route3.setPoints(List.of(
                START_POINT,
                new GeoPoint(55.8100, 37.6450),
                MIREA_POINT
        ));
        route3.setColor(Color.GREEN);
        route3.setWidth(ROUTE_WIDTH);
        mapView.getOverlays().add(route3);


        // 4. Восточный объезд
        Polyline route4 = new Polyline();
        route4.setPoints(List.of(
                START_POINT,
                new GeoPoint(55.8200, 37.6950),
                MIREA_POINT
        ));
        route4.setColor(Color.MAGENTA);
        route4.setWidth(ROUTE_WIDTH);
        mapView.getOverlays().add(route4);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (locationNewOverlay != null) {
            locationNewOverlay.enableMyLocation();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if (locationNewOverlay != null) {
            locationNewOverlay.disableMyLocation();
        }
    }
}