package com.mirea.kuznetsovkv.mireaproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DataFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_data, container, false);
            android.util.Log.d("DataFragment", "Fragment created successfully");
            return view;
        } catch (Exception e) {
            android.util.Log.e("DataFragment", "Error creating fragment", e);
            throw e;
        }
    }
}
