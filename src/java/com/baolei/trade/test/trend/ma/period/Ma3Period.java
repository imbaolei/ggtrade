package com.baolei.trade.test.trend.ma.period;

import com.baolei.ghost.common.PriceUtil;
import com.baolei.ghost.dal.dataobject.PriceDO;
import com.baolei.trade.test.trend.ma.Ma3;

/**
 * @author baolei
 * 定投 + 趋势 + 时间过滤
 */
public class Ma3Period extends Ma3 {

	
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
