package com.xiaopu.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.data.SimpleDetectionResult;

import java.util.List;

/**
 * Created by Administrator on 2017/6/5.
 */

public class SimpleResultAdapter extends BaseAdapter {

    private Context mContext;

    private List<SimpleDetectionResult> datalist;

    private LayoutInflater mInflater;

    public SimpleResultAdapter(Context mContext, List<SimpleDetectionResult> datalist) {
        this.mContext = mContext;
        this.datalist = datalist;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.simple_result_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_detection_date);
            viewHolder.tv_describe = (TextView) convertView.findViewById(R.id.tv_detection_describe);
            viewHolder.tv_result = (TextView) convertView.findViewById(R.id.tv_detection_result);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SimpleDetectionResult simpleDetectionResult = datalist.get(position);
        viewHolder.tv_date.setText(simpleDetectionResult.getDetectionTime());
        viewHolder.tv_result.setText(simpleDetectionResult.getDetectionResult());
        viewHolder.tv_describe.setText(simpleDetectionResult.getResult());

        return convertView;
    }

    private class ViewHolder {
        private TextView tv_date;

        private TextView tv_result;

        private TextView tv_describe;
    }
}
