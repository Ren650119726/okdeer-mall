/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityJoinRecordMapper.java
 * @Date 2017-10-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.discount.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountMultiItem;

public interface ActivityDiscountMultiItemMapper extends IBaseMapper {

	/**
	 * @Description: 通过活动id删除
	 * @author zhangkn
	 * @date 2017年12月6日
	 */
	void deleteByActivityId(String activityId);
	
	/**
	 * @Description: 批量添加
	 * @param list
	 * @author zhangkn
	 * @date 2017年12月6日
	 */
	void addBatch(List<ActivityDiscountMultiItem> list);
	
	/**
	 * @Description: 通过活动id加载列表
	 * @param activityId
	 * @return
	 * @author zhangkn
	 * @date 2017年12月6日
	 */
	List<ActivityDiscountMultiItem> findByActivityId(@Param("activityId")String activityId,
			@Param("activityItemId")String activityItemId);
}