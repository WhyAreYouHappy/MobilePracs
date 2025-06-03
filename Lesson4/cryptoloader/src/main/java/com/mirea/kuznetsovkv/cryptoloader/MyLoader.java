package com.mirea.kuznetsovkv.cryptoloader;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MyLoader extends AsyncTaskLoader<String> {

    // Ключи для извлечения данных из Bundle
    public static final String ARG_CIPHER = "cipher"; // ключ для зашифрованного текста
    public static final String ARG_KEY = "key";       // ключ для байтового массива ключа

    private final Bundle args;

    // Конструктор — получает context и данные (зашифрованный текст + ключ)
    public MyLoader(@NonNull Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    // Метод вызывается при запуске загрузчика
    @Override
    protected void onStartLoading() {
        forceLoad(); // запускаем loadInBackground() вручную
    }

    // Основная работа — выполняется в фоновом потоке
    @Override
    public String loadInBackground() {
        SystemClock.sleep(3000); // симуляция долгой операции (например, сети)

        // Получаем зашифрованный текст и ключ из Bundle
        byte[] cipherText = args.getByteArray(ARG_CIPHER);
        byte[] keyBytes = args.getByteArray(ARG_KEY);

        // Восстанавливаем AES-ключ из байтов
        SecretKey originalKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");

        try {
            // Создаём и настраиваем шифратор для режима DECRYPT
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, originalKey);

            // Расшифровываем и возвращаем результат в виде строки
            byte[] decryptedBytes = cipher.doFinal(cipherText);
            return new String(decryptedBytes);
        } catch (Exception e) {
            // В случае ошибки — возвращаем сообщение об ошибке
            return "Ошибка дешифровки: " + e.getMessage();
        }
    }
}

