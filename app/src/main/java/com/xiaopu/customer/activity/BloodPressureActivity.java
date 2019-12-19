package com.xiaopu.customer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.BandDataAdapter;
import com.xiaopu.customer.data.BandData;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.BloodPressure;
import com.xiaopu.customer.service.BluetoothService;
import com.xiaopu.customer.utils.ControlSave;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.TimeUtils;
import com.xiaopu.customer.utils.bluetooth.ConstantUtils;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.BloodPressureProgressView;
import com.xiaopu.customer.view.LoadingView.LoadingView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler2;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2017/9/13.
 */

public class BloodPressureActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = BloodPressureActivity.class.getSimpleName();

    private Context mContext;

    private ImageView iv_return;

    private BloodPressureProgressView bpv_blood_pressure;

    private Button bt_detection_once;

    private Button bt_detection_time;

    private GifImageView gifImageView;

    private TextView tv_detectioning;

    private PtrClassicFrameLayout mLayout;

    private ListView lv_blood_pressure;

    private LoadingView mLoadingView;

    private TextView tv_no_data;

    private TextView tv_loading_fail;

    private TextView tv_data_day;

    private TextView tv_data_week;

    private TextView tv_data_month;

    private boolean isTimeDetection;

    private boolean isOnceDetection;

    private BluetoothService bcs;

    private IntentFilter mInfilter;

    private Intent mIntent;

    private Intent intent_data;

    private BandDataAdapter mBandDataAdapter;

    private List<BandData> dataList;

    private int pageNo;

    private int pageSize;

    private boolean isNoMore;

    private boolean isCanLoadMore;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    bcs.realTimeAndOnceMeasure(0X21, 0);
                    break;
                case 2:
                    bpv_blood_pressure.setVisibility(View.VISIBLE);
                    gifImageView.setVisibility(View.GONE);
                    bt_detection_time.setVisibility(View.VISIBLE);
                    bt_detection_once.setVisibility(View.VISIBLE);
                    tv_detectioning.setVisibility(View.GONE);
                    isOnceDetection = false;
                    bpv_blood_pressure.setDescribe(TimeUtils.DateToStringSimpleDateFormat(new Date(), TimeUtils.DATA_FORMAT_NO_YEAR));

                    BandData bandData = new BandData();
                    bandData.setType(1);
                    bandData.setDetection_time(TimeUtils.DateToStringSimpleDateFormat(new Date(), TimeUtils.DATA_FORMAT_NO_YEAR));
                    bandData.setDetection_result(msg.arg1 + "/" + msg.arg2);
                    dataList.add(0, bandData);
                    mBandDataAdapter.notifyDataSetChanged();

                    BloodPressure bloodPressure = new BloodPressure();
                    bloodPressure.setCategory("1");
                    bloodPressure.setHypertension(msg.arg1 + "");
                    bloodPressure.setHypotension(msg.arg2 + "");
                    bloodPressure.setDetectTime(new Date());
                    saveDetectionData(bloodPressure);
                    String str_bloodPressure = HttpUtils.gb.create().toJson(bloodPressure);
                    ControlSave.save(mContext, ConstantUtils.BAND_RECENT_BLOOD_PRESSURE, str_bloodPressure);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_blood_pressure);
        initActionBar("血压");
        mContext = this;
        initView();
        initData();
        initListener();

    }

    private void initView() {
        iv_return = (ImageView) findViewById(R.id.iv_return);
        bpv_blood_pressure = (BloodPressureProgressView) findViewById(R.id.bpv_blood_pressure);
        gifImageView = (GifImageView) findViewById(R.id.gif_heart);
        bt_detection_once = (Button) findViewById(R.id.bt_blood_pressure_detection_once);
        bt_detection_time = (Button) findViewById(R.id.bt_blood_pressure_detection_time);
        tv_detectioning = (TextView) findViewById(R.id.tv_detectioning);
        mLayout = (PtrClassicFrameLayout) findViewById(R.id.ptr_mLayout);
        lv_blood_pressure = (ListView) findViewById(R.id.lv_blood_pressure);
        mLoadingView = (LoadingView) findViewById(R.id.loading_view);
        tv_no_data = (TextView) findViewById(R.id.tv_no_data);
        tv_loading_fail = (TextView) findViewById(R.id.tv_loading_fail);
        tv_data_day = (TextView) findViewById(R.id.tv_data_day);
        tv_data_week = (TextView) findViewById(R.id.tv_data_week);
        tv_data_month = (TextView) findViewById(R.id.tv_data_month);
    }

    private void initData() {
        isTimeDetection = false;
        isOnceDetection = false;
        pageNo = 1;
        pageSize = 10;
        isCanLoadMore = true;
        isNoMore = false;
        dataList = new ArrayList<>();
        bcs = ApplicationXpClient.getInstance().getBluetoothService();
        mIntent = new Intent(mContext, MeasureInstructionsActivity.class);
        intent_data = new Intent(mContext, BloodPressureDataActivity.class);
        initBroadcast();
        initAdapter();
        initDescribeText();
        initPtrClassicFrameLayout();
        getDetectionData(pageNo, pageSize, true);
    }

    private void initDescribeText() {
        String str_blood_pressure = ControlSave.read(mContext, ConstantUtils.BAND_RECENT_BLOOD_PRESSURE, "");
        if (!TextUtils.isEmpty(str_blood_pressure)) {
            BloodPressure recent_blood_pressure = HttpUtils.gb.create().fromJson(str_blood_pressure, BloodPressure.class);
            bpv_blood_pressure.setProgress(Integer.valueOf(recent_blood_pressure.getHypertension()), Integer.valueOf(recent_blood_pressure.getHypotension()));
            bpv_blood_pressure.setDescribe(TimeUtils.DateToStringSimpleDateFormat(recent_blood_pressure.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
        }
    }

    private void initAdapter() {
        mBandDataAdapter = new BandDataAdapter(mContext, dataList);
        lv_blood_pressure.setAdapter(mBandDataAdapter);
    }

    private void initBroadcast() {
        mInfilter = new IntentFilter();
        mInfilter.addAction(ConstantUtils.ACTION_RECEIVE_MESSAGE_FROM_BAND);
        registerReceiver(myReceiver, mInfilter);
    }


    private void initPtrClassicFrameLayout() {
        mLayout.setMode(PtrFrameLayout.Mode.BOTH);
        mLayout.setLastUpdateTimeRelateObject(this);
        mLayout.setResistanceHeader(1.7f); // 您还可以单独设置脚,头\
        mLayout.setRatioOfHeaderHeightToRefresh(1.2f);
        mLayout.setDurationToClose(1000);  // 您还可以单独设置脚,头
        mLayout.setPullToRefresh(false);
        // default is true
        mLayout.setKeepHeaderWhenRefresh(true);
        //以下为自定义header需要
        StoreHouseHeader header = new StoreHouseHeader(this);
        header.setPadding(0, PtrLocalDisplay.designedDP2px(10), 0, PtrLocalDisplay.designedDP2px(10));
        header.setTextColor(this.getResources().getColor(R.color.new_blue));
        header.initWithString("LOADING...");
        mLayout.setDurationToCloseHeader(1500);
        mLayout.setHeaderView(header);
        mLayout.addPtrUIHandler(header);

        /*StoreHouseHeader footer = new StoreHouseHeader(this);
        footer.setPadding(0, PtrLocalDisplay.dp2px(5), 0, PtrLocalDisplay.dp2px(5));
        footer.setTextColor(this.getResources().getColor(R.color.newblue));
        footer.initWithString("LOADING...");

        mLayout.setFooterView(footer);
        mLayout.addPtrUIHandler(footer);*/
        mLayout.disableWhenHorizontalMove(true);

        mLayout.setPtrHandler(new PtrHandler2() {
            @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {
                if (!isCanLoadMore)
                    return false;
                return PtrDefaultHandler2.checkContentCanBePulledUp(frame, lv_blood_pressure, footer);
            }

            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                if (isNoMore) {
                    T.showShort("暂无更多");
                    mLayout.refreshComplete();
                } else {
                    pageNo++;
                    getDetectionData(pageNo, pageSize, false);
                }
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler2.checkContentCanBePulledDown(frame, lv_blood_pressure, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                pageNo = 1;
                getDetectionData(pageNo, pageSize, false);
            }
        });

    }

    private void initListener() {
        iv_return.setOnClickListener(this);
        bt_detection_once.setOnClickListener(this);
        bt_detection_time.setOnClickListener(this);
        tv_data_day.setOnClickListener(this);
        tv_data_week.setOnClickListener(this);
        tv_data_month.setOnClickListener(this);
        tv_loading_fail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isOnceDetection) {
            T.showShort("正在进行血压单次测量，请稍后");
        } else if (isTimeDetection) {
            if (v.getId() == R.id.bt_blood_pressure_detection_time) {
                isTimeDetection = false;
                bt_detection_time.setText(getResources().getString(R.string.measure_time));
                bcs.realTimeAndOnceMeasure(0X22, 0);
                bt_detection_once.setPressed(false);
                bt_detection_once.setClickable(true);
            } else {
                T.showShort("正在进行血压实时测量，请稍后");
            }
        } else {
            switch (v.getId()) {
                case R.id.bt_blood_pressure_detection_once:
                    if (ApplicationXpClient.isBandConnect()) {
                        if (ControlSave.read(mContext, "measure_tip", "").isEmpty()) {
                            mIntent.putExtra("type", 3);
                            startActivityForResult(mIntent, 0);
                        } else {
                            isOnceDetection = true;
                            bpv_blood_pressure.setVisibility(View.GONE);
                            gifImageView.setVisibility(View.VISIBLE);
                            bt_detection_time.setVisibility(View.GONE);
                            bt_detection_once.setVisibility(View.GONE);
                            tv_detectioning.setVisibility(View.VISIBLE);
                            bcs.realTimeAndOnceMeasure(0X21, 1);
                            mHandler.sendEmptyMessageDelayed(1, 62000);
                        }
                    } else {
                        T.showShort("手环未连接");
                    }
                    break;
                case R.id.bt_blood_pressure_detection_time:
                    if (ApplicationXpClient.isBandConnect()) {
                        if (ControlSave.read(mContext, "measure_tip", "").isEmpty()) {
                            mIntent.putExtra("type", 4);
                            startActivityForResult(mIntent, 0);
                        } else {
                            isTimeDetection = true;
                            bt_detection_time.setText(getResources().getString(R.string.stop_measure));
                            bpv_blood_pressure.setProgress(0, 0);
                            bt_detection_once.setPressed(true);
                            bt_detection_once.setClickable(false);
                            bcs.realTimeAndOnceMeasure(0X22, 1);
                        }
                    } else {
                        T.showShort("手环未连接");
                    }
                    break;
                case R.id.tv_data_day:
                    intent_data.putExtra("type", 1);
                    startActivity(intent_data);
                    break;
                case R.id.tv_data_week:
                    intent_data.putExtra("type", 2);
                    startActivity(intent_data);
                    break;
                case R.id.tv_data_month:
                    intent_data.putExtra("type", 3);
                    startActivity(intent_data);
                    break;
                case R.id.iv_return:
                    finish();
                    break;
                case R.id.tv_loading_fail:
                    getDetectionData(pageNo, pageSize, true);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConstantUtils.ACTION_RECEIVE_MESSAGE_FROM_BAND)) {
                byte[] band_data = intent.getByteArrayExtra("band_data");
                if ((band_data[4] & 0xff) == 0x31 && (band_data[5] & 0xff) == 0x22) {
                    int high_value = (band_data[6] & 0xff);
                    int low_value = (band_data[7] & 0xff);
                    bpv_blood_pressure.setProgress(high_value, low_value);
                    bpv_blood_pressure.setDescribe(TimeUtils.DateToStringSimpleDateFormat(new Date(), TimeUtils.DATA_FORMAT_NO_YEAR));
                } else if ((band_data[4] & 0xff) == 0x31 && (band_data[5] & 0xff) == 0x21) {
                    bpv_blood_pressure.setProgress((band_data[6] & 0xff), (band_data[7] & 0xff));
                    Message message = new Message();
                    message.what = 2;
                    message.arg1 = (band_data[6] & 0xff);
                    message.arg2 = (band_data[7] & 0xff);
                    mHandler.sendMessage(message);
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data != null) {
                int type = data.getIntExtra("data", 0);
                if (type == 3) {
                    isOnceDetection = true;
                    bpv_blood_pressure.setVisibility(View.GONE);
                    gifImageView.setVisibility(View.VISIBLE);
                    bt_detection_time.setVisibility(View.GONE);
                    bt_detection_once.setVisibility(View.GONE);
                    tv_detectioning.setVisibility(View.VISIBLE);
                    bcs.realTimeAndOnceMeasure(0X21, 1);
                    mHandler.sendEmptyMessageDelayed(1, 62000);
                } else if (type == 4) {
                    isTimeDetection = true;
                    bt_detection_time.setText(getResources().getString(R.string.stop_measure));
                    bcs.realTimeAndOnceMeasure(0X22, 1);
                    bpv_blood_pressure.setProgress(0, 0);
                    bt_detection_once.setPressed(true);
                    bt_detection_once.setClickable(false);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isOnceDetection) {
                T.showShort("正在进行血压单次测量，请稍后");
                return false;
            } else if (isTimeDetection) {
                T.showShort("正在进行血压实时测量，请稍后");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void getDetectionData(final int pageNo, final int pageSize, final boolean isFirst) {
        if (isFirst) {
            resetView(mLoadingView);
        }

        Map<String, Object> maps = new HashMap<>();
        maps.put("pageNo", pageNo);
        maps.put("pageSize", pageSize);

        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getBloodPressureData, new HttpCallBack<List<BloodPressure>>() {
            @Override
            public void onSuccess(HttpResult result) {
                List<BloodPressure> bloodPressureList = (List<BloodPressure>) result.getData();
                isCanLoadMore = true;
                if (!isFirst)
                    mLayout.refreshComplete();
                if (bloodPressureList.size() == 0) {
                    isNoMore = true;
                    if (pageNo == 1) {
                        resetView(tv_no_data);
                        tv_no_data.setText("暂无数据");
                        isCanLoadMore = false;
                    } else {
                        T.showShort("暂无更多");
                    }
                } else {
                    resetView(lv_blood_pressure);
                    if (pageNo == 1)
                        dataList.clear();
                    if (bloodPressureList.size() < 10)
                        isNoMore = true;
                    for (int i = 0; i < bloodPressureList.size(); i++) {
                        BloodPressure bloodPressure = bloodPressureList.get(i);
                        BandData bandData = new BandData();
                        bandData.setType(1);
                        bandData.setDetection_result(bloodPressure.getHypertension() + "/" + bloodPressure.getHypotension());
                        bandData.setDetection_time(TimeUtils.DateToStringSimpleDateFormat(bloodPressure.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
                        dataList.add(bandData);
                    }
                    mBandDataAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(String msg) {
                if (isFirst) {
                    resetView(tv_loading_fail);
                    isCanLoadMore = false;
                } else {
                    T.showShort("网络故障，请稍后重试");
                }
            }
        });

//        HttpUtils.getInstantce().getBloodPressureData(pageNo, pageSize, new HttpConstant.SampleJsonResultListener<Feedback<List<BloodPressure>>>() {
//            @Override
//            public void onSuccess(Feedback<List<BloodPressure>> jsonData) {
//                isCanLoadMore = true;
//                if (!isFirst)
//                    mLayout.refreshComplete();
//                if (jsonData.getData().size() == 0) {
//                    isNoMore = true;
//                    if (pageNo == 1) {
//                        resetView(tv_no_data);
//                        tv_no_data.setText("暂无数据");
//                        isCanLoadMore = false;
//                    } else {
//                        T.showShort("暂无更多");
//                    }
//                } else {
//                    resetView(lv_blood_pressure);
//                    if (pageNo == 1)
//                        dataList.clear();
//                    if (jsonData.getData().size() < 10)
//                        isNoMore = true;
//                    for (int i = 0; i < jsonData.getData().size(); i++) {
//                        BloodPressure bloodPressure = jsonData.getData().get(i);
//                        BandData bandData = new BandData();
//                        bandData.setType(1);
//                        bandData.setDetection_result(bloodPressure.getHypertension() + "/" + bloodPressure.getHypotension());
//                        bandData.setDetection_time(TimeUtils.DateToStringSimpleDateFormat(bloodPressure.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
//                        dataList.add(bandData);
//                    }
//                    mBandDataAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onFailure(Feedback<List<BloodPressure>> jsonData) {
//                if (isFirst) {
//                    resetView(tv_loading_fail);
//                    isCanLoadMore = false;
//                } else {
//                    T.showShort("网络故障，请稍后重试");
//                }
//
//            }
//        });

    }

    private void saveDetectionData(BloodPressure bloodpressure) {
        List<BloodPressure> bloodPressureList = new ArrayList<>();
        bloodPressureList.add(bloodpressure);
        HttpUtils.getInstantce().saveBandData(bloodPressureList, HttpConstant.Url_saveBloodPressureData, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
            @Override
            public void onSuccess(Feedback<String> jsonData) {

            }

            @Override
            public void onFailure(Feedback<String> jsonData) {

            }
        });
    }

    private void resetView(View v) {
        lv_blood_pressure.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        tv_loading_fail.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.lv_blood_pressure:
                lv_blood_pressure.setVisibility(View.VISIBLE);
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

}
