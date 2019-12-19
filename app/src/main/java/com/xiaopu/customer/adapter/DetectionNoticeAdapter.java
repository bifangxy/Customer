package com.xiaopu.customer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.activity.HealthAssessmentActivity;
import com.xiaopu.customer.activity.HeartActivity;
import com.xiaopu.customer.activity.OvulationActivity;
import com.xiaopu.customer.activity.PregnancyActivity;
import com.xiaopu.customer.activity.UrineActivity;
import com.xiaopu.customer.data.DetectionNoticeData;

import java.util.List;

/**
 * Created by Administrator on 2017/8/3.
 */

public class DetectionNoticeAdapter extends BaseAdapter {
    private Context mContext;

    private List<DetectionNoticeData> dataList;

    private LayoutInflater mInflater;


    public DetectionNoticeAdapter(Context mContext, List<DetectionNoticeData> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.detection_notice_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.iv_detection_icon = (ImageView) convertView.findViewById(R.id.iv_detection_icon);
            viewHolder.tv_detection_describe = (TextView) convertView.findViewById(R.id.tv_detection_describe);
            viewHolder.bt_start = (Button) convertView.findViewById(R.id.bt_start);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_detection_describe.setText(dataList.get(position).getDescribe());
        final Intent mIntent = new Intent();
        switch (dataList.get(position).getType()) {
            case 0:
                viewHolder.iv_detection_icon.setImageResource(R.mipmap.urine_icon);
                mIntent.setClass(mContext, UrineActivity.class);
                break;
            case 1:
                viewHolder.iv_detection_icon.setImageResource(R.mipmap.pregnancys_icon);
                mIntent.setClass(mContext, PregnancyActivity.class);
                break;
            case 2:
                viewHolder.iv_detection_icon.setImageResource(R.mipmap.ovulations_icon);
                mIntent.setClass(mContext, OvulationActivity.class);
                break;
            case 3:
                viewHolder.iv_detection_icon.setImageResource(R.mipmap.heart_icon);
                mIntent.setClass(mContext, HeartActivity.class);
                break;
            case 4:
                viewHolder.iv_detection_icon.setImageResource(R.mipmap.diabetes_icon);
                mIntent.putExtra("type", 1);
                mIntent.setClass(mContext, HealthAssessmentActivity.class);
                break;
            case 5:
                viewHolder.iv_detection_icon.setImageResource(R.mipmap.sub_health_icon);
                mIntent.putExtra("type", 2);
                mIntent.setClass(mContext, HealthAssessmentActivity.class);
                break;
            case 6:
                viewHolder.iv_detection_icon.setImageResource(R.mipmap.coronary_heart_icon);
                mIntent.putExtra("type", 3);
                mIntent.setClass(mContext, HealthAssessmentActivity.class);
                break;
            case 7:
                viewHolder.iv_detection_icon.setImageResource(R.mipmap.pregnancy_test_icon);
                mIntent.putExtra("type", 4);
                mIntent.setClass(mContext, HealthAssessmentActivity.class);
                break;
            case 8:
                viewHolder.iv_detection_icon.setImageResource(R.mipmap.hyperlipidemia_icon);
                mIntent.putExtra("type", 5);
                mIntent.setClass(mContext, HealthAssessmentActivity.class);
                break;
            case 9:
                viewHolder.iv_detection_icon.setImageResource(R.mipmap.hypertension_icon);
                mIntent.putExtra("type", 6);
                mIntent.setClass(mContext, HealthAssessmentActivity.class);
                break;
        }

        viewHolder.bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(mIntent);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        private ImageView iv_detection_icon;

        private TextView tv_detection_describe;

        private Button bt_start;
    }
}
