/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2016年11月4日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.skinmanager.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.label.entity.ActivityLabel;
import com.okdeer.mall.common.utils.RobotUserUtil;
import com.okdeer.mall.operate.dto.SkinManagerDto;
import com.okdeer.mall.operate.dto.SkinManagerParamDto;
import com.okdeer.mall.operate.entity.SkinManager;
import com.okdeer.mall.operate.entity.SkinManagerDetail;
import com.okdeer.mall.operate.enums.SkinManagerStatus;
import com.okdeer.mall.operate.skinmanager.mapper.SkinManagerDetailMapper;
import com.okdeer.mall.operate.skinmanager.mapper.SkinManagerMapper;
import com.okdeer.mall.operate.skinmanager.service.SkinManagerService;

/**
 * ClassName: SkinManagerImpl 
 * @Description: 皮肤管理服务实现类
 * @author xuzq01
 * @date 2016年11月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	    V1.2开发			2016-11-4			xuzq01	                        皮肤管理服务实现类
 *
 */
@Service
public class SkinManagerServiceImpl extends BaseServiceImpl implements SkinManagerService {
	private static final Logger log = Logger.getLogger(SkinManagerServiceImpl.class); 
	
	/**
	 * 获取皮肤mapper
	 */
	@Resource
	private SkinManagerMapper skinManagerMapper;
	
	@Resource
	private SkinManagerDetailMapper skinManagerDetailMapper;
	
	/**
	 * 获取列表
	 * @see com.okdeer.mall.operate.service.SkinManagerApi#findSkinList(com.okdeer.mall.operate.dto.SkinManagerDto)
	 */
	@Override
	public PageUtils<SkinManagerDto> findSkinList(SkinManagerParamDto paramDto) {
		PageHelper.startPage(paramDto.getPageNumber(), paramDto.getPageSize(), true);
		List<SkinManagerDto> result = skinManagerMapper.findSkinList(paramDto);
		if (result == null) {
			result = new ArrayList<SkinManagerDto>();
		}
		return new PageUtils<SkinManagerDto>(result);
	}
	
	/**
	 * 逻辑删除活动皮肤
	 * @see com.okdeer.mall.operate.service.SkinManagerApi#deleteSkin(java.lang.String)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteSkinById(String skinId,String userId) {
		SkinManagerDto skinManagerDto = new SkinManagerDto();
		skinManagerDto.setUpdateUserId(userId);
		skinManagerDto.setId(skinId);
		Date date = new Date();
		skinManagerDto.setUpdateTime(date);
		SkinManager skinManager= BeanMapper.map(skinManagerDto, SkinManager.class);
		skinManagerMapper.deleteSkinById(skinManager);
	}

	/**
	 * 关闭活动皮肤
	 * @see com.okdeer.mall.operate.service.SkinManagerApi#updateSkinStatus(java.lang.String)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void closeSkinById(String skinId,String userId) {
		SkinManagerDto skinManagerDto = new SkinManagerDto();
		skinManagerDto.setUpdateUserId(userId);
		skinManagerDto.setId(skinId);
		skinManagerDto.setStatus(SkinManagerStatus.COMPLETE);
		Date date = new Date();
		skinManagerDto.setUpdateTime(date);
		SkinManager skinManager= BeanMapper.map(skinManagerDto, SkinManager.class);
		skinManagerMapper.closeSkinById(skinManager);
	}

	/**
	 * 根据时间查询是否有满足条件的活动皮肤
	 * @see com.okdeer.mall.operate.service.SkinManagerApi#updateSkinStatus(java.lang.String)
	 */
	@Override
	public int findSkinByTime(SkinManagerDto skinManagerDto){
		SkinManager skinManager= BeanMapper.map(skinManagerDto, SkinManager.class);
		return skinManagerMapper.findSkinByTime(skinManager);
	}

	/**
	 * 通过名称获取存在皮肤数量
	 * @see com.okdeer.mall.operate.service.SkinManagerApi#selectSkinCountByName(com.okdeer.mall.operate.dto.SkinManagerDto)
	 */
	@Override
	public int findSkinCountByName(SkinManagerDto skinManagerDto) {
		SkinManager skinManager= BeanMapper.map(skinManagerDto, SkinManager.class);
		return skinManagerMapper.findSkinCountByName(skinManager);
	}
	
