package com.baolei.trade.test.trend.kong.ma;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.NumberUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.trend.ma.Test3MaTrendTouCunDT;

/**
 * @author baolei
 * 做空时的 趋势 + 定投
 */
public class Test3MaTrendKongTouCunDT extends Test3MaTrendTouCunDT {

	/**
	 * 当且仅当 p1>=p2 && p2>=p3 周期的均线时 ，判断趋势走弱
	 * 
	 * @param stockDO
	 * @param p1
	 * @param p2
	 * @param p3
	 * @return
	 */
	protected boolean trendin(StockDO stockDO) {
		float ma1 = stockDO.getMa(p1.toString());
		float ma2 = stockDO.getMa(p2.toString());
		float ma3 = stockDO.getMa(p3.toString());
		// 三个周期的均线都有值时
		if ((ma1 == 0) || (ma2 == 0) || (ma3 == 0)) {
			return true;
		}
		if (ma1 > 0 && ma2 > 0 && ma3 > 0) {
			if (ma1 >= ma2 && ma2 >= ma3) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean needBuy(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		// 如果判断没有头寸 而且 趋势不是走弱，即走强
		// 如果有现金 就买入
		if ((cash > 0) && !trendin(stockDO)) {
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

	@Override
	public boolean needSale(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		if ((toucunHR > 0) && trendin(stockDO)) {
			return true;
		}
		return false;
	}

	
}
