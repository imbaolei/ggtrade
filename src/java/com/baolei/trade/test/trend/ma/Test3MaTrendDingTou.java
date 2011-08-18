package com.baolei.trade.test.trend.ma;

import com.baolei.ghost.common.CalendarUtil;
import com.baolei.ghost.dal.dataobject.StockDO;


/**
 * @author lei.baol
 * 时点交易法 的定投版本 每月1号定投
 * 
 */
public class Test3MaTrendDingTou extends Test3MaTrend {
	
	
	protected float totalMoney;
	
	protected float jyDingTouMoney = 0; //一次买入和卖出 期间 定投的金额
	
	
	public Test3MaTrendDingTou(){}
	
	
	
	@Override
	public boolean needDingTou(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		if(CalendarUtil.isFirstDayOfMonth(pdStockList, stockDO)){
			return true;
		}
		return false;
	}

	@Override
	public void dingTou(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		cash = cash + moneyDingTou;
		//定投的时候 如果总定金额等于0，相当于还没开始计算总金额
		if(totalMoney == 0 ){
			totalMoney = cash;
		}else{
			totalMoney = totalMoney + moneyDingTou;
		}
		stockDO.getReport().setTotalMoney(totalMoney);
		stockDO.getReport().setDingTou(true);
		jyDingTouMoney = jyDingTouMoney + moneyDingTou;
	}
	
	@Override
	public void buy(String dateString) {
		
		super.buy(dateString);
		jyDingTouMoney = 0;
		//如果买入当天定投过 因为定投在买入之前，所以当天定投的钱要算入收益成本 
		StockDO stockDO = jyStockMap.get(dateString);
		if(stockDO.getReport().getDingTou()){
			jyDingTouMoney = moneyDingTou;
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
