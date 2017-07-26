package com.okdeer.mall.system.service;

import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.archive.system.entity.SysBuyerUserThirdparty;
import com.okdeer.archive.system.entity.SysSmsVerifyCode;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.ca.api.buyeruser.entity.SysBuyerUserDto;
import com.okdeer.ca.api.common.ApiException;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-03-17 17:05:52
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构 4.1			2016-7-19			wusw	                            添加loadById方法（原来是在IBaseCrudService的方法）
 * 
 */
public interface SysBuyerUserService{
	
	/**
	 * DESC: 添加买家用户信息、修改验证码状态、添加第三方平台账号与本平台账号映射
	 * @author LIU.W
	 * @param sysSmsVerifyCodeUpdate
	 * @param buyerUserThirdparty
	 * @throws ServiceException
	 */
	public String addSysBuyerSync(SysBuyerUserDto sysBuyerUserDto,
			SysSmsVerifyCode sysSmsVerifyCodeUpdate,
			SysBuyerUserThirdparty buyerUserThirdparty) throws ApiException,ServiceException;

	// begin add by chenzc 2017-2-22
	/**
	 * 
	 * @Description: V2.1新增需求，pos机注册时保存当前店铺所在地址
	 * @return String 
	 * @throws 异常
	 * @author chenzc
	 * @date 2017年2月22日
	 */
	public String addSysBuyerSyncV210(SysBuyerUserDto sysBuyerUserDto, 
			SysSmsVerifyCode sysSmsVerifyCodeUpdate,
			SysBuyerUserThirdparty buyerUserThirdparty, String storeId) throws ApiException, ServiceException;
	// end add by chenzc
	
	// begin add by wangf01 2016.08.11
	/**
	 * DESC: 添加买家用户信息、修改验证码状态、添加第三方平台账号与本平台账号映射
	 * @author wangf01
	 * @param sysSmsVerifyCodeUpdate
	 * @param buyerUserThirdparty
	 * @throws ServiceException
	 */
	public String addSysBuyerSync410(SysBuyerUserDto sysBuyerUserDto,
								  SysSmsVerifyCode sysSmsVerifyCodeUpdate,
								  SysBuyerUserThirdparty buyerUserThirdparty) throws Exception;
	// end add by wangf01 2016.08.11
	
	/**
	 * DESC: 修改验证码状态、添加第三方平台账号与本平台账号映射
	 * @author LIU.W
	 * @param sysSmsVerifyCodeUpdate
	 * @param buyerUserThirdparty
	 * @throws ServiceException
	 */
	public void addSysBuyerThirdParty(SysSmsVerifyCode sysSmsVerifyCodeUpdate,
			SysBuyerUserThirdparty buyerUserThirdparty) throws ServiceException;

	/**
	 * 查询用户电话号码 
	 */
	String selectMemberMobile(String userId);
	
	/**
	 * 绑定手机号码
	 * @param phone 手机号码
	 * @param smsCodeId 验证码记录id
	 * @param userId 用户id
	 * @param storeId 店铺id
	 * @throws ServiceException 异常
	 */
	void updateMobile(String phone,String smsCodeId,String userId,String storeId) throws ServiceException, ApiException;

	// Begin 重构4.1  add by wusw  20160719
	/**
	 * @Description: 
	 * @param id
	 * @return EntityType  
	 * @author wusw
	 * @date 2016年7月19日
	 */
	public <EntityType> EntityType loadById(String id) throws ServiceException;
	
	/**
	 * 
	 * @Description: 根据id查询买家用户
	 * @param userId 用户id
	 * @return sysBuyerUser
	 * @throws ServiceException   
	 * @author YSCGD
	 * @date 2016年7月21日
	 */
	SysBuyerUser findByPrimaryKey(String userId) throws ServiceException;
	// End 重构4.1  add by wusw  20160719
}