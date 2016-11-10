package com.okdeer.mall.label;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.label.entity.ActivityLabel;
import com.okdeer.mall.activity.label.service.ActivityLabelService;
import com.okdeer.mall.base.BaseServiceTest;

//@RunWith(Parameterized.class)
public class ActivityLabelApiImplTest extends BaseServiceTest {
	
	@Autowired
	private ActivityLabelService service;
	
	@Test
	public void testList() {
		try {
			int pageNumber = 1;
			int pageSize = 10;
			Map<String,Object> params = new HashMap<String, Object>();
			params.put("status", "");
			PageUtils<ActivityLabel> page = service.list(params, pageNumber,
					pageSize);
//			Assert.assertEquals(2, page.getList().size());
			System.out.println("size:" + page.getList().size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSave() {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
