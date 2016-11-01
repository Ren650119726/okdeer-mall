package com.okdeer.mall.activity.coupons.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.okdeer.api.pay.account.dto.PayUpdateAmountDto;
import com.okdeer.api.pay.common.dto.BaseResultDto;
import com.okdeer.api.pay.service.IPayTradeServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsCategory;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRandCode;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordQueryVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordVo;
import com.okdeer.mall.activity.coupons.entity.CouponsFindVo;
import com.okdeer.mall.activity.coupons.entity.CouponsStatusCountVo;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRandCodeMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordServiceApi;
import com.okdeer.mall.order.vo.RechargeCouponVo;

import net.sf.json.JSONObject;

/**
 * @DESC: 活动代金券记录
 * @author YSCGD
 * @date 2016-04-08 19:39:19
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V4.1			2016-07-04			maojj			事务控制使用注解
 *		V1.1.0			2016-9-19		wushp				各种状态代金券数量统计
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordServiceApi")
class ActivityCouponsRecordServiceImpl implements ActivityCouponsRecordServiceApi, ActivityCouponsRecordService {

	private static final Logger log = Logger.getLogger(ActivityCouponsRecordServiceImpl.class);

	@Autowired
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;

	/**
	 * 代金券管理mapper
	 */
	@Autowired
	private ActivityCouponsMapper activityCouponsMapper;

	/**
	 * 代金券领取活动
	 */
	@Autowired
	private ActivityCollectCouponsMapper activityCollectCouponsMapper;

	/**
	 * 代金券随机码
	 */
	@Autowired
	private ActivityCouponsRandCodeMapper activityCouponsRandCodeMapper;
	
	@Reference(version = "1.0.0", check = false)
	private IPayTradeServiceApi payTradeServiceApi;

	@Override
	@Transactional(readOnly = true)
	public PageUtils<ActivityCouponsRecordVo> getAllRecords(ActivityCouponsRecordVo activityCouponsRecordVo,
			int pageNum, int pageSize) throws ServiceException {
		PageHelper.startPage(pageNum, pageSize, true);
		//begin 重构4.1 added by zhangkn
		if(activityCouponsRecordVo.getStartTime() != null) {
			String str = DateUtils.formatDate(activityCouponsRecordVo.getStartTime(), "yyyy-MM-dd") + " 00:00:00";
			try {
				activityCouponsRecordVo.setStartTime(DateUtils.parseDate(str, "yyyy-MM-dd HH:mm:ss"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		if(activityCouponsRecordVo.getEndTime() != null) {
			String str = DateUtils.formatDate(activityCouponsRecordVo.getEndTime(), "yyyy-MM-dd") + " 23:59:59";
			try {
				activityCouponsRecordVo.setEndTime(DateUtils.parseDate(str, "yyyy-MM-dd HH:mm:ss"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		//end 重构4.1 added by zhangkn
		List<ActivityCouponsRecordVo> recordInfos = activityCouponsRecordMapper
				.selectAllRecords(activityCouponsRecordVo);
		if (recordInfos == null) {
			recordInfos = new ArrayList<ActivityCouponsRecordVo>();
		} else {
			List<String> recordIds = new ArrayList<String>();
			for (ActivityCouponsRecordVo vo : recordInfos) {
				recordIds.add(vo.getId());
				Calendar cal = Calendar.getInstance();
				cal.setTime(vo.getValidTime());
				cal.add(Calendar.DATE, -1); // 减1天
				vo.setValidTime(cal.getTime());
			}
/*			if (CollectionUtils.isNotEmpty(recordIds)) {
				List<ActivityCouponsRecordVo> list = activityCouponsRecordMapper.findOrderByRecordId(recordIds);
				for (ActivityCouponsRecordVo recordVo : recordInfos) {
					for (ActivityCouponsRecordVo record : list) {
						recordVo.setOrderNo(record.getOrderNo());
					}
				}
			}*/
		}
		PageUtils<ActivityCouponsRecordVo> pageUtils = new PageUtils<ActivityCouponsRecordVo>(recordInfos);
		return pageUtils;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityCouponsRecordVo> getRecordExportData(Map<String, Object> paraMap) {
		return activityCouponsRecordMapper.selectExportRecords(paraMap);
	}

	@Override
	@Transactional(readOnly = true)
	public int selectCountByParams(ActivityCouponsRecord activityCouponsRecord) throws ServiceException {
		return activityCouponsRecordMapper.selectCountByParams(activityCouponsRecord);
	}

	// begin update by　zhulq  2016-10-17  新增方法提供给之前的版本
	@Override
	@Transactional(readOnly = true)
	public List<ActivityCouponsRecordQueryVo> findMyCouponsDetailByParams(ActivityCouponsRecordStatusEnum status,
			String currentOperateUserId,Boolean flag) throws ServiceException {
		ActivityCouponsRecord activityCouponsRecord = new ActivityCouponsRecord();
		activityCouponsRecord.setStatus(status);
		activityCouponsRecord.setCollectUserId(currentOperateUserId);
		List<ActivityCouponsRecordQueryVo> voList = new ArrayList<>();
		// true  新的版本的请求
		if (flag) {
			voList = activityCouponsRecordMapper.selectMyCouponsDetailByParams(activityCouponsRecord);
		} else {	
			voList = activityCouponsRecordMapper.selectMyCouponsDetailByParamsOld(activityCouponsRecord);
		}
		if (voList != null && voList.size() > 0) {
			for (ActivityCouponsRecordQueryVo vo : voList) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(vo.getValidTime());
				// 减1天
				cal.add(Calendar.DATE, -1); 
				vo.setValidTime(cal.getTime());
				ActivityCoupons activityCoupons = vo.getActivityCoupons();
				if (activityCoupons.getType() == 1) {
					if (activityCoupons.getIsCategory() == 1) {
						String categoryNames = "";
						//CouponsInfoQuery couponsInfo = activityCouponsMapper.findNavCategoryByCouponsId(activityCoupons.getId());
						List<ActivityCouponsCategory> cates = activityCoupons.getActivityCouponsCategory();
						if (cates != null) {
					    	for (ActivityCouponsCategory category : cates) {
					    		if (StringUtils.isNotBlank(categoryNames)) {
						    		categoryNames =  categoryNames + "、" ;	
					    		}	
					    		categoryNames = categoryNames + category.getCategoryName();
					    	}
					    }
						activityCoupons.setCategoryNames(categoryNames);
					}
				} else if (activityCoupons.getType() == 2) {
					if (activityCoupons.getIsCategory() == 1) {
						String categoryNames = "";
						//CouponsInfoQuery couponsInfo = activityCouponsMapper.findSpuCategoryByCouponsId(activityCoupons.getId());
						List<ActivityCouponsCategory> cates = activityCoupons.getActivityCouponsCategory();
						if (cates != null) {
					    	for (ActivityCouponsCategory category : cates) {
					    		if (StringUtils.isNotBlank(categoryNames)) {
						    		categoryNames =  categoryNames + "、" ;	
					    		}	
					    		categoryNames = categoryNames + category.getCategoryName();						    		
					    	}
					    }
						activityCoupons.setCategoryNames(categoryNames);
					}
					
				}
			}
		}
		return voList;

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addRecordForRecevie(String couponsId, String currentOperatUserId,
			ActivityCouponsType activityCouponsType) throws ServiceException {
		Map<String, Object> map = new HashMap<String, Object>();

		ActivityCoupons activityCoupons = activityCouponsMapper.selectByPrimaryKey(couponsId);

		// 根据数量的判断，插入代金券领取记录
		map = this.insertRecordByJudgeNum(activityCoupons, currentOperatUserId, "恭喜你，领取成功！", activityCouponsType);

		return JSONObject.fromObject(map);

	}

	/**
	 * DESC: 领取活动优惠券
	 * 
	 * @author LIU.W
	 * @param lstActivityCoupons
	 * @param activityCouponsType
	 * @param userId
	 * @throws ServiceException
	 */
	public void drawCouponsRecord(List<ActivityCoupons> lstActivityCoupons, ActivityCouponsType activityCouponsType,
			String userId) throws ServiceException {
		if (CollectionUtils.isEmpty(lstActivityCoupons)) {
			return;
		}

		List<ActivityCouponsRecord> lstCouponsRecords = new ArrayList<ActivityCouponsRecord>();
		List<String> lstActivityCouponsIds = new ArrayList<String>();
		for (ActivityCoupons activityCoupons : lstActivityCoupons) {
			int remainNum = activityCoupons.getRemainNum();// 剩余总数量
			if (remainNum <= 0) {
				continue;
			}
			// 设置代金券领取记录的代金券id、代金券领取活动id、活动类型，以便后面代码中的数量判断查询
			ActivityCouponsRecord activityCouponsRecord = new ActivityCouponsRecord();
			activityCouponsRecord.setId(UuidUtils.getUuid());
			activityCouponsRecord.setCollectType(activityCouponsType);
			activityCouponsRecord.setCouponsId(activityCoupons.getId());
			activityCouponsRecord.setCouponsCollectId(activityCoupons.getActivityId());
			activityCouponsRecord.setCollectTime(new Date());
			activityCouponsRecord.setCollectUserId(userId);
			activityCouponsRecord.setValidTime(DateUtils.addDays(new Date(), activityCoupons.getValidDay()));
			activityCouponsRecord.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);

			lstCouponsRecords.add(activityCouponsRecord);
			// 更新代金券已使用数量和剩余数量
			lstActivityCouponsIds.add(activityCoupons.getId());
		}

		addActivityCouponsRecord(lstCouponsRecords, lstActivityCouponsIds);
	}

	/**
	 * DESC: 添加代金券
	 * 
	 * @author LIU.W
	 * @throws ServiceException
	 */
	@Transactional(rollbackFor = Exception.class)
	public void addActivityCouponsRecord(List<ActivityCouponsRecord> lstCouponsRecords,
			List<String> lstActivityCouponsIds) throws ServiceException {

		try {
			// 批量插入代金券
			if (!CollectionUtils.isEmpty(lstCouponsRecords)) {
				activityCouponsRecordMapper.insertSelectiveBatch(lstCouponsRecords);
			}
			// 更新可使用的
			if (!CollectionUtils.isEmpty(lstActivityCouponsIds)) {
				for (String activityCouponId : lstActivityCouponsIds) {
					int count = activityCouponsMapper.updateRemainNum(activityCouponId);
					if (count == 0) {
						throw new Exception("添加代金卷记录失败!");
					}
				}
			}

		} catch (Exception e) {
			throw new ServiceException("", e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addRecordForExchangeCode(Map<String, Object> params, String exchangeCode,
			String currentOperatUserId, ActivityCouponsType activityCouponsType) throws ServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ActivityCollectCouponsVo> result = activityCollectCouponsMapper.selectByStoreAndLimitType(params);
		// 判断输入的优惠码是否正确
		if (result != null && result.size() > 0) {
			ActivityCoupons activityCoupons = result.get(0).getActivityCoupons().get(0);
			// 根据数量的判断，插入代金券领取记录
			map = this.insertRecordByJudgeNum(activityCoupons, currentOperatUserId, "恭喜你，优惠券兑换成功！",
					activityCouponsType);
		} else {
			map.put("code", 103);
			map.put("msg", "您输入的抵扣券优惠码错误！");
		}
		return JSONObject.fromObject(map);
	}
	
	/**
	 * 根据随机码进行领取代金劵
	 * @param activityCoupons 活动信息
	 * @param userId 用户id
	 * @param successMsg 成功提示语
	 * @param activityCouponsType  活动类型
	 * @author zhulq
	 * @date 2016年10月26日
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> insertRecordByRandCode(ActivityCoupons activityCoupons, String userId,
			String successMsg, ActivityCouponsType activityCouponsType) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 设置代金券领取记录的代金券id、代金券领取活动id、活动类型，以便后面代码中的数量判断查询
		ActivityCouponsRecord record = new ActivityCouponsRecord();
		//进行公共代金劵领取校验
		if (!checkRecordPubilc(map, activityCoupons, userId,activityCouponsType,record)) {
			return map;
		}
		//检验随机码是否可以领取
		if (!checkRandCode(map, userId, record, activityCoupons)) {
			return map;
		}
		//record.setCollectUserId(userId);
		//更新保存领取代金劵记录
		updateCouponsRecode(record, activityCoupons);
		//更新随机码表记录
		updateRandCode(activityCoupons.getRandCode(), userId);
		
		map.put("code", 100);
		map.put("msg", successMsg);
		return map;
	}
	
	/**
	 * 判断当前登陆用户领取的指定代金券数量是否已经超过限领数量，所有用户领取的指定代金券总数量是否已经超过代金券的总发行数量，否则，插入代金券领取记录
	 * @param activityCoupons 活动信息
	 * @param userId 用户id
	 * @param successMsg 成功提示语
	 * @param activityCouponsType  活动类型
	 * @author zhulq
	 * @date 2016年10月26日
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> insertRecordByJudgeNum(ActivityCoupons activityCoupons, String userId,
			String successMsg, ActivityCouponsType activityCouponsType) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 设置代金券领取记录的代金券id、代金券领取活动id、活动类型，以便后面代码中的数量判断查询
		ActivityCouponsRecord record = new ActivityCouponsRecord();
		//进行公共代金劵领取校验
		if (!checkRecordPubilc(map, activityCoupons, userId,activityCouponsType,record)) {
			return map;
		}
		//检验优惠码的领取
		if (!checkExchangeCode(map, userId, record, activityCoupons)) {
			return map;
		}
		record.setCollectUserId(userId);
		//更新保存领取代金劵记录
		updateCouponsRecode(record, activityCoupons);
		map.put("code", 100);
		map.put("msg", successMsg);
		return map;
	}
	
	/**
	 * 进行公共代金劵领取校验
	 * @Description: TODO
	 * @param map 返回结果
	 * @param activityCoupons 活动信息
	 * @param userId 用户id
	 * @param activityCouponsType 活动类型
	 * @param record 代金劵记录对象
	 * @author zhulq
	 * @date 2016年10月26日
	 */
	private boolean checkRecordPubilc(Map<String, Object> map,ActivityCoupons activityCoupons,String userId,
			 ActivityCouponsType activityCouponsType,ActivityCouponsRecord record) {
		if (activityCoupons.getRemainNum() <= 0) {
			// 剩余数量小于0 显示已领完
			map.put("code", 101);
			map.put("msg", "该代金券已经领完了！");
			return false;
		}
		Date collectTime = DateUtils.getDateStart(new Date());
		record.setCouponsId(activityCoupons.getId());
		record.setCouponsCollectId(activityCoupons.getActivityId());
		record.setCollectType(activityCouponsType);
		record.setCollectUserId(null);
		record.setCollectTime(collectTime);	
		//校验代金卷的日领取量
		if (!checkDaliyRecord(map, record, activityCoupons)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 更新随机码表记录
	 * @Description: TODO
	 * @param radeCode 随机码 
	 * @param userId 用户id
	 * @author zhulq
	 * @date 2016年10月26日
	 */
	private void updateRandCode(String radeCode,String userId) {
		//如果领取成功更新随机码表
		if (StringUtils.isNotBlank(radeCode)) {
			ActivityCouponsRandCode couponsRandCode	= activityCouponsRandCodeMapper.selectByRandCode(radeCode);
			if (couponsRandCode != null) {
				couponsRandCode.setIsExchange(1);
				couponsRandCode.setUpdateTime(new Date());
				couponsRandCode.setUpdateUserId(userId);
				activityCouponsRandCodeMapper.updateByPrimaryKey(couponsRandCode);
			}
		}
	}
	
	/**
	 * 更新保存领取代金劵记录
	 * @Description: TODO
	 * @param record 代金劵信息
	 * @param coupons 活动信息
	 * @author zhulq
	 * @date 2016年10月26日
	 */
	private void updateCouponsRecode(ActivityCouponsRecord record,ActivityCoupons coupons) {
		// 立即领取
		record.setId(UuidUtils.getUuid());
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DATE), 0,	0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		record.setCollectTime(calendar.getTime());
		record.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);
		calendar.add(Calendar.DAY_OF_YEAR, coupons.getValidDay());
		record.setValidTime(calendar.getTime());

		activityCouponsRecordMapper.insertSelective(record);
		activityCouponsMapper.updateRemainNum(coupons.getId());
	}
	
	/**
	 * 校验代金卷的日领取量
	 * @Description: TODO
	 * @param map 返回结果
	 * @param record 代金卷记录对象
	 * @param collect 活动信息
	 * @author zhulq
	 * @date 2016年10月26日
	 */
	private boolean checkDaliyRecord(Map<String, Object> map,
								ActivityCouponsRecord record,ActivityCoupons coupons) {
		// 判断活动是否已结束
		ActivityCollectCoupons collect = activityCollectCouponsMapper
				.get(coupons.getActivityId());
		if (collect == null || collect.getStatus().intValue() != 1) {
			map.put("code", 105);
			map.put("msg", "活动已结束！");
			return false;
		}
		// 当前日期已经领取的数量
		int dailyCirculation = activityCouponsRecordMapper.selectCountByParams(record);
		if(collect.getDailyCirculation() != null){
			// 获取当前登陆用户已领取的指定代金券数量
			if (dailyCirculation >= Integer.parseInt(collect.getDailyCirculation())) {
				map.put("code", 104);
				map.put("msg", "来迟啦！券已抢完，明天早点哦");
				return false;
			}
			
		}
		return true;
	}
	
	/**
	 * 检验优惠码的领取
	 * @Description: TODO
	 * @param map 返回结果
	 * @param userId 用户id
	 * @param record 代金卷记录对象
	 * @param coupons 代金卷信息
	 * @author zhulq
	 * @date 2016年10月26日
	 */
	private boolean checkExchangeCode(Map<String, Object> map,String userId,
			ActivityCouponsRecord record,ActivityCoupons coupons) {
		if (!StringUtils.isEmpty(userId)) {
			record.setCollectUserId(userId);
			record.setCollectTime(null);
			int currentRecordCount = activityCouponsRecordMapper.selectCountByParams(record);
			if (currentRecordCount >= coupons.getEveryLimit().intValue()) {
				// 已领取
				map.put("code", 102);
				map.put("msg", "每人限领" + coupons.getEveryLimit() + "张，不要贪心哦！");
				return false;
			}
		} else {
			map.put("code", 104);
			map.put("msg", "请登录后再领取");
			return false;
		}
		return true;
	}
	
	/**
	 * 检验优惠码的领取
	 * @Description: TODO
	 * @param map 返回结果
	 * @param userId 用户id
	 * @param record 代金卷记录对象
	 * @param coupons 代金卷信息
	 * @author zhulq
	 * @date 2016年10月26日
	 */
	private boolean checkRandCode(Map<String, Object> map,String userId,
			ActivityCouponsRecord record,ActivityCoupons coupons) {
		if (!StringUtils.isEmpty(userId)) {
			//判断该随机码的代金卷是否已经被领取了
			int count = activityCouponsRecordMapper.selectCountByRandCode(coupons.getRandCode());
			if (count >= 1) {
				// 已领取
				map.put("code", 106);
				map.put("msg", "相同随机码的代金券已经被领取了!");
				return false;
			}
			//判断 该用户是否还能领卷该类代金卷
			record.setCollectUserId(userId);
			record.setCollectTime(null);
			int currentRecordCount = activityCouponsRecordMapper.selectCountByParams(record);
			if (currentRecordCount >= coupons.getEveryLimit().intValue()) {
				// 已领取
				map.put("code", 102);
				map.put("msg", "每人限领" + coupons.getEveryLimit() + "张，不要贪心哦！!");
				return false;
			}
		} else {
			map.put("code", 104);
			map.put("msg", "请登录后再领取");
			return false;
		}
		return true;
	}

	@Override
	public List<ActivityCouponsRecordQueryVo> selectCouponsDetailByStoreId(ActivityCouponsRecord activityCouponsRecord)
			throws ServiceException {

		List<ActivityCouponsRecordQueryVo> couponsList = new ArrayList<ActivityCouponsRecordQueryVo>();
		List<ActivityCouponsRecordQueryVo> couponsAllList = new ArrayList<ActivityCouponsRecordQueryVo>();
		couponsAllList = activityCouponsRecordMapper.selectCouponsAllId(activityCouponsRecord);
		couponsList = activityCouponsRecordMapper.selectCouponsDetailByStoreId(activityCouponsRecord);
		List<ActivityCouponsRecordQueryVo> couponsAllLists = new ArrayList<ActivityCouponsRecordQueryVo>();

		couponsAllLists.addAll(couponsAllList);
		couponsAllLists.addAll(couponsList);

		return couponsAllLists;
	}

	@Override
	public ActivityCoupons selectCouponsItem(CouponsFindVo couponsFindVo) throws Exception {
		ActivityCoupons coupons = activityCouponsRecordMapper.selectCouponsItem(couponsFindVo);
		return coupons;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStatusByJob() throws Exception {
		List<ActivityCouponsRecord> list = activityCouponsRecordMapper.selectAllForJob();
		List<String> ids = new ArrayList<>();
		Date date = new Date(); /* date.compareTo(anotherDate) */
		if (list != null && list.size() > 0) {
			for (ActivityCouponsRecord record : list) {
				Date validTime = record.getValidTime();
				int res = date.compareTo(validTime);
				if ((res == 0) || (res == 1)) {
					ids.add(record.getId());
				}
			}
		}

		if (ids != null && ids.size() > 0) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ids", ids);
			params.put("status", ActivityCouponsRecordStatusEnum.EXPIRES);
			activityCouponsRecordMapper.updateAllByBatch(params);
		}
	}

	// 活动已经关闭 、 已经结束（未被使用的）
	/*
	 * @Override public void setRefundStatus() throws Exception{ Date date = new
	 * Date(); Map<String,Object> params = new HashMap<>(); // 将活动的退款状态改为
	 * 未领取的已经退款 params.put("closed",
	 * ActivityCollectCouponsStatus.closed.ordinal()); params.put("end",
	 * ActivityCollectCouponsStatus.end.ordinal()); params.put("refundType",
	 * RefundType.UNREFUND); //所以关闭 结束 领取了的代金卷 没有退款的
	 * List<ActivityCollectCouponsRecordVo> activityCollectCouponsList =
	 * activityCollectCouponsMapper.selectByUnusedOrExpires(params); if
	 * (activityCollectCouponsList != null && activityCollectCouponsList.size()
	 * > 0) { for(ActivityCollectCouponsRecordVo activityCollectCouponsRecordVo
	 * : activityCollectCouponsList){ List<ActivityCouponsRecordVo>
	 * activityCouponsRecordVoList = null; activityCouponsRecordVoList =
	 * activityCollectCouponsRecordVo.getActivityCouponsRecordVo(); String id =
	 * activityCollectCouponsRecordVo.getId(); if (activityCouponsRecordVoList
	 * != null && activityCouponsRecordVoList.size() > 0) { Date validTime =
	 * activityCouponsRecordVoList.get(0).getValidTime(); //String id =
	 * activityCouponsRecordVoList.get(0).getCouponsCollectId(); int res =
	 * validTime.compareTo(date); if ((res == 0) || (res == -1)) { try{
	 * updateRefundStatus(activityCouponsRecordVoList,id); }catch(Exception e){
	 * log.error("更改代金卷状态"+id,e); } } } } } }
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateRefundStatus(List<ActivityCouponsRecordVo> couponsRecordVoList, String id) throws Exception {
		BigDecimal faceValueTotal = new BigDecimal("0");
		PayUpdateAmountDto freeDto = new PayUpdateAmountDto();
		for (ActivityCouponsRecordVo couponsRecordVo : couponsRecordVoList) {
			Integer value = couponsRecordVo.getFaceValue();
			BigDecimal faceValue = new BigDecimal(value);
			faceValueTotal = faceValueTotal.add(faceValue);
			freeDto.setUserId(couponsRecordVo.getCreateUserId());
			// 算出该代金卷 所有的金额
		}
		freeDto.setAmount(faceValueTotal);
		// 将活动改为 领取未使用已经退款 状态为2
		activityCollectCouponsMapper.updateRefundTypeByVo(id);
		BaseResultDto result = payTradeServiceApi.unfreezeAmount(freeDto);
		if (result != null && !"0".equals(result.getCode())) {
			throw new Exception(result.getMsg());
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateUseStatus(String orderId) {

		Map<String, Object> params = Maps.newHashMap();
		params.put("orderId", orderId);
		List<ActivityCouponsRecord> records = activityCouponsRecordMapper.selectByParams(params);
		if (records != null && records.size() == 1) {
			if (records.get(0).getValidTime().compareTo(DateUtils.getSysDate()) > 0) {
				activityCouponsRecordMapper.updateUseStatus(orderId);
				activityCouponsMapper.updateReduceUseNum(records.get(0).getCouponsId());
			} else {
				activityCouponsRecordMapper.updateUseStatusAndExpire(orderId);
			}
		}

	}

	@Override
	public ActivityCouponsRecord selectByPrimaryKey(String id) {
		return activityCouponsMapper.selectByPrimaryKey(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateActivityCouponsStatus(Map<String, Object> params) {
		activityCouponsRecordMapper.updateActivityCouponsStatus(params);

	}
	
	//begin add by wushp 20160919 V1.1.0
	@Transactional(readOnly = true)
	@Override
	public List<CouponsStatusCountVo> findStatusCountByUserId(String userId) {
		return activityCouponsRecordMapper.findStatusCountByUserId(userId);
	}
	//end add by wushp 20160919 V1.1.0

    @Override
    public List<RechargeCouponVo> findValidRechargeCoupons(Map<String, Object> params) {
        return activityCouponsRecordMapper.findValidRechargeCoupons(params);
    }

    @Transactional(rollbackFor = Exception.class)
	@Override
	public void insertSelective(ActivityCouponsRecord couponsRecord) throws Exception {
		activityCouponsRecordMapper.insertSelective(couponsRecord);
	}

	@Override
	public List<ActivityCouponsRecord> selectActivityCouponsRecord(ActivityCouponsRecord couponsRecord) throws Exception {
		List<ActivityCouponsRecord> record = activityCouponsRecordMapper.selectAllRecordsByUserId(couponsRecord);
		return record;
	}

	@Override
	public JSONObject addRecordForRandCode(Map<String, Object> params, String randCode, String currentOperatUserId,
			ActivityCouponsType activityCouponsType) throws ServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		//如果随机码为空即不进行检验
		String radeCode = (String)params.get("randCode");
		if (StringUtils.isNotBlank(radeCode)) {
			List<ActivityCollectCouponsVo> result = activityCollectCouponsMapper.selectRandCodeVoucher(params);
			// 判断输入的随机码是否正确
			if (result != null && result.size() > 0) {
				ActivityCoupons activityCoupons = result.get(0).getActivityCoupons().get(0);
				activityCoupons.setRandCode(radeCode);
				// 根据数量的判断，插入代金券领取记录
				map = this.insertRecordByRandCode(activityCoupons, currentOperatUserId, "恭喜你，优惠券兑换成功！",
						activityCouponsType);
			} else {
				map.put("code", 103);
				map.put("msg", "您输入的抵扣券随机码无效！");
			}
		} else {
			map.put("code", 103);
			map.put("msg", "您输入的抵扣券随机码无效！");
		}
		
		return JSONObject.fromObject(map);
	}

	
}