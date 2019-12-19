package com.xiaopu.customer.adapter;

/**
 * Created by Administrator on 2016/8/24 0024.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.data.Area;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class AreaAdapter extends BaseAdapter {

    private List<Area> areaList;

    private Context mContext;


    public AreaAdapter(Context context, List<Area> list) {
        areaList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return areaList.size();
    }

    @Override
    public Object getItem(int position) {
        return areaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_area, null);
            viewHolder = new ViewHolder();

            viewHolder.tvArea = convertView.findViewById(R.id.tv_area);
            viewHolder.tvAreaNum = convertView.findViewById(R.id.tv_area_num);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Area area = areaList.get(position);
        viewHolder.tvArea.setText(area.getAreaName());
        viewHolder.tvAreaNum.setText(area.getAreaNum());
        return convertView;
    }

    protected class ViewHolder {
        private TextView tvArea;
        private TextView tvAreaNum;
    }
}

