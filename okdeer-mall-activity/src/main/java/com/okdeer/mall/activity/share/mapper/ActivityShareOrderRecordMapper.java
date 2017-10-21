/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityShareOrderRecordMapper.java
 * @Date 2017-10-20 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */

package com.okdeer.mall.activity.share.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.share.bo.ActivityShareOrderRecordParamBo;
import com.okdeer.mall.activity.share.entity.ActivityShareOrderRecord;

public interface ActivityShareOrderRecordMapper extends IBaseMapper {
	
	/**
	 * @Description: 查询列表
	 * @param activityShareOrderRecordParamBo
	 * @return
	 * @author zengjizu
	 * @date 2017年10月20日
	 */
	List<ActivityShareOrderRecord> findList(ActivityShareOrderRecordParamBo activityShareOrderRecordParamBo);
	
	
}