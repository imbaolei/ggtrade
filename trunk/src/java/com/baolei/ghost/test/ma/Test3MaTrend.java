package com.baolei.ghost.test.ma;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.ghost.test.Report;
import com.baolei.ghost.test.Test;

public class Test3MaTrend extends Test{
	
	protected float account;
	protected float toucun;
	protected float buyPoint;
	protected Integer p1 ;
	protected Integer p2 ;
	protected Integer p3 ;
	protected Report report;
	protected float rate = 0.0135f; 
	protected int transCount  = 0;
	protected float totalFee = 0;
	
	
	public Test3MaTrend(float account,Integer p1,Integer p2,Integer p3){
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
		if((ma1 ==0) || (ma2 == 0) || (ma3 ==0)){
			return true;
		}
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
		float fee = fee(account);
		toucun = account - fee;
		account = 0;
		buyPoint = stockDO.getClose();
		
		stockDO.getReport().setAccount(account+toucun);
		stockDO.getReport().setNotes(" - 买点 ： " + buyPoint);
		stockDO.getReport().setFee(fee);
		totalFee = totalFee + fee;
		stockDO.getReport().setTotalFee(totalFee);
		stockDO.getReport().setStatus(Constant.REPORT_STATUS_BUY);
		transCount = transCount + 1;
		stockDO.getReport().setTransCount(transCount);
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
		float fee = fee(toucun);
		toucun = toucun + (stockDO.getClose()-buyPoint)/buyPoint*toucun - fee;
		toucun = Float.parseFloat(decimalFormat.format(toucun));
		account = toucun;
		toucun = 0;
		stockDO.getReport().setAccount(account+toucun);
		stockDO.getReport().setNotes(" - 卖点 ： " +  stockDO.getClose() );
		stockDO.getReport().setFee(fee);
		totalFee = totalFee + fee;
		stockDO.getReport().setTotalFee(totalFee);
		stockDO.getReport().setStatus(Constant.REPORT_STATUS_SALE);
	}
	
	public float fee(float money){
		float fee = money*rate;
		fee = Float.parseFloat(decimalFormat.format(fee));
		return fee;
	}



	@Override
	public void noBuyNoSale(StockDO stockDO) {
		if(buyPoint == 0){
			stockDO.getReport().setAccount(account+toucun);
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_NOSTART);
			return;
		}
		float tmpToucun = toucun + (stockDO.getClose()-buyPoint)/buyPoint*toucun + account;
		tmpToucun = Float.parseFloat(decimalFormat.format(tmpToucun));
		stockDO.getReport().setAccount(tmpToucun);
		if(toucun > 0 ){
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_CHIYOU);
		}else{
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_KONGCANG);
		}
	}

}
