package com.example.base_station_game;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {

    TextView username = null;
    TextView email = null;
    TextView exp = null;
    TextView level = null;
    TextView id = null;
    private DatabaseReference mDatabase;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
    }

    @Override
    protected void onStart(){
        super.onStart();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()); //check at reference of user if it already exists
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        username = (TextView) findViewById(R.id.textValueView3);
        email = (TextView) findViewById(R.id.textValueView4);
        exp = (TextView) findViewById(R.id.textValueView5);
        level = (TextView) findViewById(R.id.textValueView6);
        id = (TextView) findViewById(R.id.textValueView7);
        updateUI();

    }

    private void updateUI(){
        username.setText(user.getUsername());
        email.setText(user.getEmail());
        exp.setText("" + user.getExp());
        level.setText("" + user.getLevel());
        id.setText(user.getUID());
    }
}
