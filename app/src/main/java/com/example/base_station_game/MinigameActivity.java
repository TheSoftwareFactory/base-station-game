package com.example.base_station_game;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MinigameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame);
        Intent data = new Intent();
        long score = 1000;
        data.putExtra("score", score);
        setResult(1, data);
        finish();
    }
}
