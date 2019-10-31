package com.example.smartspacesblindshopping;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    ArrayList<String> displayList = new ArrayList<>();
    ListView listView;

    String mode;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_activity);

        mode = getIntent().getStringExtra(MANAGE_OR_SHOP);
        listView = (ListView) findViewById(R.id.FileList);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.textinadapter, R.id.textthing, displayList );

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mode .equals("shop")) {
                    goToChoosingLists(i);
                }
                else if(mode.equals("manage"))
                {
                    goToManagingLists(i);
                }
            }
        });
    }

    private void goToChoosingLists(int i)
    {
        Intent intent = chooseList(i);
        startActivityForResult(intent, 10);
    }

    private void goToManagingLists(int i)
    {
        Intent intent = chooseList(i);
        startActivity(intent);
    }

    private Intent chooseList(int i)
    {
        String path = fileList.get(i);
        Intent intent = new Intent(ReadActivity.this, DisplayListActivity.class);
        intent.putExtra(EXTRA_MESSAGE, path);
        intent.putExtra(MANAGE_OR_SHOP, mode);
        return intent;
    }
    public void deleteFiles()
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

        ReadWriteCSV.flush(this, PATH);

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
            if(mode .equals("shop")) {
                goToChoosingLists(index);
            }
            else if(mode.equals("manage"))
            {
                goToManagingLists(index);
            }
        }

        else ReadActivity.this.finish();

    }


    private void switchMenu()
    {
        ArrayList<String> menu = new ArrayList<>();


        menu.addAll(displayList);
        menu.add("go back");

        String[] array = new String[displayList.size()+1];

        array = menu.toArray(array);
        switchCallback(array);
    }


    private void readPaths()
    {
        fileList.clear();
        displayList.clear();
        fileList.addAll(ReadWriteCSV.readCSV(this, PATH));

        Collections.reverse(fileList);

        for(int i = 0; i<fileList.size();++i)
        {
            displayList.add("list " + (i+1));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10)
        {
            if (resultCode == RESULT_OK && data != null) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
