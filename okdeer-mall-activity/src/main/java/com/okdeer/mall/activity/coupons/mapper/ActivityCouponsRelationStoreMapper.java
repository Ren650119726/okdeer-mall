package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRelationStore;

/**
 * 代金卷管理店铺
 * @project yschome-mall
 * @author zhulq
 * @date 2016年3月3日 下午3:02:37
 */
public interface ActivityCouponsRelationStoreMapper extends IBaseCrudMapper{
	
	/***
	 * 新增关联关系
	 * @param couponsRelationStoreList  couponsRelationStoreList
	 */
	void insertCouponsRelationStore(List<ActivityCouponsRelationStore> couponsRelationStoreList);
	
	/**
	 * 删除之前的关联关系
	 * @param id 根据代金卷id
	 */
	void deleteCouponsRelationStore(String id);
	
	/**
	 * 根据代金卷id 查询关联关系
	 * @param 代金卷 id 
	 * @return 记录条数
	 */
	int selectCouponsRelationStore(String id);
	
	/**
	 * 
	 * 根据代金券id、省id，查询关联店铺id、所在市id
	 * 
	 * @author wusw
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> selectAddressRelationStoreByParams(Map<String,Object> map);
	
	/**
	 * 
	 * 根据代金券id，查询关联店铺所在省id
	 *
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> selectAddressRelationProvinceByParams(Map<String,Object> map);

	/**
	 * @Description: 根据店铺id、代金券id查询关联关系
	 * @param storeId
	 * @param couponsId
	 * @return   
	 * @author maojj
	 * @date 2017年7月11日
	 */
	ActivityCouponsRelationStore findByStoreIdAndCouponsId(@Param("storeId")String storeId,@Param("couponsId")String couponsId);
}
