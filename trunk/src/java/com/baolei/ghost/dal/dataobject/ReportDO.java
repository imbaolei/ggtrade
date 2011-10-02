package com.baolei.ghost.dal.dataobject;

import java.util.Date;

public class ReportDO {
	
	private int id;
	
	private Float account; //资金账户
	
	private Float totalMoney; //总投入金额
	
	private String status; //状态 

	private String notes; //备注
	
	private Integer transCount; //交易次数
	
	private Float fee; //手续费
	
	private Float totalFee; //总手续费
	
	private Float shouyi; // 买入 卖出 两次交易 中的收益
	
	private boolean dingTouFlag = false;
	
	private Float price;
	
	private boolean jiacangFlag = false;
	
	private Date time;

	private Float shareHR;
	
	private Float percent;
	

	private Date gmtCreate;

	private Date gmtModified;
	
	private String code;
	
	
	private String type;
	
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Float getPrice() {
		return price;
	}

	public boolean getJiacangFlag() {
		return jiacangFlag;
	}

	public void setJiacangFlag(boolean jiacangFlag) {
		this.jiacangFlag = jiacangFlag;
	}

	public void setPrice(Float price) {
		this.price = price;
	}
	
	public Float getPercent() {
		return percent;
	}

	public void setPercent(Float percent) {
		this.percent = percent;
	}

	public Float getShareHR() {
		return shareHR;
	}

	public void setShareHR(Float shareHR) {
		this.shareHR = shareHR;
	}

	public boolean getDingTouFlag() {
		return dingTouFlag;
	}

	public void setDingTouFlag(boolean dingTou) {
		this.dingTouFlag = dingTou;
	}

	public Float getShouyi() {
		return shouyi;
	}

	public void setShouyi(Float shouyi) {
		this.shouyi = shouyi;
	}

	public Float getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(Float totalMoney) {
		this.totalMoney = totalMoney;
	}

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

}
