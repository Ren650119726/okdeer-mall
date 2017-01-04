/** 
 *@Project: okdeer-mall-activity 
 *@Author: tangzj02
 *@Date: 2016年12月29日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.api;

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
import com.okdeer.mall.operate.dto.AppRecommendDto;
import com.okdeer.mall.operate.dto.AppRecommendGoodsDto;
import com.okdeer.mall.operate.dto.AppRecommendGoodsParamDto;
import com.okdeer.mall.operate.dto.AppRecommendParamDto;
import com.okdeer.mall.operate.dto.SelectAreaDto;
import com.okdeer.mall.operate.entity.ColumnAppRecommend;
import com.okdeer.mall.operate.entity.ColumnAppRecommendGoods;
import com.okdeer.mall.operate.entity.ColumnSelectArea;
import com.okdeer.mall.operate.enums.AppRecommendStatus;
import com.okdeer.mall.operate.enums.ColumnType;
import com.okdeer.mall.operate.enums.SelectAreaType;
import com.okdeer.mall.operate.service.ColumnAppRecommendApi;
import com.okdeer.mall.operate.service.ColumnAppRecommendGoodsService;
import com.okdeer.mall.operate.service.ColumnAppRecommendService;
import com.okdeer.mall.operate.service.ColumnSelectAreaService;

/**
 * ClassName: ColumnAppRecommendApiImpl 
 * @Description: APP端服务商品推荐服务
 * @author tangzj02
 * @date 2016年12月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-12-29        tangzj02                     添加
 */

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.operate.service.ColumnAppRecommendApi")
public class ColumnAppRecommendApiImpl implements ColumnAppRecommendApi {

	/** 日志记录 */
	private static final Logger log = LoggerFactory.getLogger(ColumnAppRecommendApiImpl.class);

	@Autowired
	private ColumnAppRecommendService appRecommendService;

	@Autowired
	private ColumnAppRecommendGoodsService appRecommendGoodsService;

