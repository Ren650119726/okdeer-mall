/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: MemberServiceImpl.java 
 * @Date: 2015年11月26日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.member.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.archive.system.entity.SysBuyerUserPointsExt;
import com.okdeer.archive.system.entity.SysMemberExtVo;
import com.okdeer.archive.system.entity.TmpExample;
import com.okdeer.mall.member.member.service.MemberServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.system.mapper.SysBuyerUserMapper;

/**
 * 买家会员service实现类 
 * @project yschome-mall
 * @author zhongy
 * @date 2015年11月24日 下午3:18:08
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.member.member.service.MemberServiceApi")
public class MemberServiceImpl implements MemberServiceApi {

	/**
	 * 自动注入会员dao
	 */
	@Autowired
	private SysBuyerUserMapper sysBuyerUserMapper;

	@Override
	public PageUtils<SysBuyerUser> selectByParams(Map<String, Object> params) throws ServiceException {
		List<SysBuyerUser> result = sysBuyerUserMapper.selectByParams(params);
		PageUtils<SysBuyerUser> page = new PageUtils<SysBuyerUser>(result);
		return page;
	}

	@Override
	public PageUtils<SysMemberExtVo> findMemberByParams(Map<String, Object> params) throws ServiceException {
		Integer pageNumber = (Integer)params.get("pageNumber");
		Integer pageSize = (Integer)params.get("pageSize");
		PageHelper.startPage(pageNumber, pageSize, true);
		List<SysMemberExtVo> result = sysBuyerUserMapper.selectMemberByParams(params);
		if (result == null) {
			result =  new ArrayList<SysMemberExtVo>();
		}
		PageUtils<SysMemberExtVo> page = new PageUtils<SysMemberExtVo>(result);
		return page;
	}

	@Override
	public SysBuyerUser selectByPrimaryKey(String id) throws ServiceException {
		SysBuyerUser sysBuyerUser = sysBuyerUserMapper.selectByPrimaryKey(id);
		return sysBuyerUser;
	}
	/**
	 * 根据手机号查询用户信息
	 * 防止一个号码出现多个用户记录取其中一个
	 * @param phone 手机号
	 * tuzhiding
	 * @return
	 */
	@Override
	public SysBuyerUser selectUserByPhone(String phone) throws ServiceException {
		List<SysBuyerUser> sysBuyerUser = sysBuyerUserMapper.selectUserByPhone(phone);
		//防止一个号码出现多个用户记录取其中一个
		if(sysBuyerUser != null && sysBuyerUser.size() > 0 ){
			return sysBuyerUser.get(0);
		}
		return null;
	}

	@Override
	public PageUtils<SysBuyerUserPointsExt> pointsExtSelectByParams(Map<String, Object> map, Integer pageNumber,
			Integer pageSize) throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<SysBuyerUserPointsExt> result = sysBuyerUserMapper.pointsExtSelectByParams(map);
		PageUtils<SysBuyerUserPointsExt> page = new PageUtils<SysBuyerUserPointsExt>(result);
		return page;
	}
	
	@Transactional(readOnly = true)
	@Override
	public PageUtils<SysBuyerUserPointsExt> pointsExtSelectByParamsNew(Map<String, Object> map, Integer pageNumber,
			Integer pageSize) throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<SysBuyerUserPointsExt> result = sysBuyerUserMapper.pointsExtSelectByParamsNew(map);
		// 不存在根据积分来查询的，sql中没查询积分，需要单独查询积分信息
		if (!map.containsKey("minPoint") && !map.containsKey("maxPoint")) {
			if (CollectionUtils.isNotEmpty(result)) {
				List<String> userIds = new ArrayList<String>();
				for (SysBuyerUserPointsExt point : result) {
					userIds.add(point.getId());
				}
				// 查询积分
				List<SysMemberExtVo> memberExtVoList = sysBuyerUserMapper.findExtByUserIds(userIds);
				if (CollectionUtils.isNotEmpty(memberExtVoList)) {
					for (SysBuyerUserPointsExt point : result) {
						for (SysMemberExtVo memberExt : memberExtVoList) {
							if (point.getId() != null && point.getId().equals(memberExt.getId())) {
								point.setPointVal(memberExt.getPointVal());
								memberExtVoList.remove(memberExt);
								break;
							}
						}
					}
				}
			}
		}
		PageUtils<SysBuyerUserPointsExt> page = new PageUtils<SysBuyerUserPointsExt>(result);
		return page;
	}

	@Override
	public SysBuyerUserPointsExt pointsExtSelectById(String id) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		List<SysBuyerUserPointsExt> list = sysBuyerUserMapper.pointsExtSelectByParams(params);
		if (list != null) {
			return list.get(0);
		}
		return null;
	}

}
