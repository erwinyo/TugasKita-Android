package com.management.tugaskita;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GuestProfileActivity extends AppCompatActivity {

    Global global = Global.getInstance();
    String URL_POST_TAMBAH = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/register_teman.php");

    ImageView profile_guestact_image;
    TextView status_guestact_text, nama_guestact_text, story_guestact_text;
    Button tambah_guestact_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_profile);

        Intent intent = getIntent();
        final String profile = intent.getStringExtra("profile");
        final String namalengkap = intent.getStringExtra("namalengkap");
        final String username = intent.getStringExtra("username");
        final String story = intent.getStringExtra("story");

        status_guestact_text = findViewById(R.id.status_guestact_text);
        profile_guestact_image = findViewById(R.id.profile_guestact_image);
        nama_guestact_text = findViewById(R.id.nama_guestact_text);
        story_guestact_text = findViewById(R.id.story_guestact_text);
        tambah_guestact_button = findViewById(R.id.tambah_guestact_button);

        String url = MediaManager.get().url().transformation(new Transformation().aspectRatio("1:1").gravity("auto").radius("max").width(400).crop("fill")).secure(true).generate("tugaskita/profile/".concat(profile));
        Picasso.get().load(url)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.user)
                .into(profile_guestact_image);
        nama_guestact_text.setText(namalengkap);
        story_guestact_text.setText(story);
        tambah_guestact_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahTeman(global.getDataUserId(), username);
            }
        });
    }

    private void tambahTeman(final String userid, final String guestusername) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_TAMBAH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Qsuccess = jsonObject.getString("success");
                    String Qmessage = jsonObject.getString("message");
                    if (Qsuccess.equals("1")) {
                        status_guestact_text.setText("Friend Added!");
                        tambah_guestact_button.setVisibility(View.INVISIBLE);
                    } else {
                        status_guestact_text.setText("Already Friend!");
                        tambah_guestact_button.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(tambah_guestact_button, "JSON error: "+e.toString(), Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(tambah_guestact_button, "Volley error: "+error.toString(), Snackbar.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", userid);
                params.put("temanusername", guestusername);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Cookie", "__test=".concat(global.getDataCookie()).concat("; expires=Friday, January 1, 2038 at 6:55:55 AM; path=/"));
                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
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
