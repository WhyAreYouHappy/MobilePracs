package com.mirea.kuznetsovkv.audiorecord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mirea.kuznetsovkv.audiorecord.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 200;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private boolean isWork = false; // Флаг наличия всех необходимых разрешений

    private ActivityMainBinding binding;
    private String recordFilePath = null; // Путь к файлу с аудиозаписью

    private MediaRecorder recorder = null; // Объект для записи аудио
    private MediaPlayer player = null;     // Объект для воспроизведения аудио

    private Button recordButton = null;
    private Button playButton = null;

    // Флаги состояния записи и воспроизведения
    boolean isStartRecording = true; // true: готов к началу записи, false: идет запись
    boolean isStartPlaying = true;  // true: готов к началу воспроизведения, false: идет воспроизведение

    private static final String TAG = "AudioRecord"; // Тег для логов

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Инициализация кнопок
        recordButton = binding.recordButton;
        playButton = binding.playButton;
        playButton.setEnabled(false); // Отключаем кнопку воспроизведения изначально

        // Определение пути к файлу для записи
        // Используем getExternalFilesDir для сохранения в приватном каталоге приложения
        recordFilePath = (new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                "audiorecordtest.3gp")).getAbsolutePath();

        // Проверка и запрос разрешений при создании активности
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            isWork = true; // Если разрешения уже есть
        }

        // Обработчик нажатия на кнопку записи
        recordButton.setOnClickListener(v -> {
            if (isWork) { // Выполняем действие только если есть разрешения
                onRecord(isStartRecording);
                isStartRecording = !isStartRecording;
            } else {
                // Если разрешений нет, запросить их снова или уведомить пользователя
                requestPermissions();
                Toast.makeText(this, "Необходимы разрешения для записи", Toast.LENGTH_SHORT).show();
            }
        });

        // Обработчик нажатия на кнопку воспроизведения
        playButton.setOnClickListener(v -> {
            if (isWork) { // Выполняем действие только если есть разрешения
                onPlay(isStartPlaying);
                isStartPlaying = !isStartPlaying;
            } else {
                requestPermissions();
                Toast.makeText(this, "Необходимы разрешения для воспроизведения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Метод, вызываемый при нажатии на кнопку записи
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    // Метод, вызываемый при нажатии на кнопку воспроизведения
    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    // Метод для начала записи аудио
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // Источник аудио - микрофон
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // Формат выходного файла
        recorder.setOutputFile(recordFilePath); // Путь к файлу для сохранения
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // Аудиокодек

        try {
            recorder.prepare(); // Подготовка рекордера
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed: " + e.getMessage());
            Toast.makeText(this, "Ошибка подготовки рекордера", Toast.LENGTH_SHORT).show();
            releaseRecorder(); // Освободить ресурсы в случае ошибки
            return;
        }

        recorder.start(); // Начало записи
        recordButton.setText("Остановить запись");
        playButton.setEnabled(false); // Отключаем кнопку воспроизведения во время записи
        Toast.makeText(this, "Запись начата", Toast.LENGTH_SHORT).show();
    }

    // Метод для остановки записи аудио
    private void stopRecording() {
        if (recorder != null) {
            try {
                recorder.stop(); // Остановка записи
            } catch (RuntimeException e) {
                // Обработка ошибки: stop() может выбросить исключение, если start() не был вызван или произошла другая ошибка
                Log.e(TAG, "stop() failed: " + e.getMessage());
                // В этом случае файл может быть поврежден или отсутствовать, возможно, его стоит удалить
                File file = new File(recordFilePath);
                if (file.exists()) {
                    // file.delete(); // Опционально удалить поврежденный файл
                }
            } finally {
                releaseRecorder(); // Освобождение ресурсов рекордера
            }

            recordButton.setText("Начать запись");
            playButton.setEnabled(true); // Включаем кнопку воспроизведения после остановки записи
            Toast.makeText(this, "Запись остановлена. Файл: " + recordFilePath, Toast.LENGTH_LONG).show();
        }
    }

    // Метод для освобождения ресурсов MediaRecorder
    private void releaseRecorder() {
        if (recorder != null) {
            recorder.release(); // Освобождение ресурсов
            recorder = null;
        }
    }


    // Метод для начала воспроизведения аудио
    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(recordFilePath); // Указание файла для воспроизведения
            player.prepare(); // Подготовка плеера
            player.start(); // Начало воспроизведения
            playButton.setText("Остановить воспроизведение");
            recordButton.setEnabled(false); // Отключаем кнопку записи во время воспроизведения

            // Установка слушателя завершения воспроизведения
            player.setOnCompletionListener(mp -> {
                // Когда воспроизведение завершено
                stopPlaying();
                playButton.setText("Воспроизвести");
                recordButton.setEnabled(true); // Включаем кнопку записи
                isStartPlaying = true; // Сброс флага состояния воспроизведения
            });

            Toast.makeText(this, "Воспроизведение начато", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.e(TAG, "prepare() failed: " + e.getMessage());
            Toast.makeText(this, "Ошибка подготовки плеера", Toast.LENGTH_SHORT).show();
            releasePlayer(); // Освободить ресурсы в случае ошибки
        }
    }

    // Метод для остановки воспроизведения аудио
    private void stopPlaying() {
        if (player != null) {
            player.release(); // Освобождение ресурсов плеера
            player = null;
            playButton.setText("Воспроизвести");
            recordButton.setEnabled(true); // Включаем кнопку записи после остановки воспроизведения
            isStartPlaying = true; // Сброс флага состояния воспроизведения
            Toast.makeText(this, "Воспроизведение остановлено", Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для освобождения ресурсов MediaPlayer
    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    // Метод для проверки наличия всех необходимых разрешений
    private boolean checkPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // Метод для запроса необходимых разрешений у пользователя
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
        );
    }

    // Обработка результата запроса разрешений
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            isWork = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (isWork && grantResults.length > 1) {
                isWork = isWork && grantResults[1] == PackageManager.PERMISSION_GRANTED;
            }

            if (isWork) {
                Toast.makeText(this, "Разрешения получены", Toast.LENGTH_SHORT).show();
                // Если разрешения получены, можно включить кнопку воспроизведения, если файл существует
                File recordedFile = new File(recordFilePath);
                if (recordedFile.exists()) {
                    playButton.setEnabled(true);
                }

            } else {
                Toast.makeText(this, "Необходимы разрешения для работы с аудио", Toast.LENGTH_LONG).show();
                // Если разрешения не получены, можно отключить кнопки или закрыть приложение
                recordButton.setEnabled(false);
                playButton.setEnabled(false);
                // finish(); // Опционально: закрыть активность
            }
        }
    }

    // Важно освобождать ресурсы рекордера и плеера при уничтожении активности
    @Override
    public void onStop() {
        super.onStop();
        releaseRecorder();
        releasePlayer();
    }

    // Опционально: освобождать ресурсы при паузе активности
    @Override
    protected void onPause() {
        super.onPause();
        // Если запись или воспроизведение идут при сворачивании, остановить их
        if (!isStartRecording) { // Если идет запись
            stopRecording();
        }
        if (!isStartPlaying) { // Если идет воспроизведение
            stopPlaying();
        }
    }
}