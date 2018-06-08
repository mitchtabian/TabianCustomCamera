package codingwithmitch.com.tabiancustomcamera;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class VerticalSlideColorPicker extends View {

    private Paint paint;
    private Paint strokePaint;
    private Path path;
    private Bitmap bitmap;
    private int viewWidth;
    private int viewHeight;
    private int centerX;
    private float colorPickerRadius;
    private OnColorChangeListener onColorChangeListener;
    private RectF colorPickerBody;
    private float selectorYPos;
    private int borderColor;
    private float borderWidth;
    private int[] colors;
    private boolean cacheBitmap = true;

    public VerticalSlideColorPicker(Context context) {
        super(context);
        init();
    }

    public VerticalSlideColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.VerticalSlideColorPicker,
                0, 0);

        try {
            borderColor = a.getColor(R.styleable.VerticalSlideColorPicker_borderColor, Color.WHITE);
            borderWidth = a.getDimension(R.styleable.VerticalSlideColorPicker_borderWidth, 10f);
            int colorsResourceId = a.getResourceId(R.styleable.VerticalSlideColorPicker_colors, R.array.default_colors);
            colors = a.getResources().getIntArray(colorsResourceId);
        } finally {
            a.recycle();
        }
        init();
    }

    public VerticalSlideColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public VerticalSlideColorPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        path = new Path();

        strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(borderColor);
        strokePaint.setAntiAlias(true);
        strokePaint.setStrokeWidth(borderWidth);

        setDrawingCacheEnabled(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path.addCircle(centerX, borderWidth + colorPickerRadius, colorPickerRadius, Path.Direction.CW);
        path.addRect(colorPickerBody, Path.Direction.CW);
        path.addCircle(centerX, viewHeight - (borderWidth + colorPickerRadius), colorPickerRadius, Path.Direction.CW);

        canvas.drawPath(path, strokePaint);
        canvas.drawPath(path, paint);

        if (cacheBitmap) {
            bitmap = getDrawingCache();
            cacheBitmap = false;
            invalidate();
        } else {
            canvas.drawLine(colorPickerBody.left, selectorYPos, colorPickerBody.right, selectorYPos, strokePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float yPos = Math.min(event.getY(), colorPickerBody.bottom);
        yPos = Math.max(colorPickerBody.top, yPos);

        selectorYPos = yPos;
        int selectedColor = bitmap.getPixel(viewWidth/2, (int) selectorYPos);

        if (onColorChangeListener != null) {
            onColorChangeListener.onColorChange(selectedColor);
        }

        invalidate();

        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;

        centerX = viewWidth/2;
        colorPickerRadius = (viewWidth/2) - borderWidth;

        colorPickerBody = new RectF(centerX - colorPickerRadius, borderWidth + colorPickerRadius, centerX + colorPickerRadius, viewHeight - (borderWidth + colorPickerRadius));

        LinearGradient gradient = new LinearGradient(0, colorPickerBody.top, 0, colorPickerBody.bottom, colors, null, Shader.TileMode.CLAMP);
        paint.setShader(gradient);

        resetToDefault();
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        invalidate();
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        invalidate();
    }

    public void setColors(int[] colors) {
        this.colors = colors;
        cacheBitmap = true;
        invalidate();
    }

    public void resetToDefault() {
        selectorYPos = borderWidth + colorPickerRadius;

        if (onColorChangeListener != null) {
            onColorChangeListener.onColorChange(Color.TRANSPARENT);
        }

        invalidate();
    }

    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
        this.onColorChangeListener = onColorChangeListener;
    }

    public interface OnColorChangeListener {

        void onColorChange(int selectedColor);
    }
}