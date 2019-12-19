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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.Pickers;
import com.xiaopu.customer.data.jsonrequest.User2;
import com.xiaopu.customer.data.jsonresult.DetectionMenstruation;
import com.xiaopu.customer.data.jsonresult.UserInfoResult;
import com.xiaopu.customer.dialog.EditDialog;
import com.xiaopu.customer.utils.CameraAndPicUntil;
import com.xiaopu.customer.utils.ControlSave;
import com.xiaopu.customer.utils.DateUtils;
import com.xiaopu.customer.utils.RoundImageView;
import com.xiaopu.customer.utils.SelectPicPopupWindow;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.TimeUtils;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.utils.security.DESCipher;
import com.xiaopu.customer.utils.security.Des;
import com.xiaopu.customer.view.PickerScrollView;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/14 0014.
 */
public class MyInfoActivity extends ActivityBase {
    private static final String LOG_TAG = MyInfoActivity.class.getSimpleName();

    private Context mContext;

    private RoundImageView iv_user_header;

    private LinearLayout ll_user_nickname;

    private LinearLayout ll_user_sex;

    private LinearLayout ll_user_blood;

    private LinearLayout ll_user_height;

    private LinearLayout ll_user_weight;

    private LinearLayout ll_user_birthday;

    private LinearLayout ll_user_recent_menstruation;

    private LinearLayout ll_user_menstruation;

    private LinearLayout ll_user_menstruation_cycle;

    private LinearLayout ll_user_illhistory;

    private TextView tv_user_nickname;

    private TextView tv_user_sex;

    private TextView tv_user_blood;

    private TextView tv_user_height;

    private TextView tv_user_weight;

    private TextView tv_user_birthday;

    private TextView tv_user_recent_menstruation;

    private TextView tv_user_menstruation;

    private TextView tv_user_menstruation_cycle;

    private TextView tv_user_illhistory;

    private ImageView iv_return;

    //TODO 疾病史

    private LinearLayout ll_woman;

    private TextView tvActionbarRight;

    private MyClick myClick;

    /* 请求识别码 */
    private static final int CODE_CAMERA_REQUEST = 0xa0;
    private static final int RESULT_LOAD_IMAGE = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0xa3;
    private static final int CAMERA_REQUEST_CODE = 0xa4;
    private static final int CAMERA_FLAG = 0xa5;
    private static final int PHOTO_FLAG = 0xa6;

    private SelectPicPopupWindow menuWindow;

    private String mBirthday;
    private String mPeriod;
    private Date mBirthDate;

    private UserInfoResult mUserInfoResult;

    private User2 old_userInfoResult;

    private User2 mUser;

    private List<Pickers> list; // 滚动选择器数据

    private AlertDialog.Builder mBuilder;

    private AlertDialog mAlertDialog;

    private View rootView;

    private int select_type;

    private PickerScrollView pickerScrollView;

    private TextView tv_title;

    private Button bt_sure;

    private String menstruation_before;

    private File cameraFile;

    private File cropFile;

    private CameraAndPicUntil cameraAndPicUntil;

    private String[] id;

    private String[] name;

