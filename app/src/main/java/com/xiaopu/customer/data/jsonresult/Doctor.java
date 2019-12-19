package com.xiaopu.customer.data.jsonresult;

import java.io.Serializable;
import java.util.*;

/**
 * 医师表
 *
 * @author hujianbin
 * @since 1.0
 */
public class Doctor implements Serializable {
    public Doctor() {
    }

    /**
     * 主键
     **/
    private Integer id;
    /**
     * 手机号
     **/
    private String phone;
    /**
     * 实名
     **/
    private String realname;
    /**
     * 密码
     **/
    private String password;
    /**
     * 性别：0-女，1-男
     **/
    private Integer sex;
    /**
     * 类型，0专科医师，1健康管家
     **/
    private Integer type;
    /**
     * 身份证或者护照号
     **/
    private String identificationCode;
    /**
     * 头像地址
     **/
    private String avatar;
    /**
     * 资质证书正面图片地址
     **/
    private String certificateFont;
    /**
     * 资质证书反面图片地址
     **/
    private String certificateBack;
    /**
     * 职业证书正面图片地址
     **/
    private String professionFont;
    /**
     * 职业证书正面图片地址
     **/
    private String professionBack;
    /**
     * 医院id
     **/
    private Integer hospitalId;
    /**
     * 医院
     **/
    private String hospital;
    /**
     * 医院地址
     **/
    private String hospitalAddress;
    /**
     * 科室id
     **/
    private Integer departmentId;
    /**
     * 科室
     **/
    private String department;
    /**
     * 职称,0主任医师，1副主任医师，2主治医师，3住院医师
     **/
    private Integer title;
    /**
     * 简介
     **/
    private String introduction;
    /**
     * 擅长
     **/
    private String goodAt;
    /**
     * 医师现有资金,即余额
     **/
    private Double balance;
    /**
     * 冻结资金
     **/
    private Double freezeMoney;
    /**
     * 评价分数
     **/
    private Double commentRate;
    /**
     * 邀请码
     **/
    private String inviteCode;
    /**
     * 二位码图片地址
     **/
    private String qrCode;
    /**
     * 是否是医师之星
     **/
    private Integer isStar;
    /**
     * 在系统内状态标识，0无效，1有效
     **/
    private Integer status;
    /**
     * 审核状态，0未审核，1审核通过，2审核未通过
     **/
    private Integer auditStatus;
    /**
     * 上次登录时间
     **/
    private Date loginTime;
    /**
     * 登录次数
     **/
    private Integer loginCount;
    /**
     * 资料更新时间
     **/
    private Date updateTime;
    /**
     * 创建时间
     **/
    private Date createTime;

    /**
     * 主副医生 0,关联医生 1，主医生
     */
    private int phoneType;

    /**
     * 主科室id
     */
    private int persentDepartmentId;

    /**
     * 医生标签 多个以,隔开
     */
    private String targets;

    /**
     * 医生图片详情，多个以,隔开
     */
    private String introductionImage;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getRealname() {
        return realname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public String getIdentificationCode() {
        return identificationCode;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setCertificateFont(String certificateFont) {
        this.certificateFont = certificateFont;
    }

    public String getCertificateFont() {
        return certificateFont;
    }

    public void setCertificateBack(String certificateBack) {
        this.certificateBack = certificateBack;
    }

    public String getCertificateBack() {
        return certificateBack;
    }

    public void setProfessionFont(String professionFont) {
        this.professionFont = professionFont;
    }

    public String getProfessionFont() {
        return professionFont;
    }

    public void setProfessionBack(String professionBack) {
        this.professionBack = professionBack;
    }

    public String getProfessionBack() {
        return professionBack;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospitalAddress(String hospitalAddress) {
        this.hospitalAddress = hospitalAddress;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setTitle(Integer title) {
        this.title = title;
    }

    public Integer getTitle() {
        return title;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setGoodAt(String goodAt) {
        this.goodAt = goodAt;
    }

    public String getGoodAt() {
        return goodAt;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getBalance() {
        return balance;
    }

    public void setFreezeMoney(Double freezeMoney) {
        this.freezeMoney = freezeMoney;
    }

    public Double getFreezeMoney() {
        return freezeMoney;
    }

    public void setCommentRate(Double commentRate) {
        this.commentRate = commentRate;
    }

    public Double getCommentRate() {
        return commentRate;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setIsStar(Integer isStar) {
        this.isStar = isStar;
    }

    public Integer getIsStar() {
        return isStar;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public int getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(int phoneType) {
        this.phoneType = phoneType;
    }

    public int getPersentDepartmentId() {
        return persentDepartmentId;
    }

    public void setPersentDepartmentId(int persentDepartmentId) {
        this.persentDepartmentId = persentDepartmentId;
    }

    public String getTargets() {
        return targets;
    }

    public void setTargets(String targets) {
        this.targets = targets;
    }

    public String getIntroductionImage() {
        return introductionImage;
    }

    public void setIntroductionImage(String introductionImage) {
        this.introductionImage = introductionImage;
    }
}
