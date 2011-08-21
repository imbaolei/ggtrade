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
import com.baolei.trade.test.none.TestNoneDingTou;
import com.baolei.trade.test.trend.kong.ma.Test3MaTrendKongTouCunDT;
import com.baolei.trade.test.trend.kong.ma.Test3MaTrendKongTouCunOne;
import com.baolei.trade.test.trend.kong.ma.period.Test3MaTrendKongTouCunOnePeriod;
import com.baolei.trade.test.trend.ma.Test3MaTrendDT;
import com.baolei.trade.test.trend.ma.Test3MaTrendOne;
import com.baolei.trade.test.trend.ma.Test3MaTrendTouCunDT;
import com.baolei.trade.test.trend.ma.period.Test3MaTrendDTPeriod;
import com.baolei.trade.test.trend.ma.period.Test3MaTrendOnePeriod;


@Controller
@RequestMapping("/test/ma_trend.do")
public class MaTrend {
protected Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private StockDAO stockDAO;
	
	float account = 100000;
	float moneyPeriod = 1000;
	float accountDingTou = 1000;
	Integer p1 = 20;
	Integer p2 = 60;
	Integer p3 = 90;
	
	public static DecimalFormat decimalFormat = new DecimalFormat("#.00");
	public static DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);

	@RequestMapping(params = "m=trade")
	public String trade(HttpServletRequest request, ModelMap model) {
		String code = request.getParameter("code");
		List<StockDO> stockList = stockDAO.selectStockByCodeAndPeriod(code, Constant.STOCK_PERIOD_DAY);
//		test3MaTrend(stockList);
		return "test/ma_trend";
	}
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
		"beans.xml");
		StockDAO stockDAO = (StockDAO) context.getBean("stockDAO");
		String code = "SZ399300";
		List<StockDO> stockList = stockDAO.selectStockByCodeAndPeriod(code, Constant.STOCK_PERIOD_DAY);
		MaTrend mt = new MaTrend();
//		mt.testNone(stockList);
//		mt.test3MaTrend(stockList);
//		mt.test3MaDingTouTrend(stockList);
//		mt.testNoneDingTou(stockList);
//		mt.test3MaTrendPeriod(stockList);
//		mt.test3MaDingTouTrendPeriod(stockList);
//		mt.test3MaTrendTouCunDT(stockList);
//		mt.test3MaTrendKongTouCunDT(stockList);
		mt.test3MaTrendKongTouCunOne(stockList);
//		mt.test3MaTrendKongTouCunOnePeriod(stockList);
		
		
	}
	
	
	public void testNone(List<StockDO> stockList){
		Test test = new TestNone();
		test.initCash(account);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	public void testNoneDingTou(List<StockDO> stockList){
		Test test = new TestNoneDingTou();
		test.initCash(accountDingTou);
		test.setMoneyDingTou(moneyPeriod);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	

	
	
	
	public void test3MaDingTouTrendPeriod(List<StockDO> stockList){
		Test3MaTrendDT test = new Test3MaTrendDTPeriod();
		test.initCash(accountDingTou);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(moneyPeriod);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	public void test3MaDingTouTrend(List<StockDO> stockList){
		Test3MaTrendDT test = new Test3MaTrendDT();
		test.initCash(accountDingTou);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(moneyPeriod);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	

	
	public void test3MaTrend(List<StockDO> stockList){
		Test3MaTrendDT test = new Test3MaTrendOne();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	public void test3MaTrendPeriod(List<StockDO> stockList){
		Test3MaTrendDT test = new Test3MaTrendOnePeriod();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	public void test3MaTrendTouCunDT(List<StockDO> stockList){
		Test3MaTrendDT test = new Test3MaTrendTouCunDT();
		test.initCash(accountDingTou);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(moneyPeriod);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	public void test3MaTrendKongTouCunDT(List<StockDO> stockList){
		Test3MaTrendDT test = new Test3MaTrendKongTouCunDT();
		test.initCash(accountDingTou);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(moneyPeriod);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	public void test3MaTrendKongTouCunOne(List<StockDO> stockList){
		Test3MaTrendDT test = new Test3MaTrendKongTouCunOne();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
	
	public void test3MaTrendKongTouCunOnePeriod(List<StockDO> stockList){
		Test3MaTrendDT test = new Test3MaTrendKongTouCunOnePeriod();
		test.initCash(account);
		test.initMaParam(p1, p2, p3);
		test.setMoneyDingTou(0);
		test.initStockList(stockList, stockList);
		test.execute();
		test.printReport();
	}
}
