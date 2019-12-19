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
import android.widget.Button;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBaseWithoutHXListen;
import com.xiaopu.customer.R;
import com.xiaopu.customer.utils.T;

/**
 * Created by Administrator on 2016/7/5 0005.
 */
public class BeginGuideActivity extends ActivityBaseWithoutHXListen implements View.OnClickListener {
    private static String LOG_TAG = BeginGuideActivity.class.getSimpleName();

    private Context mContext;

    private Button bt_login;

    private Button bt_register;

    private TextView tv_call;

    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin_guide);
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        bt_login = (Button) findViewById(R.id.bt_login);
        bt_register = (Button) findViewById(R.id.bt_register);
        tv_call = (TextView) findViewById(R.id.tv_call);
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
        tv_call.setOnClickListener(this);
        bt_login.setOnClickListener(this);
        bt_register.setOnClickListener(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                startActivity(new Intent(BeginGuideActivity.this, LoginActivity.class));
                break;
            case R.id.bt_register:
                startActivity(new Intent(BeginGuideActivity.this, RegisterActivity.class));
                break;
            case R.id.tv_call:
                builder.show();
                break;
        }
    }

    private void tellPhone() {
        if (ActivityCompat.checkSelfPermission(BeginGuideActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BeginGuideActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 12);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4001528518"));
            startActivity(intent);
        }
    }
}
