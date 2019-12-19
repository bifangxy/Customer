package com.xiaopu.customer.adapter;

/**
 * Created by Administrator on 2016/9/5 0005.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaopu.customer.NearHospitalActivity;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.jsonresult.Hospital;
import com.xiaopu.customer.utils.RoundImageView;
import com.xiaopu.customer.utils.http.HttpUtils;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class NearHospitalAdapter extends BaseAdapter {

    private List<Hospital> mList;
    private ViewHolder viewHolder;
    private NearHospitalActivity mActivity;
    private double mStartLongitude = 0;
    private double mStartLatitude = 0;

    public NearHospitalAdapter(List<Hospital> list, NearHospitalActivity activity) {
        mList = list;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Hospital getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_near_hospital, null);
            viewHolder = new ViewHolder();
            viewHolder.hospitalName = (TextView) convertView.findViewById(R.id.hospital_name);
            viewHolder.hospitalAddress = (TextView) convertView.findViewById(R.id.address);
            viewHolder.hospitalLevel = (TextView) convertView.findViewById(R.id.level);
            viewHolder.hospitalType = (TextView) convertView.findViewById(R.id.type);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
            viewHolder.ivHeader = (RoundImageView) convertView.findViewById(R.id.hospital_header);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ivHeader.setImageResource(R.mipmap.hospital_default);
        /*ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + getItem(position).get, viewHolder.ivHeader, ApplicationXpClient.getmOptions(R.mipmap.hospital_default));*/
        viewHolder.hospitalName.setText(getItem(position).getName());
        viewHolder.hospitalAddress.setText(getItem(position).getAddress());

        viewHolder.distance.setText(getDistance(position)+"km");

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.itemListener(position);
            }
        });

        return convertView;
    }

    public void setPoint(double longtitude, double latitude){
        mStartLongitude = longtitude;
        mStartLatitude = latitude;
        notifyDataSetChanged();
    }

    /** 获取两地间的距离 */
    private String getDistance(int position){
        double lat1 = (Math.PI/180) * mStartLatitude;
        double lat2 = (Math.PI/180) * getItem(position).getLatitude();
        double lon1 = (Math.PI/180) * mStartLongitude;
        double lon2 = (Math.PI/180) * getItem(position).getLongitude();
        //地球半径
        double R = 6371;

        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double distance =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(distance);
    }


    private String initHospitalLevel(int level){
        switch (level){
            case 0:
                return "一甲医院";
            case 1:
                return "二甲医院";
            case 2:
                return "三甲医院";
            case 3:
                return "卫生所";
        }
        return "";
    }


    protected class ViewHolder {
        private TextView hospitalName;
        private TextView hospitalAddress;
        private TextView hospitalLevel;
        private TextView hospitalType;
        private TextView distance;
        private RoundImageView ivHeader;
    }
}
