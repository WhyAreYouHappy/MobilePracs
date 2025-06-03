package com.mirea.kuznetsovkv.mireaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    private EditText nameInput, ageInput, emailInput;
    private Button saveButton;
    private TextView savedText;

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        nameInput = view.findViewById(R.id.editTextName);
        ageInput = view.findViewById(R.id.editTextAge);
        emailInput = view.findViewById(R.id.editTextEmail);
        saveButton = view.findViewById(R.id.buttonSave);
        savedText = view.findViewById(R.id.textViewSaved);

        sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);

        // Загрузить ранее сохранённые значения
        String name = sharedPreferences.getString("name", "");
        String age = sharedPreferences.getString("age", "");
        String email = sharedPreferences.getString("email", "");

        nameInput.setText(name);
        ageInput.setText(age);
        emailInput.setText(email);

        savedText.setText("Имя: " + name + "\nВозраст: " + age + "\nEmail: " + email);

        saveButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name", nameInput.getText().toString());
            editor.putString("age", ageInput.getText().toString());
            editor.putString("email", emailInput.getText().toString());
            editor.apply();

            savedText.setText("Имя: " + nameInput.getText().toString() +
                    "\nВозраст: " + ageInput.getText().toString() +
                    "\nEmail: " + emailInput.getText().toString());
        });

        return view;
    }
}