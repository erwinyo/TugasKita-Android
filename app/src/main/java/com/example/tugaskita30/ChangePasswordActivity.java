package com.example.tugaskita30;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {

    Global global = Global.getInstance();
    Session session;
    String URL_POST_SEND = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/send_email_password_confirmed.php");
    String URL_POST_RESET = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/reset_password.php");

    EditText password_chgpwd_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new Session(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = password_chgpwd_input.getText().toString().trim();
                reset(password);
            }
        });
        password_chgpwd_input = findViewById(R.id.password_chgpwd_input);
    }

    public void send(final String email) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_SEND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            if (success.equals("1")) {
                                Toast.makeText(ChangePasswordActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                openErrorDialog("ERROR: ".concat(message));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            openErrorDialog("JSON error: ".concat(e.toString()));
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
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("namalengkap", global.getDataUserNamaLengkap());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void reset(final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_RESET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            if (success.equals("1")) {
                                Toast.makeText(ChangePasswordActivity.this, "Password Changed", Toast.LENGTH_SHORT).show();
                                session.setUserPassword(password);
                                send(global.getDataUserEmail());
                            } else {
                                openErrorDialog("ERROR: ".concat(message));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            openErrorDialog("JSON error: ".concat(e.toString()));
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
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", global.getDataUserId());
                params.put("password", password);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void openErrorDialog(String errorString) {
        DialogError dialogError = new DialogError(errorString);
        dialogError.show(getSupportFragmentManager(), "Error dialog");
    }
}
