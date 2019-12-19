package com.xiaopu.customer.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.reflect.TypeToken;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.BleDeviceAdapter;
import com.xiaopu.customer.data.EntityDevice;
import com.xiaopu.customer.dialog.EditDialog;
import com.xiaopu.customer.utils.ControlSave;
import com.xiaopu.customer.utils.T;
import com.xiaopu.customer.utils.bluetooth.BluetoothController;
import com.xiaopu.customer.utils.bluetooth.ConstantUtils;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.view.sweetAlertDialog.SweetAlertDialog;
import com.xiaopu.customer.view.swipeMenuListView.SwipeMenu;
import com.xiaopu.customer.view.swipeMenuListView.SwipeMenuCreator;
import com.xiaopu.customer.view.swipeMenuListView.SwipeMenuItem;
import com.xiaopu.customer.view.swipeMenuListView.SwipeMenuListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by Administrator on 2017/6/20.
 */

public class MyDeviceActivity extends ActivityBase {
    private static final String LOG_TAG = MyDeviceActivity.class.getSimpleName();

    private Context mContext;

    private SwipeMenuListView mSwipeMenuListView;

    private Button bt_add_device;

    private List<EntityDevice> deviceList;

    private BleDeviceAdapter mAdapter;

    private MyClickListener mClick;

    private MyReceiver myReceiver;

    private BluetoothController mController;

    private SweetAlertDialog mSweetAlertDialog;

    private boolean isNewDevice;

    private List<EntityDevice> newEntityList;

