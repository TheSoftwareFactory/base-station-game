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

import com.firebase.ui.auth.AuthUI;
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


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //mDatabase.child("stations").setValue("");
        //logout();
        login();
        load_or_create_user();
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
                    if (dataSnapshot.hasChild("email")) {  //user already exists
                        user = dataSnapshot.getValue(User.class);
                    } else {  //create new user
                        // Make the user choice his team
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme).create();
                        alertDialog.setTitle("Choose you team!");
                        final EditText et = new EditText(MainActivity.this);
                        alertDialog.setMessage("Type the name of your team");
                        alertDialog.setView(et);
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String userinput = et.getText().toString();
                                        if (userinput != "" && userinput != null) {
                                            user = new User(firebaseUser.getUid(), firebaseUser.getEmail(), firebaseUser.getDisplayName(), 0, 15, userinput);
                                            mDatabase.child("Users").child(user.getUID()).setValue(user);
                                            mDatabase.child("Users").child(user.getUID()).child("ConqueredStations").setValue("");
                                            dialog.dismiss();
                                        }
                                    }
                                });
                        alertDialog.show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
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
