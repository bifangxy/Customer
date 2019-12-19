package com.xiaopu.customer.adapter;

/**
 * Created by Administrator on 2016/8/24 0024.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xiaopu.customer.R;

import java.util.List;


/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class HealthAssessmentAdapter extends BaseAdapter {

    private List<String> mList;

    private Context mContext;

    private int value = 0;

    public HealthAssessmentAdapter(Context context, List<String> list) {
        mList = list;
        mContext = context;
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

    public int getValue() {
        return value;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.health_assessment_item, null);
            viewHolder = new ViewHolder();

            viewHolder.tv_assessment_question = (TextView) convertView.findViewById(R.id.tv_assessment_question);
            viewHolder.rg_assessment = (RadioGroup) convertView.findViewById(R.id.rg_assessment);
            viewHolder.rb_assessment_no = (RadioButton) convertView.findViewById(R.id.rb_assessment_no);
            viewHolder.rb_assessment_right = (RadioButton) convertView.findViewById(R.id.rb_assessment_right);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_assessment_question.setText(mList.get(position));

        viewHolder.rb_assessment_right.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    value = value + 1;
                } else {
                    value = value - 1;
                }
            }
        });

        return convertView;
    }

    protected class ViewHolder {
        private TextView tv_assessment_question;

        private RadioGroup rg_assessment;

        private RadioButton rb_assessment_no;

        private RadioButton rb_assessment_right;

    }


}

