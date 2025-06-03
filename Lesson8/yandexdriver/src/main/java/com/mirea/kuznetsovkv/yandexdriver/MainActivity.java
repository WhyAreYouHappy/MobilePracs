package com.mirea.kuznetsovkv.yandexdriver;

import android.os.Bundle;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.mirea.kuznetsovkv.yandexdriver.databinding.ActivityMainBinding;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements DrivingSession.DrivingRouteListener {
    private ActivityMainBinding binding;


    // Точка начала маршрута
    private final Point ROUTE_START_LOCATION = new Point(55.741510, 37.628620);
    // Точка конца маршрута
    private final Point ROUTE_END_LOCATION = new Point(55.794192, 37.700749);


    // Центр карты — между точками
    private final Point SCREEN_CENTER = new Point(
            (ROUTE_START_LOCATION.getLatitude() + ROUTE_END_LOCATION.getLatitude()) / 2,
            (ROUTE_START_LOCATION.getLongitude() + ROUTE_END_LOCATION.getLongitude()) / 2);


    private MapView mapView;
    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;


    // Цвета для маршрутов
    private int[] colors = {0xFFFF0000, 0xFF00FF00, 0xFF0000FF};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Инициализация карт и маршрутов
        MapKitFactory.initialize(this);
        DirectionsFactory.initialize(this);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mapView = binding.mapview;


        // Отключение вращения карты
        mapView.getMap().setRotateGesturesEnabled(false);


        // Перемещение камеры в центр маршрута
        mapView.getMap().move(
                new CameraPosition(SCREEN_CENTER, 10, 0, 0),
                new Animation(Animation.Type.SMOOTH, 0),
                null
        );


        // Создание маршрутизатора и коллекции объектов карты
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        mapObjects = mapView.getMap().getMapObjects().addCollection();


        // Запрос построения маршрута
        submitRequest();
    }


    // Отправка запроса на построение маршрута
    private void submitRequest() {
        DrivingOptions drivingOptions = new DrivingOptions();
        VehicleOptions vehicleOptions = new VehicleOptions();


        // Количество маршрутов — 3
        drivingOptions.setRoutesCount(3);


        // Список точек маршрута
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        requestPoints.add(new RequestPoint(ROUTE_START_LOCATION, RequestPointType.WAYPOINT, null));
        requestPoints.add(new RequestPoint(ROUTE_END_LOCATION, RequestPointType.WAYPOINT, null));


        // Запуск запроса
        drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this);
    }


    // Обработка успешного построения маршрутов
    @Override
    public void onDrivingRoutes(@NonNull List<DrivingRoute> list) {
        for (int i = 0; i < list.size(); i++) {
            // Добавление маршрутов на карту с разным цветом
            mapObjects.addPolyline(list.get(i).getGeometry()).setStrokeColor(colors[i % colors.length]);
        }


        // Добавление маркера в конце маршрута
        PlacemarkMapObject marker = mapView.getMap().getMapObjects().addPlacemark(
                ROUTE_END_LOCATION,
                ImageProvider.fromResource(this, R.drawable.img)
        );


        // Обработка нажатия на маркер
        marker.addTapListener(new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(@NonNull com.yandex.mapkit.map.MapObject mapObject, @NonNull Point point) {
                Toast.makeText(MainActivity.this, "Пункт назначения: РТУ МИРЭА, Стромынка", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }


    // Обработка ошибок маршрута
    @Override
    public void onDrivingRoutesError(@NonNull Error error) {
        String errorMessage = "Unknown error";
        if (error instanceof RemoteError) {
            errorMessage = "Remote error";
        } else if (error instanceof NetworkError) {
            errorMessage = "Network error";
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }


    // Жизненный цикл карты
    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }


    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }
}