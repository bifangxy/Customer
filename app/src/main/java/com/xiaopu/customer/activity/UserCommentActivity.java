package com.xiaopu.customer.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.CommentAdapter;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.CommentItem;
import com.xiaopu.customer.data.jsonresult.SimpleDoctorResult;
import com.xiaopu.customer.utils.PixelUtil;
import com.xiaopu.customer.utils.RoundImageView;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.CusPtrClassicFrameLayout;
import com.xiaopu.customer.view.LoadingView.LoadingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler2;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

/**
 * Created by Administrator on 2018/3/28.
 */

public class UserCommentActivity extends ActivityBase {
    private static final String LOG_TAG = UserCommentActivity.class.getSimpleName();

    private Context mContext;

    private RoundImageView riv_doctor_avatar;

    private TextView tv_doctor_name;

    private TextView tv_doctor_department;

    private TextView tv_doctor_title;

    private TextView tv_doctor_hospital;

    private TextView tv_doctor_comment;

    private LoadingView mLoadingView;

    private TextView tv_loading_fail;

    private TextView tv_no_data;

    private LinearLayout ll_doctor_tag;

    private ListView lv_doctor_comment;

    private CusPtrClassicFrameLayout mLayout;

    private List<CommentItem> dataList;

    private CommentAdapter mAdapter;

    private int doctor_id;

    private int pageNo;

    private boolean isNoMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_comment);
        mContext = this;
        initActionBar(getResources().getString(R.string.user_comment));
        initView();
        initData();
        initListener();
    }

    private void initView() {
        riv_doctor_avatar = findViewById(R.id.riv_doctor_avatar);
        tv_doctor_name = findViewById(R.id.tv_doctor_name);
        tv_doctor_department = findViewById(R.id.tv_doctor_department);
        tv_doctor_title = findViewById(R.id.tv_doctor_title);
        tv_doctor_hospital = findViewById(R.id.tv_doctor_hospital);
        tv_doctor_comment = findViewById(R.id.tv_doctor_comment);
        ll_doctor_tag = findViewById(R.id.ll_doctor_tag);
        lv_doctor_comment = findViewById(R.id.lv_doctor_comment);
        mLayout = findViewById(R.id.ptr_layout);
        mLoadingView = findViewById(R.id.loading_view);
        tv_loading_fail = findViewById(R.id.tv_loading_fail);
        tv_no_data = findViewById(R.id.tv_no_data);

    }

    private void initData() {
        isNoMore = false;
        dataList = new ArrayList<>();
        mAdapter = new CommentAdapter(dataList, mContext);
        lv_doctor_comment.setAdapter(mAdapter);

        Bundle bundle = getIntent().getExtras();
        SimpleDoctorResult mSimpleDoctorResult = (SimpleDoctorResult) bundle.getSerializable("doctorInfo");
        if (mSimpleDoctorResult != null) {
            tv_doctor_name.setText(mSimpleDoctorResult.getRealname());
            tv_doctor_hospital.setText(mSimpleDoctorResult.getHospital());
            tv_doctor_department.setText(mSimpleDoctorResult.getDepartment());
            tv_doctor_title.setText(initTitle(mSimpleDoctorResult.getTitle()));
            tv_doctor_comment.setText(mSimpleDoctorResult.getCommentRate() * 100 + "%");
            ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + mSimpleDoctorResult.getAvatar(), riv_doctor_avatar, ApplicationXpClient.getmOptions(R.mipmap.user_accountpic));
            doctor_id = mSimpleDoctorResult.getDoctorId();

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
        }
        initPtrLayout();

        pageNo = 1;
        getCommentList(pageNo, false);
    }

    private void initPtrLayout() {
        mLayout.setMode(PtrFrameLayout.Mode.BOTH);
        mLayout.setLastUpdateTimeRelateObject(this);
        mLayout.setResistanceHeader(3.0f);
        mLayout.setRatioOfHeaderHeightToRefresh(1.2f);
        mLayout.setDurationToClose(1000);
        mLayout.setPullToRefresh(false);
        mLayout.setKeepHeaderWhenRefresh(true);
        //以下为自定义header需要
        StoreHouseHeader header = new StoreHouseHeader(mContext);
        header.setPadding(0, PtrLocalDisplay.designedDP2px(10), 0, PtrLocalDisplay.designedDP2px(10));
        header.setTextColor(this.getResources().getColor(R.color.new_blue));
        header.initWithString("LOADING...");
        mLayout.setDurationToCloseHeader(1500);
        mLayout.setHeaderView(header);
        mLayout.addPtrUIHandler(header);
        mLayout.disableWhenHorizontalMove(true);
        onRefreshOrLoadMore();
    }

    private void initListener() {

    }

    private void onRefreshOrLoadMore() {

        mLayout.setPtrHandler(new PtrHandler2() {
            @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {
                return PtrDefaultHandler2.checkContentCanBePulledUp(frame, lv_doctor_comment, footer);
            }

            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                if (isNoMore) {
                    T.showShort("暂无更多");
                    mLayout.refreshComplete();
                } else {
                    pageNo++;
                    getCommentList(pageNo, false);
                }

            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler2.checkContentCanBePulledDown(frame, lv_doctor_comment, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getCommentList(1, true);
            }
        });
    }

    private void getCommentList(final int no, boolean isRefresh) {
        if (no == 1 && !isRefresh)
            resetView(mLoadingView);

        Map<String, Object> maps = new HashMap<>();
        maps.put("doctorId", doctor_id);
        maps.put("pageNo", no);
        maps.put("pageSize", 10);
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getDoctorCommentList, new HttpCallBack<List<CommentItem>>() {
            @Override
            public void onSuccess(HttpResult result) {
                List<CommentItem> commentItemList = (List<CommentItem>) result.getData();
                mLayout.refreshComplete();
                if (no == 1) {
                    if (commentItemList.size() == 0)
                        resetView(tv_no_data);
                    else {
                        resetView(lv_doctor_comment);
                    }
                    dataList.clear();
                } else {
                    if (commentItemList.size() == 0)
                        T.showShort("暂无更多评论");
                }
                if (commentItemList.size() < 10)
                    isNoMore = true;
                else
                    isNoMore = false;
                dataList.addAll(commentItemList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(String msg) {
                mLayout.refreshComplete();
                if (no == 1) {
                    resetView(tv_loading_fail);
                } else {
                    pageNo--;
                    T.showShort(getString(R.string.internet_error));
                }
            }
        });

       /* HttpUtils.getInstantce().getDoctorCommentList(doctor_id, no, 10, new HttpConstant.SampleJsonResultListener<Feedback<List<CommentItem>>>() {
            @Override
            public void onSuccess(Feedback<List<CommentItem>> jsonData) {
                mLayout.refreshComplete();
                if (no == 1) {
                    if (jsonData.getData().size() == 0)
                        resetView(tv_no_data);
                    else {
                        resetView(lv_doctor_comment);
                    }
                    dataList.clear();
                } else {
                    if (jsonData.getData().size() == 0)
                        T.showShort("暂无更多评论");
                }
                if (jsonData.getData().size() < 10)
                    isNoMore = true;
                else
                    isNoMore = false;
                dataList.addAll(jsonData.getData());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Feedback<List<CommentItem>> jsonData) {
                mLayout.refreshComplete();
                if (no == 1) {
                    resetView(tv_loading_fail);
                } else {
                    pageNo--;
                    T.showShort(getString(R.string.internet_error));
                }
            }
        });*/
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

    private void resetView(View view) {
        lv_doctor_comment.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        tv_loading_fail.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }
}
