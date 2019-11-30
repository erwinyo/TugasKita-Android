package com.example.tugaskita30;

import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import com.example.tugaskita30.ui.login.LoginActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    Global global = Global.getInstance();
    Session session;
    String URL_POST_GRUP = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/load_grup.php");
    String URL_POST_UNDANGAN = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/load_undangan_masuk.php");
    String URL_POST_SETUP = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/check_profile.php");
    CardView undanganMasukLinearLayout, setupProfileLinearLayout;
    ImageView profile_image;
    ProgressBar progress_grup;
    ImageView errGrup;
    Uri uri;
    CarouselView carouselView;
    int[] sampleImages = {R.drawable.apa_itu_tugaskita, R.drawable.tugaskita_target, R.drawable.tugaskita_checkpoint, R.drawable.tugaskita_cbt, R.drawable.tugaskita_insight};
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };
    boolean doubleBackToExitPressedOnce = false;
    private List<Grup> recyclerList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    TextView group_text, welcome_text, welcome_email, welcome_username, welcome_notify, point_text;
    AdView mAdView;
    boolean TimeoutError = false;
    boolean NoConnectionError = false;
    boolean AuthFailureError = false;
    boolean ServerError = false;
    boolean NetworkError = false;
    boolean ParseError = false;

    AnimationDrawable bannerTukitAnimation;
    ImageView banner_tukit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        session = new Session(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        banner_tukit = findViewById(R.id.banner_tukit);
        banner_tukit.setBackgroundResource(R.drawable.banner_animation);
        bannerTukitAnimation = (AnimationDrawable) banner_tukit.getBackground();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, ComingSoonActivity.class));
            }
        });


        errGrup = findViewById(R.id.errGrup);
        progress_grup = findViewById(R.id.progress_grup);
        carouselView = findViewById(R.id.news_carousel);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);
        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                if (position == 0) {
                    startActivity(new Intent(DashboardActivity.this, WhatsNewInTugasKitaActivity.class));
                } else if (position == 1) {
                    startActivity(new Intent(DashboardActivity.this, TugaskitaTargetInfoActivity.class));
                } else if (position == 2) {
                    startActivity(new Intent(DashboardActivity.this, TugaskitaCheckpointInfoActivity.class));
                } else if (position == 3) {
                    startActivity(new Intent(DashboardActivity.this, TugaskitaCBTInfoActivity.class));
                } else if (position == 4) {
                    startActivity(new Intent(DashboardActivity.this, TugaskitaInsightInfoActivity.class));
                }

            }
        });
        mAdView = findViewById(R.id.ads_adview);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        // Load Ads
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("C5829DD8C19EF0D95A5B9D8C91229FCF").build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mAdView.setVisibility(View.VISIBLE);
                banner_tukit.setVisibility(View.GONE);
                // Load Profile
                String url = MediaManager.get().url().transformation(new Transformation().aspectRatio("1:1").gravity("auto").radius("max").width(200).crop("fill")).secure(true).generate("tugaskita/profile/".concat(global.getDataUserAvatar()));
                Picasso.with(DashboardActivity.this).load(url)
                        .placeholder(R.drawable.placeholder_image)
                        .error(global.getDefaultProfilePicture(session.getUserAvatarAlternative()))
                        .into(profile_image);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                mAdView.setVisibility(View.GONE);
                banner_tukit.setVisibility(View.VISIBLE);
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

        group_text = findViewById(R.id.group_text);
        welcome_text = findViewById(R.id.welcome_text);
        welcome_email = findViewById(R.id.welcome_email);
        welcome_username = findViewById(R.id.welcome_username);
        welcome_notify = findViewById(R.id.welcome_notify);
        point_text = findViewById(R.id.pnt_text);
        undanganMasukLinearLayout = findViewById(R.id.undangan_masuk_linear_layout);
        setupProfileLinearLayout = findViewById(R.id.setup_profile_linear_layout);
        profile_image = findViewById(R.id.profile_image);


        // Load Profile
        String url = "Empty";
        Picasso.with(DashboardActivity.this).load(url)
                .placeholder(R.drawable.placeholder_image)
                .error(global.getDefaultProfilePicture(session.getUserAvatarAlternative()))
                .into(profile_image);

        // Call function
        undanganVisibility();
        setupVisibility();

        // Predefined
        welcome_text.setText("Hello, ".concat(session.getUserNamaLengkap().split(" ")[0])); // Select first word
        welcome_email.setText(session.getUserEmail());
        welcome_username.setText("ID : ".concat(session.getUserNama()));
        if (session.getUserNotification().equals("0")) {
            welcome_notify.setTextColor(ContextCompat.getColor(this, R.color.redDark));
            welcome_notify.setText("NOTIFY ME : OFF");
        } else {
            welcome_notify.setTextColor(ContextCompat.getColor(this, R.color.greenDark));
            welcome_notify.setText("NOTIFY ME : ON");
        }
        point_text.setText(session.getUserPoint());

        //////////////////////////////////////////////////////////////////////////////////////////
        // Grup

        recyclerList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView = findViewById(R.id.gpl_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ListAdapterGrup(this, recyclerList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, 8));
        recyclerView.setAdapter(mAdapter);

        // Grup
        loadGrup();
    }

    @Override
    public ComponentName startService(Intent service) {

        return super.startService(service);
    }

    public int onStartCommand (Intent intent, int flags, int startId) {
        return 1;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile_dshact) {
            openProfileActivity();
            return true;
        } else if (id == R.id.action_logout_dshact) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        bannerTukitAnimation.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        global.setDataInteger(0); // reset error
        if (resultCode == RESULT_OK && requestCode == 0) {
            if (data.hasExtra("intentCallbackTugas")) {
                String result = data.getStringExtra("intentCallbackTugas");
                if (result.equals("update-grup")) {
                    loadGrup();
                    // Reset
                    global.setDataIntentKY("end");
                }
                if (result.equals("keluar-grup")) {
                    loadGrup();
                    // Reset
                    global.setDataIntentKY("end");
                }
                if (result.equals("delete-grup")) {
                    loadGrup();
                    // Reset
                    global.setDataIntentKY("end");
                }
                if (result.equals("tambah-tugas")) {
                    loadGrup();
                    // Reset
                    global.setDataIntentKY("end");
                }
                if (result.equals("update-tugas")) {
                    loadGrup();
                    // Reset
                    global.setDataIntentKY("end");
                }
            }
            if (data.hasExtra("intentCallbackScan")) {
                String result = data.getStringExtra("intentCallbackScan");
                if (result.equals("new-absent")) {
                    point_text.setText(global.getDataUserPoint());
                    // Reset
                    global.setDataIntentKY("end");
                }
                if (result.equals("update-grup")) {
                    loadGrup();
                    // Reset
                    global.setDataIntentKY("end");
                }
            }
            if (data.hasExtra("intentCallbackNewGroup")) {
                String result = data.getStringExtra("intentCallbackNewGroup");
                if (result.equals("new-grup")) {
                    loadGrup();
                    // Reset
                    global.setDataIntentKY("end");
                }
            }
            if (data.hasExtra("intentCallbackUndanganMasuk")) {
                String result = data.getStringExtra("intentCallbackUndanganMasuk");
                if (result.equals("undangan-grup")) {
                    undanganVisibility();
                    loadGrup();
                    // Reset
                    global.setDataIntentKY("end");
                }
            }
            if (data.hasExtra("intentCallbackProfile")) {
                String result = data.getStringExtra("intentCallbackProfile");
                if (result.equals("update-profile")) {
                    welcome_username.setText("ID : ".concat(session.getUserNama()));
                    if (session.getUserNotification().equals("0")) {
                        welcome_notify.setTextColor(ContextCompat.getColor(this, R.color.redDark));
                        welcome_notify.setText("NOTIFY ME : OFF");
                    } else {
                        welcome_notify.setTextColor(ContextCompat.getColor(this, R.color.greenDark));
                        welcome_notify.setText("NOTIFY ME : ON");
                    }
                    // Reset
                    global.setDataIntentKY("end");
                }
            }
        }
    }

    public void loadGrup() {
        recyclerList.clear(); // clear the list
        errGrup.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        progress_grup.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_GRUP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("grup");
                    if (success.equals("1")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String w_idgrup = object.getString("idgrup");
                            String w_namagrup = object.getString("namagrup");
                            String w_deskripsigrup = object.getString("deskripsigrup");
                            String w_tugasgrup = object.getString("jumlahtugasgrup");
                            String w_anggotagrup = object.getString("jumlahanggotagrup");
                            String w_avatargrup = object.getString("avatargrup");
                            String w_statusingrup = object.getString("status_in_grup");
                            recyclerList.add(new Grup(w_avatargrup, w_idgrup, w_namagrup, w_deskripsigrup, w_statusingrup, w_tugasgrup, w_anggotagrup));
                        }
                        mAdapter.notifyDataSetChanged();
                        progress_grup.setVisibility(View.INVISIBLE);
                        session.setJSONObject(jsonObject.toString(), "grup");
                    } else {
                        errGrup.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        group_text.setText("Kelas - tidak ada data");
                        progress_grup.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    openErrorDialog("JSON error: ".concat(e.toString()));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (session.sessionCheckKeyword("grup")) {
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
                        JSONObject jsonObject = new JSONObject(session.getJSONObject("grup"));
                        JSONArray jsonArray = jsonObject.getJSONArray("grup");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String w_idgrup = object.getString("idgrup");
                            String w_namagrup = object.getString("namagrup");
                            String w_deskripsigrup = object.getString("deskripsigrup");
                            String w_tugasgrup = object.getString("jumlahtugasgrup");
                            String w_anggotagrup = object.getString("jumlahanggotagrup");
                            String w_avatargrup = object.getString("avatargrup");
                            String w_statusingrup = object.getString("status_in_grup");
                            recyclerList.add(new Grup(w_avatargrup, w_idgrup, w_namagrup, w_deskripsigrup, w_statusingrup, w_tugasgrup, w_anggotagrup));
                        }
                        mAdapter.notifyDataSetChanged();
                        progress_grup.setVisibility(View.INVISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    errGrup.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    group_text.setText("Kelas - no connection, tidak bisa memuat");
                    progress_grup.setVisibility(View.INVISIBLE);
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
                params.put("userid", session.getUserId());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (global.getDataInteger() == 0) {
                    Grup grup = recyclerList.get(position);
                    global.setDataGrupId(grup.getId());
                    global.setDataGrupNama(grup.getNama());
                    global.setDataGrupCerita(grup.getDeksripsi());
                    global.setDataGrupAvatar(grup.getImageUrl());
                    global.setDataGrupStatusIn(grup.getStatusInGrup());
                    global.setDataInteger(global.getDataInteger()+1);
                    startActivityForResult(new Intent(DashboardActivity.this, TugasActivity.class), 0);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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

    public String formatDate(String date) {
        String[] i = date.split("-");
        return i[0].concat(" ").concat(formatMonth(i[1]));
    }

    public String formatMonth(String nmonth) {
        switch (nmonth) {
            case "1":
                return "Jan";
            case "2":
                return "Feb";
            case "3":
                return "Mar";
            case "4":
                return "Apr";
            case "5":
                return "Mei";
            case "6":
                return "Jun";
            case "7":
                return "Jul";
            case "8":
                return "Agt";
            case "9":
                return "Sep";
            case "10":
                return "Okt";
            case "11":
                return "Nov";
            case "12":
                return "Des";
            default:
                return "err";
        }
    }

    public void openScanActivity(View view) {
        startActivityForResult(new Intent(this, ScanActivity.class), 0);
    }

    public void openUndanganMasukActivity(View view) {
        startActivityForResult(new Intent(this, UndanganMasukActivity.class), 0);
    }

    public void openTambahTemanActivity(View view) {
        startActivity(new Intent(this, TambahTemanActivity.class));
    }

    public void openProfileActivity() {
        startActivityForResult(new Intent(this, ProfileActivity.class), 0);
    }

    public void openSetupProfileActivity(View view) {
        startActivityForResult(new Intent(this, ProfileActivity.class), 0);
    }

    public void openPointActivity(View view) {
        startActivityForResult(new Intent(this, PointActivity.class),0);
    }

    public void openBuatGrupDialog(View view) {
        startActivityForResult(new Intent(this, CreateGroupActivity.class), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
    }

    public void logout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cancelJob();
        }
        global.setDataIntentKY("end");
        global.setDataGrupNama(null);
        global.setDataGrupCerita(null);
        global.setDataGrupAvatar(null);
        global.setDataTambahTugasPrioritas(null);
        global.setDataTambahTugasCerita(null);
        global.setDataTambahTugasWaktu(null);
        global.setDataTambahTugasMataPelajaran(null);
        global.setDataTambahTugasJudul(null);
        global.setDataCeritaTugas(null);
        global.setDataAuthorPicTugas(null);
        global.setDataPosisiTugas(null);
        global.setDataAuthorIdTugas(null);
        global.setDataPrioritasTugas(null);
        global.setDataWaktuTugas(null);
        global.setDataMataPelajaranTugas(null);
        global.setDataJudulTugas(null);
        global.setDataGrupId(null);
        global.setDataUserId(null);

        session.sessionClear(this);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void undanganVisibility() {
        undanganMasukLinearLayout.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_UNDANGAN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("listofinvitation");
                    if (jsonArray.length() > 0) {
                        undanganMasukLinearLayout.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", global.getDataUserId());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void setupVisibility() {
        setupProfileLinearLayout.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_SETUP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    if (success.equals("0")) {
                        setupProfileLinearLayout.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", global.getDataUserId());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void openErrorDialog(String errorString) {
        Snackbar.make(recyclerView, errorString, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            System.exit(0);
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void storeScreenshot(Bitmap bitmap, String filename) {
        String path = Environment.getExternalStorageDirectory().toString() + "/" + filename;
        OutputStream out = null;
        File imageFile = new File(path);

        try {
            out = new FileOutputStream(imageFile);
            // choose JPEG format
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
        } catch (FileNotFoundException e) {
            // manage exception ...
        } catch (IOException e) {
            // manage exception ...
        } finally {

            try {
                if (out != null) {
                    out.close();
                }

            } catch (Exception exc) {
            }

        }
    }
}
