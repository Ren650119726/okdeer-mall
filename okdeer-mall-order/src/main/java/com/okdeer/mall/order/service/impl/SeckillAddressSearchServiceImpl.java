package com.okdeer.mall.order.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.mall.common.vo.Request;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.member.mapper.MemberConsigneeAddressMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.member.service.MemberConsigneeAddressServiceApi;
import com.okdeer.mall.member.member.vo.UserAddressVo;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;
/**
 * ClassName: SeckillAddressSearchServiceImpl 
 * @Description: 秒杀地址查询
 * @author maojj
 * @date 2016年9月24日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构1.1			2016年9月24日				maojj			秒杀地址查询
 */

@Service("seckillAddressSearchService")
public class SeckillAddressSearchServiceImpl implements RequestHandler<ServiceOrderReq, ServiceOrderResp> {

	@Resource
	private MemberConsigneeAddressMapper memberConsigneeAddressMapper;
	
	/**
	 * 店铺信息查询Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoServiceApi;
	
	/**
	 * 地址service
	 */
	@Reference(version = "1.0.0", check = false)
	private MemberConsigneeAddressServiceApi memberConsigneeAddressService;
	
	@Override
	public void process(Request<ServiceOrderReq> req, Response<ServiceOrderResp> resp) throws Exception {
		ServiceOrderResp respData = resp.getData();
		ServiceOrderReq reqData = req.getData();
		OrderTypeEnum orderType = reqData.getOrderType();
		// 上门服务订单
		if (orderType != null && orderType == OrderTypeEnum.SERVICE_STORE_ORDER) {
			// 上门服务订单获取用户地址
			this.getDoorServiceOrderAddress(req,respData);
			return;
		}
		// 到店消费订单
		if (orderType != null && orderType == OrderTypeEnum.STORE_CONSUME_ORDER) {
			StoreInfo storeInfo = storeInfoServiceApi.selectDefaultAddressById(reqData.getStoreId());
			MemberConsigneeAddress memberConsignee = storeInfo.getMemberConsignee();
			if (memberConsignee != null) {
				UserAddressVo userAddressVo = new UserAddressVo();
				BeanUtils.copyProperties(userAddressVo, memberConsignee);
				respData.setDefaultAddress(userAddressVo);
			}
			// 店铺详细地址
			req.setComplete(true);
			return;
		}
		
		// 区域类型：0全国，1区域
		String seckillRangeType = String.valueOf(req.getContext().get("seckillRangeType"));
		String storeAreaType = String.valueOf(req.getContext().get("storeAreaType"));
		String userId = req.getData().getUserId();
		// 有效的地址列表
		List<UserAddressVo> addrList = null;
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("userId", userId);
		if("0".equals(seckillRangeType) && "0".equals(storeAreaType)){
			// 如果秒杀区域类型和店铺服务区域都是全国范围，则用户地址均有效
			addrList = memberConsigneeAddressMapper.findAddrWithUserId(userId);
		}else if("0".equals(seckillRangeType) && "1".equals(storeAreaType)){
			// 如果秒杀区域类型为全国范围，服务店铺服务范围为区域，则按照店铺服务范围查询用户地址
			condition.put("storeId", req.getData().getStoreId());
			addrList = memberConsigneeAddressMapper.findAddrWithStoreServRange(condition);
		}else if("1".equals(seckillRangeType) && "0".equals(storeAreaType)){
			// 如果秒杀区域类型为区域，服务店铺服务范围为全国，则按照秒杀服务范围查询
			condition.put("activitySeckillId", req.getData().getSeckillId());
			addrList = memberConsigneeAddressMapper.findAddrWithSeckillServRange(condition);
		}else if("1".equals(seckillRangeType) && "1".equals(storeAreaType)){
			// 如果秒杀区域类型和店铺服务区域类型都是区域，则按照两者的交集查询用户地址
			condition.put("storeId", req.getData().getStoreId());
			condition.put("activitySeckillId", req.getData().getSeckillId());
			addrList = memberConsigneeAddressMapper.findAddrWithServRange(condition);
		}
		if(!CollectionUtils.isEmpty(addrList)){
			respData.setDefaultAddress(addrList.get(0));
		}
		req.setComplete(true);
	}
	
	/**
	 * 
	 * @Description: 上门服务订单获取用户地址
	 * @param req 请求参数
	 * @param respData 响应参数的data对象
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private void getDoorServiceOrderAddress(Request<ServiceOrderReq> req, ServiceOrderResp respData) throws Exception {
		ServiceOrderReq reqData = req.getData();
		// 构建查询参数
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", reqData.getUserId());
		params.put("storeId", reqData.getStoreId());
		// 查询用户的所有收货地址
		Map<String, Object> map = memberConsigneeAddressService.findUserDefaultAddress(params);
		if (!map.containsKey("provinceName")) {
			req.setComplete(true);
			return;
		} 
		UserAddressVo userAddressVo = new UserAddressVo();
		// 将map转成bean
		BeanUtils.populate(userAddressVo, map);
		respData.setDefaultAddress(userAddressVo);
		req.setComplete(true);
	}
}
