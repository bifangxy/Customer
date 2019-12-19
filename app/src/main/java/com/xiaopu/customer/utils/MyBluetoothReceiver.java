package com.xiaopu.customer.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaopu.customer.data.EntityDevice;
import com.xiaopu.customer.utils.bluetooth.ConstantUtils;

/**
 * Created by Administrator on 2017/10/9.
 */

public class MyBluetoothReceiver extends BroadcastReceiver {

    MyBluetoothSateListener myBluetoothSateListener;

    public MyBluetoothSateListener getMyBluetoothSateListener() {
        return myBluetoothSateListener;
    }

    public void setMyBluetoothSateListener(MyBluetoothSateListener myBluetoothSateListener) {
        this.myBluetoothSateListener = myBluetoothSateListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //搜索到设备
        if (action.equals(ConstantUtils.ACTION_UPDATE_DEVICE_LIST)) {
            String name = intent.getStringExtra("name");
            String address = intent.getStringExtra("address");
            int rssi = intent.getIntExtra("rssi", 0);
            EntityDevice entityDevice = new EntityDevice(name, address);
            entityDevice.setRssi(rssi);
            myBluetoothSateListener.update(entityDevice);
            //搜索结束
        } else if (action.equals(ConstantUtils.ACTION_STOP_DISCOVERY)) {
            myBluetoothSateListener.discovery();
            //连接上马桶
        } else if (action.equals(ConstantUtils.ACTION_CONNECTED_ONE_DEVICE)) {
            String name = intent.getStringExtra("name");
            String address = intent.getStringExtra("address");
            EntityDevice entityDevice = new EntityDevice(name, address);
            myBluetoothSateListener.connectToilet(entityDevice);
            //从马桶收到消息
        } else if (action.equals(ConstantUtils.ACTION_RECEIVE_MESSAGE_FROM_DEVICE)) {

            //马桶连接断开
            myBluetoothSateListener.disConnectToilet();
            //连接上手环
        } else if (action.equals(ConstantUtils.ACTION_BAND_CONNECTED_ONE_DEVICE)) {
            String name = intent.getStringExtra("name");
            String address = intent.getStringExtra("address");
            EntityDevice entityDevice = new EntityDevice(name, address);
            myBluetoothSateListener.connectBand(entityDevice);
            //手环连接断开
        } else if (action.equals(ConstantUtils.ACTION_BAND_STOP_CONNECT)) {
            myBluetoothSateListener.disConnectBand();
            //从手环收到消息
        } else if (action.equals(ConstantUtils.ACTION_RECEIVE_MESSAGE_FROM_BAND)) {

        }
    }
}
