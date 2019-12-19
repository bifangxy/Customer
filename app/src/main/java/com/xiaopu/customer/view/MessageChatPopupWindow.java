package com.xiaopu.customer.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.xiaopu.customer.R;

/**
 * Created by Administrator on 2017/12/15.
 */

public class MessageChatPopupWindow extends PopupWindow {

    private Activity mContext;

    private View contentView;

    private ImageView iv_authorization;

    private ImageView iv_authorization_success;

    private ImageView iv_cancel_order;

    private ImageView iv_finish_order;

    private ImageView iv_appeal_order;

    private ImageView iv_order_appealing;

    private ImageView iv_order_appealed;

    private ImageView iv_order_comment;

    private ImageView iv_order_commented;

    private ImageView iv_order_canceled;

    private int width = 0;

    private int height = 0;

    private Drawable background_drawable;


    public MessageChatPopupWindow(Activity context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.popup_window_chat, null, false);
        contentView.setBackgroundResource(R.drawable.auto_clean_bg);
        setContentView(contentView);
        initView();
    }

    private void initView() {

        iv_authorization = (ImageView) contentView.findViewById(R.id.iv_authorization);
        iv_authorization_success = (ImageView) contentView.findViewById(R.id.iv_authorization_success);
        iv_cancel_order = (ImageView) contentView.findViewById(R.id.iv_cancel_order);
        iv_finish_order = (ImageView) contentView.findViewById(R.id.iv_finish_order);
        iv_appeal_order = (ImageView) contentView.findViewById(R.id.iv_appeal_order);
        iv_order_appealing = contentView.findViewById(R.id.iv_order_appealing);
        iv_order_appealed = contentView.findViewById(R.id.iv_order_appealed);
        iv_order_comment = contentView.findViewById(R.id.iv_order_comment);
        iv_order_commented = contentView.findViewById(R.id.iv_order_commented);
        iv_order_canceled = contentView.findViewById(R.id.iv_order_canceled);

    }

    public void initListener(View.OnClickListener listener) {
        iv_authorization.setOnClickListener(listener);
        iv_cancel_order.setOnClickListener(listener);
        iv_finish_order.setOnClickListener(listener);
        iv_appeal_order.setOnClickListener(listener);
        iv_order_appealing.setOnClickListener(listener);
        iv_order_appealed.setOnClickListener(listener);
        iv_order_comment.setOnClickListener(listener);
        iv_order_commented.setOnClickListener(listener);
        iv_order_canceled.setOnClickListener(listener);
    }

    public void initPopupWindow() {
        if (background_drawable == null) {
            setBackgroundDrawable(new ColorDrawable(0x00000000));
        } else {
            setBackgroundDrawable(background_drawable);
        }
        if (width == 0) {
            setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            setWidth(width);
        }
        if (height == 0) {
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            setHeight(height);
        }
        setOutsideTouchable(false);
        setFocusable(true);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
                lp.alpha = 1f;
                mContext.getWindow().setAttributes(lp);
            }
        });
    }

    /**
     * 设置订单状态
     * orderState 1,医生尚未回复，不能取消，2，医生尚未回复，可以取消，3，医生已回复，4，订单已完成，5  订单申述中  6  订单申诉完成  7 订单已取消
     * auth_state 0，未授权，1，已授权
     * comment_state 0,未评论，1，已评论
     *
     * @param order_state
     * @param auth_state
     */
    public void setState(int order_state, int auth_state, int comment_state) {

        iv_authorization.setVisibility(View.GONE);
        iv_authorization_success.setVisibility(View.GONE);
        iv_cancel_order.setVisibility(View.GONE);
        iv_finish_order.setVisibility(View.GONE);
        iv_appeal_order.setVisibility(View.GONE);
        iv_order_appealing.setVisibility(View.GONE);
        iv_order_appealed.setVisibility(View.GONE);
        iv_order_comment.setVisibility(View.GONE);
        iv_order_commented.setVisibility(View.GONE);
        iv_order_canceled.setVisibility(View.GONE);

        if (order_state != 7) {
            if (auth_state == 0) {
                iv_authorization.setVisibility(View.VISIBLE);
            } else if (auth_state == 1) {
                iv_authorization_success.setVisibility(View.VISIBLE);
            }

            if (order_state > 3) {
                if (comment_state == 0) {
                    iv_order_comment.setVisibility(View.VISIBLE);
                } else if (comment_state == 1) {
                    iv_order_comment.setVisibility(View.GONE);
                }
            }
        }

        switch (order_state) {
            case 1:
                iv_cancel_order.setVisibility(View.VISIBLE);
                break;
            case 2:
                iv_cancel_order.setVisibility(View.VISIBLE);
                break;
            case 3:
                iv_finish_order.setVisibility(View.VISIBLE);
                break;
            case 4:
                iv_appeal_order.setVisibility(View.VISIBLE);
                break;
            case 5:
                iv_order_appealing.setVisibility(View.VISIBLE);
                break;
            case 6:
                iv_order_appealing.setVisibility(View.VISIBLE);
                break;
            case 7:
                iv_order_canceled.setVisibility(View.VISIBLE);
                break;
        }

    }

    public void showPopupWindow(View view) {
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = 0.8f;
        mContext.getWindow().setAttributes(lp);
        if (width != 0) {
            showAsDropDown(view, (150 - width), 30);
        } else {
            getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            float scale = mContext.getResources().getDisplayMetrics().density;
            showAsDropDown(view, 20 - ((int) (getContentView().getMeasuredWidth() * scale)), 20);
        }

    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    public Drawable getBackground_drawable() {
        return background_drawable;
    }

    public void setBackground_drawable(Drawable background_drawable) {
        this.background_drawable = background_drawable;
    }


}
