
package com.okdeer.mall.activity.coupons.api;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleRemind;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRemindApi;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRemindService;
import com.okdeer.mall.activity.coupons.vo.ActivitySaleRemindVo;

/**
 * 
 * ClassName: ActivitySaleRemindApiImpl 
 * @Description: 活动安全库存预警提醒
 * @author tangy
 * @date 2017年2月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.0.0          2017年2月21日                               tangy             新增
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivitySaleRemindApi")
public class ActivitySaleRemindApiImpl implements ActivitySaleRemindApi {

	/**
	 * 安全库存联系关联人
	 */
	@Autowired
	private ActivitySaleRemindService activitySaleRemindService;

	@Override
	public void update(List<ActivitySaleRemindVo> activitySaleRemindVos, String saleId) throws ServiceException {
		if (StringUtils.isBlank(saleId)) {
			return;
		}
        // 删除活动安全库存预警联系人
		activitySaleRemindService.deleteBySaleId(saleId);
		// 添加活动安全库存预警联系人
		if (CollectionUtils.isNotEmpty(activitySaleRemindVos)) {
			List<ActivitySaleRemind> list = BeanMapper.mapList(activitySaleRemindVos, ActivitySaleRemind.class);
			activitySaleRemindService.insertSelectiveBatch(list);
		}
	}

}
