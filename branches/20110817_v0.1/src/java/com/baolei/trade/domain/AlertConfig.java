package com.baolei.trade.domain;

public class AlertConfig {
	String type;
	float price;
	int alertCount;
	String msg;
	public int getAlertCount() {
		return alertCount;
	}

	public void setAlertCount(int alertCount) {
		this.alertCount = alertCount;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}



}
