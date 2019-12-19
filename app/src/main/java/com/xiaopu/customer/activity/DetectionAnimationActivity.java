package com.xiaopu.customer.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.jsonresult.LSItemBase;
import com.xiaopu.customer.service.BluetoothService;
import com.xiaopu.customer.utils.Config;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/6/6.
 */

public class DetectionAnimationActivity extends ActivityBase {
    private static final String LOG_TAG = DetectionAnimationActivity.class.getSimpleName();

    private static final int TYPE_PREGNANCY = 1;

    private static final int TYPE_OVULATION = 2;

    private static final int DETECTION_WAITING = 1;

    private static final int DETECTION_START = 2;

    private static final int DETECTION_FINISH_ONE = 3;

    private static final int DETECTION_FINISH_TWO = 4;

    private Context mContext;

    private FrameLayout frameLayout_pregnancy;

    private FrameLayout frameLayout_ovulation;

    private TextView tv_detection_describe;

    private Button bt_stop_detection;

    private ImageView iv_return;

    private ImageView iv_pregnancy_circle;

    private ImageView iv_ovulation_circle;

    private ImageView iv_ovulation_icon;

    private List<Long> dataList;

    private StringBuffer strs;

    private int detection_type;

    private int detection_result;

    private boolean isStop;

    private BluetoothService bcs;

    private LSItemBase.YJLSItem yjItemBase;

    private LSItemBase.PLItem plItemBase;

    private MyClickListener mClick;

    private AnimatorSet animSet;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DETECTION_WAITING:
                    tv_detection_describe.setText("正在等待试纸反应，检测将于" + (180 - msg.arg1) + "秒后开始");
                    break;
                case DETECTION_START:
                    if (detection_type == TYPE_PREGNANCY) {
                        bcs.setRegisterValue(1, Config.REGISTER_URINE_TEST1, 2); //孕检开始检测
                    } else if (detection_type == TYPE_OVULATION) {
                        bcs.setRegisterValue(1, Config.REGISTER_URINE_TEST1, 3);//排卵开始检测
                    }
                    tv_detection_describe.setText("正在检测，请稍候...");
                    break;
                case DETECTION_FINISH_ONE:
                    dataList.clear();
                    for (int i = 12; i < 30; i++) {
                        long value = bcs.getRegisterValue(1, i) * 65535 + bcs.getRegisterValue(1, i + 1);
                        dataList.add(value);
                        i++;
                    }
                    Log.d(LOG_TAG, "----dataList.size() = " + dataList.size());
                    bcs.setRegisterValue(1, Config.REGISTER_URINE_TEST1, 39);
                    break;
                case DETECTION_FINISH_TWO:
                    stopDetection();
                    tv_detection_describe.setText("检测完成");
                    for (int i = 12; i < 30; i++) {

                        long value = bcs.getRegisterValue(1, i) * 65535 + bcs.getRegisterValue(1, i + 1);
                        dataList.add(value);
                        i++;
                    }
                    Log.d(LOG_TAG, "----dataList.size() = " + dataList.size());

                   /* long min1 = dataList.get(1);
                    for (int i = 1; i < 6; i++) {
                        if (min1 > dataList.get(i)) {
                            min1 = dataList.get(i);
                        }
                    }

                    long min2 = dataList.get(9);
                    for (int i = 9; i < 15; i++) {
                        if (min2 > dataList.get(i)) {
                            min2 = dataList.get(i);
                        }
                    }

                    long max3 = dataList.get(6);
                    for (int i = 6; i < 9; i++) {
                        if (max3 < dataList.get(i)) {
                            max3 = dataList.get(i);
                        }
                    }*/

                    long min1 = dataList.get(2);
                    for (int i = 2; i < 7; i++) {
                        if (min1 > dataList.get(i)) {
                            min1 = dataList.get(i);
                        }
                    }

                    long min2 = dataList.get(10);
                    for (int i = 10; i < 15; i++) {
                        if (min2 > dataList.get(i)) {
                            min2 = dataList.get(i);
                        }
                    }

                    long max3 = dataList.get(7);
                    for (int i = 7; i < 10; i++) {
                        if (max3 < dataList.get(i)) {
                            max3 = dataList.get(i);
                        }
                    }

                    if (min1 <= max3) {
                        if (min2 <= max3) {
                            if (max3 - min1 > 12 && max3 - min2 > 12 && Math.abs((max3 - min2)) / (max3 - min1) < 3) {
                                //TODO 第一杠浅 ,认为未怀孕or未怀孕
                                detection_result = 1;
                            } else {
                                //TODO 两杠比较深
                                detection_result = 0;
                            }
                        } else {
                            detection_result = 0;
                        }
                    } else {
                        if (min2 <= max3) {
                            //TODO 有一条杠
                            detection_result = 0;
                        } else {
                            detection_result = 2;
                        }
                    }
                    strs = new StringBuffer();

