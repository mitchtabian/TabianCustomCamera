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

import codingwithmitch.com.tabiancustomcamera.gestures.MoveGestureDetector;

/**
 * A {@link TextureView} that can be adjusted to a specified aspect ratio.
 */
public class ScalingTextureView extends TextureView {

    private static final String TAG = "ScalingTextureView";

    public int mRatioWidth = 0;
    public int mRatioHeight = 0;
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;
    private String mRoundedScreenAspectRatio = "";
    private String mRoundedPreviewAspectRatio = "";

    private Matrix mMatrix;

    private ScaleGestureDetector mScaleDetector;

    private MoveGestureDetector mMoveDetector;

    // scaling
    public float mScaleFactor = 1.f;
    public float mScaleFactorX = 1.f;
    public float mScaleFactorY = 1.f;
    public float mWidthCorrection = 0f;
    float mScreenAspectRatio = 1f;
    float mPreviewAspectRatio = 1f;

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
    public void setAspectRatio(int width, int height, int screenWidth, int screenHeight) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;

        mScreenAspectRatio = (float)mScreenHeight / (float)mScreenWidth;
        mPreviewAspectRatio = (float)mRatioHeight / (float)mRatioWidth;
        mRoundedScreenAspectRatio = String.format("%.2f", mScreenAspectRatio);
        mRoundedPreviewAspectRatio = String.format("%.2f", mPreviewAspectRatio);
        getWidthCorrection();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        }
        setMeasuredDimension(mScreenWidth, mScreenHeight);

    }


    private void init(Context context) {

        mMatrix = new Matrix();

        // Setup Gesture Detectors
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        mMoveDetector = new MoveGestureDetector(context, new MoveListener());
    }

    private void getWidthCorrection(){
        String roundedScreenAspectRatio = String.format("%.2f", mScreenAspectRatio);
        String roundedPreviewAspectRatio = String.format("%.2f", mPreviewAspectRatio);
        if(!roundedPreviewAspectRatio.equals(roundedScreenAspectRatio) ){

            float scaleFactor = (mScreenAspectRatio / mPreviewAspectRatio);
            Log.d(TAG, "configureTransform: scale factor: " + scaleFactor);

            mWidthCorrection = (((float)mScreenWidth * scaleFactor) - mScreenWidth) / 2;
            Log.d(TAG, "getWidthCorrection: width correction: " + mWidthCorrection);
        }
    }


    public boolean onTouch(MotionEvent motionEvent) {

        mScaleDetector.onTouchEvent(motionEvent);

        mMoveDetector.onTouchEvent(motionEvent);

        if(mScaleFactor > 1.011111 || mScaleFactor < 0.99999) {

            mMatrix.reset();

            Log.d(TAG, "onTouch: scale factor: " + mScaleFactor);

            mScaleFactorY = mScaleFactor;
            mScaleFactorX = mScaleFactor;


            if(!mRoundedPreviewAspectRatio.equals(mRoundedScreenAspectRatio) ){

                mScaleFactorX *= (mScreenAspectRatio / mPreviewAspectRatio);
                Log.d(TAG, "configureTransform: scale factor: " + mScaleFactorX);
            }

            float scaledImageCenterX = (getWidth() * mScaleFactorX) / 2;

            float scaledImageCenterY = (getHeight() * mScaleFactorY) / 2;


            mMatrix.postScale(mScaleFactorX, mScaleFactorY);

            float dx = mImageCenterX - scaledImageCenterX;

            float dy = mImageCenterY - scaledImageCenterY;

            Log.d(TAG, "onTouch: dx: " + dx + ", dy: " + dy);


            // BOUNDARY 1: Right
            if (dx <  (getWidth() - (((float)mScreenWidth - mWidthCorrection) * mScaleFactorX))) {

                dx = (getWidth() - (((float)mScreenWidth - mWidthCorrection) * mScaleFactorX));

                mImageCenterX = dx + scaledImageCenterX; // reverse the changes

            }

            //BOUNDARY 2: Bottom
            if (dy <  (getHeight() - ((float)mScreenHeight * mScaleFactorY))) {

                dy = (getHeight() - ((float)mScreenHeight * mScaleFactorY));

                mImageCenterY = dy + scaledImageCenterY;

            }


            // BOUNDARY 3: Left
            if (dx > -mWidthCorrection) {

                dx = -mWidthCorrection;

                mImageCenterX = dx + scaledImageCenterX;
            }


            // BOUNDARY 4: Top
            if (dy > 0) {

                dy = 0;

                mImageCenterY = dy + scaledImageCenterY;
            }

            mMatrix.postTranslate(dx, dy);

            setTransform(mMatrix);

            setAlpha(1);

            mFocusX = -1 * (dx / mScaleFactorX);
            mFocusY = -1 * (dy / mScaleFactorY);
        }
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

            Log.d(TAG, "onMove: image center x: " + mImageCenterX);
            Log.d(TAG, "onMove: image canter y: " + mImageCenterY);

            return true;
        }
    }

    public void resetScale(){
        mScaleFactor = 1.0f;
        mScaleFactorX = 1f;
        mScaleFactorY = 1f;
        mImageCenterX = mRatioWidth / 2;
        mImageCenterX = mRatioHeight / 2;
        mFocusX = 0f;
        mFocusY = 0f;
    }

}

















