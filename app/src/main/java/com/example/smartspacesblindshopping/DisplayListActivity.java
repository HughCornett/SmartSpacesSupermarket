package com.example.smartspacesblindshopping;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class DisplayListActivity extends MyActivity {


    CustomItemAdapter customItemAdapter;
    ArrayList<Item> itemList = new ArrayList<>();

    RelativeLayout mRelativeLayout;
    PopupWindow mPopupWindow;
    ListView listView;
    Context mContext;
    String path;
    String mode;

    Button button;

    int state=0;
    int item=0;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_activity);

        Intent intent = getIntent();
        path = intent.getStringExtra(ReadActivity.EXTRA_MESSAGE);
        mode = intent.getStringExtra(MANAGE_OR_SHOP);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.displaylayout);
        listView = (ListView) findViewById(R.id.DisplayList);
        button = (Button) findViewById(R.id.displayActivityButton);

        if(mode .equals("shop")) {
            button.setText(R.string.choosethis);
        }
        else if(mode.equals("manage"))
        {
            button.setText(R.string.deletethis);
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode .equals("shop")) {
                    chooseList();
                }
                else if(mode.equals("manage"))
                {
                    deleteList();
                    finish();
                }

            }
        });

        mContext = getApplicationContext();

        itemList.addAll(stringsToItems(ReadWriteCSV.readCSV(this, path)));

        customItemAdapter = new CustomItemAdapter(this,itemList );

        listView.setAdapter(customItemAdapter);

        createPopUpOnClick();



    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            wait(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        switchMenu(false);
        readOutTheList();


    }

    @Override
    protected void chooseOption(int index) {
        super.chooseOption(index);

        if(state == 0) {
            if (index == 3) {
                DisplayListActivity.this.finish();
            }
            else if (index== 1){
                if(mode .equals("shop")) {
                    chooseList();
                }
                else if(mode.equals("manage"))
                {
                    deleteList();
                    finish();
                }
            }
            else if(index == 0){
                readOutTheList();
            }
            else if(index==2)
            {
                iterateTheList();

                state = 2;

            }
        }
        else if (state==1)
        {
            if(index == 0)
            {
                itemList.remove(item);
                iterateTheList();
                customItemAdapter.notifyDataSetChanged();
                mPopupWindow.dismiss();
                state = 2;

            }
            else {
                iterateTheList();
                mPopupWindow.dismiss();
                state = 2;
            }
        }

        else if (state == 2)
        {
            if(index==itemList.size())
            {
                switchMenu(false);
                state = 0;
            }
            else
            {
                createPopUp(index);
                switchCallback(new String[]{"delete", "cancel"}, "Do you want to delete " +itemList.get(index).getFullName()+
                "from the list?", true);
                item = index;
                state = 1;
            }
        }

    }

    private void switchMenu(boolean initMessage)
    {
        ArrayList<String> menu = new ArrayList<>();

        menu.add("read out the list");

        if(mode .equals("shop")) {
            menu.add("choose this list");
        }
        else if(mode.equals("manage"))
        {
            menu.add("delete this list");
        }

        menu.add("delete an item from the list");

        menu.add("go back");

        String[] array = new String[4];

        array = menu.toArray(array);
        switchCallback(array, "you are reading a list",initMessage);
    }

    private void deleteList()
    {
        deleteFile(path);
        ArrayList<String> fileList = ReadWriteCSV.readCSV(getApplicationContext(),PATH);
        ReadWriteCSV.flush(getApplicationContext(),PATH);
        fileList.remove(path);
        ReadWriteCSV.writeToCSV(getApplicationContext(),fileList,PATH);

    }
    private void createPopUpOnClick()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                createPopUp(i);

            }
        });

    }

    private void chooseList()
    {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(CHOOSE_LIST, path);
            setResult(RESULT_OK, resultIntent);
            finish();
    }

    private void createPopUp(int i)
    {
        if(mPopupWindow!=null) mPopupWindow.dismiss();

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        View customView = inflater.inflate(R.layout.popup_delete,null);

        final int index = i;
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        Button closeButton = (Button) customView.findViewById(R.id.cancel);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                customItemAdapter.notifyDataSetChanged();
            }
        });
        Button deleteButton = (Button) customView.findViewById(R.id.deleteItem);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemList.remove(index);
                ReadWriteCSV.flush(getApplicationContext(),path);
                ReadWriteCSV.writeToCSV(getApplicationContext(),itemsToStrings(itemList),path);

                mPopupWindow.dismiss();
                if(itemList.isEmpty())
                {
                    deleteList();
                    DisplayListActivity.this.finish();
                }
                customItemAdapter.notifyDataSetChanged();
            }
        });


        mPopupWindow.showAtLocation(findViewById(R.id.displaylayout), Gravity.CENTER,0,0);
    }


    private void readOutTheList()
    {
        TTSHandler.speak("this list contains ");
        for(Item i: itemList)
        {
            TTSHandler.speak(i.getFullName());
        }
    }

    private void iterateTheList()
    {
        ArrayList<String> menu = new ArrayList<>();
        menu.addAll(itemsToStrings(itemList));
        menu.add("go back");

        String[] array = new String[itemList.size()+1];

        array = menu.toArray(array);
        switchCallback(array, "Do you want to delete any items from your list?",true);
    }

}
