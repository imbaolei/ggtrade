package com.baolei.trade.test.trend.kong.ma.period;

import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.trend.kong.ma.Ma3KongTc;

/**
 * @author baolei
 * 趋势 + 一次投入 + 时间过滤
 */
public class KongMa3TcOnePeriod extends Ma3KongTc{
	@Override
	public boolean needBuy(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		boolean isFirstDay =  StockUtil.isFirstDayOfMonth(pdStockList, stockDO,firstDay);
		if(super.needBuy(dateString)&& isFirstDay){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean needSale(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		boolean isFirstDay =  StockUtil.isFirstDayOfMonth(pdStockList, stockDO,firstDay);
		if(super.needSale(dateString)&&isFirstDay){
			return true;
		}
		return false;
	}

}
