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
import com.baolei.ghost.test.Test;
import com.baolei.ghost.test.ma.MaTrendTest;

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
		test(stockList);
		return "test/ma_trend";
	}
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
		"beans.xml");
		StockDAO stockDAO = (StockDAO) context.getBean("stockDAO");
		String code = "SZ399300";
		List<StockDO> stockList = stockDAO.selectStockByCodeAndPeriod(code, Constant.STOCK_PERIOD_DAY);
		MaTrend mt = new MaTrend();
		mt.test(stockList);

	}
	
	public void test(List<StockDO> stockList){
		float account = 100000;
		Integer p1 = 20;
		Integer p2 = 60;
		Integer p3 = 90;
		Test test = new MaTrendTest(account,p1,p2,p3);
		test.execute(stockList);
		test.printReport(stockList);
	}
	
	

}
