package com.okdeer.mall.activity.coupons.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.system.entity.PsmsAgent;
import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.archive.system.service.IPsmsAgentServiceApi;
import com.okdeer.archive.system.service.ISysUserServiceApi;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectArea;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCommunity;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsRecordVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityCollectCouponsApprovalStatus;
import com.okdeer.mall.activity.coupons.enums.ActivityCollectCouponsStatus;
import com.okdeer.mall.activity.coupons.enums.ActivityCollectCouponsType;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsServiceApi;
import com.okdeer.mall.common.enums.AreaType;
import com.yschome.api.pay.account.dto.PayUpdateAmountDto;
import com.yschome.api.pay.service.IPayAccountServiceApi;
import com.yschome.api.pay.service.IPayTradeServiceApi;
import com.yschome.base.common.exception.ServiceException;
import com.yschome.base.common.utils.DateUtils;
import com.yschome.base.common.utils.PageUtils;
import com.yschome.base.common.utils.UuidUtils;
import com.yschome.common.BaseResultDto;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectAreaMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectCommunityMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsService;
import com.okdeer.mall.system.mapper.SysUserMapper;
import com.yschome.mcm.entity.SmsVO;
import com.yschome.mcm.service.ISmsService;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsServiceApi")
public class ActivityCollectCouponsServiceImpl
		implements ActivityCollectCouponsService, ActivityCollectCouponsServiceApi {

	private static final Logger log = Logger.getLogger(ActivityCollectCouponsServiceImpl.class);

	@Autowired
	private ActivityCollectCouponsMapper activityCollectCouponsMapper;

	@Autowired
	private ActivityCollectAreaMapper activityCollectAreaMapper;

	@Autowired
	private ActivityCollectCommunityMapper activityCollectCommunityMapper;

	@Autowired
	private ActivityCouponsMapper activityCouponsMapper;

	@Value("${mcm.sys.code}")
	private String msgSysCode;

	@Value("${mcm.sys.token}")
	private String msgToken;

	@Autowired
	private SysUserMapper sysUserMapper;

	@Reference(version = "1.0.0")
	private IPayTradeServiceApi payTradeServiceApi;

	/**
	 * 短信接口
	 */
	@Reference(version = "1.0.0", check = false)
	ISmsService smsService;

	/**
	 * 代金券领取记录mapper
	 */
	@Autowired
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;

	@Reference(version = "1.0.0", check = false)
	private IPayAccountServiceApi payAccountServiceApi;
	
	/**
	 * userservice
	 */
	@Reference(version = "1.0.0",check=false)
	ISysUserServiceApi iSysUserServiceApi;
	
	/**
	 * userservice
	 */
	@Reference(version = "1.0.0",check=false)
	IPsmsAgentServiceApi iPsmsAgentServiceApi;

	@Transactional(rollbackFor = Exception.class)
	public void save(ActivityCollectCoupons activityCollectCoupons) {
		activityCollectCouponsMapper.save(activityCollectCoupons);
	}

	@Transactional(rollbackFor = Exception.class)
	public void save(ActivityCollectCoupons activityCollectCoupons, List<String> couponsIds, String areaIds)
			throws Exception {
		// 先保存活动主对象
		activityCollectCouponsMapper.save(activityCollectCoupons);

		// 保存关联的代金券,把activityId改为当前对象的id
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("couponsIds", couponsIds);
		map.put("activityId", activityCollectCoupons.getId());
		activityCouponsMapper.updateBatchActivityId(map);

		// 代金卷范围类型：0全国，1区域，2小区 , 3店铺
		// 如果是区域
		if (activityCollectCoupons.getAreaType().intValue() == AreaType.area.ordinal()) {
			// 批量添加新记录
			String[] array = areaIds.split(",");

			List<ActivityCollectArea> areaList = new ArrayList<ActivityCollectArea>();
			for (String str : array) {
				ActivityCollectArea a = new ActivityCollectArea();
				a.setId(UuidUtils.getUuid());
				a.setCollectCouponsId(activityCollectCoupons.getId());
				a.setType(Integer.parseInt(str.split("-")[1]));
				a.setAreaId(str.split("-")[0]);
				areaList.add(a);
			}
			activityCollectAreaMapper.saveBatch(areaList);
		}

		// 如果是小区
		if (activityCollectCoupons.getAreaType() == AreaType.community.ordinal()) {
			// 批量添加新记录
			String[] array = areaIds.split(",");
			List<ActivityCollectCommunity> areaList = new ArrayList<ActivityCollectCommunity>();
			for (String str : array) {
				ActivityCollectCommunity a = new ActivityCollectCommunity();
				a.setId(UuidUtils.getUuid());
				a.setCollectCouponsId(activityCollectCoupons.getId());
				a.setCommunityId(str);
				areaList.add(a);
			}
			activityCollectCommunityMapper.saveBatch(areaList);
		}
		// 可用余额- 冻结+ 代理商才同步
		if (!"0".equals(activityCollectCoupons.getBelongType())) {
			PayUpdateAmountDto dto = new PayUpdateAmountDto();
			dto.setUserId(activityCollectCoupons.getCreateUserId());
			dto.setAmount(activityCollectCoupons.getTotalCost());
			BaseResultDto result = payTradeServiceApi.freezeAmount(dto);
			if (result != null && !"0".equals(result.getCode())) {
				throw new Exception(result.getMsg());
			}
		}

	}

	@Transactional(rollbackFor = Exception.class)
	public void updateDynamic(ActivityCollectCoupons activityCollectCoupons) {
		activityCollectCouponsMapper.updateDynamic(activityCollectCoupons);
	}

	@Transactional(readOnly = true)
	public ActivityCollectCoupons get(String id) {
		return activityCollectCouponsMapper.get(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageUtils<ActivityCollectCoupons> list(Map<String, Object> map,int pageNumber,int pageSize) throws ServiceException{
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityCollectCoupons> result = activityCollectCouponsMapper.list(map);
		String belongType = "";
		if (map.containsKey("belongType")) {
			belongType = map.get("belongType").toString();
		}
		//转义一些字段
		if(result != null && result.size() > 0){
			for(ActivityCollectCoupons a : result){
				//类型
				a.setTypeName(ActivityCollectCouponsType.getName(a.getType()));
				//状态
				a.setStatusName(ActivityCollectCouponsStatus.getName(a.getStatus()));
				//审核状态
				a.setApprovalStatusName(ActivityCollectCouponsApprovalStatus.getName(a.getApprovalStatus()));
				
				//运营商要展示创建者名字
				if("0".equals(belongType)){
					if(StringUtils.isNotBlank(a.getCreateUserId())){
						//创建者是运营商
						if("0".equals(a.getBelongType())){
							SysUser su = iSysUserServiceApi.findSysUserById(a.getCreateUserId());
							a.setUpdateUserId(su == null ? "" : su.getUserName());
						}else{
							//创建者是代理商
							PsmsAgent pa = iPsmsAgentServiceApi.loadById(a.getBelongType());
							a.setUpdateUserId(pa == null ? "" : pa.getFullName());
						}
					}
				}
			}
		}
		
		return new PageUtils<ActivityCollectCoupons>(result);
	}

	// 以单个活动为事务单元,一个活动失败,不影响其他活动
	@Transactional(rollbackFor = Exception.class)
	public void updateBatchStatus(String id, int status, String updateUserId, Date updateTime, String belongType)
			throws Exception {
		ActivityCollectCoupons c = new ActivityCollectCoupons();
		c.setId(id);
		c.setStatus(status);
		c.setUpdateTime(updateTime);
		activityCollectCouponsMapper.updateDynamic(c);

		// 关闭,已结束,已失效,都要把每个代理商的活动返还金额
		if (status == ActivityCollectCouponsStatus.closed.getValue()
				|| status == ActivityCollectCouponsStatus.end.getValue()
				|| status == ActivityCollectCouponsStatus.disabled.getValue()) {
			ActivityCollectCoupons acc = activityCollectCouponsMapper.get(id);
			// 代理商的数据 才同步金额或者发短信
			if (acc != null && acc.getBelongType() != null && !"0".equals(acc.getBelongType())) {
				// 某个活动已经使用过的总金额
				BigDecimal useTotal = activityCollectCouponsMapper.selectTotalFaceValueByCollectId(id);
				// 活动总金额 - 已经使用过的差
				if (acc.getTotalCost() != null) {
					BigDecimal money = acc.getTotalCost().subtract(useTotal);
					// 如果差额等于0 就不用调用同步接口了
					if (money.compareTo(new BigDecimal(0)) != 0) {
						// 余额+,冻结-
						PayUpdateAmountDto dto = new PayUpdateAmountDto();
						dto.setUserId(acc.getCreateUserId());
						dto.setAmount(money);
						BaseResultDto result = payTradeServiceApi.unfreezeAmount(dto);
						if (result != null && !"0".equals(result.getCode())) {
							log.error("报错信息:");
							log.error("userId:" + dto.getUserId());
							log.error("money:" + money + " useTotal:" + useTotal + " totalCost:" + acc.getTotalCost());
							log.error("活动id:" + id);
							throw new Exception(id + "" + result.getMsg());
						}
					}
				}

				SysUser anentUser = sysUserMapper.getUserById(acc.getCreateUserId());
				if (anentUser != null && StringUtils.isNotEmpty(anentUser.getPhone())) {
					// 运营商关闭代理商的活动 或者 定时器失效给代理商发短信

					SmsVO smsVo = new SmsVO();
					smsVo.setId(UuidUtils.getUuid());
					smsVo.setIsTiming(0);
					smsVo.setToken(msgToken);
					smsVo.setSysCode(msgSysCode);
					smsVo.setMobile(anentUser.getPhone());
					smsVo.setSmsChannelType(3);
					smsVo.setSendTime(DateUtils.formatDateTime(new Date()));
					// 运营商关闭代理商的活动
					if ("0".equals(belongType) && status == ActivityCollectCouponsStatus.closed.getValue()) {
						try {
							smsVo.setContent("你的" + acc.getName() + "代金券活动已被运营商关闭");
							smsService.sendSms(smsVo);
						} catch (Exception e) {
							log.error("运营商关闭代理商的活动,发短信错误", e);
						}
					}

					// 定时器失效给代理商发短信
					if ("job".equals(belongType) && status == ActivityCollectCouponsStatus.disabled.getValue()) {
						try {
							smsVo.setContent("你的" + acc.getName() + "代金券活动超时没有通过审核，现已失效，请重新提交");
							smsService.sendSms(smsVo);
						} catch (Exception e) {
							log.error("定时器失效给代理商发短信,发短信错误", e);
						}
					}
				}
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void update(ActivityCollectCoupons activityCollectCoupons, List<String> couponsIds, String areaIds) {
		// 修改活动对象
		activityCollectCouponsMapper.updateDynamic(activityCollectCoupons);

		// 保存关联的代金券,把activityId改为当前对象的id
		activityCouponsMapper.updateActivityIdNull(activityCollectCoupons.getId());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("couponsIds", couponsIds);
		map.put("activityId", activityCollectCoupons.getId());
		activityCouponsMapper.updateBatchActivityId(map);

		// 代金卷范围类型：0全国，1区域，2小区 , 3店铺
		// 如果是区域
		if (activityCollectCoupons.getAreaType() == 1) {

			// 先删除老记录
			activityCollectAreaMapper.deleteByCollectCouponsId(activityCollectCoupons.getId());

			// 批量添加新记录
			String[] array = areaIds.split(",");

			List<ActivityCollectArea> areaList = new ArrayList<ActivityCollectArea>();
			for (String str : array) {
				ActivityCollectArea a = new ActivityCollectArea();
				a.setId(UuidUtils.getUuid());
				a.setCollectCouponsId(activityCollectCoupons.getId());
				a.setType(Integer.parseInt(str.split("-")[1]));
				a.setAreaId(str.split("-")[0]);
				areaList.add(a);
			}
			activityCollectAreaMapper.saveBatch(areaList);
		}

		// 如果是小区
		if (activityCollectCoupons.getAreaType() == 2) {

			// 先删除老记录
			activityCollectCommunityMapper.deleteByCollectCouponsId(activityCollectCoupons.getId());

			// 批量添加新记录
			String[] array = areaIds.split(",");
			List<ActivityCollectCommunity> areaList = new ArrayList<ActivityCollectCommunity>();
			for (String str : array) {
				ActivityCollectCommunity a = new ActivityCollectCommunity();
				a.setId(UuidUtils.getUuid());
				a.setCollectCouponsId(activityCollectCoupons.getId());
				a.setCommunityId(str);
				areaList.add(a);
			}
			activityCollectCommunityMapper.saveBatch(areaList);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityCollectArea> getAreaList(String collectCouponsId) {
		return activityCollectAreaMapper.listByCollectCouponsId(collectCouponsId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getAreaIds(String collectCouponsId) {
		List<ActivityCollectArea> list = this.getAreaList(collectCouponsId);

		List<String> ids = new ArrayList<String>();
		if (list != null && list.size() > 0) {
			for (ActivityCollectArea a : list) {
				ids.add(a.getAreaId());
			}
		}
		return ids;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityCollectCommunity> getCommunityList(String collectCouponsId) {
		return activityCollectCommunityMapper.listByCollectCouponsId(collectCouponsId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getCommunityIds(String collectCouponsId) {
		List<ActivityCollectCommunity> list = this.getCommunityList(collectCouponsId);

		List<String> ids = new ArrayList<String>();
		if (list != null && list.size() > 0) {
			for (ActivityCollectCommunity a : list) {
				ids.add(a.getCommunityId());
			}
		}
		return ids;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityCollectCouponsVo> findByStoreAndLimitType(Map<String, Object> params) throws ServiceException {

		List<ActivityCollectCouponsVo> result = activityCollectCouponsMapper.selectByStoreAndLimitType(params);
		int currentRecordCount = 0;
		// 判断指定用户是否领取指定代金券
		if (result != null && result.size() > 0) {
			for (ActivityCollectCouponsVo vo : result) {
				ActivityCouponsRecord activityCouponsRecord = new ActivityCouponsRecord();
				activityCouponsRecord.setCouponsCollectId(vo.getId());
				List<ActivityCoupons> activityCouponsList = new ArrayList<>();
				activityCouponsList = vo.getActivityCoupons();
				if (activityCouponsList != null && activityCouponsList.size() > 0) {
					for (ActivityCoupons activityCoupons : activityCouponsList) {
						activityCouponsRecord.setCouponsId(activityCoupons.getId());
						activityCouponsRecord.setCollectType(ActivityCouponsType.coupons);
						// 当前登陆用户id
						if (StringUtils.isNotEmpty(params.get("currentOperatUserId").toString())) {
							activityCouponsRecord.setCollectUserId(params.get("currentOperatUserId").toString());
							currentRecordCount = activityCouponsRecordMapper.selectCountByParams(activityCouponsRecord);
							// otherRecordCount =
							// activityCouponsRecordMapper.selectOtherCountByParams(activityCouponsRecord);
						}
						if (activityCoupons.getRemainNum() <= 0) {
							// 剩余数量小于0 显示已领完
							activityCoupons.setIsReceive(0);
						} else {
							if (currentRecordCount >= activityCoupons.getEveryLimit().intValue()) {
								// 已领取
								activityCoupons.setIsReceive(1);
							} else {
								// 立即领取
								activityCoupons.setIsReceive(2);
							}
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * @desc 用于判断某个时间段内活动是否冲突
	 * @param map
	 * @return
	 */
	@Transactional(readOnly = true)
	public int countTimeQuantum(Map<String, Object> map) {
		return activityCollectCouponsMapper.countTimeQuantum(map);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityCollectCouponsRecordVo> findByUnusedOrExpires(Map<String, Object> params) {
		return activityCollectCouponsMapper.selectByUnusedOrExpires(params);
	}

	/**
	 * @desc 查询出需要跑job的活动
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ActivityCollectCoupons> listByJob() {
		return activityCollectCouponsMapper.listByJob();
	}

	/**
	 * @desc 审核活动
	 * @param obj
	 */
	@Transactional(rollbackFor = Exception.class)
	public String updateApproval(ActivityCollectCoupons obj) throws Exception {
		activityCollectCouponsMapper.updateDynamic(obj);

		// 审核不通过就给代理商发短信,并且解冻代理商金额
		if (obj.getApprovalStatus() == ActivityCollectCouponsApprovalStatus.noPass.getValue()) {

			ActivityCollectCoupons acc = activityCollectCouponsMapper.get(obj.getId());
			// 代理商金额解冻 余额+,冻结-
			if (acc != null && acc.getBelongType() != null && !"0".equals(acc.getBelongType())) {
				PayUpdateAmountDto dto = new PayUpdateAmountDto();
				dto.setUserId(acc.getCreateUserId());
				dto.setAmount(acc.getTotalCost());

				BaseResultDto result = payTradeServiceApi.unfreezeAmount(dto);
				if (result != null && !"0".equals(result.getCode())) {
					return result.getMsg();
				}
			}

			try {
				// 给代理商发短信
				SysUser user = sysUserMapper.getUserById(obj.getCreateUserId());
				if (user != null && StringUtils.isNotEmpty(user.getPhone())) {
					SmsVO smsVo = new SmsVO();
					smsVo.setId(UuidUtils.getUuid());
					smsVo.setIsTiming(0);
					smsVo.setToken(msgToken);
					smsVo.setSysCode(msgSysCode);
					smsVo.setMobile(user.getPhone());
					// smsVo.setMobile("18566239287");
					smsVo.setContent("您好，你的代金券活动" + obj.getName() + "，因以下原因：（" + obj.getApprovalReason()
							+ "）审核不通过，如有疑问请联系运营商客服，客服电话：4008-357-889");
					smsVo.setSendTime(DateUtils.formatDateTime(new Date()));
					smsVo.setSmsChannelType(3);
					smsService.sendSms(smsVo);
				}
			} catch (Exception e) {
				log.error("审核活动,给代理商发短信异常:" + e);
			}
		}
		return "";
	}

	@Override
	@Transactional(readOnly = true)
	public int selectCountByStoreAndLimitType(Map<String, Object> params) throws ServiceException {
		int count = activityCollectCouponsMapper.selectCountByStoreAndLimitType(params);
		return count;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsServiceApi#findCollectCouponsAreaList(java.util.Map)
	 */
	@Override
	public int findCollectCouponsAreaList(Map<String, Object> map) throws ServiceException {
		return activityCollectCouponsMapper.findCollectCouponsAreaList(map);
	}
	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> saveCouponInfo(String userId, String provinceId, String cityId) throws ServiceException {
		Map<String, String> resultMap = new HashMap<String, String>();
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(cityId)) {
			throw new ServiceException("请求参数为空!");
		}
		
		ActivityCollectCouponsVo activityCollectCouponsVo = activityCollectCouponsMapper.findActivityCollectCouponsByCity(provinceId, cityId);
		if (activityCollectCouponsVo != null 
				&& activityCollectCouponsVo.getActivityCoupons() != null
				&& activityCollectCouponsVo.getActivityCoupons().size() > 0) {
			ActivityCoupons activityCoupon = activityCollectCouponsVo.getActivityCoupons().get(0);
			if (activityCoupon.getRemainNum() <= 0) {
				return resultMap;
			}
			// 设置代金券领取记录的代金券id、代金券领取活动id、活动类型，以便后面代码中的数量判断查询
			ActivityCouponsRecord activityCouponsRecord = new ActivityCouponsRecord();
			Date collectTime = DateUtils.getDateStart(new Date());
			activityCouponsRecord.setCouponsId(activityCoupon.getId());
			activityCouponsRecord.setCouponsCollectId(activityCoupon.getActivityId());
			activityCouponsRecord.setCollectType(ActivityCouponsType.openthedoor);
			activityCouponsRecord.setCollectTime(collectTime);
			//每日领取数
			int dailyCirculation = activityCouponsRecordMapper.selectCountByParams(activityCouponsRecord);
			//每日发行量是否已完
			if (dailyCirculation >= activityCollectCouponsVo.getDailyCirculation().intValue()) {
				return resultMap;
			}
			activityCouponsRecord.setCollectTime(null);
			activityCouponsRecord.setCollectUserId(userId);
			//总领取数
			int total = activityCouponsRecordMapper.selectCountByParams(activityCouponsRecord);
			//每人限领数量
			if (activityCoupon.getEveryLimit() > 0 && total >= activityCoupon.getEveryLimit().intValue()) {
				return resultMap;
			}
			//每人每天只能领一张代金券
			activityCouponsRecord.setCouponsCollectId(null);
			activityCouponsRecord.setCouponsId(null);
			activityCouponsRecord.setCollectTime(collectTime);
			//今天是否已领取
			int currentRecordCount = 0;
			currentRecordCount = activityCouponsRecordMapper.selectCountByParams(activityCouponsRecord);
			if (currentRecordCount > 0) {
				return resultMap;
			} else {
				log.info("userId:"+ userId +"==activityCouponId:"+ activityCoupon.getId());
				activityCouponsRecord.setId(UuidUtils.getUuid());
				Date date = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				activityCouponsRecord.setCouponsId(activityCoupon.getId());
				activityCouponsRecord.setCouponsCollectId(activityCoupon.getActivityId());
				activityCouponsRecord.setCollectTime(calendar.getTime());
				activityCouponsRecord.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);
				calendar.add(Calendar.DAY_OF_YEAR, activityCoupon.getValidDay());
				activityCouponsRecord.setValidTime(calendar.getTime());

				activityCouponsRecordMapper.insertSelective(activityCouponsRecord);
				//更新代金券剩余数量
				int rows = activityCouponsMapper.updateRemainNum(activityCoupon.getId());
				if (rows == 0) {
					throw new ServiceException("没有更新代金券剩余数量");
				}
				
				//代金券金额
				resultMap.put("couponPrice", String.valueOf(activityCoupon.getFaceValue()));
				//使用范围
				resultMap.put("usableRange", "限便利店");
			}
			
		} 		
		return resultMap;
	}
	
}