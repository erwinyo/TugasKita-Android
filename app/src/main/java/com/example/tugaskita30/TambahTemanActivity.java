package com.example.tugaskita30;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TambahTemanActivity extends AppCompatActivity {

    Context context = this;
    Global global = Global.getInstance();
    String URL_POST_TAMBAH = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/register_teman.php");
    String URL_POST_CARI = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/view_user.php");
    String CHANNEL_ID = "personal notification";
    int notificationId = 001;

    LinearLayout profile_info_linearlayout;
    ImageView profile_image;
    TextView namalengkap_text, info_text;
    EditText username_input;
    Button tambah_button;

    String Qsuccess, Qmessage, Qusername, Qnamalengkap, Qid, Qavatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_teman);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        profile_info_linearlayout = findViewById(R.id.profile_info_tbhtmnact_linearlayout);
        profile_image = findViewById(R.id.profile_tbhtmnact_image);
        namalengkap_text = findViewById(R.id.namalengkap_tbhtmnact_text);
        info_text = findViewById(R.id.info_tbhtmnact_text);
        username_input = findViewById(R.id.username_tbhtmnact_input);
        tambah_button = findViewById(R.id.tambah_tbhtmnact_button);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Searching...", Toast.LENGTH_SHORT).show();
                String search = username_input.getText().toString().trim();
                cariTeman(global.getDataUserId(), search);
            }
        });

        tambah_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambah_button.setText("loading...");
                String search = username_input.getText().toString().trim();
                tambahTeman(global.getDataUserId(), search);
            }
        });
    }

    private void tambahTeman(final String userid, final String guestusername) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_TAMBAH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Qsuccess = jsonObject.getString("success");
                    Qmessage = jsonObject.getString("message");
                    if (Qsuccess.equals("1")) {
                        tambah_button.setVisibility(View.INVISIBLE);
                        Snackbar.make(tambah_button, "Teman ditambahkan", Snackbar.LENGTH_SHORT).show();
                    } else {
                        tambah_button.setVisibility(View.INVISIBLE);
                        info_text.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(tambah_button, "JSON error: "+e.toString(), Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    //This indicates that the reuest has time out
                    Intent intent = new Intent(TambahTemanActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Server terlalu lama merespon! coba lagi nanti");
                    startActivity(intent);
                    finish();
                } else if (error instanceof NoConnectionError) {
                    //This indicates that the reuest has no connection
                    Intent intent = new Intent(TambahTemanActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Check koneksi internet Anda!");
                    startActivity(intent);
                    finish();
                } else if (error instanceof AuthFailureError) {
                    //Error indicating that there was an Authentication Failure while performing the request
                    Intent intent = new Intent(TambahTemanActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Authentication gagal! coba beberapa saat lagi");
                    startActivity(intent);
                    finish();
                } else if (error instanceof ServerError) {
                    //Indicates that the server responded with a error response
                    Intent intent = new Intent(TambahTemanActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Server Error!");
                    startActivity(intent);
                    finish();
                } else if (error instanceof NetworkError) {
                    //Indicates that there was network error while performing the request
                    Intent intent = new Intent(TambahTemanActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Network Anda mengalami error!");
                    startActivity(intent);
                    finish();
                } else if (error instanceof ParseError) {
                    // Indicates that the server response could not be parsed
                    Intent intent = new Intent(TambahTemanActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Server tidak bisa parse permintaan Anda!");
                    startActivity(intent);
                    finish();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", userid);
                params.put("temanusername", guestusername);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void cariTeman(final String userid, final String guestusername) {
        // Restart Text
        tambah_button.setText("tambah");
        info_text.setVisibility(View.INVISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_CARI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Qsuccess = jsonObject.getString("success");
                    Qmessage = jsonObject.getString("message");
                    if (Qsuccess.equals("1")) {
                        Qid = jsonObject.getString("id");
                        Qnamalengkap = jsonObject.getString("namalengkap");
                        Qusername = jsonObject.getString("username");
                        Qavatar = jsonObject.getString("avatar");

                        // Check username itself
                        if (Qid.equals(userid)) {
                            tambah_button.setVisibility(View.INVISIBLE);
                        } else {
                            tambah_button.setVisibility(View.VISIBLE);
                        }

                        String url = MediaManager.get().url().transformation(new Transformation().aspectRatio("1:1").gravity("auto").radius("max").width(1000).crop("fill")).secure(true).generate("tugaskita/profile/".concat(Qavatar));

                        // Set image & namalengkap
                        namalengkap_text.setText(Qnamalengkap);
                        Picasso.with(context).load(url)
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.user)
                                .into(profile_image);

                        profile_info_linearlayout.setVisibility(View.VISIBLE);
                    } else {
                        // user tidak ditemukan
                        Toast.makeText(context, "User not found!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(tambah_button, "JSON error: " + e.toString(), Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    //This indicates that the reuest has time out
                    Intent intent = new Intent(TambahTemanActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Server terlalu lama merespon! coba lagi nanti");
                    startActivity(intent);
                    finish();
                } else if (error instanceof NoConnectionError) {
                    //This indicates that the reuest has no connection
                    Intent intent = new Intent(TambahTemanActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Check koneksi internet Anda!");
                    startActivity(intent);
                    finish();
                } else if (error instanceof AuthFailureError) {
                    //Error indicating that there was an Authentication Failure while performing the request
                    Intent intent = new Intent(TambahTemanActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Authentication gagal! coba beberapa saat lagi");
                    startActivity(intent);
                    finish();
                } else if (error instanceof ServerError) {
                    //Indicates that the server responded with a error response
                    Intent intent = new Intent(TambahTemanActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Server Error!");
                    startActivity(intent);
                    finish();
                } else if (error instanceof NetworkError) {
                    //Indicates that there was network error while performing the request
                    Intent intent = new Intent(TambahTemanActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Network Anda mengalami error!");
                    startActivity(intent);
                    finish();
                } else if (error instanceof ParseError) {
                    // Indicates that the server response could not be parsed
                    Intent intent = new Intent(TambahTemanActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Server tidak bisa parse permintaan Anda!");
                    startActivity(intent);
                    finish();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", userid);
                params.put("username", guestusername);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
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
