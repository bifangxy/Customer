package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.DoctorListAdapter;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.SimpleDoctorResult;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.LoadingView.LoadingView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/11.
 */

public class MyDoctorActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = MyDoctorActivity.class.getSimpleName();

    private Context mContext;

    private ListView lv_doctor;

    private LoadingView mLoadingView;

    private TextView tv_loading_fail;

    private TextView tv_no_data;

    private DoctorListAdapter mAdapter;

    private List<SimpleDoctorResult> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_doctor);
        initActionBar("我的医生");
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        lv_doctor = findViewById(R.id.doctor_list);
        mLoadingView = findViewById(R.id.loading_view);
        tv_loading_fail = findViewById(R.id.tv_loading_fail);
        tv_no_data = findViewById(R.id.tv_no_data);
    }

    private void initData() {
        dataList = new ArrayList<>();
        mAdapter = new DoctorListAdapter(mContext, dataList, 1);
        lv_doctor.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDoctorList();
    }

    private void initListener() {
        lv_doctor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("doctorInfo", dataList.get(position));
                Intent mIntent = new Intent(mContext, DoctorInfoActivity.class);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
            }
        });
    }

    private void getDoctorList() {
        dataList.clear();
        resetView(mLoadingView);

        HttpUtils.getInstantce().postNoHead(HttpConstant.Url_getMyDoctorList, new HttpCallBack<List<SimpleDoctorResult>>() {
            @Override
            public void onSuccess(HttpResult result) {
                List<SimpleDoctorResult> simpleDoctorResults = (List<SimpleDoctorResult>) result.getData();
                if (simpleDoctorResults.size() != 0) {
                    resetView(lv_doctor);
                    dataList.addAll(simpleDoctorResults);
                    mAdapter.notifyDataSetChanged();
                } else {
                    resetView(tv_no_data);
                }
            }

            @Override
            public void onFail(String msg) {

            }
        });


     /*   HttpUtils.getInstantce().getMyDoctorList(new HttpConstant.SampleJsonResultListener<Feedback<List<SimpleDoctorResult>>>() {
            @Override
            public void onSuccess(Feedback<List<SimpleDoctorResult>> jsonData) {
                if (jsonData.getData().size() != 0) {
                    resetView(lv_doctor);
                    dataList.addAll(jsonData.getData());
                    mAdapter.notifyDataSetChanged();
                } else {
                    resetView(tv_no_data);
                }

            }

            @Override
            public void onFailure(Feedback<List<SimpleDoctorResult>> jsonData) {
                resetView(tv_loading_fail);
            }
        });*/

    }


    private void resetView(View view) {
        lv_doctor.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        tv_loading_fail.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_loading_fail:
                getDoctorList();
                break;
        }
    }
}
