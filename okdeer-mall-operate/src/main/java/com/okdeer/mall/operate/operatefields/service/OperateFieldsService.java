package com.okdeer.mall.operate.operatefields.service;

import java.util.List;
import java.util.Set;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.operate.dto.FieldGoodsQueryDto;
import com.okdeer.mall.operate.dto.OperateFieldContentDto;
import com.okdeer.mall.operate.dto.OperateFieldDto;
import com.okdeer.mall.operate.dto.OperateFieldsQueryParamDto;
import com.okdeer.mall.operate.dto.StoreActivitGoodsQueryDto;
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

	/**
     * 初始化店铺运营栏位
     * @param storeId
     * @throws Exception
     * @author zhaoqc
     * @date 2017-4-18
     */
	Set<OperateFieldDto> initStoreOperateFieldData(String storeId) throws Exception;
    
    /**
     * 初始化城市运营栏位
     * @param cityId
     * @param storeId
     * @throws Exception
     * @author zhaoqc
     * @date 2017-4-18
     */
	Set<OperateFieldDto> initCityOperateFieldData(String cityId)  throws Exception;
    
    /**
     * 查找店铺活动关联的商品运营位内容
     * @param queryDto
     * @return
     * @author zhaoqc
     * @date 2017-4-19
     */
    List<OperateFieldContentDto> getGoodsOfStoreActivityFields(StoreActivitGoodsQueryDto queryDto) throws Exception;
    
    /**
     * 根据店铺Id和skuId查找运营栏位关联的商品信息
     * @param goodsId 商品Id
     * @param storeId 店铺Id
     * @return
     * @author zhaoqc
     * @date 2017-4-19
     */
    OperateFieldContentDto getSingleGoodsOfOperateField(String goodsId, String storeId) throws Exception;
    
    /**
     * 根据商品的三级分类查询商品
     * @param queryDto 查询DTO
     * @return
     * @throws Exception
     */
    List<OperateFieldContentDto> getGoodsOfCategoryField(FieldGoodsQueryDto queryDto) throws Exception;
 
    /**
     * 根据店铺菜单->菜单为标签查找所属的商品
     * @param queryDto
     * @return
     * @throws Exception
     */
    List<OperateFieldContentDto> getGoodsOfStoreLabelField(FieldGoodsQueryDto queryDto) throws Exception;

	/**
	 * 店铺审核通过，初始化店铺运营栏位
	 * @Description: 
	 * @param storeId 店铺id
	 * @throws Exception
	 * @author mengsj
	 * @date 2017年4月20日
	 */
	void initOperationField(String storeId) throws Exception;

	
	List<OperateFieldContentDto> getGoodsOfStoreNavigateFields(String storeId, String navigateId, 
            int template, int sort, int sortType) throws Exception;
}
