package com.baolei.trade.app;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.baolei.ghost.dal.daointerface.StatisticsDAO;
import com.baolei.ghost.dal.daointerface.StockDAO;
import com.baolei.ghost.dal.dataobject.StatisticsDO;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.data.StockDataParser;

public class InitDB {
	
	private String statisticsPath = "D:/tmp/stock/week/";

	protected Log log = LogFactory.getLog(getClass());

	ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

	public static void main(String[] args) {
		InitDB idb = new InitDB();
//		idb.initStocksToDB();
		//idb.initStatisticsListByFile("2013-09-27");
		idb.initAllStatisticsToDB();
	}

	public void initStocksToDB() {
		StockDAO stockDAO = (StockDAO) context.getBean("stockDAO");
		StockDataParser dataParser = (StockDataParser) context
				.getBean("tdxStockDataParser");
		List<StockDO> newStockList = dataParser.getStockList();
		List<StockDO> dbStockList = stockDAO.selectAllStock();
		Map<String, StockDO> dbStockMap = new HashMap<String, StockDO>();
		for (StockDO stockDO : dbStockList) {
			dbStockMap.put(stockDO.getCode(), stockDO);
		}
		List<StockDO> newInsertList = new ArrayList<StockDO>();
		List<StockDO> newUpdateList = new ArrayList<StockDO>();
		for (StockDO newStock : newStockList) {
			if (dbStockMap.containsKey(newStock.getCode())) {
				StockDO dbStock = dbStockMap.get(newStock.getCode());
				newStock.setId(dbStock.getId());
				newUpdateList.add(newStock);
			} else {
				newInsertList.add(newStock);
			}
		}
		stockDAO.insertStocks(newInsertList);
		stockDAO.updateStocksByIdBatch(newUpdateList);
	}

	public void initAllStatisticsToDB() {
			List<String> fileNameList = new ArrayList<String>();
			File root = new File(statisticsPath);
			String[] fileList = root.list();
			for (int i = 0; i < fileList.length; i++) {
				fileNameList.add(fileList[i].replaceAll(".xls", ""));
			}
			
			for(int i = 0 ; i < fileNameList.size();i++){
				initStatisticsListByFile(fileNameList.get(i));
			}
	}

	public void initStatisticsListByFile(String fileName) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date time = dateFormat.parse(fileName);
			StatisticsDAO statisticsDAO = (StatisticsDAO) context.getBean("statisticsDAO");
			StockDataParser dataParser = (StockDataParser) context
					.getBean("tdxStockDataParser");
			String file = statisticsPath+fileName+".xls";
			List<StatisticsDO> statsList =  dataParser.getStatisticsListByFile(file,time);
			statisticsDAO.deleteStatisticsByDate(time);
			StockDAO stockDAO = (StockDAO) context.getBean("stockDAO");
			List<StockDO> dbStockList = stockDAO.selectAllStock();
			Map<String, StockDO> dbStockMap = new HashMap<String, StockDO>();
			for (StockDO stockDO : dbStockList) {
				dbStockMap.put(stockDO.getCode(), stockDO);
			}
			List<StatisticsDO> statsInsertList = new ArrayList<StatisticsDO>();
			for (StatisticsDO stats : statsList) {
					StockDO stockDO = dbStockMap.get(stats.getCode());
					stats.setIndustry(stockDO.getIndustry());
					statsInsertList.add(stats);
			}
			statisticsDAO.insertStatisticses(statsInsertList);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		

	}

}
