package magtu.com.example.circles;

/**
 * Created by Nikita on 27.02.2018.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.*;
import android.graphics.*;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import java.util.ArrayList;

public class GameActivity extends Activity {
    MediaPlayer mediaPlayer;
    private GameThread gameThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GameView(this));
        hideUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameThread.setRunning(false);
        hideUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameThread.setRunning(false);
        hideUI();
    }

    public void hideUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private class GameView extends SurfaceView implements SurfaceHolder.Callback {

        public GameView(Context context) {
            super(context);
            gameThread = new GameThread(getHolder(), context, this);
            getHolder().addCallback(this);
            mediaPlayer = MediaPlayer.create(context, R.raw.bounce);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            gameThread.setRunning(true);
            gameThread.start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            boolean retry = true;
            // завершаем работу потока
            gameThread.setRunning(false);
            while (retry) {
                try {
                    gameThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    // если не получилось, то будем пытаться еще и еще
                }
            }
        }

    }

    private class GameThread extends Thread {
        private boolean runFlag = false;
        private final SurfaceHolder surfaceHolder;
        private long prevTime;
        private ArrayList<Ball> balls = new ArrayList<>();
        private DisplayMetrics metrics;
        long now, elapsedTime;

        @SuppressLint("ClickableViewAccessibility")
        private GameThread(SurfaceHolder surfaceHolder, Context context,
                           GameActivity.GameView view) {
            this.surfaceHolder = surfaceHolder;

            metrics = context.getResources().getDisplayMetrics();

            // загружаем объекты, которые будем отрисовывать
            balls.add(new Ball(context, "Blue"));
            balls.add(new Ball(context, "Red"));
            // сохраняем текущее время
            prevTime = System.currentTimeMillis();
        }

        private void setRunning(boolean run) {
            runFlag = run;
        }

        @Override
        public void run() {
            Canvas canvas;
            while (!runFlag) {
                try {
                    surfaceHolder.wait();
                } catch (InterruptedException e) {
                }
            }
            while (runFlag) {
                // получаем текущее время и вычисляем разницу с предыдущим
                // сохраненным моментом времени
                now = System.currentTimeMillis();
                elapsedTime = now - prevTime;
                if (elapsedTime > 5) {
                    // если прошло больше 5 миллисекунд - сохраним текущее время
                    prevTime = now;
                    updateFrame(); //каждые 5 миллисекунд обновляется картинка
                }
                canvas = null;
                try {
                    // получаем объект Canvas и выполняем отрисовку
                    canvas = surfaceHolder.lockCanvas(null);
                    if (canvas != null) synchronized (surfaceHolder) {
                        canvas.drawColor(Color.WHITE);

                        Paint paint = new Paint();
                        paint.setStrokeWidth(4);
                        paint.setColor(Color.BLACK);

                        // X - axis
                        canvas.drawLine(60, 660, 1240,
                                660, paint);
                        canvas.drawLine(1210,650,1240,660, paint);
                        canvas.drawLine(1210,670,1240,660, paint);

                        // Y - axis
                        canvas.drawLine(60, 60, 60,
                                660, paint);
                        canvas.drawLine(50, 90, 60,60, paint);
                        canvas.drawLine(70, 90, 60,60, paint);

                        paint.setTextSize(40);
                        paint.setStrokeWidth(4);
                        canvas.drawText("Red ball - moved by cos", metrics.heightPixels/2,
                                40, paint);
                        canvas.drawText("Blue ball - moved by sin", metrics.heightPixels/2,
                                80, paint);

                        for (int i = 0; i < balls.size(); i++) {
                            balls.get(i).paint(canvas);
                        }
                    }
                } finally {
                    if (canvas != null) {
                        // отрисовка выполнена. выводим результат на экран
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        private void updateFrame() {
            for (int i = 0; i < balls.size(); i++) {
                balls.get(i).move();
                if(balls.get(0).colliding(balls.get(1))) {
                    mediaPlayer.start();
                    Log.d("Col", "COLLIDE");
                }
            }
        }
    }
}
