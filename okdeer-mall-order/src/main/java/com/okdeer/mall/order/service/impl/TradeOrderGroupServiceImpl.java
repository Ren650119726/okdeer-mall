package com.okdeer.mall.order.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.common.utils.ImageCutUtils;
import com.okdeer.common.utils.ImageTypeContants;
import com.okdeer.mall.order.dto.GroupJoinUserDto;
import com.okdeer.mall.order.entity.TradeOrderGroupRelation;
import com.okdeer.mall.order.mapper.TradeOrderGroupMapper;
import com.okdeer.mall.order.mapper.TradeOrderGroupRelationMapper;
import com.okdeer.mall.order.service.TradeOrderGroupService;
import com.okdeer.mall.system.service.SysBuyerUserService;
import com.okdeer.mall.system.utils.ConvertUtil;
import com.okdeer.mall.util.SysConfigComponent;

@Service
public class TradeOrderGroupServiceImpl extends BaseServiceImpl implements TradeOrderGroupService {

	@Resource
	private TradeOrderGroupMapper tradeOrderGroupMapper;
	
	@Resource
	private TradeOrderGroupRelationMapper tradeOrderGroupRelationMapper;
	
	@Resource
	private SysBuyerUserService sysBuyerUserService;
	
	@Resource
	private SysConfigComponent sysConfigComponent;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return tradeOrderGroupMapper;
	}

	@Override
	public List<GroupJoinUserDto> findGroupJoinUserList(String groupOrderId,String screen) throws ServiceException {
		// 根据团购订单Id查询已入团的用户关系列表
		List<TradeOrderGroupRelation> groupRelList = tradeOrderGroupRelationMapper.findByGroupOrderId(groupOrderId);
		List<GroupJoinUserDto> joinUserList = Lists.newArrayList();
		SysBuyerUser buyerUser = null;
		GroupJoinUserDto joinUser = null;
		for(TradeOrderGroupRelation groupRel : groupRelList){
			// 查询买家用户信息
			buyerUser = sysBuyerUserService.findByPrimaryKey(groupRel.getUserId());
			joinUser = new GroupJoinUserDto();
			// 昵称
			joinUser.setNickName(ConvertUtil.format(buyerUser.getNickName()));
			if(StringUtils.isEmpty(buyerUser.getPicUrl())){
				joinUser.setPicUrl("");
			}else{
				joinUser.setPicUrl(ImageCutUtils.changeType(ImageTypeContants.WDTX,
						String.format("%s%s", sysConfigComponent.getMyinfoImagePrefix(), buyerUser.getPicUrl()), ""));
			}
			joinUser.setJoinType(String.valueOf(groupRel.getType().getCode()));
			joinUserList.add(joinUser);
		}
		return joinUserList;
	}

}
