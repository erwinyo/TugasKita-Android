package com.management.tugaskita;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GrupScanActivity extends AppCompatActivity {

    Session session;
    Global global = Global.getInstance();
    String URL_POST_JOIN = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/register_grup_by_id.php");
    String URL_POST_GET_GRUP_DATA = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/get_grupdata.php");

    ImageView profile_grupscanact_image;
    TextView nama_grupscanact_text, status_grupscanact_text;
    Button join_grupscanact_button;

    String tempGrupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grup_scan);

        session = new Session(this);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        getGrupData(data); // Receive all data scanned class

        profile_grupscanact_image = findViewById(R.id.profile_grupscanact_image);
        status_grupscanact_text = findViewById(R.id.status_grupscanact_text);
        nama_grupscanact_text = findViewById(R.id.nama_grupscanact_text);
        join_grupscanact_button = findViewById(R.id.join_grupscanact_button);

        join_grupscanact_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gabungGrup(session.getUserId(), tempGrupId);
            }
        });
    }

    private void gabungGrup(final String userid, final String grupid) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_JOIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Qsuccess = jsonObject.getString("success");
                    String Qmessage = jsonObject.getString("message");
                    if (Qsuccess.equals("1")) {
                        join_grupscanact_button.setVisibility(View.INVISIBLE);
                        status_grupscanact_text.setText("Berhasil bergabung!");
                        global.setDataIntentKY("update-grup");
                    } else {
                        join_grupscanact_button.setVisibility(View.INVISIBLE);
                        status_grupscanact_text.setText("Anda sudah anggota!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(join_grupscanact_button, "JSON error: " + e.toString(), Snackbar.LENGTH_SHORT).show();
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
                params.put("grupid", grupid);
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


    private void getGrupData(final String grupid) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_GET_GRUP_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Qsuccess = jsonObject.getString("success");
                    String Qmessage = jsonObject.getString("message");
                    if (Qsuccess.equals("1")) {
                        String grup_id = jsonObject.getString("grup_id");
                        String grup_nama = jsonObject.getString("grup_nama");
                        String grup_cerita = jsonObject.getString("grup_cerita");
                        String grup_avatar = jsonObject.getString("grup_avatar");
                        String grup_total = jsonObject.getString("grup_total");

                        String url = MediaManager.get().url().transformation(new Transformation().aspectRatio("1:1").gravity("auto").radius("max").width(200).crop("fill")).secure(true).generate("tugaskita/grup_profile/".concat(grup_avatar.concat(".png")));
                        Picasso.get().load(url)
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.group)
                                .into(profile_grupscanact_image);

                        nama_grupscanact_text.setText(grup_nama);
                        tempGrupId = grup_id;
                    } else {
                        Toast.makeText(GrupScanActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        status_grupscanact_text.setText("Something Error!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(GrupScanActivity.this, "JSON error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    status_grupscanact_text.setText("Something Error!");
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
                params.put("grupid", grupid);
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

    public void openErrorDialog(String errorString) {
        Snackbar.make(join_grupscanact_button, errorString, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void openScanActivity(View view) {
        finish();
    }
}
