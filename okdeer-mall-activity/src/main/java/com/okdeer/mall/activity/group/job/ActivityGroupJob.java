 package com.okdeer.mall.activity.group.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.enums.BSSC;
import com.okdeer.archive.goods.store.enums.IsActivity;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.mall.activity.group.entity.ActivityGroup;
import com.okdeer.mall.activity.group.entity.ActivityGroupGoods;
import com.okdeer.mall.activity.group.enums.ActivityGroupAuditStatus;
import com.okdeer.mall.activity.group.enums.ActivityGroupStatus;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.group.service.ActivityGroupGoodsService;
import com.okdeer.mall.activity.group.service.ActivityGroupService;
import com.okdeer.mcm.entity.SmsVO;
import com.okdeer.mcm.service.ISmsService;

/**
 * 
 * 
 * @pr mall
 * @desc 修改活动状态job
 * @author chenwj
 * @date 2016年1月28日 下午1:59:59
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
@Service
public class ActivityGroupJob extends AbstractSimpleElasticJob {

	private static final Logger logger = LoggerFactory.getLogger(ActivityGroupJob.class);

	@Autowired
	private ActivityGroupService activityGroupService;
	
	@Autowired
	private ActivityGroupGoodsService activityGroupGoodsService;
	
	@Reference(version = "1.0.0",check=false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	
	/**
	 * 短信  service
	 */
	@Reference
	private ISmsService smsService;
	
	public void checkActivityGroupStatus(){
		
		logger.info("团购JOB开始执行----"  + new Date());
		ActivityGroup activityGroup = new ActivityGroup();
		List<ActivityGroup> activityGroups = null;
		Date nowTime = new Date();
		//****************检查未审核状态是否已超时 start*******************
		activityGroup.setApprovalStatus(ActivityGroupAuditStatus.unAudit.getKey());
		activityGroup.setStatus(ActivityGroupStatus.unStarted.getKey());
		activityGroups = activityGroupService.findActivityGroupList(activityGroup);
		if(activityGroups != null && activityGroups.size() > 0){
			for (ActivityGroup entity : activityGroups) {
				Date startTime = entity.getStartTime();
				//当前时间已超过活动开始时间，修改活动状态已失效
				if(nowTime.getTime() >= startTime.getTime()){
					ActivityGroup group = new ActivityGroup();
					group.setId(entity.getId());
					group.setStatus(ActivityGroupStatus.disabled.getKey());
					try {
						activityGroupService.update(group);
						releaseStock(group.getId(),entity.getStoreId());
						this.sendMsg(entity);
						logger.info("JOB--检查未审核状态是否已超时,修改状态已超时成功");
					} catch (ServiceException e) {
						logger.info("JOB--检查未审核状态时，修改团购活动状态异常",e);
					}
				}
			}
		}
		//****************检查未审核状态是否已超时 end*******************
		
		
		//****************检查审核已通过状态是否已结束 start*******************
		activityGroup.setApprovalStatus(ActivityGroupAuditStatus.auditPass.getKey());
		activityGroup.setStatus(ActivityGroupStatus.starting.getKey());
		activityGroups = activityGroupService.findActivityGroupList(activityGroup);
		if(activityGroups != null && activityGroups.size() > 0){
			for (ActivityGroup entity : activityGroups) {
				Date endTime = entity.getEndTime();
				//活动结束时间已超过当前时间，修改活动状态已结束
				if(nowTime.getTime() >= endTime.getTime() ){
					ActivityGroup group = new ActivityGroup();
					group.setId(entity.getId());
					group.setStatus(ActivityGroupStatus.over.getKey());
					try {
						activityGroupService.update(group);
						releaseStock(group.getId(),entity.getStoreId());
						logger.info("JOB--检查审核已通过状态是否已结束,修改状态已结束成功");
					} catch (ServiceException e) {
						logger.info("JOB-->检查审核已通过状态时，修改团购活动状态异常",e);
					}
				}
			}
		}
		//****************检查审核已通过状态是否已结束 end*******************
		
		
		//****************检查审核已通过状态是否已开始 start*******************
				activityGroup.setApprovalStatus(ActivityGroupAuditStatus.auditPass.getKey());
				activityGroup.setStatus(ActivityGroupStatus.unStarted.getKey());
				activityGroups = activityGroupService.findActivityGroupList(activityGroup);
				if(activityGroups != null && activityGroups.size() > 0){
					for (ActivityGroup entity : activityGroups) {
						Date startTime = entity.getStartTime();
						//审核已通过 未开始的活动是否到时间开始活动
						if(nowTime.getTime() >= startTime.getTime() ){
							ActivityGroup group = new ActivityGroup();
							group.setId(entity.getId());
							group.setStatus(ActivityGroupStatus.starting.getKey());
							try {
								activityGroupService.update(group);
								
								List<ActivityGroupGoods> activityGroupGoods = activityGroupGoodsService.getActivityGroupGoods(group.getId());
								List<String> storeSkuIds = new ArrayList<String>();
								for (int i = 0;i < activityGroupGoods.size();i++) {
									storeSkuIds.add(activityGroupGoods.get(i).getStoreSkuId());
								}
								goodsStoreSkuServiceApi.updateStoreSkuBsscStatus(storeSkuIds, BSSC.PUTAWAY);															
								logger.info("JOB--检查审核已通过状态是否已开始,修改状态已开始成功");
								
							} catch (Exception e) {
								logger.info("JOB-->检查审核已通过未开始的活动，修改团购活动状态异常",e);
							}
						}
					}
				}
				//****************检查审核已通过状态是否已开始 end*******************
				
				
				
				logger.info("团购JOB结束执行----" + new Date());
	}
	
	
	private void releaseStock(String id,String storeId){
		try {
			//释放  同步库存
			List<ActivityGroupGoods> activityGroupGoods = activityGroupGoodsService.getActivityGroupGoods(id);
			if (activityGroupGoods != null && activityGroupGoods.size() > 0) {
				List<String> storeSkuIds = new ArrayList<String>();
				for (int i = 0;i < activityGroupGoods.size();i++) {
					ActivityGroupGoods activityGroupGoodsVO = new ActivityGroupGoods();
					activityGroupGoodsVO.setStoreId(storeId);
					activityGroupGoodsVO.setStoreSkuId(activityGroupGoods.get(i).getStoreSkuId());
					activityGroupGoodsVO.setGroupInventory(0);
					activityGroupGoodsService.syncGoodsStock(activityGroupGoodsVO, "0",StockOperateEnum.ACTIVITY_END);
					
					storeSkuIds.add(activityGroupGoods.get(i).getStoreSkuId());
					
					//去掉活动关联
					GoodsStoreSku goodsStoreSku = new GoodsStoreSku();
					goodsStoreSku.setId(activityGroupGoods.get(i).getStoreSkuId());
					goodsStoreSku.setActivityId("0");
					goodsStoreSku.setActivityName("");
					goodsStoreSku.setIsActivity(IsActivity.ABSTENTION);
					goodsStoreSkuServiceApi.updateByPrimaryKey(goodsStoreSku);
					
					//活动商品下架
					ActivityGroupGoods goods  = new ActivityGroupGoods();
					goods.setId(activityGroupGoods.get(i).getId());
					goods.setStatus("1");
					activityGroupGoodsService.updateActivityGroupGoods(goods);
				}
				//删除商品后  下架
				goodsStoreSkuServiceApi.updateStoreSkuBsscStatus(storeSkuIds, BSSC.UNSHELVE); 
			}
		} catch (Exception e) {
			logger.info("JOB-->团购活动下架商品时发生异常",e);
		}
	}
	
	
	
	/**
	 * 发送短信
	 * @param activityGroup ActivityGroup
	 */
	private void sendMsg(ActivityGroup activityGroup) {
		SmsVO sms = new SmsVO();
		sms.setContent("您的团购活动 ("+activityGroup.getName()+") 超时没有通过审核，活动已失效，请重新提交");
		sms.setSysCode("mall");
		sms.setId(UuidUtils.getUuid());
		sms.setMobile(activityGroup.getTel());
		sms.setToken("654987123465851");
		sms.setIsTiming(0);
		sms.setSmsChannelType(3);
		sms.setSendTime(DateUtils.formatDateTime(new Date()));
		smsService.sendSms(sms);
		logger.info("团购JOB执行----发送短信："+sms.toString());
	}

	@Override
	public void process(JobExecutionMultipleShardingContext arg0) {
		this.checkActivityGroupStatus();
	}
}
