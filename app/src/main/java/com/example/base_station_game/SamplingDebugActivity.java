package com.example.base_station_game;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.base_station_game.sampling.Sampler;
import com.example.base_station_game.sampling.SamplingLibrary;
import com.example.base_station_game.sampling.structs.Sample;

public class SamplingDebugActivity extends AppCompatActivity {
    private Sample sample;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sampling_debug);
        this.text = (TextView) findViewById(R.id.textView);
        this.sample();
    }

    public void sample() {
        Intent batteryIntent = SamplingLibrary.getLastBatteryIntent(this);
        this.sample = Sampler.createSample(this, batteryIntent);
        this.text.getEditableText().clear();
        this.text.append(this.sample.toString());
    }

    public void onClick(View view) {
        this.sample();
    }
}
