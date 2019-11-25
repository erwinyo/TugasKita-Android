package com.example.tugaskita30;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Map;
import java.util.Random;

public class Global extends AppCompatActivity {

    private static Global instance;

    private String hosting = "sibercenter.com";

    private String intentKY = "end";
    private int integer = 0;

    private String CHANNEL_ID_CBT = "cbt notification";
    public String getChannelIdCbt(){
        return this.CHANNEL_ID_CBT;
    }

    private String data_grupid;
    private String data_grupnama;
    private String data_grupcerita;
    private String data_grupavatar;
    private String data_grupstatusin;

    private String data_useridundanganmasuk;
    private String data_grupidundanganmasuk;
    private String data_grupnamaundanganmasuk;
    private String data_grupasstatusundanganmasuk;

    private String data_userid;
    private String data_usernamalengkap;
    private String data_usernama;
    private String data_useremail;
    private String data_usercerita;
    private String data_useravatar;
    private String data_userpoint;

    private String data_userstatusingrup;

    private Map<String, String> JOBS;

    private String data_idtugas;
    private String data_judultugas;
    private String data_waktutugas;
    private String data_waktutugasharilagi;
    private String data_matapelajarantugas;
    private String data_prioritastugas;
    private String data_authoridtugas;
    private String data_authornamatugas;
    private String data_authorpictugas;
    private String data_posisitugas;
    private String data_ceritatugas;
    private int data_gambartugas;
    private int data_logotugas;

    private String data_tambahtugas_judul;
    private String data_tambahtugas_matapelajaran;
    private String data_tambahtugas_dibuat;
    private String data_tambahtugas_waktu;
    private String data_tambahtugas_cerita;
    private String data_tambahtugas_prioritas;

    private String data_anggotaid;
    private String data_anggotanamalengkap;
    private String data_anggotanama;
    private String data_anggotaemail;
    private String data_anggotaavatar;
    private String data_anggotastatus;

    private String data_scanaddid;
    private String data_scanaddnamalengkap;
    private String data_scanaddnama;
    private String data_scanaddemail;
    private String data_scanaddavatar;

    private String data_scanabsentid;
    private String data_scanabsentnama;
    private String data_scanabsentcerita;
    private String data_scanabsentavatar;
    private String data_scanabsenttotal;
    private String data_scanabsentcheckin;

    private String data_info;


    // Restrict the constructor from being instantiated
    private Global(){}

    public void setDataHosting(String d){
        this.hosting=d;
    }
    public String getDataHosting(){
        return this.hosting;
    }

    public void setDataIntentKY(String d){
        this.intentKY=d;
    }
    public String getDataIntentKY(){
        return this.intentKY;
    }

    public void setDataInteger(int d){
        this.integer=d;
    }
    public int getDataInteger(){
        return this.integer;
    }



    public void setDataGrupId(String d){
        this.data_grupid=d;
    }
    public String getDataGrupId(){
        return this.data_grupid;
    }

    public void setDataGrupNama(String d){
        this.data_grupnama=d;
    }
    public String getDataGrupNama(){
        return this.data_grupnama;
    }

    public void setDataGrupCerita(String d){
        this.data_grupcerita=d;
    }
    public String getDataGrupCerita(){
        return this.data_grupcerita;
    }

    public void setDataGrupAvatar(String d){
        this.data_grupavatar=d;
    }
    public String getDataGrupAvatar(){
        return this.data_grupavatar;
    }

    public void setDataGrupStatusIn(String d){
        this.data_grupstatusin=d;
    }
    public String getDataGrupStatusIn(){
        return this.data_grupstatusin;
    }


    public void setDataUserIdUndanganMasuk(String d){
        this.data_useridundanganmasuk=d;
    }
    public String getDataUserIdUndanganMasuk(){
        return this.data_useridundanganmasuk;
    }

    public void setDataGrupIdUndanganMasuk(String d){
        this.data_grupidundanganmasuk=d;
    }
    public String getDataGrupIdUndanganMasuk(){
        return this.data_grupidundanganmasuk;
    }

    public void setDataGrupNamaUndanganMasuk(String d){
        this.data_grupnamaundanganmasuk=d;
    }
    public String getDataGrupNamaUndanganMasuk(){
        return this.data_grupnamaundanganmasuk;
    }

