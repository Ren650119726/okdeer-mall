package com.okdeer.mall.activity.coupons.bo;

import java.util.List;

/**
 * ClassName: ActivityRecordParamBo 
 * @Description: 活动记录请求参数Bo
 * @author maojj
 * @date 2017年5月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.3 		2017年5月18日				maojj
 */
public class ActivityRecordParamBo {

	/**
	 * 业务Id列表。如果是代金券，则为代金券Id列表;如果是满减,则为满减Id列表;如果是代金券活动，则为活动ID
	 */
	private List<String> pkIdList;

	/**
	 * 代金券Id
	 */
	private String pkId;
	
	/**
	 * 代金券活动Id
	 */
	private String collectId;

	/**
	 * 设备Id
	 */
	private String deviceId;

	/**
	 * 用户Id
	 */
	private String userId;

	/**
	 * 记录日期。格式为（yyyy-MM-dd）
	 */
	private String recDate;

	public List<String> getPkIdList() {
		return pkIdList;
	}

	public void setPkIdList(List<String> pkIdList) {
		this.pkIdList = pkIdList;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRecDate() {
		return recDate;
	}

	public void setRecDate(String recDate) {
		this.recDate = recDate;
	}

	public String getPkId() {
		return pkId;
	}

	public void setPkId(String pkId) {
		this.pkId = pkId;
	}

	
	public String getCollectId() {
		return collectId;
	}

	
	public void setCollectId(String collectId) {
		this.collectId = collectId;
	}

}
