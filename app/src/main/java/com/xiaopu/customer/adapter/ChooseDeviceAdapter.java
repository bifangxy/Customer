package com.xiaopu.customer.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.data.EntityDevice;

import java.util.List;

/**
 * Created by Administrator on 2017/6/20.
 */

public class ChooseDeviceAdapter extends BaseAdapter {
    private Context mContext;

    private LayoutInflater mInflater;

    private List<EntityDevice> dataList;

    public ChooseDeviceAdapter(Context mContext, List<EntityDevice> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
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
            convertView = mInflater.inflate(R.layout.choose_device_item, null);
            holder = new ViewHolder();
            holder.rb_choose = (RadioButton) convertView.findViewById(R.id.rb_choose_device);
            holder.tv_device_name = (TextView) convertView.findViewById(R.id.tv_device_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        EntityDevice entityDevice = dataList.get(position);
        if (TextUtils.isEmpty(entityDevice.getNickName())) {
            holder.tv_device_name.setText(entityDevice.getDeviceName());
        } else {
            holder.tv_device_name.setText(entityDevice.getNickName());
        }
        return convertView;
    }


    private class ViewHolder {
        private RadioButton rb_choose;

        private TextView tv_device_name;
    }
}
