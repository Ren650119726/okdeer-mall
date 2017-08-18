/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * SysBuyerLocateInfoMapper.java
 * @Date 2017-02-17 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.member.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.member.bo.SysBuyerLocateInfoBo;
import com.okdeer.mall.member.entity.SysBuyerLocateInfo;
import com.okdeer.mall.member.member.dto.LocateInfoQueryDto;

/**
 * 
 * ClassName: SysBuyerLocateInfoMapper 
 * @Description: 用户定位信息mapper
 * @author chenzc
 * @date 2017年2月17日
 */
public interface SysBuyerLocateInfoMapper extends IBaseMapper {

	/**
	 * 
	 * @Description: 根据用户id查询用户的定位信息
	 * @return SysBuyerLocateInfo  
	 * @throws
	 * @author chenzc
	 * @date 2017年2月17日
	 */
	SysBuyerLocateInfo findByUserId(@Param("userId")String userId);

	/**
	 * @Description: 查询用户下单列表
	 * @param dto
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月18日
	 */
	List<SysBuyerLocateInfoBo> findUserList(LocateInfoQueryDto dto);
	
}