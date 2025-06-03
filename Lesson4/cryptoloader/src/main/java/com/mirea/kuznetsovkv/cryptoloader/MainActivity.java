package com.mirea.kuznetsovkv.cryptoloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mirea.kuznetsovkv.cryptoloader.databinding.ActivityMainBinding;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final int LOADER_ID = 1111; // Уникальный ID загрузчика
    private ActivityMainBinding binding;
    private byte[] encryptedText; // Зашифрованный текст
    private byte[] keyBytes;      // Ключ шифрования (в байтах)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // При нажатии на кнопку — шифруем текст и запускаем Loader
        binding.buttonEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = binding.editTextMessage.getText().toString();
                if (!input.isEmpty()) {
                    // 1. Генерируем ключ
                    SecretKey key = generateKey();

                    // 2. Шифруем фразу
                    encryptedText = encryptMsg(input, key);

                    // 3. Сохраняем ключ в байтах
                    keyBytes = key.getEncoded();

                    // 4. Подготавливаем данные для передачи в Loader
                    Bundle bundle = new Bundle();
                    bundle.putByteArray(MyLoader.ARG_CIPHER, encryptedText);
                    bundle.putByteArray(MyLoader.ARG_KEY, keyBytes);

                    // 5. Запускаем Loader
                    LoaderManager.getInstance(MainActivity.this)
                            .initLoader(LOADER_ID, bundle, MainActivity.this);
                }
            }
        });
    }

    // Генерация AES-ключа (256 бит)
    private SecretKey generateKey() {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed("some-seed".getBytes()); // любой seed для генерации
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256, sr);
            return new SecretKeySpec(kg.generateKey().getEncoded(), "AES");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Шифрование текста с помощью AES
    private byte[] encryptMsg(String message, SecretKey secret) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            return cipher.doFinal(message.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Создание Loader
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new MyLoader(this, args);
    }

    // Получение результата из Loader
    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String result) {
        Toast.makeText(this, "Расшифровка: " + result, Toast.LENGTH_LONG).show();
    }

    // Сброс Loader (ничего не делаем)
    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {}
}
