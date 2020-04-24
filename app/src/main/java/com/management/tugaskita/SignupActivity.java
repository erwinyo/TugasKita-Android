package com.management.tugaskita;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.management.tugaskita.ui.login.LoginActivity;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    Global global = Global.getInstance();
    Session session;
    String URL_SIGNUP = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/register.php");
    EditText namalengkap_daftar_input, username_daftar_input, email_daftar_input, password_daftar_input;
    Button button;

    String URL_POST_LOGIN = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/login.php");
    Context context;

    TextView status_sgnact_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        session = new Session(this);
        context = this;

        status_sgnact_text = findViewById(R.id.status_sgnact_text);
        namalengkap_daftar_input = findViewById(R.id.namalengkap_signup_input);
        username_daftar_input = findViewById(R.id.username_signup_input);
        email_daftar_input = findViewById(R.id.email_signup_input);
        password_daftar_input = findViewById(R.id.password_signup_input);
        button = findViewById(R.id.signup_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setText("Loading...");
                String namalengkap = namalengkap_daftar_input.getText().toString().trim();
                String username = username_daftar_input.getText().toString().trim().toLowerCase();
                String email = email_daftar_input.getText().toString().trim();
                String password = password_daftar_input.getText().toString().trim();
                boolean error = false;
                boolean goodEmail = false;
                if (namalengkap.isEmpty()) {
                    namalengkap_daftar_input.setError("Belum terisi!");
                    error = true;
                }
                if (username.isEmpty()) {
                    username_daftar_input.setError("Belum terisi!");
                    error = true;
                }
                if (email.isEmpty()) {
                    email_daftar_input.setError("Belum terisi!");
                    error = true;
                }
                if (password.isEmpty()) {
                    password_daftar_input.setError("Belum terisi!");
                    error = true;
                }
                // CHECK EMAIL VALID
                if (!email.contains("@gmail.com") || email.contains(" ")) {
                    if (!email.contains("@yahoo.com") || email.contains(" ")) {

                    } else {
                        goodEmail = true;
                    }
                } else {
                    goodEmail = true;
                }

                if (!goodEmail) {
                    email_daftar_input.setError("Alamat email tidak valid!");
                    error = true;
                }

                // CHECK USERNAME VALID
                if (username.contains(" ")) {
                    username_daftar_input.setError("Usernam tidak boleh ada spasi!");
                    error = true;
                }
                // RUN IF NO ERROR
                if (!error) {
                    signup(namalengkap, username, email, password);
                } else {
                    button.setText("Let's Go Again");
                    status_sgnact_text.setText("not valid!");
                }
            }
        });
    }

    private void signup(final String cFullName, final String cUsername, final String cEmail, final String cPassword) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SIGNUP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    if (success.equals("1")) {
                        button.setText("Redirecting...");
                        loginAutomate(cEmail, cPassword);
                    } else {
                        if (success.equals("001")) {
                            status_sgnact_text.setText("try another!");
                            button.setText("Let's Go Again");
                            username_daftar_input.setError("Username sudah diambil!");
                        } else if (success.equals("002")) {
                            status_sgnact_text.setText("try another!");
                            button.setText("Let's Go Again");
                            email_daftar_input.setError("Email sudah terdaftar!");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(button, "JSON error: "+e.toString(), Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    //This indicates that the reuest has time out
                    openErrorDialog("Server terlalu lama merespon! coba lagi nanti");
                } else if (error instanceof NoConnectionError) {
                    //This indicates that the reuest has no connection
                    openErrorDialog("Check koneksi internet Anda!");
                } else if (error instanceof AuthFailureError) {
                    //Error indicating that there was an Authentication Failure while performing the request
                    openErrorDialog("Authentication gagal! coba beberapa saat lagi");
                } else if (error instanceof ServerError) {
                    //Indicates that the server responded with a error response
                    openErrorDialog("Server Error!");
                } else if (error instanceof NetworkError) {
                    //Indicates that there was network error while performing the request
                    openErrorDialog("Network Anda mengalami error!");
                } else if (error instanceof ParseError) {
                    // Indicates that the server response could not be parsed
                    openErrorDialog("Server tidak bisa parse permintaan Anda!");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("fullname", cFullName);
                params.put("username", cUsername);
                params.put("email", cEmail);
                params.put("password", cPassword);
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
                                button.setText("Almost Done...");
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
                                    // SESSION
                                    session.setUserId(user_id);
                                    session.setUserNamaLengkap(user_namalengkap);
                                    session.setUserNama(user_nama);
                                    session.setUserEmail(email);
                                    session.setUserPassword(password);
                                    session.setUserCerita(user_cerita);
                                    session.setUserAvatar(user_avatar);
                                    session.setUserAvatarAlternative(user_avatar_alternative);
                                    session.setUserPoint(user_point);
                                    session.setUserNotification(user_notification);

                                    session.setJSONObject("", "tugas_session_list");
                                    session.setCBT("");
                                    startActivity(new Intent(SignupActivity.this, DashboardActivity.class));
                                    finish();
                                }
                            } else {
                                Intent intent = new Intent(SignupActivity.this, ErrorActivity.class);
                                intent.putExtra("error", "Failed: ".concat(message));
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Intent intent = new Intent(SignupActivity.this, ErrorActivity.class);
                            intent.putExtra("error", "JSON error: "+e.toString());
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            //This indicates that the reuest has time out
                            openErrorDialog("Server terlalu lama merespon! coba lagi nanti");
                        } else if (error instanceof NoConnectionError) {
                            //This indicates that the reuest has no connection
                            openErrorDialog("Check koneksi internet Anda!");
                        } else if (error instanceof AuthFailureError) {
                            //Error indicating that there was an Authentication Failure while performing the request
                            openErrorDialog("Authentication gagal! coba beberapa saat lagi");
                        } else if (error instanceof ServerError) {
                            //Indicates that the server responded with a error response
                            openErrorDialog("Server Error!");
                        } else if (error instanceof NetworkError) {
                            //Indicates that there was network error while performing the request
                            openErrorDialog("Network Anda mengalami error!");
                        } else if (error instanceof ParseError) {
                            // Indicates that the server response could not be parsed
                            openErrorDialog("Server tidak bisa parse permintaan Anda!");
                        }
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

    public void openLogin(View view) {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        finish();
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
