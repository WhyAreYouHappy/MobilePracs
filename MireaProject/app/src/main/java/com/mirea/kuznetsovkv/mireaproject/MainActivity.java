package com.mirea.kuznetsovkv.mireaproject;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.content.pm.PackageManager;
import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.Menu;
import android.view.View;
import android.content.Intent;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.mirea.kuznetsovkv.mireaproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final String TAG = "NavigationDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity created");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        Log.d(TAG, "Home ID: " + R.id.nav_home);
        Log.d(TAG, "Gallery ID: " + R.id.nav_gallery);
        Log.d(TAG, "Slideshow ID: " + R.id.nav_slideshow);
        Log.d(TAG, "Data ID: " + R.id.nav_data);
        Log.d(TAG, "WebView ID: " + R.id.nav_webview);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.nav_data,
                R.id.nav_webview,
                R.id.nav_compass,
                R.id.nav_camera,
                R.id.nav_audio,
                R.id.nav_profile,
                R.id.nav_files
        ).setOpenableLayout(drawer).build();

        try {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            Log.d(TAG, "Navigation setup completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Navigation setup failed", e);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            Log.d(TAG, "Menu item selected: " + item.getItemId() + " - " + item.getTitle());

            try {
                boolean handled = NavigationUI.onNavDestinationSelected(item,
                        Navigation.findNavController(this, R.id.nav_host_fragment_content_main));

                if (!handled) {
                    Log.w(TAG, "Navigation destination not handled: " + item.getTitle());
                }

                binding.drawerLayout.closeDrawer(navigationView);
                return handled;
            } catch (Exception e) {
                Log.e(TAG, "Navigation error for item: " + item.getTitle(), e);
                return false;
            }
        });

        requestNotificationPermission();
        requestHardwarePermissions();
    }

    @Override
    public boolean onSupportNavigateUp() {
        try {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
        } catch (Exception e) {
            Log.e(TAG, "NavigateUp error", e);
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Activity started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Activity resumed");
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void requestHardwarePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };

            boolean needRequest = false;
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    needRequest = true;
                    break;
                }
            }

            if (needRequest) {
                ActivityCompat.requestPermissions(this, permissions, 100);
            }
        }
    }
}
