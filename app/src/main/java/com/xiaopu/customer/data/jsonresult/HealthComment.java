package com.xiaopu.customer.data.jsonresult;

import java.io.Serializable;
import java.util.Date;

/**
 * 健康评测数据表
 * @author 
 * @since 1.0
 *
 */
public class HealthComment implements Serializable{
  	public HealthComment() {
  	}
	/**  主键**/
	private Long id;
	/** 用户ID **/
	private Integer userId;
	/** 糖尿病 **/
	private String dm;
	/**糖尿病检测结果**/
	private String dmResult;
	/** 糖尿病检测时间 **/
	private Date dmTime;
	/**冠心病**/
	private String coronary;
	/**冠心病检测结果**/
	private String corResult;
	/** 冠心病检测时间 **/
	private Date coronaryTime;
	/**高血脂**/
	private String hyperlipoidemia;
	/**高血脂检测结果**/
	private String hyperlipoidemiaResult;
	/** 高血脂检测时间 **/
	private Date hyperlipoidemiaTime;
	/**亚健康检测标准**/
	private String subhealthy;
	/**亚健康检测结果**/
	private String subhealthyResult;
	/** 亚健康检测时间 **/
	private Date subhealthyTime;
	/**怀孕前自测检测标准**/
	private String pregnancyTest;
	/**怀孕前自测检测结果**/
	private String pregnancyTestResult;
	/** 怀孕前自测时间 **/
	private Date pregnancyTestTime;
	/**高血压检测标准**/
	private String hypertension;
	/**高血压检测结果**/
	private String hypertensionResult;
	/** 高血压检测时间 **/
	private Date hypertensionTime;
	/** 创建时间 **/
	private Date createTime;
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
	public String getDm() {
		return dm;
	}
	public void setDm(String dm) {
		this.dm = dm;
	}
	public String getDmResult() {
		return dmResult;
	}
	public void setDmResult(String dmResult) {
		this.dmResult = dmResult;
	}
	public String getCoronary() {
		return coronary;
	}
	public void setCoronary(String coronary) {
		this.coronary = coronary;
	}
	public String getCorResult() {
		return corResult;
	}
	public void setCorResult(String corResult) {
		this.corResult = corResult;
	}
	public String getHyperlipoidemia() {
		return hyperlipoidemia;
	}
	public void setHyperlipoidemia(String hyperlipoidemia) {
		this.hyperlipoidemia = hyperlipoidemia;
	}
	public String getHyperlipoidemiaResult() {
		return hyperlipoidemiaResult;
	}
	public void setHyperlipoidemiaResult(String hyperlipoidemiaResult) {
		this.hyperlipoidemiaResult = hyperlipoidemiaResult;
	}
	public String getSubhealthy() {
		return subhealthy;
	}
	public void setSubhealthy(String subhealthy) {
		this.subhealthy = subhealthy;
	}
	public String getSubhealthyResult() {
		return subhealthyResult;
	}
	public void setSubhealthyResult(String subhealthyResult) {
		this.subhealthyResult = subhealthyResult;
	}
	public String getPregnancyTest() {
		return pregnancyTest;
	}
	public void setPregnancyTest(String pregnancyTest) {
		this.pregnancyTest = pregnancyTest;
	}
	public String getPregnancyTestResult() {
		return pregnancyTestResult;
	}
	public void setPregnancyTestResult(String pregnancyTestResult) {
		this.pregnancyTestResult = pregnancyTestResult;
	}
	public String getHypertension() {
		return hypertension;
	}
	public void setHypertension(String hypertension) {
		this.hypertension = hypertension;
	}
	public String getHypertensionResult() {
		return hypertensionResult;
	}
	public void setHypertensionResult(String hypertensionResult) {
		this.hypertensionResult = hypertensionResult;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getDmTime() {
		return dmTime;
	}
	public void setDmTime(Date dmTime) {
		this.dmTime = dmTime;
	}
	public Date getCoronaryTime() {
		return coronaryTime;
	}
	public void setCoronaryTime(Date coronaryTime) {
		this.coronaryTime = coronaryTime;
	}
	public Date getHyperlipoidemiaTime() {
		return hyperlipoidemiaTime;
	}
	public void setHyperlipoidemiaTime(Date hyperlipoidemiaTime) {
		this.hyperlipoidemiaTime = hyperlipoidemiaTime;
	}
	public Date getSubhealthyTime() {
		return subhealthyTime;
	}
	public void setSubhealthyTime(Date subhealthyTime) {
		this.subhealthyTime = subhealthyTime;
	}
	public Date getPregnancyTestTime() {
		return pregnancyTestTime;
	}
	public void setPregnancyTestTime(Date pregnancyTestTime) {
		this.pregnancyTestTime = pregnancyTestTime;
	}
	public Date getHypertensionTime() {
		return hypertensionTime;
	}
	public void setHypertensionTime(Date hypertensionTime) {
		this.hypertensionTime = hypertensionTime;
	}
	
}
