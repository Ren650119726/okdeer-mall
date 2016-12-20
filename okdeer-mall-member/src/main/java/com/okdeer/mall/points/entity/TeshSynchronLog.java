package com.okdeer.mall.points.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * ClassName: TeshSynchronLog 
 * @Description: 积分商品同步日志
 * @author tangy
 * @date 2016年12月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.3.0          2016年12月15日                               tangy
 */
public class TeshSynchronLog implements Serializable {

	/**
	 * @Fields serialVersionUID : 
	 */
	private static final long serialVersionUID = 2902566746216294780L;
	
	/**
	 * id
	 */
	private String id;

	/**
	 * 同步时间
	 */
	private Date synchronTime;

	/**
	 * 同步状态
	 */
	private Integer status;
	
	/**
	 * 同步成功数
	 */
	private Integer successNum;

	/**
	 * 备注
	 */
	private String remark;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	
	public Date getSynchronTime() {
		return synchronTime;
	}
	
	public void setSynchronTime(Date synchronTime) {
		this.synchronTime = synchronTime;
	}

	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	
	public Integer getSuccessNum() {
		return successNum;
	}

	public void setSuccessNum(Integer successNum) {
		this.successNum = successNum;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	} 
	
}
