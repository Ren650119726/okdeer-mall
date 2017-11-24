package com.okdeer.mall.activity.coupons.service.receive;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.mall.activity.coupons.enums.ActivityCollectCouponsType;
import com.okdeer.mall.activity.coupons.service.receive.impl.AdvertCouponsReceive;
import com.okdeer.mall.activity.coupons.service.receive.impl.ConsumeReturnCouponsReceive;
import com.okdeer.mall.activity.coupons.service.receive.impl.GetCouponsReceive;
import com.okdeer.mall.activity.coupons.service.receive.impl.InviteRegistCouponsReceive;
import com.okdeer.mall.activity.coupons.service.receive.impl.LzgCouponsReceive;
import com.okdeer.mall.activity.coupons.service.receive.impl.OpenDoorCouponsReceive;
import com.okdeer.mall.activity.coupons.service.receive.impl.RedPacketCouponsReceive;
import com.okdeer.mall.activity.coupons.service.receive.impl.RegisterCouponsReceive;

/**
 * ClassName: ConponsReceiveMediator 
 * @Description: 具体操作领券类的生产类
 * @author tuzhd
 * @date 2017年11月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		2.7				2017-11-23			tuzhd			
 */
@Service
public class ConponsReceiveFactory {
	@Resource
	private AdvertCouponsReceive advertCouponsReceive;
	@Resource
	private LzgCouponsReceive lzgCouponsReceive;
	@Resource
	private ConsumeReturnCouponsReceive consumeReturnCouponsReceive;
	@Resource
	private InviteRegistCouponsReceive inviteRegistCouponsReceive;
	@Resource
	private GetCouponsReceive getCouponsReceive;
	@Resource
	private OpenDoorCouponsReceive openDoorCouponsReceive;
	@Resource
	private RedPacketCouponsReceive redPacketCouponsReceive;
	@Resource
	private RegisterCouponsReceive registerCouponsReceive;
	
	/**
	 * @Description: 根据广告类型返回领取代金券操作类
	 * @param type
	 * @return AbstractCouponsReceive  
	 * @author tuzhd
	 * @date 2017年11月23日
	 */
	public AbstractCouponsReceive produce(Integer type){
		//返回的对象
		AbstractCouponsReceive result = null;
		ActivityCollectCouponsType collType = ActivityCollectCouponsType.enumValuOf(type);
		switch(collType){
			case get:
				result = getCouponsReceive;
				break;
			case OPEN_DOOR: 
				result = openDoorCouponsReceive;
				break;
			case red_packet: 
				result = redPacketCouponsReceive;
				break;
			case register: 
				result = registerCouponsReceive;
				break;
			case invite_regist: 
				result = inviteRegistCouponsReceive;
				break;
			case consume_return: 
				result = consumeReturnCouponsReceive;
				break;
			case lzg: 
				result = lzgCouponsReceive;
				break;
			case advert: 
				result = advertCouponsReceive;
				break;
			default:
				result = advertCouponsReceive;
				break;
		}
		return result;
	}
}
