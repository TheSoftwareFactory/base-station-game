package com.example.base_station_game;

public class User {

    private String username;
    private String email;
    private String uid;
    private int level;
    private long exp;
    private int team;
    //private String password;

    public User(){

    }
    public User(String uid,String email,String username,int level,long exp, int team){
        this.username=username;
        this.uid=uid;
        this.email=email;
        this.level=level;
        this.exp=exp;
        this.team=team;

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

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }
}
