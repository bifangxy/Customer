package com.xiaopu.customer.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.jsonresult.WeixinPayData;
import com.xiaopu.customer.utils.T;

import java.util.Date;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    private TextView tv_pay_result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        tv_pay_result = (TextView) findViewById(R.id.pay_result);
        api = WXAPIFactory.createWXAPI(this, ApplicationXpClient.WX_APPID);
        api.handleIntent(getIntent(), this);

        int type = getIntent().getIntExtra("type", -1);
        if (type == 1) {
            WeixinPayData weixinPayData = (WeixinPayData) getIntent().getExtras().getSerializable("weixinPayData");
            PayReq request = new PayReq();
            request.appId = ApplicationXpClient.WX_APPID;
            request.partnerId = "1387300802";
            request.prepayId = weixinPayData.getPrepay_id();
            request.packageValue = "Sign=WXPay";
            request.nonceStr = weixinPayData.getNonce_str();
            request.timeStamp = String.valueOf(weixinPayData.getDateTime());
            request.sign = weixinPayData.getSign();
            ApplicationXpClient.MSGAPI.sendReq(request);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            setResult(resp.errCode);
            Log.d("PayResult", "支付结果" + String.valueOf(resp.errCode) + resp.errStr);
            finish();
        }
    }
}