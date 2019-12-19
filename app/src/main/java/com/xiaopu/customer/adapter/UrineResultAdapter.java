package com.xiaopu.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.data.Detection_Urine_item;

import java.util.List;


/**
 * Created by Xieying on 2016/7/5.
 * 功能：尿检每一项Adapter
 */
public class UrineResultAdapter extends BaseAdapter {

    private List<Detection_Urine_item> detection_urine_items;

    private Context mContext;

    private LayoutInflater mInflater;

    public UrineResultAdapter(Context context, List<Detection_Urine_item> detection_urine_items) {
        this.mContext = context;
        this.detection_urine_items = detection_urine_items;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return detection_urine_items.size();
    }

    @Override
    public Object getItem(int position) {
        return detection_urine_items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.detection_urine_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.urine_name);
            viewHolder.tv_result = (TextView) convertView.findViewById(R.id.urine_result);
            viewHolder.tv_real_result = (TextView) convertView.findViewById(R.id.urine_real_result);
            viewHolder.rl_urine_item = (RelativeLayout) convertView.findViewById(R.id.rl_urine_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Detection_Urine_item detection_urine_item = detection_urine_items.get(position);
        viewHolder.tv_name.setText(detection_urine_item.getDetection_name());
        viewHolder.tv_result.setText(detection_urine_item.getDetection_result());
        viewHolder.tv_real_result.setText(detection_urine_item.getDetection_real_result());
        int resId = mContext.getResources().getIdentifier("detection_urine_" + (position + 1),
                "mipmap", mContext.getPackageName());
        viewHolder.rl_urine_item.setBackgroundResource(resId);
        return convertView;
    }

    private class ViewHolder {
        private TextView tv_name;
        private TextView tv_result;
        private TextView tv_real_result;
        private RelativeLayout rl_urine_item;
    }
}