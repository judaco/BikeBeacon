package com.bikebeacon.background;

import android.support.annotation.Nullable;

import com.bikebeacon.pojo.Alert;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

public final class AlertDispatcher {


    private static final boolean mIsEmulator = true;
    private static AlertDispatcher mDispatcher;
    private OkHttpClient mHttpClient;

    private AlertDispatcher() {
        mDispatcher = this;
        mHttpClient = new OkHttpClient();
    }

    public static AlertDispatcher getDispatcher() {
        return mDispatcher == null ? new AlertDispatcher() : mDispatcher;
    }

    public void fireAlert(final Alert alert, Callback callback) {
        Request request = new Request.Builder().url(getURL()).post(new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(alert.toJSON().getBytes());
            }
        }).build();
        mHttpClient.newCall(request).enqueue(callback);
    }

    private URL getURL() {
        try {
            return mIsEmulator ? new URL("http://10.0.0.2:8080/alert_api") : new URL("http://localhost:8080/alert_api");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
