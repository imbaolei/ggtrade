package com.baolei.ghost.app;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.baolei.ghost.AccountDO;
import com.baolei.ghost.DataPool;
import com.baolei.ghost.common.Constant;
import com.baolei.ghost.common.StockUtil;
import com.baolei.ghost.dal.daointerface.StockDAO;
import com.baolei.ghost.dal.dataobject.StockDO;
import com.baolei.ghost.plan.Plan;
import com.baolei.ghost.plan.hg.HGOnWBPlan;

public class TradeDB {

	private static String code = "SH600739";

	private static float amount = 100000;

	public static void main(String[] args) throws IOException, ParseException {
		System.out.println(new Date());
		DataPool dataPool = new DataPool();
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		StockDAO stockDAO = (StockDAO) context.getBean("stockDAO");
		dataPool.setDayStockList(stockDAO.selectStockByCodeAndPeriod(code,
				Constant.STOCK_PERIOD_DAY));
		dataPool.setWeekStockList(stockDAO.selectStockByCodeAndPeriod(code,
				Constant.STOCK_PERIOD_WEEK));
		dataPool.setMonthStockList(stockDAO.selectStockByCodeAndPeriod(code,
				Constant.STOCK_PERIOD_MONTH));
		DateFormat dateFormat = new SimpleDateFormat(StockUtil.dateFormatString);
		for (StockDO stockDO : dataPool.getDayStockList()) {
			dataPool.getDayStockMap().put(
					dateFormat.format(stockDO.getTime()), stockDO);
		}

		for (StockDO stockDO : dataPool.getWeekStockList()) {
			dataPool.getWeekStockMap().put(
					dateFormat.format(stockDO.getTime()), stockDO);
		}
		
		for (StockDO stockDO : dataPool.getMonthStockList()) {
			dataPool.getMonthStockMap().put(
					dateFormat.format(stockDO.getTime()), stockDO);
		}
		System.out.println(new Date());
		AccountDO accountDO = new AccountDO(amount);
//		PlanDO plan = new HGPlan(dataPool);
		Plan plan = new HGOnWBPlan(dataPool);
//		PlanDO plan = new WeekBuyPointPlan(dataPool);
		for (StockDO stockDO : dataPool.getDayStockList()) {
			if (plan.canTrade(stockDO.getTime())) {
				plan.execute(stockDO, accountDO);
			}

		}
		float total = accountDO.getMoney() + plan.getTotalNum()*plan.getStopLoss() + plan.getBalance();
		System.out.println("最后账户总额为"+total);
		String statuss = "";
		int justwin = 0;
		int stoploss = 0 ;
		for(String status : plan.getExitStatus()){
			statuss = statuss+status + ";";
			if(Constant.EXIT_STATUS_JUSTWIN.equals(status)){
				justwin++;
			}
			if(Constant.EXIT_STATUS_STOPLOST.equals(status)){
				stoploss++;
			}
		}
		System.out.println("交易退出状态："+statuss);
		System.out.println("止赢次数:"+justwin+"; 止损次数:"+stoploss);
		System.out.println(new Date());
	}
}
