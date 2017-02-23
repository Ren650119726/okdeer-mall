package com.okdeer.mall.activity.coupons.bo;

import java.util.Date;

/**
 * ClassName: ActivityCouponsBo 
 * @Description: 代金券Bo对象
 * @author maojj
 * @date 2017年2月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.1 			2017年2月23日				maojj
 */
public class ActivityCouponsBo {

	/**
	 * 代金券主键ID
	 */
	private String id;

	/**
	 * 代金券使用数量
	 */
	private Integer usedNum;

	/**
	 * 代金券剩余数量
	 */
	private Integer remainNum;

	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	public ActivityCouponsBo(){}
	
	public ActivityCouponsBo(String id){
		this(id,null);
	}
	
	public ActivityCouponsBo(String id,Integer usedNum){
		this(id,usedNum,null);
	}
	
	public ActivityCouponsBo(String id,Integer usedNum,Integer remainNum){
		this.id = id;
		this.usedNum = usedNum;
		this.remainNum = remainNum;
		this.updateTime = new Date();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getUsedNum() {
		return usedNum;
	}

	public void setUsedNum(Integer usedNum) {
		this.usedNum = usedNum;
	}

	public Integer getRemainNum() {
		return remainNum;
	}

	public void setRemainNum(Integer remainNum) {
		this.remainNum = remainNum;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
