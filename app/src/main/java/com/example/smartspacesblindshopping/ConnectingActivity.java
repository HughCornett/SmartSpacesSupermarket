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
        Set<BluetoothDevice> mBondedDevices = mBluetoothAdapter.getBondedDevices();
        //Log.d("debug",""+mBondedDevices.size());

        mDeviceList.addAll(mBondedDevices);
        Log.d("debug",""+mDeviceList.size());
        listView = (ListView) findViewById(R.id.bluetoothList);
        CustomAdapter adapter = new CustomAdapter(getApplicationContext(), mDeviceList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MAC = mDeviceList.get(i).getAddress();
                NAME = mDeviceList.get(i).getName();
            }
        });
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
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


    static class ViewHolder
    {
        TextView device;
        TextView mac;
    };

    public class CustomAdapter extends BaseAdapter
    {

        ArrayList<BluetoothDevice> objects;
        private LayoutInflater inflater;

        CustomAdapter(Context context, ArrayList<BluetoothDevice> objects)
        {
            inflater = LayoutInflater.from(context);
            this.objects = objects;
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public Object getItem(int i) {
            return objects.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if(view ==null)
            {
                holder = new ViewHolder();

                view = inflater.inflate(R.layout.bluetooth_list, null);

                holder.device = (TextView) view.findViewById(R.id.bluetoothDevice);
                holder.mac = (TextView) view.findViewById(R.id.MAC);

                view.setTag(holder);
            }
            else
                holder = (ViewHolder) view.getTag();

            holder.device.setText(objects.get(i).getName());
            holder.mac.setText(objects.get(i).getAddress());

            return view;
        }
    }

}
