/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnAppRecommendGoodsMapper.java
 * @Date 2016-12-30 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.dto.AppRecommendGoodsParamDto;
import com.okdeer.mall.operate.dto.ServerGoodsChoolseDto;
import com.okdeer.mall.operate.entity.ColumnAppRecommendGoods;

public interface ColumnAppRecommendGoodsMapper extends IBaseMapper {

	/**
	 * @Description: 查询与APP端服务商品推荐的关联数据
	 * @param paramDto 查询参数
	 * @return List<ColumnAppRecommendGoods>  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月30日
	 */
	List<ColumnAppRecommendGoods> findList(AppRecommendGoodsParamDto paramDto);

	/**
	 * @Description: 根据推荐ID查询A与APP端服务发票推荐的关联数据
	 * @param recommendId 服务商品推荐ID
	 * @return List<ColumnAppRecommendGoods>  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月30日
	 */
	List<ColumnAppRecommendGoods> findListByRecommendId(String recommendId);

	/**
	 * @Description: 根据服务店商品ID查询需要需要展示商品关联信息
	 * @param storeSkuIds 服务店商品ID集合
	 * @throws Exception   
	 * @return List<AppRecommendGoodsDto>  
	 * @author tangzj02
	 * @date 2017年1月8日
	 */
	List<ColumnAppRecommendGoods> findShowListByStoreSkuIds(@Param("storeSkuIds") List<String> storeSkuIds);
	
	/**
	 * @Description: 根据推荐ID删除数据
	 * @param id  服务商品推荐ID  
	 * @return int 成功删除数  
	 * @author tangzj02
	 * @date 2016年12月30日
	 */
	int deleteByRecommendId(String recommendId);

	/**
	 * @Description: 批量插入数据
	 * @param goodsList   
	 * @return int 成功插入记录数  
	 * @author tangzj02
	 * @date 2016年12月30日
	 */
	int insertMore(@Param("list") List<ColumnAppRecommendGoods> list);
	
	/**
	 * @Description: 
	 * @param serverGoodsChoolseDto serverGoodsChoolseDto
	 * @return serverGoodsChoolseDto集合
	 * @author zhulq
	 * @date 2017年1月11日
	 */
	List<ServerGoodsChoolseDto> findServerGoodsList(ServerGoodsChoolseDto serverGoodsChoolseDto);
}