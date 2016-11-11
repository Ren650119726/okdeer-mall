/** 
 *@Project: okdeer-mall-service 
 *@Author: yangq
 *@Date: 2016年11月2日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */

package com.okdeer.mall.order.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.Assert;

import com.okdeer.archive.system.entity.SysMsg;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.base.BaseTest;
import com.okdeer.mall.common.enums.IsRead;
import com.okdeer.mall.system.mapper.SysMsgMapper;

/**
 * ClassName: TradeOrderMapperTest 
 * @Description: TODO
 * @author yangq
 * @date 2016年11月2日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public class SysMsgMapperTest extends BaseTest {

	@Resource
	private SysMsgMapper sysMsgMapper;

	/**
	 * 
	 * @Description: 新增
	 * @author yangq
	 * @date 2016年11月2日
	 */
	@Test
	@Rollback(false)
	public void testAdd() {

		SysMsg msg = new SysMsg();

		msg.setId(UuidUtils.getUuid());
		msg.setCreateTime(new Date());
		msg.setIsRead(IsRead.READ);
		msg.setStoreId("654321");
		int result = sysMsgMapper.insertSelective(msg);
		
		boolean flag = true;
		if (result > 0) {
			Assert.isTrue(flag, "insert sysMsg data sucess");
		} else {
			Assert.isTrue(flag, "insert sysMsg data fail");
		}

	}
	
	/**
	 * 
	 * @Description: 修改
	 * @author yangq
	 * @date 2016年11月2日
	 */
	@Test
	@Rollback(true)
	public void testUpdate(){
		
		SysMsg sysMsg = new SysMsg();
		
		sysMsg.setId("2c90908c562b93ba01562b93bc9f0001");
		sysMsg.setTitle("下单通知消息");
		int result = sysMsgMapper.updateByPrimaryKeySelective(sysMsg);
		boolean flag = true;
		if(result > 0){
			Assert.isTrue(flag, "update sysMsg data success");
		}else{
			Assert.isTrue(flag, "update sysMsg data fail");
		}
		
	}

	/**
	 * 
	 * @Description: 删除
	 * @author yangq
	 * @date 2016年11月2日
	 */
	@Test
	@Rollback(true)
	public void testDelete(){
		String id = "2c90908c562b93ba01562b93bc9f0001";
		int result = sysMsgMapper.deleteByPrimaryKey(id);
		boolean flag = true;
		if(result > 0){
			Assert.isTrue(flag,"delete sysMsg data success");
		}else{
			Assert.isTrue(flag,"delete sysMsg data fail");
		}
	}
	
}
