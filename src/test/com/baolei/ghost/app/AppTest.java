package com.baolei.ghost.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.baolei.common.AbstractTestCase;
import com.baolei.ghost.common.StockUtil;
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
	
	@Autowired
	private TradeBO tradeBO;
	
	@Autowired
	private StockBO stockBO;
	
	
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
		List<StockDO> stockList = getInitStockList();
		Ma3Tc test = new Ma3Tc();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	public void testMa3LLVTc(){
		List<StockDO> stockList = getInitStockList();
		Ma3Tc test = new Ma3LLVTc();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	
	@Test
	public void testMa3KongTcLLV(){
		List<StockDO> stockList = getInitStockList();
		Ma3Tc test = new Ma3KongTcLLV();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	public void testMa3LLVStopTc(){
		List<StockDO> stockList = getInitStockList();
		Ma3Tc test = new Ma3LLVStopTc();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	
	public void testMa3LLVTcAtr(){
		List<StockDO> stockList = getInitStockList();
		Ma3Tc test = new Ma3LLVTcAtr();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	private List<StockDO> getInitStockList(){
		List<StockDO> stockList = tradeBO.getStockListByFile(code);
		stockList = stockBO.initStockListMa(stockList,"");
		stockList = stockBO.initStockListAtr(stockList,0);
		return stockList;
	}
}
