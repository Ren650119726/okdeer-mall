/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: AdvertPositionServiceImpl.java 
 * @Date: 2016年1月22日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.operate.advert.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.mall.advert.entity.AdvertPosition;
import com.okdeer.mall.advert.entity.AdvertPositionQueryVo;
import com.okdeer.mall.advert.service.IAdvertPositionServiceApi;
import com.yschome.base.common.exception.ServiceException;
import com.yschome.base.common.utils.PageUtils;
import com.okdeer.mall.operate.advert.mapper.AdvertPositionMapper;
import com.okdeer.mall.operate.advert.service.AdvertPositionService;

/**
 * 广告service实现类
 * @project yschome-mall
 * @author zhaoqc
 * @date 2016年1月22日 下午5:15:05
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.advert.service.IAdvertPositionServiceApi")
public class AdvertPositionServiceImpl implements AdvertPositionService, IAdvertPositionServiceApi {

	/**
	 * 广告位Mapper注入
	 */
	@Autowired
	private AdvertPositionMapper positionMapper;

	/**
	 * @desc 获取广告位列表
	 *
	 * @param queryVo 查询Vo
	 * @param pageNumber 页数
	 * @param pageSize 每页记录数
	 * @return 店铺分页数据
	 * @throws ServiceException 抛出异常
	 */
	@Override
	public PageUtils<AdvertPosition> findAdvertPositionPage(AdvertPositionQueryVo queryVo, int pageNumber, int pageSize)
			throws ServiceException {

		PageHelper.startPage(pageNumber, pageSize, true);
		List<AdvertPosition> advertPositions = this.positionMapper.findAdvertPositions(queryVo);

		return new PageUtils<AdvertPosition>(advertPositions);
	}

	/**
	 * @desc 通过住家查找广告位
	 *
	 * @param id 主键
	 * @return 广告位信息
	 * @throws ServiceException 抛出异常
	 */
	@Override
	public AdvertPosition findById(String id) throws ServiceException {
		return this.positionMapper.selectByPrimaryKey(id);
	}

	/**
	 * @desc 修改广告位
	 *
	 * @param position 广告位
	 * @param user 当前用户
	 * @throws ServiceException 抛出异常
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int updatePostion(AdvertPosition position, SysUser user) throws ServiceException {
		position.setUpdateTime(new Date());
		position.setUpdateUserId(user.getId());

		return this.positionMapper.updateByPrimaryKeySelective(position);
	}

	/**
	 * @desc 查找广告位方法
	 *
	 * @return 广告位列表
	 */
	@Transactional(readOnly = true)
	@Override
	public List<AdvertPosition> findAllAdvertPositions() {
		return this.positionMapper.findAdvertPositions(new AdvertPositionQueryVo());
	}

}
