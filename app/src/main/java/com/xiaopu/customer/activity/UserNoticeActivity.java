package com.xiaopu.customer.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;
import com.xiaopu.customer.utils.T;

/**
 * Created by Administrator on 2018/7/25.
 */

public class UserNoticeActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = UserNoticeActivity.class.getSimpleName();

    private Context mContext;

    private TextView tv_call_pooai;

    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notice);
        mContext = this;
        initActionBar("用户须知");
        initView();
        initData();
        initListener();
    }

    private void initView() {
        tv_call_pooai = findViewById(R.id.tv_call_pooai);
    }

    private void initData() {
        builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示");
        builder.setMessage("是否要拨打客服？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tellPhone();
            }
        });
        builder.setNegativeButton("取消", null);
    }

    private void initListener() {
        tv_call_pooai.setOnClickListener(this);
    }

    private void tellPhone() {
        if (ActivityCompat.checkSelfPermission(UserNoticeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserNoticeActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 12);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4001528518"));
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_call_pooai:
                builder.show();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 12) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4001528518"));
                startActivity(intent);
            } else {
                T.showShort("无法获取相应的权限");
            }
        }
    }
}
