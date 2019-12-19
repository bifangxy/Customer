package com.xiaopu.customer.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.data.EntityDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/24.
 */

public class BleDeviceAdapter extends BaseAdapter {
    private Context mContext;

    private List<EntityDevice> dataList = new ArrayList<>();

    private LayoutInflater mInflater;

    public BleDeviceAdapter(Context mContext, List<EntityDevice> dataList) {
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        holder = new ViewHolder();
        view = mInflater.inflate(R.layout.device_item, viewGroup, false);
        holder.tv_name = view.findViewById(R.id.tv_device_name);
        holder.tv_toilet_title = view.findViewById(R.id.tv_toilet_title);
        holder.iv_device_image = view.findViewById(R.id.iv_device_image);
        view.setTag(holder);
        EntityDevice device = dataList.get(position);
        holder.tv_name.setText(device.getDeviceName());

        if (device.getType() == 1) {
            holder.iv_device_image.setImageResource(R.mipmap.toilet_icon);
        } else if (device.getType() == 2) {
            holder.iv_device_image.setImageResource(R.mipmap.icon_band);
        }

        if (TextUtils.isEmpty(device.getNickName())) {
            if (device.getType() == 1 && device.getDeviceName().equals("Pooai-08")) {
                holder.tv_toilet_title.setText("小普云健康智能马桶盖");
            } else if (device.getType() == 1 && device.getDeviceName().equals("Pooai-07")) {
                holder.tv_toilet_title.setText("小普云健康智能马桶盖魅力版");
            } else if (device.getType() == 2) {
                holder.tv_toilet_title.setText("小普智能手环");
            }
        } else {
            holder.tv_toilet_title.setText(device.getNickName());
        }
        return view;
    }


    private class ViewHolder {
        private TextView tv_address;

        private TextView tv_toilet_title;

        private TextView tv_name;

        private ImageView iv_device_image;
    }
}
