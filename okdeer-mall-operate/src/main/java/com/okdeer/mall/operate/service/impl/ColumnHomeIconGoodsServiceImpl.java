/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2016年12月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.entity.ColumnHomeIconGoods;
import com.okdeer.mall.operate.mapper.ColumnHomeIconGoodsMapper;
import com.okdeer.mall.operate.service.ColumnHomeIconGoodsService;

/**
 * ClassName: ColumnHomeIconGoodsServiceImpl 
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
public class ColumnHomeIconGoodsServiceImpl extends BaseServiceImpl implements ColumnHomeIconGoodsService {

	@Autowired
	private ColumnHomeIconGoodsMapper homeIconGoodsMapper;

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
	 * @see com.okdeer.mall.operate.service.ColumnHomeIconGoodsService#findListByHomeIconId(java.lang.String)
	 */
	@Override
	public List<ColumnHomeIconGoods> findListByHomeIconId(String iconId) throws Exception {
		return homeIconGoodsMapper.findListByHomeIconId(iconId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnHomeIconGoodsService#findListByHomeIconIds(java.util.List)
	 */
	@Override
	public List<ColumnHomeIconGoods> findListByHomeIconIds(List<String> homeIconIds) throws Exception {
		return homeIconGoodsMapper.findListByHomeIconIds(homeIconIds);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnHomeIconGoodsService#deleteByHomeIconId(java.lang.String)
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int deleteByHomeIconId(String iconId) throws Exception {
		return homeIconGoodsMapper.deleteByHomeIconId(iconId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnHomeIconGoodsService#insertMore(java.util.List)
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int insertMore(List<ColumnHomeIconGoods> list) throws Exception {
		return homeIconGoodsMapper.insertMore(list);
	}
}
