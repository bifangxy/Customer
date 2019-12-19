package com.xiaopu.customer.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;
import com.xiaopu.customer.view.photoview.PhotoView;

/**
 * Created by Administrator on 2018/1/3.
 * 聊天大图显示页面
 */

public class BigImageActivity extends ActivityBase {
    private static final String LOG_TAG = BigImageActivity.class.getSimpleName();

    private Context mContext;

    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_big_image);
        initView();
        initData();
    }

    private void initView() {
        photoView = (PhotoView) findViewById(R.id.image);
    }

    private void initData() {
        String uri = getIntent().getStringExtra("uri");
        String imagePath = getIntent().getStringExtra("imagePath");
        if (!TextUtils.isEmpty(uri)) {
            ImageLoader.getInstance().displayImage(uri, photoView, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    Log.d(LOG_TAG, "onLoadingStarted");
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    Log.d(LOG_TAG, "onLoadingFailed");
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    Log.d(LOG_TAG, "onLoadingComplete");
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    Log.d(LOG_TAG, "onLoadingCancelled");
                }
            });
        } else if (!TextUtils.isEmpty(imagePath)) {
            ImageLoader.getInstance().displayImage(imagePath, photoView);
        }
    }
}
