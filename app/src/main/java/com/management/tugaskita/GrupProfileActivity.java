package com.management.tugaskita;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
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
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrupProfileActivity extends AppCompatActivity implements DialogRiwayat.DialogRiwayatListener, DialogHapusGrup.DialogHapusGrupListener, DialogOnAir.DialogOnAirListener, DialogError.DialogErrorListener {

    Session session;
    Global global = Global.getInstance();
    private String URL_POST_UPDATE = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/update_grup.php");
    private String URL_POST_ONAIR = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/load_tugas.php");
    private String URL_POST_HAPUS_TUGAS = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/delete_tugas.php");
    private String URL_POST_DELETE_GRUP = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/delete_grup.php");
    private String URL_POST_MATA_PELAJARAN = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/load_pelajaran.php");

    public static final int IMAGE_GALLERY_REQUEST = 20;
    public static final int CAMERA_REQUEST_CODE = 228;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 4192;
    Uri uriFromGallery = null;

    String CHANNEL_ID = "upload notification";

    boolean TimeoutError = false;
    boolean NoConnectionError = false;
    boolean AuthFailureError = false;
    boolean ServerError = false;
    boolean NetworkError = false;
    boolean ParseError = false;

    int judulLength = 25;
    int ceritaLength = 15;

    private List<JadwalPelajaranProfile> recyclerListMataPelajaran;
    private RecyclerView recyclerViewMataPelajaran;
    private RecyclerView.Adapter mAdapterMataPelajaran;

    private List<Tugas> recyclerList2;
    private RecyclerView recyclerView2;
    private RecyclerView.Adapter mAdapter2;

    private List<Tugas> recyclerList3;
    private RecyclerView recyclerView3;
    private RecyclerView.Adapter mAdapter3;

    private List<Tugas> recyclerList4;
    private RecyclerView recyclerView4;
    private RecyclerView.Adapter mAdapter4;

    ImageView profile_gprfact_image;
    TextView processing_gprfact_text, onair_today_gprfact_text, onair_tommorow_gprfact_text, onair_others_gprfact_text;
    EditText nama_gprfact_input, storyofgroup_gprfact_input;
    Button ubah_profile_gprfact_button, hapus_grup_gprfact_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grup_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new Session(this);
        createNotificationChannel();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String newNamaGrup = nama_gprfact_input.getText().toString().trim();
                final String newCeritaGrup = storyofgroup_gprfact_input.getText().toString().trim();

                if (uriFromGallery != null) {
                    final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(GrupProfileActivity.this);
                    final NotificationCompat.Builder builder = new NotificationCompat.Builder(GrupProfileActivity.this, CHANNEL_ID);
                    builder.setContentTitle("Uploading Image")
                            .setContentText("Starting...")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setColor(ContextCompat.getColor(GrupProfileActivity.this, R.color.blue))
                            .setSound(Uri.parse("android.resource://" + GrupProfileActivity.this.getPackageName() + "/" + R.raw.opening))
                            .setPriority(NotificationCompat.PRIORITY_HIGH);

                    // CREATE UNIQUE ID WITH TIME AND DATE
                    Date dNow = new Date();
                    SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd.HH:mm:ss:SSS");
                    final String uniqueID = global.getDataGrupId() + "." + ft.format(dNow);

                    Map<String, Object> options = new HashMap<>();
                    options.put("public_id", uniqueID);
                    options.put("tags", "grup_profile");
                    options.put("folder", "tugaskita/grup_profile/");
                    // UPLOAD NEW PROFILE IMAGE
                    String requestId = MediaManager.get().upload(uriFromGallery).callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            builder.setContentText("Cloud is starting for uploading...")
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .bigText("Cloud is starting for uploading..."))
                                    .setProgress(0,0,true);
                            notificationManager.notify(101, builder.build());
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {

                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            builder.setContentText("Finished")
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .bigText("Finished"))
                                    .setProgress(0,0,false);
                            notificationManager.notify(101, builder.build());
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            builder.setContentText("Something went wrong while uploading, try again later")
                                    .setProgress(0,0,false);
                            notificationManager.notify(101, builder.build());
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {

                        }
                    }).options(options).unsigned("qojyihp8").dispatch();
                    update(newNamaGrup, newCeritaGrup, uniqueID);
                } else {
                    update(newNamaGrup, newCeritaGrup, "");
                }
            }
        });

        onair_today_gprfact_text = findViewById(R.id.onair_today_gprfact_text);
        onair_tommorow_gprfact_text = findViewById(R.id.onair_tommorow_gprfact_text);
        onair_others_gprfact_text = findViewById(R.id.onair_others_gprfact_text);
        processing_gprfact_text = findViewById(R.id.processing_gprfact_text);
        profile_gprfact_image = findViewById(R.id.profile_gprfact_image);
        nama_gprfact_input = findViewById(R.id.nama_gprfact_input);
        storyofgroup_gprfact_input = findViewById(R.id.storyofgroup_gprfact_input);

        ubah_profile_gprfact_button = findViewById(R.id.ubah_profile_gprfact_button);
        ubah_profile_gprfact_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

        hapus_grup_gprfact_button = findViewById(R.id.hapus_grup_gprfact_button);
        if (global.getDataGrupStatusIn().equals("SUPER-ADMIN")) {
            hapus_grup_gprfact_button.setVisibility(View.VISIBLE);
        }

        // Predefined
        String url = MediaManager.get().url().transformation(new Transformation().aspectRatio("1:1").gravity("auto").radius("max").width(400).crop("fill")).secure(true).generate("tugaskita/grup_profile/".concat(global.getDataGrupAvatar()));
        Picasso.get().load(url)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.group)
                .into(profile_gprfact_image, new Callback() {
                    @Override
                    public void onSuccess() {
                        processing_gprfact_text.setText("Loaded");
                    }

                    @Override
                    public void onError(Exception e) {
                        processing_gprfact_text.setText("Profile set to default");
                    }
                });
        nama_gprfact_input.setText(global.getDataGrupNama());
        storyofgroup_gprfact_input.setText(global.getDataGrupCerita());


        // Screenshot Guard
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        /////////////////////////////////////////////////////////////////////////////////////////

        // MATA PELAJARAN

        recyclerListMataPelajaran = new ArrayList<>();
        RecyclerView.LayoutManager layoutManagerMataPelajaran = new LinearLayoutManager(getApplicationContext());
        recyclerViewMataPelajaran = findViewById(R.id.mata_pelajaran_gprfact_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerViewMataPelajaran.setHasFixedSize(true);

        // use a linear layout manager
        layoutManagerMataPelajaran = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewMataPelajaran.setLayoutManager(layoutManagerMataPelajaran);

        // specify an adapter (see also next example)
        mAdapterMataPelajaran = new ListAdapterJadwalPelajaranProfile(this, recyclerListMataPelajaran);
        recyclerViewMataPelajaran.setItemAnimator(new DefaultItemAnimator());
        recyclerViewMataPelajaran.setNestedScrollingEnabled(false);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, 8));
        recyclerViewMataPelajaran.setAdapter(mAdapterMataPelajaran);

        // ON AIR
        // today

        recyclerList2 = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getApplicationContext());
        recyclerView2 = findViewById(R.id.onair_today_gprfact_list);

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

        // tommorow

        recyclerList3 = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager3 = new LinearLayoutManager(getApplicationContext());
        recyclerView3 = findViewById(R.id.onair_tommorow_gprfact_list);

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

        // others

        recyclerList4 = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager4 = new LinearLayoutManager(getApplicationContext());
        recyclerView4 = findViewById(R.id.onair_others_gprfact_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView4.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager4 = new LinearLayoutManager(this);
        recyclerView4.setLayoutManager(layoutManager4);

        // specify an adapter (see also next example)
        mAdapter4 = new ListAdapterTugas(this, recyclerList4);
        recyclerView4.setItemAnimator(new DefaultItemAnimator());
        recyclerView4.setNestedScrollingEnabled(false);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, 8));
        recyclerView4.setAdapter(mAdapter4);

        // Mata Pelajaran
        loadMataPelajaran();
        // Tugas
        load();
    }

    public void loadMataPelajaran() {
        // Mata Pelajaran
        recyclerListMataPelajaran.clear();
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

                        recyclerListMataPelajaran.add(new JadwalPelajaranProfile("monday", pelajaran_monday));
                        recyclerListMataPelajaran.add(new JadwalPelajaranProfile("tuesday", pelajaran_tuesday));
                        recyclerListMataPelajaran.add(new JadwalPelajaranProfile("wednesday", pelajaran_wednesday));
                        recyclerListMataPelajaran.add(new JadwalPelajaranProfile("thursday", pelajaran_thursday));
                        recyclerListMataPelajaran.add(new JadwalPelajaranProfile("friday", pelajaran_friday));
                        recyclerListMataPelajaran.add(new JadwalPelajaranProfile("saturday", pelajaran_saturday));
                        recyclerListMataPelajaran.add(new JadwalPelajaranProfile("sunday", pelajaran_sunday));

                        session.setJSONObject(jsonObject.toString(), "matapelajaran_".concat(global.getDataGrupId()));
                        mAdapterMataPelajaran.notifyDataSetChanged();

                    } else {
                        openErrorDialog("ERROR: ".concat(message));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    openErrorDialog("JSON error: ".concat(e.toString()));
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

                            recyclerListMataPelajaran.add(new JadwalPelajaranProfile("monday", pelajaran_monday));
                            recyclerListMataPelajaran.add(new JadwalPelajaranProfile("tuesday", pelajaran_tuesday));
                            recyclerListMataPelajaran.add(new JadwalPelajaranProfile("wednesday", pelajaran_wednesday));
                            recyclerListMataPelajaran.add(new JadwalPelajaranProfile("thursday", pelajaran_thursday));
                            recyclerListMataPelajaran.add(new JadwalPelajaranProfile("friday", pelajaran_friday));
                            recyclerListMataPelajaran.add(new JadwalPelajaranProfile("saturday", pelajaran_saturday));
                            recyclerListMataPelajaran.add(new JadwalPelajaranProfile("sunday", pelajaran_sunday));

                            mAdapterMataPelajaran.notifyDataSetChanged();

                        } else {
                            openErrorDialog("ERROR: ".concat(message));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        openErrorDialog("JSON error: ".concat(e.toString()));
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

        RequestQueue requestQueueJadwal = Volley.newRequestQueue(this);
        requestQueueJadwal.add(stringRequestJadwal);

        recyclerViewMataPelajaran.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewMataPelajaran, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (global.getDataInteger() == 0) {
                    JadwalPelajaranProfile jadwalPelajaranProfile = recyclerListMataPelajaran.get(position);
                    String day = jadwalPelajaranProfile.getDay();
                    String data = jadwalPelajaranProfile.getData();
                    String result = day.concat("-").concat(data);

                    global.setDataInteger(global.getDataInteger()+1);
                    Intent intent = new Intent(GrupProfileActivity.this, MataPelajaranProfileActivity.class);
                    intent.putExtra("rawData", result);
                    startActivityForResult(intent, 0);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public void load() {
        // Today
        recyclerList2.clear();
        // Tommorow
        recyclerList3.clear();
        // Others
        recyclerList4.clear();
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, URL_POST_ONAIR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayToday = jsonObject.getJSONArray("tugas_today");
                    JSONArray jsonArrayTommorow = jsonObject.getJSONArray("tugas_tommorow");
                    JSONArray jsonArrayOthers = jsonObject.getJSONArray("tugas_others");
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

                            recyclerList2.add(new Tugas(w_tugasid, w_tugaspelajaran, w_tugasnama, format(w_tugasnama, judulLength), w_tugascerita, format(w_tugascerita, ceritaLength), w_tugaswaktu, formatDate(w_tugaswaktu), w_tugasprioritas));
                        }
                        mAdapter2.notifyDataSetChanged();
                        onair_today_gprfact_text.setText("Today - LOADED");
                    } else {
                        mAdapter2.notifyDataSetChanged();
                        onair_today_gprfact_text.setText("Today - No Job");
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

                            recyclerList3.add(new Tugas(w_tugasid, w_tugaspelajaran, w_tugasnama, format(w_tugasnama, judulLength), w_tugascerita, format(w_tugascerita, ceritaLength), w_tugaswaktu, formatDate(w_tugaswaktu), w_tugasprioritas));
                        }
                        mAdapter3.notifyDataSetChanged();
                        onair_tommorow_gprfact_text.setText("Tommorow - LOADED");
                    } else {
                        mAdapter3.notifyDataSetChanged();
                        onair_tommorow_gprfact_text.setText("Tommorow - No Job");
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

                            recyclerList4.add(new Tugas(w_tugasid, w_tugaspelajaran, w_tugasnama, format(w_tugasnama, judulLength), w_tugascerita, format(w_tugascerita, ceritaLength), w_tugaswaktu, formatDate(w_tugaswaktu), w_tugasprioritas));
                        }
                        mAdapter4.notifyDataSetChanged();
                        onair_others_gprfact_text.setText("Others - LOADED");
                    } else {
                        mAdapter4.notifyDataSetChanged();
                        onair_others_gprfact_text.setText("Others - No Job");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    openErrorDialog("JSON error: ".concat(e.toString()));
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

                                recyclerList2.add(new Tugas(w_tugasid, w_tugaspelajaran, w_tugasnama, format(w_tugasnama, judulLength), w_tugascerita, format(w_tugascerita, ceritaLength), w_tugaswaktu, formatDate(w_tugaswaktu), w_tugasprioritas));
                            }
                            mAdapter2.notifyDataSetChanged();
                            onair_today_gprfact_text.setText("Today - LOADED LAST SESSION");
                        } else {
                            mAdapter2.notifyDataSetChanged();
                            onair_today_gprfact_text.setText("Today - No Job, LAST SESSION");
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

                                recyclerList3.add(new Tugas(w_tugasid, w_tugaspelajaran, w_tugasnama, format(w_tugasnama, judulLength), w_tugascerita, format(w_tugascerita, ceritaLength), w_tugaswaktu, formatDate(w_tugaswaktu), w_tugasprioritas));
                            }
                            mAdapter3.notifyDataSetChanged();
                            onair_tommorow_gprfact_text.setText("Tommorow - LOADED LAST SESSION");
                        } else {
                            mAdapter3.notifyDataSetChanged();
                            onair_tommorow_gprfact_text.setText("Tommorow - No Job, LAST SESSION");
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

                                recyclerList4.add(new Tugas(w_tugasid, w_tugaspelajaran, w_tugasnama, format(w_tugasnama, judulLength), w_tugascerita, format(w_tugascerita, ceritaLength), w_tugaswaktu, formatDate(w_tugaswaktu), w_tugasprioritas));
                            }
                            mAdapter4.notifyDataSetChanged();
                            onair_others_gprfact_text.setText("Others - LOADED LAST SESSION");
                        } else {
                            mAdapter4.notifyDataSetChanged();
                            onair_others_gprfact_text.setText("Others - No Job, LAST SESSION");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        openErrorDialog("JSON error: ".concat(e.toString()));
                    }


                } else {
                    // Today Error
                    mAdapter2.notifyDataSetChanged();
                    onair_today_gprfact_text.setText("Today - no connection, tidak bisa memuat");
                    // Tommorow Error
                    mAdapter3.notifyDataSetChanged();
                    onair_tommorow_gprfact_text.setText("Tommorow - no connection, tidak bisa memuat");
                    // Others Error
                    mAdapter4.notifyDataSetChanged();
                    onair_others_gprfact_text.setText("Others - no connection, tidak bisa memuat");

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
        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        requestQueue2.add(stringRequest2);

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
                global.setDataPrioritasTugas(tugas.getPrioritas());
                openDialogOnAir();
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
                global.setDataPrioritasTugas(tugas.getPrioritas());
                openDialogOnAir();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        recyclerView4.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView4, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Tugas tugas = recyclerList4.get(position);
                global.setDataIdTugas(tugas.getId());
                global.setDataJudulTugas(tugas.getHeader());
                global.setDataCeritaTugas(tugas.getParagraph());
                global.setDataWaktuTugas(tugas.getDeadline());
                global.setDataMataPelajaranTugas(tugas.getAcademic());
                global.setDataGambarTugas(tugas.getImage());
                global.setDataPrioritasTugas(tugas.getPrioritas());
                openDialogOnAir();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();
        data.putExtra("intentCallbackGrupProfile", global.getDataIntentKY());
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }

    public void update(final String newNamaGrup, final String newCeritaGrup, final String newAvatarGrup) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_UPDATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    String upload = jsonObject.getString("upload");
                    if (success.equals("1")) {
                        // AND CHECK IF THERE IS UPLOADING IMAGE
                        if (upload.equals("1")) {
                            global.setDataGrupAvatar(newAvatarGrup+".png");
                        }
                        global.setDataGrupNama(newNamaGrup);
                        global.setDataGrupCerita(newCeritaGrup);

                        Toast.makeText(GrupProfileActivity.this, "All Saved!", Toast.LENGTH_SHORT).show();
                        global.setDataIntentKY("update-grup");
                        finish();
                    } else {
                        openErrorDialog("ERROR: ".concat(message));
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
                params.put("grupid", global.getDataGrupId());
                params.put("newNamaGrup", newNamaGrup);
                params.put("newCeritaGrup", newCeritaGrup);
                params.put("newAvatarGrup", newAvatarGrup);
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

    public void openDialogRiwayat() {
        DialogRiwayat dialogRiwayat = new DialogRiwayat();
        dialogRiwayat.show(getSupportFragmentManager(), "Riwayat dialog");
    }

    public void openDialogOnAir() {
        DialogOnAir dialogOnAir = new DialogOnAir();
        dialogOnAir.show(getSupportFragmentManager(), "On Air dialog");
    }

    public void openDialogHapusGrup(View view) {
        DialogHapusGrup dialogHapusGrup = new DialogHapusGrup();
        dialogHapusGrup.show(getSupportFragmentManager(), "Hapus Grup dialog");
    }

    public void openUndangTemanActivity(View view) {
        startActivity(new Intent(GrupProfileActivity.this, UndangTemanActivity.class));
    }

    public void openAnggotaActivity(View view) {
        startActivity(new Intent(GrupProfileActivity.this, AnggotaActivity.class));
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

    /**
     * This method will be invoked when the user clicks a button
     */
    public void selectImageFromGallery() {
        // invoke the image gallery using an implict intent.
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // where do we want to find the data?
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        // finally, get a URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // set the data and type.  Get all image types.
        photoPickerIntent.setDataAndType(data, "image/*");

        // we will invoke this activity, and get something back from it.
        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        global.setDataInteger(0);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Toast.makeText(this, "Image Saved.", Toast.LENGTH_LONG).show();
            }
            // if we are here, everything processed successfully.
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                // if we are here, we are hearing back from the image gallery.

                // the address of the image on the SD Card.
                Uri imageUri = data.getData();

                // declare a stream to read the image data from the SD Card.
                InputStream inputStream;

                // we are getting an input stream, based on the URI of the image.
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    // get a bitmap from the stream.
                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    uriFromGallery = imageUri;
                    // show the image to the user
                    profile_gprfact_image.setImageBitmap(image);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // show a message to the user indictating that the image is unavailable.
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }

            }
        }

        if (resultCode == RESULT_OK && requestCode == 0) {
            if (data.hasExtra("intentCallbackEditTugas")) {
                String result = data.getStringExtra("intentCallbackEditTugas");
                if (result.equals("update-tugas")) {
                    Toast.makeText(GrupProfileActivity.this, "Updated-Tugas Active", Toast.LENGTH_SHORT).show();
                    load();
                }
            }
        }
        if (resultCode == RESULT_OK && requestCode == 0) {
            if (data.hasExtra("intentCallbackMataPelajaran")) {
                String result = data.getStringExtra("intentCallbackMataPelajaran");
                if (result.equals("update-matapelajaran")) {
                    loadMataPelajaran();
                }
            }
        }
    }

    public void hapusTugas(final String tugasid) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_HAPUS_TUGAS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            if (success.equals("1")) {
                                global.setDataIntentKY("update-tugas");
                                load();
                            } else {
                                openErrorDialog("ERROR: ".concat(message));
                            }
                        }catch (JSONException e) {
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
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tugasid", tugasid);
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

    public void delete() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_DELETE_GRUP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");

                    if (success.equals("1")) {
                        Snackbar.make(recyclerView2, "Grup berhasil dihapus! redirect...", Snackbar.LENGTH_LONG).show();
                        global.setDataIntentKY("delete-grup");
                        finish();
                    } else {
                        openErrorDialog("ERROR: ".concat(message));
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
    }

    @Override
    public void onDelete() {
        hapusTugas(global.getDataIdTugas());
    }

    @Override
    public void onHapusGrup() {
        delete();
    }

    @Override
    public void onEdit() {
        startActivityForResult(new Intent(GrupProfileActivity.this, EditTugasActivity.class), 0);
    }

    public void openErrorDialog(String errorString) {
        Snackbar.make(recyclerView2, errorString, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    void createNotificationChannel() {
        Uri sound = Uri.parse("android.resource://"+this.getPackageName()+"/"+R.raw.opening);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = getString(R.string.channel_description);

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(sound, attributes); // This is IMPORTANT
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onExit() {

    }
}
