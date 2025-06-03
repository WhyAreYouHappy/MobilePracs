package com.mirea.kuznetsovkv.looper;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class MyLooper extends Thread {
    public Handler mHandler;
    private final Handler mainHandler;
    private static final String TAG = "MyLooper";

    public MyLooper(Handler mainThreadHandler) {
        mainHandler = mainThreadHandler;
    }

    @Override
    public void run() {
        Log.d(TAG, "run");
        Looper.prepare();

        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                try {
                    String age = msg.getData().getString("age");
                    String job = msg.getData().getString("job");

                    Log.d(TAG, "Получено сообщение: возраст=" + age + ", профессия=" + job);

                    // Имитация обработки с задержкой (в секундах)
                    int delaySeconds = Integer.parseInt(age);
                    Thread.sleep(delaySeconds * 1000L);

                    // Формируем ответное сообщение
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("result",
                            "Профессия: " + job + "\n" +
                                    "Обработано за " + delaySeconds + " секунд");
                    message.setData(bundle);

                    // Отправляем результат в главный поток
                    mainHandler.sendMessage(message);

                } catch (InterruptedException e) {
                    Log.e(TAG, "Ошибка в MyLooper", e);
                }
            }
        };

        Looper.loop();
    }
}