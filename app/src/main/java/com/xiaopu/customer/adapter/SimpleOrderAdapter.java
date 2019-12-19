package com.xiaopu.customer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.SimpleDoctorOrder;
import com.xiaopu.customer.utils.http.HttpConstant;

import java.util.List;

/**
 * Created by Administrator on 2017/6/7.
 */

public class SimpleOrderAdapter extends BaseAdapter {

    private Context mContext;

    private List<SimpleDoctorOrder> datalist;

    private LayoutInflater mInflater;


    public SimpleOrderAdapter(Context mContext, List<SimpleDoctorOrder> datalist) {
        this.mContext = mContext;
        this.datalist = datalist;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getViewTypeCount() {
        return 12;
    }

    @Override
    public int getItemViewType(int position) {
        return datalist.get(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
//        if (convertView == null) {
        convertView = mInflater.inflate(R.layout.simple_order_item, null);
        viewHolder = new ViewHolder();
        viewHolder.iv_doctor_head = (ImageView) convertView.findViewById(R.id.iv_doctor_head);
        viewHolder.tv_doctor_name = (TextView) convertView.findViewById(R.id.tv_doctor_name);
        viewHolder.tv_doctor_level = (TextView) convertView.findViewById(R.id.tv_doctor_level);
        viewHolder.tv_doctor_department = (TextView) convertView.findViewById(R.id.tv_doctor_department);
        viewHolder.tv_order_sn = (TextView) convertView.findViewById(R.id.tv_order_sn);
        viewHolder.tv_order_date = (TextView) convertView.findViewById(R.id.tv_order_date);
        viewHolder.tv_order_state = (TextView) convertView.findViewById(R.id.tv_order_state);
        viewHolder.tv_unread_message_count = (TextView) convertView.findViewById(R.id.tv_unread_message_count);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
        SimpleDoctorOrder simpleDoctorOrder = datalist.get(position);
        ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + simpleDoctorOrder.getDoctor_avatar(), viewHolder.iv_doctor_head, ApplicationXpClient.options);
        viewHolder.tv_doctor_name.setText(simpleDoctorOrder.getDoctor_name());
        viewHolder.tv_doctor_level.setText(simpleDoctorOrder.getDoctor_level());
        viewHolder.tv_doctor_department.setText(simpleDoctorOrder.getDoctor_department());
        viewHolder.tv_order_sn.setText("订单编号:" + simpleDoctorOrder.getOrder_sn());
        viewHolder.tv_order_date.setText(simpleDoctorOrder.getOrder_date());
        viewHolder.tv_order_state.setText(simpleDoctorOrder.getOrder_state());
        if (simpleDoctorOrder.getOrder_category() == 0) {
            viewHolder.tv_unread_message_count.setVisibility(View.GONE);
        } else if (simpleDoctorOrder.getOrder_category() == 2) {
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation("doctor_" + simpleDoctorOrder.getDoctor_id());
            if (conversation != null) {
                int count = conversation.getUnreadMsgCount();
                if (count == 0) {
                    viewHolder.tv_unread_message_count.setVisibility(View.GONE);
                } else {
                    viewHolder.tv_unread_message_count.setVisibility(View.VISIBLE);
                    viewHolder.tv_unread_message_count.setText("" + count);
                }
            }
        }
        return convertView;
    }


    private class ViewHolder {
        private ImageView iv_doctor_head;

        private TextView tv_doctor_name;

        private TextView tv_doctor_level;

        private TextView tv_doctor_department;

        private TextView tv_order_sn;

        private TextView tv_order_date;

        private TextView tv_order_state;

        private TextView tv_unread_message_count;
    }
}
