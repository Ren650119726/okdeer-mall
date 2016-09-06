package com.okdeer.mall.activity.group.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.archive.goods.spu.vo.ActivityGroupGoodsVo;
import com.okdeer.mall.activity.group.entity.ActivityGroupGoods;
import com.yschome.base.common.exception.ServiceException;

/**
 * 
 * 
 * @pr mall
 * @desc 团购商品mapper
 * @author chenwj
 * @date 2016年1月6日 下午5:52:42
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
public interface ActivityGroupGoodsMapper {

	/**
	 * 添加团购商品
	 * @param activityGroupGoods ActivityGroupGoods
	 * @throws ServiceException ServiceException
	 */
	void insert(ActivityGroupGoods activityGroupGoods) throws ServiceException;
	
	/**
	 * 根据ID 删除团购商品
	 * @param id String
	 * @throws ServiceException ServiceException
	 */
	void deleteByPrimaryKey(String id) throws ServiceException;
	
	/**
	 * 根据团购活动ID查询
	 * @param groupId String
	 * @return List
	 */
	List<ActivityGroupGoods> getActivityGroupGoods(String groupId);
	
	/**
	 * 根据ID 查询团购商品
	 * @param id String
	 */
	ActivityGroupGoods selectByPrimaryKey(String id) ;
	
	/**
	 * 根据条件查询商品列表
	 * @param activityGroupGoods ActivityGroupGoods
	 * @return List
	 */
	List<ActivityGroupGoods> findActivityGroupGoods(ActivityGroupGoods activityGroupGoods);
	
	/**
	 * 查询所有团购活动商品
	 * @param map Map
	 * @return List
	 */
	List<ActivityGroupGoods> findActivityGroupGoodsByParam(Map<String,Object> map);
	
	/**
	 * 根据用户ID 查找所属店铺商品
	 * @param userId String
	 * @return PageUtils
	 */
	List<ActivityGroupGoods> findStoreGoodsByUserId(String userId);
	
	
	/**
	 * 根据ID查询spu商品信息
	 * @param ids List
	 * @return PageUtils
	 */
	List<ActivityGroupGoodsVo> findSpuBySkuId(@Param("ids") List<String> ids,@Param("storeId") String storeId);
	
	/**
	 * 根据ID查询goodsStoreId商品信息
	 * @param ids List
	 * @return List
	 */
	List<ActivityGroupGoodsVo> findSpuByGoodsStoreId(@Param("ids") List<String> ids,@Param("storeId") String storeId,@Param("online") String online);
	
	
	/**
	 * 根据店铺ID获取商品
	 * @param storeId String
	 * @return List
	 */
	List<ActivityGroupGoodsVo> getActivityGroupGoodsByParam(Map<String,Object> map);
	
	/**
	 * 根据团购ID 删除团购商品
	 * @param id String
	 * @throws ServiceException ServiceException
	 */
	void deleteByGroupId(String groupId) throws ServiceException;
	
	/**
	 * 根据团购ID 删除团购商品
	 * @param id String
	 * @throws ServiceException ServiceException
	 */
	void removeByGroupId(String groupId) throws ServiceException;
	
	
	List<ActivityGroupGoods> getActivityGroupGoodsGByParams(ActivityGroupGoods activityGroupGoods);
	
	void updateActivityGroupGoods(ActivityGroupGoods activityGroupGoods);
	
	/**
	 * 查询商品的限购数 </p>
	 * 
	 * @author yangq
	 * @param map
	 * @return
	 */
	ActivityGroupGoods selectActivityGroupLimitNum(Map<String,Object> map);
	
	/**
	 * 
	 * 查询商品的限购数
	 *
	 * @param activityGroupGoods
	 * @return
	 */
	ActivityGroupGoods selectByObject(ActivityGroupGoods activityGroupGoods);
}
