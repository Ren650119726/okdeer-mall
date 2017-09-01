package com.okdeer.mall.activity.nadvert.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.okdeer.archive.goods.store.dto.GoodsStoreActivitySkuDto;
import com.okdeer.base.common.utils.PageUtils;
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
			String contentId,Integer goodsType) {
		if(StringUtils.isNotBlank(activityId)){
			List<ActivityH5AdvertContentGoods> goods = mapper.findByActId(activityId, contentId,goodsType);
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

	@Override
	public PageUtils<GoodsStoreActivitySkuDto> findBldGoodsByActivityId(
			String storeId, String activityId, String contentId,
			Integer pageNumber, Integer pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
        List<GoodsStoreActivitySkuDto> result = mapper.findBldGoodsByActivityId(storeId,activityId,contentId);
        return new PageUtils<GoodsStoreActivitySkuDto>(result);
	}

	@Override
	public List<Map<String, Object>> findFwdGoodsByContent(String activityId,
			String contentId) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if(StringUtils.isNotBlank(contentId)){
			result = mapper.findFwdGoodsByContent(activityId, contentId);
		}
		return result;
	}
}
