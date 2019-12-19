package com.xiaopu.customer.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.utils.security.Des;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/12 0012.
 */
public class CommentDoctorActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = CommentDoctorActivity.class.getSimpleName();

    private Context mContext;

    private RoundImageView riv_doctor_avatar;

    private LinearLayout ll_doctor_tag;

    private TextView tv_doctor_name;

    private TextView tv_doctor_department;

    private TextView tv_doctor_title;

    private TextView tv_doctor_hospital;

    private TextView tv_doctor_comment;

    private RadioGroup rg_comment;

    private EditText et_comment_content;

    private Button bt_submit;

    private SimpleDoctorOrder mSimpleDoctorOrder;

    private int doctorServices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_doctor);
        mContext = this;
        initActionBar("评价");
        initView();
        initData();// 加载数据
        setListener();
    }

    private void initView() {

        riv_doctor_avatar = (RoundImageView) findViewById(R.id.riv_doctor_avatar);
        tv_doctor_name = (TextView) findViewById(R.id.tv_doctor_name);
        tv_doctor_department = (TextView) findViewById(R.id.tv_doctor_department);
        tv_doctor_title = (TextView) findViewById(R.id.tv_doctor_title);
        tv_doctor_hospital = (TextView) findViewById(R.id.tv_doctor_hospital);
        tv_doctor_comment = (TextView) findViewById(R.id.tv_doctor_comment);
        rg_comment = (RadioGroup) findViewById(R.id.rg_comment);
        et_comment_content = (EditText) findViewById(R.id.et_comment_content);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        ll_doctor_tag = findViewById(R.id.ll_doctor_tag);
    }

    private void initData() {
        doctorServices = 3;
        Bundle bundle = getIntent().getExtras();
        mSimpleDoctorOrder = (SimpleDoctorOrder) bundle.getSerializable("SimpleDoctorOrder");
        if (mSimpleDoctorOrder != null) {
            getCommentState();
        }

    }

    private void getCommentState() {

        Map<String, Object> maps = new HashMap<>();
        maps.put("orderSn", mSimpleDoctorOrder.getOrder_sn());
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getCommentState, new HttpCallBack<String>() {
            @Override
            public void onSuccess(HttpResult result) {
                if (result.getCode() == 200) {
                    getDoctorInfo();
                } else if (result.getCode() == 317) {
                    finish();
                    T.showShort("该订单已评价");
                }
            }

            @Override
            public void onFail(String msg) {

            }
        });

       /* HttpUtils.getInstantce().getCommentState(mSimpleDoctorOrder.getOrder_sn(), new HttpConstant.SampleJsonResultListener<Feedback>() {
            @Override
            public void onSuccess(Feedback jsonData) {
                if (jsonData.getCode() == 200) {
                    getDoctorInfo();
                } else if (jsonData.getCode() == 307) {
                    finish();
                    T.showShort("该订单已评价");
                }
            }

            @Override
            public void onFailure(Feedback jsonData) {
                Log.d(LOG_TAG, "----获取失败");
            }
        });*/
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

/*
        HttpUtils.getInstantce().getDoctorInfoById(mSimpleDoctorOrder.getDoctor_id(), new HttpConstant.SampleJsonResultListener<Feedback<SimpleDoctorResult>>() {
            @Override
            public void onSuccess(Feedback<SimpleDoctorResult> jsonData) {
                initDoctorInfo(jsonData.getData());
            }

            @Override
            public void onFailure(Feedback<SimpleDoctorResult> jsonData) {

            }
        });
*/

    }

    private void setListener() {

        rg_comment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_smile:
                        doctorServices = 3;
                        break;
                    case R.id.rb_general:
                        doctorServices = 2;
                        break;
                    case R.id.rb_unhappy:
                        doctorServices = 1;
                        break;
                }
            }
        });
        bt_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:
                try {
                    String content = Des.encode(et_comment_content.getText().toString());

                    Map<String, Object> maps = new HashMap<>();
                    maps.put("orderSn", mSimpleDoctorOrder.getOrder_sn());
                    maps.put("doctorId", mSimpleDoctorOrder.getDoctor_id());
                    maps.put("content", content);
                    maps.put("service", doctorServices);
                    maps.put("commentType", mSimpleDoctorOrder.getOrder_category());
                    HttpUtils.getInstantce().showSweetDialog(getString(R.string.commiting));
                    HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_commentDoctor, new HttpCallBack<String>() {
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

/*
                    HttpUtils.getInstantce().commentDoctor(ApplicationXpClient.userInfoResult.getId(), mSimpleDoctorOrder.getOrder_sn(), mSimpleDoctorOrder.getDoctor_id(), content, doctorServices, mSimpleDoctorOrder.getOrder_category(), new HttpConstant.SampleJsonResultListener<Feedback>() {
                        @Override
                        public void onSuccess(Feedback jsonData) {
                            //TODO 成功就返回
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
                        public void onFailure(Feedback jsonData) {
                            if (jsonData == null) {
                                T.showShort("网络不通，请检查网络。");
                            } else {
                                T.showShort(jsonData.getMsg());
                            }
                        }
                    });
*/
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
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
}
