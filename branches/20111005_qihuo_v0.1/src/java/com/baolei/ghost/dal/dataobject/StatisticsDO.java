package com.baolei.ghost.dal.dataobject;

import java.util.Date;

public class StatisticsDO {
	
	private int id;
	
	private String code;
	
	private String name;
	
	private String industry;
	
	private Date time;
	
	private float open;

	private float high;

	private float low;

	private float close;
	
	private float riseRate;  //涨幅
	
	private float changeRate; //振动幅度
	
	private int riseRank; //52周排名 涨幅强度
	
	private float resultsUp; //同比业绩
	
	private Date gmtCreate;

    private Date gmtModified;
    

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public float getOpen() {
		return open;
	}

	public void setOpen(float open) {
		this.open = open;
	}

	public float getHigh() {
		return high;
	}

	public void setHigh(float high) {
		this.high = high;
	}

	public float getLow() {
		return low;
	}

	public void setLow(float low) {
		this.low = low;
	}

	public float getClose() {
		return close;
	}

	public void setClose(float close) {
		this.close = close;
	}

	public float getRiseRate() {
		return riseRate;
	}

	public void setRiseRate(float riseRate) {
		this.riseRate = riseRate;
	}

	public float getChangeRate() {
		return changeRate;
	}

	public void setChangeRate(float changeRate) {
		this.changeRate = changeRate;
	}


	public int getRiseRank() {
		return riseRank;
	}

	public void setRiseRank(int riseRank) {
		this.riseRank = riseRank;
	}

	public float getResultsUp() {
		return resultsUp;
	}

	public void setResultsUp(float resultsUp) {
		this.resultsUp = resultsUp;
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
    
    
}
