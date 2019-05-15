package com.example.base_station_game;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.base_station_game.sampling.Sampler;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;

import java.io.IOException;

public class AR_Activity extends AppCompatActivity {

    private ArchitectView architectView;

    private static final String TAG = AR_Activity.class.getSimpleName();

    /** Root directory of the sample AR-Experiences in the assets dir. */
    private static final String SAMPLES_ROOT = "minigame/";

    /** The path to the AR-Experience. This is usually the path to its index.html. */
    private String arExperience = "index.html";

    private String apiKey = BuildConfig.ApiKey;

    private int MY_PERMISSIONS_REQUEST_CAMERA_CONTACTS = 42;
    private FusedLocationProviderClient fusedLocationClient;
    private Handler handler = new Handler();
    private Intent data = new Intent();
    private DatabaseReference mDatabase;
    private User user;
    private BaseStation station;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = (User) getIntent().getSerializableExtra("user");
        station = (BaseStation) getIntent().getSerializableExtra("station");

        this.architectView = (ArchitectView) this.findViewById(R.id.architectView);
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        config.setLicenseKey(apiKey);
        this.architectView.onCreate(config);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        architectView.onPostCreate();

        try {
            Log.d(TAG, "Loading : " + SAMPLES_ROOT + arExperience);
            /*
             * Loads the AR-Experience, it may be a relative path from assets,
             * an absolute path (file://) or a server url.
             *
             * To get notified once the AR-Experience is fully loaded,
             * an ArchitectWorldLoadedListener can be registered.
             */
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 42);
            } else {
                architectView.load(SAMPLES_ROOT + arExperience);
            }
        } catch (IOException e) {
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Exception while loading arExperience " + arExperience + ".", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 42: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    try {
                        architectView.load(SAMPLES_ROOT + arExperience);
                    } catch (IOException e) {
                        Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, " onRequestPermissionsResult -> Exception while loading arExperience " + arExperience + ".", e);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        architectView.onResume(); // Mandatory ArchitectView lifecycle call
    }

    @Override
    protected void onPause() {
        super.onPause();
        architectView.onPause(); // Mandatory ArchitectView lifecycle call
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*
         * Deletes all cached files of this instance of the ArchitectView.
         * This guarantees that internal storage for this instance of the ArchitectView
         * is cleaned and app-memory does not grow each session.
         *
         * This should be called before architectView.onDestroy
         */
        architectView.clearCache();
        architectView.onDestroy(); // Mandatory ArchitectView lifecycle call
    }

    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                if (ActivityCompat.checkSelfPermission(AR_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AR_Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationClient.requestLocationUpdates(locationRequest,
                        new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                if (locationResult == null) {
                                    return;
                                }
                                for (Location location : locationResult.getLocations()) {
                                    Log.d("FUSEDLOCATION", location.toString());
                                    // TODO SEND This location to the architectView
                                }
                            }

                            ;
                        },
                        null /* Looper */);
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure( Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    /*try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        //ResolvableApiException resolvable = (ResolvableApiException) e;
                        //resolvable.startResolutionForResult(AR_Activity.this , REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }*/
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            float accuracy = location.hasAccuracy() ? location.getAccuracy() : 1000;
                            architectView.setLocation(location.getLatitude(), location.getLongitude(), 0, accuracy);
                        }
                    }
                });
        createLocationRequest();

        new Thread(new Runnable() {
            public void run() {
                try {
                    int progressStatus = 0;
                    if (Sampler.sample(AR_Activity.this))
                        System.out.println("sample inserted into the database.");
                    while (progressStatus < 100) {
                        progressStatus += 1;
                        // Update the progress bar and display the
                        //current value in the text view
                        handler.post(new Runnable() {
                            public void run() {

                            }
                        });

                        // Sleep for 100 milliseconds.

                        // Check if the player is this in the Range
                        // In case throw an exception

                        Thread.sleep(150);

                    }
                } catch (InterruptedException e) {
                    // Something went WRONG
                    e.printStackTrace();
                    setResult(Activity.RESULT_CANCELED, data);
                    finish();
                }
                // Everything went GOOD
                Long score = new Long(500);
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
}
