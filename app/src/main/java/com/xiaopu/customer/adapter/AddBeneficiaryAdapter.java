package com.xiaopu.customer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.jsonresult.UserAccount;
import com.xiaopu.customer.data.jsonresult.UserInfoResult;
import com.xiaopu.customer.utils.DateUtils;
import com.xiaopu.customer.utils.RoundImageView;
import com.xiaopu.customer.utils.http.HttpConstant;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/24.
 */

public class AddBeneficiaryAdapter extends BaseAdapter {
    private LayoutInflater mInflater;// 动态布局映射

    private Context mContext;

    private List<UserInfoResult> userInfoResultList;

    private OnDeleteClickListener onDeleteClickListener;

    public AddBeneficiaryAdapter(Context context, List<UserInfoResult> userInfoResultList) {
        this.mContext = context;
        this.userInfoResultList = userInfoResultList;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @Override
    public int getCount() {
        return userInfoResultList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_user_info, viewGroup, false);
            viewHolder.riv_beneficiary_avatar = view.findViewById(R.id.riv_beneficiary_avatar);
            viewHolder.tv_beneficiary_name = view.findViewById(R.id.tv_beneficiary_name);
            viewHolder.iv_delete = view.findViewById(R.id.iv_delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        UserInfoResult userInfoResult = userInfoResultList.get(position);
        if (position == 0) {
            viewHolder.iv_delete.setVisibility(View.GONE);
        } else {
            viewHolder.iv_delete.setVisibility(View.VISIBLE);
        }
        ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + userInfoResult.getAvatar(), viewHolder.riv_beneficiary_avatar, ApplicationXpClient.getmOptions(R.mipmap.user_accountpic));
        viewHolder.tv_beneficiary_name.setText(userInfoResult.getNickname());

        viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClickListener.delete(position);
            }
        });

        return view;
    }

    private class ViewHolder {
        private RoundImageView riv_beneficiary_avatar;
        private TextView tv_beneficiary_name;
        private ImageView iv_delete;
    }

    public interface OnDeleteClickListener {
        void delete(int position);
    }

}
