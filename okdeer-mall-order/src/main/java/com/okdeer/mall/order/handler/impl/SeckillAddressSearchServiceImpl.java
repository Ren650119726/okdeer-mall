package com.okdeer.mall.order.handler.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.mall.common.vo.Request;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.member.mapper.MemberConsigneeAddressMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.member.service.MemberConsigneeAddressServiceApi;
import com.okdeer.mall.member.member.vo.UserAddressVo;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;
import com.okdeer.mall.system.utils.ConvertUtil;
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
	 * 地址service
	 */
	@Reference(version = "1.0.0", check = false)
	private MemberConsigneeAddressServiceApi memberConsigneeAddressService;
	
	@Override
	public void process(Request<ServiceOrderReq> req, Response<ServiceOrderResp> resp) throws Exception {
		ServiceOrderResp respData = resp.getData();
		SpuTypeEnum skuType = (SpuTypeEnum)req.getContext().get("skuType");
		if(skuType == SpuTypeEnum.fwdDdxfSpu){
			// 到店消费，返回店铺地址
			processConsumeToStore(req.getData().getStoreId(),respData);
		}else{
			// 上门服务，返回用户有效的地址
			processServToDoor(req,respData);
		}
	}
	
	
	/**
	 * @Description: 处理到店消费。如果秒杀商品是到店消费的，则返回店铺地址
	 * @param storeId
	 * @param respData   
	 * @author maojj
	 * @date 2016年11月18日
	 */
	private void processConsumeToStore(String storeId, ServiceOrderResp respData) throws Exception {
		// 到店消费的商品，需要返回店铺地址
		MemberConsigneeAddress storeAddrObj = memberConsigneeAddressService.getSellerDefaultAddress(storeId);
		StringBuilder storeAddr = new StringBuilder();
		storeAddr.append(ConvertUtil.format(storeAddrObj.getProvinceName()))
				.append(ConvertUtil.format(storeAddrObj.getCityName()))
				.append(ConvertUtil.format(storeAddrObj.getAreaName()))
				.append(ConvertUtil.format(storeAddrObj.getAreaExt()))
				.append(ConvertUtil.format(storeAddrObj.getAddress()));
		respData.getStoreInfo().setAddress(storeAddr.toString());
	}
	
	/**
	 * @Description: 处理上门服务
	 * @param req
	 * @param respData   
	 * @author maojj
	 * @date 2016年11月18日
	 */
	private void processServToDoor(Request<ServiceOrderReq> req,ServiceOrderResp respData){
		// 区域类型：0全国，1区域
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("userId", req.getData().getUserId());
		condition.put("storeId", req.getData().getStoreId());
		condition.put("activitySeckillId", req.getData().getSeckillId());
		condition.put("seckillRangeType", req.getContext().get("seckillRangeType"));
		condition.put("storeAreaType", req.getContext().get("storeAreaType"));
		List<UserAddressVo> userAddrList = memberConsigneeAddressService.findUserAddr(condition);
		if(!CollectionUtils.isEmpty(userAddrList)){
			for(UserAddressVo addr : userAddrList){
				if(addr.getIsOutRange() == 0){
					respData.setDefaultAddress(addr);
					break;
				}
			}
		}
	}
}
