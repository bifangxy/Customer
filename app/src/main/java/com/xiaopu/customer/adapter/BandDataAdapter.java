package com.xiaopu.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.data.BandData;

import java.util.List;

/**
 * Created by Administrator on 2017/9/19.
 */

public class BandDataAdapter extends BaseAdapter {
    private Context mContext;

    private List<BandData> dataList;

    private LayoutInflater mInflater;

    public BandDataAdapter(Context mContext, List<BandData> dataList) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_band_data, null);
            holder = new ViewHolder();
            holder.iv_item_band = (ImageView) convertView.findViewById(R.id.iv_item_band);
            holder.tv_item_band_time = (TextView) convertView.findViewById(R.id.tv_item_band_time);
            holder.tv_item_band_value = (TextView) convertView.findViewById(R.id.tv_item_band_value);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BandData bandData = dataList.get(position);
        if (position == 0) {
            switch (bandData.getType()) {
                case 0:
                    holder.iv_item_band.setImageResource(R.mipmap.icon_home_heart_rate);
                    break;
                case 1:
                    holder.iv_item_band.setImageResource(R.mipmap.icon_home_blood_pressure);
                    break;
                case 2:
                    holder.iv_item_band.setImageResource(R.mipmap.icon_home_blood_oxygen);
                    break;
            }
        } else {
            holder.iv_item_band.setImageResource(R.mipmap.red_point);
        }
        holder.tv_item_band_time.setText(bandData.getDetection_time());
        holder.tv_item_band_value.setText(bandData.getDetection_result());
        return convertView;
    }

    private class ViewHolder {
        private ImageView iv_item_band;

        private TextView tv_item_band_time;

        private TextView tv_item_band_value;
    }
}
