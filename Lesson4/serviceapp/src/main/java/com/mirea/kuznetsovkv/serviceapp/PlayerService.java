package com.mirea.kuznetsovkv.serviceapp;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

public class PlayerService extends Service {
    private MediaPlayer mediaPlayer;
    public static final String CHANNEL_ID = "MusicPlayerChannel";

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();

            // Создаем уведомление
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Сейчас играет")
                    .setContentText("Моя любимая песня - Студент МИРЭА")
                    .setSmallIcon(R.drawable.ic_music_note)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Лучший музыкальный проигрыватель МИРЭА"))
                    .build();

            // Запускаем сервис в foreground режиме
            startForeground(1, notification);
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Создаем канал уведомлений
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Music Player Channel",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Канал для уведомлений музыкального проигрывателя");

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        // Инициализируем MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(false);

        // Обработчик завершения воспроизведения
        mediaPlayer.setOnCompletionListener(mp -> {
            stopForeground(true);
            stopSelf();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopForeground(true);
    }
}