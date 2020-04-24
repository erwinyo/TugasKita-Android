package com.management.tugaskita;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cloudinary.android.MediaManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    Session session;
    Global global = Global.getInstance();
    String TAG = "TAG";

    String URL = "http://".concat(global.getDataHosting());
    String URL_POST_LOGIN = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/login.php");
    String URL_MAINTANCE = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/information.php");
    Context context;

    TextView status_splash_text;
    String tempToken;

    WebView web;
    CookieManager cookieManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        session = new Session(this);
        context = this;
        status_splash_text = findViewById(R.id.status_splash_text);

        // Cloudinary API
        Map config = new HashMap();
        config.put("cloud_name", "dizwvnwu0");
        MediaManager.init(this, config);

        // ANDROID VERSION WARNING
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(SplashActivity.this, "Versi Android Anda terlau rendah, beberapa fungsi akan tidak bekerja ", Toast.LENGTH_LONG).show();
        }

        // Google Cloud Messaging Firebase
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult().getToken();
                tempToken = token;

                // Log and toast
                String msg = getString(R.string.msg_token_fmt, token);
                Log.d(TAG, msg);
            }
        });

        // GET COOKIE UNTUK SUB-DOMAIN ---- PERLU SEKALI UNTUK MENJAGA TIDAK ERROR
        CookieSyncManager.createInstance(context);
        cookieManager = CookieManager.getInstance();

        web = findViewById(R.id.web);
        web.getSettings().setJavaScriptEnabled(true);
        web.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                cookieManager.setAcceptCookie(true);
                String cookieStr = cookieManager.getCookie(url);

                // THIS IS COOKIR __TEST AND SET IT
                String cookie = cookieStr.split("=")[1];
                global.setDataCookie(cookie);

                cekServer();
            }
        });

        web.setWebChromeClient(new WebChromeClient());
        web.loadUrl(URL);
        // --------- //
        web.clearCache(true);
        web.clearHistory();

        cookieManager.removeAllCookie();
        cookieManager.removeSessionCookie();
    }

    public void cekServer() {
        // GET CURRENT MAINTANCE INFORMATION
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MAINTANCE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            if (success.equals("1")) {
                                String id = jsonObject.getString("id");
                                String nama = jsonObject.getString("nama");

                                if (id.equals("1")) {
                                    Intent intent = new Intent(SplashActivity.this, InformationActivity.class);
                                    intent.putExtra("type", "SERVER MAINTANCE");
                                    intent.putExtra("data", "server dalam pembenahan...");
                                    startActivity(intent);
                                    finish();
                                } else if (id.equals("2")) {
                                    // UPDATE
                                    String version = jsonObject.getString("version"); // GET THE NEWEST VERSION FROM DATABASE
                                    int versioncode = Integer.parseInt(jsonObject.getString("versioncode")); // GET THE NEWEST VERSION CODE FROM DATABASE
                                    PackageInfo pinfo = null;
                                    try {
                                        pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        e.printStackTrace();
                                        Toast.makeText(SplashActivity.this, "Package Info Error: "+e.toString() , Toast.LENGTH_SHORT).show();
                                    }
                                    int versionNumber = pinfo.versionCode;
                                    String versionName = pinfo.versionName;

                                    if (versionNumber < versioncode) {
                                        // Update tersedia
                                        Intent intent = new Intent(SplashActivity.this, InformationActivity.class);
                                        intent.putExtra("type", "UPDATE");
                                        intent.putExtra("data", "versi baru telah tersedia...");
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // UPDATED
                                        // CALL AUTOMATE LOGIN AGAIN
                                        // Session Check
                                        if (!session.getUserEmail().isEmpty() && !session.getUserPassword().isEmpty()) {
                                            status_splash_text.setText("connecting to server...");
                                            loginAutomate(session.getUserEmail(), session.getUserPassword());
                                        } else {
                                            startActivity(new Intent(SplashActivity.this, SignupActivity.class));
                                            finish();
                                        }
                                    }
                                }
                            } else {
                                // IF NO UNDER MAINTANCE
                                if (!session.getUserEmail().isEmpty() && !session.getUserPassword().isEmpty()) {
                                    status_splash_text.setText("connecting to server...");
                                    loginAutomate(session.getUserEmail(), session.getUserPassword());
                                } else {
                                    startActivity(new Intent(SplashActivity.this, SignupActivity.class));
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Intent intent = new Intent(SplashActivity.this, ErrorActivity.class);
                            intent.putExtra("error", "JSON error: "+e.toString());
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (!session.getUserId().isEmpty() && !session.getUserPassword().isEmpty()) {
                            startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            String volleyError = "Unknown Error";
                            if (error instanceof TimeoutError) {
                                //This indicates that the reuest has time out
                                volleyError = "Server terlalu lama merespon! coba lagi nanti";
                                // Redirecting to website version
                                Intent intent = new Intent(SplashActivity.this, AlternativeActivity.class);
                                intent.putExtra("error", volleyError);
                                startActivity(intent);
                                finish();
                            } else if (error instanceof NoConnectionError) {
                                //This indicates that the reuest has no connection
                                volleyError = "Check koneksi internet Anda!";
                                Intent intent = new Intent(SplashActivity.this, ErrorActivity.class);
                                intent.putExtra("error", volleyError);
                                startActivity(intent);
                                finish();
                            } else if (error instanceof AuthFailureError) {
                                //Error indicating that there was an Authentication Failure while performing the request
                                volleyError = "Authentication gagal! coba beberapa saat lagi";
                                // Redirecting to website version
                                Intent intent = new Intent(SplashActivity.this, AlternativeActivity.class);
                                intent.putExtra("error", volleyError);
                                startActivity(intent);
                                finish();
                            } else if (error instanceof ServerError) {
                                //Indicates that the server responded with a error response
                                volleyError = "Server Error!";
                                // Redirecting to website version
                                Intent intent = new Intent(SplashActivity.this, AlternativeActivity.class);
                                intent.putExtra("error", volleyError);
                                startActivity(intent);
                                finish();
                            } else if (error instanceof NetworkError) {
                                //Indicates that there was network error while performing the request
                                volleyError = "Network Anda mengalami error!";
                                // Redirecting to website version
                                Intent intent = new Intent(SplashActivity.this, AlternativeActivity.class);
                                intent.putExtra("error", volleyError);
                                startActivity(intent);
                                finish();
                            } else if (error instanceof ParseError) {
                                // Indicates that the server response could not be parsed
                                volleyError = "Server tidak bisa parse permintaan Anda!";
                                // Redirecting to website version
                                Intent intent = new Intent(SplashActivity.this, AlternativeActivity.class);
                                intent.putExtra("error", volleyError);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Cookie", "__test=".concat(global.getDataCookie()).concat("; expires=Friday, January 1, 2038 at 6:55:55 AM; path=/"));
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scheduleJob() {
        ComponentName componentName = new ComponentName(this, MyService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000) // 15 minutes or not
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            //Toast.makeText(SplashActivity.this, "Job scheduling started", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(SplashActivity.this, "Job scheduling failed", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
    }


    public void loginAutomate(final String email, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");
                            if (success.equals("1")) {
                                status_splash_text.setText("almost ready...");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String user_id = object.getString("user_id");
                                    String user_namalengkap = object.getString("user_namalengkap");
                                    String user_nama = object.getString("user_nama");
                                    String user_email = object.getString("user_email");
                                    String user_cerita = object.getString("user_cerita");
                                    String user_avatar = object.getString("user_avatar");
                                    String user_avatar_alternative = object.getString("user_avatar_alternative");
                                    String user_point = object.getString("user_point");
                                    String user_notification = object.getString("user_notification");
                                    // GLOBALS
                                    global.setDataUserId(user_id);
                                    global.setDataUserNamaLengkap(user_namalengkap);
                                    global.setDataUserNama(user_nama);
                                    global.setDataUserEmail(user_email);
                                    global.setDataUserCerita(user_cerita);
                                    global.setDataUserAvatar(user_avatar);
                                    global.setDataUserPoint(user_point);
                                    // Start notification service
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        scheduleJob();
                                    }
                                    session.setUserId(user_id);
                                    session.setUserNamaLengkap(user_namalengkap);
                                    session.setUserNama(user_nama);
                                    session.setUserEmail(user_email);
                                    session.setUserCerita(user_cerita);
                                    session.setUserPoint(user_point);
                                    session.setUserAvatar(user_avatar);
                                    session.setUserAvatarAlternative(user_avatar_alternative);
                                    session.setUserNotification(user_notification);

                                    startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                                    finish();
                                }
                            } else {
                                Intent intent = new Intent(SplashActivity.this, ErrorActivity.class);
                                intent.putExtra("error", "Failed: ".concat(message));
                                Toast.makeText(SplashActivity.this, "Hey This is Error!", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Intent intent = new Intent(SplashActivity.this, ErrorActivity.class);
                            intent.putExtra("error", "JSON error: "+e.toString());
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                        finish();
                    }
                })
        {
            @SuppressLint("HardwareIds")
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Cookie", "__test=".concat(global.getDataCookie()).concat("; expires=Friday, January 1, 2038 at 6:55:55 AM; path=/"));
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
        super.onBackPressed();
    }
}
