package com.okdeer.mall.ele.util;

import com.okdeer.base.common.enums.ViewEnum;
import org.apache.commons.lang.StringUtils;

/**
 * ClassName: ExpressOrderStatus
 *
 * @author wangf01
 * @Description: 第三方配送状态
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public enum ExpressOrderStatus implements ViewEnum {

    STATUS_4("已取消(同步取消不需要关注此状态)"),
    STATUS_5("异常"),
    STATUS_1("系统已接单"),
    STATUS_20("已分配骑手"),
    STATUS_80("骑手已到店"),
    STATUS_2("配送中"),
    STATUS_3("已送达");

    private String value;

    ExpressOrderStatus(String value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getValue() {
        return this.value;
    }

    /**
     * 根据值取枚举
     *
     * @param value 枚举值
     * @return 枚举对象
     */
    public static ExpressOrderStatus enumValueOf(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (ExpressOrderStatus expressOrderStatus : values()) {
            if (value.equalsIgnoreCase(expressOrderStatus.getValue())) {
                return expressOrderStatus;
            }
        }
        return null;
    }

    /**
     * @param ordinal
     * @return ActivityTypeEnum
     * @throws
     * @Description: 根据ordinal取枚举
     * @author maojj
     * @date 2016年7月14日
     */
    public static ExpressOrderStatus enumValueOf(int ordinal) {
        for (ExpressOrderStatus expressOrderStatus : values()) {
            if (expressOrderStatus.ordinal() == ordinal) {
                return expressOrderStatus;
            }
        }
        return null;
    }
}
