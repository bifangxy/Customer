package com.xiaopu.customer.utils;

/**
 * 颜色类
 *
 * @author James
 * @version 2015-7-10 V0.1
 */
public class Color {
    /**
     * 红色
     */
    private int R = 0;
    /**
     * 绿色
     */
    private int G = 0;
    /**
     * 蓝色
     */
    private int B = 0;

    /**
     * 透明通道
     */
    private int Algha = 0;


    /**
     * 通过整形数构造Color
     *
     * @param r 红色
     * @param g 绿色
     * @param b 蓝色
     * @param a 透明
     */
    public Color(int r, int g, int b, int a) {
        this.R = r;
        this.G = g;
        this.B = b;
        this.Algha = a;
    }

    /**
     * 通过字节构造Color
     *
     * @param r 红色
     * @param g 绿色
     * @param b 蓝色
     * @param a 透明
     */
    public Color(byte r, byte g, byte b, byte a) {
        this.R = r;
        this.G = g;
        this.B = b;
        this.Algha = a;
    }

    /**
     * 普通函数
     */
    public Color() {

    }

    /**
     * @return 红色通道
     */
    public int getR() {
        return R;
    }

    /**
     * @param r 设置红色通道
     */
    public void setR(int r) {
        R = r;
    }

    /**
     * @return 绿色通道
     */
    public int getG() {
        return G;
    }

    /**
     * @param g 设置绿色通道
     */
    public void setG(int g) {
        G = g;
    }

    /**
     * @return 蓝色通道
     */
    public int getB() {
        return B;
    }

    /**
     * @param b 设置蓝色通道
     */
    public void setB(int b) {
        B = b;
    }

    /**
     * @return 透明通道
     */
    public int getAlgha() {
        return Algha;
    }

    /**
     * @param algha 设置透明通道
     */
    public void setAlgha(int algha) {
        Algha = algha;
    }
}
