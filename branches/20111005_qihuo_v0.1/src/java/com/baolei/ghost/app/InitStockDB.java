package com.baolei.ghost.app;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.baolei.ghost.DataPool;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.daointerface.StockDAO;

public class InitStockDB {

	private static String filePath = "c:/tmp/SZ159901.TXT";
	
	public static void main(String[] args) throws IOException, ParseException {
		System.out.println(new Date());
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		StockDAO stockDAO = (StockDAO) context.getBean("stockDAO");
		DataPool dataPool = new DataPool();
		StockUtil.initDayStockMap(filePath, dataPool.getDayStockMap(), dataPool
				.getDayStockList());
		StockUtil.initWeekStockMap(dataPool.getDayStockMap(), dataPool
				.getWeekStockMap(), dataPool.getWeekStockList());
		StockUtil.initMonthStockMap(dataPool.getDayStockMap(), dataPool
				.getMonthStockMap(), dataPool.getMonthStockList());
		
		
		StockUtil.initBbiMap(dataPool.getDayStockMap(), dataPool
				.getDayStockList());
		StockUtil.initBbiMap(dataPool.getWeekStockMap(), dataPool
				.getWeekStockList());
		StockUtil.initBbiMap(dataPool.getMonthStockMap(), dataPool
				.getMonthStockList());
		
		File read = new File(filePath);
		String code = read.getName().split(".TXT")[0];;
		stockDAO.deleteStockByCode(code);
		System.out.println(new Date());

		stockDAO.insertStocks(dataPool.getDayStockList());
		stockDAO.insertStocks(dataPool.getWeekStockList());
		stockDAO.insertStocks(dataPool.getMonthStockList());
		System.out.println(new Date());
	}

}
