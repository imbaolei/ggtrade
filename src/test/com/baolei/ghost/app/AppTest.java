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

/**
 * Unit test for simple App.
 */
public class AppTest extends AbstractTestCase {
	
	private String code = "SZ399300";
	
	protected Log log = LogFactory.getLog(getClass());
	DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
	
	@Autowired
	private TradeBO tradeBO;
	
	@Autowired
	private StockBO stockBO;
	
	@Test
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
}
