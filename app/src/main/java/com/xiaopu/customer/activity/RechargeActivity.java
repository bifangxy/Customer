package com.xiaopu.customer.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.UserInfoResult;
import com.xiaopu.customer.data.jsonresult.WeixinPayData;
import com.xiaopu.customer.dialog.CustomizeDialog;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.ToastUtils;
import com.xiaopu.customer.utils.alipay.PayResult;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.wxapi.WXPayEntryActivity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xieying on 2016/8/22.
 * 功能：
 */
public class RechargeActivity extends ActivityBase {
    private static final String LOG_TAG = RechargeActivity.class.getSimpleName();

    private Context mContext;

    private Button bt_ensure_pay;

    private MyClick mClick;

    private EditText et_pay_number;

    private TextView tv_pay_number;

    private ImageView iv_pay_weixin;

    private ImageView iv_pay_ali;

    private TextView tv_user_name;

    private TextView tv_user_wealth;

    private static final int SDK_PAY_FLAG = 1;

    private String pay_result;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
//                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
//                    if (TextUtils.equals(resultStatus, "9000")) {
//                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();
//                    }
                    checkAlipay(payResult);
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        mContext = this;
        initActionBar("充值");
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        HttpUtils.getInstantce().postNoHead(HttpConstant.Url_getUserData, new HttpCallBack<UserInfoResult>() {
            @Override
            public void onSuccess(HttpResult result) {
                UserInfoResult userInfoResult = (UserInfoResult) result.getData();
                tv_user_wealth.setText("剩余:" + userInfoResult.getBalance() + "小普币");
                tv_user_name.setText(ApplicationXpClient.userInfoResult.getNickname());
            }

            @Override
            public void onFail(String msg) {
                T.showShort(getString(R.string.internet_error));
            }
        });

