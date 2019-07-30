package com.mayokun.quicknotes.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.mayokun.quicknotes.R;

/**
 * TODO: document your custom view class.
 */
public class ModuleStatusView extends View {
    public static final int MODULE_ARRAY_SIZE = 7;
    public static final int INVALID_INDEX = -1;
    public static final int SHAPE_CIRCLE = 0;
    public static final float DEFAULT_OUTLINE_WIDTH = 2f;
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private boolean[] moduleStatus;
    private float outlineWidth;
    private float shapeSize;
    private float spacing;
    private Rect[] moduleRectangles;
    private int outlineColor;
    private Paint paintOutline;
    private int fillColor;
    private Paint paintFill;
    private float radius;
    private int maxHorizontalModules;
    private int shape;

    public ModuleStatusView(Context context) {
        super(context);
        init(null, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        if (isInEditMode())
            setUpEditModeValues();

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        float displayDensity = dm.density;
        float defaultOutlineWidthPixels = displayDensity * DEFAULT_OUTLINE_WIDTH;

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ModuleStatusView, defStyle, 0);

        outlineColor = a.getColor(R.styleable.ModuleStatusView_outlineColor,Color.BLACK);
        shape = a.getInt(R.styleable.ModuleStatusView_shape, SHAPE_CIRCLE);
        outlineWidth = a.getDimension(R.styleable.ModuleStatusView_outlineWidth,defaultOutlineWidthPixels);

        a.recycle();

        shapeSize = 144f;
        spacing = 30f;
        radius = (shapeSize - outlineWidth) / 2;

        paintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOutline.setStyle(Paint.Style.STROKE);
        paintOutline.setStrokeWidth(outlineWidth);
        paintOutline.setColor(outlineColor);

        fillColor = getContext().getResources().getColor(R.color.orange);
        paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFill.setStyle(Paint.Style.FILL);
        paintFill.setColor(fillColor);

    }

    private void setUpEditModeValues() {
        boolean[] exampleModuleValues = new boolean[MODULE_ARRAY_SIZE];
        int middle = MODULE_ARRAY_SIZE/2;
        for (int i = 0; i < middle; i++) {
            exampleModuleValues[i] = true;
        }
        setModuleStatus(exampleModuleValues);
    }

    private void setUpModuleRectangles(int width) {
        int availableWidth = width - getPaddingLeft() - getPaddingRight();
        int horizontalModulesThatCanFit = (int) (availableWidth / (shapeSize + spacing));
        int mMaxHorizontalModules = Math.min(horizontalModulesThatCanFit,moduleStatus.length);

        moduleRectangles = new Rect[moduleStatus.length];
        for (int moduleIndex = 0; moduleIndex<moduleRectangles.length; moduleIndex++){
            int column = moduleIndex % mMaxHorizontalModules;
            int row = moduleIndex / mMaxHorizontalModules;
            int x = getPaddingLeft () + (int) (column * (shapeSize + spacing));
            int y = getPaddingTop() + (int) (row * (shapeSize + spacing));
            moduleRectangles[moduleIndex] = new Rect(x,y,x + (int) shapeSize, y + (int) shapeSize);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setUpModuleRectangles(w);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int moduleIndex = 0; moduleIndex<moduleRectangles.length; moduleIndex++){
            if (shape == SHAPE_CIRCLE) {
                float x = moduleRectangles[moduleIndex].centerX();
                float y = moduleRectangles[moduleIndex].centerY();

                if (moduleStatus[moduleIndex])
                    canvas.drawCircle(x, y, radius, paintFill);

                canvas.drawCircle(x, y, radius, paintOutline);
            }else {
                drawSquare(canvas,moduleIndex);
            }
        }

    }

    private void drawSquare(Canvas canvas, int moduleIndex){
        Rect moduleRectangle = moduleRectangles[moduleIndex];

        if (moduleStatus[moduleIndex])
            canvas.drawRect(moduleRectangle,paintFill);

        canvas.drawRect(moduleRectangle.left + (outlineWidth/2),
                moduleRectangle.top + (outlineWidth/2),
                moduleRectangle.right - (outlineWidth/2),
                moduleRectangle.bottom - (outlineWidth/2),
                paintOutline);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                int moduleIndex = findItemAtPoint(event.getX(),event.getY());
                onModuleSelected(moduleIndex);
                return true;

            case MotionEvent.ACTION_DOWN:
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void onModuleSelected(int moduleIndex) {
        if (moduleIndex == INVALID_INDEX)
            return;

        moduleStatus[moduleIndex] = !moduleStatus[moduleIndex];
        invalidate();
    }

    private int findItemAtPoint(float x, float y) {
        int moduleIndex = INVALID_INDEX;
        for (int i = 0; i < moduleRectangles.length; i++) {
           if (moduleRectangles[i].contains( (int) x,(int) y)){
               moduleIndex = i;
               break;
           }
        }
        return moduleIndex;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 0;
        int desiredHeight = 0;

        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = specWidth - getPaddingLeft() - getPaddingRight();
        int horizontalModulesThatCanFit = (int) (availableWidth / (shapeSize + spacing));

        maxHorizontalModules = Math.min(horizontalModulesThatCanFit,moduleStatus.length);

        desiredWidth = (int)((maxHorizontalModules * (shapeSize + spacing)) - spacing);
        desiredWidth += getPaddingLeft() + getPaddingRight();

        int rows = ((moduleStatus.length - 1) / maxHorizontalModules) + 1;

        desiredHeight = (int) ((rows * (shapeSize + spacing)) - spacing);
        desiredHeight += getPaddingBottom() + getPaddingTop();

        int width = resolveSizeAndState(desiredWidth,widthMeasureSpec,0);
        int height = resolveSizeAndState(desiredHeight,heightMeasureSpec,0);

        setMeasuredDimension(width,height);
    }

    public boolean[] getModuleStatus() {
        return moduleStatus;
    }

    public void setModuleStatus(boolean[] moduleStatus) {
        this.moduleStatus = moduleStatus;
    }
}
