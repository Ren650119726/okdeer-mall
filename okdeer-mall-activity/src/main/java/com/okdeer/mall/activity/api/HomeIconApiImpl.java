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
import com.okdeer.bdp.address.service.IAddressService;
import com.okdeer.common.utils.BaseResult;
import com.okdeer.mall.activity.dto.ActivitySelectAreaDto;
import com.okdeer.mall.activity.dto.HomeIconDto;
import com.okdeer.mall.activity.dto.HomeIconGoodsDto;
import com.okdeer.mall.activity.dto.HomeIconParamDto;
import com.okdeer.mall.activity.entity.ActivitySelectArea;
import com.okdeer.mall.activity.entity.HomeIcon;
import com.okdeer.mall.activity.entity.HomeIconGoods;
import com.okdeer.mall.activity.enums.HomeIconTaskType;
import com.okdeer.mall.activity.enums.SelectAreaType;
import com.okdeer.mall.activity.service.ActivitySelectAreaService;
import com.okdeer.mall.activity.service.HomeIconApi;
import com.okdeer.mall.activity.service.HomeIconGoodsService;
import com.okdeer.mall.activity.service.HomeIconService;

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
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.service.HomeIconApi")
public class HomeIconApiImpl implements HomeIconApi {

	/** 日志记录 */
	private static final Logger log = LoggerFactory.getLogger(HomeIconApiImpl.class);

	@Autowired
	private HomeIconService homeIconService;

	@Autowired
	private HomeIconGoodsService homeIconGoodsService;

	@Autowired
	private ActivitySelectAreaService activitySelectAreaService;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.HomeIconApi#findList(com.okdeer.mall.activity.dto.HomeIconParamDto)
	 */
	@Override
	public List<HomeIconDto> findList(HomeIconParamDto paramDto) throws Exception {
		log.info("查询首页IOCN列表参数:{}", paramDto);
		List<HomeIcon> sourceList = homeIconService.findList(paramDto);
		List<HomeIconDto> dtoList = null;
		if (null == sourceList) {
			dtoList = new ArrayList<HomeIconDto>();
		} else {
			dtoList = BeanMapper.mapList(sourceList, HomeIconDto.class);
		}
		return dtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.HomeIconApi#findHomeIconProductDtoListByHomeIcon(java.lang.String)
	 */
	@Override
	public List<HomeIconGoodsDto> findHomeIconGoodsDtoListByHomeIcon(String iconId) throws Exception {
		List<HomeIconGoodsDto> dtoList = new ArrayList<>();
		if (StringUtils.isNotBlank(iconId)) {
			List<HomeIconGoods> sourceList = homeIconGoodsService.findListByHomeIcon(iconId);
			if (sourceList != null) {
				dtoList = BeanMapper.mapList(sourceList, HomeIconGoodsDto.class);
			}
		}
		return dtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.HomeIconApi#findActivitySelectAreaDtoListByActivityId(java.lang.String)
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
	 * @see com.okdeer.mall.activity.service.HomeIconApi#findById(java.lang.String)
	 */
	@Override
	public HomeIconDto findById(String homeIconId) throws Exception {
		if (StringUtils.isBlank(homeIconId)) {
			return null;
		}
		return homeIconService.findById(homeIconId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.HomeIconApi#deleteById(java.lang.String)
	 */
	@Override
	public BaseResult deleteById(String iconId) throws Exception {
		if (StringUtils.isBlank(iconId)) {
			return new BaseResult("homeIconId不能为空");
		}
		int result = homeIconService.delete(iconId);
		if (result > 0) {
			return new BaseResult();
		}
		return new BaseResult("删除失败");
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.HomeIconApi#save(com.okdeer.mall.activity.dto.HomeIconDto)
	 */
	@Override
	public BaseResult save(HomeIconDto dto) throws Exception {
		if (dto == null) {
			return new BaseResult("HomeIconDto信息不能为空");
		}
		if (null == dto.getPlace() || null == dto.getTaskScope() || null == dto.getTaskType()
				|| StringUtils.isNotEmptyAll(dto.getName(), dto.getIconUrl())) {
			return new BaseResult("HomeIconDto信息不完整");
		}

		if (SelectAreaType.city.getCode().equals(dto.getTaskScope())
				&& (null == dto.getAreaIds() || 0 == dto.getAreaIds().size())) {
			return new BaseResult("城市ID集合 当任务范围未1:按城市选择任务范围时， 不允许为空");
		}

		if (HomeIconTaskType.goods.getCode().equals(dto.getTaskType())
				&& (null == dto.getProductIds() || 0 == dto.getProductIds().size())) {
			return new BaseResult("商品ID集合   当任务内容  0:指定指定商品推荐时， 不允许为空");
		}

		// TODO 需要判断是否已经存在同一区域、同一位置的数据

		// 复制属性信息
		HomeIcon entity = BeanMapper.map(dto, HomeIcon.class);
		if (StringUtils.isNotBlank(entity.getId())) {
			// 删除之前的插入的关联数据
			activitySelectAreaService.deleteByActivityId(entity.getId());
			homeIconGoodsService.deleteByHomeIconId(entity.getId());
			homeIconService.update(entity);
		} else {
			// 设置ID
			entity.setId(UuidUtils.getUuid());
			homeIconService.add(entity);
		}

		// 插入任务范围关联数据
		if (SelectAreaType.city.getCode().equals(dto.getTaskScope())) {
			List<ActivitySelectArea> areaList = new ArrayList<>();
			ActivitySelectArea Area = null;
			for (String item : dto.getAreaIds()) {
				Area = new ActivitySelectArea();
				Area.setId(UuidUtils.getUuid());
				Area.setActivityId(entity.getId());
				Area.setAreaId(item);
				areaList.add(Area);
			}
			if (areaList.size() > 0) {
				activitySelectAreaService.insertMore(areaList);
			}
		}

		// 插入任务类型关联数据
		if (HomeIconTaskType.goods.getCode().equals(dto.getTaskType())) {
			List<HomeIconGoods> productList = new ArrayList<>();
			HomeIconGoods product = null;
			for (String item : dto.getProductIds()) {
				product = new HomeIconGoods();
				product.setId(UuidUtils.getUuid());
				product.setIconId(entity.getId());
				product.setGoodsId(item);
				productList.add(product);
			}
			if (productList.size() > 0) {
				homeIconGoodsService.insertMore(productList);
			}
		}
		return new BaseResult();
	}

}