        /*HttpUtils.getInstantce().getUserData(ApplicationXpClient.userInfoResult.getId(), new HttpConstant.SampleJsonResultListener<Feedback>() {
            @Override
            public void onSuccess(Feedback jsonData) {
                tv_user_wealth.setText("剩余:" + jsonData.getData() + "小普币");
                tv_user_name.setText(ApplicationXpClient.userInfoResult.getNickname());
            }

            @Override
            public void onFailure(Feedback jsonData) {
                if (jsonData == null) {
                    ToastUtils.showWarnMsg("网络不通，请检查网络。");
                } else {
                    ToastUtils.showErrorMsg(jsonData.getMsg());
                }
            }
        });*/
    }

    private void initData() {
        mClick = new MyClick();
        bt_ensure_pay.setOnClickListener(mClick);
        iv_pay_weixin.setOnClickListener(mClick);
        iv_pay_ali.setOnClickListener(mClick);
        iv_pay_weixin.setSelected(true);

        et_pay_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(et_pay_number.getText().toString())) {
                    tv_pay_number.setText("充值金额:0元");
                } else {
                    tv_pay_number.setText("充值金额:" + et_pay_number.getText().toString() + "元");
                }
            }
        });
    }

    private void initView() {
        bt_ensure_pay = (Button) findViewById(R.id.bt_ensure_pay);
        et_pay_number = (EditText) findViewById(R.id.et_pay_number);
        tv_pay_number = (TextView) findViewById(R.id.tv_pay_number);
        iv_pay_weixin = (ImageView) findViewById(R.id.iv_pay_weixin);
        iv_pay_ali = (ImageView) findViewById(R.id.iv_pay_ali);
        tv_user_name = (TextView) findViewById(R.id.tv_user_account);
        tv_user_wealth = (TextView) findViewById(R.id.tv_user_wealth);
    }


    private class MyClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_ensure_pay:
                    if (TextUtils.isEmpty(et_pay_number.getText().toString()) || et_pay_number.getText().toString().equals("0")) {
                        T.showShort("请输入正确的充值数量!");
                    } else {
                        if (iv_pay_ali.isSelected()) {
                            aliPay();
                        } else if (iv_pay_weixin.isSelected()) {
                            weixinPay();
                        }
                    }
                    break;
                case R.id.iv_pay_ali:
                    iv_pay_ali.setSelected(true);
                    iv_pay_weixin.setSelected(false);
                    break;
                case R.id.iv_pay_weixin:
                    iv_pay_ali.setSelected(false);
                    iv_pay_weixin.setSelected(true);
                    break;
                case R.id.bt_return:
                    RechargeActivity.this.finish();
                    break;
            }
        }

    }

    private void weixinPay() {

        Map<String, Object> maps = new HashMap<>();
        maps.put("price", et_pay_number.getText().toString() + "00");
        maps.put("userId", ApplicationXpClient.userInfoResult.getId());
        HttpUtils.getInstantce().showSweetDialog();
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.getUrl_weiXinPay, new HttpCallBack<WeixinPayData>() {
            @Override
            public void onSuccess(HttpResult result) {
                HttpUtils.getInstantce().dismissSweetDialog();
                WeixinPayData weixinPayData = (WeixinPayData) result.getData();
                if (!ApplicationXpClient.MSGAPI.isWXAppInstalled()) {
                    T.showShort("未安装微信");
                    return;
                }
                if (!ApplicationXpClient.MSGAPI.isWXAppSupportAPI()) {
                    T.showShort("微信版本不支持，请更新微信");
                    return;
                }
                Intent intent = new Intent(mContext, WXPayEntryActivity.class);
                intent.putExtra("type", 1);
                Bundle bundle = new Bundle();
                bundle.putSerializable("weixinPayData", weixinPayData);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1001);

            }

            @Override
            public void onFail(String msg) {
                if (msg == null)
                    T.showShort(getString(R.string.internet_error));
                else
                    T.showShort(msg);
            }
        });


     /*   HttpUtils.getInstantce().weiXinPay(et_pay_number.getText().toString() + "00", ApplicationXpClient.userInfoResult.getId(), new HttpConstant.SampleJsonResultListener<Feedback<WeixinPayData>>() {
            @Override
            public void onSuccess(Feedback<WeixinPayData> jsonData) {
                WeixinPayData weixinPayData = jsonData.getData();
                if (!ApplicationXpClient.MSGAPI.isWXAppInstalled()) {
                    T.showShort("未安装微信");
                    return;
                }
                if (!ApplicationXpClient.MSGAPI.isWXAppSupportAPI()) {
                    T.showShort("微信版本不支持，请更新微信");
                    return;
                }
                PayReq request = new PayReq();
                request.appId = ApplicationXpClient.WX_APPID;
                request.partnerId = "1387300802";
                request.prepayId = weixinPayData.getPrepay_id();
                request.packageValue = "Sign=WXPay";
                request.nonceStr = weixinPayData.getNonce_str();
                request.timeStamp = weixinPayData.getDateTime();
                request.sign = weixinPayData.getSign();
                ApplicationXpClient.MSGAPI.sendReq(request);
            }

            @Override
            public void onFailure(Feedback<WeixinPayData> jsonData) {
                if (jsonData == null) {
                    ToastUtils.showWarnMsg("网络不通，请检查网络。");
                } else {
                    ToastUtils.showErrorMsg(jsonData.getMsg());
                }
            }
        });*/
    }

    private void aliPay() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("money", 1);

        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_AliPayCreateOrder, new HttpCallBack<String>() {
            @Override
            public void onSuccess(HttpResult result) {
                String strResult = (String) result.getData();
                String str = strResult.replace("amp;", "");
                payV2(str);
            }

            @Override
            public void onFail(String msg) {

            }
        });

     /*   HttpUtils.getInstantce().getAlipayOrder(1, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
            @Override
            public void onSuccess(Feedback<String> jsonData) {
                String str = jsonData.getData().replace("amp;", "");
                payV2(str);
            }

            @Override
            public void onFailure(Feedback<String> jsonData) {
//                Log.d(LOG_TAG, "获取失败");
            }
        });*/
    }


    /**
     * 支付宝支付业务
     *
     * @param
     */
    public void payV2(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(RechargeActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                pay_result = result.toString();
                Log.i("msp", result.toString());
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private void checkAlipay(PayResult payResult) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("reqJsonDesStr", HttpUtils.getInstantce().gson.toJson(payResult));
        HttpUtils.getInstantce().showSweetDialog("验证中...");
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.UrlAliPayReturnUrl, new HttpCallBack<String>() {
            @Override
            public void onSuccess(HttpResult result) {
                if (result.getCode() == 200) {
                    HttpUtils.getInstantce().changeRightSweetDialog("支付成功");
                } else if (result.getCode() == 210) {
                    HttpUtils.getInstantce().changeSweetDialog("支付失败");
                }
            }

            @Override
            public void onFail(String msg) {

            }
        });

       /* HttpUtils.getInstantce().checkAliResult(payResult, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
            @Override
            public void onSuccess(Feedback<String> jsonData) {

            }

            @Override
            public void onFailure(Feedback<String> jsonData) {

            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == 0) {
                CustomizeDialog.Builder builder = new CustomizeDialog.Builder(mContext, CustomizeDialog.SUCCESS_TYPE);
                builder.setContentText(getString(R.string.recharge_success));
                builder.setConfirmClickListener(new CustomizeDialog.OnCustomizeClickListener() {
                    @Override
                    public void onClick(CustomizeDialog customizeDialog) {
                        customizeDialog.dismiss();
                        finish();
                    }
                });
                builder.builder().show();
            } else {
                CustomizeDialog.Builder builder = new CustomizeDialog.Builder(mContext, CustomizeDialog.ERROR_TYPE);
                builder.setContentText(getString(R.string.recharge_fail));
                builder.setConfirmClickListener(new CustomizeDialog.OnCustomizeClickListener() {
                    @Override
                    public void onClick(CustomizeDialog customizeDialog) {
                        customizeDialog.dismiss();
                    }
                });
                builder.builder().show();
            }
        }
    }
}
