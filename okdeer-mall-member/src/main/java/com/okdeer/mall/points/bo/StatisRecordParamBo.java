
package com.okdeer.mall.points.bo;

import java.util.List;

/**
 * ClassName: StatisRecordParamBo 
 * @Description: 统计纪录查询参数
 * @author zengjizu
 * @date 2016年12月30日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class StatisRecordParamBo {

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 积分类型编码
	 */
	private String code;

	/**
	 * 开始时间
	 */
	private String startTime;

	/**
	 * 结束时间
	 */
	private String endTime;

	/**
	 * 排除的code
	 */
	private List<String> existsCodeList;

	private Integer type;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public List<String> getExistsCodeList() {
		return existsCodeList;
	}

	public void setExistsCodeList(List<String> existsCodeList) {
		this.existsCodeList = existsCodeList;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
