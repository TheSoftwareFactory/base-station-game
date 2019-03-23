package com.example.base_station_game;


public class BaseStation extends Object{
    private String name;
    private int id;
    private Double latitude;
    private Double longitude;

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
        if (((BaseStation) o).getID() ==this.id && ((BaseStation) o).getName() ==this.name && ((BaseStation) o).getLatitude() ==this.latitude && ((BaseStation) o).getLongitude() ==this.longitude){
            return true;
        }
        else
        {
            return false;
        }

    }
}
