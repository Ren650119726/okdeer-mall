
package com.okdeer.mall.activity.share.api;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.activity.share.dto.ActivityShareRecordDto;
import com.okdeer.mall.activity.share.dto.ActivityShareRecordParamDto;
import com.okdeer.mall.activity.share.entity.ActivityShareRecord;
import com.okdeer.mall.activity.share.service.ActivityShareRecordApi;
import com.okdeer.mall.activity.share.service.ActivityShareRecordService;
import com.okdeer.mall.system.service.SysBuyerUserService;

@Service(version = "1.0.0")
public class ActivityShareRecordApiImpl implements ActivityShareRecordApi {

	private static Logger logger = LoggerFactory.getLogger(ActivityShareRecordApiImpl.class);

	@Autowired
	private ActivityShareRecordService activityShareRecordService;

	@Autowired
	private ActivityDiscountService activityDiscountService;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	@Autowired
	private SysBuyerUserService sysBuyerUserService;

	@Override
	public void addGroupActivityShareRecord(String sysUserId, String storeSkuId, String activityId)
			throws MallApiException {
		try {
			ActivityDiscount activityDiscount = activityDiscountService.findById(activityId);
			if (activityDiscount == null) {
				throw new MallApiException("活动不存在");
			}
			GoodsStoreSku goodsStoreSku = goodsStoreSkuServiceApi.getById(storeSkuId);
			if (goodsStoreSku == null) {
				throw new MallApiException("店铺商品不存在");
			}
			SysBuyerUser sysBuyerUser = sysBuyerUserService.findByPrimaryKey(sysUserId);
			if (sysBuyerUser == null) {
				throw new MallApiException("用户不存在");
			}
			ActivityShareRecord activityShareRecord = new ActivityShareRecord();
			activityShareRecord.setActivityId(activityId);
			activityShareRecord.setId(UuidUtils.getUuid());
			activityShareRecord.setSysUserId(sysUserId);
			activityShareRecord.setSysUserPhone(sysBuyerUser.getLoginName());
			activityShareRecord.setStoreSkuId(storeSkuId);
			activityShareRecord.setStoreId(goodsStoreSku.getStoreId());
			activityShareRecord.setActivityId(activityId);
			activityShareRecord.setActivityType(ActivityTypeEnum.GROUP_ACTIVITY.ordinal());
			activityShareRecord.setDeliveryNum(0);
			activityShareRecord.setCompleteNum(0);
			activityShareRecord.setRefundNum(0);
			activityShareRecord.setCreateTime(new Date());
			activityShareRecord.setDisabled(Disabled.valid);
			activityShareRecordService.add(activityShareRecord);
		} catch (Exception e) {
			logger.error("添加团购记录出错", e);
			throw new MallApiException(e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public PageUtils<ActivityShareRecordDto> findPageList(ActivityShareRecordParamDto activityShareRecordParamDto,
			int pageNum, int pageSize) {
		return activityShareRecordService.findList(activityShareRecordParamDto, pageNum,
				pageSize).toBean(ActivityShareRecordDto.class);
	}

}
