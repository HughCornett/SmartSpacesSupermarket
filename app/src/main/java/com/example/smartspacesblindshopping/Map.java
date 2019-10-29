package com.example.smartspacesblindshopping;

import java.util.ArrayList;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class Map extends MapActivity
{
    //the real life width and height of the room in meters
    public static final double ROOM_WIDTH = 5.6;
    public static final double ROOM_HEIGHT = 3;

    public static final int SECTIONS_PER_SHELF = 3;

    public static ArrayList<RectF> aisles;
    public static ArrayList<RectF> rows;
    public static ArrayList<Shelf> shelves;

    public static ArrayList<Node> nodes;
    public static ArrayList<Edge> edges;
    public static Node exit;

    public static User user = new User(0,1, 2);
    public static ArrayList<Item> items = new ArrayList<>();
    public static Item item;

    public static void init()
    {
        aisles = new ArrayList<>();
        //aisle 0 (leftmost)
        aisles.add(new RectF(0, 0, 1.9f, 3));
        //aisle 1 (middle)
        aisles.add(new RectF(2.7f, 0, 3.8f, 3));
        //aisle 2 (rightmost)
        aisles.add(new RectF(4.6f, 0, 5.6f, 3));

        rows = new ArrayList<>();
        //row 0 (top)
        rows.add(new RectF(0,2f, 5.6f,3f));
        //row 1 (bottom)
        rows.add(new RectF(0, 0f, 5.6f, 0.8f));

        //here top and bottom of rows seem swapped, this is because of various reasons like
        //the screen being sideways. so the bottom of a row is higher of the screen than the top
        shelves = new ArrayList<>();
        //shelf 0
        RectF shelfRect = new RectF(aisles.get(0).right, rows.get(0).top, (aisles.get(0).right + (aisles.get(1).left - aisles.get(0).right)/2), rows.get(1).bottom);
        shelves.add(new Shelf(0, 0, true, SECTIONS_PER_SHELF, 2, shelfRect));

        //shelf 1
        shelfRect = new RectF((aisles.get(0).right + (aisles.get(1).left - aisles.get(0).right)/2), rows.get(0).top, aisles.get(1).left, rows.get(1).bottom);
        shelves.add(new Shelf(1, 1, false, SECTIONS_PER_SHELF, 2,  shelfRect));

        //shelf 2
        shelfRect = new RectF(aisles.get(1).right, rows.get(0).top, (aisles.get(1).right + (aisles.get(2).left - aisles.get(1).right)/2), rows.get(1).bottom);
        shelves.add(new Shelf(2, 1, true, SECTIONS_PER_SHELF, 2, shelfRect));

        //shelf 3
        shelfRect = new RectF((aisles.get(1).right + (aisles.get(2).left - aisles.get(1).right)/2), rows.get(0).top, aisles.get(2).left, rows.get(1).bottom);
        shelves.add(new Shelf(3, 2, false, SECTIONS_PER_SHELF, 2, shelfRect));

        item = new Item("", "Coffee 150g", 3, 1, 0);

        //initialise direction nodes
        //assumes all shelves are level and the same length
        //could be modified to work with multiple rows of shelves in the future
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        Node newNode;
        for(int i = 0; i < aisles.size(); i++)
        {
            double nodeXPos = aisles.get(i).left + ((aisles.get(i).right - aisles.get(i).left)/2);

            //add the top of aisle (intersect with row 0)
            double nodeYPos = rows.get(0).bottom - ((rows.get(0).bottom - rows.get(0).top)/2);

            newNode = new Node(nodeXPos, nodeYPos, false);
            nodes.add(newNode);
            if(i > 0)
            {
                //add edge to previous node in row

                edges.add(newNode.addEdge(nodes.get(nodes.size()-3)));
                edges.add(nodes.get(nodes.size()-3).addEdge(newNode));
            }

            //nodes in the middle of the aisles, one for each section
            for(int j = 0; j < SECTIONS_PER_SHELF; j++)
            {
                nodeYPos = shelves.get(0).getRect().top + (j+0.5)*shelves.get(i).getSectionWidth();
                newNode = new Node(nodeXPos, nodeYPos, false);
                nodes.add(newNode);
                edges.add(newNode.addEdge(nodes.get(nodes.size()-2)));
                edges.add(nodes.get(nodes.size()-2).addEdge(newNode));
            }
            //add the bottom of the aisle (intersection with row 1)
            nodeYPos = rows.get(1).bottom - ((rows.get(1).bottom - rows.get(1).top)/2);
            newNode = new Node(nodeXPos, nodeYPos, false);
            nodes.add(newNode);
            //add the previous node in the aisle
            edges.add(newNode.addEdge(nodes.get(nodes.size()-2)));
            edges.add(nodes.get(nodes.size()-2).addEdge(newNode));
            if(i > 0)
            {
                //add the previous node of the row
                edges.add(newNode.addEdge(nodes.get(nodes.size()-6)));
                edges.add(nodes.get(nodes.size()-6).addEdge(newNode));
            }

            //if there is another aisle after this, add nodes between them on the rows
            if(i != aisles.size()-1)
            {
                nodeXPos = aisles.get(i).right + ((aisles.get(i+1).left - aisles.get(i).right)/2);
                nodeYPos = rows.get(0).bottom - ((rows.get(0).bottom - rows.get(0).top)/2);

                newNode = new Node(nodeXPos, nodeYPos, false);
                nodes.add(newNode);
                edges.add(newNode.addEdge(nodes.get(nodes.size()-6)));
                edges.add(nodes.get(nodes.size()-6).addEdge(newNode));

                nodeYPos = rows.get(1).bottom - ((rows.get(1).bottom - rows.get(1).top)/2);

                newNode = new Node(nodeXPos, nodeYPos, false);
                nodes.add(newNode);
                edges.add(newNode.addEdge(nodes.get(nodes.size()-3)));
                edges.add(nodes.get(nodes.size()-3).addEdge(newNode));
            }
        }
        //add the exit node
        double nodeXPos = 0;
        double nodeYPos = rows.get(1).bottom - ((rows.get(1).bottom-rows.get(1).top)/2);
        newNode = new Node(nodeXPos, nodeYPos, true);
        nodes.add(newNode);
        //add the neighbour
        edges.add(newNode.addEdge(nodes.get(4)));
        edges.add(nodes.get(4).addEdge(newNode));
        exit = newNode;
    }

    public static double getItemXCoord(Item item)
    {
        Shelf shelf = Map.shelves.get(item.getShelf());
        return shelf.getRect().left + ((shelf.getRect().right - shelf.getRect().left)/2);
    }
    public static double getItemYCoord(Item item)
    {
        Shelf shelf = Map.shelves.get(item.getShelf());

        return shelf.getRect().top + (item.getSection() * shelf.getSectionWidth());
    }

    public static void addBlockage(Node first, Node second)
    {
        first.removeEdge(second);
        second.removeEdge(first);

        Directions.computeMatrices();
        Directions.setCurrentPath(Map.user, Directions.nextItem);
    }
    public static void addBlockage(int first, int second)
    {
        nodes.get(first).removeEdge(nodes.get(second));
        nodes.get(second).removeEdge(nodes.get(first));

        Directions.computeMatrices();
        Directions.setCurrentPath(Map.user, Directions.nextItem);
    }
    public static void resetPathNodes()
    {
        for(Node node: nodes)
        {
            node.setPathNode(false);
        }
        for(Edge edge: edges)
        {
            edge.setPathEdge(false);
        }
    }

    public static void nextItem()
    {
        items.remove(Directions.nextItem);
        Directions.setCurrentPath(user, Directions.getClosestItem(user, items));
    }

}
