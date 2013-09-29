package com.baolei.ghost.plan.simple;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.baolei.ghost.AccountDO;
import com.baolei.ghost.DataPool;
import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.PriceUtil;
import com.baolei.ghost.dal.dataobject.PriceDO;
import com.baolei.ghost.plan.Plan;

/**
 * 简单月线bbi交易，月线向上就进，月线向下就出
 * @author B
 *
 */
public class SimpleMonthBbiPlan extends Plan{
	public SimpleMonthBbiPlan(DataPool dataPool) {
		this.dataPool = dataPool;
		this.setAction(Constant.PLAN_ACTION_BUY);
		this.setStopLoss(0f);
		this.setJustWin(0f);
		this.setTrades(0);
		this.setPlanTrades(1);
		this.setAtr(0f);
		this.setBuyPoint(0f);
		this.setJcPoint(0f);
		this.setTradeNum(0f);
		this.setPercent(1f);
		this.setTotalNum(0f);
		this.setBalance(0f);
	}

	@Override
	public void execute(PriceDO stockDO, AccountDO accountDO)
			throws ParseException {
		if (isBuy(stockDO, accountDO)) {
			// 达到买点后,先制定操作计划,再进行买卖
			setBuyPlan(stockDO, accountDO);
			executeBuy(stockDO, accountDO);
//			setJiacangPlan(stockDO, accountDO);
		} else if (isStopLoss(stockDO, accountDO)) {
			executeStopLost(stockDO, accountDO);
//		} else if (isJustWin(stockDO, accountDO)) {
//			executeJustWin(stockDO, accountDO);
//		} else if (isJiaCang(stockDO, accountDO)) {
			// 达到加仓点后,是先执行上次加仓计划,再计算下次计划
//			executeJiaCang(stockDO, accountDO);
//			setJiacangPlan(stockDO, accountDO);
		}
	}

	public boolean isBuy(PriceDO stockDO, AccountDO accountDO)
			throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(PriceUtil.dateFormatString);
		if (Constant.PLAN_ACTION_BUY.equals(this.getAction())) {
			Date preMonthDate = PriceUtil.pre(dataPool.getMonthStockMap(), stockDO.getTime());
			PriceDO preMonthStock = dataPool.getMonthStockMap().get(dateFormat.format(preMonthDate));
			if((preMonthStock.getBbi() != 0) && (preMonthStock.getBbi() < stockDO.getBbi())){
				return true;
			}
		}
		return false;
	}

	public void setBuyPlan(PriceDO stockDO, AccountDO accountDO)
			throws ParseException {
		this.setBalance(accountDO.getMoney());
		accountDO.setMoney(0f);
		BigDecimal amount = new BigDecimal(this.getBalance());
		int num = amount.divide(new BigDecimal(stockDO.getClose()),0)
				.divide(new BigDecimal(100), 0, BigDecimal.ROUND_DOWN)
				.intValue() * 100;
		float close = stockDO.getClose();
		this.setBuyPoint(close);
		this.setTradeNum(new Float(num));
		DateFormat dateFormat = new SimpleDateFormat(PriceUtil.dateFormatString);
		System.out.println(dateFormat.format(stockDO.getTime())
				+ " Day买点" + this.getBuyPoint() + "出现,准备买入:"
				+ this.getTradeNum() + "股");
	}

	public void executeBuy(PriceDO stockDO, AccountDO accountDO)
			throws ParseException {
		this.setTotalNum(this.getTradeNum());
		this.setBalance(this.getBalance() - this.getBuyPoint()
				* this.getTradeNum());
		this.setTrades(this.getTrades() + 1);
		this.setAction(Constant.PLAN_ACTION_JIACANG);
	}

	public boolean isStopLoss(PriceDO stockDO, AccountDO accountDO) {
		if (!Constant.PLAN_ACTION_BUY.equals(this.getAction())) {
			DateFormat dateFormat = new SimpleDateFormat(PriceUtil.dateFormatString);
				Date preMonthDate = PriceUtil.pre(dataPool.getMonthStockMap(), stockDO.getTime());
				PriceDO preMonthStock = dataPool.getMonthStockMap().get(dateFormat.format(preMonthDate));
				if(preMonthStock.getBbi() > stockDO.getBbi()){
					this.setStopLoss(stockDO.getClose());
					return true;
				}
		}
		return false;

	}

	public void executeStopLost(PriceDO stockDO, AccountDO accountDO) {
		BigDecimal stopLoss = new BigDecimal(stockDO.getClose());
		BigDecimal count = new BigDecimal(this.getTotalNum());
		Float money = stopLoss.multiply(count).setScale(2,
				BigDecimal.ROUND_DOWN).floatValue();
		// 止损之后,计划中操作的余额归还账户
		this.setBalance(money + this.getBalance());
		accountDO.setMoney(accountDO.getMoney() + this.getBalance());
		DateFormat dateFormat = new SimpleDateFormat(PriceUtil.dateFormatString);
		System.out.println(dateFormat.format(stockDO.getTime())
				+ " 止损点" + this.getStopLoss()
				+ "出现,止损%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%:"
				+ this.getTotalNum() + "股");
		System.out.println("共交易" + this.getTrades() + "次,账户余额:"
				+ accountDO.getMoney());
		
		if(this.getBuyPoint() > this.getStopLoss()){
			this.getExitStatus().add(Constant.EXIT_STATUS_STOPLOST);
		}
		else{
			this.getExitStatus().add(Constant.EXIT_STATUS_JUSTWIN);
		}
		resetPlan();
	}

