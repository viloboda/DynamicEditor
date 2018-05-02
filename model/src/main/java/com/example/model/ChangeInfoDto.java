package com.example.model;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChangeInfoDto {
    @SerializedName("date")
    public Long ChangeDate;

    @SerializedName("geo_location")
    public GeoLocationDto GeoLocation;

    @Override
    public String toString() {
        double lat = GeoLocation != null ? GeoLocation.getLat() : 0;
        double lon = GeoLocation != null ? GeoLocation.getLon() : 0;

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        return "ChangeDate: " + (ChangeDate != null ? sdf.format(new Date(ChangeDate)): "not set") + ", lat=" + lat + ", lon = " + lon;
    }
}
