package com.management.tugaskita;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class secretaryAreaActivity extends AppCompatActivity implements DialogSecretaryAbsent.DialogSecretaryAbsentListener {

    Session session;
    Global global = Global.getInstance();
    String URL_POST_ABSENT = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/load_absent.php");
    String URL_POST_ABSENT_REGIS = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/absent.php");

    private List<SecretaryAbsent> recyclerListAbsent;
    private RecyclerView recyclerViewAbsent;
    private RecyclerView.Adapter mAdapterAbsent;

    boolean TimeoutError = false;
    boolean NoConnectionError = false;
    boolean AuthFailureError = false;
    boolean ServerError = false;
    boolean NetworkError = false;
    boolean ParseError = false;

    TextView status_sctact_text;

    String tempStudentid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secretary_area);

        session = new Session(this);
        status_sctact_text = findViewById(R.id.status_sctact_text);

        // absent
        recyclerListAbsent = new ArrayList<>();
        RecyclerView.LayoutManager layoutManagerAbsent = new LinearLayoutManager(getApplicationContext());
        recyclerViewAbsent = findViewById(R.id.absent_sctact_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerViewAbsent.setHasFixedSize(true);

        // use a linear layout manager
        layoutManagerAbsent = new LinearLayoutManager(this);
        recyclerViewAbsent.setLayoutManager(layoutManagerAbsent);

        // specify an adapter (see also next example)
        mAdapterAbsent = new ListAdapterSecretaryAbsent(this, recyclerListAbsent);
        recyclerViewAbsent.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAbsent.setNestedScrollingEnabled(false);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, 8));
        recyclerViewAbsent.setAdapter(mAdapterAbsent);

        loadAbsent();
    }

    public void loadAbsent() {
        recyclerListAbsent.clear(); // clear the list
        StringRequest stringRequestAbsent = new StringRequest(Request.Method.POST, URL_POST_ABSENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");

                    if (success.equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("absent");
                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String user_id = object.getString("user_id");
                                String user_name = object.getString("user_name");
                                String user_namalengkap = object.getString("user_namalengkap").split(" ")[0];
                                String user_avatar = object.getString("user_avatar");
                                String user_avatar_alternative = object.getString("user_avatar_alternative");
                                String user_story = object.getString("user_story");

                                recyclerListAbsent.add(new SecretaryAbsent(user_id, user_avatar, user_avatar_alternative, user_namalengkap, user_name, user_story));
                                mAdapterAbsent.notifyDataSetChanged();
                                status_sctact_text.setText("status: LOADED");
                            }
                        }
                        session.setJSONObject(jsonObject.toString(), "absent_".concat(global.getDataGrupId()));
                    } else if (success.equals("2")){
                        status_sctact_text.setText("status: NIHIL");
                    } else {
                        status_sctact_text.setText("status: TERJADI KESALAHAN");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    openErrorDialog(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (session.sessionCheckKeyword("absent_".concat(global.getDataGrupId()))) {
                    if (error instanceof TimeoutError && !TimeoutError) {
                        //This indicates that the reuest has time out
                        TimeoutError = true;
                        openErrorDialog("Server terlalu lama merespon! coba lagi nanti ~ ".concat("LAST SESSION LOADED"));
                    } else if (error instanceof NoConnectionError && !NoConnectionError) {
                        //This indicates that the reuest has no connection
                        NoConnectionError = true;
                        openErrorDialog("Check koneksi internet Anda! ~ ".concat("LAST SESSION LOADED"));
                    } else if (error instanceof AuthFailureError && !AuthFailureError) {
                        //Error indicating that there was an Authentication Failure while performing the request
                        AuthFailureError = true;
                        openErrorDialog("Authentication gagal! coba beberapa saat lagi ~ ".concat("LAST SESSION LOADED"));
                    } else if (error instanceof ServerError && !ServerError) {
                        //Indicates that the server responded with a error response
                        ServerError = true;
                        openErrorDialog("Server Error! ~ ".concat("LAST SESSION LOADED"));
                    } else if (error instanceof NetworkError && !NetworkError) {
                        //Indicates that there was network error while performing the request
                        NetworkError = true;
                        openErrorDialog("Network Anda mengalami error! ~ ".concat("LAST SESSION LOADED"));
                    } else if (error instanceof ParseError && !ParseError) {
                        // Indicates that the server response could not be parsed
                        ParseError = true;
                        openErrorDialog("Server tidak bisa parse permintaan Anda! ~ ".concat("LAST SESSION LOADED"));
                    }

                    try {
                        JSONObject jsonObject = new JSONObject(session.getJSONObject("absent_".concat(global.getDataGrupId())));
                        String success = jsonObject.getString("success");
                        String message = jsonObject.getString("message");
                        if (success.equals("1")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("absent");
                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String user_id = object.getString("user_id");
                                    String user_name = object.getString("user_name");
                                    String user_namalengkap = object.getString("user_namalengkap").split(" ")[0];
                                    String user_avatar = object.getString("user_avatar");
                                    String user_avatar_alternative = object.getString("user_avatar_alternative");
                                    String user_story = object.getString("user_story");

                                    recyclerListAbsent.add(new SecretaryAbsent(user_id, user_avatar, user_avatar_alternative, user_namalengkap, user_name, user_story));
                                    mAdapterAbsent.notifyDataSetChanged();
                                    status_sctact_text.setText("status: LOADED ~ connection fail");
                                }
                            }
                            session.setJSONObject(jsonObject.toString(), "absent_".concat(global.getDataGrupId()));
                        } else if (success.equals("2")){
                            status_sctact_text.setText("status: NIHIL ~ connection fail");
                        } else {
                            status_sctact_text.setText("status: TERJADI KESALAHAN ~ connection fail");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    status_sctact_text.setText("status: TIDAK BISA MEMUAT ~ connection fail");
                    if (error instanceof TimeoutError && !TimeoutError) {
                        //This indicates that the reuest has time out
                        TimeoutError = true;
                        openErrorDialog("Server terlalu lama merespon! coba lagi nanti");
                    } else if (error instanceof NoConnectionError && !NoConnectionError) {
                        //This indicates that the reuest has no connection
                        NoConnectionError = true;
                        openErrorDialog("Check koneksi internet Anda!");
                    } else if (error instanceof AuthFailureError && !AuthFailureError) {
                        //Error indicating that there was an Authentication Failure while performing the request
                        AuthFailureError = true;
                        openErrorDialog("Authentication gagal! coba beberapa saat lagi");
                    } else if (error instanceof ServerError && !ServerError) {
                        //Indicates that the server responded with a error response
                        ServerError = true;
                        openErrorDialog("Server Error!");
                    } else if (error instanceof NetworkError && !NetworkError) {
                        //Indicates that there was network error while performing the request
                        NetworkError = true;
                        openErrorDialog("Network Anda mengalami error!");
                    } else if (error instanceof ParseError && !ParseError) {
                        // Indicates that the server response could not be parsed
                        ParseError = true;
                        openErrorDialog("Server tidak bisa parse permintaan Anda!");
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
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
        RequestQueue requestQueueAbsent = Volley.newRequestQueue(this);
        requestQueueAbsent.add(stringRequestAbsent);

        recyclerViewAbsent.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewAbsent, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                SecretaryAbsent secretaryAbsent = recyclerListAbsent.get(position);
                String studentid = secretaryAbsent.getId();

                tempStudentid = studentid;
                openAbsentDialog();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public void openErrorDialog(String errorString) {
        Snackbar.make(recyclerViewAbsent, errorString, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void openLoadingDialog(String loadingString) {
        Snackbar bar = Snackbar.make(recyclerViewAbsent, loadingString, Snackbar.LENGTH_INDEFINITE);
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(com.google.android.material.R.id.snackbar_text).getParent();
        ProgressBar item = new ProgressBar(secretaryAreaActivity.this);
        contentLay.addView(item);
        bar.show();
    }

    public void openAbsentDialog() {
        DialogSecretaryAbsent dialogSecretaryAbsent = new DialogSecretaryAbsent();
        dialogSecretaryAbsent.show(getSupportFragmentManager(), "Secretary Absent dialog");
    }

    @Override
    public void onMasuk() {
        call("MASUK");
    }

    @Override
    public void onIjin() {
        call("IJIN");
    }

    @Override
    public void onSakit() {
        call("SAKIT");
    }

    @Override
    public void onAlpha() {
        call("ALPHA");
    }

    public void call(String type) {
        Date date = new Date();
        SimpleDateFormat formatter_time = new SimpleDateFormat("EE, dd MMMM yyyy HH:mm:ss");

        SimpleDateFormat formatter_date = new SimpleDateFormat("dd-MM-yyyy");
        String SPD_date = formatter_date.format(date);

        absent(tempStudentid, global.getDataGrupId(), SPD_date, formatter_time.format(date), type);
    }

    private void absent(final String userid, final String grupid, final String date, final String time, final String type) {
        openLoadingDialog("Mohon tunggu... sistem melakukan absensi");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_ABSENT_REGIS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Qsuccess = jsonObject.getString("success");
                    String Qmessage = jsonObject.getString("message");

                    if (Qsuccess.equals("1")) {
                        global.setDataIntentKY("update-absent");
                        Toast.makeText(secretaryAreaActivity.this, "Berhasil terabsent", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(secretaryAreaActivity.this, secretaryAreaActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(secretaryAreaActivity.this, "JSON error: " + e.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("date", date);
                params.put("time", time);
                params.put("type", type);
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
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();
        data.putExtra("intentCallbackSecretaryArea", global.getDataIntentKY());
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }
}
