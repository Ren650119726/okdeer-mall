package com.okdeer.mall.ele.mapper;

import com.okdeer.mall.ele.entity.ExpressCallbackLog;

import java.util.List;

/**
 * ClassName: ExpressCallbackLogMapper
 *
 * @author wangf01
 * @Description: 订单快递配送信息回调日志-mapper
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public interface ExpressCallbackLogMapper {

    /**
     * 根据条件查询符合的数据
     *
     * @param param ExpressCallbackLog 查询数据的条件
     * @return List<ExpressCallbackLog>
     */
    List<ExpressCallbackLog> selectExpressCallbackLogListByParam(ExpressCallbackLog param);

    /**
     * 保存数据
     *
     * @param param ExpressCallbackLog 查询数据的条件
     * @return int
     */
    int insert(ExpressCallbackLog param);
}
