package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBaseWithoutHXListen;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.User;
import com.xiaopu.customer.utils.EditTextUtils;
import com.xiaopu.customer.utils.MD5Utils;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.ToastUtils;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/5 0005.
 */
public class RegisterActivity extends ActivityBaseWithoutHXListen {
    private static final String LOG_TAG = RegisterActivity.class.getSimpleName();

    private Context mContext;

    private Button bt_next;

    private EditText et_user_name;

    private EditText et_user_nickname;

    private EditText et_user_password;

    private EditText et_user_ensure_password;

    private TextView tv_serviceinfo, tv_privacyinfo;

    private User user;

    private MyClickListener mClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        initActionBar("基本信息");
        initView();
        initData();
        initListener();
    }

    private void initView() {
        bt_next = (Button) findViewById(R.id.bt_next);
        et_user_name = (EditText) findViewById(R.id.et_user_name);
        et_user_nickname = (EditText) findViewById(R.id.et_user_nickname);
        et_user_password = (EditText) findViewById(R.id.et_user_password);
        et_user_ensure_password = (EditText) findViewById(R.id.et_user_ensure_password);
        tv_serviceinfo = (TextView) findViewById(R.id.tv_serviceinfo);
        tv_privacyinfo = (TextView) findViewById(R.id.tv_privacyinfo);
    }

    private void initData() {
        user = new User();
        mClick = new MyClickListener();
    }

    private void initListener() {
        bt_next.setOnClickListener(mClick);
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_next:
                    if (isInputRight()) {

                        Map<String, Object> maps = new HashMap<>();
                        maps.put("loginName", et_user_name.getText().toString().trim());
                        HttpUtils.getInstantce().showSweetDialog("验证中...");
                        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_verificationLoginName, new HttpCallBack<String>() {
                            @Override
                            public void onSuccess(HttpResult result) {
                                HttpUtils.getInstantce().dismissSweetDialog();
                                user.setLoginName(et_user_name.getText().toString().trim());
                                user.setNickname(et_user_nickname.getText().toString().trim());
                                user.setPassword(MD5Utils.MD5(et_user_password.getText().toString().trim()));
                                Intent intent = new Intent(RegisterActivity.this, RegisterPhoneActivity.class);
                                intent.putExtra("user", user);
                                startActivity(intent);
                            }

                            @Override
                            public void onFail(String msg) {
                            }
                        });

/*
                        HttpUtils.getInstantce().verificationLoginName(et_user_name.getText().toString().trim(), new HttpConstant.SampleJsonResultListener<Feedback>() {
                            @Override
                            public void onSuccess(Feedback jsonData) {
                                user.setLoginName(et_user_name.getText().toString().trim());
                                user.setNickname(et_user_nickname.getText().toString().trim());
                                user.setPassword(MD5Utils.MD5(et_user_password.getText().toString().trim()));
                                Intent intent = new Intent(RegisterActivity.this, RegisterPhoneActivity.class);
                                intent.putExtra("user", user);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(Feedback jsonData) {
                                if (jsonData == null) {
                                    ToastUtils.showWarnMsg("网络不通，请检查网络。");
                                } else {
                                    ToastUtils.showErrorMsg(jsonData.getMsg());
                                }
                            }
                        });
*/
                    }
                    break;
            }
        }
    }

    private boolean isInputRight() {
        if (TextUtils.isEmpty(et_user_name.getText().toString()) || et_user_name.getText().toString().trim().length() < 6) {
            T.showShort("请输入用户名,长度为6-14位!");
            return false;
        } else if (et_user_nickname.getText().toString().length() < 2) {
            T.showShort("请输入2-14位昵称");
            return false;
        } else if (!EditTextUtils.isW(et_user_nickname.getText().toString().trim())) {
            T.showShort("昵称不能包含特殊字符！");
            return false;
        } else if (et_user_password.getText().toString().trim().isEmpty() || !EditTextUtils.isPassword(et_user_password.getText().toString().trim())) {
            T.showShort("密码为6-15位，请输入密码！");
            return false;
        } else if (et_user_ensure_password.getText().toString().trim().isEmpty()) {
            T.showShort("请再次输入密码！");
            return false;
        } else if (!et_user_password.getText().toString().trim().equals(et_user_ensure_password.getText().toString().trim())) {
            T.showShort("两次输入密码不一致！");
            return false;
        }
        return true;
    }
}
