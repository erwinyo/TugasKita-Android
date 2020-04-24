package com.management.tugaskita;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyService extends JobService {
    private static final String TAG = "ExampleJobService";

    Global global = Global.getInstance();
    Session session;
    Context cntx;
    String CHANNEL_ID = "personal notification";
    String URL_POST_AND_NEW_JOB = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/AND_new_job.php");
    String URL_POST_AND_INVITAION = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/AND_invitation.php");
    String URL_POST_AND_ANNOUNCEMENT = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/AND_announcement.php");
    String URL_POST_AND_JOBS = "http://".concat(global.getDataHosting()).concat("/tugaskita/android/3.0/AND_jobs.php");

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        cntx = this;
        session = new Session(this);
        createNotificationChannel();
        doBackgroundWork(params);

        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (session.getUserNotification().equals("1")) {
                    Date date = new Date();
                    SimpleDateFormat formatter_time = new SimpleDateFormat("HH:mm:ss");
                    String time = formatter_time.format(date);
                    String[] timeArray = time.split(":");
                    int hour = Integer.parseInt(timeArray[0]);
                    int minute = Integer.parseInt(timeArray[1]);
                    int second = Integer.parseInt(timeArray[2]);
                    if (hour >= 18)
                        AND_jobs();
                    AND_new_job();
                }
                AND_announcement();
                AND_invitation();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    jobFinished(params, true);
                }
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        boolean jobCancelled = true;
        return true;
    }

    private void createNotificationChannel() {
        Uri sound = Uri.parse("android.resource://"+cntx.getPackageName()+"/"+R.raw.notification);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
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

    public void AND_jobs() { // Automated Notification Deliver
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_AND_JOBS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            StringBuilder job = new StringBuilder();
                            if (success.equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                if (jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String tugasid = object.getString("tugas_id");
                                        job.append(tugasid.concat("/"));
                                        boolean done = false;

                                        String tugas_nama = object.getString("tugas_nama");
                                        String tugas_cerita = object.getString("tugas_cerita");
                                        String academic = object.getString("tugas_pelajaran");
                                        String tugas_waktu = object.getString("tugas_waktu");

                                        int image = R.drawable.lainnya;
                                        String agama = "agama", ppkn = "ppkn", bahasa_indonesia = "bahasa_indonesia", matematika_peminatan = "matematika_peminatan", matematika_wajib = "matematika_wajib", fisika = "fisika", kimia = "kimia", biologi = "biologi", geografi = "geografi", sejarah = "sejarah", sosiologi = "sosiologi", seni_budaya = "seni_budaya", penjaskes = "penjaskes", bahasa_mandarin = "bahasa_mandarin", bahasa_inggris = "bahasa_inggris", lintas_minat = "lintas_minat", komputer = "komputer", lainnya = "lainnya";

                                        if (academic.equals(agama))
                                            image = R.drawable.agama;
                                        else if (academic.equals(ppkn))
                                            image = R.drawable.ppkn;
                                        else if (academic.equals(bahasa_indonesia))
                                            image = R.drawable.bahasa_indonesia;
                                        else if (academic.equals(matematika_peminatan))
                                            image = R.drawable.matematika_peminatan;
                                        else if (academic.equals(matematika_wajib))
                                            image = R.drawable.matematika_wajib;
                                        else if (academic.equals(fisika))
                                            image = R.drawable.fisika;
                                        else if (academic.equals(kimia))
                                            image = R.drawable.kimia;
                                        else if (academic.equals(biologi))
                                            image = R.drawable.biologi;
                                        else if (academic.equals(geografi))
                                            image = R.drawable.geografi;
                                        else if (academic.equals(sejarah))
                                            image = R.drawable.sejarah;
                                        else if (academic.equals(sosiologi))
                                            image = R.drawable.sosiologi;
                                        else if (academic.equals(seni_budaya))
                                            image = R.drawable.seni_budaya;
                                        else if (academic.equals(penjaskes))
                                            image = R.drawable.penjaskes;
                                        else if (academic.equals(bahasa_mandarin))
                                            image = R.drawable.bahasa_mandarin;
                                        else if (academic.equals(bahasa_inggris))
                                            image = R.drawable.bahasa_inggris;
                                        else if (academic.equals(lintas_minat))
                                            image = R.drawable.lintas_minat;
                                        else if (academic.equals(komputer))
                                            image = R.drawable.komputer;

                                        if (session.getUserNewJob() == null) {
                                            global.notificationJob(MyService.this, CHANNEL_ID, tugas_nama, tugas_cerita, image);
                                        } else {
                                            String[] arr = session.getUserJobs().split("/");
                                            for (int x = 0; x < arr.length; x++) {
                                                if (arr[x].equals(tugasid)) {
                                                    done = true;
                                                }
                                            }
                                            // check done
                                            if (!done) {
                                                global.notificationJob(MyService.this, CHANNEL_ID, tugas_nama, tugas_cerita, image);
                                            }
                                        }
                                    }
                                    session.setUserJobs(job.toString());
                                }
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", session.getUserId());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Cookie", "__test=".concat(global.getDataCookie()).concat("; expires=Friday, January 1, 2038 at 6:55:55 AM; path=/"));
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MyService.this);
        requestQueue.add(stringRequest);
    }

    public void AND_new_job() { // Automated Notification Delivery
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_AND_NEW_JOB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            StringBuilder newjob = new StringBuilder();
                            // New Job Notification
                            if (success.equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String tugasid = object.getString("tugas_id");
                                    newjob.append(tugasid.concat("/"));
                                    boolean done = false;

                                    String tugas_nama = object.getString("tugas_nama");
                                    String tugas_pelajaran = object.getString("tugas_pelajaran");
                                    String tugas_waktu = object.getString("tugas_waktu");
                                    String tugas_cerita = object.getString("tugas_cerita");

                                    String contentText = capitalize(tugas_nama).concat("\n").concat(tugas_cerita).concat("\n\n").concat("DEADLINE: ".concat(formatDateToText(tugas_waktu))).concat("\n").concat("Selamat Mengerjakan!");

                                    if (session.getUserNewJob() == null) {
                                        global.notificationNewJob(MyService.this, CHANNEL_ID, randomNewJobTitle(), formatDateToText(tugas_waktu).concat(" - ").concat(tugas_pelajaran.toUpperCase()), contentText);
                                    } else {
                                        String[] arr = session.getUserNewJob().split("/");
                                        for (int x = 0; x < arr.length; x++) {
                                            if (arr[x].equals(tugasid)) {
                                                done = true;
                                            }
                                        }
                                        // check done
                                        if (!done) {
                                            global.notificationNewJob(MyService.this, CHANNEL_ID, randomNewJobTitle(), formatDateToText(tugas_waktu).concat(". ".concat(tugas_pelajaran.toUpperCase())), contentText);
                                        }
                                    }
                                }
                                session.setUserNewJob(newjob.toString());
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", session.getUserId());

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

    public void AND_invitation() { // Automated Notification Delivery
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_AND_INVITAION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            StringBuilder newinvitation = new StringBuilder();
                            // New Job Notification
                            if (success.equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String invitationid = object.getString("id");
                                    newinvitation.append(invitationid.concat("/"));
                                    boolean done = false;

                                    String invite_from = object.getString("invite_from");
                                    String invite_to = object.getString("invite_to");
                                    String invite_to_grup = object.getString("invite_to_grup");
                                    String invite_as_status = object.getString("as_status");

                                    if (session.getUserInvitation() == null) {
                                        global.notificationInvitation(MyService.this, CHANNEL_ID, "Ada yang mengundang Anda!", "seorang mengundang Anda sebagai ".concat(invite_as_status));
                                    } else {
                                        String[] arr = session.getUserInvitation().split("/");
                                        for (int x = 0; x < arr.length; x++) {
                                            if (arr[x].equals(invitationid)) {
                                                done = true;
                                            }
                                        }
                                        // check done
                                        if (!done) {
                                            global.notificationInvitation(MyService.this, CHANNEL_ID, "Ada yang mengundang Anda!", "klik untuk melihat");
                                        }
                                    }
                                }
                                session.setUserInvitation(newinvitation.toString());
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", session.getUserId());

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

    public void AND_announcement() { // Automated Notification Deliver
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_POST_AND_ANNOUNCEMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("message");
                            StringBuilder announcement = new StringBuilder();
                            // New Job Notification
                            if (success.equals("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String id = object.getString("id");
                                    announcement.append(id.concat("/"));
                                    boolean done = false;

                                    String judul = object.getString("judul");
                                    String isi = object.getString("isi");
                                    String waktu = object.getString("waktu");

                                    if (session.getUserNewJob() == null) {
                                        global.notificationAnnoucement(MyService.this, CHANNEL_ID, capitalize(judul), isi);
                                    } else {
                                        String[] arr = session.getAnnouncement().split("/");
                                        for (int x = 0; x < arr.length; x++) {
                                            if (arr[x].equals(id)) {
                                                done = true;
                                            }
                                        }
                                        // check done
                                        if (!done) {
                                            global.notificationAnnoucement(MyService.this, CHANNEL_ID, capitalize(judul), isi);
                                        }
                                    }
                                }
                                session.setAnnouncement(announcement.toString());
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

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

    public String randomNewJobTitle() {
        String sapaan = "";
        String subjek = session.getUserNamaLengkap().split(" ")[0];
        String predikat = "";
        String objek = "";
        String tambahan = "";

        String[] sapaan_data = {"Haloo", "Hey", "Bro", "Hello", "Temanku"};
        String[] predikat_data = {"ada", "muncul", "dapet", "keluar"};
        String[] objek_data = {"tugas", "job", "kerjaan"};
        String[] tambahan_data = {"baru!", "nih!", "baru nih!"};

        Random rand = new Random();
        sapaan = sapaan_data[rand.nextInt(sapaan_data.length)];
        predikat = predikat_data[rand.nextInt(predikat_data.length)];
        objek = objek_data[rand.nextInt(objek_data.length)];
        tambahan = tambahan_data[rand.nextInt(tambahan_data.length)];

        return sapaan.concat(" ".concat(subjek.concat(", ".concat(predikat.concat(" ".concat(objek.concat(" ".concat(tambahan))))))));
    }

    public String formatDateToText(String date) {
        String[] arr = date.split("-");
        String day = arr[0];
        String month = formatMonth(arr[1]);
        String year = arr[2];
        return day.concat(" ".concat(month.concat(" ".concat(year))));
    }

    public String formatMonth(String nmonth) {
        switch (nmonth) {
            case "1": return "Januari";
            case "2": return "Februari";
            case "3": return "Maret";
            case "4": return "April";
            case "5": return "Mei";
            case "6": return "Juni";
            case "7": return "Juli";
            case "8": return "Agustus";
            case "9": return "September";
            case "10": return "Oktober";
            case "11": return "November";
            case "12": return "Desember";
            default:
                return "Date Error";
        }
    }

    public String capitalize(String str)
    {

        // Create a char array of given String
        char[] ch = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {

            // If first character of a word is found
            if (i == 0 && ch[i] != ' ' ||
                    ch[i] != ' ' && ch[i - 1] == ' ') {

                // If it is in lower-case
                if (ch[i] >= 'a' && ch[i] <= 'z') {

                    // Convert into Upper-case
                    ch[i] = (char)(ch[i] - 'a' + 'A');
                }
            }

            // If apart from first character
            // Any one is in Upper-case
            else if (ch[i] >= 'A' && ch[i] <= 'Z')

                // Convert into Lower-Case
                ch[i] = (char)(ch[i] + 'a' - 'A');
        }

        // Convert the char array to equivalent String
        String st = new String(ch);
        return st;
    }
}
