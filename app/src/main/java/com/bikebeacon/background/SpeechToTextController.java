package com.bikebeacon.background;

import android.app.Activity;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Alon on 8/22/2017.
 */

public class SpeechToTextController {

    private static SpeechToTextController mController;

    private SpeechToText mService;

    private SpeechToTextController() {
        mController = this;
        mService = new SpeechToText();
    }

    public static SpeechToTextController getController() {
        return mController == null ? new SpeechToTextController() : mController;
    }

    public void performTranslation(Activity activity, File input) {
        login(activity);

    }

    private void login(Activity activity) {
        try {
            InputStream stream = activity.getAssets().open("stt.creds");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line;
            int count = 0;

            String username = null;
            String password = null;
            while ((line = reader.readLine()) != null)
                if (count++ == 0)
                    username = line;
                else if (count == 1)
                    password = line;

            mService.setUsernameAndPassword(username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
