package com.example.base_station_game;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.example.base_station_game.R.id.settings_ID;

public class MapActivity extends AppCompatActivity {

    DatabaseService mService;
    private DatabaseReference mDatabase;
    boolean mBound = false;
    private ProgressBar expBar;
    private TextView level;
    private User user;
    private LocationManager locationManager;
    private LocationListener listener;
    private ChildEventListener childListener;
    private GeoPoint actualPosition = new GeoPoint(0, 0);
    private SimpleFastPointOverlay sfpo;
    private Polygon p = null;

    //Meters
    static int MAX_DISTANCE = 200;

    //creating fake station list
    double startKumpulaLatitude = 60.205637;
    double startKumpulaLongitude = 24.962433;

    List<BaseStation> lbs = null;

    private MapView map = null;
    private Marker marker = null;

    private Paint textStyle = new Paint();
    private Paint pointStyle = new Paint();
    private Paint selectedPointStyle = new Paint();

    private static final String LOG_TAG =
            MapActivity.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.user = (User) getIntent().getSerializableExtra("user");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        childListener = new ChildEventListener() {
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot ds, String prevChildKey) {
                Log.e("DS", ds.toString());
                if(ds.getKey().equals("level")){
                    long lvl = (long) ds.getValue();
                    level.setText(""+lvl);
                    AlertDialog levelDialog = new AlertDialog.Builder(MapActivity.this, R.style.AlertDialogTheme).create();
                    levelDialog.setTitle("Level Up!");
                    levelDialog.setMessage("You finally reached Level "+lvl);
                }
                if(ds.getKey().equals("exp")){
                    long exp = (long) ds.getValue();
                    if (Build.VERSION.SDK_INT >= 24) {
                        expBar.setProgress((int) exp, true);
                    } else {
                        expBar.setProgress((int) exp);
                    }
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addChildEventListener(childListener);
        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_map);
        getSupportActionBar().hide();
        level = findViewById(R.id.level);
        expBar = findViewById(R.id.expBar);
        mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    System.out.println("Initing level and exp");
                    Object lvlobj = dataSnapshot.child("level").getValue();
                    if (lvlobj == null) {
                        System.out.println("level was null!");
                        long i = 1;
                        lvlobj = i;
                    }
                    int lvl = ((Long) lvlobj).intValue();
                    level.setText(""+lvl);
                    Object expobj = dataSnapshot.child("exp").getValue();
                    if (expobj == null) {
                        System.out.println("exp was null!");
                        long i = 0;
                        expobj = i;
                    }
                    int exp = ((Long) expobj).intValue();
                    if (Build.VERSION.SDK_INT >= 24) {
                        expBar.setProgress(exp, true);
                    } else {
                        expBar.setProgress(exp);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            }
        );

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.getController().setZoom(3);
        p = new Polygon(map);
        //Adding base stations with Simple Fast Point Overlay

        lbs = new ArrayList<>();
        /*for (int i = 0; i < 0; i++) {
            lbs.add(new BaseStation("Station " + i,
                    startKumpulaLatitude + ((Math.random()*2-1) * 0.0054),
                    startKumpulaLongitude + ((Math.random()*2-1) * 0.004),null));
        }*/

        // create label style

        textStyle.setStyle(Paint.Style.FILL);
        textStyle.setColor(Color.parseColor("#0000ff"));
        textStyle.setTextAlign(Paint.Align.CENTER);
        textStyle.setTextSize(24);

        // create point style

        pointStyle.setStyle(Paint.Style.FILL);
        pointStyle.setColor(Color.parseColor("#0000ff"));
        pointStyle.setTextAlign(Paint.Align.CENTER);

        // create selected point style

