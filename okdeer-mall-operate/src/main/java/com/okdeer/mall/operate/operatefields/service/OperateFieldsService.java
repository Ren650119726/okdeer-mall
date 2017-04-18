package com.okdeer.mall.operate.operatefields.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.operate.dto.OperateFieldsQueryParamDto;
import com.okdeer.mall.operate.operatefields.bo.OperateFieldsBo;
import com.okdeer.mall.operate.operatefields.entity.OperateFields;
import com.okdeer.mall.operate.operatefields.entity.OperateFieldsContent;

/**
 * ClassName: OperateFieldsService 
 * @Description: 运营栏位service
 * @author zengjizu
 * @date 2017年4月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface OperateFieldsService extends IBaseService {
		
	/**
	 * @Description: 查询运营栏位列表
	 * @return
	 * @author zengjizu
	 * @date 2017年4月13日
	 */
	List<OperateFields> findList(OperateFieldsQueryParamDto queryParamDto);
	
	/**
	 * @Description: 查询运营栏位列表带内容列表
	 * @param queryParamDto 查询参数
	 * @return
	 * @author zengjizu
	 * @date 2017年4月13日
	 */
	List<OperateFieldsBo> findListWithContent(OperateFieldsQueryParamDto queryParamDto);
	
	/**
	 * @Description: 保存运营栏位
	 * @param operateFields
	 * @param operateFieldscontentList
	 * @author zengjizu
	 * @date 2017年4月13日
	 */
	void save(OperateFields operateFields,List<OperateFieldsContent> operateFieldscontentList);
	/**
	 * @Description: 修改运营栏位
	 * @param operateFields
	 * @param operateFieldscontentList
	 * @author zengjizu
	 * @date 2017年4月13日
	 */
	void update(OperateFields operateFields,List<OperateFieldsContent> operateFieldscontentList);
	/**
	 * @Description: 更新排序值
	 * @param id id逐渐
	 * @param isUp 是否上移 true:是 false：否
	 * @author zengjizu
	 * @date 2017年4月13日
	 */
    void updateSort(String id,boolean isUp) throws Exception;
	
	/**
	 * 根据店铺Id和店铺商品Id查找关联的运营栏位
	 * @param storeId 店铺Id
	 * @param storeSkuId 店铺商品Id
	 * @return 栏位列表
	 * @author zhaoqc
	 * @date 2017-4-18
	 */
	List<OperateFields> getGoodsRalationFields(String storeId, String storeSkuId);

}
