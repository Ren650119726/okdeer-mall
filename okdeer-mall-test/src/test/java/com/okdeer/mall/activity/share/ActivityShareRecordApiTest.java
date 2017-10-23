
package com.okdeer.mall.activity.share;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.activity.share.dto.ActivityShareRecordDto;
import com.okdeer.mall.activity.share.dto.ActivityShareRecordParamDto;
import com.okdeer.mall.activity.share.service.ActivityShareRecordApi;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.base.MockUtils;
import com.okdeer.mall.system.service.SysBuyerUserService;

@Transactional
public class ActivityShareRecordApiTest extends BaseServiceTest {

	@Resource
	private ActivityShareRecordApi activityShareRecordApi;

	@Mock
	SysBuyerUserService sysBuyerUserService;

	@Mock
	ActivityDiscountService activityDiscountService;

	@Mock
	GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	@Before
	public void setUp() throws Exception {
		// 初始化测试用例类中由Mockito的注解标注的所有模拟对象
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(activityShareRecordApi, "sysBuyerUserService", sysBuyerUserService);
		ReflectionTestUtils.setField(activityShareRecordApi, "activityDiscountService", activityDiscountService);
		ReflectionTestUtils.setField(activityShareRecordApi, "goodsStoreSkuServiceApi", goodsStoreSkuServiceApi);
	}

	/**
	 * @Description: 测试添加团购活动分享记录
	 * @throws Exception
	 * @author zengjizu
	 * @date 2017年10月21日
	 */
	@Test
	@Rollback(false)
	public void testAddGroupActivityShareRecord() throws Exception {
		ActivityDiscount activityDiscount = MockUtils
				.getMockSingleData("/com/okdeer/mall/mock/mock-activitydiscount.json", ActivityDiscount.class);
		GoodsStoreSku goodsStoreSku = MockUtils.getMockSingleData("/com/okdeer/mall/mock/mock-goodsStoreSku.json",
				GoodsStoreSku.class);
		SysBuyerUser sysBuyerUser = MockUtils.getMockSingleData("/com/okdeer/mall/mock/mock-sysBuyerUser.json",
				SysBuyerUser.class);
		given(this.sysBuyerUserService.findByPrimaryKey(anyString())).willReturn(sysBuyerUser);
		given(this.activityDiscountService.findById(anyString())).willReturn(activityDiscount);
		given(this.goodsStoreSkuServiceApi.getById(anyString())).willReturn(goodsStoreSku);
		activityShareRecordApi.addGroupActivityShareRecord(sysBuyerUser.getId(), goodsStoreSku.getId(),
				activityDiscount.getId());
	}

	/**
	 * @Description: 测试查询列表
	 * @author zengjizu
	 * @date 2017年10月21日
	 */
	@Test
	public void testFindPageList() {
		ActivityShareRecordParamDto activityShareRecordParamDto = new ActivityShareRecordParamDto();
		PageUtils<ActivityShareRecordDto> pageUtils = activityShareRecordApi.findPageList(activityShareRecordParamDto,
				1, 10);
		assertTrue(pageUtils != null);
	}
}
