package com.example.smartspacesblindshopping;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Point;

import java.util.Vector;

public class DrawView extends View
{
    // CONSTANT VARIABLES
    public static final int AISLE_ROW_COLOR = Color.BLACK;
    public static final int AISLE_ROW_ALPHA = 200;

    public static final int USER_COLOR = Color.BLUE;
    public static final int USER_RADIUS = 50;

    public static final int ITEM_COLOR = Color.GREEN;
    public static final int ITEM_RADIUS = 25;

    public static final int NODE_COLOR = Color.RED;
    public static final int PATH_NODE_COLOR = Color.GREEN;
    public static final int NODE_RADIUS = 50;
    public static final float EDGE_WIDTH = 10.0f;

    public static double PIXELS_PER_METER;

    // OTHER VARIABLES
    public static Point displaySize = new Point();
    //public static int navBarHeight;

    private Drawable mapImage;
    public static Rect imageBounds;
    private Vector<Rect> drawnBoxes = new Vector<>();

    private Point userScreenPosition = new Point();
    private Point itemScreenPosition = new Point();

    Paint paint = new Paint();
    public DrawView(Context context) {
        super(context);
        mapImage = context.getResources().getDrawable(R.drawable.background);
    }

    /**
     * redraws the view
     * for when the boxes are changed and must be drawn again
     */
    public void updateView()
    {
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        //draw the map background
        imageBounds = canvas.getClipBounds();

        //calculate the bounds of the background image

        displaySize.x = imageBounds.right - imageBounds.left;
        displaySize.y = imageBounds.bottom - imageBounds.top;

        mapImage.setBounds(imageBounds);
        mapImage.draw(canvas);

        PIXELS_PER_METER = displaySize.x/Map.ROOM_HEIGHT;

        //define the color, width and transparency
        paint.setColor(AISLE_ROW_COLOR);
        paint.setAlpha(AISLE_ROW_ALPHA);

        //draw all the aisles
        for(int i = 0; i < Map.aisles.size(); i++)
        {
            Point aisleTopLeft = getScreenCoords(Map.aisles.get(i).left, Map.aisles.get(i).top);
            Point aisleBottomRight = getScreenCoords(Map.aisles.get(i).right, Map.aisles.get(i).bottom);

            Rect aisleOnScreen = new Rect(aisleTopLeft.x, aisleTopLeft.y, aisleBottomRight.x, aisleBottomRight.y);
            //canvas.drawRect(aisleOnScreen, paint);
        }

        //draw all the rows
        for(int i = 0; i < Map.rows.size(); i++)
        {
            Point rowTopLeft = getScreenCoords(Map.rows.get(i).left, Map.rows.get(i).top);
            Point rowBottomRight = getScreenCoords(Map.rows.get(i).right, Map.rows.get(i).bottom);

            Rect rowOnScreen = new Rect(rowTopLeft.x, rowTopLeft.y, rowBottomRight.x, rowBottomRight.y);
            //canvas.drawRect(rowOnScreen, paint);
        }

        /*
        paint.setColor(Color.GREEN);


        //draw all the shelves
        for(int i = 0; i < Map.shelves.size(); i++)
        {
            Point shelfTopLeft = getScreenCoords(Map.shelves.get(i).getRect().left, Map.shelves.get(i).getRect().top);
            Point shelfBottomRight = getScreenCoords(Map.shelves.get(i).getRect().right, Map.shelves.get(i).getRect().bottom);

            Rect shelfOnScreen = new Rect(shelfTopLeft.x, shelfTopLeft.y, shelfBottomRight.x, shelfBottomRight.y);
            canvas.drawRect(shelfOnScreen, paint);
        }
        */

        clearBoxes();

        //redfine the color and transparency
        paint.setColor(USER_COLOR);

        //draw the position circle if it has been defined
        if(Map.user != null)
        {
            userScreenPosition = getScreenCoords(Map.user.getX(), Map.user.getY());
            canvas.drawCircle(userScreenPosition.x, userScreenPosition.y, USER_RADIUS, paint);
            //Log.d("user pos", ""+userScreenPosition);
        }

        //draw the item if it has been defined
        paint.setColor(ITEM_COLOR);
        if(Map.item != null)
        {
            itemScreenPosition = getScreenCoords(Map.item.getXPosition(), Map.item.getYPosition());
            canvas.drawCircle(itemScreenPosition.x, itemScreenPosition.y, ITEM_RADIUS, paint);
            //Log.d("item pos", ""+itemScreenPosition);
        }

        //draw the edges
        paint.setStrokeWidth(EDGE_WIDTH);
        for(int i = 0; i < Map.edges.size(); i++)
        {
            if(Map.edges.get(i).getPathEdge()) { paint.setColor(PATH_NODE_COLOR); }
            else { paint.setColor(NODE_COLOR); }
            Point from = getScreenCoords(Map.edges.get(i).getFrom().getXPosition(), Map.edges.get(i).getFrom().getYPosition());
            Point to = getScreenCoords(Map.edges.get(i).getTo().getXPosition(), Map.edges.get(i).getTo().getYPosition());

            canvas.drawLine(from.x, from.y, to.x, to.y, paint);
        }

        paint.setColor(Color.BLACK);
        paint.setAlpha(50);
        //draw the nodes rects
        for(int i = 0; i < Map.nodes.size(); i++)
        {
            Point nodeTopLeft = getScreenCoords(Map.nodes.get(i).getRect().left, Map.nodes.get(i).getRect().top);
            Point nodeBottomRight = getScreenCoords(Map.nodes.get(i).getRect().right, Map.nodes.get(i).getRect().bottom);

            Rect nodeOnScreen = new Rect(nodeTopLeft.x, nodeTopLeft.y, nodeBottomRight.x, nodeBottomRight.y);
            canvas.drawRect(nodeOnScreen, paint);
        }

        paint.setAlpha(255);
        //draw the nodes
        for(int i = 0; i < Map.nodes.size(); i++) {
            Node node = Map.nodes.get(i);
            if(node.getPathNode()) { paint.setColor(PATH_NODE_COLOR); }
            else { paint.setColor(NODE_COLOR); }
            itemScreenPosition = getScreenCoords(node.getXPosition(), node.getYPosition());
            canvas.drawCircle(itemScreenPosition.x, itemScreenPosition.y, NODE_RADIUS, paint);
            //Log.d("node pos", "" + itemScreenPosition);
        }

    }

    private static Point getScreenCoords(double worldX, double worldY)
    {
        int newX = (int) Math.round((worldY/Map.ROOM_HEIGHT)*(displaySize.x));
        int newY = (int) Math.round((worldX/Map.ROOM_WIDTH)*(displaySize.y));
        return new Point(newX, newY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return performClick();
    }

    public void clearBoxes()
    {
        drawnBoxes.clear();
    }
}
