package com.baolei.ghost.app2;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.dal.daointerface.StockDAO;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.web.manage.ManagePriceData;

public class InitStockDB2 {
	
	protected Log log = LogFactory.getLog(getClass());

	private static String code = "SZ399300";
	
	public static void main(String[] args) throws IOException, ParseException {
		InitStockDB2 isd2 = new InitStockDB2();
		isd2.init();
	}
	
	public void init(){
		
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"beans.xml");
		StockDAO stockDAO = (StockDAO) context.getBean("stockDAO");
		DataParser sdp = new TxdFileParser();
		log.info("start parse "  + new Date());
		List<StockDO> stockDOList =  sdp.parse(code);
		stockDAO.deleteStockByCode(code);
		stockDAO.insertStocks(stockDOList);
		log.info("end parse "  + new Date());
		stockDOList = stockDAO.selectStockByCodeAndPeriod(code, Constant.STOCK_PERIOD_DAY);
		ManagePriceData mpd = new ManagePriceData();
		stockDOList = mpd.maManage(stockDOList,code,"20,30,60,90,120");
		stockDAO.updateStocksByIdBatch(stockDOList);
	}

}
