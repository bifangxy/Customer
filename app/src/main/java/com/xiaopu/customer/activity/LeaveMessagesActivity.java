package com.xiaopu.customer.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.PicAdapter;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.SimpleDoctorResult;
import com.xiaopu.customer.utils.CameraAndPicUntil;
import com.xiaopu.customer.utils.PixelUtil;
import com.xiaopu.customer.utils.RoundImageView;
import com.xiaopu.customer.utils.SelectPicPopupWindow;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.ToastUtils;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.utils.security.Des;
import com.xiaopu.customer.utils.security.MD5;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaveMessagesActivity extends ActivityBase {
    private static final String LOG_TAG = LeaveMessagesActivity.class.getSimpleName();

    private Context mContext;

    private RoundImageView riv_doctor_avatar;

    private TextView tv_doctor_department;

    private TextView tv_doctor_name;

    private TextView tv_doctor_title;

    private TextView tv_doctor_hospital;

    private TextView tv_doctor_comment;

    private LinearLayout ll_doctor_tag;

    private Button bt_commit;

    private EditText et_message_content;

    private EditText et_password;

    private RecyclerView recyclerView;

    private PicAdapter mAdapter;

    private List<String> dataList;

    private CheckBox cb_service_item;

    private boolean isNull = true;

    private MyClick myClick;

    private SimpleDoctorResult mSimpleDoctorResult;

    private SelectPicPopupWindow menuWindow;

    private File cameraFile;

    private CameraAndPicUntil cameraAndPicUntil;

    private List<String> imageList;

    /* 请求识别码 */
    private static final int CODE_CAMERA_REQUEST = 0xa0;
    private static final int RESULT_LOAD_IMAGE = 0xa1;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0xa3;
    private static final int CAMERA_REQUEST_CODE = 0xa4;

    private int click_position;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    upLoadPhoto(cameraFile);
                    break;
            }
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode
                (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_leave_messages);
        mContext = this;
        initActionBar("图文咨询");
        initView();
        initData();
    }

    private void initView() {
        riv_doctor_avatar = findViewById(R.id.riv_doctor_avatar);
        tv_doctor_name = findViewById(R.id.tv_doctor_name);
        tv_doctor_department = findViewById(R.id.tv_doctor_department);
        tv_doctor_title = findViewById(R.id.tv_doctor_title);
        tv_doctor_hospital = findViewById(R.id.tv_doctor_hospital);
        tv_doctor_comment = findViewById(R.id.tv_doctor_comment);
        bt_commit = findViewById(R.id.bt_commit);
        et_message_content = findViewById(R.id.et_message_content);
        et_password = findViewById(R.id.et_password);
        cb_service_item = findViewById(R.id.cb_service_item);
        recyclerView = findViewById(R.id.rv_message_pic);
        ll_doctor_tag = findViewById(R.id.ll_doctor_tag);
    }

    private void initData() {
        dataList = new ArrayList<>();
        imageList = new ArrayList<>();
        cameraAndPicUntil = new CameraAndPicUntil(LeaveMessagesActivity.this);
        Bundle bundle = getIntent().getExtras();

        mSimpleDoctorResult = (SimpleDoctorResult) bundle.getSerializable("doctorInfo");

        if (mSimpleDoctorResult != null) {
            tv_doctor_name.setText(mSimpleDoctorResult.getRealname());
            tv_doctor_hospital.setText(mSimpleDoctorResult.getHospital());
            tv_doctor_department.setText(mSimpleDoctorResult.getDepartment());
            tv_doctor_title.setText(initTitle(mSimpleDoctorResult.getTitle()));
            tv_doctor_comment.setText(mSimpleDoctorResult.getCommentRate() * 100 + "%");
            ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + mSimpleDoctorResult.getAvatar(), riv_doctor_avatar, ApplicationXpClient.getmOptions(R.mipmap.user_accountpic));
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
        myClick = new MyClick();
        bt_commit.setOnClickListener(myClick);
        et_message_content.addTextChangedListener(watcher);
        initRecycleView();


    }

    private void initRecycleView() {
        dataList.add("");
        GridLayoutManager mgr = new GridLayoutManager(mContext, 4);
        recyclerView.setLayoutManager(mgr);
        mAdapter = new PicAdapter(mContext, dataList);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setmClickListener(new PicAdapter.OnPicAdapterClickListener() {
            @Override
            public void picClick(int position) {
                click_position = position;
                menuWindow = new SelectPicPopupWindow(LeaveMessagesActivity.this, myClick);
                menuWindow.showAtLocation(findViewById(R.id.root_view), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }

            @Override
            public void deleteClick(int position) {
                //TODO 判断imageList存不存在这个position
                try {
                    imageList.remove(position);
                } catch (IndexOutOfBoundsException e) {

                }
                dataList.remove(position);
                if (!dataList.get(dataList.size() - 1).equals(""))
                    dataList.add(3, "");
                mAdapter.notifyDataSetChanged();
            }
        });

    }


    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int content = et_message_content.getText().toString().length();
            if (content != 0) {
                isNull = false;
                bt_commit.setBackgroundResource(R.drawable.shape_bt_commit_normal);
            } else {
                bt_commit.setBackgroundResource(R.drawable.shape_bt_commit_press);
                isNull = true;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void leaveMessage(int doctorId, final int price, int orderType, String content, String password) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("doctorId", doctorId);
        maps.put("price", price);
        maps.put("orderType", orderType);
        maps.put("content", content);
        maps.put("imgUrl", HttpUtils.getInstantce().gson.toJson(imageList));
        maps.put("password", password);
        HttpUtils.getInstantce().showSweetDialog(getString(R.string.commiting));
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_leaveMessageNew, new HttpCallBack<String>() {
            @Override
            public void onSuccess(HttpResult result) {
                SweetAlertDialog mSweetDialog = HttpUtils.getInstantce().getSweetDialog();
                mSweetDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                mSweetDialog.setTitleText(getString(R.string.tip));
                mSweetDialog.setContentText(getString(R.string.commit_success));
                mSweetDialog.setConfirmText(getString(R.string.sure));
                mSweetDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        ApplicationXpClient.userInfoResult.setBalance(ApplicationXpClient.userInfoResult.getBalance() - price);
                        sweetAlertDialog.dismiss();
                        finish();
                    }
                });
            }

            @Override
            public void onFail(String msg) {

            }
        });

        /*HttpUtils.getInstantce().leaveMessage(doctorId, price, orderType, content, imageList, password, new HttpConstant.SampleJsonResultListener<Feedback>() {
            @Override
            public void onSuccess(Feedback jsonData) {
                SweetAlertDialog mSweetDialog = HttpUtils.getInstantce().getSweetDialog();
                mSweetDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                mSweetDialog.setTitleText("提示");
                mSweetDialog.setContentText("提交成功");
                mSweetDialog.setConfirmText("确定");
                mSweetDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        ApplicationXpClient.userInfoResult.setBalance(ApplicationXpClient.userInfoResult.getBalance() - price);
                        sweetAlertDialog.dismiss();
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(Feedback jsonData) {
            }
        });*/
    }

    private class MyClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_commit:
                    if (!et_message_content.getText().toString().trim().isEmpty()) {
                        if (cb_service_item.isChecked()) {
                            if (!TextUtils.isEmpty(et_password.getText())) {
                                try {
                                    int price;
                                    int type;
                                    if (mSimpleDoctorResult.isFree()) {
                                        price = mSimpleDoctorResult.getClinicPrice();
                                        type = 1;
                                    } else {
                                        price = mSimpleDoctorResult.getOldPrice();
                                        type = 0;
                                    }
                                    String password = MD5.getMD5(et_password.getText().toString());
                                    leaveMessage(mSimpleDoctorResult.getDoctorId(), price, type, Des.encode(et_message_content.getText().toString()), password);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                ToastUtils.showErrorMsg("请输入密码");
                            }
                        } else {
                            ToastUtils.showErrorMsg("请同意图文咨询服务条款");
                        }
                    } else {
                        ToastUtils.showErrorMsg("咨询内容不能为空");
                    }
                    break;
                case R.id.bt_return:
                    finish();
                    break;
                case R.id.takePhotoBtn:
                    menuWindow.dismiss();
                    if (ContextCompat.checkSelfPermission(LeaveMessagesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(LeaveMessagesActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //申请WRITE_EXTERNAL_STORAGE权限
                        ActivityCompat.requestPermissions(LeaveMessagesActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                    } else {
                        cameraFile = cameraAndPicUntil.selectToCamera(CODE_CAMERA_REQUEST, null);
                    }
                    break;
                case R.id.pickPhotoBtn:
                    menuWindow.dismiss();
                    if (ContextCompat.checkSelfPermission(LeaveMessagesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(LeaveMessagesActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                    } else {
                        cameraAndPicUntil.selectToPicture(RESULT_LOAD_IMAGE);
                    }
                    break;
                case R.id.cancelBtn:
                    menuWindow.dismiss();
                    break;
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
                        setPicToView(cameraFile);
                    }
                    break;
                case CODE_CAMERA_REQUEST:
                    //拍照返回的结果
                    if (cameraFile != null && cameraFile.exists()) {
                        setPicToView(cameraFile);
                    }
                    break;
            }
        }
    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void setPicToView(File file) {
        if (file != null && file.exists()) {
            if (click_position == 10) {
                dataList.add(dataList.size() - 1, Uri.fromFile(file).toString());
                if (dataList.size() == 5) {
                    dataList.remove(4);
                }
            } else {
                dataList.remove(click_position);
                dataList.add(click_position, Uri.fromFile(file).toString());
            }
            mAdapter.notifyDataSetChanged();
            mHandler.sendEmptyMessageDelayed(0, 100);
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
                    T.showShort("无法获取相应的权限");
                }
                break;
            case CAMERA_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    cameraFile = cameraAndPicUntil.selectToCamera(CAMERA_REQUEST_CODE, null);
                } else {
                    T.showShort("无法获取相应的权限");
                }
            default:
                break;
        }
    }

    /**
     * 上传图片
     */
    private void upLoadPhoto(File file) {


        HttpUtils.getInstantce().showSweetDialog("上传中...");
        Map<String, Object> maps = new HashMap<>();
        maps.put("profile_picture", file);

        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_uploadMessageChat, new HttpCallBack<String>() {
            @Override
            public void onSuccess(HttpResult result) {
                HttpUtils.getInstantce().changeRightSweetDialog(getString(R.string.upload_success));
                String uri = (String) result.getData();
                imageList.add(uri);
            }

            @Override
            public void onFail(String msg) {

            }
        });

       /* HttpUtils.getInstantce().uploadAvatar(HttpConstant.Url_uploadMessageChat, file, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
            @Override
            public void onSuccess(Feedback<String> jsonData) {
                imageList.add(jsonData.getData());
                Log.d(LOG_TAG, "上传成功 " + jsonData.getData());
            }

            @Override
            public void onFailure(Feedback<String> jsonData) {
                Log.d(LOG_TAG, "上传失败");
            }
        });*/
    }
}
