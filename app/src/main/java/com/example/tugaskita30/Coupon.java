package com.example.tugaskita30;

public class Coupon {
    String header, point, couponid;
    int thumbnail;
    public Coupon(String header, String point, String couponid) {
        if (point.equals("3")) {
            this.thumbnail = R.drawable.plus_3;
        } else if (point.equals("5")) {
            this.thumbnail = R.drawable.plus_5;
        } else if (point.equals("9")) {
            this.thumbnail = R.drawable.plus_9;
        }

        this.header = header;
        this.point = point;
        this.couponid = couponid;
    }

    public int getThumbnail() {
        return this.thumbnail;
    }

    public String getHeader() { return this.header; }

    public String getPoint() { return this.point; }

    public String getCouponId() { return this.couponid; }
}
