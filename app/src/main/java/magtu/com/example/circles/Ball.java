package magtu.com.example.circles;

import android.content.Context;
import android.graphics.*;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Random;

/**
 * Created by Nikita on 27.02.2018.
 */

public class Ball extends View {

    DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();

    /**
     * Main ball attributes
     */
    private float posX, posY, speedX, speedY;
    private float radius;
    String color;

    Ball(Context context, String color) {
        super(context);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        /**
         * For multi screen size support
         */
        float scale = (metrics.widthPixels + metrics.heightPixels) / 1000;
        radius = 15 * scale;
        this.color = color;

        posX = 81;
        posY = metrics.heightPixels / 2;
        speedX = 1.5f;
        speedY = 1.5f;
    }

    public void paint(Canvas canvas) {
        Paint ballColor = new Paint();
            ballColor.setAntiAlias(true);
            ballColor.setStyle(Paint.Style.FILL);
            ballColor.setColor(Color.parseColor(color));

        canvas.drawCircle(posX, posY, radius, ballColor);
    }

    public void move() {
        posX += speedX;
        if (color.equalsIgnoreCase("Blue")) {
            posY = (metrics.heightPixels / 2) + (float) Math.sin(Math.toRadians(posX)) * 100;
        }
        else if (color.equalsIgnoreCase("Red"))
            posY =(metrics.heightPixels / 2) + (float) Math.cos(Math.toRadians(posX)) * 100;

        if(posX - radius >= metrics.widthPixels || posX - radius - 55 <= 0)
            speedX*=-1;
    }

    public float getRadius () {
        return  radius;
    }

    public boolean colliding(Ball ball) {
        float xd = ball.posX - posX;
        float yd = ball.posY - posY;

        float sumRadius = ball.radius + getRadius();
        float sqrRadius = sumRadius * sumRadius;
        float distSqr = (xd * xd) + (yd * yd);
        return distSqr <= sqrRadius;
    }
}
