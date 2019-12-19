package com.xiaopu.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.jsonresult.Department;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.view.SquareLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class SquareAdpaterTwo extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Department> departmentList = new ArrayList<Department>();

    public SquareAdpaterTwo(List<Department> list, Context context) {
        super();
        inflater = LayoutInflater.from(context);
        departmentList.clear();
        departmentList = list;
    }

    @Override
    public int getCount() {
        if (null != departmentList) {
            return departmentList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return departmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.main02_itemtwo, null);
            viewHolder = new ViewHolder();
            viewHolder.squareLayout = (SquareLayout) convertView.findViewById(R.id.squarelayout);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + departmentList.get(position).getImage(), viewHolder.image, ApplicationXpClient.getmOptions(R.mipmap.user_accountpic));
        return convertView;
    }

    protected class ViewHolder {
        private SquareLayout squareLayout;
        private ImageView image;
    }

}
