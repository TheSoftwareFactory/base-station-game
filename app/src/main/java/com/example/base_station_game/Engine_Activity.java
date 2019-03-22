package com.example.base_station_game;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.example.base_station_game.BaseStation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
//import com.google.firebase

public class Engine_Activity extends AppCompatActivity {


    private DatabaseReference mDatabase;
    private TextView t;

    //FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engine_);

        t = (TextView) findViewById(R.id.value);


        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //reset database
        mDatabase.setValue("");



        writeNewBaseStation("1","user1","mail1");
        writeNewBaseStation("2","user2","mail2");
        writeNewBaseStation("3","user3","mail3");
        //t.setText(mDatabase.child("users").child("111").getKey());

        mDatabase.child("stations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                String key = ds.getKey();
                BaseStation value = ds.getValue(BaseStation.class);
                Log.d("TAG", key + value.toString());
            }
                //BaseStation station = dataSnapshot.getValue(BaseStation.class);
                //t.setText(station.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("error message:","The read failed: " + databaseError.getCode());
            }
        });

    }

    private void writeNewBaseStation(String StationId, String name, String conquerer) {
        BaseStation station = new BaseStation(StationId,name,conquerer);
        mDatabase.child("stations").child(StationId).setValue(station);
    }


}