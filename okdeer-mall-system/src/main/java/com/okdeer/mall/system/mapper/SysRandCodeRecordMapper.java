package com.okdeer.mall.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.system.entity.SysRandCodeRecord;

/**
 * 邀请码记录Mapper
 * 
 * @project yschome-mall
 * @author zhaoqc
 * @date 2016年10月05日 上午21:33:56
 */
public interface SysRandCodeRecordMapper {

	/**
	 * @desc 生成随机码
	 *
	 * @param sysRandCodeRecord 随机码实体
	 */
	void saveSysRandCodeRecord(SysRandCodeRecord sysRandCodeRecord);

	/**
	 * @desc 修改随机码
	 *
	 * @param sysRandCodeRecord 随机码实体
	 */
	void updateSysRandCodeRecord(SysRandCodeRecord sysRandCodeRecord);

	/**
	 * 根据随机码查询随机码实体
	 * 
	 * @param randCode 随机码
	 * @return 随机码实体
	 */
	SysRandCodeRecord findRecordByRandCode(@Param("randomCode") String randomCode);

	/**
	 * 获取一个可用的验证码
	 */
	SysRandCodeRecord getOneRandCode();

	/**
	 * @Description: 获取全部有效邀请码列表
	 * @throws ServiceException   
	 * @return List<String>  
	 * @author tangzj02
	 * @date 2017年3月1日
	 */
	List<String> findValidRandCodeList();

}
