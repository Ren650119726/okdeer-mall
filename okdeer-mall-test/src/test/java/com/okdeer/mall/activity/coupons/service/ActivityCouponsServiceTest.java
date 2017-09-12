package com.okdeer.mall.activity.coupons.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import com.okdeer.mall.activity.coupons.entity.CouponsInfoQuery;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.coupons.enums.CashDelivery;
import com.okdeer.mall.activity.coupons.enums.CategoryLimit;
import com.okdeer.mall.activity.nadvert.bo.ActivityH5AdvertBo;
import com.okdeer.mall.activity.nadvert.bo.ActivityH5AdvertContentBo;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5Advert;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContent;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentCoupons;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertRole;
import com.okdeer.mall.activity.nadvert.param.ActivityH5AdvertQParam;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.common.enums.UseClientType;
import com.okdeer.mall.common.enums.UseUserType;


@Transactional
public class ActivityCouponsServiceTest extends BaseServiceTest {
	
	@Autowired
	private ActivityCouponsServiceApi service;
	
	//运营后台添加 代金券
	@Rollback(true)
	@Test
	public void testAdd() throws Exception{
		ActivityCoupons co = new ActivityCoupons();
		String id = UuidUtils.getUuid();
		co.setId(id);//主键id
		co.setName("代金券名字");
		co.setBelongType("0");//0是运营商发的
		co.setFaceValue(5);//面额
		co.setTotalNum(10);//总发行量
		co.setUsedNum(1);//已经使用数量
		co.setRemainNum(9);//剩余数量
		co.setArriveLimit(0);//'使用条件（满多少可以使用）',
		co.setValidDay(3); // '有效天数',
		co.setEveryLimit(1);//'每人限领数量，0表示不限',
		co.setIsCashDelivery(CashDelivery.yes); //'是否支持货到付款: 0否，1是',
		co.setIsCategoryLimit(CategoryLimit.no); //'是否限制使用类目：0否，1是',
		co.setCode("");//'代金券编码',
		co.setExchangeCode("");// '代金券兑换码',
		co.setAreaType(AreaType.national);//'区域类型：0全国，1区域，2小区 , 3店铺',
		co.setCreateUserId("1");
		co.setCreateTime(new Date());
		co.setUpdateUserId("1");
		co.setUpdateTime(new Date());
		co.setDisabled(Disabled.valid);//'是否失效：0否，1是',
		co.setType(ActivityCouponsType.coupons.ordinal());// 代金券类型:0 友门鹿通用代金券1 便利店专用代金券2 服务店专用代金券 3手机冲值专用代金券 4 异业合作代金券", 5 电影专用代金券,6 便利店运费券',
		co.setIsCategory(0);// '0全部分类 1指定分类 (便利店是导航分类,服务店是商品分类)',
		co.setIsRandCode(0); //'是否有随机码：0否，1是',
		co.setDescription("这是备注 啊是的发生地方"); // '说明',
		co.setStartTime(DateUtils.parse("2017-09-13 00:00:00")); // '有效开始时间',
		co.setEndTime(DateUtils.parse("2017-09-22 00:00:00"));// '有效结束时间',
		co.setUseUserType(UseUserType.ALLOW_All);// '使用用户限制：0不限，1限新用户',
		co.setUseClientType(UseClientType.ALLOW_All);  //'代金劵使用客户端限制：0通用不限制,1仅限app端使用,,2仅微信端使用',
		co.setDeviceDayLimit(1); // '同一设备id每天最多使用张数(V2.4加)',
		co.setAccountDayLimit(1); // '同一帐号id每天最多使用张数(V2.4加)',
//		co.sete  `effect_day` int(11) DEFAULT '0' COMMENT '生效天数：设置领取多少天以后生效。0标识当天生效',
//		  `term_type` int(4) DEFAULT '0' COMMENT '代金券有效期限设置类型。0：设置领取后多少天生效，生效后多少天失效。1：设置设置有效时间范围',
		
		service.addCoupons(co);
		ActivityCoupons insert = service.getById(id);
		assertNotNull(insert);
	}

	//运营后台修改 代金券
	@Test
	public void testUpdate() throws Exception{
		//运营后台编辑 代金券
		CouponsInfoQuery co = new CouponsInfoQuery();
		String id = "8a8080895e703ee2015e73df2145000d";
		co.setId(id);//主键id
		co.setName("我修改一下代金券名字");
		co.setIsRandCode(WhetherEnum.not.ordinal());
		co.setUpdateTime(new Date());
		service.updateCoupons(co);
		
		ActivityCoupons update = service.getById(id);
		assertNotNull(update);
	}

	@Test
	public void testFindById() {
//		fail("Not yet implemented");
	}

//	@Test
//	public void testDeleteById() {
//		fail("Not yet implemented");
//	}

//	@Test
//	public void testFindByParam() {
//		ActivityH5AdvertQParam param = new ActivityH5AdvertQParam();
//		param.setName("标题");
//		PageUtils<ActivityH5Advert> page = service.findByParam(param, 1, 10);
//		System.out.println(page.getList().size());
//	}
}
