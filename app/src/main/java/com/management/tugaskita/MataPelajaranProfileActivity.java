package com.management.tugaskita;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MataPelajaranProfileActivity extends AppCompatActivity {

    Global global = Global.getInstance();
    String URL_POST_UPDATE_MATA_PELAJARAN = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/update_matapelajaran.php");

    EditText matapelajaran_mprfact_input;
    Button save_mprfact_button;

    String day, data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mata_pelajaran_profile);

        Intent intent = getIntent();
        String[] rawData = intent.getStringExtra("rawData").split("-");
        day = rawData[0];
        if (rawData.length == 1) {
            data = "";
        } else {
            data = rawData[1];
        }

        matapelajaran_mprfact_input = findViewById(R.id.matapelajaran_mprfact_input);
        matapelajaran_mprfact_input.setText(data);

        save_mprfact_button = findViewById(R.id.save_mprfact_button);
        save_mprfact_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_mprfact_button.setText("Loading...");
                String data = matapelajaran_mprfact_input.getText().toString().trim().toLowerCase();
                update(data, day);
            }
        });
    }

    public void update(final String data, final String day) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_UPDATE_MATA_PELAJARAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            if (success.equals("1")) {
                                // SET UPDATED INFORMATION
                                Toast.makeText(MataPelajaranProfileActivity.this, "All Saved", Toast.LENGTH_SHORT).show();
                                global.setDataIntentKY("update-matapelajaran");
                                finish();
                            } else {
                                save_mprfact_button.setText("save");
                                Toast.makeText(MataPelajaranProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MataPelajaranProfileActivity.this, "JSON error: "+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MataPelajaranProfileActivity.this, "Volley error: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grupid", global.getDataGrupId());
                params.put("day", day);
                params.put("data", data);

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
        data.putExtra("intentCallbackMataPelajaran", global.getDataIntentKY());
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }

    public void openErrorDialog(String errorString) {
        DialogError dialogError = new DialogError(errorString);
        dialogError.show(getSupportFragmentManager(), "Error dialog");
    }
}
