package com.example.base_station_game;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SecondActivity extends AppCompatActivity {


    private LocationManager locationManager;
    private LocationListener listener;
    private GeoPoint actualPosition = null;
    Polygon p = null;

    //creating fake station list
    double startKumpulaLatitude = 60.205637;
    double startKumpulaLongitude = 24.962433;

    List<BaseStation> lbs = null;

    MapView map = null;
    Marker marker = null;

    Paint textStyle = new Paint();
    Paint pointStyle = new Paint();
    Paint selectedPointStyle = new Paint();

    private static final String LOG_TAG =
            SecondActivity.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        setContentView(R.layout.activity_second);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.getController().setZoom(3);
        p = new Polygon(map);
        //Adding base stations with Simple Fast Point Overlay

        lbs = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            lbs.add(new BaseStation(i, "Station " + i,
                    startKumpulaLatitude + ((Math.random()*2-1) * 0.0064),
                    startKumpulaLongitude + ((Math.random()*2-1) * 0.007),4));
        }

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
                Log.d(LOG_TAG, "New location --> "+ location.getLatitude() +" "+ location.getLongitude());
                actualPosition = new GeoPoint(location);
                if (marker == null) {
                    //Not dynamic
                    List<GeoPoint> circle = Polygon.pointsAsCircle(actualPosition, 100);
                    p.setPoints(circle);
                    map.getOverlayManager().add(p);
                    marker = new Marker(map);
                    marker.setPosition(actualPosition);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setTitle("test");
                    map.getOverlays().add(marker);
                    updateStationsOnMap();
                } else {
                    p.setPoints(Polygon.pointsAsCircle(actualPosition, 100));
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
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
    }


    public void localize(View view) {
        //check weather gps is enabled
        map.getController().animateTo(actualPosition, (double) 18, 1500L);
        map.invalidate();
    }

    private void updateStationsOnMap() {
        if (lbs != null) {
            List<IGeoPoint> points = new ArrayList<>();
            for (int i = 0; i < lbs.size(); i++) {
                float [] dist = new float[1];
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
                    AlertDialog alertDialog = new AlertDialog.Builder(SecondActivity.this,R.style.AlertDialogTheme).create();
                    alertDialog.setTitle(((LabelledGeoPoint) points.get(point)).getLabel());
                    float [] dist = new float[1];
                    Location.distanceBetween(actualPosition.getLatitude(), actualPosition.getLongitude(), points.get(point).getLatitude() ,  points.get(point).getLongitude(), dist);
                    if(dist[0] < 100) {
                        alertDialog.setMessage("Do you want to conquer this station?");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(SecondActivity.this, MinigameActivity.class);
                                        startActivityForResult(intent,1);
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
                    }
                    else{
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
            map.getOverlays().add(sfpo);


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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        AlertDialog alertDialog = new AlertDialog.Builder(SecondActivity.this,R.style.AlertDialogTheme).create();
        alertDialog.setTitle("Result:");
        if (requestCode == 1 && resultCode == 1) {
            long score = data.getLongExtra("score", 0);
            if (score == 0){
                alertDialog.setMessage("You lost! :(");
            }
            else{
                alertDialog.setMessage("You won! You gain "+score+" exp! :)");
            }
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
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
    }
}
