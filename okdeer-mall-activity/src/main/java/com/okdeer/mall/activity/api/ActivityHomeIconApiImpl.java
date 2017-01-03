/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2016年12月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.utils.BaseResult;
import com.okdeer.mall.activity.dto.ActivityHomeIconDto;
import com.okdeer.mall.activity.dto.ActivityHomeIconGoodsDto;
import com.okdeer.mall.activity.dto.ActivityHomeIconParamDto;
import com.okdeer.mall.activity.dto.ActivitySelectAreaDto;
import com.okdeer.mall.activity.entity.ActivityHomeIcon;
import com.okdeer.mall.activity.entity.ActivityHomeIconGoods;
import com.okdeer.mall.activity.entity.ActivitySelectArea;
import com.okdeer.mall.activity.enums.HomeIconTaskType;
import com.okdeer.mall.activity.enums.SelectAreaType;
import com.okdeer.mall.activity.service.ActivityHomeIconApi;
import com.okdeer.mall.activity.service.ActivityHomeIconGoodsService;
import com.okdeer.mall.activity.service.ActivityHomeIconService;
import com.okdeer.mall.activity.service.ActivitySelectAreaService;

/**
 * ClassName: HomeIconApiImpl 
 * @Description: 首页ICON服务接口实现
 * @author tangzj02
 * @date 2016年12月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	   友门鹿2.0        2016-12-28        tangzj02                     添加
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.service.ActivityHomeIconApi")
public class ActivityHomeIconApiImpl implements ActivityHomeIconApi {

	/** 日志记录 */
	private static final Logger log = LoggerFactory.getLogger(ActivityHomeIconApiImpl.class);

	@Autowired
	private ActivityHomeIconService activityHomeIconService;

	@Autowired
	private ActivityHomeIconGoodsService activityHomeIconGoodsService;

	@Autowired
	private ActivitySelectAreaService activitySelectAreaService;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityHomeIconApi#findList(com.okdeer.mall.activity.dto.ActivityHomeIconParamDto)
	 */
	@Override
	public List<ActivityHomeIconDto> findList(ActivityHomeIconParamDto paramDto) throws Exception {
		log.info("查询首页IOCN列表参数:{}", paramDto);
		List<ActivityHomeIcon> sourceList = activityHomeIconService.findList(paramDto);
		List<ActivityHomeIconDto> dtoList = null;
		if (null == sourceList) {
			dtoList = new ArrayList<ActivityHomeIconDto>();
		} else {
			dtoList = BeanMapper.mapList(sourceList, ActivityHomeIconDto.class);
		}
		return dtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityHomeIconApi#findHomeIconProductDtoListByHomeIconId(java.lang.String)
	 */
	@Override
	public List<ActivityHomeIconGoodsDto> findHomeIconGoodsDtoListByHomeIconId(String homeIconId) throws Exception {
		List<ActivityHomeIconGoodsDto> dtoList = new ArrayList<>();
		if (StringUtils.isNotBlank(homeIconId)) {
			List<ActivityHomeIconGoods> sourceList = activityHomeIconGoodsService.findListByHomeIconId(homeIconId);
			if (sourceList != null) {
				dtoList = BeanMapper.mapList(sourceList, ActivityHomeIconGoodsDto.class);
			}
		}
		return dtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityHomeIconApi#findActivitySelectAreaDtoListByActivityId(java.lang.String)
	 */
	@Override
	public List<ActivitySelectAreaDto> findActivitySelectAreaDtoListByActivityId(String iconId) throws Exception {
		List<ActivitySelectAreaDto> dtoList = new ArrayList<>();
		if (StringUtils.isNotBlank(iconId)) {
			List<ActivitySelectArea> sourceList = activitySelectAreaService.findListByActivityId(iconId);
			if (sourceList != null) {
				dtoList = BeanMapper.mapList(sourceList, ActivitySelectAreaDto.class);
			}
		}
		return dtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityHomeIconApi#findById(java.lang.String)
	 */
	@Override
	public ActivityHomeIconDto findById(String homeIconId) throws Exception {
		if (StringUtils.isBlank(homeIconId)) {
			return null;
		}
		ActivityHomeIcon source = activityHomeIconService.findById(homeIconId);
		if (null == source) {
			return null;
		}
		ActivityHomeIconDto dto = BeanMapper.map(source, ActivityHomeIconDto.class);
		return dto;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityHomeIconApi#deleteById(java.lang.String)
	 */
	@Override
	public BaseResult deleteById(String iconId) throws Exception {
		if (StringUtils.isBlank(iconId)) {
			return new BaseResult("homeIconId不能为空");
		}
		int result = activityHomeIconService.delete(iconId);
		if (result > 0) {
			return new BaseResult();
		}
		return new BaseResult("删除失败");
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityHomeIconApi#save(com.okdeer.mall.activity.dto.ActivityHomeIconDto)
	 */
	@Override
	public BaseResult save(ActivityHomeIconDto dto) throws Exception {
		if (dto == null) {
			return new BaseResult("HomeIconDto信息不能为空");
		}
		if (null == dto.getPlace() || null == dto.getTaskScope() || null == dto.getTaskType()
				|| StringUtils.isNotEmptyAll(dto.getName(), dto.getIconUrl())) {
			return new BaseResult("HomeIconDto信息不完整");
		}

		List<ActivitySelectArea> areaList = null;
		if (SelectAreaType.city.getCode().equals(dto.getTaskScope())) {
			if (null == dto.getAreaList() || 0 == dto.getAreaList().size()) {
				return new BaseResult("城市ID集合 当任务范围未1:按城市选择任务范围时， 不允许为空");
			} else {
				areaList = BeanMapper.mapList(dto.getAreaList(), ActivitySelectArea.class);
			}
		}

		if (HomeIconTaskType.goods.getCode().equals(dto.getTaskType())
				&& (null == dto.getGoodsIds() || 0 == dto.getGoodsIds().size())) {
			return new BaseResult("商品ID集合   当任务内容  0:指定指定商品推荐时， 不允许为空");
		}

		// 复制属性信息
		ActivityHomeIcon entity = BeanMapper.map(dto, ActivityHomeIcon.class);
		// 设置首页ICONID
		String homeIconId = StringUtils.isBlank(entity.getId()) ? UuidUtils.getUuid() : entity.getId();
		// 判断是否存在重复
		if (isRepeatArea(homeIconId, entity.getTaskScope(), entity.getPlace(), areaList)) {
			return new BaseResult("同一区域、同一位置不可同时存在同一ICON栏位设置");
		}

		// 冗余字段部分信息
		if (SelectAreaType.nationwide.getCode().equals(dto.getTaskScope())) {
			// 如果任务范围是全国，则直接填充全国
			entity.setTaskContent("全国");
		} else {
			// 如果任务范围是根据城市选择， 这获取第一个城市或者是省
			ActivitySelectArea area = areaList.get(0);
			String areaName = "";
			if (SelectAreaType.province.getCode().equals(area.getAreaType())) {
				areaName = area.getProvinceName();
			} else {
				areaName = area.getCityName();
			}
			if (StringUtils.isNotBlank(areaName)) {
				entity.setTaskContent(areaName + "等" + areaList.size() + "个区域");
			}
		}

		// 修改首页ICON
		if (StringUtils.isNotBlank(entity.getId())) {
			// 删除之前的插入的关联数据
			activitySelectAreaService.deleteByActivityId(entity.getId());
			activityHomeIconGoodsService.deleteByHomeIconId(entity.getId());
			activityHomeIconService.update(entity);
		} else {
			entity.setId(homeIconId);
			activityHomeIconService.add(entity);
		}

		// 插入所选地区范围关联数据
		if (areaList.size() > 0) {
			activitySelectAreaService.insertMore(areaList);
		}

		// 插入所需的商品关联数据
		if (HomeIconTaskType.goods.getCode().equals(dto.getTaskType())) {
			List<ActivityHomeIconGoods> goodsList = new ArrayList<>();
			ActivityHomeIconGoods goods = null;
			for (String item : dto.getGoodsIds()) {
				goods = new ActivityHomeIconGoods();
				goods.setId(UuidUtils.getUuid());
				goods.setHomeIconId(entity.getId());
				goods.setStoreSkuId(item);
				goodsList.add(goods);
			}
			if (goodsList.size() > 0) {
				activityHomeIconGoodsService.insertMore(goodsList);
			}
		}
		return new BaseResult();
	}

	private boolean isRepeatArea(String homeIconId, Integer taskScope, Integer place,
			List<ActivitySelectArea> areaList) {
		// 查询是否已经存在使用了相同的ICON位置数据
		ActivityHomeIconParamDto paramDto = new ActivityHomeIconParamDto();
		paramDto.setPlace(place);
		// 设置排除自己
		paramDto.setExcludeId(homeIconId);
		List<ActivityHomeIcon> list = activityHomeIconService.findList(paramDto);
		// 未查出数据则表示当前ICON位置还未发布过
		if (null != list && list.size() > 0) {
			return false;
		}
		// 如果当前设置任务范围是全国
		if (SelectAreaType.nationwide.getCode().equals(taskScope)) {
			return true;
		}
		// 已保存省ID集合
		List<String> dbProvinceIds = new ArrayList<>();
		// 已保存选城市ID集合
		List<String> dbCityIds = new ArrayList<>();
		for (ActivitySelectArea item : areaList) {
			dbProvinceIds.add(item.getProvinceId());
			dbCityIds.add(item.getCityId());
		}
		for (ActivitySelectArea item : areaList) {
			if (dbProvinceIds.contains(item.getProvinceId())) {
				return true;
			} else if (dbProvinceIds.contains(item.getCityId())) {
				return true;
			} else {
				item.setId(UuidUtils.getUuid());
				item.setActivityId(homeIconId);
			}
		}
		return false;
	}

}
