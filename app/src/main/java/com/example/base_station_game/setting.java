package com.example.base_station_game;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class setting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_setting);


    }

    @Override
    public void onStart() {
        super.onStart();
    }


    public void logout(View v){
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
            Toast.makeText(setting.this, "Logged out successfully!",
                    Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();


            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else
        {
            Log.d("User Error","No user logged in when logging out!");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
