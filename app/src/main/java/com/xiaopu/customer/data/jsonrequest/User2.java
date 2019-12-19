package com.xiaopu.customer.data.jsonrequest;

import java.io.Serializable;
import java.util.Date;

/**
 * 消费者用户
 *
 * @author hujianbin
 * @since 1.0
 */
public class User2 implements Serializable {
    public User2() {
    }

    /**
     * 用户Id主键自增 主键
     **/
    private Integer id;
    /**
     * 登录名
     **/
    private String loginName;
    /**
     * 登录密码
     **/
    private String password;
    /**
     * 上次登录时间
     **/
    private Date loginTime;
    /**
     * 昵称
     **/
    private String nickname;
    /**
     * 头像
     **/
    private String avatar;
    /**
     * 性别，0女，1男
     **/
    private Integer sex;
    /**
     * 手机
     **/
    private String phone;
    /**
     * 邮箱
     **/
    private String mail;
    /**
     * 年龄
     **/
    private Integer age;
    /**
     * 出生日期
     **/
    private String birthday;
    /**
     * 血型，A，B，AB，O
     **/
    private String blood;
    /**
     * 疾病史
     **/
    private String illHistory;
    /**
     * 身高,cm
     **/
    private Integer height;
    /**
     * 体重,kg
     **/
    private Double weight;
    /**
     * 所在地域
     **/
    private String area;
    /**
     * 喜爱吃得主食品
     **/
    private String likeMainFood;
    /**
     * 喜爱的副食品
     **/
    private String likeSecondFood;
    /**
     * 余额，即现有可用资金
     **/
    private Double balance;
    /**
     * 冻结资金
     **/
    private Double freezeMoney;
    /**
     * 职业
     **/
    private String professional;
    /**
     * 更新（最后一次修改）时间
     **/
    private Date updateTime;
    /**
     * 登录次数
     **/
    private Integer loginCount;
    /**
     * 推送别名，唯一
     **/
    private String alias;
    /**
     * 创建(注册)时间
     **/
    private Date createTime;
    /**
     * 颜值
     **/
    private String ecSalt;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getSex() {
        return sex;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMail() {
        return mail;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getAge() {
        return age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getBlood() {
        return blood;
    }

    public void setIllHistory(String illHistory) {
        this.illHistory = illHistory;
    }

    public String getIllHistory() {
        return illHistory;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getHeight() {
        return height;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getWeight() {
        return weight;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public void setLikeMainFood(String likeMainFood) {
        this.likeMainFood = likeMainFood;
    }

    public String getLikeMainFood() {
        return likeMainFood;
    }

    public void setLikeSecondFood(String likeSecondFood) {
        this.likeSecondFood = likeSecondFood;
    }

    public String getLikeSecondFood() {
        return likeSecondFood;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getBalance() {
        return balance;
    }

    public String getProfessional() {
        return professional;
    }

    public void setProfessional(String professional) {
        this.professional = professional;
    }

    public void setFreezeMoney(Double freezeMoney) {
        this.freezeMoney = freezeMoney;
    }

    public Double getFreezeMoney() {
        return freezeMoney;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getEcSalt() {
        return ecSalt;
    }

    public void setEcSalt(String ecSalt) {
        this.ecSalt = ecSalt;
    }

    @Override
    public String toString() {
        return "User2{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", password='" + password + '\'' +
                ", loginTime=" + loginTime +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sex=" + sex +
                ", phone='" + phone + '\'' +
                ", mail='" + mail + '\'' +
                ", age=" + age +
                ", birthday='" + birthday + '\'' +
                ", blood='" + blood + '\'' +
                ", illHistory='" + illHistory + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", area='" + area + '\'' +
                ", likeMainFood='" + likeMainFood + '\'' +
                ", likeSecondFood='" + likeSecondFood + '\'' +
                ", balance=" + balance +
                ", freezeMoney=" + freezeMoney +
                ", professional='" + professional + '\'' +
                ", updateTime=" + updateTime +
                ", loginCount=" + loginCount +
                ", alias='" + alias + '\'' +
                ", createTime=" + createTime +
                ", ecSalt='" + ecSalt + '\'' +
                '}';
    }
}
