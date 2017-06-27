package com.okdeer.mall.ele.mapper;

import com.okdeer.mall.ele.entity.ExpressCallback;
import com.okdeer.mall.express.dto.ExpressCallbackParamDto;

import java.util.List;

/**
 * ClassName: ExpressCallbackMapper
 *
 * @author wangf01
 * @Description: 订单快递配送信息回调-mapper
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public interface ExpressCallbackMapper {

    /**
     * 根据条件查询符合的数据
     *
     * @param param ExpressCallback 查询数据的条件
     * @return ExpressCallback
     */
    ExpressCallback selectExpressCallbackByParam(ExpressCallback param);

    /**
     * 根据条件查询符合的数据
     *
     * @param param ExpressCallback 查询数据的条件
     * @return List<ExpressCallback>
     */
    List<ExpressCallback> selectExpressCallbackListByParam(ExpressCallback param);

    /**
     * 根据条件查询符合的数据
     *
     * @param paramDto ExpressCallbackParamDto 查询数据的条件
     * @return List<ExpressCallback>
     */
    List<ExpressCallback> selectExpressCallbackByParamDto(ExpressCallbackParamDto paramDto);

    /**
     * 保存数据
     *
     * @param param ExpressCallback 查询数据的条件
     * @return int
     */
    int insert(ExpressCallback param);

    /**
     * 更新数据
     *
     * @param param ExpressCallback 查询数据的条件
     * @return int
     */
    int update(ExpressCallback param);
}
