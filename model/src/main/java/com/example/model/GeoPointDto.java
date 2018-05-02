package com.example.model;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public class GeoPointDto {

    @SerializedName("lon")
    private double lon;

    @SerializedName("lat")
    private double lat;

    public GeoPointDto(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    // X координата
    public double getLon() {
        return lon;
    }

    // Y координата
    public double getLat() {
        return lat;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Lat = %s, Lon = %s",
                Double.toString(getLat()),
                Double.toString(getLon()));
    }
}
