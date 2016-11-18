/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月17日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.po;

import java.math.BigDecimal;
import java.util.Set;
import java.util.Set;

/**
 * ClassName: RiskOrderRecordPo 
 * @Description: 查询触发风控条件po
 * @author guocp
 * @date 2016年11月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class RiskOrderRecordPo {
	
	/**
	 * 下单次数
	 */
	Integer count;
	
	/**
	 * 总面额
	 */
	BigDecimal facePriceTotal;
	
	/**
	 * 充值手机号列表
	 */
	Set<String> tels;
	
	/**
	 * 登入帐号列表
	 */
	Set<String> loginNames;

	
	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	
	/**
	 * @param count the count to Set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	
	/**
	 * @return the facePriceTotal
	 */
	public BigDecimal getFacePriceTotal() {
		return facePriceTotal;
	}

	
	/**
	 * @param facePriceTotal the facePriceTotal to Set
	 */
	public void setFacePriceTotal(BigDecimal facePriceTotal) {
		this.facePriceTotal = facePriceTotal;
	}

	
	/**
	 * @return the tels
	 */
	public Set<String> getTels() {
		return tels;
	}

	
	/**
	 * @param tels the tels to Set
	 */
	public void setTels(Set<String> tels) {
		this.tels = tels;
	}

	
	/**
	 * @return the loginNames
	 */
	public Set<String> getLoginNames() {
		return loginNames;
	}

	
	/**
	 * @param loginNames the loginNames to Set
	 */
	public void setLoginNames(Set<String> loginNames) {
		this.loginNames = loginNames;
	}
	
	
	
	
}
