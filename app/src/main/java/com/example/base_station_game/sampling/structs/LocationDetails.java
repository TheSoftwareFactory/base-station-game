package com.example.base_station_game.sampling.structs;

import android.location.Location;

import java.io.Serializable;

public class LocationDetails implements Serializable {
    private double longitude;
    private double latitude;

    public LocationDetails(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    @Override
    public String toString() {
        return longitude + ", " + latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
