package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.EaseConstant;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ActivityChat;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.PersonalDoctorActivity;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.CommentAdapter;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.SimpleDoctorOrder;
import com.xiaopu.customer.data.jsonresult.CommentItem;
import com.xiaopu.customer.data.jsonresult.Doctor;
import com.xiaopu.customer.data.jsonresult.DoctorMessageOrderItem;
import com.xiaopu.customer.data.jsonresult.DoctorPrivateOrderItem;
import com.xiaopu.customer.data.jsonresult.SimpleDoctorResult;
import com.xiaopu.customer.utils.PixelUtil;
import com.xiaopu.customer.utils.RoundImageView;
import com.xiaopu.customer.utils.TimeUtils;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.utils.security.Des;
import com.xiaopu.customer.view.ListViewForScrollview;
import com.xiaopu.customer.view.LoadingView.LoadingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorInfoActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = DoctorInfoActivity.class.getSimpleName();

    private Context mContext;

    private RoundImageView riv_doctor_avatar;

    private TextView tv_doctor_name;

    private TextView tv_doctor_department;

    private TextView tv_doctor_title;

    private TextView tv_doctor_hospital;

    private TextView tv_doctor_good_at;

    private TextView tv_doctor_describe;

    private TextView tv_doctor_comment;

    private ListViewForScrollview lv_doctor_comment;

    private TextView tv_more_comment;

    private TextView tv_pooai_service;

    private TextView tv_attention;

    private LinearLayout ll_leave_message;

    private LinearLayout ll_private_doctor;

    private LinearLayout ll_doctor_tag;

    private LinearLayout ll_doctor_image;

    private TextView tv_leave_message_describe;

    private TextView tv_leave_message_price;

    private TextView tv_private_doctor_price;

    private LoadingView mLoadingView;

    private TextView tv_loading_fail;

    private TextView tv_no_data;

    private SimpleDoctorResult mSimpleDoctorResult;

    private CommentAdapter mAdapter;

    private List<CommentItem> mCommentList;

    private int mId;

    private int sizeOfPage = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_info);
        mContext = this;
        initActionBar("医生详情");
        initView();
        initData();
        initListener();
    }

    private void initView() {
        lv_doctor_comment = (ListViewForScrollview) findViewById(R.id.lv_doctor_comment);
        riv_doctor_avatar = (RoundImageView) findViewById(R.id.riv_doctor_avatar);
        tv_doctor_name = (TextView) findViewById(R.id.tv_doctor_name);
        tv_doctor_good_at = (TextView) findViewById(R.id.tv_doctor_good_at);
        tv_doctor_describe = (TextView) findViewById(R.id.tv_doctor_describe);
        tv_doctor_comment = (TextView) findViewById(R.id.tv_doctor_comment);
        tv_doctor_department = (TextView) findViewById(R.id.tv_doctor_department);
        tv_doctor_title = (TextView) findViewById(R.id.tv_doctor_title);
        tv_doctor_hospital = (TextView) findViewById(R.id.tv_doctor_hospital);
        mLoadingView = (LoadingView) findViewById(R.id.loading_view);
        tv_loading_fail = (TextView) findViewById(R.id.tv_loading_fail);
        tv_no_data = (TextView) findViewById(R.id.tv_no_data);

        tv_more_comment = (TextView) findViewById(R.id.tv_more_comment);
        tv_pooai_service = (TextView) findViewById(R.id.tv_pooai_service);
        tv_attention = (TextView) findViewById(R.id.tv_attention);
        ll_leave_message = (LinearLayout) findViewById(R.id.ll_leave_message);
        ll_private_doctor = (LinearLayout) findViewById(R.id.ll_private_doctor);
        tv_leave_message_describe = (TextView) findViewById(R.id.tv_leave_message_describe);
        tv_leave_message_price = (TextView) findViewById(R.id.tv_leave_message_price);
        tv_private_doctor_price = (TextView) findViewById(R.id.tv_private_doctor_private);
        ll_doctor_tag = findViewById(R.id.ll_doctor_tag);
        ll_doctor_image = findViewById(R.id.ll_doctor_image);

    }


    private void initData() {
        Bundle bundle = getIntent().getExtras();
        mSimpleDoctorResult = (SimpleDoctorResult) bundle.getSerializable("doctorInfo");
        mId = getIntent().getIntExtra("doctorId", -1);
        if (mSimpleDoctorResult == null && mId == -1)
            return;
        else if (mSimpleDoctorResult != null)
            mId = mSimpleDoctorResult.getDoctorId();
        getDoctorInfo();
    }

    private void getDoctorInfo() {

        Map<String, Object> maps = new HashMap<>();
        maps.put("doctorId", mId);
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getDoctorInfoById, new HttpCallBack<SimpleDoctorResult>() {
            @Override
            public void onSuccess(HttpResult result) {
                mSimpleDoctorResult = (SimpleDoctorResult) result.getData();
                initDoctorInfo();
            }

            @Override
            public void onFail(String msg) {

            }
        });

       /* HttpUtils.getInstantce().getDoctorInfoById(mId, new HttpConstant.SampleJsonResultListener<Feedback<SimpleDoctorResult>>() {
            @Override
            public void onSuccess(Feedback<SimpleDoctorResult> jsonData) {
                mSimpleDoctorResult = jsonData.getData();
                initDoctorInfo();
            }

            @Override
            public void onFailure(Feedback<SimpleDoctorResult> jsonData) {

            }
        });*/
    }

    private void initDoctorInfo() {
        tv_doctor_name.setText(mSimpleDoctorResult.getRealname());
        tv_doctor_hospital.setText(mSimpleDoctorResult.getHospital());
        tv_doctor_department.setText(mSimpleDoctorResult.getDepartment());
        tv_doctor_good_at.setText(mSimpleDoctorResult.getGoodAt());
        tv_doctor_describe.setText(mSimpleDoctorResult.getIntroduction());
        tv_doctor_title.setText(initTitle(mSimpleDoctorResult.getTitle()));
        tv_doctor_comment.setText(mSimpleDoctorResult.getCommentRate() * 100 + "%");
        tv_more_comment.setText(mSimpleDoctorResult.getCommentRate() * 100 + "%满意");
        ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + mSimpleDoctorResult.getAvatar(), riv_doctor_avatar, ApplicationXpClient.getmOptions(R.mipmap.user_accountpic));

        try {
            tv_doctor_good_at.setText(Des.decode(mSimpleDoctorResult.getGoodAt()));
            tv_doctor_describe.setText(Des.decode(mSimpleDoctorResult.getIntroduction()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ll_doctor_tag.removeAllViews();
        String tag = mSimpleDoctorResult.getTargets();
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
        ll_doctor_image.removeAllViews();
        String image = mSimpleDoctorResult.getIntroductionImage();
        if (image != null) {
            String[] images = image.split(",");
            for (String image1 : images) {
                ImageView imageView = new ImageView(mContext);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (PixelUtil.getScreenWidth(mContext) - 40) * 2 / 3);
                layoutParams.setMargins(0, 10, 0, 0);
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + image1, imageView, ApplicationXpClient.getmOptions(R.drawable.default_doctor_image));
                ll_doctor_image.addView(imageView);
            }
        }
        if (mSimpleDoctorResult.isFree()) {
            tv_leave_message_describe.setText("义诊咨询");
            tv_leave_message_price.setText(mSimpleDoctorResult.getClinicPrice() + "小普币/次");
        } else {
            tv_leave_message_describe.setText("图文咨询");
            tv_leave_message_price.setText(mSimpleDoctorResult.getOldPrice() + "小普币/次");
        }
        if (mSimpleDoctorResult.isAttention()) {
            tv_attention.setSelected(true);
        } else {
            tv_attention.setSelected(false);
        }
        tv_private_doctor_price.setText(mSimpleDoctorResult.getHalfYearPrice() + "小普币/半年");
        getDoctorComment();
        mCommentList = new ArrayList<>();
        mAdapter = new CommentAdapter(mCommentList, DoctorInfoActivity.this);
        lv_doctor_comment.setAdapter(mAdapter);
    }

    private void initListener() {
        tv_more_comment.setOnClickListener(this);
        tv_pooai_service.setOnClickListener(this);
        tv_attention.setOnClickListener(this);
        ll_leave_message.setOnClickListener(this);
        ll_private_doctor.setOnClickListener(this);
        tv_loading_fail.setOnClickListener(this);
    }

    private void getDoctorComment() {
        resetView(mLoadingView);
        if (mSimpleDoctorResult == null)
            return;
        int id = mSimpleDoctorResult.getDoctorId();

        Map<String, Object> maps = new HashMap<>();
        maps.put("doctorId", id);
        maps.put("pageNo", 1);
        maps.put("pageSize", 3);
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getDoctorCommentList, new HttpCallBack<List<CommentItem>>() {
            @Override
            public void onSuccess(HttpResult result) {
                List<CommentItem> commentItemList = (List<CommentItem>) result.getData();
                if (commentItemList.size() == 0) {
                    resetView(tv_no_data);
                    tv_no_data.setText("暂无评论");
                } else {
                    resetView(lv_doctor_comment);
                    mCommentList.addAll(commentItemList);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(String msg) {
                resetView(tv_loading_fail);
            }
        });


/*
        HttpUtils.getInstantce().getDoctorCommentList(id, 1, 3, new HttpConstant.SampleJsonResultListener<Feedback<List<CommentItem>>>() {
            @Override
            public void onSuccess(Feedback<List<CommentItem>> jsonData) {
                if (jsonData.getData().size() == 0) {
                    resetView(tv_no_data);
                    tv_no_data.setText("暂无评论");
                } else {
                    resetView(lv_doctor_comment);
                    mCommentList.addAll(jsonData.getData());
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Feedback<List<CommentItem>> jsonData) {
                resetView(tv_loading_fail);
            }
        });
*/

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
            case R.id.tv_more_comment:
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("doctorInfo", mSimpleDoctorResult);
                Intent mIntent = new Intent(mContext, UserCommentActivity.class);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
                break;
            case R.id.tv_pooai_service:
                Intent intentchat = new Intent(mContext, ActivityChat.class);
                Bundle bundle = new Bundle();
                bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
                bundle.putString(EaseConstant.EXTRA_DOCTOR_NAME, "小普客服");
                bundle.putString("userNick", ApplicationXpClient.userInfoResult.getNickname());
                bundle.putString(EaseConstant.EXTRA_DOCTOR_AVATAR, HttpConstant.Url_ImageServer + "user/avatar/2018/04/20/ced09609-c586-4218-834b-fd1e84ca48d3.jpg");
                bundle.putString(EaseConstant.EXTAR_USER_AVATAR, HttpConstant.Url_ImageServer + ApplicationXpClient.userInfoResult.getAvatar());
                bundle.putString(EaseConstant.EXTRA_USER_ID, "pooai_customer_service");
                bundle.putInt(EaseConstant.EXTRA_IS_SHOW, 1);
                intentchat.putExtras(bundle);
                startActivity(intentchat);
                break;
            case R.id.tv_attention:
                if (!tv_attention.isSelected()) {

                    Map<String, Object> maps = new HashMap<>();
                    maps.put("doctorId", mSimpleDoctorResult.getDoctorId());
                    HttpUtils.getInstantce().showSweetDialog(getString(R.string.attentioning));
                    HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_addAttentionToDoctor, new HttpCallBack<String>() {
                        @Override
                        public void onSuccess(HttpResult result) {
                            HttpUtils.getInstantce().changeRightSweetDialog(getString(R.string.attention_success));
                            tv_attention.setSelected(true);
                        }

                        @Override
                        public void onFail(String msg) {

                        }
                    });


                  /*  HttpUtils.getInstantce().attentionDoctor(mSimpleDoctorResult.getDoctorId(), new HttpConstant.SampleJsonResultListener<Feedback>() {
                        @Override
                        public void onSuccess(Feedback jsonData) {
                            tv_attention.setSelected(true);
                        }

                        @Override
                        public void onFailure(Feedback jsonData) {

                        }
                    });*/
                } else {
                    Map<String, Object> maps = new HashMap<>();
                    maps.put("doctorId", mSimpleDoctorResult.getDoctorId());
                    HttpUtils.getInstantce().showSweetDialog(getString(R.string.canceling));
                    HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_cancelAttentionToDoctor, new HttpCallBack<String>() {
                        @Override
                        public void onSuccess(HttpResult result) {
                            HttpUtils.getInstantce().changeRightSweetDialog(getString(R.string.cancel_success));
                            tv_attention.setSelected(false);
                        }

                        @Override
                        public void onFail(String msg) {

                        }
                    });

                   /* HttpUtils.getInstantce().cancelAttentionDoctor(mSimpleDoctorResult.getDoctorId(), new HttpConstant.SampleJsonResultListener<Feedback>() {
                        @Override
                        public void onSuccess(Feedback jsonData) {
                            tv_attention.setSelected(false);
                        }

                        @Override
                        public void onFailure(Feedback jsonData) {

                        }
                    });*/
                }
                break;
            case R.id.ll_leave_message:

                Map<String, Object> maps = new HashMap<>();
                maps.put("doctorId", mId);

                HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_effectMessageOrder, new HttpCallBack<DoctorMessageOrderItem>() {
                    @Override
                    public void onSuccess(HttpResult result) {
                        DoctorMessageOrderItem doctorMessageOrderItem = (DoctorMessageOrderItem) result.getData();
                        if (result.getCode() == 200) {
                            SimpleDoctorOrder simpleDoctorOrder = getSimpleDoctorOrder(doctorMessageOrderItem);
                            Intent mIntent = new Intent(mContext, MessageChatActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("simpleDoctorOrder", simpleDoctorOrder);
                            bundle.putInt("isShow", 1);
                            mIntent.putExtras(bundle);
                            startActivity(mIntent);
                        } else if (result.getCode() == 319) {
                            Intent mIntent1 = new Intent(DoctorInfoActivity.this, LeaveMessagesActivity.class);
                            Bundle mBundle1 = new Bundle();
                            mBundle1.putSerializable("doctorInfo", mSimpleDoctorResult);
                            mIntent1.putExtras(mBundle1);
                            startActivity(mIntent1);
                        }
                    }

                    @Override
                    public void onFail(String msg) {

                    }
                });

               /* HttpUtils.getInstantce().effectMessageOrder(mId, new HttpConstant.SampleJsonResultListener<Feedback<DoctorMessageOrderItem>>() {
                    @Override
                    public void onSuccess(Feedback<DoctorMessageOrderItem> jsonData) {
                        if (jsonData.getCode() == 200) {
                            SimpleDoctorOrder simpleDoctorOrder = getSimpleDoctorOrder(jsonData.getData());
                            Intent mIntent = new Intent(mContext, MessageChatActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("simpleDoctorOrder", simpleDoctorOrder);
                            bundle.putInt("isShow", 1);
                            mIntent.putExtras(bundle);
                            startActivity(mIntent);
                        } else if (jsonData.getCode() == 319) {
                            Intent mIntent1 = new Intent(DoctorInfoActivity.this, LeaveMessagesActivity.class);
                            Bundle mBundle1 = new Bundle();
                            mBundle1.putSerializable("doctorInfo", mSimpleDoctorResult);
                            mIntent1.putExtras(mBundle1);
                            startActivity(mIntent1);
                        }
                    }

                    @Override
                    public void onFailure(Feedback<DoctorMessageOrderItem> jsonData) {
                        Log.d(LOG_TAG, "获取状态失败");
                    }
                });*/

                break;
            case R.id.ll_private_doctor:

                Map<String, Object> order_map = new HashMap<>();
                order_map.put("doctorId", mId);
                HttpUtils.getInstantce().postWithHead(order_map, HttpConstant.Url_effectPrivateOrder, new HttpCallBack<DoctorPrivateOrderItem>() {
                    @Override
                    public void onSuccess(HttpResult result) {
                        DoctorPrivateOrderItem doctorPrivateOrderItem = (DoctorPrivateOrderItem) result.getData();
                        if (result.getCode() == 200) {
                            Intent intentchat = new Intent(mContext, ActivityChat.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
                            bundle.putString(EaseConstant.EXTRA_DOCTOR_NAME, doctorPrivateOrderItem.getRealname());
                            bundle.putString("userNick", ApplicationXpClient.userInfoResult.getNickname());
                            bundle.putString(EaseConstant.EXTRA_DOCTOR_AVATAR, HttpConstant.Url_ImageServer + doctorPrivateOrderItem.getDoctorAvatar());
                            bundle.putString(EaseConstant.EXTAR_USER_AVATAR, HttpConstant.Url_ImageServer + ApplicationXpClient.userInfoResult.getAvatar());
                            bundle.putString(EaseConstant.EXTRA_USER_ID, "doctor_" + doctorPrivateOrderItem.getDoctorId());
                            bundle.putInt(EaseConstant.EXTRA_IS_SHOW, 1);
                            intentchat.putExtras(bundle);
                            startActivity(intentchat);
                            //跳聊天界面
                        } else if (result.getCode() == 319) {
                            Intent mIntent = new Intent(DoctorInfoActivity.this, PersonalDoctorActivity.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putInt("priceOfHalfYear", mSimpleDoctorResult.getHalfYearPrice());
                            mBundle.putInt("priceOfYear", mSimpleDoctorResult.getYearPrice());
                            mBundle.putSerializable("doctorInfo", mSimpleDoctorResult);
                            mIntent.putExtras(mBundle);
                            startActivity(mIntent);
                        }
                    }

                    @Override
                    public void onFail(String msg) {

                    }
                });

               /* HttpUtils.getInstantce().effectPrivateOrder(mId, new HttpConstant.SampleJsonResultListener<Feedback<DoctorPrivateOrderItem>>() {
                    @Override
                    public void onSuccess(Feedback<DoctorPrivateOrderItem> jsonData) {
                        if (jsonData.getCode() == 200) {
                            Intent intentchat = new Intent(mContext, ActivityChat.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
                            bundle.putString(EaseConstant.EXTRA_DOCTOR_NAME, jsonData.getData().getRealname());
                            bundle.putString("userNick", ApplicationXpClient.userInfoResult.getNickname());
                            bundle.putString(EaseConstant.EXTRA_DOCTOR_AVATAR, HttpConstant.Url_ImageServer + jsonData.getData().getDoctorAvatar());
                            bundle.putString(EaseConstant.EXTAR_USER_AVATAR, HttpConstant.Url_ImageServer + ApplicationXpClient.userInfoResult.getAvatar());
                            bundle.putString(EaseConstant.EXTRA_USER_ID, "doctor_" + jsonData.getData().getDoctorId());
                            bundle.putInt(EaseConstant.EXTRA_IS_SHOW, 1);
                            intentchat.putExtras(bundle);
                            startActivity(intentchat);
                            //跳聊天界面
                        } else if (jsonData.getCode() == 319) {
                            Intent mIntent = new Intent(DoctorInfoActivity.this, PersonalDoctorActivity.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putInt("doctorId", mId);
                            mBundle.putInt("priceOfHalfYear", mSimpleDoctorResult.getHalfYearPrice());
                            mBundle.putInt("priceOfYear", mSimpleDoctorResult.getYearPrice());
                            mIntent.putExtras(mBundle);
                            startActivity(mIntent);
                        }
                    }

                    @Override
                    public void onFailure(Feedback<DoctorPrivateOrderItem> jsonData) {
                    }
                });*/
                break;
            case R.id.tv_loading_fail:
                getDoctorComment();
                break;
        }

    }

    private void resetView(View v) {
        lv_doctor_comment.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        tv_loading_fail.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.GONE);
        v.setVisibility(View.VISIBLE);
    }

    private SimpleDoctorOrder getSimpleDoctorOrder(DoctorMessageOrderItem message) {
        SimpleDoctorOrder mSimpleDoctorOrder = new SimpleDoctorOrder();
        mSimpleDoctorOrder.setDoctor_id(message.getDoctorId());
        mSimpleDoctorOrder.setDoctor_avatar(message.getDoctorAvatar());
        mSimpleDoctorOrder.setDoctor_name(message.getRealname());
        mSimpleDoctorOrder.setDoctor_department(message.getDepartment());
        if (message.getTitle() != null) {
            switch (message.getTitle()) {
                case 0:
                    mSimpleDoctorOrder.setDoctor_level(getString(R.string.chief_physician));
                    break;
                case 1:
                    mSimpleDoctorOrder.setDoctor_level(getString(R.string.deputy_chief_physician));
                    break;
                case 2:
                    mSimpleDoctorOrder.setDoctor_level(getString(R.string.doctor));
                    break;
                case 3:
                    mSimpleDoctorOrder.setDoctor_level(getString(R.string.resident));
                    break;
            }
        }
        mSimpleDoctorOrder.setOrder_category(0);
        mSimpleDoctorOrder.setOrder_date(TimeUtils.DateToString(message.getCreateTime()));
        mSimpleDoctorOrder.setOrder_sn(message.getOrderSn());
        mSimpleDoctorOrder.setOrder_id(Integer.parseInt(String.valueOf(message.getId())));
        mSimpleDoctorOrder.setOrder_price(message.getPay());

        switch (message.getProgressFlag()) {
            case 1:
                mSimpleDoctorOrder.setType(0);
                break;
            case 2:
                mSimpleDoctorOrder.setType(1);
                break;
            case 3:
                mSimpleDoctorOrder.setType(2);
                break;
            case 4:
                if (message.getCommentStatus() == 0) {
                    mSimpleDoctorOrder.setType(3);
                } else if (message.getCommentStatus() == 1) {
                    mSimpleDoctorOrder.setType(6);
                }
                break;
            case 5:
                if (message.getCommentStatus() == 0) {
                    mSimpleDoctorOrder.setType(4);
                } else if (message.getCommentStatus() == 1) {
                    mSimpleDoctorOrder.setType(7);
                }
                break;
            case 6:
                if (message.getCommentStatus() == 0) {
                    mSimpleDoctorOrder.setType(5);
                } else if (message.getCommentStatus() == 1) {
                    mSimpleDoctorOrder.setType(8);
                }
                break;
            case 7:
                mSimpleDoctorOrder.setType(8);
                break;
        }
        mSimpleDoctorOrder.setIsCanCancel(1);
        return mSimpleDoctorOrder;
    }
}
