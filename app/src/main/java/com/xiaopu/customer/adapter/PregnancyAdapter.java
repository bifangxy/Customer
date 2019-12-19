package com.xiaopu.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.data.jsonresult.DetectionPregnant;
import com.xiaopu.customer.utils.TimeUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class PregnancyAdapter extends BaseAdapter {

    private List<DetectionPregnant> mList;
    private Context mContext;
    private ViewHolder viewHolder;
    public PregnancyAdapter(List<DetectionPregnant> list, Context context) {
        mList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public DetectionPregnant getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pre_detection, null);
            viewHolder = new ViewHolder();
            /*viewHolder.detectionName = (TextView) convertView.findViewById(R.id.detection_name);*/
            viewHolder.recentData = (TextView) convertView.findViewById(R.id.recent_data);
            viewHolder.imgState = (ImageView) convertView.findViewById(R.id.img_detection_state);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.recentData.setText(TimeUtils.DateToStringDatail(getItem(position).getCreateTime()));
        setResultState(getItem(position).getPre());
        return convertView;
    }

    /**
     * 设置孕检结果不同状态
     * 怀孕，0未怀孕，1已怀孕，2无效
     */
    private void setResultState(int state){
        switch (state){
            case 0:
                viewHolder.imgState.setImageResource(R.mipmap.pregnantbg_no);
                break;
            case 1:
                viewHolder.imgState.setImageResource(R.mipmap.pregnantbg_yes);
                break;
            case 2:
                break;
        }

    }

    protected class ViewHolder {
        /*private TextView detectionName;*/
        private TextView recentData;
        private ImageView imgState;
    }
}
