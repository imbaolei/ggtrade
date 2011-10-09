package com.baolei.trade.test.trend.ma.filter;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.NumberUtil;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.trend.ma.Ma3Tc;

public class Ma2Hg55LLVTcAtr extends Ma3Tc {

	protected int jiacangTotalCount = 3; // 每次计划加仓的总次数

	protected int jiacangNextPlanCount = 0; // 计划第几次加仓

	protected float jiacangAtrRate = 0.5f; // 加仓的买点
	
	protected float saleAtrRate = 2f;
	
	protected Integer llvCount = 20;
	protected Integer hhvCount = 55;
	protected Integer ma1 = 25;
	protected Integer ma2 = 200;

	protected boolean isLLV(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		return StockUtil.isLLV(pdStockList, stockDO, llvCount);
	}

	protected boolean isHHV(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		return StockUtil.isHHV(pdStockList, stockDO, hhvCount);
	}
	
	protected float getLLV(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		return StockUtil.getLLV(pdStockList, stockDO, llvCount);
	}

	protected float getHHV(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		return StockUtil.getHHV(pdStockList, stockDO, hhvCount);
	}

	/**
	 * 买完设置的加仓计划
	 */
	protected void setJiacangPlan(String dateString) {
		jiacangNextPlanCount++;
		StockDO stockDO = jyStockMap.get(dateString);
		float buyPoint = stockDO.getReport().getPrice();
		planBuyPoint = buyPoint + jiacangAtrRate * stockDO.getAtr();
		planBuyPoint = NumberUtil.roundDown(planBuyPoint, 2);
		planSalePoint = buyPoint - saleAtrRate * stockDO.getAtr();
		planSalePoint = NumberUtil.roundDown(planSalePoint, 2);
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
	
	protected boolean canBuyFirst(String dateString) {
		StockDO lastJyStock = findLastJyStock(dateString);
		if (lastJyStock == null) {
			return false;
		}
		String lastJyStatus = lastJyStock.getReport().getStatus();
		if (!Constant.REPORT_STATUS_BUY.equals(lastJyStatus)) {
			//TODO if close>200 而且突破55日
			if(needBuyThis(dateString)){
				return true;
			}
		}
		return false;
	}

	public boolean needBuyThis(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		// 如果判断没有头寸 而且 趋势不是走弱，即走强
		// 如果有现金 就买入
		if ((cash > 0) && isMaReadyJy(stockDO) && trendin(stockDO) && isHHV(dateString)) {
			planBuyPoint = stockDO.getClose();
			planBuyToucun = cash;
			return true;
		}
		return false;
	}
	
	protected boolean trendin(StockDO stockDO){
		float m1 = stockDO.getMa(ma1.toString());
		float m2 = stockDO.getMa(ma2.toString());
		if (m1 > m2) {
			return true;
		}
		return false;
	}
	
	
	protected boolean canJiacang(String dateString) {
		StockDO lastJyStock = findLastJyStock(dateString);
		if(lastJyStock == null){
			return false;
		}
		StockDO stockDO = pdStockMap.get(dateString);
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
		StockDO stockDO = jyStockMap.get(dateString);
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
		StockDO stockDO = jyStockMap.get(dateString);
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
		planSalePoint = 0;
	}

	@Override
	public void sale(String dateString) {
		super.sale(dateString);
		StockDO stockDO = jyStockMap.get(dateString);
		stockDO.getReport().setType(Constant.REPORT_TYPE_DUO);
		clearJiacangPlan();
	}
	
	@Override
	public boolean needSale(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		if(toucunHR > 0){
			if (isLLV(dateString) || (stockDO.getLow() < planSalePoint)) {
				return true;
			}
		}
		return false;
	}
}
