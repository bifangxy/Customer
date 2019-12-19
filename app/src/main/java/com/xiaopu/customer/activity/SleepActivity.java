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
import com.xiaopu.customer.data.Pickers;
import com.xiaopu.customer.data.jsonresult.Sleeping;
import com.xiaopu.customer.data.jsonresult.Walk;
import com.xiaopu.customer.utils.BarChartManager;
import com.xiaopu.customer.utils.ControlSave;
import com.xiaopu.customer.utils.DateUtils;
import com.xiaopu.customer.utils.T;
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
 * Created by Administrator on 2017/9/11.
 */

public class SleepActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = SleepActivity.class.getSimpleName();

    private Context mContext;

    private AutoLocateHorizontalView horizontal_view_date;

    private BarChart mBarChart;

    private TextView tv_sleep_length_describe;

    private TextView tv_sleep_length_hour;

    private TextView tv_sleep_length_minute;

    private TextView tv_depth_sleep_describe;

    private TextView tv_depth_sleep_hour;

    private TextView tv_depth_sleep_minute;

    private TextView tv_shallow_sleep_describe;

    private TextView tv_shallow_sleep_hour;

    private TextView tv_shallow_sleep_minute;

    private TextView tv_sober_count_describe;

    private TextView tv_sober_count;

    private View view_instructions_day;

    private View view_instructions_week;

    private View view_instructions_month;

    private TextView tv_selected_day;

    private TextView tv_selected_week;

    private TextView tv_selected_month;

    private View select_view;

    private List<BarEntry> y_values;

    private List<String> dataList_day;

    private List<String> dataList_day_detail;

    private List<String> dataList_week;

    private List<String> dataList_week_detail;

    private List<String> dataList_month;

    private BandDateAdapter mAdapter;

    private List<Integer> dataList_key;

    private List<Integer> dataList_value;

    private List<BandDataDetail> dataDetailList;

    private HashMap<String, String> map = new HashMap<>();

    private Sleeping current_sleep;

    private int deep_sleep;

    private int shallow_sleep;

    private int sober_count;

    private String str_sober_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        initActionBar("睡眠");
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        horizontal_view_date = (AutoLocateHorizontalView) findViewById(R.id.horizontal_view_date);

        mBarChart = (BarChart) findViewById(R.id.barChat_sleep);

        tv_sleep_length_describe = (TextView) findViewById(R.id.tv_sleep_length_describe);

        tv_sleep_length_hour = (TextView) findViewById(R.id.tv_sleep_length_hour);

        tv_sleep_length_minute = (TextView) findViewById(R.id.tv_sleep_length_minute);

        tv_depth_sleep_describe = (TextView) findViewById(R.id.tv_depth_sleep_describe);

        tv_depth_sleep_hour = (TextView) findViewById(R.id.tv_depth_sleep_hour);

        tv_depth_sleep_minute = (TextView) findViewById(R.id.tv_depth_sleep_minute);

        tv_shallow_sleep_describe = (TextView) findViewById(R.id.tv_shallow_sleep_describe);

        tv_shallow_sleep_hour = (TextView) findViewById(R.id.tv_shallow_sleep_hour);

        tv_shallow_sleep_minute = (TextView) findViewById(R.id.tv_shallow_sleep_minute);

        tv_sober_count_describe = (TextView) findViewById(R.id.tv_sober_count_describe);

        tv_sober_count = (TextView) findViewById(R.id.tv_sober_count);

        view_instructions_day = findViewById(R.id.instructions_day);

        view_instructions_week = findViewById(R.id.instructions_week);

        view_instructions_month = findViewById(R.id.instructions_month);

        tv_selected_day = (TextView) findViewById(R.id.tv_selected_day);

        tv_selected_week = (TextView) findViewById(R.id.tv_selected_week);

        tv_selected_month = (TextView) findViewById(R.id.tv_selected_month);

    }

    private void initData() {
        y_values = new ArrayList<>();
        dataList_day = new ArrayList<>();
        dataList_day_detail = new ArrayList<>();
        dataList_week = new ArrayList<>();
        dataList_month = new ArrayList<>();
        dataList_week_detail = new ArrayList<>();
        dataList_key = new ArrayList<>();
        dataList_value = new ArrayList<>();
        dataDetailList = new ArrayList<>();

        dataList_day = DateUtils.getDaydate();
        dataList_week = DateUtils.getWeekdate();
        dataList_month = DateUtils.getMonthdate();
        dataList_week_detail = DateUtils.getWeekdateDatail();
        dataList_day_detail = DateUtils.getDaydateDetail();

        String str = ControlSave.read(mContext, ConstantUtils.BAND_SLEEP_DATA_DETAIL, "");
        String str_current = ControlSave.read(mContext, ConstantUtils.BAND_CURRENT_SLEEP, "");
        if (!TextUtils.isEmpty(str)) {
            dataDetailList = HttpUtils.gb.create().fromJson(str, new TypeToken<List<BandDataDetail>>() {
            }.getType());
        }

        if (!TextUtils.isEmpty(str_current)) {
            current_sleep = HttpUtils.gb.create().fromJson(str_current, Sleeping.class);
            //TODO 描述初始化
            tv_sleep_length_hour.setText(current_sleep.getSleepDuration().split("-")[0]);
            tv_sleep_length_minute.setText(current_sleep.getSleepDuration().split("-")[1]);
            tv_depth_sleep_hour.setText(current_sleep.getDeepSleepDuration().split("-")[0]);
            tv_depth_sleep_minute.setText(current_sleep.getDeepSleepDuration().split("-")[1]);
            tv_shallow_sleep_hour.setText(current_sleep.getShallowSleepDuration().split("-")[0]);
            tv_shallow_sleep_minute.setText(current_sleep.getShallowSleepDuration().split("-")[1]);
            tv_sober_count.setText("" + current_sleep.getSoberCount());
        }

        BarChartManager.setType(1);
        select_view = tv_selected_day;
        initAdapter(0);
        initInstructions();
        view_instructions_day.setVisibility(View.VISIBLE);
    }


    private void initAdapter(int type) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        horizontal_view_date.setLayoutManager(linearLayoutManager);
        horizontal_view_date.clearAdapter();
        switch (type) {
            case 0:
                mAdapter = new BandDateAdapter(mContext, dataList_day);
                horizontal_view_date.setInitPos(dataList_day.size() - 1);
                horizontal_view_date.setItemCount(5);
                break;
            case 1:
                mAdapter = new BandDateAdapter(mContext, dataList_week);
                horizontal_view_date.setInitPos(dataList_week.size() - 1);
                horizontal_view_date.setItemCount(3);
                break;
            case 2:
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
                        initSleepText();
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
                        initSleepText();
                        break;
                    case R.id.tv_selected_month:
                        y_values.clear();
                        if (dataDetailList.size() != 0) {
                            y_values = getDayListMonth(dataList_month.get(pos));
                        }
                        BarChartManager.initBarChart(mContext, mBarChart, 2, y_values);
                        initSleepText();
                        break;
                }
            }
        });
    }

    private void initSleepText() {
        if ((deep_sleep + shallow_sleep) != 0) {
            tv_sleep_length_hour.setText(String.valueOf((deep_sleep + shallow_sleep) / 60));
            tv_sleep_length_minute.setText(String.valueOf((deep_sleep + shallow_sleep) % 60));
            tv_depth_sleep_hour.setText(String.valueOf(deep_sleep / 60));
            tv_depth_sleep_minute.setText(String.valueOf(deep_sleep % 60));
            tv_shallow_sleep_hour.setText(String.valueOf(shallow_sleep / 60));
            tv_shallow_sleep_minute.setText(String.valueOf(shallow_sleep % 60));
            tv_sober_count.setText(str_sober_count);
        } else {
            tv_sleep_length_hour.setText("--");
            tv_sleep_length_minute.setText("--");
            tv_depth_sleep_hour.setText("--");
            tv_depth_sleep_minute.setText("--");
            tv_shallow_sleep_hour.setText("--");
            tv_shallow_sleep_minute.setText("--");
            tv_sober_count.setText("--");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_selected_day:
                if (select_view != tv_selected_day) {
                    select_view = tv_selected_day;
                    initInstructions();
                    view_instructions_day.setVisibility(View.VISIBLE);
                    tv_sleep_length_describe.setText(R.string.sleep_length);
                    tv_depth_sleep_describe.setText(R.string.depth_sleep);
                    tv_shallow_sleep_describe.setText(R.string.shallow_sleep);
                    tv_sober_count_describe.setText(R.string.sober_count);
                    initAdapter(0);
                }
                break;
            case R.id.tv_selected_week:
                if (select_view != tv_selected_week) {
                    select_view = tv_selected_week;
                    initInstructions();
                    view_instructions_week.setVisibility(View.VISIBLE);
                    tv_sleep_length_describe.setText(R.string.daily_sleep);
                    tv_depth_sleep_describe.setText(R.string.daily_depth_sleep);
                    tv_shallow_sleep_describe.setText(R.string.daily_shallow_sleep);
                    tv_sober_count_describe.setText(R.string.daily_sober_count);
                    initAdapter(1);
                }
                break;
            case R.id.tv_selected_month:
                if (select_view != tv_selected_month) {
                    select_view = tv_selected_month;
                    initInstructions();
                    view_instructions_month.setVisibility(View.VISIBLE);
                    tv_sleep_length_describe.setText(R.string.daily_sleep);
                    tv_depth_sleep_describe.setText(R.string.daily_depth_sleep);
                    tv_shallow_sleep_describe.setText(R.string.daily_shallow_sleep);
                    tv_sober_count_describe.setText(R.string.daily_sober_count);
                    initAdapter(2);
                }
                break;
        }
    }

    private void initInstructions() {
        view_instructions_day.setVisibility(View.INVISIBLE);
        view_instructions_week.setVisibility(View.INVISIBLE);
        view_instructions_month.setVisibility(View.INVISIBLE);
    }


    private List<BarEntry> getHourList(String str_date) {
        dataList_key.clear();
        dataList_value.clear();

        deep_sleep = 0;
        shallow_sleep = 0;
        sober_count = 0;
        map.clear();
        for (int i = 0; i < dataDetailList.size(); i++) {
            BandDataDetail bandDataDetail = dataDetailList.get(i);
            String str_band_date = TimeUtils.DateToString(bandDataDetail.getmData());
            if (str_date.equals(str_band_date)) {
                dataList_key.add(bandDataDetail.getHour());
                dataList_value.add(bandDataDetail.getValue1() + bandDataDetail.getValue2());
                if (deep_sleep < bandDataDetail.getValue2()) {
                    shallow_sleep = bandDataDetail.getValue1();
                    deep_sleep = bandDataDetail.getValue2();
                    str_sober_count = "" + bandDataDetail.getValue3();
                }
            }
        }
        List<BarEntry> dataList = new ArrayList<>();
        if (dataList_key.size() != 0) {
            Collections.sort(dataList_key);
            Collections.sort(dataList_value);
            for (int i = 0; i < dataList_key.size(); i++) {
                BarEntry barEntry;
                if (i == 0) {
                    if (dataList_value.get(i) >= 60) {
                        barEntry = new BarEntry(60, dataList_key.get(i));
                    } else {
                        barEntry = new BarEntry(dataList_value.get(i), dataList_key.get(i));
                    }
                } else {
                    barEntry = new BarEntry(dataList_value.get(i) - dataList_value.get(i - 1), dataList_key.get(i));
                }
                dataList.add(barEntry);
            }
        }
        return dataList;
    }


    private List<BarEntry> getDayListWeek(Date date_start, Date date_end) {
        dataList_key.clear();
        dataList_value.clear();
        deep_sleep = 0;
        shallow_sleep = 0;
        sober_count = 0;
        map.clear();
        for (int i = 0; i < dataDetailList.size(); i++) {
            BandDataDetail bandDataDetail = dataDetailList.get(i);
            if (bandDataDetail.getmData().getTime() >= date_start.getTime() && bandDataDetail.getmData().getTime() <= date_end.getTime()) {
                if (map.get("" + bandDataDetail.getDay_week()) != null) {
                    int value = Integer.valueOf(map.get("" + bandDataDetail.getDay_week()).split("/")[0]) + Integer.valueOf(map.get("" + bandDataDetail.getDay_week()).split("/")[1]);
                    if (value < (bandDataDetail.getValue1() + bandDataDetail.getValue2())) {
                        map.put("" + bandDataDetail.getDay_week(), bandDataDetail.getValue1() + "/" + bandDataDetail.getValue2() + "/" + bandDataDetail.getValue3());
                    }
                } else {
                    map.put("" + bandDataDetail.getDay_week(), bandDataDetail.getValue1() + "/" + bandDataDetail.getValue2() + "/" + bandDataDetail.getValue3());
                }
            }
        }
        List<BarEntry> dataList = new ArrayList<>();
        if (map.size() != 0) {
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                dataList_key.add(Integer.valueOf(key));
            }
            Collections.sort(dataList_key);
            for (int j = 0; j < dataList_key.size(); j++) {
                int key = dataList_key.get(j);
                int shallow_value = Integer.valueOf(map.get("" + key).split("/")[0]);
                int deep_value = Integer.valueOf(map.get("" + key).split("/")[1]);
                int sober_value = Integer.valueOf(map.get("" + key).split("/")[2]);

                deep_sleep = deep_sleep + deep_value;
                shallow_sleep = shallow_sleep + shallow_value;
                sober_count = sober_count + sober_value;

                BarEntry barEntry = new BarEntry(deep_value + shallow_value, key);
                dataList.add(barEntry);
            }
            DecimalFormat df = new DecimalFormat("0.00");
            deep_sleep = deep_sleep / dataList_key.size();
            shallow_sleep = shallow_sleep / dataList_key.size();
            str_sober_count = df.format((float) sober_count / dataList_key.size());
        }
        return dataList;
    }

    private List<BarEntry> getDayListMonth(String str_date) {
        dataList_key.clear();
        dataList_value.clear();

        deep_sleep = 0;
        shallow_sleep = 0;
        sober_count = 0;
        map.clear();
        for (int i = 0; i < dataDetailList.size(); i++) {
            BandDataDetail bandDataDetail = dataDetailList.get(i);
            String str_band_date = TimeUtils.DateToStringSimpleDateFormat(bandDataDetail.getmData(), TimeUtils.DATE_FORMAT_NO_DAY);
            if (str_date.equals(str_band_date)) {
                if (map.get("" + bandDataDetail.getDay_month()) != null) {
                    int value = Integer.valueOf(map.get("" + bandDataDetail.getDay_month()).split("/")[0]) + Integer.valueOf(map.get("" + bandDataDetail.getDay_month()).split("/")[1]);
                    if (value < (bandDataDetail.getValue1() + bandDataDetail.getValue2())) {
                        map.put("" + bandDataDetail.getDay_month(), bandDataDetail.getValue1() + "/" + bandDataDetail.getValue2() + "/" + bandDataDetail.getValue3());
                    }
                } else {
                    map.put("" + bandDataDetail.getDay_month(), bandDataDetail.getValue1() + "/" + bandDataDetail.getValue2() + "/" + bandDataDetail.getValue3());
                }
            }
        }
        List<BarEntry> dataList = new ArrayList<>();
        if (map.size() != 0) {
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                dataList_key.add(Integer.valueOf(key));
            }
            Collections.sort(dataList_key);
            for (int i = 0; i < dataList_key.size(); i++) {
                int key = dataList_key.get(i);
                int shallow_value = Integer.valueOf(map.get("" + key).split("/")[0]);
                int deep_value = Integer.valueOf(map.get("" + key).split("/")[1]);
                int sober_value = Integer.valueOf(map.get("" + key).split("/")[2]);

                deep_sleep = deep_sleep + deep_value;
                shallow_sleep = shallow_sleep + shallow_value;
                sober_count = sober_count + sober_value;

                BarEntry barEntry = new BarEntry(deep_value + shallow_value, key - 1);
                dataList.add(barEntry);
            }
            DecimalFormat df = new DecimalFormat("0.00");
            deep_sleep = deep_sleep / dataList_key.size();
            shallow_sleep = shallow_sleep / dataList_key.size();
            str_sober_count = df.format((float) sober_count / dataList_key.size());
        }
        return dataList;
    }
}
