package com.xiaopu.customer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.activity.MyDoctorActivity;
import com.xiaopu.customer.activity.MyInformationCollectActivity;
import com.xiaopu.customer.activity.MyAccountActivity;
import com.xiaopu.customer.activity.MallContentActivity;
import com.xiaopu.customer.MySettingsActivity;
import com.xiaopu.customer.activity.MyDeviceActivity;
import com.xiaopu.customer.activity.MyWalletActivity;
import com.xiaopu.customer.R;
import com.xiaopu.customer.utils.http.HttpConstant;

/**
 * Created by Administrator on 2016/7/7 0007.
 */
public class UserCenterFragment extends Fragment {
    private static final String LOG_TAG = UserCenterFragment.class.getSimpleName();

    private Context mContext;

    private View view;

    private ImageView iv_user_head;

    private LinearLayout ll_my_account;

    private LinearLayout ll_my_wallet;

    private LinearLayout ll_my_order;

    private LinearLayout ll_my_collection;

    private LinearLayout ll_my_doctor;

    private LinearLayout ll_my_address;

    private LinearLayout ll_my_device;

    private LinearLayout ll_about_xiaopu;

    private TextView tv_title;

    private TextView tvNickName;

    private TextView tvUserId;

    private TextView tvSex;

    private MyClickListener mClick;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        initData();
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_center, container, false);
        return view;
    }

    /**
     * activity的oncreate方法执行完之后（可以在这拿取其他fragment中控件并操作）
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        ll_my_account.setOnClickListener(mClick);
        ll_my_wallet.setOnClickListener(mClick);
        ll_my_order.setOnClickListener(mClick);
        ll_my_collection.setOnClickListener(mClick);
        ll_my_doctor.setOnClickListener(mClick);
        ll_my_address.setOnClickListener(mClick);
        ll_my_device.setOnClickListener(mClick);
        ll_about_xiaopu.setOnClickListener(mClick);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            tvNickName.setText(ApplicationXpClient.userInfoResult.getNickname());
            ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + ApplicationXpClient.userInfoResult.getAvatar(), iv_user_head, ApplicationXpClient.options);
        }
    }


    private void initData() {
        mClick = new MyClickListener();

        if (ApplicationXpClient.userInfoResult.getSex() != null) {
            tvSex.setText(initSex(ApplicationXpClient.userInfoResult.getSex()));
        } else {
            tvSex.setText("");
        }
        tvNickName.setText(ApplicationXpClient.userInfoResult.getNickname());

        tvUserId.setText(getString(R.string.phone) + ApplicationXpClient.userInfoResult.getPhone());

        ImageLoader.getInstance().displayImage(HttpConstant.Url_ImageServer + ApplicationXpClient.userInfoResult.getAvatar(), iv_user_head, ApplicationXpClient.options);
    }

    private void initView() {
        ll_my_account = (LinearLayout) view.findViewById(R.id.ll_my_account);

        ll_my_wallet = (LinearLayout) view.findViewById(R.id.ll_my_wallet);

        ll_my_order = (LinearLayout) view.findViewById(R.id.ll_my_order);

        ll_my_collection = (LinearLayout) view.findViewById(R.id.ll_my_collection);

        ll_my_doctor = view.findViewById(R.id.ll_my_doctor);

        ll_my_address = (LinearLayout) view.findViewById(R.id.ll_my_address);

        ll_my_device = (LinearLayout) view.findViewById(R.id.ll_my_device);

        ll_about_xiaopu = (LinearLayout) view.findViewById(R.id.ll_about_xiaopu);

        iv_user_head = (ImageView) view.findViewById(R.id.iv_user_head);

        tvNickName = (TextView) view.findViewById(R.id.nickname);

        tvUserId = (TextView) view.findViewById(R.id.user_id);

        tvSex = (TextView) view.findViewById(R.id.sex);
    }

    /**
     * 判断性别
     */
    public String initSex(int status) {
        switch (status) {
            case 0:
                return getString(R.string.woman);
            case 1:
                return getString(R.string.man);
        }
        return "";
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_my_account:
                    startActivityForResult(new Intent(getActivity(), MyAccountActivity.class), 0);
                    break;
                case R.id.ll_my_wallet:
                    startActivity(new Intent(getActivity(), MyWalletActivity.class));
                    break;
                case R.id.ll_my_order:
                    Intent mIntent = new Intent(new Intent(getActivity(), MallContentActivity.class));
                    mIntent.putExtra("type", 5);
                    startActivity(mIntent);
                    break;
                case R.id.ll_my_collection:
                    startActivity(new Intent(getActivity(), MyInformationCollectActivity.class));
                    break;
                case R.id.ll_my_doctor:
                    startActivity(new Intent(getActivity(), MyDoctorActivity.class));
                    break;
                case R.id.ll_my_address:
                    Intent mIntent2 = new Intent(new Intent(getActivity(), MallContentActivity.class));
                    mIntent2.putExtra("type", 4);
                    startActivity(mIntent2);
                    break;
                case R.id.ll_my_device:
                    startActivity(new Intent(mContext, MyDeviceActivity.class));
                    break;
                case R.id.ll_about_xiaopu:
                    startActivity(new Intent(getActivity(), MySettingsActivity.class));
                    break;
            }
        }
    }
}
