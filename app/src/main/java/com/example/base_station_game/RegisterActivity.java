package com.example.base_station_game;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    public User user;
    private ExtendedEditText username_field;
    private ExtendedEditText email_field;
    private ExtendedEditText team_field;
    private ExtendedEditText password_field;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    public void register(View v) {
        email_field = findViewById(R.id.email_text);
        String email=email_field.getText().toString();
        team_field = findViewById(R.id.team_text);
        String team=team_field.getText().toString();
        username_field = findViewById(R.id.username_text);
        String username=username_field.getText().toString();
        password_field =  findViewById(R.id.password_text);
        String password=password_field.getText().toString();



        if ( password.equals("") || email.equals("") || team.equals("") || username.equals(""))
        {
            Toast.makeText(RegisterActivity.this, "Please fill all fields!",
                    Toast.LENGTH_SHORT).show();
        }
        else {

            mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference ref = mDatabase.child("usernames").child(username); //check at reference of user if it already exists
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(RegisterActivity.this, "Username already in use.",
                                Toast.LENGTH_SHORT).show();

                    }
                    else {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(RegisterActivity.this, task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, username + " registered!",
                                                Toast.LENGTH_SHORT).show();
                                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                if(task.isSuccessful()){
                                                    String token = task.getResult().getToken();
                                                    user = new User(firebaseUser.getUid(), firebaseUser.getEmail(), username, team);
                                                    user.beUpdated();
                                                    mDatabase.child("Users").child(user.getUID()).child("username").setValue(user.getUsername());
                                                    mDatabase.child("Users").child(user.getUID()).child("email").setValue(user.getEmail());
                                                    mDatabase.child("Users").child(user.getUID()).child("uid").setValue(user.getUID());
                                                    mDatabase.child("Users").child(user.getUID()).child("team").setValue(user.getTeam());
                                                    mDatabase.child("Users").child(user.getUID()).child("token").setValue(token);

                                                    mDatabase.child("usernames").child(username).setValue(user.getUID());
                                                    mDatabase.child("Teams").child(team).child(firebaseUser.getUid()).setValue(username);
                                                    startMap();
                                                }
                                                else
                                                {
                                                    Log.d("token error","token couldnt get generated");
                                                }
                                            }
                                        });


                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Registering failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });


        }
    }
    public void startMap(){
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

}
