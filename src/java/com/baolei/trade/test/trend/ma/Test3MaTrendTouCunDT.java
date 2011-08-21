package com.baolei.trade.test.trend.ma;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.NumberUtil;
import com.baolei.ghost.dal.dataobject.StockDO;

/**
 * @author lei.baol 时点交易法 当且仅当 p1<=p2 && p2<=p3 周期的均线时 ，判断趋势走弱
 *         如果走弱即卖出，非走弱即走强，走强时买入或持有
 */

public class Test3MaTrendTouCunDT extends Test3MaTrendDT {

	protected Integer p1;
	protected Integer p2;
	protected Integer p3;

	

	

	@Override
	public boolean needBuy(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		// 如果判断没有头寸 而且 趋势不是走弱，即走强
		// 如果有现金 就买入
		if ((cash > 0) && !trendout(stockDO)) {
			return true;
		}

		return false;
	}

	@Override
	public void buy(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		float fee = 0;
		fee = fee(cash);
		toucunHR = toucunChange(dateString);
		toucunHR = toucunHR + cash - fee;
		cash = 0;

		// 设置report
		float buyPoint = stockDO.getClose();
		float account = toucunHR + cash;
		stockDO.getReport().setAccount(account);
		stockDO.getReport().setNotes(" - 买点 ： " + buyPoint);
		stockDO.getReport().setFee(fee);
		totalFee = totalFee + fee;
		stockDO.getReport().setTotalFee(totalFee);
		stockDO.getReport().setStatus(Constant.REPORT_STATUS_BUY);
		transCount = transCount + 1;
		stockDO.getReport().setTransCount(transCount);

//		jyDingTouMoney = 0;
//		// 如果买入当天定投过 因为定投在买入之前，所以当天定投的钱要算入收益成本
//		if (stockDO.getReport().getDingTou()) {
//			jyDingTouMoney = moneyDingTou;
//		}
		lastBuyStockDO = stockDO;
	}

	/**
	 * @param dateString
	 *            两次交易之间的头寸变化
	 */
	protected float toucunChange(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		float buyPoint = stockDO.getClose();
		if(lastBuyStockDO == null){
			return 0;
		}
		float touCunChange = toucunHR * (buyPoint / lastBuyStockDO.getClose());
		touCunChange = NumberUtil.roundDown(touCunChange, 2);
		return touCunChange;
	}

	@Override
	public boolean needSale(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		if ((toucunHR > 0) && trendout(stockDO)) {
			return true;
		}
		return false;
	}

	@Override
	public void sale(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		int index = jyStockList.indexOf(stockDO);
		StockDO preStockDO = jyStockList.get(index - 1);
		String status = preStockDO.getReport().getStatus();
		float fee = 0;

		if (Constant.REPORT_STATUS_CHICANG.equals(status)
				|| Constant.REPORT_STATUS_BUY.equals(status)) {
			fee = fee(toucunHR);
			float buyPoint = lastBuyStockDO.getClose();
			// 损益 只计算这次卖出的损益，不包含上次买入时的交易费用
			float sunyi = (stockDO.getClose() - buyPoint) / buyPoint * toucunHR
					- fee;
			cash = cash + toucunHR + sunyi;
			toucunHR = 0;
		} else {
			// 应该不会遇到
			// TODO log.error
		}

		float account = cash + toucunHR;
		stockDO.getReport().setAccount(account);
		stockDO.getReport().setNotes(" - 卖点 ： " + stockDO.getClose());
		stockDO.getReport().setFee(fee);
		totalFee = totalFee + fee;
		stockDO.getReport().setTotalFee(totalFee);
		stockDO.getReport().setStatus(Constant.REPORT_STATUS_SALE);
		float shouyi = shouyi(dateString);
		stockDO.getReport().setShouyi(shouyi);
		Float shouyiPersent = shouyiPersent(dateString);
		stockDO.getReport().setShouyiPercent(shouyiPersent);

	}
	
	
	

	@Override
	public void noBuyNoSale(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		if (lastBuyStockDO == null) {
			float account = cash + toucunHR;
			stockDO.getReport().setAccount(account);
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_NOSTART);
			return;
		}
		float buyPoint = lastBuyStockDO.getClose();
		float tmpToucun = toucunHR + (stockDO.getClose() - buyPoint) / buyPoint
				* toucunHR;
		float account = tmpToucun + cash;
		account = NumberUtil.roundDown(account, 2);
		stockDO.getReport().setAccount(account);
		if (toucunHR > 0) {
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_CHICANG);
		} else {
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_KONGCANG);
		}
	}

	

}
