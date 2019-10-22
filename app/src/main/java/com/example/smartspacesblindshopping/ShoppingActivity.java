package com.example.smartspacesblindshopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ShoppingActivity extends MyActivity {

    ArrayList<String> shoppingList = new ArrayList<>();
    TextView currentItem;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        currentItem = (TextView) findViewById(R.id.currentItem);
    }

    public void readLists(View view)
    {
        Intent intent = new Intent(this, ReadActivity.class);
        startActivityForResult(intent, 10);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10)
        {
            if (resultCode == RESULT_OK && data != null) {
                shoppingList.addAll(ReadWriteCSV.readCSV(getApplicationContext(), data.getStringExtra(CHOOSE_LIST)));
                currentItem.setText(shoppingList.get(0));

            }
        }

    }
}
