package com.example.base_station_game;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.example.base_station_game.BaseStation;

import java.util.ArrayList;


public class Engine_Activity extends AppCompatActivity {


    private DatabaseReference mDatabase;
    private TextView t_id;
    private TextView t_name;
    private TextView t_latitude;
    private TextView t_longitude;
    private TextView t_output;
    private ArrayList stations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engine_);

        t_id= findViewById(R.id.id);
        t_name = findViewById(R.id.name);
        t_latitude = findViewById(R.id.latitude);
        t_longitude = findViewById(R.id.longitude);
        t_output = findViewById(R.id.value);
        stations = new ArrayList();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //reset database:
        //mDatabase.setValue("");
        //manually interact with database:
        //writeNewBaseStationToDatabase(1,"user1", 60.6,50.5);
        //deleteBaseStationFromDatabase(1,"user1", 60.6,50.5);

       //get_stations();
       update_stations();
    }

    /*public void get_stations(){
        mDatabase.child("stations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                t5.setText("");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String id = ds.getKey();
                    BaseStation station = ds.getValue(BaseStation.class);
                    t5.append(station.toString()+"\n");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("error message:","The read failed: " + databaseError.getCode());
            }
        });

    }*/

    public void update_stations(){
        mDatabase.child("stations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String prevChildKey) {
                BaseStation station = ds.getValue(BaseStation.class);  //get station object
                stations.add(station);                                 //add station object to list of station
                t_output.setText(stations.toString());                       //update ui
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot ds) {
                BaseStation station = ds.getValue(BaseStation.class);    //get station object
                stations.remove(station);                                //remove station object to list of station
                t_output.setText(stations.toString());                         //update ui
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private void writeNewBaseStationToDatabase(int StationId, String name, double latitude, double longitude) {
        BaseStation station = new BaseStation(StationId,name,latitude,longitude);   //create station
        mDatabase.child("stations").child(Integer.toString(StationId)).setValue(station);   //attach station to database
    }

    private void deleteBaseStationFromDatabase(int StationId, String name, double latitude, double longitude) {
        BaseStation station = new BaseStation(StationId,name,latitude,longitude);   //create station
        mDatabase.child("stations").child(Integer.toString(station.getID())).removeValue();  //remove station to database
    }

    public void add_station(View view) {
        // collect data from ui
         int id= Integer.valueOf(t_id.getText().toString());
         String name= t_name.getText().toString();
         double latitude= Double.parseDouble(t_latitude.getText().toString());
         double longitude= Double.parseDouble(t_longitude.getText().toString());
        //send data to database
         writeNewBaseStationToDatabase(id,name,latitude,longitude);
    }


    public void delete_station(View view) {
        // collect data from ui
        int id= Integer.valueOf(t_id.getText().toString());
        String name= t_name.getText().toString();
        double latitude= Double.parseDouble(t_latitude.getText().toString());
        double longitude= Double.parseDouble(t_longitude.getText().toString());
        //send data to database
        deleteBaseStationFromDatabase(id,name,latitude,longitude);
    }

}