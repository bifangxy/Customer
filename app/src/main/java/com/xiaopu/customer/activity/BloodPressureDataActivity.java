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

public class BloodPressureDataActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = BloodPressureDataActivity.class.getSimpleName();

    private static final int DAY = 1;

    private static final int WEEK = 2;

    private static final int MONTH = 3;

    private Context mContext;

    private BarChart mBarChart;

    private AutoLocateHorizontalView horizontal_view_date;

    private TextView tv_average_blood_pressure;

    private TextView tv_highest_blood_pressure;

    private TextView tv_minimum_blood_pressure;

    private TextView tv_analysis_blood_pressure;

    private TextView tv_tip_describe;

    private View view_instructions_day;

    private View view_instructions_week;

    private View view_instructions_month;

    private TextView tv_selected_day;

    private TextView tv_selected_week;

    private TextView tv_selected_month;

    private List<BarEntry> y_values;

    private List<BarEntry> y_two_values;

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

    private int max_blood_high_pressure;

    private int max_blood_low_pressure;

    private int min_blood_high_pressure;

    private int min_blood_low_pressure;

    private String average_blood_pressure;

    private int blood_high_blood;

    private int blood_low_blood;

    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure_data);
        initActionBar("血压");
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mBarChart = (BarChart) findViewById(R.id.barChat_blood_pressure);
        horizontal_view_date = (AutoLocateHorizontalView) findViewById(R.id.horizontal_view_date);
        tv_average_blood_pressure = (TextView) findViewById(R.id.tv_average_blood_pressure);
        tv_highest_blood_pressure = (TextView) findViewById(R.id.tv_highest_blood_pressure);
        tv_minimum_blood_pressure = (TextView) findViewById(R.id.tv_minimum_blood_pressure);
        tv_analysis_blood_pressure = (TextView) findViewById(R.id.tv_analysis_blood_pressure);
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
        y_two_values = new ArrayList<>();
        dataList_day = new ArrayList<>();
        dataList_day_detail = new ArrayList<>();
        dataList_week = new ArrayList<>();
        dataList_week_detail = new ArrayList<>();
        dataList_month = new ArrayList<>();

        dataList_key = new ArrayList<>();
        dataList_value = new ArrayList<>();
        dataDetailList = new ArrayList<>();

        String str = ControlSave.read(mContext, ConstantUtils.BAND_BLOOD_PRESSURE_DATA_DETAIL, "");
        if (!TextUtils.isEmpty(str)) {
            dataDetailList = HttpUtils.gb.create().fromJson(str, new TypeToken<List<BandDataDetail>>() {
            }.getType());
        }

        dataList_day = DateUtils.getDaydate();
        dataList_day_detail = DateUtils.getDaydateDetail();
        dataList_week = DateUtils.getWeekdate();
        dataList_week_detail = DateUtils.getWeekdateDatail();
        dataList_month = DateUtils.getMonthdate();

        BarChartManager.setType(3);

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
                        y_two_values.clear();
                        if (dataDetailList.size() != 0) {
                            if (getHourList(dataList_day_detail.get(pos)).size() != 0) {
                                y_values = getHourList(dataList_day_detail.get(pos)).get(0);
                                y_two_values = getHourList(dataList_day_detail.get(pos)).get(1);
                            }
                        }
                        BarChartManager.initDoubleBarChart(mContext, mBarChart, 0, y_values, y_two_values);
                        initBloodPressureText();
                        initTipDescribe();
                        break;
                    case R.id.tv_selected_week:
                        y_values.clear();
                        y_two_values.clear();
                        if (dataDetailList.size() != 0) {
                            String[] dates = dataList_week_detail.get(pos).split("/");
                            Date date_start = DateUtils.YMDToDateTwo(dates[0]);
                            Date date_end = DateUtils.YMDToDateTwo(dates[1]);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date_end);
                            cal.add(Calendar.DATE, 1);
                            if (getDayListWeek(date_start, cal.getTime()).size() != 0) {
                                y_values = getDayListWeek(date_start, cal.getTime()).get(0);
                                y_two_values = getDayListWeek(date_start, cal.getTime()).get(1);
                            }
                        }
                        BarChartManager.initDoubleBarChart(mContext, mBarChart, 1, y_values, y_two_values);
                        initBloodPressureText();
                        initTipDescribe();
                        break;
                    case R.id.tv_selected_month:
                        y_values.clear();
                        y_two_values.clear();
                        if (dataDetailList.size() != 0) {
                            if (getDayListMonth(dataList_month.get(pos)).size() != 0) {
                                y_values = getDayListMonth(dataList_month.get(pos)).get(0);
                                y_two_values = getDayListMonth(dataList_month.get(pos)).get(1);
                            }
                        }
                        BarChartManager.initDoubleBarChart(mContext, mBarChart, 2, y_values, y_two_values);
                        initBloodPressureText();
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
                    BarChartManager.initBarChart(mContext, mBarChart, 2, y_values);
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

    private void initBloodPressureText() {
        tv_average_blood_pressure.setText(average_blood_pressure);
        tv_highest_blood_pressure.setText(max_blood_high_pressure + "/" + max_blood_low_pressure);
        tv_minimum_blood_pressure.setText(min_blood_high_pressure + "/" + min_blood_low_pressure);
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


    private List<List<BarEntry>> getHourList(String str_date) {
        dataList_key.clear();
        dataList_value.clear();
        map.clear();

        blood_low_blood = 0;
        blood_high_blood = 0;
        max_blood_low_pressure = 0;
        max_blood_high_pressure = 0;
        min_blood_low_pressure = 0;
        min_blood_high_pressure = 0;
        index = 0;

        for (int i = 0; i < dataDetailList.size(); i++) {
            BandDataDetail bandDataDetail = dataDetailList.get(i);
            String str_band_date = TimeUtils.DateToString(bandDataDetail.getmData());
            if (str_date.equals(str_band_date)) {
                map.put(String.valueOf(bandDataDetail.getHour()), bandDataDetail.getValue1() + "/" + bandDataDetail.getValue2());
                dataList_key.add(bandDataDetail.getHour());
            }
        }
        List<List<BarEntry>> dataList = new ArrayList<>();
        if (dataList_key.size() != 0) {
            Collections.sort(dataList_key);

            min_blood_high_pressure = Integer.valueOf(map.get("" + dataList_key.get(0)).split("/")[0]);
            min_blood_low_pressure = Integer.valueOf(map.get("" + dataList_key.get(0)).split("/")[1]);

            List<BarEntry> dataListOne = new ArrayList<>();
            List<BarEntry> dataListTwo = new ArrayList<>();
            for (int i = 0; i < dataList_key.size(); i++) {
                int key = dataList_key.get(i);

                int value1 = Integer.parseInt(map.get("" + key).split("/")[0]);
                int value2 = Integer.parseInt(map.get("" + key).split("/")[1]);

                blood_high_blood = blood_high_blood + value1;
                blood_low_blood = blood_low_blood + value2;
                if (max_blood_high_pressure < value1) {
                    max_blood_high_pressure = value1;
                }
                if (max_blood_low_pressure < value2) {
                    max_blood_low_pressure = value2;
                }
                if (min_blood_high_pressure > value1) {
                    min_blood_high_pressure = value1;
                }
                if (min_blood_high_pressure > value1) {
                    min_blood_high_pressure = value1;
                }
                BarEntry barEntryOne = new BarEntry(value1, key);
                dataListOne.add(barEntryOne);
                BarEntry barEntryTwo = new BarEntry(value2, key);
                dataListTwo.add(barEntryTwo);
            }
            average_blood_pressure = blood_high_blood / dataList_key.size() + "/" + blood_low_blood / dataList_key.size();
            dataList.add(dataListOne);
            dataList.add(dataListTwo);
        }
        return dataList;
    }


    private List<List<BarEntry>> getDayListWeek(Date date_start, Date date_end) {
        dataList_key.clear();
        dataList_value.clear();
        map.clear();

        blood_low_blood = 0;
        blood_high_blood = 0;
        max_blood_low_pressure = 0;
        max_blood_high_pressure = 0;
        min_blood_low_pressure = 0;
        min_blood_high_pressure = 0;
        index = 0;

        for (int i = 0; i < dataDetailList.size(); i++) {
            BandDataDetail bandDataDetail = dataDetailList.get(i);
            if (bandDataDetail.getmData().getTime() >= date_start.getTime() && bandDataDetail.getmData().getTime() <= date_end.getTime()) {
                if (index == 0) {
                    min_blood_high_pressure = bandDataDetail.getValue1();
                    min_blood_low_pressure = bandDataDetail.getValue2();
                }
                index++;
                if (map.get("" + bandDataDetail.getDay_week()) != null) {
                    int value1 = Integer.parseInt(map.get("" + bandDataDetail.getDay_week()).split("/")[0]);
                    int value2 = Integer.parseInt(map.get("" + bandDataDetail.getDay_week()).split("/")[1]);
                    int count = Integer.parseInt(map.get("" + bandDataDetail.getDay_week()).split("/")[2]);
                    map.put("" + bandDataDetail.getDay_week(), (bandDataDetail.getValue1() + value1) + "/" + (bandDataDetail.getValue2() + value2) + "/" + (count + 1));
                } else {
                    map.put("" + bandDataDetail.getDay_week(), bandDataDetail.getValue1() + "/" + bandDataDetail.getValue2() + "/" + 1);
                }

                blood_high_blood = blood_high_blood + bandDataDetail.getValue1();
                blood_low_blood = blood_low_blood + bandDataDetail.getValue2();

                if (max_blood_high_pressure < bandDataDetail.getValue1()) {
                    max_blood_high_pressure = bandDataDetail.getValue1();
                }
                if (max_blood_low_pressure < bandDataDetail.getValue2()) {
                    max_blood_low_pressure = bandDataDetail.getValue2();
                }
                if (min_blood_high_pressure > bandDataDetail.getValue1()) {
                    min_blood_high_pressure = bandDataDetail.getValue2();
                }
                if (min_blood_high_pressure > bandDataDetail.getValue1()) {
                    min_blood_high_pressure = bandDataDetail.getValue2();
                }
            }
        }
        List<List<BarEntry>> dataList = new ArrayList<>();
        if (index != 0) {
            average_blood_pressure = (blood_high_blood / index) + "/" + (blood_low_blood / index);
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                dataList_key.add(Integer.valueOf(key));
            }
            Collections.sort(dataList_key);
            List<BarEntry> dataListOne = new ArrayList<>();
            List<BarEntry> dataListTwo = new ArrayList<>();
            for (int i = 0; i < dataList_key.size(); i++) {
                int key = dataList_key.get(i);
                int value1 = Integer.valueOf(map.get("" + key).split("/")[0]);
                int value2 = Integer.valueOf(map.get("" + key).split("/")[1]);
                int count = Integer.valueOf(map.get("" + key).split("/")[2]);
                BarEntry barEntryOne = new BarEntry(value1 / count, key);
                dataListOne.add(barEntryOne);
                BarEntry barEntryTwo = new BarEntry(value2 / count, key);
                dataListTwo.add(barEntryTwo);
            }
            dataList.add(dataListOne);
            dataList.add(dataListTwo);
        }
        return dataList;
    }

    private List<List<BarEntry>> getDayListMonth(String str_date) {
        dataList_key.clear();
        dataList_value.clear();
        map.clear();

        blood_low_blood = 0;
        blood_high_blood = 0;
        max_blood_low_pressure = 0;
        max_blood_high_pressure = 0;
        min_blood_low_pressure = 0;
        min_blood_high_pressure = 0;
        index = 0;

        for (int i = 0; i < dataDetailList.size(); i++) {
            BandDataDetail bandDataDetail = dataDetailList.get(i);
            String str_band_date = TimeUtils.DateToStringSimpleDateFormat(bandDataDetail.getmData(), TimeUtils.DATE_FORMAT_NO_DAY);
            if (str_date.equals(str_band_date)) {
                if (index == 0) {
                    min_blood_high_pressure = bandDataDetail.getValue1();
                    min_blood_low_pressure = bandDataDetail.getValue2();
                }
                index++;
                if (map.get("" + bandDataDetail.getDay_month()) != null) {
                    int value1 = Integer.parseInt(map.get("" + bandDataDetail.getDay_month()).split("/")[0]);
                    int value2 = Integer.parseInt(map.get("" + bandDataDetail.getDay_month()).split("/")[1]);
                    int count = Integer.parseInt(map.get("" + bandDataDetail.getDay_month()).split("/")[2]);
                    map.put("" + bandDataDetail.getDay_month(), (bandDataDetail.getValue1() + value1) + "/" + (bandDataDetail.getValue2() + value2) + "/" + (count + 1));
                } else {
                    map.put("" + bandDataDetail.getDay_month(), bandDataDetail.getValue1() + "/" + bandDataDetail.getValue2() + "/" + 1);
                }
                blood_high_blood = blood_high_blood + bandDataDetail.getValue1();
                blood_low_blood = blood_low_blood + bandDataDetail.getValue2();

                if (max_blood_high_pressure < bandDataDetail.getValue1()) {
                    max_blood_high_pressure = bandDataDetail.getValue1();
                }
                if (max_blood_low_pressure < bandDataDetail.getValue2()) {
                    max_blood_low_pressure = bandDataDetail.getValue2();
                }
                if (min_blood_high_pressure > bandDataDetail.getValue1()) {
                    min_blood_high_pressure = bandDataDetail.getValue2();
                }
                if (min_blood_high_pressure > bandDataDetail.getValue1()) {
                    min_blood_high_pressure = bandDataDetail.getValue2();
                }
            }
        }
        List<List<BarEntry>> dataList = new ArrayList<>();
        if (index != 0) {
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                dataList_key.add(Integer.valueOf(key));
            }
            Collections.sort(dataList_key);
            List<BarEntry> dataListOne = new ArrayList<>();
            List<BarEntry> dataListTwo = new ArrayList<>();
            for (int i = 0; i < dataList_key.size(); i++) {
                int key = dataList_key.get(i);
                int value1 = Integer.valueOf(map.get("" + key).split("/")[0]);
                int value2 = Integer.valueOf(map.get("" + key).split("/")[1]);
                int count = Integer.valueOf(map.get("" + key).split("/")[2]);
                BarEntry barEntryOne = new BarEntry(value1 / count, key - 1);
                dataListOne.add(barEntryOne);
                BarEntry barEntryTwo = new BarEntry(value2 / count, key - 1);
                dataListTwo.add(barEntryTwo);
            }
            dataList.add(dataListOne);
            dataList.add(dataListTwo);
            average_blood_pressure = (blood_high_blood / index) + "/" + (blood_low_blood / index);
        }
        return dataList;
    }
}
