/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * AppServiceGoodsRecommendMapper.java
 * @Date 2016-12-29 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.dto.AppServiceGoodsRecommendParamDto;
import com.okdeer.mall.activity.entity.AppServiceGoodsRecommend;

public interface AppServiceGoodsRecommendMapper extends IBaseMapper {

	/**
	 * @Description: 根据app端服务商品推荐ID进行删除
	 * @param ids id集合
	 * @return int 成功删除记录数 
	 * @author tangzj02
	 * @date 2016年12月29日
	 */
	int deleteByIds(@Param("ids")List<String> ids);

	/**
	 * @Description: 查询App端服务商品推荐列表
	 * @param paramDto 查询参数
	 * @return List<AppServiceGoodsRecommend>  
	 * @author tangzj02
	 * @date 2016年12月29日
	 */
	List<AppServiceGoodsRecommend> findList(AppServiceGoodsRecommendParamDto paramDto);
}