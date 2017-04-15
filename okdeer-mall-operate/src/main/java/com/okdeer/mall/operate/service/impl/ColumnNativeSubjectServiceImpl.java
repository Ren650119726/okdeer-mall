/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2016年12月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.dto.ColumnNativeSubjectParamDto;
import com.okdeer.mall.operate.entity.ColumnNativeSubject;
import com.okdeer.mall.operate.entity.ColumnNativeSubjectGoods;
import com.okdeer.mall.operate.mapper.ColumnNativeSubjectGoodsMapper;
import com.okdeer.mall.operate.mapper.ColumnNativeSubjectMapper;
import com.okdeer.mall.operate.service.ColumnNativeSubjectService;

/**
 * ClassName: HomeIconApiImpl 
 * @Description: 原生专题服务接口实现
 * @author tangzj02
 * @date 2017-04-13
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	   友门鹿2.0        2017-04-13        zhangkn                     添加
 */
@Service
public class ColumnNativeSubjectServiceImpl extends BaseServiceImpl implements ColumnNativeSubjectService {

	/** 日志记录 */
	private static final Logger log = LoggerFactory.getLogger(ColumnNativeSubjectServiceImpl.class);

	@Autowired
	private ColumnNativeSubjectMapper columnNativeSubjectMapper;

	@Autowired
	private ColumnNativeSubjectGoodsMapper columnNativeSubjectGoodsMapper;

	@Override
	public IBaseMapper getBaseMapper() {
		return columnNativeSubjectMapper;
	}

	@Override
	public List<ColumnNativeSubject> findList(ColumnNativeSubjectParamDto param) throws Exception {
		return columnNativeSubjectMapper.findList(param);
	}

	@Override
	public PageUtils<ColumnNativeSubject> findListPage(ColumnNativeSubjectParamDto paramDto,Integer pageNumber,Integer pageSize) throws Exception {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ColumnNativeSubject> result = columnNativeSubjectMapper.findList(paramDto);
		if (result == null) {
			result = new ArrayList<ColumnNativeSubject>();
		}
		return new PageUtils<ColumnNativeSubject>(result).toBean(ColumnNativeSubject.class);
	}

	@Override
	public ColumnNativeSubject findById(String id) throws Exception {
		return columnNativeSubjectMapper.findById(id);
	}

	@Override
	public void deleteById(String id) throws Exception {
		columnNativeSubjectMapper.delete(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void add(ColumnNativeSubject obj,List<ColumnNativeSubjectGoods> goodsList) throws Exception {
		//批量添加关联数据
		columnNativeSubjectGoodsMapper.addBatch(goodsList);
		//添加主表信息
		columnNativeSubjectMapper.add(obj);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(ColumnNativeSubject obj,List<ColumnNativeSubjectGoods> goodsList) throws Exception {
		//删除老数据
		columnNativeSubjectGoodsMapper.deleteByColumnNativeSubjectId(obj.getId());
		//批量添加关联数据
		columnNativeSubjectGoodsMapper.addBatch(goodsList);
		//修改主表信息
		columnNativeSubjectMapper.update(obj);
	}
}
