/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * HomeIconAreaMapper.java
 * @Date 2016-12-29 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.entity.HomeIconArea;

public interface HomeIconAreaMapper extends IBaseMapper {

	/**
	 * @Description: 根据首页ICONID查询数据
	 * @return List<HomeIconArea>  
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	List<HomeIconArea> findListByHomeIcon(String iconId);

	/**
	 * @Description: 根据首页ICONID删除数据
	 * @param iconId   
	 * @return int 删除记录  
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	int deleteByHomeIconId(String iconId);

	/**
	 * @Description: 批量插入数据
	 * @param list   
	 * @return void  
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	int insertMore(@Param("list") List<HomeIconArea> list);
}