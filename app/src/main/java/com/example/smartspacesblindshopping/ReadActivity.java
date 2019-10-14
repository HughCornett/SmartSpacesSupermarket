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


public class ReadActivity extends AppCompatActivity
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

        fileList.addAll(ReadWriteCSV.readCSV(this, "paths.csv"));

        arrayAdapter = new ArrayAdapter<>(this, R.layout.textinadapter, R.id.textthing, fileList );

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String path = (String) listView.getItemAtPosition(i);
                 //As you are using Default String Adapter
                Intent intent = new Intent(ReadActivity.this, DisplayListActivity.class);
                intent.putExtra(EXTRA_MESSAGE, path);
                startActivity(intent);

            }
        });


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


}
