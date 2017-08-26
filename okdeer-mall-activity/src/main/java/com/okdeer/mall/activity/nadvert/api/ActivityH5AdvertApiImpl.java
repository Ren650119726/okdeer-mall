package com.okdeer.mall.activity.nadvert.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.nadvert.bo.ActivityH5AdvertBo;
import com.okdeer.mall.activity.nadvert.bo.ActivityH5AdvertContentBo;
import com.okdeer.mall.activity.nadvert.dto.ActivityH5AdvertContentDto;
import com.okdeer.mall.activity.nadvert.dto.ActivityH5AdvertContentGoodsDto;
import com.okdeer.mall.activity.nadvert.dto.ActivityH5AdvertDto;
import com.okdeer.mall.activity.nadvert.dto.ActivityH5AdvertRoleDto;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5Advert;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentGoods;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertRole;
import com.okdeer.mall.activity.nadvert.param.ActivityH5AdvertQParam;
import com.okdeer.mall.activity.nadvert.service.ActivityH5AdvertContentGoodsService;
import com.okdeer.mall.activity.nadvert.service.ActivityH5AdvertService;

/**
 * ClassName: ActivityH5AdvertApiImpl 
 * @Description: h5活动对外服务实现
 * @author mengsj
 * @date 2017年8月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
 
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.nadvert.api.ActivityH5AdvertApi")
public class ActivityH5AdvertApiImpl implements ActivityH5AdvertApi {
	@Autowired
	private ActivityH5AdvertService advertService;
	@Autowired
	private ActivityH5AdvertContentGoodsService contentGoodsService;
	@Override
	public void save(ActivityH5AdvertDto entity) throws Exception {
		advertService.save(convertDtoToBo(entity));
	}
	
	/**
	 * @Description: 将dto转化为bo对象
	 * @param dto
	 * @return ActivityH5AdvertBo
	 * @throws
	 * @author mengsj
	 * @date 2017年8月12日
	 */
	private ActivityH5AdvertBo convertDtoToBo(ActivityH5AdvertDto dto){
		ActivityH5Advert advert = BeanMapper.map(dto, ActivityH5Advert.class);
		List<ActivityH5AdvertRole> roles = BeanMapper.mapList(dto.getRoleDtos(), ActivityH5AdvertRole.class);
		List<ActivityH5AdvertContentBo> contents = new ArrayList<ActivityH5AdvertContentBo>();
		
		List<ActivityH5AdvertContentDto> contentDtos = dto.getContentDtos();
		if(CollectionUtils.isNotEmpty(contentDtos)){
			contentDtos.forEach(obj -> {
				ActivityH5AdvertContentBo bo = new ActivityH5AdvertContentBo();
				bo.convertDtoToBo(obj);
				contents.add(bo);
			});
		}
		ActivityH5AdvertBo bo = new ActivityH5AdvertBo();
		bo.setAdvert(advert);
		bo.setRoles(roles);
		bo.setContents(contents);
		return bo;
	}
	
	/**
	 * @Description: 将bo转化为dto对象
	 * @param dto
	 * @return ActivityH5AdvertBo
	 * @throws
	 * @author mengsj
	 * @date 2017年8月12日
	 */
	private ActivityH5AdvertDto convertBoToDto(ActivityH5AdvertBo bo){
		ActivityH5AdvertDto advert = BeanMapper.map(bo.getAdvert(), ActivityH5AdvertDto.class);
		List<ActivityH5AdvertRoleDto> roles = BeanMapper.mapList(bo.getRoles(), ActivityH5AdvertRoleDto.class);
		
		List<ActivityH5AdvertContentDto> contents = new ArrayList<ActivityH5AdvertContentDto>();
		
		List<ActivityH5AdvertContentBo> contentDtos = bo.getContents();
		
		if(CollectionUtils.isNotEmpty(contentDtos)){
			contentDtos.forEach(obj -> {
				contents.add(obj.convertBoToDto());
			});
		};
		advert.setRoleDtos(roles);
		advert.setContentDtos(contents);
		return advert;
	}

	@Override
	public void update(ActivityH5AdvertDto entity) throws Exception {
		advertService.update(convertDtoToBo(entity));
	}

	@Override
	public ActivityH5AdvertDto findById(String id) {
		return convertBoToDto(advertService.findById(id));
	}

	@Override
	public void deleteById(String id) throws Exception {
		advertService.deleteById(id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public PageUtils<ActivityH5AdvertDto> findByParam(ActivityH5AdvertQParam param,
			Integer pageNumber, Integer pageSize) {
		PageUtils<ActivityH5Advert> page = advertService.findByParam(param, pageNumber, pageSize);
		return page.toBean(ActivityH5AdvertDto.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public PageUtils<ActivityH5AdvertContentGoodsDto> findBldGoodsByActivityId(
			String storeId, String activityId, String contentId,
			Integer pageNumber, Integer pageSize) {
		PageUtils<ActivityH5AdvertContentGoods> page = contentGoodsService.findBldGoodsByActivityId(storeId, activityId, contentId, pageNumber, pageSize);
		return  page.toBean(ActivityH5AdvertContentGoodsDto.class);
	}
}
