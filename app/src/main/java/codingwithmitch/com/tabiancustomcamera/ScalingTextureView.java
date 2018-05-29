package codingwithmitch.com.tabiancustomcamera;

/**
 * Created by User on 5/21/2018.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;
import android.view.View;

/**
 * A {@link TextureView} that can be adjusted to a specified aspect ratio.
 */
public class ScalingTextureView extends TextureView implements View.OnTouchListener{

    private static final String TAG = "ScalingTextureView";

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private Matrix mMatrix;

    private ScaleGestureDetector mScaleDetector;

    private MoveGestureDetector mMoveDetector;

    public float mScaleFactor = 1.f;

    public float mImageCenterX = 0.f;

    public float mImageCenterY = 0.f;

    public float mFocusX = 0.f;

    public float mFocusY = 0.f;


    public ScalingTextureView(Context context) {
        this(context, null);
        init(context);
    }

    public ScalingTextureView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        init(context);
    }

    public ScalingTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScalingTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }

    private void init(Context context) {

        mMatrix = new Matrix();

        // Setup Gesture Detectors
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        mMoveDetector = new MoveGestureDetector(context, new MoveListener());
    }

    public void setDisplayMetrics(int width, int height) {

        mImageCenterX = width / 2;

        mImageCenterY = height / 2;
    }



    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        mScaleDetector.onTouchEvent(motionEvent);

        mMoveDetector.onTouchEvent(motionEvent);

        Log.d(TAG, "onTouch: scale factor: " + mScaleFactor);

        float screenAspectRatio = (float)mRatioHeight / (float)mRatioWidth;
        float scaleFactorX = mScaleFactor;
        float scaleFactorY = mScaleFactor;

        Log.d(TAG, "onTouch: screen aspect ratio: " + screenAspectRatio);

        // Probably larger (ex: samsung s8 has 1.92)
        if(screenAspectRatio > 1.9f && screenAspectRatio < 2.0f){

            // find adjustment factor based on target aspect ratio and actual aspect ratio
            float targetAspectRatio = (float)16 / (float)9;
            float adjustmentFactor = ( (float)mRatioHeight - ((float)mRatioWidth * targetAspectRatio) ) / (float)mRatioHeight;
            Log.d(TAG, "onTouch: width scale adjustment factor: " + adjustmentFactor);

            scaleFactorX = scaleFactorX + (scaleFactorX * adjustmentFactor);
            Log.d(TAG, "onTouch: adjusting x-scale because screen size is weird.");
        }


        float scaledImageCenterX = (getWidth() * scaleFactorX) / 2;

        float scaledImageCenterY = (getHeight() * scaleFactorY) / 2;

        mMatrix.reset();

        mMatrix.postScale(scaleFactorX, scaleFactorY);

        float dx = mImageCenterX - scaledImageCenterX;

        float dy = mImageCenterY - scaledImageCenterY;

        if (dx < ((1 - mScaleFactor) * getWidth())) {

            dx = (1 - mScaleFactor) * getWidth();

            mImageCenterX = dx + scaledImageCenterX;

        }

        if (dy < ((1 - mScaleFactor) * getHeight())) {

            dy = (1 - mScaleFactor) * getHeight();

            mImageCenterY = dy + scaledImageCenterY;

        }
        if (dx > 0) {

            dx = 0;

            mImageCenterX = dx + scaledImageCenterX;
        }

        if (dy > 0) {

            dy = 0;

            mImageCenterY = dy + scaledImageCenterY;
        }

        mMatrix.postTranslate(dx, dy);

        setTransform(mMatrix);

        setAlpha(1);

        mFocusX = -1 * (dx / mScaleFactor);
        mFocusY = -1 * (dy / mScaleFactor);
        Log.d(TAG, "onTouch: mFocusX: " + mFocusX);
        Log.d(TAG, "onTouch: mFocusY: " + mFocusY);


        return true; // indicate event was handled
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            mScaleFactor *= detector.getScaleFactor(); // scale change since previous event

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(1.f, Math.min(mScaleFactor, 4.0f));

            return true;
        }
    }

    private class MoveListener extends MoveGestureDetector.SimpleOnMoveGestureListener {
        @Override
        public boolean onMove(MoveGestureDetector detector) {

            PointF d = detector.getFocusDelta();

            mImageCenterX += d.x;

            mImageCenterY += d.y;

            return true;
        }
    }

    public void resetScale(){
        mScaleFactor = 1.0f;
        mImageCenterX = 1.0f;
        mImageCenterX = 1.0f;
        mFocusX = 0f;
        mFocusY = 0f;
    }

}

















