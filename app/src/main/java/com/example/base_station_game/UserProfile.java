package com.example.base_station_game;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserProfile extends AppCompatActivity {

    TextView username = null;
    TextView email = null;
    TextView exp = null;
    TextView level = null;
    TextView id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        User user = (User) getIntent().getSerializableExtra("user");
        user.beUpdated();
        username = (TextView) findViewById(R.id.textValueView3);
        email = (TextView) findViewById(R.id.textValueView4);
        exp = (TextView) findViewById(R.id.textValueView5);
        level = (TextView) findViewById(R.id.textValueView6);
        id = (TextView) findViewById(R.id.textValueView7);

        username.setText(user.getUsername());
        email.setText(user.getEmail());
        exp.setText("" + user.getExp());
        level.setText("" + user.getLevel());
        id.setText(user.getUID());


    }
}
