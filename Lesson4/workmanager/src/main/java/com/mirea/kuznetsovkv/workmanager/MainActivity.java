package com.mirea.kuznetsovkv.workmanager;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Создаем ограничения для задачи
        Constraints constraints = new Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.UNMETERED) // Только Wi-Fi (без тарификации)
                .setRequiresCharging(true) // Только при зарядке
                .build();

        // Создаем запрос на выполнение работы
        OneTimeWorkRequest uploadWorkRequest =
                new OneTimeWorkRequest.Builder(UploadWorker.class)
                        .setConstraints(constraints)
                        .build();

        // Ставим задачу в очередь
        WorkManager.getInstance(this).enqueue(uploadWorkRequest);
    }
}