package com.xiaopu.customer.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.UrineDetailActivity;
import com.xiaopu.customer.adapter.ChartGridViewAdapter;
import com.xiaopu.customer.adapter.UrineResultAdapter;
import com.xiaopu.customer.data.BandDataDetail;
import com.xiaopu.customer.data.ChartData;
import com.xiaopu.customer.data.Detection_Urine_item;
import com.xiaopu.customer.data.EntityDevice;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.BloodOxygenData;
import com.xiaopu.customer.data.jsonresult.BloodPressure;
import com.xiaopu.customer.data.jsonresult.DetectionHeartrate;
import com.xiaopu.customer.data.jsonresult.DetectionLine;
import com.xiaopu.customer.data.jsonresult.DetectionOvulation;
import com.xiaopu.customer.data.jsonresult.DetectionPregnant;
import com.xiaopu.customer.data.jsonresult.DetectionUrine;
import com.xiaopu.customer.data.jsonresult.DetectionWeight;
import com.xiaopu.customer.data.jsonresult.HealthCommentResult;
import com.xiaopu.customer.data.jsonresult.LastDetectionData;
import com.xiaopu.customer.data.jsonresult.Sleeping;
import com.xiaopu.customer.data.jsonresult.Walk;
import com.xiaopu.customer.data.jsonresult.WristbandsHeartrate;
import com.xiaopu.customer.service.BluetoothService;
import com.xiaopu.customer.utils.ControlSave;
import com.xiaopu.customer.utils.TimeUtils;
import com.xiaopu.customer.utils.bluetooth.BluetoothController;
import com.xiaopu.customer.utils.bluetooth.ConstantUtils;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.GridViewForScrollview;
import com.xiaopu.customer.view.LoadingView.LoadingView;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/15 0015.
 */
public class MyHealthDataActivity extends ActivityBase {
    private static final String LOG_TAG = MyHealthDataActivity.class.getSimpleName();

    private static final String RECRNT_DATA_TIME = "recent_getBandDataTime";

    private Context mContext;

    private final static int PREGNANCY = 1;

    private final static int UNPREGNANCY = 0;

    private final static int OVULATION = 1;

    private final static int UNOVULATION = 0;

    private final static int HEARTRATE_MORE = 1;
    private final static int OVULATION_MORE = 2;
    private final static int PREGNANT_MORE = 3;
    private final static int URINE_MORE = 4;
    private final static int WEIGHT_MORE = 5;

    private final static String NONE_URINE_DATA = "暂无尿检数据";
    private final static String NONE_HEART_DATA = "暂无心率数据";
    private final static String NONE_DM_DATA = "暂无糖尿病评测数据";
    private final static String NONE_SUBHEALTH_DATA = "暂无亚健康评测数据";
    private final static String NONE_CORONARY_DATA = "暂无冠心病评测数据";
    private final static String NONE_PREGNANCY_DATA = "暂无孕前自测评测数据";
    private final static String NONE_HYPERLIPOIDEMIA_DATA = "暂无高血脂评测数据";
    private final static String NONE_HYPERTEMSION_DATA = "暂无高血压评测数据";

    private TextView tv_connect_state;

    private RelativeLayout llHeartrate;

    private LinearLayout llOvulation;

    private LinearLayout llPregnant;

    private LinearLayout llUrine;

    private RelativeLayout llWeight;

    private TextView tvMoreHeartrate;

    private TextView tvMoreOvulation;

    private TextView tvMorePregnant;

    private TextView tvMoreUrine;

    private TextView tvMoreWeight;

    private TextView tvRecentHeartrate;

    private TextView tvRecentOvulation;

    private TextView tvRecentPregnant;

    private TextView tvRecentUrine;

    private TextView tvRecentWeight;

    private TextView tvWeight;

    private TextView tvWeightState;

    private TextView tvBMI;

    private TextView tvUserName;

    private TextView tvUserAge;

    private TextView tvUserPhone;

    private TextView tvHeartRate;

    private ListView mDetailListView;

    private ImageView ivUserHeader;

    private ImageView ivPregnantState;

    private ImageView ivOvulationState;

    private ImageView ivDownUrineDetail;

    private LineChart mHealthChart;

    private GridViewForScrollview gv_health_item;

    private LinearLayout ll_band_data;

    private TextView tv_no_band_tip;

    private LinearLayout ll_band_step;

    private LinearLayout ll_band_sleep;

    private LinearLayout ll_band_heart;

    private LinearLayout ll_band_blood_pressure;

    private LinearLayout ll_band_blood_oxygen;

    private TextView tv_measurement;

    private List<DetectionHeartrate> mDetectionHeartrateList;
    private List<DetectionOvulation> mDetectionOvulationList;
    private List<DetectionPregnant> mDetectionPregnantList;
    private List<DetectionUrine> mDetectionUrineList;
    private List<DetectionWeight> mDetectionWeightList;

    private MyClick myClick;

    private ScrollView mScrollView;

    private LoadingView mLoadingView;

    private TextView tv_loading_fail;

    private TextView tv_no_data;

    private TextView tv_no_data_two;

    private TextView tv_step_time;

    private TextView tv_step_count;

    private TextView tv_step_no_data;

    private ProgressBar pb_step;

    private TextView tv_sleep_time;

    private TextView tv_sleep_no_data;

    private ProgressBar pb_sleep;

    private TextView tv_sleep_length;

    private TextView tv_heart_time;

    private TextView tv_heart_rate;

    private ImageView iv_heart_no_data;

    private TextView tv_blood_pressure_time;

    private TextView tv_blood_pressure_value;

    private ImageView iv_blood_pressure_no_data;

    private TextView tv_blood_oxygen_time;

    private TextView tv_blood_oxygen_value;

    private ImageView iv_blood_oxygen_no_data;

    private TextView tv_heart_rate_no_data;

    private TextView tv_pregnancy_no_data;

    private TextView tv_ovulation_no_data;

    private TextView tv_urine_no_data;

    private UrineResultAdapter mAdapter;

    private String[] urine_names = new String[]{"白细胞", "亚硝酸盐", "尿胆原", "蛋白质", "PH", "潜血", "比重", "酮体", "胆红素", "葡萄糖", "维生素"

    };

    private String[] detection_names = new String[]{"白细胞", "亚硝酸盐", "尿胆原", "蛋白质", "PH", "潜血", "比重", "酮体", "胆红素", "葡萄糖", "维生素", "心率", "糖尿病"
            , "亚健康", "冠心病", "孕前自测", "高血脂", "高血压"
    };

    private List<Detection_Urine_item> detection_urine_items = new ArrayList<>();

    private List<ChartData> chartDataList;

    private LineDataSet dataSet;

    private XAxis xAxis;

    private ChartGridViewAdapter mGridViewAdapter;

    private Detection_Urine_item detection_urine_item;

    private BluetoothService bcs;

    private BluetoothController mController;

    private List<BloodOxygenData> dataList_bloodoxygen;

    private List<BloodPressure> dataList_bloodpressure;

    private List<WristbandsHeartrate> dataList_heart;

    private List<Walk> dataList_walk;

    private List<Sleeping> dataList_sleep;

    private List<BloodOxygenData> dataList_bloodoxygen_hour;

    private List<BloodPressure> dataList_bloodpressure_hour;

    private List<WristbandsHeartrate> dataList_heart_hour;

    private List<Walk> dataList_walk_hour;

    private List<Sleeping> dataList_sleep_hour;


    private Walk current_walk;

    private Sleeping current_sleep;

    private List<EntityDevice> entityDeviceList;

    private List<EntityDevice> searchDeviceList;

    private int select_position;

    private boolean isOnlyOneDevice;

