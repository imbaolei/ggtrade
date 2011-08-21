package com.baolei.trade.test.none;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.Test;

public class TestNoneDingTou extends Test {
	protected float totalMoney;

	protected float jyDingTouMoney = 0; // 一次买入和卖出 期间 定投的金额
	
	public TestNoneDingTou(){
		
	}
	
	public void initCash(float account){
		this.cash = account;
	}

	@Override
	public boolean needBuy(String dateString) {
		if(cash != 0){
			return true;
		}
		return false;
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
		tmpToucun = Float.parseFloat(decimalFormat.format(tmpToucun));
		float account = tmpToucun + cash;
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
		
		//计算 上次定投 到 这次 定投 之间的收益
		if(lastBuyStockDO != null){
			float buyPoint = lastBuyStockDO.getClose();
			toucunHR = toucunHR + (stockDO.getClose() - buyPoint) / buyPoint
					* toucunHR;
			toucunHR = Float.parseFloat(decimalFormat.format(toucunHR));
		}
		
		
		//将 剩余的钱买入 高风险账户  也就是定投账户
		fee = fee(cash);
		toucunHR = toucunHR + cash - fee;
		cash = 0;

		// 设置report
		float account = toucunHR + cash;
		stockDO.getReport().setAccount(account);
		stockDO.getReport().setNotes(" - 买点 ： " + stockDO.getClose());
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
		if (StockUtil.isFirstDayOfMonth(pdStockList, stockDO,firstDay)) {
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
		jyDingTouMoney = jyDingTouMoney + moneyDingTou;
	}


	

}
