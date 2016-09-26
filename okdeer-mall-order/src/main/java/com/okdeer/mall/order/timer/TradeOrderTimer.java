/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: TradeOrderTimer.java 
 * @Date: 2016年5月4日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.okdeer.mall.order.timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.common.message.Message;
import com.google.common.base.Charsets;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.mall.order.timer.constant.TimerMessageConstant;
import com.okdeer.mall.system.utils.mapper.JsonMapper;

/**
 * 订单超时计时器
 * 
 * @pr yschome-mall
 * @author guocp
 * @date 2016年5月4日 下午4:05:25
 */
@Service
public class TradeOrderTimer implements TimerMessageConstant {

    // private static final Logger logger =
    // LoggerFactory.getLogger(TradeOrderTimer.class);

    /**
     * 延迟等级 "0s 30s 1m 2m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h 12h 1d 2d 5d 7d"
     */
    private static final long[] DELAY_LEVELS = { 0, 30, 60, 120, 300, 360, 420, 480, 540, 600, 1200, 1800, 3600, 7200,
            43200, 86400, 172800, 432000, 604800 };

    /**
     * rocketmq 生产者
     */
    @Autowired
    private RocketMQProducer rocketMQProducer;

    /**
     * 根据时长获取延迟等级
     * 
     * @param dataLength
     *            时长
     * @return 延迟等级
     */
    public int getDelayLevel(long dataLength) {
        if (dataLength < 0) {
            return 0;
        }
        int level = 0;
        while (dataLength >= DELAY_LEVELS[level]) {
            if (++level >= DELAY_LEVELS.length) {
                break;
            }
        }
        return level - 1;
    }

    /**
     * 发送定时处理消息
     * 
     * @param key
     *            KEY
     * @param delayTimeMillis
     *            延迟时间
     */
    public void sendTimerMessage(final Tag tag, final String key, long delayTimeMillis) throws Exception {
        TimeoutMessage message = new TimeoutMessage(key, System.currentTimeMillis());
        String json = JsonMapper.nonEmptyMapper().toJson(message);
        Message msg = new Message(TOPIC_ORDER_TIMER, tag.name(), json.getBytes(Charsets.UTF_8));
        msg.setDelayTimeLevel(getDelayLevel(delayTimeMillis));
        rocketMQProducer.send(msg);
    }

    /**
     * 发送定时处理消息
     * 
     * @param key
     *            KEY
     */
    public void sendTimerMessage(final Tag tag, final String key) throws Exception {
        sendTimerMessage(tag, key, tag.getValue());
    }

    /**
     * 时间未到重新发送消息
     * 
     * @param message
     *            消息内容
     */
    public void sendAfreshTimerMessage(final Tag tag, final TimeoutMessage message, long currentTime) throws Exception {
        String json = JsonMapper.nonEmptyMapper().toJson(message);
        Message msg = new Message(TOPIC_ORDER_TIMER, tag.name(), json.getBytes(Charsets.UTF_8));
        long dateLength = (tag.getValue() * THOUSAND - (currentTime - message.getSendDate())) / THOUSAND;
        msg.setDelayTimeLevel(getDelayLevel(dateLength));
        rocketMQProducer.send(msg);
    }

    /**
     * 时间未到重新发送消息
     * 
     * @param message
     *            消息内容
     */
    public void sendAfreshTimerMessage(final Tag tag, final TimeoutMessage message, long currentTime, long endTime)
            throws Exception {
        String json = JsonMapper.nonEmptyMapper().toJson(message);
        Message msg = new Message(TOPIC_ORDER_TIMER, tag.name(), json.getBytes(Charsets.UTF_8));
        long dateLength = (endTime - currentTime) / THOUSAND;
        msg.setDelayTimeLevel(getDelayLevel(dateLength));
        rocketMQProducer.send(msg);
    }

    /**
     * 判断时间是否未到期
     */
    public boolean isTimeUnDue(final long lengthSecond, final long sendTime, long currentTime) {
        if (lengthSecond * THOUSAND - (currentTime - sendTime) >= MIN_INTERVAL) {
            return true;
        }
        return false;
    }

}
