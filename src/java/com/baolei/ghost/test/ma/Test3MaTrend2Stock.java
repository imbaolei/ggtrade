package com.baolei.ghost.test.ma;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.ghost.test.Report;
import com.baolei.ghost.test.Test2Stock;

public class Test3MaTrend2Stock extends Test2Stock{
	
	protected float account;
	protected float toucunLR; //LowRisk低风险头寸
	protected float toucunHR; //HighRisk高风险头寸
	protected float buyPoint;
	protected Integer p1 ;
	protected Integer p2 ;
	protected Integer p3 ;
	protected Report report;
	protected float rate = 0.0135f; 
	protected int transCount  = 0;
	protected float totalFee = 0;
	
	
	public Test3MaTrend2Stock(float account,Integer p1,Integer p2,Integer p3){
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
	public boolean needBuy(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString); 
		//如果判断没有头寸 而且 趋势不是走弱，即走强
		if((toucunHR == 0) && !trendout(stockDO)){
			return true;
		}
		
		return false;
	}

	
	
	@Override
	public void buy(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		float fee = fee(account);
		toucunHR = account - fee;
		account = 0;
		buyPoint = stockDO.getClose();
		
		stockDO.getReport().setAccount(account+toucunHR);
		stockDO.getReport().setNotes(" - 买点 ： " + buyPoint);
		stockDO.getReport().setFee(fee);
		totalFee = totalFee + fee;
		stockDO.getReport().setTotalFee(totalFee);
		stockDO.getReport().setStatus(Constant.REPORT_STATUS_BUY);
		transCount = transCount + 1;
		stockDO.getReport().setTransCount(transCount);
	}

	@Override
	public boolean needSale(String dateString) {
		StockDO stockDO = pdStockMap.get(dateString);
		if((toucunHR > 0) && trendout(stockDO)){
			return true;
		}
		return false;
	}

	@Override
	public void sale(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		float fee = fee(toucunHR);
		toucunHR = toucunHR + (stockDO.getClose()-buyPoint)/buyPoint*toucunHR - fee;
		toucunHR = Float.parseFloat(decimalFormat.format(toucunHR));
		account = toucunHR;
		toucunHR = 0;
		stockDO.getReport().setAccount(account+toucunHR);
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
	public void noBuyNoSale(String dateString) {
		StockDO stockDO = jyStockMap.get(dateString);
		if(buyPoint == 0){
			stockDO.getReport().setAccount(account+toucunHR);
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_NOSTART);
			return;
		}
		float tmpToucun = toucunHR + (stockDO.getClose()-buyPoint)/buyPoint*toucunHR + account;
		tmpToucun = Float.parseFloat(decimalFormat.format(tmpToucun));
		stockDO.getReport().setAccount(tmpToucun);
		if(toucunHR > 0 ){
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_CHIYOU);
		}else{
			stockDO.getReport().setStatus(Constant.REPORT_STATUS_KONGCANG);
		}
	}

}
