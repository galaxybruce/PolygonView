package com.totoro.xkf.polygonview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class PolygonView extends View {
    private Paint edgePaint;
    private Paint areaPaint;
    private Paint textPaint;
    private Paint circlePaint;

    private int width;
    private int height;
    private float maxRadius;
    private float circleRadiusRate;
    private int edgeCount;
    private int loopCount;
    private int drawLoopCount;
    private float angle;
    private int[] areaColors;
    private List<Float> pointValue;
    private List<String> pointName;
    private List<Bitmap> pointBitmap;
    private List<Float> maxPointXList;
    private List<Float> maxPointYList;

    private final int roteAngle = -90;

    public PolygonView(Context context) {
        super(context);
    }

    public PolygonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    public PolygonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void initPaint() {
        edgePaint = new Paint();
        areaPaint = new Paint();
        textPaint = new Paint();
        circlePaint = new Paint();

        edgePaint.setStyle(Paint.Style.STROKE);
        edgePaint.setAntiAlias(true);

        areaPaint.setDither(true);
        areaPaint.setStyle(Paint.Style.FILL);
        areaPaint.setAntiAlias(true);

        circlePaint.setAlpha(0);
        circlePaint.setAntiAlias(true);
        circlePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(35);
        textPaint.setAntiAlias(true);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Polygon);
        initPaint();
        setTextColor(typedArray.getColor(R.styleable.Polygon_textColor, Color.BLACK));
        setLoopCount(typedArray.getInteger(R.styleable.Polygon_loopCount, 0));
        setDrawLoopCount(typedArray.getInteger(R.styleable.Polygon_drawLoopCount, 0));
        setEdgeCount(typedArray.getInteger(R.styleable.Polygon_edgeCount, 0));
        setAreaColor(typedArray.getColor(R.styleable.Polygon_areaColor, Color.BLUE));
        setEdgeColor(typedArray.getColor(R.styleable.Polygon_edgeColor, Color.GRAY));
        setEdgeWidth(typedArray.getDimension(R.styleable.Polygon_edgeWidth, 2));
        setCircleRadius(typedArray.getFloat(R.styleable.Polygon_circleRadiusRate, 0.0f));
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        maxRadius = (float) ((width / 2) * 0.7);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!canDraw()) {
            return;
        }
        canvas.translate(width / 2f, height / 2f);
        computeMaxPoint();
        drawPolygon(canvas);
        drawLine(canvas);
        drawArea(canvas);
        drawAreaDot(canvas);
        drawAreaCircle(canvas);
        drawText(canvas);
        drawBitmap(canvas);
    }

    /*
        ?????????????????????????????????????????????????????????????????????
     */
    public void computeMaxPoint() {
        maxPointXList = new ArrayList<>();
        maxPointYList = new ArrayList<>();
        for (int i = 0; i < edgeCount; i++) {
            float currentAngle = i * angle + roteAngle;
            float currentX = (float) (maxRadius * Math.cos((currentAngle / 180) * Math.PI));
            float currentY = (float) (maxRadius * Math.sin((currentAngle / 180) * Math.PI));
            maxPointXList.add(currentX);
            maxPointYList.add(currentY);
        }
    }

    /*
       ???????????????????????????
    */
    private void drawPolygon(Canvas canvas) {
//        Path path = new Path();
//        for (int i = 0; i < loopCount; i++) {
//            path.reset();
//            // ?????????????????????????????????????????????????????????
//            float rate = computeRate(i + 1, loopCount);
//            for (int j = 0; j < edgeCount; j++) {
//                float currentX = maxPointXList.get(j) * rate;
//                float currentY = maxPointYList.get(j) * rate;
//                if (j == 0) {
//                    path.moveTo(currentX, currentY);
//                } else {
//                    path.lineTo(currentX, currentY);
//                }
//            }
//            path.close();
//            canvas.drawPath(path, edgePaint);
//        }

        for (int i = 0; i < loopCount; i++) {
            if(i + drawLoopCount >= loopCount) {
                float rate = computeRate(i + 1, loopCount);
                canvas.drawCircle(0, 0, maxRadius * rate, edgePaint);
            }
        }
    }

    private float computeRate(float value, float max) {
        return value / max;
    }

    /*
        ????????????????????????????????????
    */
    private void drawLine(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < edgeCount; i++) {
            path.reset();
            path.lineTo(maxPointXList.get(i), maxPointYList.get(i));
            canvas.drawPath(path, edgePaint);
        }
    }

    /*
        ?????????????????????????????????
     */
    private void drawArea(Canvas canvas) {
        Path path = new Path();
        //???????????????path????????????????????????????????????????????????????????????
        for (int i = 0; i < edgeCount; i++) {
            float rate = pointValue.get(i);
            float currentX = maxPointXList.get(i) * rate;
            float currentY = maxPointYList.get(i) * rate;
            if (i == 0) {
                path.moveTo(currentX, currentY);
            } else {
                path.lineTo(currentX, currentY);
            }
        }
        path.close();
        canvas.drawPath(path, areaPaint);
    }

    /*
       ??????????????????????????????
    */
    private void drawAreaDot(Canvas canvas) {
        if(areaColors == null || areaColors.length < edgeCount) {
            return;
        }
        final int dotRadius = 10;
        final Paint paint = new Paint();
        for (int i = 0; i < edgeCount; i++) {
            float rate = pointValue.get(i);
            if(rate == 0) {
                continue;
            }
            float currentX = maxPointXList.get(i) * rate;
            float currentY = maxPointYList.get(i) * rate;
            paint.reset();
            // ????????????
            paint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL));
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(areaColors[i]);
            canvas.drawCircle(currentX, currentY, dotRadius, paint);
        }
    }

    /*
       ??????????????????
    */
    private void drawAreaCircle(Canvas canvas) {
        canvas.drawCircle(0, 0, maxRadius * circleRadiusRate, circlePaint);
    }

    /*
        ????????????
     */
    private void drawText(Canvas canvas) {
        if (pointName == null) {
            return;
        }
        //????????????????????????????????????????????????????????????????????????????????????????????????
        for (int i = 0; i < pointName.size(); i++) {
            //????????????????????????????????????????????????????????????????????????x???????????????????????????????????????????????????
            float currentAngle = i * angle;
            //180?????????????????????????????????????????????????????????????????????
            if (currentAngle == 180) {
                float currentX = maxPointXList.get(i) * 1.1f;
                float currentY = maxPointYList.get(i) * 1.1f;
                canvas.drawText(pointName.get(i), currentX - (textPaint.getTextSize() / 4)
                        * (pointName.get(i).length()), currentY, textPaint);
            } else {
                canvas.save();
                float currentX = maxPointXList.get(i) * 1.2f;
                float currentY = maxPointYList.get(i) * 1.2f;
                //??????????????????????????????????????????
//                canvas.rotate(currentAngle);
                float textWidth = textPaint.measureText(pointName.get(i));
                canvas.drawText(pointName.get(i), currentX - textWidth / 2f, currentY, textPaint);
                canvas.restore();
            }
        }
    }

    /*
        ????????????
     */
    private void drawBitmap(Canvas canvas) {
        if (pointBitmap == null) {
            return;
        }
        float destWidth = 100f;
        float destHeight = 100f;
        for (int i = 0; i < pointBitmap.size(); i++) {
            Bitmap bitmap = pointBitmap.get(i);
            int bWidth = bitmap.getWidth();
            int bHeight = bitmap.getHeight();
            float currentX = maxPointXList.get(i) * 1.3f;
            float currentY = maxPointYList.get(i) * 1.3f;
            float left = currentX - bWidth / 2f;
            float top = currentY - bHeight / 2f;
            left = Math.min(Math.max(left, width / -2f), width / 2f - bWidth);
            top = Math.min(Math.max(top, height / -2f), height / 2f - bHeight);

            Rect src = new Rect(0, 0, bWidth, bHeight);
            RectF dest = new RectF(currentX - destWidth / 2f, currentY - destHeight / 2f,
                    currentX - destWidth / 2f + destWidth, currentY - destHeight / 2f + destHeight);
            canvas.drawBitmap(bitmap, src, dest, textPaint);
//            canvas.drawBitmap(bitmap, left, top, textPaint);
        }
    }

    /*
        ???????????????????????????
     */
    public void draw() {
        if (canDraw()) {
            final Float[] trueValues = pointValue.toArray(new Float[pointValue.size()]);
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(circleRadiusRate, 1);
            valueAnimator.setDuration(1000);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float rate = animation.getAnimatedFraction();
                    for (int i = 0; i < pointValue.size(); i++) {
                        pointValue.set(i, trueValues[i] * rate);
                    }
                    invalidate();
                }
            });
            valueAnimator.start();
        }
    }

    /*
        ????????????????????????
        ?????????
        loopCount(????????????)????????????0
        eageCount(??????)????????????3
        pointValue(????????????)?????????null??????size??????????????????
     */
    private boolean canDraw() {
        if (loopCount <= 0 || edgeCount <= 2 || pointValue == null
                || pointValue.size() < edgeCount) {
            return false;
        }
        return true;
    }

    public void clearData() {
        setDefValue();
        draw();
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
    }

    public void setAreaColor(int color) {
        areaPaint.setColor(color);
        areaPaint.setAlpha(120);
    }

    public void setAreaColors(int[] colors) {
        this.areaColors = colors;
        SweepGradient sweepGradient = new SweepGradient(0,0, colors, null);
        Matrix matrix = new Matrix();
        matrix.setRotate(roteAngle,0, 0);
        sweepGradient.setLocalMatrix(matrix);
        areaPaint.setShader(sweepGradient);
        areaPaint.setAlpha(150);
    }

    public void setEdgeColor(int color) {
        edgePaint.setColor(color);
    }

    public void setEdgeWidth(float width) {
        edgePaint.setStrokeWidth(width);
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public void setDrawLoopCount(int drawLoopCount) {
        this.drawLoopCount = drawLoopCount;
    }

    public void setEdgeCount(int edgeCount) {
        this.edgeCount = edgeCount;
        angle = 360f / edgeCount;

        setDefValue();
    }

    public void setPointValue(List<Float> pointValue) {
        for (int i = 0; i < pointValue.size(); i++) {
            if(pointValue.get(i) <= circleRadiusRate) {
                pointValue.set(i, circleRadiusRate);
            }
        }
        this.pointValue = pointValue;
    }

    private void setDefValue() {
        this.pointValue = new ArrayList<>();
        for (int i = 0; i < edgeCount; i++) {
            pointValue.add(0.0f);
        }
    }

    public void setPointName(List<String> pointName) {
        this.pointName = pointName;
    }

    public void setPointBitmap(List<Bitmap> pointBitmap) {
        this.pointBitmap = pointBitmap;
    }

    public void setCircleRadius(float circleRadiusRate) {
        this.circleRadiusRate = circleRadiusRate;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public List<Float> getPointValue() {
        return pointValue;
    }

    public List<String> getPointName() {
        return pointName;
    }
}
