package com.example.tugaskita30;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CouponScanActivity extends AppCompatActivity {

    Global global = Global.getInstance();
    String URL_POST_GET_COUPON_DATA = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/get_coupondata.php");
    String URL_POST_DEACTIVED_COUPON = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/deactived_coupon.php");
    TextView point_coupact_text, id_coupact_text, status_coupact_text;
    ImageView qr_coupact_image;
    Button selesai_coupact_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_scan);

        Intent intent = getIntent();
        final String id = intent.getStringExtra("data");

        getCouponData(id);
        status_coupact_text = findViewById(R.id.status_coupact_text);
        point_coupact_text = findViewById(R.id.point_coupact_text);
        id_coupact_text = findViewById(R.id.id_coupact_text);
        qr_coupact_image = findViewById(R.id.qr_coupact_image);
        selesai_coupact_button = findViewById(R.id.selesai_coupact_button);

        selesai_coupact_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deactivedCoupon(id);
            }
        });
    }

    public void openScanActivity(View view) {
        finish();
    }

    public void getCouponData(final String couponid) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_GET_COUPON_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Qsuccess = jsonObject.getString("success");
                    String Qmessage = jsonObject.getString("message");
                    if (Qsuccess.equals("1")) {
                        status_coupact_text.setText("Coupon Loaded!");
                        String point = jsonObject.getString("coupon_point");
                        String id = jsonObject.getString("coupon_id");
                        String qr = "coupon-".concat(id);

                        String pointText = "+".concat(point).concat(" Points");
                        point_coupact_text.setText(pointText);
                        id_coupact_text.setText(id);
                        String text=qr; // Whatever you need to encode in the QR code
                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                        try {
                            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,100,100);
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                            qr_coupact_image.setImageBitmap(bitmap);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }

                    } else if (Qsuccess.equals("001")) {
                        selesai_coupact_button.setVisibility(View.INVISIBLE);
                        status_coupact_text.setVisibility(View.INVISIBLE);
                        point_coupact_text.setVisibility(View.INVISIBLE);
                        id_coupact_text.setText("coupon tidak ditemukan!");
                        qr_coupact_image.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(CouponScanActivity.this, ErrorScanActivity.class);
                        intent.putExtra("data", "coupon tidak ditemukan");
                        startActivity(intent);
                    } else if (Qsuccess.equals("002")) {
                        selesai_coupact_button.setVisibility(View.INVISIBLE);
                        status_coupact_text.setVisibility(View.INVISIBLE);
                        point_coupact_text.setVisibility(View.INVISIBLE);
                        id_coupact_text.setText("coupon sudah digunakan");
                        qr_coupact_image.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(CouponScanActivity.this, ErrorScanActivity.class);
                        intent.putExtra("data", "coupon sudah digunakan");
                        startActivity(intent);
                    } else {
                        selesai_coupact_button.setVisibility(View.INVISIBLE);
                        status_coupact_text.setVisibility(View.INVISIBLE);
                        point_coupact_text.setVisibility(View.INVISIBLE);
                        id_coupact_text.setText("coupon error!");
                        qr_coupact_image.setVisibility(View.INVISIBLE);
                        openErrorDialog("ERROR: ".concat(Qmessage));
                        status_coupact_text.setText("something error!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    openErrorDialog("JSON error: ".concat(e.toString()));
                    status_coupact_text.setText("Something Error!");
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
            status_coupact_text.setText("Something Error!");
        }
    }) {
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("couponid", couponid);
            return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void deactivedCoupon(final String couponid) {
        selesai_coupact_button.setText("processing...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_DEACTIVED_COUPON, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Qsuccess = jsonObject.getString("success");
                    String Qmessage = jsonObject.getString("message");
                    if (Qsuccess.equals("1")) {
                        status_coupact_text.setText("Done!");
                        finish();
                    } else {
                        selesai_coupact_button.setText("nonaktifkan kupon");
                        openErrorDialog("ERROR: ".concat(Qmessage));
                        status_coupact_text.setText("Something Error!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    openErrorDialog("JSON error: ".concat(e.toString()));
                    status_coupact_text.setText("Something Error!");
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
                status_coupact_text.setText("Something Error!");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("couponid", couponid);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
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
