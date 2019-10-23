package com.example.smartspacesblindshopping;

import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;

public class Node
{
    private double xPosition;
    private double yPosition;
    private boolean exit;
    private ArrayList<Edge> edges;
    private RectF rect;
    private static final float NODE_AREA_WIDTH = 2f;
    private static final float NODE_AREA_HEIGHT = 0.5f;
    private boolean pathNode = false;
    private int aisle = -1;
    private int row = -1;

    public Node(double xPosition, double yPosition, boolean exit)
    {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.exit = exit;

        this.rect = new RectF((float) xPosition-(NODE_AREA_WIDTH/2), (float) yPosition-(NODE_AREA_HEIGHT/2),
                (float) xPosition+(NODE_AREA_WIDTH/2),(float) yPosition+(NODE_AREA_HEIGHT/2));

        edges = new ArrayList<>();

        //set the aisle this node is in
        for(int i = 0; i < Map.aisles.size(); i++)
        {
            if(Map.aisles.get(i).contains((float) this.xPosition, (float) this.yPosition))
            {
                this.aisle = i;
                Log.d("Node in aisle",""+this.toString()+" in aisle "+i);
                //break;
            }
        }
        //set the row this node is in
        for(int i = 0; i < Map.rows.size(); i++)
        {
            if(Map.rows.get(i).contains((float) this.xPosition, (float) this.yPosition))
            {
                this.row = i;
                Log.d("Node in row",""+this+" in row "+i);
                break;
            }
        }
    }

    //create the edge to specific node with automatic weight calculation and returns the edge created
    public Edge addEdge(Node neighbour)
    {
        //get city block distance between the two nodes, this distance is the weight
        double distance = Math.abs(neighbour.getXPosition()-this.xPosition)+Math.abs(neighbour.getYPosition()-this.yPosition);

        //find the direction of this edge automatically
        double horizontalDifference = neighbour.getXPosition() - this.xPosition;
        double verticalDifference = neighbour.getYPosition() - this.yPosition;
        int direction;

        if(Math.abs(horizontalDifference) >= Math.abs(verticalDifference))
        {
            if(horizontalDifference > 0) { direction = 1; }
            else if(horizontalDifference < 0) { direction = 3; }
            else
            {
                direction = -1;
                Log.e("direction error", "Edge between "+this+" and "+neighbour+" has no direction (they are in the same place)");
            }
        }
        else
        {
            if( verticalDifference > 0) { direction = 0; }
            else {direction = 2; }
        }

        Edge edge = new Edge(this, neighbour, distance, direction);
        edges.add(edge);
        return edge;
    }
    public void addEdge(Edge edge)
    {
        edges.add(edge);
    }
    public void removeEdge(Edge edge)
    {
        Map.edges.remove(edge);
        edges.remove(edge);
    }
    public void removeEdge(Node neighbour)
    {
        for(int i = 0; i < edges.size(); i++)
        {
            if(edges.get(i).getTo().equals(neighbour))
            {
                Edge edge = edges.get(i);
                Map.edges.remove(edge);
                edges.remove(edge);
            }
        }
    }
    public double getXPosition() { return xPosition; }
    public double getYPosition() { return yPosition; }
    public RectF getRect()
    {
        return this.rect;
    }
    public ArrayList<Edge> getEdges()
    {
        return edges;
    }
    public Edge getEdgeTo(Node node)
    {
        for(int i = 0; i < edges.size(); i++)
        {
            if(edges.get(i).getTo().equals(node))
            {
                return edges.get(i);
            }
        }
        return null;
    }

    public void setPathNode(boolean pathNode)
    {
        this.pathNode = pathNode;
    }
    public  boolean getPathNode()
    {
        return this.pathNode;
    }
    public int getAisle()
    {
        return this.aisle;
    }
    public int getRow()
    {
        return this.row;
    }

    public String toString()
    {
        return ""+Map.nodes.indexOf(this);
    }
}