    private int select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_device);
        initActionBar(getString(R.string.mydevice));
        mContext = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mSwipeMenuListView = (SwipeMenuListView) findViewById(R.id.lv_devices);
        bt_add_device = (Button) findViewById(R.id.bt_add_device);
        setResult(2);
    }

    private void initData() {
        ApplicationXpClient.getInstance().setInMydevices(true);
        mController = BluetoothController.getInstance();
        newEntityList = new ArrayList<>();
        deviceList = new ArrayList<>();
        initRecentDevice();
        initSwipMenuListView();
        mAdapter = new BleDeviceAdapter(mContext, deviceList);
        mSwipeMenuListView.setAdapter(mAdapter);
        mController.initBLE();
        mSweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        registerBroadcast();
    }

    private void initRecentDevice() {
        deviceList.clear();
        String jsonInfo = ControlSave.read(mContext, "rencent_device", "");
        if (!TextUtils.isEmpty(jsonInfo)) {
            deviceList = HttpUtils.gb.create().fromJson(jsonInfo, new TypeToken<List<EntityDevice>>() {
            }.getType());
        } else {
            deviceList = new ArrayList<>();
        }
    }


    private void initSwipMenuListView() {
        SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem resetItem = new SwipeMenuItem(
                        mContext);
                resetItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                resetItem.setWidth(200);
                resetItem.setTitle(getString(R.string.rename));
                resetItem.setTitleSize(14);
                resetItem.setTitleColor(Color.WHITE);

                SwipeMenuItem relieveItem = new SwipeMenuItem(
                        mContext);
                relieveItem.setBackground(new ColorDrawable(Color.rgb(0xEA, 0x20,
                        0x00)));
                relieveItem.setWidth(200);
                relieveItem.setTitle(getString(R.string.disbind));
                relieveItem.setTitleSize(14);
                relieveItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(resetItem);
                menu.addMenuItem(relieveItem);
            }
        };
        mSwipeMenuListView.setMenuCreator(swipeMenuCreator);
    }

    private void initListener() {
        mClick = new MyClickListener();
        bt_add_device.setOnClickListener(mClick);

        mSwipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        //重命名
                        EditDialog.Builder builder = new EditDialog.Builder(mContext, deviceList.get(position).getNickName(), getString(R.string.rename_pooai_device));
                        builder.setOnSureButtonClickListener(new EditDialog.Builder.OnSureButtonClickListener() {
                            @Override
                            public void click(String string) {
                                EntityDevice entityDevice = deviceList.get(position);
                                entityDevice.setNickName(string);
                                if (ApplicationXpClient.isBandConnect() && ApplicationXpClient.getInstance().getConnectBandDevice().getDeviceAddress().equals(deviceList.get(position).getDeviceAddress()))
                                    ApplicationXpClient.getInstance().setConnectBandDevice(entityDevice);
                                if (ApplicationXpClient.isConnect() && ApplicationXpClient.getInstance().getConnectDevice().getDeviceAddress().equals(deviceList.get(position).getDeviceAddress()))
                                    ApplicationXpClient.getInstance().setConnectDevice(entityDevice);
                                deviceList.remove(position);
                                deviceList.add(position, entityDevice);
                                ControlSave.save(mContext, "rencent_device", HttpUtils.gb.create().toJson(deviceList));
                                mAdapter = new BleDeviceAdapter(mContext, deviceList);
                                mSwipeMenuListView.setAdapter(mAdapter);
                            }
                        });
                        builder.create().show();
                        break;
                    case 1:
                        if (ApplicationXpClient.isBandConnect() && ApplicationXpClient.getInstance().getConnectBandDevice().getDeviceAddress().equals(deviceList.get(position).getDeviceAddress())) {
                            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText(getString(R.string.tip))
                                    .setContentText(getString(R.string.band_disconnect_sure))
                                    .setCancelText(getString(R.string.cancel))
                                    .setConfirmText(getString(R.string.sure))
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            BluetoothController.getInstance().disBandConnect();
                                            ApplicationXpClient.setIsBandConnect(false);
                                            ApplicationXpClient.getInstance().setConnectBandDevice(null);
                                            deviceList.remove(position);
                                            ControlSave.save(mContext, "rencent_device", HttpUtils.gb.create().toJson(deviceList));
                                            mAdapter = new BleDeviceAdapter(mContext, deviceList);
                                            mSwipeMenuListView.setAdapter(mAdapter);
                                            sweetAlertDialog.dismiss();
                                        }
                                    })
                                    .show();
                        } else if (ApplicationXpClient.isConnect() && ApplicationXpClient.getInstance().getConnectDevice().getDeviceAddress().equals(deviceList.get(position).getDeviceAddress())) {
                            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText(getString(R.string.tip))
                                    .setContentText(getString(R.string.toilet_disconnect_sure))
                                    .setCancelText(getString(R.string.cancel))
                                    .setConfirmText(getString(R.string.sure))
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            BluetoothController.getInstance().disConnect();
                                            deviceList.remove(position);
                                            ControlSave.save(mContext, "rencent_device", HttpUtils.gb.create().toJson(deviceList));
                                            mAdapter = new BleDeviceAdapter(mContext, deviceList);
                                            mSwipeMenuListView.setAdapter(mAdapter);
                                            sweetAlertDialog.dismiss();
                                        }
                                    })
                                    .show();
                        } else {
                            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText(getString(R.string.tip))
                                    .setContentText(getString(R.string.sure_cancel_bind_device))
                                    .setCancelText(getString(R.string.cancel))
                                    .setConfirmText(getString(R.string.sure))
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            deviceList.remove(position);
                                            ControlSave.save(mContext, "rencent_device", HttpUtils.gb.create().toJson(deviceList));
                                            mAdapter = new BleDeviceAdapter(mContext, deviceList);
                                            mSwipeMenuListView.setAdapter(mAdapter);
                                            sweetAlertDialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                        //解除配对

                        break;
                }
                return false;
            }
        });
    }


    private void registerBroadcast() {
        myReceiver = new MyReceiver();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ConstantUtils.ACTION_RECEIVE_MESSAGE_FROM_DEVICE);
        mIntentFilter.addAction(ConstantUtils.ACTION_STOP_CONNECT);
        mIntentFilter.addAction(ConstantUtils.ACTION_UPDATE_DEVICE_LIST);
        mIntentFilter.addAction(ConstantUtils.ACTION_CONNECTED_ONE_DEVICE);
        mIntentFilter.addAction(ConstantUtils.ACTION_STOP_DISCOVERY);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(myReceiver, mIntentFilter);
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_add_device:
                    initRecentDevice();
                    if (mController.isBleOpen()) {
                        if (ContextCompat.checkSelfPermission(MyDeviceActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MyDeviceActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
                        } else {
                            mController.startScan();
                            newEntityList.clear();
                            showLoadingDialog();
                        }
                    } else {
                        T.showShort(getString(R.string.open_bluetooth));
                    }
                    break;
            }
        }
    }

    private void showLoadingDialog() {
        mSweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        mSweetAlertDialog.setTitleText(getString(R.string.searching))
                .setConfirmText(getString(R.string.cancel))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        mController.stopScan();
                    }
                });
        mSweetAlertDialog.show();
    }


    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConstantUtils.ACTION_UPDATE_DEVICE_LIST)) {
                isNewDevice = true;
                String name = intent.getStringExtra("name");
                String address = intent.getStringExtra("address");
                int rssi = intent.getIntExtra("rssi", 0);
                if (name.equals("Pooai-08") || name.equals("L8STAR-1") || name.equals("E02") || name.equals("Pooai-07")) {
                    for (int i = 0; i < deviceList.size(); i++) {
                        if (deviceList.get(i).getDeviceAddress().equals(address)) {
                            isNewDevice = false;
                        }
                    }
                    if (isNewDevice) {
                        boolean isRepeat = false;
                        for (int i = 0; i < newEntityList.size(); i++) {
                            if (address.equals(newEntityList.get(i).getDeviceAddress())) {
                                isRepeat = true;
                            }
                        }
                        if (!isRepeat) {
                            EntityDevice entityDevice = new EntityDevice(name, address);
                            entityDevice.setRssi(rssi);
                            if (name.equals("Pooai-08") || name.equals("Pooai-07")) {
                                entityDevice.setType(1);
                            } else if (name.equals("L8STAR-1") || name.equals("E02")) {
                                entityDevice.setType(2);
                            }
                            newEntityList.add(entityDevice);
                        }
                    }
                }

            } else if (action.equals(ConstantUtils.ACTION_RECEIVE_MESSAGE_FROM_DEVICE)) {

            } else if (action.equals(ConstantUtils.ACTION_STOP_CONNECT)) {

            } else if (action.equals(ConstantUtils.ACTION_CONNECTED_ONE_DEVICE)) {

            } else if (action.equals(ConstantUtils.ACTION_STOP_DISCOVERY)) {
                if (newEntityList.size() == 0) {
                    if (mSweetAlertDialog.isShowing()) {
                        mSweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        mSweetAlertDialog.setTitleText(getString(R.string.no_scan_new_device));
                        mSweetAlertDialog.setConfirmText(getString(R.string.sure));
                        mSweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        });
                    }
                } else {
                    if (mSweetAlertDialog.isShowing()) {
                        mSweetAlertDialog.dismiss();
                        select = 0;
                        //对扫描到的设备rssi进行从大到小的排序（即信号强度进行排序）
                        Collections.sort(newEntityList);
                        String[] items = new String[newEntityList.size()];
                        for (int i = 0; i < newEntityList.size(); i++) {
                            items[i] = newEntityList.get(i).getDeviceName();
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                mContext);
                        builder.setTitle(R.string.select_add_device);
                        builder.setCancelable(false);
                        builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    deviceList.add(newEntityList.get(which));
                                } else {
                                    if (deviceList.contains(newEntityList.get(which)))
                                        deviceList.remove(newEntityList.get(which));
                                }

                            }

                        });
                        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (select != -1) {
                                    //调试
                                    mAdapter = new BleDeviceAdapter(mContext, deviceList);
                                    mSwipeMenuListView.setAdapter(mAdapter);
                                    ControlSave.save(mContext, "rencent_device", HttpUtils.gb.create().toJson(deviceList));
                                }
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                }

            } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationXpClient.getInstance().setInMydevices(false);
        unregisterReceiver(myReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mController.startScan();
                newEntityList.clear();
                showLoadingDialog();
            } else {
                T.showShort(getString(R.string.no_permission));
            }
        }
    }
}
