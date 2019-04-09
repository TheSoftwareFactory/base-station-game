package com.example.base_station_game;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.app.Activity.RESULT_OK;


public class DatabaseService extends IntentService {

    private final IBinder binder = new LocalBinder();

    private DatabaseReference mDatabase;
    private ArrayList stations;
    private static final int RC_SIGN_IN = 123;
    public User user;

    public DatabaseService() {
        super("DatabaseService");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        stations = new ArrayList();
        update_stations();

    }

    public class LocalBinder extends Binder {
        DatabaseService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DatabaseService.this;
        }
    }

    public ArrayList getStations() {
        return stations;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        user = (User) intent.getSerializableExtra("user");

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "ServiceActivity starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "ServiceActivity done", Toast.LENGTH_SHORT).show();
    }

    private void sendStation(BaseStation station, boolean delete) {
        Intent intent = new Intent("my-integer");
        intent.putExtra("station", station);
        intent.putExtra("delete", delete);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void update_stations() {
        mDatabase.child("stations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String prevChildKey) {
                try {
                    Log.d("Logging datasnapshot", ds.toString());
                    String name = null;
                    Double longitude = null;
                    Double latitude = null;
                    String winnerTeam = null;

                    HashMap value= (HashMap) ds.getValue();
                    name = (String) value.get("name");
                    longitude = (Double) value.get("longitude");
                    latitude =  (Double) value.get("latitude");
                    if(value.containsKey("Teams")) {
                        HashMap Teams = (HashMap) value.get("Teams");
                        if(Teams.containsKey("winnerTeam")) {
                            winnerTeam = (String) Teams.get("winnerTeam");
                        }
                    }
                    BaseStation station = new BaseStation(name, latitude, longitude, winnerTeam);
                    station.setId(ds.getKey());
                    stations.add(station);                                 //add station object to list of station
                    sendStation(station, false);

                    Log.d("EXTRACTED", value.toString() + " ---> " + name + " " + longitude + " " + latitude);
                } catch (Exception e) {
                    Log.e("DATABASE SERVICE", "update_stations -> onChildAdded : probably some shit in the database" + e.toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot ds) {
                BaseStation station = ds.getValue(BaseStation.class);  //get station object
                stations.remove(station);                                //remove station object to list of station
                sendStation(station, true);
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
