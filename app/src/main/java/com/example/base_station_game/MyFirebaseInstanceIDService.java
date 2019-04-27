package com.example.base_station_game;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.InstanceIdResult;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private DatabaseReference mDatabase;
    @Override
    public void onTokenRefresh() {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful()){
                    mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("token").setValue(task.getResult().getToken());
                }
                else
                {
                    Log.d("token error refresh","token couldnt get generated");
                }
            }
        });

    }
}