                    for (int i = 0; i < dataList.size(); i++) {
                        if (i == dataList.size() - 1) {
                            strs.append("" + dataList.get(i));
                        } else {
                            strs.append("" + dataList.get(i) + ",");
                        }
                    }
                    Intent mIntent = new Intent(mContext, DetectionResultActivity.class);
                    Bundle bundle = new Bundle();
                    if (detection_type == TYPE_PREGNANCY) {
                        yjItemBase.sourceData = strs.toString();
                        yjItemBase.pregnancyResult = detection_result;
                        bundle.putSerializable("result", yjItemBase);
                        mIntent.putExtra("type", 1);
                    } else if (detection_type == TYPE_OVULATION) {
                        plItemBase.sourceData = strs.toString();
                        plItemBase.ovulationResult = detection_result;
                        bundle.putSerializable("result", plItemBase);
                        mIntent.putExtra("type", 2);
                    }
                    mIntent.putExtras(bundle);
                    startActivity(mIntent);
                    finish();
                    mHandler.sendEmptyMessage(5);
                    break;
                case 5:
                    bcs.setRegisterValue(1, Config.REGISTER_URINE_DOOR, 3);
                    break;
            }
            return false;
        }
    });

    private void stopAnimation() {
        if (animSet != null) {
            animSet.end();
            animSet = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregnancy_animation);
        mContext = this;
        initView();
        initData();
        initListener();

    }

    private void initView() {
        frameLayout_pregnancy = (FrameLayout) findViewById(R.id.frameLayout_pregnancy);
        frameLayout_ovulation = (FrameLayout) findViewById(R.id.frameLayout_ovulation);
        tv_detection_describe = (TextView) findViewById(R.id.tv_detection_describe);
        bt_stop_detection = (Button) findViewById(R.id.bt_stop_detection);
        iv_pregnancy_circle = (ImageView) findViewById(R.id.iv_pregnancy_circle);
        iv_ovulation_circle = (ImageView) findViewById(R.id.iv_ovulation_circle);
        iv_ovulation_icon = (ImageView) findViewById(R.id.iv_ovulation_icon);
        iv_return = (ImageView) findViewById(R.id.iv_return);
    }

    private void initData() {
        detection_type = getIntent().getIntExtra("type", 0);
        mClick = new MyClickListener();
        bcs = ApplicationXpClient.getInstance().getBluetoothService();
        dataList = new ArrayList<>();
        yjItemBase = new LSItemBase.YJLSItem();
        plItemBase = new LSItemBase.PLItem();
        startDetection();
        startAnimation();
    }

    private void startDetection() {
        isStop = false;
        bcs.iStartAddr = 0;
        bcs.iScanLengh = 30;
        mThread.start();
    }

    private void startAnimation() {
        animSet = new AnimatorSet();
        if (detection_type == TYPE_PREGNANCY) {
            initActionBar("孕检");
            frameLayout_pregnancy.setVisibility(View.VISIBLE);
            frameLayout_ovulation.setVisibility(View.GONE);
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(iv_pregnancy_circle, "scaleX", 1f, 1.2f, 1f);
            animator1.setDuration(3000);
            animator1.setRepeatCount(1000);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(iv_pregnancy_circle, "scaleY", 1f, 1.2f, 1f);
            animator2.setDuration(3000);
            animator2.setRepeatCount(1000);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(iv_pregnancy_circle, "rotation", 0f, 360f);
            animator3.setDuration(3000);
            animator3.setRepeatCount(1000);
            animSet.playTogether(animator1, animator2, animator3);
            animSet.start();
        } else if (detection_type == TYPE_OVULATION) {
            initActionBar("排卵检测");
            frameLayout_ovulation.setVisibility(View.VISIBLE);
            frameLayout_pregnancy.setVisibility(View.GONE);
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(iv_ovulation_circle, "scaleX", 1f, 1.2f, 1f);
            animator1.setDuration(3000);
            animator1.setRepeatCount(1000);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(iv_ovulation_circle, "scaleY", 1f, 1.2f, 1f);
            animator2.setDuration(3000);
            animator2.setRepeatCount(1000);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(iv_ovulation_circle, "rotation", 0f, 360f);
            animator3.setDuration(3000);
            animator3.setRepeatCount(1000);
            ObjectAnimator animator4 = ObjectAnimator.ofFloat(iv_ovulation_icon, "rotation", 360f, 0f);
            animator4.setDuration(3000);
            animator4.setRepeatCount(1000);
            animSet.playTogether(animator1, animator2, animator3, animator4);
            animSet.start();
        }

    }

    private void initListener() {
        bt_stop_detection.setOnClickListener(mClick);
        iv_return.setOnClickListener(mClick);
    }


    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            int wait_pro = 0;
            while (wait_pro < 180 && !isStop) {
                try {
                    Message message = new Message();
                    message.what = DETECTION_WAITING;
                    message.arg1 = wait_pro;
                    mHandler.sendMessage(message);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                wait_pro++;
            }
            if (!isStop) {
                mHandler.sendEmptyMessage(DETECTION_START);
                try {
                    mThread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //获取孕检检测结果
            while (bcs.getRegisterValue(1, Config.REGISTER_URINE_TEST1) != 38 && !isStop) {
                try {
                    mThread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!isStop) {
                mHandler.sendEmptyMessage(DETECTION_FINISH_ONE);
            }
            while (bcs.getRegisterValue(1, Config.REGISTER_URINE_TEST1) < 40 && !isStop) {
                try {
                    mThread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!isStop) {
                mHandler.sendEmptyMessage(DETECTION_FINISH_TWO);
            }
        }
    });

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_stop_detection:
                    confirmQuit();
                    break;
                case R.id.iv_return:
                    confirmQuit();
                    break;
            }
        }
    }

    private void stopDetection() {
        isStop = true;
        mThread = null;
        stopAnimation();
    }


    private void confirmQuit() {
        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("提示")
                .setContentText("确定中断检测")
                .setCancelText("取消")
                .setConfirmText("确定")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        stopDetection();
                        bcs.setRegisterValue(1, Config.REGISTER_URINE_DOOR, 4);
                        mHandler.sendEmptyMessageDelayed(5, 5000);
                        finish();
                    }
                })
                .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            confirmQuit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
