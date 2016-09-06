package com.okdeer.mall.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.archive.system.entity.SysUserLoginLog;


/**
 * 系统用户登陆信息日志
 * @project yschome-psms
 * @author tangy
 * @date 2016年4月27日 下午7:04:04
 */
public interface SysUserLoginLogMapper {
	/**
	 * 根据用户(登录状态、设备id)查询用户登录信息 
	 * @param userId    用户id
	 * @param isLogin   登录状态
	 * @param deviceId  设备id
	 * @return
	 */
	List<SysUserLoginLog> findAllByUserId(@Param("userId")String userId, @Param("isLogin")Integer isLogin,
			@Param("deviceId")String deviceId, @Param("clientType")Integer clientType);
	
	/**
	 * 新增系统用户登录信息  
	 * @param sysUserLoginLog  系统用户登录信息  
	 * @return
	 */
	int insertSysUserLoginLog(SysUserLoginLog sysUserLoginLog);
	
	/**
	 * 根据id批量更新登录下线状态 
	 * @param ids   id集
	 * @return
	 */
	int updateIsLoginByIds(@Param("ids") List<String> ids);
	
	/**
	 * 更新 系统用户登录信息
	 * @param sysUserLoginLog  系统用户登录信息
	 * @return
	 */
	int updateSysUserLoginLog(SysUserLoginLog sysUserLoginLog);
	
	/**
	 * 根据用户id(设备id)批量更新登录下线状态 
	 * @param userId    用户id
	 * @param deviceId  设备id
	 * @return
	 */
	int updateIsLoginByUserId(@Param("userId")String userId,  @Param("deviceId")String deviceId);
	
}
