package com.xiaopu.customer.adapter;

/**
 * Created by Administrator on 2016/8/24 0024.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.activity.InfoContentActivity;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.jsonresult.Information;
import com.xiaopu.customer.utils.TimeUtils;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.security.Des;

import java.util.List;


/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class InfoAdapter extends BaseAdapter {

    private List<Information> mList;

    private Context mContext;

    private OnItemClickListener mListener;

    public InfoAdapter(List<Information> list, Context context) {
        mList = list;
        mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.information_item, null);
            viewHolder = new ViewHolder();

            viewHolder.infoContent = (TextView) convertView.findViewById(R.id.tv_info_content);
            viewHolder.infoLike = (TextView) convertView.findViewById(R.id.tv_collect_count);
            viewHolder.infoTitle = (TextView) convertView.findViewById(R.id.tv_info_title);
            viewHolder.infoType = (TextView) convertView.findViewById(R.id.tv_info_type);
            viewHolder.infoHeader = (ImageView) convertView.findViewById(R.id.iv_info_image);
            viewHolder.infoDate = (TextView) convertView.findViewById(R.id.tv_info_data);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String content = "";
        try {
            content = Des.decode(mList.get(position).getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewHolder.infoType.setText(mList.get(position).getTypeName());
        viewHolder.infoTitle.setText(mList.get(position).getTitle());
        viewHolder.infoLike.setText(mList.get(position).getCounts() + "");
        viewHolder.infoContent.setText(content);
        viewHolder.infoDate.setText(TimeUtils.DateToString(mList.get(position).getCreateTime()));
        ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + mList.get(position).getPhoto(), viewHolder.infoHeader, ApplicationXpClient.getmOptions(R.mipmap.info_account));

        final String finalContent = content;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Information information = mList.get(position);
                Intent mIntent = new Intent(mContext, InfoContentActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putInt("info_id", information.getId());
                mBundle.putString("link_url", information.getUrl());
                mBundle.putString("title", information.getTitle());
                mBundle.putString("content", finalContent);
                mBundle.putInt("position", position);
                mBundle.putString("image", HttpConstant.Url_ImageServer + information.getPhoto());
                mBundle.putInt("showType", 1);
                mIntent.putExtras(mBundle);
                ((Activity) mContext).startActivityForResult(mIntent, 0);
            }
        });
        return convertView;
    }

    protected class ViewHolder {
        private TextView infoTitle;
        private TextView infoContent;
        private ImageView infoHeader;
        private TextView infoType;
        private TextView infoLike;
        private TextView infoDate;
    }

    public interface OnItemClickListener {
        void click(View view, int position);
    }

}

