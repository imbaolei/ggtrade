package com.baolei.ghost.plan.hg;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.baolei.ghost.AccountDO;
import com.baolei.ghost.DataPool;
import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.ghost.plan.Plan;

/**
 * 
 * //买点：周bbi向上时前提，突破20天最高点
 * //加仓：买点 R
 * //止损：买点 -2R
 * //止盈：10日最低点
 * @author B
 *
 */
public class HGOnWBPlan extends Plan {

	public HGOnWBPlan(DataPool dataPool) {
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
	public void execute(StockDO stockDO, AccountDO accountDO)
			throws ParseException {
		if (isBuy(stockDO, accountDO)) {
			// 达到买点后,先制定操作计划,再进行买卖
			setBuyPlan(stockDO, accountDO);
			executeBuy(stockDO, accountDO);
			setJiacangPlan(stockDO, accountDO);
		} else if (isStopLoss(stockDO, accountDO)) {
			executeStopLost(stockDO, accountDO);
		} else if (isJustWin(stockDO, accountDO)) {
			executeJustWin(stockDO, accountDO);
		} else if (isJiaCang(stockDO, accountDO)) {
			// 达到加仓点后,是先执行上次加仓计划,再计算下次计划
			executeJiaCang(stockDO, accountDO);
			setJiacangPlan(stockDO, accountDO);
		}
	}

	public boolean isBuy(StockDO stockDO, AccountDO accountDO)
			throws ParseException {
		if (Constant.PLAN_ACTION_BUY.equals(this.getAction())) {
			float theDayWeekBbi = StockUtil.getTheTimeBbiOfPeriod(dataPool
					.getWeekStockMap(), stockDO);
			Date pre1WeekDayDate = StockUtil.preWeekDay(dataPool
					.getWeekStockMap(), stockDO.getTime());
			DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
			float pre1WeekBbi = dataPool.getWeekStockMap().get(
					dateFormat.format(pre1WeekDayDate)).getBbi();
			//买点：周bbi向上时前提，突破20天最高点
			if ( theDayWeekBbi > pre1WeekBbi){
				float hide = StockUtil.preHide(dataPool.getDayStockMap(), stockDO
						.getTime(), 19);
				if (hide != 0) {
					if (stockDO.getHigh() > hide) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void setBuyPlan(StockDO stockDO, AccountDO accountDO)
			throws ParseException {
		this.setPlanTrades(4);// 设置一共交易次数为4
		// 如果空仓 根据现有总金额计算 atr 购买数量 加仓点 和 止损点
		this.setBalance(accountDO.getMoney());
		accountDO.setMoney(accountDO.getMoney() - accountDO.getMoney());

		BigDecimal amount = new BigDecimal(this.getBalance());
		// 如果一直以买点的atr为标准,则需要在这里设置好;否则在每次操作完资金后,取最新的atr重新计算。这里是以买点的atr为准
		Float calatr = StockUtil.atr(dataPool.getDayStockMap(), stockDO
				.getTime());
		this.setAtr(calatr);
		int num = amount.divide(new BigDecimal(100)).divide(
				new BigDecimal(calatr.floatValue()), 0, BigDecimal.ROUND_DOWN)
				.divide(new BigDecimal(100), 0, BigDecimal.ROUND_DOWN)
				.intValue() * 100;
		float hide = StockUtil.preHide(dataPool.getDayStockMap(), stockDO
				.getTime(), 18);
		this.setBuyPoint(hide);
		this.setTradeNum(new Float(num));
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		System.out.println(dateFormat.format(stockDO.getTime())
				+ " Day买点" + this.getBuyPoint() + "出现,准备买入:"
				+ this.getTradeNum() + "股");
		// 因为还没买 应该不存在加仓和止损点
		if (this.getBalance() < num * stockDO.getClose()) {
			int buyNum = (int) ((this.getBalance()) / this.getBuyPoint() / 100);
			this.setTradeNum(new Float(buyNum));
			this.setAction(Constant.PLAN_ACTION_JUSTWIN);
			System.out.println("余额不够了,余额是" + this.getBalance() + "需要:"
					+ this.getJcPoint() * this.getTradeNum());
			System.out.println("调整购买数量为" + buyNum * 100 + ";调整计划为止赢,止损点为:"
					+ this.getStopLoss());
		}
	}

	public void executeBuy(StockDO stockDO, AccountDO accountDO)
			throws ParseException {
		this.setTotalNum(this.getTradeNum());
		this.setBalance(this.getBalance() - this.getBuyPoint()
				* this.getTradeNum());
		this.setTrades(this.getTrades() + 1);
		this.setAction(Constant.PLAN_ACTION_JIACANG);
	}

	public boolean isStopLoss(StockDO stockDO, AccountDO accountDO) {
		//
		if (!Constant.PLAN_ACTION_BUY.equals(this.getAction())) {
			// System.out.println(StockUtil.dateFormat.format(stockDO.getTime())+":"+planDO.getStopLoss()+":"+stockDO.getLow());
			// 如果止损点在最高和最低点之间或者开盘价就在止损点之下,都执行止损
			if ((this.getStopLoss() >= stockDO.getLow() && this.getStopLoss() <= stockDO
					.getHigh())
					|| this.getStopLoss() > stockDO.getOpen()) {
				return true;
			}
		}
		return false;

	}

	public void executeStopLost(StockDO stockDO, AccountDO accountDO) {
		BigDecimal stopLoss = new BigDecimal(this.getStopLoss());
		BigDecimal count = new BigDecimal(this.getTotalNum());
		Float money = stopLoss.multiply(count).setScale(2,
				BigDecimal.ROUND_DOWN).floatValue();
		// 止损之后,计划中操作的余额归还账户
		this.setBalance(money + this.getBalance());
		accountDO.setMoney(accountDO.getMoney() + this.getBalance());
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		System.out.println(dateFormat.format(stockDO.getTime())
				+ " 止损点" + this.getStopLoss()
				+ "出现,止损%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%:"
				+ this.getTotalNum() + "股");
		System.out.println("共交易" + this.getTrades() + "次,账户余额:"
				+ accountDO.getMoney());
		resetPlan();
		this.getExitStatus().add(Constant.EXIT_STATUS_STOPLOST);
	}

	public void setJiacangPlan(StockDO stockDO, AccountDO accountDO) {
		//加仓：
		// 如果不是空仓 从新计算下一次加仓点 和止损点
		Float atr = this.getAtr();
		// 先计算止损
		if (this.getTrades() == 1) {
			this.setJcPoint(new BigDecimal(this.getBuyPoint() + 0.5f
					* this.getAtr()).setScale(2, BigDecimal.ROUND_DOWN)
					.floatValue());
		} else {
			this.setJcPoint(new BigDecimal(this.getJcPoint() + 0.5f
					* this.getAtr()).setScale(2, BigDecimal.ROUND_DOWN)
					.floatValue());
		}
		this.setStopLoss(new BigDecimal(this.getJcPoint() - atr * 2).setScale(
				2, BigDecimal.ROUND_DOWN).floatValue());

		if (Constant.PLAN_ACTION_JIACANG.equals(this.getAction())) {
			// 如果还可以加仓，则计算下次计划
			// 操作余额不够支付正常加仓计划时，已余额最大可以的买的数量作为下次加仓数量
			if (this.getBalance() < this.getJcPoint() * this.getTradeNum()) {
				int jcNum = (int) (this.getBalance() / this.getJcPoint() / 100);
				if (jcNum == 0) {
					this.setPlanTrades(this.getTrades());
					this.setAction(Constant.PLAN_ACTION_JUSTWIN);
					System.out.println("=============>"
							+ " 操作资金已经满仓,调整计划为止赢!!!!!!!!!!!!!!!!" + ";止损:"
							+ this.getStopLoss());
				} else {
					DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
					System.out.println("=============>"
							+ dateFormat.format(stockDO.getTime())
							+ " 余额不够了,余额是" + this.getBalance() + " 需要:"
							+ this.getJcPoint() * this.getTradeNum());
					this.setTradeNum(new Float(jcNum * 100));
					System.out.print("=============>"
							+ dateFormat.format(stockDO.getTime())
							+" 调整购买数量为" + jcNum * 100);
					System.out.println(" 调整计划在" + this.getJcPoint() + " 加仓:"
							+ this.getTradeNum() + "股" + ";止损:"
							+ this.getStopLoss());
				}

			} else {
				DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
				System.out.println("=============>"
						+ dateFormat.format(stockDO.getTime())
						+ "计划在" + this.getJcPoint() + "加仓:"
						+ this.getTradeNum() + "股" + ";止损:"
						+ this.getStopLoss());
			}
		} else if (Constant.PLAN_ACTION_JUSTWIN.equals(this.getAction())) {
			System.out.println("=============>" + "调整计划为止赢!!!!!!!!!!!!!!!!"
					+ ";止损:" + this.getStopLoss());
		}

	}

	public boolean isJustWin(StockDO stockDO, AccountDO accountDO)
			throws ParseException {
		float low = StockUtil.preLow(dataPool.getDayStockMap(), stockDO
				.getTime(), 10);
		if (Constant.PLAN_ACTION_JUSTWIN.equals(this.getAction())) {
			if (low != 0 && stockDO.getLow() <= low) {
				return true;
			}
		}
		return false;
	}

	public void executeJustWin(StockDO stockDO, AccountDO accountDO) {
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		System.out.println(dateFormat.format(stockDO.getTime())
				+ " 止赢点" + stockDO.getClose() + "出现,止赢:" + this.getTotalNum()
				+ "股");
		float low = StockUtil.preLow(dataPool.getDayStockMap(), stockDO
				.getTime(), 10);
		BigDecimal justWin = new BigDecimal(low);
		BigDecimal count = new BigDecimal(this.getTotalNum());
		Float money = justWin.multiply(count)
				.setScale(2, BigDecimal.ROUND_DOWN).floatValue();
		this.setBalance(money + this.getBalance());
		accountDO.setMoney(accountDO.getMoney() + this.getBalance());
		System.out.println("共交易" + this.getTrades() + "次,账户余额:"
				+ accountDO.getMoney());
		resetPlan();
		this.getExitStatus().add(Constant.EXIT_STATUS_JUSTWIN);
	}

	public boolean isJiaCang(StockDO stockDO, AccountDO accountDO) {
		if (Constant.PLAN_ACTION_JIACANG.equals(this.getAction())
				&& this.getTrades() < this.getPlanTrades()) {
			if (this.getJcPoint() <= stockDO.getHigh()) {
				return true;
			}
		}
		return false;
	}

	public void executeJiaCang(StockDO stockDO, AccountDO accountDO)
			throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		System.out.println(dateFormat.format(stockDO.getTime())
				+ " 加仓点" + this.getJcPoint() + "出现,加仓:" + this.getTradeNum()
				+ "股");
		BigDecimal jcCount = new BigDecimal(this.getTradeNum());
		BigDecimal jcPoint = new BigDecimal(this.getJcPoint());
		Float money = jcPoint.multiply(jcCount).setScale(2,
				BigDecimal.ROUND_DOWN).floatValue();
		this.setBalance(this.getBalance() - money);
		this.setTotalNum(this.getTradeNum() + this.getTotalNum());
		this.setTrades(this.getTrades() + 1);
		if (this.getTrades() >= this.getPlanTrades()) {
			this.setAction(Constant.PLAN_ACTION_JUSTWIN);
		}
	}

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
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		StockDO theDay = dataPool.getDayStockMap().get(
				dateFormat.format(time));
		if (theDay == null || theDay.getBbi() <= 0) {
			return false;
		}
		Date preDay = StockUtil
				.pre(dataPool.getDayStockMap(), theDay.getTime());
		if (preDay == null) {
			return false;
		}
		
		StockDO preDayStockDO = dataPool.getDayStockMap().get(
				dateFormat.format(preDay.getTime()));
		if (preDayStockDO.getBbi() <= 0) {
			return false;
		}
		Date pre2Day = StockUtil.pre(dataPool.getDayStockMap(), preDay);
		if (pre2Day == null) {
			return false;
		}
		StockDO pre2DayStockDO = dataPool.getDayStockMap().get(
				dateFormat.format(pre2Day.getTime()));
		if (pre2DayStockDO.getBbi() <= 0) {
			return false;
		}
		Date pre3Day = StockUtil.pre(dataPool.getDayStockMap(), pre2Day);
		if (pre3Day == null) {
			return false;
		}
		StockDO pre3DayStockDO = dataPool.getDayStockMap().get(
				dateFormat.format(pre3Day.getTime()));
		if (pre3DayStockDO.getBbi() <= 0) {
			return false;
		}

		Date pre1WeekDay = StockUtil.preWeekDay(dataPool.getWeekStockMap(),
				theDay.getTime());
		if (pre1WeekDay == null) {
			return false;
		}
		StockDO pre1WeekDayStockDO = dataPool.getWeekStockMap().get(
				dateFormat.format(pre1WeekDay));
		if (pre1WeekDayStockDO.getBbi() <= 0) {
			return false;
		}

		Date pre2WeekDay = StockUtil.preWeekDay(dataPool.getWeekStockMap(),
				pre1WeekDay);
		if (pre2WeekDay == null) {
			return false;
		}

		StockDO pre2WeekDayStockDO = dataPool.getWeekStockMap().get(
				dateFormat.format(pre2WeekDay));
		if (pre2WeekDayStockDO.getBbi() <= 0) {
			return false;
		}
		List<Date> dateList = StockUtil.pre(dataPool.getDayStockMap(), time, 18);
		if (dateList != null) {
			return true;
		} else {
			return false;
		}
	}

}
