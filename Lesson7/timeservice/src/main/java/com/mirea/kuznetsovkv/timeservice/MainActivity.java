package com.mirea.kuznetsovkv.timeservice;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView textView;
    private Button button;
    private final String host = "time.nist.gov";
    private final int port = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetTimeTask timeTask = new GetTimeTask();
                timeTask.execute();
            }
        });
    }

    private class GetTimeTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String timeResult = "";
            try {
                Socket socket = new Socket(host, port);
                BufferedReader reader = SocketUtils.getReader(socket);
                reader.readLine(); // Игнорируем первую строку
                timeResult = reader.readLine(); // Считываем вторую строку
                Log.d(TAG, timeResult);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                return "Ошибка подключения: " + e.getMessage();
            }
            return timeResult;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.startsWith("Ошибка")) {
                textView.setText(result);
                return;
            }

            String[] parts = result.split("\\s+");
            if (parts.length >= 3) {
                String dateStr = parts[1];
                String timeStr = parts[2];

                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.US);
                    inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date = inputFormat.parse(dateStr + " " + timeStr);

                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss", Locale.US);
                    outputFormat.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
                    String formattedTime = outputFormat.format(date);

                    textView.setText("Московское время:\n" + formattedTime);
                } catch (Exception e) {
                    textView.setText("Ошибка разбора времени: " + e.getMessage());
                }
            } else {
                textView.setText("Неверный формат ответа сервера");
            }
        }
    }
}