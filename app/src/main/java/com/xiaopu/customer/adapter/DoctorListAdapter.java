package com.xiaopu.customer.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.jsonresult.SimpleDoctorResult;
import com.xiaopu.customer.utils.PixelUtil;
import com.xiaopu.customer.utils.StarLeverUtil;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.security.Des;

import java.util.List;

/**
 * Created by Administrator on 2017/5/27.
 */

public class DoctorListAdapter extends BaseAdapter {
    private Context mContext;

    private List<SimpleDoctorResult> dataList;

    private LayoutInflater mInflater;// 动态布局映射

    private int type = 0;

    private Drawable drawable_serviced;

    private Drawable drawable_attentioned;

    private Resources resource;

    private String[][] subsidiary_department = {{"心血管内科", "胃肠科", "肾内科", "心理科"}, {"泌尿外科", "肛肠科", "肠胃科"}, {"妇科病", "妇产科"}, {"儿科"}

    };

    public DoctorListAdapter(Context context, List<SimpleDoctorResult> simpleDoctorResults) {
        this.mContext = context;
        this.dataList = simpleDoctorResults;
        mInflater = LayoutInflater.from(mContext);
    }

    public DoctorListAdapter(Context context, List<SimpleDoctorResult> simpleDoctorResults, int type) {
        this.mContext = context;
        this.dataList = simpleDoctorResults;
        mInflater = LayoutInflater.from(mContext);
        this.type = type;
        resource = mContext.getResources();
        drawable_serviced = resource.getDrawable(R.mipmap.icon_serviced);
        drawable_serviced.setBounds(0, 0, (int) (drawable_serviced.getMinimumWidth()), (int) (drawable_serviced.getMinimumHeight()));

        drawable_attentioned = resource.getDrawable(R.mipmap.icon_attentioned);
        drawable_attentioned.setBounds(0, 0, (int) (drawable_attentioned.getMinimumWidth()), (int) (drawable_attentioned.getMinimumHeight()));
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public SimpleDoctorResult getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            if (type == 0) {
                view = mInflater.inflate(R.layout.mydoctor_item, viewGroup, false);
                viewHolder.tv_doctor_goodAt = view.findViewById(R.id.tv_doctor_goodAt);
                viewHolder.tv_doctor_comment = view.findViewById(R.id.tv_doctor_comment);
                viewHolder.tv_doctor_price = view.findViewById(R.id.tv_doctor_price);
            } else if (type == 1) {
                view = mInflater.inflate(R.layout.item_doctor_info, viewGroup, false);
                viewHolder.ll_doctor_tag = view.findViewById(R.id.ll_doctor_tag);
                viewHolder.tv_type = view.findViewById(R.id.tv_type);
            }
            viewHolder.iv_doctor_avatar = view.findViewById(R.id.iv_doctor_avatar);
            viewHolder.tv_doctor_name = view.findViewById(R.id.tv_doctor_name);
            viewHolder.tv_doctor_department = view.findViewById(R.id.tv_doctor_department);
            viewHolder.tv_doctor_title = view.findViewById(R.id.tv_doctor_title);
            viewHolder.tv_doctor_hospital = view.findViewById(R.id.tv_doctor_hospital);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        SimpleDoctorResult simpleDoctorResult = dataList.get(i);
        ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + simpleDoctorResult.getAvatar(), viewHolder.iv_doctor_avatar, ApplicationXpClient.options);
        viewHolder.tv_doctor_name.setText(simpleDoctorResult.getRealname());
        switch (simpleDoctorResult.getTitle()) {
            case 0:
                viewHolder.tv_doctor_title.setText("主任医师");
                break;
            case 1:
                viewHolder.tv_doctor_title.setText("副主任医师");
                break;
            case 2:
                viewHolder.tv_doctor_title.setText("主治医师");
                break;
            case 3:
                viewHolder.tv_doctor_title.setText("住院医师");
                break;
        }
        if (simpleDoctorResult.getPersentDepartmentId() < 4 && simpleDoctorResult.getDepartmentId() < subsidiary_department[simpleDoctorResult.getPersentDepartmentId()].length)
            viewHolder.tv_doctor_department.setText(subsidiary_department[simpleDoctorResult.getPersentDepartmentId() - 1][simpleDoctorResult.getDepartmentId() - 1]);
        viewHolder.tv_doctor_hospital.setText(simpleDoctorResult.getHospital());

        if (type == 0) {
            try {
                viewHolder.tv_doctor_goodAt.setText(Des.decode(simpleDoctorResult.getGoodAt()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (simpleDoctorResult.isFree()) {
                viewHolder.tv_doctor_price.setText(String.valueOf(simpleDoctorResult.getClinicPrice()) + "小普币起");
            } else {
                viewHolder.tv_doctor_price.setText(String.valueOf(simpleDoctorResult.getOldPrice()) + "小普币起");
            }
            viewHolder.tv_doctor_comment.setText("好评率  " + simpleDoctorResult.getCommentRate() * 100 + "%");
        } else if (type == 1) {
            viewHolder.ll_doctor_tag.removeAllViews();
            String tag = simpleDoctorResult.getTargets();
            if (tag != null) {
                String[] tags = tag.split(",");
                for (String tag1 : tags) {
                    TextView textView = new TextView(mContext);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(PixelUtil.dp2px(mContext, 50), PixelUtil.dp2px(mContext, 20));
                    lp.setMargins(0, 0, 20, 0);
                    textView.setLayoutParams(lp);
                    textView.setBackgroundColor(mContext.getResources().getColor(R.color.accent));
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextColor(mContext.getResources().getColor(R.color.white));
                    textView.setText(tag1);
                    textView.setTextSize(10);
                    viewHolder.ll_doctor_tag.addView(textView);
                }
            }
            if (simpleDoctorResult.isServed()) {
                viewHolder.tv_type.setCompoundDrawables(null, drawable_serviced, null, null);
                viewHolder.tv_type.setText("咨询过");
                viewHolder.tv_type.setTextColor(resource.getColor(R.color.accent));
            } else {
                viewHolder.tv_type.setCompoundDrawables(null, drawable_attentioned, null, null);
                viewHolder.tv_type.setText("关注过");
                viewHolder.tv_type.setTextColor(resource.getColor(R.color.orange_light));
            }

        }


        return view;
    }

    private class ViewHolder {
        ImageView iv_doctor_avatar;
        TextView tv_doctor_name;
        TextView tv_doctor_department;
        TextView tv_doctor_title;
        TextView tv_doctor_hospital;
        TextView tv_doctor_goodAt;
        TextView tv_doctor_comment;
        TextView tv_doctor_price;
        LinearLayout ll_doctor_tag;
        TextView tv_type;

    }
}




