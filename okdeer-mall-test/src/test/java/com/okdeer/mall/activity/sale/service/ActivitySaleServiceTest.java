package com.okdeer.mall.activity.sale.service;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.jxc.common.utils.DateUtils;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.entity.CouponsInfoParams;
import com.okdeer.mall.activity.coupons.entity.CouponsInfoQuery;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.enums.CashDelivery;
import com.okdeer.mall.activity.coupons.enums.CategoryLimit;
import com.okdeer.mall.activity.coupons.service.ActivitySaleELServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivitySaleServiceApi;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.common.enums.UseClientType;
import com.okdeer.mall.common.enums.UseUserType;


@Transactional
public class ActivitySaleServiceTest extends BaseServiceTest {
	
	@Autowired
	private ActivitySaleELServiceApi service;
	@Autowired
	private ActivitySaleServiceApi saleApi;
	
	//商家中心添加特惠
	@Rollback(true)
	@Test
	public void testAdd() throws Exception{
		ActivitySale as = new ActivitySale();
		String id = UuidUtils.getUuid();
		String storeId = "56583c03276511e6aaff00163e010eb1";
		as.setId(id);//主键id
		as.setName("特惠活动");
		as.setStoreId(storeId);
		as.setStartTime(DateUtils.parse("2017-09-30 00:00:00")); // '有效开始时间',
		as.setEndTime(DateUtils.parse("2017-10-01 00:00:00"));// '有效结束时间',
		as.setCreateUserId("1");
		as.setCreateTime(new Date());
		as.setUpdateUserId("1");
		as.setUpdateTime(new Date());
		as.setDisabled(Disabled.valid);//'是否失效：0否，1是',
		as.setType(ActivityTypeEnum.SALE_ACTIVITIES);// '活动类型5:特惠活动,7:低价抢购'
		as.setH5Url("");
		
		List<ActivitySaleGoods> list = new ArrayList<ActivitySaleGoods>();
		ActivitySaleGoods asg = new ActivitySaleGoods();
		asg.setId(UuidUtils.getUuid());
		asg.setSaleId(id);
		asg.setCreateUserId("1");
		asg.setCreateTime(new Date());
		asg.setUpdateUserId("1");
		asg.setUpdateTime(new Date());
		asg.setDisabled(Disabled.valid);//'是否失效：0否，1是',
		asg.setGoodsSkuId("001f47bf276511e6aaff00163e010eb1");
		asg.setIsRemind(0);
		asg.setSalePrice(new BigDecimal(1.11));
		asg.setSaleStock(11);
		asg.setSecurityStock(124);
		asg.setSellable(699);
		asg.setSort(999);
		asg.setTradeMax(1000);
		list.add(asg);
		service.save(as, list);
		ActivitySale insert = saleApi.get(id);
		assertNotNull(insert);
	}

//	//运营后台修改 代金券
//	@Test
//	public void testUpdate() throws Exception{
//		//运营后台编辑 代金券
//		CouponsInfoQuery co = new CouponsInfoQuery();
//		String id = "8a8080895e703ee2015e73df2145000d";
//		co.setId(id);//主键id
//		co.setName("我修改一下代金券名字");
//		co.setIsRandCode(WhetherEnum.not.ordinal());
//		co.setUpdateTime(new Date());
//		service.updateCoupons(co);
//		
//		ActivityCoupons update = service.getById(id);
//		assertNotNull(update);
//	}
//
//	//通过id获取对象(同时获取其他关联信息,运营后台用)
//	@Test
//	public void getCouponsInfoById() throws Exception{
//		String id = "8a8080895e703ee2015e73df2145000d";
//		CouponsInfoQuery co = service.getCouponsInfoById(id);		
//		assertNotNull(co);
//	}
//
//	//通过查询条件获取分页list (运营后台用)
//	@Test
//	public void getCouponsInfo() throws Exception{
//		CouponsInfoParams param = new CouponsInfoParams();
//		param.setBelongType("0");
//		param.setName("");
//		PageUtils<CouponsInfoQuery> page = service.getCouponsInfo(param,1,10);
//		System.out.println("list.size:" + page.getList().size());
//	}
}
