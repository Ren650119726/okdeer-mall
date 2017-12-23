package com.okdeer.mall.order.handler.impl;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.activity.bo.ActivityJoinRecParamBo;
import com.okdeer.mall.activity.coupons.enums.ActivityDiscountItemRelType;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.discount.dto.ActivityCloudItemReultDto;
import com.okdeer.mall.activity.discount.dto.ActivityCloudStoreParamDto;
import com.okdeer.mall.activity.discount.dto.ActivityCloudStoreResultDto;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountItemRel;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountItemRelMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMultiItemMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityJoinRecordMapper;
import com.okdeer.mall.activity.discount.service.ActivityCloudStoreService;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.common.enums.UseUserType;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.system.service.SysBuyerFirstOrderRecordService;
import com.okdeer.mall.system.utils.ConvertUtil;

/**
 * ClassName: CheckStoreActivityServiceImpl 
 * @Description: 检查满增/加价购 N件X元商品
 * @author tuzhd
 * @date 2017年12月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		2.7				2017-12-12			tuzhd			检查满增/加价购 N件X元商品
 */
@Service("checkStoreActivityService")
public class CheckStoreActivityServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto> {
	
	@Resource
	private ActivityJoinRecordMapper activityJoinRecordMapper;
	
	@Resource
	private ActivityDiscountMapper activityDiscountMapper;
	
	@Resource
	private SysBuyerFirstOrderRecordService sysBuyerFirstOrderRecordService;
	
	@Resource
	private ActivityCloudStoreService activityCloudStoreService;
	
	/**
	 * 梯度 业务数据关联表
	 */
	@Resource
	private ActivityDiscountItemRelMapper activityDiscountItemRelMapper;
	
	/**
	 * N件X元三级关联表
	 */
	@Resource
	private ActivityDiscountMultiItemMapper activityDiscountMultiItemMapper;
	
	/**
	 * 活动商品用户日限购
	 */
	private static final int LIMIT_SKU_USER_DAY = 0;

	/**
	 * 活动商品用户总限购
	 */
	private static final int LIMIT_SKU_USER_TOTAL = 1;

	/**
	 * 活动用户总限购
	 */
	private static final int LIMIT_ACTIVITY_USER_TOTAL = 2;
	
	/**
	 * 梯度信息key
	 */
	private static final String ACTIVITY_ITEM_INFO= "activityItemInfo";

	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		//活动id 活动项ID
		String storeActivityId = paramDto.getStoreActivityId();
		String activityItemId = paramDto.getStoreActivityItemId();
		//不存在活动id或梯度说明不存在活动 
		if(StringUtils.isBlank(activityItemId) || StringUtils.isBlank(storeActivityId) 
				|| CollectionUtils.isEmpty(paramDto.getSkuList())){
			return;
		}
		
		// 查询活动是否信息
		ActivityDiscount activity = activityDiscountMapper.findById(storeActivityId);
		if(activity == null || activity.getStatus() != ActivityDiscountStatus.ing){
			resp.setResult(ResultCodeEnum.ACTIVITY_IS_END);
			return;
		}
		ActivityCloudStoreParamDto param= new ActivityCloudStoreParamDto();
		param.setActivityId(storeActivityId);
		param.setStoreId(paramDto.getStoreId());
		List<String> skuIds =  Lists.newArrayList();
		paramDto.getSkuList().forEach(e ->{
			if(!skuIds.contains(e.getStoreSkuId())){
				skuIds.add(e.getStoreSkuId());
			}
		});
		param.setStoreSkuIdList(skuIds);
		param.setStoreInfo((StoreInfo)paramDto.get("storeInfo"));
		//查询验证活动及店铺是否匹配
		ActivityCloudStoreResultDto result = activityCloudStoreService.getCloudStoreActivity(param);
		//如果查询到了梯度信息，说明活动与店铺匹配
		switch (activity.getType()){
			case MMS:
				 if(!checkItemInfo(result.getGiveItemList(), paramDto,resp, 
						 ActivityDiscountItemRelType.MMS_GOODS,ActivityTypeEnum.MMS)){
					 return;
				 }
				break;
			case JJG:
				if(!checkItemInfo(result.getPriceItemList(), paramDto,resp, 
						ActivityDiscountItemRelType.JJG_GOODS,ActivityTypeEnum.JJG)){
					return;
				}
				break;
			case NJXY:
				//组装N件x元 
				if(!checkItemInfo(result.getMultiItemList(), paramDto,resp, null,ActivityTypeEnum.NJXY)){
					return;
				}
				break;
			default:
				break;
		}
		// 如果活动类型加入用户限制
		if(isOutOfLimitUserType(paramDto,activity)){
			resp.setResult(ResultCodeEnum.GROUP_OPEN_NOT_SUPPORT);
			return;
		}
		// 检查用户是否超出日限购
		if(isOutOfLimit(paramDto,activity,LIMIT_SKU_USER_DAY)){
			resp.setResult(ResultCodeEnum.GROUP_SKU_USER_DAY_LIMIT_OUT);
			return;
		}
		// 检查是否超出用户商品总限购
		if(isOutOfLimit(paramDto,activity,LIMIT_SKU_USER_TOTAL)){
			resp.setResult(ResultCodeEnum.GROUP_TOTAL_USER_LIMIT_OUT);
			return;
		}
		
