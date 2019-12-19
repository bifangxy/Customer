package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.loopj.android.http.RequestParams;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.HealthAssessmentAdapter;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.ListViewForScrollview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/17.
 */

public class HealthAssessmentActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = HealthAssessmentActivity.class.getSimpleName();

    private Context mContext;

    private ListViewForScrollview lv_assessment;

    private Button bt_submit;

    private HealthAssessmentAdapter mAdapter;

    private List<String> datalist;

    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_assessment);
        initActionBar("健康评测");
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        lv_assessment = (ListViewForScrollview) findViewById(R.id.lv_health_assessment);
        bt_submit = (Button) findViewById(R.id.bt_submit_assessment);
    }

    private void initData() {
        type = getIntent().getIntExtra("type", 0);
        datalist = new ArrayList<>();

        switch (type) {
            case 1:
                for (int i = 1; i < 16; i++) {
                    int strId = mContext.getResources().getIdentifier("diabetes_question_" + i,
                            "string", mContext.getPackageName());
                    String str = getResources().getString(strId);
                    datalist.add(str);
                }
                break;
            case 2:
                for (int i = 1; i < 15; i++) {
                    int strId = mContext.getResources().getIdentifier("sub_health_" + i,
                            "string", mContext.getPackageName());
                    String str = getResources().getString(strId);
                    datalist.add(str);
                }
                break;
            case 3:
                for (int i = 1; i < 16; i++) {
                    int strId = mContext.getResources().getIdentifier("coronary_heart_disease_" + i,
                            "string", mContext.getPackageName());
                    String str = getResources().getString(strId);
                    datalist.add(str);
                }
                break;
            case 4:
                for (int i = 1; i < 10; i++) {
                    int strId = mContext.getResources().getIdentifier("pregnancy_" + i,
                            "string", mContext.getPackageName());
                    String str = getResources().getString(strId);
                    datalist.add(str);
                }
                break;
            case 5:
                for (int i = 1; i < 14; i++) {
                    int strId = mContext.getResources().getIdentifier("hyperlipidemia_" + i,
                            "string", mContext.getPackageName());
                    String str = getResources().getString(strId);
                    datalist.add(str);
                }
                break;
            case 6:
                for (int i = 1; i < 21; i++) {
                    int strId = mContext.getResources().getIdentifier("hypertension_" + i,
                            "string", mContext.getPackageName());
                    String str = getResources().getString(strId);
                    datalist.add(str);
                }
                break;
        }

        mAdapter = new HealthAssessmentAdapter(mContext, datalist);
        lv_assessment.setAdapter(mAdapter);
    }

    private void initListener() {
        bt_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String score = "" + mAdapter.getValue();
        String result = "";
        String url = "";
        switch (v.getId()) {
            case R.id.bt_submit_assessment:
                switch (type) {
                    case 1:
                        if (mAdapter.getValue() >= 12) {
                            result = getResources().getString(R.string.diabetes_result_one);
                        } else if (mAdapter.getValue() >= 8) {
                            result = getResources().getString(R.string.diabetes_result_two);
                        } else {
                            result = getResources().getString(R.string.diabetes_result_three);
                        }
                        url = HttpConstant.Url_saveDmData;
                        break;
                    case 2:
                        if (mAdapter.getValue() >= 8) {
                            result = getResources().getString(R.string.sub_health_result_three);
                        } else if (mAdapter.getValue() >= 5) {
                            result = getResources().getString(R.string.sub_health_result_two);
                        } else if (mAdapter.getValue() >= 3) {
                            result = getResources().getString(R.string.sub_health_result_one);
                        } else {
                            result = getResources().getString(R.string.sub_health_result_four);
                        }
                        url = HttpConstant.Url_saveSubHealthyData;
                        break;
                    case 3:
                        if (mAdapter.getValue() >= 15) {
                            result = getResources().getString(R.string.coronary_heart_disease_result_one);
                        } else if (mAdapter.getValue() >= 8) {
                            result = getResources().getString(R.string.coronary_heart_disease_result_two);
                        } else {
                            result = getResources().getString(R.string.coronary_heart_disease_result_three);
                        }
                        url = HttpConstant.Url_saveCoronaryData;
                        break;
                    case 4:
                        if (mAdapter.getValue() >= 8) {
                            result = getResources().getString(R.string.pregnancy_result_one);
                        } else if (mAdapter.getValue() >= 4) {
                            result = getResources().getString(R.string.pregnancy_result_two);
                        } else {
                            result = getResources().getString(R.string.pregnancy_result_three);
                        }
                        url = HttpConstant.Url_savePregnancyTestData;
                        break;
                    case 5:
                        if (mAdapter.getValue() >= 8) {
                            result = getResources().getString(R.string.hyperlipidemia_result_one);
                        } else if (mAdapter.getValue() >= 4) {
                            result = getResources().getString(R.string.hyperlipidemia_result_two);
                        } else {
                            result = getResources().getString(R.string.hyperlipidemia_result_three);
                        }
                        url = HttpConstant.Url_saveHyperlipoidemiaData;
                        break;
                    case 6:
                        if (mAdapter.getValue() >= 15) {
                            result = getResources().getString(R.string.hypertension_result_one);
                        } else if (mAdapter.getValue() >= 8) {
                            result = getResources().getString(R.string.hypertension_result_two);
                        } else {
                            result = getResources().getString(R.string.hypertension_result_one);
                        }
                        url = HttpConstant.Url_saveHypertensionData;
                        break;
                }
                saveHealthAssessmentData(url, score, result);
                break;
        }
    }

    private void saveHealthAssessmentData(String url, final String score, final String assessment_result) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("score", score);
        maps.put("comment_result", assessment_result);

        HttpUtils.getInstantce().showSweetDialog("测评中...");
        HttpUtils.getInstantce().postWithHead(maps, url, new HttpCallBack<String>() {
            @Override
            public void onSuccess(HttpResult result) {
                HttpUtils.getInstantce().dismissSweetDialog();
                Intent intent = new Intent();
                intent.putExtra("result", assessment_result);
                intent.putExtra("type", type);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFail(String msg) {

            }
        });

/*
        HttpUtils.getInstantce().saveHealthAssessmentResult(url, score, result, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
            @Override
            public void onSuccess(Feedback<String> jsonData) {

                Intent intent = new Intent();
                intent.putExtra("result", result);
                intent.putExtra("type", type);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Feedback<String> jsonData) {

            }
        });
*/
    }
}
