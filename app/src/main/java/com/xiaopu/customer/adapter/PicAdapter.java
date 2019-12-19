package com.xiaopu.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.R;

import java.util.List;

/**
 * Created by Administrator on 2017/12/26.
 */

public class PicAdapter extends RecyclerView.Adapter<PicAdapter.MyViewHolder> {

    private Context mContext;

    private List<String> dataList;

    private LayoutInflater mInflater;

    private OnPicAdapterClickListener mClickListener;

    public PicAdapter(Context context, List<String> dataList) {
        this.mContext = context;
        this.dataList = dataList;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setmClickListener(OnPicAdapterClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MyViewHolder holder = new MyViewHolder(mInflater.inflate(R.layout.pic_item, parent,
                false));
        return holder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        if (dataList.get(position).equals("")) {
            holder.iv_message_delete.setVisibility(View.GONE);
            holder.iv_message_pic.setImageResource(R.mipmap.icon_camera);
        } else {
            holder.iv_message_delete.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(dataList.get(position), holder.iv_message_pic);
            holder.iv_message_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.deleteClick(position);
                }
            });
        }
        /*if (position == getItemCount() - 1) {
            holder.iv_message_delete.setVisibility(View.GONE);
            holder.iv_message_pic.setImageResource(R.mipmap.icon_camera);

        } else {
            holder.iv_message_delete.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(dataList.get(position), holder.iv_message_pic);
            holder.iv_message_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.deleteClick(position);
                }
            });
        }*/

        holder.iv_message_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataList.get(position).equals("")) {
                    mClickListener.picClick(10);
                } else {
                    mClickListener.picClick(position);
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_message_pic;

        private ImageView iv_message_delete;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_message_pic = (ImageView) itemView.findViewById(R.id.iv_message_pic);
            iv_message_delete = (ImageView) itemView.findViewById(R.id.iv_message_pic_delete);
        }
    }


    public interface OnPicAdapterClickListener {
        void picClick(int position);

        void deleteClick(int position);
    }
}
