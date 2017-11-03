package com.okdeer.mall.order.bo;

import java.util.List;

import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;


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
	private String createTime;

	/**
	 * 查询关键字
	 */
	private String keyword;
	
	/**
	 * 不查询订单来源 0:友门鹿App,1:微信,2:pos 3.友门鹿便利店  4.便利店扫码购 5.会员卡扫码付 6微信小程序
	 */
	private OrderResourceEnum notOrderResource;
	
	/**
	 * 不查询订单来源 0:友门鹿App,1:微信,2:pos 3.友门鹿便利店  4.便利店扫码购 5.会员卡扫码付 6微信小程序
	 */
	private OrderResourceEnum orderResource;
	
	/**
	 * 订单类型列表
	 */
	private List<OrderTypeEnum> orderTypeList;
	
	/**
	 * 排除的订单类型列表
	 */
	private List<OrderTypeEnum> excludeOrderTypeList;
	
	private String screen;

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

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public OrderResourceEnum getNotOrderResource() {
		return notOrderResource;
	}

	public void setNotOrderResource(OrderResourceEnum notOrderResource) {
		this.notOrderResource = notOrderResource;
	}

	public List<OrderTypeEnum> getOrderTypeList() {
		return orderTypeList;
	}

	public void setOrderTypeList(List<OrderTypeEnum> orderTypeList) {
		this.orderTypeList = orderTypeList;
	}

	public String getScreen() {
		return screen;
	}

	public void setScreen(String screen) {
		this.screen = screen;
	}

	
	public OrderResourceEnum getOrderResource() {
		return orderResource;
	}

	public void setOrderResource(OrderResourceEnum orderResource) {
		this.orderResource = orderResource;
	}

	public List<OrderTypeEnum> getExcludeOrderTypeList() {
		return excludeOrderTypeList;
	}

	public void setExcludeOrderTypeList(List<OrderTypeEnum> excludeOrderTypeList) {
		this.excludeOrderTypeList = excludeOrderTypeList;
	}

}
