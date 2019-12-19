package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.DetectionOfOneDayActivity;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.DetectionNoticeAdapter;
import com.xiaopu.customer.calendar.CalendarCard;
import com.xiaopu.customer.calendar.CalendarViewAdapter;
import com.xiaopu.customer.calendar.CustomDate;
import com.xiaopu.customer.data.DetectionNoticeData;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.DateCountDetuil;
import com.xiaopu.customer.data.jsonresult.LastDate;
import com.xiaopu.customer.utils.DateUtils;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.TimeUtils;
import com.xiaopu.customer.utils.ToastUtils;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.ListViewForScrollview;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/26.
 */

public class CalendarActivity extends ActivityBase implements CalendarCard.OnCellClickListener {
    private static final String LOG_TAG = CalendarActivity.class.getSimpleName();

    private Context mContext;

    private TextView tv_totoday;

    private TextView tv_year;

    private ImageButton preImgBtn;

    private ImageButton nextImgBtn;

    private TextView monthText;

    private ViewPager mViewPager;

    private ListViewForScrollview lv_detection_notice;

    private int mCurrentIndex = 498;

    private int selectView = 0;

    private CalendarCard[] mShowViews;

    private SildeDirection mDirection = SildeDirection.NO_SILDE;

    private CalendarViewAdapter<CalendarCard> adapter;

    private List<Integer> mIntList = new ArrayList<Integer>();//某月检测日期集合

    private List<Date> mDateList;

    private MyClickListener mClick;

    private List<DetectionNoticeData> dataList;

    private DetectionNoticeAdapter mAdapter;

