package com.xiaopu.customer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaopu.customer.R;

/**
 * Created by Administrator on 2016/7/2 0002.
 */

public class DialogLoading extends Dialog {
    private ImageView iv;
    private AnimationDrawable animationDrawable;
    private TextView tv_loading;

    public DialogLoading(Context context) {
        super(context, R.style.DialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        tv_loading = (TextView) findViewById(R.id.tv_loading);
        iv = (ImageView) findViewById(R.id.iv_animation);
//        tv_loading.setText(content);
        animationDrawable = (AnimationDrawable) iv.getDrawable();
        animationDrawable.start();
        setCancelable(false);
    }


}