//	public void setJiacangPlan(StockDO stockDO, AccountDO accountDO) {
		// 如果不是空仓 从新计算下一次加仓点 和止损点
//		Float atr = this.getAtr();
//		// 先计算止损
//		if (this.getTrades() == 1) {
//			this.setJcPoint(new BigDecimal(this.getBuyPoint() + 0.5f
//					* this.getAtr()).setScale(2, BigDecimal.ROUND_DOWN)
//					.floatValue());
//		} else {
//			this.setJcPoint(new BigDecimal(this.getJcPoint() + 0.5f
//					* this.getAtr()).setScale(2, BigDecimal.ROUND_DOWN)
//					.floatValue());
//		}
//		this.setStopLoss(new BigDecimal(this.getJcPoint() - atr * 2).setScale(
//				2, BigDecimal.ROUND_DOWN).floatValue());
//
//		if (Constant.PLAN_ACTION_JIACANG.equals(this.getAction())) {
//			// 如果还可以加仓，则计算下次计划
//			// 操作余额不够支付正常加仓计划时，已余额最大可以的买的数量作为下次加仓数量
//			if (this.getBalance() < this.getJcPoint() * this.getTradeNum()) {
//				int jcNum = (int) (this.getBalance() / this.getJcPoint() / 100);
//				if (jcNum == 0) {
//					this.setPlanTrades(this.getTrades());
//					this.setAction(Constant.PLAN_ACTION_JUSTWIN);
//					System.out.println("=============>"
//							+ " 操作资金已经满仓,调整计划为止赢!!!!!!!!!!!!!!!!" + ";止损:"
//							+ this.getStopLoss());
//				} else {
//					DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
//					System.out.println("=============>"
//							+ dateFormat.format(stockDO.getTime())
//							+ " 余额不够了,余额是" + this.getBalance() + " 需要:"
//							+ this.getJcPoint() * this.getTradeNum());
//					this.setTradeNum(new Float(jcNum * 100));
//					System.out.print("=============>"
//							+ dateFormat.format(stockDO.getTime())
//							+" 调整购买数量为" + jcNum * 100);
//					System.out.println(" 调整计划在" + this.getJcPoint() + " 加仓:"
//							+ this.getTradeNum() + "股" + ";止损:"
//							+ this.getStopLoss());
//				}
//
//			} else {
//				DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
//				System.out.println("=============>"
//						+ dateFormat.format(stockDO.getTime())
//						+ "计划在" + this.getJcPoint() + "加仓:"
//						+ this.getTradeNum() + "股" + ";止损:"
//						+ this.getStopLoss());
//			}
//		} else if (Constant.PLAN_ACTION_JUSTWIN.equals(this.getAction())) {
//			System.out.println("=============>" + "调整计划为止赢!!!!!!!!!!!!!!!!"
//					+ ";止损:" + this.getStopLoss());
//		}

