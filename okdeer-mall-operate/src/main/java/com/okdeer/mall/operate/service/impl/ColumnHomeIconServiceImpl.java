/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2016年12月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.common.utils.BaseResult;
import com.okdeer.mall.operate.dto.ColumnHomeIconClassifyDto;
import com.okdeer.mall.operate.dto.ColumnHomeIconVersionDto;
import com.okdeer.mall.operate.dto.HomeIconParamDto;
import com.okdeer.mall.operate.entity.ColumnHomeIcon;
import com.okdeer.mall.operate.entity.ColumnHomeIconGoods;
import com.okdeer.mall.operate.entity.ColumnHomeIconVersion;
import com.okdeer.mall.operate.entity.ColumnSelectArea;
import com.okdeer.mall.operate.enums.ColumnType;
import com.okdeer.mall.operate.enums.HomeIconPlace;
import com.okdeer.mall.operate.enums.HomeIconTaskType;
import com.okdeer.mall.operate.enums.SelectAreaType;
import com.okdeer.mall.operate.mapper.ColumnHomeIconMapper;
import com.okdeer.mall.operate.mapper.ColumnHomeIconVersionMapper;
import com.okdeer.mall.operate.service.ColumnHomeIconClassifyService;
import com.okdeer.mall.operate.service.ColumnHomeIconGoodsService;
import com.okdeer.mall.operate.service.ColumnHomeIconService;
import com.okdeer.mall.operate.service.ColumnSelectAreaService;

/**
 * ClassName: ColumnHomeIconServiceImpl 
 * @Description: 首页ICON的服务实现
 * @author tangzj02
 * @date 2016年12月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * * 	        友门鹿2.0        2016-12-28        tangzj02                     添加
 */

@Service
public class ColumnHomeIconServiceImpl extends BaseServiceImpl implements ColumnHomeIconService {

	@Autowired
	private ColumnHomeIconMapper homeIconMapper;

	@Autowired
	private ColumnHomeIconGoodsService homeIconGoodsService;

	@Autowired
	private ColumnSelectAreaService selectAreaService;
	
	@Autowired
	private ColumnHomeIconClassifyService columnHomeIconClassifyService;

	@Autowired
	private ColumnHomeIconVersionMapper homeIconVersionMapper;

	@Override
	public IBaseMapper getBaseMapper() {
		return homeIconMapper;
	}

