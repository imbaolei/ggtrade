package com.baolei.ghost.app2;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.daointerface.ReportDAO;
import com.baolei.trade.bo.ReportBO;
import com.baolei.trade.bo.ReportStatsBO;


public class InitDB {
	
	protected Log log = LogFactory.getLog(getClass());
	
	private String filePath = "D:/java/project/data/tdx";
	
	int threadNum = 20;
	
	ApplicationContext context = new ClassPathXmlApplicationContext(
			"beans.xml");
	
	public static void main(String[] args)  {
		InitDB isd2 = new InitDB();
		isd2.initTradeReport();
		isd2.initTradeReprotStats();
	}

	public void initTradeReprotStats(){
		ReportDAO reportDAO = (ReportDAO) context.getBean("reportDAO");
		Map param = new HashMap();
		List<String> codes = reportDAO.seleteCodesFromReport(param);
		ExecutorService pool = Executors.newFixedThreadPool(threadNum); 
		log.info(codes.size());
		for (int i  = 0 ; i < codes.size() ; i++) {
			String code = codes.get(i);
			Thread t = new ReportStatsThread(code,i);
			pool.execute(t);
		}
		pool.shutdown();
	}
	
	public void initTradeReport(){
		List<String> codes = getAllCodes();
		ExecutorService pool = Executors.newFixedThreadPool(threadNum); 
		log.info(codes.size());
		for (int i  = 0 ; i < codes.size() ; i++) {
			String code = codes.get(i);
			Thread t = new TradeExecuteThread(code,i);
			pool.execute(t);
		}
		pool.shutdown();
	}
	
	public void initStockName(){
		List<String> codes = getAllCodes();
		DataParser dataParser = (DataParser) context.getBean("txdFileParser");
		StockUtil stockUtil = (StockUtil) context.getBean("stockUtil");
		Properties props = stockUtil.getCodeProperties();
		for (int i  = 0 ; i < codes.size() ; i++) {
			String code = codes.get(i);
			log.info("start - " + i + " - : execute " + code + " ...");
			String codeName = dataParser.getStockName(code);
			props.put(code, codeName);
		}
		stockUtil.saveCodeProperties(props);
	}
	
	
	
	public List<String> getAllCodes() {
		File file = new File(filePath);
		List<String> fileList = new ArrayList<String>();
		String[] files = file.list();
		System.out.println(files.length);
		for (String filename : files) {
			filename = filename.replace(".TXT", "");
			fileList.add(filename);
		}
		return fileList;
	}
	
	class ReportStatsThread extends Thread {
		private String code;
		private Integer num;
		protected Log log = LogFactory.getLog(getClass());
		
		public ReportStatsThread(String code,Integer num){
			this.code = code;
			this.num = num;
		}
		
		public void run() {
			log.info("start - " + num + " - : execute " + code + " ...");
			ReportStatsBO reportStatsBO = (ReportStatsBO) context.getBean("reportStatsBO");
			reportStatsBO.initReportStats(code);
			log.info("end - " + num + " - : execute " + code + " ...");
		}
	}
	
	
	class TradeExecuteThread extends Thread {
		private String code;
		private Integer num;
		protected Log log = LogFactory.getLog(getClass());
		
		public TradeExecuteThread(String code,Integer num){
			this.code = code;
			this.num = num;
		}
		
		public void run() {
			log.info("start - " + num + " - : execute " + code + " ...");
			ReportBO reportBO = (ReportBO) context.getBean("reportBO");
			reportBO.executeTrade(code);
			log.info("end - " + num + " - : execute " + code + " ...");
		}
	}
}


