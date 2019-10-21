package com.example.smartspacesblindshopping;

import java.util.ArrayList;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

public class Map
{
    //the real life width and height of the room in meters
    public static final double ROOM_WIDTH = 8;
    public static final double ROOM_HEIGHT = 5;

    public static final int SECTIONS_PER_SHELF = 3;

    //this assumes a map size of width 400, height 700,
    // with aisles being 100 wide and 500 long,
    // shelves being 50 wide and 500 long
    // and rows being 100 wide and 700 long

    public static ArrayList<RectF> aisles;
    public static ArrayList<RectF> rows;
    public static ArrayList<Shelf> shelves;
    public static ArrayList<Node> nodes;
    public static User user = new User(4,2.5, 2);
    public static Item item;

    public static void init()
    {
        aisles = new ArrayList<>();
        //aisle 0 (leftmost)
        aisles.add(new RectF(0, 0, 1.67f, 5));
        //aisle 1 (middle)
        aisles.add(new RectF(2.95f, 0, 5, 5));
        //aisle 2 (rightmost)
        aisles.add(new RectF(6.32f, 0, 8, 5));

        rows = new ArrayList<>();
        //row 0 (top)
        rows.add(new RectF(0,4.5f, 8,3.5f));
        //row 1 (bottom)
        rows.add(new RectF(0, 0.9f, 8, 0));

        shelves = new ArrayList<>();
        //shelf 0
        RectF shelfRect = new RectF(aisles.get(0).right, rows.get(0).bottom, (aisles.get(0).right + (aisles.get(1).left - aisles.get(0).right)/2), rows.get(1).top);
        shelves.add(new Shelf(0, 0, true, SECTIONS_PER_SHELF, 2, shelfRect));

        shelfRect = new RectF((aisles.get(0).right + (aisles.get(1).left - aisles.get(0).right)/2), rows.get(0).bottom, aisles.get(1).left, rows.get(1).top);
        shelves.add(new Shelf(1, 1, false, SECTIONS_PER_SHELF, 2,  shelfRect));

        shelfRect = new RectF(aisles.get(1).right, rows.get(0).bottom, (aisles.get(1).right + (aisles.get(2).left - aisles.get(1).right)/2), rows.get(1).top);
        shelves.add(new Shelf(2, 1, true, SECTIONS_PER_SHELF, 2, shelfRect));

        shelfRect = new RectF((aisles.get(1).right + (aisles.get(2).left - aisles.get(1).right)/2), rows.get(0).bottom, aisles.get(2).left, rows.get(1).top);
        shelves.add(new Shelf(3, 2, false, SECTIONS_PER_SHELF, 2, shelfRect));

        item = new Item("", "Coffee 150g", 2, 0, 0);

        //initialise direction nodes
        //assumes all shelves are level and the same length
        //could be modified to work with multiple rows of shelves in the future
        nodes = new ArrayList<>();
        for(int i = 0; i < aisles.size(); i++)
        {
            double nodeXPos = aisles.get(i).left + ((aisles.get(i).right - aisles.get(i).left)/2);
            //nodes in the middle of the aisles, one for each section
            for(int j = 0; j < SECTIONS_PER_SHELF; j++)
            {
                double nodeYPos = shelves.get(0).getRect().top + (j+0.5)*shelves.get(i).getSectionWidth();
                nodes.add(new Node(nodeXPos, nodeYPos, false));
            }
            //nodes at the top and bottom of the aisles that join to the rows
            for(int j = 0; j < rows.size(); j++)
            {
                double nodeYPos = rows.get(j).top - ((rows.get(j).top - rows.get(j).bottom)/2);
                nodes.add(new Node(nodeXPos, nodeYPos, false));
            }
        }
        //add the exit node
        double nodeXPos = aisles.get(aisles.size()-1).left + ((aisles.get(aisles.size()-1).right - aisles.get(aisles.size()-1).left)/2);
        double nodeYPos = rows.get(0).top - shelves.get(0).getSectionWidth()/2;
        nodes.add(new Node(nodeXPos, nodeYPos, true));
    }
}
