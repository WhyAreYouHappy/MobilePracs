package com.mirea.kuznetsovkv.camera;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.mirea.kuznetsovkv.camera.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ActivityMainBinding binding;
    private Uri imageUri; // URI для сохранения сделанной фотографии

    // Лаунчер для запуска активности камеры и получения результата
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Регистрация ActivityResultLauncher
        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Обработка результата после съемки
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Фотография успешно сделана и сохранена по imageUri
                        binding.imageView.setImageURI(imageUri);
                    } else {
                        // Пользователь отменил съемку или произошла ошибка
                        Toast.makeText(this, "Съемка отменена или произошла ошибка", Toast.LENGTH_SHORT).show();
                        // Опционально: удалить созданный пустой файл, если он был создан
                        if (imageUri != null) {
                            // Файл может остаться пустым, если съемка отменена.
                            // Логика удаления здесь зависит от требований.
                            // File file = new File(imageUri.getPath());
                            // if (file.exists()) file.delete();
                        }
                    }
                }
        );

        // Установка слушателя нажатия на ImageView для запуска камеры
        binding.imageView.setOnClickListener(v -> {
            // Проверка наличия разрешений перед запуском камеры
            if (checkPermissions()) {
                dispatchTakePictureIntent(); // Запуск намерения камеры
            } else {
                requestPermissions(); // Запрос разрешений, если их нет
            }
        });

        // Начальная проверка разрешений при создании активности
        if (!checkPermissions()) {
            requestPermissions();
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
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // Разрешения получены, можно выполнить действие (опционально запустить камеру сразу)
                // dispatchTakePictureIntent();
                Toast.makeText(this, "Все разрешения получены", Toast.LENGTH_SHORT).show();
            } else {
                // Разрешения не получены, уведомить пользователя
                Toast.makeText(this, "Необходимы разрешения для работы с камерой", Toast.LENGTH_LONG).show();
                // Опционально: отключить функционал камеры или закрыть приложение
            }
        }
    }

    // Метод для создания временного файла изображения
    private File createImageFile() throws IOException {
        // Создание имени файла на основе метки времени
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // Получение каталога для сохранения изображений в приватном внешнем хранилище
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir == null) {
            // Обработка ошибки: внешнее хранилище недоступно
            Log.e("CameraTask", "Внешнее хранилище недоступно");
            throw new IOException("Внешнее хранилище недоступно");
        }
        // Создание временного файла
        File image = File.createTempFile(
                imageFileName,  /* префикс */
                ".jpg",         /* суффикс */
                storageDir      /* директория */
        );
        return image;
    }

    // Метод для запуска намерения (Intent) для съемки фото
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Убедиться, что есть приложение, которое может обработать это намерение
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Создать файл, куда будет сохранено фото
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Ошибка при создании файла
                Log.e("CameraTask", "Ошибка при создании файла: " + ex.getMessage());
                Toast.makeText(this, "Ошибка при создании файла для фото", Toast.LENGTH_SHORT).show();
            }
            // Продолжить, только если файл успешно создан
            if (photoFile != null) {
                // Получить URI для файла с помощью FileProvider
                // authorities должны совпадать с объявлением в AndroidManifest.xml
                String authorities = getApplicationContext().getPackageName() + ".fileprovider";
                imageUri = FileProvider.getUriForFile(this, authorities, photoFile);

                // Добавить URI в намерение, чтобы камера сохранила фото по этому пути
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                // Запустить активность камеры
                cameraActivityResultLauncher.launch(takePictureIntent);
            }
        } else {
            Toast.makeText(this, "Нет приложения для съемки фото", Toast.LENGTH_SHORT).show();
        }
    }
}