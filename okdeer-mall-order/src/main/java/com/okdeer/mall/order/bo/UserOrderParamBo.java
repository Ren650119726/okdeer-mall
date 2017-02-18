package com.okdeer.mall.order.bo;

/**
 * ClassName: UserOrderParamBo 
 * @Description: 用户订单参数
 * @author maojj
 * @date 2017年2月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.1 			2017年2月18日				maojj
 */
public class UserOrderParamBo {

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 分页页码
	 */
	private int pageNumber;

	/**
	 * 每页显示数量
	 */
	private int pageSize;

	/**
	 * 订单状态
	 */
	private String status;

	/**
	 * 下单时间
	 */
	private String orderTime;

	/**
	 * 查询关键字
	 */
	private String keyword;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}
