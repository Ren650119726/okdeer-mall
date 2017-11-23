
package com.okdeer.mall.operate.advert.api;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.advert.dto.ColumnAdvertDto;
import com.okdeer.mall.advert.dto.ColumnAdvertQueryParamDto;
import com.okdeer.mall.advert.entity.AdvertPosition;
import com.okdeer.mall.advert.entity.ColumnAdvert;
import com.okdeer.mall.advert.entity.ColumnAdvertArea;
import com.okdeer.mall.advert.enums.AdvertTypeEnum;
import com.okdeer.mall.advert.service.ColumnAdvertApi;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.operate.advert.bo.ColumnAdvertAreaParamBo;
import com.okdeer.mall.operate.advert.bo.ColumnAdvertShowRecordParamBo;
import com.okdeer.mall.operate.advert.bo.ColumnAdvertVersionParamBo;
import com.okdeer.mall.operate.advert.entity.ColumnAdvertShowRecord;
import com.okdeer.mall.operate.advert.service.AdvertPositionService;
import com.okdeer.mall.operate.advert.service.ColumnAdvertAreaService;
import com.okdeer.mall.operate.advert.service.ColumnAdvertService;
import com.okdeer.mall.operate.advert.service.ColumnAdvertShowRecordService;
import com.okdeer.mall.operate.entity.ColumnAdvertVersion;
import com.okdeer.mall.operate.service.ColumnAdvertVersionService;

/**
 * ClassName: ColumnAdvertApiImpl
 * 
 * @Description: 广告api
 * @author zengjizu
 * @date 2017年1月3日
 *
 *       =======================================================================
 *       ========================== Task ID Date Author Description
 *       ----------------+----------------+-------------------+-----------------
 *       --------------------------
 *
 */
@Service(interfaceName = "com.okdeer.mall.advert.service.ColumnAdvertApi", version = "1.0.0")
public class ColumnAdvertApiImpl implements ColumnAdvertApi {

	@Autowired
	private ColumnAdvertService columnAdvertService;

	@Autowired
	private AdvertPositionService advertPositionService;

	@Autowired
	private ColumnAdvertAreaService columnAdvertAreaService;

	@Autowired
	private ColumnAdvertVersionService columnAdvertVersionService;

	@Autowired
	private ColumnAdvertShowRecordService columnAdvertShowRecordService;

	@Override
	public List<ColumnAdvertDto> findForApp(ColumnAdvertQueryParamDto advertQueryParamDto) {
		Assert.notNull(advertQueryParamDto.getAdvertType(), "广告类型不能为空");
		AdvertPosition advertPosition = advertPositionService.findByType(advertQueryParamDto.getAdvertType());
		advertQueryParamDto.setPositionId(advertPosition.getId());
		List<ColumnAdvert> list = columnAdvertService.findList(advertQueryParamDto);
		if (CollectionUtils.isEmpty(list)) {
			return Lists.newArrayList();
		}
		// 根据区域进行过滤数据
		list = filterByArea(advertQueryParamDto, list);
		// 根据版本进行过滤
		list = filterByVersion(advertQueryParamDto, list);
		// 根据显示记录来过滤
		list = filterByShowRecord(advertQueryParamDto, list);
		return BeanMapper.mapList(list, ColumnAdvertDto.class);
	}

	private List<ColumnAdvert> filterByShowRecord(ColumnAdvertQueryParamDto advertQueryParamDto,
			List<ColumnAdvert> list) {
		if (CollectionUtils.isEmpty(list)) {
			return list;
		}
		// 是否需要记录显示日志
		boolean isNeedRecord = isNeedShowRecord(advertQueryParamDto.getAdvertType());
		if (isNeedRecord) {
			List<ColumnAdvert> resultList = Lists.newArrayList();
			Assert.hasText(advertQueryParamDto.getDeviceNo(), "设备号不能为空");
			ColumnAdvertShowRecordParamBo columnAdvertShowRecordParamBo = new ColumnAdvertShowRecordParamBo();
			columnAdvertShowRecordParamBo.setDeviceNo(advertQueryParamDto.getDeviceNo());
			for (ColumnAdvert columnAdvert : list) {
				if(isShow(columnAdvert, columnAdvertShowRecordParamBo)){
					resultList.add(columnAdvert);
				}
			}
			addShowRecord(advertQueryParamDto, resultList);
			return resultList;
		} else {
			return list;
		}

	}
	
