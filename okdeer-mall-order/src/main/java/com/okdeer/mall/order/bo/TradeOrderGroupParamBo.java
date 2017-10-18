package com.okdeer.mall.order.bo;

import java.util.Date;

import com.okdeer.mall.order.entity.TradeOrderGroup;
import com.okdeer.mall.order.enums.GroupOrderStatusEnum;

/**
 * ClassName: TradeOrderGroupParamBo 
 * @Description: 团购订单查询参数
 * @author maojj
 * @date 2017年10月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.6.3 		2017年10月12日				maojj
 */
public class TradeOrderGroupParamBo extends TradeOrderGroup {

	/**
	 * 成团时间开始区间
	 */
	private Date groupTimeStart;

	/**
	 * 成团时间结束区间
	 */
	private Date groupTimeEnd;

	/**
	 * 当前团购订单状态(乐观锁机制)
	 */
	private GroupOrderStatusEnum currentStatus;
	
	public Date getGroupTimeStart() {
		return groupTimeStart;
	}

	public void setGroupTimeStart(Date groupTimeStart) {
		this.groupTimeStart = groupTimeStart;
	}

	public Date getGroupTimeEnd() {
		return groupTimeEnd;
	}

	public void setGroupTimeEnd(Date groupTimeEnd) {
		this.groupTimeEnd = groupTimeEnd;
	}

	public GroupOrderStatusEnum getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(GroupOrderStatusEnum currentStatus) {
		this.currentStatus = currentStatus;
	}

}
