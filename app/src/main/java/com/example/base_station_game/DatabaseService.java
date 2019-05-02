package com.example.base_station_game;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;


public class DatabaseService extends Service {

    private final IBinder binder = new LocalBinder();

    private DatabaseReference mDatabase;
    private ArrayList stations;

    private LocationManager locationManager;
    private LocationListener listener;
    private GeoPoint actualPosition = new GeoPoint(0, 0);
    private GeoFire geoFire = null;

    public User user;

    public DatabaseService() {
        super();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        stations = new ArrayList();
        this.geoFire = new GeoFire(mDatabase.child("/GeoFireStations"));

        //update_stations();


    }

    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            public void onLocationChanged(Location location) {
                actualPosition = new GeoPoint(location);
                query_stations();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager.requestLocationUpdates("gps", 3000, 0, listener);


    }
    public class LocalBinder extends Binder {
        DatabaseService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DatabaseService.this;
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
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

    private void sendStation(BaseStation station, int OP_CODE) {
        // OP_CODE : 0 new ; 1 deleted; 2 changed
        Intent intent = new Intent("my-integer");
        intent.putExtra("station", station);
        intent.putExtra("OP_CODE", OP_CODE);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void query_stations() {
        // Querying for stations at most 1 kilometer far
        GeoQuery geoQuery = this.geoFire.queryAtLocation(new GeoLocation(actualPosition.getLatitude(), actualPosition.getLongitude()), 1);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                DatabaseReference ref = mDatabase
                        .child("stations")
                        .child(key);
                ref.addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot ds) {
                        try {
                            Log.d("GEOQUERY", String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                            String name = null;
                            Double longitude = null;
                            Double latitude = null;
                            String winnerTeam = null;

                            HashMap value = (HashMap) ds.getValue();
                            name = (String) value.get("name");
                            // Maybe add try/catch for  databasejava.lang.ClassCastException
                            longitude = (Double) value.get("longitude");
                            latitude = (Double) value.get("latitude");
                            if (value.containsKey("winnerTeam")) {
                                winnerTeam = (String) value.get("winnerTeam");
                            }
                            BaseStation station = new BaseStation(name, latitude, longitude, winnerTeam);
                            station.setId(ds.getKey());

                            stations.add(station);                                 //add station object to list of station
                            sendStation(station, 0);
                            addEventListenerForStation(key);
                        } catch (Exception e) {
                            Log.e("DATABASE SERVICE", "update_stations -> onChildAdded : probably some shit in the database" + e.toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                DatabaseReference ref = mDatabase
                        .child("stations")
                        .child(key);
                ref.addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot ds) {
                        BaseStation station = ds.getValue(BaseStation.class);  //get station object
                        stations.remove(station);                                //remove station object to list of station
                        sendStation(station, 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d("GEOQUERY", String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                Log.d("GEOQUERY", "All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d("GEOQUERY", "There was an error with this query: " + error);
            }
        });
    }

    public void addEventListenerForStation(String key){
        try {
            DatabaseReference ref = mDatabase
                    .child("stations")
                    .child(key);
            ref.addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot ds) {
                    try {

                        String name = null;
                        Double longitude = null;
                        Double latitude = null;
                        String winnerTeam = null;

                        HashMap value = (HashMap) ds.getValue();
                        name = (String) value.get("name");
                        longitude = (Double) value.get("longitude");
                        latitude = (Double) value.get("latitude");
                        if (value.containsKey("winnerTeam")) {
                            winnerTeam = (String) value.get("winnerTeam");
                        }
                        BaseStation station = new BaseStation(name, latitude, longitude, winnerTeam);
                        station.setId(ds.getKey());
                        stations.add(station);                                 //add station object to list of station
                        sendStation(station, 2);


                        Log.d("DATASNAPSHOT", ds.toString());
                    } catch (Exception e) {
                        Log.e("DATABASE SERVICE", "update_stations -> onChildAdded : probably some shit in the database" + e.toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e){
            BaseStation station = new BaseStation();
            station.setId(key);
            stations.remove(station);
            sendStation(station, 1);
        }
    }

    /*
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

                    HashMap value = (HashMap) ds.getValue();
                    name = (String) value.get("name");
                    // Maybe add try/catch for  databasejava.lang.ClassCastException
                    longitude = (Double) value.get("longitude");
                    latitude = (Double) value.get("latitude");
                    if (value.containsKey("winnerTeam")) {
                        winnerTeam = (String) value.get("winnerTeam");
                    }
                    BaseStation station = new BaseStation(name, latitude, longitude, winnerTeam);
                    station.setId(ds.getKey());
                    stations.add(station);                                 //add station object to list of station
                    sendStation(station, 0);

                    Log.d("EXTRACTED", value.toString() + " ---> " + name + " " + longitude + " " + latitude);
                } catch (Exception e) {
                    Log.e("DATABASE SERVICE", "update_stations -> onChildAdded : probably some shit in the database" + e.toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot ds, String prevChildKey) {
                try {
                    Log.d("Logging datasnapshot", ds.toString());
                    String name = null;
                    Double longitude = null;
                    Double latitude = null;
                    String winnerTeam = null;

                    HashMap value = (HashMap) ds.getValue();
                    name = (String) value.get("name");
                    longitude = (Double) value.get("longitude");
                    latitude = (Double) value.get("latitude");
                    if (value.containsKey("winnerTeam")) {
                        winnerTeam = (String) value.get("winnerTeam");
                    }
                    BaseStation station = new BaseStation(name, latitude, longitude, winnerTeam);
                    station.setId(ds.getKey());
                    stations.add(station);                                 //add station object to list of station
                    sendStation(station, 2);

                    Log.d("EXTRACTED", value.toString() + " ---> " + name + " " + longitude + " " + latitude);
                } catch (Exception e) {
                    Log.e("DATABASE SERVICE", "update_stations -> onChildChanged : probably some shit in the database" + e.toString());
                }
            }


            @Override
            public void onChildRemoved(DataSnapshot ds) {
                BaseStation station = ds.getValue(BaseStation.class);  //get station object
                stations.remove(station);                                //remove station object to list of station
                sendStation(station, 1);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
*/
}
