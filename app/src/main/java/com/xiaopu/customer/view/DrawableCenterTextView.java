package com.xiaopu.customer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * drawableTop居中显示TextView
 * Created by Administrator on 2018/5/29.
 */

public class DrawableCenterTextView extends android.support.v7.widget.AppCompatTextView {

    public DrawableCenterTextView(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
    }

    public DrawableCenterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableCenterTextView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            //获取TextView的drawableTop图片
            Drawable drawableTop = drawables[1];
            if (drawableTop != null) {
                Rect rect = new Rect();
                getPaint().getTextBounds(getText().toString(), 0, getText().toString().length(), rect);
                //获取文字的高度
                float textHeight = rect.height();
                //获取drawablePadding
                int drawablePadding = getCompoundDrawablePadding();
                //获取drawable高度
                int drawableHeight = 0;
                drawableHeight = drawableTop.getIntrinsicHeight();
                //计算得出图片和文字整体高度
                float bodyHeight = textHeight + drawableHeight + drawablePadding;
                //将整体进行平移
                canvas.translate(0, (getHeight() - bodyHeight) / 2);
            }
        }
        super.onDraw(canvas);
    }
}
