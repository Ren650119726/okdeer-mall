package com.okdeer.mall.system.mapper;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.mall.Application;
import com.okdeer.mall.system.entity.SysUserInvitationRecord;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SysUserInvitationTest {
	
	/**
	 * 用户邀请记录Mapper
	 */
	@Resource
	private SysUserInvitationRecordMapper sysUserInvitationRecordMapper;
	
	/**
	 * 用户邀请码Mapper
	 */
	@Resource
	private SysUserInvitationCodeMapper sysUserInvitationCodeMapper;

	@Test
	public void testUpdateInvitationRecord() {
		String buyerUserId = "05f84fa2bda643e2be0057c3a165d6e2";
		// 根据用户Id查询邀请记录
		SysUserInvitationRecord invitationRecord = sysUserInvitationRecordMapper
				.findInvitationRecordByUserId(buyerUserId);
		if (invitationRecord == null) {
			return;
		}
		// 如果邀请记录中已经是首单，则不做任何处理。
		if (invitationRecord.getIsFirstOrder() == WhetherEnum.whether) {
			return;
		}
		// 如果已存在的邀请记录中不是首单，则确认收货时，将状态更改为首单，并修改用户邀请码的下单人数。
		Date updateTime = new Date();
		invitationRecord.setFirstOrderTime(updateTime);
		invitationRecord.setUpdateTime(updateTime);
		// 更新邀请记录
		int updateResult = sysUserInvitationRecordMapper.updateCodeRecord(invitationRecord);
		// 如果更新成功，则修改邀请码下单人数
		if (updateResult == 1) {
			// 跟新邀请码下单人数
			sysUserInvitationCodeMapper.updateFirstOrderNum(invitationRecord.getInvitationCodeId(), updateTime);
		}
	}

}
