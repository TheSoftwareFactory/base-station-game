package com.example.base_station_game;


public class BaseStation {
    public String name;
    public String StationId;
    public Double latitude;
    public Double longitude;

    public BaseStation(String StationId, String name, double latitude, double longitude) {
        this.name = name;
        this.StationId = StationId;
        this.latitude = latitude;
        this.longitude= longitude;
    }

    public BaseStation() {

    }

    @Override
    public String toString(){
        return ("This is Station " + name + "with the ID: +"StationId+ "at the coordinates: Latitude: +"+latitude+ "and Longitude: "+ longitude);
    }
}
