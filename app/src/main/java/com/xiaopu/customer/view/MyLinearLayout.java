package com.xiaopu.customer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.xiaopu.customer.ApplicationXpClient;

/**
 * Created by Administrator on 2016/9/9 0009.
 */
public class MyLinearLayout extends View {
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 要画图形，最起码要有三个对象：
     * 1.颜色对象 Color
     * 2.画笔对象 Paint
     * 3.画布对象 Canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        Paint paintsmall = new Paint();
        //设置画笔颜色为红色
        paint.setColor(Color.parseColor("#f5f5f5"));
        paintsmall.setColor(Color.parseColor("#f5f5f5"));
        //4982  7580
        //设置画出的线的 粗细程度
        paint.setStrokeWidth((float) 3.0);
        paintsmall.setStrokeWidth((float) 1.0);
        //线
        for (int i = 0, j = 0; i < ApplicationXpClient.widthpx; i += 40) {
            canvas.drawLine(i, j, i, ApplicationXpClient.heightpx, paint);
        }
        for (int j = 0, i = 0; j < ApplicationXpClient.heightpx; j += 40) {
            canvas.drawLine(i, j, ApplicationXpClient.widthpx, j, paint);
        }

        //细线
        for (int i = 0, j = 0; i < ApplicationXpClient.widthpx; i += 10) {
            canvas.drawLine(i, j, i, ApplicationXpClient.heightpx, paintsmall);
        }
        for (int i = 0, j = 0; j < ApplicationXpClient.heightpx; j += 10) {
            canvas.drawLine(i, j, ApplicationXpClient.widthpx, j, paintsmall);
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
