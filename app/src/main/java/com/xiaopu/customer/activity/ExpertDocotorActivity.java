package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.DoctorListAdapter;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.SimpleDoctorResult;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.LoadingView.LoadingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class ExpertDocotorActivity extends ActivityBase {
    private static final String LOG_TAG = ExpertDocotorActivity.class.getSimpleName();

    private static String HTTP_URL;

    private static final int FAMILY_DOCTOR = 1;

    private static final int HEALTH_MISTRESS = 2;

    private static final int TODAY_CLINIC = 3;

    private Context mContext;

    private LinearLayout ll_medical_department;

    private LinearLayout ll_surgical_department;

    private LinearLayout ll_gynecology_department;

    private LinearLayout ll_pediatrics;

    private ListView mListView;

    private LoadingView mLoadingView;

    private TextView tv_loading_fail;

    private TextView tv_no_data;

    private List<SimpleDoctorResult> listSimpleDoctorResult;//健康管家列表

    private SimpleDoctorResult simpleDoctorResult;

    private DoctorListAdapter mAdapter;

    private MyClickListener mClick;

    private int mPage = 1;

    private int sizeOfPage = 10;

    private int doctor_type;

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);
        mContext = this;
        doctor_type = getIntent().getIntExtra("DoctorType", -1);
//        initView();
//        initData();
//        setListener();
    }


    private void initView() {

        mListView = findViewById(R.id.doctor_list);
        mLoadingView = findViewById(R.id.loading_view);
        tv_loading_fail = findViewById(R.id.tv_loading_fail);
        tv_no_data = findViewById(R.id.tv_no_data);
        ll_gynecology_department = findViewById(R.id.ll_gynecology_department);
        ll_medical_department = findViewById(R.id.ll_medical_department);
        ll_surgical_department = findViewById(R.id.ll_surgical_department);
        ll_pediatrics = findViewById(R.id.ll_pediatrics);
    }

    private void initData() {
        switch (doctor_type) {
            case HEALTH_MISTRESS:
                initActionBar("健康小蜜");
                HTTP_URL = HttpConstant.Url_getHealthHousekeeperlist;
                break;
            case FAMILY_DOCTOR:
                HTTP_URL = HttpConstant.Url_getExpertDoctorlist;
                initActionBar("家庭医生");
                break;
            case TODAY_CLINIC:
                HTTP_URL = HttpConstant.Url_getDoctorFreelist;
                initActionBar("今日义诊");
                break;
        }
        listSimpleDoctorResult = new ArrayList<>();
        mClick = new MyClickListener();
        mAdapter = new DoctorListAdapter(mContext, listSimpleDoctorResult);
        mListView.setAdapter(mAdapter);
        resetView(mLoadingView);
        mIntent = new Intent(mContext, DepartmentDoctorActivity.class);
        getDoctorData(1, 10);
    }


    private void setListener() {
        tv_loading_fail.setOnClickListener(mClick);
        ll_medical_department.setOnClickListener(mClick);
        ll_surgical_department.setOnClickListener(mClick);
        ll_gynecology_department.setOnClickListener(mClick);
        ll_pediatrics.setOnClickListener(mClick);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("doctorInfo", listSimpleDoctorResult.get(position));
                Intent mIntent = new Intent(mContext, DoctorInfoActivity.class);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
            }
        });
    }

    private void resetView(View v) {
        mListView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        tv_loading_fail.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.doctor_list:
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

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_order_comment:
                    break;
                case R.id.tv_loading_fail:
                    getDoctorData(1, 10);
                    break;
                case R.id.ll_medical_department:
                    mIntent.putExtra("type", 1);
                    startActivity(mIntent);
                    break;
                case R.id.ll_surgical_department:
                    mIntent.putExtra("type", 2);
                    startActivity(mIntent);
                    break;
                case R.id.ll_gynecology_department:
                    mIntent.putExtra("type", 3);
                    startActivity(mIntent);
                    break;
                case R.id.ll_pediatrics:
                    mIntent.putExtra("type", 4);
                    startActivity(mIntent);
                    break;
            }
        }
    }

    private void getDoctorData(final int pageNo, int pageSize) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("pageNo", pageNo);
        maps.put("pageSize", pageSize);
        HttpUtils.getInstantce().postWithHead(maps, HTTP_URL, new HttpCallBack<List<SimpleDoctorResult>>() {
            @Override
            public void onSuccess(HttpResult result) {
                List<SimpleDoctorResult> simpleDoctorResultList = (List<SimpleDoctorResult>) result.getData();
                sizeOfPage = simpleDoctorResultList.size();
                if (sizeOfPage == 0 && pageNo == 1) {
                    resetView(tv_no_data);
                    switch (doctor_type) {
                        case HEALTH_MISTRESS:
                            tv_no_data.setText("暂无健康小蜜");
                            break;
                        case FAMILY_DOCTOR:
                            tv_no_data.setText("暂无家庭医生");
                            break;
                        case TODAY_CLINIC:
                            tv_no_data.setText("暂无义诊医生");
                            break;
                    }

                } else if (sizeOfPage == 0 && pageNo != 1) {
                    resetView(mListView);
                    T.showShort("已经是最后一页");
                } else if (sizeOfPage != 0) {
                    if (pageNo == 1) {
                        listSimpleDoctorResult.clear();
                    }
                    resetView(mListView);
                    listSimpleDoctorResult.addAll(simpleDoctorResultList);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(String msg) {
                resetView(tv_loading_fail);
            }
        });

        /*HttpUtils.getInstantce().getExpertDoctorlist(HTTP_URL, pageNo, pageSize, new HttpConstant.SampleJsonResultListener<Feedback<List<SimpleDoctorResult>>>() {
            @Override
            public void onSuccess(Feedback<List<SimpleDoctorResult>> jsonData) {
                sizeOfPage = jsonData.getData().size();
                if (sizeOfPage == 0 && pageNo == 1) {
                    resetView(tv_no_data);
                    switch (doctor_type) {
                        case HEALTH_MISTRESS:
                            tv_no_data.setText("暂无健康小蜜");
                            break;
                        case FAMILY_DOCTOR:
                            tv_no_data.setText("暂无家庭医生");
                            break;
                        case TODAY_CLINIC:
                            tv_no_data.setText("暂无义诊医生");
                            break;
                    }

                } else if (sizeOfPage == 0 && pageNo != 1) {
                    resetView(mListView);
                    T.showShort("已经是最后一页");
                } else if (sizeOfPage != 0) {
                    if (pageNo == 1) {
                        listSimpleDoctorResult.clear();
                    }
                    resetView(mListView);
                    listSimpleDoctorResult.addAll(jsonData.getData());
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Feedback<List<SimpleDoctorResult>> jsonData) {
                resetView(tv_loading_fail);
            }
        });*/
    }

}
