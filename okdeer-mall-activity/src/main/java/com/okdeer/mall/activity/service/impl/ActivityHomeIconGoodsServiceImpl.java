/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2016年12月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.entity.ActivityHomeIconGoods;
import com.okdeer.mall.activity.mapper.ActivityHomeIconGoodsMapper;
import com.okdeer.mall.activity.service.ActivityHomeIconGoodsService;

/**
 * ClassName: ActivityHomeIconGoodsServiceImpl 
 * @Description: 首页ICON与商品关联服务
 * @author tangzj02
 * @date 2016年12月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0        2016-12-28        tangzj02                     添加
 */

@Service
public class ActivityHomeIconGoodsServiceImpl extends BaseServiceImpl implements ActivityHomeIconGoodsService {

	@Autowired
	private ActivityHomeIconGoodsMapper homeIconGoodsMapper;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return homeIconGoodsMapper;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ActivityHomeIconGoodsService#findListByHomeIconId(java.lang.String)
	 */
	@Override
	public List<ActivityHomeIconGoods> findListByHomeIconId(String iconId) throws Exception {
		return homeIconGoodsMapper.findListByHomeIconId(iconId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ActivityHomeIconGoodsService#deleteByHomeIconId(java.lang.String)
	 */
	@Override
	public int deleteByHomeIconId(String iconId) throws Exception {
		return homeIconGoodsMapper.deleteByHomeIconId(iconId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ActivityHomeIconGoodsService#insertMore(java.util.List)
	 */
	@Override
	public int insertMore(List<ActivityHomeIconGoods> list) throws Exception {
		return homeIconGoodsMapper.insertMore(list);
	}

}
