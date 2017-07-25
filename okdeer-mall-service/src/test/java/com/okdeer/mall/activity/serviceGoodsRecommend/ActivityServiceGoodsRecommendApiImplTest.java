package com.okdeer.mall.activity.serviceGoodsRecommend;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.serviceGoodsRecommend.entity.ActivityServiceGoodsRecommend;
import com.okdeer.mall.activity.serviceGoodsRecommend.service.ActivityServiceGoodsRecommendService;
import com.okdeer.mall.base.BaseServiceTest;

@RunWith(Parameterized.class)
public class ActivityServiceGoodsRecommendApiImplTest extends BaseServiceTest {
	
	@Autowired
	private ActivityServiceGoodsRecommendService service;
	
	private Map<String,Object> params;
	
	private int pageNumber;
	
	private int pageSize;
	
	public ActivityServiceGoodsRecommendApiImplTest(int pageNumber,int pageSize,Map<String,Object> params){
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.params = params;
	}
	
	@Parameters
	public static Collection<Object[]> initParam(){
		int pageNumber = 1;
		int pageSize = 10;
		Map<String,Object> param1 = new HashMap<String, Object>();
		param1.put("status", 1);
		Map<String,Object> param2 = new HashMap<String, Object>();
		param2.put("status", 2);
		
		return Arrays.asList(new Object[][]{
			{pageNumber,pageSize,param1},
			{pageNumber,pageSize,param2}
		});
	}
	
	@Test
	public void testList() {
		try {
			PageUtils<ActivityServiceGoodsRecommend> page = service.list(params, pageNumber,
					pageSize);
			Assert.assertEquals(2, page.getList().size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSave() {
		/*try {
			ActivityServiceGoodsRecommend r = new ActivityServiceGoodsRecommend();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
}
