package com.xiaopu.customer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaopu.customer.activity.InfoContentActivity;
import com.xiaopu.customer.activity.MallContentActivity;
import com.xiaopu.customer.data.jsonresult.IndexBannerMall;

import java.util.List;

/**
 * Created by Administrator on 2017/5/16.
 */

public class CarouselAdapter extends PagerAdapter {
    private Context mContext;

    private List<ImageView> imageViews;

    private List<IndexBannerMall> indexBannerMallList;


    public CarouselAdapter(Context mContext, List<ImageView> imageViews, List<IndexBannerMall> indexBannerMallList) {
        this.mContext = mContext;
        this.imageViews = imageViews;
        this.indexBannerMallList = indexBannerMallList;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(imageViews.get(position % imageViews.size()));
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        container.addView(imageViews.get(position % imageViews.size()));
        imageViews.get(position % imageViews.size()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IndexBannerMall indexBannerMall = indexBannerMallList.get(position % imageViews.size());
                if (indexBannerMall.getType() == 2) {
                    Intent mIntent = new Intent(mContext, MallContentActivity.class);
                    mIntent.putExtra("type", 6);
                    mIntent.putExtra("home_url", indexBannerMall.getLinkUrl());
                    mContext.startActivity(mIntent);
                } else {
                    Intent mIntent = new Intent(mContext, InfoContentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("indexBannerMall", indexBannerMallList.get(position % imageViews.size()));
                    mIntent.putExtras(bundle);
                    mIntent.putExtra("showType", 0);
                    mContext.startActivity(mIntent);
                }
            }
        });
        return imageViews.get(position % imageViews.size());
    }
}
