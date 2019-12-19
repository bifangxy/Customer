package com.xiaopu.customer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.reflect.TypeToken;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.BandDateAdapter;
import com.xiaopu.customer.data.BandDataDetail;
import com.xiaopu.customer.utils.BarChartManager;
import com.xiaopu.customer.utils.ControlSave;
import com.xiaopu.customer.utils.DateUtils;
import com.xiaopu.customer.utils.TimeUtils;
import com.xiaopu.customer.utils.bluetooth.ConstantUtils;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.AutoLocateHorizontalView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Administrator on 2017/9/20.
 */

public class HeartRateDataActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = HeartRateDataActivity.class.getSimpleName();

    private static final int DAY = 1;

    private static final int WEEK = 2;

    private static final int MONTH = 3;

    private Context mContext;

    private BarChart mBarChart;

    private AutoLocateHorizontalView horizontal_view_date;

    private TextView tv_average_heart_rate;

    private TextView tv_highest_heart_rate;

    private TextView tv_minimum_heart_rate;

    private TextView tv_analysis_heart_rate;

    private TextView tv_tip_describe;

    private View view_instructions_day;

    private View view_instructions_week;

    private View view_instructions_month;

    private TextView tv_selected_day;

    private TextView tv_selected_week;

    private TextView tv_selected_month;

    private List<BarEntry> y_values;

    private List<String> dataList_day;

    private List<String> dataList_day_detail;

    private List<String> dataList_week;

    private List<String> dataList_week_detail;

    private List<String> dataList_month;

    private BandDateAdapter mAdapter;

    private View select_view;

    private List<Integer> dataList_key;
    private List<Integer> dataList_value;
    private List<BandDataDetail> dataDetailList;

    private HashMap<String, String> map = new HashMap<>();

    private int max_heart_rate;

    private int min_heart_rate;

    private String average_heart_rate;

    private int heart_rate;

    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_data);
        initActionBar("心率");
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mBarChart = (BarChart) findViewById(R.id.barChat_heart_rate);
        horizontal_view_date = (AutoLocateHorizontalView) findViewById(R.id.horizontal_view_date);
        tv_average_heart_rate = (TextView) findViewById(R.id.tv_average_heart_rate);
        tv_highest_heart_rate = (TextView) findViewById(R.id.tv_highest_heart_rate);
        tv_minimum_heart_rate = (TextView) findViewById(R.id.tv_minimum_heart_rate);
        tv_analysis_heart_rate = (TextView) findViewById(R.id.tv_analysis_heart_rate);
        view_instructions_day = findViewById(R.id.instructions_day);
        view_instructions_week = findViewById(R.id.instructions_week);
        view_instructions_month = findViewById(R.id.instructions_month);
        tv_selected_day = (TextView) findViewById(R.id.tv_selected_day);
        tv_selected_week = (TextView) findViewById(R.id.tv_selected_week);
        tv_selected_month = (TextView) findViewById(R.id.tv_selected_month);
        tv_tip_describe = (TextView) findViewById(R.id.tv_tip_describe);
    }

    private void initData() {
        y_values = new ArrayList<>();
        dataList_day = new ArrayList<>();
        dataList_day_detail = new ArrayList<>();
        dataList_week = new ArrayList<>();
        dataList_week_detail = new ArrayList<>();
        dataList_month = new ArrayList<>();

        dataList_key = new ArrayList<>();
        dataList_value = new ArrayList<>();
        dataDetailList = new ArrayList<>();
        String str = ControlSave.read(mContext, ConstantUtils.BAND_HEART_RATE_DATA_DETAIL, "");
        if (!TextUtils.isEmpty(str)) {
            dataDetailList = HttpUtils.gb.create().fromJson(str, new TypeToken<List<BandDataDetail>>() {
            }.getType());
        }
        dataList_day = DateUtils.getDaydate();
        dataList_day_detail = DateUtils.getDaydateDetail();
        dataList_week = DateUtils.getWeekdate();
        dataList_week_detail = DateUtils.getWeekdateDatail();
        dataList_month = DateUtils.getMonthdate();
        BarChartManager.setType(2);

        initInstructions();
        int type = getIntent().getIntExtra("type", 0);
        initAdapter(type);
        switch (type) {
            case 1:
                view_instructions_day.setVisibility(View.VISIBLE);
                select_view = tv_selected_day;
                break;
            case 2:
                view_instructions_week.setVisibility(View.VISIBLE);
                select_view = tv_selected_week;
                break;
            case 3:
                view_instructions_month.setVisibility(View.VISIBLE);
                select_view = tv_selected_month;
                break;
        }
    }

    private void initAdapter(int type) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        horizontal_view_date.setLayoutManager(linearLayoutManager);
        horizontal_view_date.clearAdapter();
        switch (type) {
            case 1:
                mAdapter = new BandDateAdapter(mContext, dataList_day);
                horizontal_view_date.setInitPos(dataList_day.size() - 1);
                horizontal_view_date.setItemCount(5);
                break;
            case 2:
                mAdapter = new BandDateAdapter(mContext, dataList_week);
                horizontal_view_date.setInitPos(dataList_week.size() - 1);
                horizontal_view_date.setItemCount(3);
                break;
            case 3:
                mAdapter = new BandDateAdapter(mContext, dataList_month);
                horizontal_view_date.setInitPos(dataList_month.size() - 1);
                horizontal_view_date.setItemCount(5);
                break;
        }
        horizontal_view_date.setAdapter(mAdapter);
    }

    private void initListener() {
        tv_selected_day.setOnClickListener(this);
        tv_selected_week.setOnClickListener(this);
        tv_selected_month.setOnClickListener(this);

        horizontal_view_date.setOnSelectedPositionChangedListener(new AutoLocateHorizontalView.OnSelectedPositionChangedListener() {
            @Override
            public void selectedPositionChanged(int pos) {
                switch (select_view.getId()) {
                    case R.id.tv_selected_day:
                        y_values.clear();
                        if (dataDetailList.size() != 0) {
                            y_values = getHourList(dataList_day_detail.get(pos));
                        }
                        BarChartManager.initBarChart(mContext, mBarChart, 0, y_values);
                        initHeartText();
                        initTipDescribe();
                        break;
                    case R.id.tv_selected_week:
                        y_values.clear();
                        if (dataDetailList.size() != 0) {
                            String[] dates = dataList_week_detail.get(pos).split("/");
                            Date date_start = DateUtils.YMDToDateTwo(dates[0]);
                            Date date_end = DateUtils.YMDToDateTwo(dates[1]);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date_end);
                            cal.add(Calendar.DATE, 1);
                            y_values = getDayListWeek(date_start, cal.getTime());
                        }
                        BarChartManager.initBarChart(mContext, mBarChart, 1, y_values);
                        initHeartText();
                        initTipDescribe();
                        break;
                    case R.id.tv_selected_month:
                        y_values.clear();
                        if (dataDetailList.size() != 0) {
                            y_values = getDayListMonth(dataList_month.get(pos));
                        }
                        BarChartManager.initBarChart(mContext, mBarChart, 2, y_values);
                        initHeartText();
                        initTipDescribe();
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        initInstructions();
        switch (v.getId()) {
            case R.id.tv_selected_day:
                if (select_view != tv_selected_day) {
                    select_view = v;
                    view_instructions_day.setVisibility(View.VISIBLE);
                    initAdapter(1);
                }
                break;
            case R.id.tv_selected_week:
                if (select_view != tv_selected_week) {
                    select_view = v;
                    view_instructions_week.setVisibility(View.VISIBLE);
                    initAdapter(2);
                }
                break;
            case R.id.tv_selected_month:
                if (select_view != tv_selected_month) {
                    select_view = v;
                    view_instructions_month.setVisibility(View.VISIBLE);
                    initAdapter(3);
                }
                break;
        }
    }

    private void initInstructions() {
        view_instructions_day.setVisibility(View.INVISIBLE);
        view_instructions_week.setVisibility(View.INVISIBLE);
        view_instructions_month.setVisibility(View.INVISIBLE);
    }

    private void initHeartText() {
        tv_average_heart_rate.setText(average_heart_rate);
        tv_highest_heart_rate.setText(String.valueOf(max_heart_rate));
        tv_minimum_heart_rate.setText(String.valueOf(min_heart_rate));
    }


    private void initTipDescribe() {
        Random random = new Random();
        int s = random.nextInt(4);
        switch (s) {
            case 0:
                tv_tip_describe.setText(getResources().getString(R.string.band_step_tip_one));
                break;
            case 1:
                tv_tip_describe.setText(getResources().getString(R.string.band_step_tip_two));
                break;
            case 2:
                tv_tip_describe.setText(getResources().getString(R.string.band_step_tip_three));
                break;
            case 3:
                tv_tip_describe.setText(getResources().getString(R.string.band_step_tip_four));
                break;
            default:
                break;
        }
    }

    private List<BarEntry> getHourList(String str_date) {
        dataList_key.clear();
        dataList_value.clear();
        map.clear();

        heart_rate = 0;
        max_heart_rate = 0;
        min_heart_rate = 0;

        for (int i = 0; i < dataDetailList.size(); i++) {
            BandDataDetail bandDataDetail = dataDetailList.get(i);
            String str_band_date = TimeUtils.DateToString(bandDataDetail.getmData());
            if (str_date.equals(str_band_date)) {
                map.put(String.valueOf(bandDataDetail.getHour()), "" + bandDataDetail.getValue1());
                dataList_key.add(bandDataDetail.getHour());
            }
        }
        List<BarEntry> dataList = new ArrayList<>();
        if (dataList_key.size() != 0) {
            Collections.sort(dataList_key);
            min_heart_rate = Integer.valueOf(map.get("" + dataList_key.get(0)));
            for (int i = 0; i < dataList_key.size(); i++) {
                int key = dataList_key.get(i);
                int value = Integer.valueOf(map.get("" + key));
                heart_rate = heart_rate + value;
                if (max_heart_rate < value) {
                    max_heart_rate = value;
                }
                if (min_heart_rate > value) {
                    min_heart_rate = value;
                }
                BarEntry barEntry = new BarEntry(value, key);
                dataList.add(barEntry);
            }
            DecimalFormat df = new DecimalFormat("0.00");
            average_heart_rate = df.format((float) heart_rate / dataList_key.size());
        }
        return dataList;
    }


    private List<BarEntry> getDayListWeek(Date date_start, Date date_end) {
        dataList_key.clear();
        dataList_value.clear();
        map.clear();

        heart_rate = 0;
        max_heart_rate = 0;
        min_heart_rate = 0;
        index = 0;

        for (int i = 0; i < dataDetailList.size(); i++) {
            BandDataDetail bandDataDetail = dataDetailList.get(i);
            if (bandDataDetail.getmData().getTime() >= date_start.getTime() && bandDataDetail.getmData().getTime() <= date_end.getTime()) {
                if (index == 0) {
                    min_heart_rate = bandDataDetail.getValue1();
                }
                heart_rate = heart_rate + bandDataDetail.getValue1();
                index++;
                if (map.get("" + bandDataDetail.getDay_week()) != null) {
                    int value = Integer.valueOf(map.get("" + bandDataDetail.getDay_week()).split("/")[0]);
                    int count = Integer.valueOf(map.get("" + bandDataDetail.getDay_week()).split("/")[1]);
                    map.put("" + bandDataDetail.getDay_week(), (bandDataDetail.getValue1() + value) + "/" + (count + 1));
                } else {
                    map.put("" + bandDataDetail.getDay_week(), bandDataDetail.getValue1() + "/" + 1);
                }

                if (min_heart_rate > bandDataDetail.getValue1()) {
                    min_heart_rate = bandDataDetail.getValue1();
                }
                if (max_heart_rate < bandDataDetail.getValue1()) {
                    max_heart_rate = bandDataDetail.getValue1();
                }
            }
        }
        List<BarEntry> dataList = new ArrayList<>();
        if (index != 0) {
            DecimalFormat df = new DecimalFormat("0.00");
            average_heart_rate = df.format((float) heart_rate / index);
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                dataList_key.add(Integer.valueOf(key));
            }
            Collections.sort(dataList_key);
            for (int i = 0; i < dataList_key.size(); i++) {
                int key = dataList_key.get(i);
                int value = Integer.parseInt(map.get("" + key).split("/")[0]);
                int count = Integer.parseInt(map.get("" + key).split("/")[1]);

                BarEntry barEntry = new BarEntry(value / count, key);
                dataList.add(barEntry);
            }
        }
        return dataList;
    }

    private List<BarEntry> getDayListMonth(String str_date) {
        dataList_key.clear();
        dataList_value.clear();
        map.clear();

        index = 0;
        heart_rate = 0;
        max_heart_rate = 0;
        min_heart_rate = 0;

        for (int i = 0; i < dataDetailList.size(); i++) {
            BandDataDetail bandDataDetail = dataDetailList.get(i);
            String str_band_date = TimeUtils.DateToStringSimpleDateFormat(bandDataDetail.getmData(), TimeUtils.DATE_FORMAT_NO_DAY);
            if (str_date.equals(str_band_date)) {
                if (index == 0) {
                    min_heart_rate = bandDataDetail.getValue1();
                }
                index++;
                if (map.get("" + bandDataDetail.getDay_month()) != null) {
                    int value = Integer.valueOf(map.get("" + bandDataDetail.getDay_month()).split("/")[0]);
                    int count = Integer.valueOf(map.get("" + bandDataDetail.getDay_month()).split("/")[1]);
                    map.put("" + bandDataDetail.getDay_month(), (bandDataDetail.getValue1() + value) + "/" + (count + 1));
                } else {
                    map.put("" + bandDataDetail.getDay_month(), bandDataDetail.getValue1() + "/" + 1);
                }
                heart_rate = heart_rate + bandDataDetail.getValue1();
                if (min_heart_rate > bandDataDetail.getValue1()) {
                    min_heart_rate = bandDataDetail.getValue1();
                }
                if (max_heart_rate < bandDataDetail.getValue1()) {
                    max_heart_rate = bandDataDetail.getValue1();
                }
            }
        }
        List<BarEntry> dataList = new ArrayList<>();
        if (index != 0) {
            DecimalFormat df = new DecimalFormat("0.00");
            average_heart_rate = df.format((float) heart_rate / index);
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                dataList_key.add(Integer.valueOf(key));
            }
            Collections.sort(dataList_key);
            for (int i = 0; i < dataList_key.size(); i++) {
                int key = dataList_key.get(i);
                int value = Integer.parseInt(map.get("" + key).split("/")[0]);
                int count = Integer.parseInt(map.get("" + key).split("/")[1]);
                BarEntry barEntry = new BarEntry(value / count, key - 1);
                dataList.add(barEntry);
            }
        }
        return dataList;
    }
}
