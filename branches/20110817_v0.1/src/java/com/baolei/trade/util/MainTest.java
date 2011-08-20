package com.baolei.trade.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baolei.trade.domain.StockDO;
import com.thoughtworks.xstream.XStream;

public class MainTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StockDO stock1 = new StockDO();
		stock1.setTime(new Date());
		stock1.setClose(1.5f);
		StockDO stock2 = new StockDO();
		stock2.setTime(new Date());
		stock2.setClose(1.5f);
		List list = new ArrayList();     
		list.add(stock1);   
		list.add(stock2);   
//		JSONArray ja = JSONArray.fromObject(list);   
//		System.out.println( ja.toString() );    
		// json-lib默认不支持java.sql.Date的序列化，要序列化自己的类，实现一个BeanProcessor处理即可
		System.out.println("将Java对象转换为xml！\n");
        XStream xstream = new XStream();
        xstream.alias("stock", StockDO.class);
        xstream.alias("stocks",List.class);

        String xml = xstream.toXML(list);
        System.out.println(xml);

	}

}
