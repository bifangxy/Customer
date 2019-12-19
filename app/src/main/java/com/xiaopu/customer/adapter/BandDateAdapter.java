package com.xiaopu.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.view.AutoLocateHorizontalView;

import java.util.List;

/**
 * Created by Administrator on 2017/9/18.
 */

public class BandDateAdapter extends RecyclerView.Adapter<BandDateAdapter.MyHolder> implements AutoLocateHorizontalView.IAutoLocateHorizontalView {

    private Context context;
    private View view;
    private List<String> dates;

    public BandDateAdapter(Context context, List<String> dates) {
        this.context = context;
        this.dates = dates;
    }


    @Override
    public BandDateAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.item_band_date, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(BandDateAdapter.MyHolder holder, int position) {
        holder.tvDate.setText(dates.get(position));
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    @Override
    public View getItemView() {
        return view;
    }

    @Override
    public void onViewSelected(boolean isSelected, int pos, RecyclerView.ViewHolder holder, int itemWidth) {
        if (isSelected) {
            ((MyHolder) holder).tvDate.setTextSize(14);
        } else {
            ((MyHolder) holder).tvDate.setTextSize(10);
        }
    }

    static class MyHolder extends RecyclerView.ViewHolder {
        TextView tvDate;

        MyHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }
}
