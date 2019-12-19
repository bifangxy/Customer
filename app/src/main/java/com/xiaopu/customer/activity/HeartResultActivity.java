package com.xiaopu.customer.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;
import com.xiaopu.customer.utils.LineChartManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/6/12.
 */

public class HeartResultActivity extends ActivityBase {
    private static final String LOG_TAG = HeartResultActivity.class.getSimpleName();

    private Context mContext;

    private LineChart mChartView;

    private TextView tv_heart_rate;

    private TextView tv_heart_descirbe;

    private ImageView iv_pause;

    private ProgressBar mProgressBar;

    private String sourceData;

    private String[] datas;

    private int position;

    private boolean isPause;

    private boolean isStart;

    private LinkedList<Float> heartList;

    private Timer mTimer;

    private TimerTask mTask;

    private MyClickListener mClick;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    LineChartManager.changeData(heartList);
                    break;
                case 2:
                    isStart = false;
                    stop();
                    iv_pause.setImageResource(R.mipmap.heart_pause);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_result);
        mContext = this;
        initActionBar("心率详情");
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mChartView = (LineChart) findViewById(R.id.lineChart_heart);
        tv_heart_rate = (TextView) findViewById(R.id.tv_heart_rate);
        tv_heart_descirbe = (TextView) findViewById(R.id.tv_heart_describe);
        iv_pause = (ImageView) findViewById(R.id.iv_heart_pause);
        mProgressBar = (ProgressBar) findViewById(R.id.heart_progress);
    }

    private void initData() {
        isStart = false;
        mClick = new MyClickListener();
        sourceData = getIntent().getStringExtra("heartData");
        String result = getIntent().getStringExtra("result");
        String detectionResult = getIntent().getStringExtra("detectionResult");
        tv_heart_descirbe.setText(result);
        tv_heart_rate.setText(detectionResult);
        datas = sourceData.split(",");
        heartList = new LinkedList<>();
        initChart();
    }

    private void initChart() {
        mChartView.setDescription("");
        ArrayList<String> xValues = new ArrayList<>();
        for (int i = 0; i < 450; i++) {
            xValues.add("");
        }
        ArrayList<Entry> yValue = new ArrayList<>();
        LineChartManager.initSingleLineChart(mContext, mChartView, xValues, yValue);

        for (int i = 0; i < 450; i++) {
            heartList.add((Float.valueOf(datas[i])));
        }
        mHandler.sendEmptyMessage(1);
    }

    private void initListener() {
        iv_pause.setOnClickListener(mClick);
    }


    @Override
    protected void onDestroy() {
        stop();
        super.onDestroy();
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_heart_pause:
                    if (!isStart) {
                        startShowResult();
                    } else {
                        isPause = !isPause;
                    }
                    if (isPause) {
                        iv_pause.setImageResource(R.mipmap.heart_pause);
                    } else {
                        iv_pause.setImageResource(R.mipmap.translate);
                    }
                    break;
            }
        }
    }

    private void startShowResult() {
        isPause = false;
        isStart = true;
        position = 0;
        heartList.clear();
        mProgressBar.setMax(datas.length);
        mTimer = new Timer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                if (position < datas.length) {
                    if (!isPause) {
                        mProgressBar.setProgress(position);
                        if (heartList.size() >= 450) {
                            for (int i = 0; i < 10; i++) {
                                if (position < datas.length) {
                                    heartList.removeFirst();
                                    heartList.addLast((Float.valueOf(datas[position])));
                                    position++;
                                }
                            }
                        } else {
                            for (int i = 0; i < 10; i++) {
                                if (position < datas.length) {
                                    heartList.add((Float.valueOf(datas[position])));
                                    position++;
                                }
                            }
                        }
                        mHandler.sendEmptyMessage(1);
                    }
                } else {
                    mHandler.sendEmptyMessage(2);
                }
            }
        };
        mTimer.schedule(mTask, 1, 50);
    }

    private void stop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
    }

}
