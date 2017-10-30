package com.okdeer.mall.member.mapper;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;


public class MemberConsigneeAddressMapperTest extends BaseServiceTest{
	
	//private static final Logger logger = LoggerFactory.getLogger(MemberConsigneeAddressMapperTest.class);
	
	@Resource
	private MemberConsigneeAddressMapper memberConsigneeAddressMapper;

	@Test
	public void testFindByUserId() {
		List<MemberConsigneeAddress> userAddrList = memberConsigneeAddressMapper.findByUserId("14527626891242d4d00a207c4d69bd80");
		assertNotNull("查询地址列表为空", userAddrList);
	}

}
