package com.baolei.trade.test.trend.ma.period;

import com.baolei.ghost.common.CalendarUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.trend.ma.Test3MaTrendDingTou;

public class Test3MaTrendPeriodDingTou extends Test3MaTrendDingTou{

	public Test3MaTrendPeriodDingTou() {
	}
	
	
	@Override
	public boolean needBuy(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		boolean isFirstDay =  CalendarUtil.isFirstDayOfMonth(pdStockList, stockDO);
		if(super.needBuy(dateString)&& isFirstDay){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean needSale(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		boolean isFirstDay =  CalendarUtil.isFirstDayOfMonth(pdStockList, stockDO);
		if(super.needSale(dateString)&&isFirstDay){
			return true;
		}
		return false;
	}
}