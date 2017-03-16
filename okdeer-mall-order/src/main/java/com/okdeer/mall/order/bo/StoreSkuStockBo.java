package com.okdeer.mall.order.bo;

import com.okdeer.archive.stock.dto.StockUpdateDto;
import com.okdeer.jxc.stock.vo.StockUpdateVo;

/**
 * ClassName: StoreSkuStockBo 
 * @Description: 店铺商品库存业务对象
 * @author maojj
 * @date 2017年3月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.2 		2017年3月14日				maojj
 */
public class StoreSkuStockBo {

	/**
	 * 商城更新库存的Dto
	 */
	private StockUpdateDto mallStockUpdate;

	/**
	 * 进销存库存更新的Dto
	 */
	private StockUpdateVo jxcStockUpdate;

	public StockUpdateDto getMallStockUpdate() {
		return mallStockUpdate;
	}

	public void setMallStockUpdate(StockUpdateDto mallStockUpdate) {
		this.mallStockUpdate = mallStockUpdate;
	}

	public StockUpdateVo getJxcStockUpdate() {
		return jxcStockUpdate;
	}

	public void setJxcStockUpdate(StockUpdateVo jxcStockUpdate) {
		this.jxcStockUpdate = jxcStockUpdate;
	}

}
