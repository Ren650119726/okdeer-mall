package com.okdeer.mall.activity.coupons.api;

import java.util.List;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.service.ActivitySaleELServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivitySaleService;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivitySaleELServiceApi")
public class ActivitySaleServiceApiImpl implements ActivitySaleELServiceApi {
	
	private ActivitySaleService activitySaleService;
	@Override
	public void save(ActivitySale activitySale, List<ActivitySaleGoods> asgList)
			throws Exception {
		activitySaleService.save(activitySale, asgList);
	}

	@Override
	public void update(ActivitySale ActivitySale,
			List<ActivitySaleGoods> asgList) throws Exception {
		activitySaleService.update(ActivitySale, asgList);
	}
}
