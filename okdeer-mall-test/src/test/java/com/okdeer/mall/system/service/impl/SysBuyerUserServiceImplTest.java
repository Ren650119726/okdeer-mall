/** 
 *@Project: okdeer-mall-test 
 *@Author: guocp
 *@Date: 2017年7月29日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.system.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.AopTestUtils;
import org.springframework.test.util.ReflectionTestUtils;

import com.okdeer.archive.system.entity.SysSmsVerifyCode;
import com.okdeer.archive.system.service.SysSmsVerifyCodeServiceApi;
import com.okdeer.base.common.utils.EncryptionUtils;
import com.okdeer.base.common.utils.security.DESUtils;
import com.okdeer.ca.api.buyeruser.entity.SysBuyerUserItemDto;
import com.okdeer.ca.api.buyeruser.service.ISysBuyerUserApi;
import com.okdeer.ca.api.common.ApiException;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.system.entity.BuyerUserVo;
import com.okdeer.mall.system.enums.VerifyCodeBussinessTypeEnum;
import com.okdeer.mall.system.service.SysBuyerUserServiceApi;

/**
 * ClassName: SysBuyerUserServiceImplTest 
 * @Description: 买家用户测试用例（用户登入）
 * @author guocp
 * @date 2017年7月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class SysBuyerUserServiceImplTest extends BaseServiceTest {

	private SysBuyerUserServiceApi sysBuyerUserService;

	@Mock
	private ISysBuyerUserApi sysBuyerUserApi;
	
	@Mock
	private SysSmsVerifyCodeServiceApi sysSmsVerifyCodeService;
	
	
	private String mobilePhone;
	
	private String password;
	
	private String verifyCode;

	/**
	 * Before
	 * @author guocp
	 * @date 2017年7月31日
	 */
	@Before
	public void setUp() {
		sysBuyerUserService = (SysBuyerUserServiceApi) AopTestUtils.getTargetObject(
				(SysBuyerUserServiceApi) this.applicationContext.getBean(SysBuyerUserServiceApi.class));
		ReflectionTestUtils.setField(sysBuyerUserService, "sysBuyerUserApi", sysBuyerUserApi);
		ReflectionTestUtils.setField(sysBuyerUserService, "sysSmsVerifyCodeService", sysSmsVerifyCodeService);
	}
	
	/**
	 * 初始mock 数据
	 * (non-Javadoc)
	 * @see com.okdeer.mall.base.BaseServiceTest#initMocks()
	 */
	protected void initMocks() throws Exception{
		String mobilePhone = "18682310941";
		String password = "123456";
		String verifyCode = "1234";
		
		//mock login
		SysBuyerUserItemDto sysBuyerUserItemDto = new SysBuyerUserItemDto();
		sysBuyerUserItemDto.setPhone(mobilePhone);
		sysBuyerUserItemDto.setLoginPassword(EncryptionUtils.md5(password));
		given(sysBuyerUserApi.login(mobilePhone, null)).willReturn(sysBuyerUserItemDto);
		
		//mock findLatestByParams
		SysSmsVerifyCode sysSmsVerifyCode = new SysSmsVerifyCode();
		sysSmsVerifyCode.setVerifyCode(verifyCode);
		sysSmsVerifyCode.setStatus(0);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("phoneSearch", mobilePhone);
		params.put("typeSearch", 1);
		params.put("bussinessTypeSearch", VerifyCodeBussinessTypeEnum.LOGIN.getCode());
		given(sysSmsVerifyCodeService.findLatestByParams(params)).willReturn(sysSmsVerifyCode);
	}

	/**
	 * 测试用户登入（用户名+密码）
	 * @throws Exception   
	 * @author guocp
	 * @date 2017年7月29日
	 */
	@Test
	public void testLoginValidationWithPwd() throws Exception {
		String mobilePhone = "18682310941";
		String password = "123456";
		
		//测试验证用户名
		validationPwd(mobilePhone, DESUtils.encrypt(password), null);
	}

	/**
	 * 测试用户登入（用户名+验证码）
	 * @throws Exception   
	 * @author guocp
	 * @date 2017年7月29日
	 */
	@Test
	public void testLoginValidationWithVerifyCode() throws Exception {
		String mobilePhone = "18682310941";
		String verifyCode = "1234";
		
		//测试验证用户名
		validationPwd(mobilePhone, null, verifyCode);
	}
	
	
	private void validationPwd(String mobilePhone,String loginPassword,String verifyCode) throws Exception{
		BuyerUserVo buyerUserVo = new BuyerUserVo();
		buyerUserVo.setLoginName(DESUtils.encrypt(mobilePhone));
		buyerUserVo.setLoginPassword(loginPassword);
		buyerUserVo.setVerifyCode(verifyCode);
		buyerUserVo.setVerifyCodeType(1);
		int result = sysBuyerUserService.loginValidation(buyerUserVo);
		assertEquals(0, result);
	}
	
	
}
