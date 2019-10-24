package com.example.smartspacesblindshopping;

import android.util.DebugUtils;
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
        if(turnToDirection == Map.user.getFacing()) { turn = "Walk forward "; }

        //if the user is facing left of the direction they should be
        else if(turnToDirection == (Map.user.getFacing() + 1) % 4) { turn = "Turn right and walk "; }

        //if the user is facing the opposite way they should be
        else if(turnToDirection == (Map.user.getFacing() + 2) % 4) { turn = "Turn around and walk "; }

        //if the user is facing right of the way they should be
        else if(turnToDirection == (Map.user.getFacing() + 3) % 4) { turn = "Turn left and walk "; }

        //else something went wrong
        else { Log.e("Turn error", "Logic to determine turn direction failed (Directions.pathToString())"); }

        int nodesUntilNextTurn = 0;
        while(path.size() > nodesUntilNextTurn + 1 &&
                path.get(nodesUntilNextTurn).getEdgeTo(path.get(nodesUntilNextTurn+1)).getDirection() == turnToDirection)
        {
            nodesUntilNextTurn++;
        }
        String destination = "[Error]";
        String nextTurn = "[Error] ";
        if(path.size() > nodesUntilNextTurn + 1)
        {
            int nextDirection = path.get(nodesUntilNextTurn).getEdgeTo(path.get(nodesUntilNextTurn+1)).getDirection();

            //figure out which way to turn once at destination
            //if the user will be facing left of the direction they should be
            if(nextDirection == (turnToDirection + 1) % 4) { nextTurn = "then turn right"; }

            //if the user will be facing right of the way they should be
            else if(nextDirection == (turnToDirection + 3) % 4) { nextTurn = "then turn left"; }

            else { Log.e("Turn error", "User needs to turn around or not turn at next turn"); }

            //if the next direction is horizontal (therefore if it is a row)
            int turnIn = 0;
            if(nextDirection  == 1 || nextDirection == 3)
            {
                for(int i = 1; i < nodesUntilNextTurn; i++)
                {
                    if(path.get(i).getRow() != -1)
                    {
                        turnIn++;
                    }
                }
                if(turnIn == 0) { destination = "to the next row, "; }
                else { destination = (turnIn+1)+" rows, "; }
            }
            //if it is vertical (therefore if it is an aisle)
            else
            {
                for(int i = 1; i < nodesUntilNextTurn; i++)
                {
                    if(path.get(i).getAisle() != -1)
                    {
                        turnIn++;
                    }
                }
                if(turnIn == 0) { destination = "to the next aisle, "; }
                else { destination = (turnIn+1)+" aisles, "; }
            }
        }
        else
        {
            //gets the distance to the item rounded to 1 decimal place
            double distance = (double) Math.round(Math.abs(Map.user.getY() - Map.item.getYPosition()) * 10) / 10;
            destination = distance+" meters. ";
            //if the item is left of its node
            if(Map.item.getXPosition() < path.get(path.size()-1).getXPosition())
            {
                //if the user will be facing up
                if(path.get(path.size()-2).getEdgeTo(path.get(path.size()-1)).getDirection() == 0)
                {
                    nextTurn = "Your item is on the left";
                }
                //if the user will be facing down
                else if(path.get(path.size()-2).getEdgeTo(path.get(path.size()-1)).getDirection() == 2)
                {
                    nextTurn = "Your item is on the right";
                }
                //if the user will be facing left or right (this should never happen)
                else { Log.e("Turn error", "Item is neither left or right of the final node"); }
            }
            //if the item is right of its node
            else
            {
                //if the user will be facing up
                if(path.get(path.size()-2).getEdgeTo(path.get(path.size()-1)).getDirection() == 0)
                {
                    nextTurn = "Your item is on the left";
                }
                //if the user will be facing down
                else if(path.get(path.size()-2).getEdgeTo(path.get(path.size()-1)).getDirection() == 2)
                {
                    nextTurn = "Your item is on the right";
                }
                //if the user will be facing left or right (this should never happen)
                else { Log.e("Turn error", "Item is neither left or right of the final node"); }
            }
        }
        return turn+" | "+destination+" | "+nextTurn;
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

    /**
     * Gets an arrayList of nodes that is the shortest path from one node to another
     * @param origin
     *  the first/original node
     * @param destination
     *  the final/destination node
     * @return
     *  an ArrayList of nodes that is the shortest path from the origin to the destination
     */
    public static ArrayList<Node> getPath(Node origin, Node destination)
    {
        //initialise path
        ArrayList<Node> path = new ArrayList<>();

        //get indexes of nodes in the main list of nodes
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

    /**
     * Gets the closest item to the user
     * @param user
     *  The user of interest
     * @param items
     *  A list of items we are getting the closest from
     * @return
     *  The item that is closest
     */
    public Item getClosestItem(User user, ArrayList<Item> items)
    {
        //
        double minDistance = Double.MAX_VALUE;
        Item closestItem = null;
        for(Item item: items)
        {
            Node itemNode = getClosestNode(item.getXPosition(), item.getYPosition(), true);
            Node userNode = getClosestNode(user.getX(), user.getY(), false);
            double thisDistance = distanceMatrix[Map.nodes.indexOf(userNode)][Map.nodes.indexOf(itemNode)];
            if(thisDistance < minDistance)
            {
                minDistance = thisDistance;
                closestItem = item;
            }
        }
        return closestItem;
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
