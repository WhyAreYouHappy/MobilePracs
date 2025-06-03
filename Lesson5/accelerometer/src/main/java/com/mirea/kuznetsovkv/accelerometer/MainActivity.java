package com.mirea.kuznetsovkv.accelerometer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager; // Менеджер датчиков системы
    private Sensor accelerometer; // Объект для акселерометра
    private TextView azimuthTextView; // TextView для азимута (X)
    private TextView pitchTextView; // TextView для тангажа (Y)
    private TextView rollTextView; // TextView для крена (Z)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Установка основного макета для активности
        setContentView(R.layout.activity_main);

        // Настройка отступов для системных панелей (строки состояния и навигации)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Получение ссылок на TextView из макета по их идентификаторам
        azimuthTextView = findViewById(R.id.textViewAzimuth);
        pitchTextView = findViewById(R.id.textViewPitch);
        rollTextView = findViewById(R.id.textViewRoll);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // При возобновлении активности получаем SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Получаем дефолтный акселерометр
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Если акселерометр найден, регистрируем слушателя событий датчика
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // При приостановке активности отменяем регистрацию слушателя, чтобы не расходовать ресурсы
        sensorManager.unregisterListener(this);
    }

    // Метод вызывается при изменении показаний датчика
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Проверяем, является ли событие от акселерометра
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Получаем значения по осям X, Y, Z из массива values
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Обновляем текст в TextView с отформатированными значениями
            azimuthTextView.setText(String.format("Azimuth: %.2f", x));
            pitchTextView.setText(String.format("Pitch: %.2f", y));
            rollTextView.setText(String.format("Roll: %.2f", z));
        }
    }

    // Метод вызывается при изменении точности показаний датчика (не используется в данном примере)
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Ничего не делаем, так как изменение точности нас не интересует в данном приложении
    }
}