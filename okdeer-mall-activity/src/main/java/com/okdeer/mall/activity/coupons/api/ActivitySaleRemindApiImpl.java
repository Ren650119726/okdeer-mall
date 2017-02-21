package com.okdeer.mall.activity.coupons.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuStockServiceApi;
import com.okdeer.archive.store.service.ISysUserAndExtServiceApi;
import com.okdeer.base.common.constant.LoggerConstants;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.coupons.bo.ActivitySaleRemindBo;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.service.ActivitySaleGoodsServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRemindApi;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRemindService;
import com.okdeer.mcm.entity.SmsVO;
import com.okdeer.mcm.service.ISmsService;

/**
 * 
 * ClassName: ActivitySaleRemindApiImpl 
 * @Description: 活动安全库存预警提醒
 * @author tangy
 * @date 2017年2月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.0.0          2017年2月21日                               tangy             新增
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivitySaleRemindApi")
public class ActivitySaleRemindApiImpl implements ActivitySaleRemindApi {

	/**
	 * logger
	 */
	private static final Logger log = LoggerFactory.getLogger(ActivitySaleRemindApiImpl.class);

    /**
     * 充值成功短信
     */
    @Value("${sms.sale.security.stock}")
    private String saleSecurityStock;
    
    @Value("${mcm.sys.code}")
    private String mcmSysCode;

    @Value("${mcm.sys.token}")
    private String mcmSysToken;
    
	/**
	 * 安全库存联系关联人
	 */
	@Autowired
	private ActivitySaleRemindService activitySaleRemindService;
	
	/**
	 * 特惠活动商品
	 */
	@Reference(version = "1.0.0", check = false)
	private ActivitySaleGoodsServiceApi activitySaleGoodsServiceApi;
	
	/**
	 * SysUserAndExtService注入
	 */
	@Reference(version="1.0.0", check = false)
	private ISysUserAndExtServiceApi sysUserAndExtService;
	
	/**
	 * GoodsStoreSkuStockServiceApi注入
	 */
	@Reference(version="1.0.0", check = false)
	private GoodsStoreSkuStockServiceApi goodsStoreSkuStockServiceApi;
	
    /**
     * 短信接口
     */
    @Reference(version = "1.0.0", check = false)
    ISmsService smsService;
     	
	@Override
	public void sendSafetyWarning(String storeSkuId) {
		log.info(LoggerConstants.LOGGER_DEBUG_INCOMING_METHOD, storeSkuId);
		if (StringUtils.isBlank(storeSkuId)) {
			return;
		}	
		ActivitySaleGoods activitySaleGoods = activitySaleGoodsServiceApi.selectBySkuId(storeSkuId);
		//判断是否有设置安全库存
		if (activitySaleGoods != null && activitySaleGoods.getSecurityStock() != null 
				&& activitySaleGoods.getSecurityStock().intValue() > 0) {
			//是否已提醒
			if (activitySaleGoods.getIsRemind() == null || activitySaleGoods.getIsRemind().intValue() == 0) {
				GoodsStoreSkuStock stock = goodsStoreSkuStockServiceApi.getBySkuId(storeSkuId);
				//是否达到提醒条件，安全库存大于活动剩余库存
				if (stock != null && stock.getLocked() != null 
						&& activitySaleGoods.getSecurityStock().intValue() > stock.getLocked().intValue()) {
					//活动安全库存联系人
					List<ActivitySaleRemindBo> saleRemind = activitySaleRemindService.findActivitySaleRemindBySaleId(activitySaleGoods.getSaleId());
					if (CollectionUtils.isNotEmpty(saleRemind)) {
						//短信提醒联系人
						List<String> phoneList = new ArrayList<String>();
						for (ActivitySaleRemindBo activitySaleRemindBo : saleRemind) {
							if (activitySaleRemindBo.getPhone() != null) {
								phoneList.add(activitySaleRemindBo.getPhone());
							}
						}
						//是否有需要发送提醒短信的联系人
						if (CollectionUtils.isNotEmpty(phoneList)) {
							activitySaleGoods.setIsRemind(1);
							try {
								activitySaleGoodsServiceApi.updateActivitySaleGoods(activitySaleGoods);
							} catch (Exception e) {
								log.error(LoggerConstants.LOGGER_ERROR_EXCEPTION, e);
								return;
							}
							sendMessg(phoneList);
						}
					}
				}
			}
		}
		log.info(LoggerConstants.LOGGER_DEBUG_QUIT_METHOD);
	}

	/**
	 * 
	 * @Description: 发送提醒短信
	 * @param phonenos   需要发送的手机号码
	 * @return void  
	 * @author tangy
	 * @date 2017年2月21日
	 */
	private void sendMessg(List<String> phonenos) {
		//发送提醒短信
		String content = saleSecurityStock;
		List<SmsVO> list = new ArrayList<SmsVO>();
		for (String phoneno : phonenos) {
			SmsVO smsVo = createSmsVo(phoneno, content);
			list.add(smsVo);
		}
		this.smsService.sendSms(list);
	}

	/**
	 * 
	 * @Description:  创建短信发送对象
	 * @param mobile  手机号码
	 * @param content 短信内容
	 * @return SmsVO  
	 * @author tangy
	 * @date 2017年2月21日
	 */
	private SmsVO createSmsVo(String mobile, String content) {
		SmsVO smsVo = new SmsVO();
		smsVo.setId(UuidUtils.getUuid());
		smsVo.setUserId(mobile);
		smsVo.setIsTiming(0);
		smsVo.setToken(mcmSysToken);
		smsVo.setSysCode(mcmSysCode);
		smsVo.setMobile(mobile);
		smsVo.setContent(content);
		smsVo.setSmsChannelType(3);
		smsVo.setSendTime(DateUtils.formatDateTime(new Date()));
		return smsVo;
	}
}
