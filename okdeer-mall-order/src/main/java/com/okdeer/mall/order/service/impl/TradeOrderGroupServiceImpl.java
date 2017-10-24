package com.okdeer.mall.order.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuPicture;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuPictureServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.common.consts.StaticConstants;
import com.okdeer.common.utils.ImageCutUtils;
import com.okdeer.common.utils.ImageTypeContants;
import com.okdeer.common.utils.JsonDateUtil;
import com.okdeer.mall.activity.discount.dto.ActivityDiscountGroupSkuDto;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountGroup;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountGroupMapper;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.order.bo.GroupOrderRemarkConst;
import com.okdeer.mall.order.bo.TradeOrderGroupParamBo;
import com.okdeer.mall.order.dto.GroupJoinUserDto;
import com.okdeer.mall.order.dto.TradeOrderGroupDetailDto;
import com.okdeer.mall.order.dto.TradeOrderGroupDto;
import com.okdeer.mall.order.dto.TradeOrderGroupGoodsDto;
import com.okdeer.mall.order.dto.TradeOrderGroupParamDto;
import com.okdeer.mall.order.entity.TradeOrderGroup;
import com.okdeer.mall.order.entity.TradeOrderGroupRelation;
import com.okdeer.mall.order.enums.GroupOrderStatusEnum;
import com.okdeer.mall.order.mapper.TradeOrderGroupMapper;
import com.okdeer.mall.order.mapper.TradeOrderGroupRelationMapper;
import com.okdeer.mall.order.service.TradeOrderGroupService;
import com.okdeer.mall.order.timer.TradeOrderTimer;
import com.okdeer.mall.system.service.SysBuyerUserService;
import com.okdeer.mall.system.utils.ConvertUtil;
import com.okdeer.mall.util.SysConfigComponent;

@Service
public class TradeOrderGroupServiceImpl extends BaseServiceImpl implements TradeOrderGroupService {
	
	private static final Logger logger = LoggerFactory.getLogger(TradeOrderGroupServiceImpl.class);

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
	
	@Resource
	private TradeOrderTimer tradeOrderTimer;
	
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuPictureServiceApi goodsStoreSkuPictureServiceApi;
	
