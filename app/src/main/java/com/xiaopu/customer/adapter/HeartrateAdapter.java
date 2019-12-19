package com.xiaopu.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.data.jsonresult.DetectionHeartrate;
import com.xiaopu.customer.utils.TimeUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class HeartrateAdapter extends BaseAdapter {

    private List<DetectionHeartrate> mList;

    private Context mContext;

    private LayoutInflater mInflater;

    public HeartrateAdapter(List<DetectionHeartrate> list, Context context) {
        mList = list;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_hear_detection, null);
            viewHolder = new ViewHolder();
            viewHolder.recentData = (TextView) convertView.findViewById(R.id.recent_data);
            viewHolder.detectionState = (TextView) convertView.findViewById(R.id.text_detection_state);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.recentData.setText(TimeUtils.DateToStringDatail(mList.get(position).getCreateTime()));
        viewHolder.detectionState.setText(mList.get(position).getHeartRate() + "");
        return convertView;
    }


    protected class ViewHolder {
        private TextView recentData;
        private TextView detectionState;
    }
}
