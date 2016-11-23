package com.okdeer.mall.activity.coupons.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.activity.coupons.service.ActivityDrawPrizeService;
import com.okdeer.mall.activity.coupons.service.ActivityDrawPrizeServiceApi;
import com.okdeer.mall.member.member.entity.SysBuyerExt;
import com.okdeer.mall.member.member.service.SysBuyerExtServiceApi;
import com.okdeer.mall.member.service.SysBuyerExtService;

import net.sf.json.JSONObject;

/**
 * 
 * ClassName: ActivityDrawPrizeService 
 * @Description: 抽奖服务实现类
 * @author tuzhd
 * @date 2016年11月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.1.7			2016-11-23			tuzhd			抽奖服务类
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivityDrawPrizeServiceApi")
public class ActivityDrawPrizeServiceImpl implements ActivityDrawPrizeService,ActivityDrawPrizeServiceApi{
	
	/**
	 * 用户信息扩展表用于操作用户的抽奖次数
	 */
	@Autowired
	private SysBuyerExtService sysBuyerExtService;
	
	@Autowired
	private ActivityCouponsRecordService activityCouponsRecordService;
	
	/**
	 * @Description: 根据用户id进行抽奖
	 * @param userId 用户id
	 * @param iArr 中奖概率
	 * @param ids  对应的奖品代金劵id
	 * @return JSONObject  
	 * @author tuzhd
	 * @throws ServiceException 
	 * @date 2016年11月23日
	 */
	@Transactional(rollbackFor = Exception.class)
	public JSONObject processPrize(String userId,double[] iArr,String[] ids) throws ServiceException{
		SysBuyerExt user = sysBuyerExtService.findByUserId(userId);
		Map<String,Object> map =  new HashMap<String,Object>();
		//用户抽奖次数存在让其抽奖否则
		if(user != null && user.getPrizeCount() == 0){
			// 剩余数量小于0 显示已领完
			map.put("code", 108);
			map.put("msg", "每天只有1次抽奖机会哦，明天再来吧！");
			return JSONObject.fromObject(map);
		}
		
		//根据中奖概率执行中奖 
		Integer  prizeNo = isHadPrize(iArr);
		//根据用户id 抽奖之后将其抽奖机会-1,根据产品要求即使代金劵另外也扣抽奖机会
		sysBuyerExtService.updateCutPrizeCount(userId);
		
		//抽中的概率序号如果为null及未抽中
		if(prizeNo == null){
			// 剩余数量小于0 显示已领完
			map.put("code", 109);
			map.put("msg", "很遗憾,未抽中！");
			return JSONObject.fromObject(map);
			
		}
		//根据序号获取代金劵id 执行送奖
		JSONObject json = activityCouponsRecordService.addRecordsByCollectId(ids[prizeNo.intValue()], userId, ActivityCouponsType.advert_coupons);
		json.put("prizeNo", prizeNo);
		return json;
		
	}
	
	/**
 	 * 根据中奖概率执行中奖 
 	 * @param iArr
 	 * @return
 	 */
 	private Integer isHadPrize(double[] iArr){
 		double randonNo =  (Math.random() * 10000)/100;
 		double count = 0;
		//循环增加各个奖品的概率，判断是否中奖
		for (int  i = 0 ; i < iArr.length ; i++) {
			double step = count + iArr[i];  
			//如果概率为空，跳过该奖项
			if (iArr[i] != 0) {
				if (randonNo >= count && randonNo < step) {
					System.out.println("中奖概率为"+i);
					
					return i;
				}
				count += iArr[i];
			}
		}
		return null;
 	}
}
