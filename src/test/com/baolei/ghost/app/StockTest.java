package com.baolei.ghost.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.baolei.common.AbstractTestCase;
import com.baolei.ghost.app2.DataParser;
import com.baolei.ghost.app2.TxdFileParser;
import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.PriceUtil;
import com.baolei.ghost.dal.daointerface.ReportDAO;
import com.baolei.ghost.dal.daointerface.PriceDAO;
import com.baolei.ghost.dal.dataobject.ReportDO;
import com.baolei.ghost.dal.dataobject.PriceDO;
import com.baolei.trade.bo.ReportBO;
import com.baolei.trade.bo.PriceBO;
import com.baolei.trade.bo.TradeBO;
import com.baolei.trade.test.trend.ma.Ma3Tc;
import com.baolei.trade.test.trend.ma.filter.Ma200Hg55LLVTcAtr;
import com.baolei.trade.test.trend.ma.filter.Ma2Hg55LLVTcAtr;
import com.baolei.trade.test.trend.ma.filter.Ma3LLVStopTc;
import com.baolei.trade.test.trend.ma.filter.Ma3LLVTc;
import com.baolei.trade.test.trend.ma.filter.Ma3LLVTcAtr;
import com.baolei.trade.test.trend.ma.filter.Ma3UpLLVTc;

/**
 * Unit test for simple App.
 */
public class StockTest extends AbstractTestCase {

	 private String code = "SZ000001";
//	private String code = "SH600000";
	float account = 100000;
	float moneyPeriod = 1000;
	float accountDingTou = 1000;
	Integer p1 = 20;
	Integer p2 = 60;
	Integer p3 = 90;

	protected Log log = LogFactory.getLog(getClass());
	DateFormat dateFormat = new SimpleDateFormat(PriceUtil.dateFormatString);
//	private String filePath = "D:/java/project/data/";
	@Autowired
	private TradeBO tradeBO;

	@Autowired
	private PriceBO stockBO;
	
	@Autowired
	private PriceDAO stockDAO;

	@Autowired
	private ReportDAO reportDAO;
	
	@Autowired
	private ReportBO reportBO;

	public void testGetStockListByFile() {
		List<PriceDO> stockList = tradeBO.getStockListByFile(code);
		stockList = stockBO.initPriceListMa(stockList, "");
		stockList = stockBO.initPriceListAtr(stockList, 0);
		for (PriceDO stockDO : stockList) {
			log.info(dateFormat.format(stockDO.getTime()));
			log.info(stockDO.getMa());
			log.info(stockDO.getAtr());
		}
	}

	public void testMa3Tc() {
		List<PriceDO> stockList = getInitStockList(code);
		Ma3Tc test = new Ma3Tc();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}

	
	public void testMa3LLVTc() {
		List<PriceDO> stockList = getInitStockList(code);
		Ma3Tc test = new Ma3LLVTc();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}

	
	public void testMa3UpLLVTc() {
		List<PriceDO> stockList = getInitStockList(code);
		Ma3Tc test = new Ma3UpLLVTc();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}


	public void testMa3LLVStopTc() {
		List<PriceDO> stockList = getInitStockList(code);
		Ma3Tc test = new Ma3LLVStopTc();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}

	
	public void testMa3LLVTcAtr() {
		List<PriceDO> stockList = getInitStockList(code);
		Ma3Tc test = new Ma3LLVTcAtr();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	@Test
	public void testMa200Hg55LLVTcAtr() {
		List<PriceDO> stockList = getInitStockList(code);
		Ma3Tc test = new Ma200Hg55LLVTcAtr();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	
	public void testMa2Hg55LLVTcAtr() {
		List<PriceDO> stockList = getInitStockList(code);
		Ma3Tc test = new Ma2Hg55LLVTcAtr();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}

	private List<PriceDO> getInitStockList(String code) {
		List<PriceDO> stockList = tradeBO.getStockListByFile(code);
		stockList = stockBO.initPriceListMa(stockList, "");
		stockList = stockBO.initPriceListAtr(stockList, 0);
		return stockList;
	}

//	public List<String> getAllCodes() {
//		File file = new File(filePath);
//		List<String> fileList = new ArrayList<String>();
//		String[] files = file.list();
//		System.out.println(files.length);
//		for (String filename : files) {
//			filename = filename.replace(".TXT", "");
//			fileList.add(filename);
//		}
//		return fileList;
//	}

	
//	public void testMa3TcToTradeReport() {
//		List<String> codes = getAllCodes();
//		for (String code : codes) {
//			log.info("start execute " + code + " ...");
//			executeTrade(code);
//			log.info("end execute " + code + " ...");
//		}
//	}

	public void executeTrade(String code) {
		List<PriceDO> stockList = getInitStockList(code);
		Ma3Tc test = new Ma3LLVTc();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		List<ReportDO> reportList = reportBO.getAllTradeReport(stockList);
		// test.printReport();
		reportDAO.deleteReportByCode(code);
		reportDAO.insertReports(reportList);
	}

	
	
	public void testMa(){
		DataParser sdp = new TxdFileParser();
		log.info("start parse "  + new Date());
		List<PriceDO> stockDOList =  sdp.parse(code);
//		stockDAO.deleteStockByCode(code);
		log.info("start insert "  + new Date());
//		stockDAO.insertStocks(stockDOList);
		log.info("end parse "  + new Date());
		stockDOList = stockDAO.selectPriceByCodeAndPeriod(code, Constant.STOCK_PERIOD_DAY);
		stockDOList = tradeBO.maManage(stockDOList,"20,30,60,90,120");
//		stockDAO.updateStocksByIdBatch(stockDOList);
		log.info("end ma "  + new Date());
	}

	

}
