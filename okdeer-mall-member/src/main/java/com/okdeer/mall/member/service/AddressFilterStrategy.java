package com.okdeer.mall.member.service;

import com.okdeer.mall.member.bo.UserAddressFilterCondition;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;

/**
 * ClassName: AddressFilterStrategy 
 * @Description: 地址过滤策略
 * @author maojj
 * @date 2017年10月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.6.3 		2017年10月11日				maojj
 */
public interface AddressFilterStrategy {

	/**
	 * @Description: 是否超出服务范围
	 * @return   
	 * @author maojj
	 * @date 2017年10月11日
	 */
	boolean isOutRange(MemberConsigneeAddress addrInfo,UserAddressFilterCondition filterCondition);
}
