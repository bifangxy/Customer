package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.SimpleResultAdapter;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.SimpleDetectionResult;
import com.xiaopu.customer.data.jsonresult.DetectionHeartrate;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.TimeUtils;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.LoadingView.LoadingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/31.
 */

public class HeartActivity extends ActivityBase {

    private static final String LOG_TAG = HeartActivity.class.getSimpleName();

    private Context mContext;

    private ListView mListView;

    private Button bt_start_detection;

    private LoadingView mLoadingView;

    private TextView tv_loading_fail;

    private TextView tv_no_data;

    private MyClickListener mClick;

    private SimpleResultAdapter mAdapter;

    private List<SimpleDetectionResult> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        initActionBar("心率");
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.lv_recent_data);
        bt_start_detection = (Button) findViewById(R.id.bt_start_detection);
        mLoadingView = (LoadingView) findViewById(R.id.loading_view);
        tv_loading_fail = (TextView) findViewById(R.id.tv_loading_fail);
        tv_no_data = (TextView) findViewById(R.id.tv_no_data);
    }

    private void initData() {
        mClick = new MyClickListener();
        dataList = new ArrayList<>();
        mAdapter = new SimpleResultAdapter(mContext, dataList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHeartData();
    }

    private void initListener() {
        bt_start_detection.setOnClickListener(mClick);
        tv_loading_fail.setOnClickListener(mClick);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final SimpleDetectionResult simpleDetectionResult = dataList.get(position);

                Map<String, Object> maps = new HashMap<>();
                maps.put("id", simpleDetectionResult.getId());
                HttpUtils.getInstantce().showSweetDialog();
                HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getHearDetectionData, new HttpCallBack<String>() {
                    @Override
                    public void onSuccess(HttpResult result) {
                        HttpUtils.getInstantce().dismissSweetDialog();
                        String result_detail = (String) result.getData();
                        Intent mIntent = new Intent(mContext, HeartResultActivity.class);
                        mIntent.putExtra("heartData", result_detail);
                        mIntent.putExtra("result", simpleDetectionResult.getResult());
                        mIntent.putExtra("detectionResult", simpleDetectionResult.getDetectionResult());
                        startActivity(mIntent);
                    }

                    @Override
                    public void onFail(String msg) {

                    }
                });

/*
                HttpUtils.getInstantce().getHearDetectionData(Integer.valueOf(simpleDetectionResult.getId() + ""), new HttpConstant.SampleJsonResultListener<Feedback>() {
                    @Override
                    public void onSuccess(Feedback jsonData) {
                        Log.d(LOG_TAG, "获取成功");
                        Intent mIntent = new Intent(mContext, HeartResultActivity.class);
                        mIntent.putExtra("heartData", jsonData.getData().toString());
                        mIntent.putExtra("result", simpleDetectionResult.getResult());
                        mIntent.putExtra("detectionResult", simpleDetectionResult.getDetectionResult());
                        startActivity(mIntent);
                    }

                    @Override
                    public void onFailure(Feedback jsonData) {
                        Log.d(LOG_TAG, "获取失败");
                    }
                });
*/
            }
        });
    }

    private void getHeartData() {
        resetView(mLoadingView);

        HttpUtils.getInstantce().postNoHead(HttpConstant.Url_HeartrateAllDate, new HttpCallBack<List<DetectionHeartrate>>() {
            @Override
            public void onSuccess(HttpResult result) {
                List<DetectionHeartrate> detectionHeartrateList = (List<DetectionHeartrate>) result.getData();
                if (detectionHeartrateList.size() != 0) {
                    resetView(mListView);
                    dataList.clear();
                    for (int i = 0; i < detectionHeartrateList.size(); i++) {
                        DetectionHeartrate detectionHeartrate = detectionHeartrateList.get(i);
                        SimpleDetectionResult simpleDetectionResult = new SimpleDetectionResult();
                        simpleDetectionResult.setId(detectionHeartrate.getId());
                        simpleDetectionResult.setCategory(4);
                        simpleDetectionResult.setDetectionTime(TimeUtils.DateToStringSimpleDateFormat(detectionHeartrate.getCreateTime(), TimeUtils.DATE_FORMAT_Point));
                        simpleDetectionResult.setNormal_atio("55~100");
                        simpleDetectionResult.setDetectionResult("" + detectionHeartrate.getHeartRate());
                        simpleDetectionResult.setResult(detectionHeartrate.getChartUrl());
                        dataList.add(simpleDetectionResult);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    resetView(tv_no_data);
                    tv_no_data.setText("暂无心率检测数据");
                }
            }

            @Override
            public void onFail(String msg) {
                resetView(tv_loading_fail);
            }
        });

      /*  HttpUtils.getInstantce().getHeartrateAllData(new HttpConstant.SampleJsonResultListener<Feedback<List<DetectionHeartrate>>>() {
            @Override
            public void onSuccess(Feedback<List<DetectionHeartrate>> jsonData) {
                Log.d(LOG_TAG, "获取成功");
                List<DetectionHeartrate> detectionHeartrateList = jsonData.getData();
                if (detectionHeartrateList.size() != 0) {
                    resetView(mListView);
                    for (int i = 0; i < detectionHeartrateList.size(); i++) {
                        DetectionHeartrate detectionHeartrate = detectionHeartrateList.get(i);
                        SimpleDetectionResult simpleDetectionResult = new SimpleDetectionResult();
                        simpleDetectionResult.setId(detectionHeartrate.getId());
                        simpleDetectionResult.setCategory(4);
                        simpleDetectionResult.setDetectionTime(TimeUtils.DateToStringSimpleDateFormat(detectionHeartrate.getCreateTime(), TimeUtils.DATE_FORMAT_Point));
                        simpleDetectionResult.setNormal_atio("55~100");
                        simpleDetectionResult.setDetectionResult("" + detectionHeartrate.getHeartRate());
                        simpleDetectionResult.setResult(detectionHeartrate.getChartUrl());
                        dataList.add(simpleDetectionResult);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    resetView(tv_no_data);
                    tv_no_data.setText("暂无心率检测数据");
                }
            }

            @Override
            public void onFailure(Feedback<List<DetectionHeartrate>> jsonData) {
                resetView(tv_loading_fail);
                Log.d(LOG_TAG, "获取失败");
            }
        });*/

    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_start_detection:
                    if (ApplicationXpClient.isConnect()) {
                        startActivity(new Intent(mContext, HeartStepActivity.class));
                    } else {
                        T.showShort("马桶未连接，请先连接马桶");
                    }
                    break;
                case R.id.tv_loading_fail:
                    getHeartData();
                    break;
            }
        }
    }

    private void resetView(View v) {
        mListView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        tv_loading_fail.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.lv_recent_data:
                mListView.setVisibility(View.VISIBLE);
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
