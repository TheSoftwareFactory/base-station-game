package com.example.base_station_game;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class TeamCreationActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String currentTeam;
    private String uid;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase.child("Users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long lvl = (long) dataSnapshot.child("level").getValue();
                        if (lvl < 5) {
                            Intent data = new Intent();
                            data.putExtra("Error", "You need to be level 5 or higher to do this");
                            setResult(Activity.RESULT_CANCELED, data);
                            finish();
                        }
                        currentTeam = (String) dataSnapshot.child("team").getValue();
                        username = (String) dataSnapshot.child("username").getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        setContentView(R.layout.activity_team_creation);
    }

    public void createTeam(View view) {
        if (!currentTeam.equals("Lone Wolfs")) {
            Toast.makeText(this, "You are already in " + currentTeam, Toast.LENGTH_SHORT).show();
            return;
        }
        ExtendedEditText teamNameField = findViewById(R.id.team_name_text);
        String teamName = teamNameField.getText().toString();

        mDatabase.child("Teams").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.child(teamName).exists()) {
                            mDatabase.child("Teams").child("Lone Wolfs").child(uid).removeValue();
                            mDatabase.child("Users").child(uid).child("team").setValue(teamName);
                            currentTeam = teamName;
                            mDatabase.child("Teams").child(teamName).child(uid).setValue(username);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Intent data = new Intent();
                            data.putExtra("Error", "Team already exists");
                            setResult(Activity.RESULT_CANCELED, data);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }
}
