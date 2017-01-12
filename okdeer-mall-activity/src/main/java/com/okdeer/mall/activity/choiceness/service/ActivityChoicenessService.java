/**
 * @Project: yschome-mall-activity
 * @Author: lijun
 * @Date: 2016年7月18日
 * @Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved.
 */

package com.okdeer.mall.activity.choiceness.service;


import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.choiceness.entity.ActivityChoiceness;
import com.okdeer.mall.activity.choiceness.vo.ActivityChoicenessFilterVo;
import com.okdeer.mall.activity.choiceness.vo.ActivityChoicenessListPageVo;

import java.util.List;

/**
 * ClassName: ActivityChoicenessService 
 * @Description: 精选活动service接口
 * @author lijun
 * @date 2016年7月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      重构 4.1         2016年7月18日                                 lijun               新增
 *
 */
public interface ActivityChoicenessService {
    /**
     * @Description: 查询精选活动列表页（分页）
     * @param queryFilterVo 查询条件Vo
     * @param pageNumber 当前页
     * @param pageSize 每页显示记录数
     * @return PageUtils 分页插件
     * @throws Exception 抛出异常
     * @author lijun
     * @date 2016年7月19日
     */
    PageUtils<ActivityChoicenessListPageVo> findChoicenessListPageByFilter(
            ActivityChoicenessFilterVo queryFilterVo, Integer pageNumber, Integer pageSize) throws Exception;

    /**
     * @Description: 批量添加精选服务
     * @param storeSkuIds 商品id集合
     * @throws Exception 抛出异常
     * @author lijun
     * @date 2016年7月19日
     */
    List<String> addByBatch(List<String> storeSkuIds) throws Exception;

    /**
     * @Description: 批量删除精选服务
     * @param choicenessIds 精选服务id集合
     * @throws Exception 抛出异常
     * @author lijun
     * @date 2016年7月19日
     */
    List<String> deleteByIds(List<String> choicenessIds) throws Exception;

    /**
     * @Description: 通过主键id获取精选服务信息
     * @param choicenessId 精选服务主键id
     * @return ActivityChoiceness 实体类
     * @throws Exception   抛出异常
     * @author lijun
     * @date 2016年7月19日
     */
    ActivityChoiceness findById(String choicenessId) throws Exception;

    /**
     * @Description: 更新精选活动状态
     * @param activityId 活动id
     * @param sortValue 排序值
     * @throws Exception 抛出异常
     * @author lijun
     * @date 2016年7月21日
     */
    List<String> updateChoicenessStatus(String activityId, String sortValue) throws Exception;

    /**
     * @Description: 批量校验商品是否存在
     * @param skuIds 商品id集合
     * @return 返回结果
     * @author zhongy
     * @date 2016年8月04日
     */
    Integer findCountBySkuIds(List<String> skuIds) throws Exception;

    /**
     * @Description: 批量删除精选服务商品
     * @param  精选服务商品id集合
     * @author zhongy
     * @date 2016年8月05日
     */
    Integer deleteBySkuIds(List<String> goodsStoreSkuIds) throws ServiceException;
}
