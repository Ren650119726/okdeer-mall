package com.okdeer.mall.activity.coupons.bo;

/**
 * ClassName: CouponsUseRecordBo 
 * @Description: 活动使用记录统计对象
 * @author maojj
 * @date 2017年5月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.3 		2017年5月18日				maojj
 */
public class ActivityRecordBo {

	/**
	 * 活动相关主键Id
	 */
	private String pkId;

	/**
	 * 使用总次数
	 */
	private int totalNum;

	public String getPkId() {
		return pkId;
	}

	public void setPkId(String pkId) {
		this.pkId = pkId;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

}
