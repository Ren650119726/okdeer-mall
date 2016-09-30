package com.okdeer.mall.order.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
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
			for(UserAddressVo addr : addrList){
				if(addr.getIsOutRange() == 0){
					respData.setDefaultAddress(addr);
					break;
				}
			}
		}
		
		// 查询店铺地址
		SpuTypeEnum skuType = (SpuTypeEnum)req.getContext().get("skuType");
		if(skuType == SpuTypeEnum.fwdDdxfSpu){
			// 到店消费的商品，需要返回店铺地址
			MemberConsigneeAddress storeAddrObj = memberConsigneeAddressService.findByStoreId(req.getData().getStoreId());
			StringBuilder storeAddr = new StringBuilder();
			storeAddr.append(ConvertUtil.format(storeAddrObj.getProvinceName()))
					.append(ConvertUtil.format(storeAddrObj.getCityName()))
					.append(ConvertUtil.format(storeAddrObj.getAreaName()))
					.append(ConvertUtil.format(storeAddrObj.getAreaExt()));
			respData.getStoreInfo().setAddress(storeAddr.toString());
		}
		req.setComplete(true);
	}
	
}
