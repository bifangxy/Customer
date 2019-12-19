package com.xiaopu.customer.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public class ControlKeyBoard {
    private Activity mContext;
    private View mRootView;
    private View mScrollView;
    public ControlKeyBoard(Activity context, View rootView, View scrollView){
        mContext = context;
        mRootView = rootView;
        mScrollView = scrollView;
        controlKeyboardLayout();
    }

    private void controlKeyboardLayout() {
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private Rect r = new Rect();

            @Override
            public void onGlobalLayout() {
                //获取当前界面可视部分
                mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int screenHeight = mContext.getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                int heightDifference = screenHeight - r.bottom;
                mScrollView.scrollTo(0, heightDifference);
            }
        });
    }
}
