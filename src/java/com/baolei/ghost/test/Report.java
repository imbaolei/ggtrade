package com.baolei.ghost.test;

public class Report {
	
	private Float account; //资金账户
	
	public Float getAccount() {
		return account;
	}

	public void setAccount(Float account) {
		this.account = account;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Integer getTransCount() {
		return transCount;
	}

	public void setTransCount(Integer transCount) {
		this.transCount = transCount;
	}

	public Float getFee() {
		return fee;
	}

	public void setFee(Float fee) {
		this.fee = fee;
	}

	public Float getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(Float totalFee) {
		this.totalFee = totalFee;
	}

	private String status; //状态 

	private String notes; //备注
	
	private Integer transCount; //交易次数
	
	private Float fee; //手续费
	
	private Float totalFee; //总手续费

	

}