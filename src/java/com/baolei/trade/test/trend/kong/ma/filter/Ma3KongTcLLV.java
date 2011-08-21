package com.baolei.trade.test.trend.kong.ma.filter;

import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.trend.kong.ma.Ma3KongTc;

public class Ma3KongTcLLV extends Ma3KongTc{
	
	protected int llvCount = 50;
	protected int hhvCount = 20;
	
	protected boolean isLLV(String dateString){
		StockDO stockDO = pdStockMap.get(dateString);
		return StockUtil.isLLV(pdStockList, stockDO, llvCount);
	}

	@Override
	public boolean needBuy(String dateString) {
		if (super.needBuy(dateString) && isLLV(dateString)) {
			return true;
		}
		return false;
	}
	
	protected boolean isHHV(String dateString){
		StockDO stockDO = pdStockMap.get(dateString);
		return StockUtil.isHHV(pdStockList, stockDO, hhvCount);
	}
	
	@Override
	public boolean needSale(String dateString) {
		if (super.needSale(dateString) && isHHV(dateString)) {
			return true;
		}
		return false;
	}
}
