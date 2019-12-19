package com.xiaopu.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaopu.customer.R;

import java.util.List;

/**
 * Created by Xieying on 2016/1/5 0005.
 * 功能：popipWindows的适配器
 */
public class GroupAdapter extends BaseAdapter {

    private Context context;

    private List<String> list;

    public GroupAdapter(Context context, List<String> list) {

        this.context = context;
        this.list = list;

    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {


        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.group_item, null);
            holder = new ViewHolder();

            convertView.setTag(holder);

            holder.groupItem = (TextView) convertView.findViewById(R.id.group_item);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.groupItem.setText(list.get(position));

        return convertView;
    }

    static class ViewHolder {
        TextView groupItem;
    }

}
