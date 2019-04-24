package com.example.base_station_game;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public User(String uid, String email, String username, String team) {
        this.username = username;
        this.uid = uid;
        this.email = email;
        this.level = 1;
        this.exp = 0;
        this.team = team;
        beUpdated();
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

    public void setUID(String uid) {
        this.uid = uid;
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

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public void beUpdated() {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(this.getUID()).addChildEventListener(this.getChildEventLister());
    }

    public ChildEventListener getChildEventLister(){
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot ds, String prevChildKey) {
                Log.e("DS", ds.toString());
                if(ds.getKey().equals("level")){
                    level = ((Long) ds.getValue()).intValue();
                }
                if(ds.getKey().equals("exp")){
                    exp = (long) ds.getValue();
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
}
