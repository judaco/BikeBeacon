package com.bikebeacon.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bikebeacon.R;
import com.bikebeacon.background.AlertDispatcher;
import com.bikebeacon.background.Constants;
import com.bikebeacon.pojo.Alert;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TestActivity extends Activity implements View.OnClickListener, Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViewById(R.id.btn_fire_alert).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this, "Alert Shot Started", Toast.LENGTH_SHORT).show();
        Alert alert = new Alert(Constants.AlertAction.ALERT_NEW);
        alert.setGPSCoords("test,test");
        alert.setCellTowersID("test,test,test");
        alert.setIsClosed(false);
        alert.setOwner("Alon");
        alert.setPreviousAlertId("null");
        AlertDispatcher.getDispatcher().fireAlert(alert, this);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        TestActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TestActivity.this, "Failed alert shot.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {

        TestActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (response.code() == 200) {
                    Toast.makeText(TestActivity.this, "Alert shot succeeded with:", Toast.LENGTH_SHORT).show();
                    try {
                        Toast.makeText(TestActivity.this, new String(response.body().bytes()), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(response.toString());
                    Toast.makeText(TestActivity.this, "Alert shot failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
