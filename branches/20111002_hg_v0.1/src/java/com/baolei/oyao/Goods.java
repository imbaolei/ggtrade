package com.baolei.oyao;

public class Goods {
	String no;
	Float price = 0f; //零售价
	Float num = 0f;
	Float chuchang = 0f; //出厂价
	String type; //衣服类型  比如夹克、棉衣
	String jijie;//季节
	
	public String getJijie() {
		return jijie;
	}
	public void setJijie(String jijie) {
		this.jijie = jijie;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	
	public Float getNum() {
		return num;
	}
	public void setNum(Float num) {
		this.num = num;
	}

}
