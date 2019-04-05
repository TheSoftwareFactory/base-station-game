package com.example.base_station_game;
import java.io.Serializable;

public class BaseStation implements Serializable {
    private String name;
    private String id;
    private Double latitude;
    private Double longitude;

    public BaseStation(String name, Double latitude, Double longitude) {

        this.name = name;
        this.latitude = latitude;
        this.longitude= longitude;
    }

    public BaseStation() {

    }

    @Override
    public String toString(){
        return (" This is Station '" + name + "' with the ID: " +id+ " at the coordinates: Latitude: +"+latitude+ " and Longitude: "+ longitude);
    }


    @Override
    public boolean equals(Object o) {
        BaseStation station= (BaseStation) o;
        if (station.getID().equals(this.id))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public String getName(){
        return this.name;
    }
    public String getID(){
        return this.id;
    }
    public double getLatitude(){
        return this.latitude;
    }
    public double getLongitude(){
        return this.longitude;
    }
    public void setId(String id){
        this.id=id;
    }
}