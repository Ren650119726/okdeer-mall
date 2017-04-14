/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityLuckDrawMapper.java
 * @Date 2017-04-11 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.prize.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDraw;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDrawVo;

public interface ActivityLuckDrawMapper extends IBaseMapper {

	/**
	 * @Description: 查询模板列表
	 * @param activityLuckDrawVo
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月11日
	 */
	List<ActivityLuckDraw> findPrizeRecordList(ActivityLuckDrawVo activityLuckDrawVo);

	/**
	 * @Description: 通过模板名称查询模板数量
	 * @param activityLuckDraw
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月11日
	 */
	int findCountByName(ActivityLuckDraw activityLuckDraw);

	/**
	 * @Description: 批量关闭抽奖模板
	 * @param ids   
	 * @author xuzq01
	 * @date 2017年4月11日
	 */
	void closedLuckDraw(@Param("ids") List<String> ids);

}