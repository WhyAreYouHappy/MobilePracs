package com.mirea.kuznetsovkv.yandexmaps;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.mirea.kuznetsovkv.yandexmaps.databinding.ActivityMainBinding;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;


public class MainActivity extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CODE = 100;


    private ActivityMainBinding binding;
    private MapView mapView;
    private UserLocationLayer userLocationLayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Привязка layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Получаем карту
        mapView = binding.mapview;


        // Устанавливаем стартовую позицию камеры (Москва)
        mapView.getMap().move(
                new CameraPosition(
                        new Point(55.751574, 37.573856),
                        11.0f, 0.0f, 0.0f
                ),
                new Animation(Animation.Type.SMOOTH, 1),
                null
        );


        // Проверка разрешения на геолокацию
        if (checkLocationPermission()) {
            initializeUserLocationLayer();
        }
    }


    // Проверка доступа к геолокации
    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }


    // Обработка результата запроса разрешения
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeUserLocationLayer();
            } else {
                Toast.makeText(this, "Разрешение на геолокацию не предоставлено", Toast.LENGTH_LONG).show();
            }
        }
    }


    // Инициализация слоя пользовательского местоположения
    private void initializeUserLocationLayer() {
        MapKit mapKit = MapKitFactory.getInstance();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());


        // Показываем позицию пользователя и направление взгляда
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);


        // Обработка отображения объекта местоположения
        userLocationLayer.setObjectListener(new UserLocationObjectListener() {
            @Override
            public void onObjectAdded(@NonNull UserLocationView userLocationView) {
                // Меняем иконку стрелки и цвет круга точности
                userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                        MainActivity.this, android.R.drawable.arrow_up_float));
                userLocationView.getAccuracyCircle().setFillColor(Color.argb(64, 0, 0, 255));


                // Камера перемещается на текущее местоположение
                if (userLocationLayer != null && userLocationLayer.cameraPosition() != null) {
                    Point userLocation = userLocationLayer.cameraPosition().getTarget();
                    mapView.getMap().move(
                            new CameraPosition(userLocation, 15.0f, 0.0f, 0.0f),
                            new Animation(Animation.Type.SMOOTH, 1),
                            null
                    );
                }
            }


            @Override
            public void onObjectRemoved(@NonNull UserLocationView view) {
                // Ничего не делаем
            }


            @Override
            public void onObjectUpdated(@NonNull UserLocationView view, @NonNull ObjectEvent event) {
                // Ничего не делаем
            }
        });
    }


    // Жизненный цикл карты
    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }


    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }
}
