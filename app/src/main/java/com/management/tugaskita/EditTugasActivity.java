package com.management.tugaskita;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
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

public class EditTugasActivity extends AppCompatActivity {

    Global global = Global.getInstance();
    String URL_POST = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/update_tugas.php");

    CalendarView calendar_edttgs_calendar;
    TextView calendar_edttgs_text, calendar_edttgs_text2, processing_edttgs_text, mata_pelajaran_edttgs_text, prioritas_edttgs_text;
    EditText tugas_edttgs_input, info_edttgs_input;
    Button matematika_peminatan_edttgs_button, matematika_wajib_edttgs_button, agama_edttgs_button, ppkn_edttgs_button, senibudaya_edttgs_button, penjaskes_edttgs_button, fisika_edttgs_button, kimia_edttgs_button, biologi_edttgs_button, geografi_edttgs_button, sejarah_edttgs_button, sosiologi_edttgs_button, bahasaindonesia_edttgs_button, bahasamandarin_edttgs_button, bahasainggris_edttgs_button, komputer_edttgs_button, lintasminat_edttgs_button, lainnya_edttgs_button, wirausaha_edttgs_button, mulok_edttgs_button;
    Button wajib_edttgs_button, tidakwajib_edttgs_button;

    String agama = "agama", ppkn = "ppkn", bahasa_indonesia = "bahasa_indonesia", matematika_peminatan = "matematika_peminatan", matematika_wajib = "matematika_wajib", fisika = "fisika", kimia = "kimia", biologi = "biologi", geografi = "geografi", sejarah = "sejarah", sosiologi = "sosiologi", seni_budaya = "seni_budaya", penjaskes = "penjaskes", bahasa_mandarin = "bahasa_mandarin", bahasa_inggris = "bahasa_inggris", lintas_minat = "lintas_minat", komputer = "komputer", lainnya = "lainnya", wirausaha = "wirausaha", mulok = "mulok";
    String wajib = "wajib", tidak_wajib = "tidak wajib";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tugas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        processing_edttgs_text = findViewById(R.id.processing_edttgs_text);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tugas = tugas_edttgs_input.getText().toString().trim();
                String matapelajaran = global.getDataMataPelajaranTugas();
                String waktu = global.getDataWaktuTugas();
                String info = info_edttgs_input.getText().toString().trim();
                String prioritas = global.getDataPrioritasTugas();

                if (tugas.isEmpty()) {
                    openErrorDialog("Input belum masuk");
                    tugas_edttgs_input.setError("input belum masuk");
                } else if (matapelajaran == null) {
                    openErrorDialog("Mata pelajaran belum dimasukan!");
                } else if (waktu == null) {
                    openErrorDialog("Waktu belum dimasukan!");
                } else if (prioritas == null) {
                    openErrorDialog("Prioritas belum dimasukan");
                } else {
                    update(global.getDataIdTugas(), tugas, waktu, matapelajaran, info, prioritas);
                }
            }
        });

        tugas_edttgs_input = findViewById(R.id.tugas_edttgs_input);
        tugas_edttgs_input.setText(global.getDataJudulTugas());
        tugas_edttgs_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String value = tugas_edttgs_input.getText().toString().trim();
                global.setDataJudulTugas(value);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = tugas_edttgs_input.getText().toString().trim();
                global.setDataJudulTugas(value);
            }
            @Override
            public void afterTextChanged(Editable s) {
                String value = tugas_edttgs_input.getText().toString().trim();
                global.setDataJudulTugas(value);
            }
        });

        info_edttgs_input = findViewById(R.id.info_edttgs_input);
        info_edttgs_input.setText(global.getDataCeritaTugas());
        info_edttgs_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String value = info_edttgs_input.getText().toString().trim();
                global.setDataCeritaTugas(value);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = info_edttgs_input.getText().toString().trim();
                global.setDataCeritaTugas(value);
            }
            @Override
            public void afterTextChanged(Editable s) {
                String value = info_edttgs_input.getText().toString().trim();
                global.setDataCeritaTugas(value);
            }
        });
        String[] convdate = global.getDataWaktuTugas().split("-");
        int yearDB = Integer.parseInt(convdate[2]);
        int monthDB = Integer.parseInt(convdate[1]);
        int dayDB = Integer.parseInt(convdate[0]);
        calendar_edttgs_text2 = findViewById(R.id.calendar_edttgs_text2);
        calendar_edttgs_text2.setText("Batas Waktu - selected: ".concat(formatDate(dayDB, monthDB, yearDB)));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, yearDB);
        cal.set(Calendar.MONTH, monthDB);
        cal.set(Calendar.DAY_OF_MONTH, dayDB);
        calendar_edttgs_text = findViewById(R.id.calendar_edttgs_text);
        calendar_edttgs_text.setText(formatDate(dayDB, monthDB, yearDB));
        calendar_edttgs_calendar = findViewById(R.id.calendar_edttgs_calendar);
        calendar_edttgs_calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month = month + 1;
                calendar_edttgs_text.setText(formatDate(dayOfMonth, month, year));
                global.setDataWaktuTugas(String.valueOf(dayOfMonth).concat("-").concat(String.valueOf(month)).concat("-").concat(String.valueOf(year)));
                Toast.makeText(EditTugasActivity.this, "Deadline Changed", Toast.LENGTH_SHORT).show();
            }
        });

        matematika_peminatan_edttgs_button = findViewById(R.id.matematika_peminatan_edttgs_button);
        matematika_peminatan_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(matematika_peminatan_edttgs_button, matematika_peminatan);
            }
        });

        matematika_wajib_edttgs_button = findViewById(R.id.matematika_wajib_edttgs_button);
        matematika_wajib_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(matematika_wajib_edttgs_button, matematika_wajib);
            }
        });

        agama_edttgs_button = findViewById(R.id.agama_edttgs_button);
        agama_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(agama_edttgs_button, agama);
            }
        });

        ppkn_edttgs_button = findViewById(R.id.ppkn_edttgs_button);
        ppkn_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(ppkn_edttgs_button, ppkn);
            }
        });

        senibudaya_edttgs_button = findViewById(R.id.senibudaya_edttgs_button);
        senibudaya_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(senibudaya_edttgs_button, seni_budaya);
            }
        });

        penjaskes_edttgs_button = findViewById(R.id.penjaskes_edttgs_button);
        penjaskes_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(penjaskes_edttgs_button, penjaskes);
            }
        });

        fisika_edttgs_button = findViewById(R.id.fisika_edttgs_button);
        fisika_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(fisika_edttgs_button, fisika);
            }
        });

        kimia_edttgs_button = findViewById(R.id.kimia_edttgs_button);
        kimia_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(kimia_edttgs_button, kimia);
            }
        });

        biologi_edttgs_button = findViewById(R.id.biologi_edttgs_button);
        biologi_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(biologi_edttgs_button, biologi);
            }
        });

        geografi_edttgs_button = findViewById(R.id.geografi_edttgs_button);
        geografi_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(geografi_edttgs_button, geografi);
            }
        });

        sejarah_edttgs_button = findViewById(R.id.sejarah_edttgs_button);
        sejarah_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(sejarah_edttgs_button, sejarah);
            }
        });

        sosiologi_edttgs_button = findViewById(R.id.sosiologi_edttgs_button);
        sosiologi_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(sosiologi_edttgs_button, sosiologi);
            }
        });

        bahasaindonesia_edttgs_button = findViewById(R.id.bahasaindonesia_edttgs_button);
        bahasaindonesia_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(bahasaindonesia_edttgs_button, bahasa_indonesia);
            }
        });

        bahasamandarin_edttgs_button = findViewById(R.id.bahasamandarin_edttgs_button);
        bahasamandarin_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(bahasamandarin_edttgs_button, bahasa_mandarin);
            }
        });

        bahasainggris_edttgs_button = findViewById(R.id.bahasainggris_edttgs_button);
        bahasainggris_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(bahasainggris_edttgs_button, bahasa_inggris);
            }
        });

        komputer_edttgs_button = findViewById(R.id.komputer_edttgs_button);
        komputer_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(komputer_edttgs_button, komputer);
            }
        });

        lintasminat_edttgs_button = findViewById(R.id.lintasminat_edttgs_button);
        lintasminat_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(lintasminat_edttgs_button, lintas_minat);
            }
        });

        wirausaha_edttgs_button = findViewById(R.id.lainnya_edttgs_button);
        wirausaha_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(wirausaha_edttgs_button, wirausaha);
            }
        });

        mulok_edttgs_button = findViewById(R.id.lainnya_edttgs_button);
        mulok_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(mulok_edttgs_button, mulok);
            }
        });

        lainnya_edttgs_button = findViewById(R.id.lainnya_edttgs_button);
        lainnya_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMataPelajaran(lainnya_edttgs_button, lainnya);
            }
        });


        wajib_edttgs_button = findViewById(R.id.wajib_edttgs_button);
        wajib_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPrioritas(wajib_edttgs_button, wajib);
            }
        });

        tidakwajib_edttgs_button = findViewById(R.id.tidakwajib_edttgs_button);
        tidakwajib_edttgs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPrioritas(tidakwajib_edttgs_button, tidak_wajib);
            }
        });


        mata_pelajaran_edttgs_text = findViewById(R.id.mata_pelajaran_edttgs_text);
        mata_pelajaran_edttgs_text.setText("Mata Pelajaran - selected: ".concat(global.getDataMataPelajaranTugas().toUpperCase()));

        prioritas_edttgs_text = findViewById(R.id.prioritas_edttgs_text);
        prioritas_edttgs_text.setText("Prioritas - selected: ".concat(global.getDataPrioritasTugas().toUpperCase()));

        processing_edttgs_text.setText("Loaded");
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
        global.setDataMataPelajaranTugas(value); // Set Global Variable
        Toast.makeText(this, "Mata Pelajaran Changed", Toast.LENGTH_SHORT).show();
    }

    public void resetButtonPelajaran() {
        int drawable = R.drawable.rounded_solid_blue_border;
        matematika_peminatan_edttgs_button.setBackgroundResource(drawable);
        matematika_wajib_edttgs_button.setBackgroundResource(drawable);
        agama_edttgs_button.setBackgroundResource(drawable);
        ppkn_edttgs_button.setBackgroundResource(drawable);
        senibudaya_edttgs_button.setBackgroundResource(drawable);
        penjaskes_edttgs_button.setBackgroundResource(drawable);
        fisika_edttgs_button.setBackgroundResource(drawable);
        kimia_edttgs_button.setBackgroundResource(drawable);
        biologi_edttgs_button.setBackgroundResource(drawable);
        geografi_edttgs_button.setBackgroundResource(drawable);
        sejarah_edttgs_button.setBackgroundResource(drawable);
        sosiologi_edttgs_button.setBackgroundResource(drawable);
        bahasaindonesia_edttgs_button.setBackgroundResource(drawable);
        bahasamandarin_edttgs_button.setBackgroundResource(drawable);
        bahasainggris_edttgs_button.setBackgroundResource(drawable);
        komputer_edttgs_button.setBackgroundResource(drawable);
        lintasminat_edttgs_button.setBackgroundResource(drawable);
        lainnya_edttgs_button.setBackgroundResource(drawable);
        wirausaha_edttgs_button.setBackgroundResource(drawable);
        mulok_edttgs_button.setBackgroundResource(drawable);
    }

    public void selectPrioritas(Button button, String value) {
        resetButtonPrioritas();
        button.setBackgroundResource(R.drawable.rounded_solid_orange_selected_border);
        global.setDataPrioritasTugas(value);
        Toast.makeText(this, "Prioritas Changed", Toast.LENGTH_SHORT).show();
    }

    public void resetButtonPrioritas() {
        int drawable = R.drawable.rounded_solid_orange_border;
        wajib_edttgs_button.setBackgroundResource(drawable);
        tidakwajib_edttgs_button.setBackgroundResource(drawable);
    }

    @Override
    public void finish() {
        // Prepare data intent
        Intent data = new Intent();
        data.putExtra("intentCallbackEditTugas", global.getDataIntentKY());
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }

    public void update(final String tugasid, final String newnamatugas, final String newwaktutugas, final String newmatapelajarantugas, final String newceritatugas, final String newprioritastugas) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            processing_edttgs_text.setText("Saving...");
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            if (success.equals("1")) {
                                processing_edttgs_text.setText("Saved");
                                global.setDataIntentKY("update-tugas");
                                finish();
                            } else {
                                processing_edttgs_text.setText("Something Error: ".concat(message));
                                openErrorDialog("ERROR: ".concat(message));
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                            openErrorDialog("JSON error: ".concat(e.toString()));
                            processing_edttgs_text.setText("JSON error");
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
                params.put("newnamatugas", newnamatugas);
                params.put("newmatapelajarantugas", newmatapelajarantugas);
                params.put("newwaktutugas", newwaktutugas);
                params.put("newceritatugas", newceritatugas);
                params.put("newprioritastugas", newprioritastugas);
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

    public void openErrorDialog(String errorString) {
        DialogError dialogError = new DialogError(errorString);
        dialogError.show(getSupportFragmentManager(), "Error dialog");
    }
}