    public void setDataGrupAsStatusUndanganMasuk(String d){
        this.data_grupasstatusundanganmasuk=d;
    }
    public String getDataGrupAsStatusUndanganMasuk(){
        return this.data_grupasstatusundanganmasuk;
    }




    public void setDataUserId(String d){
        this.data_userid=d;
    }
    public String getDataUserId(){
        return this.data_userid;
    }

    public void setDataUserNamaLengkap(String d){
        this.data_usernamalengkap=d;
    }
    public String getDataUserNamaLengkap(){
        return this.data_usernamalengkap;
    }

    public void setDataUserNama(String d){
        this.data_usernama=d;
    }
    public String getDataUserNama(){
        return this.data_usernama;
    }

    public void setDataUserEmail(String d){
        this.data_useremail=d;
    }
    public String getDataUserEmail(){
        return this.data_useremail;
    }

    public void setDataUserCerita(String d){
        this.data_usercerita=d;
    }
    public String getDataUserCerita(){
        return this.data_usercerita;
    }

    public void setDataUserAvatar(String d){
        this.data_useravatar=d;
    }
    public String getDataUserAvatar(){
        return this.data_useravatar;
    }

    public void setDataUserPoint(String d){
        this.data_userpoint=d;
    }
    public String getDataUserPoint(){
        return this.data_userpoint;
    }




    public void setDataUserStatusInGrup(String d){
        this.data_userstatusingrup=d;
    }
    public String getDataUserStatusInGrup(){
        return this.data_userstatusingrup;
    }


    public void setDataJOBS(Map<String, String> d){
        this.JOBS=d;
    }
    public Map<String, String> getDataJOBS(){
        return this.JOBS;
    }




    public void setDataIdTugas(String d){
        this.data_idtugas=d;
    }
    public String getDataIdTugas(){
        return this.data_idtugas;
    }

    public void setDataJudulTugas(String d){
        this.data_judultugas=d;
    }
    public String getDataJudulTugas(){
        return this.data_judultugas;
    }

    public void setDataWaktuTugas(String d){
        this.data_waktutugas=d;
    }
    public String getDataWaktuTugas(){
        return this.data_waktutugas;
    }

    public void setDataWaktuTugasHariLagi(String d){
        this.data_waktutugasharilagi=d;
    }
    public String getDataWaktuTugasHariLagi(){
        return this.data_waktutugasharilagi;
    }

    public void setDataMataPelajaranTugas(String d){
        this.data_matapelajarantugas=d;
    }
    public String getDataMataPelajaranTugas(){
        return this.data_matapelajarantugas;
    }

    public void setDataPrioritasTugas(String d){
        this.data_prioritastugas=d;
    }
    public String getDataPrioritasTugas(){
        return this.data_prioritastugas;
    }

    public void setDataAuthorIdTugas(String d){
        this.data_authoridtugas=d;
    }
    public String getDataAuthorIdTugas(){
        return this.data_authoridtugas;
    }

    public void setDataAuthorNamaTugas(String d){
        this.data_authornamatugas=d;
    }
    public String getDataAuthorNamaTugas(){
        return this.data_authornamatugas;
    }

    public void setDataAuthorPicTugas(String d){
        this.data_authorpictugas=d;
    }
    public String getDataAuthorPicTugas(){
        return this.data_authorpictugas;
    }

    public void setDataPosisiTugas(String d){
        this.data_posisitugas=d;
    }
    public String getDataPosisiTugas(){
        return this.data_posisitugas;
    }

    public void setDataCeritaTugas(String d){
        this.data_ceritatugas=d;
    }
    public String getDataCeritaTugas(){
        return this.data_ceritatugas;
    }

    public void setDataGambarTugas(int d){
        this.data_gambartugas=d;
    }
    public int getDataGambarTugas(){
        return this.data_gambartugas;
    }

    public void setDataLogoTugas(int d){
        this.data_logotugas=d;
    }
    public int getDataLogoTugas(){
        return this.data_logotugas;
    }



    public void setDataTambahTugasJudul(String d){
        this.data_tambahtugas_judul=d;
    }
    public String getDataTambahTugasJudul(){
        return this.data_tambahtugas_judul;
    }

