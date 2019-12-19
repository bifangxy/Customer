package com.xiaopu.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.InfoAdapter;
import com.xiaopu.customer.adapter.MyPagerAdapter;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.InfoBean;
import com.xiaopu.customer.data.jsonresult.Information;
import com.xiaopu.customer.data.jsonresult.InformationType;
import com.xiaopu.customer.data.jsonresult.UserCollectionInformation;
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

/**
 * Created by Administrator on 2017/5/22.
 */

public class InfoActivity extends ActivityBase {

    private static final String LOG_TAG = InfoActivity.class.getSimpleName();

    private Context mContext;

    private SmartTabLayout mTableLayout;

    private ViewPager mViewpager;

    private List<String> mTitleList;

    private List<View> mViewList;

    private LayoutInflater mInflater;

    private List<InformationType> typeList;

    private List<Information> informationList;

    private List<Integer> mCollectIdList;

    private List<Integer> mThumbIdList;

    private List<String> titleList = new ArrayList<String>(); //标题链表

    private PtrClassicFrameLayout mLayout;

    private ListView mListView;

    private LoadingView mLoadingView;

    private TextView tv_loading_fail;

    private TextView tv_no_data;

    private List<InfoBean> infoBeanList = new ArrayList<>();

    private InfoAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        mContext = this;
        initActionBar("健康资讯");
        initView();
        initData();
        setTabLayout();
    }

    private void initView() {
        mTableLayout = findViewById(R.id.tb_info_type);
        mViewpager = findViewById(R.id.view_pager_info);
        mTitleList = new ArrayList<>();
        mViewList = new ArrayList<>();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        typeList = (ArrayList<InformationType>) bundle.getSerializable("type_list");
        mInflater = LayoutInflater.from(mContext);
        for (int i = 0; i < typeList.size(); i++) {
            InformationType informationType = typeList.get(i);
            View view = mInflater.inflate(R.layout.view_pager_info, null);
            InfoBean infoBean = new InfoBean();
            infoBean.setFirst(true);
            mViewList.add(view);
            infoBeanList.add(infoBean);
            titleList.add(informationType.getTitle());
        }
        setData(0);
    }


    private void setTabLayout() {
        MyPagerAdapter mAdapter = new MyPagerAdapter(InfoActivity.this, mViewList, mTitleList);
        mAdapter.setmTitleList(titleList);
        mViewpager.setAdapter(mAdapter);
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setData(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTableLayout.setViewPager(mViewpager);
    }

    private void onRefreshOrLoadMore(final int id, final int position) {

        mLayout.setPtrHandler(new PtrHandler2() {
            @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {

                return PtrDefaultHandler2.checkContentCanBePulledUp(frame, mListView, footer);
            }

            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                if (infoBeanList.get(position).isNoMore()) {
                    T.showShort("暂无更多");
                    infoBeanList.get(position).getmLayout().refreshComplete();
                } else {
                    int pageNo = infoBeanList.get(position).getPageNo() + 1;
                    infoBeanList.get(position).setPageNo(pageNo);
                    getInformation(infoBeanList.get(position).getPageNo(), 10, id, false, position);
                }
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler2.checkContentCanBePulledDown(frame, mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                infoBeanList.get(position).setPageNo(1);
                getInformation(infoBeanList.get(position).getPageNo(), 10, id, true, position);
            }
        });

    }

    private void setData(final int position) {
        View view = mViewList.get(position);
        mLayout = view.findViewById(R.id.ptr_mLayout);
        mListView = view.findViewById(R.id.type_listView);
        mLoadingView = view.findViewById(R.id.loading_view);
        tv_loading_fail = view.findViewById(R.id.tv_loading_fail);
        tv_no_data = view.findViewById(R.id.tv_no_data);


        informationList = new ArrayList<>();
        mCollectIdList = new ArrayList<>();
        mThumbIdList = new ArrayList<>();


        infoBeanList.get(position).setInformationList(informationList);
//        infoBeanList.get(position).setCollectIdList(mCollectIdList);
//        infoBeanList.get(position).setThumbIdList(mThumbIdList);

        //第一次进来，设置pageNo = 1；
        infoBeanList.get(position).setPageNo(1);

        resetView(mLoadingView);
        mAdapter = new InfoAdapter(infoBeanList.get(position).getInformationList(), mContext);
        mListView.setAdapter(mAdapter);


        mLayout.setMode(PtrFrameLayout.Mode.BOTH);
        mLayout.setLastUpdateTimeRelateObject(this);
        // the following are default settings
        mLayout.setResistanceHeader(1.7f); // 您还可以单独设置脚,头\
        mLayout.setRatioOfHeaderHeightToRefresh(1.2f);
        mLayout.setDurationToClose(1000);  // 您还可以单独设置脚,头
        // default is false
        mLayout.setPullToRefresh(false);

        // default is true
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

        infoBeanList.get(position).setmAdapter(mAdapter);
        infoBeanList.get(position).setmLayout(mLayout);
        infoBeanList.get(position).setmListView(mListView);

        final InformationType informationType = typeList.get(position);

        onRefreshOrLoadMore(informationType.getId(), position);

        tv_loading_fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetView(mLoadingView);
                getInformation(1, 10, informationType.getId(), true, position);
            }
        });

        getInformation(infoBeanList.get(position).getPageNo(), 10, informationType.getId(), true, position);

    }

    private void resetView(View view) {
        mListView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        tv_loading_fail.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getInformation(final int page, final int size, int id, final boolean isRefresh, final int position) {

        Map<String, Object> maps = new HashMap<>();
        maps.put("pageNo", page);
        maps.put("pageSize", size);
        maps.put("type", id);

        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getInformation, new HttpCallBack<List<Information>>() {
            @Override
            public void onSuccess(HttpResult result) {
                List<Information> informationList = (List<Information>) result.getData();
                infoBeanList.get(position).setFirst(false);
                infoBeanList.get(position).getmLayout().refreshComplete();

                if (informationList.size() < size) {
                    infoBeanList.get(position).setNoMore(true);
                } else {
                    infoBeanList.get(position).setNoMore(false);
                }

                if (page == 1) {
                    if (informationList.size() == 0) {
                        resetView(tv_no_data);
                    } else {
                        resetView(mListView);
                        infoBeanList.get(position).getInformationList().clear();
                        infoBeanList.get(position).getInformationList().addAll(informationList);
                        infoBeanList.get(position).getmAdapter().notifyDataSetChanged();
                    }
                } else {
                    if (informationList.size() == 0) {
                        T.showShort("暂无更多...");
                    } else {
                        infoBeanList.get(position).getInformationList().addAll(informationList);
                        infoBeanList.get(position).getmAdapter().notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFail(String msg) {
                if (page == 1) {
                    resetView(tv_loading_fail);
                } else {
                    infoBeanList.get(position).getmLayout().refreshComplete();
                    infoBeanList.get(position).setPageNo(page - 1);
                }
            }
        });


       /* HttpUtils.getInstantce().getInformation(page, size, id, new HttpConstant.SampleJsonResultListener<Feedback<UserCollectionInformation>>() {
            @Override
            public void onSuccess(Feedback<UserCollectionInformation> jsonData) {
                infoBeanList.get(position).setFirst(false);
                infoBeanList.get(position).getmLayout().refreshComplete();

                if (jsonData.getData().getInfomationList().size() < size) {
                    infoBeanList.get(position).setNoMore(true);
                } else {
                    infoBeanList.get(position).setNoMore(false);
                }

                if (page == 1) {
                    if (jsonData.getData().getInfomationList().size() == 0) {
                        resetView(tv_no_data);
                    } else {
                        resetView(mListView);
                        infoBeanList.get(position).getInformationList().clear();
                        infoBeanList.get(position).getInformationList().addAll(jsonData.getData().getInfomationList());
                        infoBeanList.get(position).getmAdapter().notifyDataSetChanged();
                    }
                } else {
                    if (jsonData.getData().getInfomationList().size() == 0) {
                        T.showShort("暂无更多...");
                    } else {
                        infoBeanList.get(position).getInformationList().addAll(jsonData.getData().getInfomationList());
                        infoBeanList.get(position).getmAdapter().notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Feedback<UserCollectionInformation> jsonData) {
                if (page == 1) {
                    resetView(tv_loading_fail);
                } else {
                    infoBeanList.get(position).getmLayout().refreshComplete();
                    infoBeanList.get(position).setPageNo(page - 1);
                }
            }
        });*/
    }
}
