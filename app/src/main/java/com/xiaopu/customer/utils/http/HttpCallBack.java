package com.xiaopu.customer.utils.http;

/**
 * Created by Administrator on 2017/12/20.
 */

public abstract class HttpCallBack<T> {
    public abstract void onSuccess(HttpResult result);

    public abstract void onFail(String msg);
}
