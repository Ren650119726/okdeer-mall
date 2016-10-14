/**
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall
 * @File: MemberConsigneeAddressService.java
 * @Date: 2015年11月26日
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */

package com.okdeer.mall.member.service;

import java.util.List;
import java.util.Map;

import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.member.vo.MemberConsigneeAddressVo;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;

/**
 * 收货地址service
 *
 * @project yschome-mall
 * @author wusw
 * @date 2015年12月9日 上午9:19:13
 */
/***
 * 
 * ClassName: MemberConsigneeAddressService 
 * @Description: TODO
 * @author luosm
 * @date 2016年10月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	   V1.1.0          2016-10-14     		luosm               根据小区id批量修改省市区名，小区名
 */
public interface MemberConsigneeAddressService {

	/**
	 * 根据userId查询会员收货地址集合
	 *
	 * @author zhongyong
	 * @param userId 请求参数
	 * @return 返回结果集
	 * @throwsServiceException
	 */
	List<String> findByUserId(String userId) throws ServiceException;
	
	/**
	 * 根据店铺ID 查询店铺默认地址
	 */
	MemberConsigneeAddress findByStoreId(String userId) throws Exception;

	/**
	 * 根据userId查询会员收货地址集合
	 *
	 * @author zhongyong
	 * @param userId 请求参数
	 * @return  List<MemberConsigneeAddress> 返回结果集
	 * @throwsServiceException
	 */
	List<MemberConsigneeAddress> findListByUserId(String userId) throws ServiceException;

	/**
	 * 根据条件查询店铺地址列表（不分页）
	 *
	 * @param memberConsigneeAddress 收货地址对象（查询条件）
	 * @return 返回店铺地址列表
	 * @throws ServiceException 异常类
	 */
	List<MemberConsigneeAddress> getList(MemberConsigneeAddress memberConsigneeAddress) throws ServiceException;

	/**
	 * 根据条件查询收货地址列表（分页）
	 *
	 * @param memberConsigneeAddress 收货地址对象（查询条件）
	 * @param pageNumber 页码
	 * @param pageSize 页大小
	 * @return 分页对象
	 * @throws ServiceException 异常类
	 */
	PageUtils<MemberConsigneeAddress> getPage(MemberConsigneeAddress memberConsigneeAddress, int pageNumber,
					int pageSize) throws ServiceException;

	/**
	 * 根据主键id获取店铺地址详细信息
	 *
	 * @param id 主键id
	 * @return 收货地址对象
	 * @throws ServiceException 异常类
	 */
	MemberConsigneeAddress getConsigneeAddress(String id);

	/**
	 * 添加收货地址信息
	 *
	 * @author luosm
	 * @param memberConsigneeAddress 收货地址对象
	 * @param currentOperateUser 当前登陆用户
	 * @throws ServiceException 异常类
	 */
	void addConsigneeAddress(MemberConsigneeAddress memberConsigneeAddress, SysUser currentOperateUser);

	/**
	 * 修改收货地址信息
	 *
	 * @param memberConsigneeAddress 收货地址对象
	 * @param currentOperateUser 当前登陆用户
	 * @throws ServiceException 异常类
	 */
	void updateConsigneeAddress(MemberConsigneeAddress memberConsigneeAddress, SysUser currentOperateUser);
	
	/**
	 * 设置默认地址
	 *
	 * @param id 收货地址对象id
	 * @param currentOperateUser 当前登陆用户
	 * @throws ServiceException 异常类
	 */
	void updateDefaultAddress(String id, SysUser currentOperateUser) throws ServiceException;

	/**
	 * 批量删除收货地址
	 *
	 * @param ids 收货地址id
	 * @param updateUserId 当前登陆用户id
	 * @throws ServiceException 异常类
	 */
	void deleteByIds(List<String> ids, String updateUserId);
	
	//begin added by luosm 20161014 V1.1.0
	/***
	 * 
	 * @Description: 根据小区id批量修改省市区名，小区名 
	 * @param memberConsigneeAddress
	 * @param currentOperateUser
	 * @author luosm
	 * @date 2016年10月14日
	 */
	void updateByCommunityIdsConsigneeAddress(MemberConsigneeAddress memberConsigneeAddress);
	//end added by luosm 20161014 V1.1.0

