package com.management.tugaskita;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InformationActivity extends AppCompatActivity {

    AnimationDrawable logoAnimation;
    ImageView logo;

    TextView type_text, data_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        String data = intent.getStringExtra("data");

        logo = findViewById(R.id.logo_information);
        logo.setBackgroundResource(R.drawable.build_animation);
        logoAnimation = (AnimationDrawable) logo.getBackground();

        type_text = findViewById(R.id.type_information);
        type_text.setText(type);
        data_text = findViewById(R.id.data_information);
        data_text.setText(data);
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

    public void openErrorDialog(String errorString) {
        DialogError dialogError = new DialogError(errorString);
        dialogError.show(getSupportFragmentManager(), "Error dialog");
    }
}
