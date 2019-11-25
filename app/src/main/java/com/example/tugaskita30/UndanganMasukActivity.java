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

public class UndanganMasukActivity extends AppCompatActivity  implements DialogInfo.DialogInfoListener, DialogTerimaUndangan.DialogTerimaUndanganListener {

    Global global = Global.getInstance();
    String URL_POST_LOAD = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/load_undangan_masuk.php");
    String URL_POST_ACCEPT = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/accept_undangan.php");
    String URL_POST_DELETE = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/delete_undangan.php");

    private List<UndanganMasuk> recyclerList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    TextView status_udm_text;
    int positionUndanganMasuk = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undangan_masuk);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogInfo("Pilih salah satu untuk menerima undangan. TERIMA atau TOLAK.");
            }
        });

        status_udm_text = findViewById(R.id.status_udm_text);

        /////////////////////////////////////////////////////////////////////////////////////////////

        recyclerList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView = findViewById(R.id.udm_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ListAdapterUndanganMasuk(this, recyclerList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, 8));
        recyclerView.setAdapter(mAdapter);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_LOAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("listofinvitation");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        String invite_from_nama = object.getString("invite_from");
                        String invite_from_id = object.getString("invite_from_id");
                        String invite_to_grup_ID = object.getString("invite_to_grup_id");
                        String invite_to_grup_nama = object.getString("invite_to_grup");
                        String invite_from_avatar = object.getString("invite_from_image_url");
                        String invite_from_avatar_alternative = object.getString("invite_from_image_url_alternative");
                        String invite_to_grup_avatar = object.getString("invite_to_grup_image_url");
                        String as_status = object.getString("as_status");

                        recyclerList.add(new UndanganMasuk(invite_from_nama, invite_from_id, invite_to_grup_nama, invite_to_grup_ID, invite_from_avatar, invite_from_avatar_alternative, invite_to_grup_avatar, as_status));
                    }
                    mAdapter.notifyDataSetChanged();
                    status_udm_text.setText("Loaded");
                } catch (JSONException e) {
                    e.printStackTrace();
                    status_udm_text.setText("Something went wrong");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError) {
                    //This indicates that the reuest has time out
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Server terlalu lama merespon! coba lagi nanti");
                    status_udm_text.setText("Server terlalu lama merespon! coba lagi nanti");
                    startActivity(intent);
                    finish();
                } else if (error instanceof NoConnectionError) {
                    //This indicates that the reuest has no connection
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Check koneksi internet Anda!");
                    status_udm_text.setText("Check koneksi internet Anda!");
                    startActivity(intent);
                    finish();
                } else if (error instanceof AuthFailureError) {
                    //Error indicating that there was an Authentication Failure while performing the request
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Authentication gagal! coba beberapa saat lagi");
                    status_udm_text.setText("Authentication gagal! coba beberapa saat lagi");
                    startActivity(intent);
                    finish();
                } else if (error instanceof ServerError) {
                    //Indicates that the server responded with a error response
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Server Error!");
                    status_udm_text.setText("Server Error!");
                    startActivity(intent);
                    finish();
                } else if (error instanceof NetworkError) {
                    //Indicates that there was network error while performing the request
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Network Anda mengalami error!");
                    status_udm_text.setText("Network Anda mengalami error!");
                    startActivity(intent);
                    finish();
                } else if (error instanceof ParseError) {
                    // Indicates that the server response could not be parsed
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Server tidak bisa parse permintaan Anda!");
                    status_udm_text.setText("Server tidak bisa parse permintaan Anda!");
                    startActivity(intent);
                    finish();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", global.getDataUserId());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                UndanganMasuk undanganMasuk = recyclerList.get(position);
                positionUndanganMasuk = position;
                global.setDataUserIdUndanganMasuk(undanganMasuk.getUserId());
                global.setDataGrupIdUndanganMasuk(undanganMasuk.getGrupId());
                global.setDataGrupNamaUndanganMasuk(undanganMasuk.getInviteToGrup());
                global.setDataGrupAsStatusUndanganMasuk(undanganMasuk.getInviteAsstatus());
                openDialogTerimaUndangan();
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

    public void accept(final String userid, final String grupid, final String as_status) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_ACCEPT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    if (success.equals("1")) {
                        status_udm_text.setText("Undangan diterima!");
                        recyclerList.remove(positionUndanganMasuk);
                        global.setDataIntentKY("undangan-grup");
                        mAdapter.notifyDataSetChanged();
                    } else {
                        status_udm_text.setText("Something went wrong: ".concat(message));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(recyclerView, "Volley error: "+e.toString(), Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    //This indicates that the reuest has time out
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Server terlalu lama merespon! coba lagi nanti");
                    status_udm_text.setText("Server terlalu lama merespon! coba lagi nanti");
                    startActivity(intent);
                    finish();
                } else if (error instanceof NoConnectionError) {
                    //This indicates that the reuest has no connection
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Check koneksi internet Anda!");
                    status_udm_text.setText("Check koneksi internet Anda!");
                    startActivity(intent);
                    finish();
                } else if (error instanceof AuthFailureError) {
                    //Error indicating that there was an Authentication Failure while performing the request
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Authentication gagal! coba beberapa saat lagi");
                    status_udm_text.setText("Authentication gagal! coba beberapa saat lagi");
                    startActivity(intent);
                    finish();
                } else if (error instanceof ServerError) {
                    //Indicates that the server responded with a error response
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Server Error!");
                    status_udm_text.setText("Server Error!");
                    startActivity(intent);
                    finish();
                } else if (error instanceof NetworkError) {
                    //Indicates that there was network error while performing the request
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Network Anda mengalami error!");
                    status_udm_text.setText("Network Anda mengalami error!");
                    startActivity(intent);
                    finish();
                } else if (error instanceof ParseError) {
                    // Indicates that the server response could not be parsed
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Server tidak bisa parse permintaan Anda!");
                    status_udm_text.setText("Server tidak bisa parse permintaan Anda!");
                    startActivity(intent);
                    finish();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", userid);
                params.put("grupid", grupid);
                params.put("as_status", as_status);

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void deleteUndangan(final String fromid, final String toid, final String grupid) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_DELETE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    if (success.equals("1")) {
                        status_udm_text.setText("Undangan berhasil dihapus!");
                        recyclerList.remove(positionUndanganMasuk);
                        global.setDataIntentKY("undangan-grup");
                        mAdapter.notifyDataSetChanged();
                    } else {
                        status_udm_text.setText("Something went wrong: ".concat(message));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(recyclerView, "Volley error: "+e.toString(), Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    //This indicates that the reuest has time out
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Server terlalu lama merespon! coba lagi nanti");
                    status_udm_text.setText("Server terlalu lama merespon! coba lagi nanti");
                    startActivity(intent);
                    finish();
                } else if (error instanceof NoConnectionError) {
                    //This indicates that the reuest has no connection
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Check koneksi internet Anda!");
                    status_udm_text.setText("Check koneksi internet Anda!");
                    startActivity(intent);
                    finish();
                } else if (error instanceof AuthFailureError) {
                    //Error indicating that there was an Authentication Failure while performing the request
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Authentication gagal! coba beberapa saat lagi");
                    status_udm_text.setText("Authentication gagal! coba beberapa saat lagi");
                    startActivity(intent);
                    finish();
                } else if (error instanceof ServerError) {
                    //Indicates that the server responded with a error response
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Server Error!");
                    status_udm_text.setText("Server Error!");
                    startActivity(intent);
                    finish();
                } else if (error instanceof NetworkError) {
                    //Indicates that there was network error while performing the request
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Network Anda mengalami error!");
                    status_udm_text.setText("Network Anda mengalami error!");
                    startActivity(intent);
                    finish();
                } else if (error instanceof ParseError) {
                    // Indicates that the server response could not be parsed
                    Intent intent = new Intent(UndanganMasukActivity.this, ErrorActivity.class);
                    intent.putExtra("error", "Server tidak bisa parse permintaan Anda!");
                    status_udm_text.setText("Server tidak bisa parse permintaan Anda!");
                    startActivity(intent);
                    finish();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("fromid", fromid);
                params.put("toid", toid);
                params.put("grupid", grupid);

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void openDialogTerimaUndangan() {
        DialogTerimaUndangan dialogTerimaUndangan = new DialogTerimaUndangan();
        dialogTerimaUndangan.show(getSupportFragmentManager(), "Example dialog");
    }

    @Override
    public void onAccept() {
        accept(global.getDataUserId(), global.getDataGrupIdUndanganMasuk(), global.getDataGrupAsStatusUndanganMasuk());
    }

    @Override
    public void onDecline() {
        deleteUndangan(global.getDataUserIdUndanganMasuk(), global.getDataUserId(), global.getDataGrupIdUndanganMasuk());
    }

    @Override
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();
        data.putExtra("intentCallbackUndanganMasuk", global.getDataIntentKY());
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }

    public void openErrorDialog(String errorString) {
        DialogError dialogError = new DialogError(errorString);
        dialogError.show(getSupportFragmentManager(), "Error dialog");
    }
}
