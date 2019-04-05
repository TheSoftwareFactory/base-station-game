package com.example.base_station_game;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MinigameActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private TextView textView;
    private Handler handler = new Handler();
    private User user;
    private BaseStation station;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame);
        user = (User) getIntent().getSerializableExtra("user");
        station = (BaseStation) getIntent().getSerializableExtra("station");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent data = new Intent();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);
        // Start long running operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            textView.setText(progressStatus + "/" + progressBar.getMax());
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.

                        // Check if the player is this in the Range
                        // In case throw an exception

                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // Something went WRONG

                        e.printStackTrace();
                    }
                }
                // Everything went GOOD
                long score = 1000;
                data.putExtra("score", score);
                conquered(station, score);
                setResult(1, data);
                finish();
            }
        }).start();

    }

    // function for minigame activity: pushes score to base station tag in database
    public void conquered(BaseStation station, double score) {
        // Undertand how to append

        try {
            //Update stations/teams/user.getTeam/ -> append user.getUID(),(score)
            mDatabase.child("stations").child(station.getID()).child("Teams").setValue(user.getTeam());
            mDatabase.child("stations").child(station.getID()).child("Teams").child(user.getTeam()).setValue(user.getUID(), (score));

            // Update users/users.UID/conqueredstations -> append station.getID() (score)
            mDatabase.child("Users").child(user.getUID()).child("conqueredStations").child(station.getID()).setValue(score);

            // Update users/users.UID/exp -> add score

            DatabaseReference ref = mDatabase.child("Users").child(user.getUID()).child("exp");
            // Attach a listener to read the data at our posts reference
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot ds) {
                    double oldvalue = (double) ds.getValue();
                    Double newvalue = Double.valueOf(score + oldvalue);
                    mDatabase.child("Users").child(user.getUID()).child("exp").setValue(newvalue);
                    user.setExp(newvalue.longValue());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("TAG", "The read failed: " + databaseError.getCode());
                }
            });
        } catch (Exception e) {
            Log.e("DATABASE SERVICE", "update_stations -> onChildAdded : probably some shit in the database" + e.toString());
        }
    }
}
