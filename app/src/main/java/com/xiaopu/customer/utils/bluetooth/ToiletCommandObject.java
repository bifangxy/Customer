package com.xiaopu.customer.utils.bluetooth;

/**
 * Created by Xieying on 2016/6/17 0017.
 * 功能：
 */
public  class ToiletCommandObject {
    private int address;
    private int value;
    private String cmd;

    private boolean isUserAvatar = false;

    public void setAddress(int ad) {
        address = ad;
    }

    public void setValue(int val) {
        value = val;
    }

    public int getAddress() {
        return address;
    }

    public int getValue() {
        return value;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public boolean isUserAvatar() {
        return isUserAvatar;
    }

    public void setIsUserAvatar(boolean isUserAvatar) {
        this.isUserAvatar = isUserAvatar;
    }


}