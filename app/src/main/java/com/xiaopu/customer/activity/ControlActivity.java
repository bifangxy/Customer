package com.xiaopu.customer.activity;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.dialog.AutoCleanDialog;
import com.xiaopu.customer.service.BluetoothService;
import com.xiaopu.customer.utils.ControlSave;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.Config;
import com.xiaopu.customer.utils.guide.GuideHelper;
import com.xiaopu.customer.utils.guide.GuideHelper.TipData;
import com.xiaopu.customer.view.DrawableCenterTextView;
import com.xiaopu.customer.view.GradientProgressBar;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;

import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2017/5/17.
 */

public class ControlActivity extends ActivityBase {

    private static final String LOG_TAG = ControlActivity.class.getSimpleName();

    private static final int MES_OPEN_DOUBLE = 0;
    private static final int MES_CLOSE_DOUBLE = 1;
    private static final int MES_OPEN_SIGNAL = 2;
    private static final int MES_NO_FLIP_STATA = 3;
    private static final int MES_HIP_WASH_OPEN = 6;
    private static final int MES_HIP_WASH_CLOSE = 7;
    private static final int MES_WOMAN_WASH_OPEN = 8;
    private static final int MES_WOMAN_WASH_CLOSE = 9;
    private static final int MES_LAXATIVE_OPEN = 10;
    private static final int MES_LAXATIVE_CLOSE = 11;
    private static final int MES_MASSAGE_OPEN = 12;
    private static final int MES_MASSAGE_CLOSE = 13;
    private static final int MES_DRY_OPEN = 14;
    private static final int MES_DRY_CLOSE = 15;
    private static final int MES_FUMIGATION_OPEN = 16;
    private static final int MES_FUMIGATION_CLOSE = 17;
    private static final int MSG_NO_SEAT = 18;
    private static final int MSG_SEAT = 19;
    private static final int MSG_NO_CONNECT = 20;
    private static final int MSG_WATER_ERROR = 21;
    private static final int MSG_WATER_RIGHT = 22;
    private static final int MSG_CUSHION_ERROR = 23;
    private static final int MSG_CUSHION_RIGHT = 24;
    private static final int MSG_DRY_ERROR = 25;
    private static final int MSG_DRY_RIGHT = 26;

    private static final int SEAT_STATE = 1;

    private static final int NO_SEAT_STATE = 2;

    private static final int SLEEP_STATE = 3;

    private Context mContext;

    private MyClickListener mClick;

    private SeekBar mSeekBar;

    private Button bt_stall_one;

    private Button bt_stall_two;

    private Button bt_stall_three;

    private Button bt_stall_four;

    private Button bt_stall_five;

    private Button bt_stall_six;

    private GifImageView iv_animation;

    private ImageView iv_stall_text;

    private GradientProgressBar mGradientProgressBar;

    private DrawableCenterTextView tv_cushion_temp;

    private DrawableCenterTextView tv_water_temp;

    private DrawableCenterTextView tv_nozzle_position;

    private DrawableCenterTextView tv_dry_temp;

    private DrawableCenterTextView tv_water_pressure;

    private DrawableCenterTextView tv_open_double;

    private DrawableCenterTextView tv_open_signal;

    private DrawableCenterTextView tv_close_flip;

    private DrawableCenterTextView tv_hip_wash;

    private DrawableCenterTextView tv_woman_wash;

    private DrawableCenterTextView tv_laxative;

    private DrawableCenterTextView tv_massage;

    private DrawableCenterTextView tv_dry;

    private DrawableCenterTextView tv_fumigation;

    private FrameLayout frameLayout_toilet_state;

    private LinearLayout ll_toilet_gear;

    private LinearLayout ll_toilet_regulation;

    private LinearLayout ll_toilet_function;

    private BluetoothService bcs;

    private int bar_progress = 0;

    private int cushion_temp_stall;

    private int water_temp_stall;

    private int nozzle_position_stall;

    private int dry_temp_stall;

    private int water_pressure_stall;

    private Timer mTimer;

    private AnimationDrawable ad_hip_wash;

    private AnimationDrawable ad_woman_wash;

    private AnimationDrawable ad_laxative;

    private AnimationDrawable ad_massage;

    private AnimationDrawable ad_dry;

    private AnimationDrawable ad_fumigation;

    private int toilet_state;

    private boolean isWaterError = false;

    private boolean isDryError = false;

    private boolean isCushionError = false;

    private SweetAlertDialog wait_dialog;

