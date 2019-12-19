package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.utils.ControlSave;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;


/**
 * Created by Administrator on 2016/7/14 0014.
 */
public class MyAccountActivity extends ActivityBase {
    private static final String LOG_TAG = MyAccountActivity.class.getSimpleName();

    private Context mContext;

    private ImageView iv_user_head;

    private LinearLayout ll_my_data;

    private LinearLayout ll_reset_password;

    private LinearLayout ll_change_phone;

    private LinearLayout ll_switch_account;

    private TextView tvNickName;

    private TextView tvUserId;

    private TextView tvSex;

    private MyClick mClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myaccount);
        mContext = MyAccountActivity.this;
        initActionBar("我的账户");
        initView();
        initData();
        setListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setListener() {
        ll_my_data.setOnClickListener(mClick);
        ll_reset_password.setOnClickListener(mClick);
        ll_change_phone.setOnClickListener(mClick);
        ll_switch_account.setOnClickListener(mClick);
    }


    private void initData() {
        mClick = new MyClick();
    }

    private void initView() {
        ll_my_data = (LinearLayout) findViewById(R.id.ll_my_data);
        ll_reset_password = (LinearLayout) findViewById(R.id.ll_reset_password);
        ll_change_phone = (LinearLayout) findViewById(R.id.ll_change_phone);
        ll_switch_account = (LinearLayout) findViewById(R.id.ll_switch_account);
        iv_user_head = (ImageView) findViewById(R.id.iv_user_head);
        tvNickName = (TextView) findViewById(R.id.nickname);
        tvUserId = (TextView) findViewById(R.id.user_id);
        tvSex = (TextView) findViewById(R.id.sex);
    }

    /**
     * 判断性别
     */
    public String initSex(int status) {
        switch (status) {
            case 0:
                return "女";
            case 1:
                return "男";
        }
        return "";
    }

    @Override
    protected void onResume() {
        initUserInfo();
        super.onResume();
    }

    private class MyClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_my_data:
                    startActivity(new Intent(mContext, MyInfoActivity.class));
                    break;
                case R.id.ll_reset_password:
                    startActivity(new Intent(mContext, ChangePassWdActivity.class));
                    break;
                case R.id.ll_change_phone:
                    startActivity(new Intent(mContext, ChangeBindPhoneActivity.class));
                    break;
                case R.id.ll_switch_account:
                    showNotice();
                    break;
            }
        }
    }

    private void initUserInfo() {
        if (ApplicationXpClient.userInfoResult.getSex() != null) {
            tvSex.setText(initSex(ApplicationXpClient.userInfoResult.getSex()));
        } else {
            tvSex.setText("");
        }
        tvNickName.setText(ApplicationXpClient.userInfoResult.getNickname());
        tvUserId.setText("电话:" + ApplicationXpClient.userInfoResult.getPhone());
        ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + ApplicationXpClient.userInfoResult.getAvatar(), iv_user_head, ApplicationXpClient.options);
    }

    private void showNotice() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitleText("提示")
                .setContentText("是否退出登录")
                .setConfirmText("确认")
                .setCancelText("取消")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        HttpUtils.getInstantce().postNoHead(HttpConstant.Url_logout, new HttpCallBack<String>() {
                            @Override
                            public void onSuccess(HttpResult result) {

                            }

                            @Override
                            public void onFail(String msg) {

                            }
                        });
                        /*HttpUtils.getInstantce().logout(new HttpConstant.SampleJsonResultListener<Feedback>() {
                            @Override
                            public void onSuccess(Feedback jsonData) {
                            }

                            @Override
                            public void onFailure(Feedback jsonData) {
                            }
                        });*/
                        ControlSave.delete(getBaseContext(), "rencent_user_new");
                        ControlSave.delete(getBaseContext(), "third_app_data");
                        EMClient.getInstance().logout(true);//退出环信
                        HttpUtils.getInstantce().clearCookie();//清除Cookie
                        Intent intent = new Intent(MyAccountActivity.this, BeginGuideActivity.class);
                        startActivity(intent);
                        MyAccountActivity.this.exit();

                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
        sweetAlertDialog.show();
    }

}
