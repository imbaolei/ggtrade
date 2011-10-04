package com.baolei.trade.util;

import java.util.List;

import com.baolei.trade.domain.StockDO;



public class QQStockHelper {
	public String getQqStockUrl(List<StockDO> stocks){
		String hkUrl = "http://qt.gtimg.cn/q=r_"; //HK
		String shUrl = "http://qt.gtimg.cn/q=";
		for (StockDO stock : stocks){
			hkUrl= hkUrl+stock.getCode()+",";
		}
		return hkUrl;
	}
	
	public List<StockDO> qqStockPaser(String text,List<StockDO> stocks){
		String[] texts = text.split("\n");
		for(String t :texts){
			String[] ts = t.split("~");
			String[] codes = ts[0].split("=")[0].split("_");
			String code = codes[codes.length-1];
			float close = Float.parseFloat(ts[3]);
			String name = ts[1];
			for(StockDO stockDO : stocks){
				if(stockDO.getCode().equalsIgnoreCase(code)){
					stockDO.setClose(close);
					stockDO.setName(name);
				}
			}
		}
		return stocks;
	}
}