		// 检查是否超出活动用户总限购
		if(isOutOfLimit(paramDto,activity,LIMIT_ACTIVITY_USER_TOTAL)){
			resp.setResult(ResultCodeEnum.GROUP_TOTAL_USER_LIMIT_OUT);
			return;
		}
		
		paramDto.setSkuType(String.valueOf(OrderTypeEnum.PHYSICAL_ORDER.ordinal()));
		// 缓存活动信息
		paramDto.put("storeActivity", activity);
	}
	
	/**
	 * @Description: 获得对于梯度信息
	 * @param paramDto 请求参数
	 * @param resp 返回参数
	 * @param relType 加价购商品或满赠商品类型
	 * @param type 活动类型
	 * @author tuzhd
	 * @date 2017年12月12日
	 */
	private boolean checkItemInfo(List<ActivityCloudItemReultDto> list,PlaceOrderParamDto paramDto,
							Response<PlaceOrderDto> resp,ActivityDiscountItemRelType relType,ActivityTypeEnum type){
		if(CollectionUtils.isEmpty(list)){
			resp.setResult(ResultCodeEnum.ACTIVITY_NOT_EXISTS);
			return false;
		}
		String multiItemId = paramDto.getStoreActivityMultiItemId();
		//如果为N件x元
		if(type == ActivityTypeEnum.NJXY &&  StringUtils.isBlank(multiItemId)){
			resp.setResult(ResultCodeEnum.ILLEGAL_PARAM);
			return false;
		}
		//1、查询梯度是否存在 // 缓存活动梯度信息
		list.forEach(e -> {
			//如果梯度id相同 ，满赠/加价购就是该梯度，是N件X元还需三级梯度id相同。
			if(paramDto.getStoreActivityItemId().equals(e.getId()) &&
					(type != ActivityTypeEnum.NJXY || multiItemId.equals(e.getActivityMultiItemId()))){
				paramDto.put(ACTIVITY_ITEM_INFO, e);
			}
		});
		//传入参数为空或梯度匹配为空
		if(paramDto.get(ACTIVITY_ITEM_INFO) == null || CollectionUtils.isEmpty(paramDto.getSkuList())){
			resp.setResult(ResultCodeEnum.ILLEGAL_PARAM);
			return false;
		}
		//校验活动商品的合法性
		if(!checkActiviItemRel(paramDto,relType,type)){
			resp.setResult(ResultCodeEnum.ACTIVITY_HAS_CHANGE);
			return false;
		}
		return true;
	}
	
	/**
	 * @Description: 校验活动商品的合法性
	 * @param paramDto 请求参数
	 * @param resp 返回参数
	 * @param relType 加价购商品或满赠商品类型
	 * @param type 活动类型
	 * @author tuzhd
	 * @date 2017年12月12日
	 */
	private boolean checkActiviItemRel(PlaceOrderParamDto paramDto,ActivityDiscountItemRelType relType,ActivityTypeEnum type){
		
		ActivityCloudItemReultDto item = (ActivityCloudItemReultDto) paramDto.get(ACTIVITY_ITEM_INFO);
		//订单满足多少金额
		BigDecimal toatalPrice = BigDecimal.ZERO;
		//攻击多少件 //记录换购或加价购数量
		int total = 0;
		int otherToltal = 0;
		List<ActivityDiscountItemRel> reList = Lists.newArrayList();
		//查询梯度下加价购或满赠商品
		if(type != ActivityTypeEnum.NJXY){
			reList = activityDiscountItemRelMapper.findNotNormalById(item.getActivityId(), item.getId(),paramDto.getStoreId());
		}
		
		boolean isNormal = true;
		//缓存需要剔除的赠品或加价购商品
		List<PlaceOrderItemDto> itemChangeDto = Lists.newArrayList();
		//N件X元提交的商品
		List<PlaceOrderItemDto> multiItemList = Lists.newArrayList();
		//2、校验梯度的商品是否存在
		for(PlaceOrderItemDto itemDto :paramDto.getSkuList()){
			//如果此活动是店铺商品，检查该正常商品是否存在该梯度中
			if(itemDto.getSkuActType() != type.ordinal()){
				continue;
			}
			//如果为0说是参加了活动的正常商品
			if(itemDto.getActivityPriceType() == ActivityDiscountItemRelType.NORMAL_GOODS.ordinal()){
				//如果不限制及验证是否是该店铺商品
				if(item.getLimitSku() == 0 ){
					toatalPrice = toatalPrice.add(itemDto.getSkuPrice().multiply(new BigDecimal(itemDto.getQuantity())));
					total = total + itemDto.getQuantity();
				//梯度信息不存在或者不包含，活动信息发生变化
				}else if(CollectionUtils.isNotEmpty(item.getSkuIdList()) && item.getSkuIdList().contains(itemDto.getStoreSkuId())){
					//用于记算优惠
					if(type == ActivityTypeEnum.NJXY ){
						multiItemList.add(itemDto);
					}
					toatalPrice = toatalPrice.add(itemDto.getSkuPrice().multiply(new BigDecimal(itemDto.getQuantity())));
					total = total + itemDto.getQuantity();
				} 
			
			//如果为1说是参加了活动的满赠商品  如果为2说是参加了活动的加价购商品
			}else if(relType != null && itemDto.getActivityPriceType() == relType.ordinal()){
				otherToltal++;
				//说明这个商品失效
				if(!checkItemRelGoods(itemDto,reList, relType)){
					itemChangeDto.add(itemDto);
					isNormal = false;
				}
			}
		}
		//如果标识不成功 说明有需要清除的赠品或加价购商品
		if(!isNormal){
			paramDto.put("itemRelChangeGoods", itemChangeDto);
			return false;
		}
		//校验价格或件数 与优惠价格相符
		if(type == ActivityTypeEnum.NJXY ){
			//根据价格比较反序
			Comparator<PlaceOrderItemDto> comparator = (item1,item2) -> item1.getSkuPrice().compareTo(item2.getSkuPrice());
			multiItemList.sort(comparator.reversed());
			//循环获得添加要计算的价格,并返回总优惠金额
			BigDecimal multiPrefere = countPrefereMulti(multiItemList, item.getPiece(), new BigDecimal(item.getPrice()));
			paramDto.put("multiPreferePrice", ConvertUtil.format(multiPrefere));
			return total >= item.getPiece() && StringUtils.isNotBlank(item.getPrice())
					&& toatalPrice.compareTo(new BigDecimal(item.getPrice())) >= 0;
		}
		//否则为满送或加价购 价格是否超过限制要求，且订单最多换购及赠送
		return StringUtils.isNotBlank(item.getLimitOrderAmount()) 
				&& toatalPrice.compareTo(new BigDecimal(item.getLimitOrderAmount())) >= 0 
				&& otherToltal <= item.getOrderMaxCount();
	}
	
	/**
	 * @Description: 循环获得添加要计算的价格
	 * @param multiItemList N件X元集合
	 * @param piece N件
	 * @param price X元
	 * @author tuzhd
	 * @date 2017年12月20日
	 */
	private BigDecimal countPrefereMulti(List<PlaceOrderItemDto> multiItemList,int piece,BigDecimal price){
		//循环获得添加要计算的价格
		BigDecimal totalPrice = BigDecimal.ZERO;
		int goodsCount = piece;
		for(PlaceOrderItemDto temp : multiItemList){
			for(int i=0 ;i < temp.getQuantity(); i++){
				if(goodsCount == 0){
					break;
				}
				//添加要计算的价格
				totalPrice = totalPrice.add(temp.getSkuPrice());
				goodsCount--;
			}
		}
		
		//已分配的优惠金额
		BigDecimal hadPrice =BigDecimal.ZERO;
		//在循环计算分摊优惠
		for(PlaceOrderItemDto temp : multiItemList){
			BigDecimal preferentialPrice = BigDecimal.ZERO;
			for(int i=0 ;i < temp.getQuantity(); i++){
				if(piece == 0){
					break;
				}
				BigDecimal actPrice = temp.getSkuPrice().divide(totalPrice,2).multiply(price);
				//计算商品的优惠价格 //最后优惠 等于总金额 - X元 - 已优惠金额
				BigDecimal prefer = piece > 1 ? temp.getSkuPrice().subtract(actPrice) : totalPrice.subtract(price).subtract(hadPrice);
				preferentialPrice = preferentialPrice.add(prefer);
				hadPrice = hadPrice.add(prefer);
				piece--;
				
			}
			//设置这个商品的优惠价格 multiItemList中元素就是paramDto.getSkuList()的对象，所以等于直接缓存
			temp.setPreferentialPrice(preferentialPrice);
		}
		return hadPrice;
	}
	
	/**
	 * @Description: 单独进行检查满赠
	 * @param reList 梯度下的商品
	 * @param itemDto 提交的商品
	 * @param relType 满赠或加价购
	 * @author tuzhd
	 * @date 2017年12月12日
	 */
	private boolean checkItemRelGoods(PlaceOrderItemDto itemDto,List<ActivityDiscountItemRel> reList,
										ActivityDiscountItemRelType relType){
		boolean isNormal = false;
		//说明这个商品失效
		if(CollectionUtils.isEmpty(reList)){
			return isNormal;
		}
		for(ActivityDiscountItemRel rel : reList){
			//关联商品为赠品 与 提交赠品对于且价格相同 即为合法；满增/加价购数量只能为1
			if(rel.getType() == relType.ordinal() &&
					rel.getBusinessId().equals(itemDto.getStoreSkuId()) &&
					rel.getPrice().compareTo(itemDto.getSkuActPrice())==0 &&
					itemDto.getQuantity() == 1){
				isNormal = true;
			}
		}
		return isNormal;
	}
	
	/**
	 * @Description: 是否超出活动用户限制类型
	 * @param paramDto
	 * @param activity
	 * @author tuzhd
	 * @date 2017年12月12日
	 */
	private boolean isOutOfLimitUserType(PlaceOrderParamDto paramDto,ActivityDiscount activity){
		if(activity.getLimitUser() == UseUserType.ALLOW_All){
			// 不限制
			return false;
		}
		// 限制新用户，判断当前用户是否为新用户.如果存在首单记录，则超出限制，否则不超出
		return sysBuyerFirstOrderRecordService.isExistsOrderRecord(paramDto.getUserId());
	}
	
	

	/**
	 * @Description: 检查活动及梯度限制
	 * @param paramDto
	 * @param activity
	 * @param limitType
	 * @return boolean  
	 * @author tuzhd
	 * @date 2017年12月13日
	 */
	private boolean isOutOfLimit(PlaceOrderParamDto paramDto,ActivityDiscount activity, int limitType) {
		// 查询用户当日参与活动商品总数量
		ActivityJoinRecParamBo paramBo = null;
		ActivityCloudItemReultDto item = (ActivityCloudItemReultDto) paramDto.get(ACTIVITY_ITEM_INFO);
		int limitNum = 0;
		switch (limitType) {
			case 0:
				if (item.getUserDayCountLimit() == Constant.ZERO) {
					// 限购数为0，标识不限购
					return false;
				}
				limitNum = item.getUserDayCountLimit();
				// 活动商品用户日限购
				paramBo = new ActivityJoinRecParamBo(paramDto.getUserId(),activity.getId(), item.getId(),new Date());
				break;
			case 1:
				if (item.getUserCountLimit() == Constant.ZERO) {
					// 限购数为0，标识不限购
					return false;
				}
				limitNum = item.getUserCountLimit();
				// 活动商品用户总限购
				paramBo = new ActivityJoinRecParamBo(paramDto.getUserId(),activity.getId(), item.getId());
				break;
			case 2:
				if (activity.getAccountDayLimit() == Constant.ZERO) {
					// 限购数为0，标识不限购
					return false;
				}
				limitNum = activity.getAccountDayLimit();
				// 活动用户每日次数
				paramBo = new ActivityJoinRecParamBo(paramDto.getUserId(),activity.getId(),null, new Date());
				break;
			default:
				break;
		}
		int totalJoinNum = activityJoinRecordMapper.countActivityJoinNum(paramBo);
		// 用户当日参与活动总数量+当前购买就是1次
		return totalJoinNum + 1 > limitNum;
	}
	
}