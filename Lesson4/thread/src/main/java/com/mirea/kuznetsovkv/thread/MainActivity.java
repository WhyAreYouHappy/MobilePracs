package com.mirea.kuznetsovkv.thread;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mirea.kuznetsovkv.thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String TAG = "ThreadProject";
    private int threadCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d(TAG, "Приложение запущено");

        // Вывод информации о главном потоке
        Thread mainThread = Thread.currentThread();
        binding.tvResult.setText("Имя текущего потока: " + mainThread.getName());
        mainThread.setName("МОЙ НОМЕР ГРУППЫ: БСБО-05-23, НОМЕР ПО СПИСКУ: 13, МОЙ ЛЮБИМНЫЙ ФИЛЬМ: ъэ");
        binding.tvResult.append("\nНовое имя потока: " + mainThread.getName());

        Log.d(TAG, String.format("Главный поток переименован: %s", mainThread.getName()));

        binding.btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Нажата кнопка расчета");
                calculateAveragePairs();
            }
        });
    }

    private void calculateAveragePairs() {
        // Проверка ввода
        if (binding.etTotalPairs.getText().toString().isEmpty() ||
                binding.etStudyDays.getText().toString().isEmpty()) {
            Log.w(TAG, "Попытка расчета без ввода данных");
            Toast.makeText(this, "Введите все данные", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalPairs = Integer.parseInt(binding.etTotalPairs.getText().toString());
        int studyDays = Integer.parseInt(binding.etStudyDays.getText().toString());

        if (studyDays == 0) {
            Log.w(TAG, "Попытка деления на ноль (studyDays = 0)");
            Toast.makeText(this, "Количество дней не может быть 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создание и запуск фонового потока
        final int threadNumber = threadCounter++;
        Log.d(TAG, String.format("Запущен поток № %d студентом группы БСБО-05-23, номер по списку 13", threadNumber));

        new Thread(new Runnable() {
            @Override
            public void run() {
                final double average = (double) totalPairs / studyDays;

                Log.d(TAG, String.format("Поток № %d: начато вычисление среднего", threadNumber));
                Log.d(TAG, String.format("Поток № %d: общее количество пар = %d, учебных дней = %d",
                        threadNumber, totalPairs, studyDays));

                // Обновление UI из главного потока
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, String.format("Поток № %d: обновление UI с результатами", threadNumber));
                        binding.tvResult.setText(String.format(
                                "Среднее количество пар в день: %.2f\n" +
                                        "Общее количество пар: %d\n" +
                                        "Учебных дней: %d",
                                average, totalPairs, studyDays
                        ));
                    }
                });

                // Имитация долгих вычислений
                long endTime = System.currentTimeMillis() + 3000;
                Log.d(TAG, String.format("Поток № %d: начало ожидания до %d", threadNumber, endTime));

                while (System.currentTimeMillis() < endTime) {
                    synchronized (this) {
                        try {
                            wait(endTime - System.currentTimeMillis());
                        } catch (Exception e) {
                            Log.e(TAG, String.format("Поток № %d: ошибка во время ожидания", threadNumber), e);
                        }
                    }
                }

                Log.d(TAG, String.format("Выполнен поток № %d студентом группы БСБО-05-23, номер по списку 13", threadNumber));
            }
        }).start();
    }
}