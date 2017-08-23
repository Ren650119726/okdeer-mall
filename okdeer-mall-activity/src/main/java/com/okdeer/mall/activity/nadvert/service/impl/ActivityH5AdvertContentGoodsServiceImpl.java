package com.okdeer.mall.activity.nadvert.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentGoods;
import com.okdeer.mall.activity.nadvert.mapper.ActivityH5AdvertContentGoodsMapper;
import com.okdeer.mall.activity.nadvert.service.ActivityH5AdvertContentGoodsService;

/**
 * ClassName: ActivityH5AdvertContentGoodsServiceImpl 
 * @Description: H5活动内容>>商品管理
 * @author mengsj
 * @date 2017年8月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
 
@Service
public class ActivityH5AdvertContentGoodsServiceImpl
		implements ActivityH5AdvertContentGoodsService {
	
	@Value("${goodsImagePrefix}")
	private String goodsImgPath;

	@Value("${storeImagePrefix}")
	private String serviceGoodsImgPath;
	
	@Autowired
	private ActivityH5AdvertContentGoodsMapper mapper;

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void batchSave(List<ActivityH5AdvertContentGoods> entitys)
			throws Exception {
		if(CollectionUtils.isNotEmpty(entitys)){
			entitys.forEach(obj -> {
				obj.setId(UuidUtils.getUuid());
				obj.setCreateTime(new Date());
			});
			mapper.batchSave(entitys);
		}		
	}

	@Override
	public List<ActivityH5AdvertContentGoods> findByActId(String activityId,
			String contentId) {
		if(StringUtils.isNotBlank(activityId)){
			List<ActivityH5AdvertContentGoods> goods = mapper.findByActId(activityId, contentId);
			goods.forEach(good -> {
				//便利店商品
				if(good.getGoodsType() == 1){
					good.setGoodsSkuPic(goodsImgPath + good.getGoodsSkuPic());
				}else if(good.getGoodsType() == 2){
					//服务店
					good.setGoodsSkuPic(serviceGoodsImgPath + good.getGoodsSkuPic());
				}
			});
			return goods;
		}
		return new ArrayList<>();
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void deleteByActId(String activityId, String contentId)
			throws Exception {
		mapper.deleteByActId(activityId, contentId);
	}
}
