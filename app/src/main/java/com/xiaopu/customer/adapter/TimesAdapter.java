package com.xiaopu.customer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaopu.customer.R;

import java.util.List;

/**
 * Created by Administrator on 2016/9/14 0014.
 */
public class TimesAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mTimesList;
    private List<Integer> mStatus;
    private List<Integer> isCheckList;
    public ViewHolder viewHolder;

    public TimesAdapter(Context context, List<String> timesList, List<Integer> status, List<Integer> checkList) {
        mContext = context;
        mTimesList = timesList;
        mStatus = status;
        isCheckList = checkList;
    }

    @Override
    public int getCount() {
        Log.d("kwk", mTimesList.size() + "timesize");
        return mTimesList.size();
    }

    @Override
    public String getItem(int i) {
        return mTimesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_times_of_day, null);
            viewHolder = new ViewHolder();
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTime.setText(mTimesList.get(i));

        if (mStatus.get(i) == 0) {
            viewHolder.tvTime.setTextColor(Color.BLACK);
        }

        if (isCheckList.get(i) == 1) {
            viewHolder.tvTime.setTextColor(Color.parseColor("#0091db"));
        }


        /*if(mStatus.get(i) == 0){
            viewHolder.tvTime.setTextColor(Color.BLACK);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int i=0; i<mTimesList.size(); i++){
                        isCheckList.set(i, 0);
                    }
                    isCheckList.set(i, 1);
                }

            });
        }

        if(isCheckList.get(i) == 1){
            viewHolder.tvTime.setTextColor(Color.parseColor("#0091db"));
            notifyDataSetChanged();
        }*/

        return convertView;
    }


    protected class ViewHolder {
        public TextView tvTime;
    }
}
