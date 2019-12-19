package com.xiaopu.customer.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.SimpleDoctorOrder;
import com.xiaopu.customer.data.jsonresult.SimpleDoctorResult;
import com.xiaopu.customer.utils.PixelUtil;
import com.xiaopu.customer.utils.RoundImageView;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.utils.security.Des;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/11.
 */

public class AppealOrderActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = AppealOrderActivity.class.getSimpleName();

    private Context mContext;

    private TextView tv_order_state;

    private TextView tv_order_sn;

    private RoundImageView riv_doctor_avatar;

    private LinearLayout ll_doctor_tag;

    private TextView tv_doctor_name;

    private TextView tv_doctor_department;

    private TextView tv_doctor_title;

    private TextView tv_doctor_hospital;

    private TextView tv_doctor_comment;

    private EditText et_appeal_content;

    private Button bt_submit;

    private SimpleDoctorOrder mSimpleDoctorOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appeal_order);
        initActionBar("申诉");
        mContext = this;
        initView();
        initData();
        initListener();

    }

    private void initView() {
        tv_order_state = findViewById(R.id.tv_order_state);
        tv_order_sn = findViewById(R.id.tv_order_sn);
        riv_doctor_avatar = findViewById(R.id.riv_doctor_avatar);
        tv_doctor_name = findViewById(R.id.tv_doctor_name);
        tv_doctor_department = findViewById(R.id.tv_doctor_department);
        tv_doctor_title = findViewById(R.id.tv_doctor_title);
        tv_doctor_hospital = findViewById(R.id.tv_doctor_hospital);
        tv_doctor_comment = findViewById(R.id.tv_doctor_comment);
        ll_doctor_tag = findViewById(R.id.ll_doctor_tag);
        bt_submit = findViewById(R.id.bt_submit);
        et_appeal_content = findViewById(R.id.et_appeal_content);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        mSimpleDoctorOrder = (SimpleDoctorOrder) bundle.getSerializable("SimpleDoctorOrder");
        if (mSimpleDoctorOrder != null) {
            tv_order_sn.setText("订单号:" + mSimpleDoctorOrder.getOrder_sn());
            tv_order_state.setText("订单已关闭");
            getDoctorInfo();
        }
    }

    private void initListener() {
        bt_submit.setOnClickListener(this);
    }


    private void getDoctorInfo() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("doctorId", mSimpleDoctorOrder.getDoctor_id());
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getDoctorInfoById, new HttpCallBack<SimpleDoctorResult>() {
            @Override
            public void onSuccess(HttpResult result) {
                SimpleDoctorResult simpleDoctorResult = (SimpleDoctorResult) result.getData();
                initDoctorInfo(simpleDoctorResult);
            }

            @Override
            public void onFail(String msg) {

            }
        });


       /* HttpUtils.getInstantce().getDoctorInfoById(mSimpleDoctorOrder.getDoctor_id(), new HttpConstant.SampleJsonResultListener<Feedback<SimpleDoctorResult>>() {
            @Override
            public void onSuccess(Feedback<SimpleDoctorResult> jsonData) {
                initDoctorInfo(jsonData.getData());
            }

            @Override
            public void onFailure(Feedback<SimpleDoctorResult> jsonData) {

            }
        });*/

    }

    private void initDoctorInfo(SimpleDoctorResult simpleDoctorResult) {
        tv_doctor_name.setText(simpleDoctorResult.getRealname());
        tv_doctor_hospital.setText(simpleDoctorResult.getHospital());
        tv_doctor_department.setText(simpleDoctorResult.getDepartment());
        tv_doctor_title.setText(initTitle(simpleDoctorResult.getTitle()));
        tv_doctor_comment.setText(simpleDoctorResult.getCommentRate() * 100 + "%");
        ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + simpleDoctorResult.getAvatar(), riv_doctor_avatar, ApplicationXpClient.getmOptions(R.mipmap.user_accountpic));

        ll_doctor_tag.removeAllViews();
        String tag = simpleDoctorResult.getTargets();
        if (tag != null) {
            String[] tags = tag.split(",");
            for (String tag1 : tags) {
                TextView textView = new TextView(mContext);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(PixelUtil.dp2px(mContext, 50), PixelUtil.dp2px(mContext, 20));
                lp.setMargins(0, 0, 20, 0);
                textView.setLayoutParams(lp);
                textView.setBackgroundColor(getResources().getColor(R.color.accent));
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setText(tag1);
                textView.setTextSize(10);
                ll_doctor_tag.addView(textView);
            }
        }
    }

    private String initTitle(int type) {
        String title = "";
        switch (type) {
            case 0:
                title = "主任医师";
                break;
            case 1:
                title = "副主任医师";
                break;
            case 2:
                title = "主治医师";
                break;
            case 3:
                title = "住院医师";
                break;
        }
        return title;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:
                String content = "";
                String image = "";
                try {
                    content = Des.encode(et_appeal_content.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Map<String, Object> maps = new HashMap<>();
                maps.put("doctorId", mSimpleDoctorOrder.getDoctor_id());
                maps.put("orderSn", mSimpleDoctorOrder.getOrder_sn());
                maps.put("content", content);
                maps.put("image", image);

                HttpUtils.getInstantce().showSweetDialog("提交中...");
                HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_commitOrderAppeal, new HttpCallBack<String>() {
                    @Override
                    public void onSuccess(HttpResult result) {
                        SweetAlertDialog mSweetDialog = HttpUtils.getInstantce().getSweetDialog();
                        mSweetDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        mSweetDialog.setTitleText("提示");
                        mSweetDialog.setContentText("提交成功");
                        mSweetDialog.setConfirmText("确定");
                        mSweetDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                setResult(3);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onFail(String msg) {

                    }
                });


//                HttpUtils.getInstantce().commitOrderAppeal(mSimpleDoctorOrder.getDoctor_id(), mSimpleDoctorOrder.getOrder_sn(), content, image, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
//                    @Override
//                    public void onSuccess(Feedback<String> jsonData) {
//                        SweetAlertDialog mSweetDialog = HttpUtils.getInstantce().getSweetDialog();
//                        mSweetDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
//                        mSweetDialog.setTitleText("提示");
//                        mSweetDialog.setContentText("提交成功");
//                        mSweetDialog.setConfirmText("确定");
//                        mSweetDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                sweetAlertDialog.dismiss();
//                                setResult(3);
//                                finish();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFailure(Feedback<String> jsonData) {
//
//                    }
//                });
                break;
        }
    }
}
