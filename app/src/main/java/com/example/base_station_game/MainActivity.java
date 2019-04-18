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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 123;
    private DatabaseReference mDatabase;
    public User user;
    private TextView email;
    private TextView username;
    private TextView password;
    private TextView team;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //login();
        //load_or_create_user();
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //mDatabase.child("stations").setValue("");
        //logout();
    }

    private void login() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == RC_SIGN_IN) {
            // Make sure the request was successful
            //if (resultCode == RESULT_OK) {
            // The user picked a contact.
            // The Intent's data Uri identifies which contact was selected.

            // Do something with the contact here (bigger example below)
            //}
            load_or_create_user();
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    private void load_or_create_user() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference ref = mDatabase.child("Users").child(firebaseUser.getUid()); //check at reference of user if it already exists
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("team")) {  //user already exists
                        user = dataSnapshot.getValue(User.class);
                        user.beUpdated();
                    } else {  //create new user

                                            user = new User(firebaseUser.getUid(), firebaseUser.getEmail(), firebaseUser.getDisplayName(),  firebaseUser.);
                                            user.beUpdated();
                                            mDatabase.child("Users").child(user.getUID()).child("username").setValue(user.getUsername());
                                            mDatabase.child("Users").child(user.getUID()).child("email").setValue(user.getEmail());
                                            mDatabase.child("Users").child(user.getUID()).child("uid").setValue(user.getUID());
                                            mDatabase.child("Users").child(user.getUID()).child("team").setValue(user.getTeam());


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }



    // -> merge together login and load create user
    public void login(View v) {
        email = (TextView) findViewById(R.id.email);
        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);
        team = (TextView) findViewById(R.id.team);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());
                        load_or_create_user();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "signInWithEmail", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    public void onClick(View v) {
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
