package com.xiaopu.customer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xiaopu.customer.R;
import com.xiaopu.customer.data.EntityDevice;
import com.xiaopu.customer.utils.T;

import java.util.List;

/**
 * Created by Administrator on 2017/6/20.
 */

public class ChooseDialogManage {

    private Context mContext;

    private Dialog mChooseDialog;

    private RadioGroup radioGroup_devices;

    private Button bt_ensure;

    private View mChooseView;

    private OnDialogClickListener mListener;

    private List<EntityDevice> entityDeviceList;

    public ChooseDialogManage(Context mContext) {
        this.mContext = mContext;
        initView();
        initData();
    }

    public void setEntityDeviceList(List<EntityDevice> entityDeviceList) {
        this.entityDeviceList = entityDeviceList;
    }

    private void initView() {
        mChooseView = LayoutInflater.from(mContext).inflate(R.layout.dialog_choose, null);
        radioGroup_devices = (RadioGroup) mChooseView.findViewById(R.id.radioGroup_devices);
        bt_ensure = (Button) mChooseView.findViewById(R.id.bt_ensure);
    }


    private void initData() {
        mChooseDialog = new Dialog(mContext, R.style.Dialog);
        mChooseDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mChooseDialog.setContentView(mChooseView);
        mChooseDialog.setCanceledOnTouchOutside(false);

    }

    public void showChooseDialog() {
//        mListener = onClickListener;
        /*for (int i = 0; i < entityDeviceList.size(); i++) {
            EntityDevice entityDevice = entityDeviceList.get(i);
            RadioButton radioButton = new RadioButton(mContext);
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);
            radioButton.setLayoutParams(layoutParams);
            if (TextUtils.isEmpty(entityDevice.getNickName())) {
                radioButton.setText(entityDevice.getDeviceName());
            } else {
                radioButton.setText(entityDevice.getNickName());
            }
            radioButton.setTag(entityDevice.getDeviceAddress());
            radioButton.setTextSize(14);
            radioButton.setButtonDrawable(R.drawable.choose_radio_bg);//隐藏单选圆形按钮
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setPadding(10, 10, 10, 10);
            radioGroup_devices.addView(radioButton);//将单选按钮添加到RadioGroup中
        }
        radioGroup_devices.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButton = (RadioButton) mChooseView.findViewById(radioGroup_devices.getCheckedRadioButtonId());
                T.showShort("---" + checkedId + "----" + radioButton.getTag());
            }
        });*/
        permission();
    }

    public interface OnDialogClickListener {
        void click();
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_sure:
                    mListener.click();
                    break;
            }
        }
    }

    public void permission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(mContext)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                mContext.startActivity(intent);
                return;
            } else {
                //Android6.0以上
                if (mChooseDialog != null && mChooseDialog.isShowing() == false) {
                    mChooseDialog.show();
                }
            }
        } else {
            //Android6.0以下，不用动态声明权限
            if (mChooseDialog != null && mChooseDialog.isShowing() == false) {
                mChooseDialog.show();
            }
        }
    }


}
