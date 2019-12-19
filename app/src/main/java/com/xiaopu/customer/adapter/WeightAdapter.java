package com.xiaopu.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.data.jsonresult.DetectionOvulation;
import com.xiaopu.customer.data.jsonresult.DetectionPregnant;
import com.xiaopu.customer.data.jsonresult.DetectionWeight;
import com.xiaopu.customer.utils.TimeUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class WeightAdapter extends BaseAdapter {

    private List<DetectionWeight> mList;
    private Context mContext;
    private ViewHolder viewHolder;
    public WeightAdapter(List<DetectionWeight> list, Context context) {
        mList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public DetectionWeight getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_weight_detection, null);
            viewHolder = new ViewHolder();
            /*viewHolder.detectionName = (TextView) convertView.findViewById(R.id.detection_name);*/
            viewHolder.recentData = (TextView) convertView.findViewById(R.id.recent_data);
            viewHolder.weightKG = (TextView) convertView.findViewById(R.id.weight_kg);
            viewHolder.weightBmi = (TextView) convertView.findViewById(R.id.weight_bmi);
            viewHolder.weightResult = (TextView) convertView.findViewById(R.id.weight_result);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.recentData.setText(TimeUtils.DateToStringDatail(getItem(position).getCreateTime()));
        viewHolder.weightResult.setText(getItem(position).getWeightResult());
        viewHolder.weightKG.setText(getItem(position).getWeight()+"KG");
        viewHolder.weightBmi.setText(getItem(position).getBmi()+"mbi");
        return convertView;
    }

    protected class ViewHolder {
        /*private TextView detectionName;*/
        private TextView recentData;
        private TextView weightBmi;
        private TextView weightKG;
        private TextView weightResult;
    }
}
