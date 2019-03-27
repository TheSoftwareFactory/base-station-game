package com.example.base_station_game;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ServiceActivity extends AppCompatActivity {

    DatabaseService mService;
    boolean mBound = false;
    private TextView text;
    private DatabaseReference mDatabase;
    private TextView t_id;
    private TextView t_name;
    private TextView t_latitude;
    private TextView t_longitude;
    private TextView t_output;
    private ArrayList stations;
    private static final int RC_SIGN_IN = 123;
    public User user;
    public FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        text = findViewById(R.id.text);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        writeNewBaseStationToDatabase(666,"aids",40.5,42);
    }

    // Defines callbacks for ServiceActivity binding, passed to bindService()
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            DatabaseService.LocalBinder binder = (DatabaseService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    // Handling the received Intents for the "my-integer" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            BaseStation station = (BaseStation) intent.getSerializableExtra("station");
            text.setText("message gekriegt");
            if (station != null){
                text.setText(station.toString());
            }
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        // This registers mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver,
                        new IntentFilter("my-integer"));

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, DatabaseService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void click(View v) {

        //Toast.makeText(this, "number", Toast.LENGTH_SHORT).show();

        ArrayList a=mService.getStations();
        text.setText(a.toString());

    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    private void writeNewBaseStationToDatabase(int StationId, String name, double latitude, double longitude) {
        BaseStation station = new BaseStation(StationId,name,latitude,longitude, 5);   //create station
        mDatabase.child("stations").child(Integer.toString(StationId)).setValue(station);   //attach station to database
    }


}
