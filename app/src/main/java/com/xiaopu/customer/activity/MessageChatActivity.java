package com.xiaopu.customer.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.MessageChatAdapter;
import com.xiaopu.customer.data.ChatMessage;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.MessageOrder;
import com.xiaopu.customer.data.SimpleDoctorOrder;
import com.xiaopu.customer.data.jsonresult.DoctorMessage;
import com.xiaopu.customer.data.jsonresult.DoctorMessageOrderItem;
import com.xiaopu.customer.utils.CameraAndPicUntil;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.utils.security.Des;
import com.xiaopu.customer.view.MessageChatPopupWindow;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;

import org.apache.http.protocol.HTTP;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelLinearLayout;

/**
 * Created by Administrator on 2018/1/2.
 */

public class MessageChatActivity extends ActivityBase implements View.OnClickListener {
    private static final String LOG_TAG = MessageChatActivity.class.getSimpleName();

    private Context mContext;

    private RecyclerView rcv_message;

    private View view_more;

    private View view_input;

    private View view_cancel;

    private View view_complete;

    private EditText et_content;

    private ImageView iv_action_plus;

    private ImageView iv_send;

    private TextView tv_camera;

    private TextView tv_photo;

    private ImageView iv_plus;

    private Button bt_cancel_continue;

    private Button bt_finish_continue;

    private Button bt_complaint;

    private TextView tv_state_msg;

    private TextView tv_surplus_time;

    private TextView tv_surplus_count;

    private LinearLayout ll_message_bar;

    private LinearLayout ll_message_cancel;

    private LinearLayout ll_message_finish;

    private KPSwitchPanelLinearLayout panel_root;

    private List<ChatMessage> chatChatMessageList;

    private MessageChatAdapter mAdapter;

    private File cameraFile;

    private CameraAndPicUntil cameraAndPicUntil;

    private MessageChatPopupWindow popupWindow;

    private CountDownTimer timer;

    private CountDownTimer surplus_timer;

    private SimpleDoctorOrder mSimpleDoctorOrder;

    private DoctorMessageOrderItem doctorMessageOrderItem;

    private int order_state;

    private int auth_state;

    private int comment_state;

    private int ask_count;

    private long orderSn;


