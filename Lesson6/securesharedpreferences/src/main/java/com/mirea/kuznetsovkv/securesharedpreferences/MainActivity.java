package com.mirea.kuznetsovkv.securesharedpreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {
    private static final String SHARED_PREFS_FILE = "secure_poet_prefs";
    private static final String POET_NAME_KEY = "poet_name";
    private static final String POET_IMAGE_KEY = "poet_image";

    private TextView poetNameTextView;
    private EditText poetNameEditText;
    private ImageView poetImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        poetNameTextView = findViewById(R.id.poetNameTextView);
        poetNameEditText = findViewById(R.id.poetNameEditText);
        poetImageView = findViewById(R.id.poetImageView);
        Button saveButton = findViewById(R.id.saveButton);

        // Загружаем сохраненные данные
        loadSecurePreferences();

        saveButton.setOnClickListener(v -> {
            String poetName = poetNameEditText.getText().toString();
            if (!poetName.isEmpty()) {
                saveSecurePreferences(poetName);
                Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Введите имя поэта", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSecurePreferences() {
        try {
            String mainKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            SharedPreferences secureSharedPreferences = EncryptedSharedPreferences.create(
                    SHARED_PREFS_FILE,
                    mainKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            String poetName = secureSharedPreferences.getString(POET_NAME_KEY, "");
            int poetImageRes = secureSharedPreferences.getInt(POET_IMAGE_KEY, R.drawable.amnyam);

            poetNameTextView.setText(poetName.isEmpty() ? "Имя не указано" : poetName);
            poetNameEditText.setText(poetName);
            poetImageView.setImageResource(poetImageRes);

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSecurePreferences(String poetName) {
        try {
            String mainKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            SharedPreferences secureSharedPreferences = EncryptedSharedPreferences.create(
                    SHARED_PREFS_FILE,
                    mainKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            secureSharedPreferences.edit()
                    .putString(POET_NAME_KEY, poetName)
                    .putInt(POET_IMAGE_KEY, R.drawable.amnyam)
                    .apply();

            // Обновляем UI
            poetNameTextView.setText(poetName);
            poetImageView.setImageResource(R.drawable.amnyam);

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка сохранения данных", Toast.LENGTH_SHORT).show();
        }
    }
}