    public void setDataTambahTugasMataPelajaran(String d){
        this.data_tambahtugas_matapelajaran=d;
    }
    public String getDataTambahTugasMataPelajaran(){
        return this.data_tambahtugas_matapelajaran;
    }

    public void setDataTambahTugasDibuat(String d){
        this.data_tambahtugas_dibuat=d;
    }
    public String getDataTambahTugasDibuat(){
        return this.data_tambahtugas_dibuat;
    }

    public void setDataTambahTugasWaktu(String d){
        this.data_tambahtugas_waktu=d;
    }
    public String getDataTambahTugasWaktu(){
        return this.data_tambahtugas_waktu;
    }

    public void setDataTambahTugasCerita(String d){
        this.data_tambahtugas_cerita=d;
    }
    public String getDataTambahTugasCerita(){
        return this.data_tambahtugas_cerita;
    }

    public void setDataTambahTugasPrioritas(String d){
        this.data_tambahtugas_prioritas=d;
    }
    public String getDataTambahTugasPrioritas(){
        return this.data_tambahtugas_prioritas;
    }



    public void setDataAnggotaId(String d){
        this.data_anggotaid=d;
    }
    public String getDataAnggotaId(){
        return this.data_anggotaid;
    }

    public void setDataAnggotaNamaLengkap(String d){
        this.data_anggotanamalengkap=d;
    }
    public String getDataAnggotaNamaLengkap(){
        return this.data_anggotanamalengkap;
    }

    public void setDataAnggotaNama(String d){
        this.data_anggotanama=d;
    }
    public String getDataAnggotaNama(){
        return this.data_anggotanama;
    }

    public void setDataAnggotaEmail(String d){
        this.data_anggotaemail=d;
    }
    public String getDataAnggotaEmail(){
        return this.data_anggotaemail;
    }

    public void setDataAnggotaStatus(String d){
        this.data_anggotastatus=d;
    }
    public String getDataAnggotaStatus(){
        return this.data_anggotastatus;
    }

    public void setDataAnggotaAvatar(String d){
        this.data_anggotaavatar=d;
    }
    public String getDataAnggotaAvatar(){
        return this.data_anggotaavatar;
    }


    public void setDataScanAddId(String d){
        this.data_scanaddid=d;
    }
    public String getDataScanAddId(){
        return this.data_scanaddid;
    }

    public void setDataScanAddNamaLengkap(String d){
        this.data_scanaddnamalengkap=d;
    }
    public String getDataScanAddNamaLengkap(){
        return this.data_scanaddnamalengkap;
    }

    public void setDataScanAddNama(String d){
        this.data_scanaddnama=d;
    }
    public String getDataScanAddaNama(){
        return this.data_scanaddnama;
    }

    public void setDataScanAddEmail(String d){
        this.data_scanaddemail=d;
    }
    public String getDataScanAddEmail(){
        return this.data_scanaddemail;
    }

    public void setDataScanAddAvatar(String d){
        this.data_scanaddavatar=d;
    }
    public String getDataScanAddAvatar(){
        return this.data_scanaddavatar;
    }



    public void setDataScanAbsentId(String d){
        this.data_scanabsentid=d;
    }
    public String getDataScanAbsentId(){
        return this.data_scanabsentid;
    }

    public void setDataScanAbsentNama(String d){
        this.data_scanabsentnama=d;
    }
    public String getDataScanAbsentNama(){
        return this.data_scanabsentnama;
    }

    public void setDataScanAbsentCerita(String d){
        this.data_scanabsentcerita=d;
    }
    public String getDataScanAbsentCerita(){
        return this.data_scanabsentcerita;
    }

    public void setDataScanAbsentAvatar(String d){
        this.data_scanabsentavatar=d;
    }
    public String getDataScanAbsentAvatar(){
        return this.data_scanabsentavatar;
    }

    public void setDataScanAbsentTotal(String d){
        this.data_scanabsenttotal=d;
    }
    public String getDataScanAbsentTotal(){
        return this.data_scanabsenttotal;
    }

    public void setDataScanAbsentCheckIn(String d){
        this.data_scanabsentcheckin=d;
    }
    public String getDataScanAbsentCheckIn(){
        return this.data_scanabsentcheckin;
    }


