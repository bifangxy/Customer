package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;
import com.xiaopu.customer.utils.ControlSave;

/**
 * Created by Administrator on 2017/9/14.
 */

public class MeasureInstructionsActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = MeasurementActivity.class.getSimpleName();

    private Context mContext;

    private TextView tv_no_tip_again;

    private Button bt_know;

    private int resultCode = 0;

    //1：心率单次测量，2：心率实时测量，3：血压单次测量，4：血压实时测量，5：血氧单词测量，6：血氧实时测量
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_instructions);
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        tv_no_tip_again = (TextView) findViewById(R.id.tv_no_tip_again);
        bt_know = (Button) findViewById(R.id.bt_know);
    }

    private void initData() {
        type = getIntent().getIntExtra("type", 0);
    }

    private void initListener() {
        tv_no_tip_again.setOnClickListener(this);
        bt_know.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_no_tip_again:
                ControlSave.save(mContext, "measure_tip", "1");
                break;
            case R.id.bt_know:
                break;
        }
        Intent mIntent = new Intent();
        mIntent.putExtra("data", type);
        // 设置结果，并进行传送
        this.setResult(resultCode, mIntent);
        this.finish();
    }
}
