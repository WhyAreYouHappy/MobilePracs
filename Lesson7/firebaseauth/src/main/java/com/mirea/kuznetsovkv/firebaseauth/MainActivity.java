package com.mirea.kuznetsovkv.firebaseauth;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.*;
import com.mirea.kuznetsovkv.firebaseauth.databinding.ActivityMainBinding;


import java.util.Objects;


public class MainActivity extends AppCompatActivity {


    // Тег для логов
    private static final String TAG = MainActivity.class.getSimpleName();


    // ViewBinding для доступа к элементам интерфейса
    private ActivityMainBinding binding;


    // Экземпляр Firebase Auth
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Инициализация ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Получение экземпляра FirebaseAuth
        mAuth = FirebaseAuth.getInstance();


        // Назначение обработчиков нажатия на кнопки
        binding.emailCreateAccountButton.setOnClickListener(v ->
                createAccount(binding.fieldEmail.getText().toString(), binding.fieldPassword.getText().toString()));


        binding.emailSignInButton.setOnClickListener(v ->
                signIn(binding.fieldEmail.getText().toString(), binding.fieldPassword.getText().toString()));


        binding.signOutButton.setOnClickListener(v -> signOut());


        binding.verifyEmailButton.setOnClickListener(v -> sendEmailVerification());
    }


    @Override
    public void onStart() {
        super.onStart();


        // Проверка авторизации пользователя при запуске приложения
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    // Обновление пользовательского интерфейса в зависимости от статуса пользователя
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Если пользователь авторизован
            binding.statusTextView.setText(getString(R.string.emailpassword_status_fmt, user.getEmail(), user.isEmailVerified()));
            binding.detailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));


            // Отображаем кнопки для авторизованного пользователя
            binding.emailPasswordButtons.setVisibility(View.GONE);
            binding.emailPasswordFields.setVisibility(View.GONE);
            binding.signedInButtons.setVisibility(View.VISIBLE);
            binding.verifyEmailButton.setEnabled(!user.isEmailVerified());
        } else {
            // Если пользователь не авторизован
            binding.statusTextView.setText(R.string.signed_out);
            binding.detailTextView.setText(null);


            // Отображаем поля ввода и кнопки входа
            binding.emailPasswordButtons.setVisibility(View.VISIBLE);
            binding.emailPasswordFields.setVisibility(View.VISIBLE);
            binding.signedInButtons.setVisibility(View.GONE);
        }
    }


    // Проверка, что поля email и password заполнены
    private boolean validateForm() {
        boolean valid = true;
        String email = binding.fieldEmail.getText().toString();
        String password = binding.fieldPassword.getText().toString();


        if (email.isEmpty()) {
            binding.fieldEmail.setError("Required.");
            valid = false;
        }
        if (password.isEmpty()) {
            binding.fieldPassword.setError("Required.");
            valid = false;
        }
        return valid;
    }


    // Регистрация нового пользователя
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);


        // Проверяем заполненность полей
        if (!validateForm()) return;


        // Создание пользователя через FirebaseAuth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user); // Обновляем интерфейс
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }


    // Вход пользователя
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);


        // Проверка полей
        if (!validateForm()) return;


        // Попытка входа
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        binding.statusTextView.setText(R.string.auth_failed);
                        updateUI(null);
                    }
                });
    }


    // Выход из аккаунта
    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }


    // Отправка письма с подтверждением email
    private void sendEmailVerification() {
        binding.verifyEmailButton.setEnabled(false); // временно отключаем кнопку
        final FirebaseUser user = mAuth.getCurrentUser();


        // Отправка письма и обработка результата
        Objects.requireNonNull(user).sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    binding.verifyEmailButton.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.getException());
                        Toast.makeText(MainActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}