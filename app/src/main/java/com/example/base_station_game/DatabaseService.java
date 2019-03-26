package com.example.base_station_game;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Random;


public class DatabaseService extends IntentService {

    private final IBinder binder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    public DatabaseService() {
        super("DatabaseService2");
    }

    public class LocalBinder extends Binder {
        DatabaseService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DatabaseService.this;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "ServiceActivity starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /** method for clients */
    public int getRandomNumber() {
        int a= mGenerator.nextInt(100);
        return a;
    }


    @Override
    public void onDestroy() {
        Toast.makeText(this, "ServiceActivity done", Toast.LENGTH_SHORT).show();
    }


}
