
package com.okdeer.mall.activity.share.service;


import java.util.List;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.share.bo.ActivityShareRecordNumParamBo;
import com.okdeer.mall.activity.share.dto.ActivityShareRecordParamDto;
import com.okdeer.mall.activity.share.entity.ActivityShareRecord;

public interface ActivityShareRecordService extends IBaseService {

	PageUtils<ActivityShareRecord> findList(ActivityShareRecordParamDto activityShareRecordParamDto, int pageNum,
			int pageSize);

	List<ActivityShareRecord> findList(ActivityShareRecordParamDto activityShareRecordParamDto);
	/**
	 * @Description: 更新数量
	 * @param activityShareRecordNumParamBo
	 * @return
	 * @author zengjizu
	 * @date 2017年10月24日
	 */
	int updateNum(ActivityShareRecordNumParamBo activityShareRecordNumParamBo);
}
