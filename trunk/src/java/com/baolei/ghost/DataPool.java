package com.baolei.ghost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.baolei.ghost.dal.dataobject.StockDO;

public class DataPool {
	
	private List<StockDO> dayStockList = new ArrayList<StockDO>();

	private List<StockDO> weekStockList = new ArrayList<StockDO>();

	private List<StockDO> monthStockList = new ArrayList<StockDO>();
	
	private Map<String, StockDO> dayStockMap = new TreeMap<String, StockDO>();

	private Map<String, StockDO> weekStockMap = new TreeMap<String, StockDO>();
	
	private Map<String, StockDO> monthStockMap = new TreeMap<String, StockDO>();

	public List<StockDO> getDayStockList() {
		return dayStockList;
	}

	public void setDayStockList(List<StockDO> dayStockList) {
		this.dayStockList = dayStockList;
	}

	public Map<String, StockDO> getDayStockMap() {
		return dayStockMap;
	}

	public void setDayStockMap(Map<String, StockDO> dayStockMap) {
		this.dayStockMap = dayStockMap;
	}

	public List<StockDO> getWeekStockList() {
		return weekStockList;
	}

	public void setWeekStockList(List<StockDO> weekStockList) {
		this.weekStockList = weekStockList;
	}

	public Map<String, StockDO> getWeekStockMap() {
		return weekStockMap;
	}

	public void setWeekStockMap(Map<String, StockDO> weekStockMap) {
		this.weekStockMap = weekStockMap;
	}

	public List<StockDO> getMonthStockList() {
		return monthStockList;
	}

	public void setMonthStockList(List<StockDO> monthStockList) {
		this.monthStockList = monthStockList;
	}

	public Map<String, StockDO> getMonthStockMap() {
		return monthStockMap;
	}

	public void setMonthStockMap(Map<String, StockDO> monthStockMap) {
		this.monthStockMap = monthStockMap;
	}
	
	
	

}