	@Override
	public List<ColumnHomeIcon> findList(HomeIconParamDto paramDto) throws Exception {
		return homeIconMapper.findList(paramDto);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseResult save(ColumnHomeIcon entity, List<ColumnSelectArea> areaList, List<String> storeSkuIds, List<Integer> sorts,
	        List<String> versions) throws Exception {
		if (entity == null) {
			return new BaseResult("HomeIconDto信息不能为空");
		}
		if (null == entity.getPlace() || null == entity.getTaskScope() || null == entity.getTaskType()
				|| !StringUtils.isNotEmptyAll(entity.getName())) {
			return new BaseResult("ICON信息不完整");
		}
		
		if(entity.getName().length() > 4){
			return new BaseResult("ICON名称不能大于4个字符");
		}

		if (SelectAreaType.city.equals(entity.getTaskScope()) && (null == areaList || 0 == areaList.size())) {
			return new BaseResult("当任务范围为“按城市选择任务范围”时， 区域列表不允许为空");
		}

		if (HomeIconTaskType.goods.equals(entity.getTaskType()) && (null == storeSkuIds || 0 == storeSkuIds.size())) {
			return new BaseResult("当任务内容为“指定商品推荐”时， 商品列表不允许为空");
		}

		if(CollectionUtils.isEmpty(versions)) {
		    return new BaseResult("请选择ICON所支持的版本号");
		}
		
		// 设置首页ICONID
		String homeIconId = StringUtils.isBlank(entity.getId()) ? UuidUtils.getUuid() : entity.getId();
		// 判断是否存在重复
		if (isRepeatArea(homeIconId, entity.getTaskScope(), entity.getPlace(), areaList, versions)) {
			return new BaseResult("同一区域、同一位置、同一版本不可同时存在同一ICON栏位设置");
		}

		// 冗余字段部分信息
		if (SelectAreaType.nationwide.equals(entity.getTaskScope())) {
			// 如果任务范围是全国，则直接填充全国
			entity.setTaskContent("全国");
		} else {
			// 如果任务范围是根据城市选择， 这获取第一个城市或者是省
			ColumnSelectArea area = areaList.get(0);
			String areaName = "";
			if (SelectAreaType.province.equals(area.getAreaType())) {
				areaName = area.getProvinceName();
			} else {
				areaName = area.getCityName();
			}
			if (StringUtils.isNotBlank(areaName)) {
				entity.setTaskContent(areaName + "等" + areaList.size() + "个区域");
			}
		}

		// 修改首页ICON
		entity.setUpdateTime(DateUtils.getSysDate());
		if (StringUtils.isNotBlank(entity.getId())) {
			// 删除之前的插入的关联数据
			selectAreaService.deleteByColumnId(entity.getId());
			homeIconGoodsService.deleteByHomeIconId(entity.getId());
			columnHomeIconClassifyService.deleteByHomeIconId(entity.getId());
			homeIconVersionMapper.deleteByIconId(entity.getId());
			homeIconMapper.update(entity);
		} else {
			entity.setId(homeIconId);
			entity.setCreateTime(DateUtils.getSysDate());
			homeIconMapper.add(entity);
		}
	
		// 插入所选地区范围关联数据
		if (null != areaList && areaList.size() > 0) {
			for (ColumnSelectArea item : areaList) {
				if (SelectAreaType.province.equals(item.getAreaType())) {
					item.setCityId("0");
				}
				item.setId(UuidUtils.getUuid());
				item.setColumnId(homeIconId);
				item.setColumnType(ColumnType.homeIcon.ordinal());
			}
			selectAreaService.insertMore(areaList);
		}

		// 插入所需的商品关联数据
		if (HomeIconTaskType.goods.equals(entity.getTaskType())) {
			List<ColumnHomeIconGoods> goodsList = new ArrayList<>();
			ColumnHomeIconGoods goods = null;
			
			int i = 0;
			for (String item : storeSkuIds) {
				goods = new ColumnHomeIconGoods();
				goods.setId(UuidUtils.getUuid());
				goods.setHomeIconId(entity.getId());
				goods.setSkuId(item);
				goods.setSort(sorts.get(i));
				goodsList.add(goods);
				i++;
			}
			if (goodsList.size() > 0) {
				homeIconGoodsService.insertMore(goodsList);
			}
		}
		
		//插入管理版本信息数据
		if(CollectionUtils.isNotEmpty(versions)) {
		    List<ColumnHomeIconVersion> iconVersions = new ArrayList<>();
		    ColumnHomeIconVersion iconVersion = null;
		    for(String version : versions) {
		        iconVersion = new ColumnHomeIconVersion();
		        iconVersion.setId(UuidUtils.getUuid());
		        iconVersion.setIconId(entity.getId());
		        iconVersion.setVersion(version);
		        
		        iconVersions.add(iconVersion);
		    }
		    
		    if(iconVersions.size() > 0) {
		        this.homeIconVersionMapper.insertBatch(iconVersions);
		    }
		}
		
		return new BaseResult();
	}
	
	@Override
	public BaseResult save(ColumnHomeIcon entity, List<ColumnSelectArea> areaList, List<String> goodsIds,
			List<Integer> sorts, List<String> versions, List<ColumnHomeIconClassifyDto> classifyList) throws Exception {
		save(entity, areaList, goodsIds, sorts, versions);
		if(CollectionUtils.isNotEmpty(classifyList)){
			columnHomeIconClassifyService.addClassifyBatch(entity.getId(),classifyList);
		}
		return new BaseResult();
	}
	
	private boolean isRepeatArea(String homeIconId, SelectAreaType taskScope, HomeIconPlace place,
			List<ColumnSelectArea> areaList, List<String> versions) throws Exception {
		// 查询是否已经存在使用了相同的ICON位置数据
		HomeIconParamDto paramDto = new HomeIconParamDto();
		//设置ICON的版本号
		paramDto.setVersions(versions);
		
		paramDto.setPlace(place);
		// 设置排除自己
		paramDto.setExcludeId(homeIconId);
		paramDto.setTaskScope(SelectAreaType.nationwide);
		// 根据位置查询设置范围为全国的数据
		List<ColumnHomeIcon> list = homeIconMapper.findList(paramDto);
		if (null != list && list.size() > 0) {
			// 如果同一位置中有设置范围是全国的则表示已经重复
			return true;
		}
		// 清空参数中的地区范围
		paramDto.setTaskScope(null);
		// 查询位置设置范围为各省和城市的数据
		list = homeIconMapper.findList(paramDto);
		// 未查出数据则表示当前ICON位置还未发布过
		if (null == list || list.size() == 0) {
			return false;
		}
		// 如果当前设置任务范围是全国
		if (SelectAreaType.nationwide.equals(taskScope)) {
			return true;
		}
		List<String> columnIds = new ArrayList<>();
		for (ColumnHomeIcon item : list) {
			columnIds.add(item.getId());
		}
		// 已保存省ID集合(全省)
		List<String> dbProvinceIds = new ArrayList<>();
		// 已保存省ID集合(部分城市)
		List<String> dbPartProvinceIds = new ArrayList<>();
		// 已保存选城市ID集合
		List<String> dbCityIds = new ArrayList<>();
		// 查询已存在的
		List<ColumnSelectArea> dbAreList = selectAreaService.findListByColumnIds(columnIds);
		// 将已经存在省、城市ID放入集合中
		for (ColumnSelectArea item : dbAreList) {
			if (SelectAreaType.province.equals(item.getAreaType())) {
				dbProvinceIds.add(item.getProvinceId());
			} else {
				dbPartProvinceIds.add(item.getProvinceId());
				dbCityIds.add(item.getCityId());
			}
		}
		// 和已经保存区域进行判断是否有重复或者是交集
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

    @Override
    public List<ColumnHomeIconVersionDto> findIconVersionByIconId(String iconId) throws Exception {
        return this.homeIconVersionMapper.findListByIconId(iconId);
    }

}
