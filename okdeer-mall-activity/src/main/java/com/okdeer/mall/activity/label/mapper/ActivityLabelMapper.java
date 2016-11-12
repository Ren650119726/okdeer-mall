package com.okdeer.mall.activity.label.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.label.entity.ActivityLabel;

/**
 * @pr yscm
 * @desc 服务标签活动
 * @author zhangkn
 * @date 2016-11-04 下午3:12:43
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
public interface ActivityLabelMapper extends IBaseMapper{
	
	/**
	 * @Description: 查询标签列表
	 * @param map 参数
	 * @param pageNumber 当前页码
	 * @param pageSize 每页条数
	 * @return PageUtils<ActivityCollectCoupons>  
	 * @throws ServiceException service异常
	 * @author YSCGD
	 * @date 2016年11月7日
	 */
	List<ActivityLabel> list(Map<String,Object> map) throws ServiceException;
	
	/**
	 * @Description: 查询可选商品列表
	 * @param map 参数
	 * @param pageNumber 当前页码
	 * @param pageSize 每页条数
	 * @return PageUtils<ActivityCollectCoupons>  
	 * @throws Exception service异常
	 * @author zhangkn
	 * @date 2016年11月7日
	 */
	List<Map<String,Object>> listGoods(Map<String,Object> map) throws Exception;
	
	
	/**
	 * @desc 用于判断某个时间段内活动是否冲突
	 * @param map
	 * @return
	 */
	int countTimeQuantum(Map<String,Object> map);
	
	/**
	 * 1、查询活动未开始，开始时间小于当前的数据 即为要设置开始，2、活动开始、结束时间小于当前的数据 即为要设置结束
	 * @param map 传递参数
	 * @author tuzhd
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ActivityLabel> listByJob(Map<String,Object> map);
	
}
