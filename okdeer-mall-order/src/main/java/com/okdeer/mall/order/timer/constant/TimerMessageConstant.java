/** 
 * @Copyright: Copyright ©205-22 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: TimerMessageConstant.java 
 * @Date: 216年5月11日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 * 
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 *     重构V4.1.0          2016年7月21日               wangf01             添加服务自动取消和确认收货枚举
 */
package com.okdeer.mall.order.timer.constant;

/**
 * 订单定时消息常量
 * 
 * @pr yschome-mall
 * @author guocp
 * @date 216年5月11日 上午11:48:06
 */
public interface TimerMessageConstant {

    /**
     * 单位*秒
     */
    int THOUSAND = 1000;
    
    /**
     * 最小时间间隔
     */
    int MIN_INTERVAL = THOUSAND * 30;

    /**
     * tag 通用通配符
     */
    String WILDCARD = "*";

    /** topic */
    String TOPIC_ORDER_TIMER = "topic_order_timer";

    /**
     * 消息TAG
     * 
     * @pr yschome-mall
     * @author guocp
     * @date 216年5月11日 下午2:15:56
     */
    enum Tag {

        /**
         * 支付超时处理 30分钟
         */
        tag_pay_timeout(30 * 60L),

        /**
         * 发货超时处理 3天
         */
        tag_delivery_timeout(3 * 24 * 60 * 60L),

        /**
         * 发货超时处理(团购) 14天
         */
        tag_delivery_group_timeout(14 * 24 * 60 * 60L),

        //begin by wangf01 2016.07.21
        /**
         * 服务订单发货超时处理，时间自定义（服务）
         */
        tag_delivery_server_timeout(0L),
        //end by wangf01 2016.07.21

        /**
         * 收货超时处理 1天
         */
        tag_confirm_timeout(1 * 24 * 60 * 60L),

        /**
         * 收货超时处理(团购) 7天
         */
        tag_confirm_group_timeout(7 * 24 * 60 * 60L),

        //begin by wangf01 2016.07.21
        /**
         * 服务订单收获超时处理，时间自定义（服务）
         */
        tag_confirm_server_timeout(0L),
        //end by wangf01 2016.07.21

        /**
         * 订单完成超时未售后处理,并送积分 1天
         */
        tag_finish_timeout(1 * 24 * 60 * 60L),

        /**
         * 订单完成超时未售后处理,并送积分(团购) 7天
         */
        tag_finish_group_timeout(7 * 24 * 60 * 60L),

        /**
         * 订单完成超时自动好评 3天
         */
        tag_finish_evaluate_timeout(3 * 24 * 60 * 60L),

        /********************* 退款单 *********************/
        /**
         * 退款单商家同意超时 3天
         */
        tag_refund_agree_timeout(3 * 24 * 60 * 60L),

        /**
         * 用户超时未撤销或未申请客服介入by商家拒绝申请 3天
         */
        tag_refund_cancel_by_refuse_apply_timeout(3 * 24 * 60 * 60L),

        /**
         * 用户超时未撤销或未申请客服介入by商家拒绝退款 3天
         */
        tag_refund_cancel_by_refuse_timeout(3 * 24 * 60 * 60L),

        /**
         * 用户退款超时未发货by商家同意 3天
         */
        tag_refund_cancel_by_agree_timeout(3 * 24 * 60 * 60L),

        /**
         * 商家超时未退款 3天
         */
        tag_refund_confirm_timeout(3 * 24 * 60 * 60L),

        /**
         * 商家超时未退款（团购） 7天
         */
        tag_refund_confirm_group_timeout(7 * 24 * 60 * 60L),

        /**
         * 服务商品结束自动退款（服务到期时间）
         */
        tag_service_order_refund_timeout(1L);

        /**
         * 枚举值
         */
        private Long value;

        Tag(Long value) {
            this.value = value;
        }

        public Long getValue() {
            return this.value;
        }

        /**
         * 根据值取枚举
         * 
         * @param value
         *            枚举值
         * @return 枚举对象
         */
        public static Tag enumValueOf(Long value) {
            if (value == null) {
                return null;
            }
            for (Tag tag : values()) {
                if (value.equals(tag.getValue())) {
                    return tag;
                }
            }
            return null;
        }

        /**
         * 根据值取枚举
         * 
         * @param value
         *            枚举值
         * @return 枚举对象
         */
        public static Tag enumNameOf(String name) {
            if (name == null) {
                return null;
            }
            for (Tag tag : values()) {
                if (name.equals(tag.name())) {
                    return tag;
                }
            }
            return null;
        }

    }

}
