/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: MemberService.java 
 * @Date: 2015年11月26日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 

package com.okdeer.mall.member.service;

import java.util.Map;

import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.archive.system.entity.SysBuyerUserPointsExt;
import com.okdeer.archive.system.entity.SysMemberExtVo;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;

/**
 * 会员管理service
 * @project yschome-mall
 * @author zhongy
 * @date 2015年11月26日 上午9:58:57
 */
public interface MemberService {

	/**
	 * 根据条件参数查询会员列表
	 * @param params 查询条件参数
	 * @return 返回分页结果集
	 * @throwsServiceException
	 */
	PageUtils<SysBuyerUser> selectByParams(Map<String,Object> params) throws ServiceException;
	
	
	/**
	 * 根据条件参数查询会员列表
	 * @param params 查询条件参数
	 * @return 返回分页结果集
	 * @throwsServiceException
	 */
	PageUtils<SysMemberExtVo> findMemberByParams(Map<String,Object> params) throws ServiceException;
	
	
	/**
	 * 根据主键查询会员
	 * @param id 主键id
	 * @return 返回会员实体
	 * @throwsServiceException
	 */
	SysBuyerUser selectByPrimaryKey(String id) throws ServiceException;
	
	/**
	 * 会员积分扩展表
	 * @param map 请求参数
	 * @return 返回查询结果
	 */
	PageUtils<SysBuyerUserPointsExt> pointsExtSelectByParams(Map<String,Object> map,
			  Integer pageNumber,Integer pageSize) throws ServiceException;
	
	/**
	 * 会员积分扩展表
	 * @param id 请求参数
	 * @return 返回查询结果
	 */
	SysBuyerUserPointsExt pointsExtSelectById(String id) throws ServiceException;
}
