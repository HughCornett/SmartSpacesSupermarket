package com.example.smartspacesblindshopping;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.Locale;

public class TextToSpeechHandler {

    static TextToSpeech textToSpeech;
    boolean ttsIsInitialized = false;

    TextToSpeechHandler(Context context)
    {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(Locale.CANADA);
                    Voice voice = new Voice("Bruh", Locale.CANADA, Voice.QUALITY_VERY_HIGH, Voice.LATENCY_HIGH, true, null);
                    textToSpeech.setVoice(voice);
                    

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS", "Language not supported");
                    } else {
                        ttsIsInitialized = true; // flag tts as initialized
                    }
                } else {
                    Log.e("TTS", "Failed");
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void speak(String message)
    {
            if(ttsIsInitialized)

                textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, LocalDateTime.now().toString());

    }

}
