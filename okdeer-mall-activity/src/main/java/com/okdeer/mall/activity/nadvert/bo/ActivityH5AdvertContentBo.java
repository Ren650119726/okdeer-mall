/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityH5AdvertContent.java
 * @Date 2017-08-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.nadvert.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.nadvert.dto.ActivityH5AdvertContentAdvDto;
import com.okdeer.mall.activity.nadvert.dto.ActivityH5AdvertContentCouponsDto;
import com.okdeer.mall.activity.nadvert.dto.ActivityH5AdvertContentDto;
import com.okdeer.mall.activity.nadvert.dto.ActivityH5AdvertContentGoodsDto;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContent;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentAdv;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentCoupons;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentGoods;

/**
 * h5_广告活动内容
 * 
 * @author mengsj
 * @version 1.0 2017-08-10
 */
public class ActivityH5AdvertContentBo implements Serializable {

    /**
	 * @Fields serialVersionUID : 序列号
	 */
	private static final long serialVersionUID = -7360264318067155011L;
	
	/**
	 * @Fields content : h5活动内容
	 */
	private ActivityH5AdvertContent content;
	
	/**
	 * @Fields contentAdv : h5活动内容>>广告图片
	 */
	private ActivityH5AdvertContentAdv contentAdv;
	
	/**
	 * @Fields contentGoods : h5活动内容>>商品列表
	 */
	private List<ActivityH5AdvertContentGoods> contentGoods;
	
	/**
	 * @Fields contentCoupons : h5活动内容>>代金券活动
	 */
	private List<ActivityH5AdvertContentCoupons> contentCoupons;

	public ActivityH5AdvertContentAdv getContentAdv() {
		return contentAdv;
	}

	public void setContentAdv(ActivityH5AdvertContentAdv contentAdv) {
		this.contentAdv = contentAdv;
	}

	public List<ActivityH5AdvertContentGoods> getContentGoods() {
		return contentGoods;
	}

	public void setContentGoods(List<ActivityH5AdvertContentGoods> contentGoods) {
		this.contentGoods = contentGoods;
	}

	public List<ActivityH5AdvertContentCoupons> getContentCoupons() {
		return contentCoupons;
	}

	public void setContentCoupons(
			List<ActivityH5AdvertContentCoupons> contentCoupons) {
		this.contentCoupons = contentCoupons;
	}

	public ActivityH5AdvertContent getContent() {
		return content;
	}

	public void setContent(ActivityH5AdvertContent content) {
		this.content = content;
	}
	
	public void convertDtoToBo(ActivityH5AdvertContentDto obj){
		if(obj != null){
			this.content = BeanMapper.map(obj, ActivityH5AdvertContent.class);
			if(obj.getAdvDto() != null){
				this.contentAdv = BeanMapper.map(obj.getAdvDto(), ActivityH5AdvertContentAdv.class);
			}
			if(CollectionUtils.isNotEmpty(obj.getContentGoodsDtos())){
				this.contentGoods = BeanMapper.mapList(obj.getContentGoodsDtos(), ActivityH5AdvertContentGoods.class);
			}
			if(CollectionUtils.isNotEmpty(obj.getCouponsDtos())){
				this.contentCoupons = BeanMapper.mapList(obj.getCouponsDtos(), ActivityH5AdvertContentCoupons.class);
			}
		}
	}
	
	public ActivityH5AdvertContentDto convertBoToDto(){
		ActivityH5AdvertContentDto contentDto = BeanMapper.map(this.getContent(), ActivityH5AdvertContentDto.class);
		ActivityH5AdvertContentAdvDto adv = BeanMapper.map(this.getContentAdv(), ActivityH5AdvertContentAdvDto.class);
		List<ActivityH5AdvertContentGoodsDto> contentGoods = new ArrayList<ActivityH5AdvertContentGoodsDto>();
		if(CollectionUtils.isNotEmpty(this.getContentGoods())){
			contentGoods = BeanMapper.mapList(this.getContentGoods(), ActivityH5AdvertContentGoodsDto.class);
		}
		List<ActivityH5AdvertContentCouponsDto> contentCoupons = new ArrayList<ActivityH5AdvertContentCouponsDto>();
		if(CollectionUtils.isNotEmpty(this.getContentCoupons())){
			contentCoupons = BeanMapper.mapList(this.getContentCoupons(), ActivityH5AdvertContentCouponsDto.class);
		}
		contentDto.setAdvDto(adv);
		contentDto.setContentGoodsDtos(contentGoods);
		contentDto.setCouponsDtos(contentCoupons);
		return contentDto;
	}
}