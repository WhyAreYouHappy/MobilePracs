package com.mirea.kuznetsovkv.favoritebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ShareActivity extends AppCompatActivity {
    private EditText editTextBook;
    private EditText editTextQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        TextView textViewDevBook = findViewById(R.id.textViewDevBook);
        TextView textViewDevQuote = findViewById(R.id.textViewDevQuote);
        editTextBook = findViewById(R.id.editTextBook);
        editTextQuote = findViewById(R.id.editTextQuote);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String bookName = extras.getString(MainActivity.BOOK_NAME_KEY);
            String quote = extras.getString(MainActivity.QUOTES_KEY);
            textViewDevBook.setText("Любимая книга разработчика: " + bookName);
            textViewDevQuote.setText("Цитата: " + quote);
        }
    }

    public void sendDataBack(View view) {
        String book = editTextBook.getText().toString();
        String quote = editTextQuote.getText().toString();

        if (!book.isEmpty() && !quote.isEmpty()) {
            Intent data = new Intent();
            data.putExtra(MainActivity.USER_MESSAGE,
                    "Название Вашей любимой книги: " + book + ". Цитата: " + quote);
            setResult(Activity.RESULT_OK, data);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }
}