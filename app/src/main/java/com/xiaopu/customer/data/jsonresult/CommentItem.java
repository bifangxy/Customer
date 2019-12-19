package com.xiaopu.customer.data.jsonresult;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hujianbin
 * @since 1.0
 */
public class CommentItem implements Serializable {
    public CommentItem() {
    }

    /**
     * 主键自增
     **/
    private Long id;
    /**
     * 订单号
     **/
    private Long orderSn;
    /**
     * 用户Id
     **/
    private String userId;
    /**
     * 医师Id
     **/
    private Integer doctorId;
    /**
     * 0留言，1预约 ,2私人
     **/
    private Integer commentType;
    /**
     * 医生服务：0很满意，1一般，2不满意
     **/
    private Integer doctorService;
    /**
     * 服务态度，暂定3档，0态度敬业，1态度良好，2服务态度差
     **/
    private Integer attitude;
    /**
     * 水平，暂定3档，0水平高超，1水平很好，2水平一般
     **/
    private Integer level;
    /**
     * 评论内容
     **/
    private String content;
    /**
     * 评价时间
     **/
    private Date createTime;
    /**
     * 状态，0无效，1有效
     **/
    private Integer status;
    /**
     * 昵称
     **/
    private String nickname;
    /**
     * 头像
     **/
    private String userAvatar;

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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setCommentType(Integer commentType) {
        this.commentType = commentType;
    }

    public Integer getCommentType() {
        return commentType;
    }

    public void setAttitude(Integer attitude) {
        this.attitude = attitude;
    }

    public Integer getAttitude() {
        return attitude;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getLevel() {
        return level;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public Integer getDoctorService() {
        return doctorService;
    }

    public void setDoctorService(Integer doctorService) {
        this.doctorService = doctorService;
    }

}
