package com.okdeer.mall.activity.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.mall.activity.coupons.enums.CouponsType;
import com.okdeer.mall.activity.service.CouponsFilterStrategy;

@Service
public class CouponsFilterStrategyFactory {

	@Resource
	private BldCouponsFilterStrategy bldCouponsFilterStrategy;
	
	@Resource
	private BldfwdCouponsFilterStrategy bldfwdCouponsFilterStrategy;
	
	@Resource
	private FwdCouponsFilterStrategy fwdCouponsFilterStrategy;

	@Resource
	private HfczCouponsFilterStrategy hfczCouponsFilterStrategy;
	
	@Resource
	private BldyfCouponsFilterStrategy bldyfCouponsFilterStrategy;
	
	@Resource
	private BldtyCouponsFilterStrategy bldtyCouponsFilterStrategy;
	
	@Resource
	private TyzkqCouponsFilterStrategy tyzkqCouponsFilterStrategy;
	
	public CouponsFilterStrategy get(Integer couponsType){
		CouponsFilterStrategy filterStrategy = null;
		CouponsType couponsTypeEnum = CouponsType.enumValuOf(couponsType);
		switch (couponsTypeEnum) {
			case bldfwd:
				filterStrategy = bldfwdCouponsFilterStrategy;
				break;
			case bld:
				filterStrategy = bldCouponsFilterStrategy;
				break;
			case fwd:
				filterStrategy = fwdCouponsFilterStrategy;
				break;
			case hfcz:
				filterStrategy = hfczCouponsFilterStrategy;
				break;
			case bldyf:
				filterStrategy = bldyfCouponsFilterStrategy;
				break;
			case bldty:
				filterStrategy = bldtyCouponsFilterStrategy;
				break;
			case tyzkq:
				filterStrategy = tyzkqCouponsFilterStrategy;
				break;
			default:
				break;
		}
		return filterStrategy;
	}
}

