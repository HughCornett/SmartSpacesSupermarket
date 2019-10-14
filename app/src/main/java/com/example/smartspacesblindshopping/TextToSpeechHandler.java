package com.example.smartspacesblindshopping;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TextToSpeechHandler {

    static TextToSpeech textToSpeech;
    public static void speak(String message, Context context)
    {
         textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
         });
         textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);


    }

}
