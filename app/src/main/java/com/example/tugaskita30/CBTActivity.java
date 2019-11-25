package com.example.tugaskita30;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CBTActivity extends AppCompatActivity implements DialogCBT.DialogCBTListener, DialogKeluarCBT.DialogKeluarCBTListener {

    Session session;
    Global global = Global.getInstance();
    String URL_POST_SEND_LISTENER_DATA = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/register_log_cbt_data.php");
    String ip;

    WebView cbt_cbtact_webview;
    ProgressBar progress_cbtact_progressbar;
    LinearLayout saving_cbtact_layout;
    TextView text_cbtfinact_text;

    CountDownThread timer;
    Thread t;
    volatile boolean tActive = true;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbt);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new Session(this);

        Intent intent = getIntent();
        ip = intent.getStringExtra("ip");

        createNotificationChannelCBT();
        progress_cbtact_progressbar = findViewById(R.id.progress_cbtact_progressbar);
        cbt_cbtact_webview = findViewById(R.id.cbt_cbtact_webview);

        cbt_cbtact_webview .setVisibility(View.INVISIBLE);
        progress_cbtact_progressbar.setVisibility(View.VISIBLE);

        saving_cbtact_layout = findViewById(R.id.saving_cbtact_layout);
        text_cbtfinact_text = findViewById(R.id.text_cbtfinact_text);

        WebSettings webSettings = cbt_cbtact_webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setBlockNetworkLoads(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);

        cbt_cbtact_webview .loadUrl("http://".concat(ip));
        cbt_cbtact_webview .setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                cbt_cbtact_webview .setVisibility(View.VISIBLE);
                progress_cbtact_progressbar.setVisibility(View.INVISIBLE);
                //storeScreenshot(takescreenshot(cbt_cbtact_webview), "HEY");
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogCBT();
            }
        });

        // Screenshot Guard
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {

        } else {
            // CREATE UNIQUE ID WITH TIME AND DATE
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
                String uniqueID = ft.format(dNow);
            session.setCBT(session.getCBT().concat(uniqueID).concat("~PAUSE").concat("/"));

            // Warning Notification
            final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, global.getChannelIdCbt());
            builder.setContentTitle("CBT Peringatan")
                    .setContentText("10 detik untuk kembali")
                    .setSmallIcon(R.drawable.ic_school_blue_24dp)
                    .setColor(ContextCompat.getColor(this, R.color.blue))
                    .setSound(Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.opening))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(true);

            // Issue the initial notification with zero progress
            final int PROGRESS_MAX = 100;
            final int PROGRESS_CURRENT = 0;
            builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
            notificationManager.notify(404, builder.build());

            timer = new CountDownThread();
            timer.start();


            // Second thread untuk mengakses notifikasi UI
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    int i = 0;
                    tActive = true;
                    while (tActive) {
                        i += 10;
                        builder.setContentText(String.valueOf((PROGRESS_MAX-i)/10).concat(" detik untuk kembali"))
                                .setProgress(PROGRESS_MAX, i, false);
                        notificationManager.notify(404, builder.build());
                        SystemClock.sleep(1000);

                        if (i >= 100) {
                            // Melewati waktu pelanggaran
                            builder.setContentText("Ujian Anda telah dibatalkan")
                                    .setProgress(0, 0, false)
                                    .setOngoing(false);
                            notificationManager.notify(404, builder.build());

                            tActive = false;
                            finish();
                        }
                    }
                    System.out.println("Stopped Running.... t Thread");
                }
            }); t.start();
        }
    }

    public void sendAllListenerData() {
        text_cbtfinact_text.setText("connecting to server...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_SEND_LISTENER_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");

                    if (success.equals("1")) {
                        text_cbtfinact_text.setText("almost done...");
                        // reset session
                        session.setCBT("");
                        // notify user
                        notificationCBT(CBTActivity.this, global.getChannelIdCbt(), "CBT Berakhir", "Terima Kasih telah megikuti CBT TugasKita", 404);
                        SystemClock.sleep(2000);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                text_cbtfinact_text.setText("internet connection error occurred... ");
                SystemClock.sleep(2000);
                sendAllListenerData(); // trying again
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Date dNow = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
                String tanggal = ft.format(dNow);

                params.put("userid", session.getUserId());
                params.put("tanggal", tanggal);
                params.put("log", session.getCBT());

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onAttachedToWindow() {
        // CREATE UNIQUE ID WITH TIME AND DATE
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
        String uniqueID = ft.format(dNow);
        session.setCBT(session.getCBT().concat(uniqueID).concat("~START").concat("/"));

        notificationCBT(this, global.getChannelIdCbt(), "CBT Dimulai", "Selamat Datang ".concat(session.getUserNamaLengkap()), 404);
        super.onAttachedToWindow();
    }

    @Override
    protected void onResume() {
        if (timer != null) {
            // CREATE UNIQUE ID WITH TIME AND DATE
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
            String uniqueID = ft.format(dNow);
            session.setCBT(session.getCBT().concat(uniqueID).concat("~RESUME").concat("/"));

            timer.stopRunning();
            notificationCBT(this, global.getChannelIdCbt(), "CBT", "Terima kasih sudah kembali!", 404);
            tActive = false;
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        // CREATE UNIQUE ID WITH TIME AND DATE
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
        String uniqueID = ft.format(dNow);
        session.setCBT(session.getCBT().concat(uniqueID).concat("~BACK").concat("/"));

        openDialogKeluarCBT();
    }

    public void openDialogCBT() {
        DialogCBT dialogCBT= new DialogCBT();
        dialogCBT.show(getSupportFragmentManager(), "CBT dialog");
    }

    public void openDialogKeluarCBT() {
        DialogKeluarCBT dialogKeluarCBT= new DialogKeluarCBT();
        dialogKeluarCBT.show(getSupportFragmentManager(), "Keluar CBT dialog");
    }

    @Override
    public void onRefresh() {
        // CREATE UNIQUE ID WITH TIME AND DATE
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
        String uniqueID = ft.format(dNow);
        session.setCBT(session.getCBT().concat(uniqueID).concat("~REFRESH").concat("/"));

        cbt_cbtact_webview .setVisibility(View.INVISIBLE);
        progress_cbtact_progressbar.setVisibility(View.VISIBLE);
        cbt_cbtact_webview .loadUrl(cbt_cbtact_webview.getUrl());
        cbt_cbtact_webview .setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                cbt_cbtact_webview .setVisibility(View.VISIBLE);
                progress_cbtact_progressbar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onKeluar() {
        // CREATE UNIQUE ID WITH TIME AND DATE
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
        String uniqueID = ft.format(dNow);
        session.setCBT(session.getCBT().concat(uniqueID).concat("~OUT").concat("/"));

        // Just Tambahan Clear
        cbt_cbtact_webview.clearCache(true );
        cbt_cbtact_webview.clearFormData();
        cbt_cbtact_webview.clearHistory();
        cbt_cbtact_webview.clearMatches();
        cbt_cbtact_webview.clearSslPreferences();
        cbt_cbtact_webview.destroy();
        // Clear Session WebView
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        // saving log_cbt
        cbt_cbtact_webview.setVisibility(View.GONE);
        saving_cbtact_layout.setVisibility(View.VISIBLE);
        sendAllListenerData();
    }

    @Override
    public void onKeluarCBT() {
        // CREATE UNIQUE ID WITH TIME AND DATE
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
        String uniqueID = ft.format(dNow);
        session.setCBT(session.getCBT().concat(uniqueID).concat("~OUT").concat("/"));

        // Just Tambahan Clear
        cbt_cbtact_webview.clearCache(true );
        cbt_cbtact_webview.clearFormData();
        cbt_cbtact_webview.clearHistory();
        cbt_cbtact_webview.clearMatches();
        cbt_cbtact_webview.clearSslPreferences();
        cbt_cbtact_webview.destroy();
        // Clear Session WebView
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        // saving log_cbt
        cbt_cbtact_webview.setVisibility(View.GONE);
        saving_cbtact_layout.setVisibility(View.VISIBLE);
        sendAllListenerData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            startActivity(new Intent(CBTActivity.this, CBTActivity.class));
            finish();
        }
    }

    private void createNotificationChannelCBT() {
        Uri sound = Uri.parse("android.resource://"+this.getPackageName()+"/"+R.raw.opening);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = global.getChannelIdCbt();
            String description = getString(R.string.channel_description);

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(global.getChannelIdCbt(), name, importance);
            channel.setDescription(description);
            channel.setSound(sound, attributes); // This is IMPORTANT
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void notificationCBT(final Context cntx, String CHANNEL_ID, String contentTitle, String contentText, int id) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(cntx);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(cntx, CHANNEL_ID);
        builder.setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_school_blue_24dp)
                .setColor(ContextCompat.getColor(cntx, R.color.blue))
                .setSound(Uri.parse("android.resource://"+cntx.getPackageName()+"/"+R.raw.opening))
                .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationManager.notify(id, builder.build());
    }
}

class CountDownThread extends Thread
{
    //Initially setting the flag as true
    private volatile boolean flag = true;

    int i = 0;

    //This method will set flag as false
    public void stopRunning()
    {
        flag = false;
    }

    @Override
    public void run()
    {
        //Keep the task in while loop
        //This will make thread continue to run until flag becomes false
        while (flag)
        {
            i++;
            System.out.println("Running.... : ".concat(String.valueOf(i)));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Stopped Running.... Count Down Thread");
    }
}
