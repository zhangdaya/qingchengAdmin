package com.qingcheng.pojo.business;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
/**
 * ad实体类
 * @author Administrator
 *
 */
@Table(name="tb_ad")
public class Ad implements Serializable{

	/**
	 * ID
	 */
	@Id
	private Integer id;

	/**
	 * 广告名称
	 */
	private String name;

	/**
	 * 广告位置
	 */
	private String position;

	/**
	 * 开始时间
	 */
	private java.util.Date startTime;

	/**
	 * 到期时间
	 */
	private java.util.Date endTime;

	/**
	 * 状态
	 */
	private String status;

	/**
	 * 图片地址
	 */
	private String image;

	/**
	 * URL
	 */
	private String url;

	/**
	 * 备注
	 */
	private String remarks;

	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}

	public java.util.Date getStartTime() {
		return startTime;
	}
	public void setStartTime(java.util.Date startTime) {
		this.startTime = startTime;
	}

	public java.util.Date getEndTime() {
		return endTime;
	}
	public void setEndTime(java.util.Date endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}


	
}
