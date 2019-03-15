package com.example.base_station_game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tv1;
    TextView tv2;
    int counter=0;
    Handler handler = new Handler();
    int millisecondsOfDelay = 1000;
    int timesToReapeat = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1 = (TextView)findViewById(R.id.textViewUP);
        tv2 = (TextView)findViewById(R.id.textViewDOWN);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                counter++;
                tv1.setText(""+counter);
                tv2.setText(""+counter);
                if(counter<timesToReapeat) {
                    handler.postDelayed(this, millisecondsOfDelay);
                }
            }
        }, millisecondsOfDelay);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //tv1.setText("Hello1");
        //tv2.setText("Hello2");
    }
}
