package com.bikebeacon.background;

import android.app.Activity;
import android.app.DialogFragment;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bikebeacon.pojo.RecordingDoneCallback;
import com.bikebeacon.ui.RecordingDialogFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.bikebeacon.background.Constants.RESPONSE_INPUT;
import static com.bikebeacon.background.Constants.RESPONSE_OUTPUT;
import static com.bikebeacon.background.GeneralUtility.getExternalStorageDir;

/**
 * Created by Alon on 8/22/2017.
 */

public class RecordController implements RecordingDoneCallback {

    private static RecordController mController;

    private RecordingDialogFragment mRecordDialog;
    private File mOutputFile;
    private MediaRecorder mRecorder;

    private RecordController() {
        mController = this;
        mOutputFile = new File(getExternalStorageDir(), "/messageToServer.3gp");

        mRecordDialog = new RecordingDialogFragment();
        mRecordDialog.setStopListener(this);
    }

    public static RecordController getController() {
        return mController == null ? new RecordController() : mController;
    }

    public void startRecording(Activity activity) {
        mRecordDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        mRecordDialog.show(activity.getFragmentManager(), "");

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mOutputFile.getPath());
        Log.i("RecordController", "startRecording: " + mOutputFile.getPath());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("RecordController", "prepare() failed");
        }

        mRecorder.start();
    }

    @Override
    public void onDone() {
        ///SpeechToTextController.getController().performTranslation(mRecordDialog.getActivity(), mOutputFile);
        //TODO: send to server to be converted to mp3 and then use the speech to text service to answer jerry.
        mRecordDialog.dismiss();
        mRecorder.stop();
        try {
            NetworkDispatcher.getDispatcher().createRequest()
                    .timeout(30)
                    .url(NetworkDispatcher.URL_TYPES.CONVERSION.toStringWithParams(RESPONSE_OUTPUT  +"=" + "mp3", RESPONSE_INPUT + "=" + "3gp"))
                    .body(new FileInputStream(mOutputFile))
                    .method("POST")
                    .build()
                    .execute(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.code() != 200) {
                                Log.e("RecordController", "onResponse: Failed " + response.toString());
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
