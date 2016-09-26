package com.okdeer.mall.operate.crossBorderElectricity.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.okdeer.mall.operate.entity.OperateCrossBorderElectricity;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.dal.IBaseCrudMapper;

/**
 * @DESC: 跨境电商设置mapper
 * @author wusw
 * @date  2016-01-06 11:08:31
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Repository
public interface OperateCrossBorderElectricityMapper extends IBaseCrudMapper {
	
	/**
	 * 
	 * 根据条件查询跨境电商设置列表信息
	 *
	 * @param crossBorderElectricity 跨境电商设置实体（查询条件）
	 * @return 列表信息
	 */
	List<OperateCrossBorderElectricity> selectByParamsEntity(OperateCrossBorderElectricity crossBorderElectricity);
	
	/**
	 * 
	 * 批量逻辑删除跨境电商设置信息 
	 *
	 * @param ids 主键id集合
	 */
	void deleteByIds(@Param("ids")List<String> ids,@Param("disabled") Disabled disabled,
            @Param("updateTime")Date updateTime,@Param("updateUserId")String updateUserId);
	
	/**
	 * 
	 * 统计指定id集合的启用（或停用）状态的记录数量
	 *
	 * @param ids
	 * @param disabled
	 * @return
	 */
	int selectCountById(@Param("ids")List<String> ids,@Param("disabled") Disabled disabled);
}