	/**
	 * @Description: 是否显示广告
	 * @param columnAdvert
	 * @param columnAdvertShowRecordParamBo
	 * @return
	 * @author zengjizu
	 * @date 2017年11月23日
	 */
	private boolean isShow(ColumnAdvert columnAdvert, ColumnAdvertShowRecordParamBo columnAdvertShowRecordParamBo) {
		columnAdvertShowRecordParamBo.setAdvertId(columnAdvert.getId());
		if (columnAdvert.getUserLimitType() == 1) {
			// 判断是否超过设备总次数限制
			if (columnAdvert.getDeviceAllLimit() != null && columnAdvert.getDeviceAllLimit() > 0
					&& queryColumnAdvertShowRecord(columnAdvertShowRecordParamBo) >= columnAdvert.getDeviceAllLimit()) {
				return false;
			}

			// 判断是否超过了总次数限制
			if (columnAdvert.getDeviceDayLimit() != null && columnAdvert.getDeviceDayLimit() > 0
					&& todayColumnAdvertShowRecord(columnAdvertShowRecordParamBo) >= columnAdvert.getDeviceDayLimit()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @Description: 根据参数查询广告显示次数
	 * @param columnAdvertShowRecordParamBo
	 * @return
	 * @author zengjizu
	 * @date 2017年11月23日
	 */
	private int queryColumnAdvertShowRecord(ColumnAdvertShowRecordParamBo columnAdvertShowRecordParamBo) {
		return columnAdvertShowRecordService.findCountByParam(columnAdvertShowRecordParamBo);
	}

	/**
	 * @Description: 今日广告显示次数
	 * @param columnAdvertShowRecordParamBo
	 * @return
	 * @author zengjizu
	 * @date 2017年11月23日
	 */
	private int todayColumnAdvertShowRecord(ColumnAdvertShowRecordParamBo columnAdvertShowRecordParamBo) {
		Date currentTime = new Date();
		columnAdvertShowRecordParamBo.setStartCreateTime(DateUtils.formatDate(currentTime, "yyyy-MM-dd 00:00:00"));
		columnAdvertShowRecordParamBo.setEndCreateTime(DateUtils.formatDate(currentTime, "yyyy-MM-dd 23:59:59"));
		return queryColumnAdvertShowRecord(columnAdvertShowRecordParamBo);
	}

	private void addShowRecord(ColumnAdvertQueryParamDto advertQueryParamDto, List<ColumnAdvert> list) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		List<ColumnAdvertShowRecord> saveList = Lists.newArrayList();
		for (ColumnAdvert columnAdvert : list) {
			ColumnAdvertShowRecord columnAdvertShowRecord = new ColumnAdvertShowRecord();
			columnAdvertShowRecord.setDeviceNo(advertQueryParamDto.getDeviceNo());
			columnAdvertShowRecord.setAdvertId(columnAdvert.getId());
			saveList.add(columnAdvertShowRecord);
		}
		columnAdvertShowRecordService.save(saveList);
	}

	/**
	 * @Description: 是否需要记录显示记录
	 * @param advertType
	 * @return
	 * @author zengjizu
	 * @date 2017年11月11日
	 */
	private boolean isNeedShowRecord(AdvertTypeEnum advertType) {
		return advertType == AdvertTypeEnum.APP_BOMB_SCREEN;
	}

	private List<ColumnAdvert> filterByVersion(ColumnAdvertQueryParamDto advertQueryParamDto, List<ColumnAdvert> list) {
		if (CollectionUtils.isEmpty(list)) {
			return list;
		}
		if (!isNeedFilterByVersion(advertQueryParamDto.getAdvertType())) {
			return list;
		}
		List<String> advertIdList = Lists.newArrayList();
		for (ColumnAdvert columnAdvert : list) {
			advertIdList.add(columnAdvert.getId());
		}
		ColumnAdvertVersionParamBo columnAdvertVersionParamBo = new ColumnAdvertVersionParamBo();
		columnAdvertVersionParamBo.setAdvertIdList(advertIdList);
		columnAdvertVersionParamBo.setType(advertQueryParamDto.getClientType().getCode());
		columnAdvertVersionParamBo.setVersion(advertQueryParamDto.getVersion());
		List<ColumnAdvertVersion> versionList = columnAdvertVersionService.findList(columnAdvertVersionParamBo);
		List<String> versionAdvertList = Lists.newArrayList();
		for (ColumnAdvertVersion columnAdvertVersion : versionList) {
			if (!versionAdvertList.contains(columnAdvertVersion.getAdvertId())) {
				versionAdvertList.add(columnAdvertVersion.getAdvertId());
			}
		}

		List<ColumnAdvert> filterList = Lists.newArrayList();
		for (ColumnAdvert columnAdvert : list) {
			if (versionAdvertList.contains(columnAdvert.getId())) {
				filterList.add(columnAdvert);
			}
		}
		return filterList;
	}

	private boolean isNeedFilterByVersion(AdvertTypeEnum advertType) {
		switch (advertType) {
			case WX_INDEX_BANNER:
			case POS_MACHINE:
			case APP_BOMB_SCREEN:
				return false;
			default:
				break;
		}
		return true;
	}

	private List<ColumnAdvert> filterByArea(ColumnAdvertQueryParamDto advertQueryParamDto, List<ColumnAdvert> list) {
		if (CollectionUtils.isEmpty(list)) {
			return list;
		}
		List<String> advertIdList = Lists.newArrayList();
		for (ColumnAdvert columnAdvert : list) {
			advertIdList.add(columnAdvert.getId());
		}
		ColumnAdvertAreaParamBo columnAdvertAreaParamBo = new ColumnAdvertAreaParamBo();
		columnAdvertAreaParamBo.setAdvertIdList(advertIdList);
		columnAdvertAreaParamBo.setType(1);
		columnAdvertAreaParamBo.setAreaId(advertQueryParamDto.getProvinceId());
		List<ColumnAdvertArea> areaList = columnAdvertAreaService.findList(columnAdvertAreaParamBo);
		List<String> areaExistsAdvertIdList = Lists.newArrayList();
		collectAdvertByArea(areaList, areaExistsAdvertIdList);

		columnAdvertAreaParamBo.setType(0);
		columnAdvertAreaParamBo.setAreaId(advertQueryParamDto.getCityId());
		areaList = columnAdvertAreaService.findList(columnAdvertAreaParamBo);
		collectAdvertByArea(areaList, areaExistsAdvertIdList);

		List<ColumnAdvert> filterList = Lists.newArrayList();
		for (ColumnAdvert columnAdvert : list) {
			if (columnAdvert.getAreaType() == AreaType.national) {
				filterList.add(columnAdvert);
			} else if (columnAdvert.getAreaType() == AreaType.area
					&& areaExistsAdvertIdList.contains(columnAdvert.getId())) {
				filterList.add(columnAdvert);
			}
		}
		return filterList;
	}

	private void collectAdvertByArea(List<ColumnAdvertArea> areaList, List<String> areaExistsAdvertIdList) {
		for (ColumnAdvertArea columnAdvertArea : areaList) {
			if (!areaExistsAdvertIdList.contains(columnAdvertArea.getAdvertId())) {
				areaExistsAdvertIdList.add(columnAdvertArea.getAdvertId());
			}
		}
	}

	@Override
	public List<ColumnAdvertDto> findForAppV220(ColumnAdvertQueryParamDto advertQueryParamDto) {
		AdvertPosition advertPosition = advertPositionService.findByType(advertQueryParamDto.getAdvertType());
		advertQueryParamDto.setPositionId(advertPosition.getId());
		List<ColumnAdvert> list = columnAdvertService.findForAppV220(advertQueryParamDto);
		if (list != null) {
			List<ColumnAdvertDto> dtoList = BeanMapper.mapList(list, ColumnAdvertDto.class);
			return dtoList;
		}
		return null;
	}

	@Override
	public List<ColumnAdvertDto> findForWx(ColumnAdvertQueryParamDto advertQueryParamDto) {
		AdvertPosition advertPosition = advertPositionService.findByType(advertQueryParamDto.getAdvertType());
		advertQueryParamDto.setPositionId(advertPosition.getId());
		List<ColumnAdvert> list = columnAdvertService.findForWx(advertQueryParamDto);
		if (list != null) {
			List<ColumnAdvertDto> dtoList = BeanMapper.mapList(list, ColumnAdvertDto.class);
			return dtoList;
		}
		return null;
	}
}
