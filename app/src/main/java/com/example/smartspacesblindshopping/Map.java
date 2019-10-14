package com.example.smartspacesblindshopping;

import java.util.ArrayList;

import android.graphics.Point;
import android.graphics.Rect;

public class Map
{
    //this assumes a map size of width 400, height 700,
    // with aisles being 100 wide and 500 long,
    // shelves being 50 wide and 500 long
    // and rows being 100 wide and 700 long

    public static ArrayList<Rect> aisles;
    public static ArrayList<Rect> rows;
    public static User user = new User(new Point(200,525), 2);
    public static Item item = new Item(new Point(250,550), 1);

    public static final int AISLE_MIDDLE = 350;

    public static void init()
    {
         aisles = new ArrayList<>();
         //aisle 0 (leftmost)
         aisles.add(new Rect(0, 0, 100, 700));
         //aisle 1 (middle)
         aisles.add(new Rect(150, 0, 250, 700));
         //aisle 2 (rightmost)
         aisles.add(new Rect(300, 0, 400, 700));

         rows = new ArrayList<>();
         //row 0 (bottom)
         rows.add(new Rect(0, 600, 400, 700));
         //row 1 (top)
         rows.add(new Rect(0, 0, 400,100));
    }
}
