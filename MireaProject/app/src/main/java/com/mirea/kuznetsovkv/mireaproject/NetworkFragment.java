package com.mirea.kuznetsovkv.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NetworkFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Создаем корневой контейнер
        View rootView = inflater.inflate(R.layout.fragment_network, container, false);

        // Получаем текущую дату и форматируем ее
        String currentDate = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        // Находим TextView в макете
        TextView infoTextView = rootView.findViewById(R.id.infoTextView);

        // Устанавливаем текст с красивым форматированием
        String infoText = getString(R.string.network_fragment_title) + "\n\n" +
                getString(R.string.current_date_label) + " " + currentDate + "\n\n" +
                getString(R.string.network_fragment_description);

        infoTextView.setText(infoText);

        return rootView;
    }
}