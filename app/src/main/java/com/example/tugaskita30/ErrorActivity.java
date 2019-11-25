package com.example.tugaskita30;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ErrorActivity extends AppCompatActivity {
    AnimationDrawable logoAnimation;
    ImageView oops_erract_image;
    TextView error_erract_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        Intent intent = getIntent();
        String error = intent.getStringExtra("error");

        oops_erract_image = findViewById(R.id.oops_erract_image);
        oops_erract_image.setBackgroundResource(R.drawable.error_animation);
        logoAnimation = (AnimationDrawable) oops_erract_image.getBackground();

        error_erract_text = findViewById(R.id.error_erract_text);
        error_erract_text.setText(error);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        logoAnimation.start();
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
        super.onBackPressed();
    }
}
