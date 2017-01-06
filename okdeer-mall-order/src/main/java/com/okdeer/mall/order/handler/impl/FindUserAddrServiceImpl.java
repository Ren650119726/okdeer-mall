package com.okdeer.mall.order.handler.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
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

	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		UserAddressVo userAddrInfo = null;
		switch (paramDto.getOrderType()) {
			case CVS_ORDER:
				userAddrInfo = findUserAddr(paramDto);
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
		List<MemberConsigneeAddressVo> userAddrList = memberConsigneeAddressService.findAppUserList(paramDto.getUserId(), paramDto.getStoreId());
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
}
