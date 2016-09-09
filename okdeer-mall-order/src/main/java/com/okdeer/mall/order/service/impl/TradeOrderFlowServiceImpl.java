/**   
* @Title: TradeOrderFlowServiceImpl.java 
* @Package com.okdeer.mall.trade.order.service.impl 
* @Description: 订单下单逻辑处理类 </p>
* @author A18ccms A18ccms_gmail_com   
* @date 2016年4月14日 上午9:56:39 
* @version V1.0   
*/

package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.goods.base.enums.GoodsTypeEnum;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuStockServiceApi;
import com.okdeer.archive.goods.store.vo.GoodsStoreSkuDetailVo;
import com.okdeer.archive.goods.store.vo.GoodsStoreSkuToAppVo;
import com.okdeer.archive.stock.entity.ImsDaily;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.exception.StockException;
import com.okdeer.archive.stock.service.ImsDailyServiceApi;
import com.okdeer.archive.stock.service.StockManagerJxcServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.archive.store.entity.StoreBranches;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.StoreBranchesServiceApi;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.common.consts.LogConstants;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleRecord;
import com.okdeer.mall.activity.coupons.entity.CouponsFindVo;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleRecordMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsService;
import com.okdeer.mall.activity.coupons.service.ActivitySaleGoodsService;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRecordService;
import com.okdeer.mall.activity.coupons.service.ActivitySaleService;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountCondition;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountRecord;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountType;
import com.okdeer.mall.activity.discount.service.ActivityDiscountRecordService;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.activity.group.entity.ActivityGroup;
import com.okdeer.mall.activity.group.entity.ActivityGroupGoods;
import com.okdeer.mall.activity.group.entity.ActivityGroupRecord;
import com.okdeer.mall.activity.group.service.ActivityGroupGoodsService;
import com.okdeer.mall.activity.group.service.ActivityGroupRecordService;
import com.okdeer.mall.activity.group.service.ActivityGroupService;
import com.okdeer.mall.common.utils.RandomStringUtil;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.service.MemberConsigneeAddressService;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderInvoice;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.enums.AppraiseEnum;
import com.okdeer.mall.order.enums.CompainStatusEnum;
import com.okdeer.mall.order.enums.OrderIsShowEnum;
import com.okdeer.mall.order.enums.OrderItemStatusEnum;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.PaymentStatusEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.enums.WithInvoiceEnum;
import com.okdeer.mall.order.mapper.GenerateNumericalMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.service.GenerateNumericalService;
import com.okdeer.mall.order.service.TradeOrderFlowService;
import com.okdeer.mall.order.service.TradeOrderFlowServiceApi;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.timer.TradeOrderTimer;
import com.okdeer.mall.order.utils.CalculateOrderStock;
import com.okdeer.mall.order.utils.CodeStatistical;
import com.okdeer.mall.order.utils.DiscountCalculate;
import com.okdeer.mall.order.utils.OrderItem;
import com.okdeer.mall.order.utils.OrderNoUtils;
import com.okdeer.mall.order.utils.OrderStock;
import com.okdeer.mall.order.utils.OrderUtils;
import com.okdeer.mall.order.utils.TradeOrderStock;
import com.okdeer.mall.order.vo.CalCulateOrderAmountInParam;
import com.okdeer.mall.order.vo.CalculateMoneyUtil;
import com.okdeer.mall.order.vo.CalculateOrderVo;
import com.okdeer.mall.order.vo.CalculateOrderVoInParam;
import com.okdeer.mall.order.vo.CalculatePrivilege;
import com.okdeer.mall.order.vo.CalculatePrivilegeInParam;
import com.okdeer.mall.order.vo.CalculateTradeOrderItem;
import com.okdeer.mall.order.vo.NewGoodsListToAppVo;
import com.okdeer.mall.order.vo.SaveOrderInfo;
import com.okdeer.mall.order.vo.SaveTradeOrderInfo;
import com.okdeer.mall.order.vo.SaveTradeOrderInfoInParam;
import com.okdeer.mall.system.mapper.SysBuyerUserMapper;
import com.okdeer.mall.system.mq.StockMQProducer;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.util.Auth;
import com.yschome.base.common.enums.Disabled;
import com.yschome.base.common.exception.ServiceException;
import com.yschome.base.common.utils.UuidUtils;
import com.yschome.file.FileUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @ClassName: TradeOrderFlowServiceImpl
 * @Description: 订单业务操作类
 *               </p>
 * @author yangq
 * @date 2016年4月14日 上午9:56:39
 * 
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构4.1          2016年7月13日                               zengj			   新增服务店订单确认订单和下单方法
 *     重构4.1          2016年8月26日                               maojj			 POS下单重复发消息。解决重复发消息的问题。
 *     1.0.Z	          2016年9月07日                 zengj              库存管理修改，采用商业管理系统校验
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderFlowServiceApi")
public class TradeOrderFlowServiceImpl implements TradeOrderFlowService, TradeOrderFlowServiceApi {

	private static final Logger logger = LoggerFactory.getLogger(TradeOrderFlowServiceImpl.class);

	@Reference(version = "1.0.0", check = false)
	private com.yschome.api.pay.service.IScanPayServiceApi scanPayServiceApi;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuService;

	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;

	@Value("${storeImagePrefix}")
	private String storeImagePrefix;

	@Value("${orderImagePrefix}")
	private String orderImagePrefix;

	@Value("${accessKey}")
	private String accessKey;

	@Value("${secretKey}")
	private String secretKey;

	@Value("${storeUploadToken}")
	private String storeToken;

	@Value("${goodsUploadToken}")
	private String goodsToken;

	@Value("${orderUploadToken}")
	private String orderToken;

	@Autowired
	private TradeOrderTimer tradeOrderTimer;

	@Reference(version = "1.0.0", check = false)
	private ImsDailyServiceApi imsDailyService;

	@Resource
	private ActivityCouponsService activityCouponsService;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuStockServiceApi goodsStoreSkuStockService;

	@Resource
	private TradeOrderService tradeOrderService;

	@Resource
	private ActivityCouponsRecordService activityCouponsRecordService;

	@Resource
	private ActivityDiscountService activityDiscountService;

	@Resource
	private ActivityDiscountRecordService activityDiscountRecordService;

	@Resource
	private GenerateNumericalService generateNumericalService;

	@Resource
	private MemberConsigneeAddressService memberConsigneeAddressService;

	@Resource
	private ActivityGroupService activityGroupService;

	@Resource
	private ActivityGroupGoodsService activityGroupGoodsService;

	@Resource
	private GenerateNumericalMapper generateNumericalMapper;

	// @Reference(version = "1.0.0", check = false)
	// private StockManagerServiceApi stockManagerService;

	// Begin 1.0.Z add by zengj
	/**
	 * 库存管理Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StockManagerJxcServiceApi stockManagerService;

	/**
	 * 机构Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreBranchesServiceApi storeBranchesService;
	// End 1.0.Z add by zengj

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceServiceApi goodsStoreSkuServiceService;

	@Resource
	private SysBuyerUserMapper sysBuyerUserMapper;

	@Resource
	private ActivitySaleGoodsService activitySaleGoodsService;

	@Resource
	private ActivitySaleService activitySaleService;

	@Resource
	private TradeOrderMapper tradeOrderMapper;

	@Resource
	private ActivitySaleRecordService activitySaleRecordService;

	@Resource
	private ActivityGroupRecordService activityGroupRecordService;

	@Resource
	private ActivitySaleRecordMapper activitySaleRecordMapper;

	/**
	 * 库存MQ信息
	 */
	@Autowired
	StockMQProducer stockMQProducer;

	/**
	 * 查询商品库存
	 * </p>
	 * 
	 * @param yunGoods
	 *            便利店正常商品ID集合
	 * @param saleGoods
	 *            特惠活动商品ID集合
	 * @return
	 * @throws Exception 
	 */
	private List<Object> getStockList(List<String> yunGoods, List<String> saleGoods) throws Exception {

		List<Object> objList = new ArrayList<Object>(); // 商品库存对象集合
		List<GoodsStoreSku> yunStock = new ArrayList<GoodsStoreSku>(); // 便利店正常商品库存集合
		List<GoodsStoreSku> saleStock = new ArrayList<GoodsStoreSku>(); // 特惠专区商品库存集合

		if (yunGoods.size() != 0) {
			yunStock = goodsStoreSkuService.getGoodsStoreSkuSelleabed(yunGoods); // 查询商品库存信息
		}
		if (saleGoods.size() != 0) {
			saleStock = goodsStoreSkuService.getGoodsStoreSkuSelleabed(saleGoods); // 查询商品库存信息
		}

		// 正常商品库存集合
		for (int i = 0; i < yunStock.size(); i++) {
			TradeOrderStock goodsStock = new TradeOrderStock();
			GoodsStoreSku storeSku = yunStock.get(i);
			String skuId = storeSku.getId(); // 商品ID
			int sellabed = storeSku.getGoodsStoreSkuStock().getSellable(); // 商品可销售库存
			goodsStock.setSkuId(skuId);
			goodsStock.setSellableStock(sellabed);
			objList.add(goodsStock);
		}
		// 特惠商品库存集合
		for (int j = 0; j < saleStock.size(); j++) {
			TradeOrderStock goodsStock = new TradeOrderStock();
			GoodsStoreSku storeSku = saleStock.get(j);
			String skuId = storeSku.getId(); // 商品ID
			int locked = storeSku.getGoodsStoreSkuStock().getLocked(); // 商品锁定库存
			goodsStock.setSkuId(skuId);
			goodsStock.setSellableStock(locked);
			objList.add(goodsStock);
		}
		return objList;

	}

