/** 
 *@Project: okdeer-mall-activity 
 *@Author: tangzj02
 *@Date: 2016年12月29日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.utils.BaseResult;
import com.okdeer.mall.activity.dto.ActivityAppRecommendDto;
import com.okdeer.mall.activity.dto.ActivityAppRecommendGoodsDto;
import com.okdeer.mall.activity.dto.ActivitySelectAreaDto;
import com.okdeer.mall.activity.dto.AppRecommendGoodsParamDto;
import com.okdeer.mall.activity.dto.AppRecommendParamDto;
import com.okdeer.mall.activity.entity.ActivityAppRecommend;
import com.okdeer.mall.activity.entity.ActivityAppRecommendGoods;
import com.okdeer.mall.activity.entity.ActivitySelectArea;
import com.okdeer.mall.activity.enums.AppRecommendStatus;
import com.okdeer.mall.activity.enums.SelectAreaType;
import com.okdeer.mall.activity.service.ActivityAppRecommendApi;
import com.okdeer.mall.activity.service.ActivityAppRecommendGoodsService;
import com.okdeer.mall.activity.service.ActivityAppRecommendService;
import com.okdeer.mall.activity.service.ActivitySelectAreaService;

/**
 * ClassName: AppServiceGoodsRecommendApiImpl 
 * @Description: APP端服务商品推荐服务
 * @author tangzj02
 * @date 2016年12月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-12-29        tangzj02                     添加
 */

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.service.ActivityAppRecommendApi")
public class ActivityAppRecommendApiImpl implements ActivityAppRecommendApi {

	/** 日志记录 */
	private static final Logger log = LoggerFactory.getLogger(ActivityAppRecommendApiImpl.class);

	@Autowired
	private ActivityAppRecommendService activityAppRecommendService;

	@Autowired
	private ActivityAppRecommendGoodsService activityAppRecommendGoodsService;

