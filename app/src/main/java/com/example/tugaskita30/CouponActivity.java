package com.example.tugaskita30;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CouponActivity extends AppCompatActivity implements  DialogDeactivedKupon.DialogDeactivedKuponListener {

    Global global = Global.getInstance();
    String URL_POST_DEACTIVED_COUPON = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/deactived_coupon.php");

    ImageView qr_coupact_image;
    TextView id_coupact_text, date_coupact_text;
    Button nonaktifkan_coupact_preview_button;

    String id, point, date, qr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        point = intent.getStringExtra("point");
        date = intent.getStringExtra("date");
        qr = "coupon-".concat(id);

        qr_coupact_image = findViewById(R.id.qr_coupact_preview_image);
        id_coupact_text = findViewById(R.id.id_coupact_preview_text);
        date_coupact_text = findViewById(R.id.date_coupact_preview_text);
        nonaktifkan_coupact_preview_button = findViewById(R.id.nonaktifkan_coupact_preview_button);

        String text=qr; // Whatever you need to encode in the QR code
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,250,250);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qr_coupact_image.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        id_coupact_text.setText(id);
        date_coupact_text.setText("Created on: ".concat(date));
        nonaktifkan_coupact_preview_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeactivedKuponDialog();
            }
        });
    }

    public void openDeactivedKuponDialog() {
        DialogDeactivedKupon dialogDeactivedKupon = new DialogDeactivedKupon();
        dialogDeactivedKupon.show(getSupportFragmentManager(), "Deactived Kupon dialog");
    }

    public void openDashboard(View view) {
        global.setDataIntentKY("update-coupon");
        finish();
    }

    @Override
    public void onBackPressed() {
        global.setDataIntentKY("update-coupon");
        finish();
        super.onBackPressed();
    }

    public void deactivedCoupon(final String couponid) {
        nonaktifkan_coupact_preview_button.setText("loading...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_DEACTIVED_COUPON, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Qsuccess = jsonObject.getString("success");
                    String Qmessage = jsonObject.getString("message");
                    if (Qsuccess.equals("1")) {
                        Toast.makeText(CouponActivity.this, "Coupon Deactivated!", Toast.LENGTH_SHORT).show();
                        global.setDataIntentKY("deactived-coupon");
                        finish();
                    } else {
                        nonaktifkan_coupact_preview_button.setText("nonaktifkan kupon");
                        openErrorDialog("ERROR: ".concat(Qmessage));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    openErrorDialog("JSON error: ".concat(e.toString()));
                    nonaktifkan_coupact_preview_button.setText("nonaktifkan kupon");
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

    @Override
    public void onKeluar() {
        deactivedCoupon(id);
    }

    @Override
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();
        data.putExtra("intentCallbackCoupon", global.getDataIntentKY());
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }

    public void openErrorDialog(String errorString) {
        DialogError dialogError = new DialogError(errorString);
        dialogError.show(getSupportFragmentManager(), "Error dialog");
    }
}
