package com.example.base_station_game;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private TextView t1;
    private TextView t2;
    private TextView t3;
    private TextView t4;
    private TextView t5;
    private Button b;


    //FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engine_);

        t1= findViewById(R.id.id);
        t2 = findViewById(R.id.name);
        t3 = findViewById(R.id.latitude);
        t4 = findViewById(R.id.longitude);
        b = findViewById(R.id.add_station);
        t5 = findViewById(R.id.value);


        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //reset database
        //mDatabase.setValue("");



        //writeNewBaseStation("1","user1", 60.6,50.5);
        //writeNewBaseStation("2","user2",60.6,50.5);
        //writeNewBaseStation("3","user3",60.6,50.5);
        //t.setText(mDatabase.child("users").child("111").getKey());

       /* mDatabase.child("stations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                String id = ds.getKey();
                BaseStation station = ds.getValue(BaseStation.class);
                t5.append(station.toString());
            }
                //BaseStation station = dataSnapshot.getValue(BaseStation.class);
                //t.setText(station.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("error message:","The read failed: " + databaseError.getCode());
            }
        });
*/
       update_stations();
    }

    public void update_stations(){
        mDatabase.child("stations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                t5.setText("");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String id = ds.getKey();
                    BaseStation station = ds.getValue(BaseStation.class);
                    t5.append(station.toString());
                    t5.append("\n");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("error message:","The read failed: " + databaseError.getCode());
            }
        });

    }

    private void writeNewBaseStation(int StationId, String name, double latitude, double longitude) {
        BaseStation station = new BaseStation(StationId,name,latitude,longitude);
        mDatabase.child("stations").child(Integer.toString(StationId)).setValue(station);
    }

    public void add_station(View view) {
         //Integer id=Integer.valueOf(t1.getText());
         int id= Integer.valueOf(t1.getText().toString());
         String name= t2.getText().toString();
         double latitude= Double.parseDouble(t3.getText().toString());
         double longitude= Double.parseDouble(t4.getText().toString());
         writeNewBaseStation(id,name,latitude,longitude);

    }


}