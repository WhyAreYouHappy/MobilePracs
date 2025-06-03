package com.mirea.kuznetsovkv.httpurlconnection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView ipTextView, locationTextView, weatherTextView;
    private Button getIpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipTextView = findViewById(R.id.ipTextView);
        locationTextView = findViewById(R.id.locationTextView);
        weatherTextView = findViewById(R.id.weatherTextView);
        getIpButton = findViewById(R.id.getIpButton);

        getIpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetIpTask().execute();
            }
        });
    }

    private class GetIpTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            try {
                URL url = new URL("https://ipinfo.io/json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                result = stringBuilder.toString();
                inputStream.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String ip = jsonObject.getString("ip");
                String city = jsonObject.getString("city");
                String region = jsonObject.getString("region");
                String country = jsonObject.getString("country");
                String loc = jsonObject.getString("loc");

                ipTextView.setText("IP: " + ip);
                locationTextView.setText("Местоположение: " + city + ", " + region + ", " + country);

                // Получаем погоду по координатам
                String[] coordinates = loc.split(",");
                new GetWeatherTask().execute(coordinates[0], coordinates[1]);
            } catch (JSONException e) {
                e.printStackTrace();
                ipTextView.setText("Ошибка получения данных");
            }
        }
    }

    private class GetWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String latitude = strings[0];
            String longitude = strings[1];
            String result = "";
            try {
                URL url = new URL("https://api.open-meteo.com/v1/forecast?latitude=" + latitude +
                        "&longitude=" + longitude + "&current_weather=true");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                result = stringBuilder.toString();
                inputStream.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject currentWeather = jsonObject.getJSONObject("current_weather");
                double temperature = currentWeather.getDouble("temperature");
                double windspeed = currentWeather.getDouble("windspeed");
                int weathercode = currentWeather.getInt("weathercode");

                String weatherDescription = getWeatherDescription(weathercode);
                weatherTextView.setText(String.format("Погода: %s, %.1f°C, ветер %.1f км/ч",
                        weatherDescription, temperature, windspeed));
            } catch (JSONException e) {
                e.printStackTrace();
                weatherTextView.setText("Ошибка получения погоды");
            }
        }

        private String getWeatherDescription(int code) {
            switch(code) {
                case 0: return "Ясно";
                case 1: return "Преимущественно ясно";
                case 2: return "Переменная облачность";
                case 3: return "Пасмурно";
                case 45: case 48: return "Туман";
                case 51: case 53: case 55: return "Морось";
                case 56: case 57: return "Ледяная морось";
                case 61: case 63: case 65: return "Дождь";
                case 66: case 67: return "Ледяной дождь";
                case 71: case 73: case 75: return "Снег";
                case 77: return "Снежные зерна";
                case 80: case 81: case 82: return "Ливень";
                case 85: case 86: return "Снегопад";
                case 95: return "Гроза";
                case 96: case 99: return "Гроза с градом";
                default: return "Неизвестно";
            }
        }
    }
}