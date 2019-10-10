package com.example.smartspacesblindshopping;

import android.graphics.Point;

public class User
{
    private Point position;

    //direction user is facing: clockwise from 0=up to 4=left
    private int facing;

    public User(Point position, int facing)
    {
        this.position = position;
        this.facing = facing;
    }

    public Point getPosition()
    {
        return position;
    }
    public int getFacing()
    {
        return facing;
    }

    public void setFacing(int facing)
    {
        this.facing = facing;
    }
    public void setPosition(Point position)
    {
        this.position = position;
    }
}