        selectedPointStyle.setStyle(Paint.Style.FILL);
        selectedPointStyle.setColor(Color.parseColor("#00ff00"));
        selectedPointStyle.setTextAlign(Paint.Align.CENTER);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d(LOG_TAG, "New location --> " + location.getLatitude() + " " + location.getLongitude());
                actualPosition = new GeoPoint(location);
                if (marker == null) {
                    //Not dynamic
                    List<GeoPoint> circle = Polygon.pointsAsCircle(actualPosition, MAX_DISTANCE);
                    p.setPoints(circle);
                    map.getOverlayManager().add(p);
                    marker = new Marker(map);
                    marker.setPosition(actualPosition);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setTitle("test");
                    map.getOverlays().add(marker);
                    updateStationsOnMap();
                } else {
                    p.setPoints(Polygon.pointsAsCircle(actualPosition, MAX_DISTANCE));
                    marker.setPosition(actualPosition);

                }
                map.invalidate();
            }

            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            public void onProviderEnabled(String s) {

            }

            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        locationManager.requestLocationUpdates("gps", 3000, 0, listener);

        SpeedDialView speedDialView = findViewById(R.id.speedDial);
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.user_profile_ID, R.drawable.ic_accessibility_black_24dp)
                        .create()
        );
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(settings_ID, R.drawable.ic_settings_black_24dp)
                        .create());
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.news_activity, R.drawable.ic_people_black_24dp)
                        .create());


        speedDialView.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public boolean onMainActionSelected() {
                //Toast.makeText(MapActivity.this, "Main action clicked!", Toast.LENGTH_LONG).show();
                return false; // True to keep the Speed Dial open
            }

            @Override
            public void onToggleChanged(boolean isOpen) {
                //Toast.makeText(MapActivity.this, "Speed dial toggle state changed. Open = " + isOpen, Toast.LENGTH_LONG).show();
            }
        });

        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.user_profile_ID:
                        //Toast.makeText(MapActivity.this, "cliccked on userprofile", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MapActivity.this, UserProfile.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        return true; // false will close it without animation
                    case settings_ID:
                        //Toast.makeText(MapActivity.this, "cliccked on settings", Toast.LENGTH_LONG).show();
                        Intent intent1 = new Intent(MapActivity.this, setting.class);
                        intent1.putExtra("user", user);
                        startActivity(intent1);
                        return true; // false will close it without animation
                    case R.id.news_activity:
                        //Toast.makeText(MapActivity.this, "cliccked on settings", Toast.LENGTH_LONG).show();
                        Intent intent2 = new Intent(MapActivity.this, NewsActivity.class);
                        intent2.putExtra("user", user);
                        startActivity(intent2);
                        return true; // false will close it without animation
                    default:
                        break;
                }

                return true; // To keep the Speed Dial open
            }
        });
    }


    public void localize(View view) {
        //check weather gps is enabled
        if (actualPosition != null && actualPosition.getLatitude() != 0 && actualPosition.getLongitude() != 0) {
            map.getController().animateTo(actualPosition, (double) 18, 1500L);
            map.invalidate();
        } else {
            Toast.makeText(this, "Waiting for location...", Toast.LENGTH_LONG).show();
        }
    }

    private void updateStationsOnMap() {
        if (lbs != null) {
            List<IGeoPoint> points = new ArrayList<>();
            for (int i = 0; i < lbs.size(); i++) {
                points.add(new LabelledGeoPoint(lbs.get(i).getLatitude(), lbs.get(i).getLongitude(), lbs.get(i).getName()));
            }

            // wrap them in a theme
            SimplePointTheme pt = new SimplePointTheme(points, true);

            // set some visual options for the overlay
            // we use here MAXIMUM_OPTIMIZATION algorithm, which works well with >100k points
            SimpleFastPointOverlayOptions opt = SimpleFastPointOverlayOptions.getDefaultStyle()
                    .setAlgorithm(SimpleFastPointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
                    .setRadius(10)
                    .setIsClickable(true)
                    .setCellSize(15)
                    .setTextStyle(textStyle)
                    .setPointStyle(pointStyle)
                    .setSelectedPointStyle(selectedPointStyle);

            // create the overlay with the theme
            final SimpleFastPointOverlay sfpo = new SimpleFastPointOverlay(pt, opt);
            // onClick callback

            // CREATE THE FUNCTION OUTISDE
            sfpo.setOnClickListener(new SimpleFastPointOverlay.OnClickListener() {
                @Override
                public void onClick(SimpleFastPointOverlay.PointAdapter points, Integer point) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MapActivity.this, R.style.AlertDialogTheme).create();
                    alertDialog.setTitle(((LabelledGeoPoint) points.get(point)).getLabel());
                    float[] dist = new float[1];
                    BaseStation clicckedbasestation = lbs.get(point);
                    Location.distanceBetween(actualPosition.getLatitude(), actualPosition.getLongitude(), points.get(point).getLatitude(), points.get(point).getLongitude(), dist);
                    if (dist[0] < MAX_DISTANCE) {
                        String winningTeam = clicckedbasestation.getWinningTeam();
                        if (winningTeam == null) {
                            alertDialog.setMessage("Nobody played the minigame of this station before you! Hurry up! Do you want to play the minigame of this station?");
                        } else {
                            if (winningTeam.equals(user.getTeam())) {
                                alertDialog.setMessage("The winning team right now is YOUR TEAM (" + winningTeam + ")! Do you want to play the minigame of this station in order to increase the score of your team?");
                            } else {
                                alertDialog.setMessage("The winning team right now is an other TEAM (" + winningTeam + ")! Do you want to play the minigame of this station in order to defeat opponents?");
                            }
                        }
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(MapActivity.this, MinigameActivity.class);
                                        intent.putExtra("user", user);
                                        intent.putExtra("station", clicckedbasestation);
                                        startActivityForResult(intent, 1);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else {
                        alertDialog.setMessage("You cant conquer this station, get closer!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
            });

            // add overlay
            map.getOverlays().remove(this.sfpo);
            this.sfpo = sfpo;
            map.getOverlays().add(sfpo);
            map.invalidate();

        /*
        //Adding base stations with Overlay Items

        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        items.add(new OverlayItem("Title", "Description", new GeoPoint(0.0d,0.0d))); // Lat/Lon decimal degrees

            //the overlay
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                });

        mOverlay.setFocusItemsOnTap(true);

        map.getOverlays().add(mOverlay);
        */
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = new Intent(this, DatabaseService.class);
        startService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        AlertDialog alertDialog = new AlertDialog.Builder(MapActivity.this, R.style.AlertDialogTheme).create();
        alertDialog.setTitle("Result:");
        if (resultCode != Activity.RESULT_OK) {
            alertDialog.setMessage("You lost! :(");
        } else {
            Long score = data.getLongExtra("score", 0);
            alertDialog.setMessage("You won! You gain " + score.toString() + " exp! :)");
            updateStationsOnMap();
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(DatabaseReceiver,
                        new IntentFilter("my-integer"));

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(ConquerReceiver,
                        new IntentFilter("conquer"));

    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(listener);

        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(DatabaseReceiver);
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(ConquerReceiver);

    }

    // Handling the received stations from the database
    private BroadcastReceiver DatabaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            BaseStation station = (BaseStation) intent.getSerializableExtra("station");
            int OP_CODE = (int) intent.getIntExtra("OP_CODE", 0);
            Log.d("Message received!!!!!", " New station -------->" + station);
            if (station != null) {
                switch (OP_CODE) {
                    case 0:
                        lbs.add(station);
                        updateStationsOnMap();
                        break;
                    case 1:
                        lbs.remove(station);
                        updateStationsOnMap();
                        break;
                    case 2:
                        // It works because uquals check only ID, TODO: write it better
                        lbs.remove(station);
                        lbs.add(station);
                        BaseStation bs = lbs.get(lbs.indexOf(station));
                        bs.setWinningTeam(station.getWinningTeam());
                        updateStationsOnMap();
                        break;

                }
                // UPDATE ONLY ONE STATION, NOT ALL OF THEM

            }
        }
    };

    private BroadcastReceiver ConquerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
            AlertDialog station_conquered_alert = new AlertDialog.Builder(MapActivity.this, R.style.AlertDialogTheme).create();
            station_conquered_alert.setTitle("Station Conquered!");
            station_conquered_alert.setMessage(message);
            station_conquered_alert.show();
        }
    };

    @Override
    public void onBackPressed(){
        AlertDialog.Builder back_alert = new AlertDialog.Builder(MapActivity.this, R.style.AlertDialogTheme);
        back_alert.setTitle("Do you really want to log out?");


        back_alert.setNegativeButton( "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        back_alert.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
        back_alert.create().show();
    }

    public void logout(){
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
            Toast.makeText(MapActivity.this, "Logged out successfully!",
                    Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();


            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else
        {
            Log.d("User Error","No user logged in when logging out!");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
