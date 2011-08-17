package com.baolei.ghost.test.ma;

import com.baolei.ghost.common.CalendarUtil;
import com.baolei.ghost.dal.dataobject.StockDO;

public class Test3MaTrendPeriod extends Test3MaTrend2Stock{
	
	
	public Test3MaTrendPeriod(float toucunLR, Integer p1, Integer p2, Integer p3) {
		super(toucunLR, p1, p2, p3);
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
