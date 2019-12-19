package com.xiaopu.customer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Administrator on 2017/5/19.
 * 自定义圆形进度条界面
 */

public class GradientProgressBar extends View {
    /*圆弧线宽*/
    private float circleBorderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
    /*内边距*/
    private float circlePadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
    /*绘制圆周的画笔*/
    private Paint backCirclePaint;
    /*绘制圆点画笔*/
    private Paint paintCircle = new Paint();
    /*绘制圆弧画笔*/
    private Paint gradientCirclePaint;
    /*百分比*/
    private int percent = 0;
    /*渐变圆周颜色数组*/
    public static final int[] SWEEP_GRADIENT_COLORS = new int[]{Color.parseColor("#bfe5f7"), Color.parseColor("#0091db"), Color.WHITE, Color.parseColor("#bfe5f7")};
    /*渐变位置数组*/
    private float[] positions = new float[]{0f, 0.747f, 0.7470001f, 0.9999f};

    public GradientProgressBar(Context context) {
        super(context);
        init();
    }

    public GradientProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GradientProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        backCirclePaint = new Paint();
        backCirclePaint.setStyle(Paint.Style.STROKE);
        backCirclePaint.setAntiAlias(true);
        backCirclePaint.setColor(Color.parseColor("#ededed"));
        backCirclePaint.setStrokeWidth(circleBorderWidth);
        backCirclePaint.setAlpha(255);

//        backCirclePaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.OUTER));

        gradientCirclePaint = new Paint();
        gradientCirclePaint.setStyle(Paint.Style.STROKE);
        gradientCirclePaint.setAntiAlias(true);
        gradientCirclePaint.setColor(Color.parseColor("#0091db"));
        gradientCirclePaint.setStrokeWidth(circleBorderWidth);
        gradientCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        gradientCirclePaint.setPathEffect(new CornerPathEffect(10));
        gradientCirclePaint.setAlpha(255);

        paintCircle = new Paint();
        paintCircle.setStyle(Paint.Style.FILL);//设置填充样式
        paintCircle.setAlpha(255);
        paintCircle.setAntiAlias(true);//抗锯齿功能
        paintCircle.setColor(Color.parseColor("#0091db"));

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(Math.min(measureWidth, measureHeight), Math.min(measureWidth, measureHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1.绘制灰色背景圆环
        canvas.drawArc(
                new RectF(circlePadding * 2, circlePadding * 2,
                        getMeasuredWidth() - circlePadding * 2, getMeasuredHeight() - circlePadding * 2), -90, 360, false, backCirclePaint);
        //2.绘制颜色渐变圆环
        SweepGradient mColorShader = new SweepGradient(getMeasuredWidth() / 2, getMeasuredHeight() / 2, SWEEP_GRADIENT_COLORS, positions);
        gradientCirclePaint.setShader(mColorShader);
        canvas.drawArc(
                new RectF(circlePadding * 2, circlePadding * 2,
                        getMeasuredWidth() - circlePadding * 2, getMeasuredHeight() - circlePadding * 2), -90, (float) (percent / 100.0) * 360, false, gradientCirclePaint);


       /* canvas.translate(getWidth() / 2, getHeight() / 2);//这时候的画布已经移动到了中心位置
        canvas.rotate((float) (percent / 100.0) * 360 + 90);
        float centerX = (getMeasuredHeight() - 4 * circlePadding) / 2;
        canvas.drawCircle(-centerX, 0, 12, paintCircle);
        canvas.rotate(-((float) (percent / 100.0) * 360) + 90);*/
    }

    /**
     * 设置百分比
     *
     * @param newpercent
     */
    public void setPercent(int newpercent) {
        if (percent < 0) {
            percent = 0;
        } else if (percent > 100) {
            percent = 100;
        }
        this.percent = newpercent;
        invalidate();
    }
}
