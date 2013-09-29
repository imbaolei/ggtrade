package com.baolei.ghost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.baolei.ghost.dal.dataobject.PriceDO;

public class DataPool {
	
	private List<PriceDO> dayStockList = new ArrayList<PriceDO>();

	private List<PriceDO> weekStockList = new ArrayList<PriceDO>();

	private List<PriceDO> monthStockList = new ArrayList<PriceDO>();
	
	private Map<String, PriceDO> dayStockMap = new TreeMap<String, PriceDO>();

	private Map<String, PriceDO> weekStockMap = new TreeMap<String, PriceDO>();
	
	private Map<String, PriceDO> monthStockMap = new TreeMap<String, PriceDO>();

	public List<PriceDO> getDayStockList() {
		return dayStockList;
	}

	public void setDayStockList(List<PriceDO> dayStockList) {
		this.dayStockList = dayStockList;
	}

	public Map<String, PriceDO> getDayStockMap() {
		return dayStockMap;
	}

	public void setDayStockMap(Map<String, PriceDO> dayStockMap) {
		this.dayStockMap = dayStockMap;
	}

	public List<PriceDO> getWeekStockList() {
		return weekStockList;
	}

	public void setWeekStockList(List<PriceDO> weekStockList) {
		this.weekStockList = weekStockList;
	}

	public Map<String, PriceDO> getWeekStockMap() {
		return weekStockMap;
	}

	public void setWeekStockMap(Map<String, PriceDO> weekStockMap) {
		this.weekStockMap = weekStockMap;
	}

	public List<PriceDO> getMonthStockList() {
		return monthStockList;
	}

	public void setMonthStockList(List<PriceDO> monthStockList) {
		this.monthStockList = monthStockList;
	}

	public Map<String, PriceDO> getMonthStockMap() {
		return monthStockMap;
	}

	public void setMonthStockMap(Map<String, PriceDO> monthStockMap) {
		this.monthStockMap = monthStockMap;
	}
	
	
	

}
