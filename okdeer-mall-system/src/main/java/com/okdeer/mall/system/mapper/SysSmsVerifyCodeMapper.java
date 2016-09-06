package com.okdeer.mall.system.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.archive.system.entity.SysSmsVerifyCode;
import com.yschome.base.dal.IBaseCrudMapper;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-03-14 11:41:04
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface SysSmsVerifyCodeMapper extends IBaseCrudMapper {
	
	/**
	 * DESC: 查询最新的一条记录
	 * @author LIU.W
	 * @param params
	 * @return
	 */
	public SysSmsVerifyCode selectLatestByParams(@Param("params")Map<String,Object> params);

	/**
	 * 更新验证码信息
	 * @desc TODO Add a description 
	 *
	 */
	public void updateByPrimaryKeySelective(SysSmsVerifyCode sysSmsVerifyCode);
}