//	}

//	public boolean isJustWin(StockDO stockDO, AccountDO accountDO)
//			throws ParseException {
//		float low = StockUtil.preLow(dataPool.getDayStockMap(), stockDO
//				.getTime(), 10);
//		if (Constant.PLAN_ACTION_JUSTWIN.equals(this.getAction())) {
//			if (low != 0 && stockDO.getLow() <= low) {
//				return true;
//			}
//		}
//		return false;
//	}

//	public void executeJustWin(StockDO stockDO, AccountDO accountDO) {
//		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
//		System.out.println(dateFormat.format(stockDO.getTime())
//				+ " 止赢点" + stockDO.getClose() + "出现,止赢:" + this.getTotalNum()
//				+ "股");
//		float low = StockUtil.preLow(dataPool.getDayStockMap(), stockDO
//				.getTime(), 10);
//		BigDecimal justWin = new BigDecimal(low);
//		BigDecimal count = new BigDecimal(this.getTotalNum());
//		Float money = justWin.multiply(count)
//				.setScale(2, BigDecimal.ROUND_DOWN).floatValue();
//		this.setBalance(money + this.getBalance());
//		accountDO.setMoney(accountDO.getMoney() + this.getBalance());
//		System.out.println("共交易" + this.getTrades() + "次,账户余额:"
//				+ accountDO.getMoney());
//		resetPlan();
//		this.getExitStatus().add(Constant.EXIT_STATUS_JUSTWIN);
//	}

//	public boolean isJiaCang(StockDO stockDO, AccountDO accountDO) {
//		if (Constant.PLAN_ACTION_JIACANG.equals(this.getAction())
//				&& this.getTrades() < this.getPlanTrades()) {
//			if (this.getJcPoint() <= stockDO.getHigh()) {
//				return true;
//			}
//		}
//		return false;
//	}

//	public void executeJiaCang(StockDO stockDO, AccountDO accountDO)
//			throws ParseException {
//		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
//		System.out.println(dateFormat.format(stockDO.getTime())
//				+ " 加仓点" + this.getJcPoint() + "出现,加仓:" + this.getTradeNum()
//				+ "股");
//		BigDecimal jcCount = new BigDecimal(this.getTradeNum());
//		BigDecimal jcPoint = new BigDecimal(this.getJcPoint());
//		Float money = jcPoint.multiply(jcCount).setScale(2,
//				BigDecimal.ROUND_DOWN).floatValue();
//		this.setBalance(this.getBalance() - money);
//		this.setTotalNum(this.getTradeNum() + this.getTotalNum());
//		this.setTrades(this.getTrades() + 1);
//		if (this.getTrades() >= this.getPlanTrades()) {
//			this.setAction(Constant.PLAN_ACTION_JUSTWIN);
//		}
//	}

	private void resetPlan() {
		this.setAction(Constant.PLAN_ACTION_BUY);
		this.setStopLoss(0f);
		this.setJustWin(0f);
		this.setTrades(0);
		this.setPlanTrades(1);
		this.setAtr(0f);
		this.setBuyPoint(0f);
		this.setJcPoint(0f);
		this.setTradeNum(0f);
		this.setTotalNum(0f);
		this.setBalance(0f);
	}

	@Override
	public boolean canTrade(Date time) throws ParseException {
		List<Date> dateList = PriceUtil
				.pre(dataPool.getMonthStockMap(), time, 21);
		if (dateList != null) {
			return true;
		} else {
			return false;
		}
	}
}
