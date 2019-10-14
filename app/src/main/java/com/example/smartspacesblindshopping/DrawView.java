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
    public static final int RECTANGLE_COLOR = Color.RED;
    public static final int RECTANGLE_ALPHA = 60;

    public static final int CIRCLE_COLOR = Color.BLUE;
    public static final int CIRCLE_RADIUS = 25;

    public static Point displaySize = new Point();
    public static double PIXELS_PER_METER;

    // OTHER VARIABLES
    private Drawable mapImage;
    public static Rect imageBounds;
    private Vector<Rect> drawnBoxes = new Vector<>();

    private Point userScreenPosition = new Point();

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

        MainActivity.display.getSize(displaySize);
        mapImage.setBounds(imageBounds);
        mapImage.draw(canvas);

        PIXELS_PER_METER = displaySize.x/Map.ROOM_HEIGHT;

        //define the color, width and transparency
        paint.setColor(RECTANGLE_COLOR);
        paint.setAlpha(RECTANGLE_ALPHA);

        //draw all the rectangles
        for(int i = 0; i < drawnBoxes.size(); i++)
        {
            canvas.drawRect(drawnBoxes.get(i), paint);
        }

        clearBoxes();
        //redfine the color and transparency
        paint.setColor(CIRCLE_COLOR);

        //draw the position circle if it has been defined
        if(Map.user != null)
            Log.d("worldPos", ""+Map.user.getPosition());

            userScreenPosition = getScreenCoords(new Point(Map.user.getPosition().x, Map.user.getPosition().y));

            Log.d("screenPos", ""+userScreenPosition);
            canvas.drawCircle(userScreenPosition.x, userScreenPosition.y, CIRCLE_RADIUS, paint);

    }

    private static Point getScreenCoords(Point worldCoordinates)
    {
        int newX = (int) Math.round((worldCoordinates.y/Map.ROOM_HEIGHT)*displaySize.x);
        int newY = (int) Math.round((worldCoordinates.x/Map.ROOM_WIDTH)*displaySize.y);
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
