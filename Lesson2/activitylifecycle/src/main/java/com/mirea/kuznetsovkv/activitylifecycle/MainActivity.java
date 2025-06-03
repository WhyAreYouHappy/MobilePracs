package com.mirea.kuznetsovkv.activitylifecycle;


import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LifecycleActivity";
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");

        editText = findViewById(R.id.editText);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart" + editText.getText());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume" + editText.getText());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause" + editText.getText());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop" + editText.getText());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart" + editText.getText());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy" + editText.getText());
    }
}