/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2016年12月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.service.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.entity.ColumnNativeSubjectGoods;
import com.okdeer.mall.operate.mapper.ColumnNativeSubjectGoodsMapper;
import com.okdeer.mall.operate.mapper.ColumnNativeSubjectMapper;
import com.okdeer.mall.operate.service.ColumnNativeSubjectGoodsService;

/**
 * ClassName: ColumnNativeSubjectGoodsServiceImpl
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
public class ColumnNativeSubjectGoodsServiceImpl extends BaseServiceImpl implements ColumnNativeSubjectGoodsService {

	/** 日志记录 */
	private static final Logger log = LoggerFactory.getLogger(ColumnNativeSubjectGoodsServiceImpl.class);

	@Autowired
	private ColumnNativeSubjectMapper columnNativeSubjectMapper;

	@Autowired
	private ColumnNativeSubjectGoodsMapper columnNativeSubjectGoodsMapper;

	@Override
	public IBaseMapper getBaseMapper() {
		return columnNativeSubjectGoodsMapper;
	}

	@Override
	public List<ColumnNativeSubjectGoods> findByColumnNativeSubjectId(String columnNativeSubjectId) throws Exception{
		return columnNativeSubjectGoodsMapper.findByColumnNativeSubjectId(columnNativeSubjectId);
	}
	

}
