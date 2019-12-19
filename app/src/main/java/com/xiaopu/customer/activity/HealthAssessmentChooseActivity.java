package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.HealthAssessmentResultAdapter;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.HealthAssessmentResultData;
import com.xiaopu.customer.data.jsonresult.HealthComment;
import com.xiaopu.customer.utils.DateUtils;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.ListViewForScrollview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/7/17.
 */

public class HealthAssessmentChooseActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = HealthAssessmentChooseActivity.class.getSimpleName();

    private Context mContext;

    private LinearLayout ll_diabetes;

    private LinearLayout ll_sub_health;

    private LinearLayout ll_coronary_heart_disease;

    private LinearLayout ll_pregnancy;

    private LinearLayout ll_hyperlipidemia;

    private LinearLayout ll_hypertension;

    private ScrollView scrollView_health_assessment;

    private ListViewForScrollview lv_health_cassessment_result;

    private HealthAssessmentResultAdapter mAdapter;

    private List<HealthAssessmentResultData> dataList;

    private Intent mIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_assessment_choose);
        initActionBar("健康分析");
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        ll_diabetes = (LinearLayout) findViewById(R.id.ll_diabetes);
        ll_sub_health = (LinearLayout) findViewById(R.id.ll_sub_health);
        ll_coronary_heart_disease = (LinearLayout) findViewById(R.id.ll_coronary_heart_disease);
        ll_pregnancy = (LinearLayout) findViewById(R.id.ll_pregnancy);
        ll_hyperlipidemia = (LinearLayout) findViewById(R.id.ll_hyperlipidemia);
        ll_hypertension = (LinearLayout) findViewById(R.id.ll_hypertension);
        lv_health_cassessment_result = (ListViewForScrollview) findViewById(R.id.lv_health_assessment_result);
        scrollView_health_assessment = (ScrollView) findViewById(R.id.scrollView_health_assessment);
    }

    private void initData() {
        mIntent = new Intent(mContext, HealthAssessmentActivity.class);
        dataList = new ArrayList<>();
        mAdapter = new HealthAssessmentResultAdapter(mContext, dataList);
        lv_health_cassessment_result.setAdapter(mAdapter);

        getHealthAssessmentData();
    }

    private void getHealthAssessmentData() {
        dataList.clear();
        HttpUtils.getInstantce().postNoHead(HttpConstant.Url_getHealthAssessmentData, new HttpCallBack<HealthComment>() {
            @Override
            public void onSuccess(HttpResult result) {
                HealthComment healthComment = (HealthComment) result.getData();
                if (healthComment != null) {
                    if (healthComment.getDmResult() != null) {
                        HealthAssessmentResultData healthAssessmentResultData = new HealthAssessmentResultData();
                        healthAssessmentResultData.setTitle("糖尿病评测结果");
                        healthAssessmentResultData.setResult(healthComment.getDmResult());
                        healthAssessmentResultData.setDay(DateUtils.getDays(healthComment.getDmTime(), new Date()));
                        dataList.add(healthAssessmentResultData);
                    }
                    if (healthComment.getSubhealthyResult() != null) {
                        HealthAssessmentResultData healthAssessmentResultData = new HealthAssessmentResultData();
                        healthAssessmentResultData.setTitle("亚健康评测结果");
                        healthAssessmentResultData.setResult(healthComment.getSubhealthyResult());
                        healthAssessmentResultData.setDay(DateUtils.getDays(healthComment.getSubhealthyTime(), new Date()));
                        dataList.add(healthAssessmentResultData);
                    }

                    if (healthComment.getCorResult() != null) {
                        HealthAssessmentResultData healthAssessmentResultData = new HealthAssessmentResultData();
                        healthAssessmentResultData.setTitle("冠心病评测结果");
                        healthAssessmentResultData.setResult(healthComment.getCorResult());
                        healthAssessmentResultData.setDay(DateUtils.getDays(healthComment.getCoronaryTime(), new Date()));
                        dataList.add(healthAssessmentResultData);
                    }

                    if (healthComment.getPregnancyTestResult() != null) {
                        HealthAssessmentResultData healthAssessmentResultData = new HealthAssessmentResultData();
                        healthAssessmentResultData.setTitle("孕前自测评测结果");
                        healthAssessmentResultData.setResult(healthComment.getPregnancyTestResult());
                        healthAssessmentResultData.setDay(DateUtils.getDays(healthComment.getPregnancyTestTime(), new Date()));
                        dataList.add(healthAssessmentResultData);
                    }

                    if (healthComment.getHyperlipoidemiaResult() != null) {
                        HealthAssessmentResultData healthAssessmentResultData = new HealthAssessmentResultData();
                        healthAssessmentResultData.setTitle("高血脂评测结果");
                        healthAssessmentResultData.setResult(healthComment.getHyperlipoidemiaResult());
                        healthAssessmentResultData.setDay(DateUtils.getDays(healthComment.getHyperlipoidemiaTime(), new Date()));
                        dataList.add(healthAssessmentResultData);
                    }

                    if (healthComment.getHypertensionResult() != null) {
                        HealthAssessmentResultData healthAssessmentResultData = new HealthAssessmentResultData();
                        healthAssessmentResultData.setTitle("高血压评测结果");
                        healthAssessmentResultData.setResult(healthComment.getHypertensionResult());
                        healthAssessmentResultData.setDay(DateUtils.getDays(healthComment.getHypertensionTime(), new Date()));
                        dataList.add(healthAssessmentResultData);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(String msg) {

            }
        });

      /*  HttpUtils.getInstantce().getHealthAssessmentResult(new HttpConstant.SampleJsonResultListener<Feedback<HealthComment>>() {
            @Override
            public void onSuccess(Feedback<HealthComment> jsonData) {
                if (jsonData.getData() != null) {
                    HealthComment healthComment = jsonData.getData();
                    if (healthComment.getDmResult() != null) {
                        HealthAssessmentResultData healthAssessmentResultData = new HealthAssessmentResultData();
                        healthAssessmentResultData.setTitle("糖尿病评测结果");
                        healthAssessmentResultData.setResult(healthComment.getDmResult());
                        healthAssessmentResultData.setDay(DateUtils.getDays(healthComment.getDmTime(), new Date()));
                        dataList.add(healthAssessmentResultData);
                    }
                    if (healthComment.getSubhealthyResult() != null) {
                        HealthAssessmentResultData healthAssessmentResultData = new HealthAssessmentResultData();
                        healthAssessmentResultData.setTitle("亚健康评测结果");
                        healthAssessmentResultData.setResult(healthComment.getSubhealthyResult());
                        healthAssessmentResultData.setDay(DateUtils.getDays(healthComment.getSubhealthyTime(), new Date()));
                        dataList.add(healthAssessmentResultData);
                    }

                    if (healthComment.getCorResult() != null) {
                        HealthAssessmentResultData healthAssessmentResultData = new HealthAssessmentResultData();
                        healthAssessmentResultData.setTitle("冠心病评测结果");
                        healthAssessmentResultData.setResult(healthComment.getCorResult());
                        healthAssessmentResultData.setDay(DateUtils.getDays(healthComment.getCoronaryTime(), new Date()));
                        dataList.add(healthAssessmentResultData);
                    }

                    if (healthComment.getPregnancyTestResult() != null) {
                        HealthAssessmentResultData healthAssessmentResultData = new HealthAssessmentResultData();
                        healthAssessmentResultData.setTitle("孕前自测评测结果");
                        healthAssessmentResultData.setResult(healthComment.getPregnancyTestResult());
                        healthAssessmentResultData.setDay(DateUtils.getDays(healthComment.getPregnancyTestTime(), new Date()));
                        dataList.add(healthAssessmentResultData);
                    }

                    if (healthComment.getHyperlipoidemiaResult() != null) {
                        HealthAssessmentResultData healthAssessmentResultData = new HealthAssessmentResultData();
                        healthAssessmentResultData.setTitle("高血脂评测结果");
                        healthAssessmentResultData.setResult(healthComment.getHyperlipoidemiaResult());
                        healthAssessmentResultData.setDay(DateUtils.getDays(healthComment.getHyperlipoidemiaTime(), new Date()));
                        dataList.add(healthAssessmentResultData);
                    }

                    if (healthComment.getHypertensionResult() != null) {
                        HealthAssessmentResultData healthAssessmentResultData = new HealthAssessmentResultData();
                        healthAssessmentResultData.setTitle("高血压评测结果");
                        healthAssessmentResultData.setResult(healthComment.getHypertensionResult());
                        healthAssessmentResultData.setDay(DateUtils.getDays(healthComment.getHypertensionTime(), new Date()));
                        dataList.add(healthAssessmentResultData);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Feedback<HealthComment> jsonData) {
            }
        });*/
    }

    private void initListener() {
        ll_diabetes.setOnClickListener(this);
        ll_sub_health.setOnClickListener(this);
        ll_coronary_heart_disease.setOnClickListener(this);
        ll_pregnancy.setOnClickListener(this);
        ll_hyperlipidemia.setOnClickListener(this);
        ll_hypertension.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            int type = data.getIntExtra("type", 0);
            String result = data.getStringExtra("result");
            HealthAssessmentResultData healthAssessmentResultData = new HealthAssessmentResultData();
            switch (type) {
                case 1:
                    healthAssessmentResultData.setTitle("糖尿病评测结果");
                    break;
                case 2:
                    healthAssessmentResultData.setTitle("亚健康评测结果");
                    break;
                case 3:
                    healthAssessmentResultData.setTitle("冠心病评测结果");
                    break;
                case 4:
                    healthAssessmentResultData.setTitle("孕前自测评测结果");
                    break;
                case 5:
                    healthAssessmentResultData.setTitle("高血脂评测结果");
                    break;
                case 6:
                    healthAssessmentResultData.setTitle("高血压评测结果");
                    break;
            }
            healthAssessmentResultData.setResult(result);
            healthAssessmentResultData.setDay(0);

            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).getTitle().equals(healthAssessmentResultData.getTitle())) {
                    dataList.remove(i);
                }
            }
            dataList.add(0, healthAssessmentResultData);
            mAdapter.notifyDataSetChanged();
            /*Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    scrollView_health_assessment.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });*/
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_diabetes:
                mIntent.putExtra("type", 1);
                startActivityForResult(mIntent, 0);
                break;
            case R.id.ll_sub_health:
                mIntent.putExtra("type", 2);
                startActivityForResult(mIntent, 0);
                break;
            case R.id.ll_coronary_heart_disease:
                mIntent.putExtra("type", 3);
                startActivityForResult(mIntent, 0);
                break;
            case R.id.ll_pregnancy:
                mIntent.putExtra("type", 4);
                startActivityForResult(mIntent, 0);
                break;
            case R.id.ll_hyperlipidemia:
                mIntent.putExtra("type", 5);
                startActivityForResult(mIntent, 0);
                break;
            case R.id.ll_hypertension:
                mIntent.putExtra("type", 6);
                startActivityForResult(mIntent, 0);
                break;
        }
    }
}
