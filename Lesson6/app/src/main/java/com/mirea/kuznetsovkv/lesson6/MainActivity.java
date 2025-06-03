package com.mirea.kuznetsovkv.lesson6;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private EditText groupNumberEditText;
    private EditText listNumberEditText;
    private EditText favoriteMovieEditText;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов
        groupNumberEditText = findViewById(R.id.groupNumberEditText);
        listNumberEditText = findViewById(R.id.listNumberEditText);
        favoriteMovieEditText = findViewById(R.id.favoriteMovieEditText);
        Button saveButton = findViewById(R.id.saveButton);

        // Получение SharedPreferences
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        // Загрузка сохраненных значений
        loadSavedValues();

        // Обработчик кнопки сохранения
        saveButton.setOnClickListener(v -> saveValues());
    }

    private void loadSavedValues() {
        // Загрузка значений из SharedPreferences
        String groupNumber = sharedPreferences.getString("groupNumber", "");
        int listNumber = sharedPreferences.getInt("listNumber", 0);
        String favoriteMovie = sharedPreferences.getString("favoriteMovie", "");

        // Установка значений в поля ввода
        groupNumberEditText.setText(groupNumber);
        listNumberEditText.setText(listNumber > 0 ? String.valueOf(listNumber) : "");
        favoriteMovieEditText.setText(favoriteMovie);
    }

    private void saveValues() {
        // Получение значений из полей ввода
        String groupNumber = groupNumberEditText.getText().toString();
        int listNumber = 0;
        try {
            listNumber = Integer.parseInt(listNumberEditText.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        String favoriteMovie = favoriteMovieEditText.getText().toString();

        // Сохранение значений в SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("groupNumber", groupNumber);
        editor.putInt("listNumber", listNumber);
        editor.putString("favoriteMovie", favoriteMovie);
        editor.apply();

        Toast.makeText(this, "Настройки сохранены", Toast.LENGTH_SHORT).show();
    }
}