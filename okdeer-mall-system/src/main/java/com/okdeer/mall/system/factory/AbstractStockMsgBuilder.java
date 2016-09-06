package com.okdeer.mall.system.factory;

import java.util.List;

import org.springframework.util.StringUtils;

import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ClassName: AbstractStockMsgBuilder 
 * @Description: 库存消息构造者
 * @author maojj
 * @date 2016年7月26日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-26			maojj			库存消息构造者
 */

public abstract class AbstractStockMsgBuilder {

	/**
	 * @Description: 重载构建发送给ERP的消息
	 * @param stockAdjustVo 库存调整对象
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public String buildMsg(StockAdjustVo stockAdjustVo) {
		return buildMsg(stockAdjustVo, null);
	}

	/**
	 * @Description: 重载构建发送给ERP的消息
	 * @param stockAdjustVo 库存调整对象
	 * @param operateType erp操作类型码
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public String buildMsg(StockAdjustVo stockAdjustVo, String operateType) {
		JSONObject msgJson = new JSONObject();
		msgJson.put("storeId", stockAdjustVo.getStoreId());
		msgJson.put("userId", stockAdjustVo.getUserId());
		putType(msgJson, operateType);
		putOrderId(msgJson, stockAdjustVo.getOrderId());
		msgJson.put(getDetailKey(), buildDetailList(stockAdjustVo.getAdjustDetailList()));

		return msgJson.toString();
	}

	/**
	 * @Description: 存放type属性，空方法，留给子类完成
	 * @param msgJson 消息JSON对象
	 * @param operateType erp操作类型码  
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public void putType(JSONObject msgJson, String operateType) {
	}

	/**
	 * @Description: 存放订单ID，空方法，留给子类完成
	 * @param msgJson 消息JSON对象
	 * @param orderId 订单ID  
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public void putOrderId(JSONObject msgJson, String orderId) {
	}

	/**
	 * @Description: 获取存放明细的键值，留给子类完成
	 * @return String  
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public abstract String getDetailKey();

	/**
	 * @Description: 构建明细列表
	 * @param adjustDetailList 库存调整明细
	 * @return JSON数组 
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public JSONArray buildDetailList(List<AdjustDetailVo> adjustDetailList) {
		JSONArray detailList = new JSONArray();
		JSONObject detail = null;
		for (AdjustDetailVo adjustDetail : adjustDetailList) {
			detail = new JSONObject();
			detail.put("storeSkuId", adjustDetail.getStoreSkuId());
			detail.put("goodsSkuId", clean(adjustDetail.getGoodsSkuId()));
			detail.put("goodsName", clean(adjustDetail.getGoodsName()));
			detail.put("barCode", clean(adjustDetail.getBarCode()));
			detail.put("styleCode", clean(adjustDetail.getStyleCode()));
			detail.put("isWeightSku", adjustDetail.getIsWeightSku());
			// put数量和价格
			detail.put(getNumKey(), adjustDetail.getNum());
			detail.put(getPriceKey(), adjustDetail.getPrice() == null ? 0 : adjustDetail.getPrice());
			detail.put("reasonDict", "");
			putPropertiesIndb(detail, adjustDetail.getPropertiesIndb());

			detailList.add(detail);
		}
		return detailList;
	}

	/**
	 * @Description: 清理字符串内容
	 * @param str 字符串
	 * @return 字符串
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public String clean(String str) {
		return str == null ? "" : str;
	}

	/**
	 * @Description: 获取存放数量的键
	 * @return 数量的键值 
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public abstract String getNumKey();

	/**
	 * @Description: 获取存放价格的键
	 * @return 价格键值  
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public abstract String getPriceKey();

	/**
	 * @Description: 解析商品属性内容
	 * @param detail 明细
	 * @param propertiesIndb 商品属性
	 * @author maojj
	 * @date 2016年7月26日
	 */
	public void putPropertiesIndb(JSONObject detail, String propertiesIndb) {
		String skuProperties = "";
		
		if (!StringUtils.isEmpty(propertiesIndb)) {
			try {
				JSONObject propertiesJson = JSONObject.fromObject(propertiesIndb);
				if (propertiesJson.get("skuName") != null) {
					skuProperties = propertiesJson.get("skuName").toString();
				}
			}catch(Exception e) {
				skuProperties = propertiesIndb;
			}
		}
		
		detail.put("propertiesIndb", skuProperties);
	}
}
