package com.okdeer.mall.activity.group.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.archive.goods.spu.vo.ActivityGroupGoodsVo;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.mall.activity.group.entity.ActivityGroupGoods;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;

/**
 * 
 * 
 * @pr mall
 * @desc 团购活动商品
 * @author chenwj
 * @date 2016年1月22日 下午2:50:50
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
public interface ActivityGroupGoodsService {

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
	 * 根据团购ID 删除团购商品
	 * @param id String
	 * @throws ServiceException ServiceException
	 */
	void deleteByGroupId(String groupId) throws ServiceException;
	
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
	
	
	void updateActivityGroupGoods(ActivityGroupGoods activityGroupGoods) throws ServiceException;
	
	/**
	 * 查询所有团购活动商品
	 * @param activityGroupGoods ActivityGroupGoods
	 * @return PageUtils
	 */
	PageUtils<ActivityGroupGoods> findActivityGroupGoods(ActivityGroupGoods activityGroupGoods,int pageNumber, int pageSize);
	
	/**
	 * 查询所有团购活动商品
	 * @param activityGroupGoods ActivityGroupGoods
	 * @return PageUtils
	 */
	List<ActivityGroupGoods> findActivityGroupGoodsByParam(Map<String,Object> map,int pageNumber, int pageSize);
	
	/**
	 * 根据ID查询spu商品信息
	 * @param ids List
	 * @param pageNumber int
	 * @param pageSize int
	 * @return PageUtils
	 */
	PageUtils<ActivityGroupGoodsVo> findSpuBySkuId(List<String> ids,String storeId,int pageNumber, int pageSize);
	
	
	List<ActivityGroupGoodsVo> findSpuBySkuIds(List<String> ids,String storeId);
	
	/**
	 * 根据店铺ID获取商品
	 * @param storeId String
	 * @return List
	 */
	PageUtils<ActivityGroupGoodsVo> getActivityGroupGoodsByParam(Map<String,Object> map,int pageNumber, int pageSize);
	
	/**
	 * 根据ID查询goodsStoreId商品信息
	 * @param ids List
	 * @return List
	 */
	List<ActivityGroupGoodsVo> findSpuByGoodsStoreId(@Param("ids") List<String> ids,@Param("storeId") String storeId,@Param("online") String online);
	
	/**
	 * 根据ID查询goodsStoreId商品信息
	 * @param ids List
	 * @return List
	 */
	PageUtils<ActivityGroupGoodsVo> findSpuByGoodsStoreId(@Param("ids") List<String> ids,String groupId,@Param("storeId") String storeId,@Param("online") String online,int pageNumber, int pageSize);
	
	List<ActivityGroupGoods> getActivityGroupGoodsGByParams(ActivityGroupGoods activityGroupGoods);
	
	void syncGoodsStock(ActivityGroupGoods activityGroupGoods,String userId,StockOperateEnum stockOperateEnum);
	
	/**
	 * 查询商品的限购数 </p>
	 * 
	 * @author yangq
	 * @param map
	 * @return
	 */
	ActivityGroupGoods selectActivityGroupLimitNum(Map<String,Object> map)throws Exception;
	
	/**
	 * 根据团购ID 删除团购商品
	 * @param id String
	 * @throws ServiceException ServiceException
	 */
	void removeByGroupId(String groupId) throws ServiceException;
	
	/**
	 * 
	 * 查询商品的限购数 </p>
	 *
	 * @param activityGroupGoods  activityGroupGoods
	 * @return
	 * @throws Exception
	 */
	ActivityGroupGoods selectByObject(ActivityGroupGoods activityGroupGoods)throws ServiceException;
}