	@Autowired
	private ActivitySelectAreaService activitySelectAreaService;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityAppRecommendApi#findList(com.okdeer.mall.activity.dto.AppRecommendParamDto)
	 */
	@Override
	public List<ActivityAppRecommendDto> findList(AppRecommendParamDto paramDto) throws Exception {
		log.info("查询APP端服务商品推荐列表参数:{}", paramDto);
		List<ActivityAppRecommend> sourceList = activityAppRecommendService.findList(paramDto);
		List<ActivityAppRecommendDto> dtoList = null;
		if (null == sourceList) {
			dtoList = new ArrayList<ActivityAppRecommendDto>();
		} else {
			dtoList = BeanMapper.mapList(sourceList, ActivityAppRecommendDto.class);
		}
		return dtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityAppRecommendApi#findById(java.lang.String)
	 */
	@Override
	public ActivityAppRecommendDto findById(String id) throws Exception {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		// 查询APP端服务商品推荐信息
		ActivityAppRecommend recommend = activityAppRecommendService.findById(id);
		if (null == recommend) {
			return null;
		}
		// 复制APP端服务商品推荐信息
		ActivityAppRecommendDto dto = BeanMapper.map(recommend, ActivityAppRecommendDto.class);

		// 如果范围是城市， 这需要查询APP端服务商品推荐与地区的关联信息
		List<ActivitySelectAreaDto> areaDtoList = null;
		if (SelectAreaType.city.getCode().equals(recommend.getAreaType())) {
			List<ActivitySelectArea> areaList = activitySelectAreaService.findListByActivityId(id);
			if (null == areaList) {
				areaDtoList = new ArrayList<>();
			} else {
				areaDtoList = BeanMapper.mapList(areaList, ActivitySelectAreaDto.class);
			}
		}
		dto.setAreaList(areaDtoList);
		return dto;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityAppRecommendApi#findAppRecommendGoodsDtoListByRecommendId(java.lang.String)
	 */
	@Override
	public List<ActivityAppRecommendGoodsDto> findAppRecommendGoodsDtoListByRecommendId(String recommendId)
			throws Exception {
		if (StringUtils.isBlank(recommendId)) {
			return null;
		}
		List<ActivityAppRecommendGoods> goodsList = activityAppRecommendGoodsService.findListByRecommendId(recommendId);
		List<ActivityAppRecommendGoodsDto> goodsDtoList = null;
		if (null == goodsList) {
			goodsDtoList = new ArrayList<>();
		} else {
			goodsDtoList = BeanMapper.mapList(goodsList, ActivityAppRecommendGoodsDto.class);
		}
		return goodsDtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityAppRecommendApi#findAppRecommendGoodsDtoList(com.okdeer.mall.activity.dto.AppRecommendGoodsParamDto)
	 */
	@Override
	public List<ActivityAppRecommendGoodsDto> findAppRecommendGoodsDtoList(AppRecommendGoodsParamDto paramDto)
			throws Exception {
		List<ActivityAppRecommendGoods> goodsList = activityAppRecommendGoodsService.findList(paramDto);
		List<ActivityAppRecommendGoodsDto> goodsDtoList = null;
		if (null == goodsList) {
			goodsDtoList = new ArrayList<>();
		} else {
			goodsDtoList = BeanMapper.mapList(goodsList, ActivityAppRecommendGoodsDto.class);
		}
		return goodsDtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityAppRecommendApi#findActivitySelectAreaDtoListByRecommendId(java.lang.String)
	 */
	@Override
	public List<ActivitySelectAreaDto> findActivitySelectAreaDtoListByRecommendId(String recommendId) throws Exception {
		List<ActivitySelectAreaDto> dtoList = new ArrayList<>();
		if (StringUtils.isNotBlank(recommendId)) {
			List<ActivitySelectArea> sourceList = activitySelectAreaService.findListByActivityId(recommendId);
			if (sourceList != null) {
				dtoList = BeanMapper.mapList(sourceList, ActivitySelectAreaDto.class);
			}
		}
		return dtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityAppRecommendApi#save(com.okdeer.mall.activity.dto.ActivityAppRecommendDto)
	 */
	@Override
	public BaseResult save(ActivityAppRecommendDto dto) throws Exception {
		if (dto == null) {
			return new BaseResult("ActivityAppRecommendDto信息不能为空");
		}

		if (null == dto.getPlace() || null == dto.getAreaType()
				|| StringUtils.isNotEmptyAll(dto.getTitle(), dto.getCoverPicUrl())) {
			return new BaseResult("ActivityAppRecommendDto信息不完整");
		}

		if (SelectAreaType.city.getCode().equals(dto.getAreaType())
				&& (null == dto.getAreaList() || 0 == dto.getAreaList().size())) {
			return new BaseResult("按城市选择任务范围时， 区域不允许为空");
		}

		if (null == dto.getGoodsList() || 0 == dto.getGoodsList().size()) {
			return new BaseResult("关联商品不允许为空");
		}

		// 复制属性信息
		ActivityAppRecommend entity = BeanMapper.map(dto, ActivityAppRecommend.class);
		String recommendId = StringUtils.isBlank(entity.getId()) ? UuidUtils.getUuid() : entity.getId();
		// 修改首页ICON
		if (StringUtils.isNotBlank(entity.getId())) {
			// 删除之前的插入的关联数据
			activitySelectAreaService.deleteByActivityId(entity.getId());
			activityAppRecommendGoodsService.deleteByRecommendId(entity.getId());
			activityAppRecommendService.update(entity);
		} else {
			entity.setId(recommendId);
			activitySelectAreaService.add(entity);
		}

		for (ActivityAppRecommendGoodsDto item : dto.getGoodsList()) {
			item.setId(UuidUtils.getUuid());
			item.setRecommendId(recommendId);
		}
		List<ActivityAppRecommendGoods> goodsList = BeanMapper.mapList(dto.getGoodsList(),
				ActivityAppRecommendGoods.class);
		activityAppRecommendGoodsService.insertMore(goodsList);

		if (SelectAreaType.city.getCode().equals(dto.getAreaType())) {
			for (ActivitySelectAreaDto item : dto.getAreaList()) {
				item.setId(UuidUtils.getUuid());
				item.setActivityId(recommendId);
				item.setActivityType(2);
			}
			List<ActivitySelectArea> areaList = BeanMapper.mapList(dto.getAreaList(), ActivitySelectArea.class);
			activitySelectAreaService.insertMore(areaList);
		}
		return new BaseResult();
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityAppRecommendApi#closeById(java.lang.String)
	 */
	@Override
	public int closeById(String id) throws Exception {
		if (StringUtils.isBlank(id)) {
			return 0;
		}
		ActivityAppRecommend entity = new ActivityAppRecommend();
		entity.setId(id);
		entity.setStatus(AppRecommendStatus.close.getCode());
		return activityAppRecommendService.update(entity);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityAppRecommendApi#updateById(java.lang.String, java.lang.Integer)
	 */
	@Override
	public int updateById(String id, Integer sort) throws Exception {
		if (StringUtils.isBlank(id) || null == sort) {
			return 0;
		}
		ActivityAppRecommend entity = new ActivityAppRecommend();
		entity.setId(id);
		entity.setSort(sort);
		return activityAppRecommendService.update(entity);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityAppRecommendApi#deleteByIds(java.util.List)
	 */
	@Override
	public int deleteByIds(List<String> ids) throws Exception {
		if (null == ids || ids.size() == 0) {
			return 0;
		}
		return activityAppRecommendService.deleteByIds(ids);
	}

	@Override
	public PageUtils<ActivityAppRecommendDto> findPageList(AppRecommendParamDto paramDto) {
		return activityAppRecommendService.findPageList(paramDto);
	}
}
