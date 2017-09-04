package com.okdeer.mall.activity.nadvert.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.archive.goods.store.dto.GoodsStoreActivitySkuDto;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.base.BaseServiceTest;

public class ActivityH5AdvertContentGoodsServiceTest extends BaseServiceTest {
	@Autowired
	private ActivityH5AdvertContentGoodsService goodsService;
	@Test
	public void testFindBldGoodsByActivityId() {
		String storeId = "558c8935276511e6aaff00163e010eb1";
		String activityId = "8a94e7c75e1da75e015e1daa8fa00004";
		String contentId = "8a94e7c75e1da75e015e1daa8fc90009";
		PageUtils<GoodsStoreActivitySkuDto> page = goodsService.findBldGoodsByActivityId(storeId, activityId, contentId, 1, 10);
		System.out.println(page.getList().size());
	}
}
