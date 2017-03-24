package com.okdeer.mall.order.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.assemble.GoodsStoreSkuAssembleApi;
import com.okdeer.archive.goods.assemble.dto.GoodsStoreAssembleDto;
import com.okdeer.archive.goods.assemble.dto.GoodsStoreSkuAssembleDto;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.jxc.stock.vo.StockOperaterTypeConst;
import com.okdeer.jxc.stock.vo.StockUpdateDetailVo;
import com.okdeer.jxc.stock.vo.StockUpdateVo;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.vo.TradeOrderContext;
import com.okdeer.mall.order.vo.TradeOrderGoodsItem;
import com.okdeer.mall.order.vo.TradeOrderReq;
import com.okdeer.mall.order.vo.TradeOrderReqDto;
import com.okdeer.mall.system.utils.ConvertUtil;

/**
 * ClassName: JxcStockUpdateBuilder 
 * @Description: 构建进销存库存更新请求对象
 * @author maojj
 * @date 2017年3月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.2 		2017年3月14日				maojj
 */
@Component
public class JxcStockUpdateBuilder {

	/**
	 * 销售单
	 */
	private static final String SALE_TYPE_A = "A";

	/**
	 * 退货单
	 */
	private static final String SALE_TYPE_B = "B";

	/**
	 * 商业管理系统的类型，固定写死XS
	 */
	private static final String ORDER_TYPE = "XS";

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuAssembleApi goodsStoreSkuAssembleApi;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	/**
	 * @Description: 构建下单时候的库存更新对象
	 * @param order
	 * @param parserBo
	 * @return   
	 * @author maojj
	 * @date 2017年3月14日
	 */
	public StockUpdateVo build(TradeOrder order, StoreSkuParserBo parserBo) throws Exception {
		if (order.getType() != OrderTypeEnum.PHYSICAL_ORDER) {
			// 只有实物订单才需要去商业系统做库存更新
			return null;
		}

		StockUpdateVo stockUpdateVo = new StockUpdateVo();
		// 机构ID
		stockUpdateVo.setBranchId(order.getStoreId());
		// 库存操作类型
		stockUpdateVo.setType(StockOperaterTypeConst.PLACE_SALE_ORDER);
		// 销售类型
		stockUpdateVo.setSaleType(SALE_TYPE_A);
		// 订单ID
		stockUpdateVo.setOrderId(order.getId());
		// 订单编号
		stockUpdateVo.setOrderNo(order.getOrderNo());
		// 问了刘玄，先固定写死XS
		stockUpdateVo.setOrderType(ORDER_TYPE);
		// 操作人
		stockUpdateVo.setOperateUserId(order.getUserId());

		List<StockUpdateDetailVo> detailList = buildDetailList(order, parserBo);

		stockUpdateVo.setDetails(detailList);
		return stockUpdateVo;
	}

	public List<StockUpdateDetailVo> buildDetailList(TradeOrder order, StoreSkuParserBo parserBo) throws Exception {
		if (CollectionUtils.isNotEmpty(parserBo.getComboSkuIdList())) {
			List<GoodsStoreAssembleDto> comboDtoList = goodsStoreSkuAssembleApi
					.findByAssembleSkuIds(parserBo.getComboSkuIdList());
			parserBo.loadComboSkuList(comboDtoList);
		}

		List<StockUpdateDetailVo> detailList = new ArrayList<StockUpdateDetailVo>();
		StockUpdateDetailVo detail = null;
		int rowNo = 1;
		for (CurrentStoreSkuBo storeSku : parserBo.getCurrentSkuMap().values()) {
			if (storeSku.getSpuType() == SpuTypeEnum.assembleSpu) {
				// 如果是组合商品，对商品进行拆分.商业系统只负责管理组合成分的库存扣减
				List<GoodsStoreSkuAssembleDto> comboDetailList = parserBo.getComboSkuMap().get(storeSku.getId());
				for (GoodsStoreSkuAssembleDto comboDetail : comboDetailList) {
					int buyNum = comboDetail.getQuantity() * storeSku.getQuantity();
					detail = buildDetail(comboDetail, rowNo++, buyNum);
					detailList.add(detail);
				}
			} else if (storeSku.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()) {
				// 如果是低价，需要将订单商品拆分为两条记录去发起库存变更请求
				if (storeSku.getSkuActQuantity() > 0) {
					detail = buildDetail(storeSku, ActivityTypeEnum.LOW_PRICE, rowNo++);
					detailList.add(detail);
				}
				if (storeSku.getQuantity() - storeSku.getSkuActQuantity() > 0) {
					storeSku.setQuantity(storeSku.getQuantity() - storeSku.getSkuActQuantity());
					detail = buildDetail(storeSku, ActivityTypeEnum.NO_ACTIVITY, rowNo++);
					detailList.add(detail);
				}
			} else if (storeSku.getActivityType() == ActivityTypeEnum.SALE_ACTIVITIES.ordinal()) {
				detail = buildDetail(storeSku, ActivityTypeEnum.SALE_ACTIVITIES, rowNo++);
				detailList.add(detail);
			} else {
				detail = buildDetail(storeSku, ActivityTypeEnum.NO_ACTIVITY, rowNo++);
				detailList.add(detail);
			}
		}
		return detailList;
	}

