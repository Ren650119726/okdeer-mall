/** 
 *@Project: okdeer-mall-activity 
 *@Author: tangzj02
 *@Date: 2016年12月29日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.utils.BaseResult;
import com.okdeer.mall.activity.dto.AppServiceGoodsRecommendDto;
import com.okdeer.mall.activity.dto.AppServiceGoodsRecommendParamDto;
import com.okdeer.mall.activity.entity.AppServiceGoodsRecommend;
import com.okdeer.mall.activity.enums.AppRecommendStatus;
import com.okdeer.mall.activity.service.AppServiceGoodsRecommendApi;
import com.okdeer.mall.activity.service.AppServiceGoodsRecommendService;

/**
 * ClassName: AppServiceGoodsRecommendApiImpl 
 * @Description: APP端服务商品推荐服务
 * @author tangzj02
 * @date 2016年12月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-12-29        tangzj02                     添加
 */

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.service.AppServiceGoodsRecommendApi")
public class AppServiceGoodsRecommendApiImpl implements AppServiceGoodsRecommendApi {

	/** 日志记录 */
	private static final Logger log = LoggerFactory.getLogger(AppServiceGoodsRecommendApiImpl.class);

	@Autowired
	private AppServiceGoodsRecommendService appServiceGoodsRecommendService;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.AppServiceGoodsRecommendApi#findList(com.okdeer.mall.activity.dto.AppServiceGoodsRecommendParamDto)
	 */
	@Override
	public List<AppServiceGoodsRecommendDto> findList(AppServiceGoodsRecommendParamDto paramDto) throws Exception {
		log.info("查询APP端服务商品推荐列表参数:{}", paramDto);
		List<AppServiceGoodsRecommend> sourceList = appServiceGoodsRecommendService.findList(paramDto);
		List<AppServiceGoodsRecommendDto> dtoList = null;
		if (null == sourceList) {
			dtoList = new ArrayList<AppServiceGoodsRecommendDto>();
		} else {
			dtoList = BeanMapper.mapList(sourceList, AppServiceGoodsRecommendDto.class);
		}
		return dtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.AppServiceGoodsRecommendApi#findById(java.lang.String)
	 */
	@Override
	public AppServiceGoodsRecommendDto findById(String id) throws Exception {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		AppServiceGoodsRecommend source = appServiceGoodsRecommendService.findById(id);
		if (null == source) {
			return null;
		}
		AppServiceGoodsRecommendDto dto = BeanMapper.map(source, AppServiceGoodsRecommendDto.class);
		return dto;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.AppServiceGoodsRecommendApi#save(com.okdeer.mall.activity.dto.AppServiceGoodsRecommendDto)
	 */
	@Override
	public BaseResult save(AppServiceGoodsRecommendDto dto) throws Exception {
		if (dto == null) {
			return new BaseResult("HomeIconDto信息不能为空");
		}
		// TODO 代完善
		return new BaseResult();
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.AppServiceGoodsRecommendApi#closeById(java.lang.String)
	 */
	@Override
	public int closeById(String id) throws Exception {
		if (StringUtils.isBlank(id)) {
			return 0;
		}
		AppServiceGoodsRecommend entity = new AppServiceGoodsRecommend();
		entity.setId(id);
		entity.setStatus(AppRecommendStatus.close.getCode());
		return appServiceGoodsRecommendService.update(entity);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.AppServiceGoodsRecommendApi#updateById(java.lang.String, java.lang.Integer)
	 */
	@Override
	public int updateById(String id, Integer sort) throws Exception {
		if (StringUtils.isBlank(id) || null == sort) {
			return 0;
		}
		AppServiceGoodsRecommend entity = new AppServiceGoodsRecommend();
		entity.setId(id);
		entity.setSort(sort);
		return appServiceGoodsRecommendService.update(entity);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.AppServiceGoodsRecommendApi#deleteByIds(java.util.List)
	 */
	@Override
	public int deleteByIds(List<String> ids) throws Exception {
		if (null == ids || ids.size() == 0) {
			return 0;
		}
		return appServiceGoodsRecommendService.deleteByIds(ids);
	}

}
