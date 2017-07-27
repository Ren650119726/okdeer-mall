/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * RiskUserManagerMapper.java
 * @Date 2016-11-11 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.risk.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.risk.dto.RiskUserManagerDto;
import com.okdeer.mall.risk.entity.RiskUserManager;
/**
 * 
 * ClassName: RiskUserManagerMapper 
 * @Description: 风控人员管理dao接口类
 * @author xuzq01
 * @date 2016年11月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2		 2016年11月21日		xuzq01				风控人员管理dao接口类
 */
public interface RiskUserManagerMapper extends IBaseMapper {


	/**
	 * @Description: 查找列表
	 * @param userManagerDto
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月15日
	 */
	List<RiskUserManager> findUserList(RiskUserManagerDto userManagerDto);

	/**
	 * @Description: 批量逻辑删除人员
	 * @param ids
	 * @param updateUserId
	 * @param updateTime   
	 * @author xuzq01
	 * @date 2016年11月15日
	 */
	void deleteBatchByIds(@Param("ids")List<String> ids,@Param("updateUserId") String updateUserId,@Param("updateTime") Date updateTime);

	/**
	 * @param riskUserManager
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月29日
	 */
	int findCountByTelephoneOrEmail(RiskUserManager riskUserManager);

}