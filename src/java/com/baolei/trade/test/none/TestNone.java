package com.baolei.trade.test.none;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.ghost.test.Report;
import com.baolei.trade.test.Test;

public class TestNone extends Test{
	float account;
	float toucun;
	float buyPoint;
	Report report;
	
	public TestNone(float account){
		this.account = account;
	}

	@Override
	public boolean needBuy(String dateString)  {
		StockDO stockDO = pdStockMap.get(dateString);
		if(toucun == 0){
			return true;
		}
		return false;
	}

	@Override
	public void noBuyNoSale(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		if(buyPoint == 0){
			stockDO.getReport().setAccount(account+toucun);
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_NOSTART);
			return;
		}
		float tmpToucun = toucun + (stockDO.getClose()-buyPoint)/buyPoint*toucun;
		tmpToucun = Float.parseFloat(decimalFormat.format(tmpToucun));
		stockDO.getReport().setAccount(tmpToucun);
		if(toucun > 0 ){
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_CHICANG);
		}else{
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_KONGCANG);
		}
		
	}

	@Override
	public void buy(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		toucun = account;
		account = 0;
		buyPoint = stockDO.getClose();
		stockDO.getReport().setAccount(account+toucun);
		stockDO.getReport().setNotes(buyPoint + " 买入 ");
		
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dingTou(String dateString) {
		// TODO Auto-generated method stub
		
	}

}
