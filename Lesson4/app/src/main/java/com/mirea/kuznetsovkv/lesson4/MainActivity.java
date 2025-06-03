package com.mirea.kuznetsovkv.lesson4;

import android.os.Bundle;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mirea.kuznetsovkv.lesson4.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Установка начальных значений
        binding.songTitle.setText("track1");
        binding.artistName.setText("artist1");
        binding.seekBar.setProgress(5);

        // Обработчики кнопок
        binding.btnPlay.setOnClickListener(v -> {
            isPlaying = !isPlaying;
            binding.btnPlay.setImageResource(
                    isPlaying ? R.drawable.ic_pause : R.drawable.ic_play
            );
        });

        binding.btnPrev.setOnClickListener(v -> {
            // Логика предыдущего трека
        });

        binding.btnNext.setOnClickListener(v -> {
            // Логика следующего трека
        });

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Обновление позиции трека
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}