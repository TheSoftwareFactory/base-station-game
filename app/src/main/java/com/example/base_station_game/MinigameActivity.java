package com.example.base_station_game;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.base_station_game.sampling.Sampler;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MinigameActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private TextView textView;
    private Handler handler = new Handler();
    private User user;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame);
        user = (User)getIntent().getSerializableExtra("user");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent data = new Intent();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);
        Context context = this;
        // Start long running operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                if (Sampler.sample(context)) System.out.println("sample inserted into the database.");
                while (progressStatus < 100) {
                    progressStatus += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            textView.setText(progressStatus+"/"+progressBar.getMax());
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                long score = 1000;
                data.putExtra("score", score);
                setResult(1, data);
                finish();
            }
        }).start();

    }

    // function for minigame activity: pushes score to base station tag in database
    public void conquered(BaseStation station,double score){

        if (user.getTeam()==1){
            mDatabase.child("stations").child(station.getID()).child("BlueConquerer").child(user.getUID()).setValue(score);
        }
        else
        {
            mDatabase.child("stations").child(station.getID()).child("RedConquerer").child(user.getUID()).setValue(score);
        }

        mDatabase.child("Users").child(user.getUID()).child("conqueredStations").child(station.getID()).setValue(score);
    }
}
