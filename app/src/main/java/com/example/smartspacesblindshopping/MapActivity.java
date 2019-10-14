package com.example.smartspacesblindshopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class MapActivity extends AppCompatActivity {

    //OTHER VARIABLES
    DrawView drawView;

    //Constant variables describing screen that are set on app launch
    public static Display display;
    //pixel distances to bottom and left sides of the actual map
    public static int BOTTOM_BORDER;
    public static int LEFT_BORDER;
    //pixel width and height of the map
    public static int WIDTH;
    public static int HEIGHT;
    //number of pixels per real world meter
    public static double PIXELS_PER_METER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //initialise the drawView
        drawView = new DrawView(this);
        drawView.setBackgroundColor(Color.WHITE);
        setContentView(drawView);

        //initialise the display object and calculate relevant variables from this
        display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        MainActivity.display.getSize(size);

        BOTTOM_BORDER = (int) Math.round(size.x*0.11);
        LEFT_BORDER = (int) Math.round(size.y*0.11);
        HEIGHT = size.x - (int) Math.round(size.x*0.19);
        WIDTH = size.y - (int) Math.round(size.y*0.35);
        PIXELS_PER_METER = WIDTH / 50;

        drawView.updateView();
    }
}
