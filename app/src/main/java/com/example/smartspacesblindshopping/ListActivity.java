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

import org.apache.commons.lang3.text.StrBuilder;

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

        arrayAdapter = new ArrayAdapter<>(this, R.layout.textinadapter, R.id.textthing, itemList);

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
    public void saveFile(View view) {
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

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    matchResults(result);
                    arrayAdapter.notifyDataSetChanged();
                }

                default: break;
        }
    }

    protected void matchResults(ArrayList<String> result){
        boolean brandMatch = false;
        boolean categoryMatch = false;
        boolean exactMatch = false;

        String tempBrand = "";
        String tempCat = "";
        Log.d("result", result.get(0));
        for (Item i : dbItems) {

            Log.d("item ", i.getProductName() + " BRAND " + i.getBrandName() + " CATEGORY" + i.getCategoryName());
            if (result.get(0).toUpperCase().matches(i.getProductName().toUpperCase())) {
                exactMatch = true;
            }

            //Does not match "NESCAFÉ" with "NESCAFÉ" ??
            else if (result.get(0).toUpperCase().matches(i.getBrandName().toUpperCase()) ||
                    result.get(0).toUpperCase().matches("(.*)" + i.getBrandName().toUpperCase() + "(.*)")) {
                brandMatch = true;
                tempBrand = i.getBrandName();
            }

            else if (result.get(0).toUpperCase().matches(i.getCategoryName().toUpperCase()) ||
                    result.get(0).toUpperCase().matches("(.*)" + i.getCategoryName().toUpperCase() + "(.*)")) {
                categoryMatch = true;
                tempCat = i.getCategoryName();
            }
        }

        Log.d("booleans", "exact match is " + exactMatch);
        Log.d("booleans", "cat match is " + categoryMatch);
        Log.d("booleans", "brand match is " + brandMatch);


        if (exactMatch){
            itemList.add(result.get(0));
        }
        else if (categoryMatch) {
            promptForCategory(tempCat);
            //Pop up window here

        }else if(brandMatch) {
            promptForBrand(tempBrand);
            //Pop up window here
        }
    }

    protected void promptForCategory(String categoryName) {
        Log.d("prompt for category", "in prompt for category");
        ArrayList<Item> items = firebase.getItemsByCategory(categoryName);
        StrBuilder builder = new StrBuilder();

        builder.append("Did you want ");
        for (Item i : items) {
            builder.append(i.getBrandName() + "'s" + i.getProductName() + " or ");

        }

        builder.delete(builder.length() - 3, builder.length() - 1);
        TTSHandler.speak(builder.build());
    }

    protected void promptForBrand(String brandName) {
        Log.d("prompt for brand", "in prompt for brand" + brandName);
        ArrayList<Item> items = firebase.getItemsByBrand(brandName);
        StrBuilder builder = new StrBuilder();

        Log.d("prompt for brand", "matches found in DB is " + items.size());
        builder.append("Did you want " + brandName + "'s");

        for (Item i : items) {
            builder.append(i.getProductName() + " or ");
        }

        builder.delete(builder.length() - 3, builder.length() - 1);
        TTSHandler.speak(builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void chooseOption(int index) {
        super.chooseOption(index);

        switch (index) {
            case 0:
                ListActivity.this.addToList(findViewById(R.id.addToList));
                break;
            case 1:
                ListActivity.this.saveFile(findViewById(R.id.Save));
                break;
            case 2:
                ListActivity.this.finish();
                break;


            default:
                break;
        }
    }
}
