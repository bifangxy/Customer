package com.xiaopu.customer.view;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.xiaopu.customer.R;

import java.util.List;

/**
 * Created by Administrator on 2017/9/14.
 */

public class CustomMarkerView extends MarkerView {

    private TextView tv_time;

    private TextView tv_data;

    private List<String> dataList;

    //0 步数 1 睡眠 2 心率 3 血压 4 血氧
    private int type;


    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tv_data = (TextView) findViewById(R.id.tv_date);
        tv_time = (TextView) findViewById(R.id.tv_time);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
//        tv_time.setText("" + e.getVal());
//        tv_data.setText("" + dataList.get(e.getXIndex()));
        switch (type) {
            case 0:
                tv_time.setText((int) e.getVal() + "步");
                break;
            case 1:
                int hour = (int) e.getVal() / 60;
                int minute = (int) e.getVal() % 60;
                tv_time.setText(hour + "小时" + minute + "分");
                break;
            case 2:
                tv_time.setText((int) e.getVal() + "次/分");
                break;
            case 3:
                tv_time.setText((int) e.getVal() + "mmhg");
                break;
            case 4:
                tv_time.setText((int) e.getVal() + "%");
                break;
        }
    }

    @Override
    public int getXOffset(float xpos) {
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        return -getHeight();
    }


    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
