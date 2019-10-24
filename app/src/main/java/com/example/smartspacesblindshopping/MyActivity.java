package com.example.smartspacesblindshopping;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyActivity extends Activity {

    public static final int MESSAGE_STATE_CHANGE = 1;

    public static final int MESSAGE_TOAST = 2;
    public static final String TOAST = "TOAST";
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_READ = 4;

    public static String MAC = "3C:71:BF:59:2D:52";
    private final String MAC1 = "6C:00:6B:30:EE:1A";

    public static String NAME = "Wearable";
    private final String NAME1 = "Galaxy A40";

    public final String PATH = "paths.csv";

    public final String CHOOSE_LIST = "choose list";


    protected BluetoothService bluetoothService;
    protected Intent intent;
    protected boolean bound;

    public static boolean isBluetoothOn = false;
    protected StringBuilder sb = new StringBuilder();

    protected TextToSpeechHandler TTSHandler;

    protected FirebaseAdapter firebase = new FirebaseAdapter();

    static protected ArrayList<Item> dbItems = new ArrayList<Item>();

    static protected Store store;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TTSHandler = new TextToSpeechHandler(getApplicationContext());

        intent = new Intent(this, BluetoothService.class);
        intent.putExtra(BluetoothService.BT_NAME, NAME);
        intent.putExtra(BluetoothService.BT_ADDRESS, MAC);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        if (dbItems.isEmpty()) {
            Log.d("DB debug", "database is loading data");
            firebase.loadAllData();
            setDbItems(firebase.getItems());
            Log.d("DB debug", "database size is " + dbItems.size());
        }

        if (dbItems.size() == 9) {
            store = new Store(dbItems);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TTSHandler.ttsIsInitialized) {
            testProximity();
        }
    }

    public void testProximity() {
        Item black500g = store.shelves.get(0).getItems().get(0).get(0);
        Item black150g = store.shelves.get(0).getItems().get(0).get(1);

        Item oatmilk = store.shelves.get(0).getItems().get(0).get(2);
        Item mars = store.shelves.get(0).getItems().get(1).get(1);

        Item bread = store.shelves.get(1).getItems().get(0).get(0);
        Item chocolate = store.shelves.get(1).getItems().get(0).get(1);

        Item chocMilk = store.shelves.get(0).getItems().get(1).get(2);

        //itemProximity(black500g,black150g);
        //itemProximity(black500g,chocMilk);
        //
        itemProximity(oatmilk, chocMilk);
        //itemProximity(bread,mars);
    }

    public void itemProximity(Item i, Item j) {
        //same aisle
        if (i.getAisle() == j.getAisle()) {
            if (i.getShelf() == j.getShelf()) {
                //TTSHandler.speak(i.getProductName() + " is on the same shelf as " + j.getProductName());
                //same level
                int sectionDifference = i.getSection() - j.getSection();
                TTSHandler.speak("i section is " + i.getSection());
                TTSHandler.speak("j section is " + j.getSection());
                String spots = "spots";
                if (sectionDifference == 1) {
                    spots = "spot";
                }
                if (i.getLevel() == j.getLevel()) {
                    if (sectionDifference <= 0)
                        TTSHandler.speak(j.getProductName() + " is " + (Math.abs(sectionDifference) + 1) + spots + " to the right of " + i.getProductName());
                    if (sectionDifference >= 1)
                        TTSHandler.speak(j.getProductName() + " is " + (Math.abs(sectionDifference) + 1) + spots + " to the left of " + i.getProductName());
                } else {
                    //directly below
                    if (j.getLevel() == 1 && sectionDifference == 0)
                        TTSHandler.speak(j.getProductName() + " is directly below " + i.getProductName());
                        //below to the left
                    else if (j.getLevel() == 1 && sectionDifference < 0)
                        TTSHandler.speak(j.getProductName() + " is below " + i.getProductName() + " and " + (Math.abs(sectionDifference)) + spots + " to the right");
                        //below to the right
                    else if (j.getLevel() == 1 && sectionDifference > 0)
                        TTSHandler.speak(j.getProductName() + " is below " + i.getProductName() + " and " + (Math.abs(sectionDifference) + 1) + spots + " to the left");

                    //directly below
                    if (j.getLevel() == 0 && sectionDifference == 0)
                        TTSHandler.speak(j.getProductName() + " is directly above " + i.getProductName());
                    else if (j.getLevel() == 0 && sectionDifference < 0)
                        TTSHandler.speak(j.getProductName() + " is above " + i.getProductName() + " and " + (Math.abs(sectionDifference)) + spots + " to the right");
                    else if (j.getLevel() == 0 && sectionDifference > 0)
                        TTSHandler.speak(j.getProductName() + " is above " + i.getProductName() + " and " + (Math.abs(sectionDifference) + 1) + spots + "  to the left");
                }
            } else {
                TTSHandler.speak("the item is on another shelf");
            }
        }

        //different aisle
        else {
            TTSHandler.speak(i.getProductName() + " is on a different shelf to  " + j.getProductName());
        }
    }

    static public ArrayList<Item> getDbItems() {
        return dbItems;
    }

    protected void setDbItems(ArrayList<Item> list) {
        dbItems = list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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


    public void switchCallback(final String[] menu) {
        ((MyApplication) getApplication()).setCallBack(new Handler.Callback() {
            int index = 0;
            boolean first = true;

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


    protected void chooseOption(int index) {

    }


}
