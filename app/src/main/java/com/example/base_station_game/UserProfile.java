package com.example.base_station_game;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.DeadObjectException;
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
    TextView team = null;
    TextView players = null;
    long player_count=0;
    boolean team_registered;
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
        team_registered=false;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                updateUI();
                if (!team_registered){
                    registerTeam();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });




        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        exp = findViewById(R.id.exp);
        level = findViewById(R.id.level);
        team = findViewById(R.id.team);
        players = findViewById(R.id.players);


    }

    private void registerTeam(){
        DatabaseReference ref = mDatabase.child("Teams").child(user.getTeam());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                player_count = dataSnapshot.getChildrenCount();
                players.setText("Active Players:  "+player_count);
                team_registered=true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void updateUI(){
        Double max_exp=100.0;
        username.setText(user.getUsername());
        email.setText("Mail Address:  "+user.getEmail());
        Double exp_percentage= user.getExp()/max_exp*100;
        exp.setText("Experience:  " + user.getExp()+"/100  ("+exp_percentage.intValue()+"%)");
        level.setText("Level " + user.getLevel());
        team.setText("" + user.getTeam());
    }
}
