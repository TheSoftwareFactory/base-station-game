package com.example.base_station_game;


public class BaseStation {
    private String name;
    private int id;
    private double latitude;
    private double longitude;

    public BaseStation(int id, String name, double latitude, double longitude) {
        this.name = name;
        this.id = id;
        this.latitude = latitude;
        this.longitude= longitude;
    }

    public BaseStation() {

    }

    @Override
    public String toString(){
        return (" This is Station '" + name + "' with the ID: " +id+ " at the coordinates: Latitude: +"+latitude+ " and Longitude: "+ longitude);
    }

    public String getName(){
        return this.name;
    }
    public int getID(){
        return this.id;
    }
    public double getLatitude(){
        return this.latitude;
    }
    public double getLongitude(){
        return this.longitude;
    }

    @Override
    public boolean equals(Object o) {
        BaseStation station= (BaseStation) o;
        if ( station.getID() == this.id && station.getName() == this.name && station.getLatitude() ==this.latitude && station.getLongitude() ==this.longitude){
            return true;
        }
        else
        {
            return false;
        }

    }
}