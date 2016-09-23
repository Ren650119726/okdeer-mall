/** 
 *@Project: yschome-mall-activity 
 *@Author: lijun
 *@Date: 2016年7月18日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.activity.choiceness.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.choiceness.entity.ActivityChoiceness;
import com.okdeer.mall.activity.choiceness.mapper.ActivityChoicenessMapper;
import com.okdeer.mall.activity.choiceness.service.ActivityChoicenessService;
import com.okdeer.mall.activity.choiceness.service.ActivityChoicenessServiceApi;
import com.okdeer.mall.activity.choiceness.vo.ActivityChoicenessFilterVo;
import com.okdeer.mall.activity.choiceness.vo.ActivityChoicenessListPageVo;
import com.okdeer.mall.common.consts.Constant;

/**
 * ClassName: ActivityChoicenessServiceImpl 
 * @Description: 精选活动service实现类
 * @author lijun
 * @date 2016年7月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      重构 4.1         2016年7月18日                                 lijun               新增
 *      重构 4.1         2016年8月04日                                 zhongy              新增校验商品是否存在方法
 *      重构 4.1         2016年8月05日                                 zhongy              批量删除精选服务商品 
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.choiceness.service.ActivityChoicenessServiceApi")
public class ActivityChoicenessServiceImpl implements ActivityChoicenessService, ActivityChoicenessServiceApi {

	/**
	 * 日志输出
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActivityChoicenessServiceImpl.class);

	/**
	 * 注入秒杀活动Mapper接口
	 */
	@Autowired
	ActivityChoicenessMapper activityChoicenessMapper;

	/**
	 * 自动注入店鋪商品Service
	 */
	@Reference(version = "1.0.0", check = false)
	GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	@Override
	public PageUtils<ActivityChoicenessListPageVo> findChoicenessListPageByFilter(
			ActivityChoicenessFilterVo queryFilterVo, Integer pageNumber, Integer pageSize) throws Exception {
		// 分页组件
		PageHelper.startPage(pageNumber, pageSize, true);
		queryFilterVo.setEndTime(
				queryFilterVo.getEndTime() != null ? DateUtils.getDateEnd(queryFilterVo.getEndTime()) : null);
		// 精选活动列表页查询
		List<ActivityChoicenessListPageVo> choicenessList = activityChoicenessMapper
				.findChoicenessListPageByFilter(queryFilterVo);
		// 分页
		return new PageUtils<ActivityChoicenessListPageVo>(choicenessList);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addByBatch(List<String> storeSkuIds) throws Exception {

		// 获取店铺商品信息
		List<GoodsStoreSku> goodsStoreSkuList = goodsStoreSkuServiceApi.findByIds(storeSkuIds);

		// 精选活动服务
		List<ActivityChoiceness> activityChoicenessList = new ArrayList<ActivityChoiceness>();
		for (GoodsStoreSku goodsStoreSku : goodsStoreSkuList) {
			ActivityChoiceness activityChoiceness = new ActivityChoiceness();
			activityChoiceness.setId(UuidUtils.getUuid());
			activityChoiceness.setGoodsStoreSkuId(goodsStoreSku.getId());
			activityChoiceness.setCreateTime(new Date());
			activityChoiceness.setStoreId(goodsStoreSku.getStoreId());
			activityChoiceness.setSort(Constant.ZERO);
			activityChoicenessList.add(activityChoiceness);
		}
		// 添加精选服务
		activityChoicenessMapper.addByBatch(activityChoicenessList);

		// 更新商品精选服务状态
		goodsStoreSkuServiceApi.updateChoicenessStatus(storeSkuIds, WhetherEnum.whether, new Date());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteByIds(List<String> choicenessIds) throws Exception {
		List<String> storeSkuIds = new ArrayList<String>();
		List<ActivityChoiceness> choicenessList = activityChoicenessMapper.findByPrimaryKeyList(choicenessIds);
		for (ActivityChoiceness cho : choicenessList) {
			storeSkuIds.add(cho.getGoodsStoreSkuId());
		}

		// 删除精选服务
		activityChoicenessMapper.deleteByIds(choicenessIds);

		// 更新商品精选服务状态
		goodsStoreSkuServiceApi.updateChoicenessStatus(storeSkuIds, WhetherEnum.not, new Date());
	}

	@Override
	public ActivityChoiceness findById(String choicenessId) throws Exception {
		return activityChoicenessMapper.findByPrimaryKey(choicenessId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateChoicenessStatus(String activityId, String sortValue) throws Exception {
		activityChoicenessMapper.updateChoicenessStatus(activityId, sortValue);
	}

	@Override
	public Integer findCountBySkuIds(List<String> skuIds) throws Exception {

		return activityChoicenessMapper.findCountBySkuIds(skuIds);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer deleteBySkuIds(List<String> goodsStoreSkuIds) throws ServiceException {
		return activityChoicenessMapper.deleteBySkuIds(goodsStoreSkuIds);
	}
}
