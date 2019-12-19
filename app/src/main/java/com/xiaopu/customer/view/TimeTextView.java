package com.xiaopu.customer.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/7 0007.
 */
public class TimeTextView extends android.support.v7.widget.AppCompatTextView {

    private static final String TIME = "time";

    private static final String CTIME = "ctime";

    private String text_after;

    private String text_before;

    private long time_length;

    private long length;

    Map<String, Long> map = new HashMap<String, Long>();

    public TimeTextView(Context context) {
        super(context);
        initData();
    }


    public TimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public TimeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:
                    setText(time_length / 1000 + text_after);
                    time_length -= 1000;
                    if (time_length < 0) {
                        setText(text_before);
                        setClickable(true);
                    } else {
                        setClickable(false);
                        mHandler.sendEmptyMessageDelayed(0x01, 1000);
                    }
                    break;
            }
        }
    };

    private void initData() {
        length = 60000;
        text_after = "s后重发";
        text_before = "重新发送";
    }


    /**
     * 和activity的onDestroy()方法同步
     */

    public void onDestroy() {
        time_length = 0;
    }

   /* */

    /**
     * 和activity的onCreate()方法同步
     *//*
    public void onCreate() {
        if (ApplicationXPMedical.map == null)
            return;
        if (ApplicationXPMedical.map.size() <= 0)// 这里表示没有上次未完成的计时
            return;
        long time = System.currentTimeMillis() - ApplicationXPMedical.map.get(CTIME) - ApplicationXPMedical.map.get(TIME);
        ApplicationXPMedical.map.clear();
        if (time > 0)
            return;
        else {
            this.time_length = Math.abs(time);
            this.setText(time + text_after);
            this.setEnabled(false);
        }
    }
*/
    public String getText_after() {
        return text_after;
    }

    public void setText_after(String text_after) {
        this.text_after = text_after;
    }

    public String getText_before() {
        return text_before;
    }

    public void setText_before(String text_before) {
        this.text_before = text_before;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setStart() {
        time_length = length;
        mHandler.sendEmptyMessage(0x01);
    }


}
