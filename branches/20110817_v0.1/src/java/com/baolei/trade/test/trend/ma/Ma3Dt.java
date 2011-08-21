package com.baolei.trade.test.trend.ma;

import com.baolei.ghost.common.CalendarUtil;
import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.NumberUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.Test;

/**
 * 
 * 定投 + 趋势
 * 
 * @author baolei
 * 
 */
public class Ma3Dt extends Test {

	protected Integer p1;
	protected Integer p2;
	protected Integer p3;

	public void initMaParam(Integer p1, Integer p2, Integer p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;

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
			// 如果还没开始计算
			totalMoney = cash;
		} else {
			totalMoney = totalMoney + moneyDingTou;
		}
		stockDO.getReport().setTotalMoney(totalMoney);
		stockDO.getReport().setDingTou(true);// 设置今天是定投日
		

	}
	
	protected boolean isMaReadyJy(StockDO stockDO){
		float ma1 = stockDO.getMa(p1.toString());
		float ma2 = stockDO.getMa(p2.toString());
		float ma3 = stockDO.getMa(p3.toString());
		if ((ma1 == 0) || (ma2 == 0) || (ma3 == 0)) {
			return false;
		}
		return true;
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
	protected boolean trendout(StockDO stockDO) {
		float ma1 = stockDO.getMa(p1.toString());
		float ma2 = stockDO.getMa(p2.toString());
		float ma3 = stockDO.getMa(p3.toString());
		
		if (ma1 > 0 && ma2 > 0 && ma3 > 0) {
			if (ma1 <= ma2 && ma2 <= ma3) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 购买份额
	 * 
	 * @param money
	 * @param buyPoint
	 * @return
	 */
	protected float buyShare(float money, float buyPoint) {
		float share = money / buyPoint;
		return NumberUtil.roundDown(share, 2);
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
	public boolean needBuy(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		// 如果判断没有高风险头寸 而且 趋势不是走弱，即走强
		// 计算 可以买入的份额，如果可以买入的份额大于0
		float buyPoint = stockDO.getClose();
		float fee = fee(cash);
		float availCash = cash - fee;
		float share = buyShare(availCash, buyPoint);
		if ((share > 0) && isMaReadyJy(stockDO) &&!trendout(stockDO)) {
			return true;
		}
		return false;
	}

	@Override
	public void buy(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		// 如果是空仓后 第一次买入 则 更新lastBuyStockDO 否则是加仓
		float fee = 0;
		float buyPoint = stockDO.getClose();
		fee = fee(cash);
		float availCash = cash - fee;
		float share = buyShare(availCash, buyPoint);
		cash = availCash - share * buyPoint;
		cash = NumberUtil.roundDown(cash, 2);
		shareHR = shareHR + share;
		lastBuyStockDO = stockDO;

		// 设置report
		float account = shareHR * buyPoint + cash;
		account = NumberUtil.roundDown(account, 2);
		stockDO.getReport().setAccount(account);
		shareHR = NumberUtil.roundDown(shareHR, 2);
		stockDO.getReport().setShareHR(shareHR);
		stockDO.getReport().setNotes(" - 买点 ： " + buyPoint);
		stockDO.getReport().setFee(fee);
		totalFee = totalFee + fee;
		stockDO.getReport().setTotalFee(totalFee);
		stockDO.getReport().setStatus(Constant.REPORT_STATUS_BUY);
		transCount = transCount + 1;
		stockDO.getReport().setTransCount(transCount);


	}

	@Override
	public boolean needSale(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		if ((shareHR > 0) && trendout(stockDO)) {
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
		float reportShare = shareHR;
		if (Constant.REPORT_STATUS_CHICANG.equals(status)
				|| Constant.REPORT_STATUS_BUY.equals(status)) {
			fee = fee(shareHR * stockDO.getClose());
			cash = cash + shareHR * stockDO.getClose() - fee;
			shareHR = 0;
		} else {
			// 应该不会遇到
			// TODO log.error
		}

		float account = cash;
		stockDO.getReport().setAccount(account);
		stockDO.getReport().setShareHR(reportShare);
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
			float account = cash;
			stockDO.getReport().setAccount(account);
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_NOSTART);
			return;
		}
		float tmpToucun = shareHR * stockDO.getClose();
		float account = tmpToucun + cash;
		account = NumberUtil.roundDown(account, 2);
		stockDO.getReport().setAccount(account);
		stockDO.getReport().setShareHR(shareHR);
		if (shareHR > 0) {
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_CHICANG);
		} else {
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_KONGCANG);
		}

	}

}
