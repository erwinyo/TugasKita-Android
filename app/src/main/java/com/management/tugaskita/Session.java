package com.management.tugaskita;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void sessionClear(Context cntx) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(cntx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public boolean sessionCheckKeyword(String keyword) {
        return prefs.contains(keyword);
    }

    // Store JSONObject
    public void setJSONObject(String userObject,String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, userObject);
        editor.apply();
    }

    public String getJSONObject(String key) {
        return prefs.getString(key, null);
    }

    // Store String
    public void setStringSession(String userString,String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, userString);
        editor.apply();
    }

    public String getStringsession(String key) {
        return prefs.getString(key, null);
    }

    public void setUserFirstIn(String user_first_in) {
        prefs.edit().putString("user_first_in", user_first_in).apply();
    }
    public void setUserId(String user_id) {
        prefs.edit().putString("user_id", user_id).apply();
    }
    public void setUserNamaLengkap(String user_namalengkap) {
        prefs.edit().putString("user_namalengkap", user_namalengkap).apply();
    }
    public void setUserNama(String user_nama) {
        prefs.edit().putString("user_nama", user_nama).apply();
    }
    public void setUserEmail(String user_email) {
        prefs.edit().putString("user_email", user_email).apply();
    }
    public void setUserPassword(String user_password) {
        prefs.edit().putString("user_password", user_password).apply();
    }
    public void setUserCerita(String user_cerita) {
        prefs.edit().putString("user_cerita", user_cerita).apply();
    }
    public void setUserAvatar(String user_avatar) {
        prefs.edit().putString("user_avatar", user_avatar).apply();
    }
    public void setUserAvatarAlternative(String user_avatar_alternative) {
        prefs.edit().putString("user_avatar_alternative", user_avatar_alternative).apply();
    }
    public void setUserPoint(String user_point) {
        prefs.edit().putString("user_point", user_point).apply();
    }
    public void setUserNotification(String user_notification) {
        prefs.edit().putString("user_notification", user_notification).apply();
    }
    public void setUserJobs(String user_jobs) {
        prefs.edit().putString("user_jobs", user_jobs).apply();
    }
    public void setUserNewJob(String user_newjob) {
        prefs.edit().putString("user_newjob", user_newjob).apply();
    }
    public void setUserInvitation(String user_invitation) {
        prefs.edit().putString("user_invitation", user_invitation).apply();
    }
    public void setAnnouncement(String announcement) {
        prefs.edit().putString("announcement", announcement).apply();
    }

    public void setCBT(String cbt) {
        prefs.edit().putString("cbt", cbt).apply();
    }




    public String getUserFirstIn() {
        String user_first_in = prefs.getString("user_first_in","");
        return user_first_in;
    }
    public String getUserId() {
        String user_id = prefs.getString("user_id","");
        return user_id;
    }
    public String getUserNamaLengkap() {
        String user_namalengkap = prefs.getString("user_namalengkap","");
        return user_namalengkap;
    }
    public String getUserNama() {
        String user_nama = prefs.getString("user_nama","");
        return user_nama;
    }
    public String getUserEmail() {
        String user_email = prefs.getString("user_email","");
        return user_email;
    }
    public String getUserPassword() {
        String user_password = prefs.getString("user_password","");
        return user_password;
    }
    public String getUserCerita() {
        String user_cerita  = prefs.getString("user_cerita","");
        return user_cerita;
    }
    public String getUserAvatar() {
        String user_avatar = prefs.getString("user_avatar","");
        return user_avatar;
    }
    public String getUserAvatarAlternative() {
        String user_avatar_alternative = prefs.getString("user_avatar_alternative","");
        return user_avatar_alternative;
    }
    public String getUserPoint() {
        String user_point = prefs.getString("user_point","");
        return user_point;
    }
    public String getUserNotification() {
        String user_notification = prefs.getString("user_notification","");
        return user_notification;
    }
    public String getUserJobs() {
        String user_jobs = prefs.getString("user_jobs", "");
        return user_jobs;
    }
    public String getUserNewJob() {
        String user_newjob = prefs.getString("user_newjob", "");
        return user_newjob;
    }
    public String getUserInvitation() {
        String user_invitation = prefs.getString("user_invitation", "");
        return user_invitation;
    }
    public String getAnnouncement() {
        String announcement = prefs.getString("announcement", "");
        return announcement;
    }

    public String getCBT() {
        String cbt = prefs.getString("cbt", "");
        return cbt;
    }
}
