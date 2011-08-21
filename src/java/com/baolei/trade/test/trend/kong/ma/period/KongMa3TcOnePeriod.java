package com.baolei.trade.test.trend.kong.ma.period;

import com.baolei.ghost.common.CalendarUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.trend.kong.ma.KongMa3TcOne;

/**
 * @author baolei
 * 趋势 + 一次投入 + 时间过滤
 */
public class KongMa3TcOnePeriod extends KongMa3TcOne{
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
