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
        login();
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
        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference ref = mDatabase.child("Users").child(firebaseUser.getUid()); //check at reference of user if it already exists
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("team")) {  //user already exists
                        user = dataSnapshot.getValue(User.class);
                    } else {  //create new user
                        final EditText et = new EditText(MainActivity.this);
                        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                .setView(et)
                                .setTitle("WELCOME! Choose you team!")
                                .setMessage("Type the name of your team")
                                .setPositiveButton("ok", null) //Set to null. We override the onclick
                                .create();

                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                            @Override
                            public void onShow(DialogInterface dialogInterface) {

                                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                button.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        String userinput = et.getText().toString().trim();
                                        if (!userinput.isEmpty()) {
                                            user = new User(firebaseUser.getUid(), firebaseUser.getEmail(), firebaseUser.getDisplayName(),  userinput);
                                            mDatabase.child("Users").child(user.getUID()).child("username").setValue(user.getUsername());
                                            mDatabase.child("Users").child(user.getUID()).child("email").setValue(user.getEmail());
                                            mDatabase.child("Users").child(user.getUID()).child("uid").setValue(user.getUID());
                                            mDatabase.child("Users").child(user.getUID()).child("team").setValue(user.getTeam());



                                            // Check if the team already exits
                                            DatabaseReference refTeam = mDatabase.child("Teams"); //check at reference of user if it already exists
                                            refTeam.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (!dataSnapshot.hasChild(userinput)) {  //user already exists
                                                        mDatabase.child("Teams").child(userinput).setValue(0);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                            dialog.dismiss();
                                        }
                                        else{
                                            et.setError("Please insert the name of your team.");
                                        }
                                    }
                                });
                            }
                        });
                        dialog.show();
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
