package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;
import java.util.Map;

import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsThirdCode;

/**
 * ClassName: ActivityCouponsYiyeExchangeCodeMapper 
 * @Description: 代金卷异业兑换码管理
 * @author zhangkn
 * @date 2016年12月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2016年12月12日 			zhangkn    新增查询对应类目名称
 */
public interface ActivityCouponsThirdCodeMapper extends IBaseCrudMapper{
	
	/**
	 * @Description: 批量插入
	 * @param list
	 * @author zhangkn
	 * @date 2016年12月8日
	 */
	void saveBatch(List<ActivityCouponsThirdCode> list);
	
	/**
	 * @Description: 根据筛选条件查询列表
	 * @param param
	 * @return
	 * @author zhangkn
	 * @date 2016年12月8日
	 */
	List<ActivityCouponsThirdCode> listByParam(Map<String,Object> param);
	
	/**
	 * @Description: 根据代金券id删除记录
	 * @param couponsId
	 * @author zhangkn
	 * @date 2016年12月9日
	 */
	void deleteByCouponsId(String couponsId);
}
