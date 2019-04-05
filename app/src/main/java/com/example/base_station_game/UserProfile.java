package com.example.base_station_game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserProfile extends AppCompatActivity {

    TextView username = null;
    TextView email = null;
    TextView exp = null;
    TextView id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        User user = (User)getIntent().getSerializableExtra("user");
        username = (TextView) findViewById(R.id.textValueView3);
        username.setText(user.getUsername());
        email = (TextView) findViewById(R.id.textValueView4);
        email.setText(user.getEmail());
        exp = (TextView) findViewById(R.id.textValueView5);
        exp.setText(""+user.getExp());
        id = (TextView) findViewById(R.id.textValueView6);
        id.setText(user.getUID());

    }
}
