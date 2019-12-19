package com.xiaopu.customer.data.jsonresult;


import java.io.Serializable;
import java.util.Date;


/**
 * 私人医师购买订单表
 *
 * @author hujianbin
 * @since 1.0
 */
public class DoctorPrivateOrder implements Serializable {
    public DoctorPrivateOrder() {
    }

    /**
     * 主键自增 主键
     **/
    private Long id;
    /**
     * 订单号
     **/
    private Long orderSn;
    /**
     * 用户id，购买服务的那个作为父级用户的id
     **/
    private Integer userId;
    /**
     * 医师id
     **/
    private Integer doctorId;
    /**
     * 持续时间，0一周，1一月，2一季度，3一年
     **/
    private Integer lastTime;
    /**
     * 支付金额，单位小普币
     **/
    private Integer pay;
    /**
     * 订单创建时间
     **/
    private Date createTime;
    /**
     * 截止时间日期
     **/
    private Date endTime;
    /**
     * 评论状态，0待评价，1已评价
     **/
    private Integer commentStatus;
    /**
     * 是否有效 1-有效 0-无效
     **/
    private Integer status;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setOrderSn(Long orderSn) {
        this.orderSn = orderSn;
    }

    public Long getOrderSn() {
        return orderSn;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setLastTime(Integer lastTime) {
        this.lastTime = lastTime;
    }

    public Integer getLastTime() {
        return lastTime;
    }

    public void setPay(Integer pay) {
        this.pay = pay;
    }

    public Integer getPay() {
        return pay;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Integer getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(Integer commentStatus) {
        this.commentStatus = commentStatus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
