package com.baolei.ghost.test.ma;

import java.text.DecimalFormat;

import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.ghost.test.Report;
import com.baolei.ghost.test.Test;

public class MaTrendTest extends Test{
	
	float account;
	float toucun;
	float buyPoint;
	Integer p1 ;
	Integer p2 ;
	Integer p3 ;
	Report report;
	DecimalFormat decimalFormat = new DecimalFormat("#.00");
	
	public MaTrendTest(float account,Integer p1,Integer p2,Integer p3){
		this.account = account;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		
	}
	
	/**
	 * 当且仅当 p1<=p2 && p2<=p3 周期的均线时 ，判断趋势走弱
	 * @param stockDO
	 * @param p1 周期1
	 * @param p2
	 * @param p3
	 * @return
	 */
	private boolean trendout(StockDO stockDO){
		float ma1 = stockDO.getMa(p1.toString());
		float ma2 = stockDO.getMa(p2.toString());
		float ma3 = stockDO.getMa(p3.toString());
		//三个周期的均线都有值时
		if(ma1 > 0 && ma2 > 0 && ma3 >0){
			if(ma1<=ma2 && ma2<= ma3){
				return true;
			}
		}
		return false;
	}
	
	

	@Override
	public boolean needBuy(StockDO stockDO) {
		//如果判断没有头寸 而且 趋势不是走弱，即走强
		if((toucun == 0) && !trendout(stockDO)){
			return true;
		}
		return false;
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
		if((toucun > 0) && trendout(stockDO)){
			return true;
		}
		return false;
	}

	@Override
	public void sale(StockDO stockDO) {
		
		
		toucun = toucun + (stockDO.getClose()-buyPoint)/buyPoint*toucun;
		toucun = Float.parseFloat(decimalFormat.format(toucun));
		account = toucun;
		toucun = 0;
		stockDO.getReport().setAccount(account+toucun);
		stockDO.getReport().setNotes(stockDO.getClose() + " 卖出 ");
		
	}



	@Override
	public void noBuyNoSale(StockDO stockDO) {
		float tmpToucun = toucun + (stockDO.getClose()-buyPoint)/buyPoint*toucun;
		tmpToucun = Float.parseFloat(decimalFormat.format(tmpToucun));
		stockDO.getReport().setAccount(tmpToucun);
		if(toucun > 0 ){
			stockDO.getReport().setNotes(" 持仓 ");
		}else{
			stockDO.getReport().setNotes(" 空仓 ");
		}
	}

}
