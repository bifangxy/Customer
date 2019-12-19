package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.DoctorListAdapter;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.SimpleDoctorResult;
import com.xiaopu.customer.utils.StarLeverUtil;
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
 * Created by Administrator on 2017/7/10.
 * 明星医生
 */

@SuppressWarnings("unused")
public class DoctorStarActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = DoctorStarActivity.class.getSimpleName();

    private Context mContext;

    private LinearLayout ll_order_one;

    private LinearLayout ll_order_two;

    private LinearLayout ll_order_three;

    private ImageView iv_doctor_head_one;

    private TextView tv_doctor_name_one;

    private TextView tv_department_one;

    private ImageView iv_doctor_head_two;

    private TextView tv_doctor_name_two;

    private TextView tv_department_two;

    private ImageView iv_doctor_head_three;

    private TextView tv_doctor_name_three;

    private TextView tv_department_three;

    private TextView tv_no_one_comment;

    private TextView tv_no_two_comment;

    private TextView tv_no_three_comment;

    private ListViewForScrollview lv_doctor;

    private DoctorListAdapter mAdapter;

    private List<SimpleDoctorResult> dataList;

    private List<SimpleDoctorResult> dataListThree;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_star);
        mContext = this;
        initActionBar("明星医生");
        initView();
        initData();
        initListener();
    }

    private void initView() {
        ll_order_one = findViewById(R.id.ll_order_one);
        ll_order_two = findViewById(R.id.ll_order_two);
        ll_order_three = findViewById(R.id.ll_order_three);

        iv_doctor_head_one = findViewById(R.id.iv_doctor_head_one);
        iv_doctor_head_two = findViewById(R.id.iv_doctor_head_two);
        iv_doctor_head_three = findViewById(R.id.iv_doctor_head_three);

        tv_doctor_name_one = findViewById(R.id.tv_doctor_name_one);
        tv_doctor_name_two = findViewById(R.id.tv_doctor_name_two);
        tv_doctor_name_three = findViewById(R.id.tv_doctor_name_three);

        tv_department_one = findViewById(R.id.tv_doctor_department_one);
        tv_department_two = findViewById(R.id.tv_doctor_department_two);
        tv_department_three = findViewById(R.id.tv_doctor_department_three);

        tv_no_one_comment = findViewById(R.id.tv_no_one_comment);
        tv_no_two_comment = findViewById(R.id.tv_no_two_comment);
        tv_no_three_comment = findViewById(R.id.tv_no_three_comment);

        lv_doctor = findViewById(R.id.lv_doctor);


    }

    private void initData() {
        dataList = new ArrayList<>();
        dataListThree = new ArrayList<>();
        mAdapter = new DoctorListAdapter(mContext, dataList);
        lv_doctor.setAdapter(mAdapter);
        getDoctorList();
    }

    private void getDoctorList() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("pageNo", 1);
        maps.put("pageSize", 8);

        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_orderByComment, new HttpCallBack<List<SimpleDoctorResult>>() {
            @Override
            public void onSuccess(HttpResult result) {
                List<SimpleDoctorResult> simpleDoctorResultList = (List<SimpleDoctorResult>) result.getData();
                dataList.addAll(simpleDoctorResultList);
                dataListThree.add(dataList.get(0));
                dataListThree.add(dataList.get(1));
                dataListThree.add(dataList.get(2));

                dataList.remove(0);
                dataList.remove(0);
                dataList.remove(0);
                mAdapter.notifyDataSetChanged();
                initShow();
            }

            @Override
            public void onFail(String msg) {

            }
        });

