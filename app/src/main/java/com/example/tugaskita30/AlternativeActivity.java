package com.example.tugaskita30;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AlternativeActivity extends AppCompatActivity {

    WebView web_altact_webview;
    ProgressBar progress_altact_progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternative);

        Intent intent = getIntent();
        String error = intent.getStringExtra("error");

        Toast.makeText(this, error.concat(" - Redirecting..."), Toast.LENGTH_SHORT).show();

        progress_altact_progressbar = findViewById(R.id.progress_altact_progressbar);
        progress_altact_progressbar.setVisibility(View.VISIBLE);

        web_altact_webview = findViewById(R.id.web_altsact_webview);
        web_altact_webview .loadUrl("http://sibercenter.com/tugaskita");
        web_altact_webview .setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                web_altact_webview .setVisibility(View.VISIBLE);
                progress_altact_progressbar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void openErrorDialog(String errorString) {
        DialogError dialogError = new DialogError(errorString);
        dialogError.show(getSupportFragmentManager(), "Error dialog");
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
        super.onBackPressed();
    }
}
