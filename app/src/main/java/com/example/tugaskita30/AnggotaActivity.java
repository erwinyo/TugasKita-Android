package com.example.tugaskita30;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnggotaActivity extends AppCompatActivity implements DialogInfo.DialogInfoListener {

    Session session;
    Global global = Global.getInstance();
    String URL_POST_LOAD = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/load_anggota.php");
    TextView processing_aggt_text;
    private List<Anggota> recyclerList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anggota);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new Session(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogInfo("Semua daftar user di dalam grup");
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////

        processing_aggt_text = findViewById(R.id.processing_aggt_text);

        recyclerList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView = findViewById(R.id.anggota_aggt_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ListAdapterAnggota(this, recyclerList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, 8));
        recyclerView.setAdapter(mAdapter);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_LOAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");

                    if (success.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("anggota");
                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String w_avatar = object.getString("user_avatar");
                                String w_avatar_alternative = object.getString("user_avatar_alternative");
                                String w_status = object.getString("user_status");
                                String w_id = object.getString("user_id");
                                String w_namalengkap = object.getString("user_namalengkap");
                                String w_usernama = object.getString("user_nama");
                                String w_usercerita = object.getString("user_cerita");

                                recyclerList.add(new Anggota(w_id, w_avatar, w_avatar_alternative, w_namalengkap, format(w_usercerita, 30), w_usernama, w_usercerita));
                            }
                            mAdapter.notifyDataSetChanged();
                            processing_aggt_text.setText("Loaded");
                        } else {
                            processing_aggt_text.setText("There's no any member yet");
                        }
                    } else {
                        processing_aggt_text.setText("Failed to load!");
                        openErrorDialog("ERROR: ".concat(message));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    processing_aggt_text.setText("Failed to load!");
                    openErrorDialog("JSON error: ".concat(e.toString()));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                processing_aggt_text.setText("Failed to load!");
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
                params.put("userid", session.getUserId());
                params.put("grupid", global.getDataGrupId());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Anggota anggota = recyclerList.get(position);
                Intent intent = new Intent(AnggotaActivity.this, GuestProfileActivity.class);
                intent.putExtra("profile", anggota.getImage());
                intent.putExtra("namalengkap", anggota.getHeader());
                intent.putExtra("username", anggota.getUsername());
                intent.putExtra("story", anggota.getStory());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public void openDialogInfo(String info) {
        global.setDataInfo(info);
        DialogInfo dialogInfo = new DialogInfo();
        dialogInfo.show(getSupportFragmentManager(), "Info dialog");
    }

    public String format(String text, int length) {
        String result = "";
        for (int i = 0; i < text.length(); i++) {
            if (i + 1 <= length) {
                result += text.charAt(i);
            } else {
                result += "...";
                break;
            }
        }
        return result;
    }

    public void openErrorDialog(String errorString) {
        Snackbar.make(recyclerView, errorString, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
