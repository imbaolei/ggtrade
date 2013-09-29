package com.baolei.trade.test.trend.ma;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.NumberUtil;
import com.baolei.ghost.common.PriceUtil;
import com.baolei.ghost.dal.dataobject.PriceDO;
import com.baolei.trade.test.Test;

/**
 * @author lei.baol 时点交易法 当且仅当 p1<=p2 && p2<=p3 周期的均线时 ，判断趋势走弱
 *         如果走弱即卖出，非走弱即走强，走强时买入或持有
 */

public class Ma3Tc extends Test {
	
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
	protected boolean trendout(PriceDO stockDO) {
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

	protected boolean isMaReadyJy(PriceDO stockDO){
		float ma1 = stockDO.getMa(p1.toString());
		float ma2 = stockDO.getMa(p2.toString());
		float ma3 = stockDO.getMa(p3.toString());
		if ((ma1 == 0) || (ma2 == 0) || (ma3 == 0)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean needBuy(String dateString) {
		PriceDO stockDO = pdStockMap.get(dateString);
		// 如果判断没有头寸 而且 趋势不是走弱，即走强
		// 如果有现金 就买入
		if ((cash > 0) && isMaReadyJy(stockDO) && !trendout(stockDO)) {
			planBuyPoint = stockDO.getClose();
			planBuyToucun = cash;
			return true;
		}

		return false;
	}
	
	

	@Override
	public void buy(String dateString) {
		PriceDO stockDO = jyStockMap.get(dateString);
		float fee = 0;
		fee = fee(planBuyToucun);
		stockDO.getReport().setFee(fee);
		toucunHR = toucunChange(dateString);
		toucunHR = toucunHR + planBuyToucun - fee;
		cash = cash - planBuyToucun;
		lastBuyStockDO = stockDO;
		setBuyReport(dateString);
	}
	
	protected void setBuyReport(String dateString){
		PriceDO stockDO = jyStockMap.get(dateString);
		// 设置report
		float buyPoint = stockDO.getClose();
		float account = toucunHR + cash;
		stockDO.getReport().setAccount(account);
		totalFee = totalFee + stockDO.getReport().getFee();
		stockDO.getReport().setTotalFee(totalFee);
		stockDO.getReport().setStatus(Constant.REPORT_STATUS_BUY);
		transCount = transCount + 1;
		stockDO.getReport().setTransCount(transCount);
		stockDO.getReport().setPrice(buyPoint);
		stockDO.getReport().setTime(stockDO.getTime());
	}

	/**
	 * @param dateString
	 * 两次交易之间的头寸变化
	 */
	protected float toucunChange(String dateString) {
		PriceDO stockDO = jyStockMap.get(dateString);
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
		PriceDO stockDO = pdStockMap.get(dateString);
		if ((toucunHR > 0) && trendout(stockDO)) {
			return true;
		}
		return false;
	}

	@Override
	public void sale(String dateString) {
		PriceDO stockDO = jyStockMap.get(dateString);
		int index = jyStockList.indexOf(stockDO);
		PriceDO preStockDO = jyStockList.get(index - 1);
		String preStatus = preStockDO.getReport().getStatus();
		float fee = 0;

		if (Constant.REPORT_STATUS_CHICANG.equals(preStatus)
				|| Constant.REPORT_STATUS_BUY.equals(preStatus)) {
			float toucunChange = toucunChange(dateString);
			fee = fee(toucunChange);
			cash = cash + toucunChange - fee;
			toucunHR = 0;
			lastBuyStockDO = null;
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
		stockDO.getReport().setPercent(shouyiPersent);
		stockDO.getReport().setPrice(stockDO.getClose());
		stockDO.getReport().setTime(stockDO.getTime());

	}
	
	/**
	 * 计算买入和卖出 两次操作中的 收益情况 包括上次买入时的交易费
	 * 
	 * @param dateString
	 * @return
	 */
	protected float shouyi(String dateString) {
		// 用卖出这天的账户金额 减去 上次买入前的 账户金额
		PriceDO stockDO = jyStockMap.get(dateString);
		// 根据jyStockList 找到这次定投最开始的 那天的 stockDO
		PriceDO startStockDO = findStartStock(dateString);
		int index = jyStockList.indexOf(startStockDO);
		PriceDO preStockDO = jyStockList.get(index - 1);
		float shouyi = stockDO.getReport().getAccount()
				- preStockDO.getReport().getAccount() - jyDingTouMoney(dateString);
		shouyi = NumberUtil.roundDown(shouyi, 2);
		return shouyi;
	}
	
	protected float shouyiPersent(String dateString) {
		// 用卖出这天的账户金额 减去 上次买入前的 账户金额
		PriceDO stockDO = jyStockMap.get(dateString);
		PriceDO startStockDO = findStartStock(dateString);
		int index = jyStockList.indexOf(startStockDO);
		PriceDO preStockDO = jyStockList.get(index - 1);
		float preAccount = preStockDO.getReport().getAccount();
		float shouyi = stockDO.getReport().getAccount() - preAccount
				- jyDingTouMoney(dateString);
		float shouyiPersent = shouyi / preAccount;
		shouyiPersent = NumberUtil.roundDown(shouyiPersent * 100, 2);
		return shouyiPersent;
	}
	
	/**
	 * 找到这次交易最开始买入的那天
	 * 
	 * @param dateString
	 * @return
	 */
	protected PriceDO findStartStock(String dateString) {
		PriceDO stockDO = jyStockMap.get(dateString);
		int index = jyStockList.indexOf(stockDO);
		PriceDO startStock = null;
		for (int i = index - 1; i > 0; i--) {
			PriceDO tmpStockDO = jyStockList.get(i);
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
	
	
	/**
	 * 找到上次操作的交易日 比如买入、卖出、没开始状态的 交易日
	 * 
	 * @param dateString
	 * @return
	 */
	protected PriceDO findLastJyStock(String dateString) {
		PriceDO stockDO = jyStockMap.get(dateString);
		int index = jyStockList.indexOf(stockDO);
		PriceDO lastJyStock = null;
		for (int i = index - 1; i > 0; i--) {
			PriceDO tmpStockDO = jyStockList.get(i);
			String status = tmpStockDO.getReport().getStatus();
			if (Constant.REPORT_STATUS_SALE.equals(status)
					|| Constant.REPORT_STATUS_NOSTART.equals(status)||Constant.REPORT_STATUS_BUY.equals(status)) {
				lastJyStock = tmpStockDO;
				break;
			}
		}
		return lastJyStock;
	}
	
	protected float jyDingTouMoney(String dateString){
		PriceDO stockDO = jyStockMap.get(dateString);
		int index = jyStockList.indexOf(stockDO);
		float jyDingTouMoney = 0;
		for (int i = index - 1; i > 0; i--) {
			PriceDO tmpStockDO = jyStockList.get(i);
			String status = tmpStockDO.getReport().getStatus();
			
			if (Constant.REPORT_STATUS_SALE.equals(status)
					|| Constant.REPORT_STATUS_NOSTART.equals(status)) {
				break;
			}
			if (tmpStockDO.getReport().getDingTouFlag()) {
				jyDingTouMoney = jyDingTouMoney + moneyDingTou;
			}
		}
		return jyDingTouMoney;
	}
	

	@Override
	public void noBuyNoSale(String dateString) {
		PriceDO stockDO = jyStockMap.get(dateString);
		if (lastBuyStockDO == null) {
			float account = cash + toucunHR;
			stockDO.getReport().setAccount(account);
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_NOSTART);
			return;
		}
		float toucunChange = toucunChange(dateString);
		float account = toucunChange + cash;
		account = NumberUtil.roundDown(account, 2);
		stockDO.getReport().setAccount(account);
		if (toucunHR > 0) {
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_CHICANG);
		} else {
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_KONGCANG);
		}
		stockDO.getReport().setTime(stockDO.getTime());
	}

	@Override
	public boolean needDingTou(String dateString) {
		PriceDO stockDO = pdStockMap.get(dateString);
		// 每个月第firstDay 天定投
		if (PriceUtil.isFirstDayOfMonth(pdStockList, stockDO, firstDay)) {
			return true;
		}
		return false;
	}

	@Override
	public void dingTou(String dateString) {
		PriceDO stockDO = jyStockMap.get(dateString);
		cash = cash + moneyDingTou;
		// 定投的时候 如果总定金额等于0，相当于还没开始计算总金额
		if (totalMoney == 0) {
			// 如果还没开始计算
			totalMoney = cash;
		} else {
			totalMoney = totalMoney + moneyDingTou;
		}
		stockDO.getReport().setTotalMoney(totalMoney);
		stockDO.getReport().setDingTouFlag(true);// 设置今天是定投日
		

	}
	

}
