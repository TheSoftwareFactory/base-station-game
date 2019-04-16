package com.example.base_station_game;
import java.io.Serializable;

public class BaseStation implements Serializable {
    private String name;
    private String id;
    private Double latitude;
    private Double longitude;
    private String winningTeam;

    public BaseStation(String name, Double latitude, Double longitude, String winningTeam) {
        this.name = name;
        this.latitude = latitude;
        this.longitude= longitude;
        this.winningTeam = winningTeam;
    }

    public BaseStation() {

    }

    @Override
    public String toString(){
        if(winningTeam != null) {
            return (" This is Station '" + name + "' with the ID: " + id + " at the coordinates: Latitude: +" + latitude + " and Longitude: " + longitude + "winningTeam->" +winningTeam);
        }
        else{
            return (" This is Station '" + name + "' with the ID: " + id + " at the coordinates: Latitude: +" + latitude + " and Longitude: " + longitude);
        }
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
    public void setWinningTeam(String winningTeam){  this.winningTeam = winningTeam;  }
    public String getWinningTeam() { return this.winningTeam; }
}