package com.example.tugaskita30;

public class Grup {
    String imageUrl, id, nama, deksripsi, statusInGrup, jumlahTugas, jumlahAnggota;

    public Grup(String imageUrl, String id, String nama, String deskripsi, String statusInGrup, String jumlahTugas, String jumlahAnggota) {
        this.imageUrl = imageUrl;
        this.id = id;
        this.nama = nama;
        this.deksripsi = deskripsi;
        this.statusInGrup = statusInGrup;
        this.jumlahTugas = jumlahTugas;
        this.jumlahAnggota = jumlahAnggota;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getId() {
        return this.id;
    }

    public  String getNama() { return this.nama; }

    public  String getDeksripsi() { return this.deksripsi; }

    public  String getStatusInGrup() { return this.statusInGrup; }

    public  String getJumlahTugas() { return this.jumlahTugas; }

    public  String getJumlahAnggota() { return this.jumlahAnggota; }

    public String format(String text, int length) {
        String result = "";
        for (int i = 0;i < text.length(); i++){
            if (i+1 <= length) {
                result += text.charAt(i);
            } else {
                result += "...";
                break;
            }
        }
        return result;
    }
}
