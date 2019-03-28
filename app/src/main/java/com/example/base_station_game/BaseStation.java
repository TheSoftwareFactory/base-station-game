package com.example.base_station_game;


<<<<<<< HEAD
import java.io.Serializable;

public class BaseStation implements Serializable {
    private String name;
    private int id;
    private Double latitude;
    private Double longitude;
    private boolean active;
    private int timeToLive;

    public BaseStation(int id, String name, double latitude, double longitude, int timeToLive) {
=======
public class BaseStation {
    private String name;
    private int id;
    private double latitude;
    private double longitude;

    public BaseStation(int id, String name, double latitude, double longitude) {
>>>>>>> b0fe9177318480707c3d8d34c297f46c0dbb73b9
        this.name = name;
        this.id = id;
        this.latitude = latitude;
        this.longitude= longitude;
        this.active=true;
        this.timeToLive=timeToLive;
    }

    public BaseStation() {

    }

    @Override
    public String toString(){
        return (" This is Station '" + name + "' with the ID: " +id+ " at the coordinates: Latitude: +"+latitude+ " and Longitude: "+ longitude);
<<<<<<< HEAD
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
        if ((station.getID() == this.id) && (station.getName() == this.name) && (station.getLatitude() == this.latitude) && (station.getLongitude() == this.longitude))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
=======
>>>>>>> b0fe9177318480707c3d8d34c297f46c0dbb73b9
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