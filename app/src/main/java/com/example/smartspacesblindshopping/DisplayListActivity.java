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

        switchMenu();

    }

    @Override
    protected void chooseOption(int index) {
        super.chooseOption(index);

        int state=0;
        int item=0;

        if(state == 0) {
            if (index == itemList.size()) {
                DisplayListActivity.this.finish();
            } else {
                switchCallback(new String[]{"delete", "cancel"});
                item=index;
                state = 1;
            }
        }
        else if (state==1)
        {
            if(index == 0)
            {
                itemList.remove(item);
                switchMenu();
                customItemAdapter.notifyDataSetChanged();
                state = 0;

            }
            else {
                switchMenu();
                state = 0;
            }
        }

    }

    private void switchMenu()
    {
        ArrayList<String> menu = new ArrayList<>();

        menu.addAll(itemsToStrings(itemList));

        menu.add("go back");

        String[] array = new String[itemList.size()+1];

        array = menu.toArray(array);
        switchCallback(array);
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
                        ArrayList<String> fileList = ReadWriteCSV.readCSV(getApplicationContext(),path);
                        ReadWriteCSV.flush(getApplicationContext(),path);
                        fileList.remove(itemList.get(index));
                        ReadWriteCSV.writeToCSV(getApplicationContext(),fileList,path);
                        itemList.remove(index);
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
        });

    }

    private void chooseList()
    {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(CHOOSE_LIST, path);
            setResult(RESULT_OK, resultIntent);
            finish();
    }
}
