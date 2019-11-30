package com.example.tugaskita30;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ComingSoonActivity extends AppCompatActivity {

    Session session;
    Global global = Global.getInstance();
    EditText ip_cmsact_input;
    Button goto_cmsact_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coming_soon);

        session = new Session(this);

        ip_cmsact_input = findViewById(R.id.ip_cmsact_input);
        goto_cmsact_button = findViewById(R.id.goto_cmsact_button);
        goto_cmsact_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ip_cmsact_input.getText().toString().trim();
                if (ip.isEmpty()) {
                    ip_cmsact_input.setError("input belum masuk!");
                } else {
                    Intent intent = new Intent(ComingSoonActivity.this, CBTActivity.class);
                    intent.putExtra("ip", ip_cmsact_input.getText().toString().trim());
                    startActivity(intent);
                }
            }
        });
    }

    public void openDashboard(View view) {
        finish();
    }

    public void openExamActivity(View view) {
        Intent intent = new Intent(ComingSoonActivity.this, CBTActivity.class);
        intent.putExtra("ip", global.getDataHosting().concat("/tugaskita/webV2/sistem/login?email=").concat(session.getUserEmail()).concat("&password=").concat(session.getUserPassword()).concat("&type=cbt-siswa"));
        startActivity(intent);
    }
}
