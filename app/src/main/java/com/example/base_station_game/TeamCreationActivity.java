package com.example.base_station_game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class TeamCreationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_creation);
    }

    public void createTeam(View view) {
        ExtendedEditText teamNameField = findViewById(R.id.team_name_text);
        String teamName = teamNameField.getText().toString();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("team")
                .setValue(teamName);
    }
}
