package com.example.base_station_game;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String username;
    private String email;
    private String uid;
    private int level;
    private long exp;
    private String team;
    //private ArrayList conqueredStations;  //-> maybe useful later to display if stations are conquereable
    //private String password;

    public User(){

    }
    public User(String uid,String email,String username,int level,long exp, String team){
        this.username=username;
        this.uid=uid;
        this.email=email;
        this.level=level;
        this.exp=exp;
        this.team=team;
        //conqueredStations=new ArrayList();
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

    public void setUID(String uid) {this.uid=uid;}

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}
