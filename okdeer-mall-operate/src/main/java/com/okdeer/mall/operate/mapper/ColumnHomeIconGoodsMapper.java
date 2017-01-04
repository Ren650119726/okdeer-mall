/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnHomeIconGoodsMapper.java
 * @Date 2016-12-30 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.entity.ColumnHomeIconGoods;

public interface ColumnHomeIconGoodsMapper extends IBaseMapper {

	/**
	 * @Description: 根据首页ICONID查询数据
	 * @param homeIconId   
	 * @return List<ColumnHomeIconGoods>  
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	List<ColumnHomeIconGoods> findListByHomeIconId(String homeIconId);

	/**
	 * @Description: 根据首页ICONID删除数据
	 * @param homeIconId   
	 * @return int 删除记录  
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	int deleteByHomeIconId(String homeIconId);

	/**
	 * @Description: 批量插入数据
	 * @param list   
	 * @return int 成功删除记录数  
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	int insertMore(@Param("list") List<ColumnHomeIconGoods> list);
}