/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: OrgMessageConstant.java 
 * @Date: 2015年11月27日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.activity.coupons.service;

/**
 * 物业同步消息常量
 * 
 * @pr yschome-mall
 * @author guocp
 * @date 2015年11月27日 下午3:09:36
 */
public interface MessageConstant {
	
	/** UTF-8转码 */
	String EN_DECODE = "utf-8";

	/** topic */
	String TOPIC_ORG_AGENT = "topic_org_agent";
	/** add tag */
	String TAG_ORG_AGENT_ADD = "tag_org_agent_add";
	/** update tag */
	String TAG_ORG_AGENT_UPDATE = "tag_org_agent_update";
	/** delete tag */
	String TAG_ORG_AGENT_DELETE = "tag_org_agent_delete";
	
	
	/** topic */
	String TOPIC_PROPERTY_COMPANY = "topic_property_company";
	/** add tag */
	String TAG_PROPERTY_COMPANY_ADD = "tag_property_company_add";
	/** update tag */
	String TAG_PROPERTY_COMPANY_UPDATE = "tag_property_company_update";
	/** delete tag */
	String TAG_PROPERTY_COMPANY_DELETE = "tag_property_company_delete";
	
	
	/** topic */
	String TOPIC_SMALL_COMMUNITY = "topic_small_community";
	/** add tag */
	String TAG_SMALL_COMMUNITY_ADD = "tag_small_community_add";
	/** update tag */
	String TAG_SMALL_COMMUNITY_UPDATE = "tag_small_community_update";
	/** delete tag */
	String TAG_SMALL_COMMUNITY_DELETE = "tag_small_community_delete";
	
	/** topic */
	String TOPIC_ACTIVITY_COLLECT_COUPONS = "topic_activity_collect_coupons";
	/** add tag */
	String TAG_ACTIVITY_COLLECT_COUPONS_AGENT_ADD = "tag_activity_collect_coupons_agent_add";
}
