package com.okdeer.mall.order.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.common.utils.ImageCutUtils;
import com.okdeer.common.utils.ImageTypeContants;
import com.okdeer.mall.activity.discount.dto.ActivityDiscountGroupSkuDto;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountGroup;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountGroupMapper;
import com.okdeer.mall.order.bo.TradeOrderGroupParamBo;
import com.okdeer.mall.order.dto.GroupJoinUserDto;
import com.okdeer.mall.order.dto.TradeOrderGroupGoodsDto;
import com.okdeer.mall.order.dto.TradeOrderGroupDto;
import com.okdeer.mall.order.dto.TradeOrderGroupParamDto;
import com.okdeer.mall.order.entity.TradeOrderGroupRelation;
import com.okdeer.mall.order.enums.GroupOrderStatusEnum;
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
	
	@Resource
	private ActivityDiscountGroupMapper activityDiscountGroupMapper;
	
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
	
	
	/**
	 * @Description: 查询团购商品信息
	 * @param activityId 用户id
	 * @param storeSkuId 商品id
	 * @return ActivityDiscountGroupDto  
	 * @author tuzhd
	 * @date 2017年10月16日
	 */
	public ActivityDiscountGroupSkuDto findGoodsGroupList(String activityId,String storeSkuId) {
		ActivityDiscountGroupSkuDto dto = new ActivityDiscountGroupSkuDto();
		ActivityDiscountGroup skuGroup = activityDiscountGroupMapper.
				findByActivityIdAndSkuId(activityId,storeSkuId);
		dto.setId(skuGroup.getId());
		dto.setDiscountId(activityId);
		dto.setStoreSkuId(storeSkuId);
		dto.setGroupCount(skuGroup.getGroupCount());
		dto.setGroupPrice(skuGroup.getGroupPrice());
		
		TradeOrderGroupParamBo paramBo = new TradeOrderGroupParamBo();
		paramBo.setActivityId(activityId);
		paramBo.setStoreSkuId(storeSkuId);
		
		//查询总团购数
		dto.setAllGroupTotal(tradeOrderGroupMapper.countGroupNum(paramBo));
		//查询总数后再查询 开团未成团记录
		paramBo.setStatus(GroupOrderStatusEnum.UN_GROUP);
		List<TradeOrderGroupGoodsDto> openGroupList = tradeOrderGroupMapper.findOrderGroupList(paramBo);
		//未成团总数
		dto.setOpenGroupTotal(openGroupList.size());
		dto.setOpenGroupList(openGroupList);
		
        return dto;
	}
	
	@Override
	public PageUtils<TradeOrderGroupDto> findPage(TradeOrderGroupParamDto param, int pageNum, int pageSize)
			throws Exception {
		PageHelper.startPage(pageNum, pageSize, true);
		return new PageUtils<TradeOrderGroupDto>(tradeOrderGroupMapper.findByParam(param));
	}

}
