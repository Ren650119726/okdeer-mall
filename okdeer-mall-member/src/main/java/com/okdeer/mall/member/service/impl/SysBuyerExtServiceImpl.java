/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: SysBuyerExtServiceImpl.java 
 * @Date: 2015年11月26日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.member.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.member.member.entity.SysBuyerExt;
import com.okdeer.mall.member.member.service.SysBuyerExtServiceApi;
import com.okdeer.mall.member.service.SysBuyerExtService;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.member.mapper.SysBuyerExtMapper;

/**
 * 用户扩展表信息查询实现类
 * @pr yschome-mall
 * @author zhongyong
 * @date 2015年11月20日 下午5:13:29
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.member.member.service.SysBuyerExtServiceApi")
public class SysBuyerExtServiceImpl implements SysBuyerExtServiceApi,SysBuyerExtService {

	/**
	 * 自动注入会员扩展实体
	 */
	@Autowired
	private SysBuyerExtMapper sysBuyerExtMapper;

	@Override
	public SysBuyerExt findByUserId(String userId) throws ServiceException {
		return sysBuyerExtMapper.selectByUserId(userId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateByUserId(SysBuyerExt sysBuyerExt) throws ServiceException {
		// TODO Auto-generated method stub
		sysBuyerExtMapper.updateByUserId(sysBuyerExt);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int insertSelective(SysBuyerExt sysBuyerExt) throws ServiceException {
		return sysBuyerExtMapper.insertSelective(sysBuyerExt);
	}
	
	/**
	 * @Description: 重置已经抽奖机会为0的用户，将抽奖机会重置为1次
	 * @throws
	 * @author tuzhd
	 * @date 2016年11月22日
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateUserPrizeCount(){
		sysBuyerExtMapper.updateUserPrizeCount();
	}
	
	/**
	 * @Description: 根据用户id 抽奖之后将其抽奖机会-1
	 * @throws
	 * @author tuzhd
	 * @date 2016年11月22日
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateCutPrizeCount(String userId){
		sysBuyerExtMapper.updateCutPrizeCount(userId);
	}

}
