package com.example.smartspacesblindshopping;

import android.util.Log;

public class Directions {

    public static final int MARGIN_DISTANCE = 50;

    public static String getNextDirection(User user, Item destination)
    {
        String direction =  "";
        String turn = "";

        //get aisle information about origin
        int originAisle = -1;
        for(int i = 0; i < Map.aisles.size(); i++)
        {
            if(Map.aisles.get(i).contains(user.getPosition().x, user.getPosition().y))
            {
                originAisle = i;
            }
        }
        //get row information about origin
        int originRow = -1;
        for(int i = 0; i < Map.rows.size(); i++)
        {
            if(Map.rows.get(i).contains(user.getPosition().x, user.getPosition().y))
            {
                originRow = i;
            }
        }
        if(originAisle == -1 && originRow == -1)
        {
            Log.e("location error","origin not in aisle or row");
        }

        //if the destination is in the same aisle as the user
        if(destination.getAisle() == originAisle)
        {
            //check if the user is within the margin distance of the item
            if(user.getPosition().y < destination.getPosition().y+MARGIN_DISTANCE
            && user.getPosition().y > destination.getPosition().y-MARGIN_DISTANCE)
            {
                //if the item is to the left of the user (map left not user's left)
                if(destination.getPosition().x < user.getPosition().x)
                {
                    if(user.getFacing() == 0) { turn = "on your left"; }
                    else if(user.getFacing() == 1) { turn = "behind you"; }
                    else if(user.getFacing() == 2) { turn = "on your right"; }
                    else if(user.getFacing() == 3) { turn = "straight ahead"; }
                }
                //if the item is to the right of the user (map right not user's right)
                if(destination.getPosition().x > user.getPosition().x)
                {
                    if(user.getFacing() == 0) { turn = "on your right"; }
                    else if(user.getFacing() == 1) { turn = "straight ahead"; }
                    else if(user.getFacing() == 2) { turn = "on your left"; }
                    else if(user.getFacing() == 3) { turn = "behind you"; }
                }
                direction = "The item is "+turn;
                Log.d("direction", ""+direction);
            }

            if(user.getPosition().y > destination.getPosition().y)
            {
                if(user.getFacing() == 1) { turn = "Turn left and "; }
                else if(user.getFacing() == 2) { turn = "Turn around and "; }
                else if(user.getFacing() == 3) { turn = "Turn right and "; }
            }
            else if(user.getPosition().y < destination.getPosition().y)
            {
                if(user.getFacing() == 0) { turn = "Turn around and "; }
                else if(user.getFacing() == 1) { turn = "Turn right and "; }
                else if(user.getFacing() == 3) { turn = "Turn left and "; }
            }
            direction = turn+"walk forwards";
            Log.d("direction", ""+direction);

        }
        //if the user is already in a row
        else if(originRow != -1)
        {
            //get the turn towards the correct aisle

            //if user is left of the destination aisle
            if(user.getPosition().x < Map.aisles.get(destination.getAisle()).left)
            {
                if(user.getFacing() == 0) { turn = "Turn right and "; }
                else if(user.getFacing() == 2) { turn = "Turn left and "; }
                else if(user.getFacing() == 3) { turn = "Turn around and "; }
            }
            //if user is right of the destination aisle
            else if(user.getPosition().x > Map.aisles.get(destination.getAisle()).right)
            {
                if(user.getFacing() == 0) { turn = "Turn left and "; }
                else if (user.getFacing() == 1) { turn = "Turn around and "; }
                else if(user.getFacing() == 2) { turn = "Turn right and "; }
            }
            //this should never be called
            else
            {
                //something is wrong, user was found to not be in the destination aisle,
                // and is not left or right of it either
                Log.e("location error", "user not in aisle, left of it or right of it");
            }
            direction = turn+"walk to aisle "+destination.getAisle();
            Log.d("direction", ""+direction);
        }
        //if the user is in an aisle but not the right one aisle
        else
        {
            //direct them to the correct row
            //the correct row is the one that is closest to origin or destination
            //this method assumes a supermarket with only 2 rows (top and bottom)
            //must be changed if it is to work optimally with 3+ rows
            int bestRow = -1;
            int bestRowDistance = Integer.MAX_VALUE;

            //get the best row to go to
            for(int i = 0; i < Map.rows.size(); i++)
            {
                int thisRowOriginDistance = Math.min(Math.abs(user.getPosition().y - Map.rows.get(i).top), Math.abs(user.getPosition().y - Map.rows.get(i).bottom));
                int thisRowItemDistance = Math.min(Math.abs(destination.getPosition().y - Map.rows.get(i).top), Math.abs(destination.getPosition().y - Map.rows.get(i).bottom));
                int thisRowMinDistance = Math.min(thisRowItemDistance, thisRowOriginDistance);
                if(thisRowMinDistance < bestRowDistance)
                {
                    bestRow = i;
                    bestRowDistance = thisRowMinDistance;
                }
            }

            if(bestRow == 0)
            {
                if(user.getFacing() == 0) {turn = "Turn around and ";}
                else if(user.getFacing() == 1) {turn = "Turn right and ";}
                else if(user.getFacing() == 3) {turn = "Turn left and ";}
            }
            else if(bestRow == 1)
            {
                if(user.getFacing() == 1) {turn = "Turn left and ";}
                else if(user.getFacing() == 2) {turn = "Turn around and ";}
                else if(user.getFacing() == 3) {turn = "Turn right and ";}
            }
            direction = turn+"walk to row "+bestRow;
            Log.d("direction: ",""+direction);
        }
        return direction;
    }


}
