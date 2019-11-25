package com.example.tugaskita30;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TambahTugasActivity extends AppCompatActivity {

    Global global = Global.getInstance();
    String URL_POST = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/register_tugas.php");

    CalendarView calendar_tbhtgs_calendar;
    TextView calendar_tbhtgs_text, processing_tbhtgs_text, progress_tbhtgs_text;
    ProgressBar progress_tbhact_progressbar;
    EditText tugas_tbhtgs_input, info_tbhtgs_input;
    Button matematika_peminatan_tbhtgs_button, matematika_wajib_tbhtgs_button, agama_tbhtgs_button, ppkn_tbhtgs_button, senibudaya_tbhtgs_button, penjaskes_tbhtgs_button, fisika_tbhtgs_button, kimia_tbhtgs_button, biologi_tbhtgs_button, geografi_tbhtgs_button, sejarah_tbhtgs_button, sosiologi_tbhtgs_button, bahasaindonesia_tbhtgs_button, bahasamandarin_tbhtgs_button, bahasainggris_tbhtgs_button, komputer_tbhtgs_button, lintasminat_tbhtgs_button, lainnya_tbhtgs_button, wirausaha_tbhtgs_button, mulok_tbhtgs_button;
    Button wajib_tbhtgs_button, tidakwajib_tbhtgs_button;

    String agama = "agama", ppkn = "ppkn", bahasa_indonesia = "bahasa_indonesia", matematika_peminatan = "matematika_peminatan", matematika_wajib = "matematika_wajib", fisika = "fisika", kimia = "kimia", biologi = "biologi", geografi = "geografi", sejarah = "sejarah", sosiologi = "sosiologi", seni_budaya = "seni_budaya", penjaskes = "penjaskes", bahasa_mandarin = "bahasa_mandarin", bahasa_inggris = "bahasa_inggris", lintas_minat = "lintas_minat", komputer = "komputer", lainnya = "lainnya", wirausaha = "wirausaha", mulok = "mulok";
    String wajib = "wajib", tidak_wajib = "tidak wajib";

    LinearLayout tambah_tbhact_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_tugas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progress_tbhact_progressbar = findViewById(R.id.progress_tbhact_progressbar);
        progress_tbhtgs_text = findViewById(R.id.progress_tbhact_text);
        tambah_tbhact_layout = findViewById(R.id.tambah_tbhact_layout);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tugas = tugas_tbhtgs_input.getText().toString().trim();
                String matapelajaran = global.getDataTambahTugasMataPelajaran();
                String dibuat = global.getDataTambahTugasDibuat();
                String waktu = global.getDataTambahTugasWaktu();
                String info = info_tbhtgs_input.getText().toString().trim();
                String prioritas = global.getDataTambahTugasPrioritas();

                if (tugas.isEmpty()) {
                    tugas_tbhtgs_input.setError("input belum masuk");
                } else if (matapelajaran == null) {
                    openErrorDialog("Mata Pelajaran belum dimasukan");
                } else if (waktu == null) {
                    openErrorDialog("Waktu belum dimasukan");
                } else if (prioritas == null) {
                    openErrorDialog("Prioritas belum dimasukan");
                } else {
                    tambah_tbhact_layout.setVisibility(View.GONE);
                    progress_tbhact_progressbar.setVisibility(View.VISIBLE);
                    progress_tbhtgs_text.setVisibility(View.VISIBLE);

                    tambah(global.getDataUserId(), tugas, matapelajaran, dibuat, waktu, prioritas, info, global.getDataGrupId());
                }
            }
        });

        tugas_tbhtgs_input = findViewById(R.id.tugas_tbhtgs_input);
        tugas_tbhtgs_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String value = tugas_tbhtgs_input.getText().toString().trim();
                global.setDataTambahTugasJudul(value);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = tugas_tbhtgs_input.getText().toString().trim();
                global.setDataTambahTugasJudul(value);
            }
            @Override
            public void afterTextChanged(Editable s) {
                String value = tugas_tbhtgs_input.getText().toString().trim();
                global.setDataTambahTugasJudul(value);
            }
        });

        info_tbhtgs_input = findViewById(R.id.info_tbhtgs_input);
        info_tbhtgs_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String value = info_tbhtgs_input.getText().toString().trim();
                global.setDataTambahTugasCerita(value);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = info_tbhtgs_input.getText().toString().trim();
                global.setDataTambahTugasCerita(value);
            }
            @Override
            public void afterTextChanged(Editable s) {
                String value = info_tbhtgs_input.getText().toString().trim();
                global.setDataTambahTugasCerita(value);
            }
        });

        calendar_tbhtgs_text = findViewById(R.id.calendar_tbhtgs_text);
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        global.setDataTambahTugasDibuat(formatDateStripe(day, month, year));
        global.setDataTambahTugasWaktu(formatDateStripe(day, month, year)); // Default
        calendar_tbhtgs_text.setText(formatDate(day, month, year));
        calendar_tbhtgs_calendar = findViewById(R.id.calendar_tbhtgs_calendar);
        calendar_tbhtgs_calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month = month + 1;
                calendar_tbhtgs_text.setText(formatDate(dayOfMonth, month, year));
                global.setDataTambahTugasWaktu(formatDateStripe(dayOfMonth, month, year));
                Toast.makeText(TambahTugasActivity.this, "Deadline Changed", Toast.LENGTH_SHORT).show();
            }
        });

        matematika_peminatan_tbhtgs_button = findViewById(R.id.matematika_peminatan_tbhtgs_button);
        matematika_peminatan_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(matematika_peminatan_tbhtgs_button, matematika_peminatan);
            }
        });

        matematika_wajib_tbhtgs_button = findViewById(R.id.matematika_wajib_tbhtgs_button);
        matematika_wajib_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(matematika_wajib_tbhtgs_button, matematika_wajib);
            }
        });

        agama_tbhtgs_button = findViewById(R.id.agama_tbhtgs_button);
        agama_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(agama_tbhtgs_button, agama);
            }
        });

        ppkn_tbhtgs_button = findViewById(R.id.ppkn_tbhtgs_button);
        ppkn_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(ppkn_tbhtgs_button, ppkn);
            }
        });

        senibudaya_tbhtgs_button = findViewById(R.id.senibudaya_tbhtgs_button);
        senibudaya_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(senibudaya_tbhtgs_button, seni_budaya);
            }
        });

        penjaskes_tbhtgs_button = findViewById(R.id.penjaskes_tbhtgs_button);
        penjaskes_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(penjaskes_tbhtgs_button, penjaskes);
            }
        });

        fisika_tbhtgs_button = findViewById(R.id.fisika_tbhtgs_button);
        fisika_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(fisika_tbhtgs_button, fisika);
            }
        });

        kimia_tbhtgs_button = findViewById(R.id.kimia_tbhtgs_button);
        kimia_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(kimia_tbhtgs_button, kimia);
            }
        });

        biologi_tbhtgs_button = findViewById(R.id.biologi_tbhtgs_button);
        biologi_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(biologi_tbhtgs_button, biologi);
            }
        });

        geografi_tbhtgs_button = findViewById(R.id.geografi_tbhtgs_button);
        geografi_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(geografi_tbhtgs_button, geografi);
            }
        });

        sejarah_tbhtgs_button = findViewById(R.id.sejarah_tbhtgs_button);
        sejarah_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(sejarah_tbhtgs_button, sejarah);
            }
        });

        sosiologi_tbhtgs_button = findViewById(R.id.sosiologi_tbhtgs_button);
        sosiologi_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(sosiologi_tbhtgs_button, sosiologi);
            }
        });

        bahasaindonesia_tbhtgs_button = findViewById(R.id.bahasaindonesia_tbhtgs_button);
        bahasaindonesia_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(bahasaindonesia_tbhtgs_button, bahasa_indonesia);
            }
        });

        bahasamandarin_tbhtgs_button = findViewById(R.id.bahasamandarin_tbhtgs_button);
        bahasamandarin_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(bahasamandarin_tbhtgs_button, bahasa_mandarin);
            }
        });

        bahasainggris_tbhtgs_button = findViewById(R.id.bahasainggris_tbhtgs_button);
        bahasainggris_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(bahasainggris_tbhtgs_button, bahasa_inggris);
            }
        });

        komputer_tbhtgs_button = findViewById(R.id.komputer_tbhtgs_button);
        komputer_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(komputer_tbhtgs_button, komputer);
            }
        });

        lintasminat_tbhtgs_button = findViewById(R.id.lintasminat_tbhtgs_button);
        lintasminat_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(lintasminat_tbhtgs_button, lintas_minat);
            }
        });

        wirausaha_tbhtgs_button = findViewById(R.id.wirausaha_tbhtgs_button);
        wirausaha_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(wirausaha_tbhtgs_button, wirausaha);
            }
        });

        mulok_tbhtgs_button = findViewById(R.id.mulok_tbhtgs_button);
        mulok_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(mulok_tbhtgs_button, mulok);
            }
        });

        lainnya_tbhtgs_button = findViewById(R.id.lainnya_tbhtgs_button);
        lainnya_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(lainnya_tbhtgs_button, lainnya);
            }
        });


        wajib_tbhtgs_button = findViewById(R.id.wajib_tbhtgs_button);
        wajib_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPrioritas(wajib_tbhtgs_button, wajib);
            }
        });

        tidakwajib_tbhtgs_button = findViewById(R.id.tidakwajib_tbhtgs_button);
        tidakwajib_tbhtgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPrioritas(tidakwajib_tbhtgs_button, tidak_wajib);
            }
        });

        processing_tbhtgs_text = findViewById(R.id.processing_tbhtgs_text);
        processing_tbhtgs_text.setText("Loaded");
    }

    public String formatDateStripe(int day, int month, int year) {
        String strDay = String.valueOf(day);
        String strMonth = String.valueOf(month);
        String strYear = String.valueOf(year);
        return strDay.concat("-").concat(strMonth).concat("-").concat(strYear);
    }

    public String formatDate(int day, int month, int year) {
        String m;
        switch (month) {
            case 1: m = "Januari"; break;
            case 2: m = "Februari"; break;
            case 3: m = "Maret"; break;
            case 4: m = "April"; break;
            case 5: m = "Mei"; break;
            case 6: m = "Juni"; break;
            case 7: m = "Juli"; break;
            case 8: m = "Agustus"; break;
            case 9: m = "September"; break;
            case 10: m = "Oktober"; break;
            case 11: m = "November"; break;
            case 12: m = "Desember"; break;
            default: m = "Unknown";break;
        }
        return String.valueOf(day).concat(" ").concat(m).concat(" ").concat(String.valueOf(year));
    }

    public void selectMataPelajaran(Button button, String value) {
        resetButtonPelajaran(); // Every Click Reset
        button.setBackgroundResource(R.drawable.rounded_solid_blue_selected_border);// New Selected Button
        global.setDataTambahTugasMataPelajaran(value); // Set Global Variable
        Toast.makeText(this, "Mata Pelajaran Changed", Toast.LENGTH_SHORT).show();
    }

    public void resetButtonPelajaran() {
        int drawable = R.drawable.rounded_solid_blue_border;
        matematika_peminatan_tbhtgs_button.setBackgroundResource(drawable);
        matematika_wajib_tbhtgs_button.setBackgroundResource(drawable);
        agama_tbhtgs_button.setBackgroundResource(drawable);
        ppkn_tbhtgs_button.setBackgroundResource(drawable);
        senibudaya_tbhtgs_button.setBackgroundResource(drawable);
        penjaskes_tbhtgs_button.setBackgroundResource(drawable);
        fisika_tbhtgs_button.setBackgroundResource(drawable);
        kimia_tbhtgs_button.setBackgroundResource(drawable);
        biologi_tbhtgs_button.setBackgroundResource(drawable);
        geografi_tbhtgs_button.setBackgroundResource(drawable);
        sejarah_tbhtgs_button.setBackgroundResource(drawable);
        sosiologi_tbhtgs_button.setBackgroundResource(drawable);
        bahasaindonesia_tbhtgs_button.setBackgroundResource(drawable);
        bahasamandarin_tbhtgs_button.setBackgroundResource(drawable);
        bahasainggris_tbhtgs_button.setBackgroundResource(drawable);
        komputer_tbhtgs_button.setBackgroundResource(drawable);
        lintasminat_tbhtgs_button.setBackgroundResource(drawable);
        lainnya_tbhtgs_button.setBackgroundResource(drawable);
        wirausaha_tbhtgs_button.setBackgroundResource(drawable);
        mulok_tbhtgs_button.setBackgroundResource(drawable);
    }

    public void selectPrioritas(Button button, String value) {
        resetButtonPrioritas();
        button.setBackgroundResource(R.drawable.rounded_solid_orange_selected_border);
        global.setDataTambahTugasPrioritas(value);
        Toast.makeText(this, "Prioritas Changed", Toast.LENGTH_SHORT).show();
    }

    public void resetButtonPrioritas() {
        int drawable = R.drawable.rounded_solid_orange_border;
        wajib_tbhtgs_button.setBackgroundResource(drawable);
        tidakwajib_tbhtgs_button.setBackgroundResource(drawable);
    }

    public void tambah(final String userid, final String tugasnama, final String tugaspelajaran, final String tugasdibuat, final String tugaswaktu, final String tugasprioritas, final String tugascerita, final String tugasposisi) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        progress_tbhact_progressbar.setProgress(50, true);
                    else
                        progress_tbhact_progressbar.setProgress(50);
                    progress_tbhtgs_text.setText("50%");
                    processing_tbhtgs_text.setText("Success created! please wait...");

                    jsonObject = new JSONObject(response);
                    String Qsuccess = jsonObject.getString("success");
                    String Qmessage = jsonObject.getString("message");
                    if (Qsuccess.equals("1")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            progress_tbhact_progressbar.setProgress(100, true);
                        else
                            progress_tbhact_progressbar.setProgress(100);
                        progress_tbhtgs_text.setText("100%");
                        processing_tbhtgs_text.setText("Updating...");

                        SystemClock.sleep(3000);
                        resetSession();
                        processing_tbhtgs_text.setText("Added");
                        bukaTugasActivity();
                    } else {
                        openErrorDialog("Kesalahan terjadi, coba lagi");
                        processing_tbhtgs_text.setText("Kesalahan terjadi, coba lagi");
                        tambah_tbhact_layout.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    openErrorDialog("JSON Error: ".concat(e.toString()));
                    processing_tbhtgs_text.setText("JSON Error");
                    tambah_tbhact_layout.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    //This indicates that the reuest has time out
                    openErrorDialog("Server terlalu lama merespon! coba lagi nanti");
                    processing_tbhtgs_text.setText("Server terlalu lama merespon! coba lagi nanti");
                } else if (error instanceof NoConnectionError) {
                    //This indicates that the reuest has no connection
                    openErrorDialog("Check koneksi internet Anda!");
                    processing_tbhtgs_text.setText("Check koneksi internet Anda!");
                } else if (error instanceof AuthFailureError) {
                    //Error indicating that there was an Authentication Failure while performing the request
                    openErrorDialog("Authentication gagal! coba beberapa saat lagi");
                    processing_tbhtgs_text.setText("Authentication gagal! coba beberapa saat lagi");
                } else if (error instanceof ServerError) {
                    //Indicates that the server responded with a error response
                    openErrorDialog("Server Error!");
                    processing_tbhtgs_text.setText("Server Error!");
                } else if (error instanceof NetworkError) {
                    //Indicates that there was network error while performing the request
                    openErrorDialog("Network Anda mengalami error!");
                    processing_tbhtgs_text.setText("Network Anda mengalami error!");
                } else if (error instanceof ParseError) {
                    // Indicates that the server response could not be parsed
                    openErrorDialog("Server tidak bisa parse permintaan Anda!");
                    processing_tbhtgs_text.setText("Server tidak bisa parse permintaan Anda!");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", userid);
                params.put("tugasnama", tugasnama);
                params.put("tugaspelajaran", tugaspelajaran);
                params.put("tugasdibuat", tugasdibuat);
                params.put("tugaswaktu", tugaswaktu);
                params.put("tugasprioritas", tugasprioritas);
                params.put("tugasposisi", tugasposisi);
                params.put("tugascerita", tugascerita);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void resetSession() {
        global.setDataTambahTugasJudul(null);
        global.setDataTambahTugasMataPelajaran(null);
        global.setDataTambahTugasWaktu(null);
        global.setDataTambahTugasCerita(null);
        global.setDataTambahTugasPrioritas(null);
    }

    @Override
    public void onBackPressed() {
        global.setDataIntentKY("end");
        finish();
        super.onBackPressed();
    }

    @Override
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();
        data.putExtra("intentCallbackTambahTugas", global.getDataIntentKY());
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }

    public void bukaTugasActivity() {
        global.setDataIntentKY("tambah-tugas");
        finish();
    }

    public void openErrorDialog(String errorString) {
        DialogError dialogError = new DialogError(errorString);
        dialogError.show(getSupportFragmentManager(), "Error dialog");
    }
}
