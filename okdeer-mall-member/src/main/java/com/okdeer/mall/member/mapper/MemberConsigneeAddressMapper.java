package com.okdeer.mall.member.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.member.vo.MemberConsigneeAddressVo;
import com.okdeer.mall.member.member.vo.UserAddressVo;

/**
 * @DESC: 
 * @author luosm
 * @date  2016-01-29 19:50:53
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构4.1          2016年7月25日                               zengj				添加查询用户收货地址列表，针对服务订单 方法
 *     1.1            2016年9月24日                               maojj				添加查询用户地址，针对秒杀
 */
public interface MemberConsigneeAddressMapper extends IBaseCrudMapper {

	/**
	 * 
	 * 批量删除收货地址
	 *
	 * @param ids 收货地址id
	 */
	void deleteByIds(@Param("ids") List<String> ids,@Param("disabled") Disabled disabled,
           @Param("updateTime") Date updatTime,@Param("updateUserId") String updateUserId);
	
	/**
	 * 根据userId查询会员收货地址
	 * @author zhongyong
	 * @param userId 请求参数
	 * @return 返回结果集
	 */
	List<MemberConsigneeAddress> selectByUserId(String userId);
	
	/**
	 * 
	 * 根据条件查询店铺地址列表（对象参数）
	 * @author luosm
	 * @param memberConsigneeAddress 店铺地址对象（查询条件）
	 * @return 店铺地址信息列表
	 */
	List<MemberConsigneeAddress> selectByParams(MemberConsigneeAddress memberConsigneeAddress);
	
	/**
	 * 
	 * 根据主键id删除店铺地址
	 *
	 * @param id 店铺地址id
	 */
	void deleteById(@Param("id") String id,@Param("disabled") Disabled disabled,
	           @Param("updateTime") Date updatTime,@Param("updateUserId") String updateUserId);
	
	
	/***
	 * 用户App接口
	 * 根据用户id，店铺id获取用户收货地址详细信息
	 * @author luosm
	 * @param id
	 * @param storeId
	 * @return
	 */
	List<MemberConsigneeAddressVo> selectByDistance(@Param("userId") String id,@Param("storeId") String storeId);
	
	/***
	 * 用户微信接口
	 * 微信用户根据id查询收货地址
	 * @author TZD
	 * @param id
	 * @param storeId
	 * @return
	 */
	List<MemberConsigneeAddressVo> selectByWxDistance(@Param("userId") String id,@Param("storeId") String storeId);
	
	/***
	 * 用户微信接口
	 * 微信用户根据地址id查询收货地址
	 * @author TZD
	 * @param id
	 * @param storeId
	 * @return
	 */
	public MemberConsigneeAddressVo selectWxDistanceById(@Param("id") String id,@Param("storeId") String storeId);
	
	
	/**
	 * 获取默认地址 </p>
	 * 
	 * @author yangq
	 * @param userId
	 * @return
	 */
	MemberConsigneeAddress getSellerDefaultAddress(String userId);
	
	/**
	 * 获取买家地址 </p>
	 * 
	 * @author yangq
	 * @param id
	 * @return
	 */
	MemberConsigneeAddress selectAddressById(String id);
	
	// Begin 重构4.1 add by zengj
	/**
	 * 
	 * @Description: 用户收货地址列表，针对服务订单 
	 * @param params 查询参数
	 * @return  返回查询结果集
	 * @author zengj
	 * @date 2016年7月25日
	 */
	List<Map<String, Object>> findUserAddressList(Map<String, Object> params);
	
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
	// End 重构4.1 add by zengj

	// Begin added by maojj 2016-09-24 友门鹿1.1
	/**
	 * @Description: 根据店铺服务范围查询用户地址信息
	 * @param params 查询参数
	 * @return 用户地址列表  
	 * @author maojj
	 * @date 2016年9月26日
	 */
	List<UserAddressVo> findAddrWithStoreServRange(Map<String, Object> params);

	/**
	 * @Description: 根据秒杀服务范围查询用户地址信息
	 * @param params 查询参数
	 * @return 用户地址列表
	 * @author maojj
	 * @date 2016年9月26日
	 */
	List<UserAddressVo> findAddrWithSeckillServRange(Map<String, Object> params);

	/**
	 * @Description: 根据服务范围查询用户地址信息
	 * @param params 查询参数
	 * @return 用户地址列表
	 * @author maojj
	 * @date 2016年9月26日
	 */
	List<UserAddressVo> findAddrWithServRange(Map<String, Object> params);

	/**
	 * @Description: 根据用户ID查询用户地址列表
	 * @param userId 用户Id
	 * @return 用户地址列表
	 * @author maojj
	 * @date 2016年9月26日
	 */
	List<UserAddressVo> findAddrWithUserId(String userId);
	// End added by maojj 2016-09-24 友门鹿1.1
}