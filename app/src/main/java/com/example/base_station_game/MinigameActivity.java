package com.example.base_station_game;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.base_station_game.sampling.Sampler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class OutOfRangeException extends Exception {
    public OutOfRangeException(String message) {
        super(message);
    }
}

public class MinigameActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private TextView textView;
    private Handler handler = new Handler();
    private User user;
    private BaseStation station;
    private DatabaseReference mDatabase;
    private long score =0;

    private LocationManager locationManager;
    private LocationListener listener;
    private GeoPoint actualPosition = null;

    // It shouldn't be public static
    public static Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame);
        user = (User) getIntent().getSerializableExtra("user");
        station = (BaseStation) getIntent().getSerializableExtra("station");
        actualPosition = new GeoPoint(station.getLatitude(), station.getLongitude());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        data = new Intent();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);
        Context context = this;
        // Start long running operation in a background thread
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            public void onLocationChanged(Location location) {
                actualPosition.setLatitude(location.getLatitude());
                actualPosition.setLongitude(location.getLongitude());
            }

            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            public void onProviderEnabled(String s) {

            }

            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        locationManager.requestLocationUpdates("gps", 2000, 0, listener);

        new Thread(new Runnable() {
            public void run() {
                try {
                    if (Sampler.sample(context))
                        System.out.println("sample inserted into the database.");
                    while (progressStatus < 100) {
                        progressStatus += 1;
                        // Update the progress bar and display the
                        //current value in the text view
                        handler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(progressStatus);
                                textView.setText(progressStatus + "/" + progressBar.getMax());

                                float[] dist = new float[1];
                                Location.distanceBetween(actualPosition.getLatitude(), actualPosition.getLongitude(), station.getLatitude(), station.getLongitude(), dist);
                                if (dist[0] > SecondActivity.MAX_DISTANCE) {
                                    try {
                                        throw new OutOfRangeException("Distance " + dist[0]);
                                    } catch (OutOfRangeException e) {
                                        e.printStackTrace();
                                        setResult(Activity.RESULT_CANCELED, data);
                                        finish();
                                    }
                                }

                            }
                        });

                        // Sleep for 100 milliseconds.

                        // Check if the player is this in the Range
                        // In case throw an exception

                        Thread.sleep(100);

                    }
                } catch (InterruptedException e) {
                    // Something went WRONG
                    e.printStackTrace();
                    setResult(Activity.RESULT_CANCELED, data);
                    finish();
                }
                // Everything went GOOD

                //Long score = Long.valueOf(ThreadLocalRandom.current().nextLong(10, 1000));
                data.putExtra("score", score);
                conquered(station, score);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        }).start();

    }

    // function for minigame activity: pushes score to base station tag in database
    public void conquered(BaseStation station, Long score) {
        try {
            DatabaseReference ref = mDatabase.
                    child("Users").
                    child(user.getUID()).
                    child("PlayedStations");

            ref.addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("PRINT",dataSnapshot.toString());

                    if (!dataSnapshot.hasChild(station.getID()) || (Long) dataSnapshot.child(station.getID()).getValue() < score) {
                            //Update stations/teams/user.getTeam/ -> append user.getUID(),(score)
                            mDatabase.child("stations").
                                    child(station.getID()).
                                    child("Teams").
                                    child(user.getTeam()).
                                    child("Players").
                                    child(user.getUID()).
                                    setValue(score);

                            // Update users/users.UID/conqueredstations -> append station.getID() (score)
                            mDatabase.child("Users").
                                    child(user.getUID()).
                                    child("PlayedStations").
                                    child(station.getID()).
                                    setValue(score);
                    }
                    ref.removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    setResult(Activity.RESULT_CANCELED, data);
                    finish();
                }
            });

        } catch (Exception e) {
            Log.e("IN CONQUERED", "Probably some shit in the database" + e.toString());
            setResult(Activity.RESULT_CANCELED, this.data);
            finish();
        }
    }

    public void onClick(View v) {
            score++;
    }

    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates("gps", 2000, 0, listener);
    }

    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(listener);

    }
}