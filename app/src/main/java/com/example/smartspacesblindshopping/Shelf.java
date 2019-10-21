package com.example.smartspacesblindshopping;

import android.graphics.Rect;
import android.graphics.RectF;

public class Shelf {

    //the shelf number
    private int shelfNumber;

    //the aisle number this shelf is on and the side of the aisle (0=left,1=right)
    private int aisle;
    private boolean rightAisleSide;

    //number of sections and levels this shelf has
    private int numberOfSections;
    private int numberOfLevels;
    private double sectionWidth;

    private RectF rect;

    public Shelf(int shelfNumber, int aisle, boolean rightAisleSide, int numberOfSections, int numberOfLevels, RectF rect)
    {
        this.shelfNumber = shelfNumber;
        this.aisle = aisle;
        this.rightAisleSide = rightAisleSide;
        this.numberOfSections = numberOfSections;
        this.numberOfLevels = numberOfLevels;
        this.rect = rect;

        sectionWidth = (rect.bottom - rect.top) / numberOfSections;
    }

    public int getShelfNumber() { return this.shelfNumber; }
    public int getAisle() { return aisle; }
    public boolean getRightAisleSide() { return rightAisleSide; }
    public int getNumberOfSections() { return numberOfSections; }
    public int getNumberOfLevels() { return numberOfLevels; }
    public RectF getRect() { return rect; }

    public double getSectionWidth() {
        return sectionWidth;
    }
}
