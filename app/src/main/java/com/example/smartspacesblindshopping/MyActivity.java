package com.example.smartspacesblindshopping;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {

    public static final int MESSAGE_STATE_CHANGE = 1;

    public static final int MESSAGE_TOAST = 2;
    public static final String TOAST = "TOAST";
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_READ = 4;
    public static final int MESSAGE_CONNECTEED = 5;

    public static String MAC = "3C:71:BF:59:2D:52";
    private final String MAC1 = "6C:00:6B:30:EE:1A";

    public static String NAME = "Wearable";
    private final String NAME1 = "Galaxy A40";

    public final String PATH = "paths.csv";

    public final String CHOOSE_LIST = "choose list";
    public final String CHOOSE_BTDEVICE_MAC = "choose btdevice mac";
    public final String CHOOSE_BTDEVICE_NAME = "choose btdevice name";
    public final String APPEND_TO_LIST = "append to list";
    public final String MANAGE_OR_SHOP= "manage or shop";


    protected BluetoothService bluetoothService;
    protected Intent intent;
    protected boolean bound;

    public static boolean isBluetoothOn = false;
    protected StringBuilder sb = new StringBuilder();

    protected TextToSpeechHandler TTSHandler;

    protected FirebaseAdapter firebase = new FirebaseAdapter();

    static protected ArrayList<Item> dbItems = new ArrayList<Item>();

    static protected Store store;

    protected int state = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TTSHandler = new TextToSpeechHandler(getApplicationContext());

        intent = new Intent(this, BluetoothService.class);

        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        state = BluetoothService.mState;
        if (dbItems.isEmpty()) {
            Log.d("DB debug", "database is loading data");
            firebase.loadAllData();
            setDbItems(firebase.getItems());
            Log.d("DB debug", "database size is " + dbItems.size());
        }

        if (dbItems.size() == 24) {
            store = new Store(dbItems);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    protected void disconnect()
    {
        if (bound) {
            unbindService(mConnection);
            stopService(intent);
            bound = false;

        }
    }

    protected ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            // messenger = new Messenger(service);
            bluetoothService = ((BluetoothService.LocalBinder) service).getService();
            bound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            bluetoothService = null;
            bound = false;
        }
    };


    public void switchCallback(final String[] menu, String firstMessage, boolean initMessage) {

        if(initMessage) {
            TTSHandler.flushAndSpeak(firstMessage + " your first option is " + menu[0]);
        }
        ((MyApplication) getApplication()).setCallBack(new Handler.Callback() {
            int index = 0;


            @Override
            public boolean handleMessage(@NonNull Message message) {
                switch (message.what) {
                    case MyActivity.MESSAGE_READ:
                        byte[] readBuf = (byte[]) message.obj;

                        String strIncom = new String(readBuf, 0, message.arg1);       //create string from bytes array

                        sb.append(strIncom);                                                //append string

                        int endOfLineIndex = sb.indexOf("\r\n");                            //determine the end-of-line

                        if (endOfLineIndex > 0) {
                            String sbprint = sb.substring(0, endOfLineIndex);
                            sb.delete(0, sb.length());
                            Log.d("debug", sbprint);


                            switch (sbprint) {
                                case "Left":
                                    index = (index - 1) % menu.length;
                                    if (index < 0) index = menu.length + index;
                                    TTSHandler.speak(menu[index]);

                                    break;
                                case "Right":
                                    index = (index + 1) % menu.length;
                                    TTSHandler.speak(menu[index]);
                                    break;

                                case "Acc":
                                    chooseOption(index);
                                    break;

                                default:
                                    Item i = firebase.getItemByNFCTag(sbprint);
                                    if (i != null)
                                        Toast.makeText(getApplicationContext(), firebase.getItemByNFCTag(sbprint).getProductName(), Toast.LENGTH_SHORT).show();

                                    break;

                            }
                            Log.d("debug", "" + menu[index]);
                        }

                        return true;
                    case MyActivity.MESSAGE_STATE_CHANGE:
                        Log.d("debug", "state:" + message.arg1);
                        if(state ==3 && message.arg1==2)
                        {
                            disconnect();
                            connectToWearable();
                        }
                        state = message.arg1;

                        return true;
                    case MyActivity.MESSAGE_TOAST:
                        Log.d("debug", "message_toast");

                        return true;
                    case MyActivity.MESSAGE_WRITE:
                        Log.d("debug", "write");

                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    public ArrayList<Item> nameToItemArray(ArrayList<String> names){
        ArrayList<Item> items = new ArrayList<>();
        for(String n : names){
           Log.d("nameToItemArray", "converting " + n.toString() + " to an item");
           Item i = firebase.fullNameToItem(n);
           if(i!=null) Log.d("nameToItemArray", "conversion completed");
           items.add(i);
        }
        return items;
    }


    protected void chooseOption(int index) {

    }


    static public ArrayList<Item> getDbItems() {
        return dbItems;
    }

    protected void setDbItems(ArrayList<Item> list) {
        dbItems = list;
    }

    public ArrayList<String> itemsToStrings(ArrayList<Item> items)
    {
        ArrayList<String> strings = new ArrayList<>();
        for(Item i: items)
        {
            strings.add(i.getFullName());
        }
        return strings;
    }

    public ArrayList<Item> stringsToItems(ArrayList<String> strings)
    {
        ArrayList<Item> items = new ArrayList<>();

        for(String s: strings)
        {
            items.add(firebase.fullNameToItem(s));
        }
        return items;
    }


    protected void connectToWearable()
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
