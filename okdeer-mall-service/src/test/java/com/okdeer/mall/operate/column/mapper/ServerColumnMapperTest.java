/** 
 *@Project: yschome-mall-service 
 *@Author: luosm
 *@Date: 2016年7月18日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.column.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.mall.operate.entity.ServerColumn;
import com.okdeer.mall.Application;


/**
 * ClassName: ServerColumnMapperTest 
 * @Description: TODO
 * @author luosm
 * @date 2016年7月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = Application.class)
public class ServerColumnMapperTest {

	@Resource
	private ServerColumnMapper serverColumnMapper;
	/**
	 * Test method for {@link com.okdeer.mall.operate.column.mapper.ServerColumnMapper#userAppGetById(java.util.List)}.
	 */
//	@Test
//	public void testUserAppGetById() {
//		List<String> ids = new ArrayList<String>();
//		ids.add("8a94e41755fc97860155fc9786090000");
//		List<ServerColumn> list = serverColumnMapper.userAppGetById(ids);
//		System.out.println(list.size());
////		fail("Not yet implemented");
//	}

	public static void main(String[] args){
		System.out.println(System.getProperty("user.home"));
	}
}
