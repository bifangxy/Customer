package com.xiaopu.customer.data.jsonresult;

import java.io.Serializable;
import java.util.Date;

/**
 * 手环心率检测表
 *
 * @author Administrator TT
 */

public class WristbandsHeartrate implements Serializable {
    public WristbandsHeartrate() {

    }

    /**
     * 主键自增 主键
     **/
    private Long id;
    /**
     * 用户id
     **/
    private Integer userId;
    /**
     * 检测时间
     **/
    private Date detectTime;
    /**
     * 心率平均值
     **/
    private Double heartRate;
    /**
     * 检测类别（单机测量or手机测量）
     **/
    private String category;
    /**
     * 更新时间
     **/
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Double heartRate) {
        this.heartRate = heartRate;
    }

    public Date getDetectTime() {
        return detectTime;
    }

    public void setDetectTime(Date detectTime) {
        this.detectTime = detectTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


}
