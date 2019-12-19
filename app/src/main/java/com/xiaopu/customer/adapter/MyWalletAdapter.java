package com.xiaopu.customer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.data.jsonresult.UserAccount;
import com.xiaopu.customer.utils.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/24.
 */

public class MyWalletAdapter extends BaseAdapter {
    private LayoutInflater mInflater;// 动态布局映射

    private Context mContext;

    private List<UserAccount> mUserAccounts;

    private String[] types;

    public MyWalletAdapter(Context context, List<UserAccount> mUserAccounts, String[] type) {
        this.mContext = context;
        this.mUserAccounts = mUserAccounts;
        this.types = type;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mUserAccounts.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.mywallet_item, viewGroup, false);
            viewHolder.tv_type = (TextView) view.findViewById(R.id.tv_expense_type);
            viewHolder.tv_date = (TextView) view.findViewById(R.id.tv_expense_date);
            viewHolder.tv_amount = (TextView) view.findViewById(R.id.tv_expense_amount);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        UserAccount userAccount = mUserAccounts.get(i);

        if (userAccount.getHandleType() == 1) {
            viewHolder.tv_type.setText("充值类型:" + userAccount.getPayWay());
        } else {
            viewHolder.tv_type.setText("消费类型:" + types[userAccount.getHandleType()]);
        }
        Date date = new Date(userAccount.getCreateTime());
        viewHolder.tv_date.setText(DateUtils.getYMDHMS(date));
        if (userAccount.getHandleType() == 0 || userAccount.getHandleType() == 1) {
            viewHolder.tv_amount.setText("+" + userAccount.getAmount() + "小普币");
            viewHolder.tv_amount.setTextColor(Color.rgb(255, 0, 0));
        } else {
            viewHolder.tv_amount.setText("-" + userAccount.getAmount() + "小普币");
            viewHolder.tv_amount.setTextColor(Color.rgb(0, 145, 219));
        }
        return view;
    }

    private class ViewHolder {
        private TextView tv_type;
        private TextView tv_date;
        private TextView tv_amount;
    }
}
