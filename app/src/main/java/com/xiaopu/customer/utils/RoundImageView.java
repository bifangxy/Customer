package com.xiaopu.customer.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xiaopu.customer.R;

/**
 * Created by Administrator on 2016/2/29 0029.
 * 自定义圆形头像框
 */
public class RoundImageView extends ImageView {
    private Context mContext;
    private int defaultColor = 0xFFFFFFFF;
    private int mBorderOutsideColor = 0;
    private int mBorderInsideColor = 0;
    private int mBorderThickness = 0;
    // 控件默认长、宽
    private int defaultWidth = 0;
    private int defaultHeight = 0;

    public RoundImageView(Context context) {
        super(context);
        mContext = context;
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setCustomAttributes(attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setCustomAttributes(attrs);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.roundedimageview);
        mBorderThickness = a.getDimensionPixelSize(R.styleable.roundedimageview_border_thickness, 0);
        mBorderOutsideColor = a.getColor(R.styleable.roundedimageview_border_outside_color, defaultColor);
        mBorderInsideColor = a.getColor(R.styleable.roundedimageview_border_inside_color, defaultColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        if (drawable.getClass() == NinePatchDrawable.class) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
        if (defaultWidth == 0) {
            defaultWidth = getWidth();
        }
        if (defaultHeight == 0) {
            defaultHeight = getHeight();
        }
        int radius = 0;
//        if (mBorderInsideColor != defaultColor && mBorderOutsideColor != defaultColor) {
//            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - 2 * mBorderThickness;
//            //画内圆（根据内圆和外圆的颜色来判断圆的半径）
//            drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderInsideColor);
//            //画外圆
//            drawCircleBorder(canvas, radius + mBorderThickness + mBorderThickness / 2, mBorderOutsideColor);
//        } else if (mBorderInsideColor != defaultColor && mBorderOutsideColor == defaultColor) {
//            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - mBorderThickness;
//            drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderInsideColor);
//        } else if (mBorderInsideColor == defaultColor && mBorderOutsideColor != defaultColor) {
//            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - mBorderThickness;
//            drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderOutsideColor);
//        } else {
//            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2;
//        }
        radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - 2 * mBorderThickness;
        drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderInsideColor);
        drawCircleBorder(canvas, radius + mBorderThickness + mBorderThickness / 2, mBorderOutsideColor);
        Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);
        canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight / 2 - radius, null);
    }

    private Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0;
        int squareHeight = 0;
        int x = 0, y = 0;
        Bitmap suqareBmp;
        if (bmpHeight > bmpWidth) {
            squareHeight = squareWidth = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            //从bmp的（x，y）处截取squareWidth*squareHeight像素区域放入新的Bitmap中
            suqareBmp = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else if (bmpHeight < bmpWidth) {
            squareHeight = squareWidth = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            suqareBmp = Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        } else {
            suqareBmp = bmp;
        }
        //判断裁剪后得到的图是否和实际尺寸相等，如不相等则对squareBmp进行缩放
        if (suqareBmp.getWidth() != diameter || suqareBmp.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(suqareBmp, diameter, diameter, true);
        } else {
            scaledSrcBmp = suqareBmp;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight());
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        //在得到的正方形上画圆
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2, scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        bmp = null;
        suqareBmp = null;
        scaledSrcBmp = null;
        return output;

        /*int width = bmp.getWidth();
        int height = bmp.getHeight();
        //正方形的边长
        int r = 0;
        //取最短边做边长
        if(width > height) {
            r = height;
        } else {
            r = width;
        }
        //构建一个bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
        //设置边缘光滑，去掉锯齿
        paint.setAntiAlias(true);
        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, r, r);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r/2, r/2, paint);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bmp, null, rect, paint);
        //返回已经绘画好的backgroundBmp
        return backgroundBmp;*/
    }

    private void drawCircleBorder(Canvas canvas, int radius, int color) {
        Paint paint = new Paint();
        /* 去锯齿 */
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
        /* 设置paint的　style　为STROKE：空心 */
        paint.setStyle(Paint.Style.STROKE);
        /* 设置paint的外框宽度 */
        paint.setStrokeWidth(mBorderThickness);
        canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);
    }
}
