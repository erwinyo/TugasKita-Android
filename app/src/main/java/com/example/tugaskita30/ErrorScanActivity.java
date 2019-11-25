package com.example.tugaskita30;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ErrorScanActivity extends AppCompatActivity {

    TextView status_errscact_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_scan);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");

        status_errscact_text = findViewById(R.id.status_errscact_text);
        status_errscact_text.setText(data);
    }

    public void openScanActivity(View view) {
        finish();
    }
}
