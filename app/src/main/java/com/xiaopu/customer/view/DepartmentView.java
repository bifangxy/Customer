package com.xiaopu.customer.view;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import com.xiaopu.customer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义科室下拉列表
 * Created by Xieying on 2018/3/12.
 */

public class DepartmentView extends PopupWindow implements View.OnClickListener {

    private View rootView;

    private Activity mContext;

    private ListView listView_item;

    private Button bt_all_department;

    private Button bt_medical_department;

    private Button bt_surgical_department;

    private Button bt_gynecology_department;

    private Button bt_pediatrics;

    private String data[];

    private String strs[][] = {{"全部内科", "心血管内科", "胃肠科", "肾内科", "心理科"}, {"全部外科", "泌尿外科", "肛肠科", "肠胃科"}, {"全部妇科", "妇科病", "妇产科"}, {"全部儿科"}

    };

    private ArrayAdapter<String> mAdapter;

    private OnItemClickListener mListener;

    private int parent_position = 1;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public DepartmentView(Activity context, int position) {
        super(context);
        mContext = context;
        parent_position = position;
        initPopupView();
    }

    private void initPopupView() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        rootView = mInflater.inflate(R.layout.department_item, null);

        this.setContentView(rootView);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);

        bt_all_department = (Button) rootView.findViewById(R.id.bt_all_department);
        bt_medical_department = (Button) rootView.findViewById(R.id.bt_medical_department);
        bt_surgical_department = (Button) rootView.findViewById(R.id.bt_surgical_department);
        bt_gynecology_department = (Button) rootView.findViewById(R.id.bt_gynecology_department);
        bt_pediatrics = (Button) rootView.findViewById(R.id.bt_pediatrics);

        listView_item = (ListView) rootView.findViewById(R.id.lv_department_item);

        switch (parent_position) {
            case 0:
                bt_all_department.setSelected(true);
                break;
            case 1:
                bt_medical_department.setSelected(true);
                break;
            case 2:
                bt_surgical_department.setSelected(true);
                break;
            case 3:
                bt_gynecology_department.setSelected(true);
                break;
            case 4:
                bt_pediatrics.setSelected(true);
                break;
        }
        if (parent_position == 0) {
            data = new String[]{};
        } else {
            data = strs[parent_position - 1];
        }
        mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, data);
        listView_item.setAdapter(mAdapter);

        bt_all_department.setOnClickListener(this);
        bt_medical_department.setOnClickListener(this);
        bt_surgical_department.setOnClickListener(this);
        bt_gynecology_department.setOnClickListener(this);
        bt_pediatrics.setOnClickListener(this);

        listView_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onItemClick(parent_position, position, strs[parent_position - 1][position]);
                dismiss();
            }
        });


    }

    @Override
    public void onClick(View v) {
        resetSelectorView(v);
        switch (v.getId()) {
            case R.id.bt_all_department:
                parent_position = 0;
                data = new String[]{};
                mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, data);
                listView_item.setAdapter(mAdapter);
                mListener.onItemClick(0, 0, mContext.getString(R.string.all_department));
                dismiss();
                break;
            case R.id.bt_medical_department:
                parent_position = 1;
                data = strs[0];
                mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, data);
                listView_item.setAdapter(mAdapter);
                break;
            case R.id.bt_surgical_department:
                parent_position = 2;
                data = strs[1];
                mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, data);
                listView_item.setAdapter(mAdapter);
                break;
            case R.id.bt_gynecology_department:
                parent_position = 3;
                data = strs[2];
                mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, data);
                listView_item.setAdapter(mAdapter);
                break;
            case R.id.bt_pediatrics:
                parent_position = 4;
                data = strs[3];
                mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, data);
                listView_item.setAdapter(mAdapter);
                break;
        }
    }

    private void resetSelectorView(View v) {
        bt_all_department.setSelected(false);
        bt_gynecology_department.setSelected(false);
        bt_medical_department.setSelected(false);
        bt_pediatrics.setSelected(false);
        bt_surgical_department.setSelected(false);
        v.setSelected(true);
    }

    public interface OnItemClickListener {
        void onItemClick(int parentId, int childId, String name);
    }

}
