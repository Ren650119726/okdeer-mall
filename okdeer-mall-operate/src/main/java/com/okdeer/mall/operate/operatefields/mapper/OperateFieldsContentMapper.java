/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * OperateFieldsContentMapper.java
 * @Date 2017-04-13 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.operatefields.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.operatefields.entity.OperateFieldsContent;

/**
 * ClassName: OperateFieldsContentMapper 
 * @Description: TODO
 * @author zengjizu
 * @date 2017年4月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface OperateFieldsContentMapper extends IBaseMapper {
 
	/**
	 * @Description: 根据fieldId查询列表
	 * @param fieldId 栏位id
	 * @return
	 * @author zengjizu
	 * @date 2017年4月13日
	 */
	List<OperateFieldsContent> findByFieldId(String fieldId);
	
	/**
	 * @Description: 根据运营位id删除
	 * @param fieldId 运营位id
	 * @return
	 * @author zengjizu
	 * @date 2017年4月13日
	 */
	int deleteByFieldId(String fieldId);
}