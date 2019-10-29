package com.example.smartspacesblindshopping;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class ShoppingActivity extends MyActivity {

    ArrayList<String> shoppingList = new ArrayList<>();
    TextView currentItemText;
    Item currentItem;
    NfcTag currentNfcTag;
    ArrayAdapter arrayAdapter;
    ListView listView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        currentItemText = new TextView(getApplicationContext());

        listView = (ListView) findViewById(R.id.shoppingList);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.textinadapter, R.id.textthing, shoppingList);

        listView.setAdapter(arrayAdapter);


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

    public void addItem(View view) {
        Intent intent = new Intent(this, ListActivity.class);
        startActivityForResult(intent, 20);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK && data != null) {
                shoppingList.addAll(ReadWriteCSV.readCSV(getApplicationContext(), data.getStringExtra(CHOOSE_LIST)));
                currentItemText.setText(shoppingList.get(0));
                currentItem = firebase.fullNameToItem(currentItemText.getText().toString());
                arrayAdapter.notifyDataSetChanged();

            }
        }
        else if(requestCode == 20)
        {
            if (resultCode == RESULT_OK && data != null) {
                shoppingList.addAll(Objects.requireNonNull(data.getStringArrayListExtra(APPEND_TO_LIST)));
                arrayAdapter.notifyDataSetChanged();
                //TODO
                //sort the list
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
            case 1:
                ShoppingActivity.this.addItem(findViewById(R.id.addItem));
            case 2: finish();
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
                                    Item scannedItem = firebase.getItemByNFCTag(sbprint);
                                    Item itemOnlist =  firebase.fullNameToItem(currentItemText.getText().toString());
                                    currentNfcTag = new NfcTag(scannedItem);

                                    if (ItemOnShoppingList(scannedItem)) {
                                        if (!shoppingList.isEmpty()) {
                                            currentItemText.setText(shoppingList.get(0));
                                            TTSHandler.speak("The next item on your shopping list is" + currentItemText.getText());
                                        }
                                    }
                                    else {
                                        if(scannedItem!=null && itemOnlist != null){
                                            itemShelfProximityFeedback(scannedItem, itemOnlist);
                                        }else{
                                            Log.d("null found", "an item is null");
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


    /**
     * Provides audio feedback for the location of an item on the shopping list (item J)
     * compared to the scanned item (Item I)
     *
     * @param i scanned item,
     * @param j item on shopping list
     */
    public void itemShelfProximityFeedback(Item i, Item j) {
        //SAME AISLE
        if (i.getAisle() == j.getAisle()) {
            if (i.getShelf() == j.getShelf()) {
                //SAME LEVEL
                int sectionDifference = i.getSection() - j.getSection();

                String spots = "spots";
                if (sectionDifference == 1) spots = "spot";

                if (i.getLevel() == j.getLevel()) {
                    if (sectionDifference <= 0)
                        TTSHandler.speak(j.getProductName() + " is " + (Math.abs(sectionDifference) + 1) + spots + " to the right of " + i.getProductName());
                    if (sectionDifference >= 1)
                        TTSHandler.speak(j.getProductName() + " is " + (Math.abs(sectionDifference) + 1) + spots + " to the left of " + i.getProductName());
                } else {
                    //directly below
                    if (j.getLevel() == 1 && sectionDifference == 0)
                        TTSHandler.speak(j.getProductName() + " is directly below " + i.getProductName());
                        //below and to the left
                    else if (j.getLevel() == 1 && sectionDifference < 0)
                        TTSHandler.speak(j.getProductName() + " is below " + i.getProductName() + " and " + (Math.abs(sectionDifference)) + spots + " to the right");
                        //below and to the right
                    else if (j.getLevel() == 1 && sectionDifference > 0)
                        TTSHandler.speak(j.getProductName() + " is below " + i.getProductName() + " and " + (Math.abs(sectionDifference) + 1) + spots + " to the left");

                        //directly above
                    else if (j.getLevel() == 0 && sectionDifference == 0)
                        TTSHandler.speak(j.getProductName() + " is directly above " + i.getProductName());
                        //above and to the right
                    else if (j.getLevel() == 0 && sectionDifference < 0)
                        TTSHandler.speak(j.getProductName() + " is above " + i.getProductName() + " and " + (Math.abs(sectionDifference)) + spots + " to the right");
                        //above and to the left
                    else if (j.getLevel() == 0 && sectionDifference > 0)
                        TTSHandler.speak(j.getProductName() + " is above " + i.getProductName() + " and " + (Math.abs(sectionDifference) + 1) + spots + "  to the left");
                }
            } else {
                TTSHandler.speak("the item is on another shelf");
            }
        }

        //different aisle
        else {
            //re-route user from here?
        }
    }

    /**
     * returns true if the parameter item is on the user's shopping list
     *
     * @param i scanned item,
     * @return true if on list, false if not
     */
    public boolean ItemOnShoppingList(Item i) {
        if (i != null) {
            if ((i.getBrandName() + " " + i.getProductName()).equals(currentItemText.getText())) {
                TTSHandler.speak("That item is on your list");
                shoppingList.remove(currentItemText.getText());
                return true;
            } else {
                //TTSHandler.speak("That item is not correct - the next item on your shopping list is " + currentItemText.getText());
                return false;
            }
        }
        return false;
    }



}
