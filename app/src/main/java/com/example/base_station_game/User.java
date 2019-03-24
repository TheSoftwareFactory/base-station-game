package com.example.base_station_game;

public class User {

    private String username;
    private String email;
    private String uid;
    private int level;
    //private String password;

    public User(){

    }
    public User(String uid,String email,String username,int level){
        this.username=username;
        this.uid=uid;
        this.email=email;
        this.level=level;

    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getUID() {
        return uid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
