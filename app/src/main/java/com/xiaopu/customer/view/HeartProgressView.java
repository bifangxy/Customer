package com.xiaopu.customer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.xiaopu.customer.R;

/**
 * Created by Administrator on 2017/9/13.
 */

public class HeartProgressView extends View {

    private float circleBorderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

    private Paint backCirclePaint;

    private Paint paintCircle = new Paint();

    private Paint gradientCirclePaint;

    private Paint gradientCircleBgPaint;

    private Paint smallTextPaint;

    private String content;

    private String describe;
    /*百分比*/
    private int progress = 0;

    public HeartProgressView(Context context) {
        super(context);
        init();
    }

    public HeartProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeartProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HeartProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        content = "--";
        describe = "最近一次测量:--";

        backCirclePaint = new Paint();
        backCirclePaint.setStyle(Paint.Style.STROKE);
        backCirclePaint.setAntiAlias(true);
        backCirclePaint.setColor(Color.parseColor("#ffffff"));
        backCirclePaint.setStrokeWidth(3);
        backCirclePaint.setAlpha(255);

//        backCirclePaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.OUTER));

        gradientCirclePaint = new Paint();
        gradientCirclePaint.setStyle(Paint.Style.STROKE);
        gradientCirclePaint.setAntiAlias(true);
        gradientCirclePaint.setColor(Color.parseColor("#ffffff"));
        gradientCirclePaint.setStrokeWidth(circleBorderWidth);
        gradientCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        gradientCirclePaint.setPathEffect(new CornerPathEffect(20));
        gradientCirclePaint.setAlpha(255);

        gradientCircleBgPaint = new Paint();
        gradientCircleBgPaint.setStyle(Paint.Style.STROKE);
        gradientCircleBgPaint.setAntiAlias(true);
        gradientCircleBgPaint.setColor(Color.parseColor("#de6562"));
        gradientCircleBgPaint.setStrokeWidth(circleBorderWidth);
        gradientCircleBgPaint.setStrokeCap(Paint.Cap.ROUND);
        gradientCircleBgPaint.setPathEffect(new CornerPathEffect(20));
        gradientCircleBgPaint.setAlpha(255);

        paintCircle = new Paint();
        paintCircle.setAlpha(255);
        paintCircle.setTextSize(100);
        paintCircle.setAntiAlias(true);//抗锯齿功能
        paintCircle.setColor(Color.parseColor("#ffffff"));

        smallTextPaint = new Paint();
        smallTextPaint.setAlpha(255);
        smallTextPaint.setTextSize(30);
        smallTextPaint.setAntiAlias(true);//抗锯齿功能
        smallTextPaint.setColor(Color.parseColor("#ffffff"));

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(new RectF(10, 10,
                getMeasuredWidth() - 50, getMeasuredHeight() * 2 - 70), 180, 180, false, backCirclePaint);

        canvas.drawArc(
                new RectF(60, 60,
                        getMeasuredWidth() - 100, getMeasuredHeight() * 2 - 150), 180, 180, false, gradientCircleBgPaint);

        canvas.drawArc(
                new RectF(60, 60,
                        getMeasuredWidth() - 100, getMeasuredHeight() * 2 - 150), 180, 180 * (progress / 150f), false, gradientCirclePaint);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.hr_kedu);
        canvas.drawBitmap(bitmap, null, new RectF(100, 100, getMeasuredWidth() - 140, (getMeasuredHeight() - 50)), null);

        canvas.drawText("0", 0, getMeasuredHeight(), smallTextPaint);
        canvas.drawText("150", getMeasuredWidth() - 75, getMeasuredHeight(), smallTextPaint);
        Rect bounds = new Rect();

        smallTextPaint.getTextBounds(describe, 0, describe.length(), bounds);
        canvas.drawText(describe, getMeasuredWidth() / 2 - bounds.width() / 2, getMeasuredHeight() - 5, smallTextPaint);

        paintCircle.getTextBounds(content, 0, content.length(), bounds);
        Paint.FontMetricsInt fontMetrics = paintCircle.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top + 80;
        canvas.drawText(content, getMeasuredWidth() / 2 - bounds.width() / 2, baseline, paintCircle);
        canvas.drawText("次/分", getMeasuredWidth() / 2 + bounds.width() / 2 + 10, baseline, smallTextPaint);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        this.content = progress + "";
        invalidate();
    }

    public void setDescribe(String describe) {
        this.describe = "最近一次测量:" + describe;
        invalidate();
    }
}
