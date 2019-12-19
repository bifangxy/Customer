package com.xiaopu.customer.activity;

import android.Manifest;
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
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.utils.KeyBoardUtils;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.utils.security.Des;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;

import java.util.HashMap;
import java.util.Map;


public class FeedbackActivity extends ActivityBase {

    private TextView tvServicePhone;
    private EditText etFeedback;
    private Button btCommit;
    private MyClick myClick;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initActionBar("意见反馈");
        initView();
        initData();
    }

    private void initView() {
        tvServicePhone = (TextView) findViewById(R.id.tv_servicephone);
        btCommit = (Button) findViewById(R.id.commit);
        etFeedback = (EditText) findViewById(R.id.feedback);
    }

    private void initData() {
        builder = new AlertDialog.Builder(FeedbackActivity.this);
        builder.setTitle("提示");
        builder.setMessage("是否要拨打客服？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4001528518"));
                if (ActivityCompat.checkSelfPermission(FeedbackActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    ActivityCompat.requestPermissions(FeedbackActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 12);
                } else {
                    startActivity(intent);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        myClick = new MyClick();
        btCommit.setOnClickListener(myClick);
        tvServicePhone.setOnClickListener(myClick);
    }

    public class MyClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.commit:
                    if (!etFeedback.getText().toString().trim().isEmpty()) {
                        try {
                            commitFeedback(Des.encode(etFeedback.getText().toString()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        KeyBoardUtils.closeKeybord(etFeedback, FeedbackActivity.this);
                    } else {
                        T.showShort("提交建议不能为空");
                        KeyBoardUtils.closeKeybord(etFeedback, FeedbackActivity.this);
                    }
                    break;
                case R.id.tv_servicephone:
                    builder.show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 12) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4001528518"));
                try {
                    startActivity(intent);
                } catch (SecurityException e) {

                }
            } else {
                T.showShort("无法获取相应的权限");
            }
        }
    }

    private void commitFeedback(String content) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("content", content);
        HttpUtils.getInstantce().showSweetDialog();
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_submitAdvice, new HttpCallBack<String>() {
            @Override
            public void onSuccess(HttpResult result) {
                etFeedback.setText("");
                SweetAlertDialog mSweetDialog = HttpUtils.getInstantce().getSweetDialog();
                mSweetDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                mSweetDialog.setTitleText("提交成功");
                mSweetDialog.setContentText("感谢您的建议");
                mSweetDialog.setConfirmText("确定");
                mSweetDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        finish();
                    }
                });
            }

            @Override
            public void onFail(String msg) {

            }
        });

       /* HttpUtils.getInstantce().submitAdvice(content, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
            @Override
            public void onSuccess(Feedback jsonData) {
                etFeedback.setText("");
                SweetAlertDialog mSweetDialog = HttpUtils.getInstantce().getSweetDialog();
                mSweetDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                mSweetDialog.setTitleText("提交成功");
                mSweetDialog.setContentText("感谢您的建议");
                mSweetDialog.setConfirmText("确定");
                mSweetDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(Feedback jsonData) {
            }
        });*/
    }
}
