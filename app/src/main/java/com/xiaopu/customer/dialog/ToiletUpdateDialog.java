package com.xiaopu.customer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.activity.ToiletUpdateActivity;

/**
 * 马桶升级对话框
 * Created by Administrator on 2017/12/27.
 */

public class ToiletUpdateDialog extends Dialog {
    private Context mContext;

    private TextView tv_update_version;

    private TextView tv_update_file_size;

    private Button bt_update_toilet;

    private ImageView iv_dismiss;

    private String url;

    private String content;


    public ToiletUpdateDialog(@NonNull Context context) {
        super(context, R.style.alert_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_toilet_update);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        initView();
        initListener();
    }

    private void initView() {
        tv_update_version = (TextView) findViewById(R.id.tv_update_version);
        tv_update_file_size = (TextView) findViewById(R.id.tv_update_file_size);
        bt_update_toilet = (Button) findViewById(R.id.bt_update_toilet);
        iv_dismiss = (ImageView) findViewById(R.id.iv_dismiss);
    }

    private void initListener() {
        iv_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        bt_update_toilet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent mIntent = new Intent(mContext, ToiletUpdateActivity.class);
                mIntent.putExtra("url", url);
                mIntent.putExtra("content", content);
                mContext.startActivity(mIntent);
            }
        });
    }


    public void initContext(Context context) {
        mContext = context;
    }

    public void setVersionCode(String versionCode) {
        tv_update_version.setText("发现新版本(V" + versionCode + ")");
    }

    public void setContent(String content) {
        this.content = content;
        tv_update_file_size.setText(content);
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
