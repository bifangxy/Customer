package com.xiaopu.customer.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpUtils;

/**
 * Created by Administrator on 2017/12/22.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String LOG_TAG = WXEntryActivity.class.getSimpleName();
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    private static final String API_KEY = "34b6139f4ce5025fa6afd86693325c84";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationXpClient.MSGAPI.handleIntent(getIntent(), this);
        int type = getIntent().getIntExtra("type", -1);
        if (type == 1) {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "diandi_wx_login";
            ApplicationXpClient.MSGAPI.sendReq(req);
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (RETURN_MSG_TYPE_LOGIN == baseResp.getType()) {
                    Intent intent = new Intent();
                    intent.putExtra("result", -2);
                    setResult(-2, intent);
                    finish();
                } else if (RETURN_MSG_TYPE_SHARE == baseResp.getType()) {
                    finish();
                }
                break;
            case 0:
                if (RETURN_MSG_TYPE_LOGIN == baseResp.getType()) {
                    String code = ((SendAuth.Resp) baseResp).code;
                    Intent intent = new Intent();
                    intent.putExtra("result", code);
                    setResult(1, intent);
                    finish();
                } else if (RETURN_MSG_TYPE_SHARE == baseResp.getType()) {
                    finish();
                }

                break;

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ApplicationXpClient.MSGAPI.handleIntent(intent, this);
    }
}