    private AutoCleanDialog autoCleanDialog;

    private int wait_count;

    private int max_up_flip = 3620;

    private int max_down_flip = 3680;

    private int min_up_flip = 750;

    private int min_down_flip = 700;

    private int recent_progress;

    private TimerTask mTask = new TimerTask() {
        @Override
        public void run() {
            if (ApplicationXpClient.isConnect()) {
                //通便进行中
                if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8) != 0) {
                    mHandler.sendEmptyMessage(MES_LAXATIVE_OPEN);
                } else {
                    mHandler.sendEmptyMessage(MES_LAXATIVE_CLOSE);
                }
                //臀洗进行中
                if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 9) != 0) {
                    mHandler.sendEmptyMessage(MES_HIP_WASH_OPEN);
                } else {
                    mHandler.sendEmptyMessage(MES_HIP_WASH_CLOSE);
                }
                //妇洗进行中
                if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 10) != 0) {
                    mHandler.sendEmptyMessage(MES_WOMAN_WASH_OPEN);
                } else {
                    mHandler.sendEmptyMessage(MES_WOMAN_WASH_CLOSE);
                }
                //烘干进行中
                if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 3) != 0) {
                    mHandler.sendEmptyMessage(MES_DRY_OPEN);
                } else {
                    mHandler.sendEmptyMessage(MES_DRY_CLOSE);
                }
                //按摩进行中
                if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 15) != 0) {
                    mHandler.sendEmptyMessage(MES_MASSAGE_OPEN);
                } else {
                    mHandler.sendEmptyMessage(MES_MASSAGE_CLOSE);
                }
                //熏疗进行中
                if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 2) != 0) {
                    mHandler.sendEmptyMessage(MES_FUMIGATION_OPEN);
                } else {
                    mHandler.sendEmptyMessage(MES_FUMIGATION_CLOSE);
                }
                //已着座
                if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_STATUS1, 13) == 1) {
                    mHandler.sendEmptyMessage(MSG_SEAT);
                } else {
                    mHandler.sendEmptyMessage(MSG_NO_SEAT);
                }
                if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_ERROR, 3) == 1) {
                    //TODO 坐垫温度调节显示红色
                    mHandler.sendEmptyMessage(MSG_CUSHION_ERROR);
                    isCushionError = true;
                } else {
                    mHandler.sendEmptyMessage(MSG_CUSHION_RIGHT);
                    isCushionError = false;
                }
                if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_ERROR, 4) == 1 || bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_ERROR, 10) == 1) {
                    //TODO 烘干温度调节显示红色
                    mHandler.sendEmptyMessage(MSG_DRY_ERROR);
                    isDryError = true;
                } else {
                    isDryError = false;
                    mHandler.sendEmptyMessage(MSG_DRY_RIGHT);
                }
                if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_ERROR, 0) == 1 || bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_ERROR, 1) == 1 || bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_ERROR, 2) == 1 || bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_ERROR, 5) == 1 || bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_ERROR, 9) == 1) {
                    //TODO 水温调节显示红色
                    mHandler.sendEmptyMessage(MSG_WATER_ERROR);
                    isWaterError = true;
                } else {
                    isWaterError = false;
                    mHandler.sendEmptyMessage(MSG_WATER_RIGHT);
                }
                //翻盖状态（待实际验证,已预留手动设置最大值和最小值接口）
                   /* if (max_up_flip != 0 && max_down_flip != 0 && min_up_flip != 0 && min_down_flip != 0) {
                        if (bcs.getRegisterValue(1, Config.REGISTER_TOILET_AUX1) > max_up_flip - 100) {
                            if (bcs.getRegisterValue(1, Config.REGISTER_TOILET_AUX2) > max_down_flip - 100) {
                                mHandler.sendEmptyMessage(MES_OPEN_DOUBLE);
                            } else {
                                mHandler.sendEmptyMessage(MES_OPEN_SIGNAL);
                            }
                        } else if (bcs.getRegisterValue(1, Config.REGISTER_TOILET_AUX1) < min_up_flip + 100) {
                            mHandler.sendEmptyMessage(MES_CLOSE_DOUBLE);
                        } else {
                            if (bcs.getRegisterValue(1, Config.REGISTER_TOILET_AUX2) < min_down_flip + 100) {
                                mHandler.sendEmptyMessage(MES_OPEN_SIGNAL);
                            } else {
                                mHandler.sendEmptyMessage(MES_NO_FLIP_STATA);
                            }
                        }
                    }*/
            } else {
                mHandler.sendEmptyMessage(MSG_NO_CONNECT);
            }
        }
    };


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MES_OPEN_DOUBLE:
                    tv_open_double.setSelected(true);
                    tv_open_signal.setSelected(false);
                    tv_close_flip.setSelected(false);
                    break;
                case MES_CLOSE_DOUBLE:
                    tv_open_double.setSelected(false);
                    tv_open_signal.setSelected(false);
                    tv_close_flip.setSelected(true);
                    break;
                case MES_OPEN_SIGNAL:
                    tv_open_double.setSelected(false);
                    tv_open_signal.setSelected(true);
                    tv_close_flip.setSelected(false);
                    break;
                case MES_NO_FLIP_STATA:
                    tv_open_double.setSelected(false);
                    tv_open_signal.setSelected(false);
                    tv_close_flip.setSelected(false);
                    break;
                case MES_HIP_WASH_OPEN:
                    if (ad_hip_wash == null) {
                        iv_animation.setImageResource(R.drawable.control_hip_wash);
                        ad_hip_wash = (AnimationDrawable) iv_animation.getDrawable();
                        ad_hip_wash.start();
                        tv_hip_wash.setSelected(true);
                    }
                    break;
                case MES_HIP_WASH_CLOSE:
                    stopAnimation(ad_hip_wash);
                    ad_hip_wash = null;
                    tv_hip_wash.setSelected(false);
                    break;
                case MES_WOMAN_WASH_OPEN:
                    if (ad_woman_wash == null) {
                        iv_animation.setImageResource(R.drawable.control_woman_wash);
                        ad_woman_wash = (AnimationDrawable) iv_animation.getDrawable();
                        ad_woman_wash.start();
                        tv_woman_wash.setSelected(true);
                    }
                    break;
                case MES_WOMAN_WASH_CLOSE:
                    tv_woman_wash.setSelected(false);
                    stopAnimation(ad_woman_wash);
                    ad_woman_wash = null;
                    break;
                case MES_LAXATIVE_OPEN:
                    if (ad_laxative == null) {
                        iv_animation.setImageResource(R.drawable.control_laxative);
                        ad_laxative = (AnimationDrawable) iv_animation.getDrawable();
                        tv_laxative.setSelected(true);
                        ad_laxative.start();
                    }
                    break;
                case MES_LAXATIVE_CLOSE:
                    tv_laxative.setSelected(false);
                    stopAnimation(ad_laxative);
                    ad_laxative = null;
                    break;
                case MES_MASSAGE_OPEN:
                    if (ad_massage == null) {
                        iv_animation.setImageResource(R.drawable.control_massage);
                        ad_massage = (AnimationDrawable) iv_animation.getDrawable();
                        tv_massage.setSelected(true);
                        ad_massage.start();
                    }
                    break;
                case MES_MASSAGE_CLOSE:
                    tv_massage.setSelected(false);
                    stopAnimation(ad_massage);
                    ad_massage = null;
                    break;
                case MES_DRY_OPEN:
                    if (ad_dry == null) {
                        iv_animation.setImageResource(R.drawable.control_dry);
                        ad_dry = (AnimationDrawable) iv_animation.getDrawable();
                        tv_dry.setSelected(true);
                        ad_dry.start();
                    }
                    break;
                case MES_DRY_CLOSE:
                    tv_dry.setSelected(false);
                    stopAnimation(ad_dry);
                    ad_dry = null;
                    break;
                case MES_FUMIGATION_OPEN:
                    if (ad_fumigation == null) {
                        iv_animation.setImageResource(R.drawable.control_fumigation);
                        ad_fumigation = (AnimationDrawable) iv_animation.getDrawable();
                        tv_fumigation.setSelected(true);
                        ad_fumigation.start();
                    }
                    break;
                case MES_FUMIGATION_CLOSE:
                    tv_fumigation.setSelected(false);
                    stopAnimation(ad_fumigation);
                    ad_fumigation = null;
                    break;
                case MSG_NO_SEAT:
                    if (toilet_state != NO_SEAT_STATE) {
                        toilet_state = NO_SEAT_STATE;
                        iv_animation.setImageResource(R.drawable.no_seat_icon);
                        cleanAnimation();
                    }
                    break;
                case MSG_SEAT:
                    if (toilet_state != SEAT_STATE) {
                        toilet_state = SEAT_STATE;
                        iv_animation.setImageResource(R.drawable.seat_icon);
                        cleanAnimation();
                    }
                    break;
                case MSG_NO_CONNECT:
                    if (toilet_state != SLEEP_STATE) {
                        toilet_state = SLEEP_STATE;
                        iv_animation.setImageResource(R.drawable.un_connect);
                        cleanAnimation();
                    }
                    break;
                case MSG_WATER_ERROR:
                    Drawable water_temp_error = getResources().getDrawable(R.mipmap.icon_water_error);
                    water_temp_error.setBounds(0, 0, (int) (water_temp_error.getMinimumWidth()), (int) (water_temp_error.getMinimumHeight()));
                    tv_water_temp.setCompoundDrawables(null, water_temp_error, null, null);
                    tv_water_temp.setTextColor(getColor(R.color.red));
                    break;
                case MSG_WATER_RIGHT:
                    Drawable water_temp = getResources().getDrawable(R.drawable.icon_water_temp_selector);
                    water_temp.setBounds(0, 0, (int) (water_temp.getMinimumWidth()), (int) (water_temp.getMinimumHeight()));
                    tv_water_temp.setCompoundDrawables(null, water_temp, null, null);
                    tv_water_temp.setTextColor(getColorStateList(R.color.selector_text_color));
                    break;
                case MSG_CUSHION_ERROR:
                    Drawable drawable_satisfaction = getResources().getDrawable(R.mipmap.icon_cushion_error);
                    drawable_satisfaction.setBounds(0, 0, (int) (drawable_satisfaction.getMinimumWidth()), (int) (drawable_satisfaction.getMinimumHeight()));
                    tv_cushion_temp.setCompoundDrawables(null, drawable_satisfaction, null, null);
                    tv_cushion_temp.setTextColor(getColor(R.color.red));
                    break;
                case MSG_CUSHION_RIGHT:
//                    tv_cushion_temp.setEnabled(false);
                    Drawable cushion_temp = getResources().getDrawable(R.drawable.icon_cushion_temp_selector);
                    cushion_temp.setBounds(0, 0, (int) (cushion_temp.getMinimumWidth()), (int) (cushion_temp.getMinimumHeight()));
                    tv_cushion_temp.setCompoundDrawables(null, cushion_temp, null, null);
                    tv_cushion_temp.setTextColor(getColorStateList(R.color.selector_text_color));
                    break;
                case MSG_DRY_ERROR:
                    Drawable dry_temp_error = getResources().getDrawable(R.mipmap.icon_dry_error);
                    dry_temp_error.setBounds(0, 0, (int) (dry_temp_error.getMinimumWidth()), (int) (dry_temp_error.getMinimumHeight()));
                    tv_dry_temp.setCompoundDrawables(null, dry_temp_error, null, null);
                    tv_dry_temp.setTextColor(getColor(R.color.red));
                    break;
                case MSG_DRY_RIGHT:
                    Drawable dry_temp = getResources().getDrawable(R.drawable.icon_dry_temp_selector);
                    dry_temp.setBounds(0, 0, (int) (dry_temp.getMinimumWidth()), (int) (dry_temp.getMinimumHeight()));
                    tv_dry_temp.setCompoundDrawables(null, dry_temp, null, null);
                    tv_dry_temp.setTextColor(getResources().getColorStateList(R.color.selector_text_color));
                    break;
                case 104:
                    autoCleanDialog.dismiss();
                    break;
            }
            return false;
        }
    });

    private void cleanAnimation() {

        ad_hip_wash = null;
        ad_woman_wash = null;
        ad_laxative = null;
        ad_massage = null;
        ad_dry = null;
        ad_fumigation = null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        initActionBar(getString(R.string.intelligent_control));
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mSeekBar = findViewById(R.id.seekBar_stall);
        iv_stall_text = findViewById(R.id.iv_stall_text);
        mGradientProgressBar = findViewById(R.id.gradientProgressBar);
        iv_animation = findViewById(R.id.iv_control_animation);

        tv_cushion_temp = findViewById(R.id.tv_cushion_temp);
        tv_water_temp = findViewById(R.id.tv_water_temp);
        tv_nozzle_position = findViewById(R.id.tv_nozzle_position);
        tv_dry_temp = findViewById(R.id.tv_dry_temp);
        tv_water_pressure = findViewById(R.id.tv_water_pressure);
        tv_open_double = findViewById(R.id.tv_control_open_double);
        tv_open_signal = findViewById(R.id.tv_control_open_signal);
        tv_close_flip = findViewById(R.id.tv_control_close_flip);
        tv_hip_wash = findViewById(R.id.tv_control_hip_wash);
        tv_woman_wash = findViewById(R.id.tv_control_woman_wash);
        tv_laxative = findViewById(R.id.tv_control_laxative);
        tv_massage = findViewById(R.id.tv_control_massage);
        tv_dry = findViewById(R.id.tv_control_dry);
        tv_fumigation = findViewById(R.id.tv_control_fumigation);

        bt_stall_one = findViewById(R.id.bt_stall_one);
        bt_stall_two = findViewById(R.id.bt_stall_two);
        bt_stall_three = findViewById(R.id.bt_stall_three);
        bt_stall_four = findViewById(R.id.bt_stall_four);
        bt_stall_five = findViewById(R.id.bt_stall_five);
        bt_stall_six = findViewById(R.id.bt_stall_six);

        frameLayout_toilet_state = findViewById(R.id.frameLayout_toilet_state);
        ll_toilet_gear = findViewById(R.id.ll_toilet_gear);
        ll_toilet_regulation = findViewById(R.id.ll_toilet_regulation);
        ll_toilet_function = findViewById(R.id.ll_toilet_function);

    }

    private void initData() {
        mClick = new MyClickListener();
        bcs = ApplicationXpClient.getInstance().getBluetoothService();
        //TODO 调试用
//        bcs.setRegisterBitValue(1, Config.REGISTER_TOILET_STATUS1, 14, 1);
        bcs.iStartAddr = 50;
        bcs.iScanLengh = 30;
        initStall();
        mTimer = new Timer();
        mTimer.schedule(mTask, 100, 1000);
        String isControlGuide = ControlSave.read(mContext, "isControlGuide", "");
        if (TextUtils.isEmpty(isControlGuide)) {
            showGuide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTask = null;
        }
    }

    private void initStall() {
        cushion_temp_stall = bcs.getRegister4HighValuebefore(1, Config.REGISTER_TOILET_CONFIG1);
        water_temp_stall = bcs.getRegister4HighValue(1, Config.REGISTER_TOILET_CONFIG1);
        water_pressure_stall = bcs.getRegister4HighValue(1, Config.REGISTER_TOILET_CONFIG2);
        nozzle_position_stall = bcs.getRegister4LowValuebefore(1, Config.REGISTER_TOILET_CONFIG1);
        dry_temp_stall = bcs.getRegister4LowValue(1, Config.REGISTER_TOILET_CONFIG1);

        tv_cushion_temp.setSelected(true);

        if (cushion_temp_stall > 5) {
            cushion_temp_stall = 5;
        }
        mSeekBar.setProgress(cushion_temp_stall * 20);
        mGradientProgressBar.setPercent(cushion_temp_stall * 20);

    }

    private void initListener() {
        tv_cushion_temp.setOnClickListener(mClick);
        tv_water_temp.setOnClickListener(mClick);
        tv_nozzle_position.setOnClickListener(mClick);
        tv_dry_temp.setOnClickListener(mClick);
        tv_water_pressure.setOnClickListener(mClick);
        tv_open_double.setOnClickListener(mClick);
        tv_open_signal.setOnClickListener(mClick);
        tv_close_flip.setOnClickListener(mClick);
        tv_hip_wash.setOnClickListener(mClick);
        tv_woman_wash.setOnClickListener(mClick);
        tv_laxative.setOnClickListener(mClick);
        tv_massage.setOnClickListener(mClick);
        tv_dry.setOnClickListener(mClick);
        tv_fumigation.setOnClickListener(mClick);
        tv_hip_wash.setOnClickListener(mClick);

        bt_stall_one.setOnClickListener(mClick);
        bt_stall_two.setOnClickListener(mClick);
        bt_stall_three.setOnClickListener(mClick);
        bt_stall_four.setOnClickListener(mClick);
        bt_stall_five.setOnClickListener(mClick);
        bt_stall_six.setOnClickListener(mClick);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bar_progress = progress;
                mGradientProgressBar.setPercent(progress);
                switch (progress) {
                    case 0:
                        iv_stall_text.setImageResource(R.mipmap.stall_one);
                        break;
                    case 20:
                        iv_stall_text.setImageResource(R.mipmap.stall_two);
                        break;
                    case 40:
                        iv_stall_text.setImageResource(R.mipmap.stall_three);
                        break;
                    case 60:
                        iv_stall_text.setImageResource(R.mipmap.stall_four);
                        break;
                    case 80:
                        iv_stall_text.setImageResource(R.mipmap.stall_five);
                        break;
                    case 100:
                        iv_stall_text.setImageResource(R.mipmap.stall_six);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                recent_progress = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //通便进行中...
                boolean isLaxativeAndWaterTemp = false;
                boolean isLaxativeAndWaterPressure = false;
                if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8) != 0 && tv_water_temp.isSelected()) {
                    isLaxativeAndWaterTemp = true;
                }
                if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8) != 0 && tv_water_pressure.isSelected()) {
                    isLaxativeAndWaterPressure = true;
                }
                if (isLaxativeAndWaterPressure) {
                    bar_progress = recent_progress;
                    T.showShort(getString(R.string.laxative_water_pressure_can_not_adjust));
                }

                if (isLaxativeAndWaterTemp) {
                    if (bar_progress > 80) {
                        bar_progress = 80;
                        T.showShort(getString(R.string.laxative_water_temp_max_five));
                    }
                }
                bar_progress = stallToProgress(bar_progress);
                mSeekBar.setProgress(bar_progress);
                mGradientProgressBar.setPercent(bar_progress);
                setStall(bar_progress);
            }
        });
    }

    /*将档位换算成百分比*/
    private int stallToProgress(int stall) {
        if (stall < 10 && stall >= 0) {
            stall = 0;
        } else if (stall < 30) {
            stall = 20;
        } else if (stall < 50) {
            stall = 40;
        } else if (stall < 70) {
            stall = 60;
        } else if (stall < 90) {
            stall = 80;
        } else if (stall <= 100) {
            stall = 100;
        }
        return stall;
    }

    /**
     * 根据seekBar百分比调节档位
     *
     * @param progress
     */
    private void setStall(int progress) {
        int stall = progress / 20;
        if (tv_cushion_temp.isSelected()) {
            cushion_temp_stall = stall;
            bcs.setRegister4HighValuebefore(1, Config.REGISTER_TOILET_CONFIG1, stall);
        } else if (tv_water_temp.isSelected()) {
            water_temp_stall = stall;
            bcs.setRegister4HighValue(1, Config.REGISTER_TOILET_CONFIG1, stall);
        } else if (tv_nozzle_position.isSelected()) {
            nozzle_position_stall = stall;
            bcs.setRegister4LowValuebefore(1, Config.REGISTER_TOILET_CONFIG1, stall);
        } else if (tv_dry_temp.isSelected()) {
            dry_temp_stall = stall;
            bcs.setRegister4LowValue(1, Config.REGISTER_TOILET_CONFIG1, stall);
        } else if (tv_water_pressure.isSelected()) {
            water_pressure_stall = stall;
            bcs.setRegister4HighValue(1, Config.REGISTER_TOILET_CONFIG2, stall);
        }
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_cushion_temp:
                    if (isCushionError) {
                        error(getString(R.string.cushion_temp));
                    } else {
                        clearStallState(v, cushion_temp_stall);
                    }
                    break;
                case R.id.tv_water_temp:
                    if (isWaterError) {
                        error(getString(R.string.water_temp));
                    } else {
                        clearStallState(v, water_temp_stall);
                    }
                    break;
                case R.id.tv_nozzle_position:
                    clearStallState(v, nozzle_position_stall);
                    break;
                case R.id.tv_dry_temp:
                    if (isDryError) {
                        error(getString(R.string.wind_temp));
                    } else {
                        clearStallState(v, dry_temp_stall);
                    }
                    break;
                case R.id.tv_water_pressure:
                    if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8) != 0) {
                        T.showShort(getString(R.string.laxative_water_pressure_can_not_adjust));
                    } else {
                        clearStallState(v, water_pressure_stall);
                    }
                    break;
                case R.id.tv_control_open_double:
                    bcs.setRegisterValue(1, 115, 1);
                    bcs.setRegister4LowValuebefore(1, Config.REGISTER_TOILET_CONTROL2, 1);
                    break;
                case R.id.tv_control_open_signal:
                    bcs.setRegisterValue(1, 115, 1);
                    bcs.setRegister4LowValuebefore(1, Config.REGISTER_TOILET_CONTROL2, 3);
                    break;
                case R.id.tv_control_close_flip:
                    bcs.setRegisterValue(1, 115, 1);
                    bcs.setRegister4LowValuebefore(1, Config.REGISTER_TOILET_CONTROL2, 2);
