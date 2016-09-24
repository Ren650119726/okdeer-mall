package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.enums.BSSC;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.store.enums.StoreActivityTypeEnum;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleGoodsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleMapper;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.order.constant.OrderTipMsgConstant;
import com.okdeer.mall.order.enums.OrderOptTypeEnum;
import com.okdeer.mall.order.service.GoodsCheckService;
import com.okdeer.mall.order.utils.CodeStatistical;
import com.okdeer.mall.order.vo.TradeOrderContext;
import com.okdeer.mall.order.vo.TradeOrderGoodsItem;
import com.okdeer.mall.order.vo.TradeOrderReqDto;
import com.okdeer.mall.order.vo.TradeOrderResp;
import com.okdeer.mall.order.vo.TradeOrderRespDto;
import com.yschome.base.common.exception.ServiceException;

/**
 * ClassName: GoodsCheckServiceImpl 
 * @Description: 检查商品信息是否发生变化
 * @author maojj
 * @date 2016年7月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-14			maojj			检查商品信息是否发生变化
 *		Bug:12572	    2016-08-10		 	maojj			添加结算校验失败的提示语
 *		Bug:12566		2016-08-10		 	maojj			获取当前商品线上价格时，添加价格是否为空的判断
 *		重构V4.1			2016-08-17			maojj			比较时间时，先判断时间转换是否成功，增强程序的健壮性，预防空指针异常
 *		V1.1.0			2016-09-23			tangy			添加商品类目ids
 */
@Service
public class GoodsCheckServiceImpl implements GoodsCheckService {

	private static final Logger logger = LoggerFactory.getLogger(GoodsCheckServiceImpl.class);

	/**
	 * 店铺商品Api
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	/**
	 * 特惠活动记录Mapper
	 */
	@Resource
	private ActivitySaleMapper activitySaleMapper;

	/**
	 * 特惠商品Mapper
	 */
	@Resource
	private ActivitySaleGoodsMapper activitySaleGoodsMapper;

	/**
	 * 校验商品信息是否发生变化
	 */
	@Override
	public void process(TradeOrderReqDto reqDto, TradeOrderRespDto respDto) throws Exception {
		TradeOrderResp resp = respDto.getResp();
		TradeOrderContext context = reqDto.getContext();
		List<TradeOrderGoodsItem> itemList = reqDto.getData().getList();

		// 查询当前店铺商品库信息
		List<GoodsStoreSku> currentStoreSkuList = getCurrentStoreSkuList(itemList);
		// 上下文对象中保存当前商品信息
		context.setCurrentStoreSkuList(currentStoreSkuList);
		// 解析当前店铺商品库，将当前商品库列表，拆分为特惠商品和正常商品
		parseCurrentStoreSkuList(currentStoreSkuList, context);

		if (context.isExistsActivityGoods()) {
			// 如果存在特惠商品，查询特惠商品记录，并存储特惠商品价格
			processActivityGoods(context);
		}
		// 判断商品信息是否发生变化
		ReturnInfo info = compareGoods(itemList, context, reqDto.getOrderOptType());
		if (!info.isFlag()) {
			// 商品信息发生变化
			respDto.setFlag(false);
			respDto.setMessage(info.getMessage());
			resp.setIsChanges(0);
			resp.setCurrentGoodsAndStock(context);
			return;
		}
	}

