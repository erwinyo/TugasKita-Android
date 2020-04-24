package com.management.tugaskita;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ComingSoonActivity extends AppCompatActivity implements DialogError.DialogErrorListener {

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
                    // CHECK INTERNET CONNECTION
                    boolean connected = false;
                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        connected = true;
                    }
                    else
                        connected = false;

                    if (connected) {
                        session.setCBT("");
                        Intent intent = new Intent(ComingSoonActivity.this, CBTActivity.class);
                        intent.putExtra("ip", ip_cmsact_input.getText().toString().trim());
                        startActivity(intent);
                    } else {
                        openErrorPopup("Tidak ada koneksi internet!");
                    }
                }
            }
        });
    }

    public void openDashboard(View view) {
        finish();
    }

    public void openExamActivity(View view) {
        // CHECK INTERNET CONNECTION
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        if (connected) {
            session.setCBT("");
            Intent intent = new Intent(ComingSoonActivity.this, CBTActivity.class);
            intent.putExtra("ip", global.getDataHosting().concat("/tugaskita/webV2/sistem/login?email=").concat(session.getUserEmail()).concat("&password=").concat(session.getUserPassword()).concat("&type=cbt-siswa"));
            startActivity(intent);
        } else {
            openErrorPopup("Tidak ada koneksi internet!");
        }
    }

    public void openErrorPopup(String errorstring) {
        DialogError dialogError = new DialogError(errorstring);
        dialogError.show(getSupportFragmentManager(), "Error dialog");
    }

    @Override
    public void onExit() {
        finish();
    }
}
