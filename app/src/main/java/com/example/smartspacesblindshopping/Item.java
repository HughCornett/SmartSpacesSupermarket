package com.example.smartspacesblindshopping;

import android.graphics.Point;

public class Item
{
    private int id;
    private String name;
    private int aisle;
    private int shelf;
    private int section;
    private Point position;


    //for testing, before shelf/section/aisle layout is sorted out
    public Item(Point position, int aisle)
    {
        this.position = position;
        this.aisle = aisle;
    }

    public Item(int id, String name, int aisle, int shelf, int section)
    {
        this.id = id;
        this.name = name;
        this.aisle = aisle;
        this.shelf = shelf;
        this.section = section;

        //TODO work out position from this information?
        //or maybe that's not needed
    }

    public int getAisle()
    {
        return this.aisle;
    }
    public Point getPosition()
    {
        return this.position;
    }
}
