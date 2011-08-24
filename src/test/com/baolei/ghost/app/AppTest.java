package com.baolei.ghost.app;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.baolei.common.AbstractTestCase;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.bo.TradeBO;

/**
 * Unit test for simple App.
 */
public class AppTest extends AbstractTestCase {
	
	private String code = "SH999999";
	
	@Autowired
	private TradeBO tradeBO;
	
	@Test
	public void testGetStockListByFile(){
		List<StockDO> stockList = tradeBO.getStockListByFile(code);
	}
}
