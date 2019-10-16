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

public class MyActivity extends Activity {

    public static final int MESSAGE_STATE_CHANGE = 1;

    public static final int MESSAGE_TOAST = 2;
    public static final String TOAST = "TOAST";
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_READ = 4;

    public static final String MAC = "3C:71:BF:59:2D:52";
    private final String MAC1 = "6C:00:6B:30:EE:1A";

    public static final String NAME = "Wearable";
    private final String NAME1 = "Galaxy A40";

    protected BluetoothService bluetoothService;
    protected Intent intent;
    protected boolean bound;
    protected StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = new Intent(this, BluetoothService.class);
        intent.putExtra(BluetoothService.BT_NAME, NAME);
        intent.putExtra(BluetoothService.BT_ADDRESS, MAC);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bound)
        {
            unbindService(mConnection);
            stopService(intent);
            bound=false;

        }
    }

    protected ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            //messenger = new Messenger(service);
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


    public void switchCallback(final String[] menu)
    {
        ((MyApplication) getApplication()).setCallBack(new Handler.Callback() {
            int index = 0;
            boolean first = true;

            @Override
            public boolean handleMessage(@NonNull Message message) {
                switch (message.what) {
                    case MyActivity.MESSAGE_READ:
                        byte[] readBuf = (byte[]) message.obj;
                        String strIncom = new String(readBuf, 0, message.arg1);                 // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                        if (endOfLineIndex > 0) {
                            String sbprint = sb.substring(0, endOfLineIndex);
                            sb.delete(0, sb.length());
                            Toast.makeText(getApplicationContext(), sbprint, Toast.LENGTH_SHORT).show();
                            Log.d("debug", sbprint);


                            switch (sbprint){
                                case "Left":
                                    index=(index-1)%menu.length;
                                    if(index<0) index = menu.length + index;
                                    break;
                                case "Right":
                                    index=(index+1)%menu.length; break;
                                case "Acc":
                                    chooseOption(index);
                                    break;


                                default: break;

                            }
                            Log.d("debug", ""+menu[index]);

                        }

                        return true;
                    case MyActivity.MESSAGE_STATE_CHANGE:
                        Log.d("debug", "state:"+message.arg1);
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


    protected void chooseOption(int index)
    {

    }


}