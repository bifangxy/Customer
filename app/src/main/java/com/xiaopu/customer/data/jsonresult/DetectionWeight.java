package com.xiaopu.customer.data.jsonresult;

import java.util.Date;

/**
 * Created by Administrator on 2016/8/26 0026.
 */
public class DetectionWeight {
    public DetectionWeight(){
    }
    /**主键自增长**/
    private Integer id;
    /**用户id**/
    private Integer userId;
    /**检测类别**/
    private String category;
    /**体重**/
    private Double weight;
    /**体重检测结果**/
    private String weightResult;
    /**体重标准**/
    private String weightStandard;
    /**BMI指数**/
    private String bmi;
    /** 状态，0无效，1有效 **/
    private Integer status;
    /**分组日期，只存储年月，用于历史查询**/
    private Date groupDate;
    /** 创建时间 **/
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getWeightResult() {
        return weightResult;
    }

    public void setWeightResult(String weightResult) {
        this.weightResult = weightResult;
    }

    public String getWeightStandard() {
        return weightStandard;
    }

    public void setWeightStandard(String weightStandard) {
        this.weightStandard = weightStandard;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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
}
