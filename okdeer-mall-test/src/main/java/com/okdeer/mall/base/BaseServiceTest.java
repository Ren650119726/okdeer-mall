package com.okdeer.mall.base;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.mall.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class BaseServiceTest {

	@Autowired
	protected ApplicationContext ac;
	
	private TestContextManager testContextManager;

	@Before
	public void setUpContext() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.testContextManager = new TestContextManager(getClass());
		this.testContextManager.prepareTestInstance(this);
		// 初始化mock对象信息
		initMocks();
	}
	
	protected void initMocks(){
		// 留给模板方法实现
	}
}
