package com.example.smartspacesblindshopping;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Directions {

    public static final int MARGIN_DISTANCE = 2;

    //array of distances from every node to every other node
    public static double[][] distanceMatrix;
    //array of nodes that should be taken to get from one to another
    //(e.g  nextMatrix[0][2] contains the neighbour of node 0 that should be taken to get to node 2
    public static Node[][] nextMatrix;

    //the last node the user was at
    public static Node lastNode = null;

    public static String pathToString(ArrayList<Node> path)
    {
        String turn = "[Error]";
        //the direction the user must face to walk to the next node
        int turnToDirection = path.get(0).getEdgeTo(path.get(1)).getDirection();

        //if the user is facing the right way
        if(turnToDirection == Map.user.getFacing()) { turn = "Walk forward"; }

        //if the user is facing left of the direction they should be
        else if(turnToDirection == (Map.user.getFacing() + 1) % 4) { turn = "Turn right and walk"; }

        //if the user is facing the opposite way they should be
        else if(turnToDirection == (Map.user.getFacing() + 2) % 4) { turn = "Turn around and walk"; }

        //if the user is facing right of the way they should be
        else if(turnToDirection == (Map.user.getFacing() + 3) % 4) { turn = "Turn left and walk"; }

        //else something went wrong
        else { Log.e("Turn error", "Logic to determine turn direction failed (Directions.pathToString())"); }

        int nextTurn = 0;
        while(path.size() > nextTurn + 1 &&
                path.get(nextTurn).getEdgeTo(path.get(nextTurn+1)).getDirection() == turnToDirection)
        {
            nextTurn++;
        }
        String destination = " forward";
        if(path.size() > nextTurn + 1)
        {
            int nextDirection = path.get(nextTurn).getEdgeTo(path.get(nextTurn+1)).getDirection();
            //if the next direction is horizontal (therefore if it is a row)
            if(nextDirection  == 1 || nextDirection == 3)
            {
                destination = " to row "+path.get(nextTurn).getRow();
            }
            //if it is vertical (therefore if it is an aisle)
            else
            {
                destination = " to aisle "+path.get(nextTurn).getAisle();
            }
        }
        return turn+destination;
    }

    //used Floyd Warshall algorithm to compute the distance between all pairs of nodes
    //this is used to find optimal order to get items as well as get paths to items
    public static void computeMatrices()
    {
        //initialise the distanceMatrix to max double values
        //(i.e. assuming very large distances initially)
        distanceMatrix = new double[Map.nodes.size()][Map.nodes.size()];
        for(int i = 0; i < Map.nodes.size(); i++)
        {
            for(int j = 0; j < Map.nodes.size(); j++)
            {
                distanceMatrix[i][j] = Double.MAX_VALUE;
            }
        }
        //initialise nextMatrix to null
        //(i.e. assuming no path between nodes initially)
        nextMatrix = new Node[Map.nodes.size()][Map.nodes.size()];

        //get distances between neighbours and change next accordingly
        for(int i = 0; i < Map.edges.size(); i++)
        {
            int nodeFrom = Map.nodes.indexOf(Map.edges.get(i).getFrom());
            int nodeTo = Map.nodes.indexOf(Map.edges.get(i).getTo());
            distanceMatrix[nodeFrom][nodeTo] = Map.edges.get(i).getWeight();
            nextMatrix[nodeFrom][nodeTo] = Map.edges.get(i).getTo();
        }

        //get distances from each node to itself and set the next node to itself
        for(int i = 0; i < Map.nodes.size(); i++)
        {
            distanceMatrix[i][i] = 0;
            nextMatrix[i][i] = Map.nodes.get(i);
        }

        //k = intermediate node, i = origin node, j = destination node
        //for each combination of origins and destination through each k
        for(int k = 0; k < Map.nodes.size(); k++)
        {
            for(int i = 0; i < Map.nodes.size(); i++)
            {
                for(int j = 0; j < Map.nodes.size(); j++)
                {
                    //if this pair of nodes (i, j) can have a shorter distance by going through k,
                    //set the distance from (i, j) to that distance
                    if(distanceMatrix[i][j] > distanceMatrix[i][k] + distanceMatrix[k][j])
                    {
                        distanceMatrix[i][j] = distanceMatrix[i][k] + distanceMatrix[k][j];
                        nextMatrix[i][j] = nextMatrix[i][k];
                    }
                }
            }
        }

        /*print the distance matrix
        Log.d("distance matrix",""+distanceMatrix);
        for(int i = 0; i < distanceMatrix.length; i++)
        {
            Log.d("row",""+i);
            for(int j = 0; j < distanceMatrix[i].length; j++)
            {
                Log.d("n",""+j+": "+distanceMatrix[i][j]);
            }
        }

        //print the next matrix
        Log.d("next matrix",""+nextMatrix);
        for(int i = 0; i < nextMatrix.length; i++)
        {
            Log.d("row",""+i);
            for(int j = 0; j < nextMatrix[i].length; j++)
            {
                Log.d("n",""+j+": "+Map.nodes.indexOf(nextMatrix[i][j]));
            }
        }
         */
    }

    //gets an arrayList of nodes that is the shortest path from one node to another
    public static ArrayList<Node> getPath(Node origin, Node destination)
    {
        //initialise path
        ArrayList<Node> path = new ArrayList<>();

        //get indexes of nodes
        int originIndex = Map.nodes.indexOf(origin);
        int destinationIndex = Map.nodes.indexOf(destination);
        //if there is no route, return blank and log error
        if(nextMatrix[originIndex][destinationIndex] == null)
        {
            Log.e("path not found", "no path found between"+originIndex+", "+destinationIndex);
            return path;
        }
        Node currentNode = origin;
        path.add(currentNode);
        currentNode.setPathNode(true);
        //while not at the destination
        while(!currentNode.equals(destination))
        {
            currentNode = nextMatrix[Map.nodes.indexOf(currentNode)][destinationIndex];
            path.add(currentNode);
            currentNode.setPathNode(true);
            currentNode.getEdgeTo(path.get(path.size()-2)).setPathEdge(true);
            path.get(path.size()-2).getEdgeTo(currentNode).setPathEdge(true);
        }

        return path;
    }

    private static double distance(double x0, double y0, double x1, double y1)
    {
        return Math.sqrt(Math.pow(x1-x0,2)+Math.pow(y1-y0,2));
    }

    /**
     *
     * @param x
     *  the x coordinate of the point we are getting the closest node to
     * @param y
     *  the y coordinate of the point we are getting the closest node to
     * @param aisle
     *  true if the node must be in an aisle, false if it doesn't matter
     * @return
     *  the closest appropriate node to the point
     */
    public static Node getClosestNode(double x, double y, boolean aisle)
    {
        double minDistance = Double.MAX_VALUE;
        Node closestNode = null;
        for(int i = 0; i < Map.nodes.size(); i++)
        {
            Node node = Map.nodes.get(i);
            double thisDistance = distance(x, y, node.getXPosition(), node.getYPosition());
            if(thisDistance < minDistance && !(aisle && node.getAisle() == -1))
            {
                minDistance = thisDistance;
                closestNode = node;
            }
        }

        return closestNode;
    }
}
