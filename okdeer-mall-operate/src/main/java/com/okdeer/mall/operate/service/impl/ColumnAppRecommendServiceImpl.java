/** 
 *@Project: okdeer-mall-activity 
 *@Author: tangzj02
 *@Date: 2016年12月29日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.common.utils.BaseResult;
import com.okdeer.mall.operate.dto.AppRecommendDto;
import com.okdeer.mall.operate.dto.AppRecommendParamDto;
import com.okdeer.mall.operate.entity.ColumnAppRecommend;
import com.okdeer.mall.operate.entity.ColumnAppRecommendGoods;
import com.okdeer.mall.operate.entity.ColumnSelectArea;
import com.okdeer.mall.operate.enums.AppRecommendPlace;
import com.okdeer.mall.operate.enums.AppRecommendStatus;
import com.okdeer.mall.operate.enums.ColumnType;
import com.okdeer.mall.operate.enums.SelectAreaType;
import com.okdeer.mall.operate.mapper.ColumnAppRecommendMapper;
import com.okdeer.mall.operate.service.ColumnAppRecommendGoodsService;
import com.okdeer.mall.operate.service.ColumnAppRecommendService;
import com.okdeer.mall.operate.service.ColumnSelectAreaService;

/**
 * ClassName: ColumnAppRecommendServiceImpl 
 * @Description: APP端服务商品推荐服务实现
 * @author tangzj02
 * @date 2016年12月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-12-29        tangzj02                     添加
 */
@Service
public class ColumnAppRecommendServiceImpl extends BaseServiceImpl implements ColumnAppRecommendService {

	@Autowired
	private ColumnAppRecommendMapper appRecommendMapper;

	@Autowired
	private ColumnAppRecommendGoodsService appRecommendGoodsService;

