package com.xiaopu.customer.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.xiaopu.customer.ActivityChat;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.activity.AppealOrderActivity;
import com.xiaopu.customer.activity.CommentDoctorActivity;
import com.xiaopu.customer.MainActivity;
import com.xiaopu.customer.R;
import com.xiaopu.customer.activity.MessageChatActivity;
import com.xiaopu.customer.adapter.SimpleOrderAdapter;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.SimpleDoctorOrder;
import com.xiaopu.customer.data.jsonresult.DoctorIndex;
import com.xiaopu.customer.data.jsonresult.DoctorMessageOrderItem;
import com.xiaopu.customer.data.jsonresult.DoctorPrivateOrderItem;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.TimeUtils;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.CusPtrClassicFrameLayout;
import com.xiaopu.customer.view.LoadingView.LoadingView;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;
import com.xiaopu.customer.view.swipeMenuListView.SwipeMenu;
import com.xiaopu.customer.view.swipeMenuListView.SwipeMenuCreator;
import com.xiaopu.customer.view.swipeMenuListView.SwipeMenuItem;
import com.xiaopu.customer.view.swipeMenuListView.SwipeMenuListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

/**
 * Created by Administrator on 2017/5/16.
 */

public class MyServiceFragment extends Fragment {
    private static final String LOG_TAG = MyServiceFragment.class.getSimpleName();

    private static final int TYPE_CURRENT = 0;

    private static final int TYPE_HISTORY = 1;

    private Context mContext;

    private View rootView;

    private View view_current;

    private View view_history;

    private CusPtrClassicFrameLayout mLayout;

    private SwipeMenuListView mListView;

    private Button bt_current_service;

    private Button bt_history_service;

    private LoadingView mLoadingView;

    private TextView tv_loading_fail;

    private TextView tv_no_data;

    private List<SimpleDoctorOrder> orderList;

    private SimpleDoctorOrder mSimpleDoctorOrder;

    private MyClickListener mClick;

    private SimpleOrderAdapter mAdapter;

    private String request_url;

    private int current_type;

    private int select_position;

