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
    CustomItemAdapter customItemAdapter;
    ListView listView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        currentItemText = (TextView) findViewById(R.id.currentItem);

        listView = (ListView) findViewById(R.id.shoppingList);

        customItemAdapter = new CustomItemAdapter(this, shoppingList);

        listView.setAdapter(customItemAdapter);

        Map.init();
        Directions.computeMatrices();


    }

    @Override
    protected void onResume() {
        super.onResume();
        switchCallback(new String[]{"Choose a list", "next instructions", "previous instruction", "current item", "add items to the list", "Go back"}, "you are in the shopping menu");

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

        if (shoppingList.isEmpty()) Directions.setCurrentPathNode(Map.user, Map.exit);
        Directions.nextDirection();
        TTSHandler.speak(Directions.pathToString());
    }

    public void nextItem(View view) {

        if (currentItem == null) return;

        Map.user.setX(Directions.getClosestNode(Map.getItemXCoord(currentItem), Map.getItemYCoord(currentItem), true).getXPosition());
        Map.user.setY(Directions.getClosestNode(Map.getItemXCoord(currentItem), Map.getItemYCoord(currentItem), true).getYPosition());
        Map.user.setFacing(Map.userFaceItem(Map.user, currentItem));

        shoppingList.remove(currentItem);

        changeItem();

        if (currentItem != null)
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

                changeItem();

                ArrayList<Node> path = Directions.currentPath;

                Log.d("path", "Path from " + path.get(0) + " to " + path.get(path.size() - 1) + ": " + path);
                Log.d("direction", "" + Directions.pathToString());
                TTSHandler.speak(Directions.pathToString());
                customItemAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == 20) {
            if (resultCode == RESULT_OK && data != null) {
                shoppingList.addAll(stringsToItems(data.getStringArrayListExtra(APPEND_TO_LIST)));
                changeItem();

                ArrayList<Node> path = Directions.currentPath;

                Log.d("path", "Path from " + path.get(0) + " to " + path.get(path.size() - 1) + ": " + path);
                Log.d("direction", "" + Directions.pathToString());
                TTSHandler.speak(Directions.pathToString());
                customItemAdapter.notifyDataSetChanged();
            }
        }
    }

    public void previousDirection(View view) {
        if (shoppingList.isEmpty()) Directions.setCurrentPathNode(Map.user, Map.exit);

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
                ShoppingActivity.this.nextInstruction(findViewById(R.id.nextInstruction));
                break;
            case 2:
                ShoppingActivity.this.previousDirection(findViewById(R.id.previousButton));
                break;
            case 3:
                TTSHandler.speak(currentItem.getFullName());
            case 4:
                ShoppingActivity.this.addItem(findViewById(R.id.addItem));
            case 5:
                finish();
            default:
                break;
        }
    }


    @Override
    public void switchCallback(final String[] menu, String firstMessage) {
        TTSHandler.speak(firstMessage+" your first option is " + menu[0]);

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
                                    if (scannedItem != null) {
                                        //set user position and facing direction
                                        Map.user.setX(Directions.getClosestNode(Map.getItemXCoord(scannedItem), Map.getItemYCoord(scannedItem), true).getXPosition());
                                        Map.user.setY(Directions.getClosestNode(Map.getItemXCoord(scannedItem), Map.getItemXCoord(scannedItem), true).getYPosition());
                                        Map.user.setFacing(Map.userFaceItem(Map.user, scannedItem));

                                        if (shoppingList.size() > 1) {
                                            if (ItemOnShoppingList(scannedItem)) {

                                                shoppingList.remove(scannedItem);

                                                changeItem();

                                                TTSHandler.speak("The next item on your shopping list is" + currentItemText.getText());

                                                itemShelfProximityFeedback(scannedItem, currentItem);

                                                customItemAdapter.notifyDataSetChanged();
                                            } else if (currentItem != null) {
                                                itemShelfProximityFeedback(scannedItem, currentItem);

                                            }
                                        }else if (shoppingList.size() == 1) {
                                            if (ItemOnShoppingList(scannedItem)) {
                                                shoppingList.clear();
                                                TTSHandler.speak("Your shopping list is complete, please make you way through to the checkout");
                                            }
                                            TTSHandler.speak(Directions.pathToString());
                                            customItemAdapter.notifyDataSetChanged();
                                        }
                                    }
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
                        TTSHandler.speak(j.getProductName() + " is " + ((Math.abs(sectionDifference)) == 0 ? 1 : Math.abs(sectionDifference)) + spots + " to the right");
                    if (sectionDifference >= 1)
                        TTSHandler.speak(j.getProductName() + " is " + (((Math.abs(sectionDifference)) == 0 ? 1 : Math.abs(sectionDifference)) + spots + " to the left "));
                } else {
                    //directly below
                    if (j.getLevel() == 1 && sectionDifference == 0)
                        TTSHandler.speak(j.getProductName() + " is directly below");
                        //below and to the left
                    else if (j.getLevel() == 1 && sectionDifference < 0)
                        TTSHandler.speak(j.getProductName() + " is below and " + ((Math.abs(sectionDifference)) == 0 ? 1 : Math.abs(sectionDifference)) + spots + " to the right");
                        //below and to the right
                    else if (j.getLevel() == 1 && sectionDifference > 0)
                        TTSHandler.speak(j.getProductName() + " is below and " + (((Math.abs(sectionDifference)) == 0 ? 1 : Math.abs(sectionDifference)) + spots + " to the left"));

                        //directly above
                    else if (j.getLevel() == 0 && sectionDifference == 0)
                        TTSHandler.speak(j.getProductName() + " is directly above");
                        //above and to the right
                    else if (j.getLevel() == 0 && sectionDifference < 0)
                        TTSHandler.speak(j.getProductName() + " is above and " + (((Math.abs(sectionDifference)) == 0 ? 1 : Math.abs(sectionDifference)) + spots + " to the right"));
                        //above and to the left
                    else if (j.getLevel() == 0 && sectionDifference > 0)
                        TTSHandler.speak(j.getProductName() + " is above and " + (((Math.abs(sectionDifference)) == 0 ? 1 : Math.abs(sectionDifference)) + spots + "  to the left"));
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
                TTSHandler.speak("That item is on your list - please select it now");
                return true;
            } else {
                for (Item j : shoppingList) {
                    if ((i.getProductName()).equals(j.getProductName())) {
                        TTSHandler.speak("That item is also on your list - you may select it now");
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public void map(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    //TODO
    //Directions.getNextDirection() when user presses the button on their glove


    private void changeItem() {
        if (shoppingList.isEmpty()) {
            currentItem = null;
            currentItemText.setText(R.string.no_current_item);
            Directions.setCurrentPathNode(Map.user, Map.exit);
        } else {
            currentItem = Directions.getClosestItem(Map.user, shoppingList);
            currentItemText.setText(currentItem.getProductName());
            Directions.setCurrentPath(Map.user, currentItem);

        }

    }

    public void blockageReported() {
        //if the user isn't at the final node of the path
        if (Directions.currentPathTurnsPos < Directions.currentPathTurns.size() - 1) {
            //set the blockage to between their current node and the next node they have to get to
            Node blockageStart = Directions.currentPathTurns.get(Directions.currentPathTurnsPos);
            Node blockageEnd = Directions.currentPathTurns.get(Directions.currentPathTurnsPos + 1);

            //get the indexes of the nodes in currentPath
            int blockageStartIndex = Directions.currentPath.indexOf(blockageStart);
            int blockageEndIndex = Directions.currentPath.indexOf(blockageEnd);

            //for each node between blockageStart and blockageEnd (not including blockage end)
            for (int i = blockageStartIndex; i < blockageEndIndex; i++) {
                //add a blockage between this node and the next one
                Map.addBlockage(Directions.currentPath.get(i), Directions.currentPath.get(i + 1));
            }
        }
        //if they are
        else {
            Log.e("blockage", "user reports error at final node");
        }
        Directions.computeMatrices();
        ;
    }


    private boolean isOnTheSameShelf(Item i, Item j) {
        return i.getShelf() == j.getShelf();
    }

}
