package codingwithmitch.com.tabiancustomcamera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 5/23/2018.
 */

public class DrawableImageView extends android.support.v7.widget.AppCompatImageView
{

    private static final String TAG = "DrawableImageView";

    private int color;
    private float width = 8f;
    private List<Pen> mPenList = new ArrayList<Pen>();
    private Activity mHostActivity;
    private boolean mIsDrawingEnabled = false;


    private class Pen {
        Path path;
        Paint paint;

        Pen(int color, float width ) {
            path = new Path();
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(width);
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
        }
    }



    public DrawableImageView(Context context) {
        super(context);
        init(context);
    }

    public DrawableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public DrawableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context) {
        mPenList.add(new Pen(color, width));
        setDrawingCacheEnabled(true);
        if(context instanceof Activity) {
            mHostActivity = (Activity) context;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Pen pen : mPenList) {
            canvas.drawPath(pen.path, pen.paint);
        }
    }


    public boolean touchEvent(MotionEvent event){

        hideStatusBar();

        if(mIsDrawingEnabled) {
            float eventX = event.getX();
            float eventY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPenList.add(new Pen(color, width));
                    mPenList.get(mPenList.size() - 1).path.moveTo(eventX, eventY);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    mPenList.get(mPenList.size() - 1).path.lineTo(eventX, eventY);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    return false;
            }
        }

        invalidate();
        return true;
    }



    public void removeLastPath(){
        if(mPenList.size() > 0){
            mPenList.remove(mPenList.size() - 1);
            invalidate();
        }
    }


    public void reset() {
        for (Pen pen : mPenList) {
            pen.path.reset();
        }
        invalidate();
    }

    public void setBrushColor(int color) {
        this.color = color;
    }

    public int getBrushColor(){
        return color;
    }

    public void setWidth(float width) {
        this.width = width;
    }


    private void hideStatusBar() {

        if(mHostActivity != null){
            View decorView = mHostActivity.getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

    }


    public void setDrawingIsEnabled(boolean bool){
        mIsDrawingEnabled = bool;
    }

}








