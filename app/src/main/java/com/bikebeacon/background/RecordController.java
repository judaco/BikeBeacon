package com.bikebeacon.background;

import android.app.Activity;
import android.app.DialogFragment;
import android.media.MediaRecorder;
import android.util.Log;

import com.bikebeacon.pojo.RecordingDoneCallback;
import com.bikebeacon.ui.RecordingDialogFragment;

import java.io.File;
import java.io.IOException;

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
    }
}
