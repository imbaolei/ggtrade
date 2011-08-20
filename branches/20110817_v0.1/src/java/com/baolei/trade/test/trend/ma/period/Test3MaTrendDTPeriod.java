package com.baolei.trade.test.trend.ma.period;

import com.baolei.ghost.common.CalendarUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.trend.ma.Test3MaTrendDT;

/**
 * @author baolei
 * 定投 + 趋势 + 时间过滤
 */
public class Test3MaTrendDTPeriod extends Test3MaTrendDT {

	
	@Override
	public boolean needBuy(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		boolean isFirstDay =  CalendarUtil.isFirstDayOfMonth(pdStockList, stockDO,firstDay);
		if(super.needBuy(dateString)&& isFirstDay){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean needSale(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		boolean isFirstDay =  CalendarUtil.isFirstDayOfMonth(pdStockList, stockDO,firstDay);
		if(super.needSale(dateString)&&isFirstDay){
			return true;
		}
		return false;
	}

}
