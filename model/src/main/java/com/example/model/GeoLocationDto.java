package com.example.model;

import com.google.gson.annotations.SerializedName;

public class GeoLocationDto extends GeoPointDto {
    @SerializedName("accuracy")
    private float accuracy;

    public GeoLocationDto(double lon, double lat, float accuracy) {
        super(lon, lat);
        this.accuracy = accuracy;
    }

    public float getAccuracy() {
        return accuracy;
    }

    @Override
    public String toString() {
        return "YouGeoLocation{" +
                "lon=" + getLon() +
                "lat=" + getLat() +
                '}';
    }
}
