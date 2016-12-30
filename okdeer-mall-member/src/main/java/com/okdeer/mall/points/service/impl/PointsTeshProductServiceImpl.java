
package com.okdeer.mall.points.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.points.entity.PointsTeshProduct;
import com.okdeer.mall.points.entity.PointsTeshProductQuery;
import com.okdeer.mall.points.mapper.PointsTeshProductMapper;
import com.okdeer.mall.points.service.PointsTeshProductService;

/**
 * ClassName: PointsTeshProductServiceImpl 
 * @Description: 积分商品service业务处理
 * @author zengjizu
 * @date 2016年12月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class PointsTeshProductServiceImpl extends BaseServiceImpl implements PointsTeshProductService {

	@Autowired
	private PointsTeshProductMapper pointsTeshProductMapper;

	@Override
	public IBaseMapper getBaseMapper() {

		return pointsTeshProductMapper;
	}

	@Override
	public PageUtils<PointsTeshProduct> findList(PointsTeshProductQuery pointsTeshProductQuery, Integer pageNum,
			Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize, true);
		List<PointsTeshProduct> list = pointsTeshProductMapper.findList(pointsTeshProductQuery);
		PageUtils<PointsTeshProduct> pageUtils = new PageUtils<PointsTeshProduct>(list);
		return pageUtils;
	}

	@Override
	public List<PointsTeshProduct> findList(PointsTeshProductQuery pointsTeshProductQuery) {
		List<PointsTeshProduct> list = pointsTeshProductMapper.findList(pointsTeshProductQuery);
		return list;
	}

}
