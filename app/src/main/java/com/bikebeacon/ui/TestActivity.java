package com.bikebeacon.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bikebeacon.R;
import com.bikebeacon.background.AlertDispatcher;
import com.bikebeacon.background.Constants;
import com.bikebeacon.background.RecordController;
import com.bikebeacon.pojo.Alert;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.bikebeacon.background.Constants.FCM_RECEIVED_RESPONSE;
import static com.bikebeacon.background.Constants.FCM_RESPONSE;

public class TestActivity extends Activity implements View.OnClickListener, Callback, TextToSpeech.OnInitListener {

    private TextToSpeech mTTS;
    private String mTTSReponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.RECORD_AUDIO}, 123);
        }
        findViewById(R.id.btn_fire_alert).setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_fire_alert:
                LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        mTTSReponse = context.getSharedPreferences("com.bikebeacon", MODE_APPEND).getString(FCM_RESPONSE, "null");
                        if (mTTSReponse.equals("null")) {
                            Log.e("TestActivity", "onReceive: No response.");
                            return;
                        }
                        mTTS = new TextToSpeech(context, TestActivity.this, "com.google.android.tts");

                        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
                    }
                }, new IntentFilter(FCM_RECEIVED_RESPONSE));
                Toast.makeText(this, "Alert Shot Started", Toast.LENGTH_SHORT).show();
                Alert alert = new Alert(Constants.AlertAction.ALERT_NEW);
                alert.setGPSCoords("test,test");
                alert.setCellTowersID("test,test,test");
                alert.setIsClosed(false);
                alert.setPreviousAlertId("null");
                AlertDispatcher.getDispatcher().fireAlert(alert, this);
                break;
        }
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        TestActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TestActivity.this, "Failed alert shot.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {

        TestActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (response.code() != 200) {
                    System.out.println(response.toString());
                    Toast.makeText(TestActivity.this, "Alert shot failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.ERROR) {
            mTTS.setLanguage(Locale.UK);
            mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String s) {

                }

                @Override
                public void onDone(String s) {
                    Log.d("TestActivity", "onDone: Spoke.");
                    RecordController.getController().startRecording(TestActivity.this);
                }

                @Override
                public void onError(String s) {

                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTTS.speak(mTTSReponse, TextToSpeech.QUEUE_FLUSH, null, "ThisIsARandomID");
            } else
                mTTS.speak(mTTSReponse, TextToSpeech.QUEUE_FLUSH, null);

        }
    }
}
