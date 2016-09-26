/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: CrossBorderElectricityServiceImpl.java 
 * @Date: 2016年1月6日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 
package com.okdeer.mall.operate.crossBorderElectricity.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.mall.operate.entity.OperateCrossBorderElectricity;
import com.okdeer.mall.operate.service.IOperateCrossBorderElectricityServiceApi;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.operate.crossBorderElectricity.mapper.OperateCrossBorderElectricityMapper;
import com.okdeer.mall.operate.crossBorderElectricity.service.OperateCrossBorderElectricityService;

/**
 * 跨境电商设置的service实现
 * @project yschome-mall
 * @author wusw
 * @date 2016年1月6日 下午2:12:21
 */
@Service(version="1.0.0", interfaceName = "com.okdeer.mall.operate.service.IOperateCrossBorderElectricityServiceApi")
public class OperateCrossBorderElectricityServiceImpl implements OperateCrossBorderElectricityService,IOperateCrossBorderElectricityServiceApi {
	
	/**
	 * 云存储跨境电商设置图片路径二级域名
	 */
	@Value("${operateImagePrefix}")
	private String operateImagePrefix;
	
	/**
	 * 跨境电商设置mapper
	 */
	@Autowired
	private OperateCrossBorderElectricityMapper crossBorderElectricityMapper;

	/**
	 * @desc 根据条件查询跨境电商设置列表信息（分页）
	 *
	 * @param crossBorderElectricity 跨境电商设置实体（查询条件）
	 * @param pageNumber 页码
	 * @param pageSize 每页数量
	 * @return 列表信息
	 * @throws ServiceException
	 */
	@Override
	public PageUtils<OperateCrossBorderElectricity> findByParams(OperateCrossBorderElectricity crossBorderElectricity, 
            int pageNumber,int pageSize) throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true);
		crossBorderElectricity.setDisabled(Disabled.valid);
		List<OperateCrossBorderElectricity> result = crossBorderElectricityMapper.selectByParamsEntity(crossBorderElectricity);
		if (result == null) {
			result = new ArrayList<OperateCrossBorderElectricity>();
		} else {
			for (OperateCrossBorderElectricity c : result) {
				if (StringUtils.isNotEmpty(c.getImageUrl())) {
					c.setImageUrl(operateImagePrefix + c.getImageUrl()); 
				}
			}
		}
		return new PageUtils<OperateCrossBorderElectricity>(result);
	}
	
	/**
	 * @desc 根据条件查询跨境电商设置列表信息（不分页）
	 *
	 * @param crossBorderElectricity 跨境电商设置实体（查询条件）
	 * @param pageNumber 页码
	 * @param pageSize 每页数量
	 * @return 列表信息
	 * @throws ServiceException
	 */
	@Override
	public List<OperateCrossBorderElectricity> findListByParams() throws ServiceException {
		List<OperateCrossBorderElectricity> result = crossBorderElectricityMapper.selectByParamsEntity(null);
		if (result == null) {
			result = new ArrayList<OperateCrossBorderElectricity>();
		}
		return result;
	}

	
	/**
	 * @desc 根据主键id获取跨境电商设置信息
	 *
	 * @param crossBorderElectricity 跨境电商设置实体
	 * @return 跨境电商设置详细信息
	 * @throws ServiceException
	 */
	@Override
	public OperateCrossBorderElectricity getById(String id)throws ServiceException {
		
		return crossBorderElectricityMapper.selectByPrimaryKey(id);
	} 

	/**
	 * @desc 添加跨境电商设置信息 
	 *
	 * @param crossBorderElectricity 跨境电商设置实体
	 * @param currentOperateUser 当然登陆用户实体
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addCrossBorderElectricity(OperateCrossBorderElectricity crossBorderElectricity,
            String currentOperateUserId)throws ServiceException {
		crossBorderElectricity.setId(UuidUtils.getUuid());
		crossBorderElectricity.setDisabled(Disabled.valid);
		crossBorderElectricity.setCreateUserId(currentOperateUserId);
		crossBorderElectricity.setUpdateUserId(currentOperateUserId);
		Date date = new Date();
		crossBorderElectricity.setCreateTime(date);
		crossBorderElectricity.setUpdateTime(date);
		crossBorderElectricityMapper.insertSelective(crossBorderElectricity);
	}

	/**
	 * @desc 修改跨境电商设置信息
	 *
	 * @param crossBorderElectricity 跨境电商设置实体
	 * @param currentOperateUser 当然登陆用户实体
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateCrossBorderElectricity(OperateCrossBorderElectricity crossBorderElectricity,
            String currentOperateUserId) throws ServiceException {
		crossBorderElectricity.setUpdateUserId(currentOperateUserId);
		crossBorderElectricity.setUpdateTime(new Date());
		crossBorderElectricityMapper.updateByPrimaryKeySelective(crossBorderElectricity);
	}

	/**
	 * @desc 批量逻辑删除跨境电商设置信息 
	 *
	 * @param ids 主键id集合
	 * @param currentOperateUser 当前登陆用户实体
	 * @throws ServiceException
	 */
	/*@Override
	public void deleteByIds(List<String> ids, String currentOperateUserId)throws ServiceException {
		if (ids != null && ids.size() > 0) {
			crossBorderElectricityMapper.deleteByIds(ids,Disabled.invalid, new Date(), currentOperateUserId);
		}
	}*/

	/**
	 * @desc TODO Add a description 
	 *
	 * @param ids
	 * @param disabled
	 * @throws ServiceException
	 */
	@Override
	public int selectCountById(List<String> ids, Disabled disabled)throws ServiceException {
		int count = 0;
		if (ids != null && ids.size() > 0) {
			count = crossBorderElectricityMapper.selectCountById(ids, disabled);
		}
		return count;
	}

	/**
	 * @desc 批量停用或启用跨境电商设置 
	 *
	 * @param ids
	 * @param disabled
	 * @param currentOperateUserId
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void disableByIds(List<String> ids, Disabled disabled,String currentOperateUserId) throws ServiceException {
		if (ids != null && ids.size() > 0) {
			crossBorderElectricityMapper.deleteByIds(ids,disabled, new Date(), currentOperateUserId);
		}
	}
	
}
