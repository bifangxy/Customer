package com.xiaopu.customer.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.GroupAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/8/30 0030.
 */
public class KComboBox {
    private LayoutInflater layoutInflater;
    private View popupview;
    private ListView lv_group;
    private PopupWindow mPopupWindow;
    private Context mContext;
    private View mView;
    private List<String> mList;
    private MyItemClickListener myItemClickListener = null;

    public KComboBox(Context context, View view, List<String> list) {
        mContext = context;
        mView = view;
        mList = list;
        initView();
        initData();
    }

    public void setLintener(MyItemClickListener itemClickListener){
        myItemClickListener = itemClickListener;
    }

    private void initView() {
        layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        popupview = layoutInflater.inflate(R.layout.group_list, null);
        lv_group = (ListView) popupview.findViewById(R.id.lvGroup);
    }
    private void initData(){
        GroupAdapter groupAdapter = new GroupAdapter(mContext, mList);
        lv_group.setAdapter(groupAdapter);
        // 创建一个PopuWidow对象
        mPopupWindow = new PopupWindow(popupview, 150, 600);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAsDropDown(mView, 0, 5);
        lv_group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(myItemClickListener != null){
                    myItemClickListener.mItemClick(view, position, mList);
                    mPopupWindow.dismiss();
                }
            }
        });
    }

    public interface MyItemClickListener{
        public void mItemClick(View v, int position, List<String> list);
    }
}
