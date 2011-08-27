package com.baolei.trade.test.trend.ma.filter;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.trend.ma.Ma3Tc;

public class Ma3LLVTc extends Ma3Tc {

	protected int llvCount = 20;
	protected int hhvCount = 20;

	protected boolean isLLV(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		return StockUtil.isLLV(pdStockList, stockDO, llvCount);
	}

	protected boolean isHHV(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		return StockUtil.isHHV(pdStockList, stockDO, hhvCount);
	}

	@Override
	public boolean needBuy(String dateString) {
		if(canBuyFirst(dateString)){
			return true;
		}
		if(canBuyDingTou(dateString)){
			return true;
		}
		return false;
	}
	
	protected boolean canBuyFirst(String dateString){
		StockDO lastJyStock = findLastJyStock(dateString);
		if(lastJyStock == null){
			return false;
		}
		String lastJyStatus = lastJyStock.getReport().getStatus();
		if(!Constant.REPORT_STATUS_BUY.equals(lastJyStatus)){
			if(super.needBuy(dateString) && isHHV(dateString)){
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean canBuyDingTou(String dateString){
		StockDO lastJyStock = findLastJyStock(dateString);
		if(lastJyStock == null){
			return false;
		}
		String lastJyStatus = lastJyStock.getReport().getStatus();
		if(Constant.REPORT_STATUS_BUY.equals(lastJyStatus)){
			if(super.needBuy(dateString)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean needSale(String dateString) {
		if (super.needSale(dateString) && isLLV(dateString)) {
			return true;
		}
		return false;
	}
	
	
	
}
