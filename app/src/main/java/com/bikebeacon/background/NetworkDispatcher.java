package com.bikebeacon.background;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by Alon on 8/19/2017.
 */

public final class NetworkDispatcher {

    public static final boolean mIsEmulator = false;
    private static NetworkDispatcher mNetDispatcher;

    private NetworkDispatcher() {
        mNetDispatcher = this;
    }

    public static NetworkDispatcher getDispatcher() {
        return mNetDispatcher == null ? new NetworkDispatcher() : mNetDispatcher;
    }

    public NetworkRequestBuilder createRequest() {
        return new NetworkRequestBuilder();
    }

    public enum URL_TYPES {
        ALERT("/alert_api"),
        FILE("/jerry_api"),
        TOKEN("/token_api");

        private String URL;

        URL_TYPES(String path) {
            URL = path;
        }

        @Override
        public String toString() {
            return (mIsEmulator ? "http://10.0.0.2:8080" : "http://10.0.0.2:75") + URL;
        }
    }

    public class NetworkRequestBuilder {

        private URL mURL;
        private byte[] mInformation;
        private String mHTTPMethod;
        private int timeout;

        private Request mRequest;

        public NetworkRequestBuilder url(String url) {
            try {
                return url(new URL(url));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

        public NetworkRequestBuilder url(URL url) {
            mURL = url;
            return this;
        }

        public NetworkRequestBuilder method(String method) {
            if (method.equals("POST") || method.equals("GET"))
                mHTTPMethod = method;
            else
                throw new IllegalArgumentException("Only POST or GET.");
            return this;
        }

        public NetworkRequestBuilder body(byte[] information) {
            if (information != null)
                mInformation = information;
            else
                throw new IllegalArgumentException("Byte[] can't be null.");
            return this;
        }

        public NetworkRequestBuilder timeout(int seconds) {
            timeout = seconds;
            return this;
        }

        public NetworkRequestBuilder build() {
            mRequest = new Request.Builder().url(mURL).method(mHTTPMethod, mHTTPMethod.toLowerCase().equals("get") ? null : new RequestBody() {
                @Nullable
                @Override
                public MediaType contentType() {
                    return null;
                }

                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    sink.write(mInformation);
                }
            }).build();
            return this;
        }

        public void execute(Callback callback) {
            if (mRequest != null)
                new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.SECONDS).writeTimeout(timeout, TimeUnit.SECONDS).readTimeout(timeout, TimeUnit.SECONDS).build().newCall(mRequest).enqueue(callback);
            else
                throw new IllegalStateException("build() wasn't called.");
        }
    }
}
