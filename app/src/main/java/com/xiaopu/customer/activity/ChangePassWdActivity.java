package com.xiaopu.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hyphenate.chat.EMClient;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.utils.ControlSave;
import com.xiaopu.customer.utils.EditTextUtils;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/14 0014.
 */
public class ChangePassWdActivity extends ActivityBase {
    private static final String LOG_TAG = ChangePassWdActivity.class.getSimpleName();

    private Button btCommit;

    private EditText etOldPassword;

    private EditText etNewPassword;

    private EditText etSurePassword;

    private MyClick myClick;

    private boolean isNull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassw);
        initActionBar("重置密码");
        initView();
        initData();
    }

    private void initData() {
        myClick = new MyClick();
        btCommit.setOnClickListener(myClick);
        etOldPassword.addTextChangedListener(watcher);
        etNewPassword.addTextChangedListener(watcher);
        etSurePassword.addTextChangedListener(watcher);
    }

    private void initView() {
        btCommit = (Button) findViewById(R.id.bt_changepassw);
        etOldPassword = (EditText) findViewById(R.id.et1_changepassw);
        etNewPassword = (EditText) findViewById(R.id.et2_changepassw);
        etSurePassword = (EditText) findViewById(R.id.et3_changepassw);
    }

    private void changePassword(String oldPassword, String newPassword) {

        Map<String, Object> maps = new HashMap<>();
        maps.put("oldPassword", oldPassword);
        maps.put("newPassword", newPassword);
        HttpUtils.getInstantce().showSweetDialog(getString(R.string.commiting));
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_modifyPassword, new HttpCallBack<String>() {
            @Override
            public void onSuccess(HttpResult result) {
                SweetAlertDialog mSweetDialog = HttpUtils.getInstantce().getSweetDialog();
                mSweetDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                mSweetDialog.setTitleText("提示");
                mSweetDialog.setContentText("密码修改成功");
                mSweetDialog.setConfirmText("确定");
                mSweetDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        HttpUtils.getInstantce().postNoHead(HttpConstant.Url_logout, new HttpCallBack<String>() {
                            @Override
                            public void onSuccess(HttpResult result) {

                            }

                            @Override
                            public void onFail(String msg) {

                            }
                        });

                     /*   HttpUtils.getInstantce().logout(new HttpConstant.SampleJsonResultListener<Feedback>() {
                            @Override
                            public void onSuccess(Feedback jsonData) {

                            }

                            @Override
                            public void onFailure(Feedback jsonData) {
                            }
                        });*/
                        ControlSave.delete(getBaseContext(), "rencent_user_new");
                        EMClient.getInstance().logout(true);//退出环信
                        HttpUtils.getInstantce().clearCookie();//清除Cookie
                        Intent intent = new Intent(ChangePassWdActivity.this, LoginActivity.class);
                        startActivity(intent);
                        ChangePassWdActivity.this.exit();
                    }
                });
            }

            @Override
            public void onFail(String msg) {

            }
        });

      /*  HttpUtils.getInstantce().modifyPassword(oldPassword, newPassword, new HttpConstant.SampleJsonResultListener<Feedback>() {
            @Override
            public void onSuccess(Feedback jsonData) {
                T.showShort(jsonData.getMsg());
                HttpUtils.getInstantce().logout(new HttpConstant.SampleJsonResultListener<Feedback>() {
                    @Override
                    public void onSuccess(Feedback jsonData) {

                    }

                    @Override
                    public void onFailure(Feedback jsonData) {
                       *//* if (jsonData == null) {
                            ToastUtils.showWarnMsg("网络不通，请检查网络。");
                        } else {
                            ToastUtils.showErrorMsg(jsonData.getMsg());
                        }*//*
                    }
                });

                ControlSave.delete(getBaseContext(), "rencent_user_new");
                EMClient.getInstance().logout(true);//退出环信
                HttpUtils.getInstantce().clearCookie();//清除Cookie
                Intent intent = new Intent(ChangePassWdActivity.this, LoginActivity.class);
                startActivity(intent);
                ChangePassWdActivity.this.exit();
            }

            @Override
            public void onFailure(Feedback jsonData) {
                if (jsonData == null) {
                    T.showShort("网络不通，请检查网络。");
                } else {
                    T.showShort(jsonData.getMsg());
                }

            }
        });*/
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int newPassword = etNewPassword.getText().toString().length();
            int oldPassword = etOldPassword.getText().toString().length();
            int surePassword = etSurePassword.getText().toString().length();
            if (newPassword * oldPassword * surePassword == 0) {
                isNull = true;
                btCommit.setBackgroundResource(R.drawable.shape_bt_commit_press);
            } else {
                btCommit.setBackgroundResource(R.drawable.shape_bt_commit_normal);
                isNull = false;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private class MyClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_changepassw:
                    if (!isNull) {
                        if (EditTextUtils.isPassword(etNewPassword.getText().toString().trim()) && EditTextUtils.isPassword(etSurePassword.getText().toString().trim())) {
                            if (!etOldPassword.getText().toString().equals(etNewPassword.getText().toString().trim())) {
                                if (etNewPassword.getText().toString().equals(etSurePassword.getText().toString())) {

                                    changePassword(etOldPassword.getText().toString(), etNewPassword.getText().toString());

                                } else {
                                    T.showShort("新密码与确认密码不一致！");
                                }
                            } else {
                                T.showShort("新密码不能和旧密码相同！");
                            }
                        } else {
                            T.showShort("新密码或确认密码字数不能少于6位，大于15位！");
                        }

                    }
                    break;
            }
        }
    }
}
