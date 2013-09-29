package com.baolei.trade.test.trend.kong.ma.filter;

import com.baolei.ghost.common.PriceUtil;
import com.baolei.ghost.dal.dataobject.PriceDO;
import com.baolei.trade.test.trend.kong.ma.Ma3KongTc;

public class Ma3KongTcLLV extends Ma3KongTc{
	
	protected int llvCount = 20;
	protected int hhvCount = 20;
	
	protected boolean isLLV(String dateString){
		PriceDO stockDO = pdStockMap.get(dateString);
		return PriceUtil.isLLV(pdStockList, stockDO, llvCount);
	}

	@Override
	public boolean needBuy(String dateString) {
		if (super.needBuy(dateString) && isLLV(dateString)) {
			return true;
		}
		return false;
	}
	
	protected boolean isHHV(String dateString){
		PriceDO stockDO = pdStockMap.get(dateString);
		return PriceUtil.isHHV(pdStockList, stockDO, hhvCount);
	}
	
	@Override
	public boolean needSale(String dateString) {
		if (super.needSale(dateString) && isHHV(dateString)) {
			return true;
		}
		return false;
	}
}
