package com.example.base_station_game;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 123;
    private DatabaseReference mDatabase;
    public User user;
    private TextView email_field;
    private TextView username_field;
    private TextView password_field;
    private TextView team_field;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

   public void logout(View v){
       Toast.makeText(MainActivity.this, user.getUsername()+" logged out.",
               Toast.LENGTH_SHORT).show();
       FirebaseAuth.getInstance().signOut();
       user=null;
   }

   public void register(View v) {
       email_field = (TextView) findViewById(R.id.email);
       String email=email_field.getText().toString();
       team_field = (TextView) findViewById(R.id.team);
       String team=team_field.getText().toString();
       username_field = (TextView) findViewById(R.id.username);
       String username=username_field.getText().toString();
       password_field = (TextView) findViewById(R.id.password);
       String password=password_field.getText().toString();



       if ( password.equals("") || email.equals("") || team.equals("") || username.equals(""))
       {
           Toast.makeText(MainActivity.this, "Please fill all fields!",
                   Toast.LENGTH_SHORT).show();
       }
       else {

           mDatabase = FirebaseDatabase.getInstance().getReference();
           DatabaseReference ref = mDatabase.child("usernames").child(username); //check at reference of user if it already exists
           ref.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   if (dataSnapshot.exists()) {
                       Toast.makeText(MainActivity.this, "Username already in use.",
                               Toast.LENGTH_SHORT).show();

                   }
                   else {
                       FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                               .addOnCompleteListener(MainActivity.this, task -> {
                                   if (task.isSuccessful()) {
                                       Toast.makeText(MainActivity.this, username + " registered!",
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
                                               }
                                               else
                                               {
                                                   Log.d("token error","token couldnt get generated");
                                               }
                                           }
                                       });


                                   } else {
                                       Toast.makeText(MainActivity.this, "Registering failed.",
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

    public void login(View v) {
        email_field = (TextView) findViewById(R.id.email);
        String email = email_field.getText().toString();
        password_field = (TextView) findViewById(R.id.password);
        String password = password_field.getText().toString();

        if ( password.equals("") || email.equals(""))
        {
            Toast.makeText(MainActivity.this, "Please enter both Email and Password",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
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
                                        Toast.makeText(MainActivity.this, user.getUsername() + " signed in.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                            } else {
                                Log.w("TAG", "signInWithEmail", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    public void onClick(View v) {
        if (user == null) {
            Toast.makeText(MainActivity.this, "Please log in before continuing.",
                    Toast.LENGTH_SHORT).show();
        } else {
            switch (v.getId()) {
                case R.id.buttonMAP: {
                    Intent intent = new Intent(this, SecondActivity.class);
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
                    Intent intent = new Intent(this, Settings.class);
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
