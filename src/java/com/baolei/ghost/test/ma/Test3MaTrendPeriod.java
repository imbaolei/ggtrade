package com.baolei.ghost.test.ma;

import java.util.List;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.dal.dataobject.StockDO;

public class Test3MaTrendPeriod extends Test3MaTrend2Stock{
	
	public Test3MaTrendPeriod(float toucunLR, Integer p1, Integer p2, Integer p3) {
		super(toucunLR, p1, p2, p3);
	}

	@Override
	public boolean needBuy(String dateString) {
		
		return false;
	}
	
	public boolean panDuanJiaoYiInPeriod(String dateString){
		StockDO stockDO = jyStockMap.get(dateString);
		List<StockDO> stockList = calendarUtil.getStockListOfMonth(jyStockList, stockDO);
		for(StockDO tmpStockDO : stockList){
			String status = tmpStockDO.getReport().getStatus();
			if(Constant.REPORT_STATUS_BUY.equals(status)){
				
			}
		}
		return false;
		
	}

}
