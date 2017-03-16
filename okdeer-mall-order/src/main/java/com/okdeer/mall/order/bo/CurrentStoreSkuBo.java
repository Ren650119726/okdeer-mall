package com.okdeer.mall.order.bo;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.goods.store.enums.BSSC;

import net.sf.json.JSONObject;

public class CurrentStoreSkuBo {

	/**
	 * 商品Id
	 */
	private String id;

	/**
	 * spuId
	 */
	private String storeSpuId;

	/**
	 * 商品名称
	 */
	private String name;

	/**
	 * 商品条码
	 */
	private String barCode;

	/**
	 * 款码
	 */
	private String styleCode;

	/**
	 * 商品主图
	 */
	private String mainPicUrl;

	/**
	 * 商品服务保障
	 */
	private String guaranteed;

	/**
	 * 标准商品id
	 */
	private String skuId;

	/**
	 * 标准商品连锁Id
	 */
	private String multipleSkuId;

	/**
	 * 商品属性
	 */
	private String propertiesIndb;

	/**
	 * 线上价格
	 */
	private BigDecimal onlinePrice;
	
	/**
	 * 线下价格
	 */
	private BigDecimal offlinePrice;

	/**
	 * 活动价格
	 */
	private BigDecimal actPrice;

	/**
	 * 单次购买上限(预留)
	 */
	private Integer tradeMax;

	/**
	 * 库存 
	 */
	private Integer sellable;

	/**
	 * 锁定库存（活动库存）
	 */
	private Integer locked;

	/**
	 * 活动商品限款数量
	 */
	private Integer limitKind;

	/**
	 * 活动限购数量
	 */
	private Integer limitBuyNum;

	/**
	 * 活动类型(0:没有参加活动，5：特惠，7：低价抢购）
	 */
	private int activityType;

	private String activityId;

	/**
	 * 是否上架
	 */
	private BSSC online;

	/**
	 * 购买数量
	 */
	private int quantity;

	/**
	 * 购买低价商品数量
	 */
	private int skuActQuantity;

	/**
	 * 商品类型
	 */
	private SpuTypeEnum spuType;

	/**
	 * 单位
	 */
	private String unit;
	
	/**
	 * 货号
	 */
	private String articleNo;

	/******************************服务店商品需要返回的信息**********************************************/
	private int saleNum;

	/**
	 * 商品支付方式
	 */
	private int paymentMode;

	/**
	 * 服务商品起购量
	 */
	private Integer shopNum;

	/**
	 * 商品最后更新时间
	 */
	private String updateTime;

	/**
	 * 到店消费商品有效截止时间
	 */
	private Date endTime;

	public BigDecimal getTotalAmount() {
		BigDecimal totalAmount = BigDecimal.valueOf(0);
		totalAmount = totalAmount.add(onlinePrice.multiply(BigDecimal.valueOf(quantity)));
		return totalAmount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	public String getMultipleSkuId() {
		return multipleSkuId;
	}

	public void setMultipleSkuId(String multipleSkuId) {
		this.multipleSkuId = multipleSkuId;
	}

	public String getPropertiesIndb() {
		if (!StringUtils.isEmpty(this.propertiesIndb)) {
			JSONObject propertiesJson = JSONObject.fromObject(this.propertiesIndb);
			String skuProperties = propertiesJson.get("skuName").toString();
			return skuProperties;
		} else {
			return "";
		}
	}

	public void setPropertiesIndb(String propertiesIndb) {
		this.propertiesIndb = propertiesIndb;
	}

	public BigDecimal getOnlinePrice() {
		return onlinePrice;
	}

	public void setOnlinePrice(BigDecimal onlinePrice) {
		this.onlinePrice = onlinePrice;
	}

	public BigDecimal getActPrice() {
		return actPrice;
	}

	public void setActPrice(BigDecimal actPrice) {
		this.actPrice = actPrice;
	}

	public Integer getTradeMax() {
		return tradeMax;
	}

	public void setTradeMax(Integer tradeMax) {
		this.tradeMax = tradeMax;
	}

	public Integer getSellable() {
		return sellable;
	}

	public void setSellable(Integer sellable) {
		this.sellable = sellable;
	}

	public Integer getLocked() {
		return locked;
	}

	public void setLocked(Integer locked) {
		this.locked = locked;
	}

	public Integer getLimitKind() {
		return limitKind;
	}

	public void setLimitKind(Integer limitKind) {
		this.limitKind = limitKind;
	}

	public int getActivityType() {
		return activityType;
	}

	public void setActivityType(int activityType) {
		this.activityType = activityType;
	}

	public BSSC getOnline() {
		return online;
	}

	public void setOnline(BSSC online) {
		this.online = online;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getSkuActQuantity() {
		return skuActQuantity;
	}

	public void setSkuActQuantity(int skuActQuantity) {
		this.skuActQuantity = skuActQuantity;
	}

	public SpuTypeEnum getSpuType() {
		return spuType;
	}

	public void setSpuType(SpuTypeEnum spuType) {
		this.spuType = spuType;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getStoreSpuId() {
		return storeSpuId;
	}

	public void setStoreSpuId(String storeSpuId) {
		this.storeSpuId = storeSpuId;
	}

	public String getStyleCode() {
		return styleCode;
	}

	public void setStyleCode(String styleCode) {
		this.styleCode = styleCode;
	}

	public String getMainPicUrl() {
		return mainPicUrl;
	}

	public void setMainPicUrl(String mainPicUrl) {
		this.mainPicUrl = mainPicUrl;
	}

	public String getGuaranteed() {
		return guaranteed;
	}

	public void setGuaranteed(String guaranteed) {
		this.guaranteed = guaranteed;
	}

	public Integer getLimitBuyNum() {
		return limitBuyNum;
	}

	public void setLimitBuyNum(Integer limitBuyNum) {
		this.limitBuyNum = limitBuyNum;
	}

	public int getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(int paymentMode) {
		this.paymentMode = paymentMode;
	}

	public Integer getShopNum() {
		return shopNum;
	}

	public void setShopNum(Integer shopNum) {
		this.shopNum = shopNum;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getSaleNum() {
		return saleNum;
	}

	public void setSaleNum(int saleNum) {
		this.saleNum = saleNum;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getArticleNo() {
		return articleNo;
	}

	public void setArticleNo(String articleNo) {
		this.articleNo = articleNo;
	}

	public BigDecimal getOfflinePrice() {
		return offlinePrice;
	}

	public void setOfflinePrice(BigDecimal offlinePrice) {
		this.offlinePrice = offlinePrice;
	}

}
