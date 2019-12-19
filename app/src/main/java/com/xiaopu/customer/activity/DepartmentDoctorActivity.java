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
import com.xiaopu.customer.view.DepartmentView;
import com.xiaopu.customer.view.LoadingView.LoadingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Xieying on 2018/3/9.
 */

public class DepartmentDoctorActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = DepartmentDoctorActivity.class.getSimpleName();

    private String[] departments;

    private Context mContext;

    private TextView tv_department_name;

    private ListView lv_doctor;

    private LoadingView mLoadingView;

    private TextView tv_no_data;

    private TextView tv_loading_fail;

    private DepartmentView mPopupWindow;

    private DoctorListAdapter mAdapter;

    private List<SimpleDoctorResult> dataList;

    private int parentId;

    private int childId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_doctor);
        initActionBar("科室");
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        tv_department_name = (TextView) findViewById(R.id.tv_department_name);
        lv_doctor = findViewById(R.id.lv_doctor);
        mLoadingView = findViewById(R.id.loading_view);
        tv_loading_fail = findViewById(R.id.tv_loading_fail);
        tv_no_data = findViewById(R.id.tv_no_data);
    }

    private void initData() {
        departments = new String[]{getString(R.string.medical_department), getString(R.string.surgical_department), getString(R.string.gynecology_department), getString(R.string.pediatrics)};
        parentId = getIntent().getIntExtra("type", -1);
        mPopupWindow = new DepartmentView(DepartmentDoctorActivity.this, parentId);
        tv_department_name.setText(departments[parentId - 1]);
        dataList = new ArrayList<>();
        mAdapter = new DoctorListAdapter(mContext, dataList);
        lv_doctor.setAdapter(mAdapter);
        doctorListByDepartment(parentId, 0, 1, true);
    }

    private void initListener() {
        tv_department_name.setOnClickListener(this);
        mPopupWindow.setOnItemClickListener(new DepartmentView.OnItemClickListener() {
            @Override
            public void onItemClick(int parentid, int childid, String name) {
                parentId = parentid;
                childId = childid;
                tv_department_name.setText(name);
                doctorListByDepartment(parentId, childId, 1, true);
            }
        });

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

    private void doctorListByDepartment(int parentId, int childId, int pageNo, final boolean isFirst) {
        if (isFirst) {
            resetView(mLoadingView);
        }

        Map<String, Object> maps = new HashMap<>();
        maps.put("persentDepartmentId", parentId);
        maps.put("departmentId", childId);
        maps.put("pageSize", 10);
        maps.put("pageNo", pageNo);

        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getDoctorListByDepartment, new HttpCallBack<List<SimpleDoctorResult>>() {
            @Override
            public void onSuccess(HttpResult result) {
                List<SimpleDoctorResult> simpleDoctorResultList = (List<SimpleDoctorResult>) result.getData();
                if (isFirst)
                    dataList.clear();
                if (simpleDoctorResultList.size() == 0) {
                    resetView(tv_no_data);
                    tv_no_data.setText("该科室暂无医生");
                } else {
                    resetView(lv_doctor);
                    dataList.addAll(simpleDoctorResultList);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(String msg) {
                resetView(tv_loading_fail);
            }
        });
/*
        HttpUtils.getInstantce().getDoctorListByDepartment(parentId, childId, pageNo, 10, new HttpConstant.SampleJsonResultListener<Feedback<List<SimpleDoctorResult>>>() {
            @Override
            public void onSuccess(Feedback<List<SimpleDoctorResult>> jsonData) {
                if (isFirst)
                    dataList.clear();
                if (jsonData.getData().size() == 0) {
                    resetView(tv_no_data);
                    tv_no_data.setText("该科室暂无医生");
                } else {
                    resetView(lv_doctor);
                    dataList.addAll(jsonData.getData());
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Feedback<List<SimpleDoctorResult>> jsonData) {
                resetView(tv_loading_fail);
            }
        });
*/


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_department_name:
                mPopupWindow.showAsDropDown(tv_department_name, 0, 20);
                break;
            case R.id.tv_loading_fail:
                doctorListByDepartment(parentId, childId, 1, true);
                break;
        }
    }

    private void resetView(View v) {
        lv_doctor.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        tv_loading_fail.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.lv_doctor:
                lv_doctor.setVisibility(View.VISIBLE);
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
}
