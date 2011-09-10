package com.baolei.ghost.app;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.baolei.common.AbstractTestCase;
import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.daointerface.ReportDAO;
import com.baolei.ghost.dal.dataobject.ReportDO;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.bo.StockBO;
import com.baolei.trade.bo.TradeBO;
import com.baolei.trade.test.trend.kong.ma.filter.Ma3KongTcLLV;
import com.baolei.trade.test.trend.ma.Ma3Tc;
import com.baolei.trade.test.trend.ma.filter.Ma3LLVStopTc;
import com.baolei.trade.test.trend.ma.filter.Ma3LLVTc;
import com.baolei.trade.test.trend.ma.filter.Ma3LLVTcAtr;

/**
 * Unit test for simple App.
 */
public class AppTest extends AbstractTestCase {
	
//	private String code = "SZ399300";
	private String code = "SH600036";
	float account = 100000;
	float moneyPeriod = 1000;
	float accountDingTou = 1000;
	Integer p1 = 20;
	Integer p2 = 60;
	Integer p3 = 90;
	
	
	protected Log log = LogFactory.getLog(getClass());
	DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
	private String filePath = "D:/java/project/data/";
	@Autowired
	private TradeBO tradeBO;
	
	@Autowired
	private StockBO stockBO;
	
	@Autowired
	private ReportDAO reportDAO;
	
	
	public void testGetStockListByFile(){
		List<StockDO> stockList = tradeBO.getStockListByFile(code);
		stockList = stockBO.initStockListMa(stockList,"");
		stockList = stockBO.initStockListAtr(stockList,0);
		for(StockDO stockDO : stockList){
			log.info(dateFormat.format(stockDO.getTime()));
			log.info(stockDO.getMa());
			log.info(stockDO.getAtr());
		}
	}
	
	
	public void testMa3Tc(){
		List<StockDO> stockList = getInitStockList(code);
		Ma3Tc test = new Ma3Tc();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	public void testMa3LLVTc(){
		List<StockDO> stockList = getInitStockList(code);
		Ma3Tc test = new Ma3LLVTc();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	
	public void testMa3KongTcLLV(){
		List<StockDO> stockList = getInitStockList(code);
		Ma3Tc test = new Ma3KongTcLLV();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	public void testMa3LLVStopTc(){
		List<StockDO> stockList = getInitStockList(code);
		Ma3Tc test = new Ma3LLVStopTc();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	
	public void testMa3LLVTcAtr(){
		List<StockDO> stockList = getInitStockList(code);
		Ma3Tc test = new Ma3LLVTcAtr();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	private List<StockDO> getInitStockList(String code){
		List<StockDO> stockList = tradeBO.getStockListByFile(code);
		stockList = stockBO.initStockListMa(stockList,"");
		stockList = stockBO.initStockListAtr(stockList,0);
		return stockList;
	}
	
	
	public List<String> getAllCodes(){
		File file = new File(filePath);
		List<String> fileList = new ArrayList<String>();
		String[] files =  file.list();
		System.out.println(files.length);
		for(String filename : files){
			filename = filename.replace(".TXT", "" );
			fileList.add(filename);
		}
		return fileList;
	}
	
	@Test
	public void testMa3TcToTradeReport(){
		List<String> codes = getAllCodes();
		for(String code : codes){
			log.info("start execute " + code  + " ...");
			executeTrade(code);
			log.info("end execute " + code  + " ...");
		}
	}
	
	public void executeTrade(String code){
		List<StockDO> stockList = getInitStockList(code);
		Ma3Tc test = new Ma3LLVTc();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		List<ReportDO> reportList = getAllTradeReport(stockList);
//		test.printReport();
		reportDAO.deleteReportByCode(code);
		reportDAO.insertReports(reportList);
	}
	
	private List<ReportDO> getAllTradeReport(List<StockDO> stockList){
		List<ReportDO> reportList = new ArrayList<ReportDO>();
		for(StockDO stockDO : stockList){
			String status = stockDO.getReport()
					.getStatus();
			if (Constant.REPORT_STATUS_SALE.equals(status) || Constant.REPORT_STATUS_BUY.equals(status)) {
				ReportDO reportDO = stockDO.getReport();
				reportDO.setCode(code);
				reportList.add(reportDO);
			}
		}
		return reportList;
	}
	
	
	
	
}
