package com.xiaopu.customer.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;

/**
 * 支付成功页面
 * Created by Administrator on 2018/7/25.
 */

public class PaySuccessActivity extends ActivityBase implements View.OnClickListener {

    private static final String LOG_TAG = PaySuccessActivity.class.getSimpleName();

    private Context mContext;

    private TextView tv_pay_amount;

    private TextView tv_start_time;

    private TextView tv_end_time;

    private TextView tv_sure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
        mContext = this;
        initActionBar("支付成功");
        initView();
        initData();
        initListener();
    }


    private void initView() {
        tv_pay_amount = findViewById(R.id.tv_pay_amount);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        tv_sure = findViewById(R.id.tv_sure);
    }

    private void initData() {
        String payAmount = getIntent().getStringExtra("pay_amount");
        String startTime = getIntent().getStringExtra("start_time");
        String end_time = getIntent().getStringExtra("end_time");

        tv_pay_amount.setText(payAmount + "小普币");
        tv_start_time.setText("开始时间：" + startTime);
        tv_end_time.setText("结束时间：" + end_time);

    }

    private void initListener() {
        tv_sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sure:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
