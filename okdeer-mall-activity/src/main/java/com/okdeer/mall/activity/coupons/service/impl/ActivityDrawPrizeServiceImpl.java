package com.okdeer.mall.activity.coupons.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.activity.coupons.service.ActivityDrawPrizeService;
import com.okdeer.mall.activity.coupons.service.ActivityDrawPrizeServiceApi;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDraw;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeWeight;
import com.okdeer.mall.activity.prize.service.ActivityDrawRecordService;
import com.okdeer.mall.activity.prize.service.ActivityLuckDrawService;
import com.okdeer.mall.activity.prize.service.ActivityPrizeRecordService;
import com.okdeer.mall.activity.prize.service.ActivityPrizeWeightService;
import com.okdeer.mall.member.member.entity.SysBuyerExt;
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
	@Autowired
	private ActivityPrizeWeightService activityPrizeWeightService;
	@Autowired
	ActivityDrawRecordService  activityDrawRecordService;
	@Autowired
	private ActivityLuckDrawService activityLuckDrawService;
	@Autowired
	ActivityPrizeRecordService activityPrizeRecordService;
	
	@Autowired
    private RedisLockRegistry redisLockRegistry;
	
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
	public JSONObject processPrize(String userId,double[] iArr,String[] ids) throws Exception{
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
		Integer  prizeNo = isHadPrize(iArr,100);
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
		JSONObject json = activityCouponsRecordService.addRecordsByCollectId(ids[prizeNo.intValue()], userId);
		json.put("prizeNo", prizeNo); 
		return json;
		
	}
	
	/**
 	 * 根据中奖概率执行中奖 
 	 * @param iArr
 	 * @return
 	 */
 	private Integer isHadPrize(double[] iArr,int weightDeno){
 		double randonNo =  Math.random() * weightDeno;
 		double count = 0;
		//循环增加各个奖品的概率，判断是否中奖
		for (int  i = 0 ; i < iArr.length ; i++) {
			double step = count + iArr[i];  
			//如果概率为空，跳过该奖项
			if (iArr[i] != 0) {
				if (randonNo >= count && randonNo < step) {
					System.out.println(i+"中奖概率为"+iArr[i]);
					
					return i;
				}
				count += iArr[i];
			}
		}
		return null;
 	}
 	
 	/**
 	 * @Description: 根据活动id查询所有奖品的比重信息 按顺序查询 顺序与奖品对应 
 	 * @param luckDrawId 抽奖活动id
 	 * @param userId 用户id
 	 * @return JSONObject 抽奖获取奖品
 	 * @author tuzhd
 	 * @date 2016年12月14日
 	 */
 	@Transactional(rollbackFor = Exception.class)
 	public JSONObject processPrizeByUser(String userId,String luckDrawId)throws Exception{
 		Map<String,Object> map =  new HashMap<String,Object>();
		//校验成功标识 //如果不存在缓存数据进行加入到缓存中 start 涂志定
        String key = "draw_user" + userId;
        Lock lock = redisLockRegistry.obtain(key);
        if (lock.tryLock(10, TimeUnit.SECONDS)) {
        	SysBuyerExt user = sysBuyerExtService.findByUserId(userId);
    		//用户抽奖次数存在让其抽奖否则
    		if(user != null && user.getPrizeCount() == 0){
    			// 剩余数量小于0 显示已领完
    			map.put("code", 108);
    			map.put("msg", "您已经没有抽奖机会哦，可以邀请好友获得抽奖机吧！");
    			return JSONObject.fromObject(map);
    		}
        	try{
        		//执行抽奖
        		return addProcessPrize(userId, luckDrawId);
	        } catch (Exception e) {
	            throw e;
	        } finally {
	            lock.unlock();
	        }
        }else{
        	map.put("code", 102);
			map.put("msg", "您抽奖速度太快，稍后再试");
			return JSONObject.fromObject(map);
        }
		
 	}
 	/**
 	 * @Description: 根据活动id查询所有奖品的比重信息 按顺序查询 顺序与奖品对应 
 	 * @param luckDrawId 抽奖活动id
 	 * @param userId 用户id
 	 * @return JSONObject 抽奖获取奖品
 	 * @author tuzhd
 	 * @throws Exception 
 	 * @date 2016年12月14日
 	 */
 	@Transactional(rollbackFor = Exception.class)
 	private JSONObject addProcessPrize(String userId,String luckDrawId)throws Exception{
 		Map<String,Object> map =  new HashMap<String,Object>();
 		ActivityLuckDraw  activityLuckDraw = activityLuckDrawService.findById(luckDrawId);
 		//根据活动id查询所有奖品的比重信息 按顺序查询 顺序与奖品对应 
 		List<ActivityPrizeWeight> list = activityPrizeWeightService.findPrizesByLuckDrawId(luckDrawId);
 		if(CollectionUtils.isNotEmpty(list) && activityLuckDraw != null){
 			//权限比重集合
 			double[] weight = new double[list.size()];
 			//概率分母
 			int weightDeno = activityLuckDraw.getWeightDeno();
 			//默认概率序号
 			int defaultNo = 0;
 			//已经无奖品数量的奖项概率和 
 			double sumWeight = 0;
 			for(int i = 0;i < list.size();i++){
 				ActivityPrizeWeight prizeWeight = list.get(i);
 				weight[i] = (double)prizeWeight.getWeight();
 				if(prizeWeight.getPrizeNumber() <= 0 && prizeWeight.getIsDefaultWeight() != WhetherEnum.whether){
 					sumWeight = sumWeight + weight[i];
 					weight[i] = 0;
 				}
 				if(prizeWeight.getIsDefaultWeight() == WhetherEnum.whether){
 					defaultNo = i;
 				}
 			}
 			//将 无奖品数量的奖项概率和 加到默认奖项 概率中
 			weight[defaultNo] = weight[defaultNo] + sumWeight;
 			//根据中奖概率执行中奖 
 			Integer  prizeNo = isHadPrize(weight,weightDeno);
 			//根据用户id 抽奖之后将其抽奖机会-1,根据产品要求即使代金劵另外也扣抽奖机会
 			if(sysBuyerExtService.updateCutPrizeCount(userId) == 0 ){
 				// 剩余数量小于0 显示已领完
    			map.put("code", 108);
    			map.put("msg", "您已经没有抽奖机会哦，可以邀请好友获得抽奖机吧！");
    			return JSONObject.fromObject(map);
 			}
 			//写入抽奖记录
 			activityDrawRecordService.addDrawRecord(userId, luckDrawId);
 			
 			//抽中的概率序号如果为null及未抽中
 			if(prizeNo == null){
 				// 剩余数量小于0 显示已领完
 				map.put("code", 109);
 				map.put("msg", "很遗憾,未抽中！");
 				return JSONObject.fromObject(map);
 			}
 			//获得中奖的实体
 			ActivityPrizeWeight prizeW = list.get(prizeNo.intValue()); 
 			JSONObject json = null;
 			//根据活动奖品扣减数量级返回 记录结果
			json = activityPrizeWeightService.updatePrizesNumber(prizeW.getId());
 			//如果奖品扣减成功 -- 写入中奖记录抽奖记录
 			Object code = json.get("code");
 			if(code != null && (int)code == 100){
 				//根据序号获取代金劵id 执行送奖 //奖品库存扣减成功后去领取代金券
 				String collectId = prizeW.getActivityCollectId();
 	 			if(StringUtils.isNotBlank(collectId) ){
 	 				activityPrizeRecordService.addPrizeRecord(collectId, userId, luckDrawId,prizeW.getId(),WhetherEnum.whether.ordinal());
 	 				json = activityCouponsRecordService.addRecordsByCollectId(collectId, userId);
 	 			}else{
 	 				activityPrizeRecordService.addPrizeRecord(collectId, userId, luckDrawId,prizeW.getId(),WhetherEnum.not.ordinal());
 	 			}
 			}
 			json.put("prizeNo", prizeW.getOrderNo()); 
 			json.put("prizeName", prizeW.getPrizeName()); 
 			return json;
 		}
 		return null;
 	}
 	
 	/**
 	 * 
 	 * @Description: 查询用户的已抽及剩余抽奖次数
 	 * @param userId 用户id
 	 * @param luckDrawId 抽奖活动id
 	 * @return JSONObject  
 	 * @author tuzhd
 	 * @throws ServiceException 
 	 * @date 2017年1月11日
 	 */
	public JSONObject findPrizeCount(String userId,String luckDrawId) throws ServiceException{
		JSONObject json  = new JSONObject();
		
		int prizeCount = 0;
		int hadCount = 0;
		if(StringUtils.isNotBlank(userId)){
			SysBuyerExt user = sysBuyerExtService.findByUserId(userId);
			hadCount = user.getPrizeCount();
		}
		if(StringUtils.isNotBlank(luckDrawId)){
			//根据用户id及活动id查询抽奖次数
			prizeCount = activityDrawRecordService.findCountByUserIdAndActivityId(userId, luckDrawId);
		}
		//已抽数量
		json.put("prizeCount", prizeCount);
		//剩余抽奖次数
		json.put("hadCount", hadCount);
		return json;
	}
}