	/**
	 * @Description: 查询当前店铺商品库信息并存入上下文
	 * @param itemList 订单购买商品明细
	 * @return List 当前店铺商品信息
	 * @throws ServiceException 服务异常
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private List<GoodsStoreSku> getCurrentStoreSkuList(List<TradeOrderGoodsItem> itemList) throws ServiceException {
		// 提取商品skuId
		List<String> skuIdList = extractSkuId(itemList);
		// 当前店铺商品信息
		List<GoodsStoreSku> currentStoreSkuList = goodsStoreSkuServiceApi.findStoreSkuForOrder(skuIdList);
		if (currentStoreSkuList.size() != itemList.size()) {
			logger.info("店铺商品库中查询商品记录与请求商品记录不匹配{}", CodeStatistical.getLineInfo());
			throw new ServiceException("店铺商品库中查询商品记录与请求商品记录不匹配" + CodeStatistical.getLineInfo());
		}
		return currentStoreSkuList;
	}

	/**
	 * @Description: 提取商品列表的skuId
	 * @param itemList 订单请求的商品列表
	 * @return List 商品SkuId列表  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private List<String> extractSkuId(List<TradeOrderGoodsItem> itemList) {
		List<String> skuIdList = new ArrayList<String>();
		for (TradeOrderGoodsItem item : itemList) {
			skuIdList.add(item.getSkuId());
		}
		return skuIdList;
	}

	/**
	 * @Description: 解析当前店铺商品库，将商品库列表拆分为特惠商品库和正常商品库，并存储正常商品价格
	 * @param storeSkuList 当前店铺商品列表
	 * @param context 封装的请求上下文对象，存储一些后续流程需要使用的数据信息
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void parseCurrentStoreSkuList(List<GoodsStoreSku> storeSkuList, TradeOrderContext context)
			throws ServiceException {
		// 特惠商品列表
		List<GoodsStoreSku> activitySkuList = new ArrayList<GoodsStoreSku>();
		// 正常商品列表
		List<GoodsStoreSku> nomalSkuList = new ArrayList<GoodsStoreSku>();
		// 特惠商品ID列表
		List<String> activitySkuIds = new ArrayList<String>();
		// 从商品列表中提取商品所参加的活动ID
		String activityId = extractActivityId(storeSkuList);
		// 特惠活动信息
		ActivitySale acSale = null;
		//Begin added by tangy  2016-9-23
		// 商品类目id
		List<String> spuCategoryIds = new ArrayList<String>();
		//End added by tangy

		if (activityId != null) {
			acSale = activitySaleMapper.getAcSaleStatus(activityId);
		}
		// 将商品分为特惠商品和正常商品
		for (GoodsStoreSku storeSku : storeSkuList) {
			spuCategoryIds.add(storeSku.getSpuCategoryId());
			if (storeSku.getActivityType() == StoreActivityTypeEnum.PRIVLIEGE) {
				// 特惠活动状态(0:未开始,1:进行中,2:已结束,3:已关闭)
				if (acSale.getStatus() == 1) {
					activitySkuIds.add(storeSku.getId());
					activitySkuList.add(storeSku);
				} else {
					context.putCurrentPrice(storeSku.getId(), cleanPrice(storeSku.getOnlinePrice()));
					nomalSkuList.add(storeSku);
				}
			} else {
				context.putCurrentPrice(storeSku.getId(), cleanPrice(storeSku.getOnlinePrice()));
				nomalSkuList.add(storeSku);
			}
		}
		// 保存特惠商品ID列表
		context.setActivitySkuIds(activitySkuIds);
		// 保存特惠活动信息
		context.setActivitySale(acSale);
		// 保存正常商品列表
		context.setNomalSkuList(nomalSkuList);
		// 保存特惠商品列表
		context.setActivitySkuList(activitySkuList);
		// 保存商品类目id集
		context.setSpuCategoryIds(spuCategoryIds);
	}

	/**
	 * @Description: 提取特惠活动ID
	 * @param storeSkuList 当前商品列表
	 * @return String
	 * @throws ServiceException 服务异常
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private String extractActivityId(List<GoodsStoreSku> storeSkuList) throws ServiceException {
		String activityId = null;
		List<String> activityIds = new ArrayList<String>();
		for (GoodsStoreSku storeSku : storeSkuList) {
			if (storeSku.getActivityType() == StoreActivityTypeEnum.PRIVLIEGE) {
				activityId = storeSku.getActivityId();
				if (!activityIds.contains(activityId)) {
					activityIds.add(activityId);
				}
			}
		}
		if (activityIds.size() > 1) {
			logger.error("订单中存在参与{}种活动的商品", activityIds.size());
			throw new ServiceException("订单中存在参与" + activityIds.size() + "种活动的商品");
		}
		if (activityIds.size() == 1) {
			activityId = activityIds.get(0);
		}
		return activityId;
	}

	/**
	 * @Description: 比对请求的商品和当前数据库商品信息，是否发生变化
	 * @param itemList 订单请求的商品列表
	 * @param context 订单请求的上下文对象
	 * @return ReturnInfo  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private ReturnInfo compareGoods(List<TradeOrderGoodsItem> itemList, TradeOrderContext context,
			OrderOptTypeEnum orderOptType) {
		ReturnInfo info = null;
		for (TradeOrderGoodsItem item : itemList) {
			// 检查商品信息是否发生变化
			info = isChange(item, context, orderOptType);
			if (!info.isFlag()) {
				break;
			} else {
				item.setSkuPrice(context.getCurrentPrice(item.getSkuId()));
			}
		}
		return info;
	}

	/**
	 * @Description: 处理特惠商品
	 * @param context 订单处理缓存信息 
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void processActivityGoods(TradeOrderContext context) {
		String activityId = context.getActivityId();
		List<String> activitySkuIds = context.getActivitySkuIds();
		// 查询特惠商品列表
		List<ActivitySaleGoods> activitySaleGoodsList = activitySaleGoodsMapper.findActivityGoodsList(activityId,
				activitySkuIds);
		context.setActivitySaleGoodsList(activitySaleGoodsList);
		// 获取特惠商品价格存储到请求上下文中
		for (ActivitySaleGoods activityGoods : activitySaleGoodsList) {
			context.putCurrentPrice(activityGoods.getStoreSkuId(), activityGoods.getSalePrice());
		}
	}

	// Begin modified by maojj 2016-08-10 Bug:12572
	/**
	 * @Description: 检查商品是否发生变化
	 * @param item 订单请求的商品列表
	 * @param context 订单请求的上下文对象
	 * @return ReturnInfo  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private ReturnInfo isChange(TradeOrderGoodsItem item, TradeOrderContext context, OrderOptTypeEnum orderOptType) {
		ReturnInfo info = new ReturnInfo();
		info.setFlag(true);
		// 获取当前数据库中的商品信息
		GoodsStoreSku storeSku = context.getStoreSku(item);
		if (storeSku == null) {
			info.setFlag(false);
			info.setMessage(OrderTipMsgConstant.GOODS_IS_CHANGE);
		} else if (!compareUpdateTime(item.getUpdateTime(), storeSku.getUpdateTime())) {
			info.setFlag(false);
			if (storeSku.getOnline() == BSSC.UNSHELVE) {
				// 商品下架
				if (orderOptType == OrderOptTypeEnum.ORDER_SETTLEMENT) {
					info.setMessage(OrderTipMsgConstant.GOODS_IS_OFFLINE_SETTLEMENT);
				} else {
					info.setMessage(OrderTipMsgConstant.GOODS_IS_OFFLINE);
				}
			} else if (storeSku.getOnlinePrice().compareTo(item.getSkuPrice()) != 0) {
				// 商品价格发生变化
				if (orderOptType == OrderOptTypeEnum.ORDER_SETTLEMENT) {
					info.setMessage(OrderTipMsgConstant.GOODS_PRICE_CHANGE_SETTLEMENT);
				} else {
					info.setMessage(OrderTipMsgConstant.GOODS_PRICE_CHANGE);
				}
			} else {
				info.setMessage(OrderTipMsgConstant.GOODS_IS_CHANGE);
			}
		}
		return info;
	}
	// End modified by maojj 2016-08-10

	/**
	 * @Description: 比较更新时间是否发生变化
	 * @param oldTimeStr 订单请求的商品更新时间
	 * @param newDate 当前数据库中的商品更新时间
	 * @return boolean  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private boolean compareUpdateTime(String oldTimeStr, Date newDate) {
		Date oldDate = DateUtils.parseDate(oldTimeStr);
		if (oldDate == null) {
			return false;
		}
		long oldTime = oldDate.getTime();
		long newTime = newDate.getTime();
		return oldTime == newTime;
	}

	// Begin added by maojj 2016-08-11 Bug:12566
	/**
	 * @Description: 价格为空的处理
	 * @param price 处理的价格
	 * @author maojj
	 * @date 2016年8月11日
	 */
	public BigDecimal cleanPrice(BigDecimal price) {
		return price == null ? new BigDecimal(0.0) : price;
	}
	// End added by maojj 2016-08-11
}

class ReturnInfo {

	/**
	 * 处理标识
	 */
	private boolean flag;

	/**
	 * 返回信息
	 */
	private String message;

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
