/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: ColumnAdvertApproval.java 
 * @Date: 2016年1月28日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.operate.advert.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.okdeer.mall.advert.entity.ColumnAdvertApproval;
import com.yschome.base.dal.IBaseCrudMapper;

/**
 * 开放栏目审核Mapper
 * 
 * @project yschome-mall
 * @author zhaoqc
 * @date 2016年1月28日 下午2:43:05
 */
@Repository
public interface ColumnAdvertApprovalMapper extends IBaseCrudMapper {
	
	/**
	 * 根据广告Id查找广告审核信息
	 *
	 * @param advertId 广告Id
	 * @return 广告审核信息
	 */
	ColumnAdvertApproval getApprovalByAdvertId(@Param("advertId") String advertId);

}
