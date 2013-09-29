package com.baolei.trade.test.trend.ma.filter;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.NumberUtil;
import com.baolei.ghost.dal.dataobject.PriceDO;

public class Ma3LLVTcAtr extends Ma3LLVTc {

	protected int jiacangTotalCount = 3; // 每次计划加仓的总次数

	protected int jiacangNextPlanCount = 0; // 计划第几次加仓

	protected float jiacangAtrRate = 2f; // 加仓的买点

	/**
	 * 买完设置的加仓计划
	 */
	protected void setJiacangPlan(String dateString) {
		jiacangNextPlanCount++;
		PriceDO stockDO = jyStockMap.get(dateString);
		float buyPoint = stockDO.getReport().getPrice();
		planBuyPoint = buyPoint + jiacangAtrRate * stockDO.getAtr();
		planBuyPoint = NumberUtil.roundDown(planBuyPoint, 2);
	}

	/**
	 * 买之前计算购买的头寸
	 * 
	 * @param dateString
	 */
	protected void setPlanBuyTouCun(String dateString) {
		// 如果第一次加仓
		if (jiacangTotalCount - jiacangNextPlanCount >= 1) {
			planBuyToucun = cash
					/ (jiacangTotalCount - jiacangNextPlanCount + 1);
		} else {
			planBuyToucun = cash;
		}
		planBuyToucun = NumberUtil.roundDown(planBuyToucun, 2);
	}

	@Override
	public boolean needBuy(String dateString) {
		// 判断是否需要加仓 优先级比买入高
		// 如果上次是买入状态 就要按照加仓的标准 来判断买入 包含定投
		if (canJiacang(dateString)) {
			return true;
		}
		if (canBuyFirst(dateString)) {
			return true;
		}
		return false;
	}

	protected boolean canJiacang(String dateString) {
		PriceDO lastJyStock = findLastJyStock(dateString);
		if(lastJyStock == null){
			return false;
		}
		PriceDO stockDO = pdStockMap.get(dateString);
		String lastJyStatus = lastJyStock.getReport().getStatus();
		if (Constant.REPORT_STATUS_BUY.equals(lastJyStatus)) {
			if (cash > 0) {
				if (stockDO.getHigh() > planBuyPoint) {
					return true;
				}
			}

		}
		return false;
	}

	@Override
	public void buy(String dateString) {
		setPlanBuyTouCun(dateString);
		executeBuy(dateString);
		setBuyReport(dateString);
		setJiacangPlan(dateString);
	}

	protected void executeBuy(String dateString) {
		PriceDO stockDO = jyStockMap.get(dateString);
		float fee = 0;
		fee = fee(planBuyToucun);
		stockDO.getReport().setFee(fee);
		float buyPoint = planBuyPoint;
		stockDO.getReport().setPrice(buyPoint);
		toucunHR = toucunChange(dateString);
		toucunHR = toucunHR + planBuyToucun - fee;
		cash = cash - planBuyToucun;
		cash = NumberUtil.roundDown(cash, 2);
		lastBuyStockDO = stockDO;
		stockDO.getReport().setTime(stockDO.getTime());
	}

	protected void setBuyReport(String dateString) {
		PriceDO stockDO = jyStockMap.get(dateString);
		float buyPoint = stockDO.getReport().getPrice();
		float fee = stockDO.getReport().getFee();
		float account = toucunHR + cash;
		stockDO.getReport().setAccount(account);
		stockDO.getReport().setNotes(" - 买点 ： " + buyPoint);
		stockDO.getReport().setFee(fee);
		totalFee = totalFee + fee;
		stockDO.getReport().setTotalFee(totalFee);
		stockDO.getReport().setStatus(Constant.REPORT_STATUS_BUY);
		transCount = transCount + 1;
		stockDO.getReport().setTransCount(transCount);
	}

	protected void clearJiacangPlan() {
		jiacangNextPlanCount = 0; // 计划第几次加仓
		planBuyPoint = 0; // 计划加仓点
		planBuyToucun = 0; // 计划加仓的头寸
	}

	@Override
	public void sale(String dateString) {
		super.sale(dateString);
		clearJiacangPlan();
	}
}
