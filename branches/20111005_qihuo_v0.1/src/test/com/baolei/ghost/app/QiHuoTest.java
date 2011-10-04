package com.baolei.ghost.app;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.baolei.common.AbstractTestCase;
import com.baolei.ghost.app2.DataParser;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.bo.StockBO;
import com.baolei.trade.test.trend.ma.Ma3Tc;
import com.baolei.trade.test.trend.ma.filter.Ma3LLVTc;

public class QiHuoTest  extends AbstractTestCase{
	float account = 100000;
	Integer p1 = 20;
	Integer p2 = 60;
	Integer p3 = 90;
	private String code = "AU0001";
	protected Log log = LogFactory.getLog(getClass());

	@Autowired
	@Qualifier("dzhTxtParser") 
	DataParser dzhTxtParser;
	
	@Autowired
	private StockBO stockBO;
	
	@Test
	public void testMa3LLVTc() {
		List<StockDO> stockList = getInitStockList(code);
		Ma3Tc test = new Ma3LLVTc();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	private List<StockDO> getInitStockList(String code) {
		List<StockDO> stockList = dzhTxtParser.parse(code);
		stockList = stockBO.initStockListMa(stockList, "");
		stockList = stockBO.initStockListAtr(stockList, 0);
		return stockList;
	}
}
