
package com.okdeer.mall.order.bo;

import java.math.BigDecimal;
import java.util.Date;

import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;

/**
 * ClassName: FmsTradeOrderBo 
 * @Description: 财务系统订单信息
 * @author zengjizu
 * @date 2017年7月27日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class FmsTradeOrderBo {

	/**
	 * 订单id
	 */
	private String id;

	/**
	* 订单号生成规则：XS+6位日期+8位流水号
	*/
	private String orderNo;

	/**
	 * 订单类型，0：实物订单，1：服务订单
	 */
	private OrderTypeEnum type;

	/**
	 * 店铺名称
	 */
	private String storeName;

	/**
	 * 店铺id
	 */
	private String storeId;

	/**
	 * 买家手机号
	 */
	private String userPhone;

	/**
	 * 买家id
	 */
	private String userId;

	/**
	 * 订单总金额
	 */
	private BigDecimal totalAmount;

	/**
	 * 实付金额
	 */
	private BigDecimal actualAmount;

	/**
	 * 优惠金额
	 */
	private BigDecimal preferentialPrice;

	/**
	 * 收入
	 */
	private BigDecimal income;

	/**
	 * 下单时间
	 */
	private Date createTime;

	/**
	 * 支付类型
	 */
	private PayWayEnum payWay;

	/**
	 * 状态，0：待付款，1：待发货，2：已取消，3：待签收，4：已拒收，5：已签收（交易成功），6：
	 */
	private OrderStatusEnum status;

	/**
	 * 下单开始时间
	 */
	private Date startTime;

	/**
	 * 下单结束时间
	 */
	private Date endTime;

	/**
	 * 订单来源
	 */
	private OrderResourceEnum orderResource;

	/**
	 * 支付方式
	 */
	private PayTypeEnum payType;

	/**
	 * 是否违约
	 */
	private WhetherEnum isBreach;

	/**
	 * 违约金
	 */
	private BigDecimal breachMoney;

	/**
	 * 接单时间
	 */
	private Date acceptTime;

	/**
	 * 发货时间
	 */
	private Date deliveryTime;

	/**
	 * 收货时间
	 */
	private Date receivedTime;

	/**
	 * 活动类型
	 */
	private ActivityTypeEnum activityType;

	/**
	 * 活动id
	 */
	private String activityId;

	/***
	 * 扩展区域信息-所在区域
	 */
	private String lAreaExt;

	/***
	 * 定位区名称
	 */
	private String lAreaName;

	/***
	 * 定位市名称
	 */
	private String lCityName;

	/***
	 * 定位省名称
	 */
	private String lProviceName;

	/***
	 * 地址区名称
	 */
	private String aAreaName;

	/***
	 * 地址市名称
	 */
	private String aCityName;

	/***
	 * 地址省名称
	 */
	private String aProviceName;

	/**
	 * 详细地址
	 */
	private String address;

	/**
	 * 所属城市
	 */
	private String cityName;

	/**
	 * 订单定位地址
	 */
	private String location;

	/**
	 * 订单更新时间
	 */
	private Date updateTime;

	/**
	 * 订单的提货类型
	 */
	private PickUpTypeEnum pickUpType;

	/**
	 * 运费
	 */
	private BigDecimal fare;

	/**
	 * 实际运费优惠金额
	 */
	private BigDecimal realFarePreferential;

	/**
	 * 平台优惠金额
	 */
	private BigDecimal platformPreferential;

	/**
	 * 店铺优惠金额
	 */
	private BigDecimal storePreferential;

	/**
	 * 配送方案 0 鹿掌柜 1 第三方配送 2 商家自己配送
	 */
	private Integer deliveryType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public OrderTypeEnum getType() {
		return type;
	}

	public void setType(OrderTypeEnum type) {
		this.type = type;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	public BigDecimal getPreferentialPrice() {
		return preferentialPrice;
	}

	public void setPreferentialPrice(BigDecimal preferentialPrice) {
		this.preferentialPrice = preferentialPrice;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public PayWayEnum getPayWay() {
		return payWay;
	}

	public void setPayWay(PayWayEnum payWay) {
		this.payWay = payWay;
	}

	public OrderStatusEnum getStatus() {
		return status;
	}

	public void setStatus(OrderStatusEnum status) {
		this.status = status;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public OrderResourceEnum getOrderResource() {
		return orderResource;
	}

	public void setOrderResource(OrderResourceEnum orderResource) {
		this.orderResource = orderResource;
	}

	public PayTypeEnum getPayType() {
		return payType;
	}

	public void setPayType(PayTypeEnum payType) {
		this.payType = payType;
	}

	public WhetherEnum getIsBreach() {
		return isBreach;
	}

	public void setIsBreach(WhetherEnum isBreach) {
		this.isBreach = isBreach;
	}

	public BigDecimal getBreachMoney() {
		return breachMoney;
	}

	public void setBreachMoney(BigDecimal breachMoney) {
		this.breachMoney = breachMoney;
	}

	public Date getAcceptTime() {
		return acceptTime;
	}

	public void setAcceptTime(Date acceptTime) {
		this.acceptTime = acceptTime;
	}

	public Date getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public Date getReceivedTime() {
		return receivedTime;
	}

	public void setReceivedTime(Date receivedTime) {
		this.receivedTime = receivedTime;
	}

	public ActivityTypeEnum getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityTypeEnum activityType) {
		this.activityType = activityType;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getlAreaExt() {
		return lAreaExt;
	}

	public void setlAreaExt(String lAreaExt) {
		this.lAreaExt = lAreaExt;
	}

	public String getlAreaName() {
		return lAreaName;
	}

	public void setlAreaName(String lAreaName) {
		this.lAreaName = lAreaName;
	}

	public String getlCityName() {
		return lCityName;
	}

	public void setlCityName(String lCityName) {
		this.lCityName = lCityName;
	}

	public String getlProviceName() {
		return lProviceName;
	}

	public void setlProviceName(String lProviceName) {
		this.lProviceName = lProviceName;
	}

	public String getaAreaName() {
		return aAreaName;
	}

	public void setaAreaName(String aAreaName) {
		this.aAreaName = aAreaName;
	}

	public String getaCityName() {
		return aCityName;
	}

	public void setaCityName(String aCityName) {
		this.aCityName = aCityName;
	}

	public String getaProviceName() {
		return aProviceName;
	}

	public void setaProviceName(String aProviceName) {
		this.aProviceName = aProviceName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public PickUpTypeEnum getPickUpType() {
		return pickUpType;
	}

	public void setPickUpType(PickUpTypeEnum pickUpType) {
		this.pickUpType = pickUpType;
	}

	public BigDecimal getFare() {
		return fare;
	}

	public void setFare(BigDecimal fare) {
		this.fare = fare;
	}

	public BigDecimal getRealFarePreferential() {
		return realFarePreferential;
	}

	public void setRealFarePreferential(BigDecimal realFarePreferential) {
		this.realFarePreferential = realFarePreferential;
	}

	public BigDecimal getPlatformPreferential() {
		return platformPreferential;
	}

	public void setPlatformPreferential(BigDecimal platformPreferential) {
		this.platformPreferential = platformPreferential;
	}

	public BigDecimal getStorePreferential() {
		return storePreferential;
	}

	public void setStorePreferential(BigDecimal storePreferential) {
		this.storePreferential = storePreferential;
	}

	public Integer getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(Integer deliveryType) {
		this.deliveryType = deliveryType;
	}

}
