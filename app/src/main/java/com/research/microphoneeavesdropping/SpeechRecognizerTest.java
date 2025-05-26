package com.research.microphoneeavesdropping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

/**
 * Implementation of the failed SpeechRecognizer approach mentioned in the research
 * Tests the manipulation of silence parameters as described in Table 6.1
 */
public class SpeechRecognizerTest {

    private static final String TAG = "SpeechRecognizerTest";
    private SpeechRecognizer speechRecognizer;
    private Context context;

    public SpeechRecognizerTest(Context context) {
        this.context = context;
    }

    public void startListening() {
        // Create speech recognizer
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    Log.d(TAG, "Ready for speech");
                }

                @Override
                public void onBeginningOfSpeech() {
                    Log.d(TAG, "Beginning of speech");
                }

                @Override
                public void onRmsChanged(float rmsdB) {
                    // Not used
                }

                @Override
                public void onBufferReceived(byte[] buffer) {
                    // Not used
                }

                @Override
                public void onEndOfSpeech() {
                    Log.d(TAG, "End of speech");
                    // Restart listening to continue recording
                    startListening();
                }

                @Override
                public void onError(int error) {
                    Log.e(TAG, "Speech recognition error: " + error);
                    // Restart on error
                    startListening();
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null) {
                        Log.d(TAG, "Speech recognized: " + matches.get(0));
                    }
                    // Restart to continue recording
                    startListening();
                }

                @Override
                public void onPartialResults(Bundle partialResults) {
                    // Not used
                }

                @Override
                public void onEvent(int eventType, Bundle params) {
                    // Not used
                }
            });

            // Create intent with manipulated parameters as described in Table 6.1
            Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

            // Attempt to manipulate endpoints as described in the paper
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, "10000");
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, "10000");
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, "60000");

            // Start listening
            speechRecognizer.startListening(recognizerIntent);
            Log.d(TAG, "Speech recognizer started with modified parameters");
        } else {
            Log.e(TAG, "Speech recognition not available on this device");
        }
    }

    public void stopListening() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.destroy();
            speechRecognizer = null;
            Log.d(TAG, "Speech recognizer stopped");
        }
    }
}