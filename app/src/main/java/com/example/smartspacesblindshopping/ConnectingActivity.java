package com.example.smartspacesblindshopping;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class ConnectingActivity extends MyActivity {


    private ListView listView;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<>();
    private BluetoothAdapter mBluetoothAdapter;
    private CustomAdapter mAdapter;


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    showToast("Enabled");
                    mBluetoothAdapter.startDiscovery();

                }
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                for(BluetoothDevice b: mDeviceList)
                {
                    if(b.getAddress().equals(device.getAddress()))
                    {
                        return;
                    }


                }
                mDeviceList.add(device);

                showToast("Found device " + device.getName());

                mAdapter.notifyDataSetChanged();

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs bluetooth access");
                builder.setMessage("Please grant bluetooth access so this app can detect bluetooth devices");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1);
                    }
                });
                builder.show();
            }
        }



        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Log.d("debug",""+mBondedDevices.size());
        makeDiscoverable();
        mBluetoothAdapter.startDiscovery();

        Log.d("debug",""+mDeviceList.size());
        listView = (ListView) findViewById(R.id.bluetoothList);
        mAdapter = new CustomAdapter(getApplicationContext(), mDeviceList, new CustomAdapter.OnPairButtonClickListener() {
            @Override
            public void onPairButtonClick(int position) {
                BluetoothDevice device = mDeviceList.get(position);

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    connect(position);
                } else {

                    pairDevice(device);
                    connect(position);
                }

            }
        });
        listView.setAdapter(mAdapter);

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(mReceiver, intentFilter);
    }


    @Override
    protected void onDestroy() {

        unregisterReceiver(mReceiver);
        mBluetoothAdapter.cancelDiscovery();
        super.onDestroy();
    }

    private void connect(int i)
    {
        Intent intent = new Intent();
        intent.putExtra(CHOOSE_BTDEVICE_MAC, mDeviceList.get(i).getAddress());
        intent.putExtra(CHOOSE_BTDEVICE_NAME, mDeviceList.get(i).getName());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
        Log.d("debug", "Discoverable ");
    }



    private void showToast(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }




}
