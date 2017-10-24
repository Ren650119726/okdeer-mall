package com.okdeer.mall.base;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.okdeer.mall.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public abstract class BaseServiceTest {

	@Autowired
	protected ApplicationContext applicationContext;
	
	protected TestContextManager testContextManager;

	@Before
	public void setUpContext() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.testContextManager = new TestContextManager(getClass());
		this.testContextManager.prepareTestInstance(this);
		// 初始化mock对象信息
		initMocks();
	}
	
	
	protected void initMocks() throws Exception{
		// 留给模板方法实现
	}
	
	protected void beforeMethod(Object testInstance, String methodName) throws Exception{
		assertNotNull("methodName must not null", methodName);
		Method testMethod = Arrays.asList(testInstance.getClass().getDeclaredMethods()).stream()
				.filter(e -> methodName.equals(e.getName())).findFirst().get();
		this.testContextManager.beforeTestMethod(testInstance,testMethod);
	}
	
	protected void afterTestMethod(Object testInstance, String methodName) throws Exception{
		assertNotNull("methodName must not null", methodName);
		Method testMethod = Arrays.asList(testInstance.getClass().getDeclaredMethods()).stream()
				.filter(e -> methodName.equals(e.getName())).findFirst().get();
		this.testContextManager.afterTestMethod(testInstance, testMethod, new RuntimeException());
	}
}
