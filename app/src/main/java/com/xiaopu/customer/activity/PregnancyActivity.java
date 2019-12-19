package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.SimpleResultAdapter;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.SimpleDetectionResult;
import com.xiaopu.customer.data.jsonresult.DetectionPregnant;
import com.xiaopu.customer.service.BluetoothService;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.TimeUtils;
import com.xiaopu.customer.utils.Config;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.LoadingView.LoadingView;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/1.
 */

public class PregnancyActivity extends ActivityBase {
    private static final String LOG_TAG = PregnancyActivity.class.getSimpleName();

    private Context mContext;

    private TextView tv_bar_right;

    private ListView mListView;

    private Button bt_start_detection;

    private LoadingView mLoadingView;

    private TextView tv_loading_fail;

    private TextView tv_no_data;

    private List<SimpleDetectionResult> datalist;

    private MyClickListener mClick;

    private SimpleResultAdapter mAdapter;

    private BluetoothService bcs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregnancy);
        initActionBar(getString(R.string.pregnancy_test));
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
        tv_bar_right.setText(getString(R.string.close_slot));
    }

    private void initData() {
        mClick = new MyClickListener();
        datalist = new ArrayList<>();
        mAdapter = new SimpleResultAdapter(mContext, datalist);
        mListView.setAdapter(mAdapter);
        bcs = ApplicationXpClient.getInstance().getBluetoothService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPregnancyData();
    }

    private void getPregnancyData() {
        resetView(mLoadingView);

        HttpUtils.getInstantce().postNoHead(HttpConstant.Url_PregnancyAllDate, new HttpCallBack<List<DetectionPregnant>>() {
            @Override
            public void onSuccess(HttpResult result) {
                List<DetectionPregnant> detectionPregnantList = (List<DetectionPregnant>) result.getData();
                if (detectionPregnantList.size() == 0) {
                    resetView(tv_no_data);
                    tv_no_data.setText(R.string.no_pregnancy_data);
                } else {
                    datalist.clear();
                    resetView(mListView);
                    for (int i = 0; i < detectionPregnantList.size(); i++) {
                        DetectionPregnant detectionPregnant = detectionPregnantList.get(i);
                        SimpleDetectionResult simpleDetectionResult = new SimpleDetectionResult();
                        simpleDetectionResult.setDetectionTime(TimeUtils.DateToStringDatail(detectionPregnant.getCreateTime()));
                        if (detectionPregnant.getPre() == 0) {
                            simpleDetectionResult.setDetectionResult(getString(R.string.negative));
                            simpleDetectionResult.setResult(getString(R.string.not_pregnant));
                        } else {
                            simpleDetectionResult.setDetectionResult(getString(R.string.masculine));
                            simpleDetectionResult.setResult(getString(R.string.already_pregnancy));
                            mContext.getCacheDir();
                        }
                        datalist.add(simpleDetectionResult);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(String msg) {
                resetView(tv_loading_fail);
            }
        });

      /*  HttpUtils.getInstantce().getPregnancyAllData(new HttpConstant.SampleJsonResultListener<Feedback<List<DetectionPregnant>>>() {
            @Override
            public void onSuccess(Feedback<List<DetectionPregnant>> jsonData) {
                if (jsonData.getData().size() == 0) {
                    resetView(tv_no_data);
                    tv_no_data.setText(R.string.no_pregnancy_data);
                } else {
                    datalist.clear();
                    resetView(mListView);
                    for (int i = 0; i < jsonData.getData().size(); i++) {
                        DetectionPregnant detectionPregnant = jsonData.getData().get(i);
                        SimpleDetectionResult simpleDetectionResult = new SimpleDetectionResult();
                        simpleDetectionResult.setDetectionTime(TimeUtils.DateToStringDatail(detectionPregnant.getCreateTime()));
                        if (detectionPregnant.getPre() == 0) {
                            simpleDetectionResult.setDetectionResult(getString(R.string.negative));
                            simpleDetectionResult.setResult(getString(R.string.not_pregnant));
                        } else {
                            simpleDetectionResult.setDetectionResult(getString(R.string.masculine));
                            simpleDetectionResult.setResult(getString(R.string.already_pregnancy));
                            mContext.getCacheDir();
                        }
                        datalist.add(simpleDetectionResult);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Feedback<List<DetectionPregnant>> jsonData) {
                resetView(tv_loading_fail);
            }
        });*/
    }

    private void initListener() {
        bt_start_detection.setOnClickListener(mClick);
        tv_loading_fail.setOnClickListener(mClick);
        tv_bar_right.setOnClickListener(mClick);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SimpleDetectionResult simpleDetectionResult = datalist.get(position);
                Intent mIntent = new Intent(mContext, ShowDetectionResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("result", simpleDetectionResult);
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
                        mIntent.putExtra("type", 2);
                        startActivityForResult(mIntent, 1);
                    } else {
                        T.showShort(getString(R.string.toilet_not_connect));
                    }
                    break;
                case R.id.tv_loading_fail:
                    getPregnancyData();
                    break;
                case R.id.tv_actionbar_text:
                    bcs.setRegisterValue(1, Config.REGISTER_URINE_DOOR, 4);
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

