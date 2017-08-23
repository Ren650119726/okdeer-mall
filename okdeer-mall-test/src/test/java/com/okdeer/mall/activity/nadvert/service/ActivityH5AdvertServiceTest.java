package com.okdeer.mall.activity.nadvert.service;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.nadvert.bo.ActivityH5AdvertBo;
import com.okdeer.mall.activity.nadvert.bo.ActivityH5AdvertContentBo;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5Advert;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContent;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentCoupons;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertRole;
import com.okdeer.mall.activity.nadvert.param.ActivityH5AdvertQParam;
import com.okdeer.mall.base.BaseServiceTest;

public class ActivityH5AdvertServiceTest extends BaseServiceTest {
	
	@Autowired
	private ActivityH5AdvertService service;
	@Test
	public void testSave() {
		try {
			ActivityH5AdvertBo bo = new ActivityH5AdvertBo();
			ActivityH5Advert  advert = new ActivityH5Advert();
			advert.setName("h5活动标题");
			advert.setPageTitle("page title");
			bo.setAdvert(advert);
			
			List<ActivityH5AdvertRole> roles = new ArrayList<ActivityH5AdvertRole>();
			ActivityH5AdvertRole role = new ActivityH5AdvertRole();
			role.setContent("测试");
			roles.add(role);
			bo.setRoles(roles);
			
			List<ActivityH5AdvertContentBo> contents = new ArrayList<ActivityH5AdvertContentBo>();
			ActivityH5AdvertContentBo contentBo = new ActivityH5AdvertContentBo();
			ActivityH5AdvertContent content = new ActivityH5AdvertContent();
			content.setColorSetting("style:xxxx");
			contentBo.setContent(content);
			
			List<ActivityH5AdvertContentCoupons> coupons = new ArrayList<ActivityH5AdvertContentCoupons>();
			ActivityH5AdvertContentCoupons coupon = new ActivityH5AdvertContentCoupons();
			coupon.setCityId("666");
			coupon.setCollectCouponsId("123456");
			coupons.add(coupon);
			
			contentBo.setContentCoupons(coupons);
			contents.add(contentBo);
			bo.setContents(contents);

			service.save(bo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindById() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteById() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindByParam() {
		ActivityH5AdvertQParam param = new ActivityH5AdvertQParam();
		param.setName("标题");
		PageUtils<ActivityH5Advert> page = service.findByParam(param, 1, 10);
		System.out.println(page.getList().size());
	}
}
