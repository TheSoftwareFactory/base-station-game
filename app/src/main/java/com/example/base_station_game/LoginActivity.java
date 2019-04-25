package com.example.base_station_game;

import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    public User user;
    private TextView email_field;
    private TextView password_field;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    public void register(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }


    public void login(View v) {
        email_field = (TextView) findViewById(R.id.email);
        String email = email_field.getText().toString();
        password_field = (TextView) findViewById(R.id.password);
        String password = password_field.getText().toString();

        if ( password.equals("") || email.equals(""))
        {
            Toast.makeText(LoginActivity.this, "Please enter both Email and Password",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference ref = mDatabase.child("Users").child(firebaseUser.getUid()); //check at reference of user if it already exists
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        user = dataSnapshot.getValue(User.class);
                                        user.beUpdated();
                                        ref.removeEventListener(this);
                                        Toast.makeText(LoginActivity.this, user.getUsername() + " signed in.",
                                                Toast.LENGTH_SHORT).show();
                                        startMap();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                            } else {
                                Log.w("TAG", "signInWithEmail", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    public void startMap(){
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void onClick(View v) {
        if (user == null) {
            Toast.makeText(LoginActivity.this, "Please log in before continuing.",
                    Toast.LENGTH_SHORT).show();
        } else {
            switch (v.getId()) {
                case R.id.buttonMAP: {
                    Intent intent = new Intent(this, MapActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    break;
                }
                case R.id.sample_debug: {
                    Intent intent = new Intent(this, SamplingDebugActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    break;
                }
                case R.id.google_engine: {
                    Intent intent = new Intent(this, Engine_Activity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    break;
                }
                case R.id.user_profile: {
                    Intent intent = new Intent(this, UserProfile.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    break;
                }
                case R.id.settings: {
                    Intent intent = new Intent(this, setting.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    break;
                }
                default:
                    throw new RuntimeException("Unknown button ID");
            }
        }
    }
}
