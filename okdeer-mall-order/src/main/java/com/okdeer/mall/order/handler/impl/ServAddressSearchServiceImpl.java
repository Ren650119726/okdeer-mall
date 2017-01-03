package com.okdeer.mall.order.handler.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.StoreInfoApi;
import com.okdeer.base.common.utils.StringUtils;
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
import com.okdeer.mall.system.utils.ConvertUtil;
/**
 * ClassName: SeckillAddressSearchServiceImpl 
 * @Description: 服务订单地址查询
 * @author wushp
 * @date 2016年9月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.1.0			2016年9月29日			wushp			服务订单地址查询
 */

@Service("servAddressSearchService")
public class ServAddressSearchServiceImpl implements RequestHandler<ServiceOrderReq, ServiceOrderResp> {

	/**
	 * 地址
	 */
	@Resource
	private MemberConsigneeAddressMapper memberConsigneeAddressMapper;
	
	/**
	 * 店铺信息查询Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoApi storeInfoServiceApi;
	
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
			MemberConsigneeAddress memberConsignee = memberConsigneeAddressMapper.getSellerDefaultAddress(reqData.getStoreId());
			//StoreInfo storeInfo = storeInfoServiceApi.selectDefaultAddressById(reqData.getStoreId());
			//MemberConsigneeAddress memberConsignee = storeInfo.getMemberConsignee();
			if (memberConsignee != null) {
				UserAddressVo userAddressVo = new UserAddressVo();
				BeanUtils.copyProperties(userAddressVo, memberConsignee);
				respData.setDefaultAddress(userAddressVo);
			}
			// 到店消费的商品，需要返回店铺地址
			StringBuilder storeAddr = new StringBuilder();
			storeAddr.append(ConvertUtil.format(memberConsignee.getProvinceName()))
					.append(ConvertUtil.format(memberConsignee.getCityName()))
					.append(ConvertUtil.format(memberConsignee.getAreaName()))
					.append(ConvertUtil.format(memberConsignee.getAreaExt()))
					.append(ConvertUtil.format(memberConsignee.getAddress()));
			if (StringUtils.isBlank(memberConsignee.getProvinceName())) {
				storeAddr = new StringBuilder();
				storeAddr.append(memberConsignee.getArea().trim());
				storeAddr.append(memberConsignee.getAddress());
			} 
			respData.getStoreInfo().setAddress(storeAddr.toString());
			req.setComplete(true);
			return;
		}
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
		//当地址为空时 为null肯定不包含 或 不包含时 start 涂志定
		if (map == null || !map.containsKey("provinceName")) {
			req.setComplete(true);
			return;
		} 
		// end 涂志定
		UserAddressVo userAddressVo = new UserAddressVo();
		// 将map转成bean
		BeanUtils.populate(userAddressVo, map);
		respData.setDefaultAddress(userAddressVo);
		req.setComplete(true);
	}
}
