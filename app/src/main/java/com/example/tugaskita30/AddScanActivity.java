package com.example.tugaskita30;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddScanActivity extends AppCompatActivity {

    Global global = Global.getInstance();
    String URL_POST_TAMBAH = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/register_teman_by_id.php");
    String URL_POST_GET_USER_DATA = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/get_userdata.php");

    ImageView profile_addscanact_image;
    TextView nama_addscanact_text, status_addscanact_text;
    Button tambah_addscanact_button;

    String tempUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scan);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        getUserData(data); // Receive all data scanned user

        profile_addscanact_image = findViewById(R.id.profile_scanaddact_image);
        status_addscanact_text = findViewById(R.id.status_addscanact_text);
        nama_addscanact_text = findViewById(R.id.nama_scanaddact_text);
        tambah_addscanact_button = findViewById(R.id.tambah_scanaddact_button);

        tambah_addscanact_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahTeman(global.getDataUserId(), tempUserId);
            }
        });
    }

    private void tambahTeman(final String userid, final String guestid) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_TAMBAH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Qsuccess = jsonObject.getString("success");
                    String Qmessage = jsonObject.getString("message");
                    if (Qsuccess.equals("1")) {
                        tambah_addscanact_button.setVisibility(View.INVISIBLE);
                        status_addscanact_text.setText("Friend Added!");
                    } else {
                        status_addscanact_text.setText("Already Friend!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(tambah_addscanact_button, "JSON error: " + e.toString(), Snackbar.LENGTH_SHORT).show();
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
                params.put("userid", userid);
                params.put("guestid", guestid);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getUserData(final String userid) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_GET_USER_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Qsuccess = jsonObject.getString("success");
                    String Qmessage = jsonObject.getString("message");
                    if (Qsuccess.equals("1")) {
                        String user_id = jsonObject.getString("user_id");
                        String user_nama = jsonObject.getString("user_nama");
                        String user_namalengkap = jsonObject.getString("user_namalengkap");
                        String user_email = jsonObject.getString("user_email");
                        String user_avatar = jsonObject.getString("user_avatar");

                        String url = MediaManager.get().url().transformation(new Transformation().aspectRatio("1:1").gravity("auto").radius("max").width(1000).crop("fill")).secure(true).generate("tugaskita/profile/".concat(user_avatar.concat(".png")));
                        Picasso.with(AddScanActivity.this).load(url)
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.user)
                                .into(profile_addscanact_image);
                        nama_addscanact_text.setText(user_namalengkap);
                        tempUserId = user_id; // define temp ID untuk tambah pertemanan

                        tambah_addscanact_button.setVisibility(View.VISIBLE); // Load when all done
                    } else {
                        Toast.makeText(AddScanActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(status_addscanact_text, "JSON error: " + e.toString(), Snackbar.LENGTH_SHORT).show();
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
                params.put("userid", userid);
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
        Snackbar.make(tambah_addscanact_button, errorString, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void openScanActivity(View view) {
        finish();
    }
}
