package com.mirea.kuznetsovkv.looper;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mirea.kuznetsovkv.looper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MyLooper myLooper;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Создаем Handler для главного потока
        Handler mainThreadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.getData().getString("result");
                binding.textViewResult.setText(result);
                Log.d(TAG, "Результат обработки: " + result);
            }
        };

        // Создаем и запускаем наш Looper-поток
        myLooper = new MyLooper(mainThreadHandler);
        myLooper.start();

        // Обработчик нажатия кнопки
        binding.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myLooper.mHandler == null) {
                    Toast.makeText(MainActivity.this,
                            "Поток еще не готов, подождите", Toast.LENGTH_SHORT).show();
                    return;
                }

                String age = binding.editTextAge.getText().toString();
                String job = binding.editTextJob.getText().toString();

                if (age.isEmpty() || job.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Введите возраст и профессию", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Создаем и отправляем сообщение
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString("age", age);
                bundle.putString("job", job);
                msg.setData(bundle);

                myLooper.mHandler.sendMessage(msg);
                Log.d(TAG, "Сообщение отправлено в фоновый поток");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myLooper != null && myLooper.mHandler != null) {
            myLooper.mHandler.getLooper().quit();
        }
    }
}