package com.xiaopu.customer.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.SimpleDetectionResult;

/**
 * Created by Administrator on 2017/6/6.
 */

public class ShowDetectionResultActivity extends ActivityBase {
    private static final String LOG_TAG = ShowDetectionResultActivity.class.getSimpleName();

    private Context mContext;

    private ImageView iv_result_left;

    private ImageView iv_result_center;

    private ImageView iv_result_right;

    private SimpleDetectionResult mSimpleDetectionResult;

    private AnimatorSet animatorSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detection_result);
        initActionBar("结果详情");
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        iv_result_left = (ImageView) findViewById(R.id.iv_result_left);
        iv_result_center = (ImageView) findViewById(R.id.iv_result_center);
        iv_result_right = (ImageView) findViewById(R.id.iv_result_right);
    }

    private void initData() {
        mSimpleDetectionResult = (SimpleDetectionResult) getIntent().getSerializableExtra("result");
        initResult();
        startAnimation();
    }

    private void startAnimation() {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(iv_result_center, "scaleX", 1f, 1.2f, 1f);
        animator1.setDuration(3000);
        animator1.setRepeatCount(10);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(iv_result_center, "scaleY", 1f, 1.2f, 1f);
        animator2.setDuration(3000);
        animator2.setRepeatCount(10);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator1, animator2);
        animatorSet.start();
    }

    private void initResult() {
        switch (mSimpleDetectionResult.getResult()) {
            case "未怀孕":
                iv_result_center.setImageResource(R.mipmap.un_pregnancy_big);
                iv_result_left.setImageResource(R.mipmap.pregnancy_small);
                iv_result_right.setImageResource(R.mipmap.pregnancy_small);
                break;
            case "已怀孕":
                iv_result_center.setImageResource(R.mipmap.pregnancy_big);
                iv_result_left.setImageResource(R.mipmap.un_pregnancy_small);
                iv_result_right.setImageResource(R.mipmap.un_pregnancy_small);
                break;
            case "未排卵":
                iv_result_center.setImageResource(R.mipmap.un_ovulation_big);
                iv_result_left.setImageResource(R.mipmap.ovulation_small);
                iv_result_right.setImageResource(R.mipmap.ovulation_small);
                break;
            case "已排卵":
                iv_result_center.setImageResource(R.mipmap.ovulation_big);
                iv_result_left.setImageResource(R.mipmap.un_ovulation_small);
                iv_result_right.setImageResource(R.mipmap.un_ovulation_small);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animatorSet != null && animatorSet.isRunning()) {
            animatorSet.end();
            animatorSet = null;
        }
    }

    private void initListener() {
    }

}
