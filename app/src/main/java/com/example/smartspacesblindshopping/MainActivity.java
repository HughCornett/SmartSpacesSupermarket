package com.example.smartspacesblindshopping;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends MyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       connectToWearable();

    }


    @Override
    protected void onResume() {
        super.onResume();

        switchCallback(new String[]{"create a new list", "read shopping lists","Go to the Map", "Do your shopping"});

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void goToList(View view) {

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

    public void localize(View view)
    {
        Intent intent =  new Intent (this, LocalizationActivity.class);
        startActivity(intent);
    }

    public void connect(View view)
    {
        Intent intent =  new Intent (this, ConnectingActivity.class);
        startActivityForResult(intent, 10);
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
            case 2:
                MainActivity.this.goToMap(findViewById(R.id.mapButton));
                break;
            case 3:
                MainActivity.this.doShopping(findViewById(R.id.shoppingButton));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK && data != null) {
                Log.d("BluetoothService", "Service started");

                if (bound) {
                    unbindService(mConnection);
                    bluetoothService.stop();
                    bound = false;
                }
                ArrayList<String> btdevice = new ArrayList<>();
                btdevice.add(data.getStringExtra(CHOOSE_BTDEVICE_NAME));
                btdevice.add(data.getStringExtra(CHOOSE_BTDEVICE_MAC));
                ReadWriteCSV.writeToCSV(getApplicationContext(),btdevice, "btdevice.csv");
                intent = new Intent(this, BluetoothService.class);
                intent.putExtra(BluetoothService.BT_NAME, data.getStringExtra(CHOOSE_BTDEVICE_NAME));
                intent.putExtra(BluetoothService.BT_ADDRESS, data.getStringExtra(CHOOSE_BTDEVICE_MAC));
                startService(intent);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }
        }

    }

    private void connectToWearable()
    {
        ArrayList<String> btdevice = ReadWriteCSV.readCSV(getApplicationContext(),"btdevice.csv");
        if(!btdevice.isEmpty()) {
            intent = new Intent(this, BluetoothService.class);
            intent.putExtra(BluetoothService.BT_NAME, btdevice.get(0));
            intent.putExtra(BluetoothService.BT_ADDRESS, btdevice.get(1));
            startService(intent);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }
}
