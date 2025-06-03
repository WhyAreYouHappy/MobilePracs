package com.mirea.kuznetsovkv.data_thread;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.TimeUnit;
import com.mirea.kuznetsovkv.data_thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String TAG = "DataThreadProject";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d(TAG, "MainActivity создана");

        // Создаем Runnable объекты
        final Runnable runn1 = new Runnable() {
            public void run() {
                binding.tvInfo.append("\nrunn1 выполнен");
                Log.d(TAG, "runn1 выполнен в UI потоке");
            }
        };

        final Runnable runn2 = new Runnable() {
            public void run() {
                binding.tvInfo.append("\nrunn2 выполнен");
                Log.d(TAG, "runn2 выполнен в UI потоке");
            }
        };

        final Runnable runn3 = new Runnable() {
            public void run() {
                binding.tvInfo.append("\nrunn3 выполнен");
                Log.d(TAG, "runn3 выполнен в UI потоке с задержкой");
            }
        };

        // Настройка TextView для отображения информации
        binding.tvInfo.setText("Последовательность выполнения Runnable:\n");
        binding.tvInfo.setMaxLines(10);
        binding.tvInfo.setLines(10);

        // Создаем и запускаем фоновый поток
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Log.d(TAG, "Фоновый поток запущен");

                    // Задержка 2 секунды
                    TimeUnit.SECONDS.sleep(2);
                    Log.d(TAG, "После 2 секунд ожидания - запуск runn1");
                    runOnUiThread(runn1);

                    // Задержка 1 секунда
                    TimeUnit.SECONDS.sleep(1);
                    Log.d(TAG, "После дополнительной 1 секунды ожидания - запуск runn2 и runn3 с задержкой");

                    // runn3 с задержкой 2 секунды
                    binding.tvInfo.postDelayed(runn3, 2000);

                    // runn2 без задержки
                    binding.tvInfo.post(runn2);

                } catch (InterruptedException e) {
                    Log.e(TAG, "Поток прерван", e);
                    e.printStackTrace();
                }
            }
        });

        t.start();
        Log.d(TAG, "Фоновый поток запущен из UI потока");

        // Добавляем объяснение различий методов
        binding.tvInfo.append("\n\nРазличия методов:");
        binding.tvInfo.append("\n1. runOnUiThread - немедленно выполняет Runnable в UI потоке");
        binding.tvInfo.append("\n2. post - добавляет Runnable в очередь сообщений UI потока");
        binding.tvInfo.append("\n3. postDelayed - выполняет Runnable с указанной задержкой");
    }
}