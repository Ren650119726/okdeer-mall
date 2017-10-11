/** 
 *@Project: okdeer-mall-test 
 *@Author: guocp
 *@Date: 2017年7月29日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.system.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.springframework.test.util.AopTestUtils;
import org.springframework.test.util.ReflectionTestUtils;

import com.okdeer.archive.system.entity.SysSmsVerifyCode;
import com.okdeer.archive.system.service.SysSmsVerifyCodeServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.model.RequestParams;
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
@RunWith(Parameterized.class)
public class SysBuyerUserServiceImplTest extends BaseServiceTest {

	private SysBuyerUserServiceApi sysBuyerUserService;

	@Mock
	private ISysBuyerUserApi sysBuyerUserApi;

	@Mock
	private SysSmsVerifyCodeServiceApi sysSmsVerifyCodeService;

	private String mobilePhone;

	private String password;

	private String verifyCode;

	private int resultCode;

	private int index;

	public SysBuyerUserServiceImplTest(int index, String mobilePhone, String password, String verifyCode,
			int resultCode) {
		this.mobilePhone = mobilePhone;
		this.password = password;
		this.verifyCode = verifyCode;
		this.resultCode = resultCode;
		this.index = index;
	}

	@Parameters
	public static Collection<Object[]> t() throws Exception {
		return Arrays.asList(new Object[][] { { 0, "18682310941", "123456", null, 0 }, // 密码登入正确
				{ 1, "18682310941", "123455", null, 3 }, // 密码登入错误
				{ 2, "18682310940", "123456", null, 1 }, // 密码登入手机号不存在
				{ 3, "18682310900", "123456", null, 2 }, // 密码登入手机号不存在

				{ 4, "18682310941", null, "1234", 0 }, // 验证码登入正确
				{ 5, "18682310941", null, "1235", 12 }, // 验证码登入错误
				{ 6, "18682310940", null, "1234", 12 }, // 验证码登入手机号错误错误
				{ 7, "18682310900", null, "1234", 11 }, // 验证码过期
		});
	}

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
	protected void initMocks() throws Exception {
		// mock 登入用户
		mockSysBuyerUser();
		// mock 登入验证码
		mockVerifyCode();
	}

	/**
	 * @Description: mock   
	 * @author guocp
	 * @throws ServiceException 
	 * @date 2017年8月30日
	 */
	private void mockSysBuyerUser() throws ServiceException {
		String mobilePhone = "18682310941";
		String mobilePhone1 = "18682310900";
		String verifyCode = "1234";

		// mock 正常验证码
		SysSmsVerifyCode sysSmsVerifyCode = new SysSmsVerifyCode();
		sysSmsVerifyCode.setVerifyCode(verifyCode);
		sysSmsVerifyCode.setStatus(0);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("phoneSearch", mobilePhone);
		params.put("typeSearch", 1);
		params.put("bussinessTypeSearch", VerifyCodeBussinessTypeEnum.LOGIN.getCode());
		given(sysSmsVerifyCodeService.findLatestByParams(params)).willReturn(sysSmsVerifyCode);

		// mock 验证码过期
		SysSmsVerifyCode sysSmsVerifyCode1 = new SysSmsVerifyCode();
		sysSmsVerifyCode1.setVerifyCode(verifyCode);
		sysSmsVerifyCode1.setStatus(1);

		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("phoneSearch", mobilePhone1);
		params1.put("typeSearch", 1);
		params1.put("bussinessTypeSearch", VerifyCodeBussinessTypeEnum.LOGIN.getCode());
		given(sysSmsVerifyCodeService.findLatestByParams(params1)).willReturn(sysSmsVerifyCode1);
	}

	/**
	 * @Description: mock   
	 * @author guocp
	 * @throws ApiException 
	 * @date 2017年8月30日
	 */
	private void mockVerifyCode() throws ApiException {
		String mobilePhone = "18682310941";
		String mobilePhone1 = "18682310900";
		String password = "123456";
		// mock 正常密码
		SysBuyerUserItemDto sysBuyerUserItemDto = new SysBuyerUserItemDto();
		sysBuyerUserItemDto.setPhone(mobilePhone);
		sysBuyerUserItemDto.setLoginPassword(EncryptionUtils.md5(password));
		given(sysBuyerUserApi.login(mobilePhone, null)).willReturn(sysBuyerUserItemDto);

		// mock login 密码为空
		SysBuyerUserItemDto sysBuyerUserItemDto1 = new SysBuyerUserItemDto();
		sysBuyerUserItemDto1.setPhone(mobilePhone1);
		given(sysBuyerUserApi.login(mobilePhone1, null)).willReturn(sysBuyerUserItemDto1);
	}

	/**
	 * 测试用户登入
	 * @throws Exception   
	 * @author guocp
	 * @date 2017年7月29日
	 */
	@Test
	public void testValidationPwd() throws Exception {
		BuyerUserVo buyerUserVo = new BuyerUserVo();
		buyerUserVo.setLoginName(DESUtils.encrypt(mobilePhone));
		if (this.password != null) {
			buyerUserVo.setLoginPassword(DESUtils.encrypt(password));
		}
		buyerUserVo.setVerifyCode(verifyCode);
		buyerUserVo.setVerifyCodeType(1);
		int result = sysBuyerUserService.loginValidation(buyerUserVo);
		assertEquals(resultCode, result);
	}
		
	@Test
	public void testSaveBuyerUserAndLog() throws Exception{
		RequestParams parameters = new RequestParams();
		//便利店
		parameters.setClientType("3");
		parameters.setBrand("iphone");
		parameters.setClientVersion("V2.6.0");
		parameters.setMachineCode("E3307355-C835-4A34-95F8-6BAFFC2B4A0B");
		parameters.setToken("8a94e7185b6a803c015b6a803cc30000");
		Map<String, Object> result = sysBuyerUserService.saveBuyerUserAndLog(parameters, mobilePhone);
		assertNotNull(result);
	}
	

}