	/**
	 * 用户图片前缀
	 */
	@Value("${myinfoImagePrefix}")
	private String userInfoPicServerUrl;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return tradeOrderGroupMapper;
	}

	@Override
	public List<GroupJoinUserDto> findGroupJoinUserList(String groupOrderId,String screen) throws ServiceException {
		Assert.notNull(groupOrderId, "团购订单Id不能为空");
		Assert.notNull(screen, "分辨率不能为空");
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
						String.format("%s%s", sysConfigComponent.getMyinfoImagePrefix(), buyerUser.getPicUrl()), screen));
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
		ActivityDiscountGroup skuGroup = activityDiscountGroupMapper.findByActivityIdAndSkuId(activityId, storeSkuId);
		dto.setId(skuGroup.getId());
		dto.setDiscountId(activityId);
		dto.setStoreSkuId(storeSkuId);
		dto.setGroupCount(skuGroup.getGroupCount());
		dto.setGroupPrice(JsonDateUtil.priceConvertToString(skuGroup.getGroupPrice(),2, 3));
		dto.setGroupValid(skuGroup.getGroupValid());
		dto.setGroupValidUnit("天");
		TradeOrderGroupParamBo paramBo = new TradeOrderGroupParamBo();
		paramBo.setActivityId(activityId);
		paramBo.setStoreSkuId(storeSkuId);
		
		//查询总团购数
		dto.setAllGroupTotal(tradeOrderGroupMapper.countGroupNum(paramBo));
		//查询总数后再查询 开团未成团记录
		paramBo.setStatus(GroupOrderStatusEnum.UN_GROUP);
		paramBo.setExpireTime(new Date());
		PageHelper.startPage(1, 5, true);
		PageUtils<TradeOrderGroupGoodsDto> openGroup = new PageUtils<>(tradeOrderGroupMapper.findOrderGroupList(paramBo));
		//循环处理用户头像
		if(CollectionUtils.isNotEmpty(openGroup.getList())){
			openGroup.getList().forEach(e -> {
				String url = e.getUserImgUrl();
				url	= StringUtils.isNotBlank(url) ? (userInfoPicServerUrl + url + StaticConstants.PIC_SUFFIX_PARM_240) : "";
				e.setUserImgUrl(url);
			});
		}
		//未成团总数
		dto.setOpenGroupTotal(openGroup.getTotal());
		dto.setOpenGroupList(openGroup.getList());
		
        return dto;
	}
	
	/**
	 * @Description: 查询分页的为成团数据
	 * @param paramBo
	 * @param pageNumber
	 * @param pageSize
	 * @author tuzhd
	 * @date 2017年10月18日
	 */
	@Override
	public PageUtils<TradeOrderGroupGoodsDto> findOrderGroupList(TradeOrderGroupParamBo paramBo,Integer pageNumber,Integer pageSize){
		PageHelper.startPage(pageNumber, pageSize, true);
		paramBo.setExpireTime(new Date());
		PageUtils<TradeOrderGroupGoodsDto> page = new PageUtils<>(tradeOrderGroupMapper.findOrderGroupList(paramBo));
		//循环处理用户头像
		if(CollectionUtils.isNotEmpty(page.getList())){
			page.getList().forEach(e -> {
				String url = e.getUserImgUrl();
				url	= StringUtils.isNotBlank(url) ? (userInfoPicServerUrl + url + StaticConstants.PIC_SUFFIX_PARM_240) : "";
				e.setUserImgUrl(url);
			});
		}
		return page;
		
	}
	
	public PageUtils<TradeOrderGroupDto> findPage(TradeOrderGroupParamDto param, int pageNum, int pageSize)
			throws Exception {
		PageHelper.startPage(pageNum, pageSize, true);
		return new PageUtils<>(tradeOrderGroupMapper.findByParam(param));
	}

	/**
	 * @Description: 关闭活动时更新团购订单
	 * 1、根据活动id关闭所有未成团的团购订单
	 * 2、查询第一步更新受影响的团购订单
	 * 3、发送团购订单过期消息，等待过期消息消费处理
	 * @param activityId   
	 * @author maojj
	 * @throws Exception 
	 * @date 2017年10月18日
	 */
	@Override
	public void updateByColseActivity(String activityId) throws Exception {
		if(StringUtils.isEmpty(activityId)){
			return;
		}
		// 第一步：构建请求参数，采用乐观锁机制更新所有未成团的团购订单状态为：活动关闭
		TradeOrderGroupParamBo paramBo = new TradeOrderGroupParamBo();
		paramBo.setActivityId(activityId);
		paramBo.setStatus(GroupOrderStatusEnum.GROUP_CLOSE);
		paramBo.setCurrentStatus(GroupOrderStatusEnum.UN_GROUP);
		paramBo.setRemark(GroupOrderRemarkConst.GROUP_CLOSE);
		paramBo.setEndTime(new Date());
		tradeOrderGroupMapper.updateDistributed(paramBo);
		
		// 第二步：查询第一步更新受影响的记录。即查询状态为活动关闭的记录
		List<TradeOrderGroup> orderGroupList = tradeOrderGroupMapper.findGroupOrderList(paramBo);
		
		// 第三步：发送团购订单过期消息，等待过期消息消费处理
		for(TradeOrderGroup orderGroup : orderGroupList){
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_group_timeout, orderGroup.getId(), 0);
		}
	}

	@Override
	public TradeOrderGroupDetailDto findGroupJoinDetail(String groupOrderId,String screen) throws ServiceException {
		// 查询团购订单信息
		TradeOrderGroup groupOrder = tradeOrderGroupMapper.findById(groupOrderId);
		if(groupOrder == null){
			logger.error("团购订单{}不存在",groupOrderId);
			throw new ServiceException("非法的请求参数");
		}
		// 查询团购商品信息
		GoodsStoreSku storeSku = goodsStoreSkuServiceApi.selectByPrimaryKey(groupOrder.getStoreSkuId());
		// 查询团购商品主图
		GoodsStoreSkuPicture storeSkuPic = goodsStoreSkuPictureServiceApi.findMainPicByStoreSkuId(groupOrder.getStoreSkuId());
		// 查询参团用户列表
		List<GroupJoinUserDto> joinUserList = findGroupJoinUserList(groupOrderId, screen);
		// 组装返回参数
		TradeOrderGroupDetailDto groupDetailDto = BeanMapper.map(groupOrder, TradeOrderGroupDetailDto.class);
		groupDetailDto.setGroupOrderId(groupOrder.getId());
		groupDetailDto.setGroupExpireTime(groupOrder.getExpireTime().getTime() - System.currentTimeMillis());
		groupDetailDto.setAbsentNum(groupOrder.getGroupCount() - joinUserList.size());
		groupDetailDto.setUnit(storeSku.getUnit());
		groupDetailDto.setOnlinePrice(storeSku.getOnlinePrice());
		groupDetailDto.setStoreId(storeSku.getStoreId());
		groupDetailDto.setStoreSkuName(storeSku.getName());
		groupDetailDto.setUpdateTime(DateUtils.formatDate(storeSku.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
		groupDetailDto.setPicUrl(ImageCutUtils.changeType(ImageTypeContants.SPQDSPLBTP,
				String.format("%s%s", sysConfigComponent.getStoreImagePrefix(), storeSkuPic.getUrl()), screen));
		groupDetailDto.setJoinUserList(joinUserList);
		groupDetailDto.setActivityId(groupOrder.getActivityId());
		return groupDetailDto;
	}

}
