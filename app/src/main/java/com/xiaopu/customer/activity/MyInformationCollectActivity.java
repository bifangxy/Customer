package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.InfoAdapter;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.Information;
import com.xiaopu.customer.data.jsonresult.InformationCollectionItemUser;
import com.xiaopu.customer.data.jsonresult.UserThumbInformation;
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

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler2;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

public class MyInformationCollectActivity extends ActivityBase {
    private static final String LOG_TAG = MyInformationCollectActivity.class.getSimpleName();

    private Context mContext;

    private MyClick mClick;

    private InfoAdapter mAdapter;

    private ListView lv_info;

    private PtrClassicFrameLayout mLayout;

    private TextView tv_loading_fail;

    private TextView tv_no_data;

    private LoadingView mLoadingView;

    private int mPage = 1;

    private int sizeOfPage = 8;

    private List<Information> mInfoCollectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_collect);
        initActionBar("我的收藏");
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initListener() {

        mLayout.setPtrHandler(new PtrHandler2() {
            @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {

                return PtrDefaultHandler2.checkContentCanBePulledUp(frame, lv_info, footer);
            }

            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                getCollectData(++mPage, 8);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler2.checkContentCanBePulledDown(frame, lv_info, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getCollectData(1, 8);
            }
        });
    }

    private void initView() {

        mLayout = (PtrClassicFrameLayout) findViewById(R.id.ptr_mLayout);
        lv_info = (ListView) findViewById(R.id.lv_info);
        tv_loading_fail = (TextView) findViewById(R.id.tv_loading_fail);
        tv_no_data = (TextView) findViewById(R.id.tv_no_data);
        mLoadingView = (LoadingView) findViewById(R.id.loading_view);
    }

    private void initData() {
        mClick = new MyClick();
        mInfoCollectList = new ArrayList<>();
        mAdapter = new InfoAdapter(mInfoCollectList, mContext);
        lv_info.setAdapter(mAdapter);

        resetView(mLoadingView);
        getCollectData(1, 8);

        mLayout.setMode(PtrFrameLayout.Mode.BOTH);
        mLayout.setLastUpdateTimeRelateObject(this);
        mLayout.setResistanceHeader(1.7f); // 您还可以单独设置脚,头\
        mLayout.setRatioOfHeaderHeightToRefresh(1.2f);
        mLayout.setDurationToClose(1000);  // 您还可以单独设置脚,头
        mLayout.setPullToRefresh(false);
        mLayout.setKeepHeaderWhenRefresh(true);
        //以下为自定义header需要
        StoreHouseHeader header = new StoreHouseHeader(this);
        header.setPadding(0, PtrLocalDisplay.designedDP2px(10), 0, PtrLocalDisplay.designedDP2px(10));
        header.setTextColor(this.getResources().getColor(R.color.new_blue));
        header.initWithString("LOADING...");
        mLayout.setDurationToCloseHeader(1500);
        mLayout.setHeaderView(header);
        mLayout.addPtrUIHandler(header);

        StoreHouseHeader footer = new StoreHouseHeader(this);
        footer.setPadding(0, PtrLocalDisplay.dp2px(5), 0, PtrLocalDisplay.dp2px(5));
        footer.setTextColor(this.getResources().getColor(R.color.new_blue));
        footer.initWithString("LOADING...");

        mLayout.setFooterView(footer);
        mLayout.addPtrUIHandler(footer);
        mLayout.disableWhenHorizontalMove(true);

    }

    private void getCollectData(final int pageNo, int pageSize) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("pageNo", pageNo);
        maps.put("pageSize", pageSize);
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getUsercollectionInformation, new HttpCallBack<UserThumbInformation>() {
            @Override
            public void onSuccess(HttpResult result) {
                UserThumbInformation userThumbInformation = (UserThumbInformation) result.getData();
                mLayout.refreshComplete();
                sizeOfPage = userThumbInformation.getInformationCollectionItemUserList().size();
                if (sizeOfPage == 0 && pageNo == 1) {
                    //数据为空，此处需要占位图
                    resetView(tv_no_data);
                    tv_no_data.setText(R.string.no_collected_info);
                } else if (sizeOfPage == 0 && pageNo != 1) {
                    resetView(lv_info);
                    T.showShort("已经是最后一页");
                } else if (sizeOfPage != 0) {
                    resetView(lv_info);
                    if (pageNo == 1) {
                        mInfoCollectList.clear();
                    }
                    List<InformationCollectionItemUser> informationCollectionItemUserList = userThumbInformation.getInformationCollectionItemUserList();
                    for (int i = 0; i < informationCollectionItemUserList.size(); i++) {
                        InformationCollectionItemUser informationCollectionItemUser = informationCollectionItemUserList.get(i);
                        Information information = new Information();
                        information.setContent(informationCollectionItemUser.getIntroduction());
                        information.setTitle(informationCollectionItemUser.getTitle());
                        information.setCounts(informationCollectionItemUser.getCounts());
                        information.setPhoto(informationCollectionItemUser.getPhoto());
                        information.setTypeName(informationCollectionItemUser.getTypeName());
                        information.setCreateTime(informationCollectionItemUser.getCreateTime());
                        information.setUrl(informationCollectionItemUser.getUrl());
                        information.setId(informationCollectionItemUser.getInformationId());
                        mInfoCollectList.add(information);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(String msg) {
                mLayout.refreshComplete();
                resetView(tv_loading_fail);
            }
        });

/*
        HttpUtils.getInstantce().getUsercollectionInformation(pageNo, pageSize, new HttpConstant.SampleJsonResultListener<Feedback<UserThumbInformation>>() {
            @Override
            public void onSuccess(Feedback<UserThumbInformation> jsonData) {
                mLayout.refreshComplete();
                sizeOfPage = jsonData.getData().getInformationCollectionItemUserList().size();
                if (sizeOfPage == 0 && pageNo == 1) {
                    //数据为空，此处需要占位图
                    resetView(tv_no_data);
                    tv_no_data.setText(R.string.no_collected_info);
                } else if (sizeOfPage == 0 && pageNo != 1) {
                    resetView(lv_info);
                    T.showShort("已经是最后一页");
                } else if (sizeOfPage != 0) {
                    resetView(lv_info);
                    if (pageNo == 1) {
                        mInfoCollectList.clear();
                    }
                    List<InformationCollectionItemUser> informationCollectionItemUserList = jsonData.getData().getInformationCollectionItemUserList();
                    for (int i = 0; i < informationCollectionItemUserList.size(); i++) {
                        InformationCollectionItemUser informationCollectionItemUser = informationCollectionItemUserList.get(i);
                        Information information = new Information();
                        information.setUrl(informationCollectionItemUser.getIntroduction());
                        information.setTitle(informationCollectionItemUser.getTitle());
                        information.setCounts(informationCollectionItemUser.getCounts());
                        information.setPhoto(informationCollectionItemUser.getPhoto());
                        information.setTypeName(informationCollectionItemUser.getTypeName());
                        information.setCreateTime(informationCollectionItemUser.getCreateTime());
                        information.setId(informationCollectionItemUser.getInformationId());
                        mInfoCollectList.add(information);
                    }
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Feedback<UserThumbInformation> jsonData) {
                mLayout.refreshComplete();
                resetView(tv_loading_fail);
            }
        });
*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            int mCollectType = data.getIntExtra("mCollectType", -1);
            int position = data.getIntExtra("position", -1);
            if (mCollectType == 1) {
                mInfoCollectList.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class MyClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_return:
                    finish();
                    break;
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void resetView(View v) {
        lv_info.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        tv_loading_fail.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.lv_info:
                lv_info.setVisibility(View.VISIBLE);
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
