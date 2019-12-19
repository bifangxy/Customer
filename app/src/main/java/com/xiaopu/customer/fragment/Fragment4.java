package com.xiaopu.customer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xiaopu.customer.DetectionOfOneDayActivity;
import com.xiaopu.customer.R;
import com.xiaopu.customer.calendar.CalendarCard;
import com.xiaopu.customer.calendar.CalendarViewAdapter;
import com.xiaopu.customer.calendar.CustomDate;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.DateCountDetuil;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.TimeUtils;
import com.xiaopu.customer.utils.ToastUtils;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/7 0007.
 */
public class Fragment4 extends Fragment implements CalendarCard.OnCellClickListener {
    private View view;
    private TextView tv_title;
    private TextView tv_totoday;
    private TextView tv_year;

    private ViewPager mViewPager;
    private int mCurrentIndex = 498;
    private int selectView = 0;
    private CalendarCard[] mShowViews;
    private CalendarViewAdapter<CalendarCard> adapter;
    private SildeDirection mDirection = SildeDirection.NO_SILDE;

    private List<Integer> mIntList = new ArrayList<Integer>();//某月检测日期集合
    private List<Date> mDateList;

    //点击日期的回调方法
    @Override
    public void clickDate(CustomDate customDate) {
        if (mIntList.contains(customDate.day)) {
            Intent intent = new Intent(getActivity(), DetectionOfOneDayActivity.class);
            intent.putExtra("year", customDate.year);
            intent.putExtra("month", customDate.month);
            intent.putExtra("day", customDate.day);
            startActivity(intent);
        } else {
            ToastUtils.showErrorMsg("当天没有检测");
        }
    }

    @Override
    public void changeDate(CustomDate customDate) {
        monthText.setText(customDate.month + "月");
        tv_year.setText(customDate.year + "年");
        getData(String.valueOf(customDate.year), String.format("%02d", customDate.month));
    }

    enum SildeDirection {
        RIGHT, LEFT, NO_SILDE;
    }

    private ImageButton preImgBtn, nextImgBtn;
    private TextView monthText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_calendar, container, false);
        return view;
    }

    /**
     * activity的oncreate方法执行完之后（可以在这拿取其他fragment中控件并操作）
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initeView();
        initeData();
        setAdapter();
        setListener();
    }

    private void setListener() {
        preImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
            }
        });
        nextImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
        });
        tv_totoday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShowViews = adapter.getAllItems();
                mShowViews[selectView % mShowViews.length].returnToday();
            }
        });

    }

    private void setAdapter() {
    }

    private void initeData() {
//        tv_title.setText("健康日历");
        CalendarCard[] views = new CalendarCard[3];
        for (int i = 0; i < 3; i++) {
            views[i] = new CalendarCard(getActivity(), this);
        }
        adapter = new CalendarViewAdapter<>(views);
        setViewPager();
    }

    private void initeView() {
//        tv_title = (TextView) view.findViewById(R.id.tv_title);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_calendar);
        preImgBtn = (ImageButton) view.findViewById(R.id.btnPreMonth);
        nextImgBtn = (ImageButton) view.findViewById(R.id.btnNextMonth);
        monthText = (TextView) view.findViewById(R.id.tvCurrentMonth);
        tv_totoday = (TextView) view.findViewById(R.id.tv_totoday);
        tv_year = (TextView) view.findViewById(R.id.tv_year);
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

    /**
     * 计算方向
     *
     * @param arg0
     */
    private void measureDirection(int arg0) {

        if (arg0 > mCurrentIndex) {
            mDirection = SildeDirection.RIGHT;
        } else if (arg0 < mCurrentIndex) {
            mDirection = SildeDirection.LEFT;
        }
        mCurrentIndex = arg0;
    }

    // 更新日历视图
    private void updateCalendarView(int arg0) {
        mShowViews = adapter.getAllItems();
        if (mDirection == SildeDirection.RIGHT) {
            mShowViews[arg0 % mShowViews.length].rightSlide();
        } else if (mDirection == SildeDirection.LEFT) {
            mShowViews[arg0 % mShowViews.length].leftSlide();
        }
        mDirection = SildeDirection.NO_SILDE;
    }

    //拿到某个月的检测日期数据
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
}