	@Override
	public SkinManagerDto findSkinDetailByParam(SkinManagerParamDto paramDto) {
		return skinManagerMapper.findSkinDetailByParam(paramDto);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return skinManagerMapper;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void update(SkinManagerDto skinManagerDto) {
		Date currentDate = new Date();
		skinManagerDto.setUpdateTime(currentDate);
		SkinManager skinManager = BeanMapper.map(skinManagerDto, SkinManager.class);
		skinManager.setStatus(SkinManagerStatus.NOSTART);
		// 先删除皮肤图片详情
		skinManagerDetailMapper.deleteBySkinId(skinManager.getId());
		// 修改皮肤活动信息
		skinManagerMapper.update(skinManager);
		// 新增修改后的图片信息
		List<SkinManagerDetail> detailList = skinManagerDto.getDetailList();

		for(SkinManagerDetail detail : detailList){
			detail.setId(UuidUtils.getUuid());
			detail.setSkinManagerId(skinManager.getId());
			detail.setCreateTime(currentDate);
			detail.setCreateUserId(skinManager.getUpdateUserId());
			detail.setUpdateTime(currentDate);
			detail.setUpdateUserId(skinManager.getUpdateUserId());
			detail.setDisabled(Disabled.valid);
		}
		skinManagerDetailMapper.addBatch(detailList);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void add(SkinManagerDto skinManagerDto) {
		String skinManagerId = UuidUtils.getUuid();
		String userId = skinManagerDto.getCreateUserId();
		Date date = new Date();

		skinManagerDto.setId(skinManagerId);
		skinManagerDto.setStatus(SkinManagerStatus.NOSTART);
		skinManagerDto.setCreateUserId(userId);
		skinManagerDto.setUpdateUserId(userId);
		skinManagerDto.setCreateTime(date);
		skinManagerDto.setUpdateTime(date);
		SkinManager skinManager = BeanMapper.map(skinManagerDto, SkinManager.class);
		skinManagerMapper.add(skinManager);
		List<SkinManagerDetail> detailList = skinManagerDto.getDetailList();

		for(SkinManagerDetail detail : detailList){
			detail.setId(UuidUtils.getUuid());
			detail.setSkinManagerId(skinManagerId);
			detail.setCreateTime(date);
			detail.setCreateUserId(userId);
			detail.setUpdateTime(date);
			detail.setUpdateUserId(userId);
			detail.setDisabled(Disabled.valid);
		}
		skinManagerDetailMapper.addBatch(detailList);
		
	}
	
	
	/**
	 * 执行换肤活动管理 JOB 任务
	 * @Description: TODO   
	 * @return void  
	 * @throws
	 * @author tuzhd
	 * @date 2016年11月16日
	 */
	public void processSkinActivityJob(){
		try{
			log.info("执行换肤活动管理 定时器开始");
			Map<String,Object> map = new HashMap<String,Object>();
			Date nowTime = new Date();
			map.put("nowTime", nowTime);
			//1、查询活动未开始，开始时间小于当前的数据 即为要设置开始，2、活动开始、结束时间小于当前的数据 即为要设置结束
			List<SkinManager> accList = skinManagerMapper.listByJob(map);;
			//获得系统当前系统用户id
			String updateUserId = RobotUserUtil.getRobotUser().getId();
			//需要更新状态的活动新不为空进行定时任务处理
			if(CollectionUtils.isNotEmpty(accList)){
				for(SkinManager a : accList){
					try{
						//将未开始的改为开始 
						if(a.getStatus().ordinal() == SkinManagerStatus.NOSTART.ordinal()){
							//根据id修改换肤活动管理状态
							updateStatusById(a.getId(),  SkinManagerStatus.UNDERWAY, updateUserId, nowTime);
						
						//将进行中的改为已结束的
						}else if(a.getStatus().ordinal() == SkinManagerStatus.UNDERWAY.ordinal()){
							//根据id修改换肤活动管理状态
							updateStatusById(a.getId(),  SkinManagerStatus.COMPLETE, updateUserId, nowTime);
						}
					}catch(Exception e){
						log.error(a.getStatus()+"状态换肤活动管理"+a.getId()+"job修改异常 :",e);
					}
				}
			}
			log.info("执行换肤活动管理 定时器结束");
		}catch(Exception e){
			log.error("执行换肤活动管理 job异常",e);
		}
	}
	
	
	/**
	 * @Description: 根据id换肤活动管理状态
	 * @param id
	 * @param status 活动状态 0 未开始 ，1：进行中2:已结束 
	 * @param updateUserId 修改人
	 * @param updateTime 修改时间
	 * @throws Exception
	 * @author tuzhiding
	 * @date 2016年11月12日
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStatusById(String id, SkinManagerStatus status, String updateUserId, Date updateTime) throws Exception {
		//修改主表信息
		SkinManager c = new SkinManager();
		c.setId(id);
		c.setStatus(status);
		c.setUpdateTime(updateTime);
		skinManagerMapper.update(c);
	}
	
}
