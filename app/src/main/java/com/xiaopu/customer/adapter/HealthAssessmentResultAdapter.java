package com.xiaopu.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.HealthAssessmentResultData;
import com.xiaopu.customer.data.jsonresult.DoctorMessage;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.view.CircleProgressView;

import java.util.List;

/**
 * Created by Administrator on 2017/7/18.
 */

public class HealthAssessmentResultAdapter extends BaseAdapter {
    private Context mContext;

    private LayoutInflater mInflater;

    private List<HealthAssessmentResultData> dataList;

    private String doctorAvatar;

    public HealthAssessmentResultAdapter(Context context, List<HealthAssessmentResultData> dataList) {
        this.mContext = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.health_assessment_result_item, parent, false);
            holder.tv_health_assessment_title = (TextView) convertView.findViewById(R.id.tv_health_assessment_title);
            holder.tv_health_assessment_day = (TextView) convertView.findViewById(R.id.tv_health_assessment_day);
            holder.cpv_health_assessment_date = (CircleProgressView) convertView.findViewById(R.id.cpv_health_assessment_date);
            holder.tv_health_assessment_result = (TextView) convertView.findViewById(R.id.tv_health_assessment_result);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HealthAssessmentResultData healthAssessmentResultData = dataList.get(position);
        holder.tv_health_assessment_title.setText(healthAssessmentResultData.getTitle());

        if (healthAssessmentResultData.getDay() >= 30) {
            holder.tv_health_assessment_day.setText(healthAssessmentResultData.getDay() + "天，请重新进行评测");
            holder.cpv_health_assessment_date.setVisibility(View.GONE);
        } else {
            holder.tv_health_assessment_day.setText(healthAssessmentResultData.getDay() + "天");
            holder.cpv_health_assessment_date.setProgress(healthAssessmentResultData.getDay());
        }


//        holder.cpv_health_assessment_date.setMaxProgress(30);

        holder.tv_health_assessment_result.setText(healthAssessmentResultData.getResult());

        return convertView;
    }

    private class ViewHolder {
        private TextView tv_health_assessment_title;

        private CircleProgressView cpv_health_assessment_date;

        private TextView tv_health_assessment_day;

        private TextView tv_health_assessment_result;

    }
}
