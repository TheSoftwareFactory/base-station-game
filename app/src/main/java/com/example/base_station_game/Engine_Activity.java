package com.example.base_station_game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
//import com.google.firebase

public class Engine_Activity extends AppCompatActivity {

    @IgnoreExtraProperties
    public class User {

        public String username;
        public String email;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }

    }

    private DatabaseReference mDatabase;

    //FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engine_);
        // Write a message to the database


        mDatabase = FirebaseDatabase.getInstance().getReference();
        writeNewUser("ernst","fotze","mail");
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        Log.d("first",mDatabase.getRoot().toString());
        Log.d("second",mDatabase.getDatabase().toString());
        Log.d("third",mDatabase.setValue("lol").toString());
        //User user2 = new User(name, email);
    }
}
