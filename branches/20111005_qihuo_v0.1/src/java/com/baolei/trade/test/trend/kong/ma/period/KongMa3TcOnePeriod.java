package com.baolei.trade.test.trend.kong.ma.period;

import com.baolei.ghost.common.PriceUtil;
import com.baolei.ghost.dal.dataobject.PriceDO;
import com.baolei.trade.test.trend.kong.ma.Ma3KongTc;

/**
 * @author baolei
 * 趋势 + 一次投入 + 时间过滤
 */
public class KongMa3TcOnePeriod extends Ma3KongTc{
	@Override
	public boolean needBuy(String dateString) {
		PriceDO stockDO = pdStockMap.get(dateString);
		boolean isFirstDay =  PriceUtil.isFirstDayOfMonth(pdStockList, stockDO,firstDay);
		if(super.needBuy(dateString)&& isFirstDay){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean needSale(String dateString) {
		PriceDO stockDO = pdStockMap.get(dateString);
		boolean isFirstDay =  PriceUtil.isFirstDayOfMonth(pdStockList, stockDO,firstDay);
		if(super.needSale(dateString)&&isFirstDay){
			return true;
		}
		return false;
	}

}
