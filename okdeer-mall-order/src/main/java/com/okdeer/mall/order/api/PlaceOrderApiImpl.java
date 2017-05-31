package com.okdeer.mall.order.api;

import static com.okdeer.common.consts.ELTopicTagConstants.TAG_GOODS_EL_UPDATE;
import static com.okdeer.common.consts.ELTopicTagConstants.TOPIC_GOODS_SYNC_EL;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.common.message.Message;
import com.dianping.cat.Cat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.okdeer.archive.goods.dto.ActivityMessageParamDto;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.jxc.common.utils.JsonMapper;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.bo.AppAdapter;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.AppStoreDto;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.enums.OrderOptTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.PlaceOrderTypeEnum;
import com.okdeer.mall.order.handler.RequestHandlerChain;
import com.okdeer.mall.order.service.PlaceOrderApi;
import com.okdeer.mall.system.utils.ConvertUtil;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.PlaceOrderApi")
public class PlaceOrderApiImpl implements PlaceOrderApi {
	
	private static final Logger logger = LoggerFactory.getLogger(PlaceOrderApiImpl.class);

	/**
	 * 便利店确认订单Service
	 */
	@Resource
	private RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> confirmOrderService;

	/**
	 * 服务店确认订单Service
	 */
	@Resource
	private RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> confirmServOrderService;
	
	/**
	 * 秒杀确认订单Service
	 */
	@Resource
	private RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> confirmSeckillOrderService;

	/**
	 * 便利店提交订单Service
	 */
	@Resource
	private RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> submitOrderService;

	/**
	 * 服务店提交订单Service
	 */
	@Resource
	private RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> submitServOrderService;
	
	/**
	 * 秒杀提交订单Service
	 */
	@Resource
	private RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> submitSeckillOrderService;

	/**
	 * mq注入
	 */
	@Autowired
	private RocketMQProducer rocketMQProducer;
	
	@Resource
	private RedisLockRegistry redisLockRegistry;

	@Override
	public Response<PlaceOrderDto> confirmOrder(Request<PlaceOrderParamDto> req) throws Exception {
		Response<PlaceOrderDto> resp = new Response<PlaceOrderDto>();
		resp.setData(new PlaceOrderDto());
		// 设置订单操作类型
		req.getData().setOrderOptType(OrderOptTypeEnum.ORDER_SETTLEMENT);
		RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> handlerChain = null;
		switch (req.getData().getOrderType()) {
			case CVS_ORDER:
				handlerChain = confirmOrderService;
				break;
			case SRV_ORDER:
				handlerChain = confirmServOrderService;
				break;
			case SECKILL_ORDER:
				handlerChain = confirmSeckillOrderService;
				break;
			default:
				break;
		}
		handlerChain.process(req, resp);
		// 填充响应结果
		fillResponse(req, resp);
		return resp;
	}

	/**
	 * @Description: 填充响应结果
	 * @param req
	 * @param resp   
	 * @author maojj
	 * @date 2017年1月17日
	 */
	private void fillResponse(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		// 店铺信息
		StoreInfo storeInfo = (StoreInfo) paramDto.get("storeInfo");
		// 秒杀信息
		ActivitySeckill seckillInfo = (ActivitySeckill) paramDto.get("seckillInfo");
		// 解析请求之后的店铺商品信息
		StoreSkuParserBo parserBo = (StoreSkuParserBo) paramDto.get("parserBo");
		// 返回App店铺信息
		AppStoreDto appStoreDto = AppAdapter.convert(storeInfo);
		if (parserBo != null) {
			resp.getData().setOrderFare(ConvertUtil.format(parserBo.getFare()));
			resp.getData().setFavour(ConvertUtil.format(parserBo.getTotalLowFavour()));
		}
		// 设置返回给App的店铺信息
		resp.getData().setStoreInfo(appStoreDto);
		// 设置返回给App的商品信息
		resp.getData().setSkuList(AppAdapter.convert(parserBo));
		if(req.getData().getOrderType() != PlaceOrderTypeEnum.CVS_ORDER){
			resp.getData().setStoreServExt(AppAdapter.convertAppStoreServiceExtDto(storeInfo));
			resp.getData().setSeckillInfo(AppAdapter.convert(seckillInfo));
			List<CurrentStoreSkuBo> skuList = new ArrayList<CurrentStoreSkuBo>();
			// 服务商品判定支付方式：如果多个商品，一定是线上支付。单个商品，根据商品设置的支付方式进行处理
			if (parserBo != null && CollectionUtils.isNotEmpty(parserBo.getCurrentSkuMap().values())) {
				for(CurrentStoreSkuBo skuBo:parserBo.getCurrentSkuMap().values()){
					if(StringUtils.isEmpty(paramDto.getVersion()) && skuBo.getActivityType() == ActivityTypeEnum.SALE_ACTIVITIES.ordinal()){
						// 如果是2.1版本之前的特惠商品，特惠商品的可售库存中存储特惠商品的锁定库存
						skuBo.setSellable(skuBo.getLocked());;
					}
					skuList.add(skuBo);
				}
				
				if (skuList.size() > 1) {
					resp.getData().setPaymentMode(PayWayEnum.PAY_ONLINE.ordinal());
				} else {
					if (skuList.get(0).getPaymentMode() == 1) {
						resp.getData().setPaymentMode(4);
					} else {
						resp.getData().setPaymentMode(skuList.get(0).getPaymentMode());
					}
				}
			}
		}
		// 商品信息发生变化丢消息
		if(resp.getCode() == ResultCodeEnum.GOODS_IS_CHANGE.getCode() || resp.getCode() == ResultCodeEnum.PART_GOODS_IS_CHANGE.getCode()){
			if(parserBo != null){
				structureProducer(parserBo.getSkuIdList(),TAG_GOODS_EL_UPDATE);
			}
		}
		if (resp.isSuccess() && req.getData().getOrderType() == PlaceOrderTypeEnum.CVS_ORDER
				&& StringUtils.isEmpty(resp.getMessage())
				&& parserBo != null && parserBo.isCloseLow()){
			// 低价活动已关闭，给出响应的提示语
			resp.setMessage(ResultCodeEnum.LOW_IS_CLOSED.getDesc());
		}
		resp.getData().setCurrentTime(System.currentTimeMillis());
	}

