package com.baolei.trade.test.trend.ma;

import com.baolei.ghost.common.CalendarUtil;
import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.NumberUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.Test;

/**
 * @author lei.baol 时点交易法 当且仅当 p1<=p2 && p2<=p3 周期的均线时 ，判断趋势走弱
 *         如果走弱即卖出，非走弱即走强，走强时买入或持有
 */

public class Test3MaTrendTouCunDT extends Test {

	protected Integer p1;
	protected Integer p2;
	protected Integer p3;

	public void initMaParam(Integer p1, Integer p2, Integer p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}

	/**
	 * 当且仅当 p1<=p2 && p2<=p3 周期的均线时 ，判断趋势走弱
	 * 
	 * @param stockDO
	 * @param p1
	 * @param p2
	 * @param p3
	 * @return
	 */
	private boolean trendout(StockDO stockDO) {
		float ma1 = stockDO.getMa(p1.toString());
		float ma2 = stockDO.getMa(p2.toString());
		float ma3 = stockDO.getMa(p3.toString());
		// 三个周期的均线都有值时
		if ((ma1 == 0) || (ma2 == 0) || (ma3 == 0)) {
			return true;
		}
		if (ma1 > 0 && ma2 > 0 && ma3 > 0) {
			if (ma1 <= ma2 && ma2 <= ma3) {
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
	
	/**
	 * 找到这次交易最开始买入的那天
	 * 
	 * @param dateString
	 * @return
	 */
	protected StockDO findStartStock(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		int index = jyStockList.indexOf(stockDO);
		StockDO startStock = null;
		for (int i = index - 1; i > 0; i--) {
			StockDO tmpStockDO = jyStockList.get(i);
			String status = tmpStockDO.getReport().getStatus();
			if (Constant.REPORT_STATUS_SALE.equals(status)
					|| Constant.REPORT_STATUS_NOSTART.equals(status)) {
				break;
			}
			if (Constant.REPORT_STATUS_BUY.equals(status)) {
				startStock = tmpStockDO;
			}

		}
		return startStock;
	}
	
	protected float jyDingTouMoney(String dateString){
		StockDO stockDO = jyStockMap.get(dateString);
		int index = jyStockList.indexOf(stockDO);
		float jyDingTouMoney = 0;
		for (int i = index - 1; i > 0; i--) {
			StockDO tmpStockDO = jyStockList.get(i);
			String status = tmpStockDO.getReport().getStatus();
			
			if (Constant.REPORT_STATUS_SALE.equals(status)
					|| Constant.REPORT_STATUS_NOSTART.equals(status)) {
				break;
			}
			if (tmpStockDO.getReport().getDingTou()) {
				jyDingTouMoney = jyDingTouMoney + moneyDingTou;
			}

		}
		return jyDingTouMoney;
	}

	/**
	 * 计算买入和卖出 两次操作中的 收益情况 包括上次买入时的交易费
	 * 
	 * @param dateString
	 * @return
	 */
	protected float shouyi(String dateString) {
		// 用卖出这天的账户金额 减去 上次买入前的 账户金额
		StockDO stockDO = jyStockMap.get(dateString);
		// 根据jyStockList 找到这次定投最开始的 那天的 stockDO
		StockDO startStockDO = findStartStock(dateString);
		int index = jyStockList.indexOf(startStockDO);
		StockDO preStockDO = jyStockList.get(index - 1);
		float shouyi = stockDO.getReport().getAccount()
				- preStockDO.getReport().getAccount() - jyDingTouMoney(dateString);
		shouyi = NumberUtil.roundDown(shouyi, 2);
		return shouyi;
	}
	
	protected float shouyiPersent(String dateString) {
		// 用卖出这天的账户金额 减去 上次买入前的 账户金额
		StockDO stockDO = jyStockMap.get(dateString);
		StockDO startStockDO = findStartStock(dateString);
		int index = jyStockList.indexOf(startStockDO);
		StockDO preStockDO = jyStockList.get(index - 1);
		float preAccount = preStockDO.getReport().getAccount();
		float shouyi = stockDO.getReport().getAccount() - preAccount
				- jyDingTouMoney(dateString);
		float shouyiPersent = shouyi / preAccount;
		shouyiPersent = NumberUtil.roundDown(shouyiPersent * 100, 2);
		return shouyiPersent;
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

	@Override
	public boolean needDingTou(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		// 每个月第firstDay 天定投
		if (CalendarUtil.isFirstDayOfMonth(pdStockList, stockDO, firstDay)) {
			return true;
		}
		return false;
	}

	@Override
	public void dingTou(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		cash = cash + moneyDingTou;
		// 定投的时候 如果总定金额等于0，相当于还没开始计算总金额
		if (totalMoney == 0) {
			totalMoney = cash;
		} else {
			totalMoney = totalMoney + moneyDingTou;
		}
		stockDO.getReport().setTotalMoney(totalMoney);
		stockDO.getReport().setDingTou(true);
//		jyDingTouMoney = jyDingTouMoney + moneyDingTou;
	}

}
