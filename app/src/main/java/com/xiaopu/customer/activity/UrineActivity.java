package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.SimpleResultAdapter;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.SimpleDetectionResult;
import com.xiaopu.customer.data.jsonresult.DetectionUrine;
import com.xiaopu.customer.service.BluetoothService;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.TimeUtils;
import com.xiaopu.customer.utils.Config;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.utils.security.Des;
import com.xiaopu.customer.view.LoadingView.LoadingView;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/1.
 */

public class UrineActivity extends ActivityBase {
    private static final String LOG_TAG = UrineActivity.class.getSimpleName();

    private Context mContext;

    private TextView tv_bar_right;

    private ListView mListView;

    private Button bt_start_detection;

    private LoadingView mLoadingView;

    private TextView tv_loading_fail;

    private TextView tv_no_data;

    private MyClickListener mClick;

    private SimpleResultAdapter mAdapter;

    private List<SimpleDetectionResult> dataList;

    private BluetoothService bcs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urine);
        initActionBar(getString(R.string.urine_detection));
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.lv_recent_data);
        bt_start_detection = (Button) findViewById(R.id.bt_start_detection);
        mLoadingView = (LoadingView) findViewById(R.id.loading_view);
        tv_loading_fail = (TextView) findViewById(R.id.tv_loading_fail);
        tv_no_data = (TextView) findViewById(R.id.tv_no_data);
        tv_bar_right = (TextView) findViewById(R.id.tv_actionbar_text);
        tv_bar_right.setVisibility(View.VISIBLE);
        tv_bar_right.setText(R.string.close_slot);
    }

    private void initData() {
        mClick = new MyClickListener();

        dataList = new ArrayList<>();

        mAdapter = new SimpleResultAdapter(mContext, dataList);

        mListView.setAdapter(mAdapter);

        bcs = ApplicationXpClient.getInstance().getBluetoothService();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUrineData();
    }

    private void getUrineData() {
        resetView(mLoadingView);

        HttpUtils.getInstantce().postNoHead(HttpConstant.Url_UrineAllDate, new HttpCallBack<List<String>>() {
            @Override
            public void onSuccess(HttpResult result) {
                List<String> strList = (List<String>) result.getData();
                if (strList.size() == 0) {
                    resetView(tv_no_data);
                } else {
                    resetView(mListView);
                    dataList.clear();
                    try {
                        for (int i = 0; i < strList.size(); i++) {
                            String urineStr = strList.get(i);
                            DetectionUrine detectionUrine = HttpUtils.getInstantce().gson.fromJson(Des.decode(urineStr), new TypeToken<DetectionUrine>() {
                            }.getType());
                            SimpleDetectionResult simpleDetectionResult = new SimpleDetectionResult();
                            simpleDetectionResult.setDetectionTime(TimeUtils.DateToStringDatail(detectionUrine.getCreateTime()));
                            simpleDetectionResult.setDetectionResult(getString(R.string.click_view));
                            simpleDetectionResult.setResult(getString(R.string.click_view));
                            simpleDetectionResult.setObject(detectionUrine);
                            dataList.add(simpleDetectionResult);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(String msg) {
                resetView(tv_loading_fail);
            }
        });

      /*  HttpUtils.getInstantce().getUrineAllData(new HttpConstant.SampleJsonResultListener<Feedback<List<DetectionUrine>>>() {
            @Override
            public void onSuccess(Feedback<List<DetectionUrine>> jsonData) {
                if (jsonData.getData().size() == 0) {
                    resetView(tv_no_data);
                } else {
                    resetView(mListView);
                    dataList.clear();
                    for (int i = 0; i < jsonData.getData().size(); i++) {
                        DetectionUrine detectionUrine = jsonData.getData().get(i);
                        SimpleDetectionResult simpleDetectionResult = new SimpleDetectionResult();
                        simpleDetectionResult.setDetectionTime(TimeUtils.DateToStringDatail(detectionUrine.getCreateTime()));
                        simpleDetectionResult.setDetectionResult(getString(R.string.click_view));
                        simpleDetectionResult.setResult(getString(R.string.click_view));
                        simpleDetectionResult.setObject(detectionUrine);
                        dataList.add(simpleDetectionResult);

                    }
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Feedback<List<DetectionUrine>> jsonData) {
                resetView(tv_loading_fail);
            }
        });*/
    }

    private void initListener() {
        bt_start_detection.setOnClickListener(mClick);
        tv_bar_right.setOnClickListener(mClick);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DetectionUrine detectionUrine = (DetectionUrine) dataList.get(position).getObject();
                Intent mIntent = new Intent(mContext, UrineResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("result", detectionUrine);
                mIntent.putExtra("type", 2);
                mIntent.putExtras(bundle);
                startActivity(mIntent);
            }
        });
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_start_detection:
                    if (ApplicationXpClient.isConnect()) {
                        Intent mIntent = new Intent(mContext, DetectionStepActivity.class);
                        mIntent.putExtra("type", 1);
                        startActivityForResult(mIntent, 1);
                    } else {
                        T.showShort(getString(R.string.toilet_not_connect));
                    }
                    break;
                case R.id.tv_loading_fail:
                    getUrineData();
                    break;
                case R.id.tv_actionbar_text:
                    bcs.setRegisterValue(1, Config.REGISTER_URINE_DOOR, 2);
                    break;
            }
        }
    }

    private void resetView(View v) {
        mListView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        tv_loading_fail.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.lv_recent_data:
                mListView.setVisibility(View.VISIBLE);
                break;
            case R.id.loading_view:
                mLoadingView.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_loading_fail:
                tv_loading_fail.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_no_data:
                tv_no_data.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            new SweetAlertDialog(mContext, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText(getString(R.string.tips))
                    .setContentText(getString(R.string.clean_slot_tip))
                    .setCustomImage(getResources().getDrawable(R.mipmap.tip_icon))
                    .setConfirmText(getString(R.string.know))
                    .show();
        }
    }
}

