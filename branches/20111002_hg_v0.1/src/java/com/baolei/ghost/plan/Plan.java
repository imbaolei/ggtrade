package com.baolei.ghost.plan;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baolei.ghost.AccountDO;
import com.baolei.ghost.DataPool;
import com.baolei.ghost.dal.dataobject.StockDO;

public class Plan {
	
	Float atr;
	
	Float justWin; 
	
	Float stopLoss;
	
	Float jcPoint;
	
	Float tradeNum;
	
	Float totalNum;
	
	Float buyPoint;
	
	Integer trades;
	
	Integer planTrades;
	
	String action;
	
	Float balance; //
	
	protected DataPool dataPool;
	
	Float percent;
	
	List<String> exitStatus = new ArrayList<String>();
	
	public Float getBalance() {
		return balance;
	}

	public void setBalance(Float balance) {
		this.balance = balance;
	}

	public Float getTradeNum() {
		return tradeNum;
	}

	public void setTradeNum(Float jcCount) {
		this.tradeNum = jcCount;
	}

	public Float getJcPoint() {
		return jcPoint;
	}

	public void setJcPoint(Float jcPoint) {
		this.jcPoint = jcPoint;
	}

	public Float getJustWin() {
		return justWin;
	}

	public void setJustWin(Float justWin) {
		this.justWin = justWin;
	}

	public Float getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(Float stopLoss) {
		this.stopLoss = stopLoss;
	}

	public Float getAtr() {
		return atr;
	}

	public void setAtr(Float atr) {
		this.atr = atr;
	}

	public Integer getTrades() {
		return trades;
	}

	public void setTrades(Integer jcNum) {
		this.trades = jcNum;
	}

	public Float getBuyPoint() {
		return buyPoint;
	}

	public void setBuyPoint(Float buyPoint) {
		this.buyPoint = buyPoint;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String status) {
		this.action = status;
	}

	public Integer getPlanTrades() {
		return planTrades;
	}

	public void setPlanTrades(Integer planTrades) {
		this.planTrades = planTrades;
	}
	
	/**
	 * @param stockDO
	 * @param accountDO
	 * @throws ParseException
	 */
	public void execute(StockDO stockDO,AccountDO accountDO) throws ParseException{}
	

	
	/**
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	public boolean canTrade(Date time) throws ParseException{
				return false;}

	public DataPool getDataPool() {
		return dataPool;
	}

	public void setDataPool(DataPool dataPool) {
		this.dataPool = dataPool;
	}

	public Float getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Float totalNum) {
		this.totalNum = totalNum;
	}

	public List<String> getExitStatus() {
		return exitStatus;
	}

	public void setExitStatus(List<String> exitStatus) {
		this.exitStatus = exitStatus;
	}

	public Float getPercent() {
		return percent;
	}

	public void setPercent(Float percent) {
		this.percent = percent;
	}
}
