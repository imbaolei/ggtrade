package com.baolei.ghost.test.none;

import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.ghost.test.Report;
import com.baolei.ghost.test.Test;

public class TestNone extends Test{
	float account;
	float toucun;
	float buyPoint;
	Report report;

	@Override
	public boolean needBuy(StockDO stockDO) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void noBuyNoSale(StockDO stockDO) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buy(StockDO stockDO) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean needSale(StockDO stockDO) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void sale(StockDO stockDO) {
		// TODO Auto-generated method stub
		
	}

}
