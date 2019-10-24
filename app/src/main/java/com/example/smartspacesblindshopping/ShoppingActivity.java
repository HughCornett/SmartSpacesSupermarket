package com.example.smartspacesblindshopping;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ShoppingActivity extends MyActivity {

    ArrayList<String> shoppingList = new ArrayList<>();
    TextView currentItemText;
    Item currentItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        currentItemText = (TextView) findViewById(R.id.currentItem);
    }

    @Override
    protected void onResume() {
        super.onResume();
        switchCallback(new String[]{"Choose a list", "Go back"});

    }

    public void readLists(View view) {
        Intent intent = new Intent(this, ReadActivity.class);
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK && data != null) {
                shoppingList.addAll(ReadWriteCSV.readCSV(getApplicationContext(), data.getStringExtra(CHOOSE_LIST)));
                currentItemText.setText(shoppingList.get(0));
                currentItem = firebase.fullNameToItem(currentItemText.getText().toString());
            }
        }
    }

    @Override
    protected void chooseOption(int index) {
        super.chooseOption(index);

        switch (index) {
            case 0:
                ShoppingActivity.this.readLists(findViewById(R.id.nextItem));
                break;
            default:
                break;
        }
    }


    @Override
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
                                    //rout user from here?
                                    break;

                                default:
                                    //or here?
                                    Item i = firebase.getItemByNFCTag(sbprint);
                                    if (matchItem(i)) {
                                        if (!shoppingList.isEmpty()) {
                                            currentItemText.setText(shoppingList.get(0));
                                            TTSHandler.speak("The next item on your shopping list is" + currentItemText.getText());
                                        } else {
                                            TTSHandler.speak("Your shopping list is empty");
                                        }
                                    }
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

    public boolean matchItem(Item i) {
        if (i != null) {
            if ((i.getBrandName() + " " + i.getProductName()).equals(currentItemText.getText())) {
                TTSHandler.speak("That item is on your list");
                shoppingList.remove(currentItemText.getText());
                return true;
            } else {
                TTSHandler.speak("That item is not correct - the next item on your shopping list is " + currentItemText.getText());
                return false;
            }
        }
        return false;
    }
}
