package com.example.smartspacesblindshopping;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;

public class MainActivity extends MyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        switchCallback(new String[]{"create a new list", "read shopping lists","Go to the Map", "Do your shopping"});

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void goToList(View view) {

        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }


    public void readList(View view) {
        Intent intent = new Intent(this, ReadActivity.class);
        startActivity(intent);

    }

    public void goToMap(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void doShopping(View view)
    {
        Intent intent = new Intent(this, ShoppingActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void chooseOption(int index) {
        super.chooseOption(index);

        switch (index) {
            case 0:
                MainActivity.this.goToList(findViewById(R.id.createButton));
                break;
            case 1:
                MainActivity.this.readList(findViewById(R.id.readButton));
                break;
            case 3:
                MainActivity.this.goToMap(findViewById(R.id.mapButton));
                break;
            case 4:
                MainActivity.this.doShopping(findViewById(R.id.shoppingButton));
                break;
            default:
                break;
        }
    }
}
