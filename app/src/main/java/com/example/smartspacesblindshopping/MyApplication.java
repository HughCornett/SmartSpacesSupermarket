package com.example.smartspacesblindshopping;

import android.app.Application;
import android.os.Handler;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    Handler.Callback realCallback = null;
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (realCallback != null) {
                realCallback.handleMessage(msg);
            }
        };
    };
    public Handler getHandler() {
        return handler;
    }


    public void setCallBack(Handler.Callback callback) {
        this.realCallback = callback;
    }
}
