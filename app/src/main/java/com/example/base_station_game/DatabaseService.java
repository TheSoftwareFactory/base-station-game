package com.example.base_station_game;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
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
import java.util.List;
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

        /*List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);*/

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

    @Override
    public void onDestroy() {
        Toast.makeText(this, "ServiceActivity done", Toast.LENGTH_SHORT).show();
    }

    private void sendStation(BaseStation station,boolean delete) {
        Intent intent = new Intent("my-integer");
        intent.putExtra("station",station);
        intent.putExtra("delete",delete);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    
    public void update_stations() {
        mDatabase.child("stations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String prevChildKey) {
                BaseStation station = ds.getValue(BaseStation.class);  //get station object
                stations.add(station);                                 //add station object to list of station
                sendStation(station,false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot ds) {
                BaseStation station = ds.getValue(BaseStation.class);  //get station object
                stations.remove(station);                                //remove station object to list of station
                sendStation(station,true);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    /*
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                //load_or_create_user(firebaseUser);
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }*/
}