	@Autowired
	private ColumnSelectAreaService selectAreaService;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#findList(com.okdeer.mall.operate.dto.AppRecommendParamDto)
	 */
	@Override
	public List<AppRecommendDto> findList(AppRecommendParamDto paramDto) throws Exception {
		log.info("查询APP端服务商品推荐列表参数:{}", paramDto);
		List<ColumnAppRecommend> sourceList = appRecommendService.findList(paramDto);
		List<AppRecommendDto> dtoList = null;
		if (null == sourceList) {
			dtoList = new ArrayList<AppRecommendDto>();
		} else {
			dtoList = BeanMapper.mapList(sourceList, AppRecommendDto.class);
		}
		return dtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#findById(java.lang.String)
	 */
	@Override
	public AppRecommendDto findById(String id) throws Exception {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		// 查询APP端服务商品推荐信息
		ColumnAppRecommend recommend = appRecommendService.findById(id);
		if (null == recommend) {
			return null;
		}
		// 复制APP端服务商品推荐信息
		AppRecommendDto dto = BeanMapper.map(recommend, AppRecommendDto.class);
		return dto;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#findAppRecommendGoodsDtoListByRecommendId(java.lang.String)
	 */
	@Override
	public List<AppRecommendGoodsDto> findAppRecommendGoodsDtoListByRecommendId(String recommendId) throws Exception {
		if (StringUtils.isBlank(recommendId)) {
			return null;
		}
		List<ColumnAppRecommendGoods> goodsList = appRecommendGoodsService.findListByRecommendId(recommendId);
		List<AppRecommendGoodsDto> goodsDtoList = null;
		if (null == goodsList) {
			goodsDtoList = new ArrayList<>();
		} else {
			goodsDtoList = BeanMapper.mapList(goodsList, AppRecommendGoodsDto.class);
		}
		return goodsDtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#findAppRecommendGoodsDtoList(com.okdeer.mall.operate.dto.AppRecommendGoodsParamDto)
	 */
	@Override
	public List<AppRecommendGoodsDto> findAppRecommendGoodsDtoList(AppRecommendGoodsParamDto paramDto)
			throws Exception {
		List<ColumnAppRecommendGoods> goodsList = appRecommendGoodsService.findList(paramDto);
		List<AppRecommendGoodsDto> goodsDtoList = null;
		if (null == goodsList) {
			goodsDtoList = new ArrayList<>();
		} else {
			goodsDtoList = BeanMapper.mapList(goodsList, AppRecommendGoodsDto.class);
		}
		return goodsDtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#save(com.okdeer.mall.operate.dto.AppRecommendDto)
	 */
	@Override
	public BaseResult save(AppRecommendDto dto) throws Exception {
		if (dto == null) {
			return new BaseResult("ActivityAppRecommendDto信息不能为空");
		}

		if (null == dto.getPlace() || null == dto.getAreaType()
				|| !StringUtils.isNotEmptyAll(dto.getTitle(), dto.getCoverPicUrl())) {
			return new BaseResult("ActivityAppRecommendDto信息不完整");
		}

		if (SelectAreaType.city.equals(dto.getAreaType())
				&& (null == dto.getAreaList() || 0 == dto.getAreaList().size())) {
			return new BaseResult("按城市选择任务范围时， 区域不允许为空");
		}

		if (null == dto.getGoodsList() || 0 == dto.getGoodsList().size()) {
			return new BaseResult("关联商品不允许为空");
		}

		// 复制属性信息
		ColumnAppRecommend entity = BeanMapper.map(dto, ColumnAppRecommend.class);
		String recommendId = StringUtils.isBlank(entity.getId()) ? UuidUtils.getUuid() : entity.getId();
		// 修改首页ICON
		if (StringUtils.isNotBlank(entity.getId())) {
			// 删除之前的插入的关联数据
			selectAreaService.deleteByColumnId(entity.getId());
			appRecommendGoodsService.deleteByRecommendId(entity.getId());
			appRecommendService.update(entity);
		} else {
			entity.setId(recommendId);
			selectAreaService.add(entity);
		}

		for (AppRecommendGoodsDto item : dto.getGoodsList()) {
			item.setId(UuidUtils.getUuid());
			item.setRecommendId(recommendId);
		}
		List<ColumnAppRecommendGoods> goodsList = BeanMapper.mapList(dto.getGoodsList(), ColumnAppRecommendGoods.class);
		appRecommendGoodsService.insertMore(goodsList);

		if (SelectAreaType.city.equals(dto.getAreaType())) {
			for (SelectAreaDto item : dto.getAreaList()) {
				item.setId(UuidUtils.getUuid());
				item.setColumnId(recommendId);
				item.setAreaType(SelectAreaType.city);
				item.setColumnType(ColumnType.appRecommend.ordinal());
			}
			List<ColumnSelectArea> areaList = BeanMapper.mapList(dto.getAreaList(), ColumnSelectArea.class);
			selectAreaService.insertMore(areaList);
		}
		return new BaseResult();
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#closeById(java.lang.String)
	 */
	@Override
	public int closeById(String id) throws Exception {
		if (StringUtils.isBlank(id)) {
			return 0;
		}
		ColumnAppRecommend entity = new ColumnAppRecommend();
		entity.setId(id);
		entity.setStatus(AppRecommendStatus.close);
		return appRecommendService.update(entity);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#updateById(java.lang.String, java.lang.Integer)
	 */
	@Override
	public int updateById(String id, Integer sort) throws Exception {
		if (StringUtils.isBlank(id) || null == sort) {
			return 0;
		}
		ColumnAppRecommend entity = new ColumnAppRecommend();
		entity.setId(id);
		entity.setSort(sort);
		return appRecommendService.update(entity);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#deleteByIds(java.util.List)
	 */
	@Override
	public int deleteByIds(List<String> ids) throws Exception {
		if (null == ids || ids.size() == 0) {
			return 0;
		}
		return appRecommendService.deleteByIds(ids);
	}

	@Override
	public PageUtils<AppRecommendDto> findListPage(AppRecommendParamDto paramDto) throws Exception {
		return appRecommendService.findListPage(paramDto);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendApi#findListByCityId(java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public List<AppRecommendDto> findListByCityId(String provinceId, String cityId, Integer place) throws Exception {
		if (!StringUtils.isNotEmptyAll(provinceId, cityId) || null == place) {
			return new ArrayList<AppRecommendDto>();
		}
		// 根据城市查询相应的服务商品推荐栏位
		List<String> ids = selectAreaService.findColumnIdsByCity(cityId, provinceId, ColumnType.appRecommend.ordinal());
		if (null == ids || ids.size() == 0) {
			return new ArrayList<AppRecommendDto>();
		}
		// 设置服务商品推荐参数
		AppRecommendParamDto paramDto = new AppRecommendParamDto();
		paramDto.setIds(ids);
		paramDto.setPlace(place);
		// 查询服务商品推荐
		List<AppRecommendDto> dtoList = findList(paramDto);
		// 设置推荐服务商品关联信息查询参数
		AppRecommendGoodsParamDto goodsParamDto = new AppRecommendGoodsParamDto();
		goodsParamDto.setRecommendIds(ids);
		// 查询推荐服务商品关联信息
		List<ColumnAppRecommendGoods> goodsList = appRecommendGoodsService.findList(goodsParamDto);
		List<AppRecommendGoodsDto> goodsDtoList = null;
		if (null == goodsList) {
			goodsDtoList = new ArrayList<>();
		} else {
			goodsDtoList = BeanMapper.mapList(goodsList, AppRecommendGoodsDto.class);
		}
		// 组装数据
		for (AppRecommendDto dto : dtoList) {
			// 初始化服务商品推荐中中的商品关联集合
			if (null == dto.getGoodsList()) {
				dto.setGoodsList(new ArrayList<>());
			}
			for (AppRecommendGoodsDto goodsDto : goodsDtoList) {
				if (dto.getId().equals(goodsDto.getRecommendId())) {
					dto.getGoodsList().add(goodsDto);
				}
			}
		}
		return dtoList;
	}
}
