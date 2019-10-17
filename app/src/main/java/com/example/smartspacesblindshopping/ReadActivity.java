package com.example.smartspacesblindshopping;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;


public class ReadActivity extends MyActivity
{
    public static final String EXTRA_MESSAGE = "ReadActivity.EXTRA_MESSAGE";

    ArrayAdapter<String> arrayAdapter;

    ArrayList<String> fileList = new ArrayList<>();



    ListView listView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_activity);


        listView = (ListView) findViewById(R.id.FileList);

        readPaths();
        Collections.reverse(fileList);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.textinadapter, R.id.textthing, fileList );

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                goToList(i);

            }
        });


    }

    private void goToList(int i)
    {
        String path = (String) listView.getItemAtPosition(i);
        Intent intent = new Intent(ReadActivity.this, DisplayListActivity.class);
        intent.putExtra(EXTRA_MESSAGE, path);
        startActivity(intent);
    }
    public void deleteFiles(View view)
    {
        for(String s: fileList)
        {
            boolean b = this.deleteFile(s);
            if(b)
            {
                Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show();
            }
        }

        fileList.clear();

        ReadWriteCSV.flush(this, "paths.csv");

        arrayAdapter.notifyDataSetChanged();



    }


    @Override
    protected void onResume() {
        super.onResume();
        readPaths();
        arrayAdapter.notifyDataSetChanged();
        switchMenu();

    }

    @Override
    protected void chooseOption(int index) {
        super.chooseOption(index);

        if(index<fileList.size())
        {
            goToList(index);
        }
        else if(index==fileList.size())
        {
            ReadActivity.this.deleteFiles(findViewById(R.id.DeleteButton));

        }
        else ReadActivity.this.finish();

    }


    private void switchMenu()
    {
        ArrayList<String> menu = new ArrayList<>();

        menu.addAll(fileList);
        menu.add("delete lists");
        menu.add("go back");

        String[] array = new String[fileList.size()+2];

        array = menu.toArray(array);
        switchCallback(array);
    }


    private void readPaths()
    {
        fileList.clear();
        fileList.addAll(ReadWriteCSV.readCSV(this, "paths.csv"));

    }

}
