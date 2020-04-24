package com.management.tugaskita;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UndangTemanActivity extends AppCompatActivity implements DialogInfo.DialogInfoListener, DialogUndangTeman.DialogUndangTemanListener {

    Session session;
    Global global = Global.getInstance();
    private String URL_POST_LOAD = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/load_undang_teman.php");
    private String URL_POST_SEND = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/send_invitation.php");

    private List<UndangTeman> recyclerList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    TextView processing_undtmn_text;

    String guestid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undang_teman);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new Session(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogInfo("Undang teman untuk bergabung dalam grup");
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////

        processing_undtmn_text = findViewById(R.id.processing_undtmn_text);

        recyclerList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView = findViewById(R.id.teman_undtmn_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ListAdapterUndangTeman(this, recyclerList);
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
                        JSONArray jsonArray = jsonObject.getJSONArray("listoffriends");
                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String w_id = object.getString("user_id");
                                String w_namalengkap = object.getString("user_namalengkap");
                                String w_username = object.getString("user_nama");
                                String w_avatar = object.getString("user_avatar");
                                String w_avatar_alternative = object.getString("user_avatar_alternative");

                                recyclerList.add(new UndangTeman(w_id, w_avatar, w_avatar_alternative, w_namalengkap, w_username));
                            }
                            mAdapter.notifyDataSetChanged();
                            processing_undtmn_text.setText("Loaded");
                        }
                    } else {
                        processing_undtmn_text.setText("You don't have friend again to invite");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    processing_undtmn_text.setText("Failed to load!");
                    Toast.makeText(UndangTemanActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                processing_undtmn_text.setText("Failed to load!");
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

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Cookie", "__test=".concat(global.getDataCookie()).concat("; expires=Friday, January 1, 2038 at 6:55:55 AM; path=/"));
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                UndangTeman undangTeman = recyclerList.get(position);
                guestid = undangTeman.getId();
                openDialogUndangTeman();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public void openDialogUndangTeman() {
        DialogUndangTeman dialogUndangTeman = new DialogUndangTeman();
        dialogUndangTeman.show(getSupportFragmentManager(), "Undang Teman dialog");
    }

    public void openDialogInfo(String info) {
        global.setDataInfo(info);
        DialogInfo dialogInfo = new DialogInfo();
        dialogInfo.show(getSupportFragmentManager(), "Info dialog");
    }

    public void sendInvitation(final String userid, final String guestid, final String grupid, final String as) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_SEND, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");

                    if (success.equals("1")) {
                        Toast.makeText(UndangTemanActivity.this, "Invitation Sended!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UndangTemanActivity.this, UndangTemanActivity.class));
                        finish();
                    } else {
                        openErrorDialog( "ERROR: ".concat(message));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    openErrorDialog("JSON error: ".concat(e.toString()));
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
                params.put("grupid", grupid);
                params.put("as_status", as);

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
    public void onAdmin() {
        sendInvitation(session.getUserId(), guestid, global.getDataGrupId(), "ADMIN");
    }

    @Override
    public void onAnggota() {
        sendInvitation(session.getUserId(), guestid, global.getDataGrupId(), "ANGGOTA");
    }

    public void openErrorDialog(String errorString) {
        Snackbar.make(recyclerView, errorString, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
