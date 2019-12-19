package com.xiaopu.customer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaopu.customer.R;

/**
 * Created by Administrator on 2018/7/19.
 */

public class CustomizeDialog extends Dialog {
    private Context context;

    private ImageView ivCustomizeImage;

    private TextView tvTitle;

    private TextView tvContent;

    private TextView tvCancel;

    private TextView tvConfirm;

    private View divideLine;

    private int type;

    private Drawable customizeImage;

    private String titleText;

    private String contentText;

    private String cancelText;

    private String confirmText;

    private OnCustomizeClickListener confirmClickListener;

    private OnCustomizeClickListener cancelClickListener;

    public static final int NORMAL_TYPE = 0;

    public static final int SUCCESS_TYPE = 1;

    public static final int CHOOSE_TYPE = 2;

    public static final int WARNING_TYPE = 3;

    public static final int ERROR_TYPE = 4;

    public static final int CUSTOMIZE_TYPE = 5;

    public static interface OnCustomizeClickListener {
        public void onClick(CustomizeDialog customizeDialog);
    }

    private CustomizeDialog(Builder builder) {
        super(builder.context, R.style.DialogStyle);
        context = builder.context;
        type = builder.type;
        customizeImage = builder.customizeImage;
        titleText = builder.titleText;
        contentText = builder.contentText;
        cancelText = builder.cancelText;
        confirmText = builder.confirmText;
        confirmClickListener = builder.confirmClickListener;
        cancelClickListener = builder.cancelClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_customize);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        ivCustomizeImage = findViewById(R.id.iv_customize_image);
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        tvCancel = findViewById(R.id.tv_cancel);
        tvConfirm = findViewById(R.id.tv_confirm);
        divideLine = findViewById(R.id.view_dividing_line);
        initTypeView();
    }

    private void initTypeView() {
        switch (type) {
            case NORMAL_TYPE:
                ivCustomizeImage.setVisibility(View.GONE);
                tvCancel.setVisibility(View.GONE);
                divideLine.setVisibility(View.GONE);
                tvConfirm.setBackgroundResource(R.drawable.selector_half);
                break;
            case SUCCESS_TYPE:
                ivCustomizeImage.setImageResource(R.mipmap.icon_succcess);
                tvTitle.setVisibility(View.GONE);
                tvCancel.setVisibility(View.GONE);
                divideLine.setVisibility(View.GONE);
                tvConfirm.setBackgroundResource(R.drawable.selector_half);
                break;
            case CHOOSE_TYPE:
                ivCustomizeImage.setVisibility(View.GONE);
                break;
            case WARNING_TYPE:
                tvTitle.setVisibility(View.GONE);
                ivCustomizeImage.setImageResource(R.mipmap.icon_warning);
                break;
            case ERROR_TYPE:
                ivCustomizeImage.setImageResource(R.mipmap.icon_error);
                tvTitle.setVisibility(View.GONE);
                tvCancel.setVisibility(View.GONE);
                divideLine.setVisibility(View.GONE);
                tvConfirm.setBackgroundResource(R.drawable.selector_half);
                break;
            case CUSTOMIZE_TYPE:
                tvTitle.setVisibility(View.GONE);
                tvCancel.setVisibility(View.GONE);
                divideLine.setVisibility(View.GONE);
                tvConfirm.setBackgroundResource(R.drawable.selector_half);
                break;
            default:
                break;
        }
    }

    private void initData() {
        if (customizeImage != null)
            ivCustomizeImage.setImageDrawable(customizeImage);
        if (titleText != null)
            tvTitle.setText(titleText);
        if (contentText != null)
            tvContent.setText(contentText);
        if (cancelText != null)
            tvCancel.setText(cancelText);
        if (confirmText != null)
            tvConfirm.setText(confirmText);
    }

    private void initListener() {
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelClickListener != null)
                    cancelClickListener.onClick(CustomizeDialog.this);
                else
                    dismiss();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmClickListener != null)
                    confirmClickListener.onClick(CustomizeDialog.this);
                else
                    dismiss();
            }
        });


    }

    public static class Builder {
        private Context context;

        private int type;

        private Drawable customizeImage;

        private String titleText;

        private String contentText;

        private String cancelText;

        private String confirmText;

        private OnCustomizeClickListener confirmClickListener;

        private OnCustomizeClickListener cancelClickListener;

        public Builder(Context context) {
            this(context, NORMAL_TYPE);
        }

        public Builder(Context context, int type) {
            this.context = context;
            this.type = type;
        }

        public void setCustomizeImage(Drawable customizeImage) {
            this.customizeImage = customizeImage;
        }

        public void setTitleText(String titleText) {
            this.titleText = titleText;
        }

        public void setContentText(String contentText) {
            this.contentText = contentText;
        }

        public void setCancelText(String cancelText) {
            this.cancelText = cancelText;
        }

        public void setConfirmText(String confirmText) {
            this.confirmText = confirmText;
        }

        public void setConfirmClickListener(OnCustomizeClickListener clickListener) {
            this.confirmClickListener = clickListener;
        }

        public void setCancelClickListener(OnCustomizeClickListener clickListener) {
            this.cancelClickListener = clickListener;
        }

        public CustomizeDialog builder() {
            return new CustomizeDialog(this);
        }

        public void show() {
            new CustomizeDialog(this).show();
        }
    }

}