    /* 请求识别码 */
    private static final int CODE_CAMERA_REQUEST = 0xa0;
    private static final int RESULT_LOAD_IMAGE = 0xa1;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0xa3;
    private static final int CAMERA_REQUEST_CODE = 0xa4;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mAdapter.changeData(msg.arg1, 1);
                    break;
                case 2:
                    mAdapter.changeData(msg.arg1, 0);
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_chat);
        mContext = this;
        initActionBar(getString(R.string.advisory_message));
        initView();
        initKeyBoard();
        initData();
        initListener();
    }

    private void initKeyBoard() {
        panel_root.setIgnoreRecommendHeight(true);

        KeyboardUtil.attach(this, panel_root,
                new KeyboardUtil.OnKeyboardShowingListener() {
                    @Override
                    public void onKeyboardShowing(boolean isShowing) {
                        rcv_message.scrollToPosition(mAdapter.getItemCount() - 1);
                    }
                });

        KPSwitchConflictUtil.attach(panel_root, et_content,
                new KPSwitchConflictUtil.SwitchClickListener() {
                    @Override
                    public void onClickSwitch(boolean switchToPanel) {
                        if (switchToPanel) {
                            et_content.clearFocus();
                        } else {
                            et_content.requestFocus();
                        }
                    }
                },
                new KPSwitchConflictUtil.SubPanelAndTrigger(view_more, iv_plus));

        rcv_message.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    KPSwitchConflictUtil.hidePanelAndKeyboard(panel_root);
                }
                return false;
            }
        });
    }

    private void initView() {
        iv_action_plus = findViewById(R.id.iv_actionbar_image);
        iv_action_plus.setImageResource(R.mipmap.icon_plus);
        iv_action_plus.setVisibility(View.VISIBLE);
        rcv_message = findViewById(R.id.rcv_message_chat);
        panel_root = findViewById(R.id.panel_root);
        view_more = panel_root.findViewById(R.id.view_more);

        et_content = findViewById(R.id.et_message);
        iv_plus = findViewById(R.id.iv_plus);
        iv_send = findViewById(R.id.iv_send);

        tv_camera = findViewById(R.id.tv_selected_camera);
        tv_photo = findViewById(R.id.tv_selected_photo);
        tv_state_msg = findViewById(R.id.tv_state_msg);

        bt_cancel_continue = findViewById(R.id.bt_cancel_continue);
        bt_finish_continue = findViewById(R.id.bt_finish_continue);
        bt_complaint = findViewById(R.id.bt_complaint);
        tv_surplus_time = findViewById(R.id.tv_surplus_time);
        tv_surplus_count = findViewById(R.id.tv_surplus_count);

        ll_message_bar = findViewById(R.id.ll_message_bar);
        ll_message_cancel = findViewById(R.id.ll_message_cancel);
        ll_message_finish = findViewById(R.id.ll_message_finish);

        view_input = findViewById(R.id.view_input);
        view_complete = findViewById(R.id.view_complete);
        view_cancel = findViewById(R.id.view_cancel);


    }

    private void initData() {
        cameraAndPicUntil = new CameraAndPicUntil(this);
        chatChatMessageList = new ArrayList<>();
        mSimpleDoctorOrder = (SimpleDoctorOrder) getIntent().getSerializableExtra("simpleDoctorOrder");
        orderSn = mSimpleDoctorOrder.getOrder_sn();
        mAdapter = new MessageChatAdapter(mContext, chatChatMessageList, HttpConstant.Url_ImageServer + ApplicationXpClient.userInfoResult.getAvatar(), HttpConstant.Url_ImageServer + mSimpleDoctorOrder.getDoctor_avatar());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv_message.setLayoutManager(linearLayoutManager);
        rcv_message.setAdapter(mAdapter);
        getMessageState();


    }

    private void getMessageState() {
        Map<String, Object> maps = new HashMap<>();
        maps.put("orderSn", mSimpleDoctorOrder.getOrder_sn());
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getMessageOrderByOrderSn, new HttpCallBack<MessageOrder>() {
            @Override
            public void onSuccess(HttpResult result) {
                MessageOrder messageOrder = (MessageOrder) result.getData();
                List<DoctorMessage> doctorMessagesList = messageOrder.getMessagesList();
                doctorMessageOrderItem = messageOrder.getDoctorMessageOrderItem();
                initState(doctorMessageOrderItem);
                for (int i = 0; i < doctorMessagesList.size(); i++) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSendState(1);
                    chatMessage.setMsgType(doctorMessagesList.get(i).getMsgType());
                    chatMessage.setContentType(doctorMessagesList.get(i).getContentType());
                    chatMessage.setCreateTime(doctorMessagesList.get(i).getCreateTime());
                    if (chatMessage.getContentType() == 0) {
                        chatMessage.setContent(doctorMessagesList.get(i).getContent());
                    } else if (chatMessage.getContentType() == 1) {
                        try {
                            chatMessage.setUri(HttpConstant.Url_ImageServer + Des.decode(doctorMessagesList.get(i).getContent()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    chatChatMessageList.add(chatMessage);
                }
                mAdapter.notifyDataSetChanged();
                rcv_message.scrollToPosition(mAdapter.getItemCount() - 1);
            }

            @Override
            public void onFail(String msg) {

            }
        });

/*
        HttpUtils.getInstantce().getMessageOrdersByOrderSn(mSimpleDoctorOrder.getOrder_sn(), new HttpConstant.SampleJsonResultListener<Feedback<MessageOrder>>() {
            @Override
            public void onSuccess(Feedback<MessageOrder> jsonData) {
                List<DoctorMessage> doctorMessagesList = jsonData.getData().getMessagesList();
                doctorMessageOrderItem = jsonData.getData().getDoctorMessageOrderItem();
                initOrderState(doctorMessageOrderItem);
                for (int i = 0; i < doctorMessagesList.size(); i++) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSendState(1);
                    chatMessage.setMsgType(doctorMessagesList.get(i).getMsgType());
                    chatMessage.setContentType(doctorMessagesList.get(i).getContentType());
                    chatMessage.setCreateTime(doctorMessagesList.get(i).getCreateTime());
                    if (chatMessage.getContentType() == 0) {
                        chatMessage.setContent(doctorMessagesList.get(i).getContent());
                    } else if (chatMessage.getContentType() == 1) {
                        try {
                            chatMessage.setUri(HttpConstant.Url_ImageServer + Des.decode(doctorMessagesList.get(i).getContent()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    chatChatMessageList.add(chatMessage);
                }
                mAdapter.notifyDataSetChanged();
                rcv_message.scrollToPosition(mAdapter.getItemCount() - 1);

            }

            @Override
            public void onFailure(Feedback<MessageOrder> jsonData) {
            }
        });
*/
    }

    private void initState(DoctorMessageOrderItem doctorMessageOrderItem) {

        order_state = doctorMessageOrderItem.getOrderState();
        auth_state = doctorMessageOrderItem.getIsAuth();
        comment_state = doctorMessageOrderItem.getCommentStatus();
        ask_count = doctorMessageOrderItem.getAskCount();
        initOrderState();
        if (doctorMessageOrderItem.getIsReply() == 0) {
            long past_time = (doctorMessageOrderItem.getCreateTime().getTime() + 30 * 60 * 1000) - new Date().getTime();
            if (past_time < 1800000) {
                Log.d(LOG_TAG, "在30分钟回复时间内");
                startTimer(past_time);
            } else {
                Log.d(LOG_TAG, "已经超过30分钟回复时间");
                tv_state_msg.setText(R.string.waiting_more_than_30_minutes);
            }
            tv_surplus_count.setText(getString(R.string.consultative_count) + (30 - ask_count));
            tv_surplus_time.setText(getString(R.string.remaining_consultation_time) + "24小时");
        } else if (doctorMessageOrderItem.getIsReply() == 1) {
            tv_state_msg.setText(R.string.doctor_answer_only_advice);
            tv_surplus_count.setText(getString(R.string.consultative_count) + (30 - ask_count));
            if (doctorMessageOrderItem.getReplyTime() != null) {
                long left_time = (doctorMessageOrderItem.getReplyTime().getTime() + 24 * 60 * 60 * 1000) - new Date().getTime();
                startSurplusTimer(left_time);
            }
        }
    }

    private void initOrderState() {
        if (order_state <= 3) {
            view_input.setVisibility(View.VISIBLE);
            view_cancel.setVisibility(View.GONE);
            view_complete.setVisibility(View.GONE);
            tv_state_msg.setVisibility(View.VISIBLE);
        } else if (order_state >= 4 && order_state != 7) {
            view_input.setVisibility(View.GONE);
            view_cancel.setVisibility(View.GONE);
            view_complete.setVisibility(View.VISIBLE);
            tv_state_msg.setVisibility(View.GONE);
        } else if (order_state == 7) {
            view_input.setVisibility(View.GONE);
            view_cancel.setVisibility(View.VISIBLE);
            view_complete.setVisibility(View.GONE);
            tv_state_msg.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    iv_plus.setVisibility(View.GONE);
                    iv_send.setVisibility(View.VISIBLE);
                } else {
                    iv_plus.setVisibility(View.VISIBLE);
                    iv_send.setVisibility(View.GONE);
                }
            }
        });

        mAdapter.setMessageItemClickListener(new MessageChatAdapter.OnMessageItemClickListener() {
            @Override
            public void doctorAvatarClick() {
                Log.d(LOG_TAG, "医生头像被点击");
            }

            @Override
            public void userAvatarClick() {
                Log.d(LOG_TAG, "用户头像被点击");
            }

            @Override
            public void messageImageClick(ChatMessage chatMessage) {
                Intent intent = new Intent(mContext, BigImageActivity.class);
                if (chatMessage.getImagePath() != null) {
                    File file = new File(chatMessage.getImagePath());
                    Log.d(LOG_TAG, file.length() + "");
                    String uri = Uri.fromFile(file).toString();
                    intent.putExtra("imagePath", uri);
                } else if (chatMessage.getUri() != null) {
                    intent.putExtra("uri", chatMessage.getUri());
                }
                startActivity(intent);
            }

            @Override
            public void errorClick(ChatMessage chatMessage) {
                Log.d(LOG_TAG, "错误被点击");
            }
        });


        tv_photo.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
        iv_send.setOnClickListener(this);
        bt_finish_continue.setOnClickListener(this);
        bt_cancel_continue.setOnClickListener(this);
        bt_complaint.setOnClickListener(this);
        iv_action_plus.setOnClickListener(this);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP &&
                event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (panel_root.getVisibility() == View.VISIBLE) {
                KPSwitchConflictUtil.hidePanelAndKeyboard(panel_root);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_selected_photo:
                if (ContextCompat.checkSelfPermission(MessageChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MessageChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                } else {
                    cameraAndPicUntil.selectToPicture(RESULT_LOAD_IMAGE);
                }
                break;
            case R.id.tv_selected_camera:
                if (ContextCompat.checkSelfPermission(MessageChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MessageChatActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(MessageChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                } else {
                    cameraFile = cameraAndPicUntil.selectToCamera(CODE_CAMERA_REQUEST, null);
                }
                break;
            case R.id.iv_send:
                if (et_content.getText().toString().length() != 0) {
                    try {
                        sendTextMessage(Des.encode(et_content.getText().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    et_content.setText("");
                }
                break;
            case R.id.bt_cancel_continue:
            case R.id.bt_finish_continue:
                Intent mIntent = new Intent(mContext, DoctorInfoActivity.class);
                mIntent.putExtra("doctorId", mSimpleDoctorOrder.getDoctor_id());
                startActivity(mIntent);
                break;
            case R.id.bt_complaint:
                if (order_state == 4)
                    appealDoctor();
                else
                    T.showShort("订单已申诉");
                break;
            case R.id.iv_cancel_order:
                cancelMessageOrder();
                break;
            case R.id.iv_actionbar_image:
                showPopupWindow();
                break;
            case R.id.iv_authorization:
                sendAuthorization();
                break;
            case R.id.iv_finish_order:
                finishOrder();
                break;
            case R.id.iv_order_comment:
                commentDoctor();
                break;
            case R.id.iv_appeal_order:
                appealDoctor();
                break;
        }
    }


    private void startTimer(long time) {
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String time;
                if (millisUntilFinished < 60000) {
                    time = millisUntilFinished / 1000 + "秒";
                } else {
                    time = (millisUntilFinished / 1000) / 60 + "分" + (millisUntilFinished / 1000) % 60 + "秒";
                }
                tv_state_msg.setText("请耐心等待，医生将在" + time + "内回答您的问题");
            }

            @Override
            public void onFinish() {

            }
        };
        timer.start();
    }


    private void startSurplusTimer(long time) {
        surplus_timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String time;
                if (millisUntilFinished < 60000) {
                    time = millisUntilFinished / 1000 + getString(R.string.second);
                } else if (millisUntilFinished < 3600000) {
                    time = (millisUntilFinished / 1000) / 60 + getString(R.string.minute) + (millisUntilFinished / 1000) % 60 + getString(R.string.second);
                } else {
                    time = (millisUntilFinished / 1000) / (60 * 60) + getString(R.string.hour) +
                            (millisUntilFinished / 1000 - ((millisUntilFinished / 1000) / (60 * 60)) * 3600) / 60 + getString(R.string.minute) + (millisUntilFinished / 1000 - 3600) % 60 + getString(R.string.second);
                }
                tv_surplus_time.setText(getString(R.string.remaining_consultation_time) + time);
            }

            @Override
            public void onFinish() {

            }
        };
        surplus_timer.start();
    }


    private void sendImageMessage(File file) {
        ChatMessage chatChatMessage = new ChatMessage();
        chatChatMessage.setMsgType(0);
        chatChatMessage.setContentType(1);
        chatChatMessage.setImagePath(file.getAbsolutePath());
        chatChatMessage.setSendState(2);
        mAdapter.addData(chatChatMessage);
        rcv_message.scrollToPosition(mAdapter.getItemCount() - 1);
        final int position = mAdapter.getItemCount() - 1;

        Map<String, Object> maps = new HashMap<>();
        maps.put("file", file);
        maps.put("orderSn", orderSn);
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_AppendPicMessages, new HttpCallBack<DoctorMessage>() {
            @Override
            public void onSuccess(HttpResult result) {
                DoctorMessage doctorMessage = (DoctorMessage) result.getData();
                Message message = new Message();
                message.what = 1;
                message.arg1 = position;
                if (doctorMessage.getCanAsk() != null)
                    tv_surplus_count.setText(getString(R.string.consultative_count) + doctorMessage.getCanAsk());
                mHandler.sendMessageDelayed(message, 100);
            }

            @Override
            public void onFail(String msg) {
                Message message = new Message();
                message.what = 2;
                message.arg1 = position;
                mHandler.sendMessageDelayed(message, 100);
            }
        });

    /*    HttpUtils.getInstantce().appendPicMessage(file, orderSn, new HttpConstant.SampleJsonResultListener<Feedback<DoctorMessage>>() {
            @Override
            public void onSuccess(Feedback<DoctorMessage> jsonData) {
                Log.d(LOG_TAG, "发送成功");
                Message message = new Message();
                message.what = 1;
                message.arg1 = position;
                if (jsonData.getData().getCanAsk() != null)
                    tv_surplus_count.setText(getString(R.string.consultative_count) + jsonData.getData().getCanAsk() + "/30");
                mHandler.sendMessageDelayed(message, 100);
            }

            @Override
            public void onFailure(Feedback<DoctorMessage> jsonData) {
                Log.d(LOG_TAG, "发送失败");
                Message message = new Message();
                message.what = 2;
                message.arg1 = position;
                mHandler.sendMessageDelayed(message, 100);
            }
        });*/
    }

    private void sendTextMessage(String content) {
        ChatMessage chatChatMessage = new ChatMessage();
        chatChatMessage.setMsgType(0);
        chatChatMessage.setContentType(0);
        chatChatMessage.setSendState(2);
        chatChatMessage.setContent(content);
        mAdapter.addData(chatChatMessage);
        rcv_message.scrollToPosition(mAdapter.getItemCount() - 1);
        final int position = mAdapter.getItemCount() - 1;

        Map<String, Object> maps = new HashMap<>();
        maps.put("id", mSimpleDoctorOrder.getOrder_id());
        maps.put("orderSn", orderSn);
        maps.put("content", content);

        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_appendMessage, new HttpCallBack<DoctorMessage>() {
            @Override
            public void onSuccess(HttpResult result) {
                DoctorMessage doctorMessage = (DoctorMessage) result.getData();
                Message message = new Message();
                message.what = 1;
                message.arg1 = position;
                if (doctorMessage.getCanAsk() != null)
                    tv_surplus_count.setText(getString(R.string.consultative_count) + doctorMessage.getCanAsk());
                mHandler.sendMessage(message);
            }

            @Override
            public void onFail(String msg) {
                Message message = new Message();
                message.what = 2;
                message.arg1 = position;
                mHandler.sendMessageDelayed(message, 100);
            }
        });


