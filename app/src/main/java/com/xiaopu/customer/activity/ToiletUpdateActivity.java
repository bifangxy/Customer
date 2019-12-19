package com.xiaopu.customer.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.dialog.CustomizeDialog;
import com.xiaopu.customer.service.BluetoothService;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.bluetooth.BluetoothController;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Administrator
 * on 2017/12/11.
 */

public class ToiletUpdateActivity extends ActivityBase implements View.OnClickListener, BluetoothController.OnUpdateReceiverListener {
    private static final String LOG_TAG = ToiletUpdateActivity.class.getSimpleName();

    private Context mContext;

    private Button bt_update;

    private TextView tv_update_content;

    private LinearLayout ll_update_state;

    private ProgressBar pb_update_progress;

    private TextView tv_surplus_time;

    private BluetoothService bcs;

    private byte[] filebytes;

    private String url;

    private String content;

    private boolean is_erase;

//    private int address;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
//                    Log.d(LOG_TAG, "正在等待马桶做好升级准备");
                    tv_surplus_time.setText("正在等待马桶做好升级准备");
                    break;
                case 2:
//                    Log.d(LOG_TAG, "正在准备升级");
                    tv_surplus_time.setText("正在准备升级");
                    BluetoothController.getInstance().setFilebytes(filebytes);
                    bcs.setUpdate(true);
                    sendEmptyMessageDelayed(14, 5000);
                    break;
                case 3:
                    is_erase = true;
//                    Log.d(LOG_TAG, "芯片擦除成功，正在向马桶发送数据");
                    break;
                case 4:
                    showUpdateFailDialog();
//                    Log.d(LOG_TAG, "芯片擦除失败，重新擦除芯片数据");
                    break;
                case 5:
                    pb_update_progress.setMax(msg.arg1);
                    pb_update_progress.setProgress(msg.arg2);
                    tv_surplus_time.setText("正在升级:" + ((msg.arg2 * 100) / msg.arg1) + "%");
//                    Log.d(LOG_TAG, "数据写入成功，正在继续发送数据" + msg.arg2 + "/" + msg.arg1);
                case 6:
//                    Log.d(LOG_TAG, "数据发送完成，正在写入06数据");
                    break;
                case 7:
//                    tv_surplus_time.setText("10数据校验失败，正在重新写入数据");
//                    Log.d(LOG_TAG, "10数据校验失败，正在重新写入数据");
                    break;
                case 8:
//                    tv_surplus_time.setText("10数据写入失败，正在重新写入数据");
//                    Log.d(LOG_TAG, "10数据写入失败，正在重新写入数据");
                    showUpdateFailDialog();
                    break;
                case 9:
//                    tv_surplus_time.setText("06写入失败，请重启马桶，重新升级");
//                    Log.d(LOG_TAG, "06写入失败，请重启马桶，重新升级");
                    showUpdateFailDialog();
                    break;
                case 10:
//                    tv_surplus_time.setText("06数据校验失败，正在重新写入数据");
//                    Log.d(LOG_TAG, "06数据校验失败，正在重新写入数据");
                    break;
                case 11:
//                    Log.d(LOG_TAG, "升级完成");
                    bt_update.setVisibility(View.VISIBLE);
                    bt_update.setText("升级完成");
                    bt_update.setClickable(false);
                    ll_update_state.setVisibility(View.GONE);
                    sendEmptyMessageDelayed(16, 1000);
                    if (bcs != null) {
                        bcs.iStartAddr = 50;
                        bcs.iScanLengh = 30;
                    }
                    break;
                case 12:
//                    Log.d(LOG_TAG, "超时错误，正在重新发送命令");
                    break;
                case 13:
                    myThread.start();
                    break;
                case 14:
                    bcs.setRegisterValue(1, 90, 2);
                    is_erase = false;
                    sendEmptyMessageDelayed(15, 10000);
                    break;
                case 15:
                    if (!is_erase) {
                        sendEmptyMessage(14);
                    }
                    break;
                case 16:
                    bcs.setUpdate(false);
                    break;
                case 17:
                    showUpdateFailDialog();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        mContext = this;
        initActionBar("马桶软件升级");
        initView();
        initData();
        initListener();

    }

    private void initView() {
        bt_update = findViewById(R.id.bt_toilet_update);
        pb_update_progress = findViewById(R.id.pb_update_progress);
        ll_update_state = findViewById(R.id.ll_update_state);
        tv_surplus_time = findViewById(R.id.tv_surplus_time);
        tv_update_content = findViewById(R.id.tv_update_content);
    }

    private void initData() {
        bcs = ApplicationXpClient.getInstance().getBluetoothService();
        BluetoothController.getInstance().setOnUpdateReceiverListener(this);
        bcs.iStartAddr = 70;
        bcs.iScanLengh = 30;
        if (!bcs.isUpdate()) {
            url = getIntent().getStringExtra("url");
            content = getIntent().getStringExtra("content");
            url = HttpConstant.Url_Server + url;
            tv_update_content.setText(content);
//            Log.d(LOG_TAG, "url = " + url);
        } else {
            bt_update.setVisibility(View.GONE);
            ll_update_state.setVisibility(View.VISIBLE);
        }
    }

    private void getFile(String url) throws Exception {

        HttpUtils.getInstantce().downloadFile(url, new HttpConstant.SampleJsonResultListener<File>() {
            @Override
            public void onSuccess(File jsonData) {
                filebytes = getBytesFromFile(jsonData);
                if (filebytes != null)
                    mHandler.sendEmptyMessage(13);
                else
                    T.showShort("获取失败");
            }

            @Override
            public void onFailure(File jsonData) {
//                Log.d(LOG_TAG, "获取失败");
            }
        });

    }

    private void initListener() {
        bt_update.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_toilet_update:
                ll_update_state.setVisibility(View.VISIBLE);
                bt_update.setVisibility(View.GONE);
                tv_surplus_time.setText("正在下载文件");
                try {
                    getFile(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private Thread myThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                if (bcs.getRegisterValue(1, 90) != 1) {
                    bcs.setRegisterValue(1, 72, 65535);
                    mHandler.sendEmptyMessage(1);
                    while (bcs.getRegisterValue(1, 90) != 1) {
                        Thread.sleep(1000);
                    }
                }
                mHandler.sendEmptyMessage(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    @Override
    public void toiletUpdateSuccess() {
        mHandler.sendEmptyMessage(11);
//        Log.d(LOG_TAG, "升级成功");
    }

    @Override
    public void toiletIcEraseSuccess() {
        mHandler.sendEmptyMessage(3);
//        Log.d(LOG_TAG, "芯片擦除成功");
    }

    @Override
    public void toiletIcEraseFail() {
        mHandler.sendEmptyMessage(4);
//        Log.d(LOG_TAG, "芯片擦除失败");
    }

    @Override
    public void toiletIcWriteSuccess(int total_length, int current_length) {
        if (current_length < total_length) {
            Message message = new Message();
            message.arg1 = total_length;
            message.arg2 = current_length;
            message.what = 5;
            mHandler.sendMessage(message);
//            Log.d(LOG_TAG, "total_length = " + total_length + "  current_length" + current_length);
        } else {
            mHandler.sendEmptyMessage(6);
        }
    }

    @Override
    public void toiletIc06WriteFail() {
        mHandler.sendEmptyMessage(9);
//        Log.d(LOG_TAG, "06写入错误");
    }

    @Override
    public void toiletIc10WriteFail() {
        mHandler.sendEmptyMessage(8);
//        Log.d(LOG_TAG, "10写入错误");
    }

    @Override
    public void toilet10IcCheckFail() {
        mHandler.sendEmptyMessage(7);
//        Log.d(LOG_TAG, "10校验错误");
    }

    @Override
    public void toilet06IcCheckFail() {
        mHandler.sendEmptyMessage(10);
    }

    @Override
    public void toiletUpdateFail() {
        mHandler.sendEmptyMessage(17);
//        Log.d(LOG_TAG, "-----内存溢出，升级失败----");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothController.getInstance().setOnUpdateReceiverListener(null);
        mHandler = null;
    }

    public static byte[] getBytesFromFile(File file) {
        byte[] ret = null;
        try {
            if (file == null) {
                // log.error("helper:the file is null!");
                return null;
            }
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            out.close();
            ret = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (bcs.isUpdate()) {
                CustomizeDialog.Builder builder = new CustomizeDialog.Builder(mContext, CustomizeDialog.WARNING_TYPE);
                builder.setContentText("马桶程序正在升级，退出可能导致升级失败，是否确定退出");
                builder.setConfirmClickListener(new CustomizeDialog.OnCustomizeClickListener() {
                    @Override
                    public void onClick(CustomizeDialog customizeDialog) {
                        finish();
                    }
                });
                builder.builder().show();
                return false;

            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showUpdateFailDialog() {
        CustomizeDialog.Builder builder = new CustomizeDialog.Builder(this, CustomizeDialog.ERROR_TYPE);
        builder.setContentText("升级失败，请重启马桶重新升级");
        builder.setConfirmClickListener(new CustomizeDialog.OnCustomizeClickListener() {
            @Override
            public void onClick(CustomizeDialog customizeDialog) {
                bcs.setUpdate(false);
                finish();
            }
        });
        builder.builder().show();
    }
}