    public void setDataInfo(String d){
        this.data_info=d;
    }
    public String getDataInfo(){
        return this.data_info;
    }

    public void notificationNewJob(Context cntx, String CHANNEL_ID, String contentTitle, String contentSubject, String contentSnippet) {
        Intent intent = new Intent(cntx, SplashActivity.class);
        // Create an explicit intent for an Activity in your app
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(cntx, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(cntx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_school_blue_24dp)
                .setContentTitle(contentTitle)
                .setContentText(contentSubject)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentSnippet))
                .setLargeIcon(BitmapFactory.decodeResource(cntx.getResources(),
                        R.drawable.tugaskita_icon))
                .setSound(Uri.parse("android.resource://"+cntx.getPackageName()+"/"+R.raw.notification))
                .setColor(ContextCompat.getColor(cntx, R.color.blue))
                .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Random rand = new Random();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(cntx);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(rand.nextInt(100), builder.build());
    }

    public void notificationInvitation(Context cntx, String CHANNEL_ID, String contentTitle, String contentSubject) {
        Intent intent = new Intent(cntx, SplashActivity.class);
        // Create an explicit intent for an Activity in your app
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(cntx, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(cntx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_school_blue_24dp)
                .setContentTitle(contentTitle)
                .setContentText(contentSubject)
                .setLargeIcon(BitmapFactory.decodeResource(cntx.getResources(),
                        R.drawable.tugaskita_icon))
                .setSound(Uri.parse("android.resource://"+cntx.getPackageName()+"/"+R.raw.notification))
                .setColor(ContextCompat.getColor(cntx, R.color.blue))
                .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Random rand = new Random();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(cntx);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(rand.nextInt(100), builder.build());
    }

    public void notificationJob(final Context cntx, String CHANNEL_ID, String contentTitle, String contentText, int drawableID) {
        Intent intent = new Intent(cntx, SplashActivity.class);
        // Create an explicit intent for an Activity in your app
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(cntx, 0, intent, 0);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(cntx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_school_blue_24dp)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentText))
                .setLargeIcon(BitmapFactory.decodeResource(cntx.getResources(),
                        drawableID))
                .setSound(Uri.parse("android.resource://"+cntx.getPackageName()+"/"+R.raw.notification))
                .setColor(ContextCompat.getColor(cntx, R.color.blue))
                .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Random rand = new Random();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(cntx);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(rand.nextInt(100), builder.build());
    }


    public void notificationAnnoucement(final Context cntx, String CHANNEL_ID, String contentTitle, String contentText) {
        Intent intent = new Intent(cntx, SplashActivity.class);
        // Create an explicit intent for an Activity in your app
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(cntx, 0, intent, 0);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(cntx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_school_blue_24dp)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentText))
                .setLargeIcon(BitmapFactory.decodeResource(cntx.getResources(),
                        R.drawable.tugaskita_icon))
                .setSound(Uri.parse("android.resource://"+cntx.getPackageName()+"/"+R.raw.notification))
                .setColor(ContextCompat.getColor(cntx, R.color.blue))
                .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(cntx);
        Random rand = new Random();

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(rand.nextInt(100), builder.build());
    }



    public void openDialogError(Context cntx, String errorString) {
        DialogError dialogError = new DialogError(errorString);
        dialogError.show(getSupportFragmentManager(), "Error dialog");
    }

    public int getDefaultProfilePicture(String keyword) {
        int result = R.drawable.user;
        if (keyword.equals("boy"))
            result = R.drawable.boy;
        else if (keyword.equals("boy_1"))
            result = R.drawable.boy_1;
        else if (keyword.equals("girl"))
            result = R.drawable.girl;
        else if (keyword.equals("girl_1"))
            result = R.drawable.girl_1;
        else if (keyword.equals("man"))
            result = R.drawable.man;
        else if (keyword.equals("man_1"))
            result = R.drawable.man_1;
        else if (keyword.equals("man_2"))
            result = R.drawable.man_2;
        else if (keyword.equals("man_3"))
            result = R.drawable.man_3;
        else if (keyword.equals("man_4"))
            result = R.drawable.man_4;
        return result;
    }



        public static synchronized Global getInstance(){
        if(instance==null){
            instance=new Global();
        }
        return instance;
    }

}
