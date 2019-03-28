package com.example.base_station_game;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.base_station_game.sampling.Sampler;
import com.example.base_station_game.sampling.SamplingLibrary;
import com.example.base_station_game.sampling.structs.Sample;

public class SamplingDebugActivity extends AppCompatActivity {
    private Sample sample;
    private TextView sampleText;
    private TextView miniGameResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sampling_debug);
        this.sampleText = (TextView) findViewById(R.id.textView);
        this.miniGameResult = (TextView) findViewById(R.id.textView2);
        this.sample();
    }

    public void sample() {
        Intent batteryIntent = SamplingLibrary.getLastBatteryIntent(this);
        this.sample = Sampler.createSample(this, batteryIntent);
//        this.sampleText.getEditableText().clear();
        this.sampleText.setText(this.sample.toString());
    }

    public void doSample(View view) {
        this.sample();
    }

    public void launchMiniGame(View view) {
        Intent intent = new Intent(this, MinigameActivity.class);
        startActivityForResult(intent,1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1) {
            long score = data.getLongExtra("score", 0);
            this.miniGameResult.setText(new StringBuilder().append(score).toString());
        }
    }
}
