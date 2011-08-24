package com.baolei.ghost.dal.dataobject;

import java.util.Date;

import net.sf.json.JSONObject;

import com.baolei.trade.test.Report;

public class StockDO {
	
	String code;
	
	int id;
	
	Date time;

	float open;

	float high;

	float low;

	float close;

	float vol;
	
	float bbi;
	
	String period;
	
	String ma;
	
	float atr;
	
	public float getAtr() {
		return atr;
	}

	public void setAtr(float atr) {
		this.atr = atr;
	}

	Report report = new Report();
	
	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public float getMa(String key){
		JSONObject json = JSONObject.fromObject(ma); 
		Object value = json.get(key);
		if(value != null){
			return Float.parseFloat(value.toString());
		}
		return 0;
	}
	
	public String getMa() {
		return ma;
	}

	public void setMa(String ma) {
		this.ma = ma;
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
