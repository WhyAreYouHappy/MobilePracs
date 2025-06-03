package com.mirea.kuznetsovkv.internalfilestorage;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String FILE_NAME = "historical_date.txt";

    private EditText dateEditText;
    private EditText descriptionEditText;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateEditText = findViewById(R.id.dateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        resultTextView = findViewById(R.id.resultTextView);
        Button saveButton = findViewById(R.id.saveButton);
        Button loadButton = findViewById(R.id.loadButton);

        saveButton.setOnClickListener(v -> saveToFile());
        loadButton.setOnClickListener(v -> loadFromFile());
    }

    private void saveToFile() {
        String date = dateEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        if (date.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        String content = date + "\n\n" + description;

        try (FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes());
            Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
            dateEditText.setText("");
            descriptionEditText.setText("");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFromFile() {
        try (FileInputStream fis = openFileInput(FILE_NAME)) {
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            String text = new String(bytes);
            resultTextView.setText(text);
            Toast.makeText(this, "Данные загружены", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show();
        }
    }
}