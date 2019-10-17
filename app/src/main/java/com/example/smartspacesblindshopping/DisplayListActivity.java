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

        switchMenu();

    }

    @Override
    protected void chooseOption(int index) {
        super.chooseOption(index);

        int state=0;
        int item=0;

        if(state == 0) {
            if (index == itemList.size()) {
                DisplayListActivity.this.finish();
            } else {
                switchCallback(new String[]{"delete", "cancel"});
                item=index;
                state = 1;
            }
        }
        else if (state==1)
        {
            if(index == 0)
            {
                itemList.remove(item);
                switchMenu();
                arrayAdapter.notifyDataSetChanged();
                state = 0;

            }
            else {
                switchMenu();
                state = 0;
            }
        }

    }

    private void switchMenu()
    {
        ArrayList<String> menu = new ArrayList<>();

        menu.addAll(itemList);

        menu.add("go back");

        String[] array = new String[itemList.size()+1];

        array = menu.toArray(array);
        switchCallback(array);
    }
}
