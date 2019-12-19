package com.xiaopu.customer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.data.jsonresult.DetectionUrine;
import com.xiaopu.customer.data.jsonresult.DetectionUrineitem;
import com.xiaopu.customer.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class UrineDetialAdapter extends BaseAdapter {

    private List<DetectionUrineitem> mUriDetailList;
    private Context mContext;

    private ViewHolder viewHolder;

    private DetectionUrine mDetectionUrine;

    private int mStyle;

    public UrineDetialAdapter(DetectionUrine detectionUrine, Context context, int style) {
        mDetectionUrine = detectionUrine;
        mContext = context;
        mUriDetailList = new ArrayList<>();
        mStyle = style;
        setUrineDetail();
        /*mUriDetailList = uriDetailList;*/
    }

    @Override
    public int getCount() {
        return mUriDetailList.size();
    }

    @Override
    public DetectionUrineitem getItem(int i) {
        return mUriDetailList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_urine_detail, null);
            viewHolder = new ViewHolder();
            if(mStyle == 0){
                convertView.setMinimumHeight(130);
            }
            viewHolder.detectionName = (TextView) convertView.findViewById(R.id.urine_name);
            viewHolder.detectionResult = (TextView) convertView.findViewById(R.id.urine_result);
            viewHolder.detectionRealResult = (TextView) convertView.findViewById(R.id.urine_real_result);
            viewHolder.rlUrineItem = (RelativeLayout) convertView.findViewById(R.id.rl_urine_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        setItemBackground(position);
        Log.d("kwk",position+"啦啦啦");
        viewHolder.detectionName.setText(getItem(position).getDetectionName());
        viewHolder.detectionResult.setText(getItem(position).getDetectionResult());
        viewHolder.detectionRealResult.setText(getItem(position).getDetectionRealResult());
        return convertView;
    }

    private void setItemBackground(int position){
        Log.d("kwk", position+"position");
        switch (position){
            case 0:
                viewHolder.rlUrineItem.setBackgroundResource(R.mipmap.detection_urine_1);
                break;
            case 1:
                viewHolder.rlUrineItem.setBackgroundResource(R.mipmap.detection_urine_2);
                break;
            case 2:
                viewHolder.rlUrineItem.setBackgroundResource(R.mipmap.detection_urine_3);
                break;
            case 3:
                viewHolder.rlUrineItem.setBackgroundResource(R.mipmap.detection_urine_4);
                break;
            case 4:
                viewHolder.rlUrineItem.setBackgroundResource(R.mipmap.detection_urine_5);
                break;
            case 5:
                viewHolder.rlUrineItem.setBackgroundResource(R.mipmap.detection_urine_6);
                break;
            case 6:
                viewHolder.rlUrineItem.setBackgroundResource(R.mipmap.detection_urine_7);
                break;
            case 7:
                viewHolder.rlUrineItem.setBackgroundResource(R.mipmap.detection_urine_8);
                break;
            case 8:
                viewHolder.rlUrineItem.setBackgroundResource(R.mipmap.detection_urine_9);
                break;
            case 9:
                viewHolder.rlUrineItem.setBackgroundResource(R.mipmap.detection_urine_10);
                break;
            case 10:
                viewHolder.rlUrineItem.setBackgroundResource(R.mipmap.detection_urine_11);
                break;
        }
    }

    /** 尿检数据详情 */
    private void setUrineDetail(){
        DetectionUrineitem detectionUrineitem1 = new DetectionUrineitem();
        detectionUrineitem1.setDetectionName("白细胞");
        detectionUrineitem1.setDetectionResult(mDetectionUrine.getLeu());
        detectionUrineitem1.setDetectionRealResult(mDetectionUrine.getLeuResult());
        mUriDetailList.add(detectionUrineitem1);

        DetectionUrineitem detectionUrineitem2 = new DetectionUrineitem();
        detectionUrineitem2.setDetectionName("尿胆原");
        detectionUrineitem2.setDetectionResult(mDetectionUrine.getUbg());
        detectionUrineitem2.setDetectionRealResult(mDetectionUrine.getUbgResult());
        mUriDetailList.add(detectionUrineitem2);

        DetectionUrineitem detectionUrineitem3 = new DetectionUrineitem();
        detectionUrineitem3.setDetectionName("PH");
        detectionUrineitem3.setDetectionResult(mDetectionUrine.getPh());
        detectionUrineitem3.setDetectionRealResult(mDetectionUrine.getPhResult());
        mUriDetailList.add(detectionUrineitem3);

        DetectionUrineitem detectionUrineitem4 = new DetectionUrineitem();
        detectionUrineitem4.setDetectionName("比重");
        detectionUrineitem4.setDetectionResult(mDetectionUrine.getSg());
        detectionUrineitem4.setDetectionRealResult(mDetectionUrine.getSgResult());
        mUriDetailList.add(detectionUrineitem4);

        DetectionUrineitem detectionUrineitem5 = new DetectionUrineitem();
        detectionUrineitem5.setDetectionName("胆红素");
        detectionUrineitem5.setDetectionResult(mDetectionUrine.getBil());
        detectionUrineitem5.setDetectionRealResult(mDetectionUrine.getBilResult());
        mUriDetailList.add(detectionUrineitem5);

        DetectionUrineitem detectionUrineitem6 = new DetectionUrineitem();
        detectionUrineitem6.setDetectionName("维生素");
        detectionUrineitem6.setDetectionResult(mDetectionUrine.getVc());
        detectionUrineitem6.setDetectionRealResult(mDetectionUrine.getVcResult());
        mUriDetailList.add(detectionUrineitem6);

        DetectionUrineitem detectionUrineitem7 = new DetectionUrineitem();
        detectionUrineitem7.setDetectionName("亚硝酸盐");
        detectionUrineitem7.setDetectionResult(mDetectionUrine.getNit());
        detectionUrineitem7.setDetectionRealResult(mDetectionUrine.getNitResult());
        mUriDetailList.add(detectionUrineitem7);

        DetectionUrineitem detectionUrineitem8 = new DetectionUrineitem();
        detectionUrineitem8.setDetectionName("蛋白质");
        detectionUrineitem8.setDetectionResult(mDetectionUrine.getPro());
        detectionUrineitem8.setDetectionRealResult(mDetectionUrine.getProResult());
        mUriDetailList.add(detectionUrineitem8);

        DetectionUrineitem detectionUrineitem9 = new DetectionUrineitem();
        detectionUrineitem9.setDetectionName("潜血");
        detectionUrineitem9.setDetectionResult(mDetectionUrine.getBld());
        detectionUrineitem9.setDetectionRealResult(mDetectionUrine.getBilResult());
        mUriDetailList.add(detectionUrineitem9);

        DetectionUrineitem detectionUrineitem10 = new DetectionUrineitem();
        detectionUrineitem10.setDetectionName("酮体");
        detectionUrineitem10.setDetectionResult(mDetectionUrine.getKet());
        detectionUrineitem10.setDetectionRealResult(mDetectionUrine.getKetResult());
        mUriDetailList.add(detectionUrineitem10);

        DetectionUrineitem detectionUrineitem11 = new DetectionUrineitem();
        detectionUrineitem11.setDetectionName("葡萄糖");
        detectionUrineitem11.setDetectionResult(mDetectionUrine.getGlu());
        detectionUrineitem11.setDetectionRealResult(mDetectionUrine.getGluResult());
        mUriDetailList.add(detectionUrineitem11);
        Log.d("kwk", mUriDetailList.size()+"添加最后");

    }


    protected class ViewHolder {
        private TextView detectionName;
        private TextView detectionResult;
        private TextView detectionRealResult;
        private RelativeLayout rlUrineItem;
    }
}
