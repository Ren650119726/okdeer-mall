package com.okdeer.mall.ele.mapper;

import com.okdeer.mall.ele.entity.ExpressPushLog;

import java.util.List;

/**
 * ClassName: ExpressPushLog
 *
 * @author wangf01
 * @Description: 订单快递配送信息推送日志-mapper
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public interface ExpressPushLogMapper {

    /**
     * 根据条件查询符合的数据
     *
     * @param param ExpressPushLog 查询数据的条件
     * @return List<ExpressPushLog>
     */
    List<ExpressPushLog> selectExpressPushLogListByParam(ExpressPushLog param);

    /**
     * 保存数据
     *
     * @param param ExpressPushLog 查询数据的条件
     * @return int
     */
    int insert(ExpressPushLog param);
}
