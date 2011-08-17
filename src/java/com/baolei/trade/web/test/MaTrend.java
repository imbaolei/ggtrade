package com.baolei.trade.web.test;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.daointerface.StockDAO;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.trade.test.Test;
import com.baolei.trade.test.none.TestNone;
import com.baolei.trade.test.trend.ma.Test3MaTrend;
import com.baolei.trade.test.trend.ma.Test3MaTrendDingTou;
import com.baolei.trade.test.trend.ma.period.Test3MaTrendPeriod;


@Controller
@RequestMapping("/test/ma_trend.do")
public class MaTrend {
protected Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private StockDAO stockDAO;
	
	public static DecimalFormat decimalFormat = new DecimalFormat("#.00");
	public static DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);

	@RequestMapping(params = "m=trade")
	public String trade(HttpServletRequest request, ModelMap model) {
		String code = request.getParameter("code");
		List<StockDO> stockList = stockDAO.selectStockByCodeAndPeriod(code, Constant.STOCK_PERIOD_DAY);
		test3MaTrend(stockList);
		return "test/ma_trend";
	}
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
		"beans.xml");
		StockDAO stockDAO = (StockDAO) context.getBean("stockDAO");
		String code = "SZ399300";
		List<StockDO> stockList = stockDAO.selectStockByCodeAndPeriod(code, Constant.STOCK_PERIOD_DAY);
		MaTrend mt = new MaTrend();
//		mt.test3MaTrend(stockList);
//		mt.testNone(stockList);
//		mt.test3MaTrend2Stock(stockList);
//		mt.test3MaTrendDingTou(stockList);
		mt.test3MaTrendPeriod(stockList);
		
	}
	
	public void test3MaTrend(List<StockDO> stockList){
		float account = 100000;
		Integer p1 = 20;
		Integer p2 = 60;
		Integer p3 = 90;
		Test3MaTrend test = new Test3MaTrend();
		test.execute();
		test.printReport();
	}
	
	public void testNone(List<StockDO> stockList){
		float account = 100000;
		Test test = new TestNone(account);
		test.execute();
		test.printReport();
	}
	
	public void test3MaTrend2Stock(List<StockDO> stockList){
		float account = 100000;
		Integer p1 = 20;
		Integer p2 = 60;
		Integer p3 = 90;
		Test3MaTrend test = new Test3MaTrend();
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	public void test3MaTrendPeriod(List<StockDO> stockList){
		float account = 100000;
		Integer p1 = 20;
		Integer p2 = 60;
		Integer p3 = 90;
		Test3MaTrend test = new Test3MaTrendPeriod();
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	public void test3MaTrendDingTou(List<StockDO> stockList){
		float account = 1000;
		Integer p1 = 20;
		Integer p2 = 60;
		Integer p3 = 90;
		Test3MaTrend test = new Test3MaTrendDingTou();
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	

}
