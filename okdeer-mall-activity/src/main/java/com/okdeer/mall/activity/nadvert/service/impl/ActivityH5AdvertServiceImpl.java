package com.okdeer.mall.activity.nadvert.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.nadvert.bo.ActivityH5AdvertBo;
import com.okdeer.mall.activity.nadvert.bo.ActivityH5AdvertContentBo;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5Advert;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertRole;
import com.okdeer.mall.activity.nadvert.mapper.ActivityH5AdvertMapper;
import com.okdeer.mall.activity.nadvert.param.ActivityH5AdvertQParam;
import com.okdeer.mall.activity.nadvert.service.ActivityH5AdvertContentService;
import com.okdeer.mall.activity.nadvert.service.ActivityH5AdvertRoleService;
import com.okdeer.mall.activity.nadvert.service.ActivityH5AdvertService;

/**
 * ClassName: ActivityH5AdvertServiceImpl 
 * @Description: H5广告活动服务实现
 * @author mengsj
 * @date 2017年8月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
 
@Service
public class ActivityH5AdvertServiceImpl implements ActivityH5AdvertService {
	
	@Autowired
	private ActivityH5AdvertMapper mapper;
	/**
	 * @Fields roleService : 活动规则
	 */
	@Autowired
	private ActivityH5AdvertRoleService roleService;
	/**
	 * @Fields contentService : 活动内容
	 */
	@Autowired
	private ActivityH5AdvertContentService contentService;

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void save(ActivityH5AdvertBo bo) throws Exception {
		//这是h5活动id
		bo.getAdvert().setId(UuidUtils.getUuid());
		//保存h5活动基本信息
		ActivityH5Advert entity = bo.getAdvert();
		entity.setId(UuidUtils.getUuid());
		entity.setCreateTime(new Date());
		entity.setUpdateTime(entity.getCreateTime());
		entity.setUpdateUserId(entity.getCreateUserId());
		mapper.add(entity);
		//保存h5活动规则和内容
		saveRoleAndContent(bo);
	}
	
	private void saveRoleAndContent(ActivityH5AdvertBo bo) throws Exception{
		//保存活动规则
		List<ActivityH5AdvertRole> roles = bo.getRoles();
		if(CollectionUtils.isNotEmpty(roles)){
			roles.forEach(obj -> {
				obj.setActivityId(bo.getAdvert().getId());
				obj.setCreateUserId(bo.getAdvert().getUpdateUserId());
				obj.setCreateTime(new Date());
			});
			roleService.saveBatch(roles);
		}
		//保存活动内容
		List<ActivityH5AdvertContentBo> contents =  bo.getContents();
		if(CollectionUtils.isNotEmpty(contents)){
			contents.forEach(obj -> {
				obj.getContent().setCreateUserId(bo.getAdvert().getUpdateUserId());
				obj.getContent().setActivityId(bo.getAdvert().getId());
			});
			contentService.batchSave(contents);
		}
	}

	@Override
	public void update(ActivityH5AdvertBo bo) throws Exception {
		ActivityH5Advert  advert = bo.getAdvert();
		advert.setUpdateTime(new Date());
		mapper.update(advert);
		//先删除h5活动原规则和内容
		//删除相关活动规则
		roleService.deleteByActId(advert.getId());
		//删除相关活动内容
		contentService.deleteByActId(advert.getId());
		//再保存h5活动规则和内容
		saveRoleAndContent(bo);
	}

	@Override
	public ActivityH5AdvertBo findById(String id) {
		ActivityH5AdvertBo bo = new ActivityH5AdvertBo();
		//h5活动基本信息
		bo.setAdvert(mapper.findById(id));
		//h5活动规则
		bo.setRoles(roleService.findByActId(id));
		//h5活动内容
		bo.setContents(contentService.findByActId(id));
		return bo;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void deleteById(String id) throws Exception {
		//删除活动规则
		roleService.deleteByActId(id);
		//删除活动内容
		contentService.deleteByActId(id);
		//删除活动
		mapper.delete(id);
	}

	@Override
	public PageUtils<ActivityH5Advert> findByParam(ActivityH5AdvertQParam param,
			Integer pageNumber, Integer pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
        List<ActivityH5Advert> result = mapper.findByParam(param);
        return new PageUtils<ActivityH5Advert>(result);
	}

	@Override
	public List<ActivityH5Advert> listByJob(Date currentTime) {
		return mapper.listByJob(currentTime);
	}

	@Override
	public void updateBatchStatus(ActivityH5Advert entity) throws Exception {
		mapper.updateBatchStatus(entity);
	}

	@Override
	public void updateNoContent(ActivityH5Advert entity) throws Exception {
		entity.setUpdateTime(new Date());
		mapper.update(entity);
	}
}
