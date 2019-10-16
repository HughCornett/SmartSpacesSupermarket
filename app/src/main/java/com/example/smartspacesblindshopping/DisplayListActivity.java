package com.example.smartspacesblindshopping;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class DisplayListActivity extends MyActivity {

    ArrayAdapter<String> arrayAdapter;

    ArrayList<String> itemList = new ArrayList<>();



    ListView listView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_activity);

        Intent intent = getIntent();
        String path = intent.getStringExtra(ReadActivity.EXTRA_MESSAGE);


        listView = (ListView) findViewById(R.id.DisplayList);

        itemList.addAll(ReadWriteCSV.readCSV(this, path));

        arrayAdapter = new ArrayAdapter<>(this, R.layout.textinadapter, R.id.textthing, itemList );

        listView.setAdapter(arrayAdapter);

    }


    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<String> menu = new ArrayList<>();

        menu.addAll(itemList);

        menu.add("go back");

        String[] array = new String[itemList.size()+1];

        array = menu.toArray(array);
        switchCallback(array);

        switchCallback(array);

    }


    @Override
    protected void chooseOption(int index) {
        super.chooseOption(index);


        if(index==itemList.size())
        {
            DisplayListActivity.this.finish();
        }


    }
}
