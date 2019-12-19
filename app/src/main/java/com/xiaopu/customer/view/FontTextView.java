package com.xiaopu.customer.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Administrator on 2017/5/26.
 */

public class FontTextView extends android.support.v7.widget.AppCompatTextView {

    private Context mContext;
    private String TypefaceName = "black";

    public String getTypefaceName() {
        return TypefaceName;
    }

    public void setTypefaceName(String typefaceName) {
        TypefaceName = typefaceName;
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "font/" + TypefaceName + ".ttf");
        this.setTypeface(typeface);
        System.gc();
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
//        int resouceId = attrs.getAttributeResourceValue(null, "typefaceName", 0);
//        if (resouceId != 0) {
//            TypefaceName = context.getResources().getString(resouceId);
//        } else {
//            TypefaceName = attrs.getAttributeValue(null, "typefaceName");
//        }
//        if (TypefaceName != null && !"".equals(TypefaceName)) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/" + TypefaceName + ".ttf");
        this.setTypeface(typeface);
//        }
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        // 先判断是否配置的资源文件
//        int resouceId = attrs.getAttributeResourceValue(null, "typefaceName", 0);
//        if (resouceId != 0) {
//            TypefaceName = context.getResources().getString(resouceId);
//        } else {
//            TypefaceName = attrs.getAttributeValue(null, "typefaceName");
//        }
//        if (TypefaceName != null && !"".equals(TypefaceName)) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/" + TypefaceName + ".ttf");
        this.setTypeface(typeface);
//        }
    }

    public FontTextView(Context context) {
        super(context);
//        this.mContext = context;
//        if (TypefaceName != null && !"".equals(TypefaceName)) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/" + TypefaceName + ".ttf");
        this.setTypeface(typeface);
//        }
    }
}
