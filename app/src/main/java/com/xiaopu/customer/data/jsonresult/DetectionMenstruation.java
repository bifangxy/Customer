package com.xiaopu.customer.data.jsonresult;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class DetectionMenstruation implements Serializable {
    /**
     * 主键自增长
     **/
    private int id;
    /**
     * 用户id
     **/
    private int userId;
    /**
     * 检测类别
     **/
    private String category;
    /**
     * 平均月经周期
     **/
    private int averageMenstrual;
    /**
     * 平均行经周期
     **/
    private int averageMenstrualPeriod;
    /**
     * 最近一次月经日期
     **/
    private Date menstrualTime;
    /**
     * 0-无效，1-有效
     **/
    private int status;
    /**
     * 分组字段，用于历史查询
     **/
    private Date groupDate;
    /**
     * 创建日期
     **/
    private Date createTime;

    public DetectionMenstruation() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getAverageMenstrual() {
        return averageMenstrual;
    }

    public void setAverageMenstrual(int averageMenstrual) {
        this.averageMenstrual = averageMenstrual;
    }

    public Integer getAverageMenstrualPeriod() {
        return averageMenstrualPeriod;
    }

    public void setAverageMenstrualPeriod(int averageMenstrualPeriod) {
        this.averageMenstrualPeriod = averageMenstrualPeriod;
    }

    public Date getMenstrualTime() {
        return menstrualTime;
    }

    public void setMenstrualTime(Date menstrualTime) {
        this.menstrualTime = menstrualTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getGroupDate() {
        return groupDate;
    }

    public void setGroupDate(Date groupDate) {
        this.groupDate = groupDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DetectionMenstruation{" +
                "id=" + id +
                ", userId=" + userId +
                ", category='" + category + '\'' +
                ", averageMenstrual=" + averageMenstrual +
                ", averageMenstrualPeriod=" + averageMenstrualPeriod +
                ", menstrualTime=" + menstrualTime +
                ", status=" + status +
                ", groupDate=" + groupDate +
                ", createTime=" + createTime +
                '}';
    }
}
