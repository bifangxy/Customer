package com.xiaopu.customer.data.jsonresult;

import java.io.Serializable;
import java.util.Date;

/**
 * 分享功能表
 * @author qinjinying
 * @since 1.0
 *
 */
public class Share implements Serializable{
	/**主键自增长**/
	private Integer id ;
	/**标题**/
	private String title;
	/**图片**/
	private String avatar;
	/**网址**/
	private String url;
	/**内容**/
	private String content;
	/**状态**/
	private Integer status;
	/**创建时间**/
	private Date createTime;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
}
