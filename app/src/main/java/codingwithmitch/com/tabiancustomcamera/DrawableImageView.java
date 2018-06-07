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

    private static final int SIZE_CHANGE_SPEED = 2;
    private static final int STICKER_STARTING_WIDTH = 300;
    private static final int STICKER_STARTING_HEIGHT = 300;
    private static final int MIN_STICKER_WIDTH = 50;
    private static final int MIN_STICKER_HEIGHT = 50;

    //vars
    private int color;
    private float width = 8f;
    private List<Pen> mPenList = new ArrayList<Pen>();
    private Activity mHostActivity;
    private boolean mIsDrawingEnabled = false;


    // Scales
    float mMinWidth = 8f;
    float mMaxWidth = 500f;
    private ScaleGestureDetector mScaleGestureDetector;
    private boolean mIsSizeChanging = false;
    private Circle mCircle;
    private int mScreenWidth;


    // Stickers
    private ArrayList<Sticker> mStickers = new ArrayList<>();
    int mPrevStickerX, mPrevStickerY;
    int mSelectedStickerIndex = -1;
    private boolean mIsStickerResizing = false;

    private class Sticker{

        Paint paint;
        Bitmap bitmap;
        Drawable drawable;
        int x, y;
        Rect rect;


        Sticker(Bitmap bitmap, Drawable drawable, int x, int y){
            paint = new Paint();
            this.x = x;
            this.y = y;
            this.bitmap = bitmap;
            this.drawable = drawable;
            rect = new Rect(x, y, x + STICKER_STARTING_WIDTH, y + STICKER_STARTING_HEIGHT);
        }

        public void adjustRect(){
            rect.left = x;
            rect.top = y;
            rect.right = x + bitmap.getWidth();
            rect.bottom = y + bitmap.getHeight();
        }
    }


    private class Circle {

        float x, y;
        Paint paint;

        Circle(int color, float x, float y) {
            this.x = x;
            this.y = y;
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(width);
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
        }
    }

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

            mScaleGestureDetector = new ScaleGestureDetector(mHostActivity, new ScaleListener());

            DisplayMetrics displayMetrics = new DisplayMetrics();
            mHostActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            mScreenWidth = displayMetrics.widthPixels;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mStickers.size() > 0){
            for(Sticker sticker: mStickers){
                canvas.drawBitmap(sticker.bitmap, sticker.x, sticker.y, sticker.paint);
            }
        }

        for (Pen pen : mPenList) {
            canvas.drawPath(pen.path, pen.paint);
        }

        if(mCircle != null){
            float radius = width / (float)mScreenWidth;
            canvas.drawCircle(mCircle.x, mCircle.y, radius, mCircle.paint);
        }
    }


    public boolean touchEvent(MotionEvent event){

        hideStatusBar();

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:{
                removeCircle();
                mIsSizeChanging = false;
                break;
            }
        }

        if(mIsDrawingEnabled) {
            if (event.getPointerCount() > 1) {

                if (!mIsSizeChanging) {
                    mIsSizeChanging = true;
                }

                try {
                    mScaleGestureDetector.onTouchEvent(event);

                    float[] xPositions = new float[2];
                    float[] yPositions = new float[2];
                    float minX = 0;
                    float minY = 0;

                    for (int i = 0; i < event.getPointerCount(); i++) {
                        int pointerId = event.getPointerId(i);
                        if (pointerId == MotionEvent.INVALID_POINTER_ID) {
                            continue;
                        }

                        if (minX == 0 && minY == 0) {
                            minX = event.getX(i);
                            minY = event.getY(i);
                        } else {
                            float tempMinX = event.getX(i);
                            float tempMinY = event.getY(i);
                            if (tempMinX < minX) {
                                minX = tempMinX;
                            }
                            if (tempMinY < minY) {
                                minY = tempMinY;
                            }
                        }

                        xPositions[i] = event.getX(i);
                        yPositions[i] = event.getY(i);
                    }

                    float circleX = (Math.abs(xPositions[1] - xPositions[0]) / 2) + minX;
                    float circleY = (Math.abs(yPositions[1] - yPositions[0]) / 2) + minY;


                    mCircle = new Circle(color, circleX, circleY);

                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, "touchEvent: IndexOutOfBoundsException: " + e.getMessage());
                }
            } else {

                if (!mIsSizeChanging) {

                    float eventX = event.getX();
                    float eventY = event.getY();

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mPenList.add(new Pen(color, width));
                            mPenList.get(mPenList.size() - 1).path.moveTo(eventX, eventY);
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            drawingStarted();
                            mPenList.get(mPenList.size() - 1).path.lineTo(eventX, eventY);
                            break;
                        case MotionEvent.ACTION_UP:
                            drawingStopped();
                            break;
                        default:
                            return false;
                    }
                }
            }
        }

        if(mStickers.size() > 0){

            int newPositionX = (int)event.getX();
            int newPositionY = (int)event.getY();

            switch(event.getAction()){

                case MotionEvent.ACTION_UP:{
                    Log.d(TAG, "touchEvent: reset sticker index.");

                    resetSticker(newPositionX, newPositionY);

                    break;
                }

                case MotionEvent.ACTION_POINTER_UP:{
                    Log.d(TAG, "touchEvent: reset sticker index.");

                    resetSticker(newPositionX, newPositionY);

                    break;
                }

                case MotionEvent.ACTION_MOVE:{

                    if(mSelectedStickerIndex != -1){

                        if(event.getPointerCount() > 1){
                            mIsStickerResizing = true;
                        }
                        else{
                            mIsStickerResizing = false;

                            moveSticker(newPositionX, newPositionY);

                        }
                    }

                    if(mIsStickerResizing){

                        moveSticker(newPositionX, newPositionY);

                        mScaleGestureDetector.onTouchEvent(event);
                    }

                    break;
                }

                case MotionEvent.ACTION_DOWN:{
                    for(int i = 0; i < mStickers.size(); i++){
                        if(mStickers.get(i).rect.contains(newPositionX, newPositionY)){
                            Log.d(TAG, "touchEvent: touched a STICKER.");
                            mSelectedStickerIndex = i;
                            mPrevStickerX = newPositionX;
                            mPrevStickerY = newPositionY;

                            break; // break the loop
                        }
                    }
                    break;
                }

            }

        }

        invalidate();
        return true;
    }


    private void resetSticker(int newPositionX, int newPositionY){

        mSelectedStickerIndex = -1; // reset the sticker index

        mIsStickerResizing = false;
    }

    private void moveSticker(int newPositionX, int newPositionY){
        int dx = newPositionX - mPrevStickerX;
        int dy = newPositionY - mPrevStickerY;

        mPrevStickerX = newPositionX;
        mPrevStickerY = newPositionY;

        mStickers.get(mSelectedStickerIndex).x
                = mStickers.get(mSelectedStickerIndex).x + dx;

        mStickers.get(mSelectedStickerIndex).y
                = mStickers.get(mSelectedStickerIndex).y + dy;

        mStickers.get(mSelectedStickerIndex).adjustRect();
    }


    public void addNewSticker(Drawable drawable){
        if(mHostActivity != null){
            if(mHostActivity instanceof MainActivity){
                Log.d(TAG, "addNewSticker: adding new sticker to canvas.");
                Bitmap newStickerBitmap = drawableToBitmap(drawable);

                Sticker sticker = new Sticker(newStickerBitmap, drawable,0, 200);
                mStickers.add(sticker);
                invalidate();
            }
        }
    }

    private void drawingStarted(){
        if(mHostActivity != null){
            if(mHostActivity instanceof MainActivity){
                ((MainActivity)mHostActivity).hideStillshotWidgets();
            }
        }
    }

    private void drawingStopped(){
        if(mHostActivity != null){
            if(mHostActivity instanceof MainActivity){
                ((MainActivity)mHostActivity).showStillshotWidgets();
            }
        }
        removeCircle();
    }

    private void removeCircle(){
        mCircle = null;
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
        width = mMinWidth;
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

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();

            if(scaleFactor > 1.011 || scaleFactor < 0.99) {

                if(mSelectedStickerIndex != -1){
                    mStickers.get(mSelectedStickerIndex).bitmap
                            = resizeBitmap(
                            mStickers.get(mSelectedStickerIndex).drawable,
                            mStickers.get(mSelectedStickerIndex).bitmap,
                            scaleFactor
                    );

                    mIsStickerResizing = true;

                }
                else{
                    float prevWidth = width;
                    if(scaleFactor > 1){
                        width += ( (SIZE_CHANGE_SPEED + (width * 0.05)) * scaleFactor );
                    }
                    else{
                        width -= ( (SIZE_CHANGE_SPEED + (width * 0.05)) * scaleFactor );
                    }
                    if ( width > mMaxWidth) {
                        width = prevWidth;
                    }
                    else if (width < mMinWidth) {
                        width = prevWidth;
                    }
                }

            }


            return true;
        }
    }

    public static Bitmap resizeBitmap(Drawable drawable, Bitmap currentBitmap, float scale){
        Bitmap bitmap = null;

        int newWidth = 0;
        int newHeight = 0;
        if(scale > 1){
            newWidth = currentBitmap.getWidth() + (int)((currentBitmap.getWidth() * 0.04) * scale);
            newHeight = currentBitmap.getHeight() + (int)((currentBitmap.getHeight() * 0.04) * scale);
        }
        else{
            newWidth = currentBitmap.getWidth() - (int)((currentBitmap.getHeight() * 0.04) * scale);
            newHeight = currentBitmap.getHeight() - (int)((currentBitmap.getHeight() * 0.04) * scale);
        }

        if(newWidth < MIN_STICKER_WIDTH){
            newWidth = currentBitmap.getWidth();
        }
        if(newHeight < MIN_STICKER_HEIGHT){
            newHeight = currentBitmap.getHeight();
        }

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return Bitmap.createScaledBitmap(
                        bitmapDrawable.getBitmap(),
                        newWidth,
                        newHeight,
                        false);
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        }
        else {
            bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, newWidth, newHeight);
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), STICKER_STARTING_WIDTH, STICKER_STARTING_HEIGHT, false);
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        }
        else {
            bitmap = Bitmap.createBitmap(STICKER_STARTING_WIDTH, STICKER_STARTING_WIDTH, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}








