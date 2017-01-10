package com.okdeer.mall.order.handler.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.StockManagerServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckillRecord;
import com.okdeer.mall.activity.seckill.mapper.ActivitySeckillRecordMapper;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.member.mapper.MemberConsigneeAddressMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.builder.TradeOrderBuilder;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.service.GenerateNumericalService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.timer.TradeOrderTimer;
import com.okdeer.mall.system.mq.RollbackMQProducer;
import com.okdeer.mall.system.utils.ConvertUtil;

/**
 * ClassName: PlaceSeckillOrderServiceImpl 
 * @Description: 秒杀下单
 * @author maojj
 * @date 2017年1月7日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.0 			2017年1月7日				maojj
 */
@Service("placeSeckillOrderService")
public class PlaceSeckillOrderServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto> {

	/**
	 * 生成编号的service
	 */
	@Resource
	private GenerateNumericalService generateNumericalService;

	@Resource
	private MemberConsigneeAddressMapper memberConsigneeAddressMapper;

	@Resource
	private ActivitySeckillRecordMapper activitySeckillRecordMapper;

	/**
	 * 订单服务Service
	 */
	@Resource
	private TradeOrderService tradeOrderService;

	/**
	 * 库存管理Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StockManagerServiceApi stockManagerServiceApi;

	/**
	 * 订单超时计时器
	 */
	@Autowired
	private TradeOrderTimer tradeOrderTimer;

	/**
	 * 消息生产者
	 */
	@Resource
	private RollbackMQProducer rollbackMQProducer;
	
	@Resource
	private TradeOrderBuilder tradeOrderBuilder;

	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		String rpcId = null;
		try {
			PlaceOrderParamDto paramDto = req.getData();
			PlaceOrderDto respData = resp.getData();
			// 根据请求构建订单
			TradeOrder tradeOrder = tradeOrderBuilder.build(paramDto);
			MemberConsigneeAddress userUseAddr = (MemberConsigneeAddress) paramDto.get("userUseAddr");
			if (paramDto.getSkuType() != OrderTypeEnum.STORE_CONSUME_ORDER && userUseAddr == null) {
				resp.setResult(ResultCodeEnum.ADDRESS_NOT_EXSITS);
				return;
			}
			// 保存用户秒杀记录
			activitySeckillRecordMapper.add(buildSeckillRecord(tradeOrder,paramDto));
			// 保存订单和订单项信息，并发送消息
			tradeOrderService.insertTradeOrder(tradeOrder);
			// 更新用户最后使用的地址
			updateLastUseAddr(userUseAddr);
			// 更新库存
			rpcId = UuidUtils.getUuid();
			updateStock(tradeOrder, paramDto, rpcId);
			// 超时未支付的，取消订单
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_pay_timeout, tradeOrder.getId());
			respData.setOrderId(tradeOrder.getId());
			respData.setOrderNo(tradeOrder.getOrderNo());
			respData.setOrderPrice(ConvertUtil.format(tradeOrder.getActualAmount()));
			respData.setTradeNum(tradeOrder.getTradeNum());
			respData.setLimitTime(1800);

		} catch (Exception e) {
			if (rpcId != null) {
				rollbackMQProducer.sendStockRollbackMsg(rpcId);
			}
			throw e;
		}
	}

	private ActivitySeckillRecord buildSeckillRecord(TradeOrder order,PlaceOrderParamDto paramDto) {
		ActivitySeckillRecord activitySeckillRecord = new ActivitySeckillRecord();
		activitySeckillRecord.setId(UuidUtils.getUuid());
		// 秒杀活动ID
		activitySeckillRecord.setActivitySeckillId(paramDto.getSeckillId());
		// 买家ID
		activitySeckillRecord.setBuyerUserId(paramDto.getUserId());
		activitySeckillRecord.setStoreId(paramDto.getStoreId());
		activitySeckillRecord.setOrderId(order.getId());
		// 活动商品ID
		activitySeckillRecord.setGoodsStoreSkuId(paramDto.getSkuList().get(0).getStoreSkuId());
		activitySeckillRecord.setOrderNo(order.getOrderNo());
		activitySeckillRecord.setOrderDisabled("0");
		return activitySeckillRecord;
	}

	public void updateLastUseAddr(MemberConsigneeAddress userUseAddr) {
		userUseAddr.setUseTime(DateUtils.getSysDate());
		memberConsigneeAddressMapper.updateByPrimaryKeySelective(userUseAddr);
	}
	
	/**
	 * @Description: 更新库存
	 * @param order 订单对象
	 * @param reqDto 请求对象
	 * @return void  
	 * @throws Exception 异常
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void updateStock(TradeOrder order, PlaceOrderParamDto paramDto, String rpcId) throws Exception {

		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		CurrentStoreSkuBo skuBo = parserBo.getCurrentStoreSkuBo(paramDto.getSkuList().get(0).getStoreSkuId());

		StockAdjustVo stockAjustVo = new StockAdjustVo();
		stockAjustVo.setRpcId(rpcId);
		stockAjustVo.setOrderId(order.getId());
		stockAjustVo.setOrderNo(order.getOrderNo());
		stockAjustVo.setOrderResource(order.getOrderResource());
		stockAjustVo.setOrderType(order.getType());

		stockAjustVo.setStoreId(paramDto.getStoreId());
		stockAjustVo.setUserId(paramDto.getUserId());
		stockAjustVo.setMethodName(this.getClass().getName() + ".process");
		stockAjustVo.setStockOperateEnum(StockOperateEnum.ACTIVITY_PLACE_ORDER);

		List<AdjustDetailVo> adjustDetailList = new ArrayList<AdjustDetailVo>();
		AdjustDetailVo adjustDetailVo = new AdjustDetailVo();
		adjustDetailVo.setBarCode(skuBo.getBarCode());
		adjustDetailVo.setGoodsName(skuBo.getName());
		adjustDetailVo.setGoodsSkuId(skuBo.getSkuId());
		adjustDetailVo.setMultipleSkuId(skuBo.getMultipleSkuId());
		adjustDetailVo.setNum(skuBo.getQuantity());
		adjustDetailVo.setPrice(skuBo.getOnlinePrice());
		adjustDetailVo.setPropertiesIndb(skuBo.getPropertiesIndb());
		adjustDetailVo.setStoreSkuId(skuBo.getId());
		adjustDetailVo.setGoodsSkuId(skuBo.getSkuId());
		adjustDetailList.add(adjustDetailVo);

		stockAjustVo.setAdjustDetailList(adjustDetailList);
		stockManagerServiceApi.updateStock(stockAjustVo);
	}
}
