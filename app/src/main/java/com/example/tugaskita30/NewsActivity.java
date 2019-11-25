package com.example.tugaskita30;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class NewsActivity extends AppCompatActivity {

    WebView web_newsact_webview;
    ProgressBar progress_newsact_progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Intent intent = getIntent();
        String source = intent.getStringExtra("source");

        progress_newsact_progressbar = findViewById(R.id.progress_newsact_progressbar);

        web_newsact_webview = findViewById(R.id.web_newsact_webview);
        web_newsact_webview.loadUrl("http://sibercenter.com/tugaskita/news/".concat(source).concat(".php"));
        web_newsact_webview.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                web_newsact_webview.setVisibility(View.VISIBLE);
                progress_newsact_progressbar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void openErrorDialog(String errorString) {
        DialogError dialogError = new DialogError(errorString);
        dialogError.show(getSupportFragmentManager(), "Error dialog");
    }
}
