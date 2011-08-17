package com.baolei.trade.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockDO {
	
	String code;
	
	String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	int id;
	
	Date time;

	float open;

	float high;

	float low;

	float close;

	float vol;
	
	float bbi;
	
	String period; //周期
	
	float upAlert;
	
	List<AlertConfig> alertConfigs = new ArrayList<AlertConfig>();
	
	public List<AlertConfig> getAlertConfigs() {
		return alertConfigs;
	}

	public void setAlertConfigs(List<AlertConfig> alertConfigs) {
		this.alertConfigs = alertConfigs;
	}

	public float getUpAlert() {
		return upAlert;
	}

	public void setUpAlert(float upAlert) {
		this.upAlert = upAlert;
	}

	private Date gmtCreate;

    private Date gmtModified;

	public float getBbi() {
		return bbi;
	}

	public void setBbi(float bbi) {
		this.bbi = bbi;
	}

	public float getClose() {
		return close;
	}

	public void setClose(float close) {
		this.close = close;
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

	public float getOpen() {
		return open;
	}

	public void setOpen(float open) {
		this.open = open;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public float getVol() {
		return vol;
	}

	public void setVol(float vol) {
		this.vol = vol;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}

