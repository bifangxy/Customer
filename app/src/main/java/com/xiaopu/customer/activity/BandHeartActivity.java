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
import com.xiaopu.customer.data.jsonresult.WristbandsHeartrate;
import com.xiaopu.customer.service.BluetoothService;
import com.xiaopu.customer.utils.ControlSave;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.TimeUtils;
import com.xiaopu.customer.utils.bluetooth.ConstantUtils;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.HeartProgressView;
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
 * Created by Administrator on 2017/9/11.
 */

public class BandHeartActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = BandHeartActivity.class.getSimpleName();

    private Context mContext;

    private ImageView iv_return;

    private HeartProgressView hpv_heart;

    private Button bt_detection_once;

    private Button bt_detection_time;

    private GifImageView gifImageView;

    private TextView tv_detectioning;

    private PtrClassicFrameLayout mLayout;

    private ListView lv_heart_rate;

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

    private int pageSize;

    private int pageNo;

    private boolean isNoMore;

    private boolean isCanLoadMore;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    bcs.realTimeAndOnceMeasure(0X09, 0);
                    break;
                case 2:
                    hpv_heart.setVisibility(View.VISIBLE);
                    gifImageView.setVisibility(View.GONE);
                    bt_detection_time.setVisibility(View.VISIBLE);
                    bt_detection_once.setVisibility(View.VISIBLE);
                    tv_detectioning.setVisibility(View.GONE);
                    isOnceDetection = false;
                    hpv_heart.setDescribe(TimeUtils.DateToStringSimpleDateFormat(new Date(), TimeUtils.DATA_FORMAT_NO_YEAR));
                    BandData bandData = new BandData();
                    bandData.setType(0);
                    bandData.setDetection_time(TimeUtils.DateToStringSimpleDateFormat(new Date(), TimeUtils.DATA_FORMAT_NO_YEAR));
                    bandData.setDetection_result(msg.arg1 + "次/分");
                    dataList.add(0, bandData);
                    mBandDataAdapter.notifyDataSetChanged();

                    WristbandsHeartrate wristbandsHeartrate = new WristbandsHeartrate();
                    wristbandsHeartrate.setCategory("0");
                    wristbandsHeartrate.setHeartRate(Double.valueOf(msg.arg1));
                    wristbandsHeartrate.setDetectTime(new Date());
                    saveDetectionData(wristbandsHeartrate);

                    String str_heartRate = HttpUtils.gb.create().toJson(wristbandsHeartrate);
                    ControlSave.save(mContext, ConstantUtils.BAND_RECENT_HEART_RATE, str_heartRate);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_heart);
        initActionBar("心率");
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        iv_return = (ImageView) findViewById(R.id.iv_return);
        hpv_heart = (HeartProgressView) findViewById(R.id.hpv_heart);
        gifImageView = (GifImageView) findViewById(R.id.gif_heart);
        bt_detection_once = (Button) findViewById(R.id.bt_heart_detection_once);
        bt_detection_time = (Button) findViewById(R.id.bt_heart_detection_time);
        tv_detectioning = (TextView) findViewById(R.id.tv_detectioning);
        lv_heart_rate = (ListView) findViewById(R.id.lv_heart_rate);
        mLoadingView = (LoadingView) findViewById(R.id.loading_view);
        tv_no_data = (TextView) findViewById(R.id.tv_no_data);
        tv_loading_fail = (TextView) findViewById(R.id.tv_loading_fail);
        tv_data_day = (TextView) findViewById(R.id.tv_data_day);
        tv_data_week = (TextView) findViewById(R.id.tv_data_week);
        tv_data_month = (TextView) findViewById(R.id.tv_data_month);
        mLayout = (PtrClassicFrameLayout) findViewById(R.id.ptr_mLayout);
    }

    private void initData() {
        isTimeDetection = false;
        isOnceDetection = false;
        pageSize = 10;
        pageNo = 1;
        isNoMore = false;
        isCanLoadMore = true;
        dataList = new ArrayList<>();
        bcs = ApplicationXpClient.getInstance().getBluetoothService();
        mIntent = new Intent(mContext, MeasureInstructionsActivity.class);
        intent_data = new Intent(mContext, HeartRateDataActivity.class);
        initBroadcast();
        initAdapter();
        getDetectionData(pageNo, pageSize, true);
        initPtrClassicFrameLayout();
        initDescribeText();
    }

    private void initDescribeText() {
        String str_heart_rate = ControlSave.read(mContext, ConstantUtils.BAND_RECENT_HEART_RATE, "");
        if (!TextUtils.isEmpty(str_heart_rate)) {
            WristbandsHeartrate wristbandsHeartrate = HttpUtils.gb.create().fromJson(str_heart_rate, WristbandsHeartrate.class);
            hpv_heart.setProgress((new Double(wristbandsHeartrate.getHeartRate())).intValue());
            hpv_heart.setDescribe(TimeUtils.DateToStringSimpleDateFormat(wristbandsHeartrate.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
        }
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
                return PtrDefaultHandler2.checkContentCanBePulledUp(frame, lv_heart_rate, footer);
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
                return PtrDefaultHandler2.checkContentCanBePulledDown(frame, lv_heart_rate, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                pageNo = 1;
                getDetectionData(pageNo, pageSize, false);
            }
        });

    }

    private void initBroadcast() {
        mInfilter = new IntentFilter();
        mInfilter.addAction(ConstantUtils.ACTION_RECEIVE_MESSAGE_FROM_BAND);
        registerReceiver(myReceiver, mInfilter);
    }

    private void initAdapter() {
        mBandDataAdapter = new BandDataAdapter(mContext, dataList);
        lv_heart_rate.setAdapter(mBandDataAdapter);
    }

    private void initListener() {
        bt_detection_once.setOnClickListener(this);
        bt_detection_time.setOnClickListener(this);
        iv_return.setOnClickListener(this);
        tv_data_day.setOnClickListener(this);
        tv_data_week.setOnClickListener(this);
        tv_data_month.setOnClickListener(this);
        tv_loading_fail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isOnceDetection) {
            T.showShort("正在进行心率单次测量，请稍后");
        } else if (isTimeDetection) {
            if (v.getId() == R.id.bt_heart_detection_time) {
                isTimeDetection = false;
                bt_detection_time.setText(getResources().getString(R.string.measure_time));
                bcs.realTimeAndOnceMeasure(0X0A, 0);
                bt_detection_once.setPressed(false);
                bt_detection_once.setClickable(true);
            } else {
                T.showShort("正在进行心率实时测量，请稍后");
            }
        } else {
            switch (v.getId()) {
                case R.id.bt_heart_detection_once:
                    if (ApplicationXpClient.isBandConnect()) {
                        if (ControlSave.read(mContext, "measure_tip", "").isEmpty()) {
                            mIntent.putExtra("type", 1);
                            startActivityForResult(mIntent, 0);
                        } else {
                            isOnceDetection = true;
                            hpv_heart.setVisibility(View.GONE);
                            gifImageView.setVisibility(View.VISIBLE);
                            bt_detection_time.setVisibility(View.GONE);
                            bt_detection_once.setVisibility(View.GONE);
                            tv_detectioning.setVisibility(View.VISIBLE);
                            bcs.realTimeAndOnceMeasure(0X09, 1);
                            mHandler.sendEmptyMessageDelayed(1, 62000);
                        }
                    } else {
                        T.showShort("手环未连接");
                    }
                    break;
                case R.id.bt_heart_detection_time:
                    if (ApplicationXpClient.isBandConnect()) {
                        if (ControlSave.read(mContext, "measure_tip", "").isEmpty()) {
                            mIntent.putExtra("type", 2);
                            startActivityForResult(mIntent, 0);
                        } else {
                            isTimeDetection = true;
                            bt_detection_time.setText(getResources().getString(R.string.stop_measure));
                            bt_detection_once.setPressed(true);
                            bt_detection_once.setClickable(false);
                            bcs.realTimeAndOnceMeasure(0X0A, 1);
                        }
                    } else {
                        T.showShort("手环未连接");
                    }
                    break;
                case R.id.iv_return:
                    finish();
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
                if ((band_data[4] & 0xff) == 0x31 && (band_data[5] & 0xff) == 0x0a) {
                    int rate = (band_data[6] & 0xff);
                    hpv_heart.setProgress(rate);
                    hpv_heart.setDescribe(TimeUtils.DateToStringSimpleDateFormat(new Date(), TimeUtils.DATA_FORMAT_NO_YEAR));
                } else if ((band_data[4] & 0xff) == 0x31 && (band_data[5] & 0xff) == 0x09) {
                    hpv_heart.setProgress((band_data[6] & 0xff));
                    Message message = new Message();
                    message.what = 2;
                    message.arg1 = (band_data[6] & 0xff);
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
                if (type == 1) {
                    isOnceDetection = true;
                    hpv_heart.setVisibility(View.GONE);
                    gifImageView.setVisibility(View.VISIBLE);
                    bt_detection_time.setVisibility(View.GONE);
                    bt_detection_once.setVisibility(View.GONE);
                    tv_detectioning.setVisibility(View.VISIBLE);
                    bcs.realTimeAndOnceMeasure(0X09, 1);
                    mHandler.sendEmptyMessageDelayed(1, 62000);
                } else if (type == 2) {
                    isTimeDetection = true;
                    bt_detection_time.setText(getResources().getString(R.string.stop_measure));
                    bcs.realTimeAndOnceMeasure(0X0A, 1);
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
                T.showShort("正在进行心率单次测量，请稍后");
                return false;
            } else if (isTimeDetection) {
                T.showShort("正在进行心率实时测量，请稍后");
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
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getWristbandsHeartrateData, new HttpCallBack<List<WristbandsHeartrate>>() {
            @Override
            public void onSuccess(HttpResult result) {
                List<WristbandsHeartrate> wristbandsHeartrateList = (List<WristbandsHeartrate>) result.getData();
                isCanLoadMore = true;
                if (!isFirst)
                    mLayout.refreshComplete();
                if (wristbandsHeartrateList.size() == 0) {
                    isNoMore = true;
                    if (pageNo == 1) {
                        resetView(tv_no_data);
                        tv_no_data.setText("暂无数据");
                        isCanLoadMore = false;
                    } else {
                        T.showShort("暂无更多");
                    }
                } else {
                    resetView(lv_heart_rate);
                    if (pageNo == 1)
                        dataList.clear();
                    if (wristbandsHeartrateList.size() < 10)
                        isNoMore = true;
                    for (int i = 0; i < wristbandsHeartrateList.size(); i++) {
                        WristbandsHeartrate wristbandsHeartrate = wristbandsHeartrateList.get(i);
                        BandData bandData = new BandData();
                        bandData.setType(0);
                        bandData.setDetection_result((new Double(wristbandsHeartrate.getHeartRate())).intValue() + "次/分");
                        bandData.setDetection_time(TimeUtils.DateToStringSimpleDateFormat(wristbandsHeartrate.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
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
                    mLayout.refreshComplete();
                }
            }
        });

//        HttpUtils.getInstantce().getHeartRateData(pageNo, pageSize, new HttpConstant.SampleJsonResultListener<Feedback<List<WristbandsHeartrate>>>() {
//            @Override
//            public void onSuccess(Feedback<List<WristbandsHeartrate>> jsonData) {
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
//                    resetView(lv_heart_rate);
//                    if (pageNo == 1)
//                        dataList.clear();
//                    if (jsonData.getData().size() < 10)
//                        isNoMore = true;
//                    for (int i = 0; i < jsonData.getData().size(); i++) {
//                        WristbandsHeartrate wristbandsHeartrate = jsonData.getData().get(i);
//                        BandData bandData = new BandData();
//                        bandData.setType(0);
//                        bandData.setDetection_result((new Double(wristbandsHeartrate.getHeartRate())).intValue() + "次/分");
//                        bandData.setDetection_time(TimeUtils.DateToStringSimpleDateFormat(wristbandsHeartrate.getDetectTime(), TimeUtils.DATA_FORMAT_NO_YEAR));
//                        dataList.add(bandData);
//                    }
//                    mBandDataAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onFailure(Feedback<List<WristbandsHeartrate>> jsonData) {
//                if (isFirst) {
//                    resetView(tv_loading_fail);
//                    isCanLoadMore = false;
//                } else {
//                    T.showShort("网络故障，请稍后重试");
//                    mLayout.refreshComplete();
//                }
//
//            }
//        });

    }

    private void saveDetectionData(WristbandsHeartrate wristbandsHeartrate) {
        List<WristbandsHeartrate> wristbandsHeartrateList = new ArrayList<>();
        wristbandsHeartrateList.add(wristbandsHeartrate);
        HttpUtils.getInstantce().saveBandData(wristbandsHeartrateList, HttpConstant.Url_saveWristbandsHeartrateData, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
            @Override
            public void onSuccess(Feedback<String> jsonData) {

            }

            @Override
            public void onFailure(Feedback<String> jsonData) {

            }
        });
    }

    private void resetView(View v) {
        lv_heart_rate.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        tv_loading_fail.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.lv_heart_rate:
                lv_heart_rate.setVisibility(View.VISIBLE);
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
