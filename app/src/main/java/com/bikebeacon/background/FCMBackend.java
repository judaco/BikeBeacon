package com.bikebeacon.background;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.bikebeacon.background.Constants.FCM_RECEIVED_RESPONSE;
import static com.bikebeacon.background.Constants.FCM_RESPONSE;

/**
 * Created by Alon on 8/19/2017.
 */

public class FCMBackend extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String key = remoteMessage.getData().get(FCM_RESPONSE);
        getSharedPreferences("com.bikebeacon", MODE_APPEND).edit().putString(FCM_RESPONSE, key).apply();
        Log.i("FCMBackend", "onMessageReceived: Received message");
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FCM_RECEIVED_RESPONSE));
    }
}
