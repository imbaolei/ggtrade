package com.baolei.trade.test.none;

import com.baolei.ghost.common.CalendarUtil;
import com.baolei.ghost.common.Constant;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.Test;

public class TestNoneDingTou extends Test {
	protected float totalMoney;

	protected float jyDingTouMoney = 0; // 一次买入和卖出 期间 定投的金额
	
	public TestNoneDingTou(){
		
	}
	
	public void initAccount(float account){
		this.toucunLR = account;
	}

	@Override
	public boolean needBuy(String dateString) {
		if(toucunHR == 0){
			return true;
		}
		return false;
	}

	@Override
	public void noBuyNoSale(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		if (lastBuyStockDO == null) {
			float account = toucunLR + toucunHR;
			stockDO.getReport().setAccount(account);
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_NOSTART);
			return;
		}
		float buyPoint = lastBuyStockDO.getClose();
		float tmpToucun = toucunHR + (stockDO.getClose() - buyPoint) / buyPoint
				* toucunHR;
		tmpToucun = Float.parseFloat(decimalFormat.format(tmpToucun));
		float account = tmpToucun + toucunLR;
		stockDO.getReport().setAccount(account);
		if (toucunHR > 0) {
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_CHICANG);
		} else {
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_KONGCANG);
		}
		
	}

	@Override
	public void buy(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		float fee = 0;
		
		fee = fee(toucunLR);
		toucunHR = toucunHR + toucunLR - fee;
		toucunLR = 0;

		// 设置report
		float buyPoint = stockDO.getClose();
		float account = toucunHR + toucunLR;
		stockDO.getReport().setAccount(account);
		stockDO.getReport().setNotes(" - 买点 ： " + buyPoint);
		stockDO.getReport().setFee(fee);
		totalFee = totalFee + fee;
		stockDO.getReport().setTotalFee(totalFee);
		stockDO.getReport().setStatus(Constant.REPORT_STATUS_BUY);
		transCount = transCount + 1;
		stockDO.getReport().setTransCount(transCount);
		lastBuyStockDO = stockDO;
		
	}

	@Override
	public boolean needSale(String dateString) {
		return false;
	}

	@Override
	public void sale(String dateString) {
		
	}

	@Override
	public boolean needDingTou(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		if (CalendarUtil.isFirstDayOfMonth(pdStockList, stockDO)) {
			return true;
		}
		return false;
	}

	@Override
	public void dingTou(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		toucunLR = toucunLR + moneyPeriod;
		// 定投的时候 如果总定金额等于0，相当于还没开始计算总金额
		if (totalMoney == 0) {
			totalMoney = toucunLR;
		} else {
			totalMoney = totalMoney + moneyPeriod;
		}
		stockDO.getReport().setTotalMoney(totalMoney);
		stockDO.getReport().setDingTou(true);
		jyDingTouMoney = jyDingTouMoney + moneyPeriod;
	}


	

}
