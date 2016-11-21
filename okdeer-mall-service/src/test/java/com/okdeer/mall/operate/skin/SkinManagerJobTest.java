package com.okdeer.mall.operate.skin;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.operate.service.SkinManagerApi;

/**
 * 
 * ClassName: SkinManagerJobTest 
 * @Description: 换肤job单元测试类
 * @author tuzhd
 * @date 2016年11月12日
 *
 * =================================================================================================
 *     Task ID			  Date			  Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.2		  2016年11月12日		tuzhiding			换肤job单元测试类
 */
//@RunWith(Parameterized.class)
public class SkinManagerJobTest extends BaseServiceTest {
	@Autowired
	SkinManagerApi skinManagerService;

	@Test
	public void testProcess() {
		skinManagerService.processSkinActivityJob();
	}
}
