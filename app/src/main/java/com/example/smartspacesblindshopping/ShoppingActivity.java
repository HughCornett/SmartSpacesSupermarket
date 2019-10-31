package com.example.smartspacesblindshopping;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ShoppingActivity extends MyActivity {

    public static final String MIME_TEXT_PLAIN = "text/plain";
    private static final String TAG = "";

    ArrayList<Item> shoppingList = new ArrayList<>();
    TextView currentItemText;
    Item currentItem;
    NfcTag currentNfcTag;
    CustomItemAdapter customItemAdapter;
    ListView listView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        currentItemText = new TextView(getApplicationContext());

        listView = (ListView) findViewById(R.id.shoppingList);

        customItemAdapter = new CustomItemAdapter(this, shoppingList);

        listView.setAdapter(customItemAdapter);

        Map.init();
        Directions.computeMatrices();


    }

    @Override
    protected void onResume() {
        super.onResume();
        switchCallback(new String[]{"Choose a list", "add items to the list", "next instructions", "previous instruction", "Go back"});

    }


    public void readLists(View view) {
        Intent intent = new Intent(this, ReadActivity.class);
        intent.putExtra(MANAGE_OR_SHOP, "shop");
        startActivityForResult(intent, 10);

    }

    public void addItem(View view) {
        Intent intent = new Intent(this, ListActivity.class);
        startActivityForResult(intent, 20);
    }

    public void nextInstruction(View view) {

        Directions.nextDirection();
        TTSHandler.speak(Directions.pathToString());
    }

    public void nextItem(View view) {

        if (currentItem == null) return;

        Map.user.setX(Directions.getClosestNode(Map.getItemXCoord(currentItem), Map.getItemYCoord(currentItem), true).getXPosition());
        Map.user.setY(Directions.getClosestNode(Map.getItemXCoord(currentItem), Map.getItemYCoord(currentItem), true).getYPosition());
        Map.user.setFacing(Map.userFaceItem(Map.user, currentItem));

        shoppingList.remove(currentItem);
        currentItem = Directions.getClosestItem(Map.user, shoppingList);
        Directions.setCurrentPath(Map.user, currentItem);
        TTSHandler.speak("your next item is " + currentItem.getBrandName() + " " + currentItem.getProductName());
        TTSHandler.speak(Directions.pathToString());
        customItemAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK && data != null) {
                shoppingList.clear();
                shoppingList.addAll(stringsToItems(ReadWriteCSV.readCSV(getApplicationContext(), data.getStringExtra(CHOOSE_LIST))));

                currentItem = Directions.getClosestItem(Map.user, shoppingList);
                Directions.setCurrentPath(Map.user, currentItem);

                ArrayList<Node> path = Directions.currentPath;

                Log.d("path", "Path from " + path.get(0) + " to " + path.get(path.size() - 1) + ": " + path);
                Log.d("direction", "" + Directions.pathToString());
                TTSHandler.speak(Directions.pathToString());
                customItemAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == 20) {
            if (resultCode == RESULT_OK && data != null) {
                shoppingList.addAll(stringsToItems(data.getStringArrayListExtra(APPEND_TO_LIST)));
                currentItem = Directions.getClosestItem(Map.user, shoppingList);
                Directions.setCurrentPath(Map.user, currentItem);

                ArrayList<Node> path = Directions.currentPath;

                Log.d("path", "Path from " + path.get(0) + " to " + path.get(path.size() - 1) + ": " + path);
                Log.d("direction", "" + Directions.pathToString());
                TTSHandler.speak(Directions.pathToString());
                customItemAdapter.notifyDataSetChanged();
            }
        }
    }

    public void previousDirection(View view) {
        TTSHandler.speak(Directions.pathToString());
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
            case 2:
                finish();
            default:
                break;
        }
    }


    @Override
    public void switchCallback(final String[] menu) {
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
                                    //rout user from here?
                                    break;

                                default:
                                    //or here?

                                    Item scannedItem = firebase.getItemByNFCTag(sbprint);
                                    //currentNfcTag = new NfcTag(scannedItem);
                                    if(scannedItem!=null) {
                                        //set user position and facing direction
                                        Map.user.setX(Directions.getClosestNode(Map.getItemXCoord(scannedItem), Map.getItemYCoord(scannedItem), true).getXPosition());
                                        Map.user.setY(Directions.getClosestNode(Map.getItemXCoord(scannedItem), Map.getItemXCoord(scannedItem), true).getYPosition());
                                        if (Map.getItemXCoord(scannedItem) < Map.user.getX()) {
                                            Map.user.setFacing(3);
                                        } else if (Map.getItemXCoord(scannedItem) > Map.user.getX()) {
                                            Map.user.setFacing(1);
                                        } else {
                                            Log.e("Direction error", "nearest node's xpos = scanned item's xpos");
                                        }

                                        if (Map.getItemXCoord(scannedItem) < Map.user.getX()) {
                                            Map.user.setFacing(3);
                                        } else if (Map.getItemXCoord(scannedItem) > Map.user.getX()) {
                                            Map.user.setFacing(1);
                                        } else {
                                            Log.e("Direction error", "nearest node's xpos = scanned item's xpos");
                                        }
                                    currentNfcTag = new NfcTag(scannedItem);

                                    //set user position and facing direction
                                    Map.user.setX(Directions.getClosestNode(Map.getItemXCoord(scannedItem), Map.getItemYCoord(scannedItem), true).getXPosition());
                                    Map.user.setY(Directions.getClosestNode(Map.getItemXCoord(scannedItem), Map.getItemXCoord(scannedItem), true).getYPosition());
                                    Map.user.setFacing(Map.userFaceItem(Map.user, scannedItem));

                                    if (ItemOnShoppingList(scannedItem)) {
                                        if (!shoppingList.isEmpty()) {

                                            shoppingList.remove(scannedItem);

                                            currentItem = Directions.getClosestItem(Map.user, shoppingList);

                                            TTSHandler.speak("The next item on your shopping list is" + currentItemText.getText());

                                            Directions.setCurrentPath(Map.user, currentItem);

                                            TTSHandler.speak(Directions.pathToString());
                                        } else {
                                            //shopping list is empty
                                            TTSHandler.speak("Your shopping list is complete, please make you way through to the checkout");
                                            Directions.setCurrentPathNode(Map.user, Map.exit);
                                        }
                                    }else if (scannedItem != null && currentItem != null) {
                                        itemShelfProximityFeedback(scannedItem, currentItem);

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
     * @param i scanned item (CAN BE A BLANK ITEM)
     * @param j item on shopping list (CANNOT BE A BLANK ITEM)
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
                        TTSHandler.speak(j.getProductName() + " is " + (Math.abs(sectionDifference) + 1) + spots + " to the right");
                    if (sectionDifference >= 1)
                        TTSHandler.speak(j.getProductName() + " is " + (Math.abs(sectionDifference) + 1) + spots + " to the left ");
                } else {
                    //directly below
                    if (j.getLevel() == 1 && sectionDifference == 0)
                        TTSHandler.speak(j.getProductName() + " is directly below");
                        //below and to the left
                    else if (j.getLevel() == 1 && sectionDifference < 0)
                        TTSHandler.speak(j.getProductName() + " is below and " + (Math.abs(sectionDifference)) + spots + " to the right");
                        //below and to the right
                    else if (j.getLevel() == 1 && sectionDifference > 0)
                        TTSHandler.speak(j.getProductName() + " is below and " + (Math.abs(sectionDifference) + 1) + spots + " to the left");

                        //directly above
                    else if (j.getLevel() == 0 && sectionDifference == 0)
                        TTSHandler.speak(j.getProductName() + " is directly above");
                        //above and to the right
                    else if (j.getLevel() == 0 && sectionDifference < 0)
                        TTSHandler.speak(j.getProductName() + " is above and " + (Math.abs(sectionDifference)) + spots + " to the right");
                        //above and to the left
                    else if (j.getLevel() == 0 && sectionDifference > 0)
                        TTSHandler.speak(j.getProductName() + " is above and " + (Math.abs(sectionDifference) + 1) + spots + "  to the left");
                }
            } else {
                TTSHandler.speak("the item you are looking for is on another shelf");
                TTSHandler.speak(Directions.pathToString());
            }
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
            if ((i.getProductName()).equals(currentItem.getProductName())) {
                TTSHandler.speak("That item is on your list");

                return true;
            } else {
                //TTSHandler.speak("That item is not correct - the next item on your shopping list is " + currentItemText.getText());
                return false;
            }
        }
        return false;
    }

    public void map(View view)
    {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    //TODO
    //Directions.getNextDirection() when user presses the button on their glove



}
