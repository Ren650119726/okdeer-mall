package com.okdeer.mall.activity.discount.service;

import java.util.List;

import com.okdeer.archive.goods.store.dto.GoodsStoreActivitySkuDto;
import com.okdeer.mall.activity.discount.dto.ActivityCloudStoreParamDto;
import com.okdeer.mall.activity.discount.dto.ActivityCloudStoreResultDto;
import com.okdeer.mall.activity.discount.dto.ActivityGoodsParamDto;

/**
 * ClassName: ActivityCloudStoreService 
 * @Description: 便利店活动服务接口
 * @author tuzhd
 * @date 2017年12月7日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.7				2017-12-07         tuzhd			 便利店活动服务接口
 */
public interface ActivityCloudStoreService {
	
	/**
	 * @Description: 根据店铺及商品查询店铺活动信息，满赠、加价购、N件X元
	 * @param paramDto
	 * @return ActivityCloudStoreResultDto  
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年12月7日
	 */
	ActivityCloudStoreResultDto getCloudStoreActivity(ActivityCloudStoreParamDto paramDto) throws Exception;

	/**
	 * @Description: 获得买满送的商品列表
	 * @param param   
	 * @author tuzhd
	 * @return 
	 * @date 2017年12月22日
	 */
	List<GoodsStoreActivitySkuDto> getGiveActivityGoods(ActivityGoodsParamDto param);

}
