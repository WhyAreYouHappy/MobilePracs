package com.mirea.kuznetsovkv.mireaproject;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;

public class MicrophoneFragment extends Fragment {
    private MediaRecorder recorder;
    private MediaPlayer player;
    private String fileName;
    private Button btnStart, btnStop, btnPlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Загружаем интерфейс с тремя кнопками: "Запись", "Стоп", "Прослушать"
        View view = inflater.inflate(R.layout.fragment_microphone, container, false);

        // Привязка кнопок к переменным
        btnStart = view.findViewById(R.id.btnStart);
        btnStop = view.findViewById(R.id.btnStop);
        btnPlay = view.findViewById(R.id.btnPlay);

        // Устанавливаем действия при нажатии на кнопки
        btnStart.setOnClickListener(v -> startRecording());
        btnStop.setOnClickListener(v -> stopRecording());
        btnPlay.setOnClickListener(v -> playRecording());

        // В начале доступна только кнопка "Запись"
        btnStop.setEnabled(false);
        btnPlay.setEnabled(false);

        return view;
    }

    // Метод для начала записи звука
    private void startRecording() {
        // Имя файла, куда будет сохранена запись
        fileName = getActivity().getExternalCacheDir().getAbsolutePath() + "/audiorecord.3gp";

        // Настраиваем MediaRecorder
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // источник — микрофон
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // формат .3gp
        recorder.setOutputFile(fileName); // путь к файлу
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // кодек

        try {
            recorder.prepare(); // готовим к записи
            recorder.start();   // начинаем запись

            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            btnPlay.setEnabled(false);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка записи", Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для завершения записи
    private void stopRecording() {
        try {
            recorder.stop(); // остановка записи
            recorder.release(); // освобождение ресурсов
            recorder = null;

            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            btnPlay.setEnabled(true);

            Toast.makeText(getContext(), "Аудио сохранено", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка при остановке", Toast.LENGTH_SHORT).show();
        }
    }

    // Упрощённое воспроизведение — запускается по нажатию на кнопку "Прослушать"
    private void playRecording() {
        player = new MediaPlayer();

        try {
            player.setDataSource(fileName); // путь к ранее записанному файлу
            player.prepare(); // подготавливаем к воспроизведению
            player.start(); // воспроизводим

            Toast.makeText(getContext(), "Воспроизведение...", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
        }
    }

    // Освобождение ресурсов при закрытии фрагмента
    @Override
    public void onStop() {
        super.onStop();

        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }
}
