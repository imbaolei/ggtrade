package com.baolei.trade.test.trend.kong.ma;

import com.baolei.ghost.common.NumberUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.trend.ma.Ma3Tc;

/**
 * @author baolei
 * 做空时的 趋势 + 定投
 */
public class Ma3KongTc extends Ma3Tc {

	@Override
	public boolean needBuy(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		// 如果判断没有头寸 而且 趋势不是走弱，即走强
		// 如果有现金 就买入
		if ((cash > 0) && trendout(stockDO)) {
			return true;
		}

		return false;
	}
	
	@Override
	public boolean needSale(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		if ((toucunHR > 0) && !trendout(stockDO)) {
			return true;
		}
		return false;
	}
	

	/**
	 * @param dateString
	 * 两次交易之间的头寸变化
	 */
	protected float toucunChange(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		float buyPoint = stockDO.getClose();
		if(lastBuyStockDO == null){
			return 0;
		}
		//计算toucunchange时 计算规则和做多时不一样
		float touCunChange = toucunHR * (lastBuyStockDO.getClose() / buyPoint);
		touCunChange = NumberUtil.roundDown(touCunChange, 2);
		return touCunChange;
	}

	

	
}
