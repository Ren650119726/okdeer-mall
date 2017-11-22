package com.okdeer.mall.activity.sale.service;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.jxc.common.utils.DateUtils;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.service.ActivitySaleELServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivitySaleServiceApi;
import com.okdeer.mall.base.BaseServiceTest;


@Transactional
public class ActivitySaleServiceTest extends BaseServiceTest {
	
	@Autowired
	private ActivitySaleELServiceApi service;
	@Autowired
	private ActivitySaleServiceApi saleApi;
	
	//商家中心添加特惠
	@Rollback(true)
//	@Test
	public void add() throws Exception{
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

//	@Rollback(true)
	@Test
	public void list() throws Exception{
		String storeId = "56583c03276511e6aaff00163e010eb1";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("createStartTime", "2016-01-01 00:00:00");
		params.put("createEndTime", "2017-12-31 23:59:59");
		
		params.put("startStartTime", "2016-01-01 00:00:00");
		params.put("startEndTime", "2017-12-31 23:59:59");
		
		params.put("endStartTime", "2016-01-01 00:00:00");
		params.put("endEndTime", "2017-12-31 23:59:59");

		params.put("storeId", storeId);
		
		params.put("type", ActivityTypeEnum.SALE_ACTIVITIES.ordinal());

		// 当前页数
		Integer pageNumber = 1;

		// 每页显示条数
		Integer pageSize = 20;
		// 条件分页查询
		PageUtils<ActivitySale> page = saleApi.pageList(params, pageNumber, pageSize);
		
		System.out.println("测试特惠活动列表：" + page.getRows().size());
		
	}
	
	@Test
	public void get() throws Exception{
		String id = "8a8080075fc38465015fc384659c0000";
		ActivitySale obj = saleApi.get(id);
		if(obj != null){
			System.out.println("测试特惠活动get：" + obj.getName());
		}
	}
	
	@Test
	public void findByPrimaryKey() throws Exception{
		String id = "8a8080075fc38465015fc384659c0000";
		ActivitySale obj = saleApi.findByPrimaryKey(id);
		if(obj != null){
			System.out.println("测试特惠活动findByPrimaryKey：" + obj.getName());
		}
	}
	
	@Test
	public void getAcSaleStatus() throws Exception{
		String id = "8a8080075fc38465015fc384659c0000";
		ActivitySale obj = saleApi.getAcSaleStatus(id);
		if(obj != null){
			System.out.println("测试特惠活动getAcSaleStatus：" + obj.getName());
		}
	}
	
	@Test
	public void selectActivitySale() throws Exception{
		String id = "8a8080075fc38465015fc384659c0000";
		int limit = saleApi.selectActivitySale(id);
		System.out.println("测试特惠活动selectActivitySale：" + limit);
	}
	
	@Test
	public void ListActivitySaleGoods() throws Exception{
		String id = "8a8080075fc38465015fc384659c0000";
		List<ActivitySaleGoods> list = saleApi.listActivitySaleGoods(id);
		System.out.println("测试特惠活动listActivitySaleGoods：" + list.size());
	}
	
	@Test
	public void listGoodsStoreSku() throws Exception{
		String storeId = "56583c03276511e6aaff00163e010eb1";
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("storeId", storeId);
		List<Map<String, Object>> list = saleApi.listGoodsStoreSku(params) ;
		System.out.println("测试特惠活动listGoodsStoreSku：" + list.size());
	}
	
	@Test
	public void pageListGoodsStoreSku() throws Exception{
		String storeId = "56583c03276511e6aaff00163e010eb1";
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("storeId", storeId);
		PageUtils<Map<String, Object>> page = saleApi.pageListGoodsStoreSku(params,1,10) ;
		System.out.println("测试特惠活动pageListGoodsStoreSku：" + page.getRows().size());
	}
	@Test
	public void pageListGoodsStoreSkuV220() throws Exception{
		String storeId = "56583c03276511e6aaff00163e010eb1";
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("storeId", storeId);
		PageUtils<Map<String, Object>> page = saleApi.pageListGoodsStoreSkuV220(params,1,10) ;
		System.out.println("测试特惠活动pageListGoodsStoreSkuV220：" + page.getRows().size());
	}
	@Test
	public void validateExist() throws Exception{
		String storeId = "56583c03276511e6aaff00163e010eb1";
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("storeId", storeId);
		int count = saleApi.validateExist(params) ;
		System.out.println("测试特惠活动validateExist：" + count);
	}
	@Test
	public void listByStoreId() throws Exception{
		String storeId = "56583c03276511e6aaff00163e010eb1";
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("storeId", storeId);
//		params.put("type", storeId);
		List<ActivitySale> list = saleApi.listByStoreId(params) ;
		System.out.println("测试特惠活动listByStoreId：" + list.size());
	}
	@Test
	public void findActivitySaleByStoreId() throws Exception{
		String storeId = "56583c03276511e6aaff00163e010eb1";
		ActivitySale ac = saleApi.findActivitySaleByStoreId(storeId,5,1);
		if(ac == null){
			System.out.println("测试特惠活动findActivitySaleByStoreId：没有活动" );
		} else {
			System.out.println("测试特惠活动findActivitySaleByStoreId：" + ac.getName());
		}
	}
	@Test
	public void findLowPriceActivitySaleByStoreId() throws Exception{
		String storeId = "56583c03276511e6aaff00163e010eb1";
		ActivitySale ac = saleApi.findLowPriceActivitySaleByStoreId(storeId);
		if(ac == null){
			System.out.println("测试特惠活动findLowPriceActivitySaleByStoreId：没有活动" );
		} else {
			System.out.println("测试特惠活动findLowPriceActivitySaleByStoreId：" + ac.getName());
		}
	}
}
