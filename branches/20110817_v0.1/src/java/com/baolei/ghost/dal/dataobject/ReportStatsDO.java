package com.baolei.ghost.dal.dataobject;

import java.util.Date;

public class ReportStatsDO {
	
	private int id;
	
	private String code;
	
	private int winNum;
	
	private float winPercent;
	
	private int lossNum;
	
	private float lossPercent;
	
	private int totalNum;
	
	private float totalPercent;
	
	private float averagePercent;
	
	private Date lastTradeTime;
	
	private float lastTradePrice;
	
	private float lastPercent;
	
	private Date gmtCreate;

	private Date gmtModified;
	
	private float maxWinPercent;
	
	private float maxLossPercent;

	public float getMaxWinPercent() {
		return maxWinPercent;
	}

	public void setMaxWinPercent(float maxWinPercent) {
		this.maxWinPercent = maxWinPercent;
	}

	public float getMaxLossPercent() {
		return maxLossPercent;
	}

	public void setMaxLossPercent(float maxLossPercent) {
		this.maxLossPercent = maxLossPercent;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getWinNum() {
		return winNum;
	}

	public void setWinNum(int winNum) {
		this.winNum = winNum;
	}

	public float getWinPercent() {
		return winPercent;
	}

	public void setWinPercent(float winPercent) {
		this.winPercent = winPercent;
	}

	public int getLossNum() {
		return lossNum;
	}

	public void setLossNum(int lossNum) {
		this.lossNum = lossNum;
	}

	public float getLossPercent() {
		return lossPercent;
	}

	public void setLossPercent(float lossPercent) {
		this.lossPercent = lossPercent;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public float getTotalPercent() {
		return totalPercent;
	}

	public void setTotalPercent(float totalPercent) {
		this.totalPercent = totalPercent;
	}

	public float getAveragePercent() {
		return averagePercent;
	}

	public void setAveragePercent(float averagePercent) {
		this.averagePercent = averagePercent;
	}

	public Date getLastTradeTime() {
		return lastTradeTime;
	}

	public void setLastTradeTime(Date lastTradeTime) {
		this.lastTradeTime = lastTradeTime;
	}

	public float getLastTradePrice() {
		return lastTradePrice;
	}

	public void setLastTradePrice(float lastTradePrice) {
		this.lastTradePrice = lastTradePrice;
	}

	public float getLastPercent() {
		return lastPercent;
	}

	public void setLastPercent(float lastPercent) {
		this.lastPercent = lastPercent;
	}
	
	

}
