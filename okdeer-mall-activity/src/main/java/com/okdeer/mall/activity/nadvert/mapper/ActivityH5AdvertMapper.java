/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityH5AdvertMapper.java
 * @Date 2017-08-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.nadvert.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5Advert;
import com.okdeer.mall.activity.nadvert.param.ActivityH5AdvertQParam;

public interface ActivityH5AdvertMapper extends IBaseMapper {
	
	/**
	 * @Description: 根据参数查询h5活动列表
	 * @return List<ActivityH5Advert>
	 * @throws
	 * @author mengsj
	 * @date 2017年8月12日
	 */
	List<ActivityH5Advert> findByParam(ActivityH5AdvertQParam param);
	
	/**
	 * @Description: 查询未开始和已结束的h5活动
	 * @return List<ActivityH5Advert>
	 * @throws
	 * @author mengsj
	 * @date 2017年8月15日
	 */
	List<ActivityH5Advert> listByJob(@Param("currentTime") Date currentTime);
	
	/**
	 * @Description: 修改h5活动状态
	 * @param entity
	 * @throws Exception void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月15日
	 */
	void updateBatchStatus(ActivityH5Advert entity) throws Exception;

}