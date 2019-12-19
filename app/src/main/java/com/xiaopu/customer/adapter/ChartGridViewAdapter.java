package com.xiaopu.customer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaopu.customer.R;

/**
 * Created by Administrator on 2017/7/27.
 */

public class ChartGridViewAdapter extends BaseAdapter {
    private Context mContext;

    private LayoutInflater mInflater;

    private String[] datalist;

    private int select_position = 0;


    public ChartGridViewAdapter(Context mContext, String[] datalist) {
        this.mContext = mContext;
        this.datalist = datalist;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return datalist.length;
    }

    @Override
    public Object getItem(int position) {
        return datalist[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.chart_item, parent, false);
            holder = new ViewHolder();
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_title.setText(datalist[position]);
        if (position == select_position) {
            holder.tv_title.setSelected(true);
            holder.tv_title.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            holder.tv_title.setSelected(false);
            holder.tv_title.setTextColor(mContext.getResources().getColor(R.color.gray));
        }
        return convertView;
    }

    public void setSelectPositon(int positon) {
        select_position = positon;
    }

    private class ViewHolder {
        private TextView tv_title;
    }
}
