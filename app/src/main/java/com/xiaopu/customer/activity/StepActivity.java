package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.reflect.TypeToken;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.BandDateAdapter;
import com.xiaopu.customer.data.BandDataDetail;
import com.xiaopu.customer.data.jsonresult.Walk;
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
 * Created by Administrator on 2017/9/5.
 */

public class StepActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = StepActivity.class.getSimpleName();

    private Context mContext;

    private AutoLocateHorizontalView horizontal_view_date;

    private BarChart mBarChart;

    private TextView tv_selected_day;

    private TextView tv_selected_week;

    private TextView tv_selected_month;

    private TextView tv_count_describe;

    private TextView tv_mileage_describe;

    private TextView tv_kcal_describe;

    private TextView tv_count;

    private TextView tv_mileage;

    private TextView tv_kcal;

    private View view_instructions_day;

    private View view_instructions_week;

    private View view_instructions_month;

    private RelativeLayout rl_step_target;

    private LinearLayout ll_step_tip;

    private ImageView iv_step_record;

    private ProgressBar pb_step;

    private TextView tv_step_tip_describe;

    private List<BarEntry> y_values;

    private List<String> dataList_day;

    private List<String> dataList_day_detail;

    private List<String> dataList_week;

    private List<String> dataList_week_detail;

    private List<String> dataList_month;

    private BandDateAdapter mAdapter;

    private View select_view;

    private Walk current_walk;

    private List<Integer> dataList_key;
    private List<Integer> dataList_value;
    private List<BandDataDetail> dataDetailList;

    private HashMap<String, String> map = new HashMap<>();

    private int max_step_count;

    private int max_step_kcal;

    private int average_step_count;

    private int average_step_kcal;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        initActionBar("步行");
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        horizontal_view_date = (AutoLocateHorizontalView) findViewById(R.id.horizontal_view_date);

        mBarChart = (BarChart) findViewById(R.id.barChat_step);

        tv_selected_day = (TextView) findViewById(R.id.tv_selected_day);

        tv_selected_week = (TextView) findViewById(R.id.tv_selected_week);

        tv_selected_month = (TextView) findViewById(R.id.tv_selected_month);

        tv_count_describe = (TextView) findViewById(R.id.tv_count_describe);

        tv_mileage_describe = (TextView) findViewById(R.id.tv_mileage_describe);

        tv_kcal_describe = (TextView) findViewById(R.id.tv_kcal_describe);

        tv_count = (TextView) findViewById(R.id.tv_step_count);

        tv_mileage = (TextView) findViewById(R.id.tv_step_mileage);

        tv_kcal = (TextView) findViewById(R.id.tv_step_kcal);

        view_instructions_day = findViewById(R.id.instructions_day);

        view_instructions_week = findViewById(R.id.instructions_week);

        view_instructions_month = findViewById(R.id.instructions_month);

        rl_step_target = (RelativeLayout) findViewById(R.id.rl_step_target);

        ll_step_tip = (LinearLayout) findViewById(R.id.ll_step_tip);

        iv_step_record = (ImageView) findViewById(R.id.iv_step_record);

        pb_step = (ProgressBar) findViewById(R.id.pb_step);

        tv_step_tip_describe = (TextView) findViewById(R.id.tv_step_tip_describe);

    }

    private void initData() {
        dataList_key = new ArrayList<>();
        dataList_value = new ArrayList<>();
        dataDetailList = new ArrayList<>();
        dataList_day = new ArrayList<>();
        dataList_day_detail = new ArrayList<>();
        dataList_week = new ArrayList<>();
        dataList_month = new ArrayList<>();
        dataList_week_detail = new ArrayList<>();
        y_values = new ArrayList<>();

        max_step_kcal = 0;
        max_step_count = 0;
        average_step_kcal = 0;
        average_step_count = 0;

        String str = ControlSave.read(mContext, ConstantUtils.BAND_STEP_DATA_DETAIL, "");
        String str_current = ControlSave.read(mContext, ConstantUtils.BAND_CURRENT_STEP, "");
        if (!TextUtils.isEmpty(str)) {
            dataDetailList = HttpUtils.gb.create().fromJson(str, new TypeToken<List<BandDataDetail>>() {
            }.getType());
        }
        if (!TextUtils.isEmpty(str_current)) {
            current_walk = HttpUtils.gb.create().fromJson(str_current, Walk.class);
            max_step_kcal = Integer.valueOf(current_walk.getCalorie());
            max_step_count = Integer.valueOf(current_walk.getWalkCount());
        }
        initStepText(0);
        dataList_day = DateUtils.getDaydate();
        dataList_week = DateUtils.getWeekdate();
        dataList_month = DateUtils.getMonthdate();
        dataList_week_detail = DateUtils.getWeekdateDatail();
        dataList_day_detail = DateUtils.getDaydateDetail();

        BarChartManager.setType(0);

        select_view = tv_selected_day;
        initAdapter(0);
        initInstructions();
        view_instructions_day.setVisibility(View.VISIBLE);
        rl_step_target.setVisibility(View.VISIBLE);
        ll_step_tip.setVisibility(View.GONE);
        tv_count_describe.setVisibility(View.INVISIBLE);
        tv_mileage_describe.setVisibility(View.INVISIBLE);
        tv_kcal_describe.setVisibility(View.INVISIBLE);

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
        iv_step_record.setOnClickListener(this);

        horizontal_view_date.setOnSelectedPositionChangedListener(new AutoLocateHorizontalView.OnSelectedPositionChangedListener() {
            @Override
            public void selectedPositionChanged(int pos) {
                switch (select_view.getId()) {
                    case R.id.tv_selected_day:
                        y_values.clear();
                        if (dataDetailList.size() != 0) {
                            y_values = getHourList(dataList_day_detail.get(pos));
                        }
                        initStepText(0);
                        BarChartManager.initBarChart(mContext, mBarChart, 0, y_values);
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
                        initStepText(1);
                        BarChartManager.initBarChart(mContext, mBarChart, 1, y_values);
                        break;
                    case R.id.tv_selected_month:
                        y_values.clear();
                        if (dataDetailList.size() != 0) {
                            y_values = getDayListMonth(dataList_month.get(pos));
                        }
                        initStepText(1);
                        BarChartManager.initBarChart(mContext, mBarChart, 2, y_values);
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_selected_day:
                if (select_view != tv_selected_day) {
                    select_view = v;
                    initInstructions();
                    view_instructions_day.setVisibility(View.VISIBLE);
                    rl_step_target.setVisibility(View.VISIBLE);
                    ll_step_tip.setVisibility(View.GONE);
                    tv_count_describe.setVisibility(View.INVISIBLE);
                    tv_mileage_describe.setVisibility(View.INVISIBLE);
                    tv_kcal_describe.setVisibility(View.INVISIBLE);
                    initAdapter(0);
                }
                break;
            case R.id.tv_selected_week:
                if (select_view != tv_selected_week) {
                    select_view = v;
                    initInstructions();
                    initTipDescribe();
                    view_instructions_week.setVisibility(View.VISIBLE);
                    rl_step_target.setVisibility(View.GONE);
                    ll_step_tip.setVisibility(View.VISIBLE);
                    tv_count_describe.setVisibility(View.VISIBLE);
                    tv_mileage_describe.setVisibility(View.VISIBLE);
                    tv_kcal_describe.setVisibility(View.VISIBLE);
                    y_values.clear();
                    initAdapter(1);
                }
                break;
            case R.id.tv_selected_month:
                if (select_view != tv_selected_month) {
                    select_view = v;
                    initInstructions();
                    initTipDescribe();
                    view_instructions_month.setVisibility(View.VISIBLE);
                    rl_step_target.setVisibility(View.GONE);
                    ll_step_tip.setVisibility(View.VISIBLE);
                    tv_count_describe.setVisibility(View.VISIBLE);
                    tv_mileage_describe.setVisibility(View.VISIBLE);
                    tv_kcal_describe.setVisibility(View.VISIBLE);
                    y_values.clear();
                    initAdapter(2);
                }
                break;
            case R.id.iv_step_record:
                startActivity(new Intent(mContext, StepEditActivity.class));
                break;
        }
    }

    private void initInstructions() {
        view_instructions_day.setVisibility(View.INVISIBLE);
        view_instructions_week.setVisibility(View.INVISIBLE);
        view_instructions_month.setVisibility(View.INVISIBLE);
    }

    private void initTipDescribe() {
        Random random = new Random();
        int s = random.nextInt(4);
        switch (s) {
            case 0:
                tv_step_tip_describe.setText(getResources().getString(R.string.band_step_tip_one));
                break;
            case 1:
                tv_step_tip_describe.setText(getResources().getString(R.string.band_step_tip_two));
                break;
            case 2:
                tv_step_tip_describe.setText(getResources().getString(R.string.band_step_tip_three));
                break;
            case 3:
                tv_step_tip_describe.setText(getResources().getString(R.string.band_step_tip_four));
                break;
            default:
                break;
        }
    }

    private void initStepText(int type) {
        if (type == 0) {
            if (max_step_count + max_step_kcal != 0) {
                tv_count.setText(String.valueOf(max_step_count));
                tv_kcal.setText(String.valueOf(max_step_kcal));
                tv_mileage.setText(String.format("%.1f", (float) max_step_count / 1400));

                String str_target = ControlSave.read(mContext, ConstantUtils.BAND_TARGET_STEP, "");
                int value;
                if (TextUtils.isEmpty(str_target)) {
                    value = (max_step_count * 100) / 8000;
                } else {
                    value = (max_step_count * 100) / Integer.valueOf(str_target);
                }
                pb_step.setProgress(value);

            } else {
                tv_count.setText("--");
                tv_kcal.setText("--");
                tv_mileage.setText("--");
                pb_step.setProgress(0);
            }

        } else if (type == 1) {
            if (average_step_count + average_step_kcal != 0) {
                tv_count.setText(String.valueOf(average_step_count));
                tv_kcal.setText(String.valueOf(average_step_kcal));
                tv_mileage.setText(String.format("%.1f", (float) average_step_count / 1400));
            } else {
                tv_count.setText("--");
                tv_kcal.setText("--");
                tv_mileage.setText("--");
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        initStepText(0);
    }

    private List<BarEntry> getHourList(String str_date) {
        dataList_key.clear();
        dataList_value.clear();
        max_step_count = 0;
        max_step_kcal = 0;
        map.clear();
        for (int i = 0; i < dataDetailList.size(); i++) {
            BandDataDetail bandDataDetail = dataDetailList.get(i);
            String str_band_date = TimeUtils.DateToString(bandDataDetail.getmData());
            if (str_date.equals(str_band_date)) {
                dataList_key.add(bandDataDetail.getHour());
                dataList_value.add(bandDataDetail.getValue1());
                if (max_step_count < bandDataDetail.getValue1()) {
                    max_step_count = bandDataDetail.getValue1();
                }
                if (max_step_kcal < bandDataDetail.getValue2()) {
                    max_step_kcal = bandDataDetail.getValue2();
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
                    barEntry = new BarEntry(dataList_value.get(i), dataList_key.get(i));
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
        average_step_count = 0;
        average_step_kcal = 0;
        map.clear();
        for (int i = 0; i < dataDetailList.size(); i++) {
            BandDataDetail bandDataDetail = dataDetailList.get(i);
            if (bandDataDetail.getmData().getTime() > date_start.getTime() && bandDataDetail.getmData().getTime() <= date_end.getTime()) {
                if (map.get("" + bandDataDetail.getDay_week()) != null) {
                    int value = Integer.valueOf(map.get("" + bandDataDetail.getDay_week()).split("/")[0]);
                    if (value < bandDataDetail.getValue1()) {
                        map.put("" + bandDataDetail.getDay_week(), bandDataDetail.getValue1() + "/" + bandDataDetail.getValue2());
                    }
                } else {
                    map.put("" + bandDataDetail.getDay_week(), bandDataDetail.getValue1() + "/" + bandDataDetail.getValue2());
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
                int value_step_count = Integer.valueOf(map.get("" + key).split("/")[0]);
                int value_step_kcal = Integer.valueOf(map.get("" + key).split("/")[1]);
                average_step_count = average_step_count + value_step_count;
                average_step_kcal = average_step_kcal + value_step_kcal;
                BarEntry barEntry = new BarEntry(value_step_count, key);
                dataList.add(barEntry);
            }
            average_step_kcal = average_step_kcal / dataList_key.size();
            average_step_count = average_step_count / dataList_key.size();
        }
        return dataList;
    }

    private List<BarEntry> getDayListMonth(String str_date) {
        dataList_key.clear();
        dataList_value.clear();
        average_step_kcal = 0;
        average_step_count = 0;
        map.clear();
        for (int i = 0; i < dataDetailList.size(); i++) {
            BandDataDetail bandDataDetail = dataDetailList.get(i);
            String str_band_date = TimeUtils.DateToStringSimpleDateFormat(bandDataDetail.getmData(), TimeUtils.DATE_FORMAT_NO_DAY);
            if (str_date.equals(str_band_date)) {
                if (map.get("" + bandDataDetail.getDay_month()) != null) {
                    int value = Integer.valueOf(map.get("" + bandDataDetail.getDay_month()).split("/")[0]);
                    if (value < bandDataDetail.getValue1()) {
                        map.put("" + bandDataDetail.getDay_month(), bandDataDetail.getValue1() + "/" + bandDataDetail.getValue2());
                    }
                } else {
                    map.put("" + bandDataDetail.getDay_month(), bandDataDetail.getValue1() + "/" + bandDataDetail.getValue2());
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
                int value_step_count = Integer.valueOf(map.get("" + key).split("/")[0]);
                int value_step_kcal = Integer.valueOf(map.get("" + key).split("/")[1]);
                average_step_count = average_step_count + value_step_count;
                average_step_kcal = average_step_kcal + value_step_kcal;
                BarEntry barEntry = new BarEntry(value_step_count, key - 1);
                dataList.add(barEntry);
            }
            average_step_kcal = average_step_kcal / dataList_key.size();
            average_step_count = average_step_count / dataList_key.size();
        }
        return dataList;
    }
}
