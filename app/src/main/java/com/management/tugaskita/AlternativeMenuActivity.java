package com.management.tugaskita;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class AlternativeMenuActivity extends AppCompatActivity implements DialogError.DialogErrorListener {

    Global global = Global.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternative_menu);
    }

    public void openTugasKitaWeb(View view) {
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
            Intent intent = new Intent(AlternativeMenuActivity.this, AlternativeMenuActivity.class);
            intent.putExtra("error", "Android version not compatible");
            startActivity(intent);
            finish();
        } else {
            openErrorPopup("Tidak ada koneksi internet!");
        }

    }

    public void openCBTActivity(View view) {
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
            Intent intent = new Intent(AlternativeMenuActivity.this, CBTActivity.class);
            intent.putExtra("ip", global.getDataHosting().concat("/tugaskita/webV2/cbt"));
            startActivity(intent);
            finish();
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
