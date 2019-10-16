package com.example.smartspacesblindshopping;

import android.graphics.Point;

public class User
{
    private double x;
    private double y;

    //direction user is facing: clockwise from 0=up to 4=left
    private int facing;

    public User(double x, double y, int facing)
    {
        this.x = x;
        this.y = y;
        this.facing = facing;
    }

    public double getX()
    {
        return x;
    }
    public double getY()
    {
        return y;
    }

    public int getFacing() { return facing; }

    public void setFacing(int facing) { this.facing = facing; }

    public void setX(double x) { this.x = x; }

    public void setY(double y) { this.y = y; }
}