    private SwipeMenuItem openItem_detail_red;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                    initMessageCount();
                    break;
            }
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_myservice, null);
        initView();
        initData();
        initListener();

        return rootView;
    }

    private void initView() {
        mLayout = (CusPtrClassicFrameLayout) rootView.findViewById(R.id.ptr_layout);
        mListView = (SwipeMenuListView) rootView.findViewById(R.id.lv_order_list);
        bt_current_service = (Button) rootView.findViewById(R.id.bt_current_service);
        bt_history_service = (Button) rootView.findViewById(R.id.bt_history_service);

        view_current = rootView.findViewById(R.id.view_current);
        view_history = rootView.findViewById(R.id.view_history);

        mLoadingView = (LoadingView) rootView.findViewById(R.id.loading_view);
        tv_loading_fail = (TextView) rootView.findViewById(R.id.tv_loading_fail);
        tv_no_data = (TextView) rootView.findViewById(R.id.tv_no_data);

    }

    private void initData() {
        current_type = TYPE_CURRENT;
        mClick = new MyClickListener();
        orderList = new ArrayList<>();
        mAdapter = new SimpleOrderAdapter(mContext, orderList);
        mListView.setAdapter(mAdapter);
        initLayout();
        initSwipeListView();
        getCurrentService(current_type, false);
    }

    private void initSwipeListView() {

        //灰色的取消按钮
        final SwipeMenuItem openItem_cancel_gray = new SwipeMenuItem(
                mContext);
        openItem_cancel_gray.setBackground(mContext.getDrawable(R.color.item_gray));
        openItem_cancel_gray.setWidth(200);
        openItem_cancel_gray.setTitle(getString(R.string.cancel));
        openItem_cancel_gray.setTitleSize(18);
        openItem_cancel_gray.setTitleColor(Color.WHITE);

        //红色的取消按钮
        final SwipeMenuItem openItem_cancel_red = new SwipeMenuItem(
                mContext);
        openItem_cancel_red.setBackground(mContext.getDrawable(R.color.item_red));
        openItem_cancel_red.setWidth(200);
        openItem_cancel_red.setTitle(getString(R.string.cancel));
        openItem_cancel_red.setTitleSize(18);
        openItem_cancel_red.setTitleColor(Color.WHITE);


        //灰色的完成按钮
        final SwipeMenuItem openItem_finish_gray = new SwipeMenuItem(
                mContext);
        openItem_finish_gray.setBackground(mContext.getDrawable(R.color.item_gray));
        openItem_finish_gray.setWidth(200);
        openItem_finish_gray.setTitle(getString(R.string.finish));
        openItem_finish_gray.setTitleSize(18);
        openItem_finish_gray.setTitleColor(Color.WHITE);

        //红色的完成按钮
        final SwipeMenuItem openItem_finish_red = new SwipeMenuItem(
                mContext);
        openItem_finish_red.setBackground(mContext.getDrawable(R.color.item_red));
        openItem_finish_red.setWidth(200);
        openItem_finish_red.setTitle(getString(R.string.finish));
        openItem_finish_red.setTitleSize(18);
        openItem_finish_red.setTitleColor(Color.WHITE);

        //黄色的评价按钮
        final SwipeMenuItem openItem_comment_red = new SwipeMenuItem(
                mContext);
        openItem_comment_red.setBackground(mContext.getDrawable(R.color.item_yellow));
        openItem_comment_red.setWidth(200);
        openItem_comment_red.setTitle(getString(R.string.comment));
        openItem_comment_red.setTitleSize(18);
        openItem_comment_red.setTitleColor(Color.WHITE);

        //红色的申诉按钮
        final SwipeMenuItem openItem_appeal_red = new SwipeMenuItem(
                mContext);
        openItem_appeal_red.setBackground(mContext.getDrawable(R.color.item_green));
        openItem_appeal_red.setWidth(200);
        openItem_appeal_red.setTitle(getString(R.string.appeal));
        openItem_appeal_red.setTitleSize(18);
        openItem_appeal_red.setTitleColor(Color.WHITE);

        //红色的删除按钮
        final SwipeMenuItem openItem_delete_red = new SwipeMenuItem(
                mContext);
        openItem_delete_red.setBackground(mContext.getDrawable(R.color.item_red));
        openItem_delete_red.setWidth(200);
        openItem_delete_red.setTitle(getString(R.string.delete));
        openItem_delete_red.setTitleSize(18);
        openItem_delete_red.setTitleColor(Color.WHITE);


        //红色的详情按钮
        openItem_detail_red = new SwipeMenuItem(
                mContext);
        openItem_detail_red.setBackground(mContext.getDrawable(R.color.item_yellow));
        openItem_detail_red.setWidth(200);
        openItem_detail_red.setTitle(getString(R.string.detail));
        openItem_detail_red.setTitleSize(18);
        openItem_detail_red.setTitleColor(Color.WHITE);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                switch (menu.getViewType()) {
                    case 0:
                        menu.addMenuItem(openItem_cancel_gray);
                        break;
                    case 1:
                        menu.addMenuItem(openItem_cancel_red);
                        break;
                    case 2:
                        menu.addMenuItem(openItem_finish_red);
                        break;
                    case 3:
                        menu.addMenuItem(openItem_comment_red);
                        menu.addMenuItem(openItem_appeal_red);
                        break;
                    case 4:
                        menu.addMenuItem(openItem_comment_red);
                        menu.addMenuItem(openItem_delete_red);
                        break;
                    case 5:
                        menu.addMenuItem(openItem_comment_red);
                        menu.addMenuItem(openItem_delete_red);
                        break;
                    case 6:
                        menu.addMenuItem(openItem_appeal_red);
                        menu.addMenuItem(openItem_delete_red);
                        break;
                    case 7:
                        menu.addMenuItem(openItem_delete_red);
                        break;
                    case 8:
                        menu.addMenuItem(openItem_delete_red);
                        break;
                    case 9:
                        menu.addMenuItem(openItem_finish_gray);
                        break;
                    case 10:
                        menu.addMenuItem(openItem_comment_red);
                        break;
                    case 11:
                        menu.addMenuItem(openItem_delete_red);
                        break;
                    default:
                        break;
                }
            }
        };
        mListView.setMenuCreator(creator);

    }

    private void initLayout() {

        mLayout.setMode(PtrFrameLayout.Mode.REFRESH);
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
        onRefresh();
    }

    private void initListener() {
        bt_history_service.setOnClickListener(mClick);
        bt_current_service.setOnClickListener(mClick);
        tv_loading_fail.setOnClickListener(mClick);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        switch (menu.getViewType()) {
                            case 0:
                                T.showShort("该订单暂不能取消");
                                break;
                            case 1:
                                cancelMessageOrder(position);
                                break;
                            case 2:
                                finishOrder(position);
                                break;
                            case 3:
                                commentDoctor(position);
                                break;
                            case 4:
                                commentDoctor(position);
                                break;
                            case 5:
                                commentDoctor(position);
                                break;
                            case 6:
                                appealOrder(position);
                                break;
                            case 7:
                                deleteOrder(position);
                                break;
                            case 8:
                                deleteOrder(position);
                                break;
                            case 9:
//                                finishOrder(position);
                                T.showShort("该订单不能手动完成");
                                break;
                            case 10:
//                                commentDoctor(position);
                                break;
                            case 11:
                                break;
                        }
                        break;
                    case 1:
                        switch (menu.getViewType()) {
                            case 3:
                                appealOrder(position);
                                break;
                            case 4:
                                deleteOrder(position);
                                break;
                            case 5:
                                deleteOrder(position);
                                break;
                            case 6:
                                deleteOrder(position);
                                break;
                            default:
                                break;
                        }
                        break;
                }
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SimpleDoctorOrder simpleDoctorOrder = orderList.get(position);
                if (simpleDoctorOrder.getOrder_category() == 0) {
                    //留言
                    Intent mIntent = new Intent(mContext, MessageChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("simpleDoctorOrder", simpleDoctorOrder);
                    if (current_type == TYPE_CURRENT) {
                        bundle.putInt("isShow", 1);
                    } else {
                        bundle.putInt("isShow", 0);
                    }
                    mIntent.putExtras(bundle);
                    startActivity(mIntent);
                } else if (simpleDoctorOrder.getOrder_category() == 2) {
                    //私人医生
                    Intent intent_chat = new Intent(mContext, ActivityChat.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
                    bundle.putString(EaseConstant.EXTRA_DOCTOR_NAME, simpleDoctorOrder.getDoctor_name());
                    bundle.putString("userNick", ApplicationXpClient.userInfoResult.getNickname());
                    bundle.putString(EaseConstant.EXTRA_DOCTOR_AVATAR, HttpConstant.Url_ImageServer + simpleDoctorOrder.getDoctor_avatar());
                    bundle.putString(EaseConstant.EXTAR_USER_AVATAR, HttpConstant.Url_ImageServer + ApplicationXpClient.userInfoResult.getAvatar());
                    bundle.putString(EaseConstant.EXTRA_USER_ID, "doctor_" + simpleDoctorOrder.getDoctor_id());
                    if (current_type == TYPE_CURRENT) {
                        bundle.putInt(EaseConstant.EXTRA_IS_SHOW, 1);
                    } else {
                        bundle.putInt(EaseConstant.EXTRA_IS_SHOW, 0);
                    }
                    intent_chat.putExtras(bundle);
                    startActivityForResult(intent_chat, 0);
                }

            }
        });

    }

    private void onRefresh() {

        mLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler2.checkContentCanBePulledDown(frame, mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getCurrentService(current_type, true);
            }
        });

    }


    private void getCurrentService(final int type, final boolean isRefresh) {
        if (type == TYPE_CURRENT) {
            request_url = HttpConstant.Url_GetUserDoctorOrder;
        } else {
            request_url = HttpConstant.Url_histolyService;
        }
        if (!isRefresh) {
            resetView(mLoadingView);
        }

        HttpUtils.getInstantce().postNoHead(request_url, new HttpCallBack<DoctorIndex>() {
            @Override
            public void onSuccess(HttpResult result) {
                DoctorIndex doctorIndex = (DoctorIndex) result.getData();
                mLayout.refreshComplete();
                orderList.clear();
                List<DoctorMessageOrderItem> messageOrderList = doctorIndex.getDoctorMessageOrderItemList();
                List<DoctorPrivateOrderItem> privateOrderList = doctorIndex.getDoctorPrivateOrderItemList();
                if (messageOrderList.size() == 0 && privateOrderList.size() == 0) {
                    resetView(tv_no_data);
                    if (type == TYPE_CURRENT) {
                        tv_no_data.setText(R.string.no_current_service);
                    } else if (type == TYPE_HISTORY) {
                        tv_no_data.setText(R.string.no_history_service);
                    }
                } else {
                    resetView(mListView);
                    for (int i = 0; i < messageOrderList.size(); i++) {
                        DoctorMessageOrderItem message = messageOrderList.get(i);
                        mSimpleDoctorOrder = new SimpleDoctorOrder();
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
                        if (type == TYPE_CURRENT) {
                            if (message.getIsReply() == 0) {
                                mSimpleDoctorOrder.setOrder_state(getString(R.string.unanswered));
                                if (message.getProgressFlag() == 1) {
                                    mSimpleDoctorOrder.setType(0);
                                } else {
                                    mSimpleDoctorOrder.setType(1);
                                }
                            } else {
                                mSimpleDoctorOrder.setOrder_state(getString(R.string.replied));
                                mSimpleDoctorOrder.setType(2);
                            }
                        } else {
                            if (message.getProgressFlag() == 7) {
                                mSimpleDoctorOrder.setOrder_state("已取消");
                                mSimpleDoctorOrder.setType(7);
                            } else {
                                if (message.getCommentStatus() == 0) {
                                    if (message.getProgressFlag() == 4) {
                                        mSimpleDoctorOrder.setOrder_state(getString(R.string.no_comment));
                                        mSimpleDoctorOrder.setType(3);
                                    } else if (message.getProgressFlag() == 5) {
                                        mSimpleDoctorOrder.setOrder_state("申诉中");
                                        mSimpleDoctorOrder.setType(4);
                                    } else if (message.getProgressFlag() == 6) {
                                        mSimpleDoctorOrder.setOrder_state("未评价");
                                        mSimpleDoctorOrder.setType(5);
                                    }
                                } else if (message.getCommentStatus() == 1) {
                                    if (message.getProgressFlag() == 4) {
                                        mSimpleDoctorOrder.setOrder_state(getString(R.string.already_comment));
                                        mSimpleDoctorOrder.setType(6);
                                    } else if (message.getProgressFlag() == 5) {
                                        mSimpleDoctorOrder.setOrder_state("申诉中");
                                        mSimpleDoctorOrder.setType(7);
                                    } else if (message.getProgressFlag() == 6) {
                                        mSimpleDoctorOrder.setOrder_state(getString(R.string.already_comment));
                                        mSimpleDoctorOrder.setType(8);
                                    }
                                }
                            }
                        }
                        mSimpleDoctorOrder.setIsCanCancel(1);
                        orderList.add(mSimpleDoctorOrder);
                    }

                    for (int i = 0; i < privateOrderList.size(); i++) {
                        DoctorPrivateOrderItem mPrivate = privateOrderList.get(i);
                        mSimpleDoctorOrder = new SimpleDoctorOrder();
                        mSimpleDoctorOrder.setDoctor_id(mPrivate.getDoctorId());
                        mSimpleDoctorOrder.setDoctor_avatar(mPrivate.getDoctorAvatar());
                        mSimpleDoctorOrder.setDoctor_name(mPrivate.getRealname());
                        mSimpleDoctorOrder.setDoctor_department(mPrivate.getDepartment());
                        if (isAdded())
                            if (mPrivate.getTitle() != null) {
                                switch (mPrivate.getTitle()) {
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
                        mSimpleDoctorOrder.setOrder_category(2);
                        mSimpleDoctorOrder.setOrder_date(TimeUtils.DateToString(mPrivate.getCreateTime()));
                        mSimpleDoctorOrder.setOrder_sn(mPrivate.getOrderSn());
                        mSimpleDoctorOrder.setOrder_id(Integer.parseInt(String.valueOf(mPrivate.getId())));
                        mSimpleDoctorOrder.setOrder_price(mPrivate.getPay());
                        if (type == TYPE_CURRENT) {
                            if (isAdded())
                                mSimpleDoctorOrder.setOrder_state(getString(R.string.Validity) + TimeUtils.getEndTime(mPrivate.getEndTime()));
                            mSimpleDoctorOrder.setType(9);
                        } else {
                            if (mPrivate.getCommentStatus() == 0) {
                                if (isAdded())
                                    mSimpleDoctorOrder.setOrder_state(getString(R.string.no_comment));
                                mSimpleDoctorOrder.setType(10);
                            } else if (mPrivate.getCommentStatus() == 1) {
                                if (isAdded())
                                    mSimpleDoctorOrder.setOrder_state(getString(R.string.already_comment));
                                mSimpleDoctorOrder.setType(11);
                            }
                        }
                        mSimpleDoctorOrder.setEndTime(mPrivate.getEndTime());
                        mSimpleDoctorOrder.setIsCanCancel(1);
                        orderList.add(mSimpleDoctorOrder);
                    }
                    mHandler.sendEmptyMessage(1);
                }
            }

            @Override
            public void onFail(String msg) {
                if (!isRefresh) {
                    resetView(tv_loading_fail);
                } else {
                    mLayout.refreshComplete();
                }
            }
        });

/*
        HttpUtils.getInstantce().getUserDoctorOrder(request_url, new HttpConstant.SampleJsonResultListener<Feedback<DoctorIndex>>() {
            @Override
            public void onSuccess(Feedback<DoctorIndex> jsonData) {
                mLayout.refreshComplete();
                orderList.clear();
                List<DoctorMessageOrderItem> messageOrderList = jsonData.getData().getDoctorMessageOrderItemList();
                List<DoctorPrivateOrderItem> privateOrderList = jsonData.getData().getDoctorPrivateOrderItemList();
                if (messageOrderList.size() == 0 && privateOrderList.size() == 0) {
                    resetView(tv_no_data);
                    if (type == TYPE_CURRENT) {
                        tv_no_data.setText(R.string.no_current_service);
                    } else if (type == TYPE_HISTORY) {
                        tv_no_data.setText(R.string.no_history_service);
                    }
                } else {
                    resetView(mListView);
                    for (int i = 0; i < messageOrderList.size(); i++) {
                        DoctorMessageOrderItem message = messageOrderList.get(i);
                        mSimpleDoctorOrder = new SimpleDoctorOrder();
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
                        if (type == TYPE_CURRENT) {
                            if (message.getIsReply() == 0) {
                                mSimpleDoctorOrder.setOrder_state(getString(R.string.unanswered));
                                if (message.getProgressFlag() == 1) {
                                    mSimpleDoctorOrder.setType(0);
                                } else {
                                    mSimpleDoctorOrder.setType(1);
                                }
                            } else {
                                mSimpleDoctorOrder.setOrder_state(getString(R.string.replied));
                                mSimpleDoctorOrder.setType(2);
                            }
                        } else {
                            if (message.getProgressFlag() == 7) {
                                mSimpleDoctorOrder.setOrder_state("已取消");
                                mSimpleDoctorOrder.setType(7);
                            } else {
                                if (message.getCommentStatus() == 0) {
                                    if (message.getProgressFlag() == 4) {
                                        mSimpleDoctorOrder.setOrder_state(getString(R.string.no_comment));
                                        mSimpleDoctorOrder.setType(3);
                                    } else if (message.getProgressFlag() == 5) {
                                        mSimpleDoctorOrder.setOrder_state("申诉中");
                                        mSimpleDoctorOrder.setType(4);
                                    } else if (message.getProgressFlag() == 6) {
                                        mSimpleDoctorOrder.setOrder_state("未评价");
                                        mSimpleDoctorOrder.setType(5);
                                    }
                                } else if (message.getCommentStatus() == 1) {
                                    if (message.getProgressFlag() == 4) {
                                        mSimpleDoctorOrder.setOrder_state(getString(R.string.already_comment));
                                        mSimpleDoctorOrder.setType(6);
                                    } else if (message.getProgressFlag() == 5) {
                                        mSimpleDoctorOrder.setOrder_state("申诉中");
                                        mSimpleDoctorOrder.setType(7);
                                    } else if (message.getProgressFlag() == 6) {
                                        mSimpleDoctorOrder.setOrder_state(getString(R.string.already_comment));
                                        mSimpleDoctorOrder.setType(8);
                                    }
                                }
                            }
                        }
                        mSimpleDoctorOrder.setIsCanCancel(1);
                        orderList.add(mSimpleDoctorOrder);
                    }

                    for (int i = 0; i < privateOrderList.size(); i++) {
                        DoctorPrivateOrderItem mPrivate = privateOrderList.get(i);
                        mSimpleDoctorOrder = new SimpleDoctorOrder();
                        mSimpleDoctorOrder.setDoctor_id(mPrivate.getDoctorId());
                        mSimpleDoctorOrder.setDoctor_avatar(mPrivate.getDoctorAvatar());
                        mSimpleDoctorOrder.setDoctor_name(mPrivate.getRealname());
                        mSimpleDoctorOrder.setDoctor_department(mPrivate.getDepartment());
                        if (mPrivate.getTitle() != null) {
                            switch (mPrivate.getTitle()) {
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
                        mSimpleDoctorOrder.setOrder_category(2);
                        mSimpleDoctorOrder.setOrder_date(TimeUtils.DateToString(mPrivate.getCreateTime()));
                        mSimpleDoctorOrder.setOrder_sn(mPrivate.getOrderSn());
                        mSimpleDoctorOrder.setOrder_id(Integer.parseInt(String.valueOf(mPrivate.getId())));
                        mSimpleDoctorOrder.setOrder_price(mPrivate.getPay());
                        if (type == TYPE_CURRENT) {
                            mSimpleDoctorOrder.setOrder_state(getString(R.string.Validity) + TimeUtils.getEndTime(mPrivate.getEndTime()));
                            mSimpleDoctorOrder.setType(9);
                        } else {
                            if (mPrivate.getCommentStatus() == 0) {
                                mSimpleDoctorOrder.setOrder_state(getString(R.string.no_comment));
                                mSimpleDoctorOrder.setType(10);
                            } else if (mPrivate.getCommentStatus() == 1) {
                                mSimpleDoctorOrder.setOrder_state(getString(R.string.already_comment));
                                mSimpleDoctorOrder.setType(11);
                            }
                        }
                        mSimpleDoctorOrder.setEndTime(mPrivate.getEndTime());
                        mSimpleDoctorOrder.setIsCanCancel(1);
                        orderList.add(mSimpleDoctorOrder);
                    }
                    mHandler.sendEmptyMessage(1);
//                  ;
                }

            }

            @Override
            public void onFailure(Feedback<DoctorIndex> jsonData) {
                if (!isRefresh) {
                    resetView(tv_loading_fail);
                } else {
                    mLayout.refreshComplete();
                }
                Log.d(LOG_TAG, "获取失败");
            }
        });
*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 3) {
            SimpleDoctorOrder simpleDoctorOrder = orderList.get(select_position);
            if (isAdded())
                simpleDoctorOrder.setOrder_state(getString(R.string.already_comment));
            simpleDoctorOrder.setType(3);
            orderList.remove(select_position);
            orderList.add(select_position, simpleDoctorOrder);
            mAdapter.notifyDataSetChanged();
            mListView.setAdapter(mAdapter);
        } else if (requestCode == 2 && resultCode == 3) {
            SimpleDoctorOrder simpleDoctorOrder = orderList.get(select_position);
            simpleDoctorOrder.setOrder_state("已申诉");
            if (simpleDoctorOrder.getType() == 3) {
                simpleDoctorOrder.setType(4);
            } else if (simpleDoctorOrder.getType() == 6) {
                simpleDoctorOrder.setType(7);
            }
            orderList.remove(select_position);
            orderList.add(select_position, simpleDoctorOrder);
            mAdapter.notifyDataSetChanged();
            mListView.setAdapter(mAdapter);
        }
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_current_service:
                    if (current_type != TYPE_CURRENT) {
                        getCurrentService(TYPE_CURRENT, false);
                        current_type = TYPE_CURRENT;
                        view_current.setVisibility(View.VISIBLE);
                        view_history.setVisibility(View.INVISIBLE);
                    }
                    break;
                case R.id.bt_history_service:
                    if (current_type != TYPE_HISTORY) {
                        getCurrentService(TYPE_HISTORY, false);
                        current_type = TYPE_HISTORY;
                        view_current.setVisibility(View.INVISIBLE);
                        view_history.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.tv_loading_fail:
                    if (current_type == TYPE_CURRENT) {
                        getCurrentService(TYPE_CURRENT, false);
                    } else {
                        getCurrentService(TYPE_HISTORY, false);
                    }
                    break;
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
            if (mAdapter != null) {
                mHandler.sendEmptyMessage(1);
            }
        } else {
            EMClient.getInstance().chatManager().removeMessageListener(emMessageListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
        mHandler.sendEmptyMessage(1);
        getCurrentService(current_type, true);
    }

    private void resetView(View v) {
        mListView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        tv_loading_fail.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.lv_order_list:
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

    private void initMessageCount() {
        if (orderList.size() != 0) {
            int count = 0;
            for (int i = 0; i < orderList.size(); i++) {
                SimpleDoctorOrder simpleDoctorOrder = orderList.get(i);
                if (simpleDoctorOrder.getOrder_category() == 2) {
                    EMConversation conversation = EMClient.getInstance().chatManager().getConversation("doctor_" + simpleDoctorOrder.getDoctor_id());
                    if (conversation != null) {
                        count = count + conversation.getUnreadMsgCount();
                    }
                }

            }
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setCount(count);
        }
    }

    private EMMessageListener emMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            if (mAdapter != null) {
                mHandler.sendEmptyMessage(1);
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageRead(List<EMMessage> list) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {

        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };

    /**
     * 点击显示订单详情
     */
    private void showOrderDetail() {

    }

    /**
     * 评论订单
     */

    private void commentDoctor(int position) {
        select_position = position;
        Intent intent = new Intent(mContext, CommentDoctorActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("SimpleDoctorOrder", orderList.get(position));
        intent.putExtras(mBundle);
        startActivityForResult(intent, 1);
    }

    /**
     * 完成订单
     */
    private void finishOrder(final int select_position) {
        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.sure_finish_order))
                .setContentText(getString(R.string.finsh_order_discribe))
                .setCancelText(getString(R.string.cancel))
                .setConfirmText(getString(R.string.sure))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        Map<String, Object> maps = new HashMap<>();
                        maps.put("orderSn", orderList.get(select_position).getOrder_sn());
                        HttpUtils.getInstantce().showSweetDialog(getString(R.string.finishing));
                        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_confirmMessageOrder, new HttpCallBack<String>() {
                            @Override
                            public void onSuccess(HttpResult result) {
                                HttpUtils.getInstantce().showSweetDialog(getString(R.string.order_finish));
                                orderList.remove(select_position);
                                mAdapter.notifyDataSetChanged();
                                mListView.setAdapter(mAdapter);
                            }

                            @Override
                            public void onFail(String msg) {

                            }
                        });

                        /*HttpUtils.getInstantce().confirmMessageOrder(orderList.get(select_position).getOrder_sn(), new HttpConstant.SampleJsonResultListener<Feedback>() {
                            @Override
                            public void onSuccess(Feedback jsonData) {
                                orderList.remove(select_position);
                                mAdapter.notifyDataSetChanged();
                                mListView.setAdapter(mAdapter);
                            }

                            @Override
                            public void onFailure(Feedback jsonData) {

                            }
                        });*/
                    }
                })
                .show();
    }

    /**
     * 取消订单
     */
    private void cancelMessageOrder(final int position) {
        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.tip))
                .setContentText(getString(R.string.sure_cancel))
                .setConfirmText(getString(R.string.sure))
                .setCancelText(getString(R.string.cancel))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        Map<String, Object> maps = new HashMap<>();
                        maps.put("orderSn", orderList.get(position).getOrder_sn());
                        HttpUtils.getInstantce().showSweetDialog();
                        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_CancelMessageOrder, new HttpCallBack<String>() {
                            @Override
                            public void onSuccess(HttpResult result) {
                                HttpUtils.getInstantce().changeRightSweetDialog(getString(R.string.cancel_success));
                                orderList.remove(position);
                                mAdapter.notifyDataSetChanged();
                                mListView.setAdapter(mAdapter);
                            }

                            @Override
                            public void onFail(String msg) {

                            }
                        });

                        /*HttpUtils.getInstantce().cancelMessageOrder(orderList.get(position).getOrder_sn(), new HttpConstant.SampleJsonResultListener<Feedback>() {
                            @Override
                            public void onSuccess(Feedback jsonData) {
                                orderList.remove(position);
                                mAdapter.notifyDataSetChanged();
                                mListView.setAdapter(mAdapter);
                            }

                            @Override
                            public void onFailure(Feedback jsonData) {
                            }
                        });*/
                    }
                })
                .show();
    }

    /**
     * 删除订单
     */
    private void deleteOrder(final int position) {
        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.tip))
                .setContentText(getString(R.string.sure_delete))
                .setConfirmText(getString(R.string.sure))
                .setCancelText(getString(R.string.cancel))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        SimpleDoctorOrder simpleDoctorOrder = orderList.get(position);
                        String url = "";
                        if (simpleDoctorOrder.getOrder_category() == 0) {
                            url = HttpConstant.Url_removeMessageOrder;
                        } else if (simpleDoctorOrder.getOrder_category() == 2) {
                            url = HttpConstant.Url_removePrivateOrders;
                        }

                        Map<String, Object> maps = new HashMap<>();
                        maps.put("orderSn", simpleDoctorOrder.getOrder_sn());
                        HttpUtils.getInstantce().showSweetDialog();
                        HttpUtils.getInstantce().postWithHead(maps, url, new HttpCallBack<String>() {
                            @Override
                            public void onSuccess(HttpResult result) {
                                HttpUtils.getInstantce().changeRightSweetDialog(getString(R.string.delete_success));
                                orderList.remove(position);
                                mAdapter.notifyDataSetChanged();
                                mListView.setAdapter(mAdapter);
                            }

                            @Override
                            public void onFail(String msg) {

                            }
                        });

                        /*HttpUtils.getInstantce().removeOrder(url, simpleDoctorOrder.getOrder_sn(), new HttpConstant.SampleJsonResultListener<Feedback>() {
                            @Override
                            public void onSuccess(Feedback jsonData) {
                                orderList.remove(position);
                                mAdapter.notifyDataSetChanged();
                                mListView.setAdapter(mAdapter);
                            }

                            @Override
                            public void onFailure(Feedback jsonData) {
                            }
                        });*/
                    }
                })
                .show();
    }

    /**
     * 申诉订单
     */

    private void appealOrder(int position) {
        select_position = position;
        Intent intent = new Intent(mContext, AppealOrderActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("SimpleDoctorOrder", orderList.get(position));
        intent.putExtras(mBundle);
        startActivityForResult(intent, 2);
    }

}
