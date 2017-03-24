/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnAdvertVersionMapper.java
 * @Date 2017-03-14 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.entity.ColumnAdvertVersionDto;

public interface ColumnAdvertVersionMapper extends IBaseMapper {

	/**
	 * @Description: 批量插入广告与APP版本的关联信息
	 * @param list 广告与APP版本关联信息集合
	 * @return long 成功插入记录数  
	 * @author tangzj02
	 * @date 2017年3月14日
	 */
	long insertBatch(@Param("list")List<ColumnAdvertVersionDto> list);

	/**
	 * @Description: 根据广告ID查询与APP版本关联信息列表
	 * @param advertId 广告ID
	 * @return List<ColumnAdvertVersion>  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2017年3月14日
	 */
	List<ColumnAdvertVersionDto> findListByAdvertId(@Param("advertId")String advertId);

	/**
	 * @Description: 根据广告ID删除关联APP版本
	 * @param advertId 广告ID   
	 * @author tangzj02
	 * @date 2017年3月14日
	 */
	void deleteByAdvertId(@Param("advertId")String advertId);

}