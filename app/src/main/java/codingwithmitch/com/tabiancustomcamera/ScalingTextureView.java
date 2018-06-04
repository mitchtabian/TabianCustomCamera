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
public class ScalingTextureView extends TextureView {

    private static final String TAG = "ScalingTextureView";

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;


    public ScalingTextureView(Context context) {
        this(context, null);
    }

    public ScalingTextureView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScalingTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScalingTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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


}

















