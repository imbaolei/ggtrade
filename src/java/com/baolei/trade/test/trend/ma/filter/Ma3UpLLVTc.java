package com.baolei.trade.test.trend.ma.filter;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.NumberUtil;
import com.baolei.ghost.common.PriceUtil;
import com.baolei.ghost.dal.dataobject.PriceDO;
import com.baolei.trade.test.Test;
import com.baolei.trade.test.trend.ma.Ma3Tc;

/**
 * @author lei.baol 当且仅当 p1>=p2 && p2>=p3 && 突破20日最高点 ，判断走强
 *         如果 p1<=p2 && p2<=p3 周期的均线时，并且跌破20日最低点是，判断走弱
 */

public class Ma3UpLLVTc extends Ma3LLVTc {
	
//	public boolean needBuy(String dateString) {
//		StockDO stockDO = pdStockMap.get(dateString);
//		// 如果判断没有头寸 而且 趋势不是走弱，即走强
//		// 如果有现金 就买入
//		if ((cash > 0) && isMaReadyJy(stockDO) && trendin(stockDO) && isHHV(dateString)) {
//			planBuyPoint = stockDO.getClose();
//			planBuyToucun = cash;
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	protected boolean canBuyDingTou(String dateString) {
//		return false;
//	}
	
	@Override
	public boolean needBuy(String dateString) {
		if (canBuyFirst(dateString)) {
			return true;
		}
		if (canBuyDingTou(dateString)) {
			return true;
		}
		return false;
	}

	protected boolean canBuyFirst(String dateString) {
		PriceDO lastJyStock = findLastJyStock(dateString);
		if (lastJyStock == null) {
			return false;
		}
		String lastJyStatus = lastJyStock.getReport().getStatus();
		if (!Constant.REPORT_STATUS_BUY.equals(lastJyStatus)) {
			if (needBuyThisTest(dateString) && isHHV(dateString)) {
				return true;
			}
		}

		return false;
	}

	protected boolean canBuyDingTou(String dateString) {
		PriceDO lastJyStock = findLastJyStock(dateString);
		if (lastJyStock == null) {
			return false;
		}
		String lastJyStatus = lastJyStock.getReport().getStatus();
		if (Constant.REPORT_STATUS_BUY.equals(lastJyStatus)) {
			if (needBuyThisTest(dateString)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean needBuyThisTest(String dateString) {
		PriceDO stockDO = pdStockMap.get(dateString);
		// 如果判断没有头寸 而且 趋势不是走弱，即走强
		// 如果有现金 就买入
		if ((cash > 0) && isMaReadyJy(stockDO) && trendin(stockDO)) {
			planBuyPoint = stockDO.getClose();
			planBuyToucun = cash;
			return true;
		}

		return false;
	}
	
	
	protected boolean trendin(PriceDO stockDO) {
		float ma1 = stockDO.getMa(p1.toString());
		float ma2 = stockDO.getMa(p2.toString());
		float ma3 = stockDO.getMa(p3.toString());
		
		if (ma1 > 0 && ma2 > 0 && ma3 > 0) {
			if (ma1 >= ma2 && ma2 >= ma3) {
				return true;
			}
		}
		return false;
	}

	

	
}
