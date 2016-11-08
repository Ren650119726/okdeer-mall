package com.okdeer.mall.activity.label.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.activity.label.entity.ActivityLabel;

/**
 * @pr yscm
 * @desc 服务标签活动
 * @author zhangkn
 * @date 2016-11-04 下午3:12:43
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
public interface ActivityLabelMapper {
	
	/**
	 * @Description: 保存
	 * @param activityLabel 标签对象
	 * @throws Exception
	 * @author YSCGD
	 * @date 2016年11月4日
	 */
	void add(ActivityLabel activityLabel) throws Exception;

	/**
	 * @Description: 修改
	 * @param activityLabel
	 * @param goodsIds
	 * @throws Exception
	 * @author YSCGD
	 * @date 2016年11月4日
	 */
	void update(ActivityLabel activityLabel) throws Exception;

	/**
	 * @Description: 通过id取对象
	 * @param id
	 * @return
	 * @author YSCGD
	 * @date 2016年11月4日
	 */
	ActivityLabel get(String id);
	
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
	 * @Description: 批量修改状态
	 * @param id
	 * @param status 活动状态 0 未开始 ，1：进行中2:已结束 3 已关闭
	 * @param updateUserId 修改人
	 * @param updateTime 修改时间
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月4日
	 */
	void updateBatchStatus(String id,int status,String updateUserId,Date updateTime) throws Exception;
	
	/**
	 * @desc 用于判断某个时间段内活动是否冲突
	 * @param map
	 * @return
	 */
	int countTimeQuantum(Map<String,Object> map);
	
	/**
	 * @desc 查询出需要跑job的活动
	 * @return
	 */
	public List<ActivityLabel> listByJob();
	
}
