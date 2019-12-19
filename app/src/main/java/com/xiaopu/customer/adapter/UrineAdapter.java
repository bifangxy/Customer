package com.xiaopu.customer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaopu.customer.R;
import com.xiaopu.customer.UrineDetailActivity;
import com.xiaopu.customer.data.Detection_Urine_item;
import com.xiaopu.customer.data.jsonresult.DetectionUrine;
import com.xiaopu.customer.utils.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class UrineAdapter extends BaseAdapter {

    private List<DetectionUrine> mList;

    public static HashMap<Integer, Boolean> isVisition;

    private Activity mContext;

    private ViewHolder viewHolder;

    private UrineResultAdapter mAdapter;

    private String[] urine_names = new String[]{"白细胞", "亚硝酸盐", "尿胆原", "蛋白质", "PH", "潜血", "比重", "酮体", "胆红素", "葡萄糖", "维生素"

    };
    private List<Detection_Urine_item> detection_urine_items;

    private Detection_Urine_item detection_urine_item;

    public UrineAdapter(List<DetectionUrine> list, Activity context) {
        mList = list;
        mContext = context;
        isVisition = new HashMap<>();
        initData();
    }

    private void initData() {
        for (int i = 0; i < mList.size(); i++) {
            isVisition.put(i, false);
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public DetectionUrine getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_uri_detection, null);
            viewHolder = new ViewHolder();
            viewHolder.recentData = (TextView) convertView.findViewById(R.id.recent_urine_data);
            viewHolder.uriDetail = (ImageView) convertView.findViewById(R.id.uri_detial);
            viewHolder.uriDetailList = (ListView) convertView.findViewById(R.id.uri_list);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.recentData.setText(TimeUtils.DateToStringDatail(getItem(position).getCreateTime()));

        viewHolder.uriDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVisition.get(position)) {
                    isVisition.put(position, false);
                    notifyDataSetChanged();
                } else {
                    isVisition.put(position, true);
                    notifyDataSetChanged();
                }
            }
        });
        if (isVisition.get(position)) {
            viewHolder.uriDetailList.setVisibility(View.VISIBLE);
            viewHolder.uriDetail.setImageResource(R.mipmap.up_stop);
            initUrineData(mList.get(position));
        } else {
            viewHolder.uriDetailList.setVisibility(View.GONE);
            viewHolder.uriDetail.setImageResource(R.mipmap.down_more);
        }

        viewHolder.uriDetailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent mIntent = new Intent(mContext, UrineDetailActivity.class);
                mIntent.putExtra("urine_id", i + 1);
                mContext.startActivity(mIntent);
            }
        });

        return convertView;
    }

    protected class ViewHolder {
        /*private TextView detectionName;*/
        private TextView recentData;
        private ImageView uriDetail;
        private ListView uriDetailList;
    }


    private void initUrineData(DetectionUrine mDetectionUrine) {

        detection_urine_items = new ArrayList<>();

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[0]);
        detection_urine_item.setDetection_result(mDetectionUrine.getLeu());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getLeuResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[1]);
        detection_urine_item.setDetection_result(mDetectionUrine.getNit());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getNitResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[2]);
        detection_urine_item.setDetection_result(mDetectionUrine.getUbg());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getUbgResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[3]);
        detection_urine_item.setDetection_result(mDetectionUrine.getPro());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getProResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[4]);
        detection_urine_item.setDetection_result(mDetectionUrine.getPh());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getPhResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[5]);
        detection_urine_item.setDetection_result(mDetectionUrine.getBld());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getBldResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[6]);
        detection_urine_item.setDetection_result(mDetectionUrine.getSg());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getSgResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[7]);
        detection_urine_item.setDetection_result(mDetectionUrine.getKet());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getKetResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[8]);
        detection_urine_item.setDetection_result(mDetectionUrine.getBil());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getBilResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[9]);
        detection_urine_item.setDetection_result(mDetectionUrine.getGlu());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getGluResult());
        detection_urine_items.add(detection_urine_item);

        detection_urine_item = new Detection_Urine_item();
        detection_urine_item.setDetection_name(urine_names[10]);
        detection_urine_item.setDetection_result(mDetectionUrine.getVc());
        detection_urine_item.setDetection_real_result(mDetectionUrine.getVcResult());
        detection_urine_items.add(detection_urine_item);

        mAdapter = new UrineResultAdapter(mContext, detection_urine_items);
        viewHolder.uriDetailList.setAdapter(mAdapter);

    }
}
