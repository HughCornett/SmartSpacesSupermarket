package com.example.smartspacesblindshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private FirebaseAdapter Firebase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Map.init();
//        Directions.getNextDirection(Map.user, Map.item);

        Firebase = new FirebaseAdapter();

    }
}
