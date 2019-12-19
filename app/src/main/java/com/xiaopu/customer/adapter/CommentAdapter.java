package com.xiaopu.customer.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.data.jsonresult.CommentItem;
import com.xiaopu.customer.utils.TimeUtils;
import com.xiaopu.customer.utils.security.Des;

import java.util.List;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class CommentAdapter extends BaseAdapter {

    private Context mContext;

    private List<CommentItem> mList;

    private Resources resource;

    private Drawable drawable_satisfaction;

    private Drawable drawable_general;

    private Drawable drawable_unhappy;

    public CommentAdapter(List<CommentItem> list, Context context) {
        mList = list;
        mContext = context;
        resource = mContext.getResources();

        drawable_satisfaction = resource.getDrawable(R.mipmap.smile_press);
        drawable_satisfaction.setBounds(0, 0, (int) (drawable_satisfaction.getMinimumWidth() * 0.6), (int) (drawable_satisfaction.getMinimumHeight() * 0.6));

        drawable_general = resource.getDrawable(R.mipmap.general_press);
        drawable_general.setBounds(0, 0, (int) (drawable_general.getMinimumWidth() * 0.6), (int) (drawable_general.getMinimumHeight() * 0.6));

        drawable_unhappy = resource.getDrawable(R.mipmap.unhappy_press);
        drawable_unhappy.setBounds(0, 0, (int) (drawable_unhappy.getMinimumWidth() * 0.6), (int) (drawable_unhappy.getMinimumHeight() * 0.6));
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CommentItem getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_comment, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_customer_name = (TextView) convertView.findViewById(R.id.tv_customer_name);
            viewHolder.tv_customer_satisfaction = (TextView) convertView.findViewById(R.id.tv_customer_satisfaction);
            viewHolder.tv_comment_date = (TextView) convertView.findViewById(R.id.tv_comment_date);
            viewHolder.tv_comment_content = (TextView) convertView.findViewById(R.id.tv_comment_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_customer_name.setText(getItem(position).getNickname());
        viewHolder.tv_comment_date.setText(TimeUtils.DateToStringDatail(getItem(position).getCreateTime()));
        try {
            viewHolder.tv_comment_content.setText(Des.decode(getItem(position).getContent()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        initEvaluate(getItem(position).getDoctorService(), viewHolder.tv_customer_satisfaction);
        return convertView;
    }


    private void initEvaluate(int type, TextView textView) {

        switch (type) {
            case 3:
                textView.setText("满意");
                textView.setCompoundDrawables(drawable_satisfaction, null, null, null);
                break;
            case 2:
                textView.setText("一般");
                textView.setCompoundDrawables(drawable_general, null, null, null);
                break;
            case 1:
                textView.setText("不满意");
                textView.setCompoundDrawables(drawable_unhappy, null, null, null);
                break;
        }
    }


    protected class ViewHolder {
        private TextView tv_customer_name;
        private TextView tv_customer_satisfaction;
        private TextView tv_comment_date;
        private TextView tv_comment_content;
    }
}