	@Autowired
	private ColumnSelectAreaService selectAreaService;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return appRecommendMapper;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ColumnAppRecommendService#deleteByIds(java.util.List)
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int deleteByIds(List<String> ids) throws Exception {
		return appRecommendMapper.deleteByIds(ids);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ColumnAppRecommendService#findList(com.okdeer.mall.operate.dto.AppRecommendParamDto)
	 */
	@Override
	public List<ColumnAppRecommend> findList(AppRecommendParamDto paramDto) throws Exception {
		return appRecommendMapper.findList(paramDto);
	}

	@Override
	@SuppressWarnings("unchecked")
	public PageUtils<AppRecommendDto> findListPage(AppRecommendParamDto paramDto) throws Exception {
		PageHelper.startPage(paramDto.getPageNumber(), paramDto.getPageSize(), true);
		List<ColumnAppRecommend> result = appRecommendMapper.findList(paramDto);
		if (result == null) {
			result = new ArrayList<ColumnAppRecommend>();
		}
		return new PageUtils<ColumnAppRecommend>(result).toBean(AppRecommendDto.class);
	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendService#save(com.okdeer.mall.operate.entity.ColumnAppRecommend, java.util.List, java.util.List)
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseResult save(ColumnAppRecommend entity, List<ColumnSelectArea> areaList,
			List<ColumnAppRecommendGoods> goodsList) throws Exception {
		if (entity == null) {
			return new BaseResult("服务商品推荐信息不能为空");
		}

		if (null == entity.getPlace() || null == entity.getAreaType()
				|| !StringUtils.isNotEmptyAll(entity.getTitle(), entity.getCoverPicUrl())) {
			return new BaseResult("服务商品推荐信息不完整");
		}

		if (SelectAreaType.city.equals(entity.getAreaType()) && (null == areaList || 0 == areaList.size())) {
			return new BaseResult("按城市选择任务范围时， 区域不允许为空");
		}

		if (null == goodsList || 0 == goodsList.size()) {
			return new BaseResult("关联商品不允许为空");
		} else if (goodsList.size() < 3 || goodsList.size() > 20) {
			return new BaseResult("推荐商品数量不能少于3款，同时不超过20款");
		}

		String recommendId = StringUtils.isBlank(entity.getId()) ? UuidUtils.getUuid() : entity.getId();

		if (isRepeatArea(entity.getId(), entity.getAreaType(), entity.getPlace(), areaList)) {
			return new BaseResult("首页栏位在同一个区域只能添加一个正在展示的服务推荐");
		}

		// 统计需要展示的商品数
		int showGoodsCount = 0;
		for (ColumnAppRecommendGoods item : goodsList) {
			item.setId(UuidUtils.getUuid());
			item.setRecommendId(recommendId);
			if (item.getIsShow().equals(1)) {
				showGoodsCount++;
			}
		}

		if (showGoodsCount < 3 || showGoodsCount > 10) {
			return new BaseResult("封面展示商品不能少于3款，同时不超过10款");
		}

		// 修改APP端服务推荐
		entity.setGoodsCount(goodsList.size());
		entity.setShowGoodsCount(showGoodsCount);
		entity.setUpdateTime(DateUtils.getSysDate());
		if (StringUtils.isNotBlank(entity.getId())) {
			// 删除之前的插入的关联数据
			selectAreaService.deleteByColumnId(entity.getId());
			appRecommendGoodsService.deleteByRecommendId(entity.getId());
			appRecommendMapper.update(entity);
		} else {
			entity.setId(recommendId);
			entity.setCreateTime(DateUtils.getSysDate());
			appRecommendMapper.add(entity);
		}

		if (null != goodsList && goodsList.size() > 0) {
			appRecommendGoodsService.insertMore(goodsList);
		}

		if (SelectAreaType.city.equals(entity.getAreaType())) {
			for (ColumnSelectArea item : areaList) {
				if (SelectAreaType.province.equals(item.getAreaType())) {
					item.setCityId("0");
				}
				item.setId(UuidUtils.getUuid());
				item.setColumnId(recommendId);
				item.setColumnType(ColumnType.appRecommend.ordinal());
			}
			selectAreaService.insertMore(areaList);
		}
		return new BaseResult();
	}

	private boolean isRepeatArea(String recommendId, SelectAreaType areaType, AppRecommendPlace place,
			List<ColumnSelectArea> areaList) throws Exception {
		if (place.equals(AppRecommendPlace.find)) {
			return false;
		}
		// 查询首页是否已经存在数据
		AppRecommendParamDto paramDto = new AppRecommendParamDto();
		paramDto.setStatus(AppRecommendStatus.show);
		paramDto.setPlace(place);
		// 设置排除自己
		paramDto.setExcludeId(recommendId);
		paramDto.setAreaType(SelectAreaType.nationwide);
		// 根据位置查询设置范围为全国的数据
		List<ColumnAppRecommend> list = appRecommendMapper.findList(paramDto);
		if (null != list && list.size() > 0) {
			// 如果同一位置中有设置范围是全国的则表示已经重复
			return true;
		}

		// 清空参数中的地区范围
		paramDto.setAreaType(null);
		// 查询位置设置范围为各省和城市的数据
		list = appRecommendMapper.findList(paramDto);
		// 未查出数据则表示首页还未发布过
		if (null == list || list.size() == 0) {
			return false;
		}
		// 如果当前设置任务范围是全国
		if (SelectAreaType.nationwide.equals(areaType)) {
			return true;
		}
		List<String> columnIds = new ArrayList<>();
		for (ColumnAppRecommend item : list) {
			columnIds.add(item.getId());
		}

		// 已保存省ID集合(全省)
		List<String> dbProvinceIds = new ArrayList<>();
		// 已保存省ID集合(部分城市)
		List<String> dbPartProvinceIds = new ArrayList<>();
		// 已保存选城市ID集合
		List<String> dbCityIds = new ArrayList<>();
		// 查询已存在的
		List<ColumnSelectArea> abAareaList = selectAreaService.findListByColumnIds(columnIds);
		// 将在同一位置已发布过推荐的省、城市ID放入集合中
		for (ColumnSelectArea item : abAareaList) {
			if (SelectAreaType.province.equals(item.getAreaType())) {
				dbProvinceIds.add(item.getProvinceId());
			} else {
				dbPartProvinceIds.add(item.getProvinceId());
				dbCityIds.add(item.getCityId());
			}
		}

		// 判断当前发布的区域是否与数据库中的数据有重复或有交集
		for (ColumnSelectArea item : areaList) {
			if (SelectAreaType.province.equals(item.getAreaType())) {
				if (dbProvinceIds.contains(item.getProvinceId()) || dbPartProvinceIds.contains(item.getProvinceId())) {
					return true;
				}
			} else if (SelectAreaType.city.equals(item.getAreaType()) && dbCityIds.contains(item.getCityId())) {
				return true;
			}
		}
		return false;
	}

}