	/**
	 * 获取后台返回的商品列表
	 * </p>
	 * 
	 * @param array
	 * @return
	 * @throws Exception
	 */
	private NewGoodsListToAppVo getNewGoodsList(JSONArray array) throws Exception {
		NewGoodsListToAppVo toAppVo = new NewGoodsListToAppVo();
		List<GoodsStoreSkuToAppVo> skuToAppVoList = new ArrayList<GoodsStoreSkuToAppVo>();
		List<String> yunGoods = new ArrayList<String>(); // 便利店商品ID集合
		List<String> saleGoods = new ArrayList<String>(); // 特惠商品ID集合
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			String skuId = obj.getString("skuId");
			String isPrivilege = obj.getString("isPrivilege"); // 是否是正常商品标识(1:特惠活动商品
																// 0:正常商品)
			String updateTime = obj.getString("updateTime"); // 修改时间
			Date update;
			try {
				update = format.parse(updateTime);
			} catch (ParseException e) {
				logger.error("日期格式异常", "update 为空-------->" + CodeStatistical.getLineInfo());
				throw new ServiceException("日期格式异常: update 为空-------->" + CodeStatistical.getLineInfo());
			}

			Map<String, Object> map = new HashMap<String, Object>(); // 查询商品是否发生变化入参
			map.put("id", skuId);

			GoodsStoreSku storeSku = goodsStoreSkuService.getGoodsStoreSkuUpdateTime(map); // 查询商品是否发生变化
			if (storeSku == null) {
				logger.error("查询商品是否发生变化", "storeSku 为空-------->" + CodeStatistical.getLineInfo());
				logger.info("查询商品是否发生变化  storeSku 为空-------->" + CodeStatistical.getLineInfo());
				throw new ServiceException("查询商品是否发生变化异常：storeSku 为空-------->" + CodeStatistical.getLineInfo());
			}

			GoodsStoreSkuToAppVo skuToAppVo = new GoodsStoreSkuToAppVo();
			skuToAppVo.setId(storeSku.getId()); // 主键
			skuToAppVo.setName(storeSku.getName()); // 商品名称
			skuToAppVo.setAlias(storeSku.getAlias()); // 商品别名
			skuToAppVo.setBarCode(storeSku.getBarCode()); // 条形码
			skuToAppVo.setOnline(storeSku.getOnline().ordinal()); // 是否上架，0:下架、1:上架
			if (storeSku.getGuaranteed() == null || storeSku.getGuaranteed().equals("")) {
				skuToAppVo.setGuaranteed(0); // 服务保障
			} else {
				skuToAppVo.setGuaranteed(Integer.valueOf(storeSku.getGuaranteed())); // 服务保障
			}
			skuToAppVo.setObsolete(storeSku.getObsolete().ordinal()); // 陈旧状态
			skuToAppVo.setPropertiesIndb(storeSku.getPropertiesIndb()); // SKU属性在数据库中的字符串表示
			skuToAppVo.setMarketPrice(storeSku.getMarketPrice()); // 市场价格
			SimpleDateFormat upFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			skuToAppVo.setUpdateTime(upFormat.format(storeSku.getUpdateTime())); // 修改时间
			skuToAppVo.setTradeMax(storeSku.getTradeMax()); // 单次购买上限(预留)

			long beforeTime = update.getTime(); // 保存在App端的商品修改时间
			Date resourceUpdateTime = storeSku.getUpdateTime(); // 商品修改信息之后的修改时间
			long afterTime = resourceUpdateTime.getTime();

			GoodsStoreSkuDetailVo detailVo = goodsStoreSkuService.selectDetailBySkuId(skuId); // 查询商品详细信息
			if (detailVo == null) {
				logger.error("查询商品详细信息", "detailVo 为空-------->" + CodeStatistical.getLineInfo());
				logger.info("查询商品详细信息 detailVo 为空-------->" + CodeStatistical.getLineInfo());
				throw new ServiceException("查询商品详细信息异常：detailVo 为空-------->" + CodeStatistical.getLineInfo());
			}
			int activityType = detailVo.getActivityType().ordinal(); // 活动类型(0无,1:团购,2:特惠)
			String activityId = detailVo.getActivityId(); // 活动ID
			int saleStatus = 0;
			if (activityType == 2) {
				ActivitySale acSale = activitySaleService.getAcSaleStatus(activityId);
				saleStatus = acSale.getStatus(); // 特惠活动状态(0:未开始,1:进行中,2:已结束,3:已关闭)
			}
			if (isPrivilege.equals("0")) { // 1:特惠活动商品 0:正常商品
				if (activityType == 2 && saleStatus == 1) { // 由正常商品变成特惠商品
					Map<String, Object> saleMap = new HashMap<String, Object>();
					saleMap.put("storeSkuId", skuId);
					saleMap.put("saleId", activityId);
					ActivitySaleGoods activitySaleGoods = activitySaleGoodsService.selectActivitySaleByParams(saleMap); // 特惠商品信息查看
					if (activitySaleGoods == null) {
						logger.error("查询特惠商品信息", "activitySaleGoods 为空-------->" + CodeStatistical.getLineInfo());
						logger.info("查询特惠商品信息 activitySaleGoods 为空-------->" + CodeStatistical.getLineInfo());
						throw new ServiceException(
								"查询特惠商品信息异常：activitySaleGoods 为空-------->" + CodeStatistical.getLineInfo());
					}
					BigDecimal salePrice = activitySaleGoods.getSalePrice();
					saleGoods.add(skuId);
					skuToAppVo.setOnlinePrice(salePrice);
				} else {
					yunGoods.add(skuId);
					if (beforeTime != afterTime) {
						skuToAppVo.setOnlinePrice(storeSku.getOnlinePrice());
					} else {
						skuToAppVo.setOnlinePrice(storeSku.getOnlinePrice());
					}
				}
			} else if (isPrivilege.endsWith("1")) { // 由特惠商品变成正常商品
				if (activityType == 2 && (saleStatus != 1)) {
					skuToAppVo.setOnlinePrice(storeSku.getOnlinePrice());
					yunGoods.add(skuId);
				}
				if (activityType == 2 && saleStatus == 1) { // 由正常商品变成特惠商品
					Map<String, Object> saleMap = new HashMap<String, Object>();
					saleMap.put("storeSkuId", skuId);
					saleMap.put("saleId", activityId);
					ActivitySaleGoods activitySaleGoods = activitySaleGoodsService.selectActivitySaleByParams(saleMap); // 特惠商品信息查看
					if (activitySaleGoods == null) {
						logger.error("查询特惠商品信息", "activitySaleGoods 为空-------->" + CodeStatistical.getLineInfo());
						logger.info("查询特惠商品信息 activitySaleGoods 为空-------->" + CodeStatistical.getLineInfo());
						throw new ServiceException(
								"查询特惠商品信息异常：activitySaleGoods 为空-------->" + CodeStatistical.getLineInfo());
					}
					BigDecimal salePrice = activitySaleGoods.getSalePrice();
					saleGoods.add(skuId);
					skuToAppVo.setOnlinePrice(salePrice);
				} else {
					yunGoods.add(skuId);
					if (beforeTime != afterTime) {
						skuToAppVo.setOnlinePrice(storeSku.getOnlinePrice());
					} else {
						skuToAppVo.setOnlinePrice(storeSku.getOnlinePrice());
					}
				}
			}
			skuToAppVoList.add(skuToAppVo);
		}
		toAppVo.setToAppVoList(skuToAppVoList);
		toAppVo.setYunGoods(yunGoods);
		toAppVo.setSaleGoods(saleGoods);
		return toAppVo;

	}

	/**
	 * 商品是否发生变化方法
	 * </p>
	 * 
	 * @param array
	 * @return
	 * @throws Exception
	 */
	private int goodsArray(JSONArray array) throws Exception {

		int isHappen = 1;

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			String skuId = obj.getString("skuId"); // 商品ID
			String updateTime = obj.getString("updateTime"); // 修改时间
			String isPrivilege = obj.getString("isPrivilege"); // 是否是特惠商品标识0:否、1:是
			Date update;
			try {
				update = format.parse(updateTime);
			} catch (ParseException e) {
				logger.error("日期格式化异常", "update 为空-------->" + CodeStatistical.getLineInfo());
				throw new ServiceException("日期格式化异常 : update 为空-------->" + CodeStatistical.getLineInfo());
			}

			Map<String, Object> map = new HashMap<String, Object>(); // 查询商品是否发生变化入参
			map.put("id", skuId);

			GoodsStoreSku storeSku = goodsStoreSkuService.getGoodsStoreSkuUpdateTime(map); // 查询商品是否发生变化
			if (storeSku == null) {
				logger.error("查询商品是否发生变化", "storeSku 为空-------->" + CodeStatistical.getLineInfo());
				logger.info("查询商品是否发生变化  storeSku 为空-------->" + CodeStatistical.getLineInfo());
				throw new ServiceException("查询商品是否发生变化异常：storeSku 为空-------->" + CodeStatistical.getLineInfo());
			}
			long beforeTime = update.getTime(); // 保存在App端的商品修改时间
			Date resourceUpdateTime = storeSku.getUpdateTime(); // 商品修改信息之后的修改时间
			long afterTime = resourceUpdateTime.getTime();
			//
			GoodsStoreSkuDetailVo detailVo = goodsStoreSkuService.selectDetailBySkuId(skuId); // 查询商品详细信息
			// GoodsStoreSkuDetailVo detailVo =
			// goodsStoreSkuMapper.selectGoodsStoreSkuDetailNotPri(skuId); //
			// 查询商品详细信息
			if (detailVo == null) {
				logger.error("查询商品详细信息", "detailVo 为空-------->" + CodeStatistical.getLineInfo());
				logger.info("查询商品详细信息 detailVo 为空-------->" + CodeStatistical.getLineInfo());
				throw new ServiceException("查询商品详细信息异常：detailVo 为空-------->" + CodeStatistical.getLineInfo());
			}
			int activityType = detailVo.getActivityType().ordinal(); // 活动类型(0无,1:团购,2:特惠)
			String activityId = detailVo.getActivityId(); // 活动ID
			int saleStatus = 0;
			if (activityType == 2) {
				ActivitySale acSale = activitySaleService.getAcSaleStatus(activityId);
				saleStatus = acSale.getStatus(); // 特惠活动状态(0:未开始,1:进行中,2:已结束,3:已关闭)
			}
			if (isPrivilege.equals("0")) { // 1:活动商品 0:正常商品
				if (activityType == 2 && saleStatus == 1) { // 由正常商品变成特惠商品
					isHappen = 0; // 表示商品发生变化
				} else {
					if (beforeTime != afterTime) {
						isHappen = 0;
					}
				}
			} else if (isPrivilege.endsWith("1")) { // 由特惠商品变成正常商品
				if (activityType == 2 && (saleStatus != 1)) {
					isHappen = 0;
				} else {
					if (beforeTime != afterTime) {
						isHappen = 0;
					}
				}
			}
		}
		return isHappen;

	}

	@Override
	public JSONObject selectValidateStoreSkuStock(String requestStr) throws Exception {

		JSONObject requestJson = JSONObject.fromObject(requestStr);
		JSONObject jsonData = requestJson.getJSONObject("data");
		List<String> list = new ArrayList<String>(); // 商品ID集合

		String storeId = jsonData.getString("storeId"); // 店铺ID
		String startMoney = jsonData.getString("startMoney"); // 起送价
		String fare = jsonData.getString("fare"); // 运费
		String userId = jsonData.getString("userId"); // 用户ID
		BigDecimal bigStartMoney = null;
		BigDecimal bigFare = null;

		List<GoodsStoreSkuToAppVo> skuToAppVoList = new ArrayList<GoodsStoreSkuToAppVo>();

		JSONArray array = jsonData.getJSONArray("list"); // 商品列表
		JSONObject result = new JSONObject();

		List<Object> objList = new ArrayList<Object>(); // 商品库存对象集合

		int isOrder = 0; // 是否可以下单标识 1:可以,0:不可以
		int isBuy = 1; // 购买数量是否大于库存数
		int isStock = 1; // 库存是否充足标识 1:库存充足,0:库存不足

		int isSize = 1; // 是否大于特惠限款数量标识(1:未达到,0:已达到)
		int isNum = 1; // 是否大于用户特惠商品限ID标识(1:未达到,0:已达到)
		int isHappen = 1; // 特惠商品是否异动标识1:未变化,0:已变化
		int isChanges = 1; // 商品是否发生变化1:未变化,0:已变化

		String message = ""; // 返回标识

		StoreInfo storeInfo = storeInfoService.getStoreStatus(storeId); // 查询店铺信息

		if (storeInfo == null) {
			logger.error("查询店铺信息", "storeInfo 为空-------->" + CodeStatistical.getLineInfo());
			logger.info("查询店铺信息 storeInfo 为空-------->" + CodeStatistical.getLineInfo());
			throw new Exception("查询店铺信息异常：storeInfo 为空-------->" + CodeStatistical.getLineInfo());
		}

		int isClosed = storeInfo.getStoreInfoExt().getIsClosed().ordinal(); // 关闭/开启店铺(0:关闭,1:开启)
		int isAcceptOrder = storeInfo.getStoreInfoExt().getIsAcceptOrder().ordinal(); // 非营业时段是否接单(0:否,1:是)
		int isBusiness = storeInfo.getStoreInfoExt().getIsBusiness().ordinal(); // 是否营业(0:暂停营业,1:开始营业)
		int isInvoice = storeInfo.getStoreInfoExt().getIsInvoice().ordinal(); // 是否有发票(0:无,1:有)
		String startTime = storeInfo.getStoreInfoExt().getServiceStartTime();
		String endTime = storeInfo.getStoreInfoExt().getServiceEndTime();

		if (startMoney == null || startMoney.equals("")) {
			bigStartMoney = new BigDecimal("0.00");
			bigFare = new BigDecimal("0.00");
		} else {
			bigStartMoney = new BigDecimal(startMoney);
			bigFare = new BigDecimal(fare);
		}

		SimpleDateFormat formats = new SimpleDateFormat("HH:mm");
		Date stTime = formats.parse(startTime);
		Date edTime = formats.parse(endTime);
		Date date = new Date();
		String currDate = date.getHours() + ":" + date.getMinutes();
		Date curDate = formats.parse(currDate);
		int isRest = 1;
		if (stTime.before(edTime)) { // 不跨天营业
			if ((curDate.after(stTime)) && (edTime.after(curDate))) {
				isRest = 1;
				isAcceptOrder = 1;
			} else {
				isRest = 0;
			}
		} else { // 跨天
			if (curDate.after(stTime) || edTime.after(curDate)) {
				isAcceptOrder = 1;
				isRest = 1;
			} else {
				isRest = 0;
			}
		}

		StoreInfo store = storeInfoService.selectDefaultAddressById(storeId); // 店铺默认地址查询
		if (store == null) {
			logger.error("查询店铺默认地址", "store为空-------->" + CodeStatistical.getLineInfo());
			logger.info("查询店铺默认地址 store为空-------->" + CodeStatistical.getLineInfo());
			throw new Exception("查询店铺默认地址异常：store为空-------->" + CodeStatistical.getLineInfo());
		}

		BigDecimal toStartMoney = storeInfo.getStoreInfoExt().getStartPrice(); // 起送价
		BigDecimal toFare = storeInfo.getStoreInfoExt().getFreight(); // 运费
		BigDecimal resultStartMoney = new BigDecimal("0.00"); // 返回的起送价
		BigDecimal resultFare = new BigDecimal("0.00"); // 返回的运费
		BigDecimal bigSum = new BigDecimal("0.00"); // 订单商品总价格

		if (isClosed == 0) { // 店铺关闭状态
			result.put(message, "店铺关闭,不能下单");
			isClosed = 0;
		} else {
			if (isBusiness == 0) { // 店铺暂停营业
				result.put(message, "店铺暂停营业,不能下单");
				isBusiness = 0;
			} else {
				if (isAcceptOrder == 0) { // 非营业时间不接单
					result.put(message, "商家已打烊，暂时不接单");
					isAcceptOrder = 0;
				} else { // 商品是否发生变化
					isChanges = goodsArray(array);
					NewGoodsListToAppVo toAppVo = getNewGoodsList(array);
					skuToAppVoList = toAppVo.getToAppVoList();
					List<String> saleGoods = toAppVo.getSaleGoods();
					List<String> yunGoods = toAppVo.getYunGoods();
					objList = getStockList(yunGoods, saleGoods);
					if (isChanges == 0) {
						result.put(message, "商品发生变化不能下单");
						isChanges = 0;
					} else {
						for (int j = 0; j < array.size(); j++) {
							JSONObject obj = array.getJSONObject(j);
							String skuId = obj.getString("skuId");
							list.add(skuId);
						}
						for (int k = 0; k < array.size(); k++) { // 循环商品列表
							JSONObject obj = array.getJSONObject(k);
							String skuId = obj.getString("skuId"); // 商品ID
							String buyNum = obj.getString("buyNum"); // 购买数量
							int isBuyNums = Integer.valueOf(buyNum);
							String isPrivilege = obj.getString("isPrivilege"); // 是否是特惠商品标识0:否、1:是

							Map<String, Object> map = new HashMap<String, Object>(); // 查询商品是否发生变化入参
							map.put("id", skuId);
							GoodsStoreSkuStock storeSkuStock = goodsStoreSkuStockService.selectSingleSkuStock(skuId); // 查询商品信息

							GoodsStoreSkuDetailVo detailVo = goodsStoreSkuService.selectDetailBySkuId(skuId); // 查询商品详细信息
							if (detailVo == null) {
								logger.error("查询商品详细信息", "detailVo 为空-------->" + CodeStatistical.getLineInfo());
								logger.info("查询商品详细信息 detailVo 为空-------->" + CodeStatistical.getLineInfo());
								throw new Exception("查询商品详细信息异常：detailVo 为空-------->" + CodeStatistical.getLineInfo());
							}
							String activityId = detailVo.getActivityId(); // 活动ID

							Map<String, Object> saleMap = new HashMap<String, Object>();
							ActivitySaleGoods activitySaleGoods = new ActivitySaleGoods();
							saleMap.put("storeSkuId", skuId);
							saleMap.put("saleId", activityId);
							if (!activityId.equals("0") && activityId != null && !activityId.equals("")) {
								activitySaleGoods = activitySaleGoodsService.selectActivitySaleByParams(saleMap); // 特惠商品信息查看
								if (activitySaleGoods == null) {
									logger.error("查询特惠商品信息",
											"activitySaleGoods 为空-------->" + CodeStatistical.getLineInfo());
									logger.info(
											"查询特惠商品信息 activitySaleGoods 为空-------->" + CodeStatistical.getLineInfo());
									throw new Exception(
											"查询特惠商品信息异常：activitySaleGoods 为空-------->" + CodeStatistical.getLineInfo());
								}
							}
							if (isPrivilege.equals("1")) { // 1:特惠活动商品 0:正常商品
								bigSum = bigSum.add(new BigDecimal(buyNum).multiply(activitySaleGoods.getSalePrice()));
								Map<String, Object> hasMap = new HashMap<String, Object>(); // 查询用户ID是否已购买特惠商品款数入参
								hasMap.put("storeId", storeId);
								hasMap.put("userId", userId);
								hasMap.put("saleId", activityId);
								List<String> buyCountList = activitySaleRecordService
										.selectActivitySaleRecordOfFund(hasMap); // 查询特惠商已品购买款数
								int buyCount = buyCountList.size();
								int saleCount = activitySaleService.selectActivitySale(activityId); // 查询特惠商品限款数量

								List<String> hasSaleList = activitySaleRecordService
										.selectActivitySaleRecordList(hasMap);
								List<String> isSaleList = OrderUtils.removeDuplicateWithOrder(hasSaleList);
								List<String> resultList = new ArrayList<String>();
								if (isSaleList.size() > 0) {
									resultList = OrderUtils.resultList(list, isSaleList);
								}
								int isBuySize = resultList.size();
								Map<String, Object> buyMap = new HashMap<String, Object>(); // 查询用户ID是否已购买特惠商品入参
								buyMap.put("storeId", storeId);
								buyMap.put("saleGoodsId", skuId);
								buyMap.put("userId", userId);
								buyMap.put("saleId", activityId);
								int isBuyCount = activitySaleRecordService.selectActivitySaleRecord(buyMap); // 查询用户ID是否已购买特惠商品
								int tradeMax = activitySaleGoods.getTradeMax();
								int toBuyNum = Integer.valueOf(buyNum);
								if (saleCount <= 0) {
									if (tradeMax > 0) {
										if (toBuyNum > (tradeMax - isBuyCount)) { // 已购买特惠商品数量大于特惠商品限购数量
											isBuy = 0;
										} else if (toBuyNum <= (tradeMax - isBuyCount)) { // 判断库存
											int locked = storeSkuStock.getLocked();
											if (locked <= 0) {
												isStock = 0;
											} else if (locked < toBuyNum) {
												isStock = 0;
											}
										}
									}
								} else {
									if (isBuySize > (saleCount - buyCount)) { // 购买款数大于特惠活动限款数量
										isSize = 0;
									} else {
										if (tradeMax > 0) {
											if (toBuyNum > (tradeMax - isBuyCount)) { // 已购买特惠商品数量大于特惠商品限购数量
												isBuy = 0;
											} else if (toBuyNum <= (tradeMax - isBuyCount)) { // 判断库存
												int locked = storeSkuStock.getLocked();
												if (locked <= 0) {
													isStock = 0;
												} else if (locked < toBuyNum) {
													isStock = 0;
												}
											}
										}
									}
								}
							} else if (isPrivilege.equals("0")) {
								bigSum = bigSum.add(new BigDecimal(buyNum).multiply(detailVo.getOnlinePrice()));
								int sellabed = storeSkuStock.getSellable(); // 商品可销售库存
								if (sellabed <= 0) {
									isStock = 0;
								} else if (sellabed < isBuyNums) {
									isStock = 0;
								}
							}
						}
					}
				}
			}
		}

		if (bigStartMoney != null && !bigStartMoney.equals("")) { // 店铺后台起送价是否有设置
			if (toStartMoney != null && !toStartMoney.equals("")) { // App传递的起送价
				if (!bigStartMoney.equals(toStartMoney)) {
					if (bigSum.compareTo(toStartMoney) == 0 || bigSum.compareTo(toStartMoney) == 1) {
						bigFare = new BigDecimal("0.00");
						resultStartMoney = toStartMoney;
						resultFare = bigFare;
					} else if (bigSum.compareTo(toStartMoney) == -1) {
						resultStartMoney = toStartMoney;
						resultFare = toFare;
					}
				} else if (bigStartMoney.equals(toStartMoney)) {
					if (bigSum.compareTo(toStartMoney) == 0 || bigSum.compareTo(toStartMoney) == 1) {
						bigFare = new BigDecimal("0.00");
						resultStartMoney = toStartMoney;
						resultFare = bigFare;
					} else if (bigSum.compareTo(toStartMoney) == -1) {
						resultStartMoney = toStartMoney;
						resultFare = toFare;
					}
				}
			}
		} else {
			resultStartMoney = bigStartMoney;
			resultFare = bigFare;
		}

		if (isAcceptOrder != 0 && isClosed != 0 && isBusiness != 0 && isChanges == 1 && isBuy == 1 && isSize == 1
				&& isNum == 1 && isStock == 1) {
			isOrder = 1;
		}

		Date current = new Date();
		long currenTime = current.getTime();

		StoreInfo stores = storeInfoService.selectDefaultAddressById(storeId); // 店铺默认地址查询
		if (stores == null) {
			logger.error("查询店铺默认地址", "store为空-------->" + CodeStatistical.getLineInfo());
			logger.info("查询店铺默认地址 store为空-------->" + CodeStatistical.getLineInfo());
			throw new Exception("查询店铺默认地址异常：store为空-------->" + CodeStatistical.getLineInfo());
		}

		String area = "";
		String address = "";
		if (store.getArea() != null) {
			area = store.getArea();
		}
		if (store.getAddress() != null) {
			address = store.getAddress();
		}
		String myAddress = area + address; // 店铺详细地址
		String consigneeName = store.getStoreName() == null ? "" : store.getStoreName(); // 店铺名称
		String stsTime = store.getStoreInfoExt().getServiceStartTime(); // 店铺营业开始时间
		String edsTime = store.getStoreInfoExt().getServiceEndTime(); // 店铺营业结束时间

		result.put("consigneeName", consigneeName); // 店铺名称
		result.put("startTime", stsTime); // 店铺营业开始时间
		result.put("endTime", edsTime); // 店铺营业结束时间
		result.put("address", myAddress); // 店铺详细地址

		result.put("startMoney", resultStartMoney); // 起送价
		result.put("fare", resultFare); // 运费
		result.put("currenTime", currenTime); // 当前时间

		result.put("isOrder", isOrder); // 是否可以结算(0:否,1:是)
		result.put("isClosed", isClosed); // 关闭/开启店铺(0:关闭,1:开启)
		result.put("isBusiness", isBusiness); // 是否营业(0:暂停营业,1:开始营业)
		result.put("isAcceptOrder", isAcceptOrder); // 非营业时段是否接单(0:否,1:是)
		result.put("isSize", isSize); // 0:达到限购,1:未达到
		result.put("isRest", isRest); // 1:营业中,0:休息中

		result.put("isChanges", isChanges); // 商品是否发生变化1:未变化,0:已变化
		result.put("isHappen", isHappen); // 特惠商品是否发生变化0:发生变化,1:已发生变化
		result.put("detail", objList); // 商品库存
		result.put("goods", skuToAppVoList); // 商品列表
		result.put("isBuy", isBuy); // 购买数量是否大于库存数
		result.put("isStock", isStock); // 库存是否满足1:库存足,0:库存不足

		result.put("isInvoice", isInvoice); // 是否有发票(0:无,1:有)

		return result;
	}

	/**
	 * 计算 库存异动
	 * </p>
	 * 
	 * @param array
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	private void updateGoodsStoreSkuStock(JSONArray array, String storeId, String userId, String orderId)
			throws Exception {

		for (int k = 0; k < array.size(); k++) {

			JSONObject obj = array.getJSONObject(k);
			String skuId = obj.getString("skuId"); // 商品ID
			String skuNum = obj.getString("skuNum"); // 商品购买数量

			GoodsStoreSku storeSkuPr = goodsStoreSkuService.selectGoodsStoreSkuDetail(skuId); // 查询店铺商品详细信息
			if (storeSkuPr == null) {
				logger.error("查询店铺商品详细信息", "storeSkuPr 为空-------->" + CodeStatistical.getLineInfo());
				logger.info("查询店铺商品详细信息 storeSkuPr 为空-------->" + CodeStatistical.getLineInfo());
				throw new Exception("查询店铺商品详细信息异常：storeSkuPr 为空-------->" + CodeStatistical.getLineInfo());
			}

			Map<String, Object> map = new HashMap<String, Object>(); // 查询商品详细信息
			map.put("id", skuId);
			GoodsStoreSku storeSku = goodsStoreSkuService.getGoodsStoreSkuUpdateTime(map); // 查询商品详细信息
			if (storeSku == null) {
				logger.error("查询商品是否发生变化", "storeSku 为空-------->" + CodeStatistical.getLineInfo());
				logger.info("查询商品是否发生变化 storeSku 为空-------->" + CodeStatistical.getLineInfo());
				throw new Exception("查询商品是否发生变化异常: storeSku 为空-------->" + CodeStatistical.getLineInfo());
			}

			int actiType = storeSkuPr.getActivityType().ordinal(); // 活动类型(0无,1:团购,2:特惠)
			String actiId = storeSkuPr.getActivityId(); // 活动ID
			int saleStatus = 0; // 活动进行中标识
			if (actiType == 2) {
				ActivitySale acSale = activitySaleService.getAcSaleStatus(actiId);
				saleStatus = acSale.getStatus(); // 特惠活动状态(0:未开始,1:进行中,2:已结束,3:已关闭)
			}
			List<AdjustDetailVo> yunGoodsList = new ArrayList<AdjustDetailVo>();
			List<AdjustDetailVo> saleGoodsList = new ArrayList<AdjustDetailVo>();
			// 活动类型(0无,2:特惠)
			if ((saleStatus != 1) && actiType == 0) {

				AdjustDetailVo yunDetail = new AdjustDetailVo();
				yunDetail.setBarCode(storeSku.getBarCode());
				yunDetail.setGoodsName(storeSku.getName());
				yunDetail.setGoodsSkuId("");
				yunDetail.setMultipleSkuId("");
				yunDetail.setNum(Integer.valueOf(skuNum));
				yunDetail.setPrice(storeSku.getOnlinePrice());
				yunDetail.setPropertiesIndb(storeSku.getPropertiesIndb());
				yunDetail.setStoreSkuId(skuId);

				yunGoodsList.add(yunDetail);

				StockAdjustVo stockYunVo = new StockAdjustVo();
				stockYunVo.setOrderId(orderId);
				stockYunVo.setStoreId(storeId);
				stockYunVo.setUserId(userId);
				stockYunVo.setAdjustDetailList(yunGoodsList);
				stockYunVo.setStockOperateEnum(StockOperateEnum.PLACE_ORDER);
				stockManagerService.updateStock(stockYunVo);

			} else if ((saleStatus != 1) && actiType == 2) {
				AdjustDetailVo yunDetail = new AdjustDetailVo();
				yunDetail.setBarCode(storeSku.getBarCode());
				yunDetail.setGoodsName(storeSku.getName());
				yunDetail.setGoodsSkuId("");
				yunDetail.setMultipleSkuId("");
				yunDetail.setNum(Integer.valueOf(skuNum));
				yunDetail.setPrice(storeSku.getOnlinePrice());
				yunDetail.setPropertiesIndb(storeSku.getPropertiesIndb());
				yunDetail.setStoreSkuId(skuId);
				yunGoodsList.add(yunDetail);

				StockAdjustVo stockYunVo = new StockAdjustVo();
				stockYunVo.setOrderId(orderId);
				stockYunVo.setStoreId(storeId);
				stockYunVo.setUserId(userId);
				stockYunVo.setAdjustDetailList(yunGoodsList);
				stockYunVo.setStockOperateEnum(StockOperateEnum.PLACE_ORDER);
				stockManagerService.updateStock(stockYunVo);

			} else if (actiType == 2 && saleStatus == 1) {

				AdjustDetailVo saleDetail = new AdjustDetailVo();
				saleDetail.setBarCode(storeSku.getBarCode());
				saleDetail.setGoodsName(storeSku.getName());
				saleDetail.setGoodsSkuId("");
				saleDetail.setMultipleSkuId("");
				saleDetail.setNum(Integer.valueOf(skuNum));
				saleDetail.setPrice(storeSku.getOnlinePrice());
				saleDetail.setPropertiesIndb(storeSku.getPropertiesIndb());
				saleDetail.setStoreSkuId(skuId);
				saleGoodsList.add(saleDetail);

				StockAdjustVo stockVo = new StockAdjustVo();
				stockVo.setOrderId(orderId);
				stockVo.setStoreId(storeId);
				stockVo.setUserId(userId);
				stockVo.setAdjustDetailList(saleGoodsList);
				stockVo.setStockOperateEnum(StockOperateEnum.ACTIVITY_PLACE_ORDER);
				stockManagerService.updateStock(stockVo);

			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addTradeOrder(String requestStr) throws Exception {

		JSONObject reqJson = JSONObject.fromObject(requestStr);
		JSONObject jsonData = reqJson.getJSONObject("data");

		String userId = jsonData.getString("userId"); // 用户名
		// String userPhone = jsonData.getString("userPhone"); // 手机号码
		String pickType = jsonData.getString("pickType"); // 提货类型(0:送货上门,1:到店自提)
		String addressId = jsonData.getString("addressId"); // 地址ID
		String receiveTime = jsonData.getString("receiveTime"); // 自提时间

		String pickTime = jsonData.getString("pickTime"); // 送货时间
		String isInvoice = jsonData.getString("isInvoice"); // 是否有发票标识(0:无,1:有)
		String invoiceHead = jsonData.getString("invoiceHead"); // 发票抬头
		String invoiceContent = jsonData.getString("invoiceContent"); // 发票内容

		String storeId = jsonData.getString("storeId"); // 店铺ID
		// String storeName = jsonData.getString("storeName"); // 店铺名称
		String orderResource = jsonData.getString("orderResource"); // 订单来源
		logger.info("into<><><><><><><><><><><><><><><><><><><><><><>" + orderResource);
		logger.info("into<><><><><><><><><><><><><><><><><><><><><><>" + orderResource);
		logger.info("into<><><><><><><><><><><><><><><><><><><><><><>" + orderResource);
		logger.info("into<><><><><><><><><><><><><><><><><><><><><><>" + orderResource);
		logger.info("into<><><><><><><><><><><><><><><><><><><><><><>" + orderResource);
		logger.info("into<><><><><><><><><><><><><><><><><><><><><><>" + orderResource);
		String type = jsonData.getString("type"); // 订单类型
		String remark = jsonData.getString("remark"); // 备注

		String payType = jsonData.getString("payType"); // 支付方式：1:货到付款、0：在线支付
		// String fare = jsonData.getString("fare"); // 运费

		String activityId = jsonData.getString("activityId"); // 活动ID
		String activityType = jsonData.getString("activityType"); // 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		String couponsType = jsonData.getString("couponsType"); // 代金券活动类型
		String activityItemId = jsonData.getString("activityItemId"); // 活动项ID(比如代金券注册活动项ID、代金券领取活动项ID、满立减活动项ID,满折减活动项ID)
		String recordId = jsonData.getString("recordId"); // 代金券领取记录ID

		TradeOrder order = new TradeOrder();
		order.setId(UuidUtils.getUuid()); // 订单ID

		BigDecimal sum = new BigDecimal("0.00"); // 订单商品总金额
		JSONArray array = jsonData.getJSONArray("list"); // 获取购买商品列表

		List<String> list = new ArrayList<String>(); // 商品ID集合

		List<GoodsStoreSkuToAppVo> skuToAppVoList = new ArrayList<GoodsStoreSkuToAppVo>(); // 订单商品是否发生变化的商品集合

		int isStock = 1; // 库存是否满足1:库存足,0:库存不足
		int isPushSize = 1; // 是否购买超过限款1:未超过限款,0:已超过限款
		int isChanges = 1; // 商品是否发生变化1:未变化,0:已变化

		int isBuy = 1; // 购买数量是否大于库存数
		int isSize = 1; // 是否大于特惠限款数量标识(1:未达到,0:已达到)
		int isValid = 1; // 优惠是否有效1:有效,0:无效

		List<TradeOrderItem> orderItemList = new ArrayList<TradeOrderItem>();
		List<Object> objList = new ArrayList<Object>();

		int actType = 0; // 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		BigDecimal orderSum = null;
		if (!activityType.equals("") && !activityType.equals("0")) {
			actType = Integer.valueOf(activityType); // 活动类型
		}

		StoreInfo storeInfo = storeInfoService.getStoreStatus(storeId); // 查询店铺信息

		if (storeInfo == null) {
			logger.error("查询店铺信息", "storeInfo 为空-------->" + CodeStatistical.getLineInfo());
			logger.info("查询店铺信息 storeInfo 为空-------->" + CodeStatistical.getLineInfo());
			throw new ServiceException("查询店铺信息异常：storeInfo 为空-------->" + CodeStatistical.getLineInfo());
		}

		int isClosed = storeInfo.getStoreInfoExt().getIsClosed().ordinal(); // 关闭/开启店铺(0:关闭,1:开启)
		int isAcceptOrder = storeInfo.getStoreInfoExt().getIsAcceptOrder().ordinal(); // 非营业时段是否接单(0:否,1:是)
		int isBusiness = storeInfo.getStoreInfoExt().getIsBusiness().ordinal(); // 是否营业(0:暂停营业,1:开始营业)
		String startTime = storeInfo.getStoreInfoExt().getServiceStartTime();
		String endTime = storeInfo.getStoreInfoExt().getServiceEndTime();

		SaveOrderInfo saveOrderInfo = new SaveOrderInfo();

		SimpleDateFormat formats = new SimpleDateFormat("HH:mm");
		Date stTime = formats.parse(startTime);
		Date edTime = formats.parse(endTime);
		Date date = new Date();
		String currDate = date.getHours() + ":" + date.getMinutes();
		Date curDate = formats.parse(currDate);
		int isRest = 1;
		if (stTime.before(edTime)) { // 不跨天营业
			if ((curDate.after(stTime)) && (edTime.after(curDate))) {
				isAcceptOrder = 1;
				isRest = 1;
			} else {
				isRest = 0;
			}
		} else { // 跨天
			if (curDate.after(stTime) || edTime.after(curDate)) {
				isAcceptOrder = 1;
				isRest = 1;
			} else {
				isRest = 0;
			}
		}

		if (isClosed == 0) { // 店铺关闭状态
			isClosed = 0;
		} else {
			if (isBusiness == 0) { // 店铺暂停营业
				isBusiness = 0;
			} else {
				if (isAcceptOrder == 0) { // 非营业时间不接单
					isAcceptOrder = 0;
				} else {
					isChanges = goodsArray(array);
					NewGoodsListToAppVo toAppVo = getNewGoodsList(array);
					skuToAppVoList = toAppVo.getToAppVoList();
					List<String> saleGoods = toAppVo.getSaleGoods();
					List<String> yunGoods = toAppVo.getYunGoods();
					objList = getStockList(yunGoods, saleGoods);
					if (isChanges == 0) {
						isChanges = 0;
					} else {
						for (int j = 0; j < array.size(); j++) {
							JSONObject obj = array.getJSONObject(j);
							String skuId = obj.getString("skuId");
							list.add(skuId);
						}
						List<OrderItem> itemList = new ArrayList<OrderItem>(); // 订单项集合
						for (int k = 0; k < array.size(); k++) {
							// 循环商品列表
							JSONObject obj = array.getJSONObject(k);
							String skuId = obj.getString("skuId"); // 商品ID
							String buyNum = obj.getString("skuNum"); // 购买数量
							int isBuyNums = Integer.valueOf(buyNum);
							String isPrivilege = obj.getString("isPrivilege"); // 是否是特惠商品标识0:否、1:是

							Map<String, Object> map = new HashMap<String, Object>(); // 查询商品是否发生变化入参
							map.put("id", skuId);
							GoodsStoreSkuStock storeSkuStock = goodsStoreSkuStockService.selectSingleSkuStock(skuId); // 查询商品信息

							GoodsStoreSkuDetailVo detailVo = goodsStoreSkuService.selectDetailBySkuId(skuId); // 查询商品详细信息
							if (detailVo == null) {
								logger.error("查询商品详细信息", "detailVo 为空-------->" + CodeStatistical.getLineInfo());
								logger.info("查询商品详细信息 detailVo 为空-------->" + CodeStatistical.getLineInfo());
								throw new Exception("查询商品详细信息异常：detailVo 为空-------->" + CodeStatistical.getLineInfo());
							}
							String activityIds = detailVo.getActivityId(); // 活动ID

							Map<String, Object> saleMap = new HashMap<String, Object>();
							ActivitySaleGoods activitySaleGoods = new ActivitySaleGoods();
							saleMap.put("storeSkuId", skuId);
							saleMap.put("saleId", activityIds); // 特惠活动ID
							if (!activityIds.equals("0") && activityIds != null && !activityIds.equals("")) {
								activitySaleGoods = activitySaleGoodsService.selectActivitySaleByParams(saleMap); // 特惠商品信息查看
								if (activitySaleGoods == null) {
									logger.error("查询特惠商品信息",
											"activitySaleGoods 为空-------->" + CodeStatistical.getLineInfo());
									logger.info(
											"查询特惠商品信息 activitySaleGoods 为空-------->" + CodeStatistical.getLineInfo());
									throw new Exception(
											"查询特惠商品信息异常：activitySaleGoods 为空-------->" + CodeStatistical.getLineInfo());
								}
							}
							if (isPrivilege.equals("1")) { // 1:特惠活动商品 0:正常商品
								Map<String, Object> hasMap = new HashMap<String, Object>(); // 查询用户ID是否已购买特惠商品款数入参
								hasMap.put("storeId", storeId);
								hasMap.put("userId", userId);
								hasMap.put("saleId", activityIds);
								List<String> buyCountList = activitySaleRecordService
										.selectActivitySaleRecordOfFund(hasMap); // 查询特惠商已品购买款数
								int buyCount = buyCountList.size();
								int saleCount = activitySaleService.selectActivitySale(activityIds); // 查询特惠商品限款数量

								List<String> hasSaleList = activitySaleRecordService
										.selectActivitySaleRecordList(hasMap);
								List<String> isSaleList = OrderUtils.removeDuplicateWithOrder(hasSaleList);
								List<String> resultList = new ArrayList<String>();
								if (isSaleList.size() > 0) {
									resultList = OrderUtils.resultList(list, isSaleList);
								}
								int isBuySize = resultList.size();
								Map<String, Object> buyMap = new HashMap<String, Object>(); // 查询用户ID是否已购买特惠商品入参
								buyMap.put("storeId", storeId);
								buyMap.put("saleGoodsId", skuId);
								buyMap.put("userId", userId);
								buyMap.put("saleId", activityIds);
								int isBuyCount = activitySaleRecordService.selectActivitySaleRecord(buyMap); // 查询用户ID是否已购买特惠商品
								int tradeMax = activitySaleGoods.getTradeMax(); // 特惠活动商品限购数量
								int toBuyNum = Integer.valueOf(buyNum); // 已购买商品数量
								if (saleCount <= 0) { // 表示不限款
									if (tradeMax <= 0) { // 不限购数量
										int locked = storeSkuStock.getLocked();
										if (locked <= 0) { // 库存不足
											isStock = 0;
										} else if (locked < toBuyNum) { // 计算结果、库存不足
											isStock = 0;
										} else if (locked >= toBuyNum) { // 库存判断通过,查看选中的特惠活动是否失效
											int types = Integer.valueOf(activityType); // 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
											if (types == 1) {
												ActivityCouponsRecord conpons = activityCouponsRecordService
														.selectByPrimaryKey(recordId);
												if (conpons.getStatus().ordinal() == 2) { // 代金券失效
													isValid = 0;
												} else {

													CalculateOrderVoInParam calInParam = new CalculateOrderVoInParam();
													calInParam.setArray(array);
													calInParam.setSkuId(skuId);
													calInParam.setSkuNum(isBuyNums);
													calInParam.setOrderId(order.getId());
													calInParam.setStoreId(storeId);
													calInParam.setUserId(userId);
													CalculateOrderVo orderVo = calculateOrder(calInParam); // 单品款数*乘以价格的集合

													sum = orderVo.getSum(); // 商品总金额

													itemList = orderVo.getItemList();

													CalculatePrivilegeInParam priInParam = new CalculatePrivilegeInParam();
													priInParam.setActivityId(activityId);
													priInParam.setActivityItemId(activityItemId);
													priInParam.setActType(actType);
													priInParam.setCouponsType(couponsType);
													priInParam.setItemList(itemList);
													priInParam.setOrderId(order.getId());
													priInParam.setStoreId(storeId);
													priInParam.setSum(sum);
													priInParam.setUserId(userId);
													CalculatePrivilege privilege = getLastPrivilege(priInParam); // 计算总优惠

													CalCulateOrderAmountInParam amountInParam = new CalCulateOrderAmountInParam();
													amountInParam.setActivityId(activityId);
													amountInParam.setActivityType(activityType);
													amountInParam.setActType(actType);
													amountInParam.setAfterItemList(itemList);
													amountInParam.setArray(array);
													amountInParam.setOrderId(order.getId());
													amountInParam.setStoreId(storeId);
													amountInParam.setUserId(userId);
													CalculateTradeOrderItem calculateItem = calCulateOrderAmount(
															amountInParam); // 计算优惠之后的订单金额

													orderSum = privilege.getOrderSum();
													orderItemList = calculateItem.getOrderItemList();

													SaveTradeOrderInfoInParam saveInParam = new SaveTradeOrderInfoInParam();
													saveInParam.setActivitId(activityId);
													saveInParam.setActivityItemId(activityItemId);
													saveInParam.setActType(actType);
													saveInParam.setAddressId(addressId);
													saveInParam.setCouponsType(couponsType);
													saveInParam.setInvoiceContent(invoiceContent);
													saveInParam.setInvoiceHead(invoiceHead);
													saveInParam.setIsInvoice(isInvoice);
													saveInParam.setItemList(itemList);
													saveInParam.setOrder(order);
													saveInParam.setOrderItemList(orderItemList);
													saveInParam.setOrderResource(orderResource);
													saveInParam.setOrderSum(orderSum);
													saveInParam.setPayType(payType);
													saveInParam.setPickTime(pickTime);
													saveInParam.setPickType(pickType);
													saveInParam.setReceiveTime(receiveTime);
													saveInParam.setRecordId(recordId);
													saveInParam.setRemark(remark);
													saveInParam.setStoreId(storeId);
													saveInParam.setSum(sum);
													saveInParam.setType(type);
													saveInParam.setUserId(userId);
													SaveTradeOrderInfo orderInfo = saveTradeOrderInfo(saveInParam); // 保存订单数据

													saveOrderInfo.setCalculateOrderVo(orderVo);
													saveOrderInfo.setCalculatePrivilege(privilege);
													saveOrderInfo.setCalculateTradeOrderItem(calculateItem);
													saveOrderInfo.setSaveTradeOrderInfo(orderInfo);
												}
											} else if (types == 2 || types == 3) { // 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
												ActivityDiscount discount = activityDiscountService
														.selectByPrimaryKey(activityId);
												int status = discount.getStatus().ordinal();
												if (status != 1) {
													isValid = 0;
												} else { // 计算商品金额
													CalculateOrderVoInParam calInParam = new CalculateOrderVoInParam();
													calInParam.setArray(array);
													calInParam.setOrderId(order.getId());
													calInParam.setStoreId(storeId);
													calInParam.setUserId(userId);
													CalculateOrderVo orderVo = calculateOrder(calInParam); // 单品款数*乘以价格的集合

													sum = orderVo.getSum(); // 商品总金额
													itemList = orderVo.getItemList();

													CalculatePrivilegeInParam priInParam = new CalculatePrivilegeInParam();
													priInParam.setActivityId(activityId);
													priInParam.setActivityItemId(activityItemId);
													priInParam.setActType(actType);
													priInParam.setCouponsType(couponsType);
													priInParam.setItemList(itemList);
													priInParam.setOrderId(order.getId());
													priInParam.setStoreId(storeId);
													priInParam.setSum(sum);
													priInParam.setUserId(userId);
													CalculatePrivilege privilege = getLastPrivilege(priInParam); // 计算优惠

													CalCulateOrderAmountInParam amountInParam = new CalCulateOrderAmountInParam();
													amountInParam.setActivityId(activityId);
													amountInParam.setActivityType(activityType);
													amountInParam.setActType(actType);
													amountInParam.setAfterItemList(itemList);
													amountInParam.setArray(array);
													amountInParam.setOrderId(order.getId());
													amountInParam.setStoreId(storeId);
													amountInParam.setUserId(userId);
													CalculateTradeOrderItem calculateItem = calCulateOrderAmount(
															amountInParam); // 计算优惠之后的订单金额

													orderSum = privilege.getOrderSum();
													orderItemList = calculateItem.getOrderItemList();

													SaveTradeOrderInfoInParam saveInParam = new SaveTradeOrderInfoInParam();
													saveInParam.setActivitId(activityId);
													saveInParam.setActivityItemId(activityItemId);
													saveInParam.setActType(actType);
													saveInParam.setAddressId(addressId);
													saveInParam.setCouponsType(couponsType);
													saveInParam.setInvoiceContent(invoiceContent);
													saveInParam.setInvoiceHead(invoiceHead);
													saveInParam.setIsInvoice(isInvoice);
													saveInParam.setItemList(itemList);
													saveInParam.setOrder(order);
													saveInParam.setOrderItemList(orderItemList);
													saveInParam.setOrderResource(orderResource);
													saveInParam.setOrderSum(orderSum);
													saveInParam.setPayType(payType);
													saveInParam.setPickTime(pickTime);
													saveInParam.setPickType(pickType);
													saveInParam.setReceiveTime(receiveTime);
													saveInParam.setRecordId(recordId);
													saveInParam.setRemark(remark);
													saveInParam.setStoreId(storeId);
													saveInParam.setSum(sum);
													saveInParam.setType(type);
													saveInParam.setUserId(userId);
													SaveTradeOrderInfo orderInfo = saveTradeOrderInfo(saveInParam); // 保存订单数据

													saveOrderInfo.setCalculateOrderVo(orderVo);
													saveOrderInfo.setCalculatePrivilege(privilege);
													saveOrderInfo.setCalculateTradeOrderItem(calculateItem);
													saveOrderInfo.setSaveTradeOrderInfo(orderInfo);

												}
											} else if (types == 0) { // 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
												CalculateOrderVoInParam calInParam = new CalculateOrderVoInParam();
												calInParam.setArray(array);
												calInParam.setOrderId(order.getId());
												calInParam.setStoreId(storeId);
												calInParam.setUserId(userId);
												CalculateOrderVo orderVo = calculateOrder(calInParam); // 单品款数*乘以价格的集合

												sum = orderVo.getSum(); // 商品总金额
												itemList = orderVo.getItemList();

												CalculatePrivilegeInParam priInParam = new CalculatePrivilegeInParam();
												priInParam.setActivityId(activityId);
												priInParam.setActivityItemId(activityItemId);
												priInParam.setActType(actType);
												priInParam.setCouponsType(couponsType);
												priInParam.setItemList(itemList);
												priInParam.setOrderId(order.getId());
												priInParam.setStoreId(storeId);
												priInParam.setSum(sum);
												priInParam.setUserId(userId);
												CalculatePrivilege privilege = getLastPrivilege(priInParam); // 计算优惠

												CalCulateOrderAmountInParam amountInParam = new CalCulateOrderAmountInParam();
												amountInParam.setActivityId(activityId);
												amountInParam.setActivityType(activityType);
												amountInParam.setActType(actType);
												amountInParam.setAfterItemList(itemList);
												amountInParam.setArray(array);
												amountInParam.setOrderId(order.getId());
												amountInParam.setStoreId(storeId);
												amountInParam.setUserId(userId);
												CalculateTradeOrderItem calculateItem = calCulateOrderAmount(
														amountInParam); // 计算优惠之后的订单金额

												orderSum = privilege.getOrderSum();
												orderItemList = calculateItem.getOrderItemList();

												SaveTradeOrderInfoInParam saveInParam = new SaveTradeOrderInfoInParam();
												saveInParam.setActivitId(activityId);
												saveInParam.setActivityItemId(activityItemId);
												saveInParam.setActType(actType);
												saveInParam.setAddressId(addressId);
												saveInParam.setCouponsType(couponsType);
												saveInParam.setInvoiceContent(invoiceContent);
												saveInParam.setInvoiceHead(invoiceHead);
												saveInParam.setIsInvoice(isInvoice);
												saveInParam.setItemList(itemList);
												saveInParam.setOrder(order);
												saveInParam.setOrderItemList(orderItemList);
												saveInParam.setOrderResource(orderResource);
												saveInParam.setOrderSum(orderSum);
												saveInParam.setPayType(payType);
												saveInParam.setPickTime(pickTime);
												saveInParam.setPickType(pickType);
												saveInParam.setReceiveTime(receiveTime);
												saveInParam.setRecordId(recordId);
												saveInParam.setRemark(remark);
												saveInParam.setStoreId(storeId);
												saveInParam.setSum(sum);
												saveInParam.setType(type);
												saveInParam.setUserId(userId);
												SaveTradeOrderInfo orderInfo = saveTradeOrderInfo(saveInParam); // 保存订单数据

												saveOrderInfo.setCalculateOrderVo(orderVo);
												saveOrderInfo.setCalculatePrivilege(privilege);
												saveOrderInfo.setCalculateTradeOrderItem(calculateItem);
												saveOrderInfo.setSaveTradeOrderInfo(orderInfo);
											}
										}

									} else if (tradeMax > 0) { // 限款
										if (toBuyNum > (tradeMax - isBuyCount)) { // 已购买特惠商品数量大于特惠商品限购数量
											isBuy = 0;
										} else if (toBuyNum <= (tradeMax - isBuyCount)) { // 判断库存
											int locked = storeSkuStock.getLocked();
											if (locked <= 0) { // 库存不足
												isStock = 0;
											} else if (locked < toBuyNum) { // 计算结果、库存不足
												isStock = 0;
											} else if (locked >= toBuyNum) { // 库存判断通过,查看选中的特惠活动是否失效
												int types = Integer.valueOf(activityType); // 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
												if (types == 1) {
													ActivityCouponsRecord conpons = activityCouponsRecordService
															.selectByPrimaryKey(recordId);
													if (conpons.getStatus().ordinal() == 2) {
														isValid = 0;
													} else { // 计算商品金额

														CalculateOrderVoInParam calInParam = new CalculateOrderVoInParam();
														calInParam.setArray(array);
														calInParam.setOrderId(order.getId());
														calInParam.setStoreId(storeId);
														calInParam.setUserId(userId);
														CalculateOrderVo orderVo = calculateOrder(calInParam); // 单品款数*乘以价格的集合

														sum = orderVo.getSum(); // 商品总金额
														itemList = orderVo.getItemList();

														CalculatePrivilegeInParam priInParam = new CalculatePrivilegeInParam();
														priInParam.setActivityId(activityId);
														priInParam.setActivityItemId(activityItemId);
														priInParam.setActType(actType);
														priInParam.setCouponsType(couponsType);
														priInParam.setItemList(itemList);
														priInParam.setOrderId(order.getId());
														priInParam.setStoreId(storeId);
														priInParam.setSum(sum);
														priInParam.setUserId(userId);
														CalculatePrivilege privilege = getLastPrivilege(priInParam); // 计算优惠

														CalCulateOrderAmountInParam amountInParam = new CalCulateOrderAmountInParam();
														amountInParam.setActivityId(activityId);
														amountInParam.setActivityType(activityType);
														amountInParam.setActType(actType);
														amountInParam.setAfterItemList(itemList);
														amountInParam.setArray(array);
														amountInParam.setOrderId(order.getId());
														amountInParam.setStoreId(storeId);
														amountInParam.setUserId(userId);
														CalculateTradeOrderItem calculateItem = calCulateOrderAmount(
																amountInParam); // 计算优惠之后的订单金额

														orderSum = privilege.getOrderSum();
														orderItemList = calculateItem.getOrderItemList();

														SaveTradeOrderInfoInParam saveInParam = new SaveTradeOrderInfoInParam();
														saveInParam.setActivitId(activityId);
														saveInParam.setActivityItemId(activityItemId);
														saveInParam.setActType(actType);
														saveInParam.setAddressId(addressId);
														saveInParam.setCouponsType(couponsType);
														saveInParam.setInvoiceContent(invoiceContent);
														saveInParam.setInvoiceHead(invoiceHead);
														saveInParam.setIsInvoice(isInvoice);
														saveInParam.setItemList(itemList);
														saveInParam.setOrder(order);
														saveInParam.setOrderItemList(orderItemList);
														saveInParam.setOrderResource(orderResource);
														saveInParam.setOrderSum(orderSum);
														saveInParam.setPayType(payType);
														saveInParam.setPickTime(pickTime);
														saveInParam.setPickType(pickType);
														saveInParam.setReceiveTime(receiveTime);
														saveInParam.setRecordId(recordId);
														saveInParam.setRemark(remark);
														saveInParam.setStoreId(storeId);
														saveInParam.setSum(sum);
														saveInParam.setType(type);
														saveInParam.setUserId(userId);
														SaveTradeOrderInfo orderInfo = saveTradeOrderInfo(saveInParam); // 保存订单数据

														saveOrderInfo.setCalculateOrderVo(orderVo);
														saveOrderInfo.setCalculatePrivilege(privilege);
														saveOrderInfo.setCalculateTradeOrderItem(calculateItem);
														saveOrderInfo.setSaveTradeOrderInfo(orderInfo);
													}
												} else if (types == 2 || types == 3) {
													ActivityDiscount discount = activityDiscountService
															.selectByPrimaryKey(activityId);
													int status = discount.getStatus().ordinal();
													if (status != 1) {
														isValid = 0;
													} else { // 计算商品金额
														CalculateOrderVoInParam calInParam = new CalculateOrderVoInParam();
														calInParam.setArray(array);
														calInParam.setOrderId(order.getId());
														calInParam.setStoreId(storeId);
														calInParam.setUserId(userId);
														CalculateOrderVo orderVo = calculateOrder(calInParam); // 单品款数*乘以价格的集合

														sum = orderVo.getSum(); // 商品总金额
														itemList = orderVo.getItemList();

														CalculatePrivilegeInParam priInParam = new CalculatePrivilegeInParam();
														priInParam.setActivityId(activityId);
														priInParam.setActivityItemId(activityItemId);
														priInParam.setActType(actType);
														priInParam.setCouponsType(couponsType);
														priInParam.setItemList(itemList);
														priInParam.setOrderId(order.getId());
														priInParam.setStoreId(storeId);
														priInParam.setSum(sum);
														priInParam.setUserId(userId);
														CalculatePrivilege privilege = getLastPrivilege(priInParam); // 计算优惠

														CalCulateOrderAmountInParam amountInParam = new CalCulateOrderAmountInParam();
														amountInParam.setActivityId(activityId);
														amountInParam.setActivityType(activityType);
														amountInParam.setActType(actType);
														amountInParam.setAfterItemList(itemList);
														amountInParam.setArray(array);
														amountInParam.setOrderId(order.getId());
														amountInParam.setStoreId(storeId);
														amountInParam.setUserId(userId);
														CalculateTradeOrderItem calculateItem = calCulateOrderAmount(
																amountInParam); // 计算优惠之后的订单金额

														orderSum = privilege.getOrderSum();
														orderItemList = calculateItem.getOrderItemList();

														SaveTradeOrderInfoInParam saveInParam = new SaveTradeOrderInfoInParam();
														saveInParam.setActivitId(activityId);
														saveInParam.setActivityItemId(activityItemId);
														saveInParam.setActType(actType);
														saveInParam.setAddressId(addressId);
														saveInParam.setCouponsType(couponsType);
														saveInParam.setInvoiceContent(invoiceContent);
														saveInParam.setInvoiceHead(invoiceHead);
														saveInParam.setIsInvoice(isInvoice);
														saveInParam.setItemList(itemList);
														saveInParam.setOrder(order);
														saveInParam.setOrderItemList(orderItemList);
														saveInParam.setOrderResource(orderResource);
														saveInParam.setOrderSum(orderSum);
														saveInParam.setPayType(payType);
														saveInParam.setPickTime(pickTime);
														saveInParam.setPickType(pickType);
														saveInParam.setReceiveTime(receiveTime);
														saveInParam.setRecordId(recordId);
														saveInParam.setRemark(remark);
														saveInParam.setStoreId(storeId);
														saveInParam.setSum(sum);
														saveInParam.setType(type);
														saveInParam.setUserId(userId);
														SaveTradeOrderInfo orderInfo = saveTradeOrderInfo(saveInParam); // 保存订单数据

														saveOrderInfo.setCalculateOrderVo(orderVo);
														saveOrderInfo.setCalculatePrivilege(privilege);
														saveOrderInfo.setCalculateTradeOrderItem(calculateItem);
														saveOrderInfo.setSaveTradeOrderInfo(orderInfo);
													}
												} else if (types == 0) {
													CalculateOrderVoInParam calInParam = new CalculateOrderVoInParam();
													calInParam.setArray(array);
													calInParam.setOrderId(order.getId());
													calInParam.setStoreId(storeId);
													calInParam.setUserId(userId);
													CalculateOrderVo orderVo = calculateOrder(calInParam); // 单品款数*乘以价格的集合

													sum = orderVo.getSum(); // 商品总金额
													itemList = orderVo.getItemList();

													CalculatePrivilegeInParam priInParam = new CalculatePrivilegeInParam();
													priInParam.setActivityId(activityId);
													priInParam.setActivityItemId(activityItemId);
													priInParam.setActType(actType);
													priInParam.setCouponsType(couponsType);
													priInParam.setItemList(itemList);
													priInParam.setOrderId(order.getId());
													priInParam.setStoreId(storeId);
													priInParam.setSum(sum);
													priInParam.setUserId(userId);
													CalculatePrivilege privilege = getLastPrivilege(priInParam); // 计算优惠

													CalCulateOrderAmountInParam amountInParam = new CalCulateOrderAmountInParam();
													amountInParam.setActivityId(activityId);
													amountInParam.setActivityType(activityType);
													amountInParam.setActType(actType);
													amountInParam.setAfterItemList(itemList);
													amountInParam.setArray(array);
													amountInParam.setOrderId(order.getId());
													amountInParam.setStoreId(storeId);
													amountInParam.setUserId(userId);
													CalculateTradeOrderItem calculateItem = calCulateOrderAmount(
															amountInParam); // 计算优惠之后的订单金额

													orderSum = privilege.getOrderSum();
													orderItemList = calculateItem.getOrderItemList();

													SaveTradeOrderInfoInParam saveInParam = new SaveTradeOrderInfoInParam();
													saveInParam.setActivitId(activityId);
													saveInParam.setActivityItemId(activityItemId);
													saveInParam.setActType(actType);
													saveInParam.setAddressId(addressId);
													saveInParam.setCouponsType(couponsType);
													saveInParam.setInvoiceContent(invoiceContent);
													saveInParam.setInvoiceHead(invoiceHead);
													saveInParam.setIsInvoice(isInvoice);
													saveInParam.setItemList(itemList);
													saveInParam.setOrder(order);
													saveInParam.setOrderItemList(orderItemList);
													saveInParam.setOrderResource(orderResource);
													saveInParam.setOrderSum(orderSum);
													saveInParam.setPayType(payType);
													saveInParam.setPickTime(pickTime);
													saveInParam.setPickType(pickType);
													saveInParam.setReceiveTime(receiveTime);
													saveInParam.setRecordId(recordId);
													saveInParam.setRemark(remark);
													saveInParam.setStoreId(storeId);
													saveInParam.setSum(sum);
													saveInParam.setType(type);
													saveInParam.setUserId(userId);
													SaveTradeOrderInfo orderInfo = saveTradeOrderInfo(saveInParam); // 保存订单数据

													saveOrderInfo.setCalculateOrderVo(orderVo);
													saveOrderInfo.setCalculatePrivilege(privilege);
													saveOrderInfo.setCalculateTradeOrderItem(calculateItem);
													saveOrderInfo.setSaveTradeOrderInfo(orderInfo);
												}
											}
										}
									}

								} else { // 特惠活动限款
									if (isBuySize > (saleCount - buyCount)) { // 购买限款数大于特惠活动限款数量
										isSize = 0;
									} else {
										if (tradeMax > 0) { // 限购
											if (toBuyNum > (tradeMax - isBuyCount)) { // 已购买特惠商品数量大于特惠商品限购数量
												isBuy = 0;
											} else if (toBuyNum <= (tradeMax - isBuyCount)) { // 判断库存
												int locked = storeSkuStock.getLocked();
												if (locked <= 0) { // 库存不足
													isStock = 0;
												} else if (locked < toBuyNum) { // 计算结果、库存不足
													isStock = 0;
												} else if (locked >= toBuyNum) { // 库存判断通过,查看选中的特惠活动是否失效
													int types = Integer.valueOf(activityType);
													if (types == 1) {// 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
														ActivityCouponsRecord conpons = activityCouponsRecordService
																.selectByPrimaryKey(recordId);
														if (conpons.getStatus().ordinal() == 2) {
															isValid = 0;
														} else { // 计算商品金额
															CalculateOrderVoInParam calInParam = new CalculateOrderVoInParam();
															calInParam.setArray(array);
															calInParam.setOrderId(order.getId());
															calInParam.setStoreId(storeId);
															calInParam.setUserId(userId);
															CalculateOrderVo orderVo = calculateOrder(calInParam); // 单品款数*乘以价格的集合

															sum = orderVo.getSum(); // 商品总金额
															itemList = orderVo.getItemList();

															CalculatePrivilegeInParam priInParam = new CalculatePrivilegeInParam();
															priInParam.setActivityId(activityId);
															priInParam.setActivityItemId(activityItemId);
															priInParam.setActType(actType);
															priInParam.setCouponsType(couponsType);
															priInParam.setItemList(itemList);
															priInParam.setOrderId(order.getId());
															priInParam.setStoreId(storeId);
															priInParam.setSum(sum);
															priInParam.setUserId(userId);
															CalculatePrivilege privilege = getLastPrivilege(priInParam); // 计算优惠

															CalCulateOrderAmountInParam amountInParam = new CalCulateOrderAmountInParam();
															amountInParam.setActivityId(activityId);
															amountInParam.setActivityType(activityType);
															amountInParam.setActType(actType);
															amountInParam.setAfterItemList(itemList);
															amountInParam.setArray(array);
															amountInParam.setOrderId(order.getId());
															amountInParam.setStoreId(storeId);
															amountInParam.setUserId(userId);
															CalculateTradeOrderItem calculateItem = calCulateOrderAmount(
																	amountInParam); // 计算优惠之后的订单金额

															orderSum = privilege.getOrderSum();
															orderItemList = calculateItem.getOrderItemList();

															SaveTradeOrderInfoInParam saveInParam = new SaveTradeOrderInfoInParam();
															saveInParam.setActivitId(activityId);
															saveInParam.setActivityItemId(activityItemId);
															saveInParam.setActType(actType);
															saveInParam.setAddressId(addressId);
															saveInParam.setCouponsType(couponsType);
															saveInParam.setInvoiceContent(invoiceContent);
															saveInParam.setInvoiceHead(invoiceHead);
															saveInParam.setIsInvoice(isInvoice);
															saveInParam.setItemList(itemList);
															saveInParam.setOrder(order);
															saveInParam.setOrderItemList(orderItemList);
															saveInParam.setOrderResource(orderResource);
															saveInParam.setOrderSum(orderSum);
															saveInParam.setPayType(payType);
															saveInParam.setPickTime(pickTime);
															saveInParam.setPickType(pickType);
															saveInParam.setReceiveTime(receiveTime);
															saveInParam.setRecordId(recordId);
															saveInParam.setRemark(remark);
															saveInParam.setStoreId(storeId);
															saveInParam.setSum(sum);
															saveInParam.setType(type);
															saveInParam.setUserId(userId);
															SaveTradeOrderInfo orderInfo = saveTradeOrderInfo(
																	saveInParam); // 保存订单数据

															saveOrderInfo.setCalculateOrderVo(orderVo);
															saveOrderInfo.setCalculatePrivilege(privilege);
															saveOrderInfo.setCalculateTradeOrderItem(calculateItem);
															saveOrderInfo.setSaveTradeOrderInfo(orderInfo);
														}
													} else if (types == 2 || types == 3) {
														ActivityDiscount discount = activityDiscountService
																.selectByPrimaryKey(activityId);
														int status = discount.getStatus().ordinal();
														if (status != 1) {
															isValid = 0;
														} else {
															CalculateOrderVoInParam calInParam = new CalculateOrderVoInParam();
															calInParam.setArray(array);
															calInParam.setOrderId(order.getId());
															calInParam.setStoreId(storeId);
															calInParam.setUserId(userId);
															CalculateOrderVo orderVo = calculateOrder(calInParam); // 单品款数*乘以价格的集合

															sum = orderVo.getSum(); // 商品总金额
															itemList = orderVo.getItemList();

															CalculatePrivilegeInParam priInParam = new CalculatePrivilegeInParam();
															priInParam.setActivityId(activityId);
															priInParam.setActivityItemId(activityItemId);
															priInParam.setActType(actType);
															priInParam.setCouponsType(couponsType);
															priInParam.setItemList(itemList);
															priInParam.setOrderId(order.getId());
															priInParam.setStoreId(storeId);
															priInParam.setSum(sum);
															priInParam.setUserId(userId);
															CalculatePrivilege privilege = getLastPrivilege(priInParam); // 计算优惠

															CalCulateOrderAmountInParam amountInParam = new CalCulateOrderAmountInParam();
															amountInParam.setActivityId(activityId);
															amountInParam.setActivityType(activityType);
															amountInParam.setActType(actType);
															amountInParam.setAfterItemList(itemList);
															amountInParam.setArray(array);
															amountInParam.setOrderId(order.getId());
															amountInParam.setStoreId(storeId);
															amountInParam.setUserId(userId);
															CalculateTradeOrderItem calculateItem = calCulateOrderAmount(
																	amountInParam); // 计算优惠之后的订单金额

															orderSum = privilege.getOrderSum();
															orderItemList = calculateItem.getOrderItemList();

															SaveTradeOrderInfoInParam saveInParam = new SaveTradeOrderInfoInParam();
															saveInParam.setActivitId(activityId);
															saveInParam.setActivityItemId(activityItemId);
															saveInParam.setActType(actType);
															saveInParam.setAddressId(addressId);
															saveInParam.setCouponsType(couponsType);
															saveInParam.setInvoiceContent(invoiceContent);
															saveInParam.setInvoiceHead(invoiceHead);
															saveInParam.setIsInvoice(isInvoice);
															saveInParam.setItemList(itemList);
															saveInParam.setOrder(order);
															saveInParam.setOrderItemList(orderItemList);
															saveInParam.setOrderResource(orderResource);
															saveInParam.setOrderSum(orderSum);
															saveInParam.setPayType(payType);
															saveInParam.setPickTime(pickTime);
															saveInParam.setPickType(pickType);
															saveInParam.setReceiveTime(receiveTime);
															saveInParam.setRecordId(recordId);
															saveInParam.setRemark(remark);
															saveInParam.setStoreId(storeId);
															saveInParam.setSum(sum);
															saveInParam.setType(type);
															saveInParam.setUserId(userId);
															SaveTradeOrderInfo orderInfo = saveTradeOrderInfo(
																	saveInParam); // 保存订单数据

															saveOrderInfo.setCalculateOrderVo(orderVo);
															saveOrderInfo.setCalculatePrivilege(privilege);
															saveOrderInfo.setCalculateTradeOrderItem(calculateItem);
															saveOrderInfo.setSaveTradeOrderInfo(orderInfo);
														}
													} else if (types == 0) { // 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
														CalculateOrderVoInParam calInParam = new CalculateOrderVoInParam();
														calInParam.setArray(array);
														calInParam.setOrderId(order.getId());
														calInParam.setStoreId(storeId);
														calInParam.setUserId(userId);
														CalculateOrderVo orderVo = calculateOrder(calInParam); // 单品款数*乘以价格的集合

														sum = orderVo.getSum(); // 商品总金额
														itemList = orderVo.getItemList();

														CalculatePrivilegeInParam priInParam = new CalculatePrivilegeInParam();
														priInParam.setActivityId(activityId);
														priInParam.setActivityItemId(activityItemId);
														priInParam.setActType(actType);
														priInParam.setCouponsType(couponsType);
														priInParam.setItemList(itemList);
														priInParam.setOrderId(order.getId());
														priInParam.setStoreId(storeId);
														priInParam.setSum(sum);
														priInParam.setUserId(userId);
														CalculatePrivilege privilege = getLastPrivilege(priInParam); // 计算优惠

														CalCulateOrderAmountInParam amountInParam = new CalCulateOrderAmountInParam();
														amountInParam.setActivityId(activityId);
														amountInParam.setActivityType(activityType);
														amountInParam.setActType(actType);
														amountInParam.setAfterItemList(itemList);
														amountInParam.setArray(array);
														amountInParam.setOrderId(order.getId());
														amountInParam.setStoreId(storeId);
														amountInParam.setUserId(userId);
														CalculateTradeOrderItem calculateItem = calCulateOrderAmount(
																amountInParam); // 计算优惠之后的订单金额

														orderSum = privilege.getOrderSum();
														orderItemList = calculateItem.getOrderItemList();

														SaveTradeOrderInfoInParam saveInParam = new SaveTradeOrderInfoInParam();
														saveInParam.setActivitId(activityId);
														saveInParam.setActivityItemId(activityItemId);
														saveInParam.setActType(actType);
														saveInParam.setAddressId(addressId);
														saveInParam.setCouponsType(couponsType);
														saveInParam.setInvoiceContent(invoiceContent);
														saveInParam.setInvoiceHead(invoiceHead);
														saveInParam.setIsInvoice(isInvoice);
														saveInParam.setItemList(itemList);
														saveInParam.setOrder(order);
														saveInParam.setOrderItemList(orderItemList);
														saveInParam.setOrderResource(orderResource);
														saveInParam.setOrderSum(orderSum);
														saveInParam.setPayType(payType);
														saveInParam.setPickTime(pickTime);
														saveInParam.setPickType(pickType);
														saveInParam.setReceiveTime(receiveTime);
														saveInParam.setRecordId(recordId);
														saveInParam.setRemark(remark);
														saveInParam.setStoreId(storeId);
														saveInParam.setSum(sum);
														saveInParam.setType(type);
														saveInParam.setUserId(userId);
														SaveTradeOrderInfo orderInfo = saveTradeOrderInfo(saveInParam); // 保存订单数据

														saveOrderInfo.setCalculateOrderVo(orderVo);
														saveOrderInfo.setCalculatePrivilege(privilege);
														saveOrderInfo.setCalculateTradeOrderItem(calculateItem);
														saveOrderInfo.setSaveTradeOrderInfo(orderInfo);
													}
												}
											}

										} else if (tradeMax <= 0) { // 不限款
											int locked = storeSkuStock.getLocked();
											if (locked <= 0) { // 库存不足
												isStock = 0;
											} else if (locked < toBuyNum) { // 计算结果、库存不足
												isStock = 0;
											} else if (locked >= toBuyNum) { // 库存判断通过,查看选中的特惠活动是否失效
												int types = Integer.valueOf(activityType);
												if (types == 1) { // 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
													ActivityCouponsRecord conpons = activityCouponsRecordService
															.selectByPrimaryKey(recordId);
													if (conpons.getStatus().ordinal() == 2) {
														isValid = 0;
													} else { // 计算商品金额
														CalculateOrderVoInParam calInParam = new CalculateOrderVoInParam();
														calInParam.setArray(array);
														calInParam.setOrderId(order.getId());
														calInParam.setStoreId(storeId);
														calInParam.setUserId(userId);
														CalculateOrderVo orderVo = calculateOrder(calInParam); // 单品款数*乘以价格的集合

														sum = orderVo.getSum(); // 商品总金额
														itemList = orderVo.getItemList();

														CalculatePrivilegeInParam priInParam = new CalculatePrivilegeInParam();
														priInParam.setActivityId(activityId);
														priInParam.setActivityItemId(activityItemId);
														priInParam.setActType(actType);
														priInParam.setCouponsType(couponsType);
														priInParam.setItemList(itemList);
														priInParam.setOrderId(order.getId());
														priInParam.setStoreId(storeId);
														priInParam.setSum(sum);
														priInParam.setUserId(userId);
														CalculatePrivilege privilege = getLastPrivilege(priInParam); // 计算优惠

														CalCulateOrderAmountInParam amountInParam = new CalCulateOrderAmountInParam();
														amountInParam.setActivityId(activityId);
														amountInParam.setActivityType(activityType);
														amountInParam.setActType(actType);
														amountInParam.setAfterItemList(itemList);
														amountInParam.setArray(array);
														amountInParam.setOrderId(order.getId());
														amountInParam.setStoreId(storeId);
														amountInParam.setUserId(userId);
														CalculateTradeOrderItem calculateItem = calCulateOrderAmount(
																amountInParam); // 计算优惠之后的订单金额

														orderSum = privilege.getOrderSum();
														orderItemList = calculateItem.getOrderItemList();

														SaveTradeOrderInfoInParam saveInParam = new SaveTradeOrderInfoInParam();
														saveInParam.setActivitId(activityId);
														saveInParam.setActivityItemId(activityItemId);
														saveInParam.setActType(actType);
														saveInParam.setAddressId(addressId);
														saveInParam.setCouponsType(couponsType);
														saveInParam.setInvoiceContent(invoiceContent);
														saveInParam.setInvoiceHead(invoiceHead);
														saveInParam.setIsInvoice(isInvoice);
														saveInParam.setItemList(itemList);
														saveInParam.setOrder(order);
														saveInParam.setOrderItemList(orderItemList);
														saveInParam.setOrderResource(orderResource);
														saveInParam.setOrderSum(orderSum);
														saveInParam.setPayType(payType);
														saveInParam.setPickTime(pickTime);
														saveInParam.setPickType(pickType);
														saveInParam.setReceiveTime(receiveTime);
														saveInParam.setRecordId(recordId);
														saveInParam.setRemark(remark);
														saveInParam.setStoreId(storeId);
														saveInParam.setSum(sum);
														saveInParam.setType(type);
														saveInParam.setUserId(userId);
														SaveTradeOrderInfo orderInfo = saveTradeOrderInfo(saveInParam); // 保存订单数据

														saveOrderInfo.setCalculateOrderVo(orderVo);
														saveOrderInfo.setCalculatePrivilege(privilege);
														saveOrderInfo.setCalculateTradeOrderItem(calculateItem);
														saveOrderInfo.setSaveTradeOrderInfo(orderInfo);
													}
												} else if (types == 2 || types == 3) { // 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
													ActivityDiscount discount = activityDiscountService
															.selectByPrimaryKey(activityId);
													int status = discount.getStatus().ordinal();
													if (status != 1) {
														isValid = 0;
													} else { // 计算商品金额
														CalculateOrderVoInParam calInParam = new CalculateOrderVoInParam();
														calInParam.setArray(array);
														calInParam.setOrderId(order.getId());
														calInParam.setStoreId(storeId);
														calInParam.setUserId(userId);
														CalculateOrderVo orderVo = calculateOrder(calInParam); // 单品款数*乘以价格的集合

														sum = orderVo.getSum(); // 商品总金额
														itemList = orderVo.getItemList();

														CalculatePrivilegeInParam priInParam = new CalculatePrivilegeInParam();
														priInParam.setActivityId(activityId);
														priInParam.setActivityItemId(activityItemId);
														priInParam.setActType(actType);
														priInParam.setCouponsType(couponsType);
														priInParam.setItemList(itemList);
														priInParam.setOrderId(order.getId());
														priInParam.setStoreId(storeId);
														priInParam.setSum(sum);
														priInParam.setUserId(userId);
														CalculatePrivilege privilege = getLastPrivilege(priInParam); // 计算优惠

														CalCulateOrderAmountInParam amountInParam = new CalCulateOrderAmountInParam();
														amountInParam.setActivityId(activityId);
														amountInParam.setActivityType(activityType);
														amountInParam.setActType(actType);
														amountInParam.setAfterItemList(itemList);
														amountInParam.setArray(array);
														amountInParam.setOrderId(order.getId());
														amountInParam.setStoreId(storeId);
														amountInParam.setUserId(userId);
														CalculateTradeOrderItem calculateItem = calCulateOrderAmount(
																amountInParam); // 计算优惠之后的订单金额

														orderSum = privilege.getOrderSum();
														orderItemList = calculateItem.getOrderItemList();

														SaveTradeOrderInfoInParam saveInParam = new SaveTradeOrderInfoInParam();
														saveInParam.setActivitId(activityId);
														saveInParam.setActivityItemId(activityItemId);
														saveInParam.setActType(actType);
														saveInParam.setAddressId(addressId);
														saveInParam.setCouponsType(couponsType);
														saveInParam.setInvoiceContent(invoiceContent);
														saveInParam.setInvoiceHead(invoiceHead);
														saveInParam.setIsInvoice(isInvoice);
														saveInParam.setItemList(itemList);
														saveInParam.setOrder(order);
														saveInParam.setOrderItemList(orderItemList);
														saveInParam.setOrderResource(orderResource);
														saveInParam.setOrderSum(orderSum);
														saveInParam.setPayType(payType);
														saveInParam.setPickTime(pickTime);
														saveInParam.setPickType(pickType);
														saveInParam.setReceiveTime(receiveTime);
														saveInParam.setRecordId(recordId);
														saveInParam.setRemark(remark);
														saveInParam.setStoreId(storeId);
														saveInParam.setSum(sum);
														saveInParam.setType(type);
														saveInParam.setUserId(userId);
														SaveTradeOrderInfo orderInfo = saveTradeOrderInfo(saveInParam); // 保存订单数据

														saveOrderInfo.setCalculateOrderVo(orderVo);
														saveOrderInfo.setCalculatePrivilege(privilege);
														saveOrderInfo.setCalculateTradeOrderItem(calculateItem);
														saveOrderInfo.setSaveTradeOrderInfo(orderInfo);
													}
												} else if (types == 0) {
													CalculateOrderVoInParam calInParam = new CalculateOrderVoInParam();
													calInParam.setArray(array);
													calInParam.setOrderId(order.getId());
													calInParam.setStoreId(storeId);
													calInParam.setUserId(userId);
													CalculateOrderVo orderVo = calculateOrder(calInParam); // 单品款数*乘以价格的集合

													sum = orderVo.getSum(); // 商品总金额
													itemList = orderVo.getItemList();

													CalculatePrivilegeInParam priInParam = new CalculatePrivilegeInParam();
													priInParam.setActivityId(activityId);
													priInParam.setActivityItemId(activityItemId);
													priInParam.setActType(actType);
													priInParam.setCouponsType(couponsType);
													priInParam.setItemList(itemList);
													priInParam.setOrderId(order.getId());
													priInParam.setStoreId(storeId);
													priInParam.setSum(sum);
													priInParam.setUserId(userId);
													CalculatePrivilege privilege = getLastPrivilege(priInParam); // 计算优惠

													CalCulateOrderAmountInParam amountInParam = new CalCulateOrderAmountInParam();
													amountInParam.setActivityId(activityId);
													amountInParam.setActivityType(activityType);
													amountInParam.setActType(actType);
													amountInParam.setAfterItemList(itemList);
													amountInParam.setArray(array);
													amountInParam.setOrderId(order.getId());
													amountInParam.setStoreId(storeId);
													amountInParam.setUserId(userId);
													CalculateTradeOrderItem calculateItem = calCulateOrderAmount(
															amountInParam); // 计算优惠之后的订单金额

													orderSum = privilege.getOrderSum();
													orderItemList = calculateItem.getOrderItemList();

													SaveTradeOrderInfoInParam saveInParam = new SaveTradeOrderInfoInParam();
													saveInParam.setActivitId(activityId);
													saveInParam.setActivityItemId(activityItemId);
													saveInParam.setActType(actType);
													saveInParam.setAddressId(addressId);
													saveInParam.setCouponsType(couponsType);
													saveInParam.setInvoiceContent(invoiceContent);
													saveInParam.setInvoiceHead(invoiceHead);
													saveInParam.setIsInvoice(isInvoice);
													saveInParam.setItemList(itemList);
													saveInParam.setOrder(order);
													saveInParam.setOrderItemList(orderItemList);
													saveInParam.setOrderResource(orderResource);
													saveInParam.setOrderSum(orderSum);
													saveInParam.setPayType(payType);
													saveInParam.setPickTime(pickTime);
													saveInParam.setPickType(pickType);
													saveInParam.setReceiveTime(receiveTime);
													saveInParam.setRecordId(recordId);
													saveInParam.setRemark(remark);
													saveInParam.setStoreId(storeId);
													saveInParam.setSum(sum);
													saveInParam.setType(type);
													saveInParam.setUserId(userId);
													SaveTradeOrderInfo orderInfo = saveTradeOrderInfo(saveInParam); // 保存订单数据

													saveOrderInfo.setCalculateOrderVo(orderVo);
													saveOrderInfo.setCalculatePrivilege(privilege);
													saveOrderInfo.setCalculateTradeOrderItem(calculateItem);
													saveOrderInfo.setSaveTradeOrderInfo(orderInfo);
												}
											}
										}
									}
								}
							} else if (isPrivilege.equals("0")) {
								int sellabed = storeSkuStock.getSellable(); // 商品可销售库存
								if (sellabed <= 0) {
									isStock = 0;
								} else if (sellabed < isBuyNums) {
									isStock = 0;
								} else {
									int types = Integer.valueOf(activityType);
									if (types == 1) { // 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
										ActivityCouponsRecord conpons = activityCouponsRecordService
												.selectByPrimaryKey(recordId);
										if (conpons.getStatus().ordinal() == 2) {
											isValid = 0;
										} else { // 计算商品金额
											CalculateOrderVoInParam calInParam = new CalculateOrderVoInParam();
											calInParam.setArray(array);
											calInParam.setOrderId(order.getId());
											calInParam.setStoreId(storeId);
											calInParam.setUserId(userId);
											CalculateOrderVo orderVo = calculateOrder(calInParam); // 单品款数*乘以价格的集合

											sum = orderVo.getSum(); // 商品总金额
											itemList = orderVo.getItemList();

											CalculatePrivilegeInParam priInParam = new CalculatePrivilegeInParam();
											priInParam.setActivityId(activityId);
											priInParam.setActivityItemId(activityItemId);
											priInParam.setActType(actType);
											priInParam.setCouponsType(couponsType);
											priInParam.setItemList(itemList);
											priInParam.setOrderId(order.getId());
											priInParam.setStoreId(storeId);
											priInParam.setSum(sum);
											priInParam.setUserId(userId);
											CalculatePrivilege privilege = getLastPrivilege(priInParam); // 计算优惠

											CalCulateOrderAmountInParam amountInParam = new CalCulateOrderAmountInParam();
											amountInParam.setActivityId(activityId);
											amountInParam.setActivityType(activityType);
											amountInParam.setActType(actType);
											amountInParam.setAfterItemList(itemList);
											amountInParam.setArray(array);
											amountInParam.setOrderId(order.getId());
											amountInParam.setStoreId(storeId);
											amountInParam.setUserId(userId);
											CalculateTradeOrderItem calculateItem = calCulateOrderAmount(amountInParam); // 计算优惠之后的订单金额

											orderSum = privilege.getOrderSum();
											orderItemList = calculateItem.getOrderItemList();

											SaveTradeOrderInfoInParam saveInParam = new SaveTradeOrderInfoInParam();
											saveInParam.setActivitId(activityId);
											saveInParam.setActivityItemId(activityItemId);
											saveInParam.setActType(actType);
											saveInParam.setAddressId(addressId);
											saveInParam.setCouponsType(couponsType);
											saveInParam.setInvoiceContent(invoiceContent);
											saveInParam.setInvoiceHead(invoiceHead);
											saveInParam.setIsInvoice(isInvoice);
											saveInParam.setItemList(itemList);
											saveInParam.setOrder(order);
											saveInParam.setOrderItemList(orderItemList);
											saveInParam.setOrderResource(orderResource);
											saveInParam.setOrderSum(orderSum);
											saveInParam.setPayType(payType);
											saveInParam.setPickTime(pickTime);
											saveInParam.setPickType(pickType);
											saveInParam.setReceiveTime(receiveTime);
											saveInParam.setRecordId(recordId);
											saveInParam.setRemark(remark);
											saveInParam.setStoreId(storeId);
											saveInParam.setSum(sum);
											saveInParam.setType(type);
											saveInParam.setUserId(userId);
											SaveTradeOrderInfo orderInfo = saveTradeOrderInfo(saveInParam); // 保存订单数据

											saveOrderInfo.setCalculateOrderVo(orderVo);
											saveOrderInfo.setCalculatePrivilege(privilege);
											saveOrderInfo.setCalculateTradeOrderItem(calculateItem);
											saveOrderInfo.setSaveTradeOrderInfo(orderInfo);
										}
									} else if (types == 2 || types == 3) { // 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
										ActivityDiscount discount = activityDiscountService
												.selectByPrimaryKey(activityId);
										int status = discount.getStatus().ordinal();
										if (status != 1) {
											isValid = 0;
										} else {
											CalculateOrderVoInParam calInParam = new CalculateOrderVoInParam();
											calInParam.setArray(array);
											calInParam.setOrderId(order.getId());
											calInParam.setStoreId(storeId);
											calInParam.setUserId(userId);
											CalculateOrderVo orderVo = calculateOrder(calInParam); // 单品款数*乘以价格的集合

											sum = orderVo.getSum(); // 商品总金额
											itemList = orderVo.getItemList();

											CalculatePrivilegeInParam priInParam = new CalculatePrivilegeInParam();
											priInParam.setActivityId(activityId);
											priInParam.setActivityItemId(activityItemId);
											priInParam.setActType(actType);
											priInParam.setCouponsType(couponsType);
											priInParam.setItemList(itemList);
											priInParam.setOrderId(order.getId());
											priInParam.setStoreId(storeId);
											priInParam.setSum(sum);
											priInParam.setUserId(userId);
											CalculatePrivilege privilege = getLastPrivilege(priInParam); // 计算优惠

											CalCulateOrderAmountInParam amountInParam = new CalCulateOrderAmountInParam();
											amountInParam.setActivityId(activityId);
											amountInParam.setActivityType(activityType);
											amountInParam.setActType(actType);
											amountInParam.setAfterItemList(itemList);
											amountInParam.setArray(array);
											amountInParam.setOrderId(order.getId());
											amountInParam.setStoreId(storeId);
											amountInParam.setUserId(userId);
											CalculateTradeOrderItem calculateItem = calCulateOrderAmount(amountInParam); // 计算优惠之后的订单金额

											orderSum = privilege.getOrderSum();
											orderItemList = calculateItem.getOrderItemList();

											SaveTradeOrderInfoInParam saveInParam = new SaveTradeOrderInfoInParam();
											saveInParam.setActivitId(activityId);
											saveInParam.setActivityItemId(activityItemId);
											saveInParam.setActType(actType);
											saveInParam.setAddressId(addressId);
											saveInParam.setCouponsType(couponsType);
											saveInParam.setInvoiceContent(invoiceContent);
											saveInParam.setInvoiceHead(invoiceHead);
											saveInParam.setIsInvoice(isInvoice);
											saveInParam.setItemList(itemList);
											saveInParam.setOrder(order);
											saveInParam.setOrderItemList(orderItemList);
											saveInParam.setOrderResource(orderResource);
											saveInParam.setOrderSum(orderSum);
											saveInParam.setPayType(payType);
											saveInParam.setPickTime(pickTime);
											saveInParam.setPickType(pickType);
											saveInParam.setReceiveTime(receiveTime);
											saveInParam.setRecordId(recordId);
											saveInParam.setRemark(remark);
											saveInParam.setStoreId(storeId);
											saveInParam.setSum(sum);
											saveInParam.setType(type);
											saveInParam.setUserId(userId);
											SaveTradeOrderInfo orderInfo = saveTradeOrderInfo(saveInParam); // 保存订单数据

											saveOrderInfo.setCalculateOrderVo(orderVo);
											saveOrderInfo.setCalculatePrivilege(privilege);
											saveOrderInfo.setCalculateTradeOrderItem(calculateItem);
											saveOrderInfo.setSaveTradeOrderInfo(orderInfo);
										}
									} else if (types == 0) { // 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
										CalculateOrderVoInParam calInParam = new CalculateOrderVoInParam();
										calInParam.setArray(array);
										calInParam.setOrderId(order.getId());
										calInParam.setStoreId(storeId);
										calInParam.setUserId(userId);
										CalculateOrderVo orderVo = calculateOrder(calInParam); // 单品款数*乘以价格的集合

										sum = orderVo.getSum(); // 商品总金额
										itemList = orderVo.getItemList();

										CalculatePrivilegeInParam priInParam = new CalculatePrivilegeInParam();
										priInParam.setActivityId(activityId);
										priInParam.setActivityItemId(activityItemId);
										priInParam.setActType(actType);
										priInParam.setCouponsType(couponsType);
										priInParam.setItemList(itemList);
										priInParam.setOrderId(order.getId());
										priInParam.setStoreId(storeId);
										priInParam.setSum(sum);
										priInParam.setUserId(userId);
										CalculatePrivilege privilege = getLastPrivilege(priInParam); // 计算优惠

										CalCulateOrderAmountInParam amountInParam = new CalCulateOrderAmountInParam();
										amountInParam.setActivityId(activityId);
										amountInParam.setActivityType(activityType);
										amountInParam.setActType(actType);
										amountInParam.setAfterItemList(itemList);
										amountInParam.setArray(array);
										amountInParam.setOrderId(order.getId());
										amountInParam.setStoreId(storeId);
										amountInParam.setUserId(userId);
										CalculateTradeOrderItem calculateItem = calCulateOrderAmount(amountInParam); // 计算优惠之后的订单金额

										orderSum = privilege.getOrderSum();
										orderItemList = calculateItem.getOrderItemList();

										SaveTradeOrderInfoInParam saveInParam = new SaveTradeOrderInfoInParam();
										saveInParam.setActivitId(activityId);
										saveInParam.setActivityItemId(activityItemId);
										saveInParam.setActType(actType);
										saveInParam.setAddressId(addressId);
										saveInParam.setCouponsType(couponsType);
										saveInParam.setInvoiceContent(invoiceContent);
										saveInParam.setInvoiceHead(invoiceHead);
										saveInParam.setIsInvoice(isInvoice);
										saveInParam.setItemList(itemList);
										saveInParam.setOrder(order);
										saveInParam.setOrderItemList(orderItemList);
										saveInParam.setOrderResource(orderResource);
										saveInParam.setOrderSum(orderSum);
										saveInParam.setPayType(payType);
										saveInParam.setPickTime(pickTime);
										saveInParam.setPickType(pickType);
										saveInParam.setReceiveTime(receiveTime);
										saveInParam.setRecordId(recordId);
										saveInParam.setRemark(remark);
										saveInParam.setStoreId(storeId);
										saveInParam.setSum(sum);
										saveInParam.setType(type);
										saveInParam.setUserId(userId);
										SaveTradeOrderInfo orderInfo = saveTradeOrderInfo(saveInParam); // 保存订单数据

										saveOrderInfo.setCalculateOrderVo(orderVo);
										saveOrderInfo.setCalculatePrivilege(privilege);
										saveOrderInfo.setCalculateTradeOrderItem(calculateItem);
										saveOrderInfo.setSaveTradeOrderInfo(orderInfo);
									}
								}
							}
						}
					}
				}
			}
		}
		/*************** 解析获取参数 end ******************/
		JSONObject json = new JSONObject();
		int isOrder = 0;
		if (isAcceptOrder != 0 && isClosed != 0 && isBusiness != 0 && isChanges == 1 && isBuy == 1 && isSize == 1
				&& isStock == 1 && isValid == 1) {
			isOrder = 1;
			// 进行数据保存操作
			BigDecimal actualAmount = CalculateMoneyUtil
					.getOrderBigMoney(saveOrderInfo.getSaveTradeOrderInfo().getOrder().getActualAmount());

			updateGoodsStoreSkuStock(array, storeId, userId, order.getId()); // 更新库存
			// 添加支付金额为零的逻辑判断 todo
			tradeOrderService.insertTradeOrder(saveOrderInfo.getSaveTradeOrderInfo().getOrder());
			Map<String, Object> activityMap = new HashMap<String, Object>();
			activityMap.put("orderId", order.getId());
			activityMap.put("id", recordId);
			activityMap.put("collectUserId", order.getUserPhone());
			activityMap.put("couponsId", activityItemId);
			activityMap.put("collectType", couponsType);
			if (!activityType.equals("0") && !couponsType.equals("")) {
				activityCouponsRecordService.updateActivityCouponsStatus(activityMap); // 更新代金券状态
				activityCouponsService.updateActivityCouponsUsedNum(activityItemId); // 修改代金券使用数量
			}

			if (saveOrderInfo.getCalculateOrderVo().getRecordList() != null) {
				if (saveOrderInfo.getCalculateOrderVo().getRecordList().size() > 0) {

					List<ActivitySaleRecord> recordList = saveOrderInfo.getCalculateOrderVo().getRecordList();
					for (int i = 0; i < recordList.size(); i++) {
						ActivitySaleRecord record = recordList.get(i);
						activitySaleRecordService.insertSelective(record);
					}
				}
			}
			if (saveOrderInfo.getCalculatePrivilege().getDiscountRecord() != null) {
				activityDiscountRecordService.insertRecord(saveOrderInfo.getCalculatePrivilege().getDiscountRecord());
			}
			json.put("orderId", order.getId()); // 订单ID
			json.put("orderNo", order.getOrderNo()); // 订单编号

			System.out.println("before------>" + actualAmount);
			json.put("orderPrice", actualAmount.toString()); // 订单价格
			json.put("tradeNum", saveOrderInfo.getSaveTradeOrderInfo().getOrder().getTradeNum()); // 订单交易号

		}
		// 1:货到付款、0：在线支付
		if (payType.equals("1")) {
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_delivery_timeout, order.getId());
		} else {
			// 发送消息
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_pay_timeout, order.getId());
		}

		json.put("isOrder", isOrder); // 是否可以结算(0:否,1:是)
		json.put("isClosed", isClosed); // 关闭/开启店铺(0:关闭,1:开启)
		json.put("isBusiness", isBusiness); // 是否营业(0:暂停营业,1:开始营业)
		json.put("isAcceptOrder", isAcceptOrder); // 非营业时段是否接单(0:否,1:是)
		json.put("isSize", isSize); // 0:达到限购,1:未达到

		json.put("isChanges", isChanges); // 商品是否发生变化1:未变化,0:已变化
		json.put("goods", skuToAppVoList); // 商品列表
		json.put("detail", objList);
		json.put("isBuy", isBuy);
		json.put("isStock", isStock); // 库存是否满足1:库存足,0:库存不足
		json.put("isValid", isValid); // 优惠是否失效
		json.put("isRest", isRest); // 1:营业中,0:休息中
		json.put("limit", isPushSize); // 是否购买超过限款1:未超过限款,0:已超过限款
		json.put("limitTime", 60 * 30); // 订单倒计时

		return json;
	}

	private SaveTradeOrderInfo saveTradeOrderInfo(SaveTradeOrderInfoInParam saveInParam) throws Exception {

		logger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		logger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		logger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		logger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		logger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		logger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		logger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		logger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

		SaveTradeOrderInfo orderInfo = new SaveTradeOrderInfo();
		TradeOrder order = new TradeOrder();
		StoreInfo store = storeInfoService.selectStoreBaseInfoById(saveInParam.getStoreId());
		List<OrderItem> afterItemList = new ArrayList<OrderItem>();

		BigDecimal orderSum = saveInParam.getOrderSum();
		BigDecimal sum = saveInParam.getSum();

		String myStoreName = store.getStoreName();
		BigDecimal startPrice = store.getStoreInfoExt().getStartPrice();
		String startTime = store.getStoreInfoExt().getServiceStartTime();
		String endTime = store.getStoreInfoExt().getServiceEndTime();
		String storeId = saveInParam.getStoreId();

		order.setId(saveInParam.getOrder().getId());

		String orderRes = saveInParam.getOrderResource();
		logger.info("<><><><><><><><><><><><><><><><><><><>");
		logger.info("<><><><><><><><><><><><><><><><><><><>");
		logger.info("<><><><><><><><><><><><><><><><><><><>");
		logger.info("<><><><><><><><><><><><><><><><><><><>");
		logger.info("<><><><><><><><><><><><><><><><><><><>");
		logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		logger.info("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		logger.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^" + orderRes);
		if (orderRes.equals("1")) {
			order.setOrderResource(OrderResourceEnum.WECHAT);
			order.setStatus(OrderStatusEnum.UNPAID);
		} else if (orderRes.equals("0")) {
			order.setOrderResource(OrderResourceEnum.YSCAPP);
		} else if (orderRes.equals("2")) {
			order.setOrderResource(OrderResourceEnum.POS);
		}

		BigDecimal freight = null;
		if (store.getStoreInfoExt().getFreight() != null && !store.getStoreInfoExt().getFreight().equals("")) {
			freight = store.getStoreInfoExt().getFreight();
		}

		String orderNo = "";
		orderNo = generateNumericalService.generateNumber("XS"); // 订单编号生成
		logger.info("~~~~~~~~生成订单编号：~~~~~~~~~~" + orderNo);

		int pick = Integer.valueOf(saveInParam.getPickType()); // 提货类型
		TradeOrderLogistics orderLogistics = new TradeOrderLogistics();
		StoreInfo storeInfo = new StoreInfo();
		if (pick == 0) { // 送货上门
			if (startPrice != null) {
				// 判断商品总金额是否达到起送金额 后台判断
				if (saveInParam.getSum().compareTo(startPrice) == -1) { // 如果商品总金额没有达到起送金额,则成订单总金额=商品通过优惠算出来的总金额+运费
					orderSum = orderSum.add(freight);
					sum = sum.add(freight);
					order.setFare(freight); // 运费
				}
			}
			MemberConsigneeAddress address = memberConsigneeAddressService
					.selectAddressById(saveInParam.getAddressId()); // 获取买家收货地址

			orderLogistics.setId(UuidUtils.getUuid());
			orderLogistics.setConsigneeName(address.getConsigneeName());
			orderLogistics.setMobile(address.getMobile());
			orderLogistics.setAddress(address.getAddress());
			orderLogistics.setArea(address.getArea());

			order.setSellerId(storeId);

			orderLogistics.setOrderId(order.getId());
			orderLogistics.setAreaId(address.getAreaId());
			orderLogistics.setProvinceId(address.getProvinceId());
			orderLogistics.setCityId(address.getCityId());
			orderLogistics.setZipCode(address.getZipCode());
			String pickTime = saveInParam.getPickTime();
			String payType = saveInParam.getPayType(); // 支付方式：1:货到付款、0：在线支付

			if (payType.equals("1")) {
				if (!pickTime.equals("")) {
					order.setPickUpTime(pickTime);
				} else {
					order.setPickUpTime("立即配送");
				}
			} else if (payType.equals("0")) {
				if (!pickTime.equals("")) {
					order.setPickUpTime(pickTime);
				} else {
					order.setPickUpTime("立即配送");
				}
			} else if (payType.equals("6")) {
				if (!pickTime.equals("")) {
					order.setPickUpTime(pickTime);
				} else {
					order.setPickUpTime("立即配送");
				}
			}
			order.setTradeOrderLogistics(orderLogistics);
		} else if (pick == 1) { // 到店自提

			TradeOrderPay orderPay = new TradeOrderPay();

			orderPay.setId(UuidUtils.getUuid());
			orderPay.setCreateTime(new Date());
			orderPay.setOrderId(order.getId());
			orderPay.setPayTime(new Date());
			orderPay.setPayAmount(orderSum);

			orderPay.setPayType(PayTypeEnum.CASH);
			String receiveTime = saveInParam.getReceiveTime();
			if (!receiveTime.equals("")) {
				storeInfo = storeInfoService.selectDefaultAddressById(storeId); // 获取默认地址
				String defaultAddressId = storeInfo.getMemberConsignee().getId();
				order.setPickUpId(defaultAddressId);
				order.setPickUpTime(receiveTime);
			} else {
				SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
				String attrTime = formats.format(date);
				String pcTime = attrTime + " " + startTime + "-" + endTime;
				order.setPickUpTime(pcTime);
			}
			order.setFare(new BigDecimal("0.00"));
		}

		TradeOrderInvoice orderInvoice = new TradeOrderInvoice();

		int invoice = Integer.valueOf(saveInParam.getIsInvoice()); // 是否有发票标识
		if (invoice == 1) {
			// 有发票
			orderInvoice.setId(UuidUtils.getUuid());
			orderInvoice.setId(order.getId());
			orderInvoice.setOrderId(order.getId());
			orderInvoice.setHead(saveInParam.getInvoiceHead());
			orderInvoice.setContext(saveInParam.getInvoiceContent());
			order.setTradeOrderInvoice(orderInvoice);
		}

		String userId = saveInParam.getUserId();
		SysBuyerUser buyerUser = sysBuyerUserMapper.selectByPrimaryKey(userId);
		if (buyerUser == null) {
			logger.error("查询买家信息", "buyerUser 为空-------->" + CodeStatistical.getLineInfo());
			logger.info("查询买家信息 buyerUser 为空-------->" + CodeStatistical.getLineInfo());
			throw new Exception("查询查询买家信息异常：buyerUser 为空-------->" + CodeStatistical.getLineInfo());
		}

		order.setUserPhone(buyerUser.getPhone());
		order.setUserId(userId);
		order.setStoreName(myStoreName);
		order.setStoreId(storeId);
		order.setUpdateTime(new Date());

		order.setPid("0");
		order.setOrderNo(orderNo);

		int picType = Integer.valueOf(saveInParam.getPickType()); // 提货类型(0:送货上门,1:到店自提,2:团购服务)
		int orderType = Integer.valueOf(saveInParam.getType()); // 订单类型

		if (orderType == 0) {
			order.setType(OrderTypeEnum.PHYSICAL_ORDER);
		} else if (orderType == 1) {
			order.setType(OrderTypeEnum.SERVICE_ORDER);
		}
		int actType = saveInParam.getActType();
		List<OrderItem> itemList = saveInParam.getItemList();
		// 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		String activityId = saveInParam.getActivitId();
		String activityItemId = saveInParam.getActivityItemId();
		if (actType == 1 || actType == 2) {
			BigDecimal big = new BigDecimal("0.00");
			for (int j = 0; j < itemList.size(); j++) {
				OrderItem orderItem = itemList.get(j);
				BigDecimal bigPrice = new BigDecimal("0.00");
				if (orderItem.getPrivilege() != null) {
					bigPrice = orderItem.getPrivilege();
				}
				big = big.add(bigPrice);
			}
			order.setPreferentialPrice(big); // 优惠金额
		} else if (actType == 3) {
			BigDecimal big = new BigDecimal("0.00");
			for (int j = 0; j < itemList.size(); j++) {
				OrderItem orderItem = itemList.get(j);
				BigDecimal bigPrice = new BigDecimal("0.00");
				if (orderItem.getPrivilege() != null) {
					bigPrice = orderItem.getPrivilege();
				}
				big = big.add(bigPrice);
			}
			order.setPreferentialPrice(big); // 优惠金额
		}

		if (actType == 0) {
			order.setIncome(orderSum);
		} else if (actType == 1) {
			BigDecimal big = new BigDecimal("0.00");
			for (int j = 0; j < itemList.size(); j++) {
				OrderItem orderItem = itemList.get(j);
				BigDecimal bigPrice = new BigDecimal("0.00");
				if (orderItem.getPrivilege() != null) {
					bigPrice = orderItem.getPrivilege();
				}
				big = big.add(bigPrice);
			}
			order.setIncome(orderSum.add(big));
		} else if (actType == 2) { // 满减活动
			Map<String, String> discountStr = new HashMap<String, String>();
			discountStr.put("discountId", activityId);
			discountStr.put("id", activityItemId);
			ActivityDiscountCondition conditions = activityDiscountService.getDiscountConditions(discountStr); // 查询满减活动对应的具体满减优惠
			ActivityDiscount discount = activityDiscountService.selectByPrimaryKey(activityId);
			String sId = discount.getStoreId();

			if (sId.equals("0")) { // 运营商发布的满减优惠活动
				BigDecimal disc = conditions.getDiscount(); // 取具体满减值
				afterItemList = DiscountCalculate.couponCalculate(itemList, disc);
				orderSum = sum.subtract(disc);
				BigDecimal big = new BigDecimal("0.00");
				for (int j = 0; j < itemList.size(); j++) {
					OrderItem orderItem = itemList.get(j);
					BigDecimal bigPrice = new BigDecimal("0.00");
					if (orderItem.getPrivilege() != null) {
						bigPrice = orderItem.getPrivilege();
					}
					big = big.add(bigPrice);
				}
				order.setIncome(orderSum.add(big));
			} else { // 店铺发布的满减活动
				BigDecimal disc = conditions.getDiscount(); // 取具体满减值
				afterItemList = DiscountCalculate.couponCalculate(itemList, disc);
				orderSum = sum.subtract(disc);
				BigDecimal big = new BigDecimal("0.00");
				for (int j = 0; j < itemList.size(); j++) {
					OrderItem orderItem = itemList.get(j);
					BigDecimal bigPrice = new BigDecimal("0.00");
					if (orderItem.getPrivilege() != null) {
						bigPrice = orderItem.getPrivilege();
					}
					big = big.add(bigPrice);
				}
				order.setIncome(orderSum);
			}

		} else if (actType == 3) { // 满折活动
			BigDecimal big = new BigDecimal("0.00");
			for (int j = 0; j < itemList.size(); j++) {
				OrderItem orderItem = itemList.get(j);
				BigDecimal bigPrice = new BigDecimal("0.00");
				if (orderItem.getPrivilege() != null) {
					bigPrice = orderItem.getPrivilege();
				}
				big = big.add(bigPrice);
			}
			order.setIncome(orderSum);
		}

		// 获得随机提货码
		String randomNo = RandomStringUtil.getRandomInt(6);
		order.setPickUpCode(randomNo);
		int intPayType = Integer.valueOf(saveInParam.getPayType());

		if (intPayType == 0) { // 支付方式：0:货到付款、1：在线支付
			order.setStatus(OrderStatusEnum.UNPAID);
			order.setPayWay(PayWayEnum.PAY_ONLINE);
		} else if (intPayType == 1) {
			order.setStatus(OrderStatusEnum.DROPSHIPPING);
			order.setPayWay(PayWayEnum.CASH_DELIERY);
		}
		order.setTotalAmount(sum); // 订单总金额
		if (actType == 0) {
			order.setActivityType(ActivityTypeEnum.NO_ACTIVITY);
			order.setActivityItemId(activityItemId);
		} else if (actType == 1) {
			order.setActivityType(ActivityTypeEnum.VONCHER);
			order.setActivityItemId(activityItemId);
		} else if (actType == 2) {
			order.setActivityType(ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES);
			order.setActivityItemId(activityItemId);
		} else if (actType == 3) {
			order.setActivityType(ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES);
			order.setActivityItemId(activityItemId);
		} else if (actType == 4) {
			order.setActivityType(ActivityTypeEnum.GROUP_ACTIVITY);
			order.setActivityItemId(activityItemId);
		}

		order.setActivityId(activityId);
		order.setRemark(saveInParam.getRemark());
		order.setInvoice(WithInvoiceEnum.HAS);
		order.setDisabled(Disabled.valid);
		order.setCreateTime(new Date());

		order.setIsShow(OrderIsShowEnum.yes);
		order.setPaymentStatus(PaymentStatusEnum.STAY_BACK);
		order.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);

		// 提货类型
		if (picType == 0) {
			order.setPickUpType(PickUpTypeEnum.DELIVERY_DOOR);
		} else if (picType == 1) {
			order.setPickUpType(PickUpTypeEnum.TO_STORE_PICKUP);
		}

		BigDecimal resultSum = CalculateMoneyUtil.getOrderBigMoney(orderSum);
		order.setActualAmount(resultSum); // 实付金额
		order.setTradeOrderItem(saveInParam.getOrderItemList());
		String tradeNum = TradeNumUtil.getTradeNum();
		order.setTradeNum(tradeNum);

		Map<String, Object> activityMap = new HashMap<String, Object>();
		activityMap.put("orderId", order.getId());
		activityMap.put("id", saveInParam.getRecordId());
		activityMap.put("collectUserId", buyerUser.getId());
		activityMap.put("couponsId", activityItemId);
		activityMap.put("collectType", saveInParam.getCouponsType());

		orderInfo.setOrder(order);
		orderInfo.setAfterItemList(afterItemList);
		return orderInfo;

	}

	private CalculateTradeOrderItem calCulateOrderAmount(CalCulateOrderAmountInParam amountInParam) throws Exception {

		CalculateTradeOrderItem calculateItem = new CalculateTradeOrderItem();
		List<TradeOrderItem> orderItemList = new ArrayList<TradeOrderItem>();
		JSONArray array = amountInParam.getArray();
		String storeId = amountInParam.getStoreId();
		String userId = amountInParam.getUserId();
		String orderId = amountInParam.getOrderId();
		String activityId = amountInParam.getActivityId();

		// 循环订单商品列表
		for (int k = 0; k < array.size(); k++) {
			TradeOrderItem tradeOrderItem = new TradeOrderItem();
			JSONObject obj = array.getJSONObject(k);
			String skuId = obj.getString("skuId"); // 商品ID
			String updateTime = obj.getString("updateTime"); // 修改时间
			String skuNum = obj.getString("skuNum"); // 商品购买数量
			int buySkuNum = Integer.valueOf(skuNum);

			GoodsStoreSku storeSkuPr = goodsStoreSkuService.selectGoodsStoreSkuDetail(skuId); // 查询店铺商品详细信息
			if (storeSkuPr == null) {
				logger.error("查询店铺商品详细信息", "storeSkuPr 为空-------->" + CodeStatistical.getLineInfo());
				logger.info("查询店铺商品详细信息 storeSkuPr 为空-------->" + CodeStatistical.getLineInfo());
				throw new ServiceException("查询店铺商品详细信息异常：storeSkuPr 为空-------->" + CodeStatistical.getLineInfo());
			}
			int actiType = storeSkuPr.getActivityType().ordinal(); // 活动类型(0无,1:团购,2:特惠)
			String actiId = storeSkuPr.getActivityId(); // 活动ID
			int saleStatus = 0; // 活动进行中标识
			if (actiType == 2) {
				ActivitySale acSale = activitySaleService.getAcSaleStatus(actiId);
				saleStatus = acSale.getStatus(); // 特惠活动状态(0:未开始,1:进行中,2:已结束,3:已关闭)
			}
			Map<String, Object> saleMap = new HashMap<String, Object>(); // 查询用户ID是否已购买特惠商品入参
			saleMap.put("storeId", storeId);
			saleMap.put("saleGoodsId", skuId);
			saleMap.put("userId", userId);
			saleMap.put("saleId", actiId);
			int isBuyCount = activitySaleRecordMapper.selectActivitySaleRecord(saleMap); // 查询用户ID是否已购买特惠商品

			Map<String, Object> map = new HashMap<String, Object>(); // 查询商品是否发生变化入参
			map.put("id", skuId);
			GoodsStoreSku storeSku = goodsStoreSkuService.getGoodsStoreSkuUpdateTime(map); // 查询商品是否发生变化
			if (storeSku == null) {
				logger.error("查询商品是否发生变化", "storeSku 为空-------->" + CodeStatistical.getLineInfo());
				logger.info("查询商品是否发生变化 storeSku 为空-------->" + CodeStatistical.getLineInfo());
				throw new ServiceException("查询商品是否发生变化异常: storeSku 为空-------->" + CodeStatistical.getLineInfo());
			}
			BigDecimal singlesPriceSum = null;

			// 活动类型(0无,2:特惠)
			if ((saleStatus != 1) || actiType == 0) {
				BigDecimal skuPrice = storeSku.getOnlinePrice(); // 商品价格
				BigDecimal bigSkuNum = new BigDecimal(skuNum);
				singlesPriceSum = bigSkuNum.multiply(skuPrice); // 单款商品总价格
				tradeOrderItem.setUnitPrice(skuPrice); // 订单项商品单价
				tradeOrderItem.setTotalAmount(singlesPriceSum);

				GoodsStoreSkuStock skuStock = goodsStoreSkuStockService.selectSingleSkuStock(skuId); // 查询便利店商品库存数量
				if (skuStock == null) {
					logger.error("查询便利店商品库存数量", "skuStock 为空------->" + CodeStatistical.getLineInfo());
					logger.info("查询便利店商品库存数量 skuStock 为空------->" + CodeStatistical.getLineInfo());
					throw new ServiceException("查询便利店商品库存数量异常：skuStock 为空-------->" + CodeStatistical.getLineInfo());
				}

			} else if ((saleStatus != 1) && actiType == 2) {
				BigDecimal skuPrice = storeSku.getOnlinePrice(); // 商品价格
				BigDecimal bigSkuNum = new BigDecimal(skuNum);
				singlesPriceSum = bigSkuNum.multiply(skuPrice); // 单款商品总价格
				tradeOrderItem.setUnitPrice(skuPrice); // 订单项商品单价
				tradeOrderItem.setTotalAmount(singlesPriceSum);

				GoodsStoreSkuStock skuStock = goodsStoreSkuStockService.selectSingleSkuStock(skuId); // 查询便利店商品库存数量
				if (skuStock == null) {
					logger.error("查询便利店商品库存数量", "skuStock 为空------->" + CodeStatistical.getLineInfo());
					logger.info("查询便利店商品库存数量 skuStock 为空------->" + CodeStatistical.getLineInfo());
					throw new ServiceException("查询便利店商品库存数量异常：skuStock 为空-------->" + CodeStatistical.getLineInfo());
				}

			} else if (actiType == 2 && saleStatus == 1) {
				Map<String, Object> mapSale = new HashMap<String, Object>();
				mapSale.put("saleId", actiId);
				mapSale.put("storeSkuId", skuId);
				ActivitySaleGoods saleGoods = activitySaleGoodsService.selectActivitySaleByParams(mapSale); // 查询特惠活动商品信息
				if (saleGoods == null) {
					logger.error("查询特惠活动商品信息", "saleGoods 为空-------->" + CodeStatistical.getLineInfo());
					logger.info("查询特惠活动商品信息 saleGoods 为空-------->" + CodeStatistical.getLineInfo());
					throw new ServiceException("查询特惠活动商品信息异常：saleGoods 为空-------->" + CodeStatistical.getLineInfo());
				}

				BigDecimal skuPrice = saleGoods.getSalePrice(); // 商品价格
				BigDecimal bigSkuNum = new BigDecimal(skuNum);
				singlesPriceSum = bigSkuNum.multiply(skuPrice); // 单款商品总价格
				tradeOrderItem.setUnitPrice(skuPrice); // 订单项商品单价
				tradeOrderItem.setTotalAmount(singlesPriceSum);

				if (actiType == 2 && saleStatus == 1) {
					ActivitySaleRecord record = new ActivitySaleRecord(); // 特惠活动记录
					record.setId(UuidUtils.getUuid());
					record.setOrderDisabled(Disabled.valid);
					record.setOrderId(orderId);
					record.setSaleGoodsId(skuId);
					record.setSaleGoodsNum(Integer.valueOf(skuNum));
					record.setStroeId(storeId);
					record.setSaleId(actiId);
					record.setUserId(userId);
					calculateItem.setRecord(record);

				}
			}

			Map<String, Object> countMap = new HashMap<String, Object>();
			countMap.put("storeSkuId", skuId);

			String skuName = storeSkuPr.getName(); // 商品名称
			String mainPicPrl = storeSkuPr.getGoodsStoreSkuPicture().getUrl();// 主图
			List<String> imageList = new ArrayList<String>();
			imageList.add(mainPicPrl);
			Auth auth = Auth.create(accessKey, secretKey);
			try {
				FileUtil.copyListFileName(auth, storeToken, imageList, orderToken);
			} catch (QiniuException e) {
				Response r = e.response;
				// statusCode:614返回状态码，图片已存在
				if (614 == r.statusCode) {
					logger.info("商品图片在订单中已存在");
					// statusCode:612返回状态码,图片不存在状态
				} else if (612 == r.statusCode) {
					logger.info("商品图片在订单中不存在");
				} else {
					logger.error("将图片从商品库目录copy到订单图片目录异常", e.getMessage());
					throw new ServiceException("将图片从标准商品库目录copy到店铺图片目录异常!");
				}
			}

			String barCode = storeSkuPr.getBarCode(); // 条形码
			String styleCode = storeSkuPr.getStyleCode(); // 款码

			tradeOrderItem.setId(UuidUtils.getUuid());
			tradeOrderItem.setStoreSkuId(skuId);
			tradeOrderItem.setSkuName(skuName);
			tradeOrderItem.setStatus(OrderItemStatusEnum.NO_REFUND);

			String propertiesIndb = "";
			String properties = storeSku.getPropertiesIndb();
			if (properties != null && !properties.equals("")) {
				JSONObject jb = JSONObject.fromObject(storeSku.getPropertiesIndb());
				String skuPrperties = jb.get("skuName").toString();
				propertiesIndb = skuPrperties; // SKU属性在数据库中的字符串表示
			}

			tradeOrderItem.setPropertiesIndb(propertiesIndb);
			tradeOrderItem.setOrderId(orderId);
			tradeOrderItem.setQuantity(new Integer(skuNum));
			tradeOrderItem.setStoreSpuId(storeSku.getStoreSpuId());

			tradeOrderItem.setCreateTime(new Date());
			if (storeSku.getGuaranteed() == null || storeSku.getGuaranteed().equals("")) {
				tradeOrderItem.setServiceAssurance(0); // 服务保障
			} else {
				tradeOrderItem.setServiceAssurance(Integer.valueOf(storeSku.getGuaranteed())); // 服务保障
			}

			tradeOrderItem.setMainPicPrl(mainPicPrl);
			tradeOrderItem.setSpuType(GoodsTypeEnum.SINGLE_GOODS);
			tradeOrderItem.setAppraise(AppraiseEnum.NOT_APPROPRIATE);
			// 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
			int actType = amountInParam.getActType();
			List<OrderItem> afterItemList = amountInParam.getAfterItemList();
			if (actType == 1) { // 代金券活动
				if (afterItemList.size() != 0) {
					for (int i = 0; i < afterItemList.size(); i++) {
						OrderItem orderItem = afterItemList.get(i);
						if (orderItem.getPrice().compareTo(tradeOrderItem.getTotalAmount()) == 0) {
							tradeOrderItem.setPreferentialPrice(orderItem.getPrivilege());
							tradeOrderItem.setActualAmount(singlesPriceSum.subtract(orderItem.getPrivilege()));
							tradeOrderItem.setIncome(singlesPriceSum);
						}
					}
				} else {
					tradeOrderItem.setPreferentialPrice(new BigDecimal("0.00"));
					tradeOrderItem.setActualAmount(singlesPriceSum.subtract(new BigDecimal("0.00")));
					tradeOrderItem.setIncome(new BigDecimal("0.00"));
				}
			} else if (actType == 2) { // 满减活动
				// 查询满减活动对应的具体满减优惠
				ActivityDiscount discount = activityDiscountService.selectByPrimaryKey(activityId);
				String sId = discount.getStoreId();
				if (sId.equals("0")) {
					if (afterItemList.size() != 0) {
						for (int i = 0; i < afterItemList.size(); i++) {
							OrderItem orderItem = afterItemList.get(i);
							if (orderItem.getPrice().compareTo(tradeOrderItem.getTotalAmount()) == 0) {
								tradeOrderItem.setPreferentialPrice(orderItem.getPrivilege());
								tradeOrderItem.setActualAmount(singlesPriceSum.subtract(orderItem.getPrivilege()));
								tradeOrderItem.setIncome(singlesPriceSum);
							}
						}
					} else {
						tradeOrderItem.setPreferentialPrice(new BigDecimal("0.00"));
						tradeOrderItem.setActualAmount(singlesPriceSum.subtract(new BigDecimal("0.00")));
						tradeOrderItem.setIncome(new BigDecimal("0.00"));
					}
				} else {
					if (afterItemList.size() != 0) {
						for (int i = 0; i < afterItemList.size(); i++) {
							OrderItem orderItem = afterItemList.get(i);
							if (orderItem.getPrice().compareTo(tradeOrderItem.getTotalAmount()) == 0) {
								tradeOrderItem.setPreferentialPrice(orderItem.getPrivilege());
								tradeOrderItem.setActualAmount(singlesPriceSum.subtract(orderItem.getPrivilege()));
								tradeOrderItem.setIncome(singlesPriceSum.subtract(orderItem.getPrivilege()));
							}
						}
					} else {
						tradeOrderItem.setPreferentialPrice(new BigDecimal("0.00"));
						tradeOrderItem.setActualAmount(singlesPriceSum.subtract(new BigDecimal("0.00")));
						tradeOrderItem.setIncome(new BigDecimal("0.00"));
					}
				}
			} else if (actType == 3) {
				if (afterItemList.size() != 0) {
					for (int i = 0; i < afterItemList.size(); i++) {
						OrderItem orderItem = afterItemList.get(i);
						if (orderItem.getPrice().compareTo(tradeOrderItem.getTotalAmount()) == 0) {
							tradeOrderItem.setPreferentialPrice(orderItem.getPrivilege());
							tradeOrderItem.setActualAmount(singlesPriceSum.subtract(orderItem.getPrivilege()));
							tradeOrderItem.setIncome(singlesPriceSum.subtract(orderItem.getPrivilege()));
						}
					}
				} else {
					tradeOrderItem.setPreferentialPrice(new BigDecimal("0.00"));
					tradeOrderItem.setActualAmount(singlesPriceSum.subtract(new BigDecimal("0.00")));
					tradeOrderItem.setIncome(new BigDecimal("0.00"));
				}
			} else {
				tradeOrderItem.setPreferentialPrice(new BigDecimal("0.00"));
				tradeOrderItem.setActualAmount(singlesPriceSum);
				tradeOrderItem.setIncome(singlesPriceSum);
				tradeOrderItem.setActivityType("0");
				tradeOrderItem.setActivityId("0");
			}
			tradeOrderItem.setActivityType(amountInParam.getActivityType());
			tradeOrderItem.setActivityId(amountInParam.getActivityId());
			tradeOrderItem.setBarCode(barCode);
			tradeOrderItem.setStyleCode(styleCode);
			tradeOrderItem.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);

			orderItemList.add(tradeOrderItem);
			calculateItem.setOrderItemList(orderItemList);
		}
		return calculateItem;
	}

	private CalculatePrivilege getLastPrivilege(CalculatePrivilegeInParam priInParam) throws Exception {
		CalculatePrivilege privilege = new CalculatePrivilege();
		List<OrderItem> afterItemList = new ArrayList<OrderItem>();
		BigDecimal orderSum = null; // 订单总金额

		int actType = priInParam.getActType();
		String couponsType = priInParam.getCouponsType();
		String activityId = priInParam.getActivityId();
		String activityItemId = priInParam.getActivityItemId();
		List<OrderItem> itemList = priInParam.getItemList();
		BigDecimal sum = priInParam.getSum();

		if (actType != 0) {
			int couType = 0;
			if (!couponsType.equals("")) {
				couType = Integer.valueOf(couponsType); // 代金券活动类型
			}
			ActivityCoupons activityCoupons = new ActivityCoupons();

			int faceValue = 0; // 代金券面额
			// 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
			if (actType == 1) {
				if (!activityId.equals("") && !activityItemId.equals("")) {
					CouponsFindVo couponsVo = new CouponsFindVo();
					if (couType == 1) {
						couponsVo.setActivityId(activityId);
						couponsVo.setActivityItemId(activityItemId);
						couponsVo.setConponsType(couType);
						activityCoupons = activityCouponsRecordService.selectCouponsItem(couponsVo); // 查询代金券具体金额
					} else if (couType == 0) {
						couponsVo.setActivityId(activityId);
						couponsVo.setActivityItemId(activityItemId);
						couponsVo.setConponsType(couType);
						activityCoupons = activityCouponsRecordService.selectCouponsItem(couponsVo); // 查询代金券具体金额
					}
				}
				privilege.setActivityType(ActivityTypeEnum.VONCHER);
				if (couType == 1) { // 注册代金券活动类型
					faceValue = activityCoupons.getFaceValue(); // 面额
					BigDecimal BigfaceValue = new BigDecimal(faceValue);
					afterItemList = DiscountCalculate.couponCalculate(itemList, BigfaceValue); // 获取订单项优惠金额
					privilege.setAfterItemList(afterItemList);
					if (sum.compareTo(BigfaceValue) == 0) { // 商品购买总金额==注册送代金券总金额时,即为优惠金额减去订单总金额
						orderSum = BigfaceValue.subtract(sum);
						privilege.setOrderSum(orderSum);
					} else if (sum.compareTo(BigfaceValue) == -1) { // 商品购买总金额大于注册送代金券总金额时,结果为订单总金额减去优惠金额
						orderSum = sum.subtract(BigfaceValue);
						privilege.setOrderSum(orderSum);
					} else if (sum.compareTo(BigfaceValue) == 1) { // 优惠金额大于商品金额时,结果为优惠金额减去商品总金额,即为0
						orderSum = sum.subtract(BigfaceValue);
						privilege.setOrderSum(orderSum);
					}
				} else if (couType == 0) { // 活动代金券
					faceValue = activityCoupons.getFaceValue();
					BigDecimal BigfaceValue = new BigDecimal(faceValue);
					afterItemList = DiscountCalculate.couponCalculate(itemList, BigfaceValue);
					privilege.setAfterItemList(afterItemList);
					orderSum = sum.subtract(BigfaceValue);
					privilege.setOrderSum(orderSum);
				}
			} else if (actType == 2) { // 满减活动
				Map<String, String> discountStr = new HashMap<String, String>();
				discountStr.put("discountId", activityId);
				discountStr.put("id", activityItemId);
				ActivityDiscountCondition conditions = activityDiscountService.getDiscountConditions(discountStr); // 查询满减活动对应的具体满减优惠
				BigDecimal disc = conditions.getDiscount(); // 取具体满减值
				afterItemList = DiscountCalculate.couponCalculate(itemList, disc);
				privilege.setAfterItemList(afterItemList);
				orderSum = sum.subtract(disc);
				privilege.setOrderSum(orderSum);
				privilege.setActivityType(ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES);
			} else if (actType == 3) { // 满折活动
				Map<String, String> reduceStr = new HashMap<String, String>();
				reduceStr.put("discountId", activityId);
				reduceStr.put("id", activityItemId);
				ActivityDiscountCondition conditions = activityDiscountService.getReduceConditions(reduceStr);
				BigDecimal disc = conditions.getDiscount(); // 取具体满折值
				afterItemList = DiscountCalculate.discountCalculateOrder(itemList, disc);
				privilege.setAfterItemList(afterItemList);
				BigDecimal discc = disc.divide(new BigDecimal(10));
				orderSum = sum.multiply(discc);
				privilege.setOrderSum(orderSum);
				privilege.setActivityType(ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES);
			}

			ActivityDiscountRecord discountRecord = new ActivityDiscountRecord();
			discountRecord.setId(UuidUtils.getUuid());
			discountRecord.setDiscountId(activityId);
			discountRecord.setDiscountConditionsId(activityItemId);
			discountRecord.setOrderId(priInParam.getOrderId());
			discountRecord.setOrderTime(new Date());
			discountRecord.setStoreId(priInParam.getStoreId());
			discountRecord.setUserId(priInParam.getUserId());
			discountRecord.setDiscountType(ActivityDiscountType.discount);

			if (actType == 2) { // 满减活动
				discountRecord.setDiscountType(ActivityDiscountType.discount);
			} else if (actType == 3) { // 满折活动
				discountRecord.setDiscountType(ActivityDiscountType.mlj);
			}
			// 保存折扣、满减活动信息
			privilege.setDiscountRecord(discountRecord);

		} else if (actType == 0) {// 没有参数活动
			privilege.setActivityType(ActivityTypeEnum.NO_ACTIVITY);
			orderSum = sum;
			privilege.setOrderSum(orderSum);
			privilege.setActivityId("0");
		}

		return privilege;
	}

	/**
	 * 第一次计算订单商品总金额
	 * </p>
	 * 
	 * @param calInParam
	 * @return
	 * @throws Exception
	 */
	private CalculateOrderVo calculateOrder(CalculateOrderVoInParam calInParam) throws Exception {
		CalculateOrderVo orderVo = new CalculateOrderVo();
		List<OrderItem> itemList = new ArrayList<OrderItem>();
		BigDecimal sum = new BigDecimal("0.00");
		JSONArray array = calInParam.getArray();
		List<ActivitySaleRecord> recordList = new ArrayList<ActivitySaleRecord>();
		// 单品款数*乘以价格的集合
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);

			String skuId = obj.getString("skuId"); // 商品ID
			String skuNum = obj.getString("skuNum"); // 商品数量

			GoodsStoreSku storeSku = goodsStoreSkuService.selectGoodsStoreSkuDetail(skuId);
			if (storeSku == null) {
				logger.error("查询店铺商品信息", "storeSku 为空-------->" + CodeStatistical.getLineInfo());
				logger.info("查询店铺商品信息 storeSku 为空-------->" + CodeStatistical.getLineInfo());
				throw new Exception("查询店铺商品信息信息异常：storeSku 为空-------->" + CodeStatistical.getLineInfo());
			}
			int actiType = storeSku.getActivityType().ordinal(); // 活动类型(0无,1:团购,2:特惠)
			String actiId = storeSku.getActivityId(); // 活动ID
			int saleStatus = 0;
			if (actiType == 2) {
				ActivitySale acSale = activitySaleService.getAcSaleStatus(actiId);
				saleStatus = acSale.getStatus(); // 特惠活动状态(0:未开始,1:进行中,2:已结束,3:已关闭)
			}
			if ((saleStatus != 1) && actiType == 0) {
				BigDecimal skuPrice = storeSku.getOnlinePrice(); // 商品价格
				BigDecimal bigSkuNum = new BigDecimal(skuNum);
				BigDecimal skuPriceSum = bigSkuNum.multiply(skuPrice); // 单款商品总价格
				sum = sum.add(skuPriceSum); // 所有订单商品总价格
				orderVo.setSum(sum);
				OrderItem orderItem = new OrderItem(skuId, skuPriceSum);
				itemList.add(orderItem);
				orderVo.setItemList(itemList);
			}
			if ((saleStatus != 1) && actiType == 2) {
				BigDecimal skuPrice = storeSku.getOnlinePrice(); // 商品价格
				BigDecimal bigSkuNum = new BigDecimal(skuNum);
				BigDecimal skuPriceSum = bigSkuNum.multiply(skuPrice); // 单款商品总价格
				sum = sum.add(skuPriceSum); // 所有订单商品总价格
				orderVo.setSum(sum);
				OrderItem orderItem = new OrderItem(skuId, skuPriceSum);
				itemList.add(orderItem);
				orderVo.setItemList(itemList);
			}
			if (actiType == 2 && saleStatus == 1) {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("saleId", actiId);
				map.put("storeSkuId", skuId);
				ActivitySaleGoods saleGoods = activitySaleGoodsService.selectActivitySaleByParams(map);
				if (saleGoods == null) {
					logger.error("查询特惠活动商品信息", "saleGooods 为空-------->" + CodeStatistical.getLineInfo());
					logger.info("查询特惠活动商品信息 saleGooods 为空-------->" + CodeStatistical.getLineInfo());
					throw new Exception("查询特惠活动商品信息异常：saleGoods 为空-------->" + CodeStatistical.getLineInfo());
				}
				BigDecimal skuPrice = saleGoods.getSalePrice(); // 商品价格
				BigDecimal bigSkuNum = new BigDecimal(skuNum);
				BigDecimal skuPriceSum = bigSkuNum.multiply(skuPrice); // 单款商品总价格
				sum = sum.add(skuPriceSum); // 所有订单商品总价格
				orderVo.setSum(sum);
				OrderItem orderItem = new OrderItem(skuId, skuPriceSum);
				itemList.add(orderItem);
				orderVo.setItemList(itemList);

				ActivitySaleRecord record = new ActivitySaleRecord(); // 特惠活动记录
				record.setId(UuidUtils.getUuid());
				record.setOrderDisabled(Disabled.valid);
				record.setOrderId(calInParam.getOrderId());
				record.setSaleGoodsId(skuId);
				record.setSaleGoodsNum(Integer.valueOf(skuNum));
				record.setStroeId(calInParam.getStoreId());
				record.setSaleId(actiId);
				record.setUserId(calInParam.getUserId());
				recordList.add(record);
				orderVo.setRecordList(recordList);
			}

		}
		return orderVo;
	}

	@Override
	public JSONObject selectValidateGroupTradeOrder(String requestStr) throws Exception {

		JSONObject reqJson = JSONObject.fromObject(requestStr);
		JSONObject jsonData = reqJson.getJSONObject("data");

		String activityId = jsonData.getString("activityId"); // 团购活动ID
		String userId = jsonData.getString("userId"); // 用户ID
		String storeId = jsonData.getString("storeId"); // 店铺ID
		String storeSkuId = jsonData.getString("skuId"); // 活动商品ID
		String storeSkuNum = jsonData.getString("skuNum"); // 数量

		int skuNum = Integer.valueOf(storeSkuNum);

		Map<String, Object> map = new HashMap<String, Object>(); // 查询团购活动商品方法入参
		map.put("groupId", activityId);
		map.put("storeSkuId", storeSkuId);

		Map<String, Object> isMap = new HashMap<String, Object>(); // 查询ID是否已购买团购商品入参
		isMap.put("userId", userId);
		isMap.put("storeId", storeId);
		isMap.put("storeSkuId", storeSkuId);

		Map<String, Object> hasBuy = new HashMap<String, Object>(); // 查询团购活动商品购买数量
		hasBuy.put("storeId", storeId); // 店铺ID
		hasBuy.put("groupGoodsId", storeSkuId); // 团购商品ID
		hasBuy.put("userId", userId); // 用户ID
		hasBuy.put("saleId", activityId); // 团购活动ID

		int isBuyCount = activityGroupRecordService.selectActivityGroupRecord(hasBuy); // 查询用户已购买团购活动商品数量记录

		ActivityGroup activityGroup = activityGroupService.selectGroupStatus(activityId); // 查询团购活动时间
		if (activityGroup == null) {
			logger.error("查询团购活动时间", "activityGroup 为空-------->" + CodeStatistical.getLineInfo());
			logger.info("查询团购活动时间 activityGroup 为空-------->" + CodeStatistical.getLineInfo());
			throw new Exception("查询团购活动时间异常：activityGroup 为空-------->" + CodeStatistical.getLineInfo());
		}
		int status = Integer.valueOf(activityGroup.getStatus()); // 团购活动状态(0:未开始,1:已开始,2:已结束,3:已失效)
		int groupStatus = Integer.valueOf(activityGroup.getStatus()); // 团购活动状态

		ActivityGroupGoods activityGroupGoods = activityGroupGoodsService.selectActivityGroupLimitNum(map); // 查询团购商品的限购数
		if (activityGroupGoods == null) {
			logger.error("查询团购商品的限购数", "activityGroupGoods 为空-------->" + CodeStatistical.getLineInfo());
			logger.info("查询团购商品的限购数 activityGroupGoods 为空-------->" + CodeStatistical.getLineInfo());
			throw new Exception("查询团购商品的限购数异常：activityGroupGoods 为空-------->" + CodeStatistical.getLineInfo());
		}
		int limitNum = activityGroupGoods.getLimitNum(); // 团购活动商品限购数量
		double groupPrice = activityGroupGoods.getGourpPrice(); // 商品团购价格

		GoodsStoreSku goodsStoreSku = goodsStoreSkuService.selectGoodsStoreSkuDetail(storeSkuId); // 查询商品信息
		if (goodsStoreSku == null) {
			logger.error("查询商品信息", "goodsStoreSku 为空-------->" + CodeStatistical.getLineInfo());
			logger.info("查询商品信息 goodsStoreSku 为空-------->" + CodeStatistical.getLineInfo());
			throw new Exception("查询商品信息异常：goodsStoreSku 为空-------->" + CodeStatistical.getLineInfo());
		}
		int violation = goodsStoreSku.getOnline().ordinal(); // 上下架标识 0:下架、1:上架
		String skuName = goodsStoreSku.getName();
		String propertiesIndb = "";
		String properties = goodsStoreSku.getPropertiesIndb();
		if (properties != null && !properties.equals("")) {
			JSONObject jb = JSONObject.fromObject(goodsStoreSku.getPropertiesIndb());
			String skuPrperties = jb.get("skuName").toString();
			propertiesIndb = skuPrperties; // SKU属性在数据库中的字符串表示
		}
		String url = goodsStoreSku.getGoodsStoreSkuPicture().getUrl();

		StoreInfo sInfo = storeInfoService.selectStoreBaseInfoById(storeId); // 查询店铺信息
		if (sInfo == null) {
			logger.error("查询店铺信息", "sInfo 为空-------->" + CodeStatistical.getLineInfo());
			logger.info("查询店铺信息 sInfo 为空-------->" + CodeStatistical.getLineInfo());
			throw new Exception("查询店铺信息异常：sInfo 为空-------->" + CodeStatistical.getLineInfo());
		}
		String sId = sInfo.getId(); // 店铺ID
		String storeName = sInfo.getStoreName(); // 店铺名称
		int isClosed = sInfo.getStoreInfoExt().getIsClosed().ordinal(); // 关闭/开启店铺(0:关闭,1:开启)

		GoodsStoreSkuStock stuStock = goodsStoreSkuStockService.selectBySkuId(storeSkuId); // 活动商品库存查询
		int groupInvent = stuStock.getLocked(); // 锁定库存

		int isContent = 1; // 限ID标识(1：未超过,0:已超过)
		int isOrder = 0; // 是否可以下单标识(0:否,1:是)
		int isStock = 1; // 是否有库存标识(0:不足,1:足)
		int result = 0; // 可购买数量标识(0不限购,大于0表示用户可购买数量)

		if (isClosed == 0) { // 团购活动店铺关闭
			isClosed = 0;
		} else {
			if (groupStatus == 4) { // 活动关闭
				status = 2;
			} else {
				if (violation == 0) { // 商品下架
					violation = 0;
				} else { // 限ID
					if (limitNum > 0) {
						if (isBuyCount >= limitNum) {
							result = limitNum - isBuyCount;
							isContent = 0;
						} else {
							result = limitNum - isBuyCount;
							if (skuNum > groupInvent) {
								isStock = 0;
							}
						}
					} else {
						if (skuNum > groupInvent) {
							isStock = 0;
						} else {
							result = groupInvent - isBuyCount;
						}
					}
				}
			}
		}

		if (isContent == 1 && isClosed == 1 && status == 1 && groupStatus == 1 && isStock == 1 && violation == 1) { // 判断是否可以下单标识
			isOrder = 1;
		}

		JSONObject json = new JSONObject(); // 返回结果出参

		json.put("storeId", sId); // 店铺ID
		json.put("storeName", storeName); // 店铺名称
		json.put("skuName", skuName); // 商品名称
		json.put("groupPrice", groupPrice); // 商品团购价格
		json.put("skuSpec", propertiesIndb); // 商品规格

		json.put("limitNum", limitNum); // 限购数
		json.put("groupInvent", groupInvent); // 库存
		json.put("url", url); // 图片地址
		json.put("result", result); // 购买数量(0:不限购买数量,大于0表示当前商品可购买数量)

		json.put("isOrder", isOrder); // 是否可以提交订单(1:是,0:否)
		json.put("isClosed", isClosed); // 是否可以提交订单(1:是,0:否)
		json.put("status", status); // 团购状态(0:未开始,1:已开始,2:已结束,3:已失效)
		json.put("violation", violation); // 商品上下架标识(0:未违规、1:违规下架)
		json.put("isContent", isContent); // 限ID的标识1：未超过,0:已超过
		json.put("isStock", isStock); // 是否有库存(0:不足,1:足)

		json.put("goodsImagePrefix", orderImagePrefix); // 定单图片地址前缀
		json.put("goodsHeightOrWidth", "120"); // 商品图片的尺寸
		json.put("storeHeightOrWidth", "120"); // 店铺图片的尺寸

		return json;
	}

	@SuppressWarnings("unused")
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addGroupTradeOrder(String requestStr) throws Exception {

		JSONObject reqJson = JSONObject.fromObject(requestStr);
		JSONObject jsonData = reqJson.getJSONObject("data");

		String userId = jsonData.getString("userId"); // 用户名
		String userPhone = jsonData.getString("userPhone"); // 手机号码
		String pickType = jsonData.getString("pickType"); // 提货类型(0:送货上门,1:到店自提,2:团购服务)
		String addressId = jsonData.getString("addressId"); // 地址ID
		String receiveTime = jsonData.getString("receiveTime"); // 自提时间

		String pickTime = jsonData.getString("pickTime"); // 送货时间
		String isInvoice = jsonData.getString("isInvoice"); // 是否有发票标识(0:无,1:有)
		String invoiceHead = jsonData.getString("invoiceHead"); // 发票抬头
		String invoiceContent = jsonData.getString("invoiceContent"); // 发票内容

		String storeId = jsonData.getString("storeId"); // 店铺ID
		// String storeName = jsonData.getString("storeName"); // 店铺名称(预留)
		String orderResource = jsonData.getString("orderResource"); // 订单来源
		String type = jsonData.getString("type"); // 订单类型
		String remark = jsonData.getString("remark"); // 备注

		String payType = jsonData.getString("payType"); // 支付方式：0:货到付款、1：在线支付
		String activityId = jsonData.getString("activityId"); // 活动ID
		String activityType = jsonData.getString("activityType"); // 活动类型
		String skuId = jsonData.getString("skuId"); // 商品ID
		String storeSkuNum = jsonData.getString("skuNum"); // 商品数量

		logger.info("pickType---------提货类型--------->" + pickType, CodeStatistical.getLineInfo());
		logger.info("payType---------支付方式--------->" + payType, CodeStatistical.getLineInfo());

		int pick = Integer.valueOf(pickType); // 提货类型(0:送货上门,1:到店自提,2:团购服务)

		int skuNum = Integer.valueOf(storeSkuNum);

		Map<String, Object> map = new HashMap<String, Object>(); // 查询团购活动商品方法入参
		map.put("groupId", activityId);
		map.put("storeSkuId", skuId);

		Map<String, Object> isMap = new HashMap<String, Object>(); // 查询ID是否已购买团购商品入参
		isMap.put("userId", userId);
		isMap.put("storeId", storeId);
		isMap.put("storeSkuId", skuId);

		Map<String, Object> hasBuy = new HashMap<String, Object>(); // 查询团购活动商品购买数量
		hasBuy.put("storeId", storeId); // 店铺ID
		hasBuy.put("groupGoodsId", skuId); // 团购商品ID
		hasBuy.put("userId", userId); // 用户ID
		hasBuy.put("saleId", activityId); // 团购活动ID

		ActivityGroupRecord groupRecord = new ActivityGroupRecord(); // 团购活动记录查询实体类
		TradeOrder order = new TradeOrder(); // 订单实体类
		StockAdjustVo stockVo = new StockAdjustVo(); // 库存异动实体类

		int isBuyCount = activityGroupRecordService.selectActivityGroupRecord(hasBuy); // 查询团购活动商品购买数量

		GoodsStoreSkuStock skuStock = goodsStoreSkuStockService.selectBySkuId(skuId); // 查询团购活动商品库存
		int locked = skuStock.getLocked();

		ActivityGroup activityGroup = activityGroupService.selectGroupStatus(activityId); // 查询团购活动时间
		if (activityGroup == null) {
			logger.error("查询团购活动时间", "activityGroup 为空-------->" + CodeStatistical.getLineInfo());
			logger.info("查询团购活动时间 activityGroup 为空-------->" + CodeStatistical.getLineInfo());
			throw new Exception("查询团购活动时间异常：activityGroup 为空-------->" + CodeStatistical.getLineInfo());
		}
		int status = Integer.valueOf(activityGroup.getStatus()); // 团购活动状态(0:未开始,1:已开始,2:已结束,3:已失效)

		ActivityGroupGoods activityGroupGoods = activityGroupGoodsService.selectActivityGroupLimitNum(map); // 查询团购商品的限购数
		if (activityGroupGoods == null) {
			logger.error("查询团购商品的限购数", "activityGroupGoods 为空-------->" + CodeStatistical.getLineInfo());
			logger.info("查询团购商品的限购数 activityGroupGoods 为空-------->" + CodeStatistical.getLineInfo());
			throw new Exception("查询团购商品的限购数异常：activityGroupGoods 为空-------->" + CodeStatistical.getLineInfo());
		}

		GoodsStoreSku goodsStoreSku = goodsStoreSkuService.selectGoodsStoreSkuDetail(skuId); // 查询商品信息
		if (goodsStoreSku == null) {
			logger.error("查询商品信息", "goodsStoreSku 为空-------->" + CodeStatistical.getLineInfo());
			logger.info("查询商品信息 goodsStoreSku 为空-------->" + CodeStatistical.getLineInfo());
			throw new Exception("查询商品信息异常：goodsStoreSku 为空-------->" + CodeStatistical.getLineInfo());
		}

		StoreInfo sInfo = storeInfoService.selectStoreBaseInfoById(storeId); // 查询店铺信息
		if (sInfo == null) {
			logger.error("查询店铺信息", "sInfo 为空-------->" + CodeStatistical.getLineInfo());
			logger.info("查询店铺信息 sInfo 为空-------->" + CodeStatistical.getLineInfo());
			throw new Exception("查询店铺信息异常：sInfo 为空-------->" + CodeStatistical.getLineInfo());
		}
		String myStoreName = sInfo.getStoreName();
		int violation = goodsStoreSku.getOnline().ordinal(); // 上下架标识 0:下架、1:上架
		int groupStatus = Integer.valueOf(activityGroup.getStatus()); // 团购活动状态
		BigDecimal skuPriceSum = null; // 订单商品总金额
		// String sId = sInfo.getId(); // 店铺ID
		// String storeName = sInfo.getStoreName(); // 店铺名称
		int limitNum = activityGroupGoods.getLimitNum(); // 单品限制数量

		int isClosed = sInfo.getStoreInfoExt().getIsClosed().ordinal(); // 关闭/开启店铺(0:关闭,1:开启)
		// int isActivityStatus = 1; // 活动状态 1:已开始,2:已结束
		int isContent = 1; // 限ID的标识1：未超过,0:已超过
		// int isLimit = 1; // 1:满足,0:不满足
		int isOrder = 0; // 是否可以下单0:否,1:是
		int isStock = 1; // 是否有库存0:不足,1:足
		String tradeNum = TradeNumUtil.getTradeNum(); // 订单交易号
		order.setTradeNum(tradeNum);
		if (isClosed == 0) { // 活动店铺关闭状态
			isClosed = 0;
		} else {
			if (groupStatus == 4) { // 活动关闭
				status = 2;
			} else {
				if (violation == 0) { // 上下架标识 0:下架、1:上架
					violation = 0;
				} else { // 限购ID
					if (limitNum > 0) {
						if (isBuyCount >= limitNum) {
							isContent = 0;
						} else {
							if (skuNum > locked) {
								isStock = 0;
							} else {
								order.setId(UuidUtils.getUuid());
								int actType = Integer.valueOf(activityType);
								String orderNo = generateNumericalService.generateNumber("XS"); // 订单编号生成

								TradeOrderItem orderItem = new TradeOrderItem();
								orderItem.setId(UuidUtils.getUuid());
								orderItem.setActivityId(activityId);
								orderItem.setActivityType(activityType);
								BigDecimal groupPrice = new BigDecimal(activityGroupGoods.getGourpPrice());
								BigDecimal bigSkuNum = new BigDecimal(skuNum);

								orderItem.setActualAmount(groupPrice.multiply(bigSkuNum));
								orderItem.setIncome(groupPrice.multiply(bigSkuNum));
								orderItem.setAppraise(AppraiseEnum.NOT_APPROPRIATE);

								TradeOrderLogistics orderLogistics = new TradeOrderLogistics();

								StoreInfo storeInfo = storeInfoService.selectDefaultAddressById(storeId); // 获取默认地址
								String defaultAddressId = storeInfo.getMemberConsignee().getId();

								if (pick == 0) { // 送货上门

									MemberConsigneeAddress address = memberConsigneeAddressService
											.selectAddressById(addressId); // 获取买家收货地址

									orderLogistics.setId(UuidUtils.getUuid());
									orderLogistics.setConsigneeName(address.getConsigneeName());
									orderLogistics.setMobile(address.getMobile());
									orderLogistics.setAddress(address.getAddress());
									orderLogistics.setArea(address.getArea());

									orderLogistics.setOrderId(order.getId());
									orderLogistics.setAreaId(address.getAreaId());
									orderLogistics.setProvinceId(address.getProvinceId());
									orderLogistics.setCityId(address.getCityId());
									orderLogistics.setZipCode(address.getZipCode());
									if (!receiveTime.equals("")) {
										order.setPickUpId(defaultAddressId);
										order.setPickUpTime(receiveTime);
										order.setFare(new BigDecimal("0.00"));
									}
									if (!pickTime.equals("")) {
										order.setPickUpTime(pickTime);
									} else {
										ActivityGroup activityGroups = activityGroupService
												.selectServiceTime(activityId); // 查询团购活动时间
										if (activityGroup == null) {
											logger.error("查询团购活动时间",
													"activityGroup 为空-------->" + CodeStatistical.getLineInfo());
											logger.info("查询团购活动时间 activityGroup 为空-------->"
													+ CodeStatistical.getLineInfo());
											throw new Exception("查询团购活动时间异常：activityGroup 为空-------->"
													+ CodeStatistical.getLineInfo());
										}
										SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
										String startTime = format.format(activityGroups.getStartTime());
										String endTime = format.format(activityGroups.getEndTime());
										order.setPickUpTime(startTime + "-" + endTime);
									}
									order.setTradeOrderLogistics(orderLogistics);

								} else if (pick == 1) { // 到店自提
									if (!receiveTime.equals("")) {
										order.setPickUpId(defaultAddressId);
										order.setPickUpTime(receiveTime);
									} else {
										StoreInfo st = storeInfoService.selectDefaultAddressById(storeId); // 店铺默认地址查询
										if (st == null) {
											logger.error("店铺默认地址查询", "st 为空-------->" + CodeStatistical.getLineInfo());
											logger.info("店铺默认地址查询 st 为空-------->" + CodeStatistical.getLineInfo());
											throw new Exception(
													"查询团购活动时间异常：st 为空-------->" + CodeStatistical.getLineInfo());
										}
										String startTime = st.getStoreInfoExt().getServiceStartTime();
										String endTime = st.getStoreInfoExt().getServiceEndTime();
										order.setPickUpTime(startTime + "-" + endTime);
									}
									order.setFare(new BigDecimal("0.00"));
								} else if (pick == 2) { // 送货上门
									ActivityGroup activityGroups = activityGroupService.selectServiceTime(activityId); // 查询团购活动时间
									if (activityGroup == null) {
										logger.error("查询团购活动时间",
												"activityGroup 为空-------->" + CodeStatistical.getLineInfo());
										logger.info(
												"查询团购活动时间 activityGroup 为空-------->" + CodeStatistical.getLineInfo());
										throw new Exception(
												"查询团购活动时间异常：activityGroup 为空-------->" + CodeStatistical.getLineInfo());
									}
									order.setPickUpId(defaultAddressId);
									if (!receiveTime.equals("")) {
										order.setPickUpTime(receiveTime);
									} else {
										SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
										String startTime = format.format(activityGroup.getStartTime());
										String endTime = format.format(activityGroup.getEndTime());
										String pcTime = startTime + "-" + endTime;
										order.setPickUpTime(pcTime);
									}
									order.setFare(new BigDecimal("0.00"));
								}

								List<TradeOrderItem> orderItemList = new ArrayList<TradeOrderItem>();

								GoodsStoreSku storeSku = goodsStoreSkuService.selectGoodsStoreSkuDetail(skuId); // 查询商品详细信息
								if (storeSku == null) {
									logger.error("查询商品详细信息", "storeSku 为空-------->" + CodeStatistical.getLineInfo());
									logger.info("查询商品详细信息 storeSku 为空-------->" + CodeStatistical.getLineInfo());
									throw new Exception(
											"查询商品详细信息异常：storeSku 为空-------->" + CodeStatistical.getLineInfo());
								}
								String propertiesIndb = "";
								String skuName = storeSku.getName(); // 商品名称

								String properties = storeSku.getPropertiesIndb();
								if (properties != null && !properties.equals("")) {
									JSONObject jb = JSONObject.fromObject(storeSku.getPropertiesIndb());
									String skuPrperties = jb.get("skuName").toString();
									propertiesIndb = skuPrperties;
								}

								String mainPicPrl = storeSku.getGoodsStoreSkuPicture().getUrl();// 主图

								List<String> imageList = new ArrayList<String>();
								imageList.add(mainPicPrl);
								Auth auth = Auth.create(accessKey, secretKey);
								try {
									FileUtil.copyListFileName(auth, storeToken, imageList, orderToken);
								} catch (QiniuException e) {
									Response r = e.response;
									// statusCode:614返回状态码，图片已存在
									if (614 == r.statusCode) {
										logger.info("商品图片在订单中已存在");
										// statusCode:612返回状态码,图片不存在状态
									} else if (612 == r.statusCode) {
										logger.info("商品图片在订单中不存在");
									} else {
										logger.error("将图片从商品库目录copy到订单图片目录异常", e.getMessage());
										throw new ServiceException("将图片从标准商品库目录copy到店铺图片目录异常!");
									}
								}

								String barCode = storeSku.getBarCode(); // 条形码
								String styleCode = storeSku.getStyleCode(); // 款码
								String strPro = activityGroupGoods.getGourpPrice().toString(); // 团购价格
								BigDecimal BigGroupPrice = new BigDecimal(strPro);

								skuPriceSum = bigSkuNum.multiply(BigGroupPrice); // 单款商品总价格

								orderItem.setId(UuidUtils.getUuid());
								orderItem.setStoreSkuId(skuId);
								orderItem.setSkuName(skuName);
								orderItem.setTotalAmount(skuPriceSum);
								orderItem.setPropertiesIndb(propertiesIndb);

								orderItem.setOrderId(order.getId());
								orderItem.setQuantity(new Integer(skuNum));
								orderItem.setUnitPrice(new BigDecimal(activityGroupGoods.getGourpPrice()));
								orderItem.setStoreSpuId(storeSku.getStoreSpuId());
								orderItem.setCreateTime(new Date());

								if (storeSku.getGuaranteed() == null || storeSku.getGuaranteed().equals("")) {
									orderItem.setServiceAssurance(0); // 服务保障
								} else {
									orderItem.setServiceAssurance(Integer.valueOf(storeSku.getGuaranteed())); // 服务保障
								}

								orderItem.setMainPicPrl(mainPicPrl);
								orderItem.setSpuType(GoodsTypeEnum.SINGLE_GOODS);
								orderItem.setAppraise(AppraiseEnum.NOT_APPROPRIATE);

								orderItem.setPreferentialPrice(new BigDecimal("0.00"));
								orderItem.setActualAmount(skuPriceSum);
								orderItem.setActivityId(activityId);

								orderItem.setActivityType(activityType);
								orderItem.setBarCode(barCode);
								orderItem.setStyleCode(styleCode);
								orderItem.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);

								int orderType = Integer.valueOf(type);

								if (orderType == 0) {
									order.setType(OrderTypeEnum.PHYSICAL_ORDER);
								} else if (orderType == 1) {
									order.setType(OrderTypeEnum.SERVICE_ORDER);
								}

								orderItemList.add(orderItem);
								order.setTradeOrderItem(orderItemList);

								TradeOrderInvoice orderInvoice = new TradeOrderInvoice();

								int invoice = Integer.valueOf(isInvoice); // 是否有发票标识
								if (invoice == 1) { // 有发票
									orderInvoice.setId(UuidUtils.getUuid());
									orderInvoice.setId(order.getId());
									orderInvoice.setOrderId(order.getId());
									orderInvoice.setHead(invoiceHead);
									orderInvoice.setContext(invoiceContent);
									order.setTradeOrderInvoice(orderInvoice);
								}

								SysBuyerUser buyerUser = sysBuyerUserMapper.selectByPrimaryKey(userId); // 查询买家用户信息
								if (buyerUser == null) {
									logger.error("查询买家用户信息", "buyerUser 为空-------->" + CodeStatistical.getLineInfo());
									logger.info("查询买家用户信息 buyerUser 为空-------->" + CodeStatistical.getLineInfo());
									throw new Exception(
											"查询买家用户信息异常：buyerUser 为空-------->" + CodeStatistical.getLineInfo());
								}
								int orType = Integer.valueOf(type);
								if (orType == 1) {
									order.setUserPhone(userPhone);
								} else {
									order.setUserPhone(buyerUser.getPhone());
								}
								order.setUserId(userId);
								order.setStoreName(myStoreName);
								order.setStoreId(storeId);
								order.setUpdateTime(new Date());
								order.setPid("0");
								order.setOrderNo(orderNo);

								// 获得随机提货码
								String randomNo = RandomStringUtil.getRandomInt(6);
								order.setPickUpCode(randomNo);

								int intPayType = Integer.valueOf(payType);

								if (intPayType == 0) { // 支付方式：0:货到付款、1：在线支付
									order.setStatus(OrderStatusEnum.UNPAID);
									order.setPayWay(PayWayEnum.PAY_ONLINE);

								} else if (intPayType == 1) {
									order.setStatus(OrderStatusEnum.DROPSHIPPING);
									order.setPayWay(PayWayEnum.CASH_DELIERY);
								}

								BigDecimal resultSum = CalculateMoneyUtil.getOrderBigMoney(skuPriceSum);
								order.setTotalAmount(resultSum); // 订单总金额
								order.setIncome(resultSum); // 订单收入
								order.setActualAmount(resultSum); // 实付金额

								if (actType == 0) {
									order.setActivityType(ActivityTypeEnum.NO_ACTIVITY);
								} else if (actType == 1) {
									order.setActivityType(ActivityTypeEnum.VONCHER);
								} else if (actType == 2) {
									order.setActivityType(ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES);
								} else if (actType == 3) {
									order.setActivityType(ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES);
								} else if (actType == 4) {
									order.setActivityType(ActivityTypeEnum.GROUP_ACTIVITY);
								}

								order.setActivityId(activityId);
								order.setRemark(remark);
								order.setInvoice(WithInvoiceEnum.HAS);
								order.setDisabled(Disabled.valid);
								order.setCreateTime(new Date());

								order.setIsShow(OrderIsShowEnum.yes);
								order.setPaymentStatus(PaymentStatusEnum.STAY_BACK);
								order.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);

								int orderRes = Integer.valueOf(orderResource); // 订单来源
								if (orderRes == 0) {
									order.setOrderResource(OrderResourceEnum.YSCAPP);
								} else if (orderRes == 1) {
									order.setOrderResource(OrderResourceEnum.WECHAT);
								} else if (orderRes == 2) {
									order.setOrderResource(OrderResourceEnum.POS);
								}
								int picType = Integer.valueOf(pickType);
								// 提货类型
								if (picType == 0) {
									order.setPickUpType(PickUpTypeEnum.DELIVERY_DOOR);
								} else if (picType == 1) {
									order.setPickUpType(PickUpTypeEnum.TO_STORE_PICKUP);
								}

								List<AdjustDetailVo> detailList = new ArrayList<AdjustDetailVo>();
								AdjustDetailVo detail = new AdjustDetailVo();

								detail.setBarCode(storeSku.getBarCode());
								detail.setGoodsName(skuName);
								detail.setGoodsSkuId("");
								detail.setMultipleSkuId("");
								detail.setNum(Integer.valueOf(skuNum));

								detail.setPrice(BigGroupPrice);
								detail.setPropertiesIndb(propertiesIndb);
								detail.setStoreSkuId(skuId);
								detail.setGoodsSkuId(goodsStoreSku.getSkuId());
								detailList.add(detail);

								stockVo.setOrderId(order.getId());
								stockVo.setStoreId(storeId);
								stockVo.setUserId(userId);
								stockVo.setAdjustDetailList(detailList);
								stockVo.setStockOperateEnum(StockOperateEnum.ACTIVITY_PLACE_ORDER);

								groupRecord.setId(UuidUtils.getUuid()); // 主键
								groupRecord.setGroupGoodsId(skuId); // 团购商品ID
								groupRecord.setStroeId(storeId); // 店铺ID
								groupRecord.setGroupGoodsNum(new Integer(skuNum)); // 团购商品数量
								groupRecord.setUserId(userId); // 买家用户ID
								groupRecord.setOrderId(order.getId()); // 订单ID
								groupRecord.setSaleId(activityId); // 团购活动ID
								groupRecord.setOrderDisabled(Disabled.valid); // 订单状态：0正常，1已失效
							}
						}
					} else {

						if (skuNum > locked) {
							isStock = 0;
						} else {
							order.setId(UuidUtils.getUuid());
							int actType = Integer.valueOf(activityType);
							String orderNo = generateNumericalService.generateNumber("XS"); // 订单编号生成

							TradeOrderItem orderItem = new TradeOrderItem();
							orderItem.setId(UuidUtils.getUuid());
							orderItem.setActivityId(activityId);
							orderItem.setActivityType(activityType);
							BigDecimal groupPrice = new BigDecimal(activityGroupGoods.getGourpPrice());
							BigDecimal bigSkuNum = new BigDecimal(skuNum);
							orderItem.setActualAmount(groupPrice.multiply(bigSkuNum));
							orderItem.setIncome(groupPrice.multiply(bigSkuNum));
							orderItem.setAppraise(AppraiseEnum.NOT_APPROPRIATE);

							TradeOrderLogistics orderLogistics = new TradeOrderLogistics();

							StoreInfo storeInfo = storeInfoService.selectDefaultAddressById(storeId); // 获取默认地址
							String defaultAddressId = storeInfo.getMemberConsignee().getId();

							if (pick == 0) { // 送货上门

								MemberConsigneeAddress address = memberConsigneeAddressService
										.selectAddressById(addressId); // 获取买家收货地址

								orderLogistics.setId(UuidUtils.getUuid());
								orderLogistics.setConsigneeName(address.getConsigneeName());
								orderLogistics.setMobile(address.getMobile());
								orderLogistics.setAddress(address.getAddress());
								orderLogistics.setArea(address.getArea());

								orderLogistics.setOrderId(order.getId());
								orderLogistics.setAreaId(address.getAreaId());
								orderLogistics.setProvinceId(address.getProvinceId());
								orderLogistics.setCityId(address.getCityId());
								orderLogistics.setZipCode(address.getZipCode());
								if (!receiveTime.equals("")) {
									order.setPickUpId(defaultAddressId);
									order.setPickUpTime(receiveTime);
									order.setFare(new BigDecimal("0.00"));
								}
								if (!pickTime.equals("")) {
									order.setPickUpTime(pickTime);
								} else {
									ActivityGroup activityGroups = activityGroupService.selectServiceTime(activityId); // 查询团购活动时间
									if (activityGroup == null) {
										logger.error("查询团购活动时间",
												"activityGroup 为空-------->" + CodeStatistical.getLineInfo());
										logger.info(
												"查询团购活动时间 activityGroup 为空-------->" + CodeStatistical.getLineInfo());
										throw new Exception(
												"查询团购活动时间异常：activityGroup 为空-------->" + CodeStatistical.getLineInfo());
									}
									SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									String startTime = format.format(activityGroups.getStartTime());
									String endTime = format.format(activityGroups.getEndTime());
									order.setPickUpTime(startTime + "-" + endTime);
								}
								order.setTradeOrderLogistics(orderLogistics);

							} else if (pick == 1) { // 到店自提
								if (!receiveTime.equals("")) {
									order.setPickUpId(defaultAddressId);
									order.setPickUpTime(receiveTime);
								} else {
									StoreInfo st = storeInfoService.selectDefaultAddressById(storeId); // 店铺默认地址查询
									if (st == null) {
										logger.error("店铺默认地址查询", "st 为空-------->" + CodeStatistical.getLineInfo());
										logger.info("店铺默认地址查询 st 为空-------->" + CodeStatistical.getLineInfo());
										throw new Exception(
												"查询团购活动时间异常：st 为空-------->" + CodeStatistical.getLineInfo());
									}
									String startTime = st.getStoreInfoExt().getServiceStartTime();
									String endTime = st.getStoreInfoExt().getServiceEndTime();
									order.setPickUpTime(startTime + "-" + endTime);
								}
								order.setFare(new BigDecimal("0.00"));
							} else if (pick == 2) {
								ActivityGroup activityGroups = activityGroupService.selectServiceTime(activityId); // 查询团购活动时间
								if (activityGroup == null) {
									logger.error("查询团购活动时间",
											"activityGroup 为空-------->" + CodeStatistical.getLineInfo());
									logger.info("查询团购活动时间 activityGroup 为空-------->" + CodeStatistical.getLineInfo());
									throw new Exception(
											"查询团购活动时间异常：activityGroup 为空-------->" + CodeStatistical.getLineInfo());
								}
								order.setPickUpId(defaultAddressId);
								if (!receiveTime.equals("")) {
									order.setPickUpTime(receiveTime);
								} else {
									SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									String startTime = format.format(activityGroup.getStartTime());
									String endTime = format.format(activityGroup.getEndTime());
									String pcTime = startTime + "-" + endTime;
									order.setPickUpTime(pcTime);
								}
								order.setFare(new BigDecimal("0.00"));
							}

							List<TradeOrderItem> orderItemList = new ArrayList<TradeOrderItem>();

							GoodsStoreSku storeSku = goodsStoreSkuService.selectGoodsStoreSkuDetail(skuId); // 查询商品详细信息
							if (storeSku == null) {
								logger.error("查询商品详细信息", "storeSku 为空-------->" + CodeStatistical.getLineInfo());
								logger.info("查询商品详细信息 storeSku 为空-------->" + CodeStatistical.getLineInfo());
								throw new Exception("查询商品详细信息异常：storeSku 为空-------->" + CodeStatistical.getLineInfo());
							}
							String propertiesIndb = "";
							String skuName = storeSku.getName(); // 商品名称

							String properties = storeSku.getPropertiesIndb();
							if (properties != null && !properties.equals("")) {
								JSONObject jb = JSONObject.fromObject(storeSku.getPropertiesIndb());
								String skuPrperties = jb.get("skuName").toString();
								propertiesIndb = skuPrperties;
							}

							String mainPicPrl = storeSku.getGoodsStoreSkuPicture().getUrl();// 主图

							List<String> imageList = new ArrayList<String>();
							imageList.add(mainPicPrl);
							Auth auth = Auth.create(accessKey, secretKey);
							try {
								FileUtil.copyListFileName(auth, storeToken, imageList, orderToken);
							} catch (QiniuException e) {
								Response r = e.response;
								// statusCode:614返回状态码，图片已存在
								if (614 == r.statusCode) {
									logger.info("商品图片在订单中已存在");
									// statusCode:612返回状态码,图片不存在状态
								} else if (612 == r.statusCode) {
									logger.info("商品图片在订单中不存在");
								} else {
									logger.error("将图片从商品库目录copy到订单图片目录异常", e.getMessage());
									throw new ServiceException("将图片从标准商品库目录copy到店铺图片目录异常!");
								}
							}

							String barCode = storeSku.getBarCode(); // 条形码
							String styleCode = storeSku.getStyleCode(); // 款码
							String strPro = activityGroupGoods.getGourpPrice().toString(); // 团购价格
							BigDecimal BigGroupPrice = new BigDecimal(strPro);

							skuPriceSum = bigSkuNum.multiply(BigGroupPrice); // 单款商品总价格

							orderItem.setId(UuidUtils.getUuid());
							orderItem.setStoreSkuId(skuId);
							orderItem.setSkuName(skuName);
							orderItem.setTotalAmount(skuPriceSum);
							orderItem.setPropertiesIndb(propertiesIndb);

							orderItem.setOrderId(order.getId());
							orderItem.setQuantity(new Integer(skuNum));
							orderItem.setUnitPrice(new BigDecimal(activityGroupGoods.getGourpPrice()));
							orderItem.setStoreSpuId(storeSku.getStoreSpuId());
							orderItem.setCreateTime(new Date());

							if (storeSku.getGuaranteed() == null || storeSku.getGuaranteed().equals("")) {
								orderItem.setServiceAssurance(0); // 服务保障
							} else {
								orderItem.setServiceAssurance(Integer.valueOf(storeSku.getGuaranteed())); // 服务保障
							}

							orderItem.setMainPicPrl(mainPicPrl);
							orderItem.setSpuType(GoodsTypeEnum.SINGLE_GOODS);
							orderItem.setAppraise(AppraiseEnum.NOT_APPROPRIATE);

							orderItem.setPreferentialPrice(new BigDecimal("0.00"));
							orderItem.setActualAmount(skuPriceSum);
							orderItem.setActivityId(activityId);

							orderItem.setActivityType(activityType);
							orderItem.setBarCode(barCode);
							orderItem.setStyleCode(styleCode);
							orderItem.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);

							int orderType = Integer.valueOf(type);

							if (orderType == 0) {
								order.setType(OrderTypeEnum.PHYSICAL_ORDER);
							} else if (orderType == 1) {
								order.setType(OrderTypeEnum.SERVICE_ORDER);
							}

							orderItemList.add(orderItem);
							order.setTradeOrderItem(orderItemList);

							TradeOrderInvoice orderInvoice = new TradeOrderInvoice();

							int invoice = Integer.valueOf(isInvoice); // 是否有发票标识
							if (invoice == 1) { // 有发票
								orderInvoice.setId(UuidUtils.getUuid());
								orderInvoice.setId(order.getId());
								orderInvoice.setOrderId(order.getId());
								orderInvoice.setHead(invoiceHead);
								orderInvoice.setContext(invoiceContent);
								order.setTradeOrderInvoice(orderInvoice);
							}

							SysBuyerUser buyerUser = sysBuyerUserMapper.selectByPrimaryKey(userId); // 查询买家用户信息
							if (buyerUser == null) {
								logger.error("查询买家用户信息", "buyerUser 为空-------->" + CodeStatistical.getLineInfo());
								logger.info("查询买家用户信息 buyerUser 为空-------->" + CodeStatistical.getLineInfo());
								throw new Exception("查询买家用户信息异常：buyerUser 为空-------->" + CodeStatistical.getLineInfo());
							}
							int orType = Integer.valueOf(type);
							if (orType == 1) {
								order.setUserPhone(userPhone);
							} else {
								order.setUserPhone(buyerUser.getPhone());
							}
							order.setUserId(userId);
							order.setStoreName(myStoreName);
							order.setStoreId(storeId);
							order.setUpdateTime(new Date());
							order.setPid("0");
							order.setOrderNo(orderNo);

							// 获得随机提货码
							String randomNo = RandomStringUtil.getRandomInt(6);
							order.setPickUpCode(randomNo);

							int intPayType = Integer.valueOf(payType);

							if (intPayType == 0) { // 支付方式：0:货到付款、1：在线支付
								order.setStatus(OrderStatusEnum.UNPAID);
								order.setPayWay(PayWayEnum.PAY_ONLINE);

							} else if (intPayType == 1) {
								order.setStatus(OrderStatusEnum.DROPSHIPPING);
								order.setPayWay(PayWayEnum.CASH_DELIERY);
							}

							BigDecimal skuPri = CalculateMoneyUtil.getOrderBigMoney(skuPriceSum);
							order.setTotalAmount(skuPri); // 订单总金额
							order.setActualAmount(skuPri); // 实付金额
							order.setIncome(skuPri); // 订单收入

							if (actType == 0) {
								order.setActivityType(ActivityTypeEnum.NO_ACTIVITY);
							} else if (actType == 1) {
								order.setActivityType(ActivityTypeEnum.VONCHER);
							} else if (actType == 2) {
								order.setActivityType(ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES);
							} else if (actType == 3) {
								order.setActivityType(ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES);
							} else if (actType == 4) {
								order.setActivityType(ActivityTypeEnum.GROUP_ACTIVITY);
							}

							order.setActivityId(activityId);
							order.setRemark(remark);
							order.setInvoice(WithInvoiceEnum.HAS);
							order.setDisabled(Disabled.valid);
							order.setCreateTime(new Date());

							order.setIsShow(OrderIsShowEnum.yes);
							order.setPaymentStatus(PaymentStatusEnum.STAY_BACK);
							order.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);

							int orderRes = Integer.valueOf(orderResource); // 订单来源
							if (orderRes == 0) {
								order.setOrderResource(OrderResourceEnum.YSCAPP);
							} else if (orderRes == 1) {
								order.setOrderResource(OrderResourceEnum.WECHAT);
							} else if (orderRes == 2) {
								order.setOrderResource(OrderResourceEnum.POS);
							}
							int picType = Integer.valueOf(pickType);
							// 提货类型
							if (picType == 0) {
								order.setPickUpType(PickUpTypeEnum.DELIVERY_DOOR);
							} else if (picType == 1) {
								order.setPickUpType(PickUpTypeEnum.TO_STORE_PICKUP);
							}

							List<AdjustDetailVo> detailList = new ArrayList<AdjustDetailVo>();
							AdjustDetailVo detail = new AdjustDetailVo();

							detail.setBarCode(storeSku.getBarCode());
							detail.setGoodsName(skuName);
							detail.setGoodsSkuId("");
							detail.setMultipleSkuId("");
							detail.setNum(Integer.valueOf(skuNum));

							detail.setPrice(BigGroupPrice);
							detail.setPropertiesIndb(propertiesIndb);
							detail.setStoreSkuId(skuId);
							detailList.add(detail);

							stockVo.setOrderId(order.getId());
							stockVo.setStoreId(storeId);
							stockVo.setUserId(userId);
							stockVo.setAdjustDetailList(detailList);
							stockVo.setStockOperateEnum(StockOperateEnum.ACTIVITY_PLACE_ORDER);

							order.setTradeNum(tradeNum);

							groupRecord.setId(UuidUtils.getUuid()); // 主键
							groupRecord.setGroupGoodsId(skuId); // 团购商品ID
							groupRecord.setStroeId(storeId); // 店铺ID
							groupRecord.setGroupGoodsNum(new Integer(skuNum)); // 团购商品数量
							groupRecord.setUserId(userId); // 买家用户ID
							groupRecord.setOrderId(order.getId()); // 订单ID
							groupRecord.setSaleId(activityId); // 团购活动ID
							groupRecord.setOrderDisabled(Disabled.valid); // 订单状态：0正常，1已失效
						}

					}
				}
			}
		}

		// String fare = jsonData.getString("fare"); //运费(预留)

		/*************** 解析获取参数 end ******************/
		JSONObject json = new JSONObject();
		if (isContent == 1 && isClosed == 1 && status == 1 && groupStatus == 1 && isStock == 1 && violation == 1) { // 判断是否可以下单标识
			isOrder = 1;
			activityGroupRecordService.insertSelective(groupRecord);
			stockManagerService.updateStock(stockVo);
			tradeOrderService.insertTradeOrder(order);
			json.put("orderId", order.getId());
			json.put("orderNo", order.getOrderNo());
			json.put("orderPrice", skuPriceSum.toString());

		}

		// 发送消息 1:货到付款、0：在线支付
		if (payType.equals("0")) {
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_pay_timeout, order.getId());
		} else {
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_delivery_group_timeout, order.getId());
		}

		json.put("isOrder", isOrder); // 是否可以提交订单(1:是,0:否)
		json.put("isClosed", isClosed);
		json.put("status", status); // 团购状态(0:未开始,1:已开始,2:已结束,3:已失效)
		json.put("violation", violation); // 强制上下架标识0:未违规、1:违规下架
		json.put("isContent", isContent); // 限ID的标识1：未超过,0:已超过
		json.put("isStock", isStock); // 是否有库存0:不足,1:足

		json.put("limitTime", 60 * 30);
		json.put("tradeNum", tradeNum);
		return json;
	}

	@Override
	public JSONObject validateStock(JSONObject jsonData) throws Exception {
		List<String> list = new ArrayList<String>();

		List<Object> objList = new ArrayList<Object>();
		List<GoodsStoreSku> stock = new ArrayList<GoodsStoreSku>();

		JSONArray array = jsonData.getJSONArray("list"); // 商品列表
		JSONObject result = new JSONObject();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<GoodsStoreSkuToAppVo> skuToAppVoList = new ArrayList<GoodsStoreSkuToAppVo>();

		for (int i = 0; i < array.size(); i++) { // 循环商品列表
			JSONObject obj = array.getJSONObject(i);
			String skuId = obj.getString("skuId"); // 商品ID
			String updateTime = obj.getString("updateTime"); // 商品修改时间
			Date update = format.parse(updateTime);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", skuId);
			GoodsStoreSku storeSku = goodsStoreSkuService.getGoodsStoreSkuUpdateTime(map); // 查询商品是否发生变化
			if (storeSku == null) {
				logger.error("查询商品是否发生变化", "storeSku 为空-------->" + CodeStatistical.getLineInfo());
				logger.info("查询商品是否发生变化 storeSku 为空-------->" + CodeStatistical.getLineInfo());
				throw new Exception("查询商品是否发生变化异常：storeSku 为空-------->" + CodeStatistical.getLineInfo());
			}
			Date beforeUpdateTime = storeSku.getUpdateTime();

			if (update != beforeUpdateTime) { // 判断商品前后变化

				String propertiesIndb = "";
				String properties = storeSku.getPropertiesIndb();
				if (properties != null && !properties.equals("")) {
					JSONObject jb = JSONObject.fromObject(storeSku.getPropertiesIndb());
					String skuPrperties = jb.get("skuName").toString();
					propertiesIndb = skuPrperties; // SKU属性在数据库中的字符串表示
				}

				GoodsStoreSkuToAppVo skuToAppVo = new GoodsStoreSkuToAppVo();

				skuToAppVo.setId(storeSku.getId()); // 主键
				skuToAppVo.setName(storeSku.getName()); // 商品名称
				skuToAppVo.setAlias(storeSku.getAlias()); // 商品别名
				skuToAppVo.setBarCode(storeSku.getBarCode()); // 条形码
				skuToAppVo.setOnline(storeSku.getOnline().ordinal()); // 是否上架，0:下架、1:上架

				if (storeSku.getGuaranteed() == null || storeSku.getGuaranteed().equals("")) {
					skuToAppVo.setGuaranteed(0); // 服务保障
				} else {
					skuToAppVo.setGuaranteed(Integer.valueOf(storeSku.getGuaranteed())); // 服务保障
				}

				skuToAppVo.setObsolete(storeSku.getObsolete().ordinal()); // 陈旧状态
				skuToAppVo.setPropertiesIndb(propertiesIndb); // SKU属性在数据库中的字符串表示
				skuToAppVo.setMarketPrice(storeSku.getMarketPrice()); // 市场价格
				skuToAppVo.setOnlinePrice(storeSku.getOnlinePrice()); // 线上价格
				skuToAppVo.setTradeMax(storeSku.getTradeMax()); // 单次购买上限(预留)

				skuToAppVoList.add(skuToAppVo);
			}

			list.add(skuId);
		}

		stock = goodsStoreSkuService.getGoodsStoreSkuSelleabed(list); // 查询可销售库存是否发生变化
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < stock.size(); i++) {
			GoodsStoreSku storeSku = stock.get(i);
			String skuId = storeSku.getId(); // 商品ID
			int selleabed = storeSku.getGoodsStoreSkuStock().getSellable(); // 商品可销售库存
			map.put("skuId", skuId);
			map.put("sellableStock", selleabed);
			objList.add(map);
		}

		result.put("detail", objList);
		result.put("goods", skuToAppVoList);

		return result;
	}

	@Override
	public JSONObject confirmSettleMent(String requestStr) throws Exception {
		List<String> list = new ArrayList<String>();
		List<Object> objList = new ArrayList<Object>();
		List<GoodsStoreSku> stock = new ArrayList<GoodsStoreSku>();

		JSONObject jsonObj = JSONObject.fromObject(requestStr);

		String sellerId = jsonObj.getString("userId"); // 商家ID
		JSONObject jsonData = jsonObj.getJSONObject("data");
		String storeId = jsonData.getString("storeId"); // 店铺ID
		// String storeName = jsonData.getString("storeName"); // 店铺名称(预留)
		String payType = jsonData.getString("payType"); // 支付方式

		String payWay = jsonData.getString("payWay"); // 支付类型
		TradeOrder order = new TradeOrder();
		order.setId(UuidUtils.getUuid());
		order.setPospay(payType);

		JSONArray array = jsonData.getJSONArray("list"); // 获取购买商品列表
		BigDecimal sum = new BigDecimal("0.00");
		List<OrderItem> itemList = new ArrayList<OrderItem>();

		StoreInfo store = storeInfoService.selectStoreBaseInfoById(storeId); // 查询店铺基本信息
		if (store == null) {
			logger.error("查询店铺基本信息", "store 为空-------->" + CodeStatistical.getLineInfo());
			logger.info("查询店铺基本信息 store 为空-------->" + CodeStatistical.getLineInfo());
			throw new Exception("查询店铺基本信息异常：store 为空-------->" + CodeStatistical.getLineInfo());
		}
		String myStoreName = store.getStoreName();

		// 单品款数*乘以价格的集合
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);

			String skuId = obj.getString("skuId"); // 商品ID
			String skuNum = obj.getString("skuNum"); // 商品数量
			String skuWeight = obj.getString("skuWeight"); // 商品重量
			String skuPrice = obj.getString("skuPrice"); // 商品价格
			String meteringMethod = obj.getString("meteringMethod"); // 是否计件与称重
			String barCode = obj.getString("barCode"); // 商品条形码

			BigDecimal bigSkuPrice = new BigDecimal(skuPrice);
			int meter = new Integer(meteringMethod);
			BigDecimal skuPriceSum = new BigDecimal("0.000");

			if (meter == 1) {
				BigDecimal bigSkuNum = new BigDecimal(skuNum);
				skuPriceSum = bigSkuNum.multiply(bigSkuPrice); // 单款商品总价格
				sum = sum.add(skuPriceSum); // 所有商品总价格
			} else if (meter == 0) {
				BigDecimal skuWi = new BigDecimal(skuWeight);
				skuPriceSum = skuWi.multiply(bigSkuPrice);
				sum = sum.add(skuPriceSum);
			} else if (meter == 2) {
				// 如果是18位条码,小计从条码字符串中解析 2VWWWWWXXXXXYYYYYZ 小计栏为“YYYYY”除以100
				skuPriceSum = new BigDecimal(barCode.substring(12, 17)).divide(new BigDecimal(100));
				sum = sum.add(skuPriceSum);
			}
			OrderItem orderItem = new OrderItem(skuId, skuPriceSum);
			itemList.add(orderItem);
			list.add(skuId);
		}

		String orderNo = "";

		TradeOrderPay orderPay = new TradeOrderPay();

		orderPay.setId(UuidUtils.getUuid());
		orderPay.setCreateTime(new Date());
		orderPay.setOrderId(order.getId());
		orderPay.setPayAmount(sum);
		orderPay.setPayTime(new Date());

		// 如果是现金支付,网银支付,tradeOrderPay表直接insert数据,如果是支付宝或者微信,暂时不insert,等回调成功后才insert
		if (payWay.equals("4")) {
			order.setTradeOrderPay(orderPay);
			orderPay.setPayType(PayTypeEnum.CASH);
		} else if (payWay.equals("1")) {
			orderPay.setPayType(PayTypeEnum.ALIPAY);
		} else if (payWay.equals("2")) {
			orderPay.setPayType(PayTypeEnum.WXPAY);
		} else if (payWay.equals("7")) {
			order.setTradeOrderPay(orderPay);
			orderPay.setPayType(PayTypeEnum.OFFLINE_BANK);
		}

		// Map<String, String> dmap = new HashMap<String, String>();
		// dmap.put("numerical_type", "XS");
		// dmap.put("numerical_order", "");
		// orderNo = generateNumericalMapper.generateNumericalNumber(dmap);
		// orderNo = generateNumericalService.generateNumberAndSave("XS");
		// Begin 1.0.Z add by zengj
		// 生成订单编号
		orderNo = generatePOSOrderNo(storeId);
		// End 1.0.Z add by zengj

		// 张克能加,生成流水号,支付宝或者微信的时候要用
		order.setTradeNum(TradeNumUtil.getTradeNum());

		order.setStoreName(myStoreName);
		order.setSellerId(sellerId);
		order.setStoreId(storeId);
		order.setPid("0");

		order.setOrderNo(orderNo);
		order.setPayWay(PayWayEnum.LINE_PAY);
		order.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
		// 如果是支付宝或者微信,状态是等待买家支付中
		if ("1".equals(payWay) || "2".equals(payWay)) {
			order.setStatus(OrderStatusEnum.BUYER_PAYING);
		}
		order.setUpdateTime(new Date());

		order.setType(OrderTypeEnum.PHYSICAL_ORDER);
		order.setTotalAmount(sum); // 订单总金额
		// BigDecimal prSum = CalculateMoneyUtil.getOrderBigMoney(sum);
		// order.setActualAmount(prSum); // 订单实际金额
		order.setActualAmount(sum); // 订单实际金额
		order.setIncome(sum); // 收入

		order.setDeliveryTime(new Date()); // 发货时间

		order.setDisabled(Disabled.valid);
		order.setCreateTime(new Date());
		order.setOrderResource(OrderResourceEnum.POS);
		JSONObject json = new JSONObject();
		List<TradeOrderItem> orderItemList = new ArrayList<TradeOrderItem>();
		JSONObject obj = new JSONObject();

		/*************** 解析获取参数 end ******************/
		json.put("orderNo", order.getOrderNo());

		List<Integer> soleStock = new ArrayList<Integer>();

		List<OrderStock> stockNumList = new ArrayList<OrderStock>();
		List<OrderStock> stockWeightList = new ArrayList<OrderStock>();

		// 0:称重,1:计件,2:18位称重

		int buyNum = 0;
		int isOrder = 0; // 是否可以下单标识1:是、0:否
		int isStock = 1; // 是否满足库存(0:不足、1:满足)

		// 张克能优化,把selectSingleSkuStock方法改造成一次查出来,而不是循环查数据库
		List<GoodsStoreSkuStock> stuStockList = goodsStoreSkuStockService.selectSingleSkuStockBySkuIdList(list);

		for (int i = 0; i < array.size(); i++) {
			JSONObject objss = array.getJSONObject(i);

			String skuId = objss.getString("skuId");

			// GoodsStoreSkuStock skuStock =
			// goodsStoreSkuStockService.selectSingleSkuStock(list); //
			// 查询店铺商品库存数量
			GoodsStoreSkuStock skuStock = stuStockList.get(i); // 查询店铺商品库存数量
			if (skuStock == null) {
				logger.error("查询店铺商品库存数量", "skuStock 为空-------->" + CodeStatistical.getLineInfo());
				logger.info("查询店铺商品库存数量 skuStock 为空-------->" + CodeStatistical.getLineInfo());
				throw new Exception("查询店铺商品库存数量异常：skuStock 为空-------->" + CodeStatistical.getLineInfo());
			}
			int sellable = skuStock.getSellable();

			String skuNum = objss.getString("skuNum");
			String skuWeight = objss.getString("skuWeight");
			String meteringMethod = objss.getString("meteringMethod"); // 是否计件与称重

			int meter = Integer.valueOf(meteringMethod);

			if (meter == 1) {
				OrderStock stockNum = new OrderStock();
				stockNum.setId(skuId);
				stockNum.setCount(Integer.valueOf(skuNum));
				stockNum.setSellable(sellable);
				stockNumList.add(stockNum);
				isStock = CalculateOrderStock.calculateSingleOrderStock(stockNumList); // 计件库存异动
			} else if (meter == 0 || meter == 2) {
				OrderStock stockWeight = new OrderStock();
				stockWeight.setId(skuId);
				stockWeight.setCount((new BigDecimal(skuWeight).multiply(new BigDecimal(1000)).intValue()));
				stockWeight.setSellable(sellable);
				stockWeightList.add(stockWeight);
				isStock = CalculateOrderStock.calculateSingleOrderStock(stockWeightList); // 称重、无条码库存异动
			}

			if (!skuNum.equals("")) {
				buyNum = Integer.valueOf(objss.getString("skuNum"));
			} else if (!skuWeight.equals("")) {
				BigDecimal bigSkuWeight = new BigDecimal(skuWeight);
				buyNum = bigSkuWeight.multiply(new BigDecimal(1000)).intValue();
			}
			soleStock.add(buyNum);
		}
		stock = goodsStoreSkuService.getGoodsStoreSkuSelleabed(list); // 查询可销售库存是否发生变化
		Map<String, Object> map = new HashMap<String, Object>();
		String msg = "";
		for (int i = 0; i < stock.size(); i++) {
			int num = soleStock.get(i);
			GoodsStoreSku storeSku = stock.get(i);
			String skuId = storeSku.getId(); // 商品ID
			int selleabed = storeSku.getGoodsStoreSkuStock().getSellable(); // 商品实际库存
			if (num > selleabed) { // 库存不足
				// 商品:***** 条码:******库存不足
				msg += "商品:" + storeSku.getName() + " 条码：" + storeSku.getBarCode() + " 库存不足\n";
				map.put("msg", storeSku.getName() + ",库存不足");
				map.put("skuId", skuId);
				map.put("sellableStock", selleabed);
				objList.add(map);
				obj.put("detail", objList);
			}
		}

		List<StockAdjustVo> stockAdjustList = new ArrayList<StockAdjustVo>();

		// 张克能优化,一次用list in的方式,避免在循环里多次调用selectGoodsStoreSkuDetailNotPri方法
		List<GoodsStoreSku> storeSkuList = goodsStoreSkuService.selectGoodsStoreSkuDetailNotPriByIdList(list);

		for (int i = 0; i < array.size(); i++) {

			TradeOrderItem orderItem = new TradeOrderItem();
			JSONObject objs = array.getJSONObject(i);

			String skuId = objs.getString("skuId"); // 商品ID
			String skuNum = objs.getString("skuNum"); // 商品数量
			String skuWeight = objs.getString("skuWeight"); // 称重商品
			String skuPrice = objs.getString("skuPrice"); // 商品价格

			String meteringMethods = objs.getString("meteringMethod"); // 是否计件与称重
																		// 0是称重
																		// 1是计件
																		// 2是18位称重
			int meters = new Integer(meteringMethods);

			// GoodsStoreSku storeSku =
			// goodsStoreSkuService.selectGoodsStoreSkuDetailNotPri(skuId); //
			// 查询店铺商品信息
			GoodsStoreSku storeSku = storeSkuList.get(i); // 查询店铺商品信息
			if (storeSku == null) {
				logger.error("查询店铺商品信息", "storeSku 为空-------->" + CodeStatistical.getLineInfo());
				logger.info("查询特惠商品信息 activitySaleGoods 为空-------->" + CodeStatistical.getLineInfo());
				throw new Exception("查询店铺商品信息异常： storeSku 为空-------->" + CodeStatistical.getLineInfo());
			}

			String skuName = storeSku.getName(); // 商品名称
			String skuPrperties = "";
			if (storeSku.getPropertiesIndb() != null && !storeSku.getPropertiesIndb().equals("")) {
				JSONObject jb = JSONObject.fromObject(storeSku.getPropertiesIndb());
				skuPrperties = jb.get("skuName").toString();
			}
			String propertiesIndb = skuPrperties;

			List<AdjustDetailVo> detailList = new ArrayList<AdjustDetailVo>();
			AdjustDetailVo detail = new AdjustDetailVo();

			detail.setBarCode(storeSku.getBarCode());
			detail.setGoodsName(skuName);
			detail.setGoodsSkuId(storeSku.getSkuId());
			detail.setMultipleSkuId(storeSku.getMultipleSkuId());

			if (meters == 0 || meters == 2) {
				BigDecimal bigSkuW = new BigDecimal(skuWeight);
				detail.setNum(bigSkuW.multiply(new BigDecimal(1000)).intValue());
				detail.setIsWeightSku("Y");
			} else if (meters == 1) {
				detail.setNum(Integer.valueOf(skuNum));
			}

			detail.setPrice(new BigDecimal(skuPrice));
			detail.setPropertiesIndb(propertiesIndb);
			detail.setStoreSkuId(skuId);
			detailList.add(detail);

			StockAdjustVo stockVo = new StockAdjustVo();

			stockVo.setOrderId(order.getId());
			stockVo.setStoreId(storeId);
			stockVo.setUserId(sellerId);
			stockVo.setAdjustDetailList(detailList);
			// 0:云钱包,1:支付宝支付,2:微信支付,3:京东支付,4:现金支付,5:友门鹿垫付,6:网银支付,7:银行转账
			// 现金,网银支付是POS_PLACE_ORDER, 支付宝,微信扫码是PLACE_ORDER("下订单"),
			if (payWay.equals("1") || payWay.equals("2")) {
				stockVo.setStockOperateEnum(StockOperateEnum.PLACE_ORDER);
			} else {
				stockVo.setStockOperateEnum(StockOperateEnum.POS_PLACE_ORDER);
				stockAdjustList.add(stockVo);
			}

			// 是否满足库存(0:不足、1:满足)
			if (isStock == 1) {
				try {
					stockManagerService.updateStock(stockVo);
				} catch (StockException e) {
					logger.error("修改异常", e);
				}
			}

			String spuType = objs.getString("spuType"); // 商品类型
			String mainPicPrl = objs.getString("mainPicPrl"); // 主图
			String serviceAssurance = objs.getString("serviceAssurance"); // 服务保障
			String barCode = objs.getString("barCode"); // 条形码
			String meteringMethod = objs.getString("meteringMethod"); // 是否计件与称重
																		// 0是称重
																		// 1是计件
																		// 2是18位称重
			String styleCode = objs.getString("styleCode"); // 款码

			BigDecimal bigSkuPrice = new BigDecimal(skuPrice);

			orderItem.setId(UuidUtils.getUuid());
			orderItem.setStoreSkuId(skuId);
			orderItem.setOrderId(order.getId());
			orderItem.setSkuName(skuName);
			// 如果是18位的条码,barCode字段要查询商品中的barCode,否则,就用入参中的barCode
			if (meters == 2) {
				orderItem.setBarCode(storeSku.getBarCode());
			} else {
				orderItem.setBarCode(barCode);
			}

			orderItem.setPropertiesIndb(propertiesIndb);

			orderItem.setCreateTime(new Date());
			orderItem.setUnitPrice(new BigDecimal(skuPrice));
			orderItem.setStyleCode(styleCode);
			int meter = new Integer(meteringMethod);

			if (meter == 1) {
				if (!skuNum.equals("")) {
					BigDecimal bigSkuNum = new BigDecimal(skuNum);
					BigDecimal skuPriceSum = bigSkuNum.multiply(bigSkuPrice); // 单款商品总价格
					orderItem.setQuantity(new Integer(skuNum));
					orderItem.setIncome(skuPriceSum);
					orderItem.setActualAmount(skuPriceSum);
					orderItem.setTotalAmount(skuPriceSum);
				}
			} else if (meter == 0) {
				if (!skuWeight.equals("")) {
					BigDecimal bigSkuWeight = new BigDecimal(skuWeight);
					orderItem.setWeight(bigSkuWeight);
					BigDecimal totalAmount = bigSkuWeight.multiply(bigSkuPrice);
					orderItem.setIncome(totalAmount);
					orderItem.setTotalAmount(totalAmount);
					orderItem.setActualAmount(totalAmount);
				}
			} else if (meter == 2) {
				if (!skuWeight.equals("")) {
					BigDecimal bigSkuWeight = new BigDecimal(skuWeight);
					orderItem.setWeight(bigSkuWeight);
					BigDecimal innerBigSkuPrice = new BigDecimal(barCode.substring(12, 17)).divide(new BigDecimal(100));
					orderItem.setIncome(innerBigSkuPrice);
					orderItem.setTotalAmount(innerBigSkuPrice);
					orderItem.setActualAmount(innerBigSkuPrice);
				}
			}

			orderItem.setPropertiesIndb(propertiesIndb);
			orderItem.setStoreSpuId(storeSku.getStoreSpuId());
			orderItem.setMainPicPrl(mainPicPrl);
			int intSpuType = Integer.valueOf(spuType);

			if (intSpuType == 0) {
				orderItem.setSpuType(GoodsTypeEnum.SINGLE_GOODS);
			} else if (intSpuType == 1) {
				orderItem.setSpuType(GoodsTypeEnum.SERVICE_GOODS);
			} else if (intSpuType == 2) {
				orderItem.setSpuType(GoodsTypeEnum.NO_BAR_CODE_GOODS);
			}

			orderItem.setServiceAssurance(Integer.valueOf(serviceAssurance));
			orderItemList.add(orderItem);
		}

		order.setTradeOrderItem(orderItemList);

		obj.put("message", msg);
		Date nowDate = new Date();
		long lnDate = nowDate.getTime();
		SimpleDateFormat formats = new SimpleDateFormat("YYYY-MM-dd");
		String strNow = formats.format(nowDate);
		System.out.println("strNow------>" + strNow);
		List<ImsDaily> dailyList = imsDailyService.selectTodayByStoreId(storeId);
		int isPass = 1; // 下单时间是否有日结判断并在日结之前(1:通过、0:不通过)
		if (dailyList.size() > 0) {
			for (int i = 0; i < dailyList.size(); i++) {
				ImsDaily daily = dailyList.get(i);
				Date dailyDate = daily.getDailyDate();
				long lndaily = dailyDate.getTime();
				if (lnDate > lndaily) {
					isPass = 0;
				}
			}
		}

		if (isPass == 1 && isStock == 1) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = format.format(new Date());
			tradeOrderService.insertPosTradeOrder(order);
			obj.put("orderNo", order.getOrderNo());
			obj.put("orderId", order.getId());
			obj.put("orderTime", createTime);
			if ("1".equals(payWay) || "2".equals(payWay)) {
				obj.put("orderPay_payAmount", sum);
				obj.put("payWay", payWay);
				obj.put("orderId", order.getId());
			}
			isOrder = 1;
		}
		obj.put("isOrder", isOrder);
		obj.put("isStock", isStock);
		obj.put("isPass", isPass);

		// 统一发送MQ生成消息
		// Begin modified by maojj 2016-08-26 重复发消息问题
		// stockMQProducer.sendMessage(stockAdjustList);
		// End modified by maojj 2016-08-26 重复发消息问题

		return obj;
	}

	// Begin 1.0.Z add by zengj
	/**
	 * @Description: 生成订单编号
	 * @param storeId 店铺ID
	 * @return void  无
	 * @throws ServiceException 自定义异常
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private String generatePOSOrderNo(String storeId) throws ServiceException {
		// 查询店铺机构信息
		StoreBranches storeBranches = storeBranchesService.findBranches(storeId);
		if (storeBranches == null || StringUtils.isEmpty(storeBranches.getBranchCode())) {
			throw new ServiceException(LogConstants.STORE_BRANCHE_NOT_EXISTS);
		}
		String orderNo = generateNumericalService.generateOrderNo(OrderNoUtils.PHYSICAL_ORDER_PREFIX,
				storeBranches.getBranchCode(), OrderNoUtils.OFFLINE_POS_ID);
		logger.info("生成订单编号：{}", orderNo);
		return orderNo;
	}
	// End 1.0.Z add by zengj

}
