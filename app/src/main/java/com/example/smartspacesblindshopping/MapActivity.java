package com.example.smartspacesblindshopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class MapActivity extends MyActivity {

    //OTHER VARIABLES
    DrawView drawView;

    //Constant variables describing screen that are set on app launch
    public static Display display;
    //pixel distances to bottom and left sides of the actual map
    //pixel width and height of the map
    public static int WIDTH;
    public static int HEIGHT;
    //number of pixels per real world meter
    public static double PIXELS_PER_METER;
    protected TextToSpeechHandler TTSHandler ;

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
        Point displaySize = new Point();
        MapActivity.display.getSize(displaySize);



        PIXELS_PER_METER = displaySize.x / Map.ROOM_HEIGHT;

        drawView.updateView();
        Map.init();
        //TTSHandler = new TextToSpeechHandler(getApplicationContext());

        //TTSHandler.speak(Directions.getNextDirection(Map.user, Map.item));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
