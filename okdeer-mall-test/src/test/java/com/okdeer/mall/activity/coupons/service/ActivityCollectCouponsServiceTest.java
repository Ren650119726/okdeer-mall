package com.okdeer.mall.activity.coupons.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.archive.system.service.ISysUserServiceApi;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.jxc.common.utils.DateUtils;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectXffqRelation;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.common.enums.GetUserType;
import com.okdeer.mall.order.enums.RefundType;


@Transactional
public class ActivityCollectCouponsServiceTest extends BaseServiceTest {
	
	@Resource
	private ActivityCollectCouponsServiceApi service;
	
	@Mock
	private ISysUserServiceApi iSysUserServiceApi;
	
	@Before
	public void setUp() throws Exception {
		// 初始化测试用例类中由Mockito的注解标注的所有模拟对象
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(service, "iSysUserServiceApi", iSysUserServiceApi);

	}

	
	//运营后台添加 代金券
	@Rollback(true)
	@Test
	public void testAdd() throws Exception{
		ActivityCollectCoupons co = new ActivityCollectCoupons();
		String id = UuidUtils.getUuid();
		co.setId(id);//主键id
		co.setName("代金券活动名字");
		co.setType(0);//'代金卷活动类型：0代金券领取活动，1注册活动，2开门成功送代金券活动，3 邀请注册送代金券活动，4 消费返券活动， 5广告领取活动，6鹿掌柜返券活动，7红包活动',
		co.setStatus(1); //'活动状态=0 未开始 ，1：进行中 2:已结束 3已失效(代理商提交过来的活动超时没审核就是已失效)  4 已关闭',
		co.setBelongType("0");//'所属代理商id，运营商以0标识',
		co.setLimitClientType(0);//'客户端类型限制：0无限制，1APP端 , 2微信端',
		co.setStartTime(DateUtils.parse("2017-09-13 00:00:00")); // '开始时间',
		co.setEndTime(DateUtils.parse("2017-09-22 00:00:00"));// '结束时间',  
		co.setDescription("这是备注 啊是的发生地方"); // '说明',
		co.setIdentityLimit(0); // '领取身份限制：0无限制，1限小区用户，2房间第一个住户',
		co.setAreaType(0);//'代金券范围类型：0全国，1区域，2小区 , 3店铺',
		co.setTotalCost(new BigDecimal(100));//'活动总费用',
		co.setApprovalStatus(1); //'审核状态：0未审核，1已通过，2审核未通过',
		co.setApprovalReason("");//'审核原因',
		co.setApprovalTime(new Date());//'审核时间',
		co.setApprovalUserId("1"); //'审核人',
		co.setRefundType(RefundType.UNREFUND);  //'退款类型：0未退款，1未领取已退款，2全部已退款',
		co.setDailyCirculation("100");//'每日发行量',
		co.setH5Url("http://www.baidu.com");//'Html5页面url,如果是邀请注册活动,必填',
		co.setLimitAmount(new BigDecimal(100)); //'订单结算金额满多少参与代金券活动',
		co.setGetUserType(GetUserType.ALLOW_All); // '领取用户类型限制：0无限制，1限新用户',
		co.setDeviceDayLimit(2);// '同一设备id每天最多使用张数(V2.4加)',
		co.setAccountDayLimit(2);// '同一帐号id每天最多使用张数(V2.4加)',
		co.setUserDayTimesLimit(1); //'同一用户每日最多领取次数(V2.6.1加)',
		co.setCreateUserId("1");
		co.setCreateTime(new Date());
		co.setUpdateUserId("1");
		co.setUpdateTime(new Date());
		co.setDisabled(Disabled.valid);//'是否失效：0否，1是',
		
		//一个代金券活动至少要关联一张代金券(代金券不能关联活动)
		List<String> couponsIds = new ArrayList<String>();
		String couponsId1 = "8a8080895e703ee2015e73df2145000d";
		couponsIds.add(couponsId1);
		
		String areaIds = "";
		List<ActivityCollectXffqRelation> xffqRelationList = new ArrayList<ActivityCollectXffqRelation>();
		service.save(co, couponsIds, areaIds, xffqRelationList);
		
		ActivityCollectCoupons insert = service.get(id);
		assertNotNull(insert);
	}

	//运营后台修改 代金券活动
	@Test
	public void testUpdate() throws Exception{
		ActivityCollectCoupons co = new ActivityCollectCoupons();
		String id = "8a94e7065e4c0fe9015e57293e340010";
		co.setId(id);//主键id
		co.setName("代金券活动名字aaaaaaaa");
		co.setUpdateUserId("1");
		co.setUpdateTime(new Date());
		
		//一个代金券活动至少要关联一张代金券(代金券不能关联活动)
		List<String> couponsIds = new ArrayList<String>();
		String couponsId1 = "8a8080895e703ee2015e73df2145000d";
		couponsIds.add(couponsId1);
		
		String areaIds = "";
		List<ActivityCollectXffqRelation> xffqRelationList = new ArrayList<ActivityCollectXffqRelation>();
		service.update(co, couponsIds, areaIds, xffqRelationList);
		
		ActivityCollectCoupons update = service.get(id);
		assertNotNull(update);
	}

	//通过id获取对象
	@Test
	public void get() throws Exception{
		String id = "8a94e7065e4c0fe9015e57293e340010";
		ActivityCollectCoupons co = service.get(id);
		assertNotNull(co);
	}

	//通过查询条件获取分页list (运营后台用)
	@Test
	public void list() throws Exception{
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("belongType", "0");
		param.put("name","");
		SysUser su = new SysUser();
		su.setUserName("admin");
		given(iSysUserServiceApi.findSysUserById(anyString())).willReturn(su);
		PageUtils<ActivityCollectCoupons> page = service.list(param, 1, 10);
		System.out.println("list.size:" + page.getList().size());
	}
}
