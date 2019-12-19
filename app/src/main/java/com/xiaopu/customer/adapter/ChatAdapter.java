package com.xiaopu.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.jsonresult.DoctorMessage;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.security.Des;

import java.util.List;

/**
 * Created by Administrator on 2017/7/18.
 */

public class ChatAdapter extends BaseAdapter {
    private Context mContext;

    private LayoutInflater mInflater;

    private List<DoctorMessage> dataList;

    private String doctorAvatar;

    public ChatAdapter(Context mContext, List<DoctorMessage> dataList, String doctorAvatar) {
        this.mContext = mContext;
        this.dataList = dataList;
        this.doctorAvatar = doctorAvatar;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.liaotian_item, parent, false);
            holder.rlcustomer = (RelativeLayout) convertView.findViewById(R.id.rl_customer);
            holder.rldoctor = (RelativeLayout) convertView.findViewById(R.id.rl_docotor);
            holder.ivcustomer = (ImageView) convertView.findViewById(R.id.iv_customer);
            holder.ivdoctor = (ImageView) convertView.findViewById(R.id.iv_docotor);
            holder.tvcustomer = (TextView) convertView.findViewById(R.id.tv_customer);
            holder.tvdoctor = (TextView) convertView.findViewById(R.id.tv_docotor);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DoctorMessage doctorMessage = dataList.get(position);
        switch (doctorMessage.getMsgType()) {
            case 0://用户留言
                holder.rldoctor.setVisibility(View.GONE);
                holder.rlcustomer.setVisibility(View.VISIBLE);
                try {
                    holder.tvcustomer.setText(Des.decode(doctorMessage.getContent()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ImageLoader.getInstance().displayImage(HttpConstant.Url_Server + ApplicationXpClient.userInfoResult.getAvatar(), holder.ivcustomer, ApplicationXpClient.options);
                break;
            case 1://医师留言
                holder.rldoctor.setVisibility(View.VISIBLE);
                holder.rlcustomer.setVisibility(View.GONE);
                try {
                    holder.tvdoctor.setText(Des.decode(doctorMessage.getContent()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

               /* if (hasLocal) {
                    holder.image_Msg.setLocalImageBitmap(ImageCheckoutUtil.getLoacalBitmap(imageSrc),
                            res);
                } else {
                    holder.image_Msg.load(imageIconUrl, res, R.mipmap.cygs_cs);
                }*/
                ImageLoader.getInstance().displayImage(HttpConstant.Url_Server + doctorAvatar, holder.ivdoctor, ApplicationXpClient.options);
                break;
        }
        return convertView;
    }

    private class ViewHolder {
        RelativeLayout rlcustomer;

        RelativeLayout rldoctor;

        ImageView ivcustomer;

        ImageView ivdoctor;

        TextView tvcustomer;

        TextView tvdoctor;
    }
}
