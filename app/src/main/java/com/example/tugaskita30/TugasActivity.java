package com.example.tugaskita30;

import android.app.job.JobScheduler;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TugasActivity extends AppCompatActivity implements DialogKeluarGrup.DialogKeluarGrupListener, DialogTugas.DialogTugasListener {

    Global global = Global.getInstance();
    Session session;
    String URL_POST_TUGAS = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/load_tugas.php");
    String URL_POST_KELUAR_GRUP = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/keluar_grup.php");
    String URL_POST_MATA_PELAJARAN = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/load_pelajaran.php");
    String URL_POST_ABSENT = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/load_absent.php");
    boolean TimeoutError = false;
    boolean NoConnectionError = false;
    boolean AuthFailureError = false;
    boolean ServerError = false;
    boolean NetworkError = false;
    boolean ParseError = false;

    LinearLayout tugas_tgsact_layout;

    int progress = 0;

    int judulLength = 18;
    int ceritaLength = 20;

    private List<JadwalPelajaran> recyclerListJadwal;
    private RecyclerView recyclerViewJadwal;
    private RecyclerView.Adapter mAdapterJadwal;
    private RecyclerView.LayoutManager layoutManagerJadwal;

    private List<JadwalPelajaranItem> recyclerListJadwalItem;
    private RecyclerView recyclerViewJadwalItem;
    private RecyclerView.Adapter mAdapterJadwalItem;
    private RecyclerView.LayoutManager layoutManagerJadwalItem;

    private List<Tugas> recyclerList1;
    private RecyclerView recyclerView1;
    private RecyclerView.Adapter mAdapter1;
    private RecyclerView.LayoutManager layoutManager1;

    private List<Tugas> recyclerList2;
    private RecyclerView recyclerView2;
    private RecyclerView.Adapter mAdapter2;
    private RecyclerView.LayoutManager layoutManager2;

    private List<Tugas> recyclerList3;
    private RecyclerView recyclerView3;
    private RecyclerView.Adapter mAdapter3;
    private RecyclerView.LayoutManager layoutManager3;

    private List<Absent> recyclerListAbsent;
    private RecyclerView recyclerViewAbsent;
    private RecyclerView.Adapter mAdapterAbsent;
    private RecyclerView.LayoutManager layoutManagerAbsent;

    ImageView errToday, errTommorow, errOthers;
    TextView processing_tgsact_text, today_tgsact_text, tommorow_tgsact_text, others_tgsact_text, absent_tgsact_text, progress_tgsact_text;
    Button pengaturan_tgsact_button;
    AdView mAdView1, mAdView2;

    AnimationDrawable bannerTukitAnimation1, bannerTukitAnimation2;
    ImageView banner_tukit1, banner_tukit2;
    ProgressBar progress_tgsact_progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tugas);

        global.setDataInteger(0);

        session = new Session(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tugas_tgsact_layout = findViewById(R.id.tugas_tgsact_layout);

        progress_tgsact_text = findViewById(R.id.progress_tgsact_text);
        progress_tgsact_progressbar = findViewById(R.id.progress_tgsact_progressbar);

        banner_tukit1 = findViewById(R.id.banner1_tgsact_tukit);
        banner_tukit1.setBackgroundResource(R.drawable.banner_animation);
        bannerTukitAnimation1 = (AnimationDrawable) banner_tukit1.getBackground();

        banner_tukit2 = findViewById(R.id.banner2_tgsact_tukit);
        banner_tukit2.setBackgroundResource(R.drawable.banner_animation);
        bannerTukitAnimation2 = (AnimationDrawable) banner_tukit2.getBackground();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTambahTugasActivity(view);
            }
        });

        pengaturan_tgsact_button = (Button) findViewById(R.id.pengaturan_tgsact_button);
        pengaturan_tgsact_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGrupProfileActivity();
            }
        });

        errToday = findViewById(R.id.errToday);
        errTommorow = findViewById(R.id.errTommorow);
        errOthers = findViewById(R.id.errOthers);

        mAdView1 = findViewById(R.id.banner_tgsact_adview);
        mAdView2 = findViewById(R.id.banner_tgsact_adview2);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                isFinished();
            }
        });
        // Load Ads
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("C5829DD8C19EF0D95A5B9D8C91229FCF").build();
        mAdView1.loadAd(adRequest);
        mAdView2.loadAd(adRequest);

        mAdView1.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                progress+=20;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    progress_tgsact_progressbar.setProgress(progress, true);
                else
                    progress_tgsact_progressbar.setProgress(progress);
                progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));

                isFinished();

                mAdView1.setVisibility(View.VISIBLE);
                banner_tukit1.setVisibility(View.GONE);
                // Jadwal Mata Pelajaran
                loadJadwalMataPelajaran();
                // Absent
                loadAbsent();
                // Tugas
                load();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                progress+=20;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    progress_tgsact_progressbar.setProgress(progress, true);
                else
                    progress_tgsact_progressbar.setProgress(progress);
                progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));

                isFinished();

                mAdView1.setVisibility(View.GONE);
                banner_tukit1.setVisibility(View.VISIBLE);
                // Jadwal Mata Pelajaran
                loadJadwalMataPelajaran();
                // Absent
                loadAbsent();
                // Tugas
                load();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        mAdView2.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                progress+=20;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    progress_tgsact_progressbar.setProgress(progress, true);
                else
                    progress_tgsact_progressbar.setProgress(progress);
                progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));

                isFinished();

                mAdView2.setVisibility(View.VISIBLE);
                banner_tukit2.setVisibility(View.GONE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                progress+=20;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    progress_tgsact_progressbar.setProgress(progress, true);
                else
                    progress_tgsact_progressbar.setProgress(progress);
                progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));

                isFinished();

                mAdView2.setVisibility(View.GONE);
                banner_tukit2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////

        processing_tgsact_text = findViewById(R.id.processing_tgsact_text);
        today_tgsact_text = findViewById(R.id.today_tgsact_text);
        tommorow_tgsact_text = findViewById(R.id.tommorow_tgsact_text);
        others_tgsact_text = findViewById(R.id.others_tgsact_text);
        absent_tgsact_text = findViewById(R.id.absent_tgsact_text);

        // jadwal pelajaran item

        recyclerListJadwalItem = new ArrayList<>();
        layoutManagerJadwalItem = new LinearLayoutManager(getApplicationContext());
        recyclerViewJadwalItem = findViewById(R.id.jadwalitem_tgsact_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerViewJadwalItem.setHasFixedSize(true);

        // use a linear layout manager
        layoutManagerJadwalItem = new LinearLayoutManager(this);
        recyclerViewJadwalItem.setLayoutManager(layoutManagerJadwalItem);

        // specify an adapter (see also next example)
        mAdapterJadwalItem = new ListAdapterJadwalPelajaranItem(this, recyclerListJadwalItem);
        recyclerViewJadwalItem.setItemAnimator(new DefaultItemAnimator());
        recyclerViewJadwalItem.setNestedScrollingEnabled(false);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, 8));
        recyclerViewJadwalItem.setAdapter(mAdapterJadwalItem);

        // jadwal pelajaran

        recyclerListJadwal = new ArrayList<>();
        layoutManagerJadwal = new LinearLayoutManager(getApplicationContext());
        recyclerViewJadwal = findViewById(R.id.jadwal_tgsact_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerViewJadwal.setHasFixedSize(true);

        // use a linear layout manager
        layoutManagerJadwal = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewJadwal.setLayoutManager(layoutManagerJadwal);

        // specify an adapter (see also next example)
        mAdapterJadwal = new ListAdapterJadwalPelajaran(this, recyclerListJadwal);
        recyclerViewJadwal.setItemAnimator(new DefaultItemAnimator());
        recyclerViewJadwal.setNestedScrollingEnabled(false);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, 8));
        recyclerViewJadwal.setAdapter(mAdapterJadwal);

        // today

        recyclerList1 = new ArrayList<>();
        layoutManager1 = new LinearLayoutManager(getApplicationContext());
        recyclerView1 = findViewById(R.id.tugas_today_tgsact_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView1.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager1 = new LinearLayoutManager(this);
        recyclerView1.setLayoutManager(layoutManager1);

        // specify an adapter (see also next example)
        mAdapter1 = new ListAdapterTugas(this, recyclerList1);
        recyclerView1.setItemAnimator(new DefaultItemAnimator());
        recyclerView1.setNestedScrollingEnabled(false);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, 8));
        recyclerView1.setAdapter(mAdapter1);

        // tommorow

        recyclerList2 = new ArrayList<>();
        layoutManager2 = new LinearLayoutManager(getApplicationContext());
        recyclerView2 = findViewById(R.id.tugas_tommorow_tgsact_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView2.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager2 = new LinearLayoutManager(this);
        recyclerView2.setLayoutManager(layoutManager2);

        // specify an adapter (see also next example)
        mAdapter2 = new ListAdapterTugas(this, recyclerList2);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setNestedScrollingEnabled(false);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, 8));
        recyclerView2.setAdapter(mAdapter2);

        // others

        recyclerList3 = new ArrayList<>();
        layoutManager3 = new LinearLayoutManager(getApplicationContext());
        recyclerView3 = findViewById(R.id.tugas_others_tgsact_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView3.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager3 = new LinearLayoutManager(this);
        recyclerView3.setLayoutManager(layoutManager3);

        // specify an adapter (see also next example)
        mAdapter3 = new ListAdapterTugas(this, recyclerList3);
        recyclerView3.setItemAnimator(new DefaultItemAnimator());
        recyclerView3.setNestedScrollingEnabled(false);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, 8));
        recyclerView3.setAdapter(mAdapter3);

        // absent
        recyclerListAbsent = new ArrayList<>();
        layoutManagerAbsent = new LinearLayoutManager(getApplicationContext());
        recyclerViewAbsent = findViewById(R.id.absent_tgsact_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerViewAbsent.setHasFixedSize(true);

        // use a linear layout manager
        layoutManagerAbsent = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewAbsent.setLayoutManager(layoutManagerAbsent);

        // specify an adapter (see also next example)
        mAdapterAbsent = new ListAdapterAbsent(this, recyclerListAbsent);
        recyclerViewAbsent.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAbsent.setNestedScrollingEnabled(false);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, 8));
        recyclerViewAbsent.setAdapter(mAdapterAbsent);
    }

    public void loadJadwalMataPelajaran() {
        recyclerListJadwal.clear(); // clear the list
        StringRequest stringRequestJadwal = new StringRequest(Request.Method.POST, URL_POST_MATA_PELAJARAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    if (success.equals("1")) {
                        String pelajaran_monday = jsonObject.getString("pelajaran_monday");
                        String pelajaran_tuesday = jsonObject.getString("pelajaran_tuesday");
                        String pelajaran_wednesday = jsonObject.getString("pelajaran_wednesday");
                        String pelajaran_thursday = jsonObject.getString("pelajaran_thursday");
                        String pelajaran_friday = jsonObject.getString("pelajaran_friday");
                        String pelajaran_saturday = jsonObject.getString("pelajaran_saturday");
                        String pelajaran_sunday = jsonObject.getString("pelajaran_sunday");

                        if (!pelajaran_monday.isEmpty())
                            recyclerListJadwal.add(new JadwalPelajaran("monday", pelajaran_monday));
                        if (!pelajaran_tuesday.isEmpty())
                            recyclerListJadwal.add(new JadwalPelajaran("tuesday", pelajaran_tuesday));
                        if (!pelajaran_wednesday.isEmpty())
                            recyclerListJadwal.add(new JadwalPelajaran("wednesday", pelajaran_wednesday));
                        if (!pelajaran_thursday.isEmpty())
                            recyclerListJadwal.add(new JadwalPelajaran("thursday", pelajaran_thursday));
                        if (!pelajaran_friday.isEmpty())
                            recyclerListJadwal.add(new JadwalPelajaran("friday", pelajaran_friday));
                        if (!pelajaran_saturday.isEmpty())
                            recyclerListJadwal.add(new JadwalPelajaran("saturday", pelajaran_saturday));
                        if (!pelajaran_sunday.isEmpty())
                            recyclerListJadwal.add(new JadwalPelajaran("sunday", pelajaran_sunday));
                        mAdapterJadwal.notifyDataSetChanged();
                        session.setJSONObject(jsonObject.toString(), "matapelajaran_".concat(global.getDataGrupId()));
                        progress+=20;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            progress_tgsact_progressbar.setProgress(progress, true);
                        else
                            progress_tgsact_progressbar.setProgress(progress);
                        progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));
                        isFinished();
                    } else {
                        Toast.makeText(TugasActivity.this, "Error: ".concat(message), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(TugasActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (session.sessionCheckKeyword("matapelajaran_".concat(global.getDataGrupId()))) {
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
                        JSONObject jsonObject = new JSONObject(session.getJSONObject("matapelajaran_".concat(global.getDataGrupId())));
                        String success = jsonObject.getString("success");
                        String message = jsonObject.getString("message");
                        if (success.equals("1")) {
                            String pelajaran_monday = jsonObject.getString("pelajaran_monday");
                            String pelajaran_tuesday = jsonObject.getString("pelajaran_tuesday");
                            String pelajaran_wednesday = jsonObject.getString("pelajaran_wednesday");
                            String pelajaran_thursday = jsonObject.getString("pelajaran_thursday");
                            String pelajaran_friday = jsonObject.getString("pelajaran_friday");
                            String pelajaran_saturday = jsonObject.getString("pelajaran_saturday");
                            String pelajaran_sunday = jsonObject.getString("pelajaran_sunday");

                            if (!pelajaran_monday.isEmpty())
                                recyclerListJadwal.add(new JadwalPelajaran("monday", pelajaran_monday));
                            if (!pelajaran_tuesday.isEmpty())
                                recyclerListJadwal.add(new JadwalPelajaran("tuesday", pelajaran_tuesday));
                            if (!pelajaran_wednesday.isEmpty())
                                recyclerListJadwal.add(new JadwalPelajaran("wednesday", pelajaran_wednesday));
                            if (!pelajaran_thursday.isEmpty())
                                recyclerListJadwal.add(new JadwalPelajaran("thursday", pelajaran_thursday));
                            if (!pelajaran_friday.isEmpty())
                                recyclerListJadwal.add(new JadwalPelajaran("friday", pelajaran_friday));
                            if (!pelajaran_saturday.isEmpty())
                                recyclerListJadwal.add(new JadwalPelajaran("saturday", pelajaran_saturday));
                            if (!pelajaran_sunday.isEmpty())
                                recyclerListJadwal.add(new JadwalPelajaran("sunday", pelajaran_sunday));
                            mAdapterJadwal.notifyDataSetChanged();
                            session.setJSONObject(jsonObject.toString(), "matapelajaran");
                            progress+=20;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                progress_tgsact_progressbar.setProgress(progress, true);
                            else
                                progress_tgsact_progressbar.setProgress(progress);
                            progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));
                            isFinished();
                        } else {
                            processing_tgsact_text.setText("Error: ".concat(message));
                            progress+=20;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                progress_tgsact_progressbar.setProgress(progress, true);
                            else
                                progress_tgsact_progressbar.setProgress(progress);
                            progress_tgsact_text.setText(String.valueOf(progress).concat("%"));
                            isFinished();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(TugasActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
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
                    progress+=20;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        progress_tgsact_progressbar.setProgress(progress, true);
                    else
                        progress_tgsact_progressbar.setProgress(progress);
                    progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));
                    isFinished();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grupid", global.getDataGrupId());
                return params;
            }
        };
        RequestQueue requestQueueJadwal = Volley.newRequestQueue(this);
        requestQueueJadwal.add(stringRequestJadwal);

        recyclerViewJadwal.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewJadwal, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                JadwalPelajaran jadwalPelajaran = recyclerListJadwal.get(position);
                showJadwal(jadwalPelajaran.getData());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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

                                recyclerListAbsent.add(new Absent(user_id, user_avatar, user_avatar_alternative, user_namalengkap, user_name, user_story));
                                mAdapterAbsent.notifyDataSetChanged();
                                absent_tgsact_text.setText("Not Absented Today");
                            }
                        }
                        progress+=20;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            progress_tgsact_progressbar.setProgress(progress, true);
                        else
                            progress_tgsact_progressbar.setProgress(progress);
                        progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));
                        isFinished();
                        session.setJSONObject(jsonObject.toString(), "absent_".concat(global.getDataGrupId()));
                    } else if (success.equals("2")){
                        absent_tgsact_text.setText("Not Absented Today - all absented");
                        progress+=20;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            progress_tgsact_progressbar.setProgress(progress, true);
                        else
                            progress_tgsact_progressbar.setProgress(progress);
                        progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));
                        isFinished();
                    } else {
                        absent_tgsact_text.setText("Not Absented Today - something went wrong");
                        progress+=20;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            progress_tgsact_progressbar.setProgress(progress, true);
                        else
                            progress_tgsact_progressbar.setProgress(progress);
                        progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));
                        isFinished();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(TugasActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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

                                    recyclerListAbsent.add(new Absent(user_id, user_avatar, user_avatar_alternative, user_namalengkap, user_name, user_story));
                                    mAdapterAbsent.notifyDataSetChanged();
                                    absent_tgsact_text.setText("Not Absented Today");
                                }
                            }
                            session.setJSONObject(jsonObject.toString(), "absent_".concat(global.getDataGrupId()));
                            progress+=20;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                progress_tgsact_progressbar.setProgress(progress, true);
                            else
                                progress_tgsact_progressbar.setProgress(progress);
                            progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));
                            isFinished();
                        } else if (success.equals("2")){
                            absent_tgsact_text.setText("Not Absented Today - all absented");
                            progress+=20;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                progress_tgsact_progressbar.setProgress(progress, true);
                            else
                                progress_tgsact_progressbar.setProgress(progress);
                            progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));
                            isFinished();
                        } else {
                            absent_tgsact_text.setText("Not Absented Today - something went wrong");
                            progress+=20;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                progress_tgsact_progressbar.setProgress(progress, true);
                            else
                                progress_tgsact_progressbar.setProgress(progress);
                            progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));
                            isFinished();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    absent_tgsact_text.setText("Not Absented Today - tidak bisa memuat");
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
                    progress+=20;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        progress_tgsact_progressbar.setProgress(progress, true);
                    else
                        progress_tgsact_progressbar.setProgress(progress);
                    progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));
                    isFinished();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grupid", global.getDataGrupId());
                return params;
            }
        };
        RequestQueue requestQueueAbsent = Volley.newRequestQueue(this);
        requestQueueAbsent.add(stringRequestAbsent);

        isFinished();

        recyclerViewAbsent.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewAbsent, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Absent absent = recyclerListAbsent.get(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public void showJadwal(String data) {
        recyclerListJadwalItem.clear();
        String[] dataArray = data.split(",");
        for (String s : dataArray) {
            recyclerListJadwalItem.add(new JadwalPelajaranItem(s));
        }
        mAdapterJadwalItem.notifyDataSetChanged();
        recyclerViewJadwalItem.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tugas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_grup_profile_tgsact) {
            openGrupProfileActivity();
            return true;
        } else if (id == R.id.action_keluar_grup_tgsact) {
            openDialogKeluar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        bannerTukitAnimation1.start();
        bannerTukitAnimation2.start();
    }

    public void openGrupProfileActivity() {
        startActivityForResult(new Intent(this, GrupProfileActivity.class), 0);
    }

    public void openDialogKeluar() {
        DialogKeluarGrup dialogKeluarGrup = new DialogKeluarGrup();
        dialogKeluarGrup.show(getSupportFragmentManager(), "Keluar Grup dialog");
    }

    public void openDialogTugas() {
        DialogTugas dialogTugas = new DialogTugas();
        dialogTugas.show(getSupportFragmentManager(), "Tugas dialog");
    }

    public void openTambahTugasActivity(View view) {
        startActivityForResult(new Intent(this, TambahTugasActivity.class), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            if (data.hasExtra("intentCallbackTambahTugas")) {
                String result = data.getStringExtra("intentCallbackTambahTugas");
                if (result.equals("tambah-tugas")) {
                    load();
                }
            }
            if (data.hasExtra("intentCallbackGrupProfile")) {
                String result = data.getStringExtra("intentCallbackGrupProfile");
                if (result.equals("delete-grup")) {
                    // Reset
                    global.setDataIntentKY("delete-grup");
                    finish();
                }
                if (result.equals("update-tugas")) {
                    load();
                }
                if (result.equals("update-matapelajaran")) {
                    loadJadwalMataPelajaran();
                    // Reset
                    global.setDataIntentKY("end");
                }
            }
        }
    }

    @Override
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();
        data.putExtra("intentCallbackTugas", global.getDataIntentKY());
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }

    public void keluarGrup(final String userstatus, final String userid, final String grupid) {
        final View view = null;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_KELUAR_GRUP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Qsuccess = jsonObject.getString("success");
                    String Qmessage = jsonObject.getString("message");
                    if (Qsuccess.equals("1")) {
                        global.setDataIntentKY("keluar-grup");
                        finish();
                    } else {
                        Toast.makeText(TugasActivity.this, Qmessage, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(TugasActivity.this, "JSON error: " + e.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("userstatus", userstatus);
                params.put("userid", userid);
                params.put("grupid", grupid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public String format(String text, int length) {
        String result = "";
        for (int i = 0;i < text.length(); i++){
            if (i+1 <= length) {
                result += text.charAt(i);
            } else {
                result += "...";
                break;
            }
        }
        return result;
    }

    public String formatDate(String date) {
        String[] i = date.split("-");
        return formatMonth(i[1]).concat(" ").concat(i[0]);
    }

    public String formatMonth(String nmonth) {
        switch (nmonth) {
            case "1": return "Jan";
            case "2": return "Feb";
            case "3": return "Mar";
            case "4": return "Apr";
            case "5": return "Mei";
            case "6": return "Jun";
            case "7": return "Jul";
            case "8": return "Agt";
            case "9": return "Sep";
            case "10": return "Okt";
            case "11": return "Nov";
            case "12": return "Des";
            default:
                return "err";
        }
    }

    @Override
    public void onKeluar() {
        keluarGrup(global.getDataGrupStatusIn(), session.getUserId(), global.getDataGrupId());
    }

    public void load() {
        // Today
        recyclerList1.clear(); // clear the list
        errToday.setVisibility(View.GONE);
        recyclerView1.setVisibility(View.VISIBLE);
        // Tommorow
        recyclerList2.clear(); // clear the list
        errTommorow.setVisibility(View.GONE);
        recyclerView2.setVisibility(View.VISIBLE);
        // Others
        recyclerList3.clear(); // clear the list
        errOthers.setVisibility(View.GONE);
        recyclerView3.setVisibility(View.VISIBLE);
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, URL_POST_TUGAS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayToday = jsonObject.getJSONArray("tugas_today");
                    JSONArray jsonArrayTommorow = jsonObject.getJSONArray("tugas_tommorow");
                    JSONArray jsonArrayOthers = jsonObject.getJSONArray("tugas_others");
                    session.setJSONObject(jsonObject.toString(), "tugas_".concat(global.getDataGrupId()));
                    session.setJSONObject(session.getJSONObject("tugas_session_list").concat("tugas_".concat(global.getDataGrupId())).concat(","), "tugas_session_list");
                    // Today
                    if (jsonArrayToday.length() != 0) {
                        for (int i = 0; i < jsonArrayToday.length(); i++) {
                            JSONObject object = jsonArrayToday.getJSONObject(i);
                            String w_tugasid = object.getString("tugasid");
                            String w_tugasnama = object.getString("tugasnama");
                            String w_tugaspelajaran = object.getString("tugaspelajaran");
                            String w_tugaswaktu = object.getString("tugaswaktu");
                            String w_tugaswaktu_harilagi = object.getString("tugaswaktu_harilagi");
                            String w_tugaswaktu_number = object.getString("tugaswaktu_number");
                            String w_tugasprioritas = object.getString("tugasprioritas");
                            String w_tugasauthorid = object.getString("tugasauthorid");
                            String w_tugasauthornama = object.getString("tugasauthornama");
                            String w_tugasposisi = object.getString("tugasposisi");
                            String w_tugasauthorpic = object.getString("tugasauthorpic");
                            String w_tugascerita = object.getString("tugascerita");

                            recyclerList1.add(new Tugas(w_tugasid, w_tugaspelajaran, w_tugasnama, format(w_tugasnama, judulLength), w_tugascerita, format(w_tugascerita, ceritaLength), w_tugaswaktu, "SEKARANG", w_tugasprioritas));
                        }
                        mAdapter1.notifyDataSetChanged();
                        today_tgsact_text.setText("Today");
                    } else {
                        today_tgsact_text.setText("Today");
                        errToday.setVisibility(View.VISIBLE);
                        recyclerView1.setVisibility(View.GONE);
                    }
                    // Tommorow
                    if (jsonArrayTommorow.length() != 0) {
                        for (int i = 0; i < jsonArrayTommorow.length(); i++) {
                            JSONObject object = jsonArrayTommorow.getJSONObject(i);
                            String w_tugasid = object.getString("tugasid");
                            String w_tugasnama = object.getString("tugasnama");
                            String w_tugaspelajaran = object.getString("tugaspelajaran");
                            String w_tugaswaktu = object.getString("tugaswaktu");
                            String w_tugaswaktu_harilagi = object.getString("tugaswaktu_harilagi");
                            String w_tugaswaktu_number = object.getString("tugaswaktu_number");
                            String w_tugasprioritas = object.getString("tugasprioritas");
                            String w_tugasauthorid = object.getString("tugasauthorid");
                            String w_tugasauthornama = object.getString("tugasauthornama");
                            String w_tugasposisi = object.getString("tugasposisi");
                            String w_tugasauthorpic = object.getString("tugasauthorpic");
                            String w_tugascerita = object.getString("tugascerita");

                            recyclerList2.add(new Tugas(w_tugasid, w_tugaspelajaran, w_tugasnama, format(w_tugasnama, judulLength), w_tugascerita, format(w_tugascerita, ceritaLength), w_tugaswaktu, "BESOK", w_tugasprioritas));
                        }
                        mAdapter2.notifyDataSetChanged();
                        tommorow_tgsact_text.setText("Tommorow");
                    } else {
                        tommorow_tgsact_text.setText("Tommorow");
                        errTommorow.setVisibility(View.VISIBLE);
                        recyclerView2.setVisibility(View.GONE);
                    }
                    // Others
                    if (jsonArrayOthers.length() != 0) {
                        for (int i = 0; i < jsonArrayOthers.length(); i++) {
                            JSONObject object = jsonArrayOthers.getJSONObject(i);
                            String w_tugasid = object.getString("tugasid");
                            String w_tugasnama = object.getString("tugasnama");
                            String w_tugaspelajaran = object.getString("tugaspelajaran");
                            String w_tugaswaktu = object.getString("tugaswaktu");
                            String w_tugaswaktu_harilagi = object.getString("tugaswaktu_harilagi");
                            String w_tugaswaktu_number = object.getString("tugaswaktu_number");
                            String w_tugasprioritas = object.getString("tugasprioritas");
                            String w_tugasauthorid = object.getString("tugasauthorid");
                            String w_tugasauthornama = object.getString("tugasauthornama");
                            String w_tugasposisi = object.getString("tugasposisi");
                            String w_tugasauthorpic = object.getString("tugasauthorpic");
                            String w_tugascerita = object.getString("tugascerita");

                            recyclerList3.add(new Tugas(w_tugasid, w_tugaspelajaran, w_tugasnama, format(w_tugasnama, judulLength), w_tugascerita, format(w_tugascerita, ceritaLength), w_tugaswaktu, formatDate(w_tugaswaktu), w_tugasprioritas));
                        }
                        mAdapter3.notifyDataSetChanged();
                        others_tgsact_text.setText("Others");
                        processing_tgsact_text.setText("Loaded");
                    } else {
                        others_tgsact_text.setText("Others");
                        errOthers.setVisibility(View.VISIBLE);
                        recyclerView3.setVisibility(View.GONE);
                        processing_tgsact_text.setText("Loaded");
                    }

                    progress+=20;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        progress_tgsact_progressbar.setProgress(progress, true);
                    else
                        progress_tgsact_progressbar.setProgress(progress);
                    progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));
                    isFinished();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(TugasActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (session.sessionCheckKeyword("tugas_".concat(global.getDataGrupId()))) {
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
                        JSONObject jsonObject = new JSONObject(session.getJSONObject("tugas_".concat(global.getDataGrupId())));
                        JSONArray jsonArrayToday = jsonObject.getJSONArray("tugas_today");
                        JSONArray jsonArrayTommorow = jsonObject.getJSONArray("tugas_tommorow");
                        JSONArray jsonArrayOthers = jsonObject.getJSONArray("tugas_others");
                        // Today
                        if (jsonArrayToday.length() != 0) {
                            for (int i = 0; i < jsonArrayToday.length(); i++) {
                                JSONObject object = jsonArrayToday.getJSONObject(i);
                                String w_tugasid = object.getString("tugasid");
                                String w_tugasnama = object.getString("tugasnama");
                                String w_tugaspelajaran = object.getString("tugaspelajaran");
                                String w_tugaswaktu = object.getString("tugaswaktu");
                                String w_tugaswaktu_harilagi = object.getString("tugaswaktu_harilagi");
                                String w_tugaswaktu_number = object.getString("tugaswaktu_number");
                                String w_tugasprioritas = object.getString("tugasprioritas");
                                String w_tugasauthorid = object.getString("tugasauthorid");
                                String w_tugasauthornama = object.getString("tugasauthornama");
                                String w_tugasposisi = object.getString("tugasposisi");
                                String w_tugasauthorpic = object.getString("tugasauthorpic");
                                String w_tugascerita = object.getString("tugascerita");

                                recyclerList1.add(new Tugas(w_tugasid, w_tugaspelajaran, w_tugasnama, format(w_tugasnama, judulLength), w_tugascerita, format(w_tugascerita, ceritaLength), w_tugaswaktu, "SEKARANG", w_tugasprioritas));
                            }
                            mAdapter1.notifyDataSetChanged();
                            today_tgsact_text.setText("Today - LAST SESSION");
                        } else {
                            today_tgsact_text.setText("Today - LAST SESSION");
                            errToday.setVisibility(View.VISIBLE);
                            recyclerView1.setVisibility(View.GONE);
                        }
                        // Tommorow
                        if (jsonArrayTommorow.length() != 0) {
                            for (int i = 0; i < jsonArrayTommorow.length(); i++) {
                                JSONObject object = jsonArrayTommorow.getJSONObject(i);
                                String w_tugasid = object.getString("tugasid");
                                String w_tugasnama = object.getString("tugasnama");
                                String w_tugaspelajaran = object.getString("tugaspelajaran");
                                String w_tugaswaktu = object.getString("tugaswaktu");
                                String w_tugaswaktu_harilagi = object.getString("tugaswaktu_harilagi");
                                String w_tugaswaktu_number = object.getString("tugaswaktu_number");
                                String w_tugasprioritas = object.getString("tugasprioritas");
                                String w_tugasauthorid = object.getString("tugasauthorid");
                                String w_tugasauthornama = object.getString("tugasauthornama");
                                String w_tugasposisi = object.getString("tugasposisi");
                                String w_tugasauthorpic = object.getString("tugasauthorpic");
                                String w_tugascerita = object.getString("tugascerita");

                                recyclerList2.add(new Tugas(w_tugasid, w_tugaspelajaran, w_tugasnama, format(w_tugasnama, judulLength), w_tugascerita, format(w_tugascerita, ceritaLength), w_tugaswaktu, "BESOK", w_tugasprioritas));
                            }
                            mAdapter2.notifyDataSetChanged();
                            tommorow_tgsact_text.setText("Tommorow - LAST SESSION");
                        } else {
                            tommorow_tgsact_text.setText("Tommorow - LAST SESSION");
                            errTommorow.setVisibility(View.VISIBLE);
                            recyclerView2.setVisibility(View.GONE);
                        }
                        // Others
                        if (jsonArrayOthers.length() != 0) {
                            for (int i = 0; i < jsonArrayOthers.length(); i++) {
                                JSONObject object = jsonArrayOthers.getJSONObject(i);
                                String w_tugasid = object.getString("tugasid");
                                String w_tugasnama = object.getString("tugasnama");
                                String w_tugaspelajaran = object.getString("tugaspelajaran");
                                String w_tugaswaktu = object.getString("tugaswaktu");
                                String w_tugaswaktu_harilagi = object.getString("tugaswaktu_harilagi");
                                String w_tugaswaktu_number = object.getString("tugaswaktu_number");
                                String w_tugasprioritas = object.getString("tugasprioritas");
                                String w_tugasauthorid = object.getString("tugasauthorid");
                                String w_tugasauthornama = object.getString("tugasauthornama");
                                String w_tugasposisi = object.getString("tugasposisi");
                                String w_tugasauthorpic = object.getString("tugasauthorpic");
                                String w_tugascerita = object.getString("tugascerita");

                                recyclerList3.add(new Tugas(w_tugasid, w_tugaspelajaran, w_tugasnama, format(w_tugasnama, judulLength), w_tugascerita, format(w_tugascerita, ceritaLength), w_tugaswaktu, formatDate(w_tugaswaktu), w_tugasprioritas));
                            }
                            mAdapter3.notifyDataSetChanged();
                            others_tgsact_text.setText("Others - LAST SESSION");
                            processing_tgsact_text.setText("Loaded");
                        } else {
                            others_tgsact_text.setText("Others - LAST SESSION");
                            errOthers.setVisibility(View.VISIBLE);
                            recyclerView3.setVisibility(View.GONE);
                            processing_tgsact_text.setText("Loaded");
                        }
                        progress+=20;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            progress_tgsact_progressbar.setProgress(progress, true);
                        else
                            progress_tgsact_progressbar.setProgress(progress);
                        progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));
                        isFinished();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    // Today Error
                    today_tgsact_text.setText("Today - no connection, tidak bisa memuat");
                    errToday.setVisibility(View.VISIBLE);
                    recyclerView1.setVisibility(View.GONE);
                    // Tommorow Error
                    tommorow_tgsact_text.setText("Tommorow - no connection, tidak bisa memuat");
                    errTommorow.setVisibility(View.VISIBLE);
                    recyclerView2.setVisibility(View.GONE);
                    // Others Error
                    others_tgsact_text.setText("Others - no connection, tidak bisa memuat");
                    errOthers.setVisibility(View.VISIBLE);
                    recyclerView3.setVisibility(View.GONE);
                    processing_tgsact_text.setText("Loaded");

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
                    progress+=20;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        progress_tgsact_progressbar.setProgress(progress, true);
                    else
                        progress_tgsact_progressbar.setProgress(progress);
                    progress_tgsact_text.setText(String.valueOf(progress_tgsact_progressbar.getProgress()).concat("%"));
                    isFinished();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grupid", global.getDataGrupId());
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(this);
        requestQueue1.add(stringRequest1);

        recyclerView1.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView1, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Tugas tugas = recyclerList1.get(position);
                global.setDataIdTugas(tugas.getId());
                global.setDataJudulTugas(tugas.getHeader());
                global.setDataCeritaTugas(tugas.getParagraph());
                global.setDataWaktuTugas(tugas.getDeadline());
                global.setDataMataPelajaranTugas(tugas.getAcademic());
                global.setDataGambarTugas(tugas.getImage());
                global.setDataLogoTugas(tugas.getLogo());
                global.setDataPrioritasTugas(tugas.getPrioritas());
                openDialogTugas();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        recyclerView2.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView2, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Tugas tugas = recyclerList2.get(position);
                global.setDataIdTugas(tugas.getId());
                global.setDataJudulTugas(tugas.getHeader());
                global.setDataCeritaTugas(tugas.getParagraph());
                global.setDataWaktuTugas(tugas.getDeadline());
                global.setDataMataPelajaranTugas(tugas.getAcademic());
                global.setDataGambarTugas(tugas.getImage());
                global.setDataLogoTugas(tugas.getLogo());
                global.setDataPrioritasTugas(tugas.getPrioritas());
                openDialogTugas();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        recyclerView3.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView3, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Tugas tugas = recyclerList3.get(position);
                global.setDataIdTugas(tugas.getId());
                global.setDataJudulTugas(tugas.getHeader());
                global.setDataCeritaTugas(tugas.getParagraph());
                global.setDataWaktuTugas(tugas.getDeadline());
                global.setDataMataPelajaranTugas(tugas.getAcademic());
                global.setDataGambarTugas(tugas.getImage());
                global.setDataLogoTugas(tugas.getLogo());
                global.setDataPrioritasTugas(tugas.getPrioritas());
                openDialogTugas();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public void isFinished() {
        if (progress >= 100)
            tugas_tgsact_layout.setVisibility(View.VISIBLE);
    }

    public void openErrorDialog(String errorString) {
        Snackbar.make(recyclerView1, errorString, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
