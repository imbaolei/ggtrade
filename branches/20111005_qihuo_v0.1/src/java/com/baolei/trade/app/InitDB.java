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
	
	private String statisticsPath = "D:/tmp/stock/week/all/";

	protected Log log = LogFactory.getLog(getClass());

	ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

	public static void main(String[] args) {
		InitDB idb = new InitDB();
//		idb.initStocksToDB();
//		idb.initStatisticsListByFile("2006-01-27");
//		idb.initAllStatisticsToDB();
		idb.initGuanzhuStocks();
	}

	/**
	 * 根据某个文件更新 stock表
	 */
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

	/**
	 * 根据某个文件夹下的所有文件 初始化 统计表 
	 */
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

	/**
	 * 根据 文件 初始化 统计表 
	 * @param fileName
	 */
	public void initStatisticsListByFile(String fileName) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date time = dateFormat.parse(fileName);
			StatisticsDAO statisticsDAO = (StatisticsDAO) context.getBean("statisticsDAO");
			StockDataParser dataParser = (StockDataParser) context
					.getBean("tdxStockDataParser");
			String file = statisticsPath+fileName+".xls";
			System.out.println(file + " start parse !");
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
			System.out.println(file + " over parse !");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 因为某些股票 只是暂时性的强势，所以计划统计 10周内超过5次强度70以上，则视为关注股
	 * 0:代表 不需要关注
	 * 1:代表第一次进入关注
	 * 2:代表不是第一次进入关注
	 * @param chongxin 是否需要重新计算
	 */
	public void initGuanzhuStocks(){
		String startTime = "2013-1-1";
		String endTime = "2013-2-27";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			StatisticsDAO statisticsDAO = (StatisticsDAO) context.getBean("statisticsDAO");
			Map param = new HashMap();
			param.put("start", dateFormat.parse(startTime));
			param.put("end", dateFormat.parse(endTime));
			List<Date> dateList = statisticsDAO.selectDatesFromStatistics(param);
			for(int i = 0; i < dateList.size(); i++){
				Date date = dateList.get(i);
				List<StatisticsDO> sdList = statisticsDAO.selectStatisticsByTime(date);
				List<StatisticsDO> sdListNew =  new ArrayList<StatisticsDO>();
				for(StatisticsDO sd: sdList){
					StatisticsDO sdNew = new StatisticsDO();
					Map param2 = new HashMap();
					param2.put("code", sd.getCode());
					param2.put("order", "time");
					List<StatisticsDO> sdList2 = statisticsDAO.selectStatistics(param2); 
					int guanzhu = panduanGuanzhu(sdList2);
					
					//和前一个统计数据比较，如果前一个数据是0;则是第一次出现
					if(i > 1 && guanzhu == 2){
						Date preDate = dateList.get(i-1);
						Map param3 = new HashMap();
						param3.put("code", sd.getCode());
						param3.put("time", preDate);
						List<StatisticsDO> tmpList = statisticsDAO.selectStatistics(param3);
						if(tmpList.size() > 0 ){
							StatisticsDO preSD = tmpList.get(0);
							if(preSD.getGuanzhu() == 0 ){
								guanzhu = 1;
							}
							
						}
					}
					
					//设置新的sd的关注数值
					sdNew.setGuanzhu(guanzhu);
					sdNew.setId(sd.getId());
					sdListNew.add(sdNew);
					
				}
				
				statisticsDAO.updateStatisticsByIdBatch(sdListNew);
			}
		
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	private int panduanGuanzhu(List<StatisticsDO> sdList){
		int zhouqi = 10; //计算10个 Statistics
		int count = 0; //个数
		int menkan = 70;//统计强度70以上的
		for(int i = 0 ; i< zhouqi;i++){
			StatisticsDO sd = sdList.get(i);
			if(sd.getRiseRank() >= menkan){
				count++;
			}
		}
		if(count >= (zhouqi/2)){
			return 2;
		}
		return 0;
	}
	
}
