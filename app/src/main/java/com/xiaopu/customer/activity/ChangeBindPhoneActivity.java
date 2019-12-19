package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.Area;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.UserInfoResult;
import com.xiaopu.customer.utils.EditTextUtils;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.TimeTextView;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/15 0015.
 */
public class ChangeBindPhoneActivity extends ActivityBase {
    private static final String LOG_TAG = ChangeBindPhoneActivity.class.getSimpleName();

    private Context mContext;

    private Button bt_commit;

    private TimeTextView timeTextView;

    private EditText et_verification;

    private EditText et_pass;

    private EditText et_newphone;

    private TextView tvAlterHint;

    private TextView tvActionBar;

    private TextView tvAreaNum;

    private MyClickListener mClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changebindingphone);
        mContext = this;
        initActionBar("更改绑定手机");
        initView();
        initData();// 加载数据
        setListener();
    }

    private void initView() {
        bt_commit = (Button) findViewById(R.id.bt_changephone);
        timeTextView = (TimeTextView) findViewById(R.id.tv_verification);
        et_verification = (EditText) findViewById(R.id.et_verification);
        et_newphone = (EditText) findViewById(R.id.et_newphone);
        et_pass = (EditText) findViewById(R.id.et_pass);
        tvAlterHint = (TextView) findViewById(R.id.alter_hint);
        tvActionBar = findViewById(R.id.tv_actionbar_text);
        tvAreaNum = findViewById(R.id.tv_area_num);

        tvActionBar.setText("更该地区");
        tvActionBar.setVisibility(View.VISIBLE);
    }

    private void initData() {
        mClick = new MyClickListener();
    }


    private void setListener() {
        bt_commit.setOnClickListener(mClick);
        timeTextView.setOnClickListener(mClick);
        tvActionBar.setOnClickListener(mClick);
        et_verification.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeTextView.onDestroy();
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_changephone:
                    tvAlterHint.setVisibility(View.INVISIBLE);
                    if (isInputRight()) {
                        Map<String, Object> maps = new HashMap<>();
                        maps.put("code", et_verification.getText().toString().trim());
                        maps.put("password", et_pass.getText().toString().trim());
                        maps.put("newPhone", et_newphone.getText().toString().trim());
                        HttpUtils.getInstantce().showSweetDialog(getString(R.string.commiting));
                        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_modifyBinding, new HttpCallBack<UserInfoResult>() {
                            @Override
                            public void onSuccess(HttpResult result) {
                                SweetAlertDialog mSweetDialog = HttpUtils.getInstantce().getSweetDialog();
                                mSweetDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                mSweetDialog.setTitleText("提示");
                                mSweetDialog.setContentText("手机号修改成功");
                                mSweetDialog.setConfirmText("确定");
                                mSweetDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        finish();
                                        ApplicationXpClient.userInfoResult.setPhone(et_newphone.getText().toString().trim());
                                        sweetAlertDialog.dismiss();
                                    }
                                });
                            }

                            @Override
                            public void onFail(String msg) {

                            }
                        });

/*
                        HttpUtils.getInstantce().modifyBinding(et_verification.getText().toString().trim(), et_pass.getText().toString().trim(), et_newphone.getText().toString().trim(), new HttpConstant.SampleJsonResultListener<Feedback>() {
                            @Override
                            public void onSuccess(Feedback jsonData) {
                                SweetAlertDialog mSweetDialog = HttpUtils.getInstantce().getSweetDialog();
                                mSweetDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                mSweetDialog.setTitleText("提示");
                                mSweetDialog.setContentText("手机号修改成功");
                                mSweetDialog.setConfirmText("确定");
                                mSweetDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        finish();
                                        ApplicationXpClient.userInfoResult.setPhone(et_newphone.getText().toString().trim());
                                        sweetAlertDialog.dismiss();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Feedback jsonData) {

                            }
                        });
*/
                    }
                    break;
                case R.id.tv_verification:
                    if (EditTextUtils.isMobileNO(et_newphone.getText().toString().trim())) {

                        Map<String, Object> maps = new HashMap<>();
                        maps.put("phone", et_newphone.getText().toString().trim());
                        HttpUtils.getInstantce().showSweetDialog("发送中");
                        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_sendValidateCodeBindPhone, new HttpCallBack<String>() {
                            @Override
                            public void onSuccess(HttpResult result) {
                                HttpUtils.getInstantce().changeRightSweetDialog(getString(R.string.send_success));
                                timeTextView.setStart();
                                et_newphone.setKeyListener(null);
                            }

                            @Override
                            public void onFail(String msg) {

                            }
                        });
                        /*HttpUtils.getInstantce().sendValidateCode(HttpConstant.Url_sendValidateCodeBindPhone, et_newphone.getText().toString().trim(), new HttpConstant.SampleJsonResultListener<Feedback>() {
                            @Override
                            public void onSuccess(Feedback jsonData) {
                                timeTextView.setStart();
                                et_newphone.setKeyListener(null);
                            }

                            @Override
                            public void onFailure(Feedback jsonData) {
                            }
                        });*/
                    } else {
                        T.showShort("请输入正确的手机号");
                    }
                    break;
                case R.id.tv_actionbar_text:
                    startActivityForResult(new Intent(mContext, ChangeAreaActivity.class), 1);
                    break;
            }
        }
    }

    private boolean isInputRight() {
        if (et_newphone.getText().length() == 0 || et_verification.getText().length() == 0 || et_pass.getText().length() == 0) {
            T.showShort("信息输入不完整");
            return false;
        } else if (TextUtils.isEmpty(et_verification.getText().toString()) || et_verification.getText().toString().length() != 6) {
            T.showShort("验证码格式有误");
            return false;
        } else if (et_pass.getText().toString().length() < 6) {
            T.showShort("密码字数不少于6位！");
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
