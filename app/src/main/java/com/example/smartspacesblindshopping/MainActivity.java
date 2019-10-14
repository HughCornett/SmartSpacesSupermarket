package com.example.smartspacesblindshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map.init();
        Directions.getNextDirection(Map.user, Map.item);
    }


    public void goToList(View view)
    {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }


    public void readList(View view)
    {
        Intent intent = new Intent(this, ReadActivity.class);
        startActivity(intent);

    }
}
