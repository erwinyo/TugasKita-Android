package com.example.tugaskita30;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AbsentScanActivity extends AppCompatActivity {

    Global global = Global.getInstance();
    String URL_POST_GET_GRUP_DATA = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/get_grupdata.php");
    String URL_POST_ABSENT = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/absent.php");
    String URL_POST_INCREASE_POINT = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/increase_point.php");

    ImageView profile_absact_image, grup_absact_image;
    TextView status_absact_text, point_absact_text, profile_absact_text, grup_absact_text, support_absact_text;
    ProgressBar progress_absact;
    Button continue_absact_button;
    AdView mAdView;

    boolean late = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absent_scan);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");

        status_absact_text = findViewById(R.id.status_absact_text);
        point_absact_text = findViewById(R.id.point_absact_text);
        profile_absact_image = findViewById(R.id.profile_absact_image);
        profile_absact_text = findViewById(R.id.profile_absact_text);
        grup_absact_image = findViewById(R.id.grup_absact_image);
        grup_absact_text = findViewById(R.id.grup_absact_text);
        support_absact_text = findViewById(R.id.support_absact_text);
        continue_absact_button = findViewById(R.id.continue_absact_button);
        progress_absact = findViewById(R.id.progress_absact);
        mAdView = findViewById(R.id.banner_absact_adview);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                progress_absact.setVisibility(View.GONE);
                support_absact_text.setVisibility(View.VISIBLE);
                continue_absact_button.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                progress_absact.setVisibility(View.GONE);
                continue_absact_button.setVisibility(View.VISIBLE);
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

        Date date = new Date();
        SimpleDateFormat formatter_time = new SimpleDateFormat("HH:mm:ss");
        String time = formatter_time.format(date);
        String[] timeArray = time.split(":");
        int hour = Integer.parseInt(timeArray[0]);
        int minute = Integer.parseInt(timeArray[1]);
        int second = Integer.parseInt(timeArray[2]);


        if (hour == 6) {
            getGrupData(data);
        } else if (hour >= 7 && hour <= 14) {
            late = true;
            getGrupData(data);
        } else {
            Intent intent_to = new Intent(AbsentScanActivity.this, ErrorScanActivity.class);
            intent_to.putExtra("data", "You can't absent at this time!");
            startActivity(intent_to);
            finish();
        }
    }

    public void openScanActivity(View view) {
        finish();
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

                        String firstname = global.getDataUserNamaLengkap().split(" ")[0];
                        String firstnamegrup = grup_nama.split(" ")[0].concat(" ").concat(grup_nama.split(" ")[1]);
                        profile_absact_text.setText(firstname);
                        grup_absact_text.setText(firstnamegrup);

                        global.setDataScanAbsentId(grupid);
                        global.setDataScanAbsentNama(grup_nama);
                        global.setDataScanAbsentCerita(grup_cerita);
                        global.setDataScanAbsentAvatar(grup_avatar);
                        global.setDataScanAbsentTotal(grup_total);

                        Date date = new Date();
                        SimpleDateFormat formatter_time = new SimpleDateFormat("EE, dd MMMM yyyy HH:mm:ss");
                        global.setDataScanAbsentCheckIn(formatter_time.format(date));

                        SimpleDateFormat formatter_date = new SimpleDateFormat("dd-MM-yyyy");
                        String SPD_date = formatter_date.format(date);

                        MediaPlayer mediaPlayer = MediaPlayer.create(AbsentScanActivity.this, R.raw.ding);
                        mediaPlayer.start(); // no need to call prepare(); create() does that for you

                        absent(global.getDataUserId(), global.getDataScanAbsentId(), SPD_date, global.getDataScanAbsentCheckIn());
                    } else {
                        Toast.makeText(AbsentScanActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        status_absact_text.setText("Something Error!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AbsentScanActivity.this, "JSON error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    status_absact_text.setText("Something Error!");
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
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void absent(final String userid, final String grupid, final String date, final String time) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_ABSENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Qsuccess = jsonObject.getString("success");
                    String Qmessage = jsonObject.getString("message");

                    String url = MediaManager.get().url().transformation(new Transformation().aspectRatio("1:1").gravity("auto").radius("max").width(200).crop("fill")).secure(true).generate("tugaskita/grup_profile/".concat(global.getDataScanAbsentAvatar().concat(".png")));
                    Picasso.with(AbsentScanActivity.this).load(url)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.team_loaded)
                            .into(grup_absact_image);

                    url = MediaManager.get().url().transformation(new Transformation().aspectRatio("1:1").gravity("auto").radius("max").width(200).crop("fill")).secure(true).generate("tugaskita/profile/".concat(global.getDataUserAvatar()));
                    Picasso.with(AbsentScanActivity.this).load(url)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.team_loaded)
                            .into(profile_absact_image);

                    // Load Ads
                    AdRequest adRequest = new AdRequest.Builder().addTestDevice("C5829DD8C19EF0D95A5B9D8C91229FCF").build();
                    mAdView.loadAd(adRequest);

                    if (Qsuccess.equals("1")) {
                        // Call increasePoint Function
                        if (late) {
                            increasePoint(global.getDataUserId(), "50");
                        } else {
                            increasePoint(global.getDataUserId(), "100");
                        }
                    } else if (Qsuccess.equals("001")) {
                        status_absact_text.setText(Qmessage);
                        point_absact_text.setVisibility(View.INVISIBLE);
                    } else if (Qsuccess.equals("002")) {
                        status_absact_text.setText("Double Absent!");
                        point_absact_text.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AbsentScanActivity.this, "JSON error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    status_absact_text.setText("Something Error!");
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
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void increasePoint(final String userid, final String point) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_INCREASE_POINT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Qsuccess = jsonObject.getString("success");
                    String Qmessage = jsonObject.getString("message");
                    if (Qsuccess.equals("1")) {
                        point_absact_text.setText("+".concat(point).concat(" points"));
                        point_absact_text.setVisibility(View.VISIBLE);
                        String new_userpoint = String.valueOf(Integer.valueOf(global.getDataUserPoint()) + Integer.valueOf(point));
                        global.setDataUserPoint(new_userpoint);
                        global.setDataIntentKY("new-absent");
                        status_absact_text.setText("Done!");
                    } else {
                        Toast.makeText(AbsentScanActivity.this, "Error: ".concat(Qmessage), Toast.LENGTH_SHORT).show();
                        status_absact_text.setText("Something Error!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AbsentScanActivity.this, "JSON error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    status_absact_text.setText("Something Error!");
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
                params.put("point", point);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void openErrorDialog(String errorString) {
        Snackbar.make(status_absact_text, errorString, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
