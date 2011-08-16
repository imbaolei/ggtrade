package com.baolei.ghost.test.ma;

import org.springframework.beans.factory.annotation.Autowired;

import com.baolei.ghost.common.CalendarUtil;
import com.baolei.ghost.common.Constant;
import com.baolei.ghost.dal.dataobject.StockDO;


public class Test3MaTrendDingTou extends Test3MaTrend2Stock {
	
	@Autowired
	CalendarUtil calendarUtil = new CalendarUtil();
	protected float totalMoney;
	protected float moneyPeriod = 1000;
	protected float jyDingTouMoney = 0; //一次买入和卖出 期间 定投的金额
	
	

	public Test3MaTrendDingTou(float account, Integer p1, Integer p2, Integer p3) {
		super(account, p1, p2, p3);
	}
	
	
	@Override
	public boolean needDingTou(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		if(calendarUtil.isFirstDayOfMonth(pdStockList, stockDO)){
			return true;
		}
		return false;
	}

	@Override
	public void dingTou(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		toucunLR = toucunLR + moneyPeriod;
		//定投的时候 如果总定金额等于0，相当于还没开始计算总金额
		if(totalMoney == 0 ){
			totalMoney = toucunLR;
		}else{
			totalMoney = totalMoney + moneyPeriod;
		}
		stockDO.getReport().setTotalMoney(totalMoney);
		stockDO.getReport().setDingTou(true);
		jyDingTouMoney = jyDingTouMoney + moneyPeriod;
	}
	
	@Override
	public void buy(String dateString) {
		
		super.buy(dateString);
		jyDingTouMoney = 0;
		//如果买入当天定投过 因为定投在买入之前，所以当天定投的钱要算入收益成本 
		StockDO stockDO = jyStockMap.get(dateString);
		if(stockDO.getReport().getDingTou()){
			jyDingTouMoney = moneyPeriod;
		}
		
	}
	
	@Override
	public void sale(String dateString){
		super.sale(dateString);
		jyDingTouMoney = 0;
	}
	
	protected float shouyi(String dateString){
		//用卖出这天的账户金额 减去 上次买入前的 账户金额
		StockDO stockDO = jyStockMap.get(dateString);
		int index = jyStockList.indexOf(lastBuyStockDO);
		StockDO preStockDO = jyStockList.get(index-1);
		float shouyi = stockDO.getReport().getAccount() - preStockDO.getReport().getAccount() - jyDingTouMoney;
		return shouyi;
	}


}