	private StockUpdateDetailVo buildDetail(CurrentStoreSkuBo storeSku, ActivityTypeEnum actType, int rowNo) {
		StockUpdateDetailVo detail = new StockUpdateDetailVo();

		detail.setSkuId(storeSku.getSkuId());
		detail.setSkuCode(storeSku.getArticleNo());
		detail.setRowNo(rowNo);
		if (actType == ActivityTypeEnum.LOW_PRICE) {
			detail.setNum(BigDecimal.valueOf(storeSku.getSkuActQuantity()));
			detail.setPrice(storeSku.getActPrice());
		} else {
			detail.setNum(BigDecimal.valueOf(storeSku.getQuantity()));
			detail.setPrice(storeSku.getOnlinePrice());
		}
		detail.setOnlineSalePrice(storeSku.getOnlinePrice());
		detail.setOfflineSalePrice(storeSku.getOfflinePrice());
		detail.setBranchSkuId(storeSku.getId());

		return detail;
	}

	private StockUpdateDetailVo buildDetail(GoodsStoreSkuAssembleDto comboDetail, int rowNo, int buyNum) {
		StockUpdateDetailVo detail = new StockUpdateDetailVo();

		detail.setSkuId(comboDetail.getSkuId());
		detail.setRowNo(rowNo);
		detail.setNum(BigDecimal.valueOf(buyNum));
		// 商品单价。商品价格
		detail.setPrice(comboDetail.getUnitPrice());
		detail.setOnlineSalePrice(comboDetail.getOnlinePrice());
		// 线下销售价是否必填
		// detail.setOfflineSalePrice(comboDetail.getOfflinePrice());
		detail.setBranchSkuId(comboDetail.getStoreSkuId());
		detail.setGroup(true);
		return detail;
	}

	public StockUpdateVo build(TradeOrder order) throws Exception {
		if (order.getType() != OrderTypeEnum.PHYSICAL_ORDER) {
			// 只有实物订单才需要去商业系统做库存更新
			return null;
		}

		StockUpdateVo stockUpdateVo = new StockUpdateVo();
		// 机构ID
		stockUpdateVo.setBranchId(order.getStoreId());
		// 库存操作类型
		stockUpdateVo.setType(convert(order.getStatus()));
		// 销售类型
		stockUpdateVo.setSaleType(SALE_TYPE_A);
		// 订单ID
		stockUpdateVo.setOrderId(order.getId());
		// 订单编号
		stockUpdateVo.setOrderNo(order.getOrderNo());
		// 问了刘玄，先固定写死XS
		stockUpdateVo.setOrderType(ORDER_TYPE);
		// 操作人
		stockUpdateVo.setOperateUserId(order.getUserId());

		List<StockUpdateDetailVo> detailList = buildDetailList(order.getTradeOrderItem());

		stockUpdateVo.setDetails(detailList);
		return stockUpdateVo;
	}

