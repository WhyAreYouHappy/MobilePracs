package com.mirea.kuznetsovkv.mireaproject;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.mirea.kuznetsovkv.mireaproject.databinding.FragmentMapBinding;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;


public class MapFragment extends Fragment {


    private FragmentMapBinding binding;
    private MapView mapView;
    private MapObjectCollection markerCollection;


    private static class Place {
        final String name;
        final String description;
        final Point location;


        Place(String name, String description, Point location) {
            this.name = name;
            this.description = description;
            this.location = location;
        }
    }


    private final Place[] places = new Place[]{
            new Place("Бар Salden's'", "Вкусное пиво\nМосква, ул. Пятницкая, 3/4", new Point(55.741510, 37.628620)),
            new Place("Бар Берлога", "Вкусные медовухи\nМосква, Лазоревый проезд, 1", new Point(55.848670, 37.638827)),
            new Place("Пиццерия 'Mamma Mia'", "Итальянская пицца\nМосква, Земляной Вал, 33", new Point(55.748, 37.63)),
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        mapView = binding.mapview;
        setupZoomButtons();
        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();


        //  ✅  Надёжно: пересоздаём маркеры при старте
        mapView.postDelayed(this::setupMarkers, 500);
    }


    @Override
    public void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }


    private void setupMarkers() {
        if (markerCollection != null) {
            markerCollection.clear();
        }


        markerCollection = mapView.getMap().getMapObjects().addCollection();


        for (Place place : places) {
            PlacemarkMapObject marker = markerCollection.addPlacemark(
                    place.location,
                    ImageProvider.fromResource(requireContext(), R.drawable.img)
            );
            marker.setUserData(place);


            marker.addTapListener((mapObject, point) -> {
                Place tappedPlace = (Place) mapObject.getUserData();
                if (tappedPlace != null) {
                    Toast.makeText(requireContext(),
                            tappedPlace.name + "\n" + tappedPlace.description,
                            Toast.LENGTH_LONG).show();
                }
                return true;
            });
        }


        mapView.getMap().move(
                new CameraPosition(places[0].location, 13.5f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1f),
                null
        );
    }


    private void setupZoomButtons() {
        LinearLayout zoomLayout = new LinearLayout(requireContext());
        zoomLayout.setOrientation(LinearLayout.HORIZONTAL);
        zoomLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        zoomLayout.setPadding(0, 30, 0, 30);


        Button btnZoomOut = new Button(requireContext());
        btnZoomOut.setText("-");
        Button btnZoomIn = new Button(requireContext());
        btnZoomIn.setText("+");


        zoomLayout.addView(btnZoomOut);
        zoomLayout.addView(btnZoomIn);


        binding.rootLayout.addView(zoomLayout);


        btnZoomIn.setOnClickListener(v -> {
            CameraPosition current = mapView.getMap().getCameraPosition();
            mapView.getMap().move(
                    new CameraPosition(current.getTarget(), current.getZoom() + 1f, current.getAzimuth(), current.getTilt()),
                    new Animation(Animation.Type.SMOOTH, 0.3f),
                    null
            );
        });


        btnZoomOut.setOnClickListener(v -> {
            CameraPosition current = mapView.getMap().getCameraPosition();
            mapView.getMap().move(
                    new CameraPosition(current.getTarget(), current.getZoom() - 1f, current.getAzimuth(), current.getTilt()),
                    new Animation(Animation.Type.SMOOTH, 0.3f),
                    null
            );
        });
    }
}