    enum SildeDirection {
        RIGHT, LEFT, NO_SILDE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mContext = this;
        initActionBar("健康日历");
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.vp_calendar);
        preImgBtn = (ImageButton) findViewById(R.id.btnPreMonth);
        nextImgBtn = (ImageButton) findViewById(R.id.btnNextMonth);
        monthText = (TextView) findViewById(R.id.tvCurrentMonth);
        tv_totoday = (TextView) findViewById(R.id.tv_totoday);
        tv_year = (TextView) findViewById(R.id.tv_year);
        lv_detection_notice = (ListViewForScrollview) findViewById(R.id.lv_detection_notice);
    }

    private void initData() {
        dataList = new ArrayList<>();
        mAdapter = new DetectionNoticeAdapter(mContext, dataList);
        lv_detection_notice.setAdapter(mAdapter);

        mClick = new MyClickListener();
        CalendarCard[] views = new CalendarCard[3];
        for (int i = 0; i < 3; i++) {
            views[i] = new CalendarCard(mContext, this);
        }
        adapter = new CalendarViewAdapter<>(views);
        setViewPager();
    }

    private void getLastDate() {
        dataList.clear();

        HttpUtils.getInstantce().postNoHead(HttpConstant.Url_getLastDetectionDate, new HttpCallBack<LastDate>() {
            @Override
            public void onSuccess(HttpResult result) {
                LastDate lastDate = (LastDate) result.getData();
                //尿检
                if (lastDate.getUrineDate().equals("0")) {
                    DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                    detectionNoticeData.setLastData("0");
                    detectionNoticeData.setType(0);
                    detectionNoticeData.setDescribe("您还没进行过尿检，是否开始检测？");
                    dataList.add(detectionNoticeData);
                } else {
                    int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getUrineDate()), new Date());
                    if (day >= 7) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("" + day);
                        detectionNoticeData.setDescribe("距离上次尿检已过" + day + "天,是否开始检测？");
                        detectionNoticeData.setType(0);
                        dataList.add(detectionNoticeData);

                    }
                }
                //孕检
                if (ApplicationXpClient.userInfoResult.getSex() == null) {
                    if (lastDate.getPregnantDate().equals("0")) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("0");
                        detectionNoticeData.setDescribe("您还没进行过孕检，是否开始检测？");
                        detectionNoticeData.setType(1);
                        dataList.add(detectionNoticeData);
                    } else {
                        int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getPregnantDate()), new Date());
                        if (day >= 7) {
                            DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                            detectionNoticeData.setLastData("" + day);
                            detectionNoticeData.setType(1);
                            detectionNoticeData.setDescribe("距离上次孕检已过" + day + "天,是否开始检测？");
                            dataList.add(detectionNoticeData);
                        }
                    }

                    if (lastDate.getOvulationDate().equals("0")) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("0");
                        detectionNoticeData.setType(2);
                        detectionNoticeData.setDescribe("您还没进行过排卵检测，是否开始检测？");
                        dataList.add(detectionNoticeData);
                    } else {
                        int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getOvulationDate()), new Date());
                        if (day >= 7) {
                            DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                            detectionNoticeData.setLastData("" + day);
                            detectionNoticeData.setType(2);
                            detectionNoticeData.setDescribe("距离上次排卵检测已过" + day + "天,是否开始检测？");
                            dataList.add(detectionNoticeData);
                        }
                    }
                } else {
                    if (ApplicationXpClient.userInfoResult.getSex() == 0) {
                        if (lastDate.getPregnantDate().equals("0")) {
                            DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                            detectionNoticeData.setLastData("0");
                            detectionNoticeData.setDescribe("您还没进行过孕检，是否开始检测？");
                            detectionNoticeData.setType(1);
                            dataList.add(detectionNoticeData);
                        } else {
                            int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getPregnantDate()), new Date());
                            if (day >= 7) {
                                DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                                detectionNoticeData.setLastData("" + day);
                                detectionNoticeData.setType(1);
                                detectionNoticeData.setDescribe("距离上次孕检已过" + day + "天,是否开始检测？");
                                dataList.add(detectionNoticeData);
                            }
                        }

                        if (lastDate.getOvulationDate().equals("0")) {
                            DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                            detectionNoticeData.setLastData("0");
                            detectionNoticeData.setType(2);
                            detectionNoticeData.setDescribe("您还没进行过排卵检测，是否开始检测？");
                            dataList.add(detectionNoticeData);
                        } else {
                            int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getOvulationDate()), new Date());
                            if (day >= 7) {
                                DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                                detectionNoticeData.setLastData("" + day);
                                detectionNoticeData.setType(2);
                                detectionNoticeData.setDescribe("距离上次排卵检测已过" + day + "天,是否开始检测？");
                                dataList.add(detectionNoticeData);
                            }
                        }
                    }
                }
                if (lastDate.getHeartDate().equals("0")) {
                    DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                    detectionNoticeData.setLastData("0");
                    detectionNoticeData.setType(3);
                    detectionNoticeData.setDescribe("您还没进行过心率检测，是否开始检测？");
                    dataList.add(detectionNoticeData);
                } else {
                    int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getHeartDate()), new Date());
                    if (day >= 7) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("" + day);
                        detectionNoticeData.setType(3);
                        detectionNoticeData.setDescribe("距离上次心率检测已过" + day + "天,是否开始检测？");
                        dataList.add(detectionNoticeData);
                    }
                }

                if (lastDate.getDmDate().equals("0")) {
                    DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                    detectionNoticeData.setLastData("0");
                    detectionNoticeData.setType(4);
                    detectionNoticeData.setDescribe("您还没进行过糖尿病测评，是否开始测评？");
                    dataList.add(detectionNoticeData);
                } else {
                    int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getDmDate()), new Date());
                    if (day >= 7) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("" + day);
                        detectionNoticeData.setType(4);
                        detectionNoticeData.setDescribe("距离上糖尿病测评已过" + day + "天,是否开始新一轮测评？");
                        dataList.add(detectionNoticeData);
                    }
                }
                if (lastDate.getSubhealthyDate().equals("0")) {
                    DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                    detectionNoticeData.setLastData("0");
                    detectionNoticeData.setType(5);
                    detectionNoticeData.setDescribe("您还没进行过亚健康测评，是否开始测评？");
                    dataList.add(detectionNoticeData);
                } else {
                    int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getSubhealthyDate()), new Date());
                    if (day >= 7) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("" + day);
                        detectionNoticeData.setType(5);
                        detectionNoticeData.setDescribe("距离上亚健康测评已过" + day + "天,是否开始新一轮测评？");
                        dataList.add(detectionNoticeData);
                    }
                }

                if (lastDate.getCoronaryDate().equals("0")) {
                    DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                    detectionNoticeData.setLastData("0");
                    detectionNoticeData.setType(6);
                    detectionNoticeData.setDescribe("您还没进行过冠心病测评，是否开始测评？");
                    dataList.add(detectionNoticeData);
                } else {
                    int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getCoronaryDate()), new Date());
                    if (day >= 7) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("" + day);
                        detectionNoticeData.setType(6);
                        detectionNoticeData.setDescribe("距离上次冠心病测评已过" + day + "天,是否开始新一轮测评？");
                        dataList.add(detectionNoticeData);
                    }
                }

                if (lastDate.getPregnancyTestDate().equals("0")) {
                    DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                    detectionNoticeData.setLastData("0");
                    detectionNoticeData.setType(7);
                    detectionNoticeData.setDescribe("您还没进行过孕前自测测评，是否开始测评？");
                    dataList.add(detectionNoticeData);
                } else {
                    int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getPregnancyTestDate()), new Date());
                    if (day >= 7) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("" + day);
                        detectionNoticeData.setType(7);
                        detectionNoticeData.setDescribe("距离上孕前自测测评已过" + day + "天,是否开始新一轮测评？");
                        dataList.add(detectionNoticeData);
                    }
                }
                if (lastDate.getHyperlipoidemiaDate().equals("0")) {
                    DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                    detectionNoticeData.setLastData("0");
                    detectionNoticeData.setType(8);
                    detectionNoticeData.setDescribe("您还没进行过高血脂测评，是否开始测评？");
                    dataList.add(detectionNoticeData);
                } else {
                    int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getHyperlipoidemiaDate()), new Date());
                    if (day >= 7) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("" + day);
                        detectionNoticeData.setType(8);
                        detectionNoticeData.setDescribe("距离上高血脂测评已过" + day + "天,是否开始新一轮测评？");
                        dataList.add(detectionNoticeData);
                    }
                }

                if (lastDate.getHypertensionDate().equals("0")) {
                    DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                    detectionNoticeData.setLastData("0");
                    detectionNoticeData.setType(9);
                    detectionNoticeData.setDescribe("您还没进行过高血压测评，是否开始测评？");
                    dataList.add(detectionNoticeData);
                } else {
                    int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getHypertensionDate()), new Date());
                    if (day >= 7) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("" + day);
                        detectionNoticeData.setType(9);
                        detectionNoticeData.setDescribe("距离上高血压测评已过" + day + "天,是否开始新一轮测评？");
                        dataList.add(detectionNoticeData);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(String msg) {

            }
        });
       /* HttpUtils.getInstantce().getLastDetectionDate(new HttpConstant.SampleJsonResultListener<Feedback<LastDate>>() {
            @Override
            public void onSuccess(Feedback<LastDate> jsonData) {
                LastDate lastDate = jsonData.getData();
                //尿检
                if (lastDate.getUrineDate().equals("0")) {
                    DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                    detectionNoticeData.setLastData("0");
                    detectionNoticeData.setType(0);
                    detectionNoticeData.setDescribe("您还没进行过尿检，是否开始检测？");
                    dataList.add(detectionNoticeData);
                } else {
                    int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getUrineDate()), new Date());
                    if (day >= 7) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("" + day);
                        detectionNoticeData.setDescribe("距离上次尿检已过" + day + "天,是否开始检测？");
                        detectionNoticeData.setType(0);
                        dataList.add(detectionNoticeData);

                    }
                }
                //孕检
                if (ApplicationXpClient.userInfoResult.getSex() == null) {
                    if (lastDate.getPregnantDate().equals("0")) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("0");
                        detectionNoticeData.setDescribe("您还没进行过孕检，是否开始检测？");
                        detectionNoticeData.setType(1);
                        dataList.add(detectionNoticeData);
                    } else {
                        int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getPregnantDate()), new Date());
                        if (day >= 7) {
                            DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                            detectionNoticeData.setLastData("" + day);
                            detectionNoticeData.setType(1);
                            detectionNoticeData.setDescribe("距离上次孕检已过" + day + "天,是否开始检测？");
                            dataList.add(detectionNoticeData);
                        }
                    }

                    if (lastDate.getOvulationDate().equals("0")) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("0");
                        detectionNoticeData.setType(2);
                        detectionNoticeData.setDescribe("您还没进行过排卵检测，是否开始检测？");
                        dataList.add(detectionNoticeData);
                    } else {
                        int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getOvulationDate()), new Date());
                        if (day >= 7) {
                            DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                            detectionNoticeData.setLastData("" + day);
                            detectionNoticeData.setType(2);
                            detectionNoticeData.setDescribe("距离上次排卵检测已过" + day + "天,是否开始检测？");
                            dataList.add(detectionNoticeData);
                        }
                    }
                } else {
                    if (ApplicationXpClient.userInfoResult.getSex() == 0) {
                        if (lastDate.getPregnantDate().equals("0")) {
                            DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                            detectionNoticeData.setLastData("0");
                            detectionNoticeData.setDescribe("您还没进行过孕检，是否开始检测？");
                            detectionNoticeData.setType(1);
                            dataList.add(detectionNoticeData);
                        } else {
                            int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getPregnantDate()), new Date());
                            if (day >= 7) {
                                DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                                detectionNoticeData.setLastData("" + day);
                                detectionNoticeData.setType(1);
                                detectionNoticeData.setDescribe("距离上次孕检已过" + day + "天,是否开始检测？");
                                dataList.add(detectionNoticeData);
                            }
                        }

                        if (lastDate.getOvulationDate().equals("0")) {
                            DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                            detectionNoticeData.setLastData("0");
                            detectionNoticeData.setType(2);
                            detectionNoticeData.setDescribe("您还没进行过排卵检测，是否开始检测？");
                            dataList.add(detectionNoticeData);
                        } else {
                            int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getOvulationDate()), new Date());
                            if (day >= 7) {
                                DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                                detectionNoticeData.setLastData("" + day);
                                detectionNoticeData.setType(2);
                                detectionNoticeData.setDescribe("距离上次排卵检测已过" + day + "天,是否开始检测？");
                                dataList.add(detectionNoticeData);
                            }
                        }
                    }
                }
                if (lastDate.getHeartDate().equals("0")) {
                    DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                    detectionNoticeData.setLastData("0");
                    detectionNoticeData.setType(3);
                    detectionNoticeData.setDescribe("您还没进行过心率检测，是否开始检测？");
                    dataList.add(detectionNoticeData);
                } else {
                    int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getHeartDate()), new Date());
                    if (day >= 7) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("" + day);
                        detectionNoticeData.setType(3);
                        detectionNoticeData.setDescribe("距离上次心率检测已过" + day + "天,是否开始检测？");
                        dataList.add(detectionNoticeData);
                    }
                }

                if (lastDate.getDmDate().equals("0")) {
                    DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                    detectionNoticeData.setLastData("0");
                    detectionNoticeData.setType(4);
                    detectionNoticeData.setDescribe("您还没进行过糖尿病测评，是否开始测评？");
                    dataList.add(detectionNoticeData);
                } else {
                    int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getDmDate()), new Date());
                    if (day >= 7) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("" + day);
                        detectionNoticeData.setType(4);
                        detectionNoticeData.setDescribe("距离上糖尿病测评已过" + day + "天,是否开始新一轮测评？");
                        dataList.add(detectionNoticeData);
                    }
                }
                if (lastDate.getSubhealthyDate().equals("0")) {
                    DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                    detectionNoticeData.setLastData("0");
                    detectionNoticeData.setType(5);
                    detectionNoticeData.setDescribe("您还没进行过亚健康测评，是否开始测评？");
                    dataList.add(detectionNoticeData);
                } else {
                    int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getSubhealthyDate()), new Date());
                    if (day >= 7) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("" + day);
                        detectionNoticeData.setType(5);
                        detectionNoticeData.setDescribe("距离上亚健康测评已过" + day + "天,是否开始新一轮测评？");
                        dataList.add(detectionNoticeData);
                    }
                }

                if (lastDate.getCoronaryDate().equals("0")) {
                    DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                    detectionNoticeData.setLastData("0");
                    detectionNoticeData.setType(6);
                    detectionNoticeData.setDescribe("您还没进行过冠心病测评，是否开始测评？");
                    dataList.add(detectionNoticeData);
                } else {
                    int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getCoronaryDate()), new Date());
                    if (day >= 7) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("" + day);
                        detectionNoticeData.setType(6);
                        detectionNoticeData.setDescribe("距离上次冠心病测评已过" + day + "天,是否开始新一轮测评？");
                        dataList.add(detectionNoticeData);
                    }
                }

                if (lastDate.getPregnancyTestDate().equals("0")) {
                    DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                    detectionNoticeData.setLastData("0");
                    detectionNoticeData.setType(7);
                    detectionNoticeData.setDescribe("您还没进行过孕前自测测评，是否开始测评？");
                    dataList.add(detectionNoticeData);
                } else {
                    int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getPregnancyTestDate()), new Date());
                    if (day >= 7) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("" + day);
                        detectionNoticeData.setType(7);
                        detectionNoticeData.setDescribe("距离上孕前自测测评已过" + day + "天,是否开始新一轮测评？");
                        dataList.add(detectionNoticeData);
                    }
                }
                if (lastDate.getHyperlipoidemiaDate().equals("0")) {
                    DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                    detectionNoticeData.setLastData("0");
                    detectionNoticeData.setType(8);
                    detectionNoticeData.setDescribe("您还没进行过高血脂测评，是否开始测评？");
                    dataList.add(detectionNoticeData);
                } else {
                    int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getHyperlipoidemiaDate()), new Date());
                    if (day >= 7) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("" + day);
                        detectionNoticeData.setType(8);
                        detectionNoticeData.setDescribe("距离上高血脂测评已过" + day + "天,是否开始新一轮测评？");
                        dataList.add(detectionNoticeData);
                    }
                }

                if (lastDate.getHypertensionDate().equals("0")) {
                    DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                    detectionNoticeData.setLastData("0");
                    detectionNoticeData.setType(9);
                    detectionNoticeData.setDescribe("您还没进行过高血压测评，是否开始测评？");
                    dataList.add(detectionNoticeData);
                } else {
                    int day = DateUtils.getDays(TimeUtils.StringToDateSimpeDateFormat(lastDate.getHypertensionDate()), new Date());
                    if (day >= 7) {
                        DetectionNoticeData detectionNoticeData = new DetectionNoticeData();
                        detectionNoticeData.setLastData("" + day);
                        detectionNoticeData.setType(9);
                        detectionNoticeData.setDescribe("距离上高血压测评已过" + day + "天,是否开始新一轮测评？");
                        dataList.add(detectionNoticeData);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Feedback<LastDate> jsonData) {
                T.showShort("失败");
            }
        });*/
    }

    private void initListener() {
        nextImgBtn.setOnClickListener(mClick);
        preImgBtn.setOnClickListener(mClick);
        tv_totoday.setOnClickListener(mClick);
    }

    private void setViewPager() {
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(498);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                selectView = position;
                measureDirection(position);
                updateCalendarView(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    @Override
    public void clickDate(CustomDate date) {
        if (mIntList.contains(date.day)) {
            Intent intent = new Intent(mContext, DetectionOfOneDayActivity.class);
            intent.putExtra("year", date.year);
            intent.putExtra("month", date.month);
            intent.putExtra("day", date.day);
            startActivity(intent);
        } else {
            ToastUtils.showErrorMsg("当天没有检测");
        }
    }

    @Override
    public void changeDate(CustomDate date) {
        monthText.setText(date.month + "月");
        tv_year.setText(date.year + "年");
        getData(String.valueOf(date.year), String.format("%02d", date.month));
    }

    private void getData(String year, String month) {
        mIntList.clear();

        Map<String, Object> maps = new HashMap<>();
        maps.put("year", year);
        maps.put("month", month);
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getDateByMonth, new HttpCallBack<DateCountDetuil>() {
            @Override
            public void onSuccess(HttpResult result) {
                DateCountDetuil dateCountDetuil = (DateCountDetuil) result.getData();
                mShowViews = adapter.getAllItems();
                mDateList = dateCountDetuil.getDateList();
                if (mDateList != null) {
                    for (int i = 0; i < mDateList.size(); i++) {
                        String tool = TimeUtils.DateToString(mDateList.get(i));
                        mIntList.add(Integer.parseInt(tool.split("-")[2]));
                    }
                    mShowViews[selectView % mShowViews.length].updateCheck(mIntList);
                }
            }

            @Override
            public void onFail(String msg) {
                if (msg != null)
                    T.showShort(msg);
                else
                    T.showShort(getString(R.string.internet_error));
            }
        });

/*
        HttpUtils.getInstantce().getDateByMonth(year, month, new HttpConstant.SampleJsonResultListener<Feedback<DateCountDetuil>>() {
            @Override
            public void onSuccess(Feedback<DateCountDetuil> jsonData) {
                mShowViews = adapter.getAllItems();
                mDateList = jsonData.getData().getDateList();
                if (mDateList != null) {
                    for (int i = 0; i < mDateList.size(); i++) {
                        String tool = TimeUtils.DateToString(mDateList.get(i));
                        mIntList.add(Integer.parseInt(tool.split("-")[2]));
                    }
                    mShowViews[selectView % mShowViews.length].updateCheck(mIntList);
                }
            }

            @Override
            public void onFailure(Feedback<DateCountDetuil> jsonData) {
                if (jsonData == null) {
                    ToastUtils.showWarnMsg("网络不通，请检查网络。");
                } else {
                    ToastUtils.showErrorMsg(jsonData.getMsg());
                }
            }
        });
*/
    }

    private void measureDirection(int arg0) {

        if (arg0 > mCurrentIndex) {
            mDirection = SildeDirection.RIGHT;
        } else if (arg0 < mCurrentIndex) {
            mDirection = SildeDirection.LEFT;
        }
        mCurrentIndex = arg0;
    }

    private void updateCalendarView(int arg0) {
        mShowViews = adapter.getAllItems();
        if (mDirection == SildeDirection.RIGHT) {
            mShowViews[arg0 % mShowViews.length].rightSlide();
        } else if (mDirection == SildeDirection.LEFT) {
            mShowViews[arg0 % mShowViews.length].leftSlide();
        }
        mDirection = SildeDirection.NO_SILDE;
    }

    private class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnNextMonth:
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                    break;
                case R.id.btnPreMonth:
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                    break;
                case R.id.tv_totoday:
                    mShowViews = adapter.getAllItems();
                    mShowViews[selectView % mShowViews.length].returnToday();
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLastDate();
    }
}
