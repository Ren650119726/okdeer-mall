/** 
	 *@Project: okdeer-mall-service 
 *@Author: yangq
 *@Date: 2016年11月2日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.base;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.okdeer.mall.Application;

/**
 * ClassName: BaseTest 
 * @Description: TODO
 * @author yangq
 * @date 2016年11月2日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持！
@SpringApplicationConfiguration(classes=Application.class)	// 指定我们SpringBoot工程的Application启动类
@WebAppConfiguration	// 测试环境使用，用来表示测试环境使用的ApplicationContext将是WebApplicationContext类型的；value指定web应用的根
@Transactional
public class BaseTest extends AbstractTransactionalJUnit4SpringContextTests{

	protected MockMvc mockMvc;
	
	/**
	 * 注入web环境的ApplicationContext容器
	 */
	@Resource
	protected WebApplicationContext webApplicationContext;
	
	/**
	 * 
	 * @Description: 创建mockMvc进行测试演示
	 * @author yangq
	 * @date 2016年11月2日
	 */
	@Before
	public void setup(){
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
	}
	
}
