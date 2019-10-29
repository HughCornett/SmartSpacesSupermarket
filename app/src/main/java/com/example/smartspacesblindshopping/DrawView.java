package com.example.smartspacesblindshopping;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Point;

import androidx.core.content.ContextCompat;

import java.util.Vector;

public class DrawView extends View
{
    // CONSTANT VARIABLES
    public static final int AISLE_ROW_COLOR = Color.BLACK;
    public static final int AISLE_ROW_ALPHA = 150;

    public static final int USER_COLOR = Color.BLUE;
    public static final int USER_RADIUS = 50;

    public static final int ITEM_COLOR = Color.GREEN;
    public static final int ITEM_RADIUS = 25;

    final int image_width = 560;
    final int image_hight = 300;
    public static double PIXELS_PER_METER;

    // OTHER VARIABLES
    public static Point displaySize = new Point();
    //public static int navBarHeight;

    private Drawable mapImage;
    public static Rect imageBounds;
    private Vector<Rect> drawnBoxes = new Vector<>();

    private Point userScreenPosition = new Point();
    private Point itemScreenPosition = new Point();

    Display display;

    Paint paint = new Paint();
    public DrawView(Context context, Display display) {
        super(context);
        mapImage = ContextCompat.getDrawable(context, R.drawable.map1);

        this.display = display;
        display.getSize(displaySize);

        double scale1 = ((double)(displaySize.x) / (double)(mapImage.getIntrinsicWidth()));
        double scale2 = ((double)(displaySize.y) / (double)(mapImage.getIntrinsicHeight()));

        double scale=1.0;
        if (scale1<1.0 || scale2<1.0)
        {
            if(scale1>=scale2)
            {
                scale = scale2;
            }
            else scale = scale1;
        }
        double right = Math.round(mapImage.getIntrinsicWidth()*scale);
        double bottom = Math.round(mapImage.getIntrinsicHeight()*scale);
        Rect rect = new Rect(0,0,(int) right,(int) bottom);

        //calculate the bounds of the background image

        imageBounds = rect;

    }

    /**
     * redraws the view
     */
    public void updateView()
    {
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        //draw the map background
        //imageBounds = canvas.getClipBounds();

        //calculate the bounds of the background image

        displaySize.x = imageBounds.right - imageBounds.left;
        displaySize.y = imageBounds.bottom - imageBounds.top;
        //MapActivity.display.getSize(displaySize);
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
            canvas.drawRect(aisleOnScreen, paint);
        }

        //draw all the rows
        for(int i = 0; i < Map.rows.size(); i++)
        {
            Point rowTopLeft = getScreenCoords(Map.rows.get(i).left, Map.rows.get(i).top);
            Point rowBottomRight = getScreenCoords(Map.rows.get(i).right, Map.rows.get(i).bottom);

            Rect rowOnScreen = new Rect(rowTopLeft.x, rowTopLeft.y, rowBottomRight.x, rowBottomRight.y);
            canvas.drawRect(rowOnScreen, paint);
        }


        paint.setColor(Color.RED);
        for(int i = 0; i < Map.shelves.size(); i++)
        {
            Point rowTopLeft = getScreenCoords(Map.shelves.get(i).getRect().left, Map.shelves.get(i).getRect().top);
            Point rowBottomRight = getScreenCoords(Map.shelves.get(i).getRect().right, Map.shelves.get(i).getRect().bottom);

            Rect rowOnScreen = new Rect(rowTopLeft.x, rowTopLeft.y, rowBottomRight.x, rowBottomRight.y);
            canvas.drawRect(rowOnScreen, paint);
        }

        paint.setStrokeWidth(10);
        for(int i = 0; i < Map.edges.size(); i++)
        {
            if(Map.edges.get(i).getPathEdge()) { paint.setColor(Color.GREEN); }
            else { paint.setColor(Color.RED); }
            Point from = getScreenCoords(Map.edges.get(i).getFrom().getXPosition(), Map.edges.get(i).getFrom().getYPosition());
            Point to = getScreenCoords(Map.edges.get(i).getTo().getXPosition(), Map.edges.get(i).getTo().getYPosition());

            canvas.drawLine(from.x, from.y, to.x, to.y, paint);
        }
        for(int i = 0; i < Map.nodes.size(); i++)
        {
            if(Map.nodes.get(i).getPathNode()) { paint.setColor(Color.GREEN); }
            else { paint.setColor(Color.RED); }
            Node node = Map.nodes.get(i);
            Point screenPos = getScreenCoords(node.getXPosition(), node.getYPosition());


            canvas.drawCircle(screenPos.x, screenPos.y, USER_RADIUS, paint);
        }



        //redfine the color and transparency
        paint.setColor(USER_COLOR);

        //draw the position circle if it has been defined
        if(Map.user != null)
        {
            userScreenPosition = getScreenCoords(Map.user.getX(), Map.user.getY());
            canvas.drawCircle(userScreenPosition.x, userScreenPosition.y, USER_RADIUS, paint);
        }

        paint.setColor(ITEM_COLOR);
        if(Map.item != null)
        {
            itemScreenPosition = getScreenCoords(Map.item.getXPosition(), Map.item.getYPosition());
            canvas.drawCircle(itemScreenPosition.x, itemScreenPosition.y, ITEM_RADIUS, paint);
        }
    }

    private static Point getScreenCoords(double worldX, double worldY)
    {
        int newX = (int) Math.round((worldY/Map.ROOM_HEIGHT)*(displaySize.x));
        int newY = (int) Math.round((worldX/Map.ROOM_WIDTH)*(displaySize.y));
        return new Point(newX, newY);

    }
    //adds a box to the list
    public void addBox(Rect box)
    {
        drawnBoxes.add(box);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        return performClick();
    }

    public void clearBoxes()
    {
        drawnBoxes.clear();
    }
}
