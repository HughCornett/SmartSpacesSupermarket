package com.example.smartspacesblindshopping;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListActivity extends MyActivity {


    BluetoothService bluetoothService;
    Intent intent;
    boolean bound;
    ArrayAdapter<String> arrayAdapter;

    ArrayList<String> itemList = new ArrayList<>();


    StringBuilder sb = new StringBuilder();

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        listView = (ListView) findViewById(R.id.ListView);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.textinadapter, R.id.textthing, itemList );

        listView.setAdapter(arrayAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();

        switchCallback(new String[]{"add item to the list", "save the list", "go back"});
    }

    public void addToList(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);


        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "give permissions to the app", Toast.LENGTH_SHORT).show();

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveFile(View view)
    {
        LocalDateTime now = LocalDateTime.now();

        String fileName = now.toString() + ".csv";

        ArrayList<String> data = new ArrayList<>();

        data.add(fileName);

        ReadWriteCSV.writeToCSV(this, data, "paths.csv");

        ReadWriteCSV.writeToCSV(this, itemList, fileName);

        Toast.makeText(this, "List written", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case 10:
                if(resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    ArrayList<String> matched = new ArrayList<>();
                    int previousSize = itemList.size();

                    for (Item i : dbItems) {
                        //WORD MATCHES
                        //Checks for exact match on word

                        if (result.get(0).toUpperCase().matches(i.getProductName().toUpperCase())) {
                            matched.add(i.getProductName());
                            Log.d("Match", "match found 1");
                        }

                        //Checks for match on word with extra letters/words on either side
                        else if (result.get(0).toUpperCase().matches("(.*)" + i.getProductName().toUpperCase() + "(.*)")) {
                            matched.add(i.getProductName());
                            Log.d("Match", "match found 2");
                        }

                        //BRAND MATCHES
                        //Checks for exact match on word
                        else if (result.get(0).toUpperCase().matches(i.getBrandName().toUpperCase())) {
                            promptForBrand(i.getBrandName());
                            matched.add(i.getProductName());
                            Log.d("Match", "match found 3");
                        }

                        //Checks for match on word with extra letters/words on either side
                        else if (result.get(0).toUpperCase().matches("(.*)" + i.getBrandName().toUpperCase() + "(.*)")) {
                            promptForBrand(i.getBrandName());
                            matched.add(i.getProductName());
                            Log.d("Match", "match found 4");
                        }

                        //CATEGORY MATCHES
                        //Checks for exact match on word
                        else if (result.get(0).toUpperCase().matches(i.getCategoryName().toUpperCase())) {
                            promptForCategory(i.getCategoryName());
                            matched.add(i.getProductName());
                            Log.d("Match", "match found 5");
                        }

                        //Checks for match on word with extra letters/words on either side
                        else if (result.get(0).toUpperCase().matches("(.*)" + i.getCategoryName().toUpperCase() + "(.*)")) {
                            promptForCategory(i.getCategoryName());
                            matched.add(i.getProductName());
                            Log.d("Match", "match found 6");
                        }

                        //Detects if a match has been found
                        if (matched.size() >= 2) {
                            Log.d("list activity", "Two matches found" );

                            promptForOptions();

                        }else{
                            if(!matched.isEmpty() && !itemList.contains(matched.get(0))){
                                itemList.add(matched.get(0));
                            }

                        }
                    }
                    arrayAdapter.notifyDataSetChanged();
                }

                break;
        }
    }

    protected void promptForOptions(){

    }


    protected void promptForCategory(String categoryName){
        Log.d("Test", "in prompt for category");
        ArrayList<Item> items = firebase.getItemsByCategory(categoryName);

        TextToSpeechHandler.speak("did you want", this);
        for (Item i : items){
            TextToSpeechHandler.speak(i.getProductName() + " or ", this);
        }

    }

    protected void promptForBrand(String brandName){
        Log.d("Test", "in prompt for brand");
        ArrayList<Item> items = firebase.getItemsByBrand(brandName);

        TextToSpeechHandler.speak("did you want", this);
        for (Item i : items){
            TextToSpeechHandler.speak(i.getProductName()  + " + or ", this);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void chooseOption(int index) {
        super.chooseOption(index);

        switch (index)
        {
            case 0:
                ListActivity.this.addToList(findViewById(R.id.addToList)); break;
            case 1:
                ListActivity.this.saveFile(findViewById(R.id.Save)); break;
            case 2:
                ListActivity.this.finish(); break;


            default: break;
        }
    }
}