    private static final String NO_DATA = "未填写";

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    upLoadPhoto(cropFile);
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myinfo);
        initActionBar("我的资料");
        mContext = this;
        initView();
        initData();
        initListener();
    }


    private void initView() {
        iv_user_header = (RoundImageView) findViewById(R.id.user_header_img);
        tvActionbarRight = (TextView) findViewById(R.id.tv_actionbar_text);

        ll_user_nickname = (LinearLayout) findViewById(R.id.ll_user_nickname);
        ll_user_sex = findViewById(R.id.ll_user_sex);
        ll_user_weight = (LinearLayout) findViewById(R.id.ll_user_weight);
        ll_user_height = (LinearLayout) findViewById(R.id.ll_user_height);
        ll_user_blood = (LinearLayout) findViewById(R.id.ll_user_blood);
        ll_user_birthday = (LinearLayout) findViewById(R.id.ll_user_birthday);
        ll_user_recent_menstruation = (LinearLayout) findViewById(R.id.ll_user_recent_menstruation);
        ll_user_menstruation = (LinearLayout) findViewById(R.id.ll_user_menstruation);
        ll_user_menstruation_cycle = (LinearLayout) findViewById(R.id.ll_user_menstruation_cycle);
        ll_user_illhistory = (LinearLayout) findViewById(R.id.ll_user_illhistory);


        tv_user_nickname = (TextView) findViewById(R.id.tv_user_nickname);
        tv_user_sex = findViewById(R.id.tv_user_sex);
        tv_user_weight = (TextView) findViewById(R.id.tv_user_weight);
        tv_user_height = (TextView) findViewById(R.id.tv_user_height);
        tv_user_blood = (TextView) findViewById(R.id.tv_user_blood);
        tv_user_birthday = (TextView) findViewById(R.id.tv_user_birthday);
        tv_user_recent_menstruation = (TextView) findViewById(R.id.tv_user_recent_menstruation);
        tv_user_menstruation = (TextView) findViewById(R.id.tv_user_menstruation);
        tv_user_menstruation_cycle = (TextView) findViewById(R.id.tv_user_menstruation_cycle);
        tv_user_illhistory = (TextView) findViewById(R.id.tv_user_illhistory);

        ll_woman = (LinearLayout) findViewById(R.id.ll_woman);


        rootView = LayoutInflater.from(mContext).inflate(R.layout.picker_view, null);
        pickerScrollView = (PickerScrollView) rootView.findViewById(R.id.picker_scroll_view);
        tv_title = (TextView) rootView.findViewById(R.id.tv_picker_title);
        bt_sure = (Button) rootView.findViewById(R.id.bt_picker_sure);
        iv_return = (ImageView) findViewById(R.id.iv_return);

    }

    private void initData() {

        myClick = new MyClick();
        cameraAndPicUntil = new CameraAndPicUntil(MyInfoActivity.this);

        list = new ArrayList<>();

        if (ApplicationXpClient.userInfoResult.getSex() != null) {
            if (ApplicationXpClient.userInfoResult.getSex() == 1) {
                ll_woman.setVisibility(View.GONE);
            } else {
                ll_woman.setVisibility(View.VISIBLE);
                getMenstruationData();
            }
        } else {
            ll_woman.setVisibility(View.GONE);
        }
        mUserInfoResult = ApplicationXpClient.userInfoResult;

        old_userInfoResult = new User2();
        mUser = new User2();
        mUser.setId(mUserInfoResult.getId());
        old_userInfoResult.setId(mUserInfoResult.getId());
        mUser.setAvatar(mUserInfoResult.getAvatar());
        old_userInfoResult.setAvatar(mUserInfoResult.getAvatar());
        mUser.setNickname(mUserInfoResult.getNickname());
        old_userInfoResult.setNickname(mUserInfoResult.getNickname());

        if (mUserInfoResult.getSex() != null) {
            mUser.setSex(mUserInfoResult.getSex());
            old_userInfoResult.setSex(mUserInfoResult.getSex());
            ll_user_sex.setVisibility(View.GONE);
        } else {
            mUser.setSex(null);
            old_userInfoResult.setSex(null);
            tv_user_sex.setText(NO_DATA);
        }

        if (mUserInfoResult.getIllHistory() != null) {
            mUser.setIllHistory(mUserInfoResult.getIllHistory());
            old_userInfoResult.setIllHistory(mUserInfoResult.getIllHistory());
            try {
                tv_user_illhistory.setText(Des.decode(mUserInfoResult.getIllHistory()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mUser.setIllHistory(null);
            old_userInfoResult.setIllHistory(null);
            tv_user_illhistory.setText(NO_DATA);
        }

        if (mUserInfoResult.getBlood() != null) {
            mUser.setBlood(mUserInfoResult.getBlood());
            old_userInfoResult.setBlood(mUserInfoResult.getBlood());
            tv_user_blood.setText(mUserInfoResult.getBlood());
        } else {
            mUser.setBlood(null);
            old_userInfoResult.setBlood(null);
            tv_user_blood.setText(NO_DATA);
        }

        if (mUserInfoResult.getHeight() != null) {
            mUser.setHeight(mUserInfoResult.getHeight());
            old_userInfoResult.setHeight(mUserInfoResult.getHeight());
            tv_user_height.setText(mUserInfoResult.getHeight() + "CM");
        } else {
            mUser.setHeight(null);
            old_userInfoResult.setHeight(null);
            tv_user_height.setText(NO_DATA);
        }

        if (mUserInfoResult.getWeight() != null) {
            mUser.setWeight(mUserInfoResult.getWeight());
            old_userInfoResult.setWeight(mUserInfoResult.getWeight());
            tv_user_weight.setText(mUserInfoResult.getWeight() + "KG");
        } else {
            mUser.setWeight(null);
            old_userInfoResult.setWeight(null);
            tv_user_weight.setText(NO_DATA);
        }
        if (mUserInfoResult.getBirthday() != null) {
            mBirthDate = mUserInfoResult.getBirthday();
            mUser.setBirthday(Long.toString((mUserInfoResult.getBirthday().getTime() / 1000)));
            old_userInfoResult.setBirthday(Long.toString((mUserInfoResult.getBirthday().getTime() / 1000)));
            tv_user_birthday.setText(TimeUtils.DateToString(mUserInfoResult.getBirthday()));
        } else {
            mBirthDate = null;
            mUser.setBirthday(null);
            old_userInfoResult.setBirthday(null);
            tv_user_birthday.setText(NO_DATA);
        }

        ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + mUserInfoResult.getAvatar(), iv_user_header, ApplicationXpClient.getmOptions(R.mipmap.user_accountpic));

        tv_user_nickname.setText(mUserInfoResult.getNickname());
        tvActionbarRight.setVisibility(View.VISIBLE);
        tvActionbarRight.setText("保存");

        mBuilder = new AlertDialog.Builder(this);
        mBuilder.setView(rootView);
        mBuilder.setCancelable(false);
        mAlertDialog = mBuilder.create();

    }

    private void initListener() {
        iv_user_header.setOnClickListener(myClick);
        tvActionbarRight.setOnClickListener(myClick);
        ll_user_nickname.setOnClickListener(myClick);
        ll_user_sex.setOnClickListener(myClick);
        ll_user_weight.setOnClickListener(myClick);
        ll_user_height.setOnClickListener(myClick);
        ll_user_blood.setOnClickListener(myClick);
        ll_user_birthday.setOnClickListener(myClick);
        ll_user_recent_menstruation.setOnClickListener(myClick);
        ll_user_menstruation.setOnClickListener(myClick);
        ll_user_menstruation_cycle.setOnClickListener(myClick);
        ll_user_illhistory.setOnClickListener(myClick);
        iv_return.setOnClickListener(myClick);
        bt_sure.setOnClickListener(myClick);
        pickerScrollView.setOnSelectListener(new PickerScrollView.onSelectListener() {
            @Override
            public void onSelect(Pickers pickers) {
                setText();
            }
        });

    }


    private class MyClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.user_header_img:
                    //调用ppw添加拍照修改或者图相修改
                    menuWindow = new SelectPicPopupWindow(MyInfoActivity.this, myClick);
                    menuWindow.showAtLocation(findViewById(R.id.info_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    break;
                case R.id.tv_actionbar_text:
                    if (tv_user_nickname.getText().length() < 2) {
                        T.showShort("昵称不能小于两位");
                    } else {
                        if (ApplicationXpClient.userInfoResult.getSex() != null && ApplicationXpClient.userInfoResult.getSex() == 0) {
                            //修改经期信息请求
                            HttpUtils.getInstantce().showSweetDialog();
                            saveMenstruationData(mUserInfoResult.getId(), Integer.parseInt(tv_user_menstruation_cycle.getText().toString().replace("天", "")),
                                    Integer.parseInt(tv_user_menstruation.getText().toString().replace("天", "")),
                                    tv_user_recent_menstruation.getText().toString());
                        } else {
                            HttpUtils.getInstantce().showSweetDialog();
                            modifyUser(mUser);
                        }
                    }
                    break;
                case R.id.takePhotoBtn:
                    menuWindow.dismiss();
                    if (ContextCompat.checkSelfPermission(MyInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MyInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //申请WRITE_EXTERNAL_STORAGE权限
                        ActivityCompat.requestPermissions(MyInfoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                    } else {
                        cameraFile = cameraAndPicUntil.selectToCamera(CODE_CAMERA_REQUEST, null);
                    }
                    break;
                case R.id.pickPhotoBtn:
                    menuWindow.dismiss();
                    if (ContextCompat.checkSelfPermission(MyInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MyInfoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                    } else {
                        cameraAndPicUntil.selectToPicture(RESULT_LOAD_IMAGE);
                    }
                    break;
                case R.id.cancelBtn:
                    menuWindow.dismiss();
                    break;
                case R.id.ll_user_nickname:
                    //重命名
                    EditDialog.Builder nick_builder = new EditDialog.Builder(mContext, tv_user_nickname.getText().toString(), "请输入昵称");
                    nick_builder.setOnSureButtonClickListener(new EditDialog.Builder.OnSureButtonClickListener() {
                        @Override
                        public void click(String string) {
                            tv_user_nickname.setText(string);
                            mUser.setNickname(string);
                        }
                    });
                    nick_builder.create().show();
                    break;
                case R.id.ll_user_sex:
                    select_type = 1;
                    tv_title.setText("请选择性别");
                    setSexData();
                    pickerScrollView.setData(list);
                    if (!tv_user_sex.getText().toString().equals(getResources().getString(R.string.no_write))) {
                        pickerScrollView.setSelectedContent(tv_user_sex.getText().toString());
                    }
                    mAlertDialog.show();
                    break;
                case R.id.ll_user_height:
                    select_type = 2;
                    tv_title.setText("请选择身高");
                    setHeightData();
                    pickerScrollView.setData(list);
                    if (!tv_user_height.getText().toString().equals(NO_DATA)) {
                        pickerScrollView.setSelectedContent(tv_user_height.getText().toString());
                    } else {
                        pickerScrollView.setSelectedContent("165CM");
                    }
                    mAlertDialog.show();
                    break;
                case R.id.ll_user_weight:
                    select_type = 3;
                    tv_title.setText("请选择体重");
                    setWeightData();
                    pickerScrollView.setData(list);
                    if (!tv_user_weight.getText().toString().equals(NO_DATA)) {
                        pickerScrollView.setSelectedContent(tv_user_weight.getText().toString());
                    } else {
                        pickerScrollView.setSelectedContent("55KG");
                    }
                    mAlertDialog.show();
                    break;
                case R.id.ll_user_blood:
                    select_type = 4;
                    tv_title.setText("请选择血型");
                    setBloodData();
                    pickerScrollView.setData(list);
                    if (!tv_user_blood.getText().toString().equals(NO_DATA))
                        pickerScrollView.setSelectedContent(tv_user_blood.getText().toString());
                    mAlertDialog.show();
                    break;
                case R.id.ll_user_birthday:
                    TimePickerView birthday_date = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date, View v) {//选中事件回调
                            tv_user_birthday.setText(TimeUtils.DateToString(date));
                            mBirthDate = date;
                            mUser.setBirthday(Long.toString(date.getTime() / 1000));
                        }
                    })
                            .setTitleText("请选择出生日期")//标题文字
                            .setType(new boolean[]{true, true, true, false, false, false})
                            .isDialog(true)
                            .build();
                    if (!tv_user_birthday.getText().toString().equals(NO_DATA)) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = sdf.parse(tv_user_birthday.getText().toString());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            birthday_date.setDate(calendar);
                        } catch (ParseException e) {
                            birthday_date.setDate(Calendar.getInstance());
                        }
                    } else {
                        birthday_date.setDate(Calendar.getInstance());
                    }
                    birthday_date.show();
                    break;
                case R.id.ll_user_recent_menstruation:
                    TimePickerView pvTime = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date, View v) {
                            tv_user_recent_menstruation.setText(TimeUtils.DateToString(date));
                        }
                    })
                            .setTitleText("请选择最近经期")
                            .setType(new boolean[]{true, true, true, false, false, false})
                            .isDialog(true)
                            .build();
                    if (!tv_user_recent_menstruation.getText().toString().equals(NO_DATA)) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = sdf.parse(tv_user_recent_menstruation.getText().toString());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            pvTime.setDate(calendar);
                        } catch (ParseException e) {
                            pvTime.setDate(Calendar.getInstance());
                        }
                    } else {
                        pvTime.setDate(Calendar.getInstance());
                    }
                    pvTime.show();
                    break;
                case R.id.ll_user_menstruation:
                    select_type = 7;
                    tv_title.setText("请选择经期");
                    setMenstruationData();
                    pickerScrollView.setData(list);
                    if (!tv_user_menstruation.getText().toString().equals(NO_DATA))
                        pickerScrollView.setSelectedContent(tv_user_menstruation.getText().toString());
                    mAlertDialog.show();
                    break;
                case R.id.ll_user_menstruation_cycle:
                    select_type = 8;
                    tv_title.setText("请选择周期");
                    setMenstruationCycleData();
                    pickerScrollView.setData(list);
                    if (!tv_user_menstruation_cycle.getText().toString().equals(NO_DATA))
                        pickerScrollView.setSelectedContent(tv_user_menstruation_cycle.getText().toString());
                    mAlertDialog.show();
                    break;
                case R.id.ll_user_illhistory:
                    EditDialog.Builder ill_builder;
                    if (!tv_user_illhistory.getText().toString().equals(NO_DATA)) {
                        ill_builder = new EditDialog.Builder(mContext, tv_user_illhistory.getText().toString(), "请输入疾病史");
                    } else {
                        ill_builder = new EditDialog.Builder(mContext, "", "请输入疾病史");
                    }
                    ill_builder.setOnSureButtonClickListener(new EditDialog.Builder.OnSureButtonClickListener() {
                        @Override
                        public void click(String string) {
                            tv_user_illhistory.setText(string);
                            try {
                                mUser.setIllHistory(Des.encode(string));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    ill_builder.create().show();
                    break;
                case R.id.bt_picker_sure:
                    mAlertDialog.dismiss();
                    setText();
                    if (select_type == 1) {
                        if (tv_user_sex.getText().toString().equals("男")) {
                            ll_woman.setVisibility(View.GONE);
                        } else {
                            ll_woman.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case R.id.iv_return:
                    if (mUserInfoResult.getSex() != null) {
                        if (mUserInfoResult.getSex() == 0) {
                            if (!menstruation_before.equals(tv_user_menstruation_cycle.getText().toString() + tv_user_menstruation.getText().toString() + tv_user_recent_menstruation.getText().toString())) {
                                confirmQuit();
                                return;
                            }
                        }
                    }
                    if (!mUser.toString().equals(old_userInfoResult.toString())) {
                        confirmQuit();
                        return;
                    }
                    finish();
                    break;
            }
        }
    }


    /**
     * 获取经期信息
     */
    private void getMenstruationData() {

        HttpUtils.getInstantce().postNoHead(HttpConstant.Url_getMenstruationData, new HttpCallBack<DetectionMenstruation>() {
            @Override
            public void onSuccess(HttpResult result) {
                menstruation_before = "";
                DetectionMenstruation detectionMenstruation = (DetectionMenstruation) result.getData();
                if (detectionMenstruation != null && detectionMenstruation.getAverageMenstrual() != null) {
                    tv_user_menstruation_cycle.setText(detectionMenstruation.getAverageMenstrual() + "天");
                    menstruation_before = menstruation_before + detectionMenstruation.getAverageMenstrual() + "天";
                } else {
                    tv_user_menstruation_cycle.setText(NO_DATA);
                }
                if (detectionMenstruation != null && detectionMenstruation.getAverageMenstrualPeriod() != null) {
                    tv_user_menstruation.setText(detectionMenstruation.getAverageMenstrualPeriod() + "天");
                    menstruation_before = menstruation_before + detectionMenstruation.getAverageMenstrualPeriod() + "天";
                } else {
                    tv_user_menstruation.setText(NO_DATA);
                }
                if (detectionMenstruation != null && detectionMenstruation.getMenstrualTime() != null) {
                    tv_user_recent_menstruation.setText(TimeUtils.DateToString(detectionMenstruation.getMenstrualTime()));
                    menstruation_before = menstruation_before + TimeUtils.DateToString(detectionMenstruation.getMenstrualTime());
                } else {
                    tv_user_recent_menstruation.setText(NO_DATA);
                }
            }

            @Override
            public void onFail(String msg) {
            }
        });


       /* HttpUtils.getInstantce().getMenstruationData(new HttpConstant.SampleJsonResultListener<Feedback<DetectionMenstruation>>() {
            @Override
            public void onSuccess(Feedback<DetectionMenstruation> jsonData) {
                menstruation_before = "";
                if (jsonData.getData().getAverageMenstrual() != null) {
                    tv_user_menstruation_cycle.setText(jsonData.getData().getAverageMenstrual() + "天");
                    menstruation_before = menstruation_before + jsonData.getData().getAverageMenstrual() + "天";
                } else {
                    tv_user_menstruation_cycle.setText(NO_DATA);
                }
                if (jsonData.getData().getAverageMenstrualPeriod() != null) {
                    tv_user_menstruation.setText(jsonData.getData().getAverageMenstrualPeriod() + "天");
                    menstruation_before = menstruation_before + jsonData.getData().getAverageMenstrualPeriod() + "天";
                } else {
                    tv_user_menstruation.setText(NO_DATA);
                }
                if (jsonData.getData().getMenstrualTime() != null) {
                    tv_user_recent_menstruation.setText(TimeUtils.DateToString(jsonData.getData().getMenstrualTime()));
                    menstruation_before = menstruation_before + TimeUtils.DateToString(jsonData.getData().getMenstrualTime());
                } else {
                    tv_user_recent_menstruation.setText(NO_DATA);
                }
            }

            @Override
            public void onFailure(Feedback<DetectionMenstruation> jsonData) {
            }
        });*/
    }

    /**
     * 修改经期信息
     */
    private void saveMenstruationData(int userId, int averageMenstrual, int averageMenstrualPeriod, String menstrualTime) {

        Map<String, Object> maps = new HashMap<>();
        maps.put("userId", userId);
        maps.put("averageMenstrual", averageMenstrual);
        maps.put("averageMenstrualPeriod", averageMenstrualPeriod);
        long time = DateUtils.getTimeStampP(menstrualTime);
        maps.put("menstrualTime", time / 1000 + "");

        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_saveMenstruationData, new HttpCallBack<DetectionMenstruation>() {
            @Override
            public void onSuccess(HttpResult result) {
                modifyUser(mUser);
            }

            @Override
            public void onFail(String msg) {

            }
        });

/*
        HttpUtils.getInstantce().saveMenstruationData(userId, averageMenstrual, averageMenstrualPeriod, menstrualTime, new HttpConstant.SampleJsonResultListener<Feedback>() {
            @Override
            public void onSuccess(Feedback jsonData) {
                modifyUser(mUser);
            }

            @Override
            public void onFailure(Feedback jsonData) {
                if (jsonData == null || jsonData.getMsg() == null) {
                    HttpUtils.getInstantce().changeSweetDialog("网络故障，请稍后重试");
                } else {
                    HttpUtils.getInstantce().changeSweetDialog(jsonData.getMsg());
                }
            }
        });
*/
    }

    /**
     * 提交修改后的个人信息
     * String avatar, String nickname, int height, Double weight, String boold, String illHistory, String birthday
     * avatar, nickname, height, weight, boold, illHistory, birthday,
     */
    private void modifyUser(final User2 user) {


        Map<String, Object> maps = new HashMap<>();
        maps.put("nickname", user.getNickname());
        maps.put("birthday", user.getBirthday());
        maps.put("blood", user.getBlood());
        maps.put("illHistory", user.getIllHistory());
        maps.put("height", user.getHeight());
        maps.put("weight", user.getWeight());
        maps.put("avatar", user.getAvatar());
        maps.put("sex", user.getSex());

        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_modifyUser, new HttpCallBack<UserInfoResult>() {
            @Override
            public void onSuccess(HttpResult result) {
                ApplicationXpClient.userInfoResult.setNickname(user.getNickname());

                if (user.getWeight() != null) {
                    ApplicationXpClient.userInfoResult.setWeight(user.getWeight());
                } else {
                    ApplicationXpClient.userInfoResult.setWeight(null);
                }
                if (user.getHeight() != null) {
                    ApplicationXpClient.userInfoResult.setHeight(user.getHeight());
                } else {
                    ApplicationXpClient.userInfoResult.setHeight(null);
                }

                if (user.getIllHistory() != null) {
                    ApplicationXpClient.userInfoResult.setIllHistory(user.getIllHistory());
                } else {
                    ApplicationXpClient.userInfoResult.setIllHistory(null);
                }

                if (user.getBlood() != null) {
                    ApplicationXpClient.userInfoResult.setBlood(user.getBlood());
                } else {
                    ApplicationXpClient.userInfoResult.setBlood(null);
                }

                if (mBirthDate != null) {
                    ApplicationXpClient.userInfoResult.setBirthday(mBirthDate);
                } else {
                    ApplicationXpClient.userInfoResult.setBirthday(null);
                }
                ApplicationXpClient.userInfoResult.setAvatar(user.getAvatar());

                String c_userInfo = DESCipher.doEncrypt(HttpUtils.gb.create().toJson(ApplicationXpClient.userInfoResult), HttpConstant.Des_W_Key);
                ControlSave.save(getApplicationContext(), "rencent_user_new", c_userInfo);

                SweetAlertDialog mSweetDialog = HttpUtils.getInstantce().getSweetDialog();
                mSweetDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                mSweetDialog.setTitleText("提示");
                mSweetDialog.setContentText("保存成功");
                mSweetDialog.setConfirmText("确定");
                mSweetDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        MyInfoActivity.this.finish();
                    }
                });
            }

            @Override
            public void onFail(String msg) {

            }
        });

      /*  HttpUtils.getInstantce().modifyUser(user, new HttpConstant.SampleJsonResultListener<Feedback>() {
            @Override
            public void onSuccess(Feedback jsonData) {
                ApplicationXpClient.userInfoResult.setNickname(user.getNickname());

                if (user.getWeight() != null) {
                    ApplicationXpClient.userInfoResult.setWeight(user.getWeight());
                } else {
                    ApplicationXpClient.userInfoResult.setWeight(null);
                }
                if (user.getHeight() != null) {
                    ApplicationXpClient.userInfoResult.setHeight(user.getHeight());
                } else {
                    ApplicationXpClient.userInfoResult.setHeight(null);
                }

                if (user.getIllHistory() != null) {
                    ApplicationXpClient.userInfoResult.setIllHistory(user.getIllHistory());
                } else {
                    ApplicationXpClient.userInfoResult.setIllHistory(null);
                }

                if (user.getBlood() != null) {
                    ApplicationXpClient.userInfoResult.setBlood(user.getBlood());
                } else {
                    ApplicationXpClient.userInfoResult.setBlood(null);
                }

                if (mBirthDate != null) {
                    ApplicationXpClient.userInfoResult.setBirthday(mBirthDate);
                } else {
                    ApplicationXpClient.userInfoResult.setBirthday(null);
                }
                ApplicationXpClient.userInfoResult.setAvatar(user.getAvatar());


                String c_userInfo = DESCipher.doEncrypt(HttpUtils.gb.create().toJson(ApplicationXpClient.userInfoResult), HttpConstant.Des_W_Key);
                ControlSave.save(getApplicationContext(), "rencent_user_new", c_userInfo);

                SweetAlertDialog mSweetDialog = HttpUtils.getInstantce().getSweetDialog();
                mSweetDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                mSweetDialog.setTitleText("提示");
                mSweetDialog.setContentText("保存成功");
                mSweetDialog.setConfirmText("确定");
                mSweetDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        MyInfoActivity.this.finish();
                    }
                });

            }

            @Override
            public void onFailure(Feedback jsonData) {
                if (jsonData == null || jsonData.getMsg() == null) {
                    HttpUtils.getInstantce().changeSweetDialog("网络故障，请稍后重试");
                } else {
                    HttpUtils.getInstantce().changeSweetDialog(jsonData.getMsg());
                }
            }
        });*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 此处的用于判断接收的Activity是不是你想要的那个（参数RESULT_LOAD_IMAGE）,data有没有，resultCode是否为-1
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_LOAD_IMAGE:
                    //相册返回结果
                    cropFile = cameraAndPicUntil.cropRawPhoto(data.getData(), CODE_RESULT_REQUEST);
                    break;
                case CODE_CAMERA_REQUEST:
                    //拍照返回的结果
                    if (cameraFile != null && cameraFile.exists()) {
                        cropFile = cameraAndPicUntil.cropRawPhoto(FileProvider.getUriForFile(mContext, "com.xiaopu.customer.fileprovider", cameraFile), CODE_RESULT_REQUEST);
                    }
                    break;
                case CODE_RESULT_REQUEST:
                    setPicToView();
                    break;
            }
        }
    }


    /**
     * 保存裁剪之后的图片数据
     */
    private void setPicToView() {
        if (cropFile != null && cropFile.exists()) {
            ImageLoader.getInstance().displayImage(Uri.fromFile(cropFile).toString(), iv_user_header);
            Log.d(LOG_TAG, "+++" + Uri.fromFile(cropFile).toString());
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
     * 上传头像
     */
    private void upLoadPhoto(File file) {

        HttpUtils.getInstantce().showSweetDialog("上传中...");
        Map<String, Object> maps = new HashMap<>();
        maps.put("profile_picture", file);

        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_uploadAvatar, new HttpCallBack<String>() {
            @Override
            public void onSuccess(HttpResult result) {
                HttpUtils.getInstantce().changeRightSweetDialog(getString(R.string.upload_success));
                String uri = (String) result.getData();
                mUser.setAvatar(uri);
            }

            @Override
            public void onFail(String msg) {

            }
        });

      /*  HttpUtils.getInstantce().uploadAvatar(HttpConstant.Url_uploadAvatar, file, new HttpConstant.SampleJsonResultListener<Feedback<String>>() {
            @Override
            public void onSuccess(Feedback<String> jsonData) {
                mUser.setAvatar(jsonData.getData());
            }

            @Override
            public void onFailure(Feedback<String> jsonData) {
            }
        });*/
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if (mUserInfoResult.getSex() != null) {
                if (mUserInfoResult.getSex() == 0) {
                    if (!menstruation_before.equals(tv_user_menstruation_cycle.getText().toString() + tv_user_menstruation.getText().toString() + tv_user_recent_menstruation.getText().toString())) {
                        confirmQuit();
                        return false;
                    }
                }
            }
            if (!mUser.toString().equals(old_userInfoResult.toString())) {
                confirmQuit();
                return false;
            }
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setHeightData() {
        list.clear();
        for (int i = 50; i <= 200; i++) {
            list.add(new Pickers("" + i + "CM", "" + i));
        }
    }

    private void setWeightData() {
        list.clear();
        for (int i = 20; i <= 150; i++) {
            list.add(new Pickers("" + i + "KG", "" + i));
        }
    }

    private void setBloodData() {
        list.clear();
        list.add(new Pickers("A", "0"));
        list.add(new Pickers("B", "1"));
        list.add(new Pickers("AB", "2"));
        list.add(new Pickers("O", "3"));
    }

    private void setMenstruationData() {
        list.clear();
        for (int i = 2; i <= 10; i++) {
            list.add(new Pickers(i + "天", "" + i));
        }
    }

    private void setMenstruationCycleData() {
        list.clear();
        for (int i = 20; i <= 35; i++) {
            list.add(new Pickers(i + "天", "" + i));
        }
    }

    private void setSexData() {
        list.clear();
        id = new String[]{"1", "2", "3", "4"};
        name = new String[]{"男", "女", "男", "女"};
        for (int i = 0; i < name.length; i++) {
            list.add(new Pickers(name[i], id[i]));
        }
    }

    private void setText() {
        switch (select_type) {
            case 1:
                tv_user_sex.setText(pickerScrollView.getSelectData().getShowConetnt());
                if (tv_user_sex.getText().toString().equals("男")) {
                    mUser.setSex(1);
                } else {
                    mUser.setSex(0);
                }
                break;
            case 2:
                tv_user_height.setText(pickerScrollView.getSelectData().getShowConetnt());
                mUser.setHeight(Integer.valueOf(tv_user_height.getText().toString().replace("CM", "")));
                break;
            case 3:
                tv_user_weight.setText(pickerScrollView.getSelectData().getShowConetnt());
                mUser.setWeight(Double.valueOf(tv_user_weight.getText().toString().replace("KG", "")));
                break;
            case 4:
                tv_user_blood.setText(pickerScrollView.getSelectData().getShowConetnt());
                mUser.setBlood(tv_user_blood.getText().toString());
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                tv_user_menstruation.setText(pickerScrollView.getSelectData().getShowConetnt());
                break;
            case 8:
                tv_user_menstruation_cycle.setText(pickerScrollView.getSelectData().getShowConetnt());
                break;
        }
    }

    private void confirmQuit() {
        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("提示")
                .setContentText("您的资料尚未保存，是否确认退出")
                .setCancelText("取消")
                .setConfirmText("确定")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .show();
    }

}
