/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2017年8月15日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.dto.ColumnHomeIconClassifyDto;
import com.okdeer.mall.operate.entity.ColumnHomeIconClassify;
import com.okdeer.mall.operate.mapper.ColumnHomeIconClassifyMapper;
import com.okdeer.mall.operate.service.ColumnHomeIconClassifyService;


/**
 * ClassName: ColumnHomeIconClassifyServiceImpl 
 * @Description: TODO
 * @author xuzq01
 * @date 2017年8月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class ColumnHomeIconClassifyServiceImpl extends BaseServiceImpl implements ColumnHomeIconClassifyService {

	@Autowired
	private ColumnHomeIconClassifyMapper columnHomeIconClassifyMapper;
	@Override
	public IBaseMapper getBaseMapper() {
		return columnHomeIconClassifyMapper;
	}

	@Override
	public List<ColumnHomeIconClassify> findListByHomeIconId(String homeIconId) {
		return columnHomeIconClassifyMapper.findListByHomeIconId(homeIconId);
	}

	@Override
	public void deleteByHomeIconId(String homeIconId) {
		columnHomeIconClassifyMapper.deleteByHomeIconId(homeIconId);
		
	}

	@Override
	public void addClassifyBatch(String homeIconId, List<ColumnHomeIconClassifyDto> classifyList) {
		for(ColumnHomeIconClassifyDto categoryDto : classifyList){
			ColumnHomeIconClassify classify = BeanMapper.map(categoryDto, ColumnHomeIconClassify.class);
			classify.setId(UuidUtils.getUuid());
			classify.setHomeIconId(homeIconId);
			columnHomeIconClassifyMapper.add(classify);
		}
		
	}

}
