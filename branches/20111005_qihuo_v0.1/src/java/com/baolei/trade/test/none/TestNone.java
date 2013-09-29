package com.baolei.trade.test.none;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.dal.dataobject.PriceDO;
import com.baolei.trade.test.Test;

public class TestNone extends Test{

	@Override
	public boolean needBuy(String dateString)  {
		if(toucunHR == 0){
			return true;
		}
		return false;
	}

	@Override
	public void noBuyNoSale(String dateString) {
		PriceDO stockDO = jyStockMap.get(dateString);
		float buyPoint = lastBuyStockDO.getClose();
		if(buyPoint == 0){
			stockDO.getReport().setAccount(cash+toucunHR);
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_NOSTART);
			return;
		}
		float tmpToucun = toucunHR + (stockDO.getClose()-buyPoint)/buyPoint*toucunHR;
		tmpToucun = Float.parseFloat(decimalFormat.format(tmpToucun));
		stockDO.getReport().setAccount(tmpToucun);
		if(toucunHR > 0 ){
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_CHICANG);
		}else{
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_KONGCANG);
		}
		
	}

	@Override
	public void buy(String dateString) {
		PriceDO stockDO = jyStockMap.get(dateString);
		toucunHR = cash;
		cash = 0;
		lastBuyStockDO = stockDO;
		float buyPoint = lastBuyStockDO.getClose();
		stockDO.getReport().setAccount(cash+toucunHR);
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
