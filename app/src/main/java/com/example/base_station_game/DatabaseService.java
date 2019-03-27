package com.example.base_station_game;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;


public class DatabaseService extends IntentService {

    private final IBinder binder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    private DatabaseReference mDatabase;
    private ArrayList stations;

    public DatabaseService() {
        super("DatabaseService2");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        stations = new ArrayList();
        update_stations();
        sendMessage();
    }

    public class LocalBinder extends Binder {
        DatabaseService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DatabaseService.this;
        }
    }

    public ArrayList getStations(){
        return stations;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "ServiceActivity starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /** method for clients */
    public int getRandomNumber() {
        int a= mGenerator.nextInt(100);
        return a;
    }


    @Override
    public void onDestroy() {
        Toast.makeText(this, "ServiceActivity done", Toast.LENGTH_SHORT).show();
    }


    private void sendMessage() {
        // The string "my-integer" will be used to filer the intent
        Intent intent = new Intent("my-integer");
        // Adding some data

        intent.putExtra("message", 5);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    private void sendMessage(BaseStation station) {
        // The string "my-integer" will be used to filer the intent
        Intent intent = new Intent("my-integer");
        intent.putExtra("station",station);
        // Adding some data

        intent.putExtra("message", 5);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    
    public void update_stations() {
        mDatabase.child("stations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String prevChildKey) {
                BaseStation station = ds.getValue(BaseStation.class);  //get station object
                stations.add(station);                                 //add station object to list of station
                //t_output.setText(stations.toString());                       //update ui
                sendMessage(station);
                //broadcast to activity

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot ds) {
                BaseStation station = ds.getValue(BaseStation.class);    //get station object
                stations.remove(station);                                //remove station object to list of station
                //t_output.setText(stations.toString());                         //update ui
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}
