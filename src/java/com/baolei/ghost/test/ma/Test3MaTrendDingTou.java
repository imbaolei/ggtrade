package com.baolei.ghost.test.ma;

import org.springframework.beans.factory.annotation.Autowired;

import com.baolei.ghost.common.CalendarUtil;
import com.baolei.ghost.dal.dataobject.StockDO;


public class Test3MaTrendDingTou extends Test3MaTrend2Stock {
	
	@Autowired
	CalendarUtil calendarUtil = new CalendarUtil();
	protected float totalMoney;
	protected float moneyPeriod = 1000;
	
	

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
		
	}


}
