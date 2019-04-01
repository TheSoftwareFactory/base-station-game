package com.example.base_station_game;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Engine_Activity extends AppCompatActivity {


    private DatabaseReference mDatabase;
    private TextView t_id;
    private TextView t_name;
    private TextView t_latitude;
    private TextView t_longitude;
    private TextView t_output;
    private ArrayList stations;
    private static final int RC_SIGN_IN = 123;
    //public FirebaseUser firebaseUser;
    public User user;
    public FirebaseAuth auth;
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

        //final FirebaseAuth auth = FirebaseAuth.getInstance();

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //reset database:
        //mDatabase.setValue("");
        //manually interact with database:
        //writeNewBaseStationToDatabase(1,"user1", 60.6,50.5);
        //deleteBaseStationFromDatabase(1,"user1", 60.6,50.5);

       //get_stations();
       update_stations();

   /*     List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
*/
    }

    private void load_or_create_user(final FirebaseUser firebaseUser){
        DatabaseReference ref=mDatabase.child("Users").child(firebaseUser.getUid()); //check at reference of user if it already exists
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("email")){  //user already exists
                     user = dataSnapshot.getValue(User.class);
                }
                else{  //create new user
                    user = new User(firebaseUser.getUid(),firebaseUser.getEmail(),firebaseUser.getDisplayName(),0,15,0);
                    mDatabase.child("Users").child(user.getUID()).setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void login(View view){
        BaseStation station=(BaseStation) stations.get(0);
        int score=14;
       // conquered(station,score);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                load_or_create_user(firebaseUser);
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }


    }

    public void runservice(View view){
        Intent intent = new Intent(this,  ServiceActivity.class);
        startActivity(intent);
    }

    public void update_stations(){
        mDatabase.child("stations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String prevChildKey) {
                BaseStation station = ds.getValue(BaseStation.class);  //get station object
                station.setId(ds.getKey());
                stations.add(station);                                 //add station object to list of station
                t_output.setText(stations.toString());                       //update ui
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot ds) {
                BaseStation station = ds.getValue(BaseStation.class);    //get station object
                station.setId(ds.getKey());
                stations.remove(station);                                //remove station object to list of station
                t_output.setText(stations.toString());                         //update ui
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private void writeNewBaseStationToDatabase(String StationId, String name, double latitude, double longitude) {
        BaseStation station = new BaseStation(name,latitude,longitude, 5);   //create station
        mDatabase.child("stations").child(StationId).setValue(station);   //attach station to database
    }

    private void deleteBaseStationFromDatabase(String StationId) {
         //create station
        mDatabase.child("stations").child(StationId).removeValue();  //remove station to database
    }

    public void add_station(View view) {
        // collect data from ui
         String id= t_id.getText().toString();
         String name= t_name.getText().toString();
         double latitude= Double.parseDouble(t_latitude.getText().toString());
         double longitude= Double.parseDouble(t_longitude.getText().toString());
        //send data to database
         writeNewBaseStationToDatabase(id,name,latitude,longitude);
    }

    public void delete_station(View view) {
        // collect data from ui
        String id= t_id.getText().toString();
        //send data to database
        deleteBaseStationFromDatabase(id);
    }

}