package com.okdeer.mall.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.archive.system.entity.SysBuyerUserPointsExt;
import com.okdeer.archive.system.entity.SysMemberExtVo;
import com.okdeer.base.dal.IBaseCrudMapper;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-03-17 17:05:52
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface SysBuyerUserMapper extends IBaseCrudMapper {
	
	/**
	 * 会员积分扩展表
	 *
	 * @param map 请求参数
	 * @return 返回查询结果
	 */
	List<SysBuyerUserPointsExt> pointsExtSelectByParams(@Param("params")Map<String,Object> map);
	
	/**
	 * zhongy
	 * 会员列表查询 
	 *
	 * @param map 请求参数
	 * @return 返回查询结果
	 */
	List<SysMemberExtVo> selectMemberByParams(@Param("params")Map<String,Object> map);
	
	/**
	 * 查找用户电话号码
	 */
	String selectMemberMobile(String id);

	/**
	 * 会员积分扩展表
	 *
	 * @param map 请求参数
	 * @return 返回查询结果
	 */
	List<SysBuyerUserPointsExt> pointsExtSelectByParamsNew(@Param("params")Map<String,Object> map);
	
	/**
	 * 
	 * @Description: 根据用户ID集合查询积分信息
	 * @param userIds 用户ID集合
	 * @return   积分信息VO
	 * @author zengj
	 * @date 2016年8月18日
	 */
	List<SysMemberExtVo> findExtByUserIds(@Param("userIds") List<String> userIds);
	
	/**
	 * 
	 * @Description: 根据ID集合获取用户信息
	 * @param ids List<String>
	 * @return list List<SysBuyerUser>
	 * @author wangf01
	 * @date 2016年9月9日
	 */
	List<SysBuyerUser> findByIds(@Param("ids") List<String> ids);
}