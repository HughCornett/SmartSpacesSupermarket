package com.example.smartspacesblindshopping;

public class Edge {
    private Node from;
    private Node to;
    private double weight;
    //0 = up, 1 = right, 2 = down, 3 = left
    private int direction;
    private boolean pathEdge = false;

    public Edge(Node from, Node to, double weight, int direction)
    {
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.direction = direction;
    }

    public Node getFrom()
    {
        return from;
    }
    public Node getTo()
    {
        return to;
    }
    public double getWeight()
    {
        return weight;
    }
    public int getDirection()
    {
        return direction;
    }
    public void setPathEdge(boolean pathEdge)
    {
        this.pathEdge = pathEdge;
    }
    public boolean getPathEdge()
    {
        return pathEdge;
    }

    public String toString()
    {
        return "Edge from "+from+" to "+to+" w/ weight "+weight;
    }
}
