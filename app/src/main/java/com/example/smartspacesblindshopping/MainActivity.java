package com.example.smartspacesblindshopping;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
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

import java.util.ArrayList;
import java.util.Vector;



public class MainActivity extends MyActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map.init();
        Directions.getNextDirection(Map.user, Map.item);

        startService(intent);



    }



    @Override
    protected void onResume() {
        super.onResume();

        switchCallback(new String[]{"create a list", "read lists"});

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void goToList(View view) {
        TTSHandler.speak("create a list");
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }


    public void readList(View view) {
        Intent intent = new Intent(this, ReadActivity.class);
        startActivity(intent);

    }

    public void goToMap(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void doShopping(View view)
    {
        Intent intent = new Intent(this, ShoppingActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void chooseOption(int index) {
        super.chooseOption(index);

        switch (index) {
            case 0:
                MainActivity.this.goToList(findViewById(R.id.createButton));
                break;
            case 1:
                MainActivity.this.readList(findViewById(R.id.readButton));
                break;

            default:
                break;
        }
    }
}
