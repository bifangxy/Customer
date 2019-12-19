package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBaseWithoutHXListen;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.Area;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.User;
import com.xiaopu.customer.utils.EditTextUtils;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.TimeTextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/5 0005.
 */
public class RegisterPhoneActivity extends ActivityBaseWithoutHXListen {
    private static final String LOG_TAG = RegisterPhoneActivity.class.getSimpleName();

    private Context mContext;

    private Button btNext;

    private User user;

    private EditText et_phone, et_verification;

    private TimeTextView timeTextView;

    private String phone;//手机号

    private String verificationcode;//验证码

    private MyClickListener mClick;

    private TextView tvActionBar;

    private TextView tvAreaNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registertwo);
        mContext = this;
        initActionBar("手机验证");
        initView();
        initData();
        initListener();

    }

    private void initView() {
        btNext = (Button) findViewById(R.id.bt_next);
        et_phone = (EditText) findViewById(R.id.et_user_phone);
        et_verification = (EditText) findViewById(R.id.et_code);
        timeTextView = (TimeTextView) findViewById(R.id.tv_verification);
        tvActionBar = findViewById(R.id.tv_actionbar_text);
        tvAreaNum = findViewById(R.id.tv_area_num);

        tvActionBar.setText("更换地区");
        tvActionBar.setVisibility(View.VISIBLE);
    }

    private void initData() {
        user = (User) getIntent().getSerializableExtra("user");
        mClick = new MyClickListener();
    }

    private void initListener() {
        btNext.setOnClickListener(mClick);
        timeTextView.setOnClickListener(mClick);
        tvActionBar.setOnClickListener(mClick);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        timeTextView.onDestroy();
        super.onDestroy();
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_verification:
                    if (isPhoneInputRight()) {
                        Map<String, Object> maps = new HashMap<>();
                        maps.put("phone", et_phone.getText().toString().trim());
                        HttpUtils.getInstantce().showSweetDialog("发送中");
                        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_sendValidateCode, new HttpCallBack<String>() {
                            @Override
                            public void onSuccess(HttpResult result) {
                                HttpUtils.getInstantce().changeRightSweetDialog(getString(R.string.send_success));
                                timeTextView.setStart();
                                verificationcode = (String) result.getData();
                                et_phone.setKeyListener(null);
                                phone = et_phone.getText().toString().trim();
                            }

                            @Override
                            public void onFail(String msg) {

                            }
                        });

                      /*  HttpUtils.getInstantce().sendValidateCode(HttpConstant.Url_sendValidateCode, et_phone.getText().toString().trim(), new HttpConstant.SampleJsonResultListener<Feedback>() {
                            @Override
                            public void onSuccess(Feedback jsonData) {
                                timeTextView.setStart();
                                verificationcode = (String) jsonData.getData();
                                et_phone.setKeyListener(null);
                                phone = et_phone.getText().toString().trim();
                            }

                            @Override
                            public void onFailure(Feedback jsonData) {
                            }
                        });*/
                    }
                    break;
                case R.id.bt_next:
                    if (isInputRight()) {

                        Map<String, Object> maps = new HashMap<>();
                        maps.put("code", et_verification.getText().toString().trim());
                        maps.put("phone", et_phone.getText().toString().trim());
                        HttpUtils.getInstantce().showSweetDialog();

                        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_verificationCode, new HttpCallBack<String>() {
                            @Override
                            public void onSuccess(HttpResult result) {
                                HttpUtils.getInstantce().dismissSweetDialog();
                                user.setPhone(phone);
                                Intent intent = new Intent(RegisterPhoneActivity.this, RegisterActivityThree.class);
                                intent.putExtra("user", user);
                                startActivity(intent);
                            }

                            @Override
                            public void onFail(String msg) {

                            }
                        });

/*
                        HttpUtils.getInstantce().verificationCode(et_verification.getText().toString().trim(), et_phone.getText().toString().trim(), new HttpConstant.SampleJsonResultListener<Feedback>() {
                            @Override
                            public void onSuccess(Feedback jsonData) {
                                user.setPhone(phone);
                                Intent intent = new Intent(RegisterPhoneActivity.this, RegisterActivityThree.class);
                                intent.putExtra("user", user);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(Feedback jsonData) {
                            }
                        });
*/
                    }
                    break;
                case R.id.tv_actionbar_text:
                    startActivityForResult(new Intent(mContext, ChangeAreaActivity.class), 1);
                    break;
            }
        }

    }

    private boolean isPhoneInputRight() {
        if (!EditTextUtils.isMobileNO(et_phone.getText().toString().trim()) || timeTextView.getText().equals("")) {
            T.showShort("请输入正确的手机号！");
            return false;
        }
        return true;
    }

    private boolean isInputRight() {
        if (!EditTextUtils.isMobileNO(et_phone.getText().toString().trim())) {
            T.showShort("请输入正确的手机号！");
            return false;
        } else if (et_verification.getText().toString().trim().isEmpty()) {
            T.showShort("请输入验证码！");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            Area area = (Area) data.getSerializableExtra("result");
            tvAreaNum.setText(area.getAreaNum());
        }
    }
}