/*
        HttpUtils.getInstantce().appendMessage(mSimpleDoctorOrder.getOrder_id(), orderSn, content, new HttpConstant.SampleJsonResultListener<Feedback<DoctorMessage>>() {
            @Override
            public void onSuccess(Feedback<DoctorMessage> jsonData) {
                Message message = new Message();
                message.what = 1;
                message.arg1 = position;
                if (jsonData.getData().getCanAsk() != null)
                    tv_surplus_count.setText(getString(R.string.consultative_count) + jsonData.getData().getCanAsk() + "/30");
                mHandler.sendMessage(message);
            }

            @Override
            public void onFailure(Feedback<DoctorMessage> jsonData) {
                Message message = new Message();
                message.what = 2;
                message.arg1 = position;
                mHandler.sendMessageDelayed(message, 100);
            }
        });
*/

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 此处的用于判断接收的Activity是不是你想要的那个（参数RESULT_LOAD_IMAGE）,data有没有，resultCode是否为-1
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_LOAD_IMAGE:
                    //相册返回结果
                    String img_path = cameraAndPicUntil.getRealFilePath(data.getData());
                    if (img_path != null) {
                        cameraFile = new File(img_path);
                        sendImageMessage(cameraFile);
                    }
                    break;
                case CODE_CAMERA_REQUEST:
                    //拍照返回的结果
                    if (cameraFile != null && cameraFile.exists()) {
                        sendImageMessage(cameraFile);
                    }
                    break;
            }
        }
        if (requestCode == 2 && resultCode == 3) {
            order_state = 5;
            initOrderState();
        } else if (requestCode == 1 && resultCode == 3) {
            comment_state = 1;
            initOrderState();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraAndPicUntil.selectToPicture(RESULT_LOAD_IMAGE);
                } else {
                    T.showShort(getString(R.string.no_permission));
                }
                break;
            case CAMERA_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    cameraFile = cameraAndPicUntil.selectToCamera(CAMERA_REQUEST_CODE, null);
                } else {
                    T.showShort(getString(R.string.no_permission));
                }
            default:
                break;
        }
    }

    private void showPopupWindow() {
        if (popupWindow == null) {
            popupWindow = new MessageChatPopupWindow(this);
            popupWindow.setWidth(500);
            popupWindow.setBackground_drawable(mContext.getResources().getDrawable(R.drawable.auto_clean_bg, null));
            popupWindow.initPopupWindow();
            popupWindow.initListener(this);
            popupWindow.setState(order_state, auth_state, comment_state);
            popupWindow.showPopupWindow(iv_action_plus);
        } else {
            popupWindow.setState(order_state, auth_state, comment_state);
            popupWindow.showPopupWindow(iv_action_plus);
        }
    }

    /**
     * 取消订单
     */
    private void cancelMessageOrder() {
        popupWindow.dismiss();
        if (order_state == 1)
            T.showShort("该订单暂时不能取消");
        else {
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
                            maps.put("orderSn", orderSn);
                            HttpUtils.getInstantce().showSweetDialog();
                            HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_CancelMessageOrder, new HttpCallBack<String>() {
                                @Override
                                public void onSuccess(HttpResult result) {
                                    HttpUtils.getInstantce().changeRightSweetDialog(getString(R.string.cancel_success));
                                    order_state = 7;
                                    initOrderState();
                                }

                                @Override
                                public void onFail(String msg) {

                                }
                            });

                            /*HttpUtils.getInstantce().cancelMessageOrder(orderSn, new HttpConstant.SampleJsonResultListener<Feedback>() {
                                @Override
                                public void onSuccess(Feedback jsonData) {
                                    if (jsonData.getCode() == 200) {
                                        order_state = 7;
                                        view_input.setVisibility(View.GONE);
                                        view_cancel.setVisibility(View.VISIBLE);
                                        view_complete.setVisibility(View.GONE);
                                        tv_state_msg.setVisibility(View.GONE);
                                        Log.d(LOG_TAG, "订单取消成功");
                                    } else if (jsonData.getCode() == 301) {
                                        T.showShort(jsonData.getMsg());
                                    }

                                }

                                @Override
                                public void onFailure(Feedback jsonData) {
                                    Log.d(LOG_TAG, "订单取消失败");
                                }
                            });*/
                        }
                    })
                    .show();
        }
    }

    /**
     * 授权订单
     */
    private void sendAuthorization() {
        popupWindow.dismiss();
        Map<String, Object> maps = new HashMap<>();
        maps.put("orderSn", mSimpleDoctorOrder.getOrder_sn());
        maps.put("doctorId", mSimpleDoctorOrder.getDoctor_id());
        HttpUtils.getInstantce().showSweetDialog("授权中...");
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_authorizePersonalDataAccess, new HttpCallBack<String>() {
            @Override
            public void onSuccess(HttpResult result) {
                HttpUtils.getInstantce().changeRightSweetDialog("授权成功");
                auth_state = 1;
            }

            @Override
            public void onFail(String msg) {

            }
        });

       /* HttpUtils.getInstantce().authorizePersonalDataAccess(mSimpleDoctorOrder.getOrder_sn(), mSimpleDoctorOrder.getDoctor_id(), new
                HttpConstant.SampleJsonResultListener<Feedback>() {
                    @Override
                    public void onSuccess(Feedback jsonData) {
                        auth_state = 1;
                    }

                    @Override
                    public void onFailure(Feedback jsonData) {
                    }
                });*/
    }

    /**
     * 完成订单
     */
    private void finishOrder() {
        popupWindow.dismiss();
        Map<String, Object> maps = new HashMap<>();
        maps.put("orderSn", orderSn);
        HttpUtils.getInstantce().showSweetDialog(getString(R.string.finishing));
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_confirmMessageOrder, new HttpCallBack<String>() {
            @Override
            public void onSuccess(HttpResult result) {
                HttpUtils.getInstantce().changeRightSweetDialog(getString(R.string.order_finish));
                order_state = 4;
                initOrderState();
            }

            @Override
            public void onFail(String msg) {

            }
        });


               /* HttpUtils.getInstantce().confirmMessageOrder(mSimpleDoctorOrder.getOrder_sn(), new HttpConstant.SampleJsonResultListener<Feedback>() {
                    @Override
                    public void onSuccess(Feedback jsonData) {
                        order_state = 4;
                    }

                    @Override
                    public void onFailure(Feedback jsonData) {
                    }
                });*/
    }

    private void commentDoctor() {
        popupWindow.dismiss();
        Intent intent = new Intent(mContext, CommentDoctorActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("SimpleDoctorOrder", mSimpleDoctorOrder);
        intent.putExtras(mBundle);
        startActivityForResult(intent, 1);
    }

    private void appealDoctor() {
        if (popupWindow != null)
            popupWindow.dismiss();
        Intent intent = new Intent(mContext, AppealOrderActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("SimpleDoctorOrder", mSimpleDoctorOrder);
        intent.putExtras(mBundle);
        startActivityForResult(intent, 2);
    }


}

