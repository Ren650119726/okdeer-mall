/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * OperateFieldsMapper.java
 * @Date 2017-04-13 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.operatefields.mapper;


import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.dto.OperateFieldsQueryParamDto;
import com.okdeer.mall.operate.enums.OperateFieldsType;
import com.okdeer.mall.operate.operatefields.bo.OperateFieldsBo;
import com.okdeer.mall.operate.operatefields.entity.OperateFields;

/**
 * ClassName: OperateFieldsMapper 
 * @Description: TODO
 * @author zengjizu
 * @date 2017年4月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface OperateFieldsMapper extends IBaseMapper {
	
	/**
	 * @Description: 获取列表
	 * @param queryParamDto 查询参数
	 * @return 运营栏位列表
	 * @author zengjizu
	 * @date 2017年4月13日
	 */
	List<OperateFields> findList(OperateFieldsQueryParamDto queryParamDto);
	
	/**
	 * @Description: 获取列表
	 * @param queryParamDto 查询参数
	 * @return  运营栏位列表带内容列表
	 * @author zengjizu
	 * @date 2017年4月13日
	 */
	List<OperateFieldsBo> findListWithContent(OperateFieldsQueryParamDto queryParamDto);
	
	/**
	 * @Description: 查询最小排序值
	 * @param type 运营栏位类型
	 * @param businessId 业务id
	 * @return
	 * @author zengjizu
	 * @date 2017年4月13日
	 */
	Integer queryMinSort(OperateFieldsType type,String businessId);
	
	/**
	 * @Description: 查询需要对比的数据
	 * @param id
	 * @param sort
	 * @return
	 * @author zengjizu
	 * @date 2017年4月13日
	 */
	OperateFields findCompareBySort(String id, int sort,int type);
	
}