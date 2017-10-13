package com.okdeer.mall.order.handler.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.discount.entity.ActivityBusinessRel;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.enums.ActivityBusinessType;
import com.okdeer.mall.activity.discount.mapper.ActivityBusinessRelMapper;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.member.bo.UserAddressFilterCondition;
import com.okdeer.mall.member.mapper.GroupUserAddrFilterStrategy;
import com.okdeer.mall.member.mapper.MemberConsigneeAddressMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.member.vo.MemberConsigneeAddressVo;
import com.okdeer.mall.member.member.vo.UserAddressVo;
import com.okdeer.mall.member.service.MemberConsigneeAddressService;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.handler.RequestHandler;

/**
 * ClassName: FindUserAddressServiceImpl 
 * @Description: 查找用户有效的优惠地址
 * @author maojj
 * @date 2017年1月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.0 			2017年1月4日				maojj
 */
@Service("findUserAddrService")
public class FindUserAddrServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto> {
	
	@Autowired
	private MemberConsigneeAddressService memberConsigneeAddressService;
	
	@Resource
	private MemberConsigneeAddressMapper memberConsigneeAddressMapper;
	
	@Resource
	private ActivityBusinessRelMapper activityBusinessRelMapper;
	
	@Resource
	private GroupUserAddrFilterStrategy groupUserAddrFilterStrategy;

	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		UserAddressVo userAddrInfo = null;
		switch (paramDto.getSkuType()) {
			case PHYSICAL_ORDER:
				userAddrInfo = findUserAddr(paramDto);
				break;
			case SERVICE_STORE_ORDER:
				userAddrInfo = findUserServAddr(paramDto);
				break;
			case STORE_CONSUME_ORDER:
				userAddrInfo = findStoreServAddr(paramDto.getStoreId());
				break;
			case GROUP_ORDER:
				userAddrInfo = findUserGroupAddr(paramDto);
				break;
			default:
				break;
		}
		
