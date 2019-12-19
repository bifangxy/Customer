package com.xiaopu.customer.utils;

import com.xiaopu.customer.data.EntityDevice;

/**
 * Created by Administrator on 2017/10/9.
 */

public interface MyBluetoothSateListener {
    void update(EntityDevice device);

    void discovery();

    void connectToilet(EntityDevice entityDevice);

    void disConnectToilet();

    void receiverToiletMessage(byte[] data);

    void connectBand(EntityDevice entityDevice);

    void disConnectBand();

    void receiverBandMessage(byte[] date);

}
