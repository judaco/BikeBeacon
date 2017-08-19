package com.bikebeacon.background;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Alon on 8/18/2017.
 */

public class FCMIDReceiver extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

    }
}
