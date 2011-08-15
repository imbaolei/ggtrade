package com.baolei.ghost.test.none;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.ghost.test.Report;
import com.baolei.ghost.test.Test;

public class TestNone extends Test{
	float account;
	float toucun;
	float buyPoint;
	Report report;
	
	public TestNone(float account){
		this.account = account;
	}

	@Override
	public boolean needBuy(StockDO stockDO) {
		if(toucun == 0){
			return true;
		}
		return false;
	}

	@Override
	public void noBuyNoSale(StockDO stockDO) {
		if(buyPoint == 0){
			stockDO.getReport().setAccount(account+toucun);
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_NOSTART);
			return;
		}
		float tmpToucun = toucun + (stockDO.getClose()-buyPoint)/buyPoint*toucun;
		tmpToucun = Float.parseFloat(decimalFormat.format(tmpToucun));
		stockDO.getReport().setAccount(tmpToucun);
		if(toucun > 0 ){
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_CHIYOU);
		}else{
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_KONGCANG);
		}
		
	}

	@Override
	public void buy(StockDO stockDO) {
		toucun = account;
		account = 0;
		buyPoint = stockDO.getClose();
		stockDO.getReport().setAccount(account+toucun);
		stockDO.getReport().setNotes(buyPoint + " 买入 ");
		
	}

	@Override
	public boolean needSale(StockDO stockDO) {
		return false;
	}

	@Override
	public void sale(StockDO stockDO) {
		
	}

}
