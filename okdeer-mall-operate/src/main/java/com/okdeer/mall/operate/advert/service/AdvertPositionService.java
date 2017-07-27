/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: AdvertPositionService.java 
 * @Date: 2016年1月22日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 

package com.okdeer.mall.operate.advert.service;

import java.util.List;

import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.mall.advert.entity.AdvertPosition;
import com.okdeer.mall.advert.entity.AdvertPositionQueryVo;
import com.okdeer.mall.advert.enums.AdvertTypeEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;

/**
 * 广告service类
 * @project yschome-mall
 * @author zhaoqc
 * @date 2016年1月22日 下午4:44:23
 */
public interface AdvertPositionService {
	
	/**
	 * 获取广告位列表
	 *
	 * @param queryVo 查询Vo
	 * @param pageNumber 页数
	 * @param pageSize 每页记录数
	 * @return 广告位分页数据
	 * @throws ServiceException 抛出异常
	 */
	PageUtils<AdvertPosition> findAdvertPositionPage(AdvertPositionQueryVo queryVo, 
					int pageNumber, int pageSize) throws ServiceException;
	
	/**
	 * 通过住家查找广告位
	 *
	 * @param id 主键
	 * @return 广告位信息
	 * @throws ServiceException 抛出异常
	 */
	AdvertPosition findById(String id) throws ServiceException;
	
	/**
	 * 修改广告位信息
	 *
	 * @param position 广告位
	 * @param user 当前用户
	 * @throws ServiceException 抛出异常
	 */
	int updatePostion(AdvertPosition position, SysUser user) throws ServiceException;
	/**
	 * 查找广告位列表
	 *
	 * @return 广告位列表
	 */
	List<AdvertPosition> findAllAdvertPositions();
	
	/**
	 * 
	 * @Description: 
	 * @param advertType
	 * @return
	 * @author zengjizu
	 * @date 2017年1月3日
	 */
	AdvertPosition findByType(AdvertTypeEnum advertType);

}
