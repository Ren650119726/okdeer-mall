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
import com.okdeer.mall.activity.entity.HomeIconGoods;
import com.okdeer.mall.activity.mapper.HomeIconGoodsMapper;
import com.okdeer.mall.activity.service.HomeIconGoodsService;

/**
 * ClassName: HomeIconGoodsServiceImpl 
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
public class HomeIconGoodsServiceImpl extends BaseServiceImpl implements HomeIconGoodsService {

	@Autowired
	private HomeIconGoodsMapper homeIconGoodsMapper;

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
	 * @see com.okdeer.mall.operate.service.HomeIconGoodsService#findListByHomeIcon(java.lang.String)
	 */
	@Override
	public List<HomeIconGoods> findListByHomeIcon(String iconId) throws Exception {
		return homeIconGoodsMapper.findListByHomeIcon(iconId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.HomeIconGoodsService#deleteByHomeIconId(java.lang.String)
	 */
	@Override
	public int deleteByHomeIconId(String iconId) throws Exception {
		return homeIconGoodsMapper.deleteByHomeIconId(iconId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.HomeIconGoodsService#insertMore(java.util.List)
	 */
	@Override
	public int insertMore(List<HomeIconGoods> list) throws Exception {
		return homeIconGoodsMapper.insertMore(list);
	}

}
