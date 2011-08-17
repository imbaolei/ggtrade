package com.baolei.trade.test.trend.ma.period;

import com.baolei.ghost.common.CalendarUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.trend.ma.Test3MaTrend;

/**
 * 每个月 交易一次 买入 或 卖出
 * @author Administrator
 *
 */
public class Test3MaTrendPeriod extends Test3MaTrend{
	
	
	public Test3MaTrendPeriod() {
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
