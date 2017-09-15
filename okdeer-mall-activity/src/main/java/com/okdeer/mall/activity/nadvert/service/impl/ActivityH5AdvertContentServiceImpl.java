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
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.nadvert.bo.ActivityH5AdvertContentBo;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContent;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentAdv;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentCoupons;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentGoods;
import com.okdeer.mall.activity.nadvert.mapper.ActivityH5AdvertContentMapper;
import com.okdeer.mall.activity.nadvert.service.ActivityH5AdvertContentAdvService;
import com.okdeer.mall.activity.nadvert.service.ActivityH5AdvertContentCouponsService;
import com.okdeer.mall.activity.nadvert.service.ActivityH5AdvertContentGoodsService;
import com.okdeer.mall.activity.nadvert.service.ActivityH5AdvertContentService;

/**
 * ClassName: ActivityH5AdvertContentServiceImpl 
 * @Description: h5活动内容管理
 * @author mengsj
 * @date 2017年8月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
 
@Service
public class ActivityH5AdvertContentServiceImpl
		implements ActivityH5AdvertContentService {
	
	@Autowired
	private ActivityH5AdvertContentMapper mapper;
	@Autowired
	private ActivityH5AdvertContentAdvService advService;
	@Autowired
	private ActivityH5AdvertContentGoodsService goodsService;
	@Autowired
	private ActivityH5AdvertContentCouponsService couponsService;
	
	@Value("${goodsImagePrefix}")
	private String goodsImgPath;

	@Value("${storeImagePrefix}")
	private String serviceGoodsImgPath;

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void batchSave(List<ActivityH5AdvertContentBo> bos) throws Exception {
		//h5活动内容集合
		List<ActivityH5AdvertContent> contents = new ArrayList<ActivityH5AdvertContent>();
		//h5活动内容>>广告图片集合
		List<ActivityH5AdvertContentAdv> advs = new ArrayList<ActivityH5AdvertContentAdv>();
		
		//h5活动内容>>代金券活动集合
		List<ActivityH5AdvertContentCoupons> coupons = new ArrayList<ActivityH5AdvertContentCoupons>();
		//h5活动内容>>商品列表集合
		List<ActivityH5AdvertContentGoods> goods = new ArrayList<ActivityH5AdvertContentGoods>();
		if(CollectionUtils.isNotEmpty(bos)){
			for(ActivityH5AdvertContentBo obj : bos){
				//活动内容
				ActivityH5AdvertContent content = obj.getContent();
				content.setId(UuidUtils.getUuid());
				content.setCreateTime(new Date());
				contents.add(content);
				
				//广告图片
				ActivityH5AdvertContentAdv adv = obj.getContentAdv();
				if(adv != null){
					adv.setActivityId(content.getActivityId());
					adv.setContentId(content.getId());
					adv.setCreateUserId(content.getCreateUserId());
					advs.add(adv);
				}
				//代金券
				List<ActivityH5AdvertContentCoupons> cc = obj.getContentCoupons();
				List<ActivityH5AdvertContentCoupons> ccc = new ArrayList<ActivityH5AdvertContentCoupons>();
				if(CollectionUtils.isNotEmpty(cc)){
					cc.forEach(temp -> {
						if(StringUtils.isNotBlank(temp.getCollectCouponsId())){
							temp.setActivityId(content.getActivityId());
							temp.setContentId(content.getId());
							temp.setCreateUserId(content.getCreateUserId());
							temp.setCreateTime(new Date());
							ccc.add(temp);
						}
					});
					coupons.addAll(ccc);
				}
				//商品列表
				List<ActivityH5AdvertContentGoods> cg = obj.getContentGoods();
				List<ActivityH5AdvertContentGoods> cgg = new ArrayList<ActivityH5AdvertContentGoods>();
				if(CollectionUtils.isNotEmpty(cg)){
					cg.forEach(temp -> {
						if(StringUtils.isNotBlank(temp.getStoreSkuId())){
							temp.setActivityId(content.getActivityId());
							temp.setContentId(content.getId());
							temp.setCreateUserId(content.getCreateUserId());
							temp.setCreateTime(new Date());
							cgg.add(temp);
						}
					});
					goods.addAll(cgg);
				}
				
			}
			//保存广告
			advService.batchSave(advs);
			//保存代金券活动
			couponsService.batchSave(coupons);
			//保存商品列表
			goodsService.batchSave(goods);
			mapper.batchSave(contents);
		}		
	}

	@Override
	public List<ActivityH5AdvertContentBo> findByActId(String activityId) {
		List<ActivityH5AdvertContentBo> bos = new ArrayList<ActivityH5AdvertContentBo>();
		if(StringUtils.isNotBlank(activityId)){
			List<ActivityH5AdvertContent> contents = mapper.findByActId(activityId);
			contents.forEach(obj -> {
				ActivityH5AdvertContentBo bo = new ActivityH5AdvertContentBo();
				bo.setContent(BeanMapper.map(obj, ActivityH5AdvertContent.class));
				//设置广告图片
				Integer contentType = obj.getContentType() != null ? obj.getContentType() : 0;
				if(contentType == 1){
					List<ActivityH5AdvertContentAdv> advs = advService.findByActId(activityId, obj.getId());
					bo.setContentAdv(advs.size() > 0 ? advs.get(0) : new ActivityH5AdvertContentAdv());
				}
				//设置代金券活动
				if(contentType == 3){
					bo.setContentCoupons(couponsService.findByActId(activityId, obj.getId()));
				}
				//设置商品列表
				if(contentType == 2 || contentType == 4){
					List<ActivityH5AdvertContentGoods> goods = goodsService.findByActId(activityId, obj.getId(),obj.getContentType());
					//便利店商品
					if(obj.getGoodsType() == 1){
						goods.forEach(good -> {
							good.setGoodsSkuPic(goodsImgPath + good.getGoodsSkuPic());
						});
					}else if(obj.getGoodsType() == 2){
						//服务店商品
						goods.forEach(good -> {
							if(serviceGoodsImgPath.lastIndexOf("/")  > 0){
								good.setGoodsSkuPic(serviceGoodsImgPath + good.getGoodsSkuPic());
							}else{
								good.setGoodsSkuPic(serviceGoodsImgPath + "/" + good.getGoodsSkuPic());
							}
						});
					}
					bo.setContentGoods(goods);
				}
				bos.add(bo);
			});
			return bos;
		}
		return new ArrayList<>();
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void deleteByActId(String activityId) throws Exception {
		if(StringUtils.isNotBlank(activityId)){
			//删除h5活动内容>>管理商品
			goodsService.deleteByActId(activityId, null);
			//删除h5活动内容>>代金券
			couponsService.deleteByActId(activityId, null);
			//删除h5活动内容>>广告
			advService.deleteByActId(activityId, null);
			//删除h5活动内容
			mapper.deleteByActId(activityId);
		}
	}
}
