package com.example.tugaskita30;

import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TransactionCouponActivity extends AppCompatActivity {

    Global global = Global.getInstance();
    String URL_POST_BUY = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/buy_coupon.php");

    ImageView point_transact_image;
    TextView paket_transact_text, subpaket_transact_text, harga_point_transact_text, saldo_point_transact_text, total_subpoint_transact_text, total_point_transact_text;
    Button bayar_transact_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_coupon);

        Intent intent = getIntent();
        final String paket = intent.getStringExtra("paket");
        final String subpaket = intent.getStringExtra("subpaket");
        final String point = intent.getStringExtra("point");
        final String harga = intent.getStringExtra("harga");

        point_transact_image = findViewById(R.id.point_transact_image);
        paket_transact_text = findViewById(R.id.paket_transact_text);
        subpaket_transact_text = findViewById(R.id.subpaket_transact_text);
        harga_point_transact_text = findViewById(R.id.harga_point_transact_text);
        saldo_point_transact_text = findViewById(R.id.saldo_point_transact_text);
        total_subpoint_transact_text = findViewById(R.id.total_subpoint_transact_text);
        total_point_transact_text = findViewById(R.id.total_point_transact_text);
        bayar_transact_button = findViewById(R.id.bayar_transact_button);

        final String saldo = global.getDataUserPoint();
        final String total_subpoint = global.getDataUserPoint().concat(" - ").concat(harga);
        final int saldo_int = Integer.parseInt(saldo);
        final int harga_int = Integer.parseInt(harga);
        final String total_point = String.valueOf(saldo_int - harga_int).concat(" points");
        int image = 0;
        switch (point) {
            case "3": {
                image = R.drawable.plus_3;
                break;
            }
            case "5": {
                image = R.drawable.plus_5;
                break;
            }
            case "9": {
                image = R.drawable.plus_9;
                break;
            }
        }
        point_transact_image.setImageResource(image);
        paket_transact_text.setText(paket);
        subpaket_transact_text.setText(subpaket);
        harga_point_transact_text.setText(harga);
        saldo_point_transact_text.setText(saldo);
        total_subpoint_transact_text.setText(total_subpoint);
        total_point_transact_text.setText(total_point);
        bayar_transact_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bayar_transact_button.setText("Loading...");
                bayar(global.getDataUserId(), harga, point);
            }
        });
    }

    public void openPointActivity(View view) {
        global.setDataIntentKY("end");
        Toast.makeText(this, "Transaksi dibatalkan!", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void bayar(final String userid, final String harga, final String point) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_BUY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            if (success.equals("1")) {
                                // Update global variable user_point
                                String new_userpoint = jsonObject.getString("new_userpoint");
                                global.setDataUserPoint(new_userpoint); // update point

                                global.setDataIntentKY("update-coupon"); // update intent keyword
                                Toast.makeText(TransactionCouponActivity.this, "Transaksi berhasil!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                bayar_transact_button.setText("bayar");
                                Toast.makeText(TransactionCouponActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Intent intent = new Intent(TransactionCouponActivity.this, ErrorActivity.class);
                            intent.putExtra("error", "JSON error: "+e.toString());
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            //This indicates that the reuest has time out
                            Intent intent = new Intent(TransactionCouponActivity.this, ErrorActivity.class);
                            intent.putExtra("error", "Server terlalu lama merespon! coba lagi nanti");
                            startActivity(intent);
                            finish();
                        } else if (error instanceof NoConnectionError) {
                            //This indicates that the reuest has no connection
                            Intent intent = new Intent(TransactionCouponActivity.this, ErrorActivity.class);
                            intent.putExtra("error", "Check koneksi internet Anda!");
                            startActivity(intent);
                            finish();
                        } else if (error instanceof AuthFailureError) {
                            //Error indicating that there was an Authentication Failure while performing the request
                            Intent intent = new Intent(TransactionCouponActivity.this, ErrorActivity.class);
                            intent.putExtra("error", "Authentication gagal! coba beberapa saat lagi");
                            startActivity(intent);
                            finish();
                        } else if (error instanceof ServerError) {
                            //Indicates that the server responded with a error response
                            Intent intent = new Intent(TransactionCouponActivity.this, ErrorActivity.class);
                            intent.putExtra("error", "Server Error!");
                            startActivity(intent);
                            finish();
                        } else if (error instanceof NetworkError) {
                            //Indicates that there was network error while performing the request
                            Intent intent = new Intent(TransactionCouponActivity.this, ErrorActivity.class);
                            intent.putExtra("error", "Network Anda mengalami error!");
                            startActivity(intent);
                            finish();
                        } else if (error instanceof ParseError) {
                            // Indicates that the server response could not be parsed
                            Intent intent = new Intent(TransactionCouponActivity.this, ErrorActivity.class);
                            intent.putExtra("error", "Server tidak bisa parse permintaan Anda!");
                            startActivity(intent);
                            finish();
                        }
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", userid);
                params.put("harga", harga);
                params.put("point", point);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        global.setDataIntentKY("end");
        Toast.makeText(this, "Transaksi dibatalkan!", Toast.LENGTH_SHORT).show();
        finish();
        super.onBackPressed();
    }

    @Override
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();
        data.putExtra("intentCallbackTransactionCoupon", global.getDataUserPoint());
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }

    public void openErrorDialog(String errorString) {
        DialogError dialogError = new DialogError(errorString);
        dialogError.show(getSupportFragmentManager(), "Error dialog");
    }
}
