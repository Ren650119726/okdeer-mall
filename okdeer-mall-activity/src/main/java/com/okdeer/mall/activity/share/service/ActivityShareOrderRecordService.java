
package com.okdeer.mall.activity.share.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.share.bo.ActivityShareOrderRecordParamBo;
import com.okdeer.mall.activity.share.entity.ActivityShareOrderRecord;

public interface ActivityShareOrderRecordService extends IBaseService {

	List<ActivityShareOrderRecord> findList(ActivityShareOrderRecordParamBo activityShareOrderRecordParam);

}
