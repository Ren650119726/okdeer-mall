package com.okdeer.mall.system.service;

import java.util.List;

import com.okdeer.base.common.exception.ServiceException;

/**
 * ClassName: SysRandCodeRecordService 
 * @Description: 随机码serviceApi
 * @author tangzj02
 * @date 2017年02月27日
 *
 * =================================================================================================
 *     Task ID            Date               Author           Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     V2.1           2017年02月27日                        tangzj02       添加
 */
public interface SysRandCodeRecordService {

	/**
	 * 获取随机码
	 * @return 随机码
	 * @throws ServiceException 异常
	 * @author tangzj02
	 * @date 2017年02月27日
	 */
	String findRecordByRandCode() throws ServiceException;

	/**
	 * @Description: 根据验证码进行删除
	 * @param code 已使用的验证码  
	 * @return void  
	 * @throws ServiceException
	 * @author tangzj02
	 * @date 2017年2月27日
	 */
	void deleteRecordByRandCodeByCode(String code) throws ServiceException;

	/**
	 * @Description: 获取全部有效邀请码列表
	 * @throws ServiceException   
	 * @return List<String>  
	 * @author tangzj02
	 * @date 2017年3月1日
	 */
	List<String> findValidRandCodeList() throws ServiceException;

}
