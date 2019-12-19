package com.xiaopu.customer.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaopu.customer.AppointmentActivity;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.jsonresult.DoctorDateTime;
import com.xiaopu.customer.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/9/14 0014.
 */
public class AppointmentDayAdapter extends BaseAdapter {
    private AppointmentActivity mContext;
    private List<Date> mDayList;
    private List<Integer> mWeekStatuList;
    private List<Integer> timeAppointmentStatusList;
    private List<Integer> isCheckList;
    private List<Integer> isMCheckList;
    private List<Integer> isACheckList;
    private List<DoctorDateTime> mDoctorDateTimeList;
    private List<String> mTimeList;
    private List<String> mAfteroonTimeList;
    private List<String> mMorningTimeList;
    private TimesAdapter mMAdapter;
    private TimesAdapter mAAdapter;
    private ViewHolder viewHolder;
    int count = 0;
    public AppointmentDayAdapter(AppointmentActivity context, List<DoctorDateTime> doctorDateTimesList) {
        mContext = context;
        mDoctorDateTimeList = doctorDateTimesList;
        mDayList = new ArrayList<>();
        isCheckList = new ArrayList<>();
        mWeekStatuList = new ArrayList<>();
        timeAppointmentStatusList = new ArrayList<>();
        for(int i = 0; i< mDoctorDateTimeList.size(); i++){
            if(!mDayList.contains(mDoctorDateTimeList.get(i).getScheduleDate())){
                mDayList.add(count, mDoctorDateTimeList.get(i).getScheduleDate());
                mWeekStatuList.add(count, mDoctorDateTimeList.get(i).getWeekNum());
                count++;
            }
        }
        for(int i=0;i<mDayList.size();i++){
            if(i == 0){
                isCheckList.add(i, 1);
            }else{
                isCheckList.add(i, 0);
            }
        }
    }

    @Override
    public int getCount() {
        Log.d("kwk", mDayList.size()+"size");
        return mDayList.size();
    }

    @Override
    public Date getItem(int i) {
        return mDayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_appointment_day, null);
            viewHolder = new ViewHolder();
            viewHolder.tvWeek = (TextView) convertView.findViewById(R.id.week);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.month_day);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvWeek.setText(initWeek(mWeekStatuList.get(i)));

        viewHolder.tvDate.setText(TimeUtils.DateToString(mDayList.get(i)));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<mDayList.size();i++){
                    isCheckList.set(i, 0);
                }
                isCheckList.set(i, 1);
                initTimes(TimeUtils.DateToString(mDayList.get(i)));
                mContext.mAppointmentMessage.setAppointmentDate(TimeUtils.DateToString(mDayList.get(i)));
                notifyDataSetChanged();
            }
        });

        if(isCheckList.get(i) == 1){
            viewHolder.tvWeek.setTextColor(Color.parseColor("#0091db"));
            viewHolder.tvDate.setTextColor(Color.parseColor("#0091db"));
            notifyDataSetChanged();
        }else{
            viewHolder.tvDate.setTextColor(Color.parseColor("#808080"));
            viewHolder.tvWeek.setTextColor(Color.parseColor("#808080"));
        }

        return convertView;
    }



    public void initTimes(String date){
        int num = 0;
        mTimeList = new ArrayList<>();
        mAfteroonTimeList = new ArrayList<>();
        mMorningTimeList = new ArrayList<>();
        isMCheckList = new ArrayList<>();
        isACheckList = new ArrayList<>();
        for(int i = 0; i < mDoctorDateTimeList.size(); i++){
            if(TimeUtils.DateToString(mDoctorDateTimeList.get(i).getScheduleDate()).equals(date)){
                mTimeList.add(num, mDoctorDateTimeList.get(i).getStartTime()+"-"+mDoctorDateTimeList.get(i).getEndTime());
                timeAppointmentStatusList.add(num, mDoctorDateTimeList.get(i).getTimeAppointmentStatus());
                num++;
            }
        }

        for(int i = 0; i< mTimeList.size(); i++){
            String[] dateArr = mTimeList.get(i).split(":");
             if(Integer.parseInt(dateArr[0]) < 13){
                mMorningTimeList.add(i,mTimeList.get(i));
                 isMCheckList.add(i, 0);
            }else{
                mAfteroonTimeList.add(i-mMorningTimeList.size(),mTimeList.get(i));
                 isACheckList.add(i-mMorningTimeList.size(), 0);
            }
        }
        mMAdapter = new TimesAdapter(mContext, mMorningTimeList, timeAppointmentStatusList, isMCheckList);
        mContext.listMorningOfDay.setAdapter(mMAdapter);
        mContext.listMorningOfDay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for(int j=0;j<isMCheckList.size();j++){
                    isMCheckList.set(j, 0);
                    isACheckList.set(j, 0);
                }
                isMCheckList.set(i, 1);
                mContext.mAppointmentMessage.setTimeStamp(mTimeList.get(i));
                mMAdapter.notifyDataSetChanged();
                mAAdapter.notifyDataSetChanged();
            }
        });
        mAAdapter = new TimesAdapter(mContext, mAfteroonTimeList, timeAppointmentStatusList, isACheckList);
        mContext.listAfterroonOfDay.setAdapter(mAAdapter);
        mContext.listAfterroonOfDay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for(int j=0;j<isACheckList.size();j++){
                    isACheckList.set(j, 0);
                    isMCheckList.set(j, 0);
                }
                isACheckList.set(i, 1);
                mContext.mAppointmentMessage.setTimeStamp(mTimeList.get(i+isMCheckList.size()));
                mMAdapter.notifyDataSetChanged();
                mAAdapter.notifyDataSetChanged();
            }
        });

    }

    private String initWeek(int status){
        String strWeek = "";
        switch (status){
            case 0:
                strWeek = "周日";
                break;
            case 1:
                strWeek = "周一";
                break;
            case 2:
                strWeek = "周二";
                break;
            case 3:
                strWeek = "周三";
                break;
            case 4:
                strWeek = "周四";
                break;
            case 5:
                strWeek = "周五";
                break;
            case 6:
                strWeek = "周六";
                break;
        }
        return strWeek;
    }


    protected class ViewHolder {
        private TextView tvWeek;
        private TextView tvDate;
    }
}
