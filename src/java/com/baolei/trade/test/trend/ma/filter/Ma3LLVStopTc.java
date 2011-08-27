package com.baolei.trade.test.trend.ma.filter;

import com.baolei.ghost.dal.dataobject.StockDO;

public class Ma3LLVStopTc extends Ma3LLVTc {

	protected int hhvCount = 20;
	protected float stopLoss = 0.08f;

	@Override
	public boolean needSale(String dateString) {
		if (isStopLoss(dateString)) {
			return true;
		}
		if (super.needSale(dateString)) {
			return true;
		}
		return false;
	}

	protected boolean isStopLoss(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		float buyPoint = stockDO.getClose();
		if (lastBuyStockDO == null) {
			return false;
		}
		if ((toucunHR > 0) && trendout(stockDO)) {
			StockDO startStockDO = findStartStock(dateString);
			float startBuyPoint = startStockDO.getClose();
			float change = 1 - buyPoint / startBuyPoint;
			if (change > stopLoss) {
				return true;
			}
		}
		return false;
	}

}
