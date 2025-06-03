package com.mirea.kuznetsovkv.mireaproject;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mirea.kuznetsovkv.mireaproject.R;
import com.mirea.kuznetsovkv.mireaproject.PlayerService;

public class PlayerFragment extends Fragment {
    private PlayerService playerService;
    private boolean isBound = false;
    private TextView statusText;
    private Button playButton, pauseButton, stopButton;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
            playerService = binder.getService();
            isBound = true;
            updateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        statusText = view.findViewById(R.id.status_text);
        playButton = view.findViewById(R.id.play_button);
        pauseButton = view.findViewById(R.id.pause_button);
        stopButton = view.findViewById(R.id.stop_button);

        playButton.setOnClickListener(v -> {
            if (isBound) {
                playerService.play();
                updateUI();
            }
        });

        pauseButton.setOnClickListener(v -> {
            if (isBound) {
                playerService.pause();
                updateUI();
            }
        });

        stopButton.setOnClickListener(v -> {
            if (isBound) {
                playerService.stop();
                updateUI();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), PlayerService.class);
        requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        requireActivity().startService(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isBound) {
            requireActivity().unbindService(connection);
            isBound = false;
        }
    }

    private void updateUI() {
        if (isBound) {
            if (playerService.isPlaying()) {
                statusText.setText("Статус: Играет");
                playButton.setEnabled(false);
                pauseButton.setEnabled(true);
                stopButton.setEnabled(true);
            } else {
                statusText.setText("Статус: Пауза");
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                stopButton.setEnabled(true);
            }
        }
    }
}