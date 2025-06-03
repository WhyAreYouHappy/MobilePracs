package com.mirea.kuznetsovkv.mireaproject;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.*;

public class FilesFragment extends Fragment {

    private TextView decryptedText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files, container, false);

        decryptedText = view.findViewById(R.id.textViewDecrypted);
        FloatingActionButton fab = view.findViewById(R.id.fab_add);

        fab.setOnClickListener(v -> showInputDialog());

        // Показать расшифрованное содержимое
        String decrypted = readAndDecryptFile();
        if (decrypted != null) {
            decryptedText.setText(decrypted);
        }

        return view;
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Введите текст");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String userInput = input.getText().toString();
            String encrypted = encrypt(userInput);
            writeToFile(encrypted);
            decryptedText.setText(userInput);
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private String encrypt(String text) {
        // Простейшее шифрование: сдвиг символов
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            result.append((char)(c + 3));
        }
        return result.toString();
    }

    private String decrypt(String text) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            result.append((char)(c - 3));
        }
        return result.toString();
    }

    private void writeToFile(String data) {
        try (FileOutputStream fos = getContext().openFileOutput("encrypted.txt", Context.MODE_PRIVATE)) {
            fos.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readAndDecryptFile() {
        try (FileInputStream fis = getContext().openFileInput("encrypted.txt")) {
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            String encrypted = new String(bytes);
            return decrypt(encrypted);
        } catch (IOException e) {
            return null;
        }
    }
}