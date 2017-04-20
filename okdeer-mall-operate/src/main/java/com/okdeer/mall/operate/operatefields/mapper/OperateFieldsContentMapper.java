/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * OperateFieldsContentMapper.java
 * @Date 2017-04-13 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.operatefields.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.dto.FieldGoodsQueryDto;
import com.okdeer.mall.operate.dto.OperateFieldContentDto;
import com.okdeer.mall.operate.dto.StoreActivitGoodsQueryDto;
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
	
    /**
     * 查找店铺活动关联的商品运营位内容
     * @param queryDto
     * @return
     * @author zhaoqc
     * @date 2017-4-19
     */
	List<OperateFieldContentDto> getGoodsOfStoreActivityFields(StoreActivitGoodsQueryDto queryDto);
	
    /**
     * 根据店铺Id和skuId查找运营栏位关联的商品信息
     * @param goodsId 商品Id
     * @param storeId 店铺Id
     * @return
     * @author zhaoqc
     * @date 2017-4-19
     */
	OperateFieldContentDto getSingleGoodsOfOperateField(@Param("goodsId") String goodsId, @Param("storeId") String storeId);

    /**
     * 根据商品的三级分类查询商品
     * @param queryDto 查询DTO
     * @return
     * @throws Exception
     */
	List<OperateFieldContentDto> getGoodsOfCategoryField(FieldGoodsQueryDto queryDto);
	
    /**
     * 根据店铺菜单->菜单为标签查找所属的商品
     * @param queryDto
     * @return
     * @throws Exception
     */
	List<OperateFieldContentDto> getGoodsOfStoreLabelField(FieldGoodsQueryDto queryDto);
}