	public List<StockUpdateDetailVo> buildDetailList(List<TradeOrderItem> orderItemList) throws Exception {
		// 提取组合商品列表
		List<String> comboSkuIdList = extraComboSkuIdList(orderItemList);
		List<GoodsStoreAssembleDto> comboDtoList = null;
		if (CollectionUtils.isNotEmpty(comboSkuIdList)) {
			comboDtoList = goodsStoreSkuAssembleApi.findByAssembleSkuIds(comboSkuIdList);
		}

		List<StockUpdateDetailVo> detailList = new ArrayList<StockUpdateDetailVo>();
		StockUpdateDetailVo detail = null;
		int rowNo = 1;
		for (TradeOrderItem orderItem : orderItemList) {
			if (orderItem.getSpuType() == SpuTypeEnum.assembleSpu) {
				// 如果是组合商品，对商品进行拆分.商业系统只负责管理组合成分的库存扣减
				List<GoodsStoreSkuAssembleDto> comboDetailList = extraComboDetailList(comboDtoList,
						orderItem.getStoreSkuId());
				for (GoodsStoreSkuAssembleDto comboDetail : comboDetailList) {
					int buyNum = comboDetail.getQuantity() * orderItem.getQuantity();
					detail = buildDetail(comboDetail, rowNo++, buyNum);
					detailList.add(detail);
				}
			} else if (Integer.valueOf(ActivityTypeEnum.LOW_PRICE.ordinal()).equals(orderItem.getActivityType())) {
				// 如果是低价，需要将订单商品拆分为两条记录去发起库存变更请求
				if (orderItem.getActivityQuantity() > 0) {
					detail = buildDetail(orderItem, ActivityTypeEnum.LOW_PRICE, rowNo++);
					detailList.add(detail);
				}
				if (orderItem.getQuantity() - orderItem.getActivityQuantity() > 0) {
					detail = buildDetail(orderItem, ActivityTypeEnum.NO_ACTIVITY, rowNo++);
					detailList.add(detail);
				}
			} else {
				detail = buildDetail(orderItem, ActivityTypeEnum.NO_ACTIVITY, rowNo++);
				detailList.add(detail);
			}
		}
		return detailList;
	}

	private List<String> extraComboSkuIdList(List<TradeOrderItem> orderItemList) {
		List<String> comboSkuIdList = new ArrayList<String>();
		for (TradeOrderItem orderItem : orderItemList) {
			if (orderItem.getSpuType() == SpuTypeEnum.assembleSpu) {
				comboSkuIdList.add(orderItem.getStoreSkuId());
			}
		}
		return comboSkuIdList;
	}

	private List<GoodsStoreSkuAssembleDto> extraComboDetailList(List<GoodsStoreAssembleDto> comboDtoList,
			String storeSkuId) {
		List<GoodsStoreSkuAssembleDto> detailList = new ArrayList<GoodsStoreSkuAssembleDto>();
		for (GoodsStoreAssembleDto comboDto : comboDtoList) {
			for (GoodsStoreSkuAssembleDto comboDetail : comboDto.getGoodsStoreSkuAssembleDtos()) {
				if (comboDetail.getAssembleSkuId().equals(storeSkuId)) {
					detailList.add(comboDetail);
				}
			}
		}
		return detailList;
	}

	private StockUpdateDetailVo buildDetail(TradeOrderItem orderItem, ActivityTypeEnum actType, int rowNo)
			throws Exception {
		StockUpdateDetailVo detail = new StockUpdateDetailVo();
		GoodsStoreSku storeSku = goodsStoreSkuServiceApi.getById(orderItem.getStoreSkuId());
		detail.setSkuId(storeSku.getSkuId());
		detail.setSkuCode(storeSku.getArticleNo());
		detail.setRowNo(rowNo);
		if (actType == ActivityTypeEnum.LOW_PRICE) {
			detail.setNum(BigDecimal.valueOf(orderItem.getActivityQuantity()));
			detail.setPrice(orderItem.getActivityPrice());
		} else {
			detail.setNum(
					BigDecimal.valueOf(orderItem.getQuantity() - ConvertUtil.format(orderItem.getActivityQuantity())));
			detail.setPrice(storeSku.getOnlinePrice());
		}
		detail.setOnlineSalePrice(storeSku.getOnlinePrice());
		detail.setOfflineSalePrice(storeSku.getOfflinePrice());
		detail.setBranchSkuId(storeSku.getId());

		return detail;
	}

	private String convert(OrderStatusEnum orderStatus) {
		if (orderStatus == OrderStatusEnum.CANCELING || orderStatus == OrderStatusEnum.CANCELED) {
			return StockOperaterTypeConst.CANCEL_SALE_ORDER;
		} else {
			return StockOperaterTypeConst.REFUSE_SALE_ODRER;
		}
	}
	
	
	public StockUpdateVo build(TradeOrderRefunds orderRefunds,List<TradeOrderItem> orderItemList) throws Exception {
		if (orderRefunds.getType() != OrderTypeEnum.PHYSICAL_ORDER) {
			// 只有实物订单才需要去商业系统做库存更新
			return null;
		}

		StockUpdateVo stockUpdateVo = new StockUpdateVo();
		// 机构ID
		stockUpdateVo.setBranchId(orderRefunds.getStoreId());
		// 库存操作类型
		stockUpdateVo.setType(StockOperaterTypeConst.RETURN_SALE_ORDER);
		// 销售类型
		stockUpdateVo.setSaleType(SALE_TYPE_B);
		// 订单ID
		stockUpdateVo.setOrderId(orderRefunds.getId());
		// 订单编号
		stockUpdateVo.setOrderNo(orderRefunds.getRefundNo());
		// 问了刘玄，先固定写死XS
		stockUpdateVo.setOrderType(ORDER_TYPE);
		// 操作人
		stockUpdateVo.setOperateUserId(orderRefunds.getUserId());
		
		List<StockUpdateDetailVo> detailList = buildDetailList(orderItemList);
		stockUpdateVo.setDetails(detailList);
		return stockUpdateVo;
	}
	