	@Override
	public Response<PlaceOrderDto> submitOrder(Request<PlaceOrderParamDto> req) throws Exception {
		Response<PlaceOrderDto> resp = new Response<PlaceOrderDto>();
		resp.setData(new PlaceOrderDto());
		req.getData().setOrderOptType(OrderOptTypeEnum.ORDER_SUBMIT);
		RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> handlerChain = null;
		switch (req.getData().getOrderType()) {
			case CVS_ORDER:
				handlerChain = submitOrderService;
				break;
			case SRV_ORDER:
				handlerChain = submitServOrderService;
				break;
			case SECKILL_ORDER:
				handlerChain = submitSeckillOrderService;
				break;
			default:
				break;
		}
		// Begin V2.4 modified by maojj 2017-05-27
		// 获取锁
		Lock lock = obtain(req.getData());
		try{
			if(lock == null || lock.tryLock(10, TimeUnit.SECONDS)){
				handlerChain.process(req, resp);
			}else{
				logger.error("提交订单，获取锁失败，请求参数为：{}",JsonMapper.nonDefaultMapper().toJson(req.getData()));
				resp.setResult(ResultCodeEnum.FAIL);
			}
		}finally{
			if(lock != null){
				lock.unlock();
			}
		}
		// End V2.4 modified by maojj 2017-05-27
		// 下单埋点
		Cat.logMetricForCount("submitOrder");
		resp.getData().setCurrentTime(System.currentTimeMillis());
		if(!resp.isSuccess()){
			// 如果处理失败
			fillResponse(req, resp);
		}
		return resp;
	}

	/**
	 * 发送消息同步数据到搜索引擎执行
	 *
	 * @param list List<String>
	 * @throws Exception
	 */
	private void structureProducer(List<String> list, String tag) throws Exception {
		if(CollectionUtils.isEmpty(list)){
			return;
		}
		ActivityMessageParamDto paramDto = new ActivityMessageParamDto();
		paramDto.setSkuIds(list);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(paramDto);
		Message msg = new Message(TOPIC_GOODS_SYNC_EL, tag, json.getBytes(Charsets.UTF_8));
		rocketMQProducer.send(msg);
	}
	
	/**
	 * @Description: 获取分布式锁
	 * @param paramDto
	 * @return   
	 * @author maojj
	 * @date 2017年5月27日
	 */
	private Lock obtain(PlaceOrderParamDto paramDto){
		// 订单参与的活动类型
		ActivityTypeEnum actType = paramDto.getActivityType();
		if (actType != ActivityTypeEnum.VONCHER
				&& actType != ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES) {
			// 如果提交订单不是满减也不是代金券，则无需加锁
			return null;
		}
		// 锁的键值为：活动类型名称（代金券、满减） : 账户Id : 活动Id（代金券、满减Id）
		String limitActId = null;
		if(actType == ActivityTypeEnum.VONCHER){
			// 如果是代金券，代金券Id为活动项Id
			limitActId = paramDto.getActivityItemId();
		}else if(actType == ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES){
			limitActId = paramDto.getActivityId();
		}
		String lockKey = String.format("%s:%s:%s", actType.name(),paramDto.getUserId(),limitActId);
		return redisLockRegistry.obtain(lockKey);
	}
}