	/**
	 * 根据ID批量删除店铺地址
	 *
	 * @author luosm
	 * @param id 店铺地址id
	 * @param updateUserId 当前登陆用户id
	 * @throws ServiceException 异常类
	 */
	void deleteById(String id, String updateUserId);

	/**
	 * 根据主键查询对象
	 *
	 * @param addressId 地址id
	 * @return 地址对象
	 */
	MemberConsigneeAddress findById(String addressId);
	
	/**
	 * 添加APP用户收货地址
	 * @author luosm
	 * @param memberConsigneeAddress 请求参数
	 * @return  String id
	 * @throwsServiceException
	 */
	String addListByAppUser(MemberConsigneeAddress memberConsigneeAddress) throws ServiceException;
	
	/**
	 * 修改APP用户收货地址（非物业推送地址）
	 * @author luosm
	 * @param memberConsigneeAddress 请求参数
	 * @return  String id
	 * @throwsServiceException
	 */
	String editListByAppUser(MemberConsigneeAddress memberConsigneeAddress) throws ServiceException;
	
	
	/**
	 * APP用户查询收货地址（计算是否超出配送范围）
	 * @author luosm
	 * @param userId
	 * @param storeId
	 * @return MemberConsigneeAddressVo
	 * @throws ServiceException
	 */
	List<MemberConsigneeAddressVo> findAppUserList(String userId,String storeId) throws ServiceException;
	
	
	/**
	 * APP用户查询收货地址
	 * @author luosm
	 * @param userId
	 * @return MemberConsigneeAddressVo
	 * @throws ServiceException
	 */
	List<MemberConsigneeAddress> findAppUserAddress(String userId) throws ServiceException;
	
	/**
	 * APP用户根据id查询收货地址
	 * @author luosm
	 * @param id
	 * @return MemberConsigneeAddressVo
	 * @throws ServiceException
	 */
	MemberConsigneeAddress findAppUserById(String id) throws ServiceException;
	
	
	
	/**
	 * 获取默认地址 </p>
	 * 
	 * @author yangq
	 * @param userId
	 * @return
	 */
	MemberConsigneeAddress getSellerDefaultAddress(String userId)throws Exception;
	
	/**
	 * 获取买家地址 </p>
	 * 
	 * @author yangq
	 * @param id
	 * @return
	 */
	MemberConsigneeAddress selectAddressById(String id) throws Exception;
	/**
	 * 微信用户根据用户id查询收货地址
	 * @author TZD
	 * @param id
	 * @return MemberConsigneeAddressVo
	 * @throws ServiceException
	 */
	public List<MemberConsigneeAddressVo> findWxUserList(String userId, String storeId)
			throws ServiceException;
	
	/**
	 * 微信用户根据地址id查询收货地址
	 * @author TZD
	 * @param 地址id
	 * @return MemberConsigneeAddressVo
	 * @throws ServiceException
	 */
	public MemberConsigneeAddressVo findWxUserById(String id,String storeId)
			throws ServiceException;
	
	/**
	 * 
	 * @Description: 更新地址信息
	 * @return int  
	 * @throws ServiceException
	 * @author wushp
	 * @date 2016年7月2日
	 */
	int updateByPrimaryKeySelective(MemberConsigneeAddress memberConsigneeAddress) throws ServiceException;
	
	// Begin 12395 add by zengj
	/**
	 * 
	 * @Description: 查询用户默认收货地址
	 * @param params 查询参数
	 * @return   返回查询结果集
	 * @author zengj 
	 * @date 2016年8月11日
	 */
	Map<String, Object> findUserDefaultAddress(Map<String, Object> params);
	
	/**
	 * 
	 * @Description: 查询用户默认收货地址-针对秒杀商品
	 * @param params 查询参数
	 * @return   返回查询结果集
	 * @author zengj 
	 * @date 2016年8月11日
	 */
	Map<String, Object> findUserDefilatSeckillAddress(Map<String, Object> params);
	// End 12395 add by zengj
}
