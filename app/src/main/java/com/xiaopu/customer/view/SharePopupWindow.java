package com.xiaopu.customer.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xiaopu.customer.R;

/**
 * Created by Administrator on 2017/6/29.
 */

public class SharePopupWindow extends PopupWindow {

    private static final String LOG_TAG = SharePopupWindow.class.getSimpleName();

    private Context mContext;

    private View.OnClickListener mClick;

    private View conventView;

    private ImageView iv_weChat_friend;

    private ImageView iv_weChat_moments;

    private ImageView iv_weibo;

    private TextView tv_cancle;

    public SharePopupWindow(Context context, View.OnClickListener mClick) {
        super(context);
        this.mContext = context;
        this.mClick = mClick;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conventView = inflater.inflate(R.layout.layout_dialog_share, null);
        iv_weChat_friend = conventView.findViewById(R.id.weChat_friend);
        iv_weChat_moments = conventView.findViewById(R.id.weChat_moments);
        iv_weibo = conventView.findViewById(R.id.weibo);
        tv_cancle = conventView.findViewById(R.id.tv_cancle);
    }


    private void initData() {
        this.setContentView(conventView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.PopupAnimation);
        ColorDrawable dw = new ColorDrawable(0x80000000);

        this.setBackgroundDrawable(dw);
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        setBackgroundAlpha(0.5f);
    }

    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        setBackgroundAlpha(1f);
    }

    private void initListener() {
        iv_weChat_friend.setOnClickListener(mClick);
        iv_weChat_moments.setOnClickListener(mClick);
        iv_weibo.setOnClickListener(mClick);
        tv_cancle.setOnClickListener(mClick);
    }

}
