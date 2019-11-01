package com.example.smartspacesblindshopping;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;

import java.util.ArrayList;

public class MapActivity extends MyActivity {

    //CONSTANTS

    //number of milliseconds to wait before repeating direction checks
    public static final int REPEAT_DELAY = 10000;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //initialise the drawView
        drawView = new DrawView(this,getWindowManager().getDefaultDisplay());
        drawView.setBackgroundColor(Color.DKGRAY);
        setContentView(drawView);

        //initialise the display object and calculate relevant variables from this
        display = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        MapActivity.display.getSize(displaySize);



        PIXELS_PER_METER = displaySize.x / Map.ROOM_HEIGHT;

        drawView.updateView();

        //Map.addBlockage(7, 12);


        started = true;
        handler.postDelayed(runnable, 1000);



    }
    private boolean started = false;
    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run()
        {
            drawView.updateView();


            ArrayList<Node> path = Directions.currentPath;
            if(path != null && path.size()>1)
            {
                Log.d("path", "Path from "+path.get(0)+" to "+path.get(path.size()-1)+": "+path);
                Log.d("direction", ""+Directions.pathToString());
                //TTSHandler.speak(Directions.pathToString());
            }




            if(started)
            {
                start();
            }
        }
    };

    public void stop() {
        started = false;
        handler.removeCallbacks(runnable);
    }

    public void start() {
        started = true;
        handler.postDelayed(runnable, REPEAT_DELAY);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

    public void userScansTag(String tag)
    {
        Item item = firebase.getItemByNFCTag(tag);
        Map.user.setX(Directions.getClosestNode(Map.getItemXCoord(Map.item), Map.getItemYCoord(Map.item), true).getXPosition());
        Map.user.setY(Directions.getClosestNode(Map.getItemXCoord(Map.item), Map.getItemYCoord(Map.item), true).getYPosition());
        if(Map.getItemXCoord(Map.item) < Map.user.getX())
        {
            Map.user.setFacing(3);
        }
        else if(Map.getItemXCoord(Map.item) > Map.user.getX())
        {
            Map.user.setFacing(1);
        }
        else
        {
            Log.e("Direction error", "nearest node's xpos = scanned item's xpos");
        }
    }
}