		resp.getData().setUserAddrInfo(userAddrInfo);
	}

	/**
	 * @Description: 遍历店获取用户最优地址
	 * @param paramDto
	 * @return
	 * @throws ServiceException   
	 * @author maojj
	 * @date 2017年1月4日
	 */
	private UserAddressVo findUserAddr(PlaceOrderParamDto paramDto) throws ServiceException{
		List<MemberConsigneeAddressVo> userAddrList = memberConsigneeAddressService.findAppUserList(paramDto.getUserId(), 
		        paramDto.getStoreId(), String.valueOf(paramDto.getChannel().ordinal()));
		UserAddressVo userAddressVo = null;
		if (CollectionUtils.isNotEmpty(userAddrList)) {
			for (MemberConsigneeAddressVo memberConsigneeAddressVo : userAddrList) {
				if ("0".equals(memberConsigneeAddressVo.getBeyondType())) {
					userAddressVo = new UserAddressVo();
					userAddressVo.setAddressId(memberConsigneeAddressVo.getId());
					userAddressVo.setAddress(memberConsigneeAddressVo.getAddress());
					userAddressVo.setIsOutRange(Integer.parseInt(memberConsigneeAddressVo.getBeyondType()));
					userAddressVo.setLatitude(memberConsigneeAddressVo.getLatitude());
					userAddressVo.setLongitude(memberConsigneeAddressVo.getLongitude());
					userAddressVo.setMobile(memberConsigneeAddressVo.getMobile());
					userAddressVo.setConsigneeName(memberConsigneeAddressVo.getConsigneeName());
					userAddressVo.setProvinceName(memberConsigneeAddressVo.getProvinceName());
					userAddressVo.setCityName(memberConsigneeAddressVo.getCityName());
					userAddressVo.setAreaExt(memberConsigneeAddressVo.getAreaExt());
					userAddressVo.setAreaName(memberConsigneeAddressVo.getAreaName());
					if (StringUtils.isNotBlank(memberConsigneeAddressVo.getCommunityId())
							&& StringUtils.isNotBlank(memberConsigneeAddressVo.getRoomId())) {
						userAddressVo.setIsCommunity(0);
					}else {
						userAddressVo.setIsCommunity(1);
					}
					break;
				}
			}
		}
		return userAddressVo;
	}
	
	/**
	 * @Description: 上门服务订单获取用户地址
	 * @param paramDto
	 * @return
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年1月6日
	 */
	private UserAddressVo findUserServAddr(PlaceOrderParamDto paramDto) throws Exception {
		UserAddressVo userAddressVo = null;
		switch (paramDto.getOrderType()) {
			case SRV_ORDER:
				userAddressVo = findUserAddrForServ(paramDto);
				break;
			case SECKILL_ORDER:
				userAddressVo = findUserAddrForSeckill(paramDto);
				break;
			default:
				break;
		}
		return userAddressVo;
	}
	
	private UserAddressVo findStoreServAddr(String storeId) throws Exception{
		MemberConsigneeAddress memberConsignee = memberConsigneeAddressService.getSellerDefaultAddress(storeId);
		UserAddressVo userAddressVo = null;
		if (memberConsignee != null) {
			userAddressVo = BeanMapper.map(memberConsignee, UserAddressVo.class);
		}
		return userAddressVo;
	}
	
	/**
	 * @Description: 查询用户秒杀的有效地址
	 * @param paramDto   
	 * @author maojj
	 * @date 2017年1月7日
	 */
	private UserAddressVo findUserAddrForSeckill(PlaceOrderParamDto paramDto){
		UserAddressVo userAddr = null;
		ActivitySeckill seckillInfo = (ActivitySeckill)paramDto.get("seckillInfo");
		StoreInfo storeInfo = (StoreInfo)paramDto.get("storeInfo");
		// 区域类型：0全国，1区域
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("userId", paramDto.getUserId());
		condition.put("storeId", paramDto.getStoreId());
		condition.put("activitySeckillId", seckillInfo.getId());
		condition.put("seckillRangeType", seckillInfo.getSeckillRangeType().ordinal());
		condition.put("storeAreaType", storeInfo.getAreaType());
		condition.put("clientType", paramDto.getChannel().ordinal());
		List<UserAddressVo> userAddrList = memberConsigneeAddressService.findUserAddr(condition);
		if(!CollectionUtils.isEmpty(userAddrList)){
			for(UserAddressVo addr : userAddrList){
				if(addr.getIsOutRange() == 0){
					userAddr = addr;
					break;
				}
			}
		}
		return userAddr;
	}
	
	/**
	 * @Description: 服务店普通商品下单
	 * @param paramDto
	 * @return   
	 * @author maojj
	 * @date 2017年1月7日
	 */
	private UserAddressVo findUserAddrForServ(PlaceOrderParamDto paramDto){
		// 构建查询参数
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", paramDto.getUserId());
		params.put("storeId", paramDto.getStoreId());
		params.put("clientType", paramDto.getChannel().ordinal());
		// 查询用户的所有收货地址
		Map<String, Object> map = memberConsigneeAddressService.findUserDefaultAddress(params);
		//当地址为空时 为null肯定不包含 或 不包含时 
		if (map == null || !map.containsKey("provinceName")) {
			return null;
		} 
		UserAddressVo userAddressVo = BeanMapper.map(map, UserAddressVo.class);
		return userAddressVo;
	}
	
	// Begin V2.6.3 added by maojj 2017-10-11
	/**
	 * @Description: 查询用户地址信息
	 * @param paramDto
	 * @return   
	 * @author maojj
	 * @date 2017年10月11日
	 */
	private UserAddressVo findUserGroupAddr(PlaceOrderParamDto paramDto) {
		UserAddressFilterCondition filterCondition = new UserAddressFilterCondition();
		ActivityDiscount actInfo = (ActivityDiscount) paramDto.get("activityGroup");
		filterCondition.setActivityInfo(actInfo);
		if (actInfo.getLimitRange() != AreaType.national) {
			// 如果团购活动限制了范围，需要查询范围关系
			List<ActivityBusinessRel> relList = activityBusinessRelMapper.findByActivityId(actInfo.getId());
			Map<ActivityBusinessType, List<String>> areaLimitCondition = Maps.newHashMap();
			relList.forEach(rel -> {
				if (areaLimitCondition.containsKey(rel.getBusinessType())) {
					areaLimitCondition.get(rel.getBusinessType()).add(rel.getBusinessId());
				} else {
					areaLimitCondition.put(rel.getBusinessType(), Arrays.asList(new String[] { rel.getBusinessId() }));
				}
			});
			filterCondition.setAreaLimitCondition(areaLimitCondition);
		}
		List<UserAddressVo> userAddrList = memberConsigneeAddressService.findUserAddrList(paramDto.getUserId(),
				filterCondition, groupUserAddrFilterStrategy);
		return CollectionUtils.isEmpty(userAddrList) ? null : userAddrList.get(0);
	}
	// End V2.6.3 added by maojj 2017-10-11
}


