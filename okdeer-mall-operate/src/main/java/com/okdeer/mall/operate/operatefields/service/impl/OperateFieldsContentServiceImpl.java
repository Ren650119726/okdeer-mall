package com.okdeer.mall.operate.operatefields.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.operatefields.entity.OperateFieldsContent;
import com.okdeer.mall.operate.operatefields.mapper.OperateFieldsContentMapper;
import com.okdeer.mall.operate.operatefields.service.OperateFieldsContentService;

/**
 * ClassName: OperateFieldsContentServiceImpl 
 * @Description: 运营栏位内service
 * @author zengjizu
 * @date 2017年4月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class OperateFieldsContentServiceImpl extends BaseServiceImpl implements OperateFieldsContentService {

	@Autowired
	private OperateFieldsContentMapper operateFieldsContentMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return operateFieldsContentMapper;
	}

	@Override
	public List<OperateFieldsContent> findByFieldId(String fieldId) {
		return operateFieldsContentMapper.findByFieldId(fieldId);
	}

	@Override
	public void initOperationFieldContext(String storeId) throws ServiceException {
		if(StringUtils.isNotBlank(storeId)){
			operateFieldsContentMapper.initOperationFieldContext(storeId);
		}
	}

}
