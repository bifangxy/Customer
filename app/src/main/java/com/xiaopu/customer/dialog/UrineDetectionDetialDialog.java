
package com.xiaopu.customer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpUtils;


/**
 * Created by Administrator on 2016/8/9 0009.
 */
public class UrineDetectionDetialDialog {

    private Context mContext;

    private LayoutInflater mInflater;

    private View rootView;

    private WebView mWebView;

    private Dialog mAgreementDialog;

    private Button btSure;

    private MyClick mClick;

    private int mId;

    public UrineDetectionDetialDialog(Context context, int id) {
        mContext = context;
        mId = id;
        initView();
        initDate();
    }

    private void initView() {
        mInflater = LayoutInflater.from(mContext);
        rootView = mInflater.inflate(R.layout.dialog_urine_detial, null);
        mWebView = (WebView) rootView.findViewById(R.id.wv_uridetial);
        btSure = (Button) rootView.findViewById(R.id.bt_sure);
        mAgreementDialog = new Dialog(mContext, R.style.DialogStyle);
        mAgreementDialog.setCanceledOnTouchOutside(false);
        mAgreementDialog.setContentView(rootView);

    }

    public void show() {
        mAgreementDialog.show();
    }

    private void initDate() {
        mClick = new MyClick();
        btSure.setOnClickListener(mClick);
        mWebView.loadUrl(HttpConstant.Url_Server + "detection/urine/getDetectionDetail?id=" + mId);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

            }
        });
    }

    private void dismiss() {

        if (mAgreementDialog.isShowing()) {
            mAgreementDialog.dismiss();
        }
    }

    private class MyClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_sure:
                    mAgreementDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    }
}
