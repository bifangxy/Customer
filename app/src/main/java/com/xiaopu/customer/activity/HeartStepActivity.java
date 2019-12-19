package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;

/**
 * Created by Administrator on 2017/6/13.
 */

public class HeartStepActivity extends ActivityBase {
    private static final String LOG_TAG = HeartStepActivity.class.getSimpleName();

    private Context mContext;

    private Button bt_next;

    private MyClickListener mClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_step);
        mContext = this;
        initActionBar("心率检测");
        initView();
        initData();
        initListener();
    }

    private void initView() {
        bt_next = (Button) findViewById(R.id.bt_step_next);
    }

    private void initData() {
        mClick = new MyClickListener();
    }

    private void initListener() {
        bt_next.setOnClickListener(mClick);
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_step_next:
                    startActivity(new Intent(mContext, HeartDetectionActivity.class));
                    finish();
                    break;
            }
        }
    }
}