    private MyReceiver myReceiver;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                case 2:
                    tv_step_no_data.setVisibility(View.GONE);
                    pb_step.setVisibility(View.VISIBLE);
                    String str_target = ControlSave.read(mContext, ConstantUtils.BAND_TARGET_STEP, "");
                    int value;
                    if (TextUtils.isEmpty(str_target)) {
                        value = (current_walk.getWalkCount() * 100) / 8000;
                    } else {
                        value = (current_walk.getWalkCount() * 100) / Integer.valueOf(str_target);
                    }
                    pb_step.setProgress(value);
                    tv_step_time.setText(TimeUtils.DateToStringSimpleDateFormat(current_walk.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
                    tv_step_count.setText("步行:" + current_walk.getWalkCount() + "/步");
                    break;
                case 3:
                    tv_sleep_no_data.setVisibility(View.GONE);
                    pb_sleep.setVisibility(View.VISIBLE);
                    tv_sleep_time.setText(TimeUtils.DateToStringSimpleDateFormat(current_sleep.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
                    String[] datas = current_sleep.getSleepDuration().split("-");
                    tv_sleep_length.setText("睡眠:" + datas[0] + "小时" + datas[1] + "分钟");
                    int sleep_value = Integer.valueOf(datas[0]) * 60 + Integer.valueOf(datas[1]);
                    pb_sleep.setProgress(sleep_value * 100 / 480);
                    break;
                case 1:
                    //缓存本次获取到数据的时间
                    ControlSave.save(mContext, RECRNT_DATA_TIME, TimeUtils.DateToStringDatail(new Date()));
                    EntityDevice connectedDevice = ApplicationXpClient.getInstance().getConnectBandDevice();
                    if (TextUtils.isEmpty(connectedDevice.getNickName())) {
                        tv_connect_state.setText("已连接" + connectedDevice.getDeviceName());
                    } else {
                        tv_connect_state.setText("已连接" + connectedDevice.getNickName());
                    }

                    if (dataList_walk.size() != 0) {
                        HttpUtils.getInstantce().saveBandData(dataList_walk, HttpConstant.Url_saveWalkData, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
                            @Override
                            public void onSuccess(Feedback<String> jsonData) {
                                Log.d(LOG_TAG, "步行保存成功");
                            }

                            @Override
                            public void onFailure(Feedback<String> jsonData) {
                                Log.d(LOG_TAG, "步行保存失败");
                            }
                        });

                    }
                    if (dataList_sleep.size() != 0) {
                        HttpUtils.getInstantce().saveBandData(dataList_sleep, HttpConstant.Url_saveSleepingData, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
                            @Override
                            public void onSuccess(Feedback<String> jsonData) {
                                Log.d(LOG_TAG, "睡眠保存成功");
                            }

                            @Override
                            public void onFailure(Feedback<String> jsonData) {
                                Log.d(LOG_TAG, "睡眠保存失败");
                            }
                        });

                        Sleeping sleeping = null;
                        if (dataList_sleep_hour.size() != 0) {
                            if (dataList_sleep_hour.get(dataList_sleep_hour.size() - 1).getDetectTime().getTime() < dataList_sleep.get(dataList_sleep.size() - 1).getDetectTime().getTime()) {
                                sleeping = dataList_sleep.get(dataList_sleep.size() - 1);
                            } else {
                                sleeping = dataList_sleep_hour.get(dataList_sleep_hour.size() - 1);
                            }
                        } else {
                            sleeping = dataList_sleep.get(dataList_sleep.size() - 1);
                        }
                        tv_sleep_no_data.setVisibility(View.GONE);
                        pb_sleep.setVisibility(View.VISIBLE);
                        tv_sleep_time.setText(TimeUtils.DateToStringSimpleDateFormat(sleeping.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
                        String[] datas_new = sleeping.getSleepDuration().split("-");
                        tv_sleep_length.setText("睡眠:" + datas_new[0] + "小时" + datas_new[1] + "分钟");
                        int sleep_value_new = Integer.valueOf(datas_new[0]) * 60 + Integer.valueOf(datas_new[1]);
                        pb_sleep.setProgress(sleep_value_new * 100 / 480);

                        String str_sleeping = ControlSave.read(mContext, ConstantUtils.BAND_CURRENT_SLEEP, "");
                        if (!TextUtils.isEmpty(str_sleeping)) {
                            Sleeping recent_sleeping = HttpUtils.gb.create().fromJson(str_sleeping, Sleeping.class);
                            if (recent_sleeping.getDetectTime().getTime() < sleeping.getDetectTime().getTime()) {
                                ControlSave.save(mContext, ConstantUtils.BAND_CURRENT_SLEEP, HttpUtils.gb.create().toJson(sleeping));
                            }
                        } else {
                            ControlSave.save(mContext, ConstantUtils.BAND_CURRENT_SLEEP, HttpUtils.gb.create().toJson(sleeping));
                        }

                    }
                    if (dataList_heart.size() != 0) {
                        HttpUtils.getInstantce().saveBandData(dataList_heart, HttpConstant.Url_saveWristbandsHeartrateData, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
                            @Override
                            public void onSuccess(Feedback<String> jsonData) {
                                Log.d(LOG_TAG, "心率保存成功");
                            }

                            @Override
                            public void onFailure(Feedback<String> jsonData) {
                                Log.d(LOG_TAG, "心率保存失败");
                            }
                        });
                        //找出整点测量和单次测量的最新一条数据
                        WristbandsHeartrate wristbandsHeartrate = null;
                        if (dataList_heart_hour.size() != 0) {
                            if (dataList_heart_hour.get(dataList_heart_hour.size() - 1).getDetectTime().getTime() < dataList_heart.get(dataList_heart.size() - 1).getDetectTime().getTime()) {
                                wristbandsHeartrate = dataList_heart.get(dataList_heart.size() - 1);
                            } else {
                                wristbandsHeartrate = dataList_heart_hour.get(dataList_heart_hour.size() - 1);
                            }
                        } else {
                            wristbandsHeartrate = dataList_heart.get(dataList_heart.size() - 1);
                        }
                        tv_heart_time.setText(TimeUtils.DateToStringSimpleDateFormat(wristbandsHeartrate.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
                        tv_heart_rate.setText("心率:" + (new Double(wristbandsHeartrate.getHeartRate())).intValue() + "次/分");
                        String str_heart_rate = ControlSave.read(mContext, ConstantUtils.BAND_RECENT_HEART_RATE, "");
                        if (!TextUtils.isEmpty(str_heart_rate)) {
                            WristbandsHeartrate recent_heart_rate = HttpUtils.gb.create().fromJson(str_heart_rate, WristbandsHeartrate.class);
                            if (recent_heart_rate.getDetectTime().getTime() < wristbandsHeartrate.getDetectTime().getTime()) {
                                ControlSave.save(mContext, ConstantUtils.BAND_RECENT_HEART_RATE, HttpUtils.gb.create().toJson(wristbandsHeartrate));
                            }
                        } else {
                            ControlSave.save(mContext, ConstantUtils.BAND_RECENT_HEART_RATE, HttpUtils.gb.create().toJson(wristbandsHeartrate));
                        }

                        String str_heart_rate_new = ControlSave.read(mContext, ConstantUtils.BAND_RECENT_HEART_RATE, "");
                        WristbandsHeartrate recent_heart_rate_new = HttpUtils.gb.create().fromJson(str_heart_rate_new, WristbandsHeartrate.class);
                        tv_heart_time.setText(TimeUtils.DateToStringSimpleDateFormat(recent_heart_rate_new.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
                        tv_heart_rate.setText("心率:" + (new Double(recent_heart_rate_new.getHeartRate())).intValue() + "次/分");
                    }
                    if (dataList_bloodoxygen.size() != 0) {
                        iv_blood_oxygen_no_data.setImageResource(R.mipmap.blood_oxygen_progress);
                        HttpUtils.getInstantce().saveBandData(dataList_bloodoxygen, HttpConstant.Url_saveBloodoxygenData, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
                            @Override
                            public void onSuccess(Feedback<String> jsonData) {
                                Log.d(LOG_TAG, "血氧保存成功");
                            }

                            @Override
                            public void onFailure(Feedback<String> jsonData) {
                                Log.d(LOG_TAG, "血氧保存失败");
                            }
                        });

                        BloodOxygenData bloodOxygenData = null;
                        if (dataList_bloodoxygen_hour.size() != 0) {
                            if (dataList_bloodoxygen_hour.get(dataList_bloodoxygen_hour.size() - 1).getDetectTime().getTime() < dataList_bloodoxygen.get(dataList_bloodoxygen.size() - 1).getDetectTime().getTime()) {
                                bloodOxygenData = dataList_bloodoxygen.get(dataList_bloodoxygen.size() - 1);
                            } else {
                                bloodOxygenData = dataList_bloodoxygen_hour.get(dataList_bloodoxygen_hour.size() - 1);
                            }
                        } else {
                            bloodOxygenData = dataList_bloodoxygen.get(dataList_bloodoxygen.size() - 1);
                        }
                        tv_blood_oxygen_time.setText(TimeUtils.DateToStringSimpleDateFormat(bloodOxygenData.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
                        tv_blood_oxygen_value.setText("血氧" + bloodOxygenData.getBloodoxygen() + "%");
                        String str_blood_oxygen = ControlSave.read(mContext, ConstantUtils.BAND_RECENT_BLOOD_OXYGEN, "");
                        if (!TextUtils.isEmpty(str_blood_oxygen)) {
                            BloodOxygenData recent_blood_oxygen = HttpUtils.gb.create().fromJson(str_blood_oxygen, BloodOxygenData.class);
                            if (recent_blood_oxygen.getDetectTime().getTime() < bloodOxygenData.getDetectTime().getTime()) {
                                ControlSave.save(mContext, ConstantUtils.BAND_RECENT_BLOOD_OXYGEN, HttpUtils.gb.create().toJson(bloodOxygenData));
                            }
                        } else {
                            ControlSave.save(mContext, ConstantUtils.BAND_RECENT_BLOOD_OXYGEN, HttpUtils.gb.create().toJson(bloodOxygenData));
                        }
                        String str_blood_oxygen_new = ControlSave.read(mContext, ConstantUtils.BAND_RECENT_BLOOD_OXYGEN, "");
                        BloodOxygenData recent_blood_oxygen_new = HttpUtils.gb.create().fromJson(str_blood_oxygen_new, BloodOxygenData.class);
                        tv_blood_oxygen_time.setText(TimeUtils.DateToStringSimpleDateFormat(recent_blood_oxygen_new.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
                        tv_blood_oxygen_value.setText("血氧" + recent_blood_oxygen_new.getBloodoxygen() + "%");

                    }
                    if (dataList_bloodpressure.size() != 0) {
                        iv_blood_pressure_no_data.setImageResource(R.mipmap.blood_oxygen_progress);
                        HttpUtils.getInstantce().saveBandData(dataList_bloodpressure, HttpConstant.Url_saveBloodPressureData, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
                            @Override
                            public void onSuccess(Feedback<String> jsonData) {
                                Log.d(LOG_TAG, "血压保存成功");
                            }

                            @Override
                            public void onFailure(Feedback<String> jsonData) {
                                Log.d(LOG_TAG, "血压保存成功");
                            }
                        });

                        BloodPressure bloodPressure = null;
                        if (dataList_bloodpressure_hour.size() != 0) {
                            if (dataList_bloodpressure_hour.get(dataList_bloodpressure_hour.size() - 1).getDetectTime().getTime() < dataList_bloodpressure.get(dataList_bloodpressure.size() - 1).getDetectTime().getTime()) {
                                bloodPressure = dataList_bloodpressure.get(dataList_bloodpressure.size() - 1);
                            } else {
                                bloodPressure = dataList_bloodpressure_hour.get(dataList_bloodpressure_hour.size() - 1);
                            }
                        } else {
                            bloodPressure = dataList_bloodpressure.get(dataList_bloodpressure.size() - 1);
                        }
                        String str_blood_pressure = ControlSave.read(mContext, ConstantUtils.BAND_RECENT_BLOOD_PRESSURE, "");
                        if (!TextUtils.isEmpty(str_blood_pressure)) {
                            BloodPressure recent_blood_pressure = HttpUtils.gb.create().fromJson(str_blood_pressure, BloodPressure.class);
                            if (recent_blood_pressure.getDetectTime().getTime() < bloodPressure.getDetectTime().getTime()) {
                                ControlSave.save(mContext, ConstantUtils.BAND_RECENT_BLOOD_PRESSURE, HttpUtils.gb.create().toJson(bloodPressure));
                            }
                        } else {
                            ControlSave.save(mContext, ConstantUtils.BAND_RECENT_BLOOD_PRESSURE, HttpUtils.gb.create().toJson(bloodPressure));
                        }
                        String str_blood_pressure_new = ControlSave.read(mContext, ConstantUtils.BAND_RECENT_BLOOD_PRESSURE, "");
                        BloodPressure recent_blood_pressure_new = HttpUtils.gb.create().fromJson(str_blood_pressure_new, BloodPressure.class);
                        tv_blood_pressure_time.setText(TimeUtils.DateToStringSimpleDateFormat(recent_blood_pressure_new.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
                        tv_blood_pressure_value.setText(recent_blood_pressure_new.getHypertension() + "/" + recent_blood_pressure_new.getHypotension() + "mmhg");
                    }
                    break;
                case 10:
                    bcs.setOnTimeMeasure(1);
                    mHandler.sendEmptyMessageDelayed(11, 500);
                    break;
                case 11:
                    bcs.setTimeSync();
                    mHandler.sendEmptyMessageDelayed(12, 500);
                    break;
                case 12:
                    sendBandCommand();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myhealthdata);
        initActionBar("我的健康");
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        tv_connect_state = (TextView) findViewById(R.id.tv_connect_state);

        llHeartrate = findViewById(R.id.ll_heartrate);
        llOvulation = findViewById(R.id.ll_ovulation);
        llPregnant = findViewById(R.id.ll_pregnant);
        llUrine = (LinearLayout) findViewById(R.id.ll_urine);
        llWeight = (RelativeLayout) findViewById(R.id.ll_weight);

        tvMoreHeartrate = (TextView) findViewById(R.id.more_heartrate);
        tvMoreOvulation = (TextView) findViewById(R.id.more_ovulation);
        tvMorePregnant = (TextView) findViewById(R.id.more_pregnant);
        tvMoreUrine = (TextView) findViewById(R.id.more_urine);
        tvMoreWeight = (TextView) findViewById(R.id.more_weight);

        tvRecentHeartrate = (TextView) findViewById(R.id.recent_heartrate_data);
        tvRecentOvulation = (TextView) findViewById(R.id.recent_ovulation_data);
        tvRecentPregnant = (TextView) findViewById(R.id.recent_pregnant_data);
        tvRecentUrine = (TextView) findViewById(R.id.recent_urine_data);
        tvRecentWeight = (TextView) findViewById(R.id.recent_weight_data);

        tvWeight = (TextView) findViewById(R.id.weight_kg);
        tvBMI = (TextView) findViewById(R.id.weight_bmi);
        tvUserName = (TextView) findViewById(R.id.user_name);
        tvUserAge = (TextView) findViewById(R.id.user_age);
        tvUserPhone = (TextView) findViewById(R.id.user_phone);
        tvHeartRate = (TextView) findViewById(R.id.text_detection_state);
        mDetailListView = (ListView) findViewById(R.id.uri_list);

        ivUserHeader = (ImageView) findViewById(R.id.user_header);
        ivPregnantState = (ImageView) findViewById(R.id.pre_detection_state);
        ivOvulationState = (ImageView) findViewById(R.id.ovu_detection_state);
        tvWeightState = (TextView) findViewById(R.id.weight_result);
        ivDownUrineDetail = (ImageView) findViewById(R.id.down_detail);

        mScrollView = (ScrollView) findViewById(R.id.health_scrollView);
        mLoadingView = (LoadingView) findViewById(R.id.loading_view);
        tv_loading_fail = (TextView) findViewById(R.id.tv_loading_fail);
        tv_no_data = (TextView) findViewById(R.id.tv_no_data);

        gv_health_item = (GridViewForScrollview) findViewById(R.id.gv_chart_item);
        mHealthChart = (LineChart) findViewById(R.id.health_chart);

        tv_no_data_two = (TextView) findViewById(R.id.tv_no_data_two);

        ll_band_data = (LinearLayout) findViewById(R.id.ll_band_data);
        ll_band_step = (LinearLayout) findViewById(R.id.ll_band_step);
        ll_band_sleep = (LinearLayout) findViewById(R.id.ll_band_sleep);
        ll_band_heart = (LinearLayout) findViewById(R.id.ll_band_heart);
        ll_band_blood_pressure = (LinearLayout) findViewById(R.id.ll_band_blood_pressure);
        ll_band_blood_oxygen = (LinearLayout) findViewById(R.id.ll_band_blood_oxygen);
        tv_measurement = (TextView) findViewById(R.id.tv_measurement);

        tv_no_band_tip = (TextView) findViewById(R.id.tv_no_band_tip);
        tv_step_time = (TextView) findViewById(R.id.tv_step_time);
        tv_step_count = (TextView) findViewById(R.id.tv_step_count);
        tv_step_no_data = (TextView) findViewById(R.id.tv_step_no_data);
        pb_step = (ProgressBar) findViewById(R.id.pb_step);
        tv_sleep_time = (TextView) findViewById(R.id.tv_sleep_time);
        tv_sleep_length = (TextView) findViewById(R.id.tv_sleep_length);
        tv_sleep_no_data = (TextView) findViewById(R.id.tv_sleep_no_data);
        pb_sleep = (ProgressBar) findViewById(R.id.pb_sleep);
        tv_heart_time = (TextView) findViewById(R.id.tv_heart_time);
        tv_heart_rate = (TextView) findViewById(R.id.tv_heart_rate);
        iv_heart_no_data = (ImageView) findViewById(R.id.iv_heart_no_data);
        tv_blood_oxygen_time = (TextView) findViewById(R.id.tv_blood_oxygen_time);
        tv_blood_oxygen_value = (TextView) findViewById(R.id.tv_blood_oxygen_value);
        iv_blood_oxygen_no_data = (ImageView) findViewById(R.id.iv_blood_oxygen_no_data);
        tv_blood_pressure_time = (TextView) findViewById(R.id.tv_blood_pressure_time);
        tv_blood_pressure_value = (TextView) findViewById(R.id.tv_blood_pressure_value);
        iv_blood_pressure_no_data = (ImageView) findViewById(R.id.iv_blood_pressure_no_data);

        tv_heart_rate_no_data = (TextView) findViewById(R.id.tv_heart_rate_no_data);
        tv_pregnancy_no_data = (TextView) findViewById(R.id.tv_pregnancy_no_data);
        tv_ovulation_no_data = (TextView) findViewById(R.id.tv_ovulation_no_data);
        tv_urine_no_data = (TextView) findViewById(R.id.tv_urine_no_data);

        ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + ApplicationXpClient.userInfoResult.getAvatar(), ivUserHeader, ApplicationXpClient.options);
    }

    private void initData() {
        ApplicationXpClient.getInstance().setInMyBand(true);
        searchDeviceList = new ArrayList<>();

        mDetectionHeartrateList = new ArrayList<>();
        mDetectionOvulationList = new ArrayList<>();
        mDetectionPregnantList = new ArrayList<>();
        mDetectionUrineList = new ArrayList<>();
        mDetectionWeightList = new ArrayList<>();

        dataList_bloodoxygen = new ArrayList<>();
        dataList_bloodpressure = new ArrayList<>();
        dataList_sleep = new ArrayList<>();
        dataList_walk = new ArrayList<>();
        dataList_heart = new ArrayList<>();

        dataList_bloodoxygen_hour = new ArrayList<>();
        dataList_bloodpressure_hour = new ArrayList<>();
        dataList_sleep_hour = new ArrayList<>();
        dataList_walk_hour = new ArrayList<>();
        dataList_heart_hour = new ArrayList<>();

        chartDataList = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            ChartData chartData = new ChartData();
            chartDataList.add(chartData);
        }
        bcs = ApplicationXpClient.getInstance().getBluetoothService();

        tvUserName.setText(ApplicationXpClient.userInfoResult.getNickname());
        if (ApplicationXpClient.userInfoResult.getAge() != null) {
            tvUserAge.setText(ApplicationXpClient.userInfoResult.getAge() + "岁");
        } else {
            tvUserAge.setText("");
        }
        tvUserPhone.setText(ApplicationXpClient.userInfoResult.getPhone());
        mGridViewAdapter = new ChartGridViewAdapter(mContext, detection_names);
        gv_health_item.setAdapter(mGridViewAdapter);

        mController = BluetoothController.getInstance();
        initBluetooth();
        initChart();
        getLastDetectionData();
        initBandData();
    }

    private void initBluetooth() {
        entityDeviceList = new ArrayList<>();
        initRecentDevice();
        if (entityDeviceList.size() != 0) {
            tv_connect_state.setVisibility(View.VISIBLE);
            ll_band_data.setVisibility(View.VISIBLE);
            tv_no_band_tip.setVisibility(View.GONE);
            tv_measurement.setVisibility(View.VISIBLE);
            if (ApplicationXpClient.isBandConnect()) {
                EntityDevice connectedDevice = ApplicationXpClient.getInstance().getConnectBandDevice();
                if (TextUtils.isEmpty(connectedDevice.getNickName())) {
                    tv_connect_state.setText("已连接" + connectedDevice.getDeviceName());
                } else {
                    tv_connect_state.setText("已连接" + connectedDevice.getNickName());
                }
                registerBroadcast();
                mHandler.sendEmptyMessageDelayed(10, 500);
            } else {
                mController.initBLE();
                registerBroadcast();
                if (mController.isBleOpen()) {
                    mController.startScan();
                    tv_connect_state.setText("正在搜索我的手环");
                } else {
                    tv_connect_state.setText("请打开蓝牙");
                }
            }
        } else {
            ll_band_data.setVisibility(View.GONE);
            tv_connect_state.setVisibility(View.GONE);
            tv_no_band_tip.setVisibility(View.VISIBLE);
            tv_measurement.setVisibility(View.GONE);
        }
    }

    private void initRecentDevice() {
        entityDeviceList.clear();
        String jsonInfo = ControlSave.read(mContext, "rencent_device", "");
        if (!TextUtils.isEmpty(jsonInfo)) {
            List<EntityDevice> dataList = HttpUtils.gb.create().fromJson(jsonInfo, new TypeToken<List<EntityDevice>>() {
            }.getType());
            for (int i = 0; i < dataList.size(); i++) {
                EntityDevice entityDevice = dataList.get(i);
                if (entityDevice.getType() == 2) {
                    entityDeviceList.add(entityDevice);
                }
            }
        }
        if (entityDeviceList.size() == 1) {
            isOnlyOneDevice = true;
        } else {
            isOnlyOneDevice = false;
        }
    }

    private void registerBroadcast() {
        myReceiver = new MyReceiver();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ConstantUtils.ACTION_RECEIVE_MESSAGE_FROM_DEVICE);
        mIntentFilter.addAction(ConstantUtils.ACTION_BAND_STOP_CONNECT);
        mIntentFilter.addAction(ConstantUtils.ACTION_UPDATE_DEVICE_LIST);
        mIntentFilter.addAction(ConstantUtils.ACTION_BAND_CONNECTED_ONE_DEVICE);
        mIntentFilter.addAction(ConstantUtils.ACTION_STOP_DISCOVERY);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mIntentFilter.addAction(ConstantUtils.ACTION_RECEIVE_MESSAGE_FROM_BAND);
        mContext.registerReceiver(myReceiver, mIntentFilter);
    }


    private void sendBandCommand() {
        String str_recent_data_time = ControlSave.read(mContext, RECRNT_DATA_TIME, "");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tv_connect_state.setText("正在同步手环数据...");
        try {
            if (TextUtils.isEmpty(str_recent_data_time)) {
                Long time = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000;
                String d = format.format(time);
                Date date = format.parse(d);
                bcs.setSyncData(date);
                mHandler.sendEmptyMessageDelayed(1, 30000);
                ControlSave.save(mContext, RECRNT_DATA_TIME, TimeUtils.DateToStringDatail(new Date()));
            } else {
                Date myDate = format.parse(str_recent_data_time);
                Calendar c = Calendar.getInstance();
                c.setTime(myDate);
                c.add(java.util.Calendar.HOUR_OF_DAY, -1);
                myDate = c.getTime();
//                Log.d(LOG_TAG, "---" + TimeUtils.DateToStringDatail(myDate));
                bcs.setSyncData(myDate);
                mHandler.sendEmptyMessageDelayed(1, 10000);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initListener() {
        myClick = new MyClick();

        tv_connect_state.setOnClickListener(myClick);

        tvMoreHeartrate.setOnClickListener(myClick);
        tvMoreOvulation.setOnClickListener(myClick);
        tvMorePregnant.setOnClickListener(myClick);
        tvMoreUrine.setOnClickListener(myClick);
        tvMoreWeight.setOnClickListener(myClick);
        llHeartrate.setOnClickListener(myClick);
        tv_loading_fail.setOnClickListener(myClick);
        ivDownUrineDetail.setOnClickListener(myClick);

        ll_band_step.setOnClickListener(myClick);
        ll_band_sleep.setOnClickListener(myClick);
        ll_band_heart.setOnClickListener(myClick);
        ll_band_blood_pressure.setOnClickListener(myClick);
        ll_band_blood_oxygen.setOnClickListener(myClick);

        tv_measurement.setOnClickListener(myClick);
        tv_no_band_tip.setOnClickListener(myClick);

        tv_heart_rate_no_data.setOnClickListener(myClick);
        tv_pregnancy_no_data.setOnClickListener(myClick);
        tv_ovulation_no_data.setOnClickListener(myClick);
        tv_urine_no_data.setOnClickListener(myClick);

        gv_health_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mGridViewAdapter.setSelectPositon(position);
                mGridViewAdapter.notifyDataSetChanged();
                if (chartDataList.get(position).getDataList().size() == 0) {
                    tv_no_data_two.setVisibility(View.VISIBLE);
                    mHealthChart.setVisibility(View.GONE);
                    if (position <= 10) {
                        tv_no_data_two.setText(NONE_URINE_DATA);
                    } else if (position == 11) {
                        tv_no_data_two.setText(NONE_HEART_DATA);
                    } else if (position == 12) {
                        tv_no_data_two.setText(NONE_DM_DATA);
                    } else if (position == 13) {
                        tv_no_data_two.setText(NONE_SUBHEALTH_DATA);
                    } else if (position == 14) {
                        tv_no_data_two.setText(NONE_CORONARY_DATA);
                    } else if (position == 15) {
                        tv_no_data_two.setText(NONE_PREGNANCY_DATA);
                    } else if (position == 16) {
                        tv_no_data_two.setText(NONE_HYPERLIPOIDEMIA_DATA);
                    } else if (position == 17) {
                        tv_no_data_two.setText(NONE_HYPERTEMSION_DATA);
                    }
                } else {
                    tv_no_data_two.setVisibility(View.GONE);
                    mHealthChart.setVisibility(View.VISIBLE);
                    initLineData(chartDataList.get(position));
                }
            }
        });
        mDetailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent mIntent = new Intent(MyHealthDataActivity.this, UrineDetailActivity.class);
                mIntent.putExtra("urine_id", i + 1);
                startActivity(mIntent);
            }
        });

    }

    private void initBandData() {
        String str_current_walk = ControlSave.read(mContext, ConstantUtils.BAND_CURRENT_STEP, "");
        String str_current_sleep = ControlSave.read(mContext, ConstantUtils.BAND_CURRENT_SLEEP, "");
        String str_heart_rate = ControlSave.read(mContext, ConstantUtils.BAND_RECENT_HEART_RATE, "");
        String str_blood_pressure = ControlSave.read(mContext, ConstantUtils.BAND_RECENT_BLOOD_PRESSURE, "");
        String str_blood_oxygen = ControlSave.read(mContext, ConstantUtils.BAND_RECENT_BLOOD_OXYGEN, "");

        //初始化步数显示
        if (!TextUtils.isEmpty(str_current_walk)) {
            Walk walk = HttpUtils.gb.create().fromJson(str_current_walk, Walk.class);
            tv_step_no_data.setVisibility(View.GONE);
            pb_step.setVisibility(View.VISIBLE);
            tv_step_time.setText(TimeUtils.DateToStringSimpleDateFormat(walk.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
            tv_step_count.setText("步行:" + walk.getWalkCount() + "/步");
            String str_target = ControlSave.read(mContext, ConstantUtils.BAND_TARGET_STEP, "");
            int value;
            if (TextUtils.isEmpty(str_target)) {
                value = (walk.getWalkCount() * 100) / 8000;
            } else {
                value = (walk.getWalkCount() * 100) / Integer.valueOf(str_target);
            }
            pb_step.setProgress(value);
        } else {
            tv_step_no_data.setVisibility(View.VISIBLE);
            pb_step.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(str_current_sleep)) {
            Sleeping sleeping = HttpUtils.gb.create().fromJson(str_current_sleep, Sleeping.class);
            tv_sleep_no_data.setVisibility(View.GONE);
            pb_sleep.setVisibility(View.VISIBLE);
            tv_sleep_time.setText(TimeUtils.DateToStringSimpleDateFormat(sleeping.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
            String[] datas = sleeping.getSleepDuration().split("-");
            tv_sleep_length.setText("睡眠:" + datas[0] + "小时" + datas[1] + "分钟");
            int value = Integer.valueOf(datas[0]) * 60 + Integer.valueOf(datas[1]);
            pb_sleep.setProgress(value * 100 / 480);
        } else {
            tv_sleep_no_data.setVisibility(View.VISIBLE);
            pb_sleep.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(str_heart_rate)) {
            iv_heart_no_data.setImageResource(R.mipmap.heart_rate_progress);
            WristbandsHeartrate recent_heart_rate = HttpUtils.gb.create().fromJson(str_heart_rate, WristbandsHeartrate.class);
            tv_heart_time.setText(TimeUtils.DateToStringSimpleDateFormat(recent_heart_rate.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
            tv_heart_rate.setText("心率:" + (new Double(recent_heart_rate.getHeartRate())).intValue() + "次/分");
        } else {
            iv_heart_no_data.setImageResource(R.mipmap.no_heart_rate);
        }
        if (!TextUtils.isEmpty(str_blood_pressure)) {
            iv_blood_pressure_no_data.setImageResource(R.mipmap.blood_oxygen_progress);
            BloodPressure recent_blood_pressure = HttpUtils.gb.create().fromJson(str_blood_pressure, BloodPressure.class);
            tv_blood_pressure_time.setText(TimeUtils.DateToStringSimpleDateFormat(recent_blood_pressure.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
            tv_blood_pressure_value.setText(recent_blood_pressure.getHypertension() + "/" + recent_blood_pressure.getHypotension() + "mmhg");
        } else {
            iv_blood_pressure_no_data.setImageResource(R.mipmap.no_blood_pressure);
        }
        if (!TextUtils.isEmpty(str_blood_oxygen)) {
            iv_blood_oxygen_no_data.setImageResource(R.mipmap.blood_oxygen_progress);
            BloodOxygenData recent_blood_oxygen = HttpUtils.gb.create().fromJson(str_blood_oxygen, BloodOxygenData.class);
            tv_blood_oxygen_time.setText(TimeUtils.DateToStringSimpleDateFormat(recent_blood_oxygen.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
            tv_blood_oxygen_value.setText("血氧" + recent_blood_oxygen.getBloodoxygen() + "%");
        } else {
            iv_blood_oxygen_no_data.setImageResource(R.mipmap.no_blood_pressure);
        }
    }

    private void initChart() {
        mHealthChart.setDescription("");
        mHealthChart.setTouchEnabled(true);
        mHealthChart.zoom(1f, 0f, 0, 0);
        mHealthChart.setNoDataText(NONE_URINE_DATA);

        xAxis = mHealthChart.getXAxis();
        xAxis.setTextSize(6);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineWidth(1f);
        xAxis.setDrawGridLines(false);
        YAxis yAxisRight = mHealthChart.getAxisRight();
        yAxisRight.setEnabled(false);

        tv_no_data_two.setVisibility(View.VISIBLE);
        mHealthChart.setVisibility(View.GONE);

    }

    private void getLastDetectionData() {
        resetView(mLoadingView);
        HttpUtils.getInstantce().getLastDetectionData(new HttpConstant.SampleJsonResultListener<LastDetectionData>() {
            @Override
            public void onSuccess(LastDetectionData jsonData) {
                resetView(mScrollView);
                displayDetection(jsonData);
                HttpUtils.getInstantce().getLineChartsData(new HttpConstant.SampleJsonResultListener<DetectionLine>() {
                    @Override
                    public void onSuccess(DetectionLine jsonData) {
                        List<DetectionUrine> detectionUrineList = jsonData.getData();
                        List<DetectionHeartrate> detectionHeartrateList = jsonData.getData2();
                        HealthCommentResult healthCommentResult = jsonData.getData3();
                        List<String> describeList = new ArrayList<>();
                        for (int i = 0; i < detectionUrineList.size(); i++) {
                            describeList.add(i, TimeUtils.DateToStringDatail(detectionUrineList.get(i).getCreateTime()));
                            /** MPChart */
                            chartDataList.get(0).getDataList().add(new Entry(Float.valueOf(detectionUrineList.get(i).getLeuStandard()), i));
                            chartDataList.get(1).getDataList().add(new Entry(Float.valueOf(detectionUrineList.get(i).getNitStandard()), i));
                            chartDataList.get(2).getDataList().add(new Entry(Float.valueOf(detectionUrineList.get(i).getUbgStandard()), i));
                            chartDataList.get(3).getDataList().add(new Entry(Float.valueOf(detectionUrineList.get(i).getProStandard()), i));
                            chartDataList.get(4).getDataList().add(new Entry(Float.valueOf(detectionUrineList.get(i).getPhStandard()), i));
                            chartDataList.get(5).getDataList().add(new Entry(Float.valueOf(detectionUrineList.get(i).getBldStandard()), i));
                            chartDataList.get(6).getDataList().add(new Entry(Float.valueOf(detectionUrineList.get(i).getSgStandard()), i));
                            chartDataList.get(7).getDataList().add(new Entry(Float.valueOf(detectionUrineList.get(i).getKetStandard()), i));
                            chartDataList.get(8).getDataList().add(new Entry(Float.valueOf(detectionUrineList.get(i).getBilStandard()), i));
                            chartDataList.get(9).getDataList().add(new Entry(Float.valueOf(detectionUrineList.get(i).getGluStandard()), i));
                            chartDataList.get(10).getDataList().add(new Entry(Float.valueOf(detectionUrineList.get(i).getVcStandard()), i));
                        }
                        for (int i = 0; i < 11; i++) {
                            chartDataList.get(i).setDescribeList(describeList);
                        }

                        if (detectionHeartrateList.size() != 0) {
                            for (int j = 0; j < detectionHeartrateList.size(); j++) {
                                double value = detectionHeartrateList.get(j).getHeartRate();
                                chartDataList.get(11).getDataList().add(new Entry((float) value, j));
                                chartDataList.get(11).getDescribeList().add(j, TimeUtils.DateToStringDatail(detectionHeartrateList.get(j).getCreateTime()));
                            }
                        }

                        if (healthCommentResult.getHealthCommentDmList().size() != 0) {
                            for (int j = 0; j < healthCommentResult.getHealthCommentDmList().size(); j++) {
                                double value = Double.parseDouble(healthCommentResult.getHealthCommentDmList().get(j).getDm());
                                chartDataList.get(12).getDataList().add(new Entry((float) value, j));
                                chartDataList.get(12).getDescribeList().add(j, TimeUtils.DateToStringDatail(healthCommentResult.getHealthCommentDmList().get(j).getCreateTime()));
                            }
                        }

                        if (healthCommentResult.getHealthCommentSubhealthyList().size() != 0) {
                            for (int j = 0; j < healthCommentResult.getHealthCommentSubhealthyList().size(); j++) {
                                double value = Double.parseDouble(healthCommentResult.getHealthCommentSubhealthyList().get(j).getSubhealthy());
                                chartDataList.get(13).getDataList().add(new Entry((float) value, j));
                                chartDataList.get(13).getDescribeList().add(j, TimeUtils.DateToStringDatail(healthCommentResult.getHealthCommentSubhealthyList().get(j).getCreateTime()));
                            }
                        }

                        if (healthCommentResult.getHealthCommentCoronaryList().size() != 0) {
                            for (int j = 0; j < healthCommentResult.getHealthCommentCoronaryList().size(); j++) {
                                double value = Double.parseDouble(healthCommentResult.getHealthCommentCoronaryList().get(j).getCoronary());
                                chartDataList.get(14).getDataList().add(new Entry((float) value, j));
                                chartDataList.get(14).getDescribeList().add(j, TimeUtils.DateToStringDatail(healthCommentResult.getHealthCommentCoronaryList().get(j).getCreateTime()));
                            }
                        }
                        if (healthCommentResult.getHealthCommentPregnancyTestList().size() != 0) {
                            for (int j = 0; j < healthCommentResult.getHealthCommentPregnancyTestList().size(); j++) {
                                double value = Double.parseDouble(healthCommentResult.getHealthCommentPregnancyTestList().get(j).getPregnancyTest());
                                chartDataList.get(15).getDataList().add(new Entry((float) value, j));
                                chartDataList.get(15).getDescribeList().add(j, TimeUtils.DateToStringDatail(healthCommentResult.getHealthCommentPregnancyTestList().get(j).getCreateTime()));
                            }
                        }

                        if (healthCommentResult.getHealthCommentHyperlipoidemiaList().size() != 0) {
                            for (int j = 0; j < healthCommentResult.getHealthCommentHyperlipoidemiaList().size(); j++) {
                                double value = Double.parseDouble(healthCommentResult.getHealthCommentHyperlipoidemiaList().get(j).getHyperlipoidemia());
                                chartDataList.get(16).getDataList().add(new Entry((float) value, j));
                                chartDataList.get(16).getDescribeList().add(j, TimeUtils.DateToStringDatail(healthCommentResult.getHealthCommentHyperlipoidemiaList().get(j).getCreateTime()));
                            }
                        }

                        if (healthCommentResult.getHealthCommentHypertensionList().size() != 0) {
                            for (int j = 0; j < healthCommentResult.getHealthCommentHypertensionList().size(); j++) {
                                double value = Double.parseDouble(healthCommentResult.getHealthCommentHypertensionList().get(j).getHypertension());
                                chartDataList.get(17).getDataList().add(new Entry((float) value, j));
                                chartDataList.get(17).getDescribeList().add(j, TimeUtils.DateToStringDatail(healthCommentResult.getHealthCommentHypertensionList().get(j).getCreateTime()));
                            }
                        }

                        for (int i = 0; i < detection_names.length; i++) {
                            chartDataList.get(i).setTitle(detection_names[i]);
                        }
                        chartDataList.get(0).setColor("#607d8b");
                        chartDataList.get(1).setColor("#fe5621");
                        chartDataList.get(2).setColor("#009587");
                        chartDataList.get(3).setColor("#feea3a");
                        chartDataList.get(4).setColor("#785447");
                        chartDataList.get(5).setColor("#dec4e9");
                        chartDataList.get(6).setColor("#8bc34a");
                        chartDataList.get(7).setColor("#00bcd4");
                        chartDataList.get(8).setColor("#5346fe");
                        chartDataList.get(9).setColor("#ff2770");
                        chartDataList.get(10).setColor("#d32f2f");
                        chartDataList.get(11).setColor("#dec4e9");
                        chartDataList.get(12).setColor("#feea3a");
                        chartDataList.get(13).setColor("#785447");
                        chartDataList.get(14).setColor("#dec4e9");
                        chartDataList.get(15).setColor("#8bc34a");
                        chartDataList.get(16).setColor("#00bcd4");
                        chartDataList.get(17).setColor("#d32f2f");

                        if (detectionUrineList.size() != 0) {
                            initLineData(chartDataList.get(0));
                            tv_no_data_two.setVisibility(View.GONE);
                            mHealthChart.setVisibility(View.VISIBLE);
                        } else {
                            tv_no_data_two.setVisibility(View.VISIBLE);
                            mHealthChart.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(DetectionLine jsonData) {

                    }
                });

            }

            @Override
            public void onFailure(LastDetectionData jsonData) {
                resetView(tv_loading_fail);
            }
        });
    }

    /**
     * 判断数据表里面各种检测的item
     */
    private void displayDetection(LastDetectionData lastDetectionData) {
        mDetectionHeartrateList = lastDetectionData.getHeartrate();
        mDetectionOvulationList = lastDetectionData.getOvulation();
        mDetectionPregnantList = lastDetectionData.getPregnant();
        mDetectionUrineList = lastDetectionData.getUrine();
        mDetectionWeightList = lastDetectionData.getWeight();
        /** 心电 */
        if (mDetectionHeartrateList.size() != 0) {
            llHeartrate.setVisibility(View.VISIBLE);
            tv_heart_rate_no_data.setVisibility(View.GONE);
            tvRecentHeartrate.setText("最近数据 " + TimeUtils.DateToString(mDetectionHeartrateList.get(0).getCreateTime()));
            tvHeartRate.setText(mDetectionHeartrateList.get(0).getHeartRate() + "");
        } else {
            tv_heart_rate_no_data.setVisibility(View.VISIBLE);
            llHeartrate.setVisibility(View.GONE);
        }
        /** 孕检 */
        if (mDetectionPregnantList.size() != 0) {
            ivPregnantState.setVisibility(View.VISIBLE);
            tv_pregnancy_no_data.setVisibility(View.GONE);
            tvRecentPregnant.setText("最近数据 " + TimeUtils.DateToString(mDetectionPregnantList.get(0).getCreateTime()));
            isPregnancy(mDetectionPregnantList.get(0).getPre());
        } else {
            tv_pregnancy_no_data.setVisibility(View.VISIBLE);
            ivPregnantState.setVisibility(View.GONE);
        }
        /** 尿检 */
        if (mDetectionUrineList.size() != 0) {
            llUrine.setVisibility(View.VISIBLE);
            tv_urine_no_data.setVisibility(View.GONE);
            tvRecentUrine.setText("最近数据 " + TimeUtils.DateToString(mDetectionUrineList.get(0).getCreateTime()));
            mDetailListView.setVisibility(View.VISIBLE);
            initUrineData(mDetectionUrineList.get(0));
        } else {
            tv_urine_no_data.setVisibility(View.VISIBLE);
            llUrine.setVisibility(View.GONE);
        }
        /** 排卵 */
        if (mDetectionOvulationList.size() != 0) {
            ivOvulationState.setVisibility(View.VISIBLE);
            tv_ovulation_no_data.setVisibility(View.GONE);
            tvRecentOvulation.setText("最近数据 " + TimeUtils.DateToString(mDetectionOvulationList.get(0).getCreateTime()));
            isOvulation(mDetectionOvulationList.get(0).getOvu());
        } else {
            tv_ovulation_no_data.setVisibility(View.VISIBLE);
            ivOvulationState.setVisibility(View.GONE);
        }
        /** 体重 */
        if (mDetectionWeightList.size() != 0) {
            llWeight.setVisibility(View.VISIBLE);
            tvRecentWeight.setText("最近数据 " + TimeUtils.DateToString(mDetectionWeightList.get(0).getCreateTime()));
            tvWeightState.setText(mDetectionWeightList.get(0).getWeightResult());
            tvWeight.setText(mDetectionWeightList.get(0).getWeight() + " KG");
            tvBMI.setText("BMI:" + mDetectionWeightList.get(0).getBmi());
        }
//        if (mDetectionHeartrateList.size() + mDetectionOvulationList.size() + mDetectionPregnantList.size() + mDetectionUrineList.size() + mDetectionWeightList.size() == 0) {
//            resetView(mScrollView);
////            tv_no_data.setText(R.string.no_detection_data);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBandData();
    }

    private void initUrineData(DetectionUrine mDetectionUrine) {
        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[0]);
        detection_urine_item.setDetection_result(mDetectionUrine.getLeu());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getLeuResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[1]);
        detection_urine_item.setDetection_result(mDetectionUrine.getNit());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getNitResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[2]);
        detection_urine_item.setDetection_result(mDetectionUrine.getUbg());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getUbgResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[3]);
        detection_urine_item.setDetection_result(mDetectionUrine.getPro());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getProResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[4]);
        detection_urine_item.setDetection_result(mDetectionUrine.getPh());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getPhResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[5]);
        detection_urine_item.setDetection_result(mDetectionUrine.getBld());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getBldResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[6]);
        detection_urine_item.setDetection_result(mDetectionUrine.getSg());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getSgResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[7]);
        detection_urine_item.setDetection_result(mDetectionUrine.getKet());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getKetResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[8]);
        detection_urine_item.setDetection_result(mDetectionUrine.getBil());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getBilResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[9]);
        detection_urine_item.setDetection_result(mDetectionUrine.getGlu());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getGluResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[10]);
        detection_urine_item.setDetection_result(mDetectionUrine.getVc());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getVcResult());
        detection_urine_items.add(detection_urine_item);

        mAdapter = new UrineResultAdapter(mContext, detection_urine_items);
        mDetailListView.setAdapter(mAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationXpClient.getInstance().setInMyBand(false);
        try {
            if (myReceiver != null) {
                unregisterReceiver(myReceiver);
            }
        } catch (Exception e) {

        }
    }

    private class MyClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent mIntent = new Intent(MyHealthDataActivity.this, MoreDataActivity.class);
            switch (view.getId()) {
                case R.id.more_heartrate:
                    mIntent.putExtra("data_state", HEARTRATE_MORE);
                    startActivity(mIntent);
                    break;
                case R.id.more_ovulation:
                    mIntent.putExtra("data_state", OVULATION_MORE);
                    startActivity(mIntent);
                    break;
                case R.id.more_pregnant:
                    mIntent.putExtra("data_state", PREGNANT_MORE);
                    startActivity(mIntent);
                    break;
                case R.id.more_urine:
                    mIntent.putExtra("data_state", URINE_MORE);
                    startActivity(mIntent);
                    break;
                case R.id.more_weight:
                    mIntent.putExtra("data_state", WEIGHT_MORE);
                    startActivity(mIntent);
                    break;
                case R.id.ll_heartrate:
                    getHeartDataDetail();
                    break;
                case R.id.tv_loading_fail:
                    getLastDetectionData();
                    break;
                case R.id.down_detail:
                    if (mDetailListView.getVisibility() == View.VISIBLE) {
                        mDetailListView.setVisibility(View.GONE);
                        ivDownUrineDetail.setImageResource(R.mipmap.down_more);
                    } else {
                        mDetailListView.setVisibility(View.VISIBLE);
                        ivDownUrineDetail.setImageResource(R.mipmap.up_stop);
                    }
                    break;
                case R.id.ll_band_step:
                    startActivity(new Intent(mContext, StepActivity.class));
                    break;
                case R.id.tv_measurement:
                    startActivity(new Intent(mContext, MeasurementActivity.class));
                    break;
                case R.id.ll_band_sleep:
                    startActivity(new Intent(mContext, SleepActivity.class));
                    break;
                case R.id.ll_band_heart:
                    startActivity(new Intent(mContext, BandHeartActivity.class));
                    break;
                case R.id.ll_band_blood_pressure:
                    startActivity(new Intent(mContext, BloodPressureActivity.class));
                    break;
                case R.id.ll_band_blood_oxygen:
                    startActivity(new Intent(mContext, BloodOxygenActivity.class));
                    break;
                case R.id.tv_connect_state:
                    if (tv_connect_state.getText().toString().equals("未搜索到智能手环，点击重新搜索") || tv_connect_state.getText().toString().equals("手环已断开,正在尝试重新连接,点击重新搜索") || tv_connect_state.getText().toString().equals("点击重新搜索手环")) {
                        initRecentDevice();
                        mController.disBandConnect();
                        if (entityDeviceList.size() != 0) {
                            tv_connect_state.setVisibility(View.VISIBLE);
                            if (mController.isBleOpen()) {
                                mController.startScan();
                                searchDeviceList.clear();
                                tv_connect_state.setText("正在搜索我的手环");
                            } else {
                                tv_connect_state.setText("请打开蓝牙");
                            }
                        } else {
                            tv_connect_state.setVisibility(View.GONE);
                        }
                    } else if (ApplicationXpClient.isBandConnect()) {
                        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("提示")
                                .setContentText("\n是否断开当前手环连接")
                                .setCancelText("取消")
                                .setConfirmText("确定")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                        mController.disBandConnect();
                                        tv_connect_state.setText("点击重新搜索手环");
                                    }
                                })
                                .show();
                    }
                    break;
                case R.id.tv_no_band_tip:
                    startActivityForResult(new Intent(mContext, MyDeviceActivity.class), 1);
                    break;
                case R.id.tv_heart_rate_no_data:
                    mIntent.setClass(mContext, HeartActivity.class);
                    startActivity(mIntent);
                    break;
                case R.id.tv_pregnancy_no_data:
                    mIntent.setClass(mContext, PregnancyActivity.class);
                    startActivity(mIntent);
                    break;
                case R.id.tv_ovulation_no_data:
                    mIntent.setClass(mContext, OvulationActivity.class);
                    startActivity(mIntent);
                    break;
                case R.id.tv_urine_no_data:
                    mIntent.setClass(mContext, UrineActivity.class);
                    startActivity(mIntent);
                    break;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            initBluetooth();
            initBandData();
        }
    }

