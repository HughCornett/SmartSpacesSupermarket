package com.example.smartspacesblindshopping;

import android.graphics.Point;

import java.util.ArrayList;

public class Node
{
    private double xPosition;
    private double yPosition;
    private boolean exit;
    private ArrayList<Node> neighbours;

    public Node(double xPosition, double yPosition, boolean exit)
    {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.exit = exit;
    }

    public void addNeighbour(Node neighbour)
    {
        neighbours.add(neighbour);
    }
    public void removeNeighbour(Node node)
    {
        neighbours.remove(node);
    }
    public double getXPosition()
    {
        return xPosition;
    }
    public double getYPosition()
    {
        return yPosition;
    }
}
