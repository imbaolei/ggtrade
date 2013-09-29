package com.baolei.trade.alert;

import java.util.List;

import com.baolei.trade.alert.domian.AlertStockDO;



public class QQStockHelper {
	public String getQqStockUrl(List<AlertStockDO> stocks){
		String hkUrl = "http://qt.gtimg.cn/q=r_"; //HK
		String shUrl = "http://qt.gtimg.cn/q=";
		for (AlertStockDO stock : stocks){
			hkUrl= hkUrl+stock.getCode()+",";
		}
		return hkUrl;
	}
	
	public List<AlertStockDO> qqStockPaser(String text,List<AlertStockDO> stocks){
		String[] texts = text.split("\n");
		for(String t :texts){
			String[] ts = t.split("~");
			String[] codes = ts[0].split("=")[0].split("_");
			String code = codes[codes.length-1];
			float close = Float.parseFloat(ts[3]);
			String name = ts[1];
			for(AlertStockDO stockDO : stocks){
				if(stockDO.getCode().equalsIgnoreCase(code)){
					stockDO.setClose(close);
					stockDO.setName(name);
				}
			}
		}
		return stocks;
	}
}