	public StockUpdateVo buildForCompleteOrder(TradeOrder order) throws Exception {
		if (order.getType() != OrderTypeEnum.PHYSICAL_ORDER) {
			// 只有实物订单才需要去商业系统做库存更新
			return null;
		}

		StockUpdateVo stockUpdateVo = new StockUpdateVo();
		// 机构ID
		stockUpdateVo.setBranchId(order.getStoreId());
		// 库存操作类型
		stockUpdateVo.setType(StockOperaterTypeConst.DELIVER_SALE_ORDER);
		// 销售类型
		stockUpdateVo.setSaleType(SALE_TYPE_A);
		// 订单ID
		stockUpdateVo.setOrderId(order.getId());
		// 订单编号
		stockUpdateVo.setOrderNo(order.getOrderNo());
		// 问了刘玄，先固定写死XS
		stockUpdateVo.setOrderType(ORDER_TYPE);
		// 操作人
		stockUpdateVo.setOperateUserId(order.getUserId());

		List<StockUpdateDetailVo> detailList = buildDetailList(order.getTradeOrderItem());

		stockUpdateVo.setDetails(detailList);
		return stockUpdateVo;
	}
	
	/**
	 * @Description: V2.1版本之前的便利店下单库存变更
	 * @param order
	 * @param reqDto
	 * @return
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年3月18日
	 */
	public StockUpdateVo build(TradeOrder order, TradeOrderReqDto reqDto) throws Exception {
		StockUpdateVo stockUpdateVo = new StockUpdateVo();
		// 机构ID
		stockUpdateVo.setBranchId(order.getStoreId());
		// 库存操作类型
		stockUpdateVo.setType(StockOperaterTypeConst.PLACE_SALE_ORDER);
		// 销售类型
		stockUpdateVo.setSaleType(SALE_TYPE_A);
		// 订单ID
		stockUpdateVo.setOrderId(order.getId());
		// 订单编号
		stockUpdateVo.setOrderNo(order.getOrderNo());
		// 问了刘玄，先固定写死XS
		stockUpdateVo.setOrderType(ORDER_TYPE);
		// 操作人
		stockUpdateVo.setOperateUserId(order.getUserId());

		List<StockUpdateDetailVo> detailList = buildDetailList(order,reqDto);

		stockUpdateVo.setDetails(detailList);
		return stockUpdateVo;
	}
	
	public List<StockUpdateDetailVo> buildDetailList(TradeOrder order, TradeOrderReqDto reqDto){
		TradeOrderReq req = reqDto.getData();
		TradeOrderContext context = reqDto.getContext();
		
		List<StockUpdateDetailVo> detailList = new ArrayList<StockUpdateDetailVo>();
		StockUpdateDetailVo detail = null;
		
		List<GoodsStoreSku> storeSkuList = new ArrayList<GoodsStoreSku>();
		if(CollectionUtils.isNotEmpty(context.getNomalSkuList())){
			storeSkuList.addAll(context.getNomalSkuList());
		}
		if(CollectionUtils.isNotEmpty(context.getActivitySkuList())){
			storeSkuList.addAll(context.getActivitySkuList());
		}
		
		int rowNo = 1;
		for(GoodsStoreSku storeSku : storeSkuList){
			detail = new StockUpdateDetailVo();
			TradeOrderGoodsItem orderItem = req.findOrderItem(storeSku.getId());
			
			detail.setSkuId(storeSku.getSkuId());
			detail.setSkuCode(storeSku.getArticleNo());
			detail.setRowNo(rowNo++);
			detail.setNum(BigDecimal.valueOf(orderItem.getSkuNum()));
			detail.setPrice(orderItem.getSkuPrice());
			detail.setOnlineSalePrice(storeSku.getOnlinePrice());
			detail.setOfflineSalePrice(storeSku.getOfflinePrice());
			detail.setBranchSkuId(storeSku.getId());
			
			detailList.add(detail);
		}
		return detailList;
	}
}
