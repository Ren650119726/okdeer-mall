/** 
 *@Project: okdeer-mall-activity 
 *@Author: tangzj02
 *@Date: 2016年12月30日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.dto.AppRecommendGoodsParamDto;
import com.okdeer.mall.activity.entity.ActivityAppRecommendGoods;
import com.okdeer.mall.activity.mapper.ActivityAppRecommendGoodsMapper;
import com.okdeer.mall.activity.service.ActivityAppRecommendGoodsService;

/**
 * ClassName: ActivityAppRecommendGoodsServiceImpl 
 * @Description: APP端服务商品推荐与商品关联服务
 * @author tangzj02
 * @date 2016年12月30日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-12-30       tangzj02                           添加
 */

@Service
public class ActivityAppRecommendGoodsServiceImpl extends BaseServiceImpl implements ActivityAppRecommendGoodsService {

	@Autowired
	private ActivityAppRecommendGoodsMapper appRecommendGoodsMapper;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return appRecommendGoodsMapper;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityAppRecommendGoodsService#findListByRecommendId(java.lang.String)
	 */
	@Override
	public List<ActivityAppRecommendGoods> findListByRecommendId(String recommendId) {
		return appRecommendGoodsMapper.findListByRecommendId(recommendId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityAppRecommendGoodsService#findList(com.okdeer.mall.activity.dto.AppRecommendGoodsParamDto)
	 */
	@Override
	public List<ActivityAppRecommendGoods> findList(AppRecommendGoodsParamDto paramDto) throws Exception {
		return appRecommendGoodsMapper.findList(paramDto);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityAppRecommendGoodsService#deleteByRecommendId(java.lang.String)
	 */
	@Override
	public int deleteByRecommendId(String recommendId) throws Exception {
		if (StringUtils.isBlank(recommendId)) {
			return 0;
		}
		return appRecommendGoodsMapper.deleteByRecommendId(recommendId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityAppRecommendGoodsService#insertMore(java.util.List)
	 */
	@Override
	public int insertMore(List<ActivityAppRecommendGoods> goodsList) throws Exception {
		return appRecommendGoodsMapper.insertMore(goodsList);
	}

}
