package com.mirea.kuznetsovkv.simplefragmentapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Альбомная ориентация - показываем оба фрагмента
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer1, new FirstFragment())
                    .replace(R.id.fragmentContainer2, new SecondFragment())
                    .commit();
        } else {
            // Портретная ориентация - показываем кнопки и один фрагмент
            Button btnFirstFragment = findViewById(R.id.btnFirstFragment);
            Button btnSecondFragment = findViewById(R.id.btnSecondFragment);

            btnFirstFragment.setOnClickListener(v -> showFragment(new FirstFragment()));
            btnSecondFragment.setOnClickListener(v -> showFragment(new SecondFragment()));

            if (savedInstanceState == null) {
                showFragment(new FirstFragment());
            }
        }
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}