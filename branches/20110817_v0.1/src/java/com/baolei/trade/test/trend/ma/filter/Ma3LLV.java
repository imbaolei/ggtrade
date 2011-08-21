package com.baolei.trade.test.trend.ma.filter;

import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.trend.ma.Ma3;

public class Ma3LLV extends Ma3{
	
	protected int llvCount = 20;
	protected int hhvCount = 50;
	
	protected boolean isLLV(String dateString){
		StockDO stockDO = pdStockMap.get(dateString);
		return StockUtil.isLLV(pdStockList, stockDO, llvCount);
	}
	
	protected boolean isHHV(String dateString){
		StockDO stockDO = pdStockMap.get(dateString);
		return StockUtil.isHHV(pdStockList, stockDO, hhvCount);
	}
	
	public boolean needBuy(String dateString) {
		if(super.needBuy(dateString)&& isHHV(dateString)){
			return true;
		}
		return false;
	}

	@Override
	public boolean needSale(String dateString) {
		if (super.needSale(dateString) && isLLV(dateString)) {
			return true;
		}
		return false;
	}
}
