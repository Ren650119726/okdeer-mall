package com.okdeer.mall.order.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * ClassName: TradeOrderItemBo
 * @Description: 订单项业务bo
 * @author tuzhd
 * @date 2017年5月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *   V2.4				2017-05-17			tuzhd			订单项业务bo 用于销量统计
 */
public class TradeOrderItemBo  implements Serializable {

	private static final long serialVersionUID = -7397122651369126862L;

	/**
     * 主键ID
     */
    private String id;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 店铺SPU ID
     */
    private String storeSpuId;

    /**
     * 店铺SKU ID
     */
    private String storeSkuId;
	/**
	 * 店铺id
	 */
	private String storeId;

	/**
     * sku名称
     */
    private String skuName;

    /**
     * SKU属性在数据库中的字符串表示
     */
    private String propertiesIndb;

    /**
     * 主图地址
     */
    private String mainPicPrl;

    /**
     * 商品类型：0:单品、1:服务商品、2:无条码...
     */
    private Integer spuType;

    /**
     * 下单时的sku价格
     */
    private BigDecimal unitPrice;

    /**
     * 购买时的 sku 数量
     */
    private Integer quantity;
    
	/**
	 * 购买时的 sku 重量,单位是千克
	 */
	private BigDecimal weight;

	/**
     * 订单项总额(理论上=单价*数量, 若有使用积分或现金券, 则分摊至单个订单项)
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
     * 订单项状态，0：无退货退款，1：部分退货退款，2：全部退货退款
     */
    private Integer status;

    /**
     * 评价，0：未评价，1：已评价
     */
    private Integer appraise;

    /**
     * 创建时间
     */
    private Date createTime;

	/** 规格单位 (商业系统对接增加字段) **/
	private String unit;

	
	public String getId() {
		return id;
	}

	
	public void setId(String id) {
		this.id = id;
	}

	
	public String getOrderId() {
		return orderId;
	}

	
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	
	public String getStoreSpuId() {
		return storeSpuId;
	}

	
	public void setStoreSpuId(String storeSpuId) {
		this.storeSpuId = storeSpuId;
	}

	
	public String getStoreSkuId() {
		return storeSkuId;
	}

	
	public void setStoreSkuId(String storeSkuId) {
		this.storeSkuId = storeSkuId;
	}

	
	public String getStoreId() {
		return storeId;
	}

	
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	
	public String getSkuName() {
		return skuName;
	}

	
	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	
	public String getPropertiesIndb() {
		return propertiesIndb;
	}

	
	public void setPropertiesIndb(String propertiesIndb) {
		this.propertiesIndb = propertiesIndb;
	}

	
	public String getMainPicPrl() {
		return mainPicPrl;
	}

	
	public void setMainPicPrl(String mainPicPrl) {
		this.mainPicPrl = mainPicPrl;
	}

	
	public Integer getSpuType() {
		return spuType;
	}

	
	public void setSpuType(Integer spuType) {
		this.spuType = spuType;
	}

	
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	
	public Integer getQuantity() {
		return quantity;
	}

	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	
	public BigDecimal getWeight() {
		return weight;
	}

	
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
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

	
	public Integer getStatus() {
		return status;
	}

	
	public void setStatus(Integer status) {
		this.status = status;
	}

	
	public Integer getAppraise() {
		return appraise;
	}

	
	public void setAppraise(Integer appraise) {
		this.appraise = appraise;
	}

	
	public Date getCreateTime() {
		return createTime;
	}

	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	
	public String getUnit() {
		return unit;
	}

	
	public void setUnit(String unit) {
		this.unit = unit;
	}

}
