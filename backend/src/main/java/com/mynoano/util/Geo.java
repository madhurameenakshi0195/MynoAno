package com.mynoano.util;

public final class Geo {
    private Geo() {}

    public static double meters(double lat1, double lng1, double lat2, double lng2) {
        double r = 6371000, p1 = Math.toRadians(lat1), p2 = Math.toRadians(lat2);
        double dp = Math.toRadians(lat2 - lat1), dl = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dp / 2) * Math.sin(dp / 2) + Math.cos(p1) * Math.cos(p2) * Math.sin(dl / 2) * Math.sin(dl / 2);
        return 2 * r * Math.asin(Math.sqrt(a));
    }

    public static double miles(double lat1, double lng1, double lat2, double lng2) {
        return meters(lat1, lng1, lat2, lng2) / 1609.344;
    }
}
