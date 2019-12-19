package com.xiaopu.customer.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.xiaopu.customer.R;
import com.xiaopu.customer.view.CustomMarkerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/14.
 */

public class BarChartManager {

    private static BarChart mBarChart;

    private static BarDataSet mBarDataSet;

    private static BarDataSet mBarDataSetTwo;

    //0 步数 1 睡眠 2 心率 3  血压 4 血氧
    private static int chartType;

    /**
     * 单个柱状图
     *
     * @param mContext
     * @param barChart
     * @param type
     * @param y_values
     */
    public static void initBarChart(Context mContext, BarChart barChart, int type, List<BarEntry> y_values) {
        mBarChart = barChart;
        mBarChart.clear();
        mBarChart.setDescriptionColor(Color.WHITE);
        mBarChart.setNoDataText("暂无检测数据");
        if (y_values.size() != 0) {
            mBarDataSet = new BarDataSet(y_values, "步数");
            mBarDataSet.setColor(Color.WHITE);//设置第一组数据颜色
            mBarDataSet.setDrawValues(false);
            mBarDataSet.setHighLightColor(Color.parseColor("#dbebed"));
            List<String> x_values = new ArrayList<>();
            switch (type) {
                case 0:
                    for (int i = 0; i < 25; i++) {
                        if (i < 10) {
                            x_values.add("0" + i + ":00");
                        } else {
                            x_values.add(i + ":00");
                        }
                    }
                    break;
                case 1:
                    x_values.add("周日");
                    x_values.add("周一");
                    x_values.add("周二");
                    x_values.add("周三");
                    x_values.add("周四");
                    x_values.add("周五");
                    x_values.add("周六");
                    break;
                case 2:
                    for (int i = 1; i < 32; i++) {
                        x_values.add("" + i);
                    }
                    break;
            }
            initDataStyle(mContext, type, mBarChart, x_values);
            BarData bardata = new BarData(x_values, mBarDataSet);
            mBarChart.setData(bardata);
        }
    }


    public static void initDoubleBarChart(Context mContext, BarChart barChart, int type, List<BarEntry> y_values, List<BarEntry> y_two_values) {
        mBarChart = barChart;
        mBarChart.clear();
        mBarChart.setDescriptionColor(Color.WHITE);
        mBarChart.setNoDataText("暂无检测数据");
        if (y_values.size() != 0) {
            mBarDataSet = new BarDataSet(y_values, "");
            mBarDataSet.setColor(mContext.getResources().getColor(R.color.band_chart_blood_one));//设置第一组数据颜色
            mBarDataSet.setDrawValues(false);
            mBarDataSet.setHighLightColor(mContext.getResources().getColor(R.color.band_chart_blood_three));

            mBarDataSetTwo = new BarDataSet(y_two_values, "");
            mBarDataSetTwo.setColor(mContext.getResources().getColor(R.color.band_chart_blood_two));
            mBarDataSetTwo.setDrawValues(false);
            mBarDataSet.setHighLightColor(mContext.getResources().getColor(R.color.band_chart_blood_four));

            List<String> x_values = new ArrayList<>();
            switch (type) {
                case 0:
                    for (int i = 0; i < 25; i++) {
                        if (i < 10) {
                            x_values.add("0" + i + ":00");
                        } else {
                            x_values.add(i + ":00");
                        }
                    }
                    break;
                case 1:
                    x_values.add("周日");
                    x_values.add("周一");
                    x_values.add("周二");
                    x_values.add("周三");
                    x_values.add("周四");
                    x_values.add("周五");
                    x_values.add("周六");
                    break;
                case 2:
                    for (int i = 1; i < 32; i++) {
                        x_values.add("" + i);
                    }
                    break;
            }
            initDataStyle(mContext, type, mBarChart, x_values);
            List<IBarDataSet> dataSetList = new ArrayList<>();
            dataSetList.add(mBarDataSet);
            dataSetList.add(mBarDataSetTwo);
            BarData bardata = new BarData(x_values, dataSetList);
            mBarChart.setData(bardata);
        }
    }

    public static void changeData(List<BarEntry> dataList) {
        mBarDataSet.clear();
        for (int i = 0; i < dataList.size(); i++) {
            mBarDataSet.addEntry(dataList.get(i));
        }
        mBarChart.notifyDataSetChanged(); // let the chart know it's data changed
        mBarChart.invalidate();
        mBarChart.animateXY(1000, 2000);//设置动画
    }


    private static void initDataStyle(Context context, int type, BarChart mBarChart, List<String> datalist) {
        mBarChart.getLegend().setEnabled(false);
        mBarChart.setScaleEnabled(false);
        mBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        mBarChart.getXAxis().setDrawGridLines(false);//不显示网格
        if (type == 0) {
            mBarChart.getXAxis().setLabelsToSkip(3);
        } else {
            mBarChart.getXAxis().setLabelsToSkip(0);
        }
        mBarChart.getXAxis().setTextColor(Color.WHITE);
        mBarChart.getXAxis().setGridLineWidth(0.5f);
        switch (type) {
            case 0:
                mBarChart.getXAxis().setTextSize(10);
                break;
            case 1:
                mBarChart.getXAxis().setTextSize(10);
                break;
            case 2:
                mBarChart.getXAxis().setTextSize(6);
                break;
        }
        mBarChart.getAxisRight().setEnabled(false);//右侧不显示Y轴
        mBarChart.getAxisLeft().setEnabled(false);
        mBarChart.setDescription("");//设置描述
        mBarChart.animateXY(0, 1000);//设置动画
        mBarChart.getAxisLeft().setAxisMinValue(0);
        CustomMarkerView cmv = new CustomMarkerView(context, R.layout.custom_view);
        cmv.setDataList(datalist);
        cmv.setType(chartType);
        mBarChart.setMarkerView(cmv);
    }

    public static int getType() {
        return chartType;
    }

    public static void setType(int type) {
        BarChartManager.chartType = type;
    }
}
