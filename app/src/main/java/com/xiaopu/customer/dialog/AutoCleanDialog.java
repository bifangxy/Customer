package com.xiaopu.customer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.xiaopu.customer.R;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2017/12/11.
 */

public class AutoCleanDialog extends Dialog {
    private Context mContext;

    private GifImageView gf_loading;

    private int resId;

    public AutoCleanDialog(@NonNull Context context) {
        super(context, R.style.alert_dialog);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_auto_clean);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView();
        initData();
    }

    private void initView() {
        gf_loading = (GifImageView) findViewById(R.id.gf_loading);
        gf_loading.setImageResource(resId);
    }

    private void initData() {

    }

    public void setImageResource(int resId) {
        this.resId = resId;
    }
}
