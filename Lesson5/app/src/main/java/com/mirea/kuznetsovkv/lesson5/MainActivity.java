package com.mirea.kuznetsovkv.lesson5;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Получаем менеджер сенсоров
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Получаем список всех сенсоров
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // Находим ListView в разметке
        ListView listSensor = findViewById(R.id.sensorListView);

        // Создаем список для отображения в ListView
        ArrayList<Map<String, Object>> sensorList = new ArrayList<>();

        // Заполняем список данными о датчиках
        for (Sensor sensor : sensors) {
            Map<String, Object> sensorMap = new HashMap<>();
            sensorMap.put("name", sensor.getName());
            sensorMap.put("vendor", "Производитель: " + sensor.getVendor());
            sensorMap.put("type", "Тип: " + getSensorType(sensor.getType()));
            sensorMap.put("power", "Потребление: " + sensor.getPower() + " mA");
            sensorMap.put("version", "Версия: " + sensor.getVersion());
            sensorList.add(sensorMap);
        }

        // Создаем адаптер
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                sensorList,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "vendor", "type", "power", "version"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        // Устанавливаем адаптер
        listSensor.setAdapter(adapter);
    }

    // Метод для преобразования типа датчика в читаемый текст
    private String getSensorType(int type) {
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
                return "Акселерометр";
            case Sensor.TYPE_GYROSCOPE:
                return "Гироскоп";
            case Sensor.TYPE_MAGNETIC_FIELD:
                return "Магнитометр";
            case Sensor.TYPE_LIGHT:
                return "Датчик освещенности";
            case Sensor.TYPE_PRESSURE:
                return "Барометр";
            case Sensor.TYPE_PROXIMITY:
                return "Датчик приближения";
            case Sensor.TYPE_GRAVITY:
                return "Датчик гравитации";
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return "Линейное ускорение";
            case Sensor.TYPE_ROTATION_VECTOR:
                return "Вектор вращения";
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return "Влажность";
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return "Температура";
            case Sensor.TYPE_STEP_COUNTER:
                return "Счетчик шагов";
            case Sensor.TYPE_STEP_DETECTOR:
                return "Детектор шагов";
            case Sensor.TYPE_HEART_RATE:
                return "Пульс";
            default:
                return "Неизвестный датчик (" + type + ")";
        }
    }
}