package com.xiaopu.customer.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.Pickers;
import com.xiaopu.customer.utils.ControlSave;
import com.xiaopu.customer.utils.bluetooth.ConstantUtils;
import com.xiaopu.customer.view.PickerScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/27.
 */

public class StepEditActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = StepEditActivity.class.getSimpleName();

    private Context mContext;

    private PickerScrollView pickerScrollView;

    private TextView tv_actionbar_text;

    private List<Pickers> list;

    private String target_step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_edit);
        initActionBar("个人目标");
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        tv_actionbar_text = (TextView) findViewById(R.id.tv_actionbar_text);
        pickerScrollView = (PickerScrollView) findViewById(R.id.picker_scroll_view_step);
    }

    private void initData() {
        tv_actionbar_text.setText("保存");
        list = new ArrayList<>();
        for (int i = 0; i < 27; i++) {
            list.add(new Pickers(String.valueOf(4000 + i * 1000), "" + i));
        }
        pickerScrollView.setData(list);
        pickerScrollView.setPainColor(0xffffff);
        pickerScrollView.setShowCount(6.0f);

        target_step = ControlSave.read(mContext, ConstantUtils.BAND_TARGET_STEP, "");
        if (!TextUtils.isEmpty(target_step)) {
            pickerScrollView.setSelectedContent(target_step);
        }

    }

    private void initListener() {
        tv_actionbar_text.setOnClickListener(this);
        pickerScrollView.setOnSelectListener(new PickerScrollView.onSelectListener() {
            @Override
            public void onSelect(Pickers pickers) {
                target_step = pickers.getShowConetnt();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_actionbar_text:
                ControlSave.save(mContext, ConstantUtils.BAND_TARGET_STEP, target_step);
                finish();
                break;
        }
    }
}