/*
        HttpUtils.getInstantce().getExpertDoctorlist(HttpConstant.Url_orderByComment, 1, 8, new HttpConstant.SampleJsonResultListener<Feedback<List<SimpleDoctorResult>>>() {
            @Override
            public void onSuccess(Feedback<List<SimpleDoctorResult>> jsonData) {
                dataList.addAll(jsonData.getData());
                dataListThree.add(dataList.get(0));
                dataListThree.add(dataList.get(1));
                dataListThree.add(dataList.get(2));

                dataList.remove(0);
                dataList.remove(0);
                dataList.remove(0);
                mAdapter.notifyDataSetChanged();
                initShow();
            }

            @Override
            public void onFailure(Feedback<List<SimpleDoctorResult>> jsonData) {

            }
        });
*/
    }

    private void initShow() {
        ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + dataListThree.get(0).getAvatar(), iv_doctor_head_one, ApplicationXpClient.getmOptions(R.mipmap.user_accountpic));
        ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + dataListThree.get(1).getAvatar(), iv_doctor_head_two, ApplicationXpClient.getmOptions(R.mipmap.user_accountpic));
        ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + dataListThree.get(2).getAvatar(), iv_doctor_head_three, ApplicationXpClient.getmOptions(R.mipmap.user_accountpic));

        tv_doctor_name_one.setText(dataListThree.get(0).getRealname());
        tv_doctor_name_two.setText(dataListThree.get(1).getRealname());
        tv_doctor_name_three.setText(dataListThree.get(2).getRealname());

        switch (dataListThree.get(0).getTitle()) {
            case 0:
                tv_department_one.setText(dataListThree.get(0).getDepartment() + "\n" + "主任医师");
                break;
            case 1:
                tv_department_one.setText(dataListThree.get(0).getDepartment() + "\n" + "副主任医师");
                break;
            case 2:
                tv_department_one.setText(dataListThree.get(0).getDepartment() + "\n" + "主治医师");
                break;
            case 3:
                tv_department_one.setText(dataListThree.get(0).getDepartment() + "\n" + "住院医师");
                break;
        }

        switch (dataListThree.get(1).getTitle()) {
            case 0:
                tv_department_two.setText(dataListThree.get(1).getDepartment() + "\n" + "主任医师");
                break;
            case 1:
                tv_department_two.setText(dataListThree.get(1).getDepartment() + "\n" + "副主任医师");
                break;
            case 2:
                tv_department_two.setText(dataListThree.get(1).getDepartment() + "\n" + "主治医师");
                break;
            case 3:
                tv_department_two.setText(dataListThree.get(1).getDepartment() + "\n" + "住院医师");
                break;
        }

        switch (dataListThree.get(2).getTitle()) {
            case 0:
                tv_department_three.setText(dataListThree.get(2).getDepartment() + "\n" + "主任医师");
                break;
            case 1:
                tv_department_three.setText(dataListThree.get(2).getDepartment() + "\n" + "副主任医师");
                break;
            case 2:
                tv_department_three.setText(dataListThree.get(2).getDepartment() + "\n" + "主治医师");
                break;
            case 3:
                tv_department_three.setText(dataListThree.get(2).getDepartment() + "\n" + "住院医师");
                break;
        }
        tv_no_one_comment.setText(dataListThree.get(0).getCommentRate() * 100 + "%");
        tv_no_two_comment.setText(dataListThree.get(1).getCommentRate() * 100 + "%");
        tv_no_three_comment.setText(dataListThree.get(2).getCommentRate() * 100 + "%");
    }

    private void initListener() {
        ll_order_one.setOnClickListener(this);
        ll_order_two.setOnClickListener(this);
        ll_order_three.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        Bundle mBundle = new Bundle();
        switch (v.getId()) {
            case R.id.ll_order_one:
                mBundle.putSerializable("doctorInfo", dataListThree.get(0));
                break;
            case R.id.ll_order_two:
                mBundle.putSerializable("doctorInfo", dataListThree.get(1));
                break;
            case R.id.ll_order_three:
                mBundle.putSerializable("doctorInfo", dataListThree.get(2));
                break;
        }
        Intent mIntent = new Intent(mContext, DoctorInfoActivity.class);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
    }
}
