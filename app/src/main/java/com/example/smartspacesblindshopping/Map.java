package com.example.smartspacesblindshopping;

import java.util.ArrayList;

import android.graphics.Point;
import android.graphics.Rect;

public class Map
{
    //the real life width and height of the room in meters
    public static final double ROOM_WIDTH = 100;
    public static final double ROOM_HEIGHT = 56.25;

    //this assumes a map size of width 400, height 700,
    // with aisles being 100 wide and 500 long,
    // shelves being 50 wide and 500 long
    // and rows being 100 wide and 700 long

    public static ArrayList<Rect> aisles;
    public static ArrayList<Rect> rows;
    public static User user = new User(80,11, 2);
    public static Item item = new Item(new Point(74,10), 2);

    public static void init()
    {
         aisles = new ArrayList<>();
         //aisle 0 (leftmost)
         aisles.add(new Rect(0, 0, 25, 57));
         //aisle 1 (middle)
         aisles.add(new Rect(37, 0, 63, 57));
         //aisle 2 (rightmost)
         aisles.add(new Rect(75, 0, 100, 57));

         rows = new ArrayList<>();
         //row 0 (bottom)
         rows.add(new Rect(0, 8, 100, 0));
         //row 1 (top)
         rows.add(new Rect(0, 57, 100,49));
    }
}
