package com.xiaopu.customer.activity;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.BloodOxygenData;
import com.xiaopu.customer.data.jsonresult.BloodPressure;
import com.xiaopu.customer.data.jsonresult.WristbandsHeartrate;
import com.xiaopu.customer.service.BluetoothService;
import com.xiaopu.customer.utils.ControlSave;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.TimeUtils;
import com.xiaopu.customer.utils.bluetooth.ConstantUtils;
import com.xiaopu.customer.utils.bluetooth.ConvertUtils;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/9/11.
 */

public class MeasurementActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = MeasurementActivity.class.getSimpleName();

    private Context mContext;

    private ImageView iv_measure_bg;

    private TextView tv_measurement;

    private TextView tv_measure_time;

    private RelativeLayout rvl_heart_rate;

    private TextView tv_heart_rate;

    private RelativeLayout rvl_blood_pressure;

    private TextView tv_blood_pressure;

    private RelativeLayout rvl_blood_oxygen;

    private TextView tv_blood_oxygen;

    private BluetoothService bcs;

    private IntentFilter mInfilter;

    private ObjectAnimator animator;

    private Date max_date;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    bcs.oneKeyMeasure(0);
                    break;
                case 2:
                    unregisterReceiver(myReceicer);
                    animator.end();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        initActionBar("测量");
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        iv_measure_bg = (ImageView) findViewById(R.id.iv_measure_bg);
        tv_measurement = (TextView) findViewById(R.id.tv_measurement);
        tv_measure_time = (TextView) findViewById(R.id.tv_recent_measure_time);
        rvl_heart_rate = (RelativeLayout) findViewById(R.id.rvl_heart_rate);
        tv_heart_rate = (TextView) findViewById(R.id.tv_heart_rate);
        rvl_blood_pressure = (RelativeLayout) findViewById(R.id.rvl_blood_pressure);
        tv_blood_pressure = (TextView) findViewById(R.id.tv_blood_pressure);
        rvl_blood_oxygen = (RelativeLayout) findViewById(R.id.rvl_blood_oxygen);
        tv_blood_oxygen = (TextView) findViewById(R.id.tv_blood_oxygen);
    }

    private void initData() {
        bcs = ApplicationXpClient.getInstance().getBluetoothService();
        mInfilter = new IntentFilter();
        mInfilter.addAction(ConstantUtils.ACTION_RECEIVE_MESSAGE_FROM_BAND);
        animator = ObjectAnimator.ofFloat(iv_measure_bg, "rotation", 0f, 360f);
        animator.setDuration(1500);
        animator.setRepeatCount(-1);

        initBandData();

    }

    private void initBandData() {
        String str_heart_rate = ControlSave.read(mContext, ConstantUtils.BAND_RECENT_HEART_RATE, "");
        String str_blood_pressure = ControlSave.read(mContext, ConstantUtils.BAND_RECENT_BLOOD_PRESSURE, "");
        String str_blood_oxygen = ControlSave.read(mContext, ConstantUtils.BAND_RECENT_BLOOD_OXYGEN, "");
        if (!TextUtils.isEmpty(str_heart_rate)) {
            WristbandsHeartrate recent_heart_rate = HttpUtils.gb.create().fromJson(str_heart_rate, WristbandsHeartrate.class);
            tv_heart_rate.setText("心率:" + (new Double(recent_heart_rate.getHeartRate())).intValue() + "次/分");
            max_date = recent_heart_rate.getDetectTime();
        }
        if (!TextUtils.isEmpty(str_blood_pressure)) {
            BloodPressure recent_blood_pressure = HttpUtils.gb.create().fromJson(str_blood_pressure, BloodPressure.class);
            tv_blood_pressure.setText("血压:" + recent_blood_pressure.getHypertension() + "/" + recent_blood_pressure.getHypotension() + "mmhg");
            if (max_date.getTime() < recent_blood_pressure.getDetectTime().getTime()) {
                max_date = recent_blood_pressure.getDetectTime();
            }
        }
        if (!TextUtils.isEmpty(str_blood_oxygen)) {
            BloodOxygenData recent_blood_oxygen = HttpUtils.gb.create().fromJson(str_blood_oxygen, BloodOxygenData.class);
            tv_blood_oxygen.setText("血氧:" + recent_blood_oxygen.getBloodoxygen() + "%");
            if (max_date.getTime() < recent_blood_oxygen.getDetectTime().getTime()) {
                max_date = recent_blood_oxygen.getDetectTime();
            }
        }
        if (max_date != null) {
            tv_measure_time.setText(TimeUtils.DateToStringSimpleDateFormat(max_date, TimeUtils.DATA_FORMAT_NO_YEAR));
        }
    }

    private void initListener() {
        tv_measurement.setOnClickListener(this);
        rvl_heart_rate.setOnClickListener(this);
        rvl_blood_pressure.setOnClickListener(this);
        rvl_blood_oxygen.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_measurement:
                if (ApplicationXpClient.isBandConnect()) {
                    tv_measurement.setText("正在测量");
                    animator.start();
                    bcs.oneKeyMeasure(1);
                    mHandler.sendEmptyMessageDelayed(1, 61000);
                    registerReceiver(myReceicer, mInfilter);
                } else {
                    T.showShort("手环未连接");
                }
                break;
            case R.id.rvl_heart_rate:
                startActivity(new Intent(mContext, BandHeartActivity.class));
                break;
            case R.id.rvl_blood_pressure:
                startActivity(new Intent(mContext, BloodPressureActivity.class));
                break;
            case R.id.rvl_blood_oxygen:
                startActivity(new Intent(mContext, BloodPressureActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBandData();
    }

    private BroadcastReceiver myReceicer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConstantUtils.ACTION_RECEIVE_MESSAGE_FROM_BAND)) {
                byte[] band_data = intent.getByteArrayExtra("band_data");
                if ((band_data[4] & 0xff) == 0x32 && (band_data[5] & 0xff) == 0x80) {
                    tv_measurement.setText("一键测量");
                    mHandler.sendEmptyMessage(2);
                    tv_heart_rate.setText("心率:" + (band_data[6] & 0xff) + "bpm");
                    tv_blood_pressure.setText("血压:" + (band_data[8] & 0xff) + "/" + (band_data[9] & 0xff) + "mmhg");
                    tv_blood_oxygen.setText("血氧:" + (band_data[7] & 0xff) + "%");

                    tv_measure_time.setText(TimeUtils.DateToStringSimpleDateFormat(new Date(), TimeUtils.DATA_FORMAT_NO_YEAR));

                    WristbandsHeartrate wristbandsHeartrate = new WristbandsHeartrate();
                    wristbandsHeartrate.setDetectTime(new Date());
                    wristbandsHeartrate.setHeartRate(Double.valueOf((band_data[6] & 0xff)));
                    wristbandsHeartrate.setCategory("1");
                    List<WristbandsHeartrate> heartrateList = new ArrayList<>();
                    heartrateList.add(wristbandsHeartrate);
                    ControlSave.save(mContext, ConstantUtils.BAND_RECENT_HEART_RATE, HttpUtils.gb.create().toJson(wristbandsHeartrate));

                    BloodPressure bloodPressure = new BloodPressure();
                    bloodPressure.setCategory("1");
                    bloodPressure.setDetectTime(new Date());
                    bloodPressure.setHypertension(String.valueOf((band_data[8] & 0xff)));
                    bloodPressure.setHypotension((String.valueOf((band_data[9] & 0xff))));
                    List<BloodPressure> bloodPressureList = new ArrayList<>();
                    bloodPressureList.add(bloodPressure);
                    ControlSave.save(mContext, ConstantUtils.BAND_RECENT_BLOOD_PRESSURE, HttpUtils.gb.create().toJson(bloodPressure));

                    BloodOxygenData bloodOxygenData = new BloodOxygenData();
                    bloodOxygenData.setCategory("1");
                    bloodOxygenData.setDetectTime(new Date());
                    bloodOxygenData.setBloodoxygen(String.valueOf((band_data[7] & 0xff)));
                    List<BloodOxygenData> bloodOxygenDataList = new ArrayList<>();
                    bloodOxygenDataList.add(bloodOxygenData);
                    ControlSave.save(mContext, ConstantUtils.BAND_RECENT_BLOOD_OXYGEN, HttpUtils.gb.create().toJson(bloodOxygenData));

                    HttpUtils.getInstantce().saveBandData(heartrateList, HttpConstant.Url_saveWristbandsHeartrateData, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
                        @Override
                        public void onSuccess(Feedback<String> jsonData) {
                            Log.d(LOG_TAG, "手环心率保存成功");
                        }

                        @Override
                        public void onFailure(Feedback<String> jsonData) {
                            Log.d(LOG_TAG, "手环心率保存失败");
                        }
                    });

                    HttpUtils.getInstantce().saveBandData(bloodOxygenDataList, HttpConstant.Url_saveBloodoxygenData, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
                        @Override
                        public void onSuccess(Feedback<String> jsonData) {
                            Log.d(LOG_TAG, "手环血氧保存成功");
                        }

                        @Override
                        public void onFailure(Feedback<String> jsonData) {
                            Log.d(LOG_TAG, "手环血氧保存失败");
                        }
                    });

                    HttpUtils.getInstantce().saveBandData(bloodPressureList, HttpConstant.Url_saveBloodPressureData, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
                        @Override
                        public void onSuccess(Feedback<String> jsonData) {
                            Log.d(LOG_TAG, "手环血压保存成功");
                        }

                        @Override
                        public void onFailure(Feedback<String> jsonData) {
                            Log.d(LOG_TAG, "手环血压保存失败");
                        }
                    });

                }
            }
        }
    };
}