//                    showGuide();
                    break;
                case R.id.tv_control_hip_wash:
                    if (toilet_state == NO_SEAT_STATE) {
                        T.showShort("请着座");
                        return;
                    }
                    if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 9) == 1) {
                        bcs.setRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 0, 1);
                        showWaitDialog(false);
                    } else {
                        if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 10) == 1 || bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8) == 1)
                            showWaitDialog(true);
                        bcs.setRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 9, 1);
                    }
                    break;
                case R.id.tv_control_woman_wash:
                    if (toilet_state == NO_SEAT_STATE) {
                        T.showShort("请着座");
                        return;
                    }
                    if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 10) == 1) {
                        bcs.setRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 0, 1);
                        showWaitDialog(false);
                    } else {
                        if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 9) == 1 || bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8) == 1)
                            showWaitDialog(true);
                        bcs.setRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 10, 1);
                    }
                    break;
                case R.id.tv_control_laxative:
                    if (toilet_state == NO_SEAT_STATE) {
                        T.showShort("请着座");
                        return;
                    }
                    if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8) == 1) {
                        bcs.setRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 0, 1);
                        showWaitDialog(false);
                    } else {
                        new SweetAlertDialog(mContext, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                                .setCustomImage(R.mipmap.tip_icon)
                                .setTitleText(getString(R.string.pooai_tip))
                                .setContentText(getString(R.string.child_can_not_use))
                                .setCancelText(getString(R.string.cancel_use))
                                .setConfirmText(getString(R.string.confirm_use))
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                        if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 10) == 1 || bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 9) == 1)
                                            showWaitDialog(true);
                                        if (water_temp_stall > 3) {
                                            water_temp_stall = 3;
                                            bcs.setRegister4HighValue(1, Config.REGISTER_TOILET_CONFIG1, water_temp_stall);
                                            if (tv_water_temp.isSelected()) {
                                                mSeekBar.setProgress(40);
                                                mGradientProgressBar.setPercent(40);
                                            }
                                        }
                                        if (water_pressure_stall > 4) {
                                            water_pressure_stall = 4;
                                            bcs.setRegister4HighValue(1, Config.REGISTER_TOILET_CONFIG2, water_pressure_stall);
                                            if (tv_water_pressure.isSelected()) {
                                                mSeekBar.setProgress(60);
                                                mGradientProgressBar.setPercent(60);
                                            }
                                        }
                                        bcs.setRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8, 1);
                                    }
                                })
                                .show();

                    }
                    break;
                case R.id.tv_control_massage:
                    if (toilet_state == NO_SEAT_STATE) {
                        T.showShort("请着座");
                        return;
                    }
                    if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 15) == 1) {
                        bcs.setRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 15, 0);
                    } else {
                        bcs.setRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 15, 1);
                    }
                    break;
                case R.id.tv_control_dry:
                    if (toilet_state == NO_SEAT_STATE) {
                        T.showShort("请着座");
                        return;
                    }
                    if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 3) == 1) {
                        bcs.setRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 0, 1);
                    } else {
                        if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 9) == 1 || bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8) == 1 || bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 10) == 1)
                            showWaitDialog(false);
                        bcs.setRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 3, 1);
                    }
                    break;
                case R.id.tv_control_fumigation:
                    if (toilet_state == NO_SEAT_STATE) {
                        T.showShort("请着座");
                        return;
                    }
                    if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 2) == 1) {
                        bcs.setRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 0, 1);
                    } else {
                        if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 9) == 1 || bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8) == 1 || bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 10) == 1)
                            showWaitDialog(false);
                        bcs.setRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 2, 1);
                    }
                    break;
                case R.id.bt_stall_one:
                    if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8) != 0 && tv_water_pressure.isSelected()) {
                        T.showShort(getString(R.string.laxative_water_pressure_can_not_adjust));
                    } else {
                        mSeekBar.setProgress(0);
                        setStall(0);
                    }
                    break;
                case R.id.bt_stall_two:
                    if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8) != 0 && tv_water_pressure.isSelected()) {
                        T.showShort(getString(R.string.laxative_water_pressure_can_not_adjust));
                    } else {
                        mSeekBar.setProgress(20);
                        setStall(20);
                    }
                    break;
                case R.id.bt_stall_three:
                    if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8) != 0 && tv_water_pressure.isSelected()) {
                        T.showShort(getString(R.string.laxative_water_pressure_can_not_adjust));
                    } else {
                        mSeekBar.setProgress(40);
                        setStall(40);
                    }
                    break;
                case R.id.bt_stall_four:
                    if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8) != 0 && tv_water_pressure.isSelected()) {
                        T.showShort(getString(R.string.laxative_water_pressure_can_not_adjust));
                    } else {
                        mSeekBar.setProgress(60);
                        setStall(60);
                    }
                    break;
                case R.id.bt_stall_five:
                    if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8) != 0 && tv_water_pressure.isSelected()) {
                        T.showShort(getString(R.string.laxative_water_pressure_can_not_adjust));
                    } else {
                        mSeekBar.setProgress(80);
                        setStall(80);
                    }

                    break;
                case R.id.bt_stall_six:
                    if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8) != 0 && tv_water_pressure.isSelected()) {
                        T.showShort(getString(R.string.laxative_water_pressure_can_not_adjust));
                    } else if (bcs.getRegisterBitValue(1, Config.REGISTER_TOILET_CONTROL, 8) != 0 && tv_water_temp.isSelected()) {
                        T.showShort(getString(R.string.laxative_water_temp_max_five));
                    } else {
                        mSeekBar.setProgress(100);
                        setStall(100);
                    }
                    break;
            }
        }
    }

    private void error(String msg) {
        SweetAlertDialog dialog = new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.toilet_error))
                .setContentText(msg + getString(R.string.error_connect_pooai))
                .setConfirmText(getString(R.string.sure))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void clearStallState(View v, int stall) {
        tv_cushion_temp.setSelected(false);
        tv_water_temp.setSelected(false);
        tv_nozzle_position.setSelected(false);
        tv_dry_temp.setSelected(false);
        tv_water_pressure.setSelected(false);
        v.setSelected(true);
        mSeekBar.setProgress(20 * stall);
        mGradientProgressBar.setPercent(20 * stall);
    }

    private void stopAnimation(AnimationDrawable animationDrawable) {
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
            switch (toilet_state) {
                case SEAT_STATE:
                    iv_animation.setImageResource(R.drawable.seat_icon);
                    break;
                case NO_SEAT_STATE:
                    iv_animation.setImageResource(R.drawable.no_seat_icon);
                    break;
                case SLEEP_STATE:
                    iv_animation.setImageResource(R.drawable.sleep_icon);
                    break;
            }
            cleanAnimation();
        }
    }

    private void showWaitDialog(boolean isSwitch) {
        autoCleanDialog = new AutoCleanDialog(mContext);
        if (isSwitch) {
            autoCleanDialog.setImageResource(R.drawable.auto_clean_loading_short);
            mHandler.sendEmptyMessageDelayed(104, 7000);
        } else {
            autoCleanDialog.setImageResource(R.drawable.auto_clean_loading);
            mHandler.sendEmptyMessageDelayed(104, 13000);
        }
        autoCleanDialog.show();


    }

    private void showGuide() {
        final GuideHelper guideHelper = new GuideHelper(ControlActivity.this);
        TipData tipDataCustom1 = new TipData(R.mipmap.control_guide_one, Gravity.BOTTOM | Gravity.CENTER, frameLayout_toilet_state);
        TipData tipDataCustom2 = new TipData(R.mipmap.control_guide_two, Gravity.CENTER | Gravity.BOTTOM, ll_toilet_gear);
        TipData tipDataCustom3 = new TipData(R.mipmap.control_guide_three, Gravity.CENTER | Gravity.BOTTOM, ll_toilet_regulation);
        TipData tipDataCustom4 = new TipData(R.mipmap.control_guide_four, Gravity.CENTER | Gravity.TOP, ll_toilet_function);
        TipData tipDataCustom5 = new TipData(R.mipmap.control_guide_five, Gravity.CENTER | Gravity.TOP, tv_massage);
        tipDataCustom5.setLocation(100, 0);
        TipData tipDataCustom6 = new TipData(R.mipmap.control_guide_six, Gravity.LEFT | Gravity.TOP, tv_laxative);
        tipDataCustom6.setLocation(150, 0);
        guideHelper.setOnDismissListener(new GuideHelper.OnDialogDismissListener() {
            @Override
            public void dismiss() {
                ControlSave.save(mContext, "isControlGuide", "1");
            }
        });
        guideHelper.addPage(tipDataCustom1);
        guideHelper.addPage(tipDataCustom2);
        guideHelper.addPage(tipDataCustom3);
        guideHelper.addPage(tipDataCustom4);
        guideHelper.addPage(tipDataCustom5);
        guideHelper.addPage(tipDataCustom6);
        guideHelper.show(false);
    }
}
