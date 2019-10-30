package com.example.smartspacesblindshopping;

import android.util.DebugUtils;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Directions {

    //array of distances from every node to every other node
    public static double[][] distanceMatrix;
    //array of nodes that should be taken to get from one to another
    //(e.g  nextMatrix[0][2] contains the neighbour of node 0 that should be taken to get to node 2
    public static Node[][] nextMatrix;

    public static ArrayList<Node> currentPath;
    public static ArrayList<Node> currentPathTurns;
    public static int currentPathTurnsPos;
    public static boolean exiting = false;
    public static Item currentItem;

    //uses Floyd Warshall algorithm to compute the distance between all pairs of nodes
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
    }

    /**
     * Gets an arrayList of nodes that is the shortest path from one node to another
     * @param user
     *  the user to create a path from
     * @param destination
     *  the node to create a path to
     * @return
     *  an ArrayList of nodes that is the shortest path from the origin to the destination
     */
    public static void setCurrentPathNode(User user, Node destination)
    {
        if(destination.getExit())
        {
            exiting = true;
        }
        else
        {
            exiting = false;
        }
        Map.resetPathNodes();

        Node origin = getClosestNode(user.getX(), user.getY(), false);

        //initialise path
        currentPath = new ArrayList<>();

        //get indexes of nodes in the main list of nodes
        int originIndex = Map.nodes.indexOf(origin);
        int destinationIndex = Map.nodes.indexOf(destination);

        //if there is no route, return blank and log error
        if(nextMatrix[originIndex][destinationIndex] == null)
        {
            Log.e("path not found", "no path found between"+originIndex+", "+destinationIndex);
        }
        Node currentNode = origin;
        currentPath.add(currentNode);
        currentNode.setPathNode(true);
        //while not at the destination
        while(!currentNode.equals(destination))
        {
            currentNode = nextMatrix[Map.nodes.indexOf(currentNode)][destinationIndex];
            currentPath.add(currentNode);
            currentNode.setPathNode(true);
            currentNode.getEdgeTo(currentPath.get(currentPath.size()-2)).setPathEdge(true);
            currentPath.get(currentPath.size()-2).getEdgeTo(currentNode).setPathEdge(true);
        }

        setCurrentPathTurns();
    }

    public static void setCurrentPath(User user, Item item)
    {
        currentItem = item;
        Node node = getClosestNode(Map.getItemXCoord(item), Map.getItemYCoord(item), true);
        setCurrentPathNode(user, node);
    }

    public static void setCurrentPathTurns()
    {
        currentPathTurns = new ArrayList<>();
        for(int i = 0; i < currentPath.size(); i++)
        {
            //if this is the first or last node of the currentPath, it must be in the list
            if(i == 0 || i == currentPath.size()-1)
            {
                currentPathTurns.add(currentPath.get(i));
            }
            //if this is a node where there is a turn
            //(so if the direction from the previous node to this one is different to
            //the direction from this node to the next one)
            else if(currentPath.get(i).getEdgeTo(currentPath.get(i+1)).getDirection() != currentPath.get(i-1).getEdgeTo(currentPath.get(i)).getDirection())
            {
                currentPathTurns.add(currentPath.get(i));
            }
        }
        //should usually be 0 because this method will usually only be run
        // when a path has just been created from the user
        currentPathTurnsPos = currentPathTurns.indexOf(getClosestNode(Map.user.getX(), Map.user.getY(), false));
    }

    /**
     * converts the current path to a string of instructions on what to do next
     * to get to the destination
     *
     * @return instructions on what to do next on the path
     */
    public static String pathToString()
    {
        String turn = "[Error]";

        //get direction the user must face to walk to the next node

        int turnToDirection;
        //if there is more than one node in the path (so if not already at destination)
        if (currentPath.size() > 1) {
            //turn to the next node
            turnToDirection = currentPath.get(0).getEdgeTo(currentPath.get(1)).getDirection();
        }
        //if already at last node
        else
        {
            if(exiting)
            {
                //face the exit, which is always direction 3 of the user when at the last node
                turnToDirection = 3;
            }
            else
            {
                //get the direction the user must face
                turnToDirection = Map.userFaceItem(Map.user, currentItem);
            }

        }
        //if the user is facing the right way
        if(turnToDirection == Map.user.getFacing())
        {
            turn = "Walk forward ";
        }
        //if the user is facing left of the direction they should be
        else if(turnToDirection == (Map.user.getFacing() + 1) % 4)
        {
            turn = "Turn right and walk ";
        }

        //if the user is facing the opposite way they should be
        else if(turnToDirection == (Map.user.getFacing() + 2) % 4)
        {
            turn = "Turn around and walk ";
        }

        //if the user is facing right of the way they should be
        else if(turnToDirection == (Map.user.getFacing() + 3) % 4)
        {
            turn = "Turn left and walk ";
        }

        //else something went wrong
        else
        {
            Log.e("Turn error", "Logic to determine turn direction failed (Directions.pathToString())");
        }

        int nodesUntilNextTurn = 0;
        while(currentPath.size() > nodesUntilNextTurn + 1 &&
                currentPath.get(nodesUntilNextTurn).getEdgeTo(currentPath.get(nodesUntilNextTurn+1)).getDirection() == turnToDirection)
        {
            nodesUntilNextTurn++;
        }


        String destination = "[Error]";
        String nextTurn = "[Error] ";
        if(currentPath.size() > nodesUntilNextTurn + 1)
        {
            int nextDirection = currentPath.get(nodesUntilNextTurn).getEdgeTo(currentPath.get(nodesUntilNextTurn+1)).getDirection();

            //figure out which way to turn once at destination
            //if the user will be facing left of the direction they should be
            if(nextDirection == (turnToDirection + 1) % 4)
            {
                nextTurn = "then turn right";
            }

            //if the user will be facing right of the way they should be
            else if(nextDirection == (turnToDirection + 3) % 4)
            {
                nextTurn = "then turn left";
            }

            else
            {
                Log.e("Turn error", "User needs to turn around or not turn at next turn");
            }

            //if the next direction is horizontal (therefore if it is a row)
            int turnIn = 0;
            if(nextDirection  == 1 || nextDirection == 3)
            {
                for(int i = 1; i < nodesUntilNextTurn; i++)
                {
                    if(currentPath.get(i).getRow() != -1)
                    {
                        turnIn++;
                    }
                }
                if(turnIn == 0)
                {
                    destination = "to the next row, ";
                }
                else
                {
                    destination = (turnIn+1)+" rows, ";
                }
            }
            //if it is vertical (therefore if it is an aisle)
            else
            {
                for(int i = 1; i < nodesUntilNextTurn; i++)
                {
                    if(currentPath.get(i).getAisle() != -1)
                    {
                        turnIn++;
                    }
                }
                if(turnIn == 0)
                {
                    destination = "to the next aisle, ";
                }
                else
                {
                    destination = (turnIn+1)+" aisles, ";
                }
            }
        }
        else
        {
            //distance is number of mid-aisle floor markers to walk.
            //in the demo supermarket, there is only one in each aisle.
            //in a real supermarket, there would be more
            int distance  = 1;
            if(distance == 1)
            {
                destination = " to the next intersection, ";
            }
            else
            {
                destination = distance+" intersections, ";
            }
            if(currentPath.size()>1) {
                //if the item is left of its node
                if (Map.getItemXCoord(currentItem) < currentPath.get(currentPath.size() - 1).getXPosition()) {

                    //if the user will be facing up
                    if (currentPath.get(currentPath.size() - 2).getEdgeTo(currentPath.get(currentPath.size() - 1)).getDirection() == 0)
                    {
                        nextTurn = "Your item is on the left";
                    }
                    //if the user will be facing down
                    else if (currentPath.get(currentPath.size() - 2).getEdgeTo(currentPath.get(currentPath.size() - 1)).getDirection() == 2)
                    {
                        nextTurn = "Your item is on the right";
                    }
                    //if the user will be facing left or right (this should never happen)
                    else
                    {
                        Log.e("Turn error", "Item is neither left or right of the final node");
                    }
                }
                //if the item is right of its node
                else
                {
                    //if the user will be facing up
                    if (currentPath.get(currentPath.size() - 2).getEdgeTo(currentPath.get(currentPath.size() - 1)).getDirection() == 0)
                    {
                        nextTurn = "Your item is on the left";
                    }
                    //if the user will be facing down
                    else if (currentPath.get(currentPath.size() - 2).getEdgeTo(currentPath.get(currentPath.size() - 1)).getDirection() == 2)
                    {
                        nextTurn = "Your item is on the right";
                    }
                    //if the user will be facing left or right (this should never happen)
                    else
                    {
                        Log.e("Turn error", "Item is neither left or right of the final node");
                    }
                }
            }
        }
        return turn+" | "+destination+" | "+nextTurn;
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
    public static Item getClosestItem(User user, ArrayList<Item> items)
    {
        double minDistance = Double.MAX_VALUE;
        Item closestItem = null;
        for(Item item: items)
        {
            Node itemNode = getClosestNode(Map.getItemXCoord(item), Map.getItemYCoord(item), true);
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

    /**
     * gets the linear distance between two positions
     *
     * @param x0 x-coord of first position
     * @param y0 y-coord of first position
     * @param x1 x-coord of second position
     * @param y1 y-coord of second position
     * @return linear distance between the two positions
     */
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

    /**
     * -Gets the next direction on the path to the next item.
     * -Is called when the user has followed the last instruction and is ready for the next.
     * -Changes the users location to the place they are now at (assuming they followed the instructions)
     * and calculates the path from this new spot to the destination
     */
    public static void nextDirection()
    {
        //if this isn't the last node
        currentPathTurnsPos += 1;
        if(currentPathTurnsPos < currentPathTurns.size()-1)
        {

            Map.user.setX(currentPathTurns.get(currentPathTurnsPos).getXPosition());
            Map.user.setY(currentPathTurns.get(currentPathTurnsPos).getYPosition());
            //get the direction the user will now be facing
            int posInFullPath = currentPath.indexOf(currentPathTurns.get(currentPathTurnsPos));
            Map.user.setFacing(currentPath.get(posInFullPath).getEdgeTo(currentPath.get(posInFullPath+1)).getDirection());

            Directions.setCurrentPath(Map.user, currentItem);
        }
        //if it is
        else
        {
            Map.user.setX(currentPathTurns.get(currentPathTurns.size()-1).getXPosition());
            Map.user.setY(currentPathTurns.get(currentPathTurns.size()-1).getYPosition());
            //get the direction the user will now be facing
            Map.user.setFacing(Map.userFaceItem(Map.user, currentItem));
        }
    }
}
