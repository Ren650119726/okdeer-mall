package com.okdeer.mall.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.archive.system.entity.SysMsg;
import com.okdeer.archive.system.entity.SysMsgVo;
import com.okdeer.base.dal.IBaseCrudMapper;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-24 11:57:36
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface SysMsgMapper extends IBaseCrudMapper {

	/**
	 * 查询店铺未读消息数量
	 */
	int selectUnReadMsg(String storeId);
	
	/**
	 * 获得最新的消息
	 *
	 * @param storeId 店铺Id
	 * @param count 消息条数
	 * @return 消息VO
	 */
	List<SysMsgVo> selectNearestMsg(@Param("storeId")String storeId, @Param("count")int count);
	
	/**
	 * 插入订单消息到消息中心
	 * 
	 * @author yangq
	 * @param sysMsg
	 */
	void insertSysMsgWithOrder(SysMsg sysMsg); 
	
}