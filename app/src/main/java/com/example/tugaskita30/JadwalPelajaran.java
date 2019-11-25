package com.example.tugaskita30;

public class JadwalPelajaran {
    String day, data;
    int image;

    public JadwalPelajaran(String day, String data) {
        if (day.equals("monday")) {
            this.image = R.drawable.monday;
        } else if (day.equals("tuesday")) {
            this.image = R.drawable.tuesday;
        } else if (day.equals("wednesday")) {
            this.image = R.drawable.wednesday;
        } else if (day.equals("thursday")) {
            this.image = R.drawable.thursday;
        } else if (day.equals("friday")) {
            this.image = R.drawable.friday;
        } else if (day.equals("saturday")) {
            this.image = R.drawable.saturday;
        }

        this.day = day;
        this.data = data;
    }

    public String getDay() {
        return this.day;
    }

    public int getImage() {
        return this.image;
    }

    public String getData() { return this.data; }
}
