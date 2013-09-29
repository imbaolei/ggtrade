package com.baolei.ghost.app;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.baolei.ghost.DataPool;
import com.baolei.ghost.common.PriceUtil;
import com.baolei.ghost.dal.daointerface.PriceDAO;

public class InitStockDB {

	private static String filePath = "c:/tmp/SZ159901.TXT";
	
	public static void main(String[] args) throws IOException, ParseException {
		System.out.println(new Date());
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		PriceDAO stockDAO = (PriceDAO) context.getBean("stockDAO");
		DataPool dataPool = new DataPool();
		PriceUtil.initDayStockMap(filePath, dataPool.getDayStockMap(), dataPool
				.getDayStockList());
		PriceUtil.initWeekStockMap(dataPool.getDayStockMap(), dataPool
				.getWeekStockMap(), dataPool.getWeekStockList());
		PriceUtil.initMonthStockMap(dataPool.getDayStockMap(), dataPool
				.getMonthStockMap(), dataPool.getMonthStockList());
		
		
		PriceUtil.initBbiMap(dataPool.getDayStockMap(), dataPool
				.getDayStockList());
		PriceUtil.initBbiMap(dataPool.getWeekStockMap(), dataPool
				.getWeekStockList());
		PriceUtil.initBbiMap(dataPool.getMonthStockMap(), dataPool
				.getMonthStockList());
		
		File read = new File(filePath);
		String code = read.getName().split(".TXT")[0];;
		stockDAO.deletePriceByCode(code);
		System.out.println(new Date());

		stockDAO.insertPrices(dataPool.getDayStockList());
		stockDAO.insertPrices(dataPool.getWeekStockList());
		stockDAO.insertPrices(dataPool.getMonthStockList());
		System.out.println(new Date());
	}

}
