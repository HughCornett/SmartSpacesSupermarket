package com.example.smartspacesblindshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Vector;

public class MainActivity extends MyActivity {

    private FirebaseAdapter Firebase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase = new FirebaseAdapter();

        Firebase.loadAllData();

        Map.init();
        Directions.getNextDirection(Map.user, Map.item);

        startService(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();

        switchCallback(new String[]{"go to list", "read lists"});

    }

    public void goToList(View view)
    {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }


    public void readList(View view)
    {
        Intent intent = new Intent(this, ReadActivity.class);
        startActivity(intent);

    }

    public void goToMap(View view)
    {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void chooseOption(int index) {
        super.chooseOption(index);

        switch (index)
        {
            case 0:
                MainActivity.this.goToList(findViewById(R.id.createButton)); break;
            case 1:
                MainActivity.this.readList(findViewById(R.id.readButton)); break;

            default: break;
        }