    private void getHeartDataDetail() {
        final DetectionHeartrate detectionHeartrate = mDetectionHeartrateList.get(0);

        Map<String, Object> maps = new HashMap<>();
        maps.put("id", detectionHeartrate.getId());
        HttpUtils.getInstantce().showSweetDialog();
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getHearDetectionData, new HttpCallBack<String>() {
            @Override
            public void onSuccess(HttpResult result) {
                HttpUtils.getInstantce().dismissSweetDialog();
                String result_detail = (String) result.getData();
                Intent mIntent = new Intent(mContext, HeartResultActivity.class);
                mIntent.putExtra("heartData", result_detail);
                mIntent.putExtra("result", detectionHeartrate.getChartUrl());
                mIntent.putExtra("detectionResult", String.valueOf(detectionHeartrate.getHeartRate()));
                startActivity(mIntent);
            }

            @Override
            public void onFail(String msg) {

            }
        });


/*
        HttpUtils.getInstantce().getHearDetectionData(Integer.valueOf(detectionHeartrate.getId() + ""), new HttpConstant.SampleJsonResultListener<Feedback>() {
            @Override
            public void onSuccess(Feedback jsonData) {
                Intent mIntent = new Intent(mContext, HeartResultActivity.class);
                mIntent.putExtra("heartData", jsonData.getData().toString());
                mIntent.putExtra("result", detectionHeartrate.getChartUrl());
                mIntent.putExtra("detectionResult", String.valueOf(detectionHeartrate.getHeartRate()));
                startActivity(mIntent);
            }

            @Override
            public void onFailure(Feedback jsonData) {
            }
        });
*/
    }


    /**
     * 判断是否 怀孕后， item的布局
     */
    private void isPregnancy(int state) {
        switch (state) {
            case PREGNANCY:
                ivPregnantState.setImageResource(R.mipmap.pregnantbg_yes);
                break;
            case UNPREGNANCY:
                ivPregnantState.setImageResource(R.mipmap.pregnantbg_no);
                break;
        }
    }

    /**
     * 判断是否 排卵后， item的布局
     */
    private void isOvulation(int state) {
        switch (state) {
            case OVULATION:
                ivOvulationState.setImageResource(R.mipmap.ovalationbg_yes);
                break;
            case UNOVULATION:
                ivOvulationState.setImageResource(R.mipmap.ovalationbg_no);
                break;
        }
    }

    private void resetView(View v) {
        mScrollView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        tv_loading_fail.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.health_scrollView:
                mScrollView.setVisibility(View.VISIBLE);
                break;
            case R.id.loading_view:
                mLoadingView.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_loading_fail:
                tv_loading_fail.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_no_data:
                tv_no_data.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initLineData(ChartData mChartData) {
        dataSet = new LineDataSet(mChartData.getDataList(), mChartData.getTitle());
        dataSet.setColor(Color.parseColor(mChartData.getColor()));
        dataSet.setCircleSize(5);
        dataSet.setCircleColorHole(Color.parseColor(mChartData.getColor()));
        dataSet.setLineWidth(2);
        dataSet.setDrawValues(false);
        dataSet.setValueTextColor(Color.parseColor(mChartData.getColor()));
        dataSet.setCircleColor(Color.parseColor(mChartData.getColor()));

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        //构建一个LineData  将dataSets放入
        LineData lineData = new LineData(mChartData.getDescribeList(), dataSets);

        switch (mChartData.getTitle()) {
            case "白细胞":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(4);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "阴性";
                            case 1:
                                return "微量";
                            case 2:
                                return "少量";
                            case 3:
                                return "中量";
                            case 4:
                                return "大量";
                            default:
                                return "";
                        }
                    }
                });
                break;
            case "亚硝酸盐":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(1);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "阴性";
                            case 1:
                                return "阳性";
                            default:
                                return "";
                        }
                    }
                });
                break;
            case "尿胆原":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(3);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "正常";
                            case 1:
                                return "+";
                            case 2:
                                return "++";
                            case 3:
                                return "+++";
                            default:
                                return "";
                        }
                    }
                });
                break;
            case "蛋白质":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(5);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "阴性";
                            case 1:
                                return "±";
                            case 2:
                                return "+";
                            case 3:
                                return "++";
                            case 4:
                                return "+++";
                            case 5:
                                return "++++";
                            default:
                                return "";
                        }
                    }
                });
                break;
            case "PH":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(6);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "5.0";
                            case 1:
                                return "6.0";
                            case 2:
                                return "6.5";
                            case 3:
                                return "7.0";
                            case 4:
                                return "7.5";
                            case 5:
                                return "8.0";
                            case 6:
                                return "8.5";
                            default:
                                return "";
                        }
                    }
                });
                break;
            case "潜血":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(5);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "阴性";
                            case 1:
                                return "微量";
                            case 2:
                                return "微量";
                            case 3:
                                return "少量";
                            case 4:
                                return "中量";
                            case 5:
                                return "大量";
                            default:
                                return "";
                        }
                    }
                });
                break;
            case "比重":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(6);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "1.000";
                            case 1:
                                return "1.005";
                            case 2:
                                return "1.010";
                            case 3:
                                return "1.015";
                            case 4:
                                return "1.020";
                            case 5:
                                return "1.025";
                            case 6:
                                return "1.030";
                            default:
                                return "";
                        }
                    }
                });
                break;
            case "酮体":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(5);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "阴性";
                            case 1:
                                return "微量";
                            case 2:
                                return "少量";
                            case 3:
                                return "中量";
                            case 4:
                                return "大量";
                            case 5:
                                return "大量";
                            default:
                                return "";
                        }
                    }
                });
                break;
            case "胆红素":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(3);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "阴性";
                            case 1:
                                return "少量";
                            case 2:
                                return "中量";
                            case 3:
                                return "大量";
                            default:
                                return "";
                        }
                    }
                });
                break;
            case "葡萄糖":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(5);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "阴性";
                            case 1:
                                return "微量";
                            case 2:
                                return "+";
                            case 3:
                                return "++";
                            case 4:
                                return "+++";
                            case 5:
                                return "++++";
                            default:
                                return "";
                        }
                    }
                });
                break;
            case "维生素":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(4);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "0";
                            case 1:
                                return "0.6";
                            case 2:
                                return "1.4";
                            case 3:
                                return "2.8";
                            case 4:
                                return "5.7";
                            default:
                                return "";
                        }
                    }
                });
                break;
            case "心率":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(200);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        return "" + v;
                    }
                });
                break;
            case "糖尿病":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(15);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "低风险";
                            case 15:
                                return "高风险";
                            default:
                                return "";
                        }
                    }
                });
                break;
            case "亚健康":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(14);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "低风险";
                            case 14:
                                return "高风险";
                            default:
                                return "";
                        }
                    }
                });
                break;
            case "冠心病":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(15);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "低风险";
                            case 15:
                                return "高风险";
                            default:
                                return "";
                        }
                    }
                });
                break;
            case "孕前自测":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(10);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "低风险";
                            case 10:
                                return "高风险";
                            default:
                                return "";
                        }
                    }
                });
                break;
            case "高血脂":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(14);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "低风险";
                            case 14:
                                return "高风险";
                            default:
                                return "";
                        }
                    }
                });
                break;
            case "高血压":
                mHealthChart.getAxisLeft().setAxisMinValue(0);
                mHealthChart.getAxisLeft().setAxisMaxValue(21);
                mHealthChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float v, YAxis yAxis) {
                        switch ((int) v) {
                            case 0:
                                return "低风险";
                            case 20:
                                return "高风险";
                            default:
                                return "";
                        }
                    }
                });
                break;
            default:
                break;
        }
        mHealthChart.moveViewToX(mChartData.getDataList().size() - 1);
        mHealthChart.setData(lineData);
    }


    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!ApplicationXpClient.getInstance().isInMydevices()) {
                if (action.equals(ConstantUtils.ACTION_RECEIVE_MESSAGE_FROM_BAND)) {
                    byte[] band_data = intent.getByteArrayExtra("band_data");
                    if ((band_data[4] & 0xff) == 0x51) {
                        if ((band_data[5] & 0xff) == 0x08) {
                            int step = (band_data[6] & 0xff) * 65536 + (band_data[7] & 0xff) * 256 + (band_data[8] & 0xff);

                            int calorie = (band_data[9] & 0xff) * 65536 + (band_data[10] & 0xff) * 256 + (band_data[11] & 0xff);

                            int hour1 = (band_data[12] & 0xff);

                            int minute1 = (band_data[13] & 0xff);

                            int hour2 = (band_data[14] & 0xff);

                            int minute2 = (band_data[15] & 0xff);

                            int count = (band_data[16] & 0xff);

                            current_walk = new Walk();
                            current_walk.setWalkCount(step);
                            current_walk.setCalorie("" + calorie);
                            current_walk.setDetectTime(new Date());
                            ControlSave.save(mContext, ConstantUtils.BAND_CURRENT_STEP, HttpUtils.gb.create().toJson(current_walk));
                            mHandler.sendEmptyMessage(2);

                            if (hour1 + hour2 + minute1 + minute2 != 0) {
                                current_sleep = new Sleeping();
                                if (minute1 + minute2 >= 60) {
                                    current_sleep.setSleepDuration((hour1 + hour2 + 1) + "-" + (minute1 + minute2 - 60));
                                } else {
                                    current_sleep.setSleepDuration((hour1 + hour2) + "-" + (minute1 + minute2));
                                }
                                current_sleep.setShallowSleepDuration(hour1 + "-" + hour2);
                                current_sleep.setDeepSleepDuration(hour2 + "-" + minute2);
                                current_sleep.setSoberCount(count);
                                current_sleep.setDetectTime(new Date());
                                ControlSave.save(mContext, ConstantUtils.BAND_CURRENT_SLEEP, HttpUtils.gb.create().toJson(current_sleep));
                                mHandler.sendEmptyMessage(3);
                            }
                        } else if ((band_data[5] & 0xff) == 0x11) {
                            int year = (band_data[6] & 0xff);
                            int month = (band_data[7] & 0xff);
                            int day = (band_data[8] & 0xff);
                            int hour = (band_data[9] & 0xff);
                            int minute = (band_data[10] & 0xff);
                            int heart_rate = (band_data[11] & 0xff);
                            WristbandsHeartrate heartrate = new WristbandsHeartrate();
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            try {
                                Date myDate = dateFormat.parse((year + 2000) + "-" + month + "-" + day + " " + hour + ":" + minute);
                                heartrate.setDetectTime(myDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            heartrate.setHeartRate((double) heart_rate);
                            heartrate.setCategory("1");
                            dataList_heart.add(heartrate);
//                            Log.d(LOG_TAG, "检测时间：" + (year + 2000) + "年" + month + "月" + day + "日" + hour + "时" + minute + "分" + "     心率为:" + heart_rate);


                        } else if ((band_data[5] & 0xff) == 0x12) {
                            int year = (band_data[6] & 0xff);
                            int month = (band_data[7] & 0xff);
                            int day = (band_data[8] & 0xff);
                            int hour = (band_data[9] & 0xff);
                            int minute = (band_data[10] & 0xff);
                            int blood_oxygen = (band_data[11] & 0xff);

                            BloodOxygenData bloodOxygenData = new BloodOxygenData();
                            bloodOxygenData.setBloodoxygen("" + blood_oxygen);
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            try {
                                Date myDate = dateFormat.parse((year + 2000) + "-" + month + "-" + day + " " + hour + ":" + minute);
                                bloodOxygenData.setDetectTime(myDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            bloodOxygenData.setCategory("1");
                            dataList_bloodoxygen.add(bloodOxygenData);
//                            Log.d(LOG_TAG, "检测时间：" + (year + 2000) + "年" + month + "月" + day + "日" + hour + "时" + minute + "分" + "     血氧为:" + blood_oxygen);

                        } else if ((band_data[5] & 0xff) == 0x14) {
                            int year = (band_data[6] & 0xff);
                            int month = (band_data[7] & 0xff);
                            int day = (band_data[8] & 0xff);
                            int hour = (band_data[9] & 0xff);
                            int minute = (band_data[10] & 0xff);
                            String blood_pressure = (band_data[11] & 0xff) + "/" + (band_data[12] & 0xff);

                            BloodPressure bloodPressure = new BloodPressure();
                            bloodPressure.setHypertension("" + (band_data[11] & 0xff));
                            bloodPressure.setHypotension("" + (band_data[12] & 0xff));
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            try {
                                Date myDate = dateFormat.parse((year + 2000) + "-" + month + "-" + day + " " + hour + ":" + minute);
                                bloodPressure.setDetectTime(myDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            bloodPressure.setCategory("1");
                            dataList_bloodpressure.add(bloodPressure);
//                            Log.d(LOG_TAG, "检测时间：" + (year + 2000) + "年" + month + "月" + day + "日" + hour + "时" + minute + "分" + "     血压为:" + blood_pressure);
                        } else if ((band_data[5] & 0xff) == 0x20) {
                            int year = (band_data[6] & 0xff);
                            int month = (band_data[7] & 0xff);
                            int day = (band_data[8] & 0xff);
                            int hour = (band_data[9] & 0xff);
                            int step = (band_data[10] & 0xff) * 65536 + (band_data[11] & 0xff) * 256 + (band_data[12] & 0xff);
                            int enery = (band_data[13] & 0xff) * 65536 + (band_data[14] & 0xff) * 256 + (band_data[15] & 0xff);
                            int heart = (band_data[16] & 0xff);
                            int blood_oxygen = (band_data[17] & 0xff);
                            int high_blood = (band_data[18] & 0xff);
                            int low_blood = (band_data[19] & 0xff);

                            int hour1 = (band_data[21] & 0xff);
                            int minute1 = (band_data[22] & 0xff);
                            int hour2 = (band_data[23] & 0xff);
                            int minute2 = (band_data[24] & 0xff);
                            int count = (band_data[25] & 0xff);
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Date myDate = null;
                            try {
                                myDate = dateFormat.parse((year + 2000) + "-" + month + "-" + day + " " + hour + ":" + "00");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            BandDataDetail bandDataDetail = new BandDataDetail();
                            bandDataDetail.setHour(hour);
                            bandDataDetail.setDay_week(TimeUtils.getWeekOfDate(myDate));
                            bandDataDetail.setDay_month(day);
                            bandDataDetail.setMonth(month);
                            bandDataDetail.setYear(year);
                            bandDataDetail.setmData(myDate);

                            if (step != 0 || enery != 0) {
                                bandDataDetail.setValue1(step);
                                bandDataDetail.setValue2(enery);
                                String str = ControlSave.read(mContext, ConstantUtils.BAND_STEP_DATA_DETAIL, "");
                                List<BandDataDetail> dataDetails;
                                if (TextUtils.isEmpty(str)) {
                                    dataDetails = new ArrayList<>();
                                    dataDetails.add(bandDataDetail);
                                } else {
                                    dataDetails = HttpUtils.gb.create().fromJson(str, new TypeToken<List<BandDataDetail>>() {
                                    }.getType());
                                    if (!dataDetails.contains(bandDataDetail)) {
                                        dataDetails.add(bandDataDetail);
                                    }
                                }
                                Walk walk = new Walk();
                                walk.setDetectTime(myDate);
                                walk.setWalkCount(step);
                                walk.setCalorie("" + enery);
                                walk.setMileage(String.format("%.1f", (float) step / 1400));

                                dataList_walk.add(walk);
                                dataList_walk_hour.add(walk);
                                ControlSave.save(mContext, ConstantUtils.BAND_STEP_DATA_DETAIL, HttpUtils.gb.create().toJson(dataDetails));
                            }

                            if (hour1 + hour2 + minute1 + minute2 != 0) {
                                bandDataDetail.setValue1(hour1 * 60 + minute1);
                                bandDataDetail.setValue2(hour2 * 60 + minute2);
                                bandDataDetail.setValue3(count);
                                String str = ControlSave.read(mContext, ConstantUtils.BAND_SLEEP_DATA_DETAIL, "");
                                List<BandDataDetail> dataDetails;
                                if (TextUtils.isEmpty(str)) {
                                    dataDetails = new ArrayList<>();
                                    dataDetails.add(bandDataDetail);
                                } else {
                                    dataDetails = HttpUtils.gb.create().fromJson(str, new TypeToken<List<BandDataDetail>>() {
                                    }.getType());
                                    if (!dataDetails.contains(bandDataDetail)) {
                                        dataDetails.add(bandDataDetail);
                                    }
                                }
                                Sleeping sleeping = new Sleeping();
                                if (minute1 + minute2 >= 60) {
                                    sleeping.setSleepDuration((hour1 + hour2 + 1) + "-" + (minute1 + minute2 - 60));
                                } else {
                                    sleeping.setSleepDuration((hour1 + hour2) + "-" + (minute1 + minute2));
                                }
                                sleeping.setShallowSleepDuration(hour1 + "-" + hour2);
                                sleeping.setDeepSleepDuration(hour2 + "-" + minute2);
                                sleeping.setSoberCount(count);
                                sleeping.setDetectTime(myDate);
                                dataList_sleep.add(sleeping);
                                dataList_sleep_hour.add(sleeping);
                                ControlSave.save(mContext, ConstantUtils.BAND_SLEEP_DATA_DETAIL, HttpUtils.gb.create().toJson(dataDetails));
                            }
                            if (heart != 0) {
                                bandDataDetail.setValue1(heart);
                                String str = ControlSave.read(mContext, ConstantUtils.BAND_HEART_RATE_DATA_DETAIL, "");
                                List<BandDataDetail> dataDetails;
                                if (TextUtils.isEmpty(str)) {
                                    dataDetails = new ArrayList<>();
                                    dataDetails.add(bandDataDetail);
                                } else {
                                    dataDetails = HttpUtils.gb.create().fromJson(str, new TypeToken<List<BandDataDetail>>() {
                                    }.getType());
                                    if (!dataDetails.contains(bandDataDetail)) {
                                        dataDetails.add(bandDataDetail);
                                    }
                                }
                                WristbandsHeartrate heartrate = new WristbandsHeartrate();
                                heartrate.setDetectTime(myDate);
                                heartrate.setHeartRate((double) heart);
                                heartrate.setCategory("3");
                                dataList_heart.add(heartrate);
                                dataList_heart_hour.add(heartrate);
                                ControlSave.save(mContext, ConstantUtils.BAND_HEART_RATE_DATA_DETAIL, HttpUtils.gb.create().toJson(dataDetails));
                            }
                            if (high_blood + low_blood != 0) {
                                bandDataDetail.setValue1(high_blood);
                                bandDataDetail.setValue2(low_blood);
                                String str = ControlSave.read(mContext, ConstantUtils.BAND_BLOOD_PRESSURE_DATA_DETAIL, "");
                                List<BandDataDetail> dataDetails;
                                if (TextUtils.isEmpty(str)) {
                                    dataDetails = new ArrayList<>();
                                    dataDetails.add(bandDataDetail);
                                } else {
                                    dataDetails = HttpUtils.gb.create().fromJson(str, new TypeToken<List<BandDataDetail>>() {
                                    }.getType());
                                    if (!dataDetails.contains(bandDataDetail)) {
                                        dataDetails.add(bandDataDetail);
                                    }
                                }

                                BloodPressure bloodPressure = new BloodPressure();
                                bloodPressure.setHypertension("" + high_blood);
                                bloodPressure.setHypotension("" + low_blood);
                                bloodPressure.setDetectTime(myDate);
                                bloodPressure.setCategory("3");
                                dataList_bloodpressure.add(bloodPressure);
                                dataList_bloodpressure_hour.add(bloodPressure);
                                ControlSave.save(mContext, ConstantUtils.BAND_BLOOD_PRESSURE_DATA_DETAIL, HttpUtils.gb.create().toJson(dataDetails));
                            }
                            if (blood_oxygen != 0) {
                                bandDataDetail.setValue1(blood_oxygen);
                                String str = ControlSave.read(mContext, ConstantUtils.BAND_BLOOD_OXYGEN_DATA_DETAIL, "");
                                List<BandDataDetail> dataDetails;
                                if (TextUtils.isEmpty(str)) {
                                    dataDetails = new ArrayList<>();
                                    dataDetails.add(bandDataDetail);
                                } else {
                                    dataDetails = HttpUtils.gb.create().fromJson(str, new TypeToken<List<BandDataDetail>>() {
                                    }.getType());
                                    if (!dataDetails.contains(bandDataDetail)) {
                                        dataDetails.add(bandDataDetail);
                                    }
                                }
                                BloodOxygenData bloodOxygenData = new BloodOxygenData();
                                bloodOxygenData.setBloodoxygen("" + blood_oxygen);
                                bloodOxygenData.setDetectTime(myDate);
                                bloodOxygenData.setCategory("3");
                                dataList_bloodoxygen.add(bloodOxygenData);
                                dataList_bloodoxygen_hour.add(bloodOxygenData);
                                ControlSave.save(mContext, ConstantUtils.BAND_BLOOD_OXYGEN_DATA_DETAIL, HttpUtils.gb.create().toJson(dataDetails));
                            }
//                            Log.d(LOG_TAG, "检测时间：" + (year + 2000) + "年" + month + "月" + day + "日" + hour + "时" + "     步数为:" + step + "        卡路里：" + enery + "       心率:" + heart + "     血压为:" + high_blood + "/" + low_blood
//                                    + "      血氧:" + blood_oxygen + "     浅度睡眠时间：" + hour1 + "时" + minute1 + "分" + "        深度睡眠时间:" + hour2 + "时" + minute2 + "分" + "        醒来次数:" + count);
                        }
                    }
                } else if (action.equals(ConstantUtils.ACTION_UPDATE_DEVICE_LIST)) {
                    String name = intent.getStringExtra("name");
                    String address = intent.getStringExtra("address");
                    int rssi = intent.getIntExtra("rssi", 0);
                    EntityDevice entityDevice = new EntityDevice(name, address);
                    entityDevice.setRssi(rssi);
                    for (int i = 0; i < entityDeviceList.size(); i++) {
                        if (entityDevice.getDeviceAddress().equals(entityDeviceList.get(i).getDeviceAddress())) {
                            boolean isRepeat = false;
                            for (int j = 0; j < searchDeviceList.size(); j++) {
                                if (address.equals(searchDeviceList.get(j).getDeviceAddress())) {
                                    isRepeat = true;
                                }
                            }
                            if (!isRepeat) {
                                searchDeviceList.add(entityDeviceList.get(i));
                                if (isOnlyOneDevice)
                                    mController.sendStopScanMessage();
                            }
                        }
                    }
                } else if (action.equals(ConstantUtils.ACTION_RECEIVE_MESSAGE_FROM_DEVICE)) {


                } else if (action.equals(ConstantUtils.ACTION_BAND_STOP_CONNECT)) {
                    tv_connect_state.setText("手环已断开,正在尝试重新连接,点击重新搜索");
                } else if (action.equals(ConstantUtils.ACTION_BAND_CONNECTED_ONE_DEVICE)) {
                    EntityDevice connectedDevice = ApplicationXpClient.getInstance().getConnectBandDevice();
                    if (TextUtils.isEmpty(connectedDevice.getNickName())) {
                        tv_connect_state.setText("已连接" + connectedDevice.getDeviceName());
                    } else {
                        tv_connect_state.setText("已连接" + connectedDevice.getNickName());
                    }
                    mHandler.sendEmptyMessageDelayed(10, 2000);
                } else if (action.equals(ConstantUtils.ACTION_STOP_DISCOVERY)) {
                    if (searchDeviceList.size() != 0) {
                        if (searchDeviceList.size() == 1) {
                            mController.connectBindDevice(searchDeviceList.get(0));
                        } else {
                            select_position = 0;
                            //对扫描到的设备rssi进行从大到小的排序（即信号强度进行排序）
                            Collections.sort(searchDeviceList);
                            String[] items = new String[searchDeviceList.size()];
                            for (int i = 0; i < searchDeviceList.size(); i++) {
                                if (TextUtils.isEmpty(searchDeviceList.get(i).getNickName())) {
                                    items[i] = searchDeviceList.get(i).getDeviceName();
                                } else {
                                    items[i] = searchDeviceList.get(i).getNickName();
                                }
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    mContext);
                            builder.setTitle("请选择连接的设备");
                            builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    select_position = which;
                                }
                            });
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mController.connectBindDevice(searchDeviceList.get(select_position));
                                }
                            });
                            builder.show();
                        }
                        tv_connect_state.setText("正在连接我的手环");
                    } else {
                        tv_connect_state.setText("未搜索到智能手环，点击重新搜索");
                    }
                } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                    if (state == BluetoothAdapter.STATE_ON) {
                        mController.startScan();
                        tv_connect_state.setText("正在搜索我的手环");
                    } else if (state == BluetoothAdapter.STATE_OFF) {
                        tv_connect_state.setText("请打开蓝牙");
                    }
                }
            }
        }
    }


    public static Bitmap getScrollViewBitmap(ScrollView scrollView, String picpath) {
        int h = 0;
        Bitmap bitmap;
        // 获取listView实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        Log.d(LOG_TAG, "实际高度:" + h);
        Log.d(LOG_TAG, " 高度:" + scrollView.getHeight());
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        // 测试输出
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(picpath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 截图listview
     **/
    public static Bitmap getListViewBitmap(ListView listView, String picpath) {
        int h = 0;
        Bitmap bitmap;
        // 获取listView实际高度
        for (int i = 0; i < listView.getChildCount(); i++) {
            h += listView.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(listView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        listView.draw(canvas);
        // 测试输出
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(picpath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
        }
        return bitmap